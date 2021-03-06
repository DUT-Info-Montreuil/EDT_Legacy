package EDT;

public class Groupe implements Comparable {
    private int id;
	private String nom;
	private String promotionINUTILE;
	private int nbColonnes;
	private int indexColonne;
	private int niveau;
    private String type_ppn;
    private int id_departement;
    private int id_promo;
    public Groupe(int _id, String _nom, String _promotion, int _nbColonnes, int _indexColonne, int _niveau, String _type_ppn, int id_departement, int id_promo) {
		id=_id;
		nom = _nom;
		promotionINUTILE = _promotion;
		nbColonnes = _nbColonnes;
		indexColonne = _indexColonne;
		niveau = _niveau;
        type_ppn=_type_ppn;
        this.id_departement=id_departement;
        this.id_promo=id_promo;
	}
	public int getIndexColonne() {
		return indexColonne;
	}
	public int getNbColonnes() {
		return nbColonnes;
	}
    public int getIdPromo() {
        return id_promo;
    }
	public String getNom() {
		return nom;
	}
	public String getPromoINUTILE() {
		return promotionINUTILE;
	}
	public String toString() {
		return nom + " " + promotionINUTILE; // + " (" + niveau + ")";
	}
	public int compareTo(Object o) {
		int diffPromo=id_promo-((Groupe)o).id_promo;
		if (diffPromo!=0) return diffPromo;
		else {
			int diffNiv=niveau-((Groupe)o).niveau;
			if (diffNiv!=0) return diffNiv;
			else {
				int diffIndexCol=indexColonne-((Groupe)o).indexColonne;
                if (diffIndexCol!=0) return diffIndexCol;
                else return id-((Groupe)o).id;
			}
		}
	}
	public boolean equals(Object o) {
		return id==((Groupe)o).id;
		//return promotion.equals(g.promotion) && niveau==g.niveau && indexColonne==g.indexColonne;  //id==((Groupe)o).id; // nom.equals(((Groupe)o).nom);
	}
	public int getIdGroupe() {
		return id;
	}
    public String getType() {
        return type_ppn;
    }
    /**
     * utilise pour calculer le nombre d'heures hebdo : renvoie vrai si c'est un "vrai" TP (minimal)
     */
    public boolean isTP() {
        return niveau==2;
    }
    public int getIdDepartement() {
        return id_departement;
    }
}
