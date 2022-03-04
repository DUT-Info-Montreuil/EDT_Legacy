package EDT;

import eu.webtoolkit.jwt.WDialog;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WLabel;
import eu.webtoolkit.jwt.WLineEdit;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WCheckBox;
import eu.webtoolkit.jwt.WBorderLayout;
import eu.webtoolkit.jwt.WGridLayout;
import eu.webtoolkit.jwt.WRadioButton;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WButtonGroup;
import eu.webtoolkit.jwt.WText;
import eu.webtoolkit.jwt.WRegExpValidator;
import eu.webtoolkit.jwt.WValidator;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import eu.webtoolkit.jwt.WFileUpload;
import eu.webtoolkit.jwt.WFileResource;
import eu.webtoolkit.jwt.FileUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * formulaire d'ajout ou de modification d'étudiant
 */
public class FormulaireSaisieEtudiant_wt extends WDialog {
	Etudiant etudiant;
	EDTApplication_wt edtAppli;
	WLabel l_prenom,l_nom,l_login;
	WLineEdit tf_prenom, tf_nom, tf_login, tf_nomfichier;
	WPushButton b_valider, b_annuler, b_parcourir;
	WRadioButton rb_fichier,rb_manuel;
	ArrayList<Groupe>listeGroupesPromo;
	WCheckBox[] cb_groupes;
	File fichier_etudiants;
	WFileUpload fu;
	WFileResource fileResource;
	FileInputStream fileInputStream;
	WLabel label;
	WLineEdit loginEdit = new WLineEdit();
	WRegExpValidator validator = new WRegExpValidator("^[A-Za-z][A-Za-z][A-Za-z]*[0-9]*$");


	FormulaireSaisieEtudiant_wt(EDTApplication_wt _edtAppli, Etudiant _etudiant, WDialog dial) {
		super("Ajout/modif d'étudiant");
		edtAppli=_edtAppli;
		etudiant=_etudiant;
		WContainerWidget panel = new WContainerWidget();
		WBorderLayout grid = new WBorderLayout();
		panel.setLayout(grid);
		WContainerWidget panelEtudiant = new WContainerWidget();
		listeGroupesPromo=new ArrayList<Groupe>(edtAppli.getControleurDb().getListeGroupes((String)(edtAppli.getListePromotions().getValueText())));
		WGridLayout gridEtudiant = new WGridLayout();
		panelEtudiant.setLayout(gridEtudiant);		
		l_prenom=new WLabel("prénom :");
		l_nom=new WLabel("nom :");
		l_login=new WLabel("login :");
		cb_groupes=new WCheckBox[listeGroupesPromo.size()];
		rb_fichier=new WRadioButton("Fichier");
		rb_manuel=new WRadioButton("Manuel");
		rb_manuel.checked().addListener(rb_manuel, new Signal.Listener() {
			public void trigger() {
				action("manuel");
			}        		
		});
		rb_fichier.checked().addListener(rb_fichier, new Signal.Listener() {
			public void trigger() {
				action("fichier");	
			}
		});
		rb_fichier.setChecked(true);

		b_valider=new WPushButton("Valider");
		b_annuler=new WPushButton("Annuler");

		WButtonGroup gr=new WButtonGroup();

		gr.addButton(rb_fichier);
		gr.addButton(rb_manuel);

		tf_nomfichier=new WLineEdit();
		tf_prenom=new WLineEdit();        
		tf_nom=new WLineEdit();        
		tf_login=new WLineEdit();

		tf_prenom.setMaxLength(80);
		tf_nom.setMaxLength(80);
		tf_login.setMaxLength(30);

		fu = new WFileUpload();
		fu.setAttributeValue("label", "label");
		fu.setAttributeValue("text", "text");
		fu.setAttributeValue("caption", "caption");
		fu.setAttributeValue("value", "value");
		if (etudiant!=null) {
			tf_prenom.setText(etudiant.getPrenom());
			tf_nom.setText(etudiant.getNom());
			tf_login.setText(etudiant.getLogin());
			gridEtudiant.addWidget(l_prenom, 0, 0);
			gridEtudiant.addWidget(tf_prenom, 0, 1);
			gridEtudiant.addWidget(l_nom, 1, 0);
			gridEtudiant.addWidget(tf_nom, 1, 1);
			gridEtudiant.addWidget(l_login, 2, 0);
			gridEtudiant.addWidget(tf_login, 2, 1);
			rb_manuel.setChecked(true);

			b_valider.setDisabled(false);
		} else {
			l_prenom.setDisabled(true);
			l_nom.setDisabled(true);
			l_login.setDisabled(true);
			tf_prenom.setDisabled(true);
			tf_nom.setDisabled(true);
			tf_login.setDisabled(true);
			gridEtudiant.addWidget(rb_fichier, 0, 0);
			gridEtudiant.addWidget(rb_manuel, 0, 1);
			gridEtudiant.addWidget(l_prenom, 1, 0);
			gridEtudiant.addWidget(tf_prenom, 1, 2);
			gridEtudiant.addWidget(l_nom, 2, 0);
			gridEtudiant.addWidget(tf_nom, 2, 2);
			gridEtudiant.addWidget(l_login, 3, 0);
			gridEtudiant.addWidget(tf_login, 3, 2);
			gridEtudiant.addWidget(new WLabel("fichier CSV :"), 4, 0);	//(avec chemin complet) 

			WText out = new WText();
			gridEtudiant.addWidget(fu, 4, 2);
			fu.changed().addListener(fu, new Signal.Listener() {
				public void trigger() {
					fu.upload();
				}
			});
			fu.uploaded().addListener(fu, new Signal.Listener() {
				public void trigger() {
					out.setText("File upload is changed");	
				}
			});
			fu.fileTooLarge().addListener(fu, new Signal.Listener() {
				public void trigger() {
					out.setText("File is too large");	
				}
			});
		}

		validator.setMandatory(true);
		tf_login.setValidator(validator);

		// on récupère les groupes auxquels appartient l'étudiant à modifier
		ArrayList<Groupe>listeGroupesEtudiant=null;
		if (etudiant!=null)
			listeGroupesEtudiant=new ArrayList<Groupe>(edtAppli.getControleurDb().getListeGroupesEtudiant(etudiant.getIdEtudiant()));
		// affichage des groupes de la promo
		int index=0;
		WContainerWidget panelGroupe = new WContainerWidget();
		for (Iterator<Groupe> iter = listeGroupesPromo.iterator(); iter.hasNext();) {
			Groupe g=iter.next();
			cb_groupes[index]=new WCheckBox(g.getNom());
			if (etudiant!=null && listeGroupesEtudiant.contains(g))
				cb_groupes[index].setChecked(true);
			panelGroupe.addWidget(cb_groupes[index++]);
		}
		cb_groupes[0].setEnabled(false);
		cb_groupes[0].setChecked(true);

		b_valider.clicked().addListener(b_valider, new Signal.Listener() {
			public void trigger() {
				if (tf_login.validate() == WValidator.State.Valid || tf_nomfichier.isEnabled()) {
					action("valider");
				} else {
					b_valider.setDisabled(true);	
				}
			}
		});
		b_annuler.clicked().addListener(b_annuler, new Signal.Listener() {
			public void trigger() {
				remove();
				new FormulaireEtudiants_wt(edtAppli);
			}
		});
		b_valider.setStyleClass("btn-info");
		b_annuler.setStyleClass("btn-primary");
		getFooter().addWidget(b_valider);
		getFooter().addWidget(b_annuler);

		tf_login.keyWentUp().addListener(tf_login, new Signal.Listener() {
			public void trigger() {
				b_valider.setDisabled(tf_login.validate() != WValidator.State.Valid);	
			}
		});
		tf_login.changed().addListener(tf_login, new Signal.Listener() {
			public void trigger() {
				b_valider.setDisabled(tf_login.validate() != WValidator.State.Valid);	
			}
		});	


		// on ajoute tout dans la fenetre
		grid.addWidget(panelEtudiant, WBorderLayout.Position.North);
		grid.addWidget(panelGroupe, WBorderLayout.Position.Center);	
		getContents().addWidget(panel);

		this.setResizable(true);
		this.resize(700, 500);
		this.show();		
	}

	public void action(String action) {    
		if (action.equals("valider")) {
			String nom=null,prenom=null,login=null;
			TreeSet<Etudiant>listeEtudiants=new TreeSet<Etudiant>();
			// on vérifie les saisies
			if (rb_manuel.isChecked()) {
				prenom=tf_prenom.getText().trim().toLowerCase().replaceAll("[^-_.() éàèùA-Za-z0-9]","");
				nom=tf_nom.getText().trim().toUpperCase().replaceAll("[^-_.() éàèùA-Za-z0-9]","");            
				login=tf_login.getText().toLowerCase().replaceAll("[^-_.() éàèùA-Za-z0-9]","");
				login=login.substring(0,Math.min(30,login.length())); // on tronque à 30 caractères(comme ds la table Etudiant)

				if (nom.length()>0 && prenom.length()>0) {
					if (nom.replaceAll("[a-zA-Z -]","").length()+prenom.replaceAll("[a-zA-Z -]","").length()==0)
						listeEtudiants.add(new Etudiant(-1,prenom,nom,login));
					else {
						Utilitaire.showMessageDialog(this, "Entrée manuelle incorrecte : nom et prénom ne doivent contenir que des caractèreres alphabétiques");
						return;
					}
				} else {
					Utilitaire.showMessageDialog(this, "Entrée manuelle incorrecte : nom et prénom ne peuvent être vides");
					return;
				}
			} else { // on lit les noms/prénoms dans le fichier CSV
				String erreur="";
				String nomfichier=fu.getSpoolFileName();	//chmein fichier temp
				if (nomfichier!=null && nomfichier!="") {
					BufferedReader buf;
					InputStream inputStream = null;
					try {                    
						inputStream = FileUtils.getResourceAsStream(nomfichier);                        
						buf = new BufferedReader(new InputStreamReader(inputStream));

						String line=buf.readLine();
						while (line!=null) {
							line=line.replaceAll("[ '\"]",""); // on enlève espaces et guillemets
							String tab[]=line.split("[;,\t]");
							if (tab.length==3) {
								if (tab[0].replaceAll("[a-zA-Z -]", "").length()+tab[1].replaceAll("[a-zA-Z -]", "").length()+tab[2].replaceAll("[a-zA-Z]", "").length()==0) {
									tab[0]=tab[0].toLowerCase();
									tab[1]=tab[1].toLowerCase();
									tab[2]=tab[2].toLowerCase();

									if (tab[0].length()>0 && tab[1].length()>0 && tab[2].length()>0) {
										Etudiant et=new Etudiant(-1,tab[0],tab[1],tab[2]);
										listeEtudiants.add(et);
									} else erreur+="Entrée incorrecte dans le fichier CSV ["+line+"] : nom, prénom et login ne peuvent être vides&";
								} else	erreur+="Entrée incorrecte dans le fichier CSV ["+line+"] : nom, prénom et login ne doivent contenir que des caractèreres alphabétiques&";
							} else	erreur+="Entrée incorrecte dans le fichier CSV ["+line+"] : l'entrée doit être au format \"nom,prénom,login\" ou \"nom;prénom;login\"&";

							line=buf.readLine();
						}
						buf.close();
						inputStream.close();
					} catch (FileNotFoundException ex) {
						Utilitaire.showMessageDialog(this,"Fichier inexistant "+nomfichier);	
						ex.printStackTrace();
						return;
					} catch (IOException ex) {
						Utilitaire.showMessageDialog(this,"Problème de lecture dans le fichier "+nomfichier);	
						ex.printStackTrace();
						return;
					} catch (Exception ex) {
						Utilitaire.showMessageDialog(this,"Erreur indéterminée !!! Voir la console !");
						ex.printStackTrace();
						return;
					} finally {

					}                    

					if (!erreur.equals("")) {
						String tab[]=erreur.split("&");
						for(int i=0; i<tab.length; i++) 
							Utilitaire.showMessageDialog(this,tab[i]);
						return;
					}
				} else {
					Utilitaire.showMessageDialog(this, "Entrée incorrecte pour le nom du fichier CSV : le nom du fichier ne peut être vide !");
					return;
				}
			}
			// on cherche les groupes cochés
			ArrayList<Groupe>listeGroupesEtudiant=new ArrayList<Groupe>();
			for (int index=0;index<cb_groupes.length;index++)
				if (cb_groupes[index].isChecked())
					listeGroupesEtudiant.add(listeGroupesPromo.get((index)));
			// on récupère la liste des étudiants déjà dans la promo
			TreeSet<Etudiant> liste=edtAppli.getControleurDb().getEtudiants(edtAppli.getControleurDb().getIdGroupeCours(edtAppli.getControleurDb().getIdPromotion((String)edtAppli.getListePromotions().getValueText())));//.getSelectedItem())));
			// on ajoute (ou modifie) les étudiants de la liste
			for (Iterator<Etudiant> iter = listeEtudiants.iterator(); iter.hasNext();) {
				Etudiant nouv_etud=iter.next();
				// on vérifie que l'étudiant n'existe pas dans cette promo
				if (etudiant!=null) liste.remove(etudiant); // on enlève l'étudiant de la liste (mais pas de la BD !)
				if (liste.contains(nouv_etud)) {
					Utilitaire.showMessageDialog(this, "L'étudiant '"+nouv_etud.getNom()+" "+ nouv_etud.getPrenom() +"' existe déjà");
					if (etudiant!=null) liste.add(etudiant); // on remet l'ancien étudiant
					return;
				} else if (nouv_etud.getPrenom().equals("") || nouv_etud.getNom().equals("") || listeGroupesEtudiant.size()==0) {
					Utilitaire.showMessageDialog(this, "Il faut saisir prénom et nom et au moins un groupe !");
					if (etudiant!=null) liste.add(etudiant); // on remet l'ancien étudiant
					return;
				} else {
					if (etudiant!=null) edtAppli.getControleurDb().modifierEtudiant(etudiant, nouv_etud.getPrenom().replaceAll("[^ éàèùA-Za-z0-9]",""), nouv_etud.getNom().replaceAll("[^ éàèùA-Za-z0-9]",""), nouv_etud.getLogin().replaceAll("[^ éàèùA-Za-z0-9]",""), listeGroupesEtudiant);
					else edtAppli.getControleurDb().ajouterEtudiant(nouv_etud.getPrenom().replaceAll("[^ éàèùA-Za-z0-9]",""),nouv_etud.getNom().replaceAll("[^ éàèùA-Za-z0-9]",""),nouv_etud.getLogin().replaceAll("[^ éàèùA-Za-z0-9]",""), listeGroupesEtudiant);
					liste.add(nouv_etud); // on met le nouvel étudiant dans la liste à la place de l'ancien
				}
			}
			accept();
			new FormulaireEtudiants_wt(edtAppli);            
		} else if (action.equals("fichier")) {
			l_prenom.setDisabled(true);	
			l_nom.setDisabled(true);
			l_login.setDisabled(true);
			tf_nom.setDisabled(true);
			tf_prenom.setDisabled(true);
			tf_login.setDisabled(true);
			fu.setDisabled(false);
			b_valider.setDisabled(false);
		}
		else if (action.equals("manuel")) {
			l_prenom.setDisabled(false);
			l_nom.setDisabled(false);
			l_login.setDisabled(false);
			tf_nom.setDisabled(false);
			tf_prenom.setDisabled(false);
			tf_login.setDisabled(false);
			fu.setDisabled(true);            
		}
	}
}
