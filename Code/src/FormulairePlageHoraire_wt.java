package EDT;

import eu.webtoolkit.jwt.WDialog;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WComboBox;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WCheckBox;
import eu.webtoolkit.jwt.WLineEdit;
import eu.webtoolkit.jwt.WGridLayout;
import eu.webtoolkit.jwt.WLabel;
import eu.webtoolkit.jwt.WSpinBox;
import eu.webtoolkit.jwt.WDateEdit;
import eu.webtoolkit.jwt.WTimeEdit;
import eu.webtoolkit.jwt.WTime;
import eu.webtoolkit.jwt.Side;
import eu.webtoolkit.jwt.Signal;

import java.util.*;

/**
 * formulaire d'ajout ou de modification de plage horaire
 */
public class FormulairePlageHoraire_wt extends WDialog {
	ArrayList<PlageHoraire> plages; // liste des plages Ã  ajouter/modifier/supprimer
	EDTApplication_wt edtAppli;
	WLineEdit tf_memo;
	WComboBox salles, salles2, groupes, matieres, profs, profs2, profs3, profs4, profQuiFaitAppel;
	WPushButton b_valider, b_supprimer, b_annuler;
	WCheckBox cb_controle, cb_internet;
	WSpinBox tf_repeter;	//repetition;
	WDateEdit tf_date;
	WTimeEdit tf_debut, tf_fin;
	Vector<Salle> listeSallesV;
	WLabel label;
	Vector<Groupe>listeGroupesV;

	Vector<Matiere>listeMatieresV;
	Vector<Prof>listeProfsV;
	HashMap<String,Prof> profQuiFaitAppelV;
	Signal.Listener signalValider;
	WGridLayout gridBoutons;


	FormulairePlageHoraire_wt(EDTApplication_wt _edtAppli, PlageHoraire _plage) {
		super("Ajout/modification de plage horaire");	

		edtAppli=_edtAppli;
		plages=new ArrayList<PlageHoraire>();
		plages.add(_plage);

		WContainerWidget panel = new WContainerWidget();		
		WGridLayout grid = new WGridLayout();
		panel.setLayout(grid);

		WContainerWidget panelDates = new WContainerWidget();
		WGridLayout gridDates = new WGridLayout();
		panelDates.setLayout(gridDates);

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

		cb_controle=new WCheckBox("controle");
		cb_controle.setChecked(_plage.IsControle());
		cb_internet=new WCheckBox("internet");
		cb_internet.setChecked(_plage.accesInternet());		
		WContainerWidget cbs = new WContainerWidget();
		cbs.addWidget(cb_controle);
		cbs.addWidget(cb_internet);

		gridDates.addWidget(new WLabel("le :"), 0, 0);
		gridDates.addWidget(tf_date, 0, 1);
		gridDates.addWidget(new WLabel("de :"), 1, 0);
		gridDates.addWidget(tf_debut, 1, 1);
		gridDates.addWidget(new WLabel("à  :"), 3, 0);
		gridDates.addWidget(tf_fin, 3, 1);
		gridDates.addWidget(new WLabel("répétition :"), 4, 0);	
		gridDates.addWidget(tf_repeter, 4, 1);
		gridDates.addWidget(new WLabel("mémo :"), 5, 0);
		gridDates.addWidget(tf_memo, 5, 1);
		gridDates.addWidget(cbs, 6, 1);

		WContainerWidget panelBas = new WContainerWidget(); 
		WGridLayout gridBas = new WGridLayout();
		panelBas.setLayout(gridBas);

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

		salles.setEnabled(false);
		salles2.setEnabled(false);
		groupes.setEnabled(false);
		matieres.setEnabled(false);
		profs.setEnabled(false);
		profs2.setEnabled(false);
		profs3.setEnabled(false);
		profs4.setEnabled(false);
		profQuiFaitAppel.setEnabled(false);

		WContainerWidget panelBoutons=new WContainerWidget();
		gridBoutons = new WGridLayout();
		panelBoutons.setLayout(gridBoutons);		

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

		b_supprimer.setEnabled(false);

		grid.addWidget(panelDates, 0, 0);
		grid.addWidget(panelBas, 0, 1);
		getContents().addWidget(panel);
		getFooter().addWidget(b_supprimer);
		getFooter().addWidget(b_valider);		
		getFooter().addWidget(b_annuler);		


		if(edtAppli.getHistoriquePlagesHoraires().isChecked() && _plage.getIdGroupe()!=0) {
			label = new WLabel(edtAppli.getControleurDb().getHistoriquePlageHoraire(_plage));
		}else {
			label = new WLabel();
		}
		label.setFloatSide(Side.Left);
		getFooter().addWidget(label);
		
		
		
		this.setClosable(true);
		this.rejectWhenEscapePressed(true);
		this.show();
	}
	public void action(String action) {
		if (action.equals("Valider date/heures")) {
			if (tf_debut.getText().lastIndexOf(':')==tf_debut.getText().length()-1) // la saisie se termine par h				
				tf_debut.setText(tf_debut.getText()+"0");
			if (tf_fin.getText().lastIndexOf(':')==tf_fin.getText().length()-1) // la saisie se termine par h
				tf_fin.setText(tf_fin.getText()+"0");
			tf_date.setText(tf_date.getText().replaceAll(" +","")); // on Ã©limine tous les espaces
			tf_debut.setText(tf_debut.getText().replaceAll(" +","")); // on Ã©limine tous les espaces
			tf_fin.setText(tf_fin.getText().replaceAll(" +","")); // on Ã©limine tous les espaces
			Vector<PlageHoraire> jours_feries=edtAppli.getControleurDb().getJoursFeries(tf_date.getText());
			for (Iterator<PlageHoraire> iter = jours_feries.iterator(); iter.hasNext();) {
				PlageHoraire plage_jf= iter.next();
				if (plage_jf.getDateDebut().get(Calendar.DAY_OF_YEAR)==Utilitaire.calculerDate(tf_date.getText()).get(Calendar.DAY_OF_YEAR)) {
					Utilitaire.showMessageDialog(this, "Vous ne pouvez pas mettre de cours un jour fÃ©riÃ©...");
					return;
				}
			}
			if (Utilitaire.dateCorrecte(tf_date.getText(),edtAppli.getLoginEDT()) && Utilitaire.heureCorrecte(tf_debut.getText()) && Utilitaire.heureCorrecte(tf_fin.getText())) {
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
					Utilitaire.showMessageDialog(this, "Attention : vous saisissez une date hors de la semaine en cours...");
				if (!Utilitaire.anneeScolaireEnCours2(tf_date.getText(),edtAppli.getLoginEDT()))
					Utilitaire.showMessageDialog(this,  "Attention, la date est en dehors de l'annÃ©e scolaire !!!");
				if (fin.getTimeInMillis()-debut.getTimeInMillis()<600000) { // temps nÃ©gatif ou de moins de 10 minutes
					Utilitaire.showMessageDialog(this, "Pas de plage horaire de moins de 10 minutes..."); 
					return;
				}
				int repetition=1;
				boolean plageIdentique=true;


				PlageHoraire plage_originale=(PlageHoraire)(plages.get(0));


				if (plage_originale.getIdPlage()!=-1) { // on a une plage existante Ã  modifier

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
						PlageHoraire jourFerie=edtAppli.getControleurDb().getPlageHoraire(debut_temp, "", 0);

						Groupe g=edtAppli.getControleurDb().getGroupe(plage_originale.getIdGroupe());
						PlageHoraire plage_temp=edtAppli.getControleurDb().getPlageHoraire(debut_temp, edtAppli.getControleurDb().getPromo(g.getIdGroupe()), g.getIndexColonne());

						if (jourFerie!=null) // on a un jour fÃ©riÃ© la semaine suivante : on avance
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
							Utilitaire.showMessageDialog(this,"Seulement " + repetition + " répétitions possibles...");
						}
					}
				}
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
				listeSallesV=new Vector<Salle>(listeSalles);
				listeSallesV.add(0, edtAppli.getControleurDb().getSalle(0));

				for (Iterator<Salle> i=listeSallesV.iterator(); i.hasNext();) {
					Salle s=i.next();

					salles.addItem(s.toString());
					salles2.addItem(s.toString());
				}

				salles.setValueText(((Salle)edtAppli.getControleurDb().getSalle(((PlageHoraire)plages.get(0)).getIdSalle())).toString());
				salles2.setValueText(((Salle)edtAppli.getControleurDb().getSalle(((PlageHoraire)plages.get(0)).getIdSalle2())).toString());

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
					listeGroupes.retainAll(temp);	
					repetition++;
				}
				
				listeGroupesV=new Vector<Groupe>(listeGroupes);
				for(Groupe g:listeGroupesV) {
					groupes.addItem(g.getNom());
				}				
				
				

				Groupe l=edtAppli.getControleurDb().getGroupe(plages.get(0).getIdGroupe());


				
				groupes.setValueText(l.getNom());
				
				
				
				
				
				
				
				if (groupes.getCount()==0) {					
					Utilitaire.showMessageDialog(this,"Aucun groupe n'est libre : changer l'heure ou la promo");
					/*setCursor(null);*/
					return;
				}

				matieres.clear();
				TreeSet<Matiere> listeMatieresPromo=edtAppli.getControleurDb().getMatieresPromo(edtAppli.getControleurDb().getIdPromotion((String)edtAppli.getListePromotions().getValueText()));	//SelectedItem()));
				TreeSet<Matiere> listeMatieresAdmin=edtAppli.getControleurDb().getMatieresAdmin(edtAppli.getControleurDb().getIdAdmin());
				listeMatieresAdmin.removeAll(listeMatieresPromo); // on enlÃ¨ve les matiÃ¨res qu'on a dÃ©jÃ  mis en dÃ©but de liste
				TreeSet<Matiere> listeMatieresOrphelines=edtAppli.getControleurDb().getMatieresOrphelines();
				listeMatieresOrphelines.removeAll(listeMatieresPromo); // on enlÃ¨ve les matiÃ¨res qu'on a dÃ©jÃ  mis en dÃ©but de liste
				listeMatieresOrphelines.removeAll(listeMatieresAdmin); // on enlÃ¨ve les matiÃ¨res qu'on a dÃ©jÃ  mis en dÃ©but de liste
				/*Vector<Matiere>listeMatieresV=new Vector<Matiere>(listeMatieresPromo); // on place en premier les matiÃ¨res de la promo courante*/
				listeMatieresV=new Vector<Matiere>(listeMatieresPromo); // on place en premier les matiÃ¨res de la promo courante
				listeMatieresV.add(edtAppli.getControleurDb().getMatiere(0));
				listeMatieresV.addAll(listeMatieresAdmin); // on ajoute les matiÃ¨res de l'admin qui ne sont attribuÃ©es Ã  aucune promo
				listeMatieresV.add(edtAppli.getControleurDb().getMatiere(0));
				listeMatieresV.addAll(listeMatieresOrphelines); // on ajoute les autres matiÃ¨res qui ne sont attribuÃ©es Ã  aucune promo
				//System.out.println("liste=" + liste);
				for (Iterator<Matiere> i=listeMatieresV.iterator(); i.hasNext();)
					/*matieres.addItem(i.next());*/
					matieres.addItem(((Matiere)i.next()).toString());
				/*if (matieres.getItemCount()==0) {*/
				if (matieres.getCount()==0) {					
					/*JOptionPane.showMessageDialog(this,"Il faut crÃ©er une matiÃ¨re !!!");*/
					Utilitaire.showMessageDialog(this,"Il faut créer une matiere !!!");
					/*dispose();*/
					reject();
				}
				// prÃ©selection de la matiÃ¨re
				int id_m=((PlageHoraire)plages.get(0)).getIdMatiere();
				if (id_m!=0) // matiÃ¨re non vide
					/*matieres.setSelectedItem(edtAppli.getControleurDb().getMatiere(id_m));*/
					matieres.setValueText(edtAppli.getControleurDb().getMatiere(id_m).toString());
				/*else matieres.setSelectedIndex(0); // sÃ©lection de la premiÃ¨re matiÃ¨re*/
				else matieres.setCurrentIndex(0); // sÃ©lection de la premiÃ¨re matiÃ¨re
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
				if (Integer.parseInt(tab[1])<7) // on est avant juin : le dÃ©but de l'annÃ©e est l'annÃ©e d'avant
					dateDebutAnneeScolaire--;
				TreeSet<Prof> listeProfsAdmin=edtAppli.getControleurDb().getProfsAdmin(edtAppli.getControleurDb().getIdAdmin(), dateDebutAnneeScolaire, true); // les profs visibles de l'admin
				// on prÃ©pare la liste de tous les profs (pour choisir celui qui fera l'appel)
				profQuiFaitAppel.addItem(((Prof)edtAppli.getControleurDb().getProf((0))).toString()); // on ajoute une ligne vide au dÃ©but (par dÃ©faut: aucun prof)
				profQuiFaitAppelV = new HashMap<String,Prof>();
				for (Iterator<Prof> i=listeProfsAdmin.iterator(); i.hasNext();) {
					Prof p=(Prof)i.next();
					/*profQuiFaitAppel.addItem(p);*/
					profQuiFaitAppel.addItem(p.toString());
					profQuiFaitAppelV.put(p.toString(),p);
				}
				listeProfsLibres.remove(edtAppli.getControleurDb().getProf((0))); // on retire le prof numÃ©ro 0 (sinon il apparait une fois de trop en bas de liste)
				listeProfsPromo.retainAll(listeProfsLibres); // on ne garde que les profs libres
				listeProfsAdmin.retainAll(listeProfsLibres); // on ne garde que les profs libres
				listeProfsAdmin.removeAll(listeProfsPromo); // on ne garde que les profs qui ne sont pas dÃ©jÃ  dans la premiÃ¨re liste
				listeProfsLibres.removeAll(listeProfsAdmin); // on ne garde que les profs qui ne sont pas dÃ©jÃ  dans la premiÃ¨re liste
				listeProfsLibres.removeAll(listeProfsPromo); // on ne garde que les profs qui ne sont pas dÃ©jÃ  dans la premiÃ¨re liste
				listeProfsV=new Vector<Prof>(listeProfsPromo); // on place en premier les profs de la promo courante
				listeProfsV.add(edtAppli.getControleurDb().getProf((0))); // on ajoute une ligne vide pour sÃ©parer des autres profs
				listeProfsV.addAll(listeProfsAdmin); // on place le reste des profs libres de l'admin
				listeProfsV.add(0, edtAppli.getControleurDb().getProf((0))); // on ajoute une ligne vide en tete de listeV
				listeProfsV.addAll(listeProfsLibres); // on place le reste des profs libres
				for (Iterator<Prof> i=listeProfsV.iterator(); i.hasNext();) {
					Prof p=(Prof)i.next();
					profs.addItem(p.toString());
					profs2.addItem(p.toString());
					profs3.addItem(p.toString());
					profs4.addItem(p.toString());
				}
				profs.setValueText(((Prof)edtAppli.getControleurDb().getProf(((PlageHoraire)plages.get(0)).getIdProf())).toString());
				profs2.setValueText(((Prof)edtAppli.getControleurDb().getProf(((PlageHoraire)plages.get(0)).getIdProf2())).toString());
				profs3.setValueText(((Prof)edtAppli.getControleurDb().getProf(((PlageHoraire)plages.get(0)).getIdProf3())).toString());
				profs4.setValueText(((Prof)edtAppli.getControleurDb().getProf(((PlageHoraire)plages.get(0)).getIdProf4())).toString());
				profQuiFaitAppel.setValueText(((Prof)edtAppli.getControleurDb().getProf(((PlageHoraire)plages.get(0)).getIdProfQuiFaitAppel())).toString());				

				b_supprimer.setEnabled(true);
				b_valider.remove();
				b_valider.clicked().removeListener(signalValider);
				signalValider = null;					
				// bouton b_valider2
				WPushButton b_valider2 = new WPushButton("Valider la plage horaire");
				b_valider2.setStyleClass("btn-success");
				Signal.Listener signalValider2 = new Signal.Listener() {
					public void trigger() {
						action("Valider");
					}
				};				
				b_valider2.clicked().addListener(this, signalValider2);

				b_valider2.setAttributeValue("style", "float:right;");
				b_annuler.setAttributeValue("style", "float:right");				
				getFooter().addWidget(b_valider2);

			}
			else {
				Utilitaire.showMessageDialog(this, "Mauvais format de date(jj/mm/aaaa) ou d'heure (ex. 10h10) ou modif. interdite (plus de trois mois ou en dehors de l'annÃ©e scolaire)");
				/*setCursor(null);*/
				return;
			} 
			// on Ã©change les parties actives et non actives
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
			salles2.setEnabled(true); //added 12/02/2019
			if (cb_controle.isChecked()) {	//isSelected()) {
				//salles2.setEnabled(true);//debug : 12/02/2019
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
			Matiere matiereCurrent = listeMatieresV.elementAt(matieres.getCurrentIndex()); 
			Prof profCurrent = listeProfsV.elementAt(profs.getCurrentIndex());
			Prof profCurrent2 = listeProfsV.elementAt(profs2.getCurrentIndex());
			Prof profCurrent3 = listeProfsV.elementAt(profs3.getCurrentIndex());
			Prof profCurrent4 = listeProfsV.elementAt(profs4.getCurrentIndex());  
			
			Groupe groupeCurrent = listeGroupesV.elementAt(groupes.getCurrentIndex());
			Prof profAppel;
			if(profQuiFaitAppel.getCurrentText().getValue()=="" || profQuiFaitAppel.getCurrentText().getValue()==null)profAppel=new Prof(0,null,null,null,null,false);
			else profAppel = profQuiFaitAppelV.get(profQuiFaitAppel.getCurrentText().getValue());

			PlageHoraire plageNEW;
			String memo=tf_memo.getText();
			if (matiereCurrent.getIdMatiere()==0) {
				/*JOptionPane.showMessageDialog(this,"Il faut choisir une matiÃ¨re...");*/
				Utilitaire.showMessageDialog(this,"Il faut choisir une matiÃ¨re...");
				/* setCursor(null);*/
				return;
			}
			if (memo.length()>20) {
				/*JOptionPane.showMessageDialog(this, "ATTENTION : le mÃ©mo est limitÃ© Ã  20 caractÃ¨res...");*/
				Utilitaire.showMessageDialog(this, "ATTENTION : le mÃ©mo est limitÃ© Ã  20 caractÃ¨res...");
				memo=memo.substring(0,20);
			}
			if (profCurrent.getIdProf()>0 && profAppel.getIdProf()>0) {
				/*JOptionPane.showMessageDialog(this, "ATTENTION : vous avez saisi Ã  la fois un prof et un prof qui fait l'appel...");*/
				Utilitaire.showMessageDialog(this, "ATTENTION : vous avez saisi Ã  la fois un prof et un prof qui fait l'appel...");
			}
			if (cb_controle.isChecked()) { //debug : 12/02/2019 
				plageNEW=new PlageHoraire(edtAppli.getControleurDb().getIdPlageHoraire(), debut,
					fin, memo, salleCurrent.getIdSalle(), groupeCurrent.getIdGroupe(), matiereCurrent.getIdMatiere(),
					profCurrent.getIdProf(), cb_internet.isChecked(), true, salleCurrent2.getIdSalle(), 
					profCurrent2.getIdProf(), profCurrent3.getIdProf(), profCurrent4.getIdProf(), profAppel.getIdProf());
			}
			else 
				plageNEW=new PlageHoraire(edtAppli.getControleurDb().getIdPlageHoraire(), debut,
					fin, memo, salleCurrent.getIdSalle(), groupeCurrent.getIdGroupe(), matiereCurrent.getIdMatiere(),
					profCurrent.getIdProf(), cb_internet.isChecked(), false, salleCurrent2.getIdSalle(), 
					profCurrent2.getIdProf(), profCurrent3.getIdProf(), profCurrent4.getIdProf(), profAppel.getIdProf());
				
			//      plageNEW=new PlageHoraire(edtAppli.getControleurDb().getIdPlageHoraire(), debut,
			//	    fin,memo, salleCurrent.getIdSalle(), groupeCurrent.getIdGroupe(), matiereCurrent.getIdMatiere(),
			//       profCurrent.getIdProf(), cb_internet.isChecked(), profAppel.getIdProf());			
				
			
			
			
			

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

				plageNEW=new PlageHoraire(edtAppli.getControleurDb().getIdPlageHoraire(), 
					         debut_temp, fin_temp, tf_memo.getText(), salleCurrent.getIdSalle(), groupeCurrent.getIdGroupe(), 
					         matiereCurrent.getIdMatiere(), profCurrent.getIdProf(), cb_internet.isChecked(), profAppel.getIdProf());

				if (edtAppli.getControleurDb().getPlageHoraire(plageNEW.getDateDebut(), "", 0)==null) // ce n'est pas un jour fÃ©riÃ©
					erreurInsertion+=edtAppli.getControleurDb().ajouterPlageHoraire(plageNEW);
				repetition++;
			}
			
			if (!erreurInsertion.equals("")) {
				for (int i = 0; i < plages.size(); i++)
					erreurInsertion+=edtAppli.getControleurDb().ajouterPlageHoraire((PlageHoraire)(plages.get(i)))+"\n";
				Utilitaire.showMessageDialog(this, erreurInsertion.toString());
			}
			else {
				accept();
				edtAppli.mettreAJour();
			}
		}
		else if (action.equals("Supprimer")) {
			accept();
			edtAppli.mettreAJour();
		}
		else if (action.equals("Annuler")) {
			if (((PlageHoraire)(plages.get(0))).getIdPlage()!=-1) // on doit remettre la ou les plages dans la base de donnÃ©es
				for (int i = 0; i < plages.size(); i++)
					edtAppli.getControleurDb().ajouterPlageHoraire((PlageHoraire)(plages.get(i)));
			label = new WLabel();
			reject();
			edtAppli.mettreAJour();
		}
	    
	}
}
