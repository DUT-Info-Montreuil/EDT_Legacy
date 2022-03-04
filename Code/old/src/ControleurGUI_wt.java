package EDT;

import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.Coordinates;
import eu.webtoolkit.jwt.KeyboardModifier;
import eu.webtoolkit.jwt.Signal1.Listener;
import eu.webtoolkit.jwt.WKeyEvent;

import java.awt.event.*;
import java.util.*;

/*
 * Created on 26 juin 2004
 *
 */
/**
 * @author fredphil
 * 
 */
public class ControleurGUI_wt implements  ItemListener, MouseListener, Listener<WKeyEvent> { //MouseListener, MouseMotionListener,  {ActionListener,
	EDTApplication_wt edtAppli; // l'application
	Vector<PlageHoraire> listePlagesSem; // la liste des plages de la semaine en cours
	PlageHoraire plageEnCours, plageOLD; // la plage en cours de déplacement et son ancienne position
	PlageHoraire historique, historiqueOLD; // pour les calculs du nombre d'heures effectué jusqu'à aujourd'hui

	public ControleurGUI_wt(EDTApplication_wt _edtAppli) {
		edtAppli = _edtAppli;
		listePlagesSem = null;
		plageEnCours = null;
		plageOLD = null;
		historique = null;
		historiqueOLD = null;
	}
	// les boutons de changement de semaine
	public void actionPerformed(ActionEvent e) {
		/*String label = ((JButton) e.getSource()).getText();
        String date; // la date courante
        int index_depart; // et son index dans la liste déroulante
        if (label.equals("Semaine précédente")) {
            date = (String) (edtAppli.getListeSemaines().getValueText());	//SelectedItem()); // la date courante
            index_depart = edtAppli.getListeSemaines().getSelectedIndex();
            int jour, mois, annee;
            GregorianCalendar cal = Utilitaire.calculerDate(date);
            cal.add(Calendar.DAY_OF_MONTH, -7); // on recule d'une semaine
            jour = cal.get(Calendar.DAY_OF_MONTH);
            mois = cal.get(Calendar.MONTH) + 1; // +1 car janvier=0
            annee = cal.get(Calendar.YEAR);
            date = "" + jour + "/" + mois + "/" + annee;
            edtAppli.getListeSemaines().setEditable(true);
            edtAppli.getListeSemaines().setSelectedItem(date);
            edtAppli.getListeSemaines().setEditable(false);
            if (edtAppli.getListeSemaines().getSelectedIndex() == -1) { // la semaine précédente n'était pas dans la liste
            // if (cal.get(Calendar.MONTH)<Calendar.AUGUST) { // si on essaie de retourner avant août (année scolaire précédente non modifiable)
            // cal.add(Calendar.DAY_OF_MONTH, 7); // on avance d'une semaine
            // jour = cal.get(Calendar.DAY_OF_MONTH);
            // mois = cal.get(Calendar.MONTH) + 1; // +1 car janvier=0
            // annee = cal.get(Calendar.YEAR);
            // date = "" + jour + "/" + mois + "/" + annee;
            // edtAppli.getListeSemaines().setSelectedItem(date);
            // } else {
                edtAppli.getListeSemaines().insertItemAt(date, index_depart);
                edtAppli.getListeSemaines().setSelectedItem(date);
                // System.out.println("N'EXISTAIT PAS : " + date + " " + index_depart);
                // }
            }
        } else if (label.equals("Semaine suivante")) {
            date = (String) (edtAppli.getListeSemaines().getValueText());	//getSelectedItem()); // la date courante
            index_depart = edtAppli.getListeSemaines().getSelectedIndex(); // l'index actuel dans la liste
            int jour, mois, annee;
            GregorianCalendar cal = Utilitaire.calculerDate(date);
            cal.add(Calendar.DAY_OF_MONTH, 7); // on avance d'une semaine
            jour = cal.get(Calendar.DAY_OF_MONTH);
            mois = cal.get(Calendar.MONTH) + 1; // +1 car janvier=0
            annee = cal.get(Calendar.YEAR);
            date = "" + jour + "/" + mois + "/" + annee;
            edtAppli.getListeSemaines().setEditable(true);
            edtAppli.getListeSemaines().setSelectedItem(date);
            edtAppli.getListeSemaines().setEditable(false);
            if (edtAppli.getListeSemaines().getSelectedIndex() == -1) { // la semaine suivante n'était pas dans la liste
            // if (cal.get(Calendar.MONTH)>Calendar.JULY) { // si on essaie d'aller après juillet (année scolaire suivante non modifiable)
            // cal.add(Calendar.DAY_OF_MONTH, -7); // on recule d'une semaine
            // jour = cal.get(Calendar.DAY_OF_MONTH);
            // mois = cal.get(Calendar.MONTH) + 1; // +1 car janvier=0
            // annee = cal.get(Calendar.YEAR);
            // date = "" + jour + "/" + mois + "/" + annee;
            // edtAppli.getListeSemaines().setSelectedItem(date);
            // } else {
                edtAppli.getListeSemaines().insertItemAt(date, index_depart + 1);
                edtAppli.getListeSemaines().setSelectedItem(date);
                // System.out.println("N'EXISTAIT PAS : " + date + " " +index_depart);
                // }
            }
        }
        edtAppli.mettreAJour();
		 */
	}

	public void action(String action) {
		if (action.equals("Semaine précédente")) {
			String date; // la date courante
			int index_depart, jour, mois, annee;

			date = (String) (edtAppli.getListeSemaines().getValueText());	//SelectedItem()); // la date courante
			index_depart = edtAppli.getListeSemaines().getCurrentIndex();	//SelectedIndex();

			GregorianCalendar cal = Utilitaire.calculerDate(date);
			cal.add(Calendar.DAY_OF_MONTH, -7); // on recule d'une semaine
			jour = cal.get(Calendar.DAY_OF_MONTH);
			mois = cal.get(Calendar.MONTH) + 1; // +1 car janvier=0
			annee = cal.get(Calendar.YEAR);
			date = "" + jour + "/" + mois + "/" + annee;
//			/*edtAppli.getListeSemaines().setReadOnly(false);	//setEditable(true);*/
//			edtAppli.getListeSemaines().setValueText(date);	//SelectedItem(date);
//			/*edtAppli.getListeSemaines().setReadOnly(true);	//Editable(false);*/
//			if (edtAppli.getListeSemaines().getCurrentIndex() == -1) {	//SelectedIndex() == -1) { // la semaine précédente n'était pas dans la liste
//			
//			}
			edtAppli.getListeSemaines().insertItem(index_depart, date);	//..At(date, index_depart);
			edtAppli.getListeSemaines().setValueText(date);	//SelectedItem(date);
			edtAppli.mettreAJour();	
		}
		if (action.equals("Semaine suivante")) {
			String date; // la date courante
			int index_depart, jour, mois, annee;				

			date = (String) (edtAppli.getListeSemaines().getValueText());	//getSelectedItem()); // la date courante
			index_depart = edtAppli.getListeSemaines().getCurrentIndex(); // l'index actuel dans la liste

			GregorianCalendar cal = Utilitaire.calculerDate(date);
			cal.add(Calendar.DAY_OF_MONTH, 7); // on avance d'une semaine
			jour = cal.get(Calendar.DAY_OF_MONTH);
			mois = cal.get(Calendar.MONTH) + 1; // +1 car janvier=0
			annee = cal.get(Calendar.YEAR);
			date = "" + jour + "/" + mois + "/" + annee;
			/*edtAppli.getListeSemaines().setReadOnly(false);*/
			//edtAppli.getListeSemaines().setValueText(date);
			/*edtAppli.getListeSemaines().setReadOnly(true);*/
			/*if (edtAppli.getListeSemaines().getCurrentIndex() == -1) { // la semaine suivante n'était pas dans la liste	
				
			}*/
			edtAppli.getListeSemaines().insertItem(index_depart + 1, date);
			edtAppli.getListeSemaines().setValueText(date);
			edtAppli.mettreAJour();					
		}				

	}

	// changement de semaine dans la liste déroulante
	public void itemStateChanged(ItemEvent e) {
		edtAppli.mettreAJour();
	}


	public void mouseClicked(MouseEvent e) {
	}
	public void mousePressed(MouseEvent e) {
	}
	public void mousePressed(WMouseEvent e) {
		//Utilitaire.showMessageDialog("cGUI","mousePressed!");
		if (edtAppli.getControleurDb().isAdmin()) { // on gère les jours fériés uniquement
			/*mousePressedAdmin(e);*/
			return;
		}
		if (edtAppli.getListePromotions().getValueText() == "") { // on gère si promo sélectionné uniquement
			return;
		}        
		if (!Utilitaire.anneeScolaireEnCours((String) edtAppli.getListeSemaines().getValueText())) { // SelectedItem())) { on n'est pas dans l'année scolaire en cours ou alors il y a plus de 3 mois
			/*JOptionPane.showMessageDialog(edtAppli, "Modif. interdite (plus de trois mois ou en dehors de l'année scolaire)");*/
			Utilitaire.showMessageDialog(edtAppli.getTitle().toString(), "Modif. interdite (plus de trois mois ou en dehors de l'année scolaire)");
			// setCursor(null);
			return;
		} else
			if (plageEnCours == null) { // on n'est pas en train de déplacer une plage horaire

				VueEDT_wt vue = edtAppli.getVueEDT(); // la vue
				EDTSemaine edtSemaine = vue.getEdtSemaine(); // la liste des cours de la semaine
				listePlagesSem = edtSemaine.getListePlages(); // on récupère la liste des plages horaires de la semaine
				/*int x = e.getX(); // on récupère les coordonnées de la souris
                int y = e.getY();*/
				Coordinates c = e.getWidget();
				int x = c.x;	
				int y = c.y;	


				// on récupère la plage horaire sur laquelle on a (éventuellement) cliqué
				plageEnCours = vue.trouverPlageHoraire(x, y); // la plage en cours de déplacement
				plageOLD = vue.trouverPlageHoraire(x, y); // sauvegarde de l'emplacement actuel de cette plage
				// System.out.println(plageEnCours);
				// System.out.println("jour férié ? " + edtSemaine.getListeJoursFeries().contains(plageEnCours));
				if (edtSemaine.getListeJoursFeries().contains(plageEnCours)) { // on a cliqué sur un jour férié
					plageEnCours = null;
					plageOLD = null;
					return;
				}
				if (plageEnCours != null) { // on veut déplacer ou modifier/supprimer une plage horaire
					//Utilitaire.showMessageDialog(edtAppli.getTitle().toString(), "on veut déplacer ou modifier/supprimer une plage horaire");
					// System.out.println("on a une plage à déplacer !" + plageEnCours);

					/* if (e.getButton() == MouseEvent.BUTTON1) { // bouton gauche : on déplace*/
					if (e.getButton() == WMouseEvent.Button.LeftButton) {                   
						// on place la plage cliquée en fin de liste (pour qu'elle se dessine par dessus les autres pendant le déplacement)
						if (!edtAppli.getCopierColler()) // si on n'est pas en train de copier/coller (sinon on laisse la plage dans la liste)
							listePlagesSem.remove(plageEnCours);
						int nbColonnes = edtAppli.getControleurDb().getGroupe(plageEnCours.getIdGroupe()).getNbColonnes();
						long duree = (plageEnCours.getDateFin().getTimeInMillis() - plageEnCours.getDateDebut().getTimeInMillis()) / 1000;
						// System.out.println("nbColonnes="+nbColonnes);
						plageEnCours.changerDateDebut(edtAppli.getVueEDT().getDateHeure(x, y, nbColonnes, duree)); // on aligne le haut de la plage sur la souris
						listePlagesSem.add(plageEnCours);
						if (!edtAppli.getCopierColler()) // si on n'est pas en train de copier/coller
							edtAppli.getControleurDb().supprimerPlageHoraire(plageEnCours.getIdPlage());
						// edtSemaine.setListePlages(listePlagesSem);
						// if (edtSemaine.getListePlages().size()>0)
						// System.out.println("taille AVANT : " + edtSemaine.getListePlages().size());
						// System.out.println("edtSemaine AVANT : " + edtSemaine);
						/*edtAppli.getVueEDT().repaint();*/
						edtAppli.getVueEDT().update();
						/*} else if (e.getButton() == MouseEvent.BUTTON3) { // bouton droit : on modifie ou supprime*/
					} else {	// if (e.getButton() == WMouseEvent.Button.RightButton) { // bouton droit : on modifie ou supprime
						// System.out.println(plageEnCours);
						// JPopupMenu pop=new JPopupMenu(); pop.add("Modifier"); pop.add("Supprimer"); pop.setVisible(true);
						edtAppli.getControleurDb().supprimerPlageHoraire(plageEnCours.getIdPlage());
						FormulairePlageHoraire_wt form = new FormulairePlageHoraire_wt(edtAppli, plageEnCours);
						plageEnCours = null;
						plageOLD = null;
						/*form.setVisible(true);*/
						form.show();
					}
				} else if (edtAppli.getListePromotions().getValueText() != "") { //SelectedItem() != "") { // on a cliqué dans le vide : on crée une nouvelle plage (si une promo est sélectionnée)
					//Utilitaire.showMessageDialog("cGUI"," on a cliqué dans le vide !");
					GregorianCalendar debut = vue.getDateHeure(x, y, 0, 0);
					GregorianCalendar fin = vue.getDateHeure(x, y, 0, 0);
					fin.add(Calendar.HOUR_OF_DAY, 2);
					// int groupe = vue.getPositionEDT(x, y);
					PlageHoraire plage = new PlageHoraire(-1, debut, fin, "", 0, 0, 0, 0, false, 0);
					FormulairePlageHoraire_wt form = new FormulairePlageHoraire_wt(edtAppli, plage);
					/*form.setVisible(true);*/
					form.show();
				}
			}
	}

	public void mouseReleased(MouseEvent e) {
	}
	public void mouseReleased(WMouseEvent e) {
		if (edtAppli.getControleurDb().isAdmin()) { // on gère les jours fériés uniquement
			/*mouseReleasedAdmin(e);*/
			return;
		}
		//Utilitaire.showMessageDialog("this","mouseRelased");            
		if (plageEnCours != null) { // on déplace une plage
			// System.out.println("released !");
			// on récupère les coordonnées de la souris
			/*int x = e.getX(); // on récupère les coordonnées de la souris
                int y = e.getY();*/
			Coordinates c = e.getWidget();
			int x = c.x;	
			int y = c.y;
			int nbColonnes = edtAppli.getControleurDb().getGroupe(plageEnCours.getIdGroupe()).getNbColonnes();
			long duree = (plageEnCours.getDateFin().getTimeInMillis() - plageEnCours.getDateDebut().getTimeInMillis()) / 1000;
			GregorianCalendar cal = edtAppli.getVueEDT().getDateHeure(x, y, nbColonnes, duree);
			//GregorianCalendar cal = edtAppli.getVueEDT().getDateHeure(x, y, 3, duree);	 	//AFAIRE: valeur "3" marche pas(si td=tp=1) ! //mais ?  
			// int position_edt=edtAppli.getVueEDT().getPositionEDT(x,y);
			plageEnCours.changerDateDebut(cal);
			// System.out.println("en cours : " + plageEnCours);
			// System.out.println("ancienne : " + plageOLD);
			String erreurInsertion = edtAppli.getControleurDb().ajouterPlageHoraire(plageEnCours);
			//Utilitaire.showMessageDialog(edtAppli.getTitle().toString(), erreurInsertion);
			if (!erreurInsertion.equals("")) {
				edtAppli.getControleurDb().ajouterPlageHoraire(plageOLD);
				/*JOptionPane.showMessageDialog(edtAppli, erreurInsertion);*/
				Utilitaire.showMessageDialog(edtAppli.getTitle().toString(), erreurInsertion);
			}
			if(edtAppli.getCopypaste().isChecked()) {
				edtAppli.getControleurDb().ajouterPlageHoraire(plageOLD);
			} 
			plageEnCours = null;
			plageOLD = null;
			edtAppli.mettreAJour(); // getVueEDT().repaint();
		}
	}
	//public void mouseDragged(MouseEvent e) {
	//}    
	public void mouseDragged(WMouseEvent e) {
		if (edtAppli.getControleurDb().isAdmin()) { // on gère les jours fériés uniquement
			/*mouseDraggedAdmin(e);*/
			return;
		}
		if (plageEnCours != null) { // on déplace une plage
			System.out.println("on déplace " + "plageEnCours");
			// on récupère les coordonnées de la souris
			/*int x = e.getX(); // on récupère les coordonnées de la souris
                int y = e.getY();*/
			Coordinates c = e.getWidget();
			int x = c.x;	
			int y = c.y; 
			int nbColonnes = edtAppli.getControleurDb().getGroupe(plageEnCours.getIdGroupe()).getNbColonnes();
			long duree = (plageEnCours.getDateFin().getTimeInMillis() - plageEnCours.getDateDebut().getTimeInMillis()) / 1000;			

			GregorianCalendar cal = edtAppli.getVueEDT().getDateHeure(x, y, nbColonnes, duree);
			//GregorianCalendar cal = edtAppli.getVueEDT().getDateHeure(x, y, 3, duree);        //AFAIRE: valeur "3" marche pas! (si td=tp=1)// mais ?    
			cal.set(Calendar.MINUTE, (cal.get(Calendar.MINUTE) / 10) * 10);
			// int position_edt=edtAppli.getVueEDT().getPositionEDT(x,y);
			plageEnCours.changerDateDebut(cal);

			/*edtAppli.getVueEDT().repaint();*/
			edtAppli.getVueEDT().update();
		}
	}
	//public void mouseMoved(MouseEvent e) {
	//}
	public void mouseMoved(WMouseEvent e) {
		// on récupère les coordonnées de la souris
		/*int x = e.getX(); // on récupère les coordonnées de la souris
                int y = e.getY();*/
		Coordinates c = e.getWidget();
		int x = c.x;	
		int y = c.y; 
		// on récupère la plage horaire sur laquelle on se trouve (éventuellement)
		VueEDT_wt vue = edtAppli.getVueEDT(); // la vue
		//////////Utilitaire.showMessageDialog("this","mouseMoved");    
		if (edtAppli.getHistoriquePlagesHoraires()) {
			String tip = "";
			/*int x = e.getX(); // on récupère les coordonnées de la souris
            int y = e.getY();
            // on récupère la plage horaire sur laquelle on se trouve (éventuellement)
            VueEDT_wt vue = edtAppli.getVueEDT(); // la vue*/
			historique = vue.trouverPlageHoraire(x, y); // la plage pour laquelle on va calculer l'historique
			if (historique == null)
				/*vue.setToolTipText(null);*/
				vue.setToolTip(null);
			else if (historiqueOLD == null || !historique.equals(historiqueOLD)) { // on a changé de plage : il faut recalculer
				historiqueOLD = historique;
				// EDTSemaine edtSemaine = vue.getEdtSemaine(); // la liste des cours de la semaine
				// listePlagesSem = edtSemaine.getListePlages(); // on récupère la liste des plages horaires de la semaine
				// if (!historique.getMemo().equals(""))
				// tip+=historique.getMemo()+"\n"; // le passage à la ligne ne fonctionne pas !
				tip += edtAppli.getControleurDb().getHistoriquePlageHoraire(historique);
				/*vue.setToolTipText(tip);*/
				vue.setToolTip(tip);
			}
		} else {
			/*int x = e.getX(); // on récupère les coordonnées de la souris
            int y = e.getY();
            // on récupère la plage horaire sur laquelle on se trouve (éventuellement)
            VueEDT_wt vue = edtAppli.getVueEDT(); // la vue*/
			PlageHoraire plage = vue.trouverPlageHoraire(x, y);
			/*vue.setToolTipText(edtAppli.getControleurDb().getNbHeuresHebdo(plage));*/
			vue.setToolTip(edtAppli.getControleurDb().getNbHeuresHebdo(plage));
		}

	}


	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}
	/**
	 * quand l'administrateur gère les jours fériés à la souris
	 */
	/*
    private void mousePressedAdmin(MouseEvent e) {
        if (plageEnCours == null) { // on n'est pas en train de déplacer un jour férié
            VueEDT vue = edtAppli.getVueEDT(); // la vue
            EDTSemaine edtSemaine = vue.getEdtSemaine(); // la liste des cours de la semaine
            listePlagesSem = edtSemaine.getListeJoursFeries(); // on récupère la liste des jours fériés de la semaine
            // on récupère les coordonnées de la souris
            int x = e.getX();
            int y = e.getY();
            // on récupère le jour férié sur lequel on a (éventuellement) cliqué
            plageEnCours = vue.trouverPlageHoraire(x, y); // le jour férié en cours de déplacement
            plageOLD = vue.trouverPlageHoraire(x, y); // sauvegarde de l'emplacement actuel du jour férié
            if (plageEnCours != null) { // on veut déplacer ou modifier/supprimer un jour férié
                // System.out.println("on a un jour férié à déplacer !" + plageEnCours);
                if (e.getButton() == MouseEvent.BUTTON1) { // bouton gauche : on déplace
                    // on place le jour férié cliqué en fin de liste (pour qu'il se dessine par dessus les autres pendant le déplacement)
                    listePlagesSem.remove(plageEnCours);
                    // int nbColonnes = edtAppli.getControleurDb().getGroupe(plageEnCours.getIdGroupe()).getNbColonnes();
                    long duree = (plageEnCours.getDateFin().getTimeInMillis() - plageEnCours.getDateDebut().getTimeInMillis()) / 1000;
                    plageEnCours.changerDateDebut(edtAppli.getVueEDT().getDateHeure(x, y, 1, duree)); // on
                    // aligne le haut de la plage sur la souris
                    listePlagesSem.add(plageEnCours);
                    edtAppli.getControleurDb().supprimerPlageHoraire(plageEnCours.getIdPlage());
                    // edtSemaine.setListePlages(listePlagesSem);
                    // if (edtSemaine.getListePlages().size()>0)
                    // System.out.println("taille AVANT : " + edtSemaine.getListePlages().size());
                    // System.out.println("edtSemaine AVANT : " + edtSemaine);
                    edtAppli.getVueEDT().repaint();
                } else if (e.getButton() == MouseEvent.BUTTON3) { // bouton droit : on supprime le jour férié
                    /*
	 * JPopupMenu pop=new JPopupMenu(); pop.add("Modifier"); pop.add("Supprimer"); pop.setVisible(true);
	 * /
                    edtAppli.getControleurDb().supprimerPlageHoraire(plageEnCours.getIdPlage());
                    plageEnCours = null;
                    plageOLD = null;
                    edtAppli.mettreAJour();
                }
            } else {
                // on a cliqué dans le vide : on crée un nouveau jour férié
                String texte;
                do {
                    texte = (String) JOptionPane.showInputDialog(edtAppli, "Entrer le texte du jour férié :", "Jour férié", JOptionPane.QUESTION_MESSAGE, null, null, "FERIE");
                } while (texte != null && texte == "");
                String erreurInsertion = "";
                ArrayList<Matiere> listeJoursFeries = new ArrayList<Matiere>(edtAppli.getControleurDb().getJoursFeries());
                // System.out.println(listeJoursFeries);
                if (texte != null) {
                    int index_jf = (listeJoursFeries).indexOf(new Matiere(-1, "JOURFERIE", texte, 0, 0, 0, ""));
                    if (index_jf == -1)
                        erreurInsertion = edtAppli.getControleurDb().ajouterMatiere("JOURFERIE", texte, 150, 150, 150, "", 0);
                    if (!erreurInsertion.equals("")) {
                        JScrollPane erreurs = new JScrollPane(new JTextArea(erreurInsertion));
                        erreurs.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                        erreurs.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                        erreurs.getVerticalScrollBar().setUnitIncrement(20);
                        erreurs.setMaximumSize(new Dimension(800, 800));
                        JOptionPane.showMessageDialog(edtAppli, erreurInsertion);
                        return;
                    }
                    listeJoursFeries = new ArrayList<Matiere>(edtAppli.getControleurDb().getJoursFeries());
                    index_jf = listeJoursFeries.indexOf(new Matiere(-1, "JOURFERIE", texte, 0, 0, 0, ""));
                    int id_matiere = ((Matiere) (listeJoursFeries.get(index_jf))).getIdMatiere();
                    GregorianCalendar debut = vue.getDateHeure(x, y, 0, 0);
                    GregorianCalendar fin = vue.getDateHeure(x, y, 0, 0);
                    debut.set(Calendar.HOUR_OF_DAY, 8); // un jour férié commence à 8h...
                    debut.set(Calendar.MINUTE, 0);
                    fin.set(Calendar.HOUR_OF_DAY, 18); // ...et se termine à 18h30
                    fin.set(Calendar.MINUTE, 30);
                    PlageHoraire plage = new PlageHoraire(edtAppli.getControleurDb().getIdPlageHoraire(), debut, fin, "", 0, 0, id_matiere, 0, false, 0);
                    erreurInsertion += edtAppli.getControleurDb().ajouterJourFerie(plage);
                    if (!erreurInsertion.equals("")) {
                        JScrollPane erreurs = new JScrollPane(new JTextArea(erreurInsertion));
                        erreurs.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                        erreurs.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                        erreurs.getVerticalScrollBar().setUnitIncrement(20);
                        erreurs.setMaximumSize(new Dimension(800, 800));
                        JOptionPane.showMessageDialog(edtAppli, erreurInsertion);
                    }
                }
                edtAppli.mettreAJour();
            }
        }
    }
    private void mouseReleasedAdmin(MouseEvent e) {
        if (plageEnCours != null) { // on déplace un jour férié
            // on récupère les coordonnées de la souris
            int x = e.getX();
            int y = e.getY();
            long duree = (plageEnCours.getDateFin().getTimeInMillis() - plageEnCours.getDateDebut().getTimeInMillis()) / 1000;
            GregorianCalendar cal = edtAppli.getVueEDT().getDateHeure(x, y, 1, duree);
            cal.set(Calendar.HOUR_OF_DAY, 8); // le jour férié commence à 8h (et finit à 18h30)
            // int position_edt=edtAppli.getVueEDT().getPositionEDT(x,y);
            plageEnCours.changerDateDebut(cal);
            // System.out.println("en cours : " + plageEnCours);
            // System.out.println("ancienne : " + plageOLD);
            String erreurInsertion = edtAppli.getControleurDb().ajouterPlageHoraire(plageEnCours);
            if (!erreurInsertion.equals("")) {
                edtAppli.getControleurDb().ajouterPlageHoraire(plageOLD);
                JScrollPane erreurs = new JScrollPane(new JTextArea(erreurInsertion));
                erreurs.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                erreurs.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                erreurs.getVerticalScrollBar().setUnitIncrement(20);
                erreurs.setMaximumSize(new Dimension(800, 800));
                JOptionPane.showMessageDialog(edtAppli, erreurInsertion);
            }
            plageEnCours = null;
            plageOLD = null;
            edtAppli.mettreAJour(); // getVueEDT().repaint();
        }
    }
    private void mouseDraggedAdmin(MouseEvent e) {
        if (plageEnCours != null) { // on déplace un jour férié
            // on récupère les coordonnées de la souris
            int x = e.getX();
            int y = e.getY();
            long duree = (plageEnCours.getDateFin().getTimeInMillis() - plageEnCours.getDateDebut().getTimeInMillis()) / 1000;
            GregorianCalendar cal = edtAppli.getVueEDT().getDateHeure(x, y, 1, duree);
            cal.set(Calendar.HOUR_OF_DAY, 8); // le jour férié commence à 8h (et finit à 18h30)
            // int position_edt=edtAppli.getVueEDT().getPositionEDT(x,y);
            plageEnCours.changerDateDebut(cal);
            edtAppli.getVueEDT().repaint();
        }
    }
	 */
	public void trigger(WKeyEvent e) {
	//	System.out.println(e.getKey().getValue());
	//	System.out.println(e.getModifiers().size());
	//	System.out.println(KeyEvent.VK_C);
		Iterator<KeyboardModifier>e1=e.getModifiers().iterator();
		while(e1.hasNext()) {
			if ((e1.next().getValue() == KeyEvent.CTRL_MASK) && (e.getKey().getValue() == KeyEvent.VK_C)) {
				if(!edtAppli.getCopypaste().isChecked())
					edtAppli.getCopypaste().setChecked();
				else edtAppli.getCopypaste().setUnChecked();
			}	
		}				
	}
}
