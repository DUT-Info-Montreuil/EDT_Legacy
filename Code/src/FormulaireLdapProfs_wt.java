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

public class FormulaireLdapProfs_wt extends WDialog {
	EDTApplication_wt edtAppli;
	SortedMap<String, Prof> profs = new TreeMap<String, Prof>();
	ArrayList<Prof> listeProfs = new ArrayList<Prof>();
	WPushButton b_ajouter,b_remove;
	WCheckBox[] cb_profs;
	WDialog dialog = this;
	ArrayList<String> allProfs = new ArrayList<String>();
	String promotion;

	FormulaireLdapProfs_wt(EDTApplication_wt _edtAppli) {
		super("Gestion des profs LDAP");
		edtAppli=_edtAppli;
		afficher();
	}

	public void afficher() {
		this.getContents().clear();
		this.getFooter().clear();
		allProfs.clear();
		profs.clear();
		allProfs = edtAppli.getControleurDb().getProfs2();
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
				NamingEnumeration values = contexte.search("ou=personnel,ou=utilisateurs,o=iut.univ-paris8.fr",filter,searchCtrls);
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
								nom=nom.substring(4);
							}
						}
					}

					String[]login = Objects.toString(attribs.get("uid")).split(" ");
					//String[]dates = Objects.toString(attribs.get("createTimestamp")).split(" ");
					//String date = dates[1].substring(0, 4);
					//if((Year.now().getValue()-Integer.parseInt(date))<=2) {
						if(login[0]!="null") {
							if(!allProfs.contains(login[1].toLowerCase())) {
								if(nom!=null) {
									if(prenom!=null) {
										Prof e = new Prof(i, null,prenom, nom, login[1],true);
										profs.put(e.getNom()+" "+ e.getPrenom(),e);
										i++;

									}
								}
							}
						}
					//}
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

		WContainerWidget panelProfs=new WContainerWidget();
		WGridLayout grid2 = new WGridLayout();
		panelProfs.setLayout(grid2);
		cb_profs=new WCheckBox[profs.size()];       
		int i=0;
		for(String p : profs.keySet()) {
			cb_profs[i]=new WCheckBox(p);
			grid2.addWidget(cb_profs[i], i, 0);
			i++;
		}
		grid.addWidget(panelProfs,0,0);
		getContents().addWidget(panel);

		b_remove=new WPushButton("Annuler");
		b_remove.clicked().addListener(b_remove, new Signal.Listener() {
			public void trigger() {
				remove();
				new FormulaireProfs_wt(edtAppli);
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
		this.resize(600, 500);
		this.show();
	}

	public void action() {    
		for (int i=0;i<cb_profs.length;i++){
			if (!cb_profs[i].equals(null)) {
				if (cb_profs[i].isChecked()) {
					listeProfs.add(profs.get(cb_profs[i].getText().getValue()));
				}	
			}
		} 
		ArrayList<String> sl = new ArrayList<String>();
		for (int i=0;i<listeProfs.size();i++){   
			if(listeProfs.get(i)!=null) {
				if (allProfs.contains(listeProfs.get(i).getLogin().toLowerCase())) {
					Utilitaire.showMessageDialog(FormulaireLdapProfs_wt.this, "Le Prof '"+listeProfs.get(i).getNom()+" "+ listeProfs.get(i).getPrenom() +"' existe déjà");
				}else {
					int idE = edtAppli.getControleurDb().getProfLogin(listeProfs.get(i).getLogin());
					if(idE!=0) {
						Utilitaire.showMessageDialog(FormulaireLdapProfs_wt.this, "Le Prof '"+listeProfs.get(i).getNom()+" "+ listeProfs.get(i).getPrenom() +"' existe déjà");
					}else {
						String semaineEnCours=(String)(edtAppli.getListeSemaines().getValueText());
						String[]tab=semaineEnCours.split("/");
						int dateDebutAnneeScolaire=Integer.parseInt(tab[2].substring(tab[2].length()-4,4));
						if (Integer.parseInt(tab[1])<7)dateDebutAnneeScolaire--;

						String initiale=""+listeProfs.get(i).getPrenom().charAt(0)+listeProfs.get(i).getNom().charAt(0);
						int ini=1;
						while(edtAppli.getControleurDb().getListeInitialesProfs().contains(initiale)) {
							initiale=""+listeProfs.get(i).getPrenom().charAt(0)+listeProfs.get(i).getPrenom().charAt(0+ini)+listeProfs.get(i).getNom().charAt(0)+listeProfs.get(i).getNom().charAt(0+ini);
							ini++;
						}							
						edtAppli.getControleurDb().ajouterProf(dateDebutAnneeScolaire, initiale.toUpperCase(), listeProfs.get(i).getPrenom(), listeProfs.get(i).getNom(),0,0,0,0,0,null,false,listeProfs.get(i).getLogin(),edtAppli.getControleurDb().getIdPromotion(edtAppli.getListePromotions().getValueText()));
						sl.add(listeProfs.get(i).getPrenom()+" "+listeProfs.get(i).getNom()+"\n");
					}
				}
			}
		}
		Utilitaire.showMessageDialog("Prof Ajouter :", sl.toString());
		afficher();
	}    
}
