package EDT;

import eu.webtoolkit.jwt.WDialog;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WCheckBox;
import eu.webtoolkit.jwt.WLineEdit;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WLabel;
import eu.webtoolkit.jwt.WGridLayout;
import eu.webtoolkit.jwt.WBorderLayout;
import eu.webtoolkit.jwt.WSpinBox;
import eu.webtoolkit.jwt.Signal;


//import java.io.FileUtils;
//import java.io.UploadedFile;

import java.util.*;

/**
 * formulaire d'ajout ou de modification de salle
 */
public class FormulaireSaisieSalle_wt extends WDialog { 
	Salle salle;
	EDTApplication_wt edtAppli;
	WLineEdit tf_nom_salle,tf_ipDbt,tf_ipFin;
	WSpinBox tf_nbplaces, tf_nbordis;
	WCheckBox cb_videoprojecteur,cb_internet;
	WPushButton b_valider, b_annuler;

	WDialog dialog = this;

	FormulaireSaisieSalle_wt(EDTApplication_wt _edtAppli, Salle _salle, WDialog dial) {
		super("Ajout/modif de salle");

		edtAppli=_edtAppli;
		salle=_salle;

		WContainerWidget panelSalle = new WContainerWidget();
		WGridLayout grid = new WGridLayout();
		panelSalle.setLayout(grid);		

		tf_nom_salle=new WLineEdit();

		tf_ipDbt=new WLineEdit();
		tf_ipDbt.setMaxLength(15);
		tf_ipFin=new WLineEdit();
		tf_ipFin.setMaxLength(15);

		tf_nbplaces=new WSpinBox();
		tf_nbordis=new WSpinBox();
		cb_videoprojecteur=new WCheckBox();  

		cb_internet=new WCheckBox();  

		tf_nbplaces.setRange(0,255);
		tf_nbplaces.setValue(0);
		tf_nbordis.setRange(0,255);
		tf_nbordis.setValue(0); 

		if (salle!=null) {
			tf_nom_salle.setText(salle.getNom());

			if(salle.getIpDbt()!= null)tf_ipDbt.setText(salle.getIpDbt());else tf_ipDbt.setText("");
			if(salle.getIpFin()!= null)tf_ipFin.setText(salle.getIpFin());else tf_ipFin.setText("");

			tf_nbplaces.setValue(salle.getNbPlaces());
			tf_nbordis.setValue(salle.getNbOrdinateurs());

			if (salle.getVideoprojecteur()) cb_videoprojecteur.setChecked(true);else cb_videoprojecteur.setChecked(false);
			if (salle.isInternet()) cb_internet.setChecked(true);else cb_internet.setChecked(false);	
		}

		grid.addWidget(new WLabel("nom :"), 0 , 0);
		grid.addWidget(tf_nom_salle, 0 , 1);
		grid.addWidget(new WLabel("nombre de places :"), 1 , 0);
		grid.addWidget(tf_nbplaces, 1, 1);
		grid.addWidget(new WLabel("nombre d'ordinateurs :"), 2, 0);
		grid.addWidget(tf_nbordis, 2, 1);

		grid.addWidget(new WLabel("Ip d??but :"), 3, 0);
		grid.addWidget(tf_ipDbt, 3, 1);
		grid.addWidget(new WLabel("Ip fin :"), 4, 0);
		grid.addWidget(tf_ipFin,4, 1);

		grid.addWidget(new WLabel("vid??oprojecteur ?"), 5, 0);
		grid.addWidget(cb_videoprojecteur,5, 1);

		grid.addWidget(new WLabel("internet ?"), 6, 0);
		grid.addWidget(cb_internet, 6, 1);  

		WContainerWidget panelBoutons = new WContainerWidget();
		grid = new WGridLayout();
		panelBoutons.setLayout(grid);

		b_valider=new WPushButton("Valider");
		b_annuler=new WPushButton("Annuler");		

		b_valider.clicked().addListener(b_valider, new Signal.Listener() {
			public void trigger() {
				actionValider();
			}
		});		
		b_annuler.clicked().addListener(b_annuler, new Signal.Listener() {
			public void trigger() {
				new FormulaireSalles_wt(edtAppli);
				remove();
			}
		});
		b_valider.setStyleClass("btn-info");
		b_annuler.setStyleClass("btn-primary");

		grid.addWidget(b_valider);
		grid.addWidget(b_annuler);

		WContainerWidget panel=new WContainerWidget();
		WBorderLayout border = new WBorderLayout();		
		panel.setLayout(border);
		border.addWidget(panelSalle, WBorderLayout.Position.Center);		
		getContents().addWidget(panel);
		getFooter().addWidget(b_valider);
		getFooter().addWidget(b_annuler);

		this.setResizable(true);
		this.setClosable(false);
		this.resize(500, 500);
		this.show();
	}

	public void actionValider() {
		// on v??rifie les saisies 
		String nomSalle=tf_nom_salle.getText().replaceAll("[^-_.() ????????A-Za-z0-9]","");
		String ipDbt=tf_ipDbt.getText().replaceAll("[^.0-9]","");
		String ipFin=tf_ipFin.getText().replaceAll("[^.0-9]","");

		int nbPlaces=tf_nbplaces.getValue();
		int nbOrdinateurs=tf_nbordis.getValue();
		boolean videoproj=cb_videoprojecteur.isChecked();
		boolean internet=cb_internet.isChecked();

		// on v??rifie que la salle n'existe pas
		TreeSet<Salle> liste=edtAppli.getControleurDb().getSalles();
		if (salle!=null) liste.remove(salle); // on enl??ve la salle de la liste (mais pas de la BD !)

		Salle nouv_salle=new Salle(-1, nomSalle, nbPlaces, nbOrdinateurs, videoproj, ipDbt, ipFin, internet);
		if (liste.contains(nouv_salle)) {
			Utilitaire.showMessageDialog(this, "Cette salle existe d??j??!!");
			if (salle!=null) liste.add(salle); // on remet l'ancienne salle
		} else {
			if (salle!=null) edtAppli.getControleurDb().modifierSalle(this, salle, nomSalle, nbPlaces, nbOrdinateurs, videoproj, ipDbt, ipFin, internet);
			else edtAppli.getControleurDb().ajouterSalle(this, nomSalle, nbPlaces, nbOrdinateurs, videoproj, ipDbt, ipFin, internet);
			if (this.isVisible()) {
				accept();
				new FormulaireSalles_wt(edtAppli);
			}
		}
	}
}
