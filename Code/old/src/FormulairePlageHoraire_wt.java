package EDT;

import eu.webtoolkit.jwt.WDialog;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WComboBox;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WCheckBox;
import eu.webtoolkit.jwt.WLineEdit;
import eu.webtoolkit.jwt.WGridLayout;
import eu.webtoolkit.jwt.WBorderLayout;
import eu.webtoolkit.jwt.WLabel;
import eu.webtoolkit.jwt.WSpinBox;
import eu.webtoolkit.jwt.WDateEdit;
import eu.webtoolkit.jwt.WTimeEdit;
import eu.webtoolkit.jwt.WTime;
import eu.webtoolkit.jwt.WLength;
import eu.webtoolkit.jwt.Signal;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

/**
 * formulaire d'ajout ou de modification de plage horaire
 */
/*public class FormulairePlageHoraire extends JDialog implements ActionListener {*/
public class FormulairePlageHoraire_wt extends WDialog {
	ArrayList<PlageHoraire> plages; // liste des plages à ajouter/modifier/supprimer
	EDTApplication_wt edtAppli;
	/*JTextField tf_date, tf_debut, tf_fin, tf_repeter, tf_memo;*/
	WLineEdit /*tf_date, tf_debut, tf_fin, tf_repeter,*/ tf_memo;
	WComboBox salles, salles2, groupes, matieres, profs, profs2, profs3, profs4, profQuiFaitAppel;
	WPushButton b_valider, b_supprimer, b_annuler;
	WCheckBox cb_controle, cb_internet;

	WSpinBox tf_repeter;	//repetition;
	WDateEdit tf_date;
	WTimeEdit tf_debut, tf_fin;

	Vector<Salle> listeSallesV;
	Vector<Groupe>listeGroupesV;
	Vector<Matiere>listeMatieresV;
	Vector<Prof>listeProfsV;
	//TreeSet<Prof> listeProfsAdmin;
	HashMap<String,Prof> profQuiFaitAppelV;

	Signal.Listener signalValider;
	WGridLayout gridBoutons;


	FormulairePlageHoraire_wt(EDTApplication_wt _edtAppli, PlageHoraire _plage) {
		/*super(_edtAppli, "Ajout/modification de plage horaire", true);*/
		super("Ajout/modification de plage horaire");	

		/*//procedure qui masque et ferme la fenetre
		setUndecorated(true);
		getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // sinon quand on ferme on perd la plage horaire*/
		edtAppli=_edtAppli;
		plages=new ArrayList<PlageHoraire>();
		plages.add(_plage);
		//System.out.println("on reçoit : " + _plage);
		/*JPanel panel=new JPanel();
		panel.setLayout(new GridLayout(3,1));*/
		WContainerWidget panel = new WContainerWidget();		
		WGridLayout grid = new WGridLayout();
		panel.setLayout(grid);

		// la date et les heures de début et fin
		/*JPanel panelDates=new JPanel();
		panelDates.setLayout(new GridLayout(5,3));*/
		WContainerWidget panelDates = new WContainerWidget();
		WGridLayout gridDates = new WGridLayout();
		panelDates.setLayout(gridDates);

		/*tf_date=new JTextField();
		tf_debut=new JTextField();
		tf_fin=new JTextField();
        tf_repeter=new JTextField("1");
		tf_memo=new JTextField(_plage.getMemo());*/
		tf_date=new WDateEdit();
		tf_date.setFormat("dd/MM/yyyy");
		tf_debut = new WTimeEdit();
		tf_debut.setFormat("HH:mm");
		tf_debut.setValueText("8:00");
		tf_fin=new WTimeEdit();
		tf_fin.setFormat("HH:mm");
		tf_fin.setTime(WTime.getCurrentTime());
		tf_repeter = new WSpinBox();
		tf_repeter.setRange(0, 20);
		tf_repeter.setValue(0);		
		tf_memo=new WLineEdit(_plage.getMemo());


		GregorianCalendar cal=_plage.getDateDebut();
		tf_date.setText(""+cal.get(Calendar.DAY_OF_MONTH)+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR));
		tf_debut.setText(""+cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE));
		cal=_plage.getDateFin();
		tf_fin.setText(""+cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE));

		//tf_debut.setTime(WTime.getCurrentTime());
		//tf_fin.setTime(WTime.getCurrentTime().addSecs(3600*2));
		//Utilitaire.showMessageDialog(this,tf_fin.getValueText());/////////

		/*cb_controle=new JCheckBox("contrôle");
		cb_controle.setSelected(_plage.IsControle());
		cb_internet=new JCheckBox("internet");
		cb_internet.setSelected(_plage.accesInternet());*/		
		cb_controle=new WCheckBox("contrôle");
		cb_controle.setChecked(_plage.IsControle());
		cb_internet=new WCheckBox("internet");
		cb_internet.setChecked(_plage.accesInternet());		
		WContainerWidget cbs = new WContainerWidget();
		cbs.addWidget(cb_controle);
		cbs.addWidget(cb_internet);

		/*panelDates.add(new JLabel("le :"));
		panelDates.add(tf_date);
		panelDates.add(new JLabel(""));
		panelDates.add(new JLabel("de :"));
		panelDates.add(tf_debut);
		panelDates.add(new JLabel(""));
		panelDates.add(new JLabel("à :"));
		panelDates.add(tf_fin);
		panelDates.add(cb_internet);
		panelDates.add(new JLabel("nombre de répétitions :"));
		panelDates.add(tf_repeter);
		panelDates.add(cb_controle);
        panelDates.add(new JLabel("mémo :"));
        panelDates.add(tf_memo);
        panelDates.add(new JLabel(""));*/
		gridDates.addWidget(new WLabel("le :"), 0, 0);
		gridDates.addWidget(tf_date, 0, 1);
		gridDates.addWidget(new WLabel("de :"), 1, 0);
		gridDates.addWidget(tf_debut, 1, 1);
		gridDates.addWidget(new WLabel("à :"), 3, 0);
		gridDates.addWidget(tf_fin, 3, 1);
		gridDates.addWidget(new WLabel("répétitions :"), 4, 0);	/*nombre de */
		gridDates.addWidget(tf_repeter, 4, 1);
		gridDates.addWidget(new WLabel("mémo :"), 5, 0);
		gridDates.addWidget(tf_memo, 5, 1);
		gridDates.addWidget(cbs, 6, 1);

		// les informations restantes
		/*JPanel panelBas=new JPanel();*/
		WContainerWidget panelBas = new WContainerWidget(); 
		WGridLayout gridBas = new WGridLayout();
		panelBas.setLayout(gridBas);

		/*panelBas.setLayout(new GridLayout(6,3));
		salles=new JComboBox();
		panelBas.add(new JLabel("salle"));
		panelBas.add(salles);
		salles2=new JComboBox();
		panelBas.add(salles2);
		groupes=new JComboBox();
		panelBas.add(new JLabel("groupe"));
		panelBas.add(groupes);
		panelBas.add(new JLabel(""));
		matieres=new JComboBox();
		panelBas.add(new JLabel("matiere"));
		panelBas.add(matieres);
		panelBas.add(new JLabel(""));
		profs=new JComboBox();
		panelBas.add(new JLabel("prof"));
		panelBas.add(profs);
		profs2=new JComboBox();
		panelBas.add(profs2);
		panelBas.add(new JLabel(""));
		profs3=new JComboBox();
		panelBas.add(profs3);
		profs4=new JComboBox();
		panelBas.add(profs4);
		profQuiFaitAppel=new JComboBox();
		panelBas.add(new JLabel("Appel fait par"));
		panelBas.add(profQuiFaitAppel);
		panelBas.add(new JLabel(""));*/		
		salles=new WComboBox();
		gridBas.addWidget(new WLabel("salle :"), 0, 0);
		gridBas.addWidget(salles, 0, 1);
		salles2=new WComboBox();
		gridBas.addWidget(salles2, 0, 2);
		groupes=new WComboBox();
		gridBas.addWidget(new WLabel("groupe :"), 1, 0);
		gridBas.addWidget(groupes, 1, 1);
		gridBas.addWidget(new WLabel(""), 2, 0);
		matieres=new WComboBox();
		gridBas.addWidget(new WLabel("matiere :"), 3, 0);
		gridBas.addWidget(matieres, 3, 1);
		profs=new WComboBox();
		gridBas.addWidget(new WLabel("prof :"), 4, 0);
		gridBas.addWidget(profs, 4, 1);
		profs2=new WComboBox();
		gridBas.addWidget(profs2, 4, 2);
		profs3=new WComboBox();
		gridBas.addWidget(profs3, 5, 1);
		profs4=new WComboBox();
		gridBas.addWidget(profs4, 5, 2);
		profQuiFaitAppel=new WComboBox();
		gridBas.addWidget(new WLabel("Appel fait par :"), 6, 0);
		gridBas.addWidget(profQuiFaitAppel, 6, 1);

		// on désactive les listes déroulantes dans un premier temps
		salles.setEnabled(false);
		salles2.setEnabled(false);
		groupes.setEnabled(false);
		matieres.setEnabled(false);
		profs.setEnabled(false);
		profs2.setEnabled(false);
		profs3.setEnabled(false);
		profs4.setEnabled(false);
		profQuiFaitAppel.setEnabled(false);

		// les boutons
		/*JPanel panelBoutons=new JPanel();
		panelBoutons.setLayout(new GridLayout(1,3));*/
		WContainerWidget panelBoutons=new WContainerWidget();
		gridBoutons = new WGridLayout();
		panelBoutons.setLayout(gridBoutons);		

		/*b_valider=new JButton("Valider date/heures");
		b_supprimer=new JButton("Supprimer");
		b_annuler=new JButton("Annuler");
		b_valider.addActionListener(this);
		b_supprimer.addActionListener(this);
		b_annuler.addActionListener(this);
		panelBoutons.add(b_valider);
		panelBoutons.add(b_supprimer);
		panelBoutons.add(b_annuler);*/
		b_valider=new WPushButton("Valider la date / heures");
		b_supprimer=new WPushButton("Supprimer");
		b_annuler=new WPushButton("Annuler");
		b_valider.setStyleClass("btn-success");
		b_supprimer.setStyleClass("btn-danger");
		b_annuler.setStyleClass("btn-primary");		
		signalValider = new Signal.Listener() {
			public void trigger() {
				action("Valider date/heures");				
			}
		};		
		b_valider.clicked().addListener(this, signalValider);		
		b_supprimer.clicked().addListener(b_supprimer, new Signal.Listener() {
			public void trigger() {
				action("Supprimer");	
			}
		});
		b_annuler.clicked().addListener(b_annuler, new Signal.Listener() {
			public void trigger() {
				action("Annuler");	
			}
		});		
		gridBoutons.addWidget(b_valider, 0, 0);
		gridBoutons.addWidget(b_supprimer, 0, 1);
		gridBoutons.addWidget(b_annuler, 0, 2);			

		// au début on ne peut pas supprimer (il faut d'abord appuyer sur Valider date/heures (pour les cas de suppression multiples)
		b_supprimer.setEnabled(false);
		// on ajoute tout dans la fenetre
		/*panel.add(panelDates);
		panel.add(panelBas);
		panel.add(panelBoutons);
		getContentPane().add(panel);*/
		grid.addWidget(panelDates, 0, 0);
		grid.addWidget(panelBas, 0, 1);
		//grid.addWidget(panelBoutons, 1, 1);
		getContents().addWidget(panel);
		//getFooter().addWidget(panelBoutons);
		getFooter().addWidget(b_supprimer);
		getFooter().addWidget(b_valider);		
		getFooter().addWidget(b_annuler);		

		// positionner au centre de l'écran
		/*setResizable(false);
		pack();
		Rectangle rect=edtAppli.getBounds(); // getToolkit().getScreenSize();
		setLocation(Math.max(0,(int)(rect.x+(rect.width-getWidth())/2)), Math.max(0,(int)(rect.y+(rect.height-getHeight())/2)));*/
		//this.setResizable(true);
		this.setClosable(true);
		this.rejectWhenEscapePressed(true);
		this.show();
	}
	public void action(String action) {
		/*public void actionPerformed(ActionEvent e) {
		JButton b = (JButton)e.getSource();
		String label=b.getText();
        // on affiche le curseur d'attente
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));*/

		//Utilitaire.showMessageDialog(this,"public void action(String action) { salleCurrent="+salles.getCurrentIndex()+"; ..");

		if (action.equals("Valider date/heures")) {
			//Utilitaire.showMessageDialog("this","action Valider date/heures");
			/*if (label.equals("Valider date/heures")) {*/
			// on vérifie que les dates et heures saisies sont correctes
			/*if (tf_debut.getText().lastIndexOf('h')==tf_debut.getText().length()-1) // la saisie se termine par h*/
			if (tf_debut.getText().lastIndexOf(':')==tf_debut.getText().length()-1) // la saisie se termine par h				
				tf_debut.setText(tf_debut.getText()+"0");
			/*if (tf_fin.getText().lastIndexOf('h')==tf_fin.getText().length()-1) // la saisie se termine par h*/
			if (tf_fin.getText().lastIndexOf(':')==tf_fin.getText().length()-1) // la saisie se termine par h
				tf_fin.setText(tf_fin.getText()+"0");
			tf_date.setText(tf_date.getText().replaceAll(" +","")); // on élimine tous les espaces
			tf_debut.setText(tf_debut.getText().replaceAll(" +","")); // on élimine tous les espaces
			tf_fin.setText(tf_fin.getText().replaceAll(" +","")); // on élimine tous les espaces
			// est-ce qu'on essaie d'insérer sur un jour férié ?
			Vector<PlageHoraire> jours_feries=edtAppli.getControleurDb().getJoursFeries(tf_date.getText());
			for (Iterator<PlageHoraire> iter = jours_feries.iterator(); iter.hasNext();) {
				PlageHoraire plage_jf= iter.next();
				if (plage_jf.getDateDebut().get(Calendar.DAY_OF_YEAR)==Utilitaire.calculerDate(tf_date.getText()).get(Calendar.DAY_OF_YEAR)) {
					/*JOptionPane.showMessageDialog(this, "Vous ne pouvez pas mettre de cours un jour férié...");*/
					Utilitaire.showMessageDialog(this, "Vous ne pouvez pas mettre de cours un jour férié...");
					/*setCursor(null);*/
					return;
				}
			}
			if (Utilitaire.dateCorrecte(tf_date.getText()) && Utilitaire.heureCorrecte(tf_debut.getText()) && Utilitaire.heureCorrecte(tf_fin.getText())) {
				GregorianCalendar debut=Utilitaire.calculerDate(tf_date.getText());
				GregorianCalendar fin=Utilitaire.calculerDate(tf_date.getText());
				debut.add(Calendar.HOUR_OF_DAY,Integer.parseInt((tf_debut.getText().split(":"))[0]));	//.split("h"))[0]));
				debut.add(Calendar.MINUTE,Integer.parseInt("0"+(tf_debut.getText().split(":"))[1]));
				fin.add(Calendar.HOUR_OF_DAY,Integer.parseInt((tf_fin.getText().split(":"))[0]));
				fin.add(Calendar.MINUTE,Integer.parseInt("0"+(tf_fin.getText().split(":"))[1]));
				GregorianCalendar debutSemaineEnCours=Utilitaire.calculerDate((String)edtAppli.getListeSemaines().getValueText());	//SelectedItem());
				GregorianCalendar finSemaineEnCours=Utilitaire.calculerDate((String)edtAppli.getListeSemaines().getValueText());	//SelectedItem());
				finSemaineEnCours.add(Calendar.DAY_OF_MONTH,7);
				if (debut.before(debutSemaineEnCours) || fin.after(finSemaineEnCours))
					/*JOptionPane.showMessageDialog(this, "Attention : vous saisissez une date hors de la semaine en cours...");*/
					Utilitaire.showMessageDialog(this, "Attention : vous saisissez une date hors de la semaine en cours...");
				if (!Utilitaire.anneeScolaireEnCours(tf_date.getText()))
					/*JOptionPane.showMessageDialog(this, "Attention, la date est en dehors de l'année scolaire !!!");*/
					Utilitaire.showMessageDialog(this,  "Attention, la date est en dehors de l'année scolaire !!!");
				if (fin.getTimeInMillis()-debut.getTimeInMillis()<600000) { // temps négatif ou de moins de 10 minutes
					/*JOptionPane.showMessageDialog(this, "Pas de plage horaire de moins de 10 minutes...");*/
					Utilitaire.showMessageDialog(this, "Pas de plage horaire de moins de 10 minutes..."); 
					/*setCursor(null);*/
					return;
				}
				// on supprime les plages horaires identiques des semaines suivantes en cas de modification avec répétition
				// et on stocke ces plages dans la liste : plages
				int repetition=1;
				boolean plageIdentique=true;
				PlageHoraire plage_originale=(PlageHoraire)(plages.get(0));
				if (plage_originale.getIdPlage()!=-1) // on a une plage existante à modifier
					while (repetition<Integer.parseInt("0"+tf_repeter.getText()) && plageIdentique) { // on cherche les plages identiques
						SimpleTimeZone tz=new SimpleTimeZone(1*60*60*1000, "CET"); // GMT+1
						tz.setStartRule(Calendar.MARCH, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
						tz.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
						GregorianCalendar debut_temp=new GregorianCalendar(tz);
						GregorianCalendar fin_temp=new GregorianCalendar(tz);
						debut_temp.setTimeInMillis(plage_originale.getDateDebut().getTimeInMillis());
						debut_temp.add(Calendar.DAY_OF_YEAR, repetition*7); // on ajoute 7 jours
						fin_temp.setTimeInMillis(plage_originale.getDateFin().getTimeInMillis());
						fin_temp.add(Calendar.DAY_OF_YEAR, repetition*7); // on ajoute 7 jours
						Groupe g=edtAppli.getControleurDb().getGroupe(plage_originale.getIdGroupe());
						PlageHoraire jourFerie=edtAppli.getControleurDb().getPlageHoraire(debut_temp, "", 0);
						PlageHoraire plage_temp=edtAppli.getControleurDb().getPlageHoraire(debut_temp, edtAppli.getControleurDb().getPromo(g.getIdGroupe()), g.getIndexColonne());
						if (jourFerie!=null) // on a un jour férié la semaine suivante : on avance
							repetition++;
						else if (plage_temp!=null && plage_temp.getIdSalle()==plage_originale.getIdSalle() && plage_temp.getIdSalle2()==plage_originale.getIdSalle2() && plage_temp.getIdGroupe()==plage_originale.getIdGroupe() && plage_temp.getIdMatiere()==plage_originale.getIdMatiere() 
								&& plage_temp.getIdProf()==plage_originale.getIdProf() && plage_temp.getIdProfQuiFaitAppel()==plage_originale.getIdProfQuiFaitAppel() && plage_temp.getIdProf2()==plage_originale.getIdProf2() && plage_temp.getIdProf3()==plage_originale.getIdProf3() && plage_temp.getIdProf4()==plage_originale.getIdProf4() && plage_temp.IsControle()==plage_originale.IsControle()
								&& plage_temp.getDateDebut().get(Calendar.HOUR_OF_DAY)==plage_originale.getDateDebut().get(Calendar.HOUR_OF_DAY) && plage_temp.getDateDebut().get(Calendar.MINUTE)==plage_originale.getDateDebut().get(Calendar.MINUTE) && plage_temp.getDateFin().get(Calendar.HOUR_OF_DAY)==plage_originale.getDateFin().get(Calendar.HOUR_OF_DAY) && plage_temp.getDateFin().get(Calendar.MINUTE)==plage_originale.getDateFin().get(Calendar.MINUTE)) {
							// si on a une autre plage identique la semaine suivante
							plages.add(plage_temp);
							edtAppli.getControleurDb().supprimerPlageHoraire(plage_temp.getIdPlage());
							repetition++;
						}
						else {
							plageIdentique=false;
							tf_repeter.setText(""+repetition);
							/*JOptionPane.showMessageDialog(this,"Seulement " + repetition + " répétitions possibles...");*/
							Utilitaire.showMessageDialog(this,"Seulement " + repetition + " répétitions possibles...");
						}
					}
				// on recalcule les salles libres
				/*salles.removeAllItems();
				salles2.removeAllItems();*/
				salles.clear();
				salles2.clear();

				TreeSet<Salle> listeSalles=edtAppli.getControleurDb().getSallesLibres(debut, fin);
				repetition=1;
				while (repetition<Integer.parseInt("0"+tf_repeter.getText())) {
					SimpleTimeZone tz=new SimpleTimeZone(1*60*60*1000, "CET"); // GMT+1
					tz.setStartRule(Calendar.MARCH, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
					tz.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
					GregorianCalendar debut_temp=new GregorianCalendar(tz);
					GregorianCalendar fin_temp=new GregorianCalendar(tz);
					debut_temp.setTimeInMillis(debut.getTimeInMillis());
					debut_temp.add(Calendar.DAY_OF_YEAR, repetition*7); // on ajoute 7 jours
					fin_temp.setTimeInMillis(fin.getTimeInMillis());
					fin_temp.add(Calendar.DAY_OF_YEAR, repetition*7); // on ajoute 7 jours
					TreeSet<Salle> temp=edtAppli.getControleurDb().getSallesLibres(debut_temp, fin_temp);
					listeSalles.retainAll(temp);
					repetition++;
				}
				/*Vector<Salle> listeSallesV=new Vector<Salle>(listeSalles);*/
				listeSallesV=new Vector<Salle>(listeSalles);
				listeSallesV.add(0, edtAppli.getControleurDb().getSalle(0));
				//System.out.println("liste=" + liste);
				for (Iterator<Salle> i=listeSallesV.iterator(); i.hasNext();) {
					Salle s=i.next();
					/*salles.addItem(s);
					salles2.addItem(s);*/
					salles.addItem(s.toString());
					salles2.addItem(s.toString());
				}

				/*salles.setSelectedItem(edtAppli.getControleurDb().getSalle(((PlageHoraire)plages.get(0)).getIdSalle()));
				salles2.setSelectedItem(edtAppli.getControleurDb().getSalle(((PlageHoraire)plages.get(0)).getIdSalle2()));*/
				salles.setValueText(((Salle)edtAppli.getControleurDb().getSalle(((PlageHoraire)plages.get(0)).getIdSalle())).toString());
				salles2.setValueText(((Salle)edtAppli.getControleurDb().getSalle(((PlageHoraire)plages.get(0)).getIdSalle2())).toString());
				//Utilitaire.showMessageDialog("this",salles.getValueText());

				//System.out.println("salles=" + salles);
				// on recalcule les groupes libres
				/*groupes.removeAllItems();*/
				groupes.clear();
				TreeSet<Groupe>listeGroupes=edtAppli.getControleurDb().getGroupesLibres((String)(edtAppli.getListePromotions().getValueText()), debut, fin, false);	//SelectedItem()), debut, fin, false);
				repetition=1;
				while (repetition<Integer.parseInt("0"+tf_repeter.getText())) {
					SimpleTimeZone tz=new SimpleTimeZone(1*60*60*1000, "CET"); // GMT+1
					tz.setStartRule(Calendar.MARCH, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
					tz.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
					GregorianCalendar debut_temp=new GregorianCalendar(tz);
					GregorianCalendar fin_temp=new GregorianCalendar(tz);
					debut_temp.setTimeInMillis(debut.getTimeInMillis());
					debut_temp.add(Calendar.DAY_OF_YEAR, repetition*7); // on ajoute 7 jours
					fin_temp.setTimeInMillis(fin.getTimeInMillis());
					fin_temp.add(Calendar.DAY_OF_YEAR, repetition*7); // on ajoute 7 jours
					TreeSet<Groupe> temp=edtAppli.getControleurDb().getGroupesLibres((String)(edtAppli.getListePromotions().getValueText()), debut_temp, fin_temp, false);	//SelectedItem()), debut_temp, fin_temp, false);
					//System.out.println(liste + "\n" + temp);
					listeGroupes.retainAll(temp);
					//System.out.println(liste + "\n" + temp);
					repetition++;
				}
				/*Vector<Groupe>listeGroupesV=new Vector<Groupe>(listeGroupes);*/
				listeGroupesV=new Vector<Groupe>(listeGroupes);
				//System.out.println("liste=" + liste);
				for (Iterator<Groupe> i=listeGroupesV.iterator(); i.hasNext();)
					groupes.addItem(((Groupe)i.next()).getNom());
				/*groupes.addItem(i.next());*/
				/*groupes.setSelectedItem(edtAppli.getControleurDb().getGroupe(((PlageHoraire)plages.get(0)).getIdGroupe()));*/
				groupes.setValueText(((Groupe)edtAppli.getControleurDb().getGroupe(((PlageHoraire)plages.get(0)).getIdGroupe())).getNom());
				//System.out.println("groupes=" + groupes);
				/*if (groupes.getItemCount()==0) {*/
				if (groupes.getCount()==0) {					
					/*JOptionPane.showMessageDialog(this,"Aucun groupe n'est libre : changer l'heure ou la promo");*/
					Utilitaire.showMessageDialog(this,"Aucun groupe n'est libre : changer l'heure ou la promo");
					/*setCursor(null);*/
					return;
				}
				//System.out.println("idgroupe="+plage.getIdGroupe() + " groupe=" + edtAppli.getControleurDb().getGroupe(plage.getIdGroupe()));
				// on recalcule les matieres libres
				/*matieres.removeAllItems();*/
				matieres.clear();
				TreeSet<Matiere> listeMatieresPromo=edtAppli.getControleurDb().getMatieresPromo(edtAppli.getControleurDb().getIdPromotion((String)edtAppli.getListePromotions().getValueText()));	//SelectedItem()));
				TreeSet<Matiere> listeMatieresAdmin=edtAppli.getControleurDb().getMatieresAdmin(edtAppli.getControleurDb().getIdAdmin());
				listeMatieresAdmin.removeAll(listeMatieresPromo); // on enlève les matières qu'on a déjà mis en début de liste
				TreeSet<Matiere> listeMatieresOrphelines=edtAppli.getControleurDb().getMatieresOrphelines();
				listeMatieresOrphelines.removeAll(listeMatieresPromo); // on enlève les matières qu'on a déjà mis en début de liste
				listeMatieresOrphelines.removeAll(listeMatieresAdmin); // on enlève les matières qu'on a déjà mis en début de liste
				/*Vector<Matiere>listeMatieresV=new Vector<Matiere>(listeMatieresPromo); // on place en premier les matières de la promo courante*/
				listeMatieresV=new Vector<Matiere>(listeMatieresPromo); // on place en premier les matières de la promo courante
				listeMatieresV.add(edtAppli.getControleurDb().getMatiere(0));
				listeMatieresV.addAll(listeMatieresAdmin); // on ajoute les matières de l'admin qui ne sont attribuées à aucune promo
				listeMatieresV.add(edtAppli.getControleurDb().getMatiere(0));
				listeMatieresV.addAll(listeMatieresOrphelines); // on ajoute les autres matières qui ne sont attribuées à aucune promo
				//System.out.println("liste=" + liste);
				for (Iterator<Matiere> i=listeMatieresV.iterator(); i.hasNext();)
					/*matieres.addItem(i.next());*/
					matieres.addItem(((Matiere)i.next()).toString());
				/*if (matieres.getItemCount()==0) {*/
				if (matieres.getCount()==0) {					
					/*JOptionPane.showMessageDialog(this,"Il faut créer une matière !!!");*/
					Utilitaire.showMessageDialog(this,"Il faut créer une matière !!!");
					/*dispose();*/
					reject();
				}
				// préselection de la matière
				int id_m=((PlageHoraire)plages.get(0)).getIdMatiere();
				if (id_m!=0) // matière non vide
					/*matieres.setSelectedItem(edtAppli.getControleurDb().getMatiere(id_m));*/
					matieres.setValueText(edtAppli.getControleurDb().getMatiere(id_m).toString());
				/*else matieres.setSelectedIndex(0); // sélection de la première matière*/
				else matieres.setCurrentIndex(0); // sélection de la première matière
				// on recalcule les profs libres
				/*profs.removeAllItems();
				profs2.removeAllItems();
				profs3.removeAllItems();
				profs4.removeAllItems();*/
				profs.clear();
				profs2.clear();
				profs3.clear();
				profs4.clear();
				TreeSet<Prof>listeProfsLibres=edtAppli.getControleurDb().getProfsLibres(debut, fin);
				repetition=1;
				while (repetition<Integer.parseInt("0"+tf_repeter.getText())) {
					SimpleTimeZone tz=new SimpleTimeZone(1*60*60*1000, "CET"); // GMT+1
					tz.setStartRule(Calendar.MARCH, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
					tz.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
					GregorianCalendar debut_temp=new GregorianCalendar(tz);
					GregorianCalendar fin_temp=new GregorianCalendar(tz);
					debut_temp.setTimeInMillis(debut.getTimeInMillis());
					debut_temp.add(Calendar.DAY_OF_YEAR, repetition*7); // on ajoute 7 jours
					fin_temp.setTimeInMillis(fin.getTimeInMillis());
					fin_temp.add(Calendar.DAY_OF_YEAR, repetition*7); // on ajoute 7 jours
					TreeSet<Prof> temp=edtAppli.getControleurDb().getProfsLibres(debut_temp, fin_temp);
					listeProfsLibres.retainAll(temp);
					repetition++;
				}
				TreeSet<Prof> listeProfsPromo=edtAppli.getControleurDb().getProfsPromo(edtAppli.getControleurDb().getIdPromotion((String)edtAppli.getListePromotions().getValueText())); //SelectedItem() les profs visibles de la promo en cours
				String semaineEnCours=(String)(edtAppli.getListeSemaines().getValueText());	//SelectedItem());
				String[]tab=semaineEnCours.split("/");
				int dateDebutAnneeScolaire=Integer.parseInt(tab[2].substring(tab[2].length()-4,4));
				if (Integer.parseInt(tab[1])<7) // on est avant juin : le début de l'année est l'année d'avant
					dateDebutAnneeScolaire--;
				TreeSet<Prof> listeProfsAdmin=edtAppli.getControleurDb().getProfsAdmin(edtAppli.getControleurDb().getIdAdmin(), dateDebutAnneeScolaire, true); // les profs visibles de l'admin
				// on prépare la liste de tous les profs (pour choisir celui qui fera l'appel)
				profQuiFaitAppel.addItem(((Prof)edtAppli.getControleurDb().getProf((0))).toString()); // on ajoute une ligne vide au début (par défaut: aucun prof)
				profQuiFaitAppelV = new HashMap<String,Prof>();
				for (Iterator<Prof> i=listeProfsAdmin.iterator(); i.hasNext();) {
					Prof p=(Prof)i.next();
					/*profQuiFaitAppel.addItem(p);*/
					profQuiFaitAppel.addItem(p.toString());
					profQuiFaitAppelV.put(p.toString(),p);
				}
				listeProfsLibres.remove(edtAppli.getControleurDb().getProf((0))); // on retire le prof numéro 0 (sinon il apparait une fois de trop en bas de liste)
				listeProfsPromo.retainAll(listeProfsLibres); // on ne garde que les profs libres
				listeProfsAdmin.retainAll(listeProfsLibres); // on ne garde que les profs libres
				listeProfsAdmin.removeAll(listeProfsPromo); // on ne garde que les profs qui ne sont pas déjà dans la première liste
				listeProfsLibres.removeAll(listeProfsAdmin); // on ne garde que les profs qui ne sont pas déjà dans la première liste
				listeProfsLibres.removeAll(listeProfsPromo); // on ne garde que les profs qui ne sont pas déjà dans la première liste
				/*Vector<Prof>listeProfsV=new Vector<Prof>(listeProfsPromo); // on place en premier les profs de la promo courante*/
				listeProfsV=new Vector<Prof>(listeProfsPromo); // on place en premier les profs de la promo courante
				listeProfsV.add(edtAppli.getControleurDb().getProf((0))); // on ajoute une ligne vide pour séparer des autres profs
				listeProfsV.addAll(listeProfsAdmin); // on place le reste des profs libres de l'admin
				listeProfsV.add(0, edtAppli.getControleurDb().getProf((0))); // on ajoute une ligne vide en tete de listeV
				listeProfsV.addAll(listeProfsLibres); // on place le reste des profs libres
				for (Iterator<Prof> i=listeProfsV.iterator(); i.hasNext();) {
					Prof p=(Prof)i.next();
					/*profs.addItem(p);
					profs2.addItem(p);
					profs3.addItem(p);
					profs4.addItem(p);*/
					profs.addItem(p.toString());
					profs2.addItem(p.toString());
					profs3.addItem(p.toString());
					profs4.addItem(p.toString());
				}
				/*profs.setSelectedItem(edtAppli.getControleurDb().getProf(((PlageHoraire)plages.get(0)).getIdProf()));*/
				profs.setValueText(((Prof)edtAppli.getControleurDb().getProf(((PlageHoraire)plages.get(0)).getIdProf())).toString());

				/*profs2.setSelectedItem(edtAppli.getControleurDb().getProf(((PlageHoraire)plages.get(0)).getIdProf2()));
				profs3.setSelectedItem(edtAppli.getControleurDb().getProf(((PlageHoraire)plages.get(0)).getIdProf3()));
				profs4.setSelectedItem(edtAppli.getControleurDb().getProf(((PlageHoraire)plages.get(0)).getIdProf4()));
				profQuiFaitAppel.setSelectedItem(edtAppli.getControleurDb().getProf(((PlageHoraire)plages.get(0)).getIdProfQuiFaitAppel()));*/
				profs2.setValueText(((Prof)edtAppli.getControleurDb().getProf(((PlageHoraire)plages.get(0)).getIdProf2())).toString());
				profs3.setValueText(((Prof)edtAppli.getControleurDb().getProf(((PlageHoraire)plages.get(0)).getIdProf3())).toString());
				profs4.setValueText(((Prof)edtAppli.getControleurDb().getProf(((PlageHoraire)plages.get(0)).getIdProf4())).toString());
				profQuiFaitAppel.setValueText(((Prof)edtAppli.getControleurDb().getProf(((PlageHoraire)plages.get(0)).getIdProfQuiFaitAppel())).toString());				

				// on va pouvoir valider ou supprimer la/les plage(s) horaire(s)
				/*b_valider.setText("Valider");
				b_supprimer.setEnabled(true);*/
				b_supprimer.setEnabled(true);
				b_valider.remove();
				b_valider.clicked().removeListener(signalValider);
				signalValider = null;					
				// bouton b_valider2
				WPushButton b_valider2 = new WPushButton("Valider la plage horaire");
				b_valider2.setStyleClass("btn-success");
				Signal.Listener signalValider2 = new Signal.Listener() {
					public void trigger() {
						//Utilitaire.showMessageDialog("this","b_valider 2!"+b_valider2.getJsRef());
						action("Valider");
					}
				};				
				b_valider2.clicked().addListener(this, signalValider2);
				//b_valider2.setRef(b_valider.getJsRef());
				//getContents().addWidget(b_valider2);
				//gridBoutons.addWidget(b_valider2,0,0);
				b_valider2.setAttributeValue("style", "float:right;");
				b_annuler.setAttributeValue("style", "float:right");				
				getFooter().addWidget(b_valider2);

				/*salles.changed().addListener(this, new Signal.Listener() {
													public void trigger() {
														Utilitaire.showMessageDialog("this","salls changed:"+salles.getCurrentIndex());	
													}
				});*/


			}
			else {
				/*JOptionPane.showMessageDialog(this, "Mauvais format de date(jj/mm/aaaa) ou d'heure (ex. 10h10) ou modif. interdite (plus de trois mois ou en dehors de l'année scolaire)");*/
				Utilitaire.showMessageDialog(this, "Mauvais format de date(jj/mm/aaaa) ou d'heure (ex. 10h10) ou modif. interdite (plus de trois mois ou en dehors de l'année scolaire)");
				/*setCursor(null);*/
				return;
			} 
			// on échange les parties actives et non actives
			tf_date.setEnabled(false);
			tf_debut.setEnabled(false);
			tf_fin.setEnabled(false);
			tf_repeter.setEnabled(false);
			tf_memo.setEnabled(false);
			cb_controle.setEnabled(false);
			cb_internet.setEnabled(false);
			salles.setEnabled(true);
			groupes.setEnabled(true);
			matieres.setEnabled(true);
			profs.setEnabled(true);
			profQuiFaitAppel.setEnabled(true);
			if (cb_controle.isChecked()) {	//isSelected()) {
				salles2.setEnabled(true);
				profs2.setEnabled(true);
				profs3.setEnabled(true);
				profs4.setEnabled(true);
			}
		}
		else if (action.equals("Valider")) {
			//Utilitaire.showMessageDialog(this,"salleCurrent="+salles.getCurrentIndex());
			String[] jma=tf_date.getText().split("/");
			int jour=Integer.parseInt(jma[0]);
			int mois=Integer.parseInt(jma[1])-1; // -1 car janvier=0
			int annee=Integer.parseInt(jma[2]);
			/*String[] hm_debut=tf_debut.getText().split("h");*/
			String[] hm_debut=tf_debut.getText().split(":");
			int heure_debut=Integer.parseInt(hm_debut[0]);
			int minute_debut=Integer.parseInt(hm_debut[1]);
			/*String[] hm_fin=tf_fin.getText().split("h");*/
			String[] hm_fin=tf_fin.getText().split(":");
			int heure_fin=Integer.parseInt(hm_fin[0]);
			int minute_fin=Integer.parseInt(hm_fin[1]);
			SimpleTimeZone tz=new SimpleTimeZone(1*60*60*1000, "CET"); // GMT+1
			tz.setStartRule(Calendar.MARCH, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
			tz.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
			GregorianCalendar debut=new GregorianCalendar(tz);
			debut.clear();
			debut.set(annee, mois, jour, heure_debut, minute_debut);
			GregorianCalendar fin=new GregorianCalendar(tz);
			fin.clear();
			fin.set(annee, mois, jour, heure_fin, minute_fin);

			/*Salle salle=(Salle)salles.getSelectedItem();
			Groupe groupe=(Groupe)groupes.getSelectedItem();
			Matiere matiere=(Matiere)matieres.getSelectedItem();
			Prof prof=(Prof)profs.getSelectedItem();
			Prof profAppel=(Prof)profQuiFaitAppel.getSelectedItem();*/			
			Salle salleCurrent = listeSallesV.elementAt(salles.getCurrentIndex());
			Salle salleCurrent2 = listeSallesV.elementAt(salles2.getCurrentIndex());
			Groupe groupeCurrent = listeGroupesV.elementAt(groupes.getCurrentIndex());
			Matiere matiereCurrent = listeMatieresV.elementAt(matieres.getCurrentIndex()); 
			Prof profCurrent = listeProfsV.elementAt(profs.getCurrentIndex());
			Prof profCurrent2 = listeProfsV.elementAt(profs2.getCurrentIndex());
			Prof profCurrent3 = listeProfsV.elementAt(profs3.getCurrentIndex());
			Prof profCurrent4 = listeProfsV.elementAt(profs4.getCurrentIndex());  
			Prof profAppel;
			
			if(profQuiFaitAppel.getCurrentText().getValue()=="" || profQuiFaitAppel.getCurrentText().getValue()==null)profAppel=new Prof(0,null,null,null,null,false);
			else profAppel = profQuiFaitAppelV.get(profQuiFaitAppel.getCurrentText().getValue());
			
			PlageHoraire plageNEW;
			String memo=tf_memo.getText();
			if (matiereCurrent.getIdMatiere()==0) {
				/*JOptionPane.showMessageDialog(this,"Il faut choisir une matière...");*/
				Utilitaire.showMessageDialog(this,"Il faut choisir une matière...");
				/* setCursor(null);*/
				return;
			}
			if (memo.length()>20) {
				/*JOptionPane.showMessageDialog(this, "ATTENTION : le mémo est limité à 20 caractères...");*/
				Utilitaire.showMessageDialog(this, "ATTENTION : le mémo est limité à 20 caractères...");
				memo=memo.substring(0,20);
			}
			if (profCurrent.getIdProf()>0 && profAppel.getIdProf()>0) {
				/*JOptionPane.showMessageDialog(this, "ATTENTION : vous avez saisi à la fois un prof et un prof qui fait l'appel...");*/
				Utilitaire.showMessageDialog(this, "ATTENTION : vous avez saisi à la fois un prof et un prof qui fait l'appel...");
			}
			if (cb_controle.isChecked()) { 
				/*Salle salle2=(Salle)salles2.getSelectedItem();
				Prof prof2=(Prof)profs2.getSelectedItem();
				Prof prof3=(Prof)profs3.getSelectedItem();
				Prof prof4=(Prof)profs4.getSelectedItem();*/
				/*plageNEW=new PlageHoraire(edtAppli.getControleurDb().getIdPlageHoraire(), debut, fin, memo, salle.getIdSalle(), groupe.getIdGroupe(), matiere.getIdMatiere(), prof.getIdProf(), cb_internet.isChecked(), true, salle2.getIdSalle(), prof2.getIdProf(), prof3.getIdProf(), prof4.getIdProf(), profAppel.getIdProf());*/
				plageNEW=new PlageHoraire(edtAppli.getControleurDb().getIdPlageHoraire(), debut, fin, memo, salleCurrent.getIdSalle(), groupeCurrent.getIdGroupe(), matiereCurrent.getIdMatiere(), profCurrent.getIdProf(), cb_internet.isChecked(), true, salleCurrent2.getIdSalle(), profCurrent2.getIdProf(), profCurrent3.getIdProf(), profCurrent4.getIdProf(), profAppel.getIdProf());
			}
			/*else plageNEW=new PlageHoraire(edtAppli.getControleurDb().getIdPlageHoraire(), debut, fin,memo, salle.getIdSalle(), groupe.getIdGroupe(), matiere.getIdMatiere(), prof.getIdProf(), cb_internet.isChecked(), profAppel.getIdProf());*/
			else plageNEW=new PlageHoraire(edtAppli.getControleurDb().getIdPlageHoraire(), debut, fin,memo, salleCurrent.getIdSalle(), groupeCurrent.getIdGroupe(), matiereCurrent.getIdMatiere(), profCurrent.getIdProf(), cb_internet.isChecked(), profAppel.getIdProf());			
			//System.out.println("nouvelle plage : " + plageNEW + salle + " " + groupe + " " + matiere + " " + prof);
			String erreurInsertion=edtAppli.getControleurDb().ajouterPlageHoraire(plageNEW);
			edtAppli.getControleurDb().majPromoMatiereProf(groupeCurrent.getIdPromo(),matiereCurrent.getIdMatiere(),profCurrent.getIdProf());
			int repetition=1;
			while (repetition<Integer.parseInt("0"+tf_repeter.getText())) {
				GregorianCalendar debut_temp=new GregorianCalendar(tz);
				GregorianCalendar fin_temp=new GregorianCalendar(tz);
				debut_temp.setTimeInMillis(debut.getTimeInMillis());
				debut_temp.add(Calendar.DAY_OF_YEAR, repetition*7); // on ajoute 7 jours
				fin_temp.setTimeInMillis(fin.getTimeInMillis());
				fin_temp.add(Calendar.DAY_OF_YEAR, repetition*7); // on ajoute 7 jours
				/*plageNEW=new PlageHoraire(edtAppli.getControleurDb().getIdPlageHoraire(), debut_temp, fin_temp, tf_memo.getText(), salle.getIdSalle(), groupe.getIdGroupe(), matiere.getIdMatiere(), prof.getIdProf(), cb_internet.isChecked(), profAppel.getIdProf());*/
				plageNEW=new PlageHoraire(edtAppli.getControleurDb().getIdPlageHoraire(), debut_temp, fin_temp, tf_memo.getText(), salleCurrent.getIdSalle(), groupeCurrent.getIdGroupe(), matiereCurrent.getIdMatiere(), profCurrent.getIdProf(), cb_internet.isChecked(), profAppel.getIdProf());
			
				if (edtAppli.getControleurDb().getPlageHoraire(plageNEW.getDateDebut(), "", 0)==null) // ce n'est pas un jour férié
					erreurInsertion+=edtAppli.getControleurDb().ajouterPlageHoraire(plageNEW);
				repetition++;
			}
			if (!erreurInsertion.equals("")) {
				for (int i = 0; i < plages.size(); i++)
					erreurInsertion+=edtAppli.getControleurDb().ajouterPlageHoraire((PlageHoraire)(plages.get(i)))+"\n";
				/*JScrollPane erreurs=new JScrollPane(new JTextArea(erreurInsertion));
				erreurs.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
				erreurs.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		        erreurs.getVerticalScrollBar().setUnitIncrement(20);
				erreurs.setMaximumSize(new Dimension(800,800));*/
				/*JOptionPane.showMessageDialog(edtAppli, erreurs);*/
				Utilitaire.showMessageDialog(this, erreurInsertion.toString());
			}
			else {
				/*this.dispose();*/
				accept();
				edtAppli.mettreAJour();
			}
		}
		/*else if (b.equals(b_supprimer)) {*/
		else if (action.equals("Supprimer")) {
			/*this.dispose();*/
			accept();
			edtAppli.mettreAJour();
		}
		/*else if (b.equals(b_annuler)) {*/
		else if (action.equals("Annuler")) {
			if (((PlageHoraire)(plages.get(0))).getIdPlage()!=-1) // on doit remettre la ou les plages dans la base de données
				for (int i = 0; i < plages.size(); i++)
					edtAppli.getControleurDb().ajouterPlageHoraire((PlageHoraire)(plages.get(i)));
			/*this.dispose();*/
			reject();
			edtAppli.mettreAJour();
		}
		// on réaffiche le curseur normal
		/*setCursor(null);*/        
	}
}
