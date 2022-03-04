package EDT;

import eu.webtoolkit.jwt.WDialog;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WLabel;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WCheckBox;
import eu.webtoolkit.jwt.WBorderLayout;
import eu.webtoolkit.jwt.WGridLayout;
import eu.webtoolkit.jwt.Side;
import eu.webtoolkit.jwt.Signal;

import java.util.*;
import java.util.ArrayList;

/**
 * formulaire de gestion des étudiants
 */
public class FormulaireEtudiants_wt extends WDialog {
	EDTApplication_wt edtAppli;
	ArrayList<Etudiant> etudiants;
	ArrayList<WLabel> labelEtudiant;
	ArrayList<WPushButton> boutonModif;
	ArrayList<WPushButton> boutonSuppr;
	WCheckBox cb_parGroupe;
	ArrayList<Etudiant> tousLesEtudiants;
	ArrayList<WPushButton> boutonAjouter;
	WPushButton b_ajouter,b_supprimerTout,b_ldap,b_export;
	WCheckBox selection_groupe;
	boolean affichageParGroupe;	
	boolean affichageExport=true;	
	WDialog dialog = this;

	FormulaireEtudiants_wt(EDTApplication_wt _edtAppli) {
		super("Gestion des étudiants");
		edtAppli=_edtAppli;
		affichageParGroupe=false; // par défaut : affichage de la promo entière
		afficher();
	}

	public void afficher() {
		getContents().clear();

		WContainerWidget panel = new WContainerWidget();
		WBorderLayout border = new WBorderLayout();
		panel.setLayout(border);

		WContainerWidget panelRadio = new WContainerWidget();
		WGridLayout gridRadio = new WGridLayout();
		panelRadio.setLayout(gridRadio);

		cb_parGroupe=new WCheckBox("Affichage par groupe ");        
		cb_parGroupe.changed().addListener(cb_parGroupe, new Signal.Listener() {
			public void trigger() {
				affichageParGroupe=!affichageParGroupe;
				//b_supprimerTout.setDisabled(affichageParGroupe);
				//Utilitaire.showMessageDialog("cb",""+b_supprimerTout.isDisabled());
				afficher();
			}
		});
		cb_parGroupe.setChecked(affichageParGroupe);
		cb_parGroupe.setFloatSide(Side.Left);

		b_ajouter=new WPushButton("Ajouter des étudiants");
		b_ajouter.clicked().addListener(b_ajouter, new Signal.Listener() {
			public void trigger() {
				accept();
				new FormulaireSaisieEtudiant_wt(edtAppli, null, dialog);
			}
		});
		b_ajouter.setStyleClass("btn-success");       
   
		//b_supprimerTout=new WPushButton("Supprimer tous les étudiants dans la base");
		b_supprimerTout=new WPushButton("Supprimer tous les étudiants dans la promo");
		b_supprimerTout.clicked().addListener(b_supprimerTout, new Signal.Listener() {
			public void trigger() {
				WDialog confirmerSuppr = new WDialog("Suppression de tous les étudiants");
				//new WLabel("Confirmez-vous la suppression DÉFINITIVE de tous les étudiants, AINSI QUE de leurs notes et de leurs absences ?", confirmerSuppr.getContents());
				new WLabel("Confirmez-vous la suppression de tous les étudiants dans cette promo ? Ces notes et absences seront sauvegarder.", confirmerSuppr.getContents());
				WPushButton ok = new WPushButton("Oui", confirmerSuppr.getFooter());
				WPushButton annuler = new WPushButton("Non", confirmerSuppr.getFooter());

				ok.setStyleClass("btn-danger");
				annuler.setStyleClass("btn-primary");

				ok.clicked().addListener(ok, new Signal.Listener() {
					public void trigger() {
						confirmerSuppr.accept();
						//Enlever ce commentaire pour remmetre la suppresion hard qui efface l'etudient en entier
						//edtAppli.getControleurDb().supprimerTousLesEtudiants(tousLesEtudiants);
						
						TreeSet<Groupe>listeGroupes=edtAppli.getControleurDb().getListeGroupes((String)edtAppli.getListePromotions().getValueText());	//SelectedItem());
						for(Groupe g : listeGroupes) {
							ArrayList<Etudiant> etudiantsS =new ArrayList<Etudiant>(edtAppli.getControleurDb().getEtudiants(g.getIdGroupe()));
							for (Iterator<Etudiant> iterator = etudiantsS.iterator(); iterator.hasNext();) {
								Etudiant e=iterator.next();
								edtAppli.getControleurDb().supprimerTousLesEtudiantsSoft1Par1(e.getIdEtudiant(),g.getIdGroupe());
							}
						}
						accept();
						new FormulaireEtudiants_wt(edtAppli);
					}
				});
				annuler.clicked().addListener(annuler, new Signal.Listener() {
					public void trigger() {
						confirmerSuppr.reject();
					}
				});

				confirmerSuppr.setClosable(true);
				confirmerSuppr.rejectWhenEscapePressed(true);
				confirmerSuppr.show();

			}
		});
		b_supprimerTout.setStyleClass("btn-danger");    

		getFooter().clear();
		getFooter().addWidget(cb_parGroupe);
		//getFooter().addWidget(b_ajouter);
		getFooter().addWidget(b_supprimerTout);
		
		String[] sA = edtAppli.getListePromotions().getValueText().split(" ");
		if(!sA.equals(null)) {
			if(sA.length>1) {
				b_ldap=new WPushButton("Ajouter des etudiants "+sA[1].toLowerCase());
				b_ldap.clicked().addListener(b_ldap, new Signal.Listener() {
					public void trigger() {
						accept();
						new FormulaireLdap_wt(edtAppli);
					}
				});
				b_ldap.setStyleClass("btn-success");       
				getFooter().addWidget(b_ldap);
			}
		}

		/*
		String semestre="";
		if(edtAppli.getListePromotions().getValueText().endsWith("S1")) {
			semestre="S2";
		}else if(edtAppli.getListePromotions().getValueText().endsWith("S1 APP")) {
			semestre="S2 APP";
		}else if(edtAppli.getListePromotions().getValueText().endsWith("S2")) {
			semestre="S3";
		}else if(edtAppli.getListePromotions().getValueText().endsWith("S2 APP")) {
			semestre="S3 APP";
		}else if(edtAppli.getListePromotions().getValueText().endsWith("S3")) {
			semestre="S4";
		}else if(edtAppli.getListePromotions().getValueText().endsWith("S3 APP")) {
			semestre="S4 APP";
		}else if(edtAppli.getListePromotions().getValueText().endsWith("S4")) {
			semestre="fin d'année";
		}else if(edtAppli.getListePromotions().getValueText().endsWith("S4 APP")) {
			semestre="fin d'année";
		}else {
			affichageExport=false;
		}

		if(affichageExport!=false) {
			b_export=new WPushButton("Passage en "+semestre);
			b_export.clicked().addListener(b_export, new Signal.Listener() {
				public void trigger() {
					accept();
					new FormulaireExport_wt(edtAppli);
				}
			});
			b_export.setStyleClass("btn-info"); 
			getFooter().addWidget(b_export);
		}
		*/
		WContainerWidget panelEtudiants = new WContainerWidget();
		WGridLayout gridEtudiants = new WGridLayout();
		panelEtudiants.setLayout(gridEtudiants);    

		if (affichageParGroupe) { // on prépare l'affichage par groupe
			String affichage="<html>";
			TreeSet<Groupe>listeGroupes=edtAppli.getControleurDb().getListeGroupes((String)edtAppli.getListePromotions().getValueText());	//SelectedItem());
			for (Iterator<Groupe> iter = listeGroupes.iterator(); iter.hasNext();) {
				Groupe g=iter.next();
				/*affichage+=g.getNom()+"<br/>";*/
				affichage+="<h4>"+g.getNom()+"</h4>";
				etudiants=new ArrayList<Etudiant>(edtAppli.getControleurDb().getEtudiants(g.getIdGroupe()));
				for (Iterator<Etudiant> iterator = etudiants.iterator(); iterator.hasNext();) {
					Etudiant e=iterator.next();
					affichage+=e.getNom()+" "+e.getPrenom()+"<br/>";
				}
				affichage+="<br/><br/>";
			}
			affichage+="</html>";
			gridEtudiants.addWidget(new WLabel(affichage));
		} else { // on affiche la promo entière
			etudiants=new ArrayList<Etudiant>(edtAppli.getControleurDb().getEtudiants(edtAppli.getControleurDb().getIdGroupeCours(edtAppli.getControleurDb().getIdPromotion((String)edtAppli.getListePromotions().getValueText())))); //.getSelectedItem()))));
			tousLesEtudiants=etudiants;
			/*if(tousLesEtudiants.isEmpty() && affichageExport) {
				b_export.disable();
			}*/

			labelEtudiant=new ArrayList<WLabel>();
			boutonModif=new ArrayList<WPushButton>();
			boutonSuppr=new ArrayList<WPushButton>();
			WPushButton b;
			for (int i=0; i<etudiants.size(); i++) {    			
				Etudiant etudiant = etudiants.get(i);    			
				labelEtudiant.add(new WLabel(""+ etudiant.getNom() + " " + etudiant.getPrenom()));

				b=new WPushButton("Modifier");
				b.clicked().addListener(b, new Signal.Listener() {
					public void trigger() {
						accept();
						new FormulaireSaisieEtudiant_wt(edtAppli, etudiant, dialog);	
					}
				});
				b.setStyleClass("btn-info");    			
				boutonModif.add(b);

				b=new WPushButton("Supprimer");
				b.clicked().addListener(b, new Signal.Listener() {
					public void trigger() {
						//Enlevez ces commentaires pour remmetre comme avant la suppresion definitive
						WDialog confirmerSuppr = new WDialog("Suppression d'étudiant");
						//new WLabel("Confirmez-vous la suppression DÉFINITIVE de "+etudiant.getPrenom() + " " + etudiant.getNom() + " ainsi que de ses notes et absences ?", confirmerSuppr.getContents());
						new WLabel("Confirmez-vous la suppression DÉFINITIVE de "+etudiant.getPrenom() + " " + etudiant.getNom() + " de cette promo ?", confirmerSuppr.getContents());
						WPushButton ok = new WPushButton("Oui", confirmerSuppr.getFooter());
						WPushButton annuler = new WPushButton("Non", confirmerSuppr.getFooter());

						ok.setStyleClass("btn-danger");
						annuler.setStyleClass("btn-primary");

						ok.clicked().addListener(ok, new Signal.Listener() {
							public void trigger() {
								confirmerSuppr.accept();
								//edtAppli.getControleurDb().supprimerEtudiant(etudiant);
								TreeSet<Groupe>listeGroupes=edtAppli.getControleurDb().getListeGroupes((String)edtAppli.getListePromotions().getValueText());	//SelectedItem());
								for(Groupe g : listeGroupes) {
									edtAppli.getControleurDb().supprimerEtudiant3ParGroupe(etudiant.getIdEtudiant(),g.getIdGroupe());
								}
								accept();
								new FormulaireEtudiants_wt(edtAppli);
							}
						});
						annuler.clicked().addListener(annuler, new Signal.Listener() {
							public void trigger() {
								confirmerSuppr.reject();
							}
						});

						confirmerSuppr.setClosable(true);
						confirmerSuppr.rejectWhenEscapePressed(true);
						confirmerSuppr.show();
					}
				});    			
				b.setStyleClass("btn-warning");
				boutonSuppr.add(b);
			}
			for (int index=0; index<etudiants.size(); index++) {
				gridEtudiants.addWidget((WLabel)labelEtudiant.get(index), index, 0);
				gridEtudiants.addWidget((WPushButton)boutonModif.get(index), index, 1);
				gridEtudiants.addWidget((WPushButton)boutonSuppr.get(index), index, 2);    			
			}
		}
		getContents().addWidget(panelEtudiants);
		this.setResizable(true);
		this.setClosable(true);
		this.rejectWhenEscapePressed(true);
		this.resize(1200, 500);
		this.show();		
	}
}
