package EDT;

import java.util.TreeMap;

import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WBoxLayout;
import eu.webtoolkit.jwt.WComboBox;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WDialog;
import eu.webtoolkit.jwt.WLabel;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WSpinBox;
import eu.webtoolkit.jwt.WBoxLayout.Direction;

public class FormulaireCleanerAdmin_wt extends WDialog {
	private WPushButton b_cleanAll,b_cleanMatieres,b_cleanProfs,b_cleanPromos;
	private WContainerWidget panel;
	private WBoxLayout grid;
	private WLabel ar;
	private WSpinBox tf_ar;
	private EDTApplication_wt edtAppli;
	private TreeMap<Integer,String> listeC;
	
	FormulaireCleanerAdmin_wt(EDTApplication_wt _edtAppli) {
		super("Cleaner DB");
		edtAppli = _edtAppli;
		afficher();
	}

	public void afficher() {

		this.getContents().clear();

		panel = new WContainerWidget();
		grid = new WBoxLayout(Direction.TopToBottom);
		panel.setLayout(grid);
		getContents().addWidget(panel);

		b_cleanAll = new WPushButton("Tout Effacer");
		b_cleanAll.clicked().addListener(b_cleanAll, new Signal.Listener() {
			public void trigger() {
				action("Tout");	
			}
		});
		b_cleanAll.setStyleClass("btn-warning");
		grid.addWidget(b_cleanAll);


		b_cleanProfs = new WPushButton("Effacer les Profs NB : "+ edtAppli.getControleurDb().getNbProfs());
		b_cleanProfs.clicked().addListener(b_cleanProfs, new Signal.Listener() {
			public void trigger() {
				action("Profs");	
			}
		});
		b_cleanProfs.setStyleClass("btn-warning");
		grid.addWidget(b_cleanProfs);


		b_cleanMatieres = new WPushButton("Effacer les Matières NB : "+ edtAppli.getControleurDb().getNbMatieres());
		b_cleanMatieres.clicked().addListener(b_cleanMatieres, new Signal.Listener() {
			public void trigger() {
				action("Matieres");	
			}
		});
		b_cleanMatieres.setStyleClass("btn-warning");
		grid.addWidget(b_cleanMatieres);



		b_cleanPromos = new WPushButton("Effacer les Promos NB : "+ edtAppli.getControleurDb().getNbPromos());
		b_cleanPromos.clicked().addListener(b_cleanPromos, new Signal.Listener() {
			public void trigger() {
				action("Promos");	
			}
		});
		b_cleanPromos.setStyleClass("btn-warning");
		grid.addWidget(b_cleanPromos);


		ar = new WLabel("Année de restriction :");
		grid.addWidget(ar);
		tf_ar = new WSpinBox();
		tf_ar.setValue(10);
		grid.addWidget(tf_ar);



		this.setResizable(false);
		this.setClosable(true);
		this.rejectWhenEscapePressed(false);
		this.resize(300, 300);
		this.show();
	}

	public void action(String b) {
		final WDialog confirmerSuppr = new WDialog("Cleaner "+b);
		new WLabel("Etes-vous sûr de vouloir clean "+b+" d'il y-a "+tf_ar.getText()+" ans ?",confirmerSuppr.getContents());
		if(!b.equals("Profs")) {
			new WLabel("Les éléments vides ou pas utiliser seront aussi effacé.",confirmerSuppr.getFooter()).setAttributeValue("style", "float:left;margin:10px;");
		}
		if(!b.equals("Tout")) {
			listeC = new TreeMap<Integer,String>();
			if (b.equals("Profs")) {	
				listeC = edtAppli.getControleurDb().getProfs3(Integer.parseInt(tf_ar.getText()));
			}else if (b.equals("Matieres")) {	
				listeC = edtAppli.getControleurDb().getMatieres3(Integer.parseInt(tf_ar.getText()));
			}else if (b.equals("Promos")) {	
				listeC = edtAppli.getControleurDb().getPromotions3(Integer.parseInt(tf_ar.getText()));
			}
			
			WComboBox liste = new WComboBox(confirmerSuppr.getContents());
			liste.setAttributeValue("style", "margin:10px;");		
			
			if(!listeC.isEmpty()) {
				for(Integer i:listeC.keySet()) {
						liste.addItem(listeC.get(i));
				}
			}
			
			new WLabel("Nb : "+liste.getCount(),confirmerSuppr.getContents());
		}
		WPushButton ok = new WPushButton("Oui", confirmerSuppr.getFooter());
		WPushButton annuler = new WPushButton("Non", confirmerSuppr.getFooter());
		
		ok.setStyleClass("btn-danger");
		annuler.setStyleClass("btn-primary");
		ok.clicked().addListener(ok, new Signal.Listener() {
			public void trigger() {
				confirmerSuppr.accept();
				int compteur=0;
				if (b.equals("Tout")) {
					
					listeC = edtAppli.getControleurDb().getProfs3(Integer.parseInt(tf_ar.getText()));
					for(Integer l : listeC.keySet()) {
						if(edtAppli.getControleurDb().effacerProf2(l,Integer.parseInt(tf_ar.getText())))compteur++;
					}
					
					listeC.clear();
					listeC = edtAppli.getControleurDb().getMatieres3(Integer.parseInt(tf_ar.getText()));
					for(Integer l : listeC.keySet()) {
						if(edtAppli.getControleurDb().effacerMatiere2(l,Integer.parseInt(tf_ar.getText())))compteur++;
					}
					
					listeC.clear();
					listeC = edtAppli.getControleurDb().getPromotions3(Integer.parseInt(tf_ar.getText()));
					for(Integer l : listeC.keySet()) {
						if(edtAppli.getControleurDb().effacerPromo2(l,Integer.parseInt(tf_ar.getText())))compteur++;
					}
					
				}else if (b.equals("Profs")) {	
					
					for(Integer l : listeC.keySet()) {
						if(edtAppli.getControleurDb().effacerProf2(l,Integer.parseInt(tf_ar.getText())))compteur++;
					}
					
				}else if (b.equals("Matieres")) {	
					
					for(Integer l : listeC.keySet()) {
						if(edtAppli.getControleurDb().effacerMatiere2(l,Integer.parseInt(tf_ar.getText())))compteur++;
					}
					
				}else if (b.equals("Promos")) {	
					
					for(Integer l : listeC.keySet()) {
						if(edtAppli.getControleurDb().effacerPromo2(l,Integer.parseInt(tf_ar.getText())))compteur++;
					}
					
				}			
				Utilitaire.showMessageDialog("Suppresion",compteur+" "+b+" on été suprimé");
				b_cleanProfs.setText("Effacer les Profs NB :"+ edtAppli.getControleurDb().getNbProfs());
				b_cleanPromos.setText("Effacer les Promos NB :"+ edtAppli.getControleurDb().getNbPromos());
				b_cleanMatieres.setText("Effacer les Matières NB :"+ edtAppli.getControleurDb().getNbMatieres());

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

}