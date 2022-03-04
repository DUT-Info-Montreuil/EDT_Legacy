package EDT;

public class Matiere implements Comparable {
	private int id;
	private String nom;
	private String nom_compl;
	private int rouge;
	private int vert;
	private int bleu;
	private String pole;
	public Matiere(int _id, String _nom, String _nom_compl, int _rouge, int _vert, int _bleu, String _pole) {
		id= _id;
		nom= _nom;
		nom_compl= _nom_compl;
		rouge= _rouge;
		vert= _vert;
		bleu= _bleu;
		pole= _pole;
	}
	public int getRouge() {
		return rouge;
	}
	public int getVert() {
		return vert;
	}
	public int getBleu() {
		return bleu;
	}
	public String getPole() {
		return pole;
	}
	public String toString() {
        if (id==0) return "";
		return nom + " " + nom_compl; // + " (" + rouge + ", " + vert + ", " + bleu + ")";
	}
	public String getNom() {
		return nom;
	}
	public String getNomCompl() {
		return nom_compl;
	}
	public int compareTo(Object o) {
		return (nom+nom_compl).compareTo(((Matiere)o).nom+((Matiere)o).nom_compl);
	}
	public boolean equals(Object o) {
		return (nom+nom_compl).equals(((Matiere)o).nom+((Matiere)o).nom_compl); // id==((Matiere)o).id
	}
	public int getIdMatiere() {
		return id;
	}
	/**
	 * regle les infos d'une matiere
	 */
	public void setInfos(String _nom, String _nom_compl, int _rouge, int _vert, int _bleu, String _pole) {
		nom=_nom;
		nom_compl=_nom_compl;
		rouge=_rouge;
		vert=_vert;
		bleu=_bleu;
		pole=_pole;
	}
}
