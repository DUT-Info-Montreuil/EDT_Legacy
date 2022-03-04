package EDT;

import eu.webtoolkit.jwt.WDialog;
import eu.webtoolkit.jwt.WFitLayout;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WBoxLayout.Direction;
import eu.webtoolkit.jwt.WGridLayout;
import eu.webtoolkit.jwt.WHBoxLayout;
import eu.webtoolkit.jwt.WLayout;
import eu.webtoolkit.jwt.AlignmentFlag;
import eu.webtoolkit.jwt.LayoutDirection;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WBorderLayout;
import eu.webtoolkit.jwt.WBoxLayout;
import eu.webtoolkit.jwt.WCheckBox;

import java.io.File;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class FormulaireLdap_wt extends WDialog {
	EDTApplication_wt   edtAppli;
	SortedMap<String, Etudiant> etudiants;
	ArrayList<Groupe>   groupes;
	ArrayList<Etudiant> listeGroupesEtudiant;
	ArrayList<Groupe>   listeGroupesPromo;
	WPushButton         b_ajouter,b_remove;
	WCheckBox[]         cb_groupesEtudiants;
	WCheckBox[]         cb_groupes;
	WDialog dialog = this;
	ArrayList<String> allEtudiantsPromo;
	String promotion;

	FormulaireLdap_wt(EDTApplication_wt _edtAppli) {
		super("Gestion des etudiants LDAP");
		edtAppli=_edtAppli;
		promotion = edtAppli.getListePromotions().getValueText();
		allEtudiantsPromo = edtAppli.getControleurDb().getEtudiantsAll(promotion);
		afficher();
	}

	public void afficher() {
		this.getContents().clear();
		this.getFooter().clear();
		allEtudiantsPromo.clear();
		allEtudiantsPromo = edtAppli.getControleurDb().getEtudiantsAll(promotion);
		etudiants = new TreeMap<String, Etudiant>();
		String[] sA = promotion.split(" ");
		String departement = sA[1].toLowerCase(); 
		departement        = departement.replaceAll(" ", "_").toLowerCase();
		String serverIP    = "ldap1.iut.univ-paris8.fr";
		String serverPort  = "389";
		String serverLogin = "uid=stagiaire03,ou=info,ou=etudiants,ou=enseignement,ou=utilisateurs,o=iut.univ-paris8.fr";
		String serverPass  = "Stag$2018";
		Hashtable<String, String> environnement = new Hashtable<String, String>();
		environnement.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		environnement.put(Context.PROVIDER_URL, "ldap://"+serverIP+":"+serverPort+"/");
		environnement.put(Context.SECURITY_AUTHENTICATION, "simple");
		environnement.put(Context.SECURITY_PRINCIPAL, serverLogin);
		environnement.put(Context.SECURITY_CREDENTIALS, serverPass);        
		try {
			DirContext contexte = new InitialDirContext(environnement);
			//System.out.println("Connexion au serveur : SUCCES");
			try {
				//System.out.println("Recuperation des utilisateurs : SUCCES");
				SearchControls searchCtrls = new SearchControls();
				searchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
				searchCtrls.setReturningAttributes(new String[]{"*","+"});
				String filter = "(objectClass=*)";
				//Add filter for date
				NamingEnumeration values = contexte.search("ou="+departement+",ou=etudiants,ou=enseignement,ou=utilisateurs,o=iut.univ-paris8.fr",filter,searchCtrls);
				int i=0;
				while (values.hasMoreElements()){
					SearchResult result = (SearchResult) values.next();
					Attributes attribs = result.getAttributes();
					String prenom = Objects.toString(attribs.get("givenName"));
					if(prenom!=null) {
						if(prenom!="") {
							if(prenom.startsWith("givenName:")) {
								prenom=prenom.substring(11);
							}
						}
					}
					//String[]nom = Objects.toString(attribs.get("sn")).split(" ");
					String nom = Objects.toString(attribs.get("sn"));
					if(nom!=null) {
						if(nom!="") {
							if(nom.startsWith("sn:")) {
								nom=nom.substring(3);
							}
						}
					}

					String[]login = Objects.toString(attribs.get("uid")).split(" ");
					String[]dates = Objects.toString(attribs.get("createTimestamp")).split(" ");
					String date = dates[1].substring(0, 4);
					if((Year.now().getValue()-Integer.parseInt(date))<=2) {
						if(login[0]!="null") {
							if(!allEtudiantsPromo.contains(login[1].toLowerCase())) {
								if(nom!=null) {
									if(prenom!=null) {
										Etudiant e = new Etudiant(i, prenom, nom, login[1]);
										etudiants.put(e.getNom()+" "+ e.getPrenom(),e);
										i++;
									}
								}
							}
						}
					}
				}
			} catch (NamingException e) {
				//System.out.println("Recuperation des utilisateurs : ECHEC");
				e.printStackTrace();
			}
		} catch (NamingException e) {
			//System.out.println("Connexion au serveur : ECHEC");
			e.printStackTrace();
		}     

		WContainerWidget panel = new WContainerWidget();
		WGridLayout grid = new WGridLayout();
		panel.setLayout(grid);

		WContainerWidget panelEtudiants=new WContainerWidget();
		WGridLayout grid2 = new WGridLayout();
		panelEtudiants.setLayout(grid2);
		cb_groupesEtudiants=new WCheckBox[etudiants.size()];       
		int i=0;
		for(String e : etudiants.keySet()) {
			cb_groupesEtudiants[i]=new WCheckBox(e);
			grid2.addWidget(cb_groupesEtudiants[i], i, 0);
			i++;
		}
		grid.addWidget(panelEtudiants,0,0);
		getContents().addWidget(panel);

		WContainerWidget panelGroupeG = new WContainerWidget();
		WBoxLayout gridF = new WBoxLayout(Direction.TopToBottom);
		WContainerWidget panelGroupe = new WContainerWidget();
		listeGroupesPromo=new ArrayList<Groupe>(edtAppli.getControleurDb().getListeGroupes((String)(edtAppli.getListePromotions().getValueText())));
		listeGroupesEtudiant =  new ArrayList<Etudiant>();
		groupes = new ArrayList<Groupe>();
		cb_groupes=new WCheckBox[listeGroupesPromo.size()];
		for (i=0; i<listeGroupesPromo.size(); i++) {
			cb_groupes[i]=new WCheckBox(listeGroupesPromo.get(i).getNom());
			panelGroupe.addWidget(cb_groupes[i]);
		}
		cb_groupes[0].setEnabled(false);
		cb_groupes[0].setChecked(true);
		gridF.addWidget(panelGroupe, 0,AlignmentFlag.AlignLeft);
		panelGroupeG.setLayout(gridF);

		grid.addWidget(panelGroupeG,0,2);

		b_remove=new WPushButton("Annuler");
		b_remove.clicked().addListener(b_remove, new Signal.Listener() {
			public void trigger() {
				remove();
			}        
		});
		b_remove.setStyleClass("btn-info");     


		b_ajouter=new WPushButton("Ajouter");
		b_ajouter.clicked().addListener(b_ajouter, new Signal.Listener() {
			public void trigger() {
				action();
			}                       
		});
		b_ajouter.setStyleClass("btn-success");     

		getFooter().addWidget(b_remove);
		getFooter().addWidget(b_ajouter);

		this.setResizable(true);
		this.setClosable(true);
		this.rejectWhenEscapePressed(true);
		this.resize(800, 500);
		this.show();
	}

	public void action() {    
		for (int i=0;i<cb_groupesEtudiants.length;i++){
			if (!cb_groupesEtudiants[i].equals(null)) {
				if (cb_groupesEtudiants[i].isChecked()) {
					listeGroupesEtudiant.add(etudiants.get(cb_groupesEtudiants[i].getText().getValue()));
				}	
			}
		}   
		for (int i=0;i<cb_groupes.length;i++){
			if (!cb_groupes[i].equals(null)) {
				if (cb_groupes[i].isChecked())
					groupes.add(listeGroupesPromo.get(i));
			}
		}    
		ArrayList<String> sl = new ArrayList<String>();
		for (int i=0;i<listeGroupesEtudiant.size();i++){   
			if(listeGroupesEtudiant.get(i)!=null) {
				if (allEtudiantsPromo.contains(listeGroupesEtudiant.get(i).getLogin().toLowerCase())) {
					Utilitaire.showMessageDialog(FormulaireLdap_wt.this, "L'étudiant '"+listeGroupesEtudiant.get(i).getNom()+" "+ listeGroupesEtudiant.get(i).getPrenom() +"' existe déjà");
				}else {
					int idE = edtAppli.getControleurDb().getEtudiantLogin(listeGroupesEtudiant.get(i).getLogin());
					if(idE!=0) {
						for(Groupe g:groupes) {
							edtAppli.getControleurDb().ajouterEtudiant2(g.getIdGroupe(),idE);
						}
						sl.add(listeGroupesEtudiant.get(i).getPrenom()+" "+listeGroupesEtudiant.get(i).getNom());
					}else {
						edtAppli.getControleurDb().ajouterEtudiant(listeGroupesEtudiant.get(i).getPrenom(),listeGroupesEtudiant.get(i).getNom(),listeGroupesEtudiant.get(i).getLogin(), groupes);
						sl.add(listeGroupesEtudiant.get(i).getPrenom()+" "+listeGroupesEtudiant.get(i).getNom()+"\n");
					}
				}
			}
		}
		Utilitaire.showMessageDialog("Elèves Ajouté :", sl.toString());
		afficher();
	}    
}
