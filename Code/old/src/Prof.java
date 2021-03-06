package EDT;

//public class Prof implements Comparable {
public class Prof implements Comparable<Prof> {

	private int id;
	private String initiale;
	private String prenom;
	private String nom;
	private int serviceMax;
    private int decharge_projets;
    private int decharge_stages;
    private int decharge_apprentis;
    private int decharge;
	private String commentaire;
	private boolean titulaire;
    private String login;
    private boolean visible;
    
    public Prof(int _id, String _initiale, String _prenom, String _nom, String _login, boolean _visible) {
        id=_id;
        initiale= _initiale;
        prenom= _prenom;
        nom= _nom;
        login=_login;
        visible=_visible;
    }
	public Prof(int _id, String _initiale, String _prenom, String _nom, int _service, int _decharge_projets, int _decharge_stages, int _decharge_apprentis, int _decharge, String _commentaire, boolean _titulaire, String _login, boolean _visible) {
		id=_id;
		initiale= _initiale;
		prenom= _prenom;
		nom= _nom;
		serviceMax= _service;
        decharge_projets=_decharge_projets;
        decharge_stages=_decharge_stages;
        decharge_apprentis=_decharge_apprentis;
        decharge=_decharge;
		commentaire=_commentaire;
		titulaire= _titulaire;
        login=_login;
        visible=_visible;
	}
	public String getInitiale() {
		return initiale;
	}
	public String toString() {
		if (id==0) return "";
		else return nom + " " + prenom + " (" + initiale + ")";
	}
	/* public int compareTo(Object o) {
		int comp_nom=nom.compareTo(((Prof)o).nom);
        if (comp_nom==0)
            return prenom.compareTo(((Prof)o).prenom);
        else return comp_nom;
	} */
	public boolean equals(Object o) {
		return id==((Prof)o).id; //initiale.equals(((Prof)o).initiale);
	}
	public int getIdProf() {
		return id;
	}
	public String getPrenom() {
		return prenom;
	}
	public String getNom() {
		return nom;
	}
	public int getServiceMax() {
		return serviceMax;
	}
    public int getDechargeProjets() {
        return decharge_projets;
    }
    public int getDechargeStages() {
        return decharge_stages;
    }
    public int getDechargeApprentis() {
        return decharge_apprentis;
    }
    public int getDecharge() {
        return decharge;
    }
	public String getCommentaire() {
		return commentaire;
	}
	public boolean getTitulaire() {
		return titulaire;
	}
    public boolean getVisible() {
        return visible;
    }
    public String getLogin() {
        return login;
    }
	/**
	 * regle les infos d'un prof
	 */
	public void setInfos(String _initiale, String _prenom, String _nom, int _serviceMax, int _decharge_projets, int _decharge_stages, int _decharge_apprentis, int _decharge, String commentaire, boolean _titulaire, String _login, boolean _visible) {
		initiale=_initiale;
		prenom=_prenom;
		nom=_nom;
		serviceMax=_serviceMax;
        decharge_projets=_decharge_projets;
        decharge_stages=_decharge_stages;
        decharge_apprentis=_decharge_apprentis;
        decharge=_decharge;
        titulaire=_titulaire;
        login=_login;
        visible=_visible;
	}
    /**
     * @decharges : tableau contenant service_max ; decharges pour projets, stages, apprentis, autres ; commentaire ; titulaire (t ou f)
     */
    public void setDecharges(String[] decharges) {
        serviceMax=Integer.parseInt(decharges[0]);
        decharge_projets=Integer.parseInt(decharges[1]);
        decharge_stages=Integer.parseInt(decharges[2]);
        decharge_apprentis=Integer.parseInt(decharges[3]);
        decharge=Integer.parseInt(decharges[4]);
        commentaire=decharges[5];
        titulaire=decharges[6].equals("t");
    }
    public int compareTo(Prof o) {
		int comp_nom=nom.toUpperCase().compareTo(((Prof)o).nom.toUpperCase());
        if (comp_nom==0)
            return prenom.compareTo(((Prof)o).prenom);
        else return comp_nom;	
    }
}
