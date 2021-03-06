package EDT;

import eu.webtoolkit.jwt.WDialog;
import eu.webtoolkit.jwt.WLabel;
import eu.webtoolkit.jwt.WLineEdit;
import eu.webtoolkit.jwt.WCheckBox;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WGridLayout;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WRegExpValidator;
import eu.webtoolkit.jwt.WSpinBox;
import eu.webtoolkit.jwt.WValidator;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WBorderLayout;
import eu.webtoolkit.jwt.WLength;
import eu.webtoolkit.jwt.WComboBox;
import java.util.*;

/**
 * formulaire de gestion des groupes
 */

public class FormulaireGroupes_wt extends WDialog {
	private EDTApplication_wt edtAppli;
	private WLabel wTps,wTds;
	private WLineEdit tf_nomCours,tf_newNomPromo;
	private WLineEdit[]tf_nomTD,tf_nomTP;
	private WCheckBox cb_groupeVisible,cb_comptabiliserServices;
	private WRegExpValidator validator = new WRegExpValidator("[A-Za-z][A-Za-z0-9- _]*[A-Za-z0-9]"); //pour valider le nom promo
	private WComboBox listePromotions;
	private WPushButton b_renommerPromo,b_ajouterPromo;
	private WContainerWidget panel;
	private WGridLayout grid;
	private ArrayList<Groupe> groupes;
	private TreeSet<Groupe> groupesPromo;
	private TreeSet<String> nomsPromos;
	private String promo = "";
	private String promoArenommer,tpsS,tdsS;
	private int nbTD, nbTP;
	private boolean promoVisible,promoComptabiliserServices;
	private WDialog dialog ;

	FormulaireGroupes_wt(EDTApplication_wt _edtAppli) {
		super("Gestion des promos");
		edtAppli = _edtAppli;
		afficher();
	}


	public void afficher() {

		this.getContents().clear();

		panel = new WContainerWidget();
		grid = new WGridLayout();
		panel.setLayout(grid);
		getContents().addWidget(panel);

		b_ajouterPromo = new WPushButton("Ajouter une Promo");
		b_ajouterPromo.clicked().addListener(b_ajouterPromo, new Signal.Listener() {
			public void trigger() {
				action("ajouterPromo");	
			}
		});
		b_ajouterPromo.setStyleClass("btn-primary");
		grid.addWidget(b_ajouterPromo,0, 0);
		
		listePromotions = new WComboBox();
		listePromotions.changed().addListener(listePromotions, new Signal.Listener() {
			public void trigger() {
				action("mettreAJour");				
			}
		});	
		grid.addWidget(listePromotions, 1, 0);

		groupes = edtAppli.getControleurDb().getGroupes(edtAppli.getControleurDb().getIdAdmin());
		nomsPromos = new TreeSet<String>();
		for (Groupe g : groupes) {
			promo = edtAppli.getControleurDb().getPromo(g.getIdGroupe());
			if(!nomsPromos.contains(promo)) {
				listePromotions.addItem(promo);   
				nomsPromos.add(promo);
			}
		}

		promo=listePromotions.getCurrentText().getValue();
		promoVisible=edtAppli.getControleurDb().getVisibilitePromo(promo);     

		cb_groupeVisible=new WCheckBox();              
		cb_groupeVisible.changed().addListener(cb_groupeVisible, new Signal.Listener() {
			public void trigger() {
				action("groupeVisible");	
			}
		});
		cb_groupeVisible.setChecked(promoVisible);
		cb_groupeVisible.setText("groupe visible");
		grid.addWidget(cb_groupeVisible, 2, 0);


		promoComptabiliserServices=edtAppli.getControleurDb().getComptabiliserServicesPromo(promo);
		if (promo.equals("RESERVATIONS"))System.out.println(promoComptabiliserServices);

		cb_comptabiliserServices=new WCheckBox();
		cb_comptabiliserServices.changed().addListener(cb_comptabiliserServices, new Signal.Listener() {
			public void trigger() {
				action("comptabiliserServices");	
			}
		});
		cb_comptabiliserServices.setChecked(promoComptabiliserServices);
		cb_comptabiliserServices.setText("comptabiliser les services enseignants");
		grid.addWidget(cb_comptabiliserServices, 3, 0);


		groupesPromo = edtAppli.getControleurDb().getGroupes2(promo);

		tdsS="TDs :";
		tpsS="TPs :";
		for(Groupe gp : groupesPromo) {
			if(gp.getType().equals("TD")) {
				tdsS+=" "+gp.getNom();
			}else if(gp.getType().equals("TP")){
				tpsS+=" "+gp.getNom();
			}
		}
		wTds = new WLabel(tdsS);
		grid.addWidget(wTds, 4, 0);
		wTps = new WLabel(tpsS);
		grid.addWidget(wTps, 5, 0);

		//espace a la ligne avant le button
		WContainerWidget container = new WContainerWidget();
		container.resize(10, 1);
		grid.addWidget(container, 6, 0);

		b_renommerPromo = new WPushButton("Renommer la promo "+promo);
		b_renommerPromo.clicked().addListener(b_renommerPromo, new Signal.Listener() {
			public void trigger() {
				promoArenommer = b_renommerPromo.getText().toString();
				action("renommerPromo");	
			}
		});
		b_renommerPromo.setStyleClass("btn-info");
		grid.addWidget(b_renommerPromo,7, 0);

		if (edtAppli.loginEDT.compareTo("hassoun")!=0) {
			b_ajouterPromo.disable();
			b_renommerPromo.disable();
			b_ajouterPromo.setText("Ajouter une Promo : Veuillez Contacter le ccri.");
			b_renommerPromo.setText("Renommer la Promo : Veuillez Contacter le ccri.");
		}

		this.setResizable(true);
		this.setClosable(true);
		this.rejectWhenEscapePressed(true);
		this.resize(500, 330);
		this.show();
	}

	public void action(String b) {     	 
		if (b.equals("renommerPromo")) { 
			WDialog dialog = new WDialog(promoArenommer);
			dialog.resize(500, 300);
			WLineEdit edit = new WLineEdit(dialog.getContents());
			edit.setText(promo);
			edit.setToolTip("Entrer le nouveau nom ici, puis valider");                
			edit.setWidth(new WLength(400));
			validator.setMandatory(true);
			edit.setValidator(validator);
			WPushButton ok = new WPushButton("Valider", dialog.getFooter());
			ok.setDisabled(true);
			ok.setDefault(true);
			ok.setStyleClass("btn-info");
			ok.setFocus();
			WPushButton cancel = new WPushButton("Annuler", dialog.getFooter());
			cancel.setStyleClass("btn-primary");
			dialog.rejectWhenEscapePressed();
			edit.keyWentUp().addListener(this, new Signal.Listener() {
				public void trigger() {
					ok.setDisabled(edit.validate() != WValidator.State.Valid);
				}                
			});
			cancel.clicked().addListener(dialog, new Signal1.Listener<WMouseEvent>() {
				public void trigger(WMouseEvent e1) {
					dialog.reject();
				}
			});
			ok.clicked().addListener(this, new Signal.Listener() {
				public void trigger() {
					if (edit.validate() != null) {
						dialog.accept();
					}
				}
			});
			dialog.show();
			dialog.finished().addListener(this, new Signal.Listener() {
				public void trigger() {
					if (dialog.getResult() == WDialog.DialogCode.Accepted) {
						String nouveauNom = edit.getText().replaceAll("[^-_.() ????????A-Za-z0-9]","");
						boolean nomOK = true;

						TreeSet<Groupe> listeGroupes = edtAppli.getControleurDb().getGroupes();
						for (Iterator<Groupe> iter = listeGroupes.iterator(); iter.hasNext();) {
							Groupe g = iter.next();
							if (edtAppli.getControleurDb().getPromo(g.getIdGroupe()).equalsIgnoreCase(nouveauNom)) {	
								nomOK = false;
								break;
							}
						}
						if (!nomOK) {
							Utilitaire.showMessageDialog(dialog.getWindowTitle().toString(), "Le nom de promo '"+nouveauNom+"' existe d??j??...");
							nouveauNom=null;
						}
						if (nouveauNom.length() > 30) {
							Utilitaire.showMessageDialog(dialog.getWindowTitle().toString(), "Pas plus de 30 caract??res pour la promo");
							nouveauNom=null;
						}


						if (nouveauNom != null && !nouveauNom.equals(promo)) {
							edtAppli.getControleurDb().modifierNomPromo(promo, nouveauNom);
							afficher();
						}						
					}
					if (dialog != null)
						dialog.remove();                		
				}     		
			});
		} else if (b.equals("groupeVisible")) {	
			edtAppli.getControleurDb().modifierVisibilitePromo(promo,cb_groupeVisible.isChecked());
		} else if (b.equals("comptabiliserServices")) {	
			edtAppli.getControleurDb().modifierComptabiliserServicesPromo(promo,cb_comptabiliserServices.isChecked());   
		} else if (b.equals("mettreAJour")) {	
			promo=listePromotions.getCurrentText().getValue();
			promoVisible=edtAppli.getControleurDb().getVisibilitePromo(promo);       
			cb_groupeVisible.setChecked(promoVisible);
			cb_groupeVisible.setText("groupe visible");
			promoComptabiliserServices=edtAppli.getControleurDb().getComptabiliserServicesPromo(promo);
			if (promo.equals("RESERVATIONS"))System.out.println(promoComptabiliserServices);
			cb_comptabiliserServices.setChecked(promoComptabiliserServices);
			cb_comptabiliserServices.setText("comptabiliser les services enseignants");
			groupesPromo = edtAppli.getControleurDb().getGroupes2(promo);
			tpsS="TPs :";
			tdsS="TDs :";
			for(Groupe gp : groupesPromo) {
				if(gp.getType().equals("TD")) {
					tdsS+=" "+gp.getNom();
				}else if(gp.getType().equals("TP")){
					tpsS+=" "+gp.getNom();
				}
			}
			wTps.setText(tpsS);
			wTds.setText(tdsS);
			b_renommerPromo.setText("Renommer la promo "+promo);
		} else if (b.equals("ajouterPromo")) {	
			dialog = new WDialog("Ajouter Promo");
			dialog.resize(500, 300);

			WContainerWidget panel2 = new WContainerWidget();
			WGridLayout grid2 = new WGridLayout();
			panel2.setLayout(grid2);
			dialog.getContents().addWidget(panel2);

			WLabel nom = new WLabel("Nom promo :");
			grid2.addWidget(nom,0,0);
			tf_newNomPromo = new WLineEdit();
			grid2.addWidget(tf_newNomPromo,0,1);
			
			WLabel nbTd = new WLabel("Nombre de TDs :");
			grid2.addWidget(nbTd,2,0);
			WSpinBox tf_td = new WSpinBox();
			tf_td.setMaximum(10);
			tf_td.setMaxLength(2);
			grid2.addWidget(tf_td,2,1);
			
			WLabel nbTp = new WLabel("Nombre de TPs :");
			grid2.addWidget(nbTp,3,0);
			WSpinBox tf_tp = new WSpinBox();
			tf_tp.setMaximum(10);
			tf_tp.setMaxLength(2);
			grid2.addWidget(tf_tp,3,1);

			WPushButton ok = new WPushButton("Valider", dialog.getFooter());
			ok.setStyleClass("btn-info");
			ok.clicked().addListener(this, new Signal.Listener() {
				public void trigger() {
					try {
						nbTD = Integer.parseInt(tf_td.getValueText());
						nbTP = Integer.parseInt(tf_tp.getValueText());
					} catch (NumberFormatException exc) {
						Utilitaire.showMessageDialog("Attention ERREUR :", "Nombres incorrectes !");
						return;
					}
					if (tf_newNomPromo.getText().length()<1) {
						Utilitaire.showMessageDialog("Attention ERREUR :", "Veuillez taper le nom de la promo");
						return;
					}
					String nouveauNom = tf_newNomPromo.getText().replaceAll("[^-_.() ????????A-Za-z0-9]","");
					if (nbTD > nbTP) {
						Utilitaire.showMessageDialog("Attention ERREUR :", "Il doit y avoir plus de TP que de TD");
						return;
					}else if (nbTD<1 || nbTP<1) {
						Utilitaire.showMessageDialog("Attention ERREUR :", "Les tps et tds ne doivent pas etre nul");
						return;
					}
					boolean saisiesOK = true;
					TreeSet<Groupe> listeGroupes = edtAppli.getControleurDb().getGroupes();
					for (Iterator<Groupe> iter = listeGroupes.iterator(); iter.hasNext();) {
						Groupe g = iter.next();
						if (edtAppli.getControleurDb().getPromo(g.getIdGroupe()).equals(nouveauNom)) {
							Utilitaire.showMessageDialog("nouvelpromo", ""+edtAppli.getControleurDb().getPromo(g.getIdGroupe())+"="+nouveauNom);
							break;
						}
					} 
					if (!saisiesOK) {
						Utilitaire.showMessageDialog("Attention ERREUR :", "Le nom de promo existe d??j??a...");
						return;
					}
					if (nouveauNom.length() > 30) {
						Utilitaire.showMessageDialog("Attention ERREUR :", "Pas plus de 30 caract??res pour le nom de la promo");
						return;
					}
					
					int ppcmTDTP = nbTD * nbTP / Utilitaire.pgcd(nbTD, nbTP);

					tf_nomTD = new WLineEdit[nbTD];
					tf_nomTP = new WLineEdit[nbTP];
					
					panel2.clear();
					dialog.getFooter().clear();

					WBorderLayout border = new WBorderLayout();
					panel2.setLayout(border);
					WGridLayout gridNouvellePromo = new WGridLayout();
					panel2.setLayout(gridNouvellePromo);
					WLabel label = new WLabel("LABEL");
					gridNouvellePromo.addWidget(label, 0, 0);
					for (int i = 0; i < ppcmTDTP; i++) {
						new WLabel("LABEL");
					}			           
					label = new WLabel("Cours :");
					gridNouvellePromo.addWidget(label, 0, 0);
					tf_nomCours = new WLineEdit("Cours");
					tf_nomCours.setAttributeValue("border-width", "1");
					gridNouvellePromo.addWidget(tf_nomCours, 0, 1);
					label = new WLabel("TD :");
					gridNouvellePromo.addWidget(label, 1, 0);
					for (int i = 0; i < nbTD; i++) {
						tf_nomTD[i] = new WLineEdit("TD" + (char) ('A' + i));
						gridNouvellePromo.addWidget(tf_nomTD[i], 1, i * 1 + 1 );
					}
					label = new WLabel("TP :");
					gridNouvellePromo.addWidget(label, 2, 0);
					for (int i = 0; i < nbTP; i++) {
						tf_nomTP[i] = new WLineEdit("TP" + (i + 1));
						gridNouvellePromo.addWidget(tf_nomTP[i], 2, i + 1);
					}

					WPushButton b_annuler = new WPushButton("Annuler",dialog.getFooter());
					b_annuler.setStyleClass("btn-primary");
					b_annuler.clicked().addListener(b_annuler, new Signal.Listener() {
						public void trigger() {
							dialog.remove();                		
						}
					});
					WPushButton b_creerPromoEtape2 = new WPushButton("Cr??er les groupes",dialog.getFooter());
					b_creerPromoEtape2.setStyleClass("btn-info");
					b_creerPromoEtape2.clicked().addListener(b_creerPromoEtape2, new Signal.Listener() {
						public void trigger() {
							dialog.accept();
							action("Creation-groupe");
						}
					});
				}
			});
			WPushButton cancel = new WPushButton("Annuler", dialog.getFooter());
			cancel.setStyleClass("btn-primary");		
			cancel.clicked().addListener(dialog, new Signal1.Listener<WMouseEvent>() {
				public void trigger(WMouseEvent e1) {
					dialog.remove();                		
				}
			});
			dialog.show();			
		} else if (b.equals("Creation-groupe")) {
			boolean saisiesOK = true;
			boolean tropLong = false;
			if (tf_nomCours.getText().isEmpty())
				saisiesOK = false;
			for (int i = 0; i < tf_nomTD.length; i++) {
				if (tf_nomTD[i].getText().equals(""))
					saisiesOK = false;
				if (tf_nomTD[i].getText().length() > 15)
					tropLong = true;
			}
			for (int i = 0; i < tf_nomTP.length; i++) {
				if (tf_nomTP[i].getText().equals(""))
					saisiesOK = false;
				if (tf_nomTP[i].getText().length() > 15)
					tropLong = true;
			}
			if (!saisiesOK){
				Utilitaire.showMessageDialog(this, "Il faut remplir tous les champs");
				return;
			}
			else if (tropLong){
				Utilitaire.showMessageDialog(this, "Pas plus de 15 caract??res pour les noms...");
				return;
			}
			else {
				// ?? quel d??partement appartient ce groupe ?
				int id_departement=edtAppli.getControleurDb().getIdDepartement();
				// on trouve un identifiant pour cette promo
				int id_promo=edtAppli.getControleurDb().getIdPromo();
				int ppcmTDTP = nbTD * nbTP / Utilitaire.pgcd(nbTD, nbTP);
				String nouveauNom = tf_newNomPromo.getText().replaceAll("[^-_.() ????????A-Za-z0-9]","");

				int niveau = 0;
				String erreurInsertion = "";
				// insertion de la promo
				erreurInsertion += edtAppli.getControleurDb().ajouterPromo(nouveauNom, id_departement, true, id_promo);
				// insertion du cours
				erreurInsertion += edtAppli.getControleurDb().ajouterGroupe(tf_nomCours.getText(), ppcmTDTP, 0, niveau, "Cours", id_departement, id_promo);

				// insertion des TDs
				niveau++;
				int nbCol = ppcmTDTP / nbTD;
				for (int i = 0; i < tf_nomTD.length; i++)
					erreurInsertion += edtAppli.getControleurDb().ajouterGroupe(tf_nomTD[i].getText(), nbCol, i * nbCol, niveau, "TD", id_departement, id_promo);
				// insertion des TPs
				niveau++;
				nbCol = ppcmTDTP / nbTP;
				for (int i = 0; i < tf_nomTP.length; i++)
					erreurInsertion += edtAppli.getControleurDb().ajouterGroupe(tf_nomTP[i].getText(), nbCol, i * nbCol, niveau, "TP", id_departement, id_promo);
				if (!erreurInsertion.equals("")) {
					Utilitaire.showMessageDialog(this, erreurInsertion);
					return;
				}
				afficher();
			}
		}else if (b.equals("Annuler"))
			afficher();
	}

}
