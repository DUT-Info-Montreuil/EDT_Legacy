package EDT;

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

public class FormulaireMatiereAdmin_wt extends WDialog {
	private EDTApplication_wt edtAppli;
	private WComboBox listeMatieres;
	private WPushButton b_effacerMatiere;
	private WContainerWidget panel;
	private WBoxLayout grid;
	private WLabel dates,profs,promos,nbCours,ar;
	private WSpinBox tf_ar;
	private TreeSet<Matiere> matieresN;
	private TreeMap<String, Integer> nomsMatieres;
	private String matiere;
	private HashMap<Integer,String[]> matiereInfos=null;

	FormulaireMatiereAdmin_wt(EDTApplication_wt _edtAppli) {
		super("Gestion des matières");
		edtAppli = _edtAppli;
		afficher();
	}

	public void afficher() {

		this.getContents().clear();

		panel = new WContainerWidget();
		grid = new WBoxLayout(Direction.TopToBottom);
		panel.setLayout(grid);
		getContents().addWidget(panel);

		listeMatieres = new WComboBox();
		listeMatieres.changed().addListener(listeMatieres, new Signal.Listener() {
			public void trigger() {
				action("mettreAJour");				
			}
		});	
		grid.addWidget(listeMatieres);
		matieresN = edtAppli.getControleurDb().getMatieres();
		nomsMatieres = new TreeMap<String,Integer>();
		for (Matiere g : matieresN) {
			//prof = edtAppli.getControleurDb().getPromo();
			if(!nomsMatieres.containsKey(g.getNom())) {
				listeMatieres.addItem(g.getNom());   
				nomsMatieres.put(g.getNom(),g.getIdMatiere());
			}
		}
		matiere=listeMatieres.getCurrentText().getValue();

		dates = new WLabel("Dates : "+edtAppli.getControleurDb().datesMatieres(nomsMatieres.get(matiere)));
		grid.addWidget(dates); 
		
		
		nbCours = new WLabel("Nombre de cours : Aucun");
		grid.addWidget(nbCours); 
		
		profs = new WLabel("Matieres : Aucune");
		grid.addWidget(profs); 
		
		promos = new WLabel("Promos : Aucune");
		grid.addWidget(promos);
		
		if(!nomsMatieres.isEmpty()) {
			matiereInfos=edtAppli.getControleurDb().infosMatieres2(nomsMatieres.get(matiere));
			if(matiereInfos.get(nomsMatieres.get(matiere))!=null) {
				if(matiereInfos.get(nomsMatieres.get(matiere))[0]!=null) {
					nbCours.setText("Nombre de cours : "+ matiereInfos.get(nomsMatieres.get(matiere))[0]);
				} 
				if(matiereInfos.get(nomsMatieres.get(matiere))[1]!=null) {
					profs.setText("Matieres : "+ matiereInfos.get(nomsMatieres.get(matiere))[1]);
				}
				if(matiereInfos.get(nomsMatieres.get(matiere))[2]!=null) {
					promos.setText("Promos : "+ matiereInfos.get(nomsMatieres.get(matiere))[2]);
				}
			}
		}
		
		ar = new WLabel("Année de restriction :");
		grid.addWidget(ar);
		tf_ar = new WSpinBox();
		tf_ar.setValue(6);
		grid.addWidget(tf_ar);
		
		b_effacerMatiere = new WPushButton("Effacer la matière "+matiere);
		b_effacerMatiere.clicked().addListener(b_effacerMatiere, new Signal.Listener() {
			public void trigger() {
				action("effacerMatiere");	
			}
		});
		b_effacerMatiere.setStyleClass("btn-warning");
		grid.addWidget(b_effacerMatiere);


		this.setResizable(false);
		this.setClosable(true);
		this.rejectWhenEscapePressed(false);
		this.resize(800, 355);
		this.show();
	}

	public void action(String b) {     	 
		if (b.equals("effacerMatiere")) {
			final WDialog confirmerSuppr = new WDialog("Suppression de matiere");
			new WLabel(edtAppli.getControleurDb().checkMatiere(nomsMatieres.get(matiere)), confirmerSuppr.getContents());
			WPushButton ok = new WPushButton("Oui", confirmerSuppr.getFooter());
			WPushButton annuler = new WPushButton("Non", confirmerSuppr.getFooter());
			ok.setStyleClass("btn-danger");
			annuler.setStyleClass("btn-primary");
			ok.clicked().addListener(ok, new Signal.Listener() {
				public void trigger() {
					confirmerSuppr.accept();
					if(!edtAppli.getControleurDb().effacerMatiere(nomsMatieres.get(matiere),tf_ar.getValue())) {
						Utilitaire.showMessageDialog(confirmerSuppr, "La matière n'as pas pu etre effacer car elle a moins de "+tf_ar.getValue()+" ans d'existence.");
					}else {
						String mOld=matiere;
						int index=listeMatieres.getCurrentIndex();
						accept();
						afficher();	
						listeMatieres.setCurrentIndex(index);
						action("mettreAJour");
						Utilitaire.showMessageDialog("Suppresion Matière","La matière "+mOld+" a bien été suprimmer");
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
			matiere = listeMatieres.getCurrentText().getValue();
			edtAppli.getControleurDb().infosProf(nomsMatieres.get(matiere));
			dates.setText("Dates : "+edtAppli.getControleurDb().datesMatieres(nomsMatieres.get(matiere)));
			
			if(!nomsMatieres.isEmpty()) {
				matiereInfos=edtAppli.getControleurDb().infosMatieres2(nomsMatieres.get(matiere));
				if(matiereInfos.get(nomsMatieres.get(matiere))!=null) {
					if(matiereInfos.get(nomsMatieres.get(matiere))[0]!=null) {
						nbCours.setText("Nombre de cours : "+ matiereInfos.get(nomsMatieres.get(matiere))[0]);
					} 
					if(matiereInfos.get(nomsMatieres.get(matiere))[1]!=null) {
						profs.setText("Profs : "+ matiereInfos.get(nomsMatieres.get(matiere))[1]);
					}
					if(matiereInfos.get(nomsMatieres.get(matiere))[2]!=null) {
						promos.setText("Promos : "+ matiereInfos.get(nomsMatieres.get(matiere))[2]);
					}
				}
			}
			b_effacerMatiere.setText("Effacer la matière "+matiere);
		}else if (b.equals("Annuler")) {
			afficher();
		}
	}

}


