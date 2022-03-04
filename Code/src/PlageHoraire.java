package EDT;

import java.util.GregorianCalendar;
/*
 * Created on 26 juin 2004
 *
 */
/**
 * @author fredphil
 *
 */
public class PlageHoraire {
	private int idPlage; // identifiant dans la base de données
	private GregorianCalendar dateDebut; // en secondes
	private GregorianCalendar dateFin; // en secondes
    private String memo; // mémo pour les administrateurs
	private int idSalle, idSalle2; // identifiant de la (ou des 2) salle(s)
	private int idGroupe; // identifiant du groupe
	private int idMatiere; // identifiant de la matière
	private int idProf, idProf2, idProf3, idProf4; // identifiant du (ou des 2, 3, ou 4) prof(s)
	private int idProfQuiFaitAppel; // l'id du prof qui fait l'appel pour cette plage horaire
	private boolean controle, internet; // est-ce un contrôle ? Y a-t-il accès à internet
	
	PlageHoraire(int _idPlage, GregorianCalendar _dateDebut, GregorianCalendar _dateFin, String _memo,
		int _idSalle, int _idGroupe, int _idMatiere, int _idProf, boolean _internet, int _idProfQuiFaitAppel) {
		idPlage=_idPlage;
		dateDebut = _dateDebut;
		dateFin = _dateFin;
        if (_memo==null) memo="";
        else memo=_memo;
		idSalle = _idSalle;
		idGroupe = _idGroupe;
		idMatiere = _idMatiere;
		idProf = _idProf;
		internet=_internet;
		controle=false;
		idSalle2 = 0;
		idProf2=0;
		idProf3=0;
		idProf4=0;
		idProfQuiFaitAppel=_idProfQuiFaitAppel;
	}
	PlageHoraire(int _idPlage, GregorianCalendar _dateDebut, GregorianCalendar _dateFin, String _memo,
		int _idSalle, int _idGroupe, int _idMatiere, int _idProf, boolean _internet, boolean _controle,
		int _idSalle2, int _idProf2, int _idProf3, int _idProf4, int _idProfQuiFaitAppel) {
		idPlage=_idPlage;
		dateDebut = _dateDebut;
		dateFin = _dateFin;
        if (_memo==null) memo="";
        else memo=_memo;
		controle=_controle;
		internet=_internet;
		idSalle = _idSalle;
		idSalle2 = _idSalle2;
		idGroupe = _idGroupe;
		idMatiere = _idMatiere;
		idProf = _idProf;
		idProf2 = _idProf2;
		idProf3 = _idProf3;
		idProf4 = _idProf4;
		idProfQuiFaitAppel=_idProfQuiFaitAppel;
	}
	public int getIdPlage() {
		return idPlage;
	}
	public GregorianCalendar getDateDebut() {
		return dateDebut;
	}
	public GregorianCalendar getDateFin() {
		return dateFin;
	}
	public String getMemo() {
        return memo;
    }
    public boolean accesInternet() {
	    return internet;
	}
	public boolean IsControle() {
		return controle;
	}
	public int getIdSalle() {
		return idSalle;
	}
	public int getIdSalle2() {
		return idSalle2;
	}
	public int getIdGroupe() {
		return idGroupe;
	}
	public int getIdMatiere() {
		return idMatiere;
	}
	public int getIdProf() {
		return idProf;
	}
	public int getIdProf2() {
		return idProf2;
	}
	public int getIdProf3() {
		return idProf3;
	}
	public int getIdProf4() {
		return idProf4;
	}
	public int getIdProfQuiFaitAppel() {
		return idProfQuiFaitAppel;
	}
	/*
	 * modifie la date de début (et aussi la date de fin pour conserver la même durée)
	 */
	public void changerDateDebut(GregorianCalendar cal) {
		long duree=dateFin.getTimeInMillis()-dateDebut.getTimeInMillis();
		dateDebut=cal;
		dateFin.setTimeInMillis(dateDebut.getTimeInMillis()+duree);
	}
	public String toString() {
		String s="id : " + idPlage;
//		s+=dateDebut.getTimeInMillis();
		s+= " (" + Utilitaire.afficherDate(dateDebut, dateFin) + ")";
		s+=" controle=" + controle + " salle : " + idSalle + " salle2=" + idSalle2 + " groupe : " + idGroupe + " matière : " + idMatiere + " prof : " + idProf + " prof2 : " + idProf2 + " prof3 : " + idProf3 + " prof4 : " + idProf4 + " idProfQuiFaitAppel : " + idProfQuiFaitAppel; 
		return s;
	}
	public boolean equals(Object o) {
		PlageHoraire p=(PlageHoraire)o;
//System.out.println("ON TESTE L'EGALITE : " + this + " et " + p);
//System.out.println("" + (p.dateDebut.equals(dateDebut)) + (p.dateFin.equals(dateFin)) + (p.idGroupe==idGroupe) + (p.idMatiere==idMatiere) + (p.idProf==idProf) + (p.idSalle==idSalle));
		return p.idPlage==idPlage; // (p.dateDebut.equals(dateDebut) && p.dateFin.equals(dateFin) && p.idGroupe==idGroupe && p.idMatiere==idMatiere && p.idProf==idProf && p.idSalle==idSalle);
	}
}
