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

public class FormulaireProfAdmin_wt extends WDialog {
	private EDTApplication_wt edtAppli;
	private WComboBox listeProfs;
	private WPushButton b_effacerProf;
	private WContainerWidget panel;
	private WBoxLayout grid;
	private WLabel ar,dates,nbCours,matieres,promos;
	private WSpinBox tf_ar;
	private TreeSet<Prof> profs;
	private TreeMap<String, Integer> nomsProfs;
	private HashMap<Integer,String[]> profsInfos=null;
	private String prof;
	FormulaireProfAdmin_wt(EDTApplication_wt _edtAppli) {
		super("Gestion des professeurs");
		edtAppli = _edtAppli;
		afficher();
	}

	public void afficher() {

		this.getContents().clear();

		panel = new WContainerWidget();
		grid = new WBoxLayout(Direction.TopToBottom);
		panel.setLayout(grid);
		getContents().addWidget(panel);

		listeProfs = new WComboBox();
		listeProfs.changed().addListener(listeProfs, new Signal.Listener() {
			public void trigger() {
				action("mettreAJour");				
			}
		});	
		grid.addWidget(listeProfs);
		profs = edtAppli.getControleurDb().getProfs();
		nomsProfs = new TreeMap<String,Integer>();
		for (Prof g : profs) {
			//prof = edtAppli.getControleurDb().getPromo();
			if(!nomsProfs.containsKey(g.getNom().toUpperCase()+" "+g.getPrenom())) {
				listeProfs.addItem(g.getNom().toUpperCase()+" "+g.getPrenom());   
				nomsProfs.put(g.getNom().toUpperCase()+" "+g.getPrenom(),g.getIdProf());
			}
		}
		prof=listeProfs.getCurrentText().getValue();

		dates = new WLabel("Dates : "+edtAppli.getControleurDb().datesCours(nomsProfs.get(prof)));
		grid.addWidget(dates); 

		nbCours = new WLabel("Nombre de cours : Aucun");
		grid.addWidget(nbCours); 
		
		matieres = new WLabel("Matieres : Aucune");
		grid.addWidget(matieres); 
		
		promos = new WLabel("Promos : Aucune");
		grid.addWidget(promos);
		
		if(!profs.isEmpty()) {
			profsInfos = edtAppli.getControleurDb().infosProf(nomsProfs.get(prof));
			if(profsInfos.get(nomsProfs.get(prof))!=null) {
				if(profsInfos.get(nomsProfs.get(prof))[0]!=null) {
					nbCours.setText("Nombre de cours : "+ profsInfos.get(nomsProfs.get(prof))[0]);
				} 
				if(profsInfos.get(nomsProfs.get(prof))[1]!=null) {
					matieres.setText("Matieres : "+ profsInfos.get(nomsProfs.get(prof))[1]);
				}
				if(profsInfos.get(nomsProfs.get(prof))[2]!=null) {
					promos.setText("Promos : "+ profsInfos.get(nomsProfs.get(prof))[2]);
				}
			}
		}
				
		ar = new WLabel("Ann??e de restriction :");
		grid.addWidget(ar);

		tf_ar = new WSpinBox();
		tf_ar.setValue(6);
		grid.addWidget(tf_ar);

		b_effacerProf = new WPushButton("Effacer le prof "+prof);
		b_effacerProf.clicked().addListener(b_effacerProf, new Signal.Listener() {
			public void trigger() {
				action("effacerProf");	
			}
		});
		b_effacerProf.setStyleClass("btn-warning");
		grid.addWidget(b_effacerProf);


		this.setResizable(false);
		this.setClosable(true);
		this.rejectWhenEscapePressed(false);
		this.resize(800, 370);
		this.show();
	}

	public void action(String b) {     	 
		if (b.equals("effacerProf")) {
			final WDialog confirmerSuppr = new WDialog("Suppression de prof");
			new WLabel(edtAppli.getControleurDb().checkProf(nomsProfs.get(prof)), confirmerSuppr.getContents());
			WPushButton ok = new WPushButton("Oui", confirmerSuppr.getFooter());
			WPushButton annuler = new WPushButton("Non", confirmerSuppr.getFooter());
			ok.setStyleClass("btn-danger");
			annuler.setStyleClass("btn-primary");
			ok.clicked().addListener(ok, new Signal.Listener() {
				public void trigger() {
					confirmerSuppr.accept();
					if(!edtAppli.getControleurDb().effacerProf(nomsProfs.get(prof),tf_ar.getValue())) {
						Utilitaire.showMessageDialog(confirmerSuppr, "Le prof n'as pas pu etre effacer car il a moins de "+tf_ar.getValue()+" ans d'existence.");
					}else {
						String profOld=prof;
						int index=listeProfs.getCurrentIndex();
						accept();
						afficher();	
						listeProfs.setCurrentIndex(index);
						action("mettreAJour");
						Utilitaire.showMessageDialog("Suppresion Prof","Le prof "+profOld+" a bien ??t?? suprimmer");
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
			prof = listeProfs.getCurrentText().getValue();
			profsInfos = edtAppli.getControleurDb().infosProf(nomsProfs.get(prof));
			dates.setText("Dates : "+edtAppli.getControleurDb().datesCours(nomsProfs.get(prof)));
			b_effacerProf.setText("Effacer le prof "+prof);
			if(!profs.isEmpty()) {
				if(profsInfos.get(nomsProfs.get(prof))!=null) {
					
					if(profsInfos.get(nomsProfs.get(prof))[0]!=null) {
						nbCours.setText("Nombre de cours : "+ profsInfos.get(nomsProfs.get(prof))[0]);
					}else nbCours.setText("Nombre de cours : Aucun");
					
					if(profsInfos.get(nomsProfs.get(prof))[1]!=null) {
						matieres.setText("Matieres : "+ profsInfos.get(nomsProfs.get(prof))[1]);
					}else matieres.setText("Matieres : Aucune");
					
					if(profsInfos.get(nomsProfs.get(prof))[2]!=null) {
						promos.setText("Promos : "+ profsInfos.get(nomsProfs.get(prof))[2]);
					}else promos.setText("Promos : Aucune");

				}
			}else {
				nbCours.setText("Nombre de cours : Aucun");
				matieres.setText("Matieres : Aucune");
				promos.setText("Promos : Aucune");
			}
		}else if (b.equals("Annuler")) {
			afficher();
		}
	}

}


