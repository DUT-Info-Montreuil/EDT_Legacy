package EDT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WBoxLayout;
import eu.webtoolkit.jwt.WComboBox;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WDialog;
import eu.webtoolkit.jwt.WLabel;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WSpinBox;
import eu.webtoolkit.jwt.WBoxLayout.Direction;

public class FormulairePromotion_wt extends WDialog {
	private EDTApplication_wt edtAppli;
	private WComboBox listePromotions,listeMatieres;
	private WPushButton b_effacerPromo;
	private WContainerWidget panel;
	private WBoxLayout grid;
	private String promo = "",matiere = "";
	private ArrayList<Groupe> groupes;
	private TreeSet<String> nomsPromos,nomMatieres;
	private HashMap<Integer,String[]> matieresInfos=null;
	private WLabel ar,nbCours,salles,profsM,dates,nbCoursT,nbPromos;
	private WSpinBox tf_ar;
	private TreeMap<String,Integer> matieres;
	FormulairePromotion_wt(EDTApplication_wt _edtAppli) {
		super("Gestion des promotions");
		edtAppli = _edtAppli;
		afficher();
	}

	public void afficher() {

		this.getContents().clear();

		panel = new WContainerWidget();
		grid = new WBoxLayout(Direction.TopToBottom);
		panel.setLayout(grid);
		getContents().addWidget(panel);

		nbPromos = new WLabel("Nombre de promos total : "+edtAppli.getControleurDb().getNbPromos());
		grid.addWidget(nbPromos); 
		
		listePromotions = new WComboBox();
		listePromotions.changed().addListener(listePromotions, new Signal.Listener() {
			public void trigger() {
				action("mettreAJour");				
			}
		});	
		grid.addWidget(listePromotions,0);
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
		
		dates = new WLabel("Dates : "+edtAppli.getControleurDb().datesCours(promo));
		grid.addWidget(dates); 
		
		nbCoursT = new WLabel("Nombre de cours total : "+edtAppli.getControleurDb().nbCoursT(promo));
		grid.addWidget(nbCoursT); 
		
		listeMatieres = new WComboBox();
		listeMatieres.changed().addListener(listeMatieres, new Signal.Listener() {
			public void trigger() {
				action("mettreAJourMatieres");				
			}
		});	
		grid.addWidget(listeMatieres);
		matieres = edtAppli.getControleurDb().promoMatieres(promo);
		nomMatieres = new TreeSet<String>();
		for (String id : matieres.keySet()) {
			if(!nomMatieres.contains(id)) {
				listeMatieres.addItem(id);   
				nomMatieres.add(id);
			}
		}
		matiere=listeMatieres.getCurrentText().getValue();

		nbCours = new WLabel("Nombre de cours pour cette matière :");
		grid.addWidget(nbCours); 
		salles = new WLabel("Salles de cours :");
		grid.addWidget(salles); 
		profsM = new WLabel("Profs :");
		grid.addWidget(profsM); 
		if(!matieres.isEmpty()) {
			matieresInfos = edtAppli.getControleurDb().infosMatiere(matieres.get(matiere),promo);
			if(matieresInfos.get(matieres.get(matiere))!=null) {
				if(matieresInfos.get(matieres.get(matiere))[0]!=null) {
					nbCours.setText("Nombre de cours pour cette matière : "+ matieresInfos.get(matieres.get(matiere))[0]);
				} 
				if(matieresInfos.get(matieres.get(matiere))[1]!=null) {
					salles.setText("Salles de cours : "+ matieresInfos.get(matieres.get(matiere))[1]);
				}
				if(matieresInfos.get(matieres.get(matiere))[2]!=null) {
					profsM.setText("Profs : "+ matieresInfos.get(matieres.get(matiere))[2]);
				}
			}
		}
		
		ar = new WLabel("Année de restriction :");
		grid.addWidget(ar);

		tf_ar = new WSpinBox();
		tf_ar.setValue(5);
		grid.addWidget(tf_ar);
		
		b_effacerPromo = new WPushButton("Effacer la promo "+promo);
		b_effacerPromo.clicked().addListener(b_effacerPromo, new Signal.Listener() {
			public void trigger() {
				action("effacerPromo");	
			}
		});
		b_effacerPromo.setStyleClass("btn-warning");
		grid.addWidget(b_effacerPromo);

		this.setResizable(false);
		this.setClosable(true);
		this.rejectWhenEscapePressed(false);
		this.resize(500, 450);
		this.show();
	}

	public void action(String b) {     	 
		if (b.equals("effacerPromo")) {
			final WDialog confirmerSuppr = new WDialog("Suppression de promotion");
			new WLabel(edtAppli.getControleurDb().checkPromo(promo), confirmerSuppr.getContents());
			WPushButton ok = new WPushButton("Oui", confirmerSuppr.getFooter());
			WPushButton annuler = new WPushButton("Non", confirmerSuppr.getFooter());
			ok.setStyleClass("btn-danger");
			annuler.setStyleClass("btn-primary");
			ok.clicked().addListener(ok, new Signal.Listener() {
				public void trigger() {
					confirmerSuppr.accept();
					if(!edtAppli.getControleurDb().effacerPromo(promo,tf_ar.getValue())) {
						Utilitaire.showMessageDialog(confirmerSuppr, "La promo n'as pas pu etre effacer car elle a moins de "+tf_ar.getValue()+" ans d'existence.");
					}else {
						String promoOld=promo;
						int index=listePromotions.getCurrentIndex();
						accept();
						afficher();	
						listePromotions.setCurrentIndex(index);
						nbPromos.setText("Nombre de promos total : "+edtAppli.getControleurDb().getNbPromos());
						action("mettreAJour");
						Utilitaire.showMessageDialog("Suppresion Promo","La promo "+promoOld+" a bien été suprimmer");
					}				
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

		} else if (b.equals("mettreAJour")) {	
			promo=listePromotions.getCurrentText().getValue();
			matieres.clear();
			nomMatieres.clear();
			listeMatieres.clear();
			dates.setText("Dates : "+edtAppli.getControleurDb().datesCours(promo));
			nbCoursT.setText("Nombre de cours total : "+edtAppli.getControleurDb().nbCoursT(promo));
			if(!matieres.isEmpty())matieresInfos.clear();
			matieres = edtAppli.getControleurDb().promoMatieres(promo);
			for (String id : matieres.keySet()) {
				if(!nomMatieres.contains(id)) {
					listeMatieres.addItem(id);   
					nomMatieres.add(id);
				}
			}
			matiere=listeMatieres.getCurrentText().getValue();			
			matieresInfos = edtAppli.getControleurDb().infosMatiere(matieres.get(matiere),promo);
			if(!matieres.isEmpty()) {
				matieresInfos = edtAppli.getControleurDb().infosMatieres(matieres,promo);
				matiere=listeMatieres.getCurrentText().getValue();
				if(matieresInfos.get(matieres.get(matiere))!=null) {
					if(matieresInfos.get(matieres.get(matiere))[0]!=null) {
						nbCours.setText("Nombre de cours pour cette matière : "+ matieresInfos.get(matieres.get(matiere))[0]);
					}else {
						nbCours.setText("Nombre de cours pour cette matière :");
					}
					if(matieresInfos.get(matieres.get(matiere))[1]!=null) {
						salles.setText("Salles de cours : "+ matieresInfos.get(matieres.get(matiere))[1]);
					}else {
						salles.setText("Salles de cours :");
					}
					if(matieresInfos.get(matieres.get(matiere))[2]!=null) {
						profsM.setText("Profs : "+ matieresInfos.get(matieres.get(matiere))[2]);
					}else {
						profsM.setText("Profs :");
					}
				}
			}else {
				nbCours.setText("Nombre de cours pour cette matière :");
				salles.setText("Salles de cours :");
				profsM.setText("Profs :");
			}
			b_effacerPromo.setText("Effacer la promo "+promo);
		}else if (b.equals("mettreAJourMatieres")) {	
			matiere=listeMatieres.getCurrentText().getValue();			
			matieresInfos = edtAppli.getControleurDb().infosMatiere(matieres.get(matiere),promo);			
			nbCours.setText("Nombre de cours pour cette matière : "+matieresInfos.get(matieres.get(matiere))[0]);
			salles.setText("Salles de cours : "+ matieresInfos.get(matieres.get(matiere))[1]);
			profsM.setText("Profs : "+ matieresInfos.get(matieres.get(matiere))[2]);	
		}else if (b.equals("Annuler")) {
			afficher();
		}
	}

}