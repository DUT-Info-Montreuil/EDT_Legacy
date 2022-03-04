package EDT;

import java.util.TreeMap;
import java.util.TreeSet;

import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WBoxLayout;
import eu.webtoolkit.jwt.WBoxLayout.Direction;
import eu.webtoolkit.jwt.WComboBox;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WDialog;
import eu.webtoolkit.jwt.WLabel;
import eu.webtoolkit.jwt.WPushButton;

public class FormulaireMatieres_wt extends WDialog {	
	EDTApplication_wt edtAppli;

	WDialog dialog = this;
	private WComboBox listeMatieres;
	private TreeMap<String,Matiere> matieres;
	private WPushButton b_ajouter,b_supprimer,b_modifier;
	private TreeSet<String> nomsMatieres;
	private Matiere matiere;
	FormulaireMatieres_wt(EDTApplication_wt _edtAppli) {
		super("Gestion des matières");

		edtAppli=_edtAppli;

		afficher();
	}
	public void afficher() {
		this.getFooter().clear();
		this.getContents().clear();

		WContainerWidget panelMatieres = new WContainerWidget();
		WBoxLayout grid = new WBoxLayout(Direction.TopToBottom);
		panelMatieres.setLayout(grid);

		b_ajouter=new WPushButton("Ajouter");
		b_ajouter.clicked().addListener(b_ajouter, new Signal.Listener() {
			public void trigger() {
				accept();
				new FormulaireSaisieMatiere_wt(edtAppli, null, dialog);
			}
		});
		b_ajouter.setStyleClass("btn-success");
		getFooter().addWidget(b_ajouter);

		listeMatieres = new WComboBox();
		listeMatieres.setAttributeValue("style", "text-align:center;");

		matieres = new TreeMap<String,Matiere>();
		
		//      TreeMap<Integer,String> promos=edtAppli.getControleurDb().getPromotions2(edtAppli.getControleurDb().getIdAdmin());
		//		for(Integer i : promos.keySet()) {
		//			listeMatieres.addItem("-----"+promos.get(i)+"-----");   
		//			matieres[i] = edtAppli.getControleurDb().getMatieresPromo3(i);
		//			nomsMatieres = new TreeSet<String>();
		//			for(String m: matieres[i].keySet()) {
		//				if(!nomsMatieres.contains(m)) {
		//					listeMatieres.addItem(m);   
		//					nomsMatieres.add(m);
		//				}
		//			}
		//
		//		}
		//		if(!listeMatieres.getCurrentText().getValue().startsWith("-----")) {
		//			matiere=matieres.get(listeMatieres.getCurrentText().getValue());
		//		}
		//		grid.addWidget(listeMatieres);
		//		listeMatieres.changed().addListener(listeMatieres, new Signal.Listener() {
		//			public void trigger() {
		//				if(!listeMatieres.getCurrentText().getValue().startsWith("-----")) {
		//					b_modifier.enable();
		//					b_supprimer.enable();
		//					matiere=matieres.get(listeMatieres.getCurrentText().getValue());
		//				}else {
		//					b_modifier.disable();
		//					b_supprimer.disable();
		//				}
		//			}
		//		});	

		matieres=edtAppli.getControleurDb().getMatieresAdmin2(edtAppli.getControleurDb().getIdAdmin());
		nomsMatieres = new TreeSet<String>();
		for(String m: matieres.keySet()) {
			if(!nomsMatieres.contains(m)) {
				listeMatieres.addItem(m);   
				nomsMatieres.add(m);
			}
		}
		matiere=matieres.get(listeMatieres.getCurrentText().getValue());
		grid.addWidget(listeMatieres);
		listeMatieres.changed().addListener(listeMatieres, new Signal.Listener() {
			public void trigger() {
				matiere=matieres.get(listeMatieres.getCurrentText().getValue());
			}
		});	


		b_modifier=new WPushButton("Modifier");// "+matiere.getNom());
		b_modifier.clicked().addListener(b_modifier, new Signal.Listener() {
			public void trigger() {
				accept();
				new FormulaireSaisieMatiere_wt(edtAppli, matiere, dialog);            				
			}
		});
		b_modifier.setStyleClass("btn-info"); 
		b_modifier.setAttributeValue("style", "margin-right:50px;margin-left:50px;margin-top:10px;");
		grid.addWidget(b_modifier);

		b_supprimer=new WPushButton("Supprimer");// "+matiere.getNom());            
		b_supprimer.clicked().addListener(b_supprimer, new Signal.Listener() {
			public void trigger() {
				final WDialog confirmerSuppr = new WDialog("Suppression de matière");
				new WLabel("Confirmez-vous la suppression de "+matiere.getNom()+" ?", confirmerSuppr.getContents());
				WPushButton ok = new WPushButton("Oui", confirmerSuppr.getFooter());
				WPushButton annuler = new WPushButton("Non", confirmerSuppr.getFooter());
				ok.setStyleClass("btn-danger");
				annuler.setStyleClass("btn-primary");
				ok.clicked().addListener(ok, new Signal.Listener() {
					public void trigger() {
						confirmerSuppr.accept();
						edtAppli.getControleurDb().supprimerMatiere(matiere);
						int index=listeMatieres.getCurrentIndex();
						afficher();							
						listeMatieres.setCurrentIndex(index);
						matiere=matieres.get(listeMatieres.getCurrentText().getValue());
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
		b_supprimer.setStyleClass("btn-warning");
		b_supprimer.setAttributeValue("style", "margin-right:50px;margin-left:50px;margin-top:10px;");
		grid.addWidget(b_supprimer);

		//		if(!listeMatieres.getCurrentText().getValue().startsWith("-----")) {
		//			b_modifier.enable();
		//			b_supprimer.enable();
		//		}else {
		//			b_modifier.disable();
		//			b_supprimer.disable();
		//		}

		getContents().addWidget(panelMatieres);
		this.setResizable(true);
		this.setClosable(true);
		this.rejectWhenEscapePressed(true);
		this.resize(500, 300);
		this.show();		
	} 

}
//package EDT;
//
//import java.util.TreeMap;
//import java.util.TreeSet;
//
//import eu.webtoolkit.jwt.Signal;
//import eu.webtoolkit.jwt.WBoxLayout;
//import eu.webtoolkit.jwt.WBoxLayout.Direction;
//import eu.webtoolkit.jwt.WColor;
//import eu.webtoolkit.jwt.WComboBox;
//import eu.webtoolkit.jwt.WContainerWidget;
//import eu.webtoolkit.jwt.WDialog;
//import eu.webtoolkit.jwt.WLabel;
//import eu.webtoolkit.jwt.WLength;
//import eu.webtoolkit.jwt.WLineEdit;
//import eu.webtoolkit.jwt.WPushButton;
//import eu.webtoolkit.jwt.WSlider;
//
//public class FormulaireMatieres3_wt extends WDialog {	
//	EDTApplication_wt edtAppli;
//	private WComboBox listeMatieres;
//	private TreeMap<String,Matiere> matieres;
//	private WPushButton b_ajouter,b_supprimer,b_modifier;
//	private TreeSet<String> nomsMatieres;
//	private Matiere matiere;
//	private WLabel nom_m,nom_compl_m;
//	private WLineEdit tf_nom,tf_nom_compl;
//	private WSlider tf_r,tf_b,tf_v ;
//	private String mode="modifier";
//	FormulaireMatieres3_wt(EDTApplication_wt _edtAppli) {
//		super("Gestion des matières");
//
//		edtAppli=_edtAppli;
//
//		afficher();
//	}
//	public void afficher() {
//		this.getFooter().clear();
//		this.getContents().clear();
//
//		WContainerWidget panelMatieres = new WContainerWidget();
//		WBoxLayout grid = new WBoxLayout(Direction.TopToBottom);
//		panelMatieres.setLayout(grid);
//
//		if(mode.equals("modifier")) {
//			listeMatieres = new WComboBox();
//			matieres=edtAppli.getControleurDb().getMatieresAdmin2(edtAppli.getControleurDb().getIdAdmin());
//			nomsMatieres = new TreeSet<String>();
//			for(String m: matieres.keySet()) {
//				if(!nomsMatieres.contains(m)) {
//					listeMatieres.addItem(m);   
//					nomsMatieres.add(m);
//				}
//			}
//			listeMatieres.setAttributeValue("style", "text-align:center;");
//			listeMatieres.changed().addListener(listeMatieres, new Signal.Listener() {
//				public void trigger() {
//					action("mettreAJour");				
//				}
//			});	
//			grid.addWidget(listeMatieres);
//			matiere=matieres.get(listeMatieres.getCurrentText().getValue());
//
//			b_modifier=new WPushButton("Modifier");
//			b_modifier.clicked().addListener(b_modifier, new Signal.Listener() {
//				public void trigger() {
//					action("modifier");				
//				}
//			});
//			b_modifier.setStyleClass("btn-info");    
//			b_modifier.setAttributeValue("style", "margin:10px;");
//
//			b_supprimer=new WPushButton("Supprimer "+matiere.getNom());    
//			b_supprimer.clicked().addListener(b_supprimer, new Signal.Listener() {
//				public void trigger() {
//					action("supprimer");
//				}
//			});
//			b_supprimer.setStyleClass("btn-warning");			
//			getFooter().addWidget(b_supprimer);
//			
//			b_ajouter=new WPushButton("Changer pour Ajout");
//			b_ajouter.setStyleClass("btn-success");
//			b_ajouter.clicked().addListener(b_ajouter, new Signal.Listener() {
//				public void trigger() {
//					mode="ajouter";
//					afficher();
//				}
//			});
//			getFooter().addWidget(b_ajouter);
//		}else {
//			b_ajouter=new WPushButton("Ajouter");
//			b_ajouter.clicked().addListener(b_ajouter, new Signal.Listener() {
//				public void trigger() {
//					action("ajouter");				
//				}
//			});
//			b_ajouter.setStyleClass("btn-info");    
//			b_ajouter.setAttributeValue("style", "margin:10px;margin-top:40px;");
//
//			b_modifier=new WPushButton("Changer pour Modif");
//			b_modifier.setStyleClass("btn-success");
//			b_modifier.clicked().addListener(b_modifier, new Signal.Listener() {
//				public void trigger() {
//					mode="modifier";
//					afficher();
//				}
//			});
//			getFooter().addWidget(b_modifier);	
//		}
//
//		nom_m = new WLabel("Nom :");
//		grid.addWidget(nom_m);
//
//		tf_nom = new WLineEdit();
//		tf_nom.setAttributeValue("style", "text-align:center;");
//		tf_nom.setMaxLength(30);
//		grid.addWidget(tf_nom);
//
//		nom_compl_m = new WLabel("Complément du nom :");
//		grid.addWidget(nom_compl_m);
//
//		tf_nom_compl = new WLineEdit();
//		tf_nom_compl.setAttributeValue("style", "text-align:center;");
//		tf_nom_compl.setMaxLength(30);
//		grid.addWidget(tf_nom_compl);
//
//
//		WLabel rouge = new WLabel("Rouge");
//		rouge.setAttributeValue("style", "color:red;font:bold");
//		grid.addWidget(rouge);
//
//		tf_r = new WSlider(panelMatieres);
//		tf_r.setMinimum(0);
//		tf_r.setMaximum(255);
//		tf_r.setHeight(new WLength(grid.getSpacing()));
//		grid.addWidget(tf_r);
//
//		WLabel vert = new WLabel("Vert");
//		vert.setAttributeValue("style", "color:green;font:bold");
//		grid.addWidget(vert);
//
//		tf_v = new WSlider(panelMatieres);
//		tf_v.setMinimum(0);
//		tf_v.setMaximum(255);
//		tf_v.setHeight(new WLength(grid.getSpacing()));
//		grid.addWidget(tf_v);
//
//		WLabel bleu = new WLabel("Bleu");
//		bleu.setAttributeValue("style", "color:blue;font:bold");
//		grid.addWidget(bleu);
//
//		tf_b = new WSlider(panelMatieres);	
//		tf_b.setMinimum(0);
//		tf_b.setMaximum(255);
//		tf_b.setHeight(new WLength(grid.getSpacing()));
//		grid.addWidget(tf_b);	
//
//
//		if(mode.equals("modifier") && matiere!=null) {
//			tf_nom.setText(matiere.getNom());
//			tf_nom_compl.setText(matiere.getNomCompl());
//			tf_r.setValue(matiere.getRouge());
//			tf_v.setValue(matiere.getVert());
//			tf_b.setValue(matiere.getBleu());
//			grid.addWidget(b_modifier);
//		}else {
//			tf_nom.setText("");
//			tf_nom_compl.setText("");
//			tf_r.setValue(255);
//			tf_v.setValue(255);
//			tf_b.setValue(255);
//			grid.addWidget(b_ajouter);
//		}
//
//		tf_r.setToolTip(""+tf_r.getValue());
//		tf_r.valueChanged().addListener(this, new Signal.Listener() {
//			public void trigger() {
//				tf_nom.getDecorationStyle().setBackgroundColor(new WColor(tf_r.getValue(),tf_v.getValue(),tf_b.getValue()));
//				tf_nom_compl.getDecorationStyle().setBackgroundColor(new WColor(tf_r.getValue(),tf_v.getValue(),tf_b.getValue()));
//				tf_r.setToolTip(tf_r.getValueText());
//			}
//		});
//
//		tf_v.setToolTip(""+tf_v.getValue());
//		tf_v.valueChanged().addListener(this, new Signal.Listener() {
//			public void trigger() {
//				tf_nom.getDecorationStyle().setBackgroundColor(new WColor(tf_r.getValue(),tf_v.getValue(),tf_b.getValue()));
//				tf_nom_compl.getDecorationStyle().setBackgroundColor(new WColor(tf_r.getValue(),tf_v.getValue(),tf_b.getValue()));
//				tf_v.setToolTip(tf_v.getValueText());
//			}
//		});
//
//		tf_b.setToolTip(""+tf_b.getValue());
//		tf_b.valueChanged().addListener(this, new Signal.Listener() {
//			public void trigger() {
//				tf_nom.getDecorationStyle().setBackgroundColor(new WColor(tf_r.getValue(),tf_v.getValue(),tf_b.getValue()));
//				tf_nom_compl.getDecorationStyle().setBackgroundColor(new WColor(tf_r.getValue(),tf_v.getValue(),tf_b.getValue()));
//				tf_b.setToolTip(tf_b.getValueText());
//			}
//		});
//
//		tf_nom.getDecorationStyle().setBackgroundColor(new WColor(tf_r.getValue(),tf_v.getValue(),tf_b.getValue()));
//		tf_nom_compl.getDecorationStyle().setBackgroundColor(new WColor(tf_r.getValue(),tf_v.getValue(),tf_b.getValue()));
//
//		getContents().addWidget(panelMatieres);
//		this.setResizable(true);
//		this.setClosable(true);
//		this.rejectWhenEscapePressed(true);
//		this.resize(610, 520);
//		this.show();		
//	} 
//
//	public void action(String b) {     	 
//		if (b.equals("mettreAJour")) {
//			matiere=matieres.get(listeMatieres.getCurrentText().getValue());
//			tf_nom.setText(matiere.getNom());
//			tf_nom_compl.setText(matiere.getNomCompl());
//
//			tf_r.setValue(matiere.getRouge());
//			tf_r.setToolTip(""+tf_r.getValue());
//
//			tf_v.setValue(matiere.getVert());
//			tf_v.setToolTip(""+tf_v.getValue());
//
//			tf_b.setValue(matiere.getBleu());
//			tf_b.setToolTip(""+tf_b.getValue());
//
//			tf_nom.getDecorationStyle().setBackgroundColor(new WColor(tf_r.getValue(),tf_v.getValue(),tf_b.getValue()));
//			tf_nom_compl.getDecorationStyle().setBackgroundColor(new WColor(tf_r.getValue(),tf_v.getValue(),tf_b.getValue()));
//
//			b_supprimer.setText("Supprimer "+matiere.getNom());    
//		}else if (b.equals("modifier")) {
//			if(!tf_nom.getText().equals(matiere.getNom()) || !tf_nom_compl.getText().equals(matiere.getNomCompl()) || tf_r.getValue()!=matiere.getRouge() || tf_v.getValue()!=matiere.getVert() || tf_b.getValue()!=matiere.getBleu()) {
//				matiere.setInfos(tf_nom.getText().replaceAll("&",""), tf_nom_compl.getText().replaceAll("&",""), Math.min(255,Integer.parseInt("0"+tf_r.getValue())), Math.min(255,Integer.parseInt("0"+tf_v.getValue())), Math.min(255,Integer.parseInt("0"+tf_b.getValue())), matiere.getPole());
//				String erreurInsertion = "";
//				erreurInsertion = edtAppli.getControleurDb().modifierMatiere2(matiere);
//				if (erreurInsertion=="") {
//					int index=listeMatieres.getCurrentIndex();
//					afficher();	
//					listeMatieres.setCurrentIndex(index);
//					action("mettreAJour");
//					Utilitaire.showMessageDialog(this, matiere.getNom() +" a bie été mis a jour");
//				}else {
//					Utilitaire.showMessageDialog("ControleurDB : modifierMatiere()", erreurInsertion.toString());
//				}
//			}else {
//				Utilitaire.showMessageDialog(this,"Veuillez au moins changer une valeur avant de cliquer ci dessous.");
//			}
//		}else if (b.equals("ajouter")) {
//			if(!tf_nom.getText().equals("")) {
//				String erreurInsertion = "";
//				erreurInsertion=edtAppli.getControleurDb().ajouterMatiere(this, tf_nom.getText().replaceAll("&",""), tf_nom_compl.getText().replaceAll("&",""),  Math.min(255,Integer.parseInt("0"+tf_r.getValue())), Math.min(255,Integer.parseInt("0"+tf_v.getValue())), Math.min(255,Integer.parseInt("0"+tf_b.getValue())), null, edtAppli.getControleurDb().getIdPromotion((String)edtAppli.getListePromotions().getValueText()));
//				if (erreurInsertion.equals("")) {
//					Utilitaire.showMessageDialog(this, "La matière "+tf_nom.getText()+" a bien été ajouté.");
//					afficher();	
//				}else {
//					Utilitaire.showMessageDialog(this, erreurInsertion);
//				}
//			}else {
//				Utilitaire.showMessageDialog(this,"Veuillez au moins entrer un nom avant de cliquer ci dessous.");
//			}
//		}else if(b.equals("supprimer")) {
//			final WDialog confirmerSuppr = new WDialog("Suppression de matière");
//			new WLabel("Confirmez-vous la suppression de "+listeMatieres.getCurrentText().getValue()+" ?", confirmerSuppr.getContents());
//			WPushButton ok = new WPushButton("Oui", confirmerSuppr.getFooter());
//			WPushButton annuler = new WPushButton("Non", confirmerSuppr.getFooter());
//			ok.setStyleClass("btn-danger");
//			annuler.setStyleClass("btn-primary");
//			ok.clicked().addListener(ok, new Signal.Listener() {
//				public void trigger() {
//					confirmerSuppr.accept();
//					int index=listeMatieres.getCurrentIndex();
//					edtAppli.getControleurDb().supprimerMatiere(matieres.get(listeMatieres.getCurrentText().getValue()));
//					afficher();	
//					listeMatieres.setCurrentIndex(index);
//					action("mettreAJour");
//				}
//			});
//			annuler.clicked().addListener(annuler, new Signal.Listener() {
//				public void trigger() {
//					confirmerSuppr.reject();
//				}
//			});
//			confirmerSuppr.setClosable(true);
//			confirmerSuppr.rejectWhenEscapePressed(true);
//			confirmerSuppr.show();
//		}
//	}
//}