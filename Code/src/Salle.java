package EDT;

public class Salle implements Comparable<Salle> {
	private int id;
	private String nom;
	private String ipDbt;
	private String ipFin;
	private int nb_places;
	private int nb_ordinateurs;
	private boolean videoprojecteur;
	private boolean internet;

	/*public Salle(int _id, String _nom) {
		id= _id;
		nom= _nom;
	}*/
    public Salle(int _id, String _nom, int _nb_places, int _nb_ordinateurs, boolean _videoprojecteur,String ipDbt,String ipFin,boolean internet) {
        id= _id;
        nom= _nom;
        nb_places=_nb_places;
        nb_ordinateurs=_nb_ordinateurs;
        videoprojecteur=_videoprojecteur;
        this.ipDbt=ipDbt;
        this.ipFin=ipFin;
        this.internet=internet;
    }
	public String getNom() {
		return nom;
	}
	public String toString() {
		if (id==0) return "";
		else {
		    String resultat=nom;
		    if (nb_places>0) resultat+=" " + nb_places + "pl.";
            if (videoprojecteur) resultat+=" Vidéo";
            if (nb_ordinateurs>0) resultat+=" " + nb_ordinateurs + "PC";		    
		    return resultat;
		}
	}
	
	public boolean equals(Object o) {
		return id==((Salle)o).id; // || nom.equals(((Salle)o).nom);
	}
	public int getIdSalle() {
		return id;
	}
	public void setNom(String nouveauNom) {
		nom=nouveauNom;
	}
	public int getNbPlaces() {
		return nb_places;
	}
	public void setNbPlaces(int nbPlaces) {
		nb_places = nbPlaces;
	}
	public int getNbOrdinateurs() {
		return nb_ordinateurs;
	}
	public void setNbOrdinateurs(int nbOrdinateurs) {
		nb_ordinateurs = nbOrdinateurs;
	}
	public boolean getVideoprojecteur() {
		return videoprojecteur;
	}
	public void setVideoprojecteur(boolean videoprojecteur) {
		this.videoprojecteur = videoprojecteur;
	}
	public boolean isInternet() {
		return internet;
	}
	public void setInternet(boolean internet) {
		this.internet = internet;
	}
	public String getIpFin() {
		return ipFin;
	}
	public void setIpFin(String ipFin) {
		this.ipFin = ipFin;
	}
	public String getIpDbt() {
		return ipDbt;
	}
	public void setIpDbt(String ipDbt) {
		this.ipDbt = ipDbt;
	}
	@Override
	public int compareTo(Salle o) {
		return nom.compareTo(((Salle)o).nom);

	}
}
