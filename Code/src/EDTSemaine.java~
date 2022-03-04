package EDT;

import eu.webtoolkit.jwt.WDialog;

import java.util.*;
/*
 * Created on 26 juin 2004
 *
 */
/**
 * @author fredphil
 *
 */
public class EDTSemaine {
	private String semaine;
	private String promotion;
	private Vector<PlageHoraire> listePlages;
	private Vector<PlageHoraire> listeJoursFeries;
	/*
		private Vector dispenseAjouter;
		private Vector dispenseModifier;
		private Vector dispenseSupprimer;
		private VueEDT vueEmploiDuTemps;
	*/
	public EDTSemaine(EDTApplication_wt _edtAppli) {
		semaine= (String)_edtAppli.getListeSemaines().getValueText(); //getSelectedItem();
		promotion= (String)_edtAppli.getListePromotions().getValueText(); //getSelectedItem();
		/*listePlages = _edtAppli.getControleurDb().getPlagesHoraires(semaine, promotion);
		listeJoursFeries=_edtAppli.getControleurDb().getJoursFeries(semaine);*/
		
		//ControleurDB cdb=_edtAppli.getControleurDb();
		///WDialog wd= new WDialog("semaine="+semaine+",promotion="+promotion+",idAdmin="+cdb.getIdAdmin());
		//wd.show();
		
		//semaine = "31/10/2015";
		//promotion = "DUT QLIO S1";
		listePlages = _edtAppli.getControleurDb().getPlagesHoraires(semaine, promotion);	
		listeJoursFeries= _edtAppli.getControleurDb().getJoursFeries(semaine);		
		//Utilitaire.showMessageDialog("listePlages",""+listePlages.size());
		//Utilitaire.showMessageDialog("listeJoursFeries",""+listeJoursFeries.size());
	}
	public Vector<PlageHoraire> getListeJoursFeries() {
		return listeJoursFeries;
	}
	public Vector<PlageHoraire> getListePlages() {
		return listePlages;
	}
    /**
     * renvoie le nombre d'heures de cours de la semaine, hors pauses
     */
    public String getNbHeures() {
        long duree=0;
        if (listePlages!=null) {
            for(Iterator<PlageHoraire> iter=listePlages.iterator(); iter.hasNext(); ) {
                PlageHoraire p=iter.next();
                duree+=p.getDateFin().getTimeInMillis()-p.getDateDebut().getTimeInMillis();
            }
            duree=duree/1000/60; // duree en minutes
        }
        return ""+(duree/60)+"h"+(duree%60);
    }
}
