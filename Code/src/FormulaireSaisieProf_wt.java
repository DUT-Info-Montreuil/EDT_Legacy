package EDT;

import eu.webtoolkit.jwt.WDialog;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WLabel;
import eu.webtoolkit.jwt.WGridLayout;
import eu.webtoolkit.jwt.WBorderLayout;
import eu.webtoolkit.jwt.WCheckBox;
import eu.webtoolkit.jwt.WLineEdit;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WSpinBox;

import eu.webtoolkit.jwt.Signal;
import java.util.*;

/**
 * formulaire d'ajout ou de modification de prof
 */
public class FormulaireSaisieProf_wt extends WDialog {
	Prof prof;
	EDTApplication_wt edtAppli;
	WLineEdit tf_initiale, tf_prenom, tf_nom, tf_login, tf_commentaire;
	WSpinBox tf_serviceMax, tf_decharge_projets, tf_decharge_stages, tf_decharge_apprentis, tf_decharge;
	WCheckBox cb_titulaire, cb_visible;
	WPushButton b_valider, b_annuler;

	FormulaireSaisieProf_wt(EDTApplication_wt _edtAppli, Prof _prof, WDialog dial) {
		super("Ajout/modif de prof");		

		edtAppli=_edtAppli;
		prof=_prof;
		
		WContainerWidget panel=new WContainerWidget();
		WBorderLayout border = new WBorderLayout();
		panel.setLayout(border);

		WContainerWidget panelProf=new WContainerWidget();
		WGridLayout grid = new WGridLayout();
		panelProf.setLayout(grid);

		tf_initiale=new WLineEdit();
		tf_prenom=new WLineEdit();
		tf_nom=new WLineEdit();
		tf_login=new WLineEdit();
		tf_serviceMax=new WSpinBox();
		tf_decharge_projets=new WSpinBox();
		tf_decharge_stages=new WSpinBox();
		tf_decharge_apprentis=new WSpinBox();
		tf_decharge=new WSpinBox();        
		tf_commentaire=new WLineEdit();
		cb_titulaire=new WCheckBox();
		cb_visible=new WCheckBox();

		if (prof!=null) {
			tf_initiale.setText(prof.getInitiale());
			tf_prenom.setText(prof.getPrenom());
			tf_nom.setText(prof.getNom());
			tf_login.setText(prof.getLogin());
			tf_serviceMax.setText(""+prof.getServiceMax());
			tf_decharge_projets.setText(""+prof.getDechargeProjets());
			tf_decharge_stages.setText(""+prof.getDechargeStages());
			tf_decharge_apprentis.setText(""+prof.getDechargeApprentis());
			tf_decharge.setText(""+prof.getDecharge());
			tf_commentaire.setText(""+prof.getCommentaire());
			if (prof.getTitulaire()) cb_titulaire.setChecked(true);
			else cb_titulaire.setChecked(false);
			if (prof.getVisible()) cb_visible.setChecked(true);
			else cb_visible.setChecked(false);
		}

		grid.addWidget(new WLabel("initiale :"), 0, 0);
		grid.addWidget(tf_initiale, 0, 1);
		grid.addWidget(new WLabel("prénom :"), 1, 0);
		grid.addWidget(tf_prenom, 1, 1);
		grid.addWidget(new WLabel("nom :"), 2, 0);
		grid.addWidget(tf_nom, 2, 1);
		grid.addWidget(new WLabel("login (obligatoire) :"), 3, 0); /*optionnel*/
		grid.addWidget(tf_login, 3, 1);        
		grid.addWidget(new WLabel("service max :"), 4, 0);
		grid.addWidget(tf_serviceMax, 4, 1);
		grid.addWidget(new WLabel("décharge projets :"), 5, 0);
		grid.addWidget(tf_decharge_projets, 5, 1);
		grid.addWidget(new WLabel("décharge stages :"), 6, 0);
		grid.addWidget(tf_decharge_stages, 6, 1);
		grid.addWidget(new WLabel("décharge apprentis :"), 7, 0);
		grid.addWidget(tf_decharge_apprentis, 7, 1);
		grid.addWidget(new WLabel("autres décharges :"), 8, 0);
		grid.addWidget(tf_decharge, 8, 1);
		grid.addWidget(new WLabel("commentaire :"), 9, 0);
		grid.addWidget(tf_commentaire, 9, 1);
		grid.addWidget(new WLabel("titulaire :"), 10, 0);
		grid.addWidget(cb_titulaire, 10, 1);
		grid.addWidget(new WLabel("visible :"), 11, 0);
		grid.addWidget(cb_visible, 11, 1);

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
				new FormulaireProfs_wt(edtAppli);
			}
		});
		b_valider.setStyleClass("btn-info");
		b_annuler.setStyleClass("btn-primary");


		getFooter().addWidget(b_valider);
		getFooter().addWidget(b_annuler);


		border.addWidget(panelProf, WBorderLayout.Position.Center);
		getContents().addWidget(panel);


		this.setResizable(true);
		this.resize(500, 500);
		this.show();		
	}

	public void actionValider() {
		// on vérifie les saisies
		String initiale=tf_initiale.getValueText().replaceAll("[^-_.() éàèùA-Za-z0-9]","");
		String prenom=tf_prenom.getValueText().toLowerCase().replaceAll("[^-_.() éàèùA-Za-z0-9]","");
		String nom=tf_nom.getValueText().toUpperCase().replaceAll("[^-_.() éàèùA-Za-z0-9]","");
		String login=tf_login.getValueText().replaceAll("[^-_.() éàèùA-Za-z0-9]","");
		int serviceMax=Integer.parseInt("0"+tf_serviceMax.getValueText());
		int decharge_projets=Integer.parseInt("0"+tf_decharge_projets.getValueText());
		int decharge_stages=Integer.parseInt("0"+tf_decharge_stages.getValueText());
		int decharge_apprentis=Integer.parseInt("0"+tf_decharge_apprentis.getValueText());
		int decharge=Integer.parseInt("0"+tf_decharge.getValueText());
		String commentaire=tf_commentaire.getValueText();
		boolean titulaire=cb_titulaire.isChecked();
		boolean visible=cb_visible.isChecked();
		// on vérifie que le prof n'existe pas
		TreeSet<Prof> liste=edtAppli.getControleurDb().getProfs();
		if (prof!=null) liste.remove(prof); // on enlève le prof de la liste (mais pas de la BD !)
		// et que ses initiales ne sont pas déjà utilisées
		TreeSet<String> listeInitiales=edtAppli.getControleurDb().getListeInitialesProfs();
		TreeSet<String> listeLogins=edtAppli.getControleurDb().getListeLoginsProfs();
		if (prof!=null) listeInitiales.remove(prof.getInitiale()); 	// on enlève l'initiale de la liste (mais pas de la BD !)
		if (prof!=null) listeLogins.remove(prof.getLogin()); 		// on enlève l'initiale de la liste (mais pas de la BD !)

		Prof nouv_prof=new Prof(-1,initiale, prenom, nom, serviceMax, decharge_projets, decharge_stages, decharge_apprentis, decharge, commentaire, titulaire, login, visible);
		if (liste.contains(nouv_prof)) {
			Utilitaire.showMessageDialog(this, "Ce prof (ou ses initiales) existe déjà");
			if (prof!=null) liste.add(prof); // on remet l'ancien prof
		} else if (prof!=null && initiale.equals(prof.getInitiale()) && prenom.equals(prof.getPrenom()) && nom.equals(prof.getNom()) && login.equals(prof.getLogin()) && serviceMax==prof.getServiceMax() && decharge_projets==prof.getDechargeProjets() && decharge_apprentis==prof.getDechargeApprentis() && decharge_stages==prof.getDechargeStages() && decharge==prof.getDecharge() && commentaire.equals(prof.getCommentaire()) && titulaire==prof.getTitulaire() && visible==prof.getVisible()) {        
			Utilitaire.showMessageDialog(this, "Veuillez au moins changer une valeur avant de cliquer ci dessous.");
		}else if (initiale.equals("") || prenom.equals("") || nom.equals("")) {
			Utilitaire.showMessageDialog(this, "Il faut saisir initiales, prénom et nom !");
			if (prof!=null) liste.add(prof); // on remet l'ancien prof
		} else if (login.equals("")) {
			Utilitaire.showMessageDialog(this, "Il faut saisir le login !");
			if (prof!=null) liste.add(prof); // on remet l'ancien prof
		} else if (listeInitiales.contains(initiale)) {
			Utilitaire.showMessageDialog(this, "Ces initiales existent déja...\n");
		} else if (listeLogins.contains(login)) {
			Utilitaire.showMessageDialog(this, "Ce login existe déja...\n");
		} else {
			if (commentaire.length()>20) {
				Utilitaire.showMessageDialog(this, "ATTENTION : le commentaire a été tronqué à 20 caractères...");
				commentaire=commentaire.substring(0,20);
			}
			String semaineEnCours=(String)(edtAppli.getListeSemaines().getValueText());
			String[]tab=semaineEnCours.split("/");
			int dateDebutAnneeScolaire=Integer.parseInt(tab[2].substring(tab[2].length()-4,4));
			if (Integer.parseInt(tab[1])<7) // on est avant juin : le début de l'année est l'année d'avant
				dateDebutAnneeScolaire--;
			if (prof!=null) edtAppli.getControleurDb().modifierProf(dateDebutAnneeScolaire, prof, initiale, prenom, nom, serviceMax, decharge_projets, decharge_stages, decharge_apprentis, decharge, commentaire, titulaire, login, visible);
			else edtAppli.getControleurDb().ajouterProf(dateDebutAnneeScolaire, initiale, prenom, nom, serviceMax, decharge_projets, decharge_stages, decharge_apprentis, decharge, commentaire, titulaire, login, edtAppli.getControleurDb().getIdPromotion((String)edtAppli.getListePromotions().getValueText()));
			liste.add(nouv_prof); // on met le nouveau prof dans la liste à la place de l'ancien
			accept();
			new FormulaireProfs_wt(edtAppli);
			if (prof!=null)Utilitaire.showMessageDialog(this, "Le prof a bien été modifié.");else Utilitaire.showMessageDialog(this, "Le prof a bien été ajouté.");
		}
	}
}
