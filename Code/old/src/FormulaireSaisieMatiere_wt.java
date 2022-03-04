package EDT;

import eu.webtoolkit.jwt.WDialog;
import eu.webtoolkit.jwt.WLineEdit;
import eu.webtoolkit.jwt.WComboBox;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WBoxLayout;
import eu.webtoolkit.jwt.WBoxLayout.Direction;
import eu.webtoolkit.jwt.WGridLayout;
import eu.webtoolkit.jwt.WLabel;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WColor;
import eu.webtoolkit.jwt.WSlider;
import eu.webtoolkit.jwt.AlignmentFlag;
import eu.webtoolkit.jwt.WLength;

import java.util.*;
import javax.swing.*;
//import javax.swing.colorchooser.AbstractColorChooserPanel;

/**
 * formulaire d'ajout ou de modification de matière
 */
/*public class FormulaireSaisieMatiere extends JDialog implements ActionListener {*/ 
public class FormulaireSaisieMatiere_wt extends WDialog {
	Matiere matiere;
	EDTApplication_wt edtAppli;
	WLineEdit tf_nom, tf_nom_compl;
	WSlider tf_r, tf_v, tf_b;    
	WComboBox pole;
	WPushButton b_choixCouleurs, b_valider, b_annuler;
	JColorChooser colorChooser;

	WDialog dialog = this;

	FormulaireSaisieMatiere_wt(EDTApplication_wt _edtAppli, Matiere _matiere, WDialog dial) {
		/*super(dial, "Ajout/modif de matiere", true);*/
		super("Ajout/modif de matiere");		

		edtAppli=_edtAppli;
		matiere=_matiere;
		//System.out.println("on reçoit : " + matiere);

		WContainerWidget panelMatiere = new WContainerWidget();
		WBoxLayout grid = new WBoxLayout(Direction.TopToBottom);
		panelMatiere.setLayout(grid);

		tf_nom=new WLineEdit();

		tf_nom_compl=new WLineEdit();
		tf_r=new WSlider(panelMatiere);
		tf_v=new WSlider(panelMatiere);
		tf_b=new WSlider(panelMatiere);		

		tf_nom.setMaxLength(65);
		tf_r.setMinimum(0);
		tf_r.setMaximum(255);
		tf_r.setValue(255);
		tf_r.setToolTip(""+tf_r.getValue());		
		tf_r.setHeight(new WLength(grid.getSpacing()));
		tf_r.setVerticalAlignment(AlignmentFlag.AlignTextTop);		
		tf_r.setAttributeValue("style", "font-size:2.8em;bgcolor:red");		

		tf_v.setMinimum(0);
		tf_v.setMaximum(255);
		tf_v.setValue(255);
		tf_v.setToolTip(""+tf_v.getValue());
		tf_v.setHeight(new WLength(grid.getSpacing()));

		tf_b.setMinimum(0);
		tf_b.setMaximum(255);
		tf_b.setValue(255);
		tf_b.setToolTip(""+tf_b.getValue());
		tf_b.setHeight(new WLength(grid.getSpacing()));


		/*pole=new WComboBox(new Vector<String>(edtAppli.getControleurDb().getPoles(edtAppli.getControleurDb().getIdPromotion((String)edtAppli.getListePromotions().getSelectedItem()))));*/
		ArrayList<String> liste = new ArrayList<String>(edtAppli.getControleurDb().getPoles(edtAppli.getControleurDb().getIdPromotion((String)edtAppli.getListePromotions().getValueText())));
		pole = new WComboBox();
		for(int i=0;i<liste.size();i++){
			pole.addItem(liste.get(i));
		}

		if (matiere!=null) {

			tf_nom.setText(matiere.getNom());
			tf_nom_compl.setText(matiere.getNomCompl());

			tf_r.setValue(matiere.getRouge());
			tf_r.setToolTip(""+tf_r.getValue());

			tf_v.setValue(matiere.getVert());
			tf_v.setToolTip(""+tf_v.getValue());

			tf_b.setValue(matiere.getBleu());
			tf_b.setToolTip(""+tf_b.getValue());

			pole.setValueText(matiere.getPole());
		}		
		tf_nom.getDecorationStyle().setBackgroundColor(new WColor(tf_r.getValue(),tf_v.getValue(),tf_b.getValue()));
		tf_nom_compl.getDecorationStyle().setBackgroundColor(new WColor(tf_r.getValue(),tf_v.getValue(),tf_b.getValue()));

		grid.addWidget(new WLabel("Nom :"));
		grid.addWidget(tf_nom);
		grid.addWidget(new WLabel("Complément du nom :"));
		grid.addWidget(tf_nom_compl);
		WLabel rouge = new WLabel("Rouge");
		rouge.setAttributeValue("style", "color:red;font-style:bold");
		grid.addWidget(rouge);
		grid.addWidget(tf_r);
		WLabel vert = new WLabel("Vert");
		vert.setAttributeValue("style", "color:green;font-style:bold");
		grid.addWidget(vert);
		grid.addWidget(tf_v);	//
		WLabel bleu = new WLabel("Bleu");
		bleu.setAttributeValue("style", "color:blue;font-style:bold");
		grid.addWidget(bleu);
		grid.addWidget(tf_b);			

		tf_r.valueChanged().addListener(this, new Signal.Listener() {
			public void trigger() {
				tf_nom.getDecorationStyle().setBackgroundColor(new WColor(tf_r.getValue(),tf_v.getValue(),tf_b.getValue()));
				tf_nom_compl.getDecorationStyle().setBackgroundColor(new WColor(tf_r.getValue(),tf_v.getValue(),tf_b.getValue()));
				tf_r.setToolTip(tf_r.getValueText());

			}
		});
		tf_v.valueChanged().addListener(this, new Signal.Listener() {
			public void trigger() {
				tf_nom.getDecorationStyle().setBackgroundColor(new WColor(tf_r.getValue(),tf_v.getValue(),tf_b.getValue()));
				tf_nom_compl.getDecorationStyle().setBackgroundColor(new WColor(tf_r.getValue(),tf_v.getValue(),tf_b.getValue()));
				tf_v.setToolTip(tf_v.getValueText());
			}
		});
		tf_b.valueChanged().addListener(this, new Signal.Listener() {
			public void trigger() {
				tf_nom.getDecorationStyle().setBackgroundColor(new WColor(tf_r.getValue(),tf_v.getValue(),tf_b.getValue()));
				tf_nom_compl.getDecorationStyle().setBackgroundColor(new WColor(tf_r.getValue(),tf_v.getValue(),tf_b.getValue()));
				tf_b.setToolTip(tf_b.getValueText());
			}
		});

		WContainerWidget panelBoutons=new WContainerWidget();
		WGridLayout gridBoutons = new WGridLayout();
		panelBoutons.setLayout(gridBoutons);

		b_valider=new WPushButton("Valider");
		b_annuler=new WPushButton("Annuler");

		b_valider.clicked().addListener(b_valider, new Signal.Listener() {
			public void trigger() {
				actionValider();
			}

		});
		b_annuler.clicked().addListener(b_annuler, new Signal.Listener() {
			public void trigger() {
				remove();
				new FormulaireMatieres_wt(edtAppli);
			}					
		});
		b_valider.setStyleClass("btn-info");
		b_annuler.setStyleClass("btn-primary");		

		getFooter().addWidget(b_valider);
		getFooter().addWidget(b_annuler);

		getContents().addWidget(panelMatiere);
		this.resize(700, 450);
		this.setResizable(true);
		this.show();
	}

	public void actionValider() {
		String nom = tf_nom.getText().replaceAll("&","");
		String nomCompl = tf_nom_compl.getText().replaceAll("&","");
		String erreurInsertion = "";
		int id_promo=edtAppli.getControleurDb().getIdPromotion((String)edtAppli.getListePromotions().getValueText());
		int r = Math.min(255,Integer.parseInt("0"+tf_r.getValue()));
		int v = Math.min(255,Integer.parseInt("0"+tf_v.getValue()));
		int b = Math.min(255,Integer.parseInt("0"+tf_b.getValue()));
		TreeSet<String> liste=edtAppli.getControleurDb().getMatieresPromo2(id_promo);
		if(!nom.equals("")) {
			if(!liste.contains(nom+" "+nomCompl)){
				if (matiere!=null) {
					if(!nom.equals(matiere.getNom()) || !nomCompl.equals(matiere.getNomCompl()) || r!=matiere.getRouge() ||v!=matiere.getVert() || b!=matiere.getBleu()) {
						matiere.setInfos(nom,nomCompl, r, v, b, matiere.getPole());
						erreurInsertion = edtAppli.getControleurDb().modifierMatiere2(matiere);
						if (erreurInsertion=="") {
							accept();
							new FormulaireMatieres_wt(edtAppli);
							Utilitaire.showMessageDialog(this, matiere.getNom() +" a bien été mis a jour");
						}else {
							Utilitaire.showMessageDialog("ControleurDB : modifierMatiere()", erreurInsertion.toString());
						}
					}else {
						Utilitaire.showMessageDialog(this,"Veuillez au moins changer une valeur avant de cliquer ci dessous.");
					}
				}else {
					erreurInsertion=edtAppli.getControleurDb().ajouterMatiere(this, nom,nomCompl,r,v,b,null, id_promo);
					if (erreurInsertion.equals("")) {
						accept();
						new FormulaireMatieres_wt(edtAppli);
						Utilitaire.showMessageDialog(this, "La matière "+tf_nom.getText()+" a bien été ajouté.");
					}else {
						Utilitaire.showMessageDialog(this, erreurInsertion);
					}
				}
			}else {
				Utilitaire.showMessageDialog(this,"Une matière avec ces propriétes existe déja dans cette promo.");
			}
		}else {
			Utilitaire.showMessageDialog(this,"Veuillez au moins entrer un nom avant de cliquer ci dessous.");
		}
	}
}


//// on vérifie les saisies
//String nom=tf_nom.getText();
//nom=nom.replaceAll("&","");
//String nom_compl=tf_nom_compl.getText();
//nom_compl=nom_compl.replaceAll("&","");
//int rouge=Math.min(255,Integer.parseInt("0"+tf_r.getValue()));
//int vert=Math.min(255,Integer.parseInt("0"+tf_v.getValue()));
//int bleu=Math.min(255,Integer.parseInt("0"+tf_b.getValue()));
//String nom_pole=null;/*(String)pole.getSelectedItem();*/
//if (nom_pole==null)
//	nom_pole="";
//// on vérifie que la matière n'existe pas
//int id_promo=edtAppli.getControleurDb().getIdPromotion((String)edtAppli.getListePromotions().getValueText());
//TreeSet<Matiere> liste=edtAppli.getControleurDb().getMatieresPromo(id_promo);
//if (matiere!=null) liste.remove(matiere); // on enlève la matière de la liste (mais pas de la BD !)
//Matiere nouv_mat=new Matiere(-1,nom, nom_compl, rouge, vert, bleu, nom_pole);
//if (liste.contains(nouv_mat)) {
//	Utilitaire.showMessageDialog(this, "Cette matière existe déjà");
//	if (matiere!=null) liste.add(matiere); // on remet l'ancienne matière
//}
//else if (!nom.equals("")){
//	String erreurInsertion="";
//	if (matiere!=null) erreurInsertion=edtAppli.getControleurDb().modifierMatiere(matiere, nom, nom_compl, rouge, vert, bleu, nom_pole);
//	else erreurInsertion=edtAppli.getControleurDb().ajouterMatiere(this, nom, nom_compl, rouge, vert, bleu, nom_pole, id_promo);
//
//	if (!erreurInsertion.equals("")) {
//		Utilitaire.showMessageDialog(this, erreurInsertion);
//	}else {
//		liste.add(nouv_mat); // on met la nouvelle matière dans la liste à la place de l'ancienne
//		accept();
//		new FormulaireMatieres_wt(edtAppli);
//		Utilitaire.showMessageDialog(this, "L'opération s'est bien déroulée");
//	}				
//}
//else {
//	Utilitaire.showMessageDialog(this, "Il faut saisir un nom !");
//	if (matiere!=null) liste.add(matiere); // on remet l'ancienne matière
//}
//}
