package EDT;

import eu.webtoolkit.jwt.WDialog;

import java.awt.Dimension;
import java.sql.*;
import java.util.*;

import javax.swing.*;

/*
 * Created on 26 juin 2004
 *
 */
/**
 * @author fredphil
 *  
 */
public class ControleurDB {
	private Connection db;
	private int idAdmin;

	ControleurDB(Connection _db, int _idAdmin) {
		db = _db;
		idAdmin = _idAdmin;
	}

	public String getLogin() {

		return ""+idAdmin ;

	}

	public void closeDB() {
		try {
			db.close();
		} catch (Exception e) {
			Utilitaire.showMessageDialog("ControleurDB : closeDB()","Impossible de fermer la connexion à la bdd "+e.toString());	
		}
	}

	//REQUETE pour xml
	/**
	 * @param alias
	 * @returns renvoie l'id
	 */
	private int getIdProf(String alias) {
		int id = 0;
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on récupère l'identifiant maximum
			ResultSet resultat = requeteRecherche.executeQuery("SELECT id_prof as (idprof) FROM prof where initial_prof='"+alias+"';");
			id=resultat.getInt("idprof");
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getIdProf()", e.toString());
		}
		return id;
	}

	//REQUETE pour xml todo:refaire
	/**
	 * @param alias
	 * @returns renvoie l'id
	 */
	private int getIdMatiere(String alias) {
		int id = 0;
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on récupère l'identifiant maximum
			ResultSet resultat = requeteRecherche.executeQuery("SELECT id_prof as (idprof) FROM prof where initial_prof='"+alias+"';");
			id=resultat.getInt("idprof");
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getIdProf()", e.toString());
		}
		return id;
	}


	//REQUETE pour xml
	/**
	 * @param salle
	 * @returns renvoie l'id salle
	 */
	private int getIdSalle(String salle) {
		int id = 0;
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on récupère l'identifiant maximum
			ResultSet resultat = requeteRecherche.executeQuery("SELECT id_salle as (idsalle) FROM salle where nom_salle='"+salle+"';");
			id=resultat.getInt("idsalle");
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getIdProf()", e.toString());
		}
		return id;
	}



	/**
	 * @returns renvoie le Vector contenant les promotions (et une ligne vide au début)
	 */
	public Vector<String> getPromotions(int idAdmin) {
		Vector<String> liste = new Vector<String>();
		liste.add("");
		Statement requeteRecherche;

		//WDialog wd= new WDialog("idAdmin="+idAdmin+", SELECT promo FROM promo,admin_promo WHERE admin_promo.id_promo=promo.id_promo AND admin_promo.id_admin="+idAdmin+" ORDER BY visible DESC, promo");
		//wd.show();		
		try {					
			requeteRecherche = db.createStatement();
			ResultSet resultatTemporaire = requeteRecherche.executeQuery("SELECT promo FROM promo,admin_promo WHERE admin_promo.id_promo=promo.id_promo AND admin_promo.id_admin="+idAdmin+" ORDER BY visible DESC, promo");
			// envoi de chaque promotion dans la liste déroulante
			while (resultatTemporaire.next()) {
				liste.add(resultatTemporaire.getString("promo"));
			}
			// fermeture du flux et de la requete
			resultatTemporaire.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getPromotions()", e.toString());
		}
		return liste;
	}
	
	public TreeMap<Integer,String> getPromotions2(int idAdmin) {
		TreeMap<Integer,String> liste = new TreeMap<Integer,String>();
		Statement requeteRecherche;	
		try {					
			requeteRecherche = db.createStatement();
			ResultSet resultatTemporaire = requeteRecherche.executeQuery("SELECT promo.id_promo,promo.promo FROM admin_promo inner join promo using(id_promo) WHERE id_admin="+idAdmin);
			while (resultatTemporaire.next()) {
				liste.put(resultatTemporaire.getInt("id_promo"),resultatTemporaire.getString("promo"));
			}
			resultatTemporaire.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getPromotions()", e.toString());
		}
		return liste;
	}

	/**
	 * @param uniquementVisibles
	 * @returns renvoie la liste des promotions existantes
	 */
	public TreeSet<String> getPromotions(boolean uniquementVisibles) {
		TreeSet<String> liste = new TreeSet<String>();
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			ResultSet resultatTemporaire;
			if (uniquementVisibles)
				resultatTemporaire = requeteRecherche.executeQuery("SELECT promo FROM promo where visible='t'");
			else resultatTemporaire = requeteRecherche.executeQuery("SELECT promo FROM promo");
			// envoi de chaque promotion dans la liste
			while (resultatTemporaire.next()) {
				liste.add(resultatTemporaire.getString("promo"));
			}
			// fermeture du flux et de la requete
			resultatTemporaire.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getPromotions()", e.toString());
		}
		return liste;
	}

	/**
	 * renvoie l'id du "cours" (groupe complet) de la promotion 
	 */
	int getIdGroupeCours(int id_promo) {
		int id_groupe = -1; // par défaut
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			ResultSet resultat = requeteRecherche.executeQuery("SELECT id_groupe FROM groupe,promo WHERE promo.id_promo=groupe.id_promo AND promo.id_promo="+ id_promo +" and niveau_edt=0");
			if (resultat.next()) { // on a un résultat
				id_groupe = resultat.getInt("id_groupe");
			}
			// fermeture du flux et de la requete
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getIdGroupeCours()", e.toString());
		}
		return id_groupe;
	}

	/**
	 * renvoie l'id de la promotion 
	 */
	int getIdPromotion(String promo) {
		int id_promo = -1; // par défaut
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			ResultSet resultat = requeteRecherche.executeQuery("SELECT id_promo FROM promo WHERE promo='"+ promo +"'");
			if (resultat.next()) { // on a un résultat
				id_promo = resultat.getInt("id_promo");
			}
			// fermeture du flux et de la requete
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getIdPromotion()", e.toString());
		}
		return id_promo;
	}

	/**
	 * @return la liste des jours fériés de la semaine commençant le jour indiqué, sous forme de Vector de PlageHoraire
	 */
	public Vector<PlageHoraire> getJoursFeries(String dateDebutSemaine) {
		Vector<PlageHoraire> liste = new Vector<PlageHoraire>();
		Statement requeteRecherche;
		// récupération de jour, mois, année
		GregorianCalendar date = Utilitaire.calculerDate(dateDebutSemaine);
		long date_debut = date.getTimeInMillis()/1000; // en secondes
		long date_fin = date_debut + 518400; // 6 jours de plus : le samedi suivant
		try {
			requeteRecherche = db.createStatement();
			ResultSet resultat = requeteRecherche.executeQuery("SELECT DISTINCT id_dispense, date_dbt, date_fin, memo, dispense.id_matiere FROM dispense, matiere WHERE dispense.id_matiere=matiere.id_matiere AND nom_matiere='JOURFERIE' AND date_dbt>=" + date_debut + " AND date_fin<" + date_fin);
			GregorianCalendar dateDebut, dateFin;
			PlageHoraire plage;
			while (resultat.next()) {
				SimpleTimeZone tz=new SimpleTimeZone(1*60*60*1000, "CET"); // GMT+1
				tz.setStartRule(Calendar.MARCH, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
				tz.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
				dateDebut = new GregorianCalendar(tz);
				dateFin = new GregorianCalendar(tz);
				dateDebut.setTimeInMillis(resultat.getLong("date_dbt") * 1000);
				dateFin.setTimeInMillis(resultat.getLong("date_fin") * 1000);
				plage = new PlageHoraire(resultat.getInt("id_dispense"), dateDebut, dateFin, resultat.getString("memo"), 0, 0, resultat.getInt("id_matiere"), 0, false, 0);
				liste.add(plage);
			}
			// fermeture du flux et de la requete
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getJoursFeries()", e.toString());
		}
		return liste;
	}

	public Vector<PlageHoraire> getPlagesHoraires(String dateDebutSemaine, String promo) {
		if (promo == "" || promo == null) // si on n'a pas sélectionné de promo, on n'affiche que les jours fériés
			return null;
		Vector<PlageHoraire> liste = new Vector<PlageHoraire>();
		Statement requeteRecherche;
		// récupération de jour, mois, année
		GregorianCalendar date = Utilitaire.calculerDate(dateDebutSemaine);
		long date_debut = date.getTimeInMillis()/1000; // en secondes
		long date_fin = date_debut + 518400; // 6 jours de plus : le samedi suivant
		try {
			requeteRecherche = db.createStatement();
			// on a la promo : il faut trouver les sous-groupes !
			ResultSet resultat = requeteRecherche.executeQuery("SELECT DISTINCT id_dispense, date_dbt, date_fin, memo, dispense.id_groupe, id_prof, id_matiere, id_salle, controle, id_salle2, id_prof2, id_prof3, id_prof4, acces_internet, idProfquifaitappel FROM dispense, promo, groupe as g1, groupe as g2 WHERE dispense.id_groupe=g2.id_groupe AND g1.id_promo=g2.id_promo AND g2.id_promo=promo.id_promo AND promo.promo='" + promo
					+ "' AND date_dbt>=" + date_debut + " AND date_fin<" + date_fin);
			/*Utilitaire.showMessageDialog("req","SELECT DISTINCT id_dispense, date_dbt, date_fin, memo, dispense.id_groupe, id_prof, id_matiere, id_salle, controle, id_salle2, id_prof2, id_prof3, id_prof4, acces_internet, idProfquifaitappel FROM dispense, promo, groupe as g1, groupe as g2 WHERE dispense.id_groupe=g2.id_groupe AND g1.id_promo=g2.id_promo AND g2.id_promo=promo.id_promo AND promo.promo='" + promo
					+ "' AND date_dbt>=" + date_debut + " AND date_fin<" + date_fin);*/
			// envoi de chaque promotion dans la liste déroulante
			GregorianCalendar dateDebut, dateFin;
			PlageHoraire plage;
			while (resultat.next()) {
				SimpleTimeZone tz=new SimpleTimeZone(1*60*60*1000, "CET"); // GMT+1
				tz.setStartRule(Calendar.MARCH, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
				tz.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
				dateDebut = new GregorianCalendar(tz);
				dateFin = new GregorianCalendar(tz);
				dateDebut.setTimeInMillis(resultat.getLong("date_dbt") * 1000);
				//System.out.println(resultat.getLong("date_dbt") + " " + Utilitaire.afficherDate(dateDebut, dateDebut));
				dateFin.setTimeInMillis(resultat.getLong("date_fin") * 1000);
				boolean internet=resultat.getString("acces_internet").charAt(0)=='t';
				boolean controle=resultat.getString("controle").charAt(0)=='t';
				plage = new PlageHoraire(resultat.getInt("id_dispense"), dateDebut, dateFin, resultat.getString("memo"), resultat.getInt("id_salle"), resultat.getInt("id_groupe"), resultat.getInt("id_matiere"), resultat.getInt("id_prof"), internet, controle, resultat.getInt("id_salle2"), resultat.getInt("id_prof2"), resultat.getInt("id_prof3"), resultat.getInt("id_prof4"), resultat.getInt("idProfquifaitappel"));
				liste.add(plage);
			}
			// fermeture du flux et de la requete
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getPlagesHoraires()", e.toString());
		}
		return liste;
	}

	/**
	 * supprime toutes les plages horaires de la semaine passée en paramétre pour la promo indiquée
	 */
	public void supprimerPlagesHorairesSemaine(String dateDebutSemaine, String promo) {
		Statement requeteRecherche;
		// récupération de jour, mois, année
		GregorianCalendar date = Utilitaire.calculerDate(dateDebutSemaine);
		long date_debut = date.getTimeInMillis()/1000; // en secondes
		long date_fin = date_debut + 518400; // 6 jours de plus : le samedi suivant
		try {
			requeteRecherche = db.createStatement();
			// on a la promo : il faut trouver les sous-groupes !
			requeteRecherche.executeUpdate("DELETE FROM dispense WHERE id_dispense IN (SELECT DISTINCT id_dispense FROM dispense, promo, groupe as g1, groupe as g2 WHERE dispense.id_groupe=g2.id_groupe AND g1.id_promo=g2.id_promo AND g2.id_promo=promo.id_promo AND promo.promo='" + promo
					+ "' AND date_dbt>=" + date_debut + " AND date_fin<" + date_fin+")");
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : supprimerPlagesHorairesSemaine()", e.toString());
		}
	}

	/**
	 * supprime toutes les plages horaires se trouvant sur la plage horaire passée en paramétre pour la promo indiquée
	 */
	public void supprimerPlagesHorairesJour(PlageHoraire plage, String promo) {
		Statement requeteRecherche;
		// récupération de jour, mois, année
		long date_debut = plage.getDateDebut().getTimeInMillis()/1000; // en secondes
		long date_fin = plage.getDateFin().getTimeInMillis()/1000; // en secondes
		try {
			requeteRecherche = db.createStatement();
			// on a la promo : il faut trouver les sous-groupes !
			requeteRecherche.executeUpdate("DELETE FROM dispense WHERE id_dispense IN (SELECT DISTINCT id_dispense FROM dispense, promo, groupe as g1, groupe as g2 WHERE dispense.id_groupe=g2.id_groupe AND g1.id_promo=g2.id_promo AND g2.id_promo=promo.id_promo AND promo.promo='" + promo
					+ "' AND date_dbt>=" + date_debut + " AND date_fin<=" + date_fin+")");
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : supprimerPlagesHorairesJour()", e.toString());
		}
	}

	public Salle getSalle(int idSalle) {
		Salle salle = null;
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			ResultSet resultat = requeteRecherche.executeQuery("SELECT * FROM salle where id_salle=" + idSalle);
			if (resultat.next()) { // on a un résultat
				salle = new Salle(resultat.getInt("id_salle"), resultat.getString("nom_salle"), resultat.getInt("nb_places"), resultat.getInt("nb_ordinateurs"), resultat.getString("videoprojecteur").charAt(0)=='t',resultat.getString("ip_dbt"), resultat.getString("ip_fin"), resultat.getBoolean("acces_internet"));
			}
			// fermeture du flux et de la requete
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getSalle()", e.toString());
		}
		return salle;
	}

	public Groupe getGroupe(int idGroupe) {
		Groupe groupe = null;
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			ResultSet resultat = requeteRecherche.executeQuery("SELECT id_groupe,nom_groupe,promo.promo,promo.id_promo,taille_edt,position_edt,niveau_edt,type_ppn,promo.id_departement FROM groupe,promo where promo.id_promo=groupe.id_promo AND id_groupe=" + idGroupe);
			if (resultat.next()) { // on a un résultat
				groupe = new Groupe(resultat.getInt("id_groupe"), resultat.getString("nom_groupe"), resultat.getString("promo"), resultat.getInt("taille_edt"), resultat.getInt("position_edt"), resultat.getInt("niveau_edt"), resultat.getString("type_ppn"), resultat.getInt("id_departement"),resultat.getInt("id_promo"));
			}
			// fermeture du flux et de la requete
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getGroupe()", e.toString());
		}
		return groupe;
	}

	/**
	 * @return la promo du groupe dont l'id est passé en paramètre
	 */ 
	public String getPromo(int idGroupe) {
		String promo = null;
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			ResultSet resultat = requeteRecherche.executeQuery("SELECT promo.promo FROM groupe,promo where promo.id_promo=groupe.id_promo AND id_groupe=" + idGroupe);
			if (resultat.next()) { // on a un résultat
				promo = resultat.getString("promo");
			}
			// fermeture du flux et de la requete
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getPromo()", e.toString());
		}
		return promo;
	}

	/**
	 * @return la matière dont l'identifiant est passé en paramètre ou null si elle n'existe pas
	 */
	public Matiere getMatiere(int idMatiere) {
		Matiere matiere = null;
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			ResultSet resultat = requeteRecherche.executeQuery("SELECT * FROM matiere where id_matiere=" + idMatiere);
			if (resultat.next()) { // on a un résultat
				matiere = new Matiere(resultat.getInt("id_matiere"), resultat.getString("nom_matiere"), resultat.getString("nom_compl_matiere"), resultat.getInt("r_matiere"), resultat.getInt("v_matiere"), resultat.getInt("b_matiere"), resultat.getString("pole"));
			}
			// fermeture du flux et de la requete
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getMatiere()", e.toString());
		}
		return matiere;
	}

	/**
	 * @return les informations pérennes du prof (hors décharges, titulaire, commentaire, service max)
	 */
	public Prof getProf(int idProf) {
		Prof prof = null;
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			ResultSet resultat = requeteRecherche.executeQuery("SELECT * FROM prof WHERE id_prof=" + idProf);
			if (resultat.next()) { // on a un résultat
				boolean visible=resultat.getString("visible").charAt(0)=='t';
				prof = new Prof(resultat.getInt("id_prof"), resultat.getString("initiale_prof"), resultat.getString("prenom_prof"), resultat.getString("nom_prof"), resultat.getString("login_prof"), visible);
			}
			// fermeture du flux et de la requete
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getProf()", e.toString());
		}
		return prof;
	}

	/**
	 * @return un tableau de String de 7 cases correspondant à l'année scolaire précisée
	 * service_max ; décharges pour projets, stages, apprentis, autres ; commentaire ; titulaire (t ou f)
	 */
	public String[] getDechargesProf(int idProf, int debutAnneeScolaire) {
		/*
        // date d'aujourd'hui
        SimpleTimeZone tz=new SimpleTimeZone(1*60*60*1000, "CET"); // GMT+1
        tz.setStartRule(Calendar.MARCH, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
        tz.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
        GregorianCalendar calendrier = new GregorianCalendar(tz);
        // on cherche le début de l'année scolaire
        int debutAnneeScolaire = calendrier.get(Calendar.YEAR); // année en cours
        int mois = calendrier.get(Calendar.MONTH); // mois en cours
        if (mois<Calendar.JULY) // on est aujourd'hui entre janvier et juin
            debutAnneeScolaire--; // le début de l'année scolaire en cours est l'an dernier
		 */
		//        String semaineEnCours=(String)edtAppli.getListeSemaines().getSelectedItem();
		//        int debutAnneeScolaire=Integer.parseInt(semaineEnCours.substring(semaineEnCours.length()-4,4));
		Statement requeteRecherche;
		String[] result=new String[7];
		try {
			requeteRecherche = db.createStatement();
			ResultSet resultat = requeteRecherche.executeQuery("SELECT * FROM prof,decharge WHERE prof.id_prof=decharge.id_prof AND prof.id_prof=" + idProf + " AND debutAnneeScolaire="+debutAnneeScolaire);
			if (resultat.next()) { // on a un résultat
				result[0]=""+resultat.getInt("service_max");
				result[1]=""+resultat.getInt("decharge_projets");
				result[2]=""+resultat.getInt("decharge_stages");
				result[3]=""+resultat.getInt("decharge_apprentis");
				result[4]=""+resultat.getInt("decharge");
				result[5]=resultat.getString("commentaire");
				result[6]=resultat.getString("titulaire");
			} else // on remplit un tableau par défaut
				result=new String[] {"0","0","0","0","0","","f"};
			// fermeture du flux et de la requete
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getDechargesProf()", e.toString());
		}
		return result;
	}

	public Etudiant getEtudiant(int idEtudiant) {
		Etudiant etudiant = null;
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			ResultSet resultat = requeteRecherche.executeQuery("SELECT * FROM etudiant where id_etudiant=" + idEtudiant);
			if (resultat.next()) { // on a un résultat
				etudiant = new Etudiant(resultat.getInt("id_etudiant"), resultat.getString("prenom_etudiant"), resultat.getString("nom_etudiant"), resultat.getString("login_etudiant"));
			}
			// fermeture du flux et de la requete
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getEtudiant()", e.toString());
		}
		return etudiant;
	}

	public int getEtudiantLogin(String loginE) {
		Statement requeteRecherche;
		int id=0;
		try {
			requeteRecherche = db.createStatement();
			ResultSet resultat = requeteRecherche.executeQuery("SELECT id_etudiant FROM etudiant where login_etudiant='"+loginE+"'");
			if (resultat.next()) { // on a un résultat
				id = resultat.getInt("id_etudiant");
			}
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getEtudiant()", e.toString());
		}
		return id;
	}

	public int getProfLogin(String loginP) {
		Statement requeteRecherche;
		int id=0;
		try {
			requeteRecherche = db.createStatement();
			ResultSet resultat = requeteRecherche.executeQuery("SELECT id_prof FROM prof where login_prof='"+loginP+"'");
			if (resultat.next()) { // on a un résultat
				id = resultat.getInt("id_prof");
			}
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getProf()", e.toString());
		}
		return id;
	}

	public int getNbColonnesVirtuelles(String promo) {
		int nbColVirt = 1; // par défaut
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			ResultSet resultat = requeteRecherche.executeQuery("SELECT max(taille_edt) as maxi FROM groupe,promo where groupe.id_promo=promo.id_promo AND promo.promo='" + promo + "'");
			if (resultat.next()) { // on a un résultat
				nbColVirt = resultat.getInt("maxi");
			}
			// fermeture du flux et de la requete
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getNbColonnesVirtuelles()", e.toString());
		}
		return nbColVirt;
	}

	/**
	 * @return renvoie le nombre de TP dans la promo
	 */
	public int getNbTP(String promo) {
		int nbGroupes = 1; // par défaut
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			ResultSet resultat = requeteRecherche.executeQuery("SELECT count(id_groupe) as nombre FROM groupe,promo WHERE groupe.id_promo=promo.id_promo AND promo.promo='" + promo + "' AND niveau_edt=2");
			if (resultat.next()) { // on a un résultat
				nbGroupes = resultat.getInt("nombre");
			}
			// fermeture du flux et de la requete
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getNbTP()", e.toString());
		}
		return nbGroupes;
	}

	/**
	 * @return renvoie la liste des groupes de la promo spécifiée
	 */
	public TreeSet<Groupe> getListeGroupes(String promo) {
		TreeSet<Groupe> listeGroupes=new TreeSet<Groupe>();
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			ResultSet resultat = requeteRecherche.executeQuery("SELECT id_groupe,nom_groupe,taille_edt,position_edt,niveau_edt,type_ppn,promo.id_departement,promo.promo,promo.id_promo FROM groupe,promo WHERE groupe.id_promo=promo.id_promo AND promo.promo='" + promo + "'");
			while (resultat.next()) {
				Groupe groupe = new Groupe(resultat.getInt("id_groupe"), resultat.getString("nom_groupe"), promo, resultat.getInt("taille_edt"), resultat.getInt("position_edt"), resultat.getInt("niveau_edt"), resultat.getString("type_ppn"), resultat.getInt("id_departement"), resultat.getInt("id_promo"));
				listeGroupes.add(groupe);
			}
			// fermeture du flux et de la requete
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getListeGroupes()", e.toString());
		}
		return listeGroupes;
	}


	public ArrayList<Groupe> getListeGroupes2(String promo) {
		ArrayList<Groupe> listeGroupes=new ArrayList<Groupe>();
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			ResultSet resultat = requeteRecherche.executeQuery("SELECT id_groupe,nom_groupe,taille_edt,position_edt,niveau_edt,type_ppn,promo.id_departement,promo.promo,promo.id_promo FROM groupe,promo WHERE groupe.id_promo=promo.id_promo AND promo.promo='" + promo + "'");
			while (resultat.next()) {
				Groupe groupe = new Groupe(resultat.getInt("id_groupe"), resultat.getString("nom_groupe"), promo, resultat.getInt("taille_edt"), resultat.getInt("position_edt"), resultat.getInt("niveau_edt"), resultat.getString("type_ppn"), resultat.getInt("id_departement"), resultat.getInt("id_promo"));
				listeGroupes.add(groupe);
			}
			// fermeture du flux et de la requete
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getListeGroupes()", e.toString());
		}
		return listeGroupes;
	}

	public HashMap<String,Groupe> getListeGroupes3(String promo) {
		HashMap<String,Groupe> listeGroupes=new  HashMap<String,Groupe>();
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			ResultSet resultat = requeteRecherche.executeQuery("SELECT id_groupe,nom_groupe,taille_edt,position_edt,niveau_edt,type_ppn,promo.id_departement,promo.promo,promo.id_promo FROM groupe,promo WHERE groupe.id_promo=promo.id_promo AND promo.promo='" + promo + "'");
			while (resultat.next()) {
				Groupe groupe = new Groupe(resultat.getInt("id_groupe"), resultat.getString("nom_groupe"), promo, resultat.getInt("taille_edt"), resultat.getInt("position_edt"), resultat.getInt("niveau_edt"), resultat.getString("type_ppn"), resultat.getInt("id_departement"), resultat.getInt("id_promo"));
				listeGroupes.put(groupe.getNom(),groupe);
			}
			// fermeture du flux et de la requete
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getListeGroupes()", e.toString());
		}
		return listeGroupes;
	}


	/**
	 * @return renvoie la liste des groupes auxquels appartient un étudiant
	 */
	public TreeSet<Groupe> getListeGroupesEtudiant(int id_etudiant) {
		TreeSet<Groupe> listeGroupes=new TreeSet<Groupe>();
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			ResultSet resultat = requeteRecherche.executeQuery("SELECT groupe.id_groupe,nom_groupe,taille_edt,position_edt,niveau_edt,type_ppn,promo.id_departement,promo.promo,promo.id_promo FROM promo,groupe,composition_groupe WHERE promo.id_promo=groupe.id_promo AND groupe.id_groupe=composition_groupe.id_groupe AND id_etudiant=" + id_etudiant);
			while (resultat.next()) {
				Groupe groupe = new Groupe(resultat.getInt("id_groupe"), resultat.getString("nom_groupe"), resultat.getString("promo"), resultat.getInt("taille_edt"), resultat.getInt("position_edt"), resultat.getInt("niveau_edt"), resultat.getString("type_ppn"), resultat.getInt("id_departement"), resultat.getInt("id_promo"));
				listeGroupes.add(groupe);
			}
			// fermeture du flux et de la requete
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getListeGroupesEtudiant()", e.toString());
		}
		return listeGroupes;
	}

	/**
	 * @return renvoie la liste des promotions que l'administrateur peut gérer
	 */
	public TreeSet<String> getListePromosAdmin(String admin) {
		TreeSet<String> listeGroupes=new TreeSet<String>();
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			ResultSet resultat = requeteRecherche.executeQuery("SELECT promo FROM promo,admin_promo,admin WHERE promo.id_promo=admin_promo.id_promo AND admin.id_admin=admin_promo.id_admin AND login_admin='" + admin + "'");
			while (resultat.next()) {
				//boolean isVisible=resultat.getString("visible").charAt(0)=='t';
				//Groupe groupe = new Groupe(resultat.getInt("id_groupe"), resultat.getString("nom_groupe"), resultat.getString("promo"), resultat.getInt("taille_edt"), resultat.getInt("position_edt"), resultat.getInt("niveau_edt"), resultat.getString("type_ppn"), resultat.getInt("id_departement"), isVisible);
				//listeGroupes.add(groupe);
				listeGroupes.add(resultat.getString("promo"));
			}
			// fermeture du flux et de la requete
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getListePromosAdmin()", e.toString());
		}
		return listeGroupes;
	}

	/**
	 * cherche la plage horaire ou le jour férié pour la date/heure et le groupe indiqués
	 * (si promo=="" cherche uniquement les jours fériés)
	 */
	public PlageHoraire getPlageHoraire(GregorianCalendar cal, String promo, int position_edt) {
		//System.out.println("dans getPlageHoraire : position=" + position_edt + " date=" + Utilitaire.afficherDate(cal,cal));
		PlageHoraire plage = null;
		Statement requeteRecherche;
		long date_secondes = cal.getTimeInMillis() / 1000;
		try {
			requeteRecherche = db.createStatement();
			ResultSet resultat;
			// on cherche un jour férié
			resultat= requeteRecherche.executeQuery("SELECT id_dispense, date_dbt, date_fin, memo, dispense.id_matiere FROM dispense, matiere WHERE dispense.id_matiere=matiere.id_matiere AND nom_matiere='JOURFERIE' AND date_dbt<=" + date_secondes + " AND date_fin>" + date_secondes);
			if (resultat.next()) { // on a un résultat
				SimpleTimeZone tz=new SimpleTimeZone(1*60*60*1000, "CET"); // GMT+1
				tz.setStartRule(Calendar.MARCH, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
				tz.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
				GregorianCalendar dbt = new GregorianCalendar(tz), fin = new GregorianCalendar(tz);
				dbt.setTimeInMillis(resultat.getLong("date_dbt") * 1000);
				fin.setTimeInMillis(resultat.getLong("date_fin") * 1000);
				plage = new PlageHoraire(resultat.getInt("id_dispense"), dbt, fin, resultat.getString("memo"), 0, 0, resultat.getInt("id_matiere"), 0, false, 0);
			}
			else if (promo.length()>0) { // on a sélectionné une promo 
				resultat= requeteRecherche.executeQuery("SELECT * FROM dispense,groupe,promo WHERE dispense.id_groupe=groupe.id_groupe AND groupe.id_promo=promo.id_promo AND promo.promo='" + promo + "' AND date_dbt<=" + date_secondes + " AND date_fin>" + date_secondes + " AND position_edt<=" + position_edt
						+ " AND (position_edt+taille_edt)>" + position_edt);
				if (resultat.next()) { // on a un résultat
					SimpleTimeZone tz=new SimpleTimeZone(1*60*60*1000, "CET"); // GMT+1
					tz.setStartRule(Calendar.MARCH, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
					tz.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
					GregorianCalendar dbt = new GregorianCalendar(tz), fin = new GregorianCalendar(tz);
					dbt.setTimeInMillis(resultat.getLong("date_dbt") * 1000);
					fin.setTimeInMillis(resultat.getLong("date_fin") * 1000);
					boolean internet=resultat.getString("acces_internet").charAt(0)=='t';
					boolean controle=resultat.getString("controle").charAt(0)=='t';
					plage = new PlageHoraire(resultat.getInt("id_dispense"), dbt, fin, resultat.getString("memo"), resultat.getInt("id_salle"), resultat.getInt("id_groupe"), resultat.getInt("id_matiere"), resultat.getInt("id_prof"), internet, controle, resultat.getInt("id_salle2"), resultat.getInt("id_prof2"), resultat.getInt("id_prof3"), resultat.getInt("id_prof4"), resultat.getInt("idProfquifaitappel"));
					//System.out.println(plage);
				}
			}
			// fermeture du flux et de la requete
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getPlageHoraire()", e.toString());
		}
		return plage;
	}

	/**
	 * @param db
	 * @return la liste des salles existantes
	 */
	public TreeSet<Salle> getSalles() {
		TreeSet<Salle> listeSalles = new TreeSet<Salle>();
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on récupère les salles
			ResultSet resultat = requeteRecherche.executeQuery("SELECT * FROM salle WHERE id_salle>0");
			while (resultat.next()) {
				Salle salle = new Salle(resultat.getInt("id_salle"), resultat.getString("nom_salle"), resultat.getInt("nb_places"), resultat.getInt("nb_ordinateurs"), resultat.getString("videoprojecteur").charAt(0)=='t',resultat.getString("ip_dbt"), resultat.getString("ip_fin"), resultat.getBoolean("acces_internet"));
				listeSalles.add(salle);
			}
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getSalles()", e.toString());
		}
		return listeSalles;
	}

	/**
	 * @return la liste des groupes existants
	 */
	public TreeSet<Groupe> getGroupes() {
		TreeSet<Groupe> listeGroupes = new TreeSet<Groupe>();
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on récupère les groupes
			ResultSet resultat = requeteRecherche.executeQuery("SELECT id_groupe,nom_groupe,promo.promo,promo.id_promo,taille_edt,position_edt,niveau_edt,type_ppn,promo.id_departement FROM groupe,promo WHERE promo.id_promo=groupe.id_promo AND id_groupe>0 ORDER BY promo");
			while (resultat.next()) {
				Groupe groupe = new Groupe(resultat.getInt("id_groupe"), resultat.getString("nom_groupe"), resultat.getString("promo"), resultat.getInt("taille_edt"), resultat.getInt("position_edt"), resultat.getInt("niveau_edt"), resultat.getString("type_ppn"), resultat.getInt("id_departement"), resultat.getInt("id_promo"));
				listeGroupes.add(groupe);
			}
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getGroupes()", e.toString());
		}
		return listeGroupes;
	}

	public TreeSet<Groupe> getGroupes2(String promo) {
		TreeSet<Groupe> listeGroupes = new TreeSet<Groupe>();
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			ResultSet resultat = requeteRecherche.executeQuery("SELECT * FROM groupe inner join promo using(id_promo) where promo='"+promo+"'");
			while (resultat.next()) {
				Groupe groupe = new Groupe(resultat.getInt("id_groupe"), resultat.getString("nom_groupe"), resultat.getString("promo"), resultat.getInt("taille_edt"), resultat.getInt("position_edt"), resultat.getInt("niveau_edt"), resultat.getString("type_ppn"), resultat.getInt("id_departement"), resultat.getInt("id_promo"));
				listeGroupes.add(groupe);
			}
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getGroupes()", e.toString());
		}
		return listeGroupes;
	}

	public int getPositionEdt(String promo,String groupe) {
		Statement requeteRecherche;
		int id_promo=0;
		int position=0;
		try {
			requeteRecherche = db.createStatement();
			// on récupère les administrateurs
			ResultSet resultat = requeteRecherche.executeQuery("SELECT id_promo FROM promo WHERE promo='"+promo+"';");
			while (resultat.next()) {
				id_promo=resultat.getInt("id_promo");
			}
			resultat = requeteRecherche.executeQuery("SELECT position_edt FROM groupe WHERE id_promo="+id_promo+" and nom_groupe='"+groupe+"';");
			while (resultat.next()) {
				position=resultat.getInt("position_edt");
			}	    
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getListeAdministrateurs()", e.toString());
		}
		return position;
	}

	public int getIdGroupeEdt(String promo,String groupe) {
		Statement requeteRecherche;
		int id_promo=0;
		int id_groupe=0;
		try {
			requeteRecherche = db.createStatement();
			// on récupère les administrateurs
			ResultSet resultat = requeteRecherche.executeQuery("SELECT id_promo FROM promo WHERE promo='"+promo+"';");
			while (resultat.next()) {
				id_promo=resultat.getInt("id_promo");
			}
			resultat = requeteRecherche.executeQuery("SELECT id_groupe FROM groupe WHERE id_promo="+id_promo+" and nom_groupe='"+groupe+"';");
			while (resultat.next()) {
				id_groupe=resultat.getInt("id_groupe");
			}	    
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getListeAdministrateurs()", e.toString());
		}
		return id_groupe;
	}


	public int getTailleGroupeEdt(String promo,String groupe) {
		Statement requeteRecherche;
		int id_promo=0;
		int taille_edt=0;
		try {
			requeteRecherche = db.createStatement();
			// on récupère les administrateurs
			ResultSet resultat = requeteRecherche.executeQuery("SELECT id_promo FROM promo WHERE promo='"+promo+"';");
			while (resultat.next()) {
				id_promo=resultat.getInt("id_promo");
			}
			resultat = requeteRecherche.executeQuery("SELECT taille_edt FROM groupe WHERE id_promo="+id_promo+" and nom_groupe='"+groupe+"';");
			while (resultat.next()) {
				taille_edt=resultat.getInt("taille_edt");
			}	    
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getListeAdministrateurs()", e.toString());
		}
		return taille_edt;
	}

	public ArrayList<Integer> getCouleursMatieres(String matiere) {
		Statement requeteRecherche;
		int r=0;
		int v=0;
		int b=0;
		try {
			requeteRecherche = db.createStatement();
			// on récupère les administrateurs
			ResultSet resultat = requeteRecherche.executeQuery("SELECT * FROM matiere WHERE nom_matiere='"+matiere+"';");
			while (resultat.next()) {
				r=resultat.getInt("r_matiere");
				v=resultat.getInt("v_matiere");
				b=resultat.getInt("b_matiere");

			}   
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getListeAdministrateurs()", e.toString());
		}
		ArrayList<Integer> ai = new ArrayList<Integer>();
		ai.add(r);
		ai.add(v);
		ai.add(b);
		return ai;
	}


	/**
	 * @return la liste des administrateurs existants
	 */
	public TreeSet<String> getListeAdministrateurs() {
		TreeSet<String> listeAdmins = new TreeSet<String>();
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on récupère les administrateurs
			ResultSet resultat = requeteRecherche.executeQuery("SELECT login_admin FROM admin WHERE id_admin>0");
			while (resultat.next()) {
				listeAdmins.add(resultat.getString("login_admin"));
			}
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getListeAdministrateurs()", e.toString());
		}
		return listeAdmins;
	}

	/**
	 * @return la liste des administrateurs existants mais pas chefs de départements
	 */
	public TreeSet<String> getListeAdministrateursNonChefs() {
		TreeSet<String> listeAdmins = new TreeSet<String>();
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on récupère les administrateurs
			ResultSet resultat = requeteRecherche.executeQuery("SELECT login_admin FROM admin WHERE id_admin>0 AND id_admin NOT IN (SELECT id_chef_departement FROM departement)");
			while (resultat.next()) {
				listeAdmins.add(resultat.getString("login_admin"));
			}
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getListeAdministrateursNonChefs()", e.toString());
		}
		return listeAdmins;
	}

	/**
	 * @return la liste des départements existants
	 */
	public TreeSet<String> getListeDepartements() {
		TreeSet<String> listeDpts = new TreeSet<String>();
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on récupère les départements
			ResultSet resultat = requeteRecherche.executeQuery("SELECT nom_departement FROM departement");
			while (resultat.next()) {
				listeDpts.add(resultat.getString("nom_departement"));
			}
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getListeDepartements()", e.toString());
		}
		return listeDpts;
	}

	/**
	 * @return la liste des matières existantes
	 */
	public TreeSet<Matiere> getMatieres() {
		TreeSet<Matiere> listeMatieres = new TreeSet<Matiere>();
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on récupère les matières
			ResultSet resultat = requeteRecherche.executeQuery("SELECT * FROM matiere WHERE id_matiere>0 AND nom_matiere!='JOURFERIE'");
			while (resultat.next()) {
				Matiere matiere = new Matiere(resultat.getInt("id_matiere"), resultat.getString("nom_matiere"), resultat.getString("nom_compl_matiere"), resultat.getInt("r_matiere"), resultat.getInt("v_matiere"), resultat.getInt("b_matiere"), resultat.getString("pole"));
				listeMatieres.add(matiere);
			}
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getMatieres()", e.toString());
		}
		return listeMatieres;
	}

	/**
	 * @return la liste des jours fériés existants sous forme d'un TreeSet de Matiere
	 */
	public TreeSet<Matiere> getJoursFeries() {
		TreeSet<Matiere> listeJF = new TreeSet<Matiere>();
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on récupère les jours fériés
			ResultSet resultat = requeteRecherche.executeQuery("SELECT * FROM matiere WHERE nom_matiere='JOURFERIE'");
			while (resultat.next()) {
				Matiere matiere = new Matiere(resultat.getInt("id_matiere"), resultat.getString("nom_matiere"), resultat.getString("nom_compl_matiere"), resultat.getInt("r_matiere"), resultat.getInt("v_matiere"), resultat.getInt("b_matiere"), resultat.getString("pole"));
				listeJF.add(matiere);
			}
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getJoursFeries()", e.toString());
		}
		return listeJF;
	}

	/**
	 * @return la liste des profs existants (visibles et non visibles)
	 */
	public TreeSet<Prof> getProfs() {
		TreeSet<Prof> listeProfs = new TreeSet<Prof>();
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on récupère les profs
			ResultSet resultat = requeteRecherche.executeQuery("SELECT * FROM prof WHERE id_prof>0");
			while (resultat.next()) {
				boolean visible=resultat.getString("visible").charAt(0)=='t';
				Prof prof = new Prof(resultat.getInt("id_prof"), resultat.getString("initiale_prof"), resultat.getString("prenom_prof"), resultat.getString("nom_prof"), resultat.getString("login_prof"), visible);
				listeProfs.add(prof);
			}
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getProfs()", e.toString());
		}
		return listeProfs;
	}

	public ArrayList<String> getProfs2() {
		ArrayList<String> listeProfs = new ArrayList<String>();
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on récupère les profs
			ResultSet resultat = requeteRecherche.executeQuery("SELECT * FROM prof WHERE id_prof>0");
			while (resultat.next()) {
				boolean visible=resultat.getString("visible").charAt(0)=='t';
				Prof prof = new Prof(resultat.getInt("id_prof"), resultat.getString("initiale_prof"), resultat.getString("prenom_prof"), resultat.getString("nom_prof"), resultat.getString("login_prof"), visible);
				listeProfs.add(prof.getLogin().toLowerCase());
			}
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getProfs()", e.toString());
		}
		return listeProfs;
	}

	/**
	 * @return la liste des initiales des profs existants (visibles et non visibles)
	 */
	public TreeSet<String> getListeInitialesProfs() {
		TreeSet<String> liste = new TreeSet<String>();
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on récupère les initiales des profs
			ResultSet resultat = requeteRecherche.executeQuery("SELECT initiale_prof FROM prof WHERE id_prof>0");
			while (resultat.next()) {
				liste.add(resultat.getString("initiale_prof"));
			}
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getListeInitialesProfs()", e.toString());
		}
		return liste;
	}    

	/**
	 * @return la liste des initiales des profs existants (visibles et non visibles)
	 */
	public TreeSet<String> getListeLoginsProfs() {
		TreeSet<String> liste = new TreeSet<String>();
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on récupère les initiales des profs
			ResultSet resultat = requeteRecherche.executeQuery("SELECT login_prof FROM prof WHERE id_prof>0");
			while (resultat.next()) {
				liste.add(resultat.getString("login_prof"));
			}
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getListeLoginsProfs()", e.toString());
		}
		return liste;
	}

	/**
	 * @return la liste des poles existants pour cette promo
	 */
	public TreeSet<String> getPoles(int id_promo) {
		TreeSet<String> listePoles = new TreeSet<String>();
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on récupère les matières
			ResultSet resultat = requeteRecherche.executeQuery("SELECT pole FROM matiere,promo_matiere WHERE matiere.id_matiere>0 AND matiere.id_matiere=promo_matiere.id_matiere AND promo_matiere.id_promotion=" + id_promo);
			while (resultat.next()) {
				listePoles.add(resultat.getString("pole"));
			}
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getPoles()", e.toString());
		}
		return listePoles;
	}

	/**
	 * @param loginAdmin
	 * @return la liste des groupes existants pour l'administrateur spécifié
	 */
	public ArrayList<Groupe> getGroupes(int idAdmin) {
		ArrayList<Groupe> listeGroupes = new ArrayList<Groupe>();
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on récupère les groupes
			ResultSet resultat = requeteRecherche.executeQuery("SELECT id_groupe,nom_groupe,taille_edt,position_edt,niveau_edt,type_ppn,promo.id_departement,promo.promo,promo.id_promo FROM groupe,promo,admin_promo WHERE groupe.id_groupe>0 AND promo.id_promo=groupe.id_promo AND groupe.id_promo=admin_promo.id_promo AND admin_promo.id_admin=" + idAdmin + " ORDER BY promo,nom_groupe");
			while (resultat.next()) {
				Groupe groupe = new Groupe(resultat.getInt("id_groupe"), resultat.getString("nom_groupe"), resultat.getString("promo"), resultat.getInt("taille_edt"), resultat.getInt("position_edt"), resultat.getInt("niveau_edt"), resultat.getString("type_ppn"), resultat.getInt("id_departement"), resultat.getInt("id_promo"));
				listeGroupes.add(groupe);
			}
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getGroupes()", e.toString());
		}
		return listeGroupes;
	}


	public ArrayList<Groupe> getGroupesPromos(int idP) {
		ArrayList<Groupe> listeGroupes = new ArrayList<Groupe>();
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on récupère les groupes
			ResultSet resultat = requeteRecherche.executeQuery("SELECT id_groupe,nom_groupe,taille_edt,position_edt,niveau_edt,type_ppn,promo.id_departement,promo.promo,promo.id_promo FROM groupe,promo WHERE groupe.id_groupe>0 AND promo.id_promo=groupe.id_promo AND groupe.id_promo="+idP+" ORDER BY promo,nom_groupe");
			while (resultat.next()) {
				Groupe groupe = new Groupe(resultat.getInt("id_groupe"), resultat.getString("nom_groupe"), resultat.getString("promo"), resultat.getInt("taille_edt"), resultat.getInt("position_edt"), resultat.getInt("niveau_edt"), resultat.getString("type_ppn"), resultat.getInt("id_departement"), resultat.getInt("id_promo"));
				listeGroupes.add(groupe);
			}
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getGroupes()", e.toString());
		}
		return listeGroupes;
	}
	/**
	 * @return la liste des matières existantes pour la promo spécifiée
	 */
	public TreeSet<Matiere> getMatieresPromo(int id_promo) {
		TreeSet<Matiere> listeMatieres = new TreeSet<Matiere>();
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on récupère les matières
			ResultSet resultat = requeteRecherche.executeQuery("SELECT matiere.* FROM matiere,promo_matiere WHERE matiere.id_matiere>0 AND matiere.id_matiere=promo_matiere.id_matiere AND promo_matiere.id_promotion=" + id_promo);
			while (resultat.next()) {
				Matiere matiere = new Matiere(resultat.getInt("id_matiere"), resultat.getString("nom_matiere"), resultat.getString("nom_compl_matiere"), resultat.getInt("r_matiere"), resultat.getInt("v_matiere"), resultat.getInt("b_matiere"), resultat.getString("pole"));
				listeMatieres.add(matiere);
			}
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getMatieresPromo()", e.toString());
		}
		return listeMatieres;
	}
	
	public TreeSet<String> getMatieresPromo2(int id_promo) {
		TreeSet<String> listeMatieres = new TreeSet<String>();
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on récupère les matières
			ResultSet resultat = requeteRecherche.executeQuery("SELECT matiere.nom_matiere,matiere.nom_compl_matiere FROM matiere,promo_matiere WHERE matiere.id_matiere>0 AND matiere.id_matiere=promo_matiere.id_matiere AND promo_matiere.id_promotion=" + id_promo);
			while (resultat.next()) {
				listeMatieres.add(resultat.getString("nom_matiere")+" "+resultat.getString("nom_compl_matiere"));
			}
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getMatieresPromo()", e.toString());
		}
		return listeMatieres;
	}
	
	public TreeMap<String,Matiere> getMatieresPromo3(int id_promo) {
		TreeMap<String,Matiere> listeMatieres = new TreeMap<String,Matiere>();
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on récupère les matières
			ResultSet resultat = requeteRecherche.executeQuery("SELECT matiere.* FROM matiere,promo_matiere WHERE matiere.id_matiere>0 AND matiere.id_matiere=promo_matiere.id_matiere AND promo_matiere.id_promotion=" + id_promo);
			while (resultat.next()) {
				Matiere matiere = new Matiere(resultat.getInt("id_matiere"), resultat.getString("nom_matiere"), resultat.getString("nom_compl_matiere"), resultat.getInt("r_matiere"), resultat.getInt("v_matiere"), resultat.getInt("b_matiere"), resultat.getString("pole"));
				listeMatieres.put(matiere.getNom()+" "+matiere.getNomCompl(),matiere);			}
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getMatieresPromo()", e.toString());
		}
		return listeMatieres;
	}
	

	/**
	 * @return la liste des matières existantes pour l'admin spécifié
	 */
	public TreeSet<Matiere> getMatieresAdmin(int id_admin) {
		TreeSet<Matiere> listeMatieres = new TreeSet<Matiere>();
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on récupère les matières
			ResultSet resultat = requeteRecherche.executeQuery("SELECT matiere.* FROM matiere,promo_matiere,admin_promo WHERE matiere.id_matiere>0 AND matiere.id_matiere=promo_matiere.id_matiere AND promo_matiere.id_promotion=admin_promo.id_promo AND nom_matiere!='JOURFERIE' AND admin_promo.id_admin=" + id_admin);
			while (resultat.next()) {
				Matiere matiere = new Matiere(resultat.getInt("id_matiere"), resultat.getString("nom_matiere"), resultat.getString("nom_compl_matiere"), resultat.getInt("r_matiere"), resultat.getInt("v_matiere"), resultat.getInt("b_matiere"), resultat.getString("pole"));
				listeMatieres.add(matiere);
			}
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getMatieresAdmin()", e.toString());
		}
		return listeMatieres;
	}
	
	public TreeMap<String,Matiere>getMatieresAdmin2(int id_admin) {
		TreeMap<String,Matiere> listeMatieres = new TreeMap<String,Matiere>();
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on récupère les matières
			ResultSet resultat = requeteRecherche.executeQuery("SELECT matiere.* FROM matiere,promo_matiere,admin_promo WHERE matiere.id_matiere>0 AND matiere.id_matiere=promo_matiere.id_matiere AND promo_matiere.id_promotion=admin_promo.id_promo AND nom_matiere!='JOURFERIE' AND admin_promo.id_admin=" + id_admin);
			while (resultat.next()) {
				Matiere matiere = new Matiere(resultat.getInt("id_matiere"), resultat.getString("nom_matiere"), resultat.getString("nom_compl_matiere"), resultat.getInt("r_matiere"), resultat.getInt("v_matiere"), resultat.getInt("b_matiere"), resultat.getString("pole"));
				listeMatieres.put(matiere.getNom()+" "+matiere.getNomCompl(),matiere);
			}
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getMatieresAdmin()", e.toString());
		}
		return listeMatieres;
	}

	/* public TreeMap<String,Matiere> getMatieresAdminMap(int id_admin) {
        TreeMap<String,Matiere> listeMatieres = new  TreeMap<String,Matiere>();
        Statement requeteRecherche;
        ResultSet resultat;
        try {
            requeteRecherche = db.createStatement();
            // on récupère les matières
            resultat = requeteRecherche.executeQuery("SELECT matiere.* FROM matiere,promo_matiere,admin_promo WHERE matiere.id_matiere>0 AND matiere.id_matiere=promo_matiere.id_matiere AND promo_matiere.id_promotion=admin_promo.id_promo AND nom_matiere!='JOURFERIE' AND admin_promo.id_admin=" + id_admin);
            while (resultat.next()) {
                Matiere matiere = new Matiere(resultat.getInt("id_matiere"), resultat.getString("nom_matiere"), resultat.getString("nom_compl_matiere"), resultat.getInt("r_matiere"), resultat.getInt("v_matiere"), resultat.getInt("b_matiere"), resultat.getString("pole"));
                listeMatieres.put(matiere.getNom(),matiere);
            }
            resultat.close();
            requeteRecherche.close();
        } catch (SQLException e) {
            e.printStackTrace();
            Utilitaire.showMessageDialog("ControleurDB : getMatieresAdmin()", e.toString());
        }

        return listeMatieres;
    }*/


	/**
	 * @return la liste des matières associées à aucune promo
	 */
	public TreeSet<Matiere> getMatieresOrphelines() {
		TreeSet<Matiere> listeMatieres = new TreeSet<Matiere>();
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on récupère les matières
			ResultSet resultat = requeteRecherche.executeQuery("SELECT matiere.* FROM matiere WHERE id_matiere>0 AND nom_matiere!='JOURFERIE' AND id_matiere NOT IN (SELECT id_matiere FROM promo_matiere)");
			while (resultat.next()) {
				Matiere matiere = new Matiere(resultat.getInt("id_matiere"), resultat.getString("nom_matiere"), resultat.getString("nom_compl_matiere"), resultat.getInt("r_matiere"), resultat.getInt("v_matiere"), resultat.getInt("b_matiere"), resultat.getString("pole"));
				listeMatieres.add(matiere);
			}
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getMatieresOrphelines()", e.toString());
		}
		return listeMatieres;
	}

	/**
	 * @return la liste des profs visibles pour la promo spécifiée
	 */
	public TreeSet<Prof> getProfsPromo(int id_promo) {
		TreeSet<Prof> listeProfs = new TreeSet<Prof>();
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on récupère les profs
			ResultSet resultat = requeteRecherche.executeQuery("SELECT prof.* FROM prof,promo_prof WHERE prof.visible='t' AND prof.id_prof=promo_prof.id_prof AND promo_prof.id_promotion=" + id_promo);
			while (resultat.next()) {
				Prof prof = new Prof(resultat.getInt("id_prof"), resultat.getString("initiale_prof"), resultat.getString("prenom_prof"), resultat.getString("nom_prof"), resultat.getString("login_prof"), true);
				listeProfs.add(prof);
			}
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getProfsPromo()", e.toString());
		}
		return listeProfs;
	}

	/**
	 * @param visible : si vrai, ne liste que les profs "visibles"
	 * @return la liste des profs pour l'admin spécifié, et pour l'année scolaire en cours d'affichage
	 */
	public TreeSet<Prof> getProfsAdmin(int id_admin, int debutAnneeScolaire, boolean visible) {
		TreeSet<Prof> listeProfs = new TreeSet<Prof>();
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on récupère les profs
			String $query_profs="SELECT prof.* FROM prof,promo_prof,admin_promo WHERE ";
			if (visible) // on ne veut que les profs visibles
				$query_profs+="prof.visible='t'";
			else $query_profs+="prof.id_prof>0";
			$query_profs+=" AND prof.id_prof=promo_prof.id_prof AND promo_prof.id_promotion=admin_promo.id_promo AND admin_promo.id_admin=" + id_admin;
			ResultSet resultat = requeteRecherche.executeQuery($query_profs);
			while (resultat.next()) {
				boolean isVisible=resultat.getString("visible").charAt(0)=='t';
				Prof prof = new Prof(resultat.getInt("id_prof"), resultat.getString("initiale_prof"), resultat.getString("prenom_prof"), resultat.getString("nom_prof"), resultat.getString("login_prof"), isVisible);
				prof.setDecharges(getDechargesProf(resultat.getInt("id_prof"), debutAnneeScolaire));
				listeProfs.add(prof);
			}
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getProfsAdmin()", e.toString());
		}
		return listeProfs;
	}
	
	public TreeMap<String,Prof> getProfsAdmin2(int id_admin, int debutAnneeScolaire, boolean visible) {
		TreeMap<String,Prof> listeProfs = new TreeMap<String,Prof>();
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on récupère les profs
			String $query_profs="SELECT prof.* FROM prof,promo_prof,admin_promo WHERE ";
			if (visible) // on ne veut que les profs visibles
				$query_profs+="prof.visible='t'";
			else $query_profs+="prof.id_prof>0";
			$query_profs+=" AND prof.id_prof=promo_prof.id_prof AND promo_prof.id_promotion=admin_promo.id_promo AND admin_promo.id_admin=" + id_admin;
			ResultSet resultat = requeteRecherche.executeQuery($query_profs);
			while (resultat.next()) {
				boolean isVisible=resultat.getString("visible").charAt(0)=='t';
				Prof prof = new Prof(resultat.getInt("id_prof"), resultat.getString("initiale_prof"), resultat.getString("prenom_prof"), resultat.getString("nom_prof"), resultat.getString("login_prof"), isVisible);
				prof.setDecharges(getDechargesProf(resultat.getInt("id_prof"), debutAnneeScolaire));
				listeProfs.put(prof.getNom()+" "+prof.getPrenom(),prof);
			}
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getProfsAdmin()", e.toString());
		}
		return listeProfs;
	}

	/**
	 * @return la liste des étudiants pour le groupe spécifié
	 */
	public TreeSet<Etudiant> getEtudiants(int id_groupe) {
		TreeSet<Etudiant> listeEtudiants = new TreeSet<Etudiant>();
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on récupère les étudiants
			ResultSet resultat = requeteRecherche.executeQuery("SELECT etudiant.* FROM etudiant, composition_groupe WHERE etudiant.id_etudiant>0 AND etudiant.id_etudiant=composition_groupe.id_etudiant AND composition_groupe.id_groupe=" + id_groupe);
			while (resultat.next()) {
				Etudiant etudiant = new Etudiant(resultat.getInt("id_etudiant"), resultat.getString("prenom_etudiant"), resultat.getString("nom_etudiant"), resultat.getString("login_etudiant"));
				listeEtudiants.add(etudiant);
			}
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getEtudiants()", e.toString());
		}
		return listeEtudiants;
	}


	public ArrayList<String> getEtudiantsAll(String promo) {
		ArrayList<String> listeEtudiants = new ArrayList<String>();
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			ResultSet resultat = requeteRecherche.executeQuery("SELECT login_etudiant FROM etudiant inner join composition_groupe using (id_etudiant) inner join groupe using (id_groupe) where id_promo=(select id_promo from promo where promo='"+promo+"')");
			while (resultat.next()) {
				listeEtudiants.add(resultat.getString("login_etudiant").toLowerCase());
			}
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getEtudiants()", e.toString());
		}
		return listeEtudiants;
	}

	/**
	 * @return renvoie la liste des salles libres entre les deux dates données
	 */
	public TreeSet<Salle> getSallesLibres(GregorianCalendar debut, GregorianCalendar fin) {
		TreeSet<Salle> listeSallesOccupees = new TreeSet<Salle>();
		TreeSet<Salle> listeSalles = new TreeSet<Salle>();
		Statement requeteRecherche;
		long date_debut = debut.getTimeInMillis() / 1000; // en secondes
		long date_fin = fin.getTimeInMillis() / 1000; // en secondes
		try {
			requeteRecherche = db.createStatement();
			// on récupère les salles occupées
			ResultSet resultat = requeteRecherche.executeQuery("SELECT salle.id_salle FROM salle,dispense WHERE salle.id_salle>0 AND (salle.id_salle=dispense.id_salle OR salle.id_salle=dispense.id_salle2) AND ((dispense.date_dbt<=" + date_debut + " AND " + date_debut
					+ "<dispense.date_fin) OR (dispense.date_dbt<" + date_fin + " AND " + date_fin + "<=dispense.date_fin) OR (" + date_debut + "<dispense.date_dbt AND dispense.date_fin<" + date_fin + "))");
			while (resultat.next()) {
				listeSallesOccupees.add(getSalle(resultat.getInt("id_salle")));
			}
			// on récupère toutes les salles
			resultat = requeteRecherche.executeQuery("SELECT * FROM salle");
			while (resultat.next()) {
				Salle salle = new Salle(resultat.getInt("id_salle"), resultat.getString("nom_salle"), resultat.getInt("nb_places"), resultat.getInt("nb_ordinateurs"), resultat.getString("videoprojecteur").charAt(0)=='t', resultat.getString("ip_dbt"), resultat.getString("ip_fin"), resultat.getBoolean("acces_internet"));
				listeSalles.add(salle);
			}
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getSallesLibres()", e.toString());
		}
		listeSalles.removeAll(listeSallesOccupees);
		return listeSalles;
	}

	/**
	 * @return renvoie la liste des groupes libres pour la promo donnée entre les deux dates données
	 *  si promo==null : teste sur toutes les promos et renvoie null si la plage horaire est complètement libre
	 *  si promo!=null : le boolean indique si l'on tient compte des jours fériés (true) ou pas (false)
	 */
	public TreeSet<Groupe> getGroupesLibres(String promo, GregorianCalendar debut, GregorianCalendar fin, boolean avecJourFeries) {
		TreeSet<Groupe> liste = new TreeSet<Groupe>();
		if (promo==null) {
			//boolean libre=true;
			Statement requeteRecherche;
			long date_debut = debut.getTimeInMillis() / 1000; // en secondes
			long date_fin = fin.getTimeInMillis() / 1000; // en secondes
			try {
				requeteRecherche = db.createStatement();
				// on récupère les groupes occupés (on ne met pas WHERE id_groupe>0 pour les jours fériés)
				ResultSet resultat = requeteRecherche.executeQuery("SELECT groupe.id_groupe,nom_groupe,promo,promo.id_promo,taille_edt,position_edt,niveau_edt,type_ppn,promo.id_departement FROM groupe,promo,dispense WHERE groupe.id_promo=promo.id_promo AND groupe.id_groupe=dispense.id_groupe AND ((dispense.date_dbt <=" + date_debut + " AND " + date_debut
						+ "<dispense.date_fin) OR (dispense.date_dbt<" + date_fin + " AND " + date_fin + "<=dispense.date_fin) OR (" + date_debut + "<dispense.date_dbt AND dispense.date_fin<" + date_fin + "))");
				if (!resultat.next()) { // il n'y a aucun groupe pris
					requeteRecherche.close();
					resultat.close();
					return null;
				}
				else {
					do {
						Groupe groupe = new Groupe(resultat.getInt("id_groupe"), resultat.getString("nom_groupe"), "", resultat.getInt("taille_edt"), resultat.getInt("position_edt"), resultat.getInt("niveau_edt"), resultat.getString("type_ppn"), resultat.getInt("id_departement"), resultat.getInt("id_promo"));
						liste.add(groupe);
					} while (resultat.next());
					resultat.close();
					requeteRecherche.close();
					return liste;
				}
			}
			catch (SQLException e) {
				e.printStackTrace();
				Utilitaire.showMessageDialog("ControleurDB : getGroupesLibres()", e.toString());
			}
		}
		boolean[] colonnes_occupees = new boolean[getNbColonnesVirtuelles(promo)]; // tableau indiquant  l'occupation des colonnes virtuelles
		for (int i = 0; i < colonnes_occupees.length; i++)
			colonnes_occupees[i] = false;
		Statement requeteRecherche;
		long date_debut = debut.getTimeInMillis() / 1000; // en secondes
		long date_fin = fin.getTimeInMillis() / 1000; // en secondes
		try {
			if (avecJourFeries && getPlageHoraire(debut,"",0)!=null) // on a un jour férié et on en tient compte
				return new TreeSet<Groupe>();
			requeteRecherche = db.createStatement();
			// on récupère les groupes occupés
			ResultSet resultat = requeteRecherche.executeQuery("SELECT taille_edt,position_edt FROM promo,groupe,dispense WHERE groupe.id_groupe=dispense.id_groupe AND  groupe.id_promo=promo.id_promo AND promo.promo='" + promo + "' AND ((dispense.date_dbt <=" + date_debut + " AND " + date_debut
					+ "<dispense.date_fin) OR (dispense.date_dbt<" + date_fin + " AND " + date_fin + "<=dispense.date_fin) OR (" + date_debut + "<dispense.date_dbt AND dispense.date_fin<" + date_fin + "))");
			while (resultat.next()) {
				//System.out.println("position=" + resultat.getInt("position_edt") + " taille=" + resultat.getInt("taille_edt"));
				for (int i = resultat.getInt("position_edt"); i < resultat.getInt("position_edt") + resultat.getInt("taille_edt"); i++)
					colonnes_occupees[i] = true;
			}
			//System.out.println("colonnes occupées :");
			//for (int i=0;i<colonnes_occupees.length;i++)
			//System.out.print(colonnes_occupees[i] + " ");
			resultat = requeteRecherche.executeQuery("SELECT id_groupe,nom_groupe,promo.promo,promo.id_promo,taille_edt,position_edt,niveau_edt,type_ppn,promo.id_departement FROM groupe,promo WHERE  groupe.id_promo=promo.id_promo AND promo.promo='" + promo + "'");
			boolean libre;
			while (resultat.next()) {
				libre = true;
				for (int i = resultat.getInt("position_edt"); i < resultat.getInt("position_edt") + resultat.getInt("taille_edt"); i++)
					libre &= !colonnes_occupees[i];
				if (libre) {
					Groupe groupe = new Groupe(resultat.getInt("id_groupe"), resultat.getString("nom_groupe"), "", resultat.getInt("taille_edt"), resultat.getInt("position_edt"), resultat.getInt("niveau_edt"), resultat.getString("type_ppn"), resultat.getInt("id_departement"), resultat.getInt("id_promo"));
					liste.add(groupe);
				}
			}
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getGroupesLibres()", e.toString());
		}
		return liste;
	}

	/**
	 * @return renvoie la liste des profs libres (visibles et non visibles) entre les deux dates données
	 */
	public TreeSet<Prof> getProfsLibres(GregorianCalendar debut, GregorianCalendar fin) {
		TreeSet<Prof> listeProfsOccupes = new TreeSet<Prof>();
		TreeSet<Prof> listeProfs = new TreeSet<Prof>();
		Statement requeteRecherche;
		long date_debut = debut.getTimeInMillis() / 1000; // en secondes
		long date_fin = fin.getTimeInMillis() / 1000; // en secondes
		try {
			requeteRecherche = db.createStatement();
			// on récupère les profs occupés
			ResultSet resultat = requeteRecherche.executeQuery("SELECT prof.* FROM prof,dispense WHERE prof.id_prof>0 AND (prof.id_prof=dispense.id_prof OR prof.id_prof=dispense.id_prof2 OR prof.id_prof=dispense.id_prof3 OR prof.id_prof=dispense.id_prof4) AND ((dispense.date_dbt <="
					+ date_debut + " AND " + date_debut + "<dispense.date_fin) OR (dispense.date_dbt<" + date_fin + " AND " + date_fin + "<=dispense.date_fin) OR (" + date_debut + "<dispense.date_dbt AND dispense.date_fin<" + date_fin + "))");
			while (resultat.next()) {
				Prof prof = new Prof(resultat.getInt("id_prof"), resultat.getString("initiale_prof"), resultat.getString("prenom_prof"), resultat.getString("nom_prof"), resultat.getString("login_prof"), true);
				listeProfsOccupes.add(prof);
			}
			// on récupère tous les profs (visibles et non visibles)
			resultat = requeteRecherche.executeQuery("SELECT * FROM prof");
			while (resultat.next()) {
				Prof prof = new Prof(resultat.getInt("id_prof"), resultat.getString("initiale_prof"), resultat.getString("prenom_prof"), resultat.getString("nom_prof"), resultat.getString("login_prof"), true);
				listeProfs.add(prof);
			}
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getProfsLibres()", e.toString());
		}
		listeProfs.removeAll(listeProfsOccupes);
		return listeProfs;
	}

	/**
	 * insère un jour férié dans la base de données
	 * 
	 * @return renvoie une chaine de caractères contenant les erreurs
	 *         éventuelles ("" si pas d'erreur)
	 */
	public String ajouterJourFerie(PlageHoraire plage) {
		String erreurInsertion = "";
		String avertissement="";
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on récupère les données
			GregorianCalendar dateDebut = plage.getDateDebut();
			GregorianCalendar dateFin = plage.getDateFin();
			String memo=plage.getMemo();
			int idMatiere = plage.getIdMatiere();
			// on vérifie si on peut ajouter le jour férié
			TreeSet<Groupe> listeGroupesPris=getGroupesLibres(null, dateDebut, dateFin, true);
			boolean groupeLibre = (listeGroupesPris==null);
			if (!groupeLibre) {
				for (Iterator<Groupe> iter = listeGroupesPris.iterator(); iter.hasNext();) {
					Groupe g = iter.next();
					avertissement += "Groupe " + getPromo(g.getIdGroupe()) + " "+g.getNom() + " occupé\n";
					supprimerPlagesHorairesJour(plage, getPromo(g.getIdGroupe()));
				}
				Utilitaire.showMessageDialog("ControleurDB - Ajout d'un jour férié", avertissement);				
			}
			boolean insertionReussie = (requeteRecherche.executeUpdate("INSERT INTO dispense VALUES (" + /*plage.getIdPlage()*/ getIdPlageHoraire() + "," + dateDebut.getTimeInMillis() / 1000 + "," + dateFin.getTimeInMillis() / 1000 + ",'" + memo + "',0,0," + idMatiere + ",0)") == 1);
			if (!insertionReussie)
				erreurInsertion += "Erreur d'insertion dans la base de données : " + Utilitaire.afficherDate(dateDebut, dateFin);
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			erreurInsertion+=e.toString()+"\n";
			Utilitaire.showMessageDialog("ControleurDB : ajouterJourFerie() ", erreurInsertion.toString());
		}
		return erreurInsertion;
	}

	/**
	 * insère une plage horaire dans la base de données
	 * 
	 * @return renvoie une chaine de caractères contenant les erreurs
	 *         éventuelles ("" si pas d'erreur)
	 */
	public String ajouterPlageHoraire(PlageHoraire plage) {
		String erreurInsertion = "";
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on récupère les données
			GregorianCalendar dateDebut = plage.getDateDebut();
			GregorianCalendar dateFin = plage.getDateFin();
			String memo=plage.getMemo();
			char controle=plage.IsControle()==true?'t':'f';
			char internet=plage.accesInternet()==true?'t':'f';
			int idSalle = plage.getIdSalle();
			int idSalle2 = plage.getIdSalle2();
			int idGroupe = plage.getIdGroupe();
			int idMatiere = plage.getIdMatiere();
			int idProf = plage.getIdProf();
			int idProfAppel = plage.getIdProfQuiFaitAppel();
			int idProf2 = plage.getIdProf2();
			int idProf3 = plage.getIdProf3();
			int idProf4 = plage.getIdProf4();
			// on vérifie si on peut ajouter la nouvelle plage
			TreeSet<Salle> sallesLibres = getSallesLibres(dateDebut, dateFin);
			boolean salleLibre = sallesLibres.contains(getSalle(idSalle));
			boolean salle2Libre=sallesLibres.contains(getSalle(idSalle2));
			TreeSet<Prof> profsLibres = getProfsLibres(dateDebut, dateFin);
			boolean profLibre = profsLibres.contains(getProf(idProf));
			boolean prof2Libre = profsLibres.contains(getProf(idProf2)) || idProf2==0;
			boolean prof3Libre = profsLibres.contains(getProf(idProf3)) || idProf3==0;
			boolean prof4Libre = profsLibres.contains(getProf(idProf4)) || idProf4==0;
			boolean groupeLibre = getGroupesLibres(getPromo(plage.getIdGroupe()), dateDebut, dateFin, true).contains(getGroupe(idGroupe));
			if (salleLibre && salle2Libre && profLibre && prof2Libre && prof3Libre && prof4Libre && groupeLibre) {
				//System.out.println("INSERT INTO dispense VALUES (" + getIdPlageHoraire() + "," + dateDebut.getTimeInMillis() / 1000 + "," + dateFin.getTimeInMillis() / 1000 + ",'" +  memo + "'," + idSalle + "," + idGroupe + "," + idMatiere + "," + idProf + ",'" + controle + "'," + idSalle2 + "," + idProf2 + "," + idProf3 + "," + idProf4 + ",'f', '" + internet + "')");
				boolean insertionReussie = (requeteRecherche.executeUpdate("INSERT INTO dispense VALUES (" + getIdPlageHoraire() + "," + dateDebut.getTimeInMillis() / 1000 + "," + dateFin.getTimeInMillis() / 1000 + ",'" +  memo + "'," + idSalle + "," + idGroupe + "," + idMatiere + "," + idProf + ",'" + controle + "'," + idSalle2 + "," + idProf2 + "," + idProf3 + "," + idProf4 + ",'f', '" + internet + "'," + idProfAppel + ")") == 1);
				if (!insertionReussie)
					erreurInsertion += "Erreur d'insertion dans la base de données : " + Utilitaire.afficherDate(dateDebut, dateFin);
			} else if (!groupeLibre)
				erreurInsertion += Utilitaire.afficherDate(dateDebut, dateFin) + " : groupe " + getGroupe(idGroupe) + " occupé\n";
			else {
				String erreurTemporaire="";
				if (!salleLibre) {
					erreurTemporaire+= Utilitaire.afficherDate(dateDebut, dateFin) + " : salle " + getSalle(idSalle) + " occupée\n";
					idSalle=0;
				}
				if (!salle2Libre) {
					erreurTemporaire += Utilitaire.afficherDate(dateDebut, dateFin) + " : salle " + getSalle(idSalle2) + " occupée\n";
					idSalle2=0;
				}
				if (!profLibre) {
					erreurTemporaire += Utilitaire.afficherDate(dateDebut, dateFin) + " : prof " + getProf(idProf) + " occupé\n";
					idProf=0;
				}
				if (!prof2Libre) {
					erreurTemporaire += Utilitaire.afficherDate(dateDebut, dateFin) + " : prof " + getProf(idProf2) + " occupé\n";
					idProf2=0;
				}
				if (!prof3Libre) {
					erreurTemporaire += Utilitaire.afficherDate(dateDebut, dateFin) + " : prof " + getProf(idProf3) + " occupé\n";
					idProf3=0;
				}
				if (!prof4Libre) {
					erreurTemporaire += Utilitaire.afficherDate(dateDebut, dateFin) + " : prof " + getProf(idProf4) + " occupé\n";
					idProf4=0;
				}
				/*int reponse=JOptionPane.showOptionDialog(null, erreurTemporaire+"\nVoulez-vous insérer sans la (les) salle(s) occupée(s) ou sans le(s) prof(s) occupé(s) ?", "Erreur plage horaire", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);*/
				//Utilitaire.showMessageDialog("Erreur plage horaire","Voulez-vous insérer sans la (les) salle(s) occupée(s) ou sans le(s) prof(s) occupé(s) ?");
				erreurInsertion += "Erreur d'insertion dans la base de données (prof ou salle non libre) : " + Utilitaire.afficherDate(dateDebut, dateFin);
				/*if (reponse==JOptionPane.YES_OPTION) {
	boolean insertionReussie = (requeteRecherche.executeUpdate("INSERT INTO dispense VALUES (" + getIdPlageHoraire() + "," + dateDebut.getTimeInMillis() / 1000 + "," + dateFin.getTimeInMillis() / 1000 + ",'" + memo + "'," + idSalle + "," + idGroupe + "," + idMatiere + "," + idProf + ",'" + controle + "'," + idSalle2 + "," + idProf2 + "," + idProf3 + "," + idProf4 + ",'f', '" + internet + "'," + idProfAppel + ")") == 1);
	if (!insertionReussie)
		erreurInsertion += "Erreur d'insertion dans la base de données : " + Utilitaire.afficherDate(dateDebut, dateFin);
}
else erreurInsertion+=erreurTemporaire;*/
			}
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();			
			erreurInsertion+=e.toString()+"\n";
			Utilitaire.showMessageDialog("ControleurDB : ajouterPlageHoraire()", erreurInsertion.toString());
		}
		return erreurInsertion;
	}

	/**
	 * supprime la plage horaire dont l'identifiant est passé en argument
	 */
	public void supprimerPlageHoraire(int idPlage) {
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on supprime l'ancienne plage
			//System.out.println("on supprime " + idPlage);
			requeteRecherche.executeUpdate("DELETE FROM dispense WHERE id_dispense=" + idPlage);
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : supprimerPlageHoraire()", e.toString());
		}
	}

	/**
	 * @return un nouvel identifiant pour une plage horaire
	 */
	public int getIdPlageHoraire() {
		int id = 0;
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on récupère l'identifiant maximum
			ResultSet resultat = requeteRecherche.executeQuery("SELECT max(id_dispense) FROM dispense");
			resultat.next();
			id = resultat.getInt("max") + 1;
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getIdPlageHoraire()", e.toString());
		}
		return id;
	}

	/**
	 * @return un nouvel identifiant pour une salle
	 */
	private int getIdSalle() {
		int id = 0;
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on récupère l'identifiant maximum
			ResultSet resultat = requeteRecherche.executeQuery("SELECT max(id_salle) FROM salle");
			resultat.next();
			id = resultat.getInt("max") + 1;
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getIdSalle()", e.toString());
		}
		return id;
	}

	/**
	 * @return un nouvel identifiant pour un groupe
	 */
	private int getIdGroupe() {
		int id = 0;
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on récupère l'identifiant maximum
			ResultSet resultat = requeteRecherche.executeQuery("SELECT max(id_groupe) FROM groupe");
			resultat.next();
			id = resultat.getInt("max") + 1;
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getIdGroupe()", e.toString());
		}
		return id;
	}

	/**
	 * @return un nouvel identifiant pour une promo
	 */
	public int getIdPromo() {
		int id = 0;
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on récupère l'identifiant maximum
			ResultSet resultat = requeteRecherche.executeQuery("SELECT max(id_promo) FROM promo");
			resultat.next();
			id = resultat.getInt("max") + 1;
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getIdPromo()", e.toString());
		}
		return id;
	}

	/**
	 * @return un nouvel identifiant pour une matiére
	 */
	private int getIdMatiere() {
		int id = 0;
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on récupère l'identifiant maximum
			ResultSet resultat = requeteRecherche.executeQuery("SELECT max(id_matiere) FROM matiere");
			resultat.next();
			id = resultat.getInt("max") + 1;
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getIdMatiere()", e.toString());
		}
		return id;
	}

	/**
	 * @return un nouvel identifiant pour un prof
	 */
	private int getIdProf() {
		int id = 0;
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on récupère l'identifiant maximum
			ResultSet resultat = requeteRecherche.executeQuery("SELECT max(id_prof) FROM prof");
			resultat.next();
			id = resultat.getInt("max") + 1;
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getIdProf()", e.toString());
		}
		return id;
	}

	/**
	 * @return un nouvel identifiant pour un étudiant
	 */
	private int getIdEtudiant() {
		int id = 0;
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on récupère l'identifiant maximum
			ResultSet resultat = requeteRecherche.executeQuery("SELECT max(id_etudiant) FROM etudiant");
			resultat.next();
			id = resultat.getInt("max") + 1;
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getIdEtudiant()", e.toString());
		}
		return id;
	}

	/**
	 * modifie la salle passée en paramètre et met à jour la base de données
	 */
	public void modifierSalle(WDialog dialog, Salle s, String nouveauNom, int nbPlaces, int nbOrdis, boolean videoproj,String ipDbt ,String ipFin, boolean internet) {
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			String requete = "UPDATE salle SET nom_salle='" + nouveauNom + "', nb_places=" + nbPlaces + ", nb_ordinateurs=" + nbOrdis + ", videoprojecteur=";
			if (videoproj) requete+="'t'";
			else requete+="'f'";
			requete+=",ip_dbt='"+ipDbt+"', ip_fin='"+ipFin+"',acces_internet="+internet;
			requete+=" WHERE id_salle=" + s.getIdSalle();
			int nbModif = requeteRecherche.executeUpdate(requete);
			if (nbModif==1) {
				s.setNom(nouveauNom);
				s.setNbPlaces(nbPlaces);
				s.setNbOrdinateurs(nbOrdis);
				s.setVideoprojecteur(videoproj);
			} else
				Utilitaire.showMessageDialog(dialog, "Modification non effectuée !!!");
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			//Utilitaire.showMessageDialog("ControleurDB", e.toString());
			Utilitaire.showMessageDialog(dialog, e.toString());
		}
	}

	/**
	 * modifie le nom de la promo dans la base de données les deux noms passés en paramètre doivent être différents !
	 */
	public void modifierNomPromo(String ancienNom, String nouveauNom) {
		Statement requeteRecherche;
		boolean nouveauNomOK = true;
		TreeSet<Groupe> listeGroupes = getGroupes();
		for (Iterator<Groupe> iter = listeGroupes.iterator(); iter.hasNext();) {
			Groupe g = iter.next();
			if (getPromo(g.getIdGroupe()).equalsIgnoreCase(nouveauNom)) {
				nouveauNomOK = false;
				break;
			}
		}
		if (!nouveauNomOK) {
			Utilitaire.showMessageDialog("Modification nom de promo", "Le nom de promo '"+nouveauNom+"' existe déjà...");
			/*JOptionPane.showMessageDialog(null, "Le nom de promo existe déjà...");*/            
			return;
		}
		else {
			try {
				requeteRecherche = db.createStatement();
				//int nbModifGroupes = requeteRecherche.executeUpdate("UPDATE groupe SET promo='" + nouveauNom + "' WHERE promo='" + ancienNom + "'");
				int nbModifPromos = requeteRecherche.executeUpdate("UPDATE promo SET promo='" + nouveauNom + "' WHERE promo='" + ancienNom + "'");
				Utilitaire.showMessageDialog("Modification nom de promo", "La promo " + ancienNom + " a été renommée en " + nouveauNom );
				/*JOptionPane.showMessageDialog(null, "Mise à jour de " + nbModifPromos + " promo(s)");*/				
				requeteRecherche.close();
			} catch (SQLException e) {
				e.printStackTrace();
				Utilitaire.showMessageDialog("ControleurDB : modifierNomPromo()", e.toString());
			}
		}
	}

	/**
	 * modifie la matière passée en paramètre (il faut avoir vérifié
	 * préalablement qu'elle n'existe pas déjà !) et met à jour la base de
	 * données
	 * 
	 * @return renvoie une chaine de caractères contenant les erreurs
	 *         éventuelles ("" si pas d'erreur)
	 */
	public String modifierMatiere(Matiere matiere, String nom, String nom_compl, int rouge, int vert, int bleu, String nom_pole) {
		String erreurInsertion="";
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			int nbModif = requeteRecherche.executeUpdate("UPDATE matiere SET nom_matiere='" + nom + "', nom_compl_matiere='" + nom_compl + "',r_matiere=" + rouge + ",v_matiere=" + vert + ",b_matiere=" + bleu + ",pole='" + nom_pole + "' WHERE id_matiere=" + matiere.getIdMatiere());
			if (nbModif == 1)
				matiere.setInfos(nom, nom_compl, rouge, vert, bleu, nom_pole);
			else
				erreurInsertion+="Modification non effectuée...\n";
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();			
			erreurInsertion+=e.toString()+"\n";
			Utilitaire.showMessageDialog("ControleurDB : modifierMatiere()", erreurInsertion.toString());
		}
		return erreurInsertion;
	}

	public String modifierMatiere2(Matiere matiere) {
		String erreurInsertion="";
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			int nbModif = requeteRecherche.executeUpdate("UPDATE matiere SET nom_matiere='" + matiere.getNom() + "', nom_compl_matiere='" + matiere.getNomCompl() + "',r_matiere=" +  matiere.getRouge() + ",v_matiere=" +  matiere.getVert() + ",b_matiere=" +  matiere.getBleu() + ",pole='" +  matiere.getPole() + "' WHERE id_matiere=" + matiere.getIdMatiere());
			if (nbModif != 1)erreurInsertion+="Modification non effectuée.\n";
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();			
			erreurInsertion+=e.toString()+"\n";
		}
		return erreurInsertion;
	}
	
	
	/**
	 * modifie le prof passé en paramètre (il faut avoir vérifié préalablement
	 * qu'il n'existe pas déjà !) et met à jour la base de données
	 * les décharges concernent l'année scolaire passée en paramètre
	 */
	public void modifierProf(int debutAnneeScolaire, Prof prof, String initiale, String prenom, String nom, int serviceMax, int decharge_projets, int decharge_stages, int decharge_apprentis, int decharge, String commentaire, boolean titulaire, String login, boolean visible) {
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			String requete1 = "UPDATE prof SET initiale_prof='" + initiale + "', prenom_prof='" + prenom + "', nom_prof='" + nom + "', login_prof='" + login + "', visible=";
			if (visible) requete1+="'t'";
			else requete1+="'f'";
			requete1+=" WHERE id_prof=" + prof.getIdProf();
			int nbModif1 = requeteRecherche.executeUpdate(requete1);
			String requete2 = "SELECT count(*) as nb FROM decharge WHERE debutAnneeScolaire="+debutAnneeScolaire+" AND id_prof="+prof.getIdProf();
			int nbModif2;
			ResultSet result=requeteRecherche.executeQuery(requete2);
			result.next();
			if (result.getInt("nb")==1) { // décharges déjà existantes pour cette année scolaire
				String requete2bis = "UPDATE decharge SET service_max='"+serviceMax+"', decharge_projets="+decharge_projets+", decharge_stages="+decharge_stages+", decharge_apprentis="+decharge_apprentis+", decharge="+decharge+", commentaire='"+commentaire+"'";
				if (titulaire)
					requete2bis += ", titulaire='t'";
				else
					requete2bis += ", titulaire='f'";
				requete2bis+=" WHERE debutAnneeScolaire=" + debutAnneeScolaire + " AND id_prof=" + prof.getIdProf();
				nbModif2 = requeteRecherche.executeUpdate(requete2bis);
			} else { // on n'avait pas encore saisi les décharges
				String requete2bis = "INSERT INTO decharge VALUES (" + debutAnneeScolaire + "," + prof.getIdProf() + "," + serviceMax + "," + decharge_projets+","+decharge_stages+","+decharge_apprentis+","+decharge+","+"'"+commentaire+"'";
				if (titulaire)
					requete2bis += ",'t')";
				else
					requete2bis += ",'f')";
				nbModif2 = requeteRecherche.executeUpdate(requete2bis);
			}
			if (nbModif1 == 1 && nbModif2==1)
				prof.setInfos(initiale, prenom, nom, serviceMax, decharge_projets, decharge_stages, decharge_apprentis, decharge, commentaire, titulaire, login, visible);
			else
				Utilitaire.showMessageDialog("ControleurDB : modifierProf()", "Modification non effectuée !!!");
			/*JOptionPane.showMessageDialog(null, "Modification non effectuée !!!");*/				
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : modifierProf()", e.toString());
		}
	}

	/**
	 * modifie l'administrateur passé en paramètre (il faut avoir vérifié préalablement
	 * qu'il n'existe pas déjà !) et met à jour la base de données
	 */
	public void modifierAdmin(String login_admin, String nouveau_login, ArrayList<String>listePromos) {
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			String requete = "UPDATE admin SET login_admin='" + nouveau_login + "' WHERE login_admin='" + login_admin + "'";
			int nbModif = requeteRecherche.executeUpdate(requete);
			if (nbModif != 1)
				Utilitaire.showMessageDialog("ControleurDB : modifierAdmin()", "Modification non effectuée !!!");
			/*JOptionPane.showMessageDialog(null, "Modification non effectuée !!!");*/            	
			ResultSet resultat = requeteRecherche.executeQuery("SELECT id_admin FROM admin WHERE login_admin='"+nouveau_login+"'");
			resultat.next();
			int id= resultat.getInt("id_admin");
			resultat.close();
			requeteRecherche.executeUpdate("DELETE FROM admin_promo WHERE id_admin=" + id);
			for (Iterator<String> iter = listePromos.iterator(); iter.hasNext();)
				requeteRecherche.executeUpdate("INSERT INTO admin_promo VALUES (" + id + "," + getIdPromotion(iter.next()) + ")");
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : modifierAdmin()", e.toString());
		}
	}

	/**
	 * modifie le département passé en paramètre
	 */
	public void modifierDepartement(String ancien_nom_departement, String nouveau_nom_departement, String nouveau_nom_chef) {
		Statement requeteRecherche;
		try {
			int id_nouveau_chef;
			requeteRecherche = db.createStatement();
			String requete = "SELECT id_admin FROM admin WHERE login_admin='" + nouveau_nom_chef + "'";
			ResultSet resultat = requeteRecherche.executeQuery(requete);
			resultat.next();
			id_nouveau_chef=resultat.getInt("id_admin");
			if (ancien_nom_departement==null) { // on crée un nouveau département
				requete="SELECT MAX(id_departement) AS max FROM departement";
				resultat = requeteRecherche.executeQuery(requete);
				resultat.next();
				int id_dpt=resultat.getInt("max")+1;
				requete="INSERT INTO departement VALUES (" + id_dpt + ",'" + nouveau_nom_departement + "','" + id_nouveau_chef + "')";
			} else // on modifie un département existant
				requete = "UPDATE departement SET id_chef_departement=" + id_nouveau_chef + ", nom_departement='" + nouveau_nom_departement + "' WHERE nom_departement='" + ancien_nom_departement + "'";
			int nbModif = requeteRecherche.executeUpdate(requete);
			if (nbModif != 1)
				Utilitaire.showMessageDialog("ControleurDB : modifierDepartement()", "Modification non effectuée !!!");
			/*JOptionPane.showMessageDialog(null, "Modification non effectuée !!!");*/
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : modifierDepartement()", e.toString());
		}
	}

	/**
	 * renvoie le nom du chef du département
	 */
	public String getChefDepartement(String departement) {
		String chef="";
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			ResultSet resultat = requeteRecherche.executeQuery("SELECT login_admin FROM admin,departement WHERE admin.id_admin=departement.id_chef_departement AND nom_departement='" + departement + "'");
			if (resultat.next())
				chef=resultat.getString("login_admin");
			else Utilitaire.showMessageDialog("ControleurDB : getChefDepartement()", "Pas de chef pour ce département !!!");	/*JOptionPane.showMessageDialog(null, "Pas de chef pour ce département !!!");*/
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getChefDepartement()", e.toString());
		}
		return chef;
	}

	/**
	 * modifie l'étudiant passé en paramètre (il faut avoir vérifié préalablement
	 * qu'il n'existe pas déjà !) et met à jour la base de données
	 */
	public void modifierEtudiant(Etudiant etudiant, String prenom, String nom, String login, ArrayList<Groupe>listeGroupes) {
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			String requete = "UPDATE etudiant SET prenom_etudiant='" + prenom + "', nom_etudiant='" + nom + "', login_etudiant='" + login + "' WHERE id_etudiant=" + etudiant.getIdEtudiant();
			int nbModif = requeteRecherche.executeUpdate(requete);
			if (nbModif == 1)
				etudiant.setInfos(prenom, nom, login);
			else
				Utilitaire.showMessageDialog("ControleurDB : modifierEtudiant()", "Modification non effectuée !!!");
			/*JOptionPane.showMessageDialog(null, "Modification non effectuée !!!");*/
			requeteRecherche.executeUpdate("DELETE FROM composition_groupe WHERE id_etudiant="+etudiant.getIdEtudiant());
			for (Iterator<Groupe> iter = listeGroupes.iterator(); iter.hasNext();)
				requeteRecherche.executeUpdate("INSERT INTO composition_groupe VALUES("+iter.next().getIdGroupe()+","+etudiant.getIdEtudiant()+")");
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : modifierEtudiant()", e.toString());
		}
	}

	/**
	 * supprime une salle de la base de données
	 */
	public boolean supprimerSalle(Salle s) {
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on vérifie que la salle n'est plus utilisée avant de la supprimer
			ResultSet resultat = requeteRecherche.executeQuery("SELECT id_dispense FROM dispense WHERE id_salle=" + s.getIdSalle());
			if (resultat.next()) {
				Utilitaire.showMessageDialog("ControleurDB : supprimerSalle()", "Suppression impossible : la salle est encore utilisée");
				return false;
			} else
				requeteRecherche.executeUpdate("DELETE FROM salle WHERE id_salle=" + s.getIdSalle());
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : supprimerSalle()", e.toString());
		}
		return true;
	}

	/**
	 * supprime une matière de la base de données
	 */
	public void supprimerMatiere(Matiere m) {
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on vérifie que la matière n'est plus utilisée avant de la
			// supprimer
			ResultSet resultat = requeteRecherche.executeQuery("SELECT id_dispense FROM dispense WHERE id_matiere=" + m.getIdMatiere());
			if (resultat.next()) {
				Utilitaire.showMessageDialog("ControleurDB : supprimerMatiere()", "Suppression impossible : la matière est encore utilisée");
				/*JOptionPane.showMessageDialog(null, "Suppression impossible : la matière est encore utilisée");*/
			} else {
				requeteRecherche.executeUpdate("DELETE FROM matiere WHERE id_matiere=" + m.getIdMatiere());
				requeteRecherche.executeUpdate("DELETE FROM promo_matiere WHERE id_matiere=" + m.getIdMatiere());
			}
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : supprimerMatiere()", e.toString());
		}
	}

	/**
	 * supprime un prof de la base de données
	 */
	public void supprimerProf(Prof p) {
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on vérifie que le prof n'est plus utilisé avant de le supprimer
			ResultSet resultat = requeteRecherche.executeQuery("SELECT id_dispense FROM dispense WHERE id_prof=" + p.getIdProf());
			if (resultat.next()) {
				Utilitaire.showMessageDialog("ControleurDB : supprimerProf()", "Suppression impossible : le prof est encore utilisée");
				/*JOptionPane.showMessageDialog(null, "Suppression impossible : le prof est encore utilisé");*/
			} else {
				requeteRecherche.executeUpdate("DELETE FROM prof WHERE id_prof=" + p.getIdProf());
				requeteRecherche.executeUpdate("DELETE FROM decharge WHERE id_prof=" + p.getIdProf());
				requeteRecherche.executeUpdate("DELETE FROM promo_prof WHERE id_prof=" + p.getIdProf());
			}
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : supprimerProf()", e.toString());
		}
	}
	
	public String supprimerProf2(Prof p) {
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on vérifie que le prof n'est plus utilisé avant de le supprimer
			ResultSet resultat = requeteRecherche.executeQuery("SELECT id_dispense FROM dispense WHERE id_prof=" + p.getIdProf());
			if (resultat.next()) {
				return "Suppression impossible : le prof est encore utilisée";
			} else {
				requeteRecherche.executeUpdate("DELETE FROM prof WHERE id_prof=" + p.getIdProf());
				requeteRecherche.executeUpdate("DELETE FROM decharge WHERE id_prof=" + p.getIdProf());
				requeteRecherche.executeUpdate("DELETE FROM promo_prof WHERE id_prof=" + p.getIdProf());
			}
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return e.toString();
		}
		return null;
	}


	/**
	 * supprime un administrateur de la base de données
	 */
	/*public void supprimerAdministrateur(String admin) {*/
	public boolean supprimerAdministrateur(String admin) {    	
		Statement requeteRecherche;
		boolean supprimer=false;
		try {
			requeteRecherche = db.createStatement();
			ResultSet resultat = requeteRecherche.executeQuery("SELECT id_admin FROM admin WHERE login_admin='"+admin+"'");
			resultat.next();
			int id= resultat.getInt("id_admin");
			resultat.close();
			requeteRecherche = db.createStatement();
			// on vérifie que l'admin n'est plus utilisé avant de le supprimer
			resultat = requeteRecherche.executeQuery("SELECT count(id_admin) AS nb FROM admin_promo WHERE id_admin=" + id);
			resultat.next();
			if (resultat.getInt("nb")>0) {
				Utilitaire.showMessageDialog("Suppression d'un administrateur", "Suppression impossible : l'administrateur '"+admin+"' est encore utilisé !");
				/*JOptionPane.showMessageDialog(null, "Suppression impossible : l'administrateur est encore utilisé");*/
			} else {
				requeteRecherche.executeUpdate("DELETE FROM admin WHERE id_admin=" + id);
				requeteRecherche.executeUpdate("DELETE FROM admin_promo WHERE id_admin=" + id);
				supprimer=true;
			}
			requeteRecherche.close();

		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : supprimerAdministrateur()", e.toString());
		}
		return supprimer;
	}

	/**
	 * supprime un étudiant de la base de données
	 */
	public void supprimerEtudiant(Etudiant etudiant) {
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			requeteRecherche.executeUpdate("DELETE FROM etudiant WHERE id_etudiant=" + etudiant.getIdEtudiant());
			requeteRecherche.executeUpdate("DELETE FROM composition_groupe WHERE id_etudiant=" + etudiant.getIdEtudiant());
			requeteRecherche.executeUpdate("DELETE FROM justificatif WHERE id_etudiant=" + etudiant.getIdEtudiant());
			requeteRecherche.executeUpdate("DELETE FROM appel WHERE id_etudiant=" + etudiant.getIdEtudiant());
			requeteRecherche.executeUpdate("DELETE FROM gn_avoirnote WHERE id_etudiant=" + etudiant.getIdEtudiant());
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : supprimerEtudiant()", e.toString());
		}
	}

	public void supprimerEtudiant2(int id) {
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			requeteRecherche.executeUpdate("DELETE FROM composition_groupe WHERE id_etudiant=" + id);
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : supprimerEtudiant()", e.toString());
		}
	}

	public void supprimerEtudiant3ParGroupe(int idE,int idG) {
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			requeteRecherche.executeUpdate("DELETE FROM composition_groupe WHERE id_etudiant="+idE+" and id_groupe="+idG);
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : supprimerEtudiant()", e.toString());
		}
	}

	/**
	 * supprime tous les étudiants passés en argument, AINSI QUE leurs notes et leurs absences
	 */
	public void supprimerTousLesEtudiants(ArrayList<Etudiant> listeIdEtudiants) {
		Statement requeteRecherche;
		String liste="(";
		for (Iterator<Etudiant> iter = listeIdEtudiants.iterator(); iter.hasNext();)
			liste+=iter.next().getIdEtudiant()+",";
		liste+="0)";
		try {
			requeteRecherche = db.createStatement();
			requeteRecherche.executeUpdate("DELETE FROM etudiant WHERE id_etudiant IN " + liste);
			requeteRecherche.executeUpdate("DELETE FROM composition_groupe WHERE id_etudiant IN " + liste);
			requeteRecherche.executeUpdate("DELETE FROM justificatif WHERE id_etudiant IN " + liste);
			requeteRecherche.executeUpdate("DELETE FROM appel WHERE id_etudiant IN " + liste);
			requeteRecherche.executeUpdate("DELETE FROM gn_avoirnote WHERE id_etudiant IN " + liste);
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : supprimerTousLesEtudiants()", e.toString());
		}
	}

	//Suprime simplement la liaison dans la promo
	public void supprimerTousLesEtudiantsSoft1Par1(int idE,int idG) {
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			requeteRecherche.executeUpdate("DELETE FROM composition_groupe WHERE id_groupe="+idG+" and id_etudiant="+idE);
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : supprimerTousLesEtudiants()", e.toString());
		}
	}

	/**
	 * ajoute une salle dans la base de données
	 */
	public void ajouterSalle(WDialog dialog, String nomNouvelleSalle, int nbPlaces, int nbOrdis, boolean videoproj,String ipDbt ,String ipFin, boolean internet) {
		Statement requeteRecherche;
		if (getSalles().contains(new Salle(0, nomNouvelleSalle, 0, 0, false,null,null,false)))
			Utilitaire.showMessageDialog(dialog, "Cette salle existe déjà!!!");
		/*JOptionPane.showMessageDialog(null, "Cette salle existe déjà!!!");*/
		else {
			try {
				requeteRecherche = db.createStatement();
				int nbModif = requeteRecherche.executeUpdate("INSERT INTO salle VALUES(" + getIdSalle() + ",'" + nomNouvelleSalle + "', '"+ ipDbt +"' , '"+ ipFin +"', "+ internet +", " + nbPlaces + ", " + nbOrdis + ", '" + (videoproj?'t':'f') + "')");
				if (nbModif != 1)
					Utilitaire.showMessageDialog(dialog, "Erreur lors de l'insertion !!!");
				requeteRecherche.close();
			} catch (SQLException e) {
				e.printStackTrace();
				Utilitaire.showMessageDialog(dialog, e.toString());
			}
		}
	}

	/**
	 * ajoute un groupe dans la base de données
	 * 
	 * @return renvoie une chaine de caractères contenant les erreurs
	 *         éventuelles ("" si pas d'erreur)
	 */
	public String ajouterGroupe(String nom, int nbColonnes, int indexColonne, int niveau, String typePPN, int id_departement, int id_promo) {
		String erreurInsertion = "";
		Statement requeteRecherche;
		if (getGroupes().contains(new Groupe(0, nom, "", nbColonnes, indexColonne, niveau, typePPN, id_departement, id_promo)))
			erreurInsertion+="Ce groupe existe déjà...\n";
		else {
			try {
				requeteRecherche = db.createStatement();
				int id_groupe=getIdGroupe();
				int nbModif = requeteRecherche.executeUpdate("INSERT INTO groupe VALUES(" + id_groupe + ",'" + nom + "',''," + nbColonnes + "," + indexColonne + "," + niveau + ",'" + typePPN + "',"+ id_departement + "," + id_promo + ")");
				if (nbModif != 1)
					erreurInsertion+="Problème d'insertion dans la base de donnée !\n";
				requeteRecherche.close();
			} catch (SQLException e) {
				e.printStackTrace();
				erreurInsertion+=e.toString()+"\n";
				Utilitaire.showMessageDialog("ControleurDB : ajouterGroupe()", erreurInsertion.toString());
			}
		}
		return erreurInsertion;
	}

	/**
	 * ajoute une promo dans la base de données
	 * 
	 * @return renvoie une chaine de caractères contenant les erreurs
	 *         éventuelles ("" si pas d'erreur)
	 */
	public String ajouterPromo(String promotion, int id_departement, boolean isVisible, int id_promo) {
		String erreurInsertion = "";
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			int nbModif = requeteRecherche.executeUpdate("INSERT INTO admin_promo VALUES(" + idAdmin + ",'" + id_promo + "')");
			if (nbModif != 1)
				erreurInsertion+="Problème d'insertion dans la table admin_promo !\n";
			nbModif = requeteRecherche.executeUpdate("INSERT INTO promo VALUES(" + id_promo + ",'" + promotion + "'," + id_departement + ",'" + (isVisible?'t':'f') + "')");
			if (nbModif != 1)
				erreurInsertion+="Problème d'insertion dans la table promo !\n";
			requeteRecherche.close();

		} catch (SQLException e) {
			e.printStackTrace();
			erreurInsertion+=e.toString()+"\n";
			Utilitaire.showMessageDialog("ControleurDB : ajouterPromo()", erreurInsertion.toString());
		}
		return erreurInsertion;
	}

	public Boolean effacerPromo(String promotion,int ar) {
		boolean date5ans=false;
		int idP = getIdPromotion(promotion);
		int nbModif;
		int date;
		String erreurInsertion = "";
		ArrayList<Groupe> g=getGroupesPromos(idP);
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			ResultSet r = requeteRecherche.executeQuery("select date_dbt from dispense inner join groupe using(id_groupe) inner join promo using(id_promo) where id_promo="+idP+" order by date_dbt desc");  
			if (r.next()) {
				GregorianCalendar dateDebut;
				SimpleTimeZone tz=new SimpleTimeZone(1*60*60*1000, "CET");
				tz.setStartRule(Calendar.MARCH, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
				tz.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
				dateDebut = new GregorianCalendar(tz);
				dateDebut.setTimeInMillis(r.getLong("date_dbt") * 1000);
				if((Calendar.getInstance().get(Calendar.YEAR)-dateDebut.get(Calendar.YEAR))>=ar) {
					date5ans=true;
				}
			}else {
				date5ans=true;
			}
			if(date5ans) {
				if(idP>0 && promotion!=null) {
					if(g!=null){
						for(Groupe g1 : g) {
							if(g1.getIdGroupe()>0) {
								nbModif = requeteRecherche.executeUpdate("DELETE from dispense where id_groupe="+g1.getIdGroupe());
								if (nbModif != 1)
									erreurInsertion+="Dispense Inexistante";

								nbModif = requeteRecherche.executeUpdate("DELETE from composition_groupe where id_groupe="+g1.getIdGroupe());
								if (nbModif != 1)
									erreurInsertion+="Composition groupe Inexistante";

								nbModif = requeteRecherche.executeUpdate("DELETE from admin_groupe where id_groupe="+g1.getIdGroupe());
								if (nbModif != 1)
									erreurInsertion+="Composition groupe Inexistante";
							}
						}
					}

					nbModif = requeteRecherche.executeUpdate("DELETE from groupe where id_promo="+ idP);
					if (nbModif != 1)
						erreurInsertion+="Groupes inexistants \n";

					nbModif = requeteRecherche.executeUpdate("DELETE FROM admin_promo WHERE id_promo="+ idP);
					if (nbModif != 1)
						erreurInsertion+="Responsables de promotion inexistants \n";

					nbModif = requeteRecherche.executeUpdate("DELETE from promo where promo='"+promotion+"'");
					if (nbModif != 1)
						erreurInsertion+="Promotion Inexistante";

					nbModif = requeteRecherche.executeUpdate("DELETE from promo_prof where id_promotion="+idP);
					if (nbModif != 1)
						erreurInsertion+="Promotion Prof Inexistante";

					nbModif = requeteRecherche.executeUpdate("DELETE from promo_matiere where id_promotion="+idP);
					if (nbModif != 1)
						erreurInsertion+="Promotion Prof Inexistante";
				}
				else {
					Utilitaire.showMessageDialog("Promo érronné :", "Des valeurs nulles ou limite d'années");
				}
			}
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			erreurInsertion+=e.toString()+"\n";
			Utilitaire.showMessageDialog("ControleurDB : effacerPromo()", erreurInsertion.toString());
		}
		return date5ans;
	}

	public Boolean effacerProf(int prof,int ar) {
		boolean date5ans=false; 
		int nbModif;
		int date;
		String erreurInsertion = "";
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			ResultSet r = requeteRecherche.executeQuery("select date_dbt from dispense where id_prof="+prof+" order by date_dbt desc");  
			if (r.next()) {
				GregorianCalendar dateDebut;
				SimpleTimeZone tz=new SimpleTimeZone(1*60*60*1000, "CET");
				tz.setStartRule(Calendar.MARCH, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
				tz.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
				dateDebut = new GregorianCalendar(tz);
				dateDebut.setTimeInMillis(r.getLong("date_dbt") * 1000);
				if((Calendar.getInstance().get(Calendar.YEAR)-dateDebut.get(Calendar.YEAR))>=ar) {
					date5ans=true;
				}
			}else {
				date5ans=true;
			}
			if(date5ans) {		
				nbModif = requeteRecherche.executeUpdate("DELETE from promo_prof where id_prof="+ prof);
				if (nbModif != 1)
					erreurInsertion+="Promo inexistante \n";

				nbModif = requeteRecherche.executeUpdate("DELETE FROM prof WHERE id_prof="+ prof);
				if (nbModif != 1)
					erreurInsertion+="Prof inexistant \n";

				nbModif = requeteRecherche.executeUpdate("DELETE FROM dispense WHERE id_prof="+ prof);
				if (nbModif != 1)
					erreurInsertion+="Cours inexistant \n";
			}		
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			erreurInsertion+=e.toString()+"\n";
			Utilitaire.showMessageDialog("ControleurDB : effacerProf()", erreurInsertion.toString());
		}
		return date5ans;
	}

	public Boolean effacerMatiere(int mat,int ar) {
		boolean date5ans=false; 
		int nbModif;
		int date;
		String erreurInsertion = "";
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			ResultSet r = requeteRecherche.executeQuery("select date_dbt from dispense where id_matiere="+mat+" order by date_dbt desc");  
			if (r.next()) {
				GregorianCalendar dateDebut;
				SimpleTimeZone tz=new SimpleTimeZone(1*60*60*1000, "CET");
				tz.setStartRule(Calendar.MARCH, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
				tz.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
				dateDebut = new GregorianCalendar(tz);
				dateDebut.setTimeInMillis(r.getLong("date_dbt") * 1000);
				if((Calendar.getInstance().get(Calendar.YEAR)-dateDebut.get(Calendar.YEAR))>=ar) {
					date5ans=true;
				}
			}else {
				date5ans=true;
			}
			if(date5ans) {		
				nbModif = requeteRecherche.executeUpdate("DELETE from promo_matiere where id_matiere="+ mat);
				if (nbModif != 1)
					erreurInsertion+="Promo inexistante \n";

				nbModif = requeteRecherche.executeUpdate("DELETE FROM matiere WHERE id_matiere="+ mat);
				if (nbModif != 1)
					erreurInsertion+="Matiere inexistante \n";

				nbModif = requeteRecherche.executeUpdate("DELETE FROM dispense WHERE id_matiere="+ mat);
				if (nbModif != 1)
					erreurInsertion+="Dispense inexistante \n";
			}		
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			erreurInsertion+=e.toString()+"\n";
			Utilitaire.showMessageDialog("ControleurDB : effacerProf()", erreurInsertion.toString());
		}
		return date5ans;
	}



	public String checkPromo(String promotion) {
		String erreurInsertion = "";
		Statement requeteRecherche;
		boolean cours=false;
		try {
			requeteRecherche = db.createStatement();
			ResultSet r = requeteRecherche.executeQuery("select * from dispense inner join groupe using(id_groupe) inner join promo using(id_promo) where promo='"+promotion+"'");
			if (r.next()) {
				cours=true;
			}
			r.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			erreurInsertion+=e.toString()+"\n";
			Utilitaire.showMessageDialog("ControleurDB : effacerPromo()", erreurInsertion.toString());
		}
		if(cours==true) {
			return erreurInsertion+= "Il existe des cours dans cette promotion !!! Confirmez-vous la suppression de "+promotion+" et tous ses groupes et cours ?";
		}else {
			return erreurInsertion+= "Il n'existe pas des cours dans cette promotion. Confirmez-vous la suppression de "+promotion+" et tous ses groupes ?";
		}
	}


	public String checkProf(int idprof) {
		String erreurInsertion = "";
		Statement requeteRecherche;
		boolean cours=false;
		try {
			requeteRecherche = db.createStatement();
			ResultSet r = requeteRecherche.executeQuery("select * from dispense where id_prof='"+idprof+"'");
			if (r.next()) {
				cours=true;
			}
			r.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			erreurInsertion+=e.toString()+"\n";
			Utilitaire.showMessageDialog("ControleurDB : effacerProf()", erreurInsertion.toString());
		}
		if(cours==true) {
			return erreurInsertion+= "Il existe des cours avec ce prof !!! Confirmez-vous la suppression de ce prof et tous ces cours ?";
		}else {
			return erreurInsertion+= "Il n'existe pas des cours avec ce prof. Confirmez-vous la suppression de ce prof?";
		}
	}

	public String checkMatiere(int id_matiere) {
		String erreurInsertion = "";
		Statement requeteRecherche;
		boolean cours=false;
		try {
			requeteRecherche = db.createStatement();
			ResultSet r = requeteRecherche.executeQuery("select * from dispense where id_matiere='"+id_matiere+"'");
			if (r.next()) {
				cours=true;
			}
			r.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			erreurInsertion+=e.toString()+"\n";
			Utilitaire.showMessageDialog("ControleurDB : effacerProf()", erreurInsertion.toString());
		}
		if(cours==true) {
			return erreurInsertion+= "Il existe des cours avec cette matière !!! Confirmez-vous la suppression de cette matière et tous ces cours ?";
		}else {
			return erreurInsertion+= "Il n'existe pas des cours avec cette matière. Confirmez-vous la suppression de cette matière ?";
		}
	}


	public TreeMap<String,Integer> promoMatieres(String promotion) {
		int idP = getIdPromotion(promotion);
		TreeMap<String,Integer> matieres = new TreeMap<String,Integer>();
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			ResultSet r = requeteRecherche.executeQuery("select distinct id_matiere,nom_matiere from dispense inner join matiere using(id_matiere) inner join groupe using(id_groupe) where id_promo="+idP+" order by nom_matiere"); 
			while(r.next()) {
				matieres.put(r.getString("nom_matiere"),r.getInt("id_matiere"));
			}
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : Infos Matieres", e.toString());
		}
		return matieres;
	}

	public String nbCoursT(String promotion) {
		int idP = getIdPromotion(promotion);
		String res="";
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			ResultSet r = requeteRecherche.executeQuery("select count(*) from dispense inner join groupe using(id_groupe) where id_promo="+idP);  
			if(r.next()) {
				res= Integer.toString(r.getInt("count"));
			}
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : Total Cours ", e.toString());
		}
		return res;
	}


	public HashMap<Integer,String[]> infosMatieres(TreeMap<String,Integer> matieres,String promotion) {
		HashMap<Integer,String[]> matieresInfos = new HashMap<Integer,String[]>();
		int idP = getIdPromotion(promotion);
		Statement requeteRecherche;
		try {
			// 0=nombre de cours,1=sales,2=profs,

			requeteRecherche = db.createStatement();
			ResultSet r = null;
			for(Integer idM : matieres.values()) {
				String[]infos   = new String[3];
				String nbCours = "";
				String profs   = "";
				String salles  = "";
				r = requeteRecherche.executeQuery("select count(*) from dispense inner join groupe using(id_groupe) where id_matiere="+idM+" and id_promo="+idP);  
				if(r.next()) {
					nbCours = Integer.toString(r.getInt("count"));
				}
				r = requeteRecherche.executeQuery("select distinct nom_salle from dispense inner join groupe using(id_groupe) inner join salle using(id_salle) where id_matiere="+idM+" and id_promo="+idP);
				boolean a = false;
				while(r.next()) {
					if(!r.getString("nom_salle").equals("SALLE VIDE") && !a) {
						salles += r.getString("nom_salle");
						a=true;
					}else if (a){
						if(!r.getString("nom_salle").equals("SALLE VIDE")) {
							salles += ", "+r.getString("nom_salle");
						}
					}	
				}
				r = requeteRecherche.executeQuery("select DISTINCT nom_prof,prenom_prof from dispense inner join groupe using(id_groupe) inner join prof using(id_prof) where id_matiere="+idM+" and id_promo="+idP);
				boolean b = false;
				while(r.next()) {
					if(!(r.getString("nom_prof")+"_"+r.getString("prenom_prof")).equals("NOM VIDE_PRENOM VIDE") && !b) {
						profs += r.getString("nom_prof")+" "+r.getString("prenom_prof");
						b=true;
					}else if (b){
						if(!(r.getString("nom_prof")+"_"+r.getString("prenom_prof")).equals("NOM VIDE_PRENOM VIDE"))
							profs +=", "+r.getString("nom_prof")+" "+r.getString("prenom_prof");
					}
				}
				if(nbCours!=null) {
					infos[0]=nbCours;
				}
				if(salles!=null) {
					infos[1]=salles;
				}
				if(profs!=null) {
					infos[2]=profs;
				}
				matieresInfos.put(idM, infos);
			}
			r.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : Infos Matieres", e.toString());
		}
		return matieresInfos;
	}

	public HashMap<Integer,String[]> infosMatieres2(int idM) {
		HashMap<Integer,String[]> matieresInfos = new HashMap<Integer,String[]>();
		Statement requeteRecherche;
		try {
			// 0=nombre de cours,1=sales,2=profs,

			requeteRecherche = db.createStatement();
			ResultSet r = null;
			String[]infos   = new String[3];
			String nbCours = "";
			String profs   = "";
			String promos  = "";
			r = requeteRecherche.executeQuery("select count(*) from dispense where id_matiere="+idM);  
			if(r.next()) {
				nbCours = Integer.toString(r.getInt("count"));
			}
			r = requeteRecherche.executeQuery("select distinct promo from dispense inner join groupe using(id_groupe) inner join promo using(id_promo) where id_matiere="+idM);
			boolean a = false;
			while(r.next()) {
				if(!r.getString("promo").equals("PROMO VIDE") && !a) {
					promos += r.getString("promo");
					a=true;
				}else if (a){
					if(!r.getString("promo").equals("PROMO VIDE VIDE")) {
						promos += ", "+r.getString("promo");
					}
				}	
			}
			r = requeteRecherche.executeQuery("select DISTINCT nom_prof,prenom_prof from dispense inner join prof using(id_prof) where id_matiere="+idM);
			boolean b = false;
			while(r.next()) {
				if(!(r.getString("nom_prof")+"_"+r.getString("prenom_prof")).equals("NOM VIDE_PRENOM VIDE") && !b) {
					profs += r.getString("nom_prof")+" "+r.getString("prenom_prof");
					b=true;
				}else if (b){
					if(!(r.getString("nom_prof")+"_"+r.getString("prenom_prof")).equals("NOM VIDE_PRENOM VIDE"))
						profs +=", "+r.getString("nom_prof")+" "+r.getString("prenom_prof");
				}
			}
			if(nbCours!=null) {
				infos[0]=nbCours;
			}
			if(profs!=null) {
				infos[1]=profs;
			}
			if(promos!=null) {
				infos[2]=promos;
			}
			matieresInfos.put(idM, infos);
			r.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : Infos Matieres", e.toString());
		}
		return matieresInfos;
	}


	public HashMap<Integer,String[]> infosMatiere(Integer idM,String promotion) {
		HashMap<Integer,String[]> matieresInfos = new HashMap<Integer,String[]>();
		int idP = getIdPromotion(promotion);
		Statement requeteRecherche;
		try {
			// 0=nombre de cours,1=sales,2=profs,
			requeteRecherche = db.createStatement();
			ResultSet r = null;
			String[]infos   = new String[3];
			String nbCours = "";
			String profs   = "";
			String salles  = "";
			r = requeteRecherche.executeQuery("select count(*) from dispense inner join groupe using(id_groupe) where id_matiere="+idM+" and id_promo="+idP);  
			if(r.next()) {
				nbCours = Integer.toString(r.getInt("count"));
			}
			r = requeteRecherche.executeQuery("select distinct nom_salle from dispense inner join groupe using(id_groupe) inner join salle using(id_salle) where id_matiere="+idM+" and id_promo="+idP);
			boolean a = false;
			while(r.next()) {
				if(!r.getString("nom_salle").equals("SALLE VIDE") && !a) {
					salles += r.getString("nom_salle");
					a=true;
				}else if (a){
					if(!r.getString("nom_salle").equals("SALLE VIDE")) {
						salles += ", "+r.getString("nom_salle");
					}
				}	
			}
			r = requeteRecherche.executeQuery("select DISTINCT nom_prof,prenom_prof from dispense inner join groupe using(id_groupe) inner join prof using(id_prof) where id_matiere="+idM+" and id_promo="+idP);
			boolean b = false;
			while(r.next()) {
				if(!(r.getString("nom_prof")+"_"+r.getString("prenom_prof")).equals("NOM VIDE_PRENOM VIDE") && !b) {
					profs += r.getString("nom_prof")+" "+r.getString("prenom_prof");
					b=true;
				}else if (b){
					if(!(r.getString("nom_prof")+"_"+r.getString("prenom_prof")).equals("NOM VIDE_PRENOM VIDE"))
						profs +=", "+r.getString("nom_prof")+" "+r.getString("prenom_prof");
				}
			}
			if(nbCours!=null) {
				infos[0]=nbCours;
			}
			if(salles!=null) {
				infos[1]=salles;
			}
			if(profs!=null) {
				infos[2]=profs;
			}
			matieresInfos.put(idM, infos);
			r.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : Infos Matieres", e.toString());
		}
		return matieresInfos;
	}

	public String datesCours(String promotion) {
		boolean date5ans=false;
		int idP = getIdPromotion(promotion);
		String dates="";
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			ResultSet r = requeteRecherche.executeQuery("select date_fin from dispense inner join groupe using(id_groupe) inner join promo using(id_promo) where id_promo="+idP+" order by date_fin desc");  
			if (r.next()) {
				GregorianCalendar dateFin;
				SimpleTimeZone tz=new SimpleTimeZone(1*60*60*1000, "CET");
				tz.setStartRule(Calendar.MARCH, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
				tz.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
				dateFin = new GregorianCalendar(tz);
				dateFin.setTimeInMillis(r.getLong("date_fin") * 1000);
				dates+= "Le dernier cours etait en "+dateFin.getWeekYear();
			}
			r = requeteRecherche.executeQuery("select date_dbt from dispense inner join groupe using(id_groupe) inner join promo using(id_promo) where id_promo="+idP+" order by date_dbt asc");  
			if (r.next()) {
				GregorianCalendar dateDebut;
				SimpleTimeZone tz=new SimpleTimeZone(1*60*60*1000, "CET");
				tz.setStartRule(Calendar.MARCH, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
				tz.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
				dateDebut = new GregorianCalendar(tz);
				dateDebut.setTimeInMillis(r.getLong("date_dbt") * 1000);
				dates+= " et le premier cours etait en "+dateDebut.getWeekYear();
			}

		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : Infos Profs", e.toString());
		}
		return dates;
	}

	public String datesCours(int idprof) {
		boolean date5ans=false;
		String dates="";
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			ResultSet r = requeteRecherche.executeQuery("select date_fin from dispense where id_prof="+idprof+" order by date_fin desc");  
			if (r.next()) {
				GregorianCalendar dateFin;
				SimpleTimeZone tz=new SimpleTimeZone(1*60*60*1000, "CET");
				tz.setStartRule(Calendar.MARCH, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
				tz.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
				dateFin = new GregorianCalendar(tz);
				dateFin.setTimeInMillis(r.getLong("date_fin") * 1000);
				dates+= "Son dernier cours etait en "+dateFin.getWeekYear();
			}
			r = requeteRecherche.executeQuery("select date_dbt from dispense where id_prof="+idprof+" order by date_dbt asc");  
			if (r.next()) {
				GregorianCalendar dateDebut;
				SimpleTimeZone tz=new SimpleTimeZone(1*60*60*1000, "CET");
				tz.setStartRule(Calendar.MARCH, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
				tz.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
				dateDebut = new GregorianCalendar(tz);
				dateDebut.setTimeInMillis(r.getLong("date_dbt") * 1000);
				dates+= " et le premier cours etait en "+dateDebut.getWeekYear();
			}

		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : Infos Profs", e.toString());
		}
		return dates;
	}

	public String datesMatieres(int idMatiere) {
		boolean date5ans=false;
		String dates="";
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			ResultSet r = requeteRecherche.executeQuery("select date_fin from dispense where id_matiere="+idMatiere+" order by date_fin desc");  
			if (r.next()) {
				GregorianCalendar dateFin;
				SimpleTimeZone tz=new SimpleTimeZone(1*60*60*1000, "CET");
				tz.setStartRule(Calendar.MARCH, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
				tz.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
				dateFin = new GregorianCalendar(tz);
				dateFin.setTimeInMillis(r.getLong("date_fin") * 1000);
				dates+= "Le dernier cours etait en "+dateFin.getWeekYear();
			}
			r = requeteRecherche.executeQuery("select date_dbt from dispense where id_matiere="+idMatiere+" order by date_dbt asc");  
			if (r.next()) {
				GregorianCalendar dateDebut;
				SimpleTimeZone tz=new SimpleTimeZone(1*60*60*1000, "CET");
				tz.setStartRule(Calendar.MARCH, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
				tz.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
				dateDebut = new GregorianCalendar(tz);
				dateDebut.setTimeInMillis(r.getLong("date_dbt") * 1000);
				dates+= " et le premier cours etait en "+dateDebut.getWeekYear();
			}

		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : Infos Matieres", e.toString());
		}
		return dates;
	}

	public TreeMap<String,Integer> promoProfs(String promotion) {
		int idP = getIdPromotion(promotion);
		TreeMap<String,Integer> profs = new TreeMap<String,Integer>();
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			ResultSet r = requeteRecherche.executeQuery(
					"select distinct id_prof,nom_prof,prenom_prof from "+
							"dispense inner join prof using(id_prof) "+
							"inner join groupe using(id_groupe) "+
							"where id_promo="+idP+" order by nom_prof"); 
			while(r.next()) {
				profs.put(r.getString("nom_prof")+" "+r.getString("prenom_prof"),r.getInt("id_prof"));
			}
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : Infos Profs", e.toString());
		}
		return profs;
	}

	public HashMap<Integer,String[]> infosProfs(TreeMap<String,Integer> profs) {
		HashMap<Integer,String[]> profsInfos = new HashMap<Integer,String[]>();
		Statement requeteRecherche;
		try {
			// 0=nombre de cours,1=sales,2=profs,
			requeteRecherche = db.createStatement();
			ResultSet r = null;
			for(Integer idP : profs.values()) {
				String[]infos   = new String[3];
				String nbCours = "";
				String promos   = "";
				String matieres  = "";
				r = requeteRecherche.executeQuery("select count(*) from dispense where id_prof="+idP);  
				if(r.next()) {
					nbCours = Integer.toString(r.getInt("count"));
				}
				r = requeteRecherche.executeQuery("select distinct promo from dispense inner join groupe using(id_groupe) inner join promo using(id_promo) where id_prof="+idP);
				boolean a = false;
				while(r.next()) {
					if(!r.getString("promo").equals("PROMO VIDE") && !a) {
						promos += r.getString("promo");
						a=true;
					}else if (a){
						if(!r.getString("promo").equals("PROMO VIDE")) {
							promos += ", "+r.getString("promo");
						}
					}	
				}
				r = requeteRecherche.executeQuery("select DISTINCT nom_matiere from dispense inner join matiere using(id_matiere) where id_prof="+idP);
				boolean b = false;
				while(r.next()) {
					if(!r.getString("nom_matiere").equals("MATIERE VIDE") && !b) {
						matieres += r.getString("nom_matiere");
						b=true;
					}else if (b){
						if(!r.getString("nom_matiere").equals("MATIERE VIDE"))
							matieres +=", "+r.getString("nom_matiere");
					}
				}
				if(nbCours!=null) {
					infos[0]=nbCours;
				}
				if(matieres!=null) {
					infos[1]=matieres;
				}
				if(promos!=null) {
					infos[2]=promos;
				}

				profsInfos.put(idP, infos);
			}
			r.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : Infos Profs", e.toString());
		}
		return profsInfos;
	}

	public HashMap<Integer,String[]> infosProf(Integer idP) {
		HashMap<Integer,String[]> profsInfos = new HashMap<Integer,String[]>();
		Statement requeteRecherche;
		try {
			// 0=nombre de cours,1=sales,2=profs,
			requeteRecherche = db.createStatement();
			ResultSet r = null;
			String[]infos   = new String[3];
			String nbCours = "";
			String promos   = "";
			String matieres  = "";
			r = requeteRecherche.executeQuery("select count(*) from dispense where id_prof="+idP);  
			if(r.next()) {
				nbCours = Integer.toString(r.getInt("count"));
			}
			r = requeteRecherche.executeQuery("select distinct promo from dispense inner join groupe using(id_groupe) inner join promo using(id_promo) where id_prof="+idP);
			boolean a = false;
			while(r.next()) {
				if(!r.getString("promo").equals("PROMO VIDE") && !a) {
					promos += r.getString("promo");
					a=true;
				}else if (a){
					if(!r.getString("promo").equals("PROMO VIDE")) {
						promos += ", "+r.getString("promo");
					}
				}	
			}
			r = requeteRecherche.executeQuery("select DISTINCT nom_matiere from dispense inner join matiere using(id_matiere) where id_prof="+idP);
			boolean b = false;
			while(r.next()) {
				if(!r.getString("nom_matiere").equals("MATIERE VIDE") && !b) {
					matieres += r.getString("nom_matiere");
					b=true;
				}else if (b){
					if(!r.getString("nom_matiere").equals("MATIERE VIDE"))
						matieres +=", "+r.getString("nom_matiere");
				}
			}
			if(nbCours!=null) {
				infos[0]=nbCours;
			}
			if(matieres!=null) {
				infos[1]=matieres;
			}
			if(promos!=null) {
				infos[2]=promos;
			}

			profsInfos.put(idP, infos);

			r.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : Infos Profs", e.toString());
		}
		return profsInfos;
	}


	/**
	 * ajoute une matière dans la base de données pour la promo indiquée
	 * 
	 * @return renvoie une chaine de caractères contenant les erreurs
	 *         éventuelles ("" si pas d'erreur)
	 */
	public String ajouterMatiere(WDialog dialog, String nom, String nom_compl, int rouge, int vert, int bleu, String nom_pole, int id_promo) {
		String erreurInsertion="";
		Statement requeteRecherche;
		if (getMatieresPromo(id_promo).contains(new Matiere(-1, nom, nom_compl, rouge, vert, bleu, nom_pole)))
			erreurInsertion+="Cette matière existe déja...\n";
		else {
			try {
				requeteRecherche = db.createStatement();
				int id_matiere=getIdMatiere();
				int nbModif = requeteRecherche.executeUpdate("INSERT INTO matiere VALUES(" + id_matiere + ",'" + nom + "','"+nom_compl+"'," + rouge + "," + vert + "," + bleu + ",'" + nom_pole + "')");
				if (nbModif != 1)
					erreurInsertion+="Erreur lors de l'insertion !\n";
				requeteRecherche.close();
				if (nbModif==1) {
					requeteRecherche = db.createStatement();
					nbModif = requeteRecherche.executeUpdate("INSERT INTO promo_matiere VALUES (0," + id_matiere + "," + id_promo + ")"); // le 0 pour rétro-compatibilité avec la colonne id_groupe qui ne sert plus
					if (nbModif <= 0)
						erreurInsertion+="Problème d'insertion dans la base de donnée !\n";
					requeteRecherche.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				erreurInsertion+=e.toString()+"\n";
				Utilitaire.showMessageDialog("ControleurDB : ajouterMatiere()", erreurInsertion.toString());
			}
		}
		return erreurInsertion;
	}

	/**
	 * ajoute un prof dans la base de données pour la promo sélectionnée (les décharges concernent l'année scolaire passée en paramètre)
	 */
	public void ajouterProf(int debutAnneeScolaire, String initiale, String prenom, String nom, int serviceMax, int decharge_projets, int decharge_stages, int decharge_apprentis, int decharge, String commentaire, boolean titulaire, String login, int id_promo) {
		Statement requeteRecherche;
		String erreurInsertion="";
		if (getListeInitialesProfs().contains(initiale))
			erreurInsertion+="Ce prof existe déja...\n";
		else {
			try {
				requeteRecherche = db.createStatement();
				int id_prof=getIdProf();
				String requete1 = "INSERT INTO prof VALUES (" + id_prof + ",'" + initiale + "','" + prenom + "','" + nom + "'," + getIdDepartement() + ",'" + login + "', 't')";
				int nbModif1 = requeteRecherche.executeUpdate(requete1);
				String requete2 = "INSERT INTO decharge VALUES (" + debutAnneeScolaire + "," + id_prof + "," + serviceMax + "," + decharge_projets+","+decharge_stages+","+decharge_apprentis+","+decharge+","+"'"+commentaire+"'";
				if (titulaire)
					requete2 += ",'t')";
				else
					requete2 += ",'f')";
				int nbModif2 = requeteRecherche.executeUpdate(requete2);
				if (nbModif1 != 1 || nbModif2 != 1)
					erreurInsertion+="Erreur lors de l'insertion !!!";
				requeteRecherche.close();
				if (nbModif1==1 && nbModif2==1) { // insertion correcte
					requeteRecherche = db.createStatement();
					nbModif1 = requeteRecherche.executeUpdate("INSERT INTO promo_prof VALUES (0,"+id_prof+","+id_promo+")"); // le 0 pour rétro-compatibilité avec la colonne id_groupe qui ne sert plus
					if (nbModif1 <= 0)
						erreurInsertion+="Problème d'insertion dans la base de donnée !\n";
					requeteRecherche.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				erreurInsertion+=e.toString()+"\n";
				Utilitaire.showMessageDialog("ControleurDB : ajouterProf()", erreurInsertion.toString());
			}
		}
	}



	/**
	 * ajoute un étudiant dans la base de données dans les groupes indiqués
	 * IL FAUT AVOIR VERIFIE PREALABLEMENT QUE CET ETUDIANT N'EXISTE PAS DEJA DANS CES GROUPES
	 */
	public void ajouterEtudiant(String prenom, String nom, String login, ArrayList<Groupe>listeGroupes) {
		Statement requeteRecherche;
		String erreurInsertion="";
		try {
			requeteRecherche = db.createStatement();
			int id_etudiant=getIdEtudiant();
			String requete = "INSERT INTO etudiant VALUES(" + id_etudiant + ",'" + prenom + "','" + nom + "','" + login + "')";
			int nbModif = requeteRecherche.executeUpdate(requete);
			if (nbModif != 1)
				erreurInsertion+="Erreur lors de l'insertion !!!";
			if (nbModif==1) {
				requeteRecherche = db.createStatement();
				for (Iterator<Groupe> iter = listeGroupes.iterator(); iter.hasNext();)
					requeteRecherche.executeUpdate("INSERT INTO composition_groupe VALUES("+iter.next().getIdGroupe()+","+id_etudiant+")");
			}
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			erreurInsertion+=e.toString()+"\n";
			Utilitaire.showMessageDialog("ControleurDB : ajouterEtudiant()", erreurInsertion.toString());
		}
	}


	public void ajouterEtudiant2(int idG, int idE) {
		Statement requeteRecherche;
		String erreurInsertion="";
		try {
			requeteRecherche = db.createStatement();
			requeteRecherche.executeUpdate("INSERT INTO composition_groupe VALUES("+idG+","+idE+")");
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			erreurInsertion+=e.toString()+"\n";
			Utilitaire.showMessageDialog("ControleurDB : ajouterEtudiant()", erreurInsertion.toString());
		}
	}


	/**
	 * ajoute un nouvel administrateur avec tous les groupes des promos indiqués
	 */
	public void ajouterAdmin(String nouvelAdmin, ArrayList<String>listePromos) {
		int id = 0;
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on récupère l'identifiant maximum
			ResultSet resultat = requeteRecherche.executeQuery("SELECT max(id_admin) FROM admin");
			resultat.next();
			id = resultat.getInt("max") + 1;
			resultat.close();
			requeteRecherche.executeUpdate("INSERT INTO admin VALUES (" + id + ",'" + nouvelAdmin + "')");
			for (Iterator<String> iter = listePromos.iterator(); iter.hasNext();)
				requeteRecherche.executeUpdate("INSERT INTO admin_promo VALUES (" + id + "," + getIdPromotion(iter.next()) + ")");
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : ajouterAdmin()", e.toString());
		}
	}

	public int getIdAdmin() {
		return idAdmin;
	}

	/**
	 * retourne l'id du département de l'administrateur actuellement connecté
	 */
	public int getIdDepartement() {
		Statement requeteRecherche;
		ArrayList<String> listeDepartements=new ArrayList<String>();
		ArrayList<Integer> listeIdDepartements=new ArrayList<Integer>();
		try {
			requeteRecherche = db.createStatement();
			// on récupère les départements dont l'administrateur s'est déjà occupé
			ResultSet resultat = requeteRecherche.executeQuery("SELECT DISTINCT departement.id_departement,nom_departement FROM promo,admin_promo,departement WHERE id_admin="+idAdmin+" AND admin_promo.id_promo=promo.id_promo AND promo.id_departement=departement.id_departement");
			while (resultat.next()) {
				listeDepartements.add(resultat.getString("nom_departement"));
				listeIdDepartements.add(resultat.getInt("id_departement"));
			}
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getIdDepartement()", e.toString());
		}
		//        if (listeDepartements.size()==1) {
		return listeIdDepartements.get(0);
		//        } else { // présentation d'une liste déroulante de départements

		//            return 0;
		//        }
	}

	public String getLoginAdmin() {
		String login="";
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on récupère le login de l'administrateur
			ResultSet resultat = requeteRecherche.executeQuery("SELECT login_admin FROM admin WHERE id_admin="+idAdmin);
			resultat.next();
			login = resultat.getString("login_admin");
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getLoginAdmin()", e.toString());
		}
		return login;
	}

	public boolean isAdmin() {
		return idAdmin == 0;
	}

	/**
	 * vérifie que la matière et le prof sont bien associés à la promo
	 */
	public void majPromoMatiereProf(int id_promo, int id_matiere, int id_prof) {
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			ResultSet resultat = requeteRecherche.executeQuery("SELECT * FROM promo_matiere WHERE id_promotion="+ id_promo +" AND id_matiere="+id_matiere);
			if (!resultat.next()) // la matière n'est pas associée à la promo
				requeteRecherche.executeUpdate("INSERT INTO promo_matiere VALUES (0,"+id_matiere+","+id_promo+")"); // le 0 pour rétro-compatibilité avec la colonne id_groupe qui ne sert plus
			resultat.close();
			resultat = requeteRecherche.executeQuery("SELECT * FROM promo_prof WHERE id_promotion="+ id_promo +" AND id_prof="+id_prof);
			if (!resultat.next()) // la matière n'est pas associée à la promo
				requeteRecherche.executeUpdate("INSERT INTO promo_prof VALUES (0,"+id_prof+","+id_promo+")"); // le 0 pour rétro-compatibilité avec la colonne id_groupe qui ne sert plus
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : majPromoMatiereProf()", e.toString());
		}
	}

	/**
	 * renvoie le nombre d'heures de cette matière avec ce groupe depuis le début de l'année jusqu'à cette plage
	 */
	public String getHistoriquePlageHoraire(PlageHoraire plage) {
		Groupe groupe=getGroupe(plage.getIdGroupe());
		int idMatiere=plage.getIdMatiere();
		long dateFin=plage.getDateFin().getTimeInMillis()/1000;
		//on cherche le début de l'année scolaire en cours
		GregorianCalendar debut_annee_scolaire=plage.getDateDebut();
		debut_annee_scolaire.set(Calendar.DAY_OF_MONTH,1); // on se place au 1er du mois en cours
		while (debut_annee_scolaire.get(Calendar.MONTH)!=Calendar.AUGUST) // on retourne en arrière jusqu'au 1er août précédant
			debut_annee_scolaire.add(Calendar.MONTH, -1);
		String histo="Total des heures en " + getMatiere(idMatiere) + " pour " + groupe + " : ";
		Statement requeteRecherche;
		int total=0;
		try {
			requeteRecherche = db.createStatement();
			ResultSet resultat = requeteRecherche.executeQuery("SELECT SUM(date_fin-date_dbt) AS total FROM dispense WHERE id_matiere=" + idMatiere + " AND id_groupe=" + groupe.getIdGroupe() + " AND date_dbt>=" + (debut_annee_scolaire.getTimeInMillis()/1000) + " AND date_fin<=" + dateFin);
			while (resultat.next())
				total = resultat.getInt("total");
			// fermeture du flux et de la requete
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getHistoriquePlageHoraire()", e.toString());
		}
		total/=60;
		int totalH=total/60;
		int totalM=total%60;
		String minutes="0"+totalM;
		minutes=minutes.substring(minutes.length()-2);
		return histo+totalH+"h"+minutes;
	}

	/**
	 * renvoie le nombre d'heures de ce groupe dans la semaine (ou null si plage n'est pas un TP)
	 */
	public String getNbHeuresHebdo(PlageHoraire plage) {
		if (plage!=null) {
			Groupe groupe=getGroupe(plage.getIdGroupe());
			if (groupe.isTP()) {
				int position=groupe.getIndexColonne();
				int taille=groupe.getNbColonnes();
				String promo=getPromo(groupe.getIdGroupe());
				//on cherche le début de la semaine en cours
				GregorianCalendar debut_semaine=plage.getDateDebut();
				while (debut_semaine.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY) // on retourne au dimanche précédent
					debut_semaine.add(Calendar.HOUR,-24);
				//on cherche la fin de la semaine en cours
				GregorianCalendar fin_semaine=plage.getDateFin();
				while (fin_semaine.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY) // on avance au dimanche suivant
					fin_semaine.add(Calendar.HOUR, 24);
				String histo="Total hebdomadaire (hors pauses) pour " + groupe + " : ";
				Statement requeteRecherche;
				int total=0;
				try {
					requeteRecherche = db.createStatement();
					ResultSet resultat = requeteRecherche.executeQuery("SELECT SUM(date_fin-date_dbt) AS total FROM dispense,groupe,promo WHERE dispense.id_groupe=groupe.id_groupe AND promo.id_promo=groupe.id_promo AND promo='" + promo + "' AND position_edt<=" + position + " AND position_edt+taille_edt>=" + (position+taille) + " AND date_dbt>=" + (debut_semaine.getTimeInMillis()/1000) + " AND date_fin<=" + (fin_semaine.getTimeInMillis()/1000));
					while (resultat.next())
						total = resultat.getInt("total");
					// fermeture du flux et de la requete
					resultat.close();
					requeteRecherche.close();
				} catch (SQLException e) {
					e.printStackTrace();
					Utilitaire.showMessageDialog("ControleurDB : getNbHeuresHebdo()", e.toString());
				}
				total/=60;
				int totalH=total/60;
				int totalM=total%60;
				String minutes="0"+totalM;
				minutes=minutes.substring(minutes.length()-2);
				return histo+totalH+"h"+minutes;
			}
		}
		return null;
	}

	// version 1.9 : optimisation pour l'accès à distance
	/**
	 * renvoie dans l'ordre nbColonnesVirtuelles, Salle, Groupe, Matiere, Prof, ProfQuiFaitAppel
	 */
	@SuppressWarnings("unchecked")
	public ArrayList getInformationsPourVue(String promotion, int id_salle, int id_groupe, int id_matiere, int id_prof, int id_profAppel) {
		ArrayList informations=new ArrayList();
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			// on récupère les informations
			ResultSet resultat = requeteRecherche.executeQuery("SELECT * FROM salle,groupe,matiere,prof WHERE id_salle="+id_salle+" and id_groupe="+id_groupe+" and id_matiere="+id_matiere+" and id_prof="+id_prof);
			if (resultat.next()) {
				Salle salle = new Salle(resultat.getInt("id_salle"), resultat.getString("nom_salle"), resultat.getInt("nb_places"), resultat.getInt("nb_ordinateurs"), resultat.getString("videoprojecteur").charAt(0)=='t',resultat.getString("ip_dbt"), resultat.getString("ip_fin"), resultat.getBoolean("acces_internet"));
				Groupe groupe = new Groupe(resultat.getInt("id_groupe"), resultat.getString("nom_groupe"), "", resultat.getInt("taille_edt"), resultat.getInt("position_edt"), resultat.getInt("niveau_edt"), resultat.getString("type_ppn"), resultat.getInt("id_departement"),resultat.getInt("id_promo"));
				Matiere matiere = new Matiere(resultat.getInt("id_matiere"), resultat.getString("nom_matiere"), resultat.getString("nom_compl_matiere"), resultat.getInt("r_matiere"), resultat.getInt("v_matiere"), resultat.getInt("b_matiere"), resultat.getString("pole"));
				Prof prof = new Prof(resultat.getInt("id_prof"), resultat.getString("initiale_prof"), resultat.getString("prenom_prof"), resultat.getString("nom_prof"), resultat.getString("login_prof"), true);
				if (matiere==null)
					matiere=getMatiere(0);
				if (prof==null)
					prof=getProf(0);
				informations.add(salle);
				informations.add(groupe);
				informations.add(matiere);
				informations.add(prof);
			}
			resultat = requeteRecherche.executeQuery("SELECT * FROM prof WHERE id_prof="+id_profAppel);
			if (resultat.next()) {
				Prof profAppel = new Prof(resultat.getInt("id_prof"), resultat.getString("initiale_prof"), resultat.getString("prenom_prof"), resultat.getString("nom_prof"), resultat.getString("login_prof"), true);
				if (profAppel==null)
					profAppel=getProf(0);
				informations.add(profAppel);
			}
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getInformationsPourVue()", e.toString());
		}
		return informations;
	}

	/**
	 * modifie le fait qu'une promo est visible ou non dans les listes déroulantes
	 */
	public void modifierVisibilitePromo(String nomPromo, boolean isVisible) {
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			requeteRecherche.executeUpdate("UPDATE promo SET visible='" + (isVisible==true?'t':'f') + "' WHERE promo='" + nomPromo + "'");
			/*
            ResultSet resultat = requeteRecherche.executeQuery("SELECT id_promo FROM promo WHERE promo='" + nomPromo + "'");
            if (resultat.next()) { // on a un résultat
                int id_promo = resultat.getInt("id_promo");
                requeteRecherche.executeUpdate("UPDATE promo SET visible='" + (isVisible==true?'t':'f') + "' WHERE id_promo=" + id_promo);
            }
			 */
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : modifierVisibilitePromo()", e.toString());
		}
	}

	/**
	 * récupère le fait qu'une promo est visible ou non dans les listes déroulantes
	 */
	public boolean getVisibilitePromo(String nomPromo) {
		Statement requeteRecherche;
		boolean visible=true;
		try {
			requeteRecherche = db.createStatement();
			ResultSet resultat = requeteRecherche.executeQuery("SELECT visible FROM promo WHERE promo='" + nomPromo + "'");
			if (resultat.next()) // on a un résultat
				visible = resultat.getBoolean("visible");
			requeteRecherche.close();
			return visible;
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getVisibilitePromo()", e.toString());
		}
		return true;
	}

	/**
	 * modifie le fait qu'on comptabilise les services enseignants ou pas pour une promo
	 */
	public void modifierComptabiliserServicesPromo(String nomPromo, boolean comptabiliser) {
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			requeteRecherche.executeUpdate("UPDATE promo SET comptabiliser_services='" + (comptabiliser==true?'t':'f') + "' WHERE promo='" + nomPromo + "'");
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : modifierComptabiliserServicesPromo()", e.toString());
		}
	}

	/**
	 * récupère le fait qu'on comptabilise ou non les services d'une promo
	 */
	public boolean getComptabiliserServicesPromo(String nomPromo) {
		Statement requeteRecherche;
		boolean compter=true;
		try {
			requeteRecherche = db.createStatement();
			ResultSet resultat = requeteRecherche.executeQuery("SELECT comptabiliser_services FROM promo WHERE promo='" + nomPromo + "'");
			if (resultat.next()) // on a un résultat
				compter = resultat.getBoolean("comptabiliser_services");
			requeteRecherche.close();
			return compter;
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getComptabiliserServicesPromo()", e.toString());
		}
		return true;
	}

	/**
	 * récupère le nom de la salle (éventuelle) du prof qui fait l'appel (ou renvoie "")
	 */
	public String getSalleAppel(int dateDebut, int dateFin, int idProfAppel) {
		String nomSalle = "";
		Statement requeteRecherche;
		try {
			requeteRecherche = db.createStatement();
			ResultSet resultat = requeteRecherche.executeQuery("SELECT dispense.id_salle,nom_salle FROM dispense,salle WHERE dispense.id_salle=salle.id_salle AND date_dbt=" + dateDebut + " AND date_fin=" + dateFin + " AND id_prof=" + idProfAppel);
			if (resultat.next()) { // on a un résultat
				if (resultat.getInt("id_salle")>0) // et une salle non vide
					nomSalle = resultat.getString("nom_salle");
			}
			// fermeture du flux et de la requete
			resultat.close();
			requeteRecherche.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Utilitaire.showMessageDialog("ControleurDB : getSalleAppel()", e.toString());
		}
		System.out.println(nomSalle);
		return nomSalle;
	}

}//2755,2843(+1ligne),2343(-bloc6lignes)