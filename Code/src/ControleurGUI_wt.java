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
public class ControleurGUI_wt implements  ItemListener, Listener<WKeyEvent> { //MouseListener, MouseMotionListener,  {ActionListener,
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

	public void mouseMoved(MouseEvent e) {
	}   
	/*public void mouseEntered(WMouseEvent e) {
		VueEDT_wt vue1 = edtAppli.getVueEDT(); 
 		Coordinates c1 = e.getWidget();
 		int x1 = c1.x;	
 		int y1 = c1.y; 
 		PlageHoraire plage = vue1.trouverPlageHoraire(x1, y1);
 		if(plage!=null){
 			String nbreHeuresHebdo=edtAppli.getControleurDb().getNbHeuresHebdo(plage);
 			if(nbreHeuresHebdo!=null) Utilitaire.showMessageDialog("", nbreHeuresHebdo);
 			else Utilitaire.showMessageDialog("", "ceci n'est pas un groupe de TP");
 		}
	}*/
	public void mouseExited(MouseEvent e) {
	}
	public void mouseReleased(MouseEvent e) {
	}
	public void mouseClicked(MouseEvent e) {
	}
	public void mousePressed(MouseEvent e) {
	}
	public void mouseDragged(MouseEvent e){
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
			if (edtAppli.getListeSemaines().getCurrentIndex() == 0 || !edtAppli.getListeSemaines().getItemText(edtAppli.getListeSemaines().getCurrentIndex()-1).getValue().equals(date)) {	 // la semaine précédente n'était pas dans la liste
				edtAppli.getListeSemaines().insertItem(index_depart, date);	//..At(date, index_depart);
			}
			edtAppli.getNotifsGroups().clear();
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
			//System.out.println(edtAppli.getListeSemaines().getCurrentText().getValue());
			if (edtAppli.getListeSemaines().getCount()-1 == edtAppli.getListeSemaines().getCurrentIndex() || !edtAppli.getListeSemaines().getItemText(edtAppli.getListeSemaines().getCurrentIndex()+1).getValue().equals(date)) { // la semaine suivante n'était pas dans la liste	
				edtAppli.getListeSemaines().insertItem(index_depart + 1, date);
			}
			edtAppli.getNotifsGroups().clear();
			edtAppli.getListeSemaines().setValueText(date);
			edtAppli.mettreAJour();					
		}				
	}

	// changement de semaine dans la liste déroulante
	public void itemStateChanged(ItemEvent e) {
		edtAppli.mettreAJour();
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
		if (!Utilitaire.anneeScolaireEnCours2((String) edtAppli.getListeSemaines().getValueText(),edtAppli.getLoginEDT())) { // SelectedItem())) { on n'est pas dans l'année scolaire en cours ou alors il y a plus de 3 mois
			/*JOptionPane.showMessageDialog(edtAppli, "Modif. interdite (plus de trois mois ou en dehors de l'année scolaire)");*/
			Utilitaire.showMessageDialog(edtAppli.getTitle().toString(), "Modif. interdite (plus de trois mois ou en dehors de l'année scolaire)");
			// setCursor(null);
			return;
		} else
			if (plageEnCours == null) { // on n'est pas en train de déplacer une plage horaire

				VueEDT_wt vue = edtAppli.getVueEDT(); // la vue
				//VueBlanche_wt vueB = edtAppli.getVueBlanche_wt(); // la vue
				Coordinates c = e.getWidget();
				
				int x = c.x;	
				int y = c.y;	
				
				EDTSemaine edtSemaine = null;
				//EDTSemaine_Blanche edtSemaine2 = null;

				/*if(y>edtAppli.getVueEDT().getHeight().getValue()) {
					System.out.println("BLANCERINO");
					edtSemaine2 = vueB.getEdtSemaine();
					listePlagesSem = edtSemaine2.getListePlages(); // on récupère la liste des plages horaires de la semaine
					plageEnCours = vueB.trouverPlageHoraire(x, y); // la plage en cours de déplacement
					plageOLD = vueB.trouverPlageHoraire(x, y);
				}else {*/
					edtSemaine = vue.getEdtSemaine(); // la liste des cours de la semaine
					listePlagesSem = edtSemaine.getListePlages(); // on récupère la liste des plages horaires de la semaine
					plageEnCours = vue.trouverPlageHoraire(x, y); // la plage en cours de déplacement
					plageOLD = vue.trouverPlageHoraire(x, y); // sauvegarde de l'emplacement actuel de cette plage
					
				//}
				
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
						if (!edtAppli.getCopypaste().isChecked()) // si on n'est pas en train de copier/coller (sinon on laisse la plage dans la liste)
							listePlagesSem.remove(plageEnCours);
						int nbColonnes = edtAppli.getControleurDb().getGroupe(plageEnCours.getIdGroupe()).getNbColonnes();
						long duree = (plageEnCours.getDateFin().getTimeInMillis() - plageEnCours.getDateDebut().getTimeInMillis()) / 1000;
						// System.out.println("nbColonnes="+nbColonnes);
						plageEnCours.changerDateDebut(edtAppli.getVueEDT().getDateHeure(x, y, nbColonnes, duree)); // on aligne le haut de la plage sur la souris
						listePlagesSem.add(plageEnCours);
						if (!edtAppli.getCopypaste().isChecked()) { // si on n'est pas en train de copier/coller
							edtAppli.getControleurDb().supprimerPlageHoraire(plageEnCours.getIdPlage());
							edtAppli.getNotifsGroups().put(plageEnCours.getIdGroupe(),plageEnCours.getIdProf());//getnom groupe
						}
						// edtSemaine.setListePlages(listePlagesSem);
						// if (edtSemaine.getListePlages().size()>0)
						// System.out.println("taille AVANT : " + edtSemaine.getListePlages().size());
						// System.out.println("edtSemaine AVANT : " + edtSemaine);
						/*edtAppli.getVueEDT().repaint();*/
						edtAppli.getVueEDT().update();
						/*} else if (e.getButton() == MouseEvent.BUTTON3) { // bouton droit : on modifie ou supprime*/
					} else if(e.getButton() == WMouseEvent.Button.MiddleButton) {	//ajouté 25/02/2019
 					     VueEDT_wt vue1 = edtAppli.getVueEDT(); 
 					     Coordinates c1 = e.getWidget();
		   			     int x1 = c1.x;	
			             int y1 = c1.y; 
					  /*if (edtAppli.getHistoriquePlagesHoraires().isChecked()) {
					     String tip = "";
					     historique = vue1.trouverPlageHoraire(x1, y1);
			 
					     if (historique == null) {				 
					     	 vue1.setToolTip(null);
					     } else if (historiqueOLD == null || !historique.equals(historiqueOLD)) {  
					        historiqueOLD = historique;
					        tip += edtAppli.getControleurDb().getHistoriquePlageHoraire(historique);
					        vue1.setToolTip(tip);
		  			     } else {
 
	   				        PlageHoraire plage = vue1.trouverPlageHoraire(x1, y1);
					        vue1.setToolTip(edtAppli.getControleurDb().getNbHeuresHebdo(plage));
					     }*/
					     PlageHoraire plage = vue1.trouverPlageHoraire(x1, y1);
					     String nbreHeuresHebdo=edtAppli.getControleurDb().getNbHeuresHebdo(plage);
					     if(nbreHeuresHebdo!=null)
				            Utilitaire.showMessageDialog("", nbreHeuresHebdo);
				         else 
                     		Utilitaire.showMessageDialog("", "ceci n'est pas un groupe de TP");		      
					  }
					  else {
						// if (e.getButton() == WMouseEvent.Button.RightButton) { // bouton droit : on modifie ou supprime
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
			System.out.println("x="+x+"  y="+y);
			if(y>edtAppli.getVueEDT().getHeight().getValue()) {
				System.out.println("Blanche");
				edtAppli.getControleurDb().ajouterPlageHoraire(plageOLD);
				edtAppli.getControleurDb().ajouterPlageHoraire(plageEnCours);
				//edtAppli.mettreAJourBlanche(plageEnCours);
				plageEnCours = null;
				plageOLD = null;
				edtAppli.mettreAJour();
			}else {
				int nbColonnes = edtAppli.getControleurDb().getGroupe(plageEnCours.getIdGroupe()).getNbColonnes();
				long duree = (plageEnCours.getDateFin().getTimeInMillis() - plageEnCours.getDateDebut().getTimeInMillis()) / 1000;
				GregorianCalendar cal = edtAppli.getVueEDT().getDateHeure(x, y, nbColonnes, duree);
				plageEnCours.changerDateDebut(cal);
				String erreurInsertion = edtAppli.getControleurDb().ajouterPlageHoraire(plageEnCours);
				if (!erreurInsertion.equals("")) {
					edtAppli.getControleurDb().ajouterPlageHoraire(plageOLD);
					Utilitaire.showMessageDialog(edtAppli.getTitle().toString(), erreurInsertion);
				}else {
					edtAppli.getNotifsGroups().put(plageEnCours.getIdGroupe(),plageEnCours.getIdProf());//getnom groupe
					/*if(edtAppli.getVueBlanche_wt().getEdtSemaine().getListePlages().contains(plageEnCours)) {
						edtAppli.getVueBlanche_wt().getEdtSemaine().getListePlages().remove(plageEnCours);
					}*/
				}
				if(edtAppli.getCopypaste().isChecked()) {
					edtAppli.getControleurDb().ajouterPlageHoraire(plageOLD);
				} 
				plageEnCours = null;
				plageOLD = null;
				edtAppli.mettreAJour();
			}
		}
	}
  
	public void mouseDragged(WMouseEvent e) {
		if (edtAppli.getControleurDb().isAdmin()) { // on gère les jours fériés uniquement
			//mouseDraggedAdmin(e);
			return;
		}
		if (plageEnCours != null) { // on déplace une plage
			//System.out.println("on déplace " + "plageEnCours");
			// on récupère les coordonnées de la souris
			//int x = e.getX(); // on récupère les coordonnées de la souris
                //int y = e.getY();
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

			//edtAppli.getVueEDT().repaint();
			edtAppli.getVueEDT().update();
		}
	}

	public void mouseMoved(WMouseEvent e) {
		
//		System.out.println("NoiceN");
		Coordinates c = e.getWidget();
		int x = c.x;	
		int y = c.y; 
		VueEDT_wt vue = edtAppli.getVueEDT(); // la vue
		if (edtAppli.getHistoriquePlagesHoraires().isChecked()) {
			String tip = "";
			historique = vue.trouverPlageHoraire(x, y);
			//Utilitaire.showMessageDialog(this, "ca marche");
//			la plage pour laquelle on va calculer l'historique
			if (historique == null) {
//				System.out.println("Nnoice2");
				vue.setToolTip(null);
			}else if (historiqueOLD == null || !historique.equals(historiqueOLD)) { // on a changé de plage : il faut recalculer
//				System.out.println("NotNoice");
				historiqueOLD = historique;
				tip += edtAppli.getControleurDb().getHistoriquePlageHoraire(historique);
				vue.setToolTip(tip);
			} else {
//				System.out.println("Noice");
				PlageHoraire plage = vue.trouverPlageHoraire(x, y);
				vue.setToolTip(edtAppli.getControleurDb().getNbHeuresHebdo(plage));
			}
		}
	}

	/**
	 * quand l'administrateur gère les jours fériés à la souris
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
