package EDT;

public class Etudiant implements Comparable {
    private int id_etudiant;
    private String prenom, nom, login;

    Etudiant(int id, String p, String n, String l) {
        id_etudiant = id;
        prenom = p;
        nom = n;
        login = l;
    }
    public String toString() {
        if (id_etudiant == 0)
            return "";
        else
            return nom + " " + prenom;
    }
    public int compareTo(Object o) {
        int comp_nom = nom.compareTo(((Etudiant) o).nom);
        if (comp_nom == 0) {
            int comp_prenom=prenom.compareTo(((Etudiant) o).prenom);
            if (comp_prenom==0)
                return id_etudiant-id_etudiant;
            else return comp_prenom;
        }
        else
            return comp_nom;
    }
    public boolean equals(Object o) {
        return id_etudiant == ((Etudiant) o).id_etudiant;
    }
    public int getIdEtudiant() {
        return id_etudiant;
    }
    public String getPrenom() {
        return prenom;
    }
    public String getNom() {
        return nom;
    }
    public String getLogin() {
        return login;
    }
    /**
     * regle les infos d'un etudiant
     */
    public void setInfos(String _prenom, String _nom, String _login) {
        prenom=_prenom;
        nom=_nom;
        login=_login;
    }
}
