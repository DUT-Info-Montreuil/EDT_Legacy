package EDT;

import eu.webtoolkit.jwt.WEnvironment;
import eu.webtoolkit.jwt.WApplication;

import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WCssDecorationStyle;
import eu.webtoolkit.jwt.WColor;
import eu.webtoolkit.jwt.WPaintedWidget;
import eu.webtoolkit.jwt.WPaintDevice;
import eu.webtoolkit.jwt.utils.WebGraphics2D;
import eu.webtoolkit.jwt.WPainter;
import eu.webtoolkit.jwt.WLength;
import eu.webtoolkit.jwt.WFont;
import eu.webtoolkit.jwt.Side;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import javax.swing.*;

//import javafx.application.Application;
//import javafx.scene.Scene;
//import javafx.scene.control.Label;
//import javafx.stage.Stage;

//import javafx.embed.swing.JFXPanel;

/*
 * Created on 26 juin 2004
 */
/**
 * @author fredphil
 * Visualisation de l'EDT dans un JPanel
 */
/*public class VueEDT extends JPanel {*/
public class VueEDT_wt extends WPaintedWidget {
	
	
	private WebGraphics2D g;
	
	private EDTApplication_wt edtAppli; // l'application principale
	private EDTSemaine edtSemaine; // la liste des cours de la semaine
	// calcul de la largeur d'une colonne et hauteur d'une ligne
	private int largeurJour, largeurPlageTP;
	private float hauteurLigne;
	// récupère hauteur et largeur de la fenêtre
	private int hauteurVue, largeurVue;
	private int largeurHeures;
	private int nbTP;
	private int nbColonnesVirtuelles;
	private Font font_jours;
	private Font font_heures;
	private Font font_cours;
	
    //int screenWidth = (int) Screen.getPrimary().getBounds().getWidth();
    //int screenHeight = (int) Screen.getPrimary().getBounds().getHeight();

	VueEDT_wt(EDTApplication_wt appli) {
		edtAppli = appli;
		hauteurVue = 600;	//320; // par défaut
		largeurVue = 800;	//200; // par défaut
		
		WEnvironment env = WApplication.getInstance().getEnvironment();
			//double hauteurPanneauHaut = appli.getPanneauHaut().getHeight().toPixels();
		
		//double hauteurContainer = appli.getContainer().getHeight().toPixels();
			//Utilitaire.showMessageDialog("hauteurRoot:height", "" + (int)this.edtAppli.getRoot().getHeight().toPixels());
		hauteurVue = env.getScreenHeight() - 200;	
			//(int)this.edtAppli.getRoot().getHeight().toPixels()*1;		//220; 
			//(int)appli.getPanneauHaut().getHeight().toPixels();
			//Utilitaire.showMessageDialog("paneauHaut:height", "" + hauteurVue);
			//Utilitaire.showMessageDialog("hauteur this", "this=" + (int) this.getHeight().toPixels());
			//Utilitaire.showMessageDialog("hauteur container", "container=" + edtAppli.getContainer().getHeight().toString());		
		
		largeurVue = env.getScreenWidth() ;
		//Utilitaire.showMessageDialog("Dimension fenetre login", "hauteurvue =" + hauteurVue +", largeurvue =" +largeurVue);
	
		//this.resize(largeurVue, hauteurVue);
			//Utilitaire.showMessageDialog("this","haut="+this.getVerticalAlignmentLength().toPixels());
			//Utilitaire.showMessageDialog("this","top="+env.getScreenTop());
			//Utilitaire.showMessageDialog("this","haut="+this.getVerticalAlignmentLength().toPixels());
		//Utilitaire.showMessageDialog("Dimension fenetre login", "hauteurvue =" + height +", largeurvue =" +width);		
		this.resize(new WLength(largeurVue), new WLength(hauteurVue));
		
		this.setMargin(0);
		
		//edtAppli.getRoot().addWidget(this);
		//Utilitaire.showMessageDialog("this","Valeur de login dans vueEDT: "+edtAppli.getLoginEDT());
		nbTP = 1; // nombre de groupes de TP par défaut
		nbColonnesVirtuelles = 1; // nombre de colonnes virtuelles par défaut
		largeurHeures = 50; // les 2 colonnes d'heures
		try {
			InputStream is=(this.getClass().getResource("../comic.ttf")).openStream();
			Font fonte=Font.createFont(Font.TRUETYPE_FONT, is);
			font_jours=fonte.deriveFont(Font.BOLD,16.0f);
			font_heures=fonte.deriveFont(Font.BOLD,12.0f);
			font_cours=fonte.deriveFont(Font.BOLD,11.0f);
			is.close();
		} catch (FontFormatException e) {
			e.printStackTrace();
			font_jours = new Font("Serif", Font.BOLD, 16);
			font_heures = new Font("Serif", Font.BOLD, 12);
			font_cours = new Font("Serif", Font.BOLD, 11);
		} catch (IOException e) {
			e.printStackTrace();
		}	
		edtSemaine = new EDTSemaine(edtAppli);		
	}
	
	public void dessinerGrille(WPainter painter, Graphics g, boolean promoVisible) {
		// récupère hauteur et largeur de la fenêtre
		hauteurVue = Math.max(hauteurVue,(int)getHeight().toPixels());	//(320,
		largeurVue = Math.max(largeurVue,(int)getWidth().toPixels());	//(200,
		//hauteurVue = 600;	//(320,
		//largeurVue = 800;	//(200,
		// on récupère le nombre de groupes de TP dans la promo
		nbTP = edtAppli.getControleurDb().getNbTP((String) (edtAppli.getListePromotions().getValueText()));//SelectedItem()));
		// calcul de la largeur d'une colonne et hauteur d'une ligne
		largeurJour = (int) ((largeurVue - 2 * largeurHeures) / (float)5); // 5 jours + 2 colonnes d'heures
		hauteurLigne = hauteurVue / (float)22; // 21 lignes + ligne des jours
		//Utilitaire.showMessageDialog("Dimension dessineGrille", "hauteurvue =" + hauteurVue +", largeurvue =" +largeurVue);
		if (nbTP!=0) {
			largeurPlageTP = (int) (largeurJour / (float)nbTP);
			largeurJour=largeurPlageTP*nbTP; // pour que ça tombre juste à l'affichage
		}
		else
			largeurPlageTP=largeurJour;
		// on dessine le quadrillage
		//System.out.println("hauteurVue="+hauteurVue + " hauteurLigne="+hauteurLigne);
		traitGrasVertical(g, 0, (int)hauteurLigne, (int) (hauteurVue - hauteurLigne));
		traitGrasVertical(g, (int)largeurVue, (int)hauteurLigne, (int) (hauteurVue - hauteurLigne));
		// traçage des lignes horizontales		
		for (int ligne = 2; ligne < 22; ligne++) {
			if (ligne==11) {
				g.setColor(Color.BLACK);
				traitGrasHorizontal(g, largeurHeures, (int)(hauteurLigne * ligne), 5 * largeurJour); 
			} else if (ligne%2 == 0) {
				g.setColor(Color.lightGray);
				traitFinHorizontal(g, largeurHeures, (int)(hauteurLigne * ligne), 5 * largeurJour); 
			} else {
				g.setColor(Color.gray);
				traitHorizontal(g, largeurHeures, (int)(hauteurLigne * ligne), 5 * largeurJour);
			}
		}
		g.setColor(Color.BLACK);
		traitGrasHorizontal(g, 0, (int)hauteurVue-3, (int)largeurVue);	//derniere ligne grise en noir		
		
		for (int jour = 0; jour <= 5; jour++) {
			// tracage des lignes en gras indiquant les jours
			traitGrasVertical(g, (int) (largeurHeures + largeurJour * jour), 0, (int)hauteurVue);
			// tracage des lignes fines indiquant les cours (tp,td,cours...)
			for (int col = 1; col < nbTP && jour < 5; col++) {
				//System.out.println("nbTP="+nbTP+" x="+(int)(largeurHeures+largeurJour*jour+largeurPlageTP*col));
				traitFinVertical(g, (int) (largeurHeures + largeurJour * jour + largeurPlageTP * col), (int)hauteurLigne, (int) (hauteurVue - hauteurLigne));
			}
		}
		traitGrasHorizontal(g, (int)largeurHeures, 0, (int) (5 * largeurJour));
		traitGrasHorizontal(g, 0, (int)hauteurLigne, (int)largeurVue);		
		traitGrasHorizontal(g, 0, (int)hauteurVue, (int)largeurVue);
		//traitGrasHorizontal(g, 0, (int)(hauteurLigne * 22), (int)largeurVue);		
				
		
		// tracage des points blancs toutes les heures
		g.setColor(Color.WHITE);
		for (int ligne = 2; ligne < 22; ligne++) {
			for (int jour=1 ; jour<5 ; jour++)
				traitGrasHorizontal(g, largeurHeures+jour*largeurJour-1, (int)(hauteurLigne * ligne), 3);
		}
		
		g.setColor(Color.BLACK);
		// changement de la police pour les jours
		g.setFont(font_jours);
		// affichage des jours
		GregorianCalendar cal=Utilitaire.calculerDate((String)edtAppli.getListeSemaines().getValueText());//SelectedItem());
		int numeroJour=cal.get(Calendar.DAY_OF_MONTH);
		afficherChaine(painter, g, "Lundi "+numeroJour, largeurHeures-50, 0, largeurJour, (int)hauteurLigne,'c', promoVisible);
		cal.add(Calendar.DAY_OF_MONTH,1); // on avance d'un jour
		numeroJour=cal.get(Calendar.DAY_OF_MONTH);
		afficherChaine(painter, g, "Mardi "+numeroJour, largeurHeures + largeurJour, 0, largeurJour, (int)hauteurLigne,'c', promoVisible);
		cal.add(Calendar.DAY_OF_MONTH,1); // on avance d'un jour
		numeroJour=cal.get(Calendar.DAY_OF_MONTH);
		afficherChaine(painter, g, "Mercredi "+numeroJour, largeurHeures + 2 * largeurJour, 0, largeurJour, (int)hauteurLigne,'c', promoVisible);
		cal.add(Calendar.DAY_OF_MONTH,1); // on avance d'un jour
		numeroJour=cal.get(Calendar.DAY_OF_MONTH);
		afficherChaine(painter, g, "Jeudi "+numeroJour, largeurHeures + 3 * largeurJour, 0, largeurJour, (int)hauteurLigne,'c', promoVisible);
		cal.add(Calendar.DAY_OF_MONTH,1); // on avance d'un jour
		numeroJour=cal.get(Calendar.DAY_OF_MONTH);
		afficherChaine(painter, g, "Vendredi "+numeroJour, largeurHeures + 4 * largeurJour, 0, largeurJour, (int)hauteurLigne,'c', promoVisible);
		// changement de la police pour les heures
		g.setFont(font_heures);
		// affichage des heures
		String hm;
		for (int heure = 8; heure <= 17; heure++) {
			hm=""+heure+"h30";
			g.setFont(new Font("Serif", Font.PLAIN, 10));
			afficherChaine(painter, g, hm, 0, (int)(hauteurLigne*((heure-7)*2-0.5)), largeurHeures, (int)hauteurLigne,'d', promoVisible);
			afficherChaine(painter, g, hm, largeurHeures+5*largeurJour, (int)(hauteurLigne*((heure-7)*2-0.5)), largeurHeures, (int)hauteurLigne,'g', promoVisible);
			hm=""+(heure+1)+"h";
			if ((heure+1)==13)
				g.setFont(new Font("Serif", Font.BOLD, 16));
			else
				g.setFont(new Font("Serif", Font.BOLD, 14));
			afficherChaine(painter, g, hm, 0, (int)(hauteurLigne*((heure-7)*2+0.5)), largeurHeures, (int)hauteurLigne,'d', promoVisible);
			afficherChaine(painter, g, hm, largeurHeures+5*largeurJour, (int)(hauteurLigne*((heure-7)*2+0.5)), largeurHeures, (int)hauteurLigne,'g', promoVisible);
		}
	}
	
	/*	
	public void paint(Graphics g) {
        super.paint(g);*/
	public void paintEvent(WPaintDevice paintDevice) {
		WPainter painter = new WPainter(paintDevice);
		g = new WebGraphics2D(painter);
		g.setColor(Color.black);
		//Utilitaire.showMessageDialog("hauteur paintDevice", ""+((int)paintDevice.getHeight().toPixels()));
		//Utilitaire.showMessageDialog("largeur paintDevice", ""+((int)paintDevice.getWidth().toPixels()));		
        
		// on met à jour la liste des cours de la semaine
		//edtSemaine.getListePlages(); // = new EDTSemaine(edtAppli.getDb(), (String) (edtAppli.getListeSemaines().getSelectedItem()), (String) (edtAppli.getListePromotions().getSelectedItem()));
		/*setBackground(Color.WHITE);*/
		WCssDecorationStyle cds = new WCssDecorationStyle();
		cds.setBackgroundColor(WColor.white);
		setDecorationStyle(cds);
		
		//String semaine = (String) (edtAppli.getListeSemaines().getSelectedItem());
//System.out.println("promo="+promo+" semaine="+semaine);
		dessinerGrille(painter, g, edtAppli.getControleurDb().getVisibilitePromo(((String)edtAppli.getListePromotions().getValueText())));//SelectedItem())));
//System.out.println("nbTP="+nbTP+" nbColVirt="+nbColonnesVirtuelles);
		// variables pour les différentes données
		int idGroupe, idMatiere, idProf, idProfAppel, idSalle, idSalle2=0; //, largeurChaine, hauteurChaine;
		GregorianCalendar dateDebut, dateFin;
        String memo;
		Groupe groupe;
		Matiere matiere;
		Prof prof, profAppel;
		Salle salle=null, salle2=null;
		//String affichage;
		// on récupère les plages horaires de la semaine et les jours fériés
		Vector<PlageHoraire> listeJoursFeries=edtSemaine.getListeJoursFeries();
		Vector<PlageHoraire> listePlages = edtSemaine.getListePlages();
//System.out.println(listeJoursFeries + "\n" + listePlages);
		// on ajuste la "taille" du jour férié à celle de la promo pour l'affichage
        for (Iterator<PlageHoraire> iter = listeJoursFeries.iterator(); iter.hasNext();) {
			if (listePlages==null) listePlages=new Vector<PlageHoraire>();
			listePlages.add(iter.next());
		}
//if (listePlages!=null) System.out.println("taille dans paint : " + listePlages.size());
		if (listePlages != null) { // s'il y en a à afficher
//System.out.println("BOUCLE0 " + new GregorianCalendar().get(Calendar.SECOND) + ":" + new GregorianCalendar().get(Calendar.MILLISECOND));
            nbColonnesVirtuelles = edtAppli.getControleurDb().getNbColonnesVirtuelles((String)edtAppli.getListePromotions().getValueText());//SelectedItem());
			for (Iterator<PlageHoraire> i = listePlages.iterator(); i.hasNext(); ) {
				// récupération des données
				PlageHoraire plage = (PlageHoraire)i.next();
//if (plage.getDateDebut().getTimeInMillis()<1317027000000L)
//	System.out.println("plage: " + plage);
				dateDebut = plage.getDateDebut();
				dateFin = plage.getDateFin();
                memo=plage.getMemo();
				idSalle = plage.getIdSalle();
				idGroupe = plage.getIdGroupe();
				idMatiere = plage.getIdMatiere();
				idProf = plage.getIdProf();
				idProfAppel = plage.getIdProfQuiFaitAppel();
/* APRES OPTIMISATION */
//System.out.println("BOUCLE1 " + new GregorianCalendar().get(Calendar.SECOND) + ":" + new GregorianCalendar().get(Calendar.MILLISECOND));
                // on récupère les informations sur la plage horaire  à  afficher
                ArrayList informations=edtAppli.getControleurDb().getInformationsPourVue((String)(edtAppli.getListePromotions().getValueText()),idSalle, idGroupe, idMatiere, idProf, idProfAppel);//SelectedItem()),idSalle, idGroupe, idMatiere, idProf, idProfAppel);
//System.out.println("BOUCLE2 " + new GregorianCalendar().get(Calendar.SECOND) + ":" + new GregorianCalendar().get(Calendar.MILLISECOND));
                salle = (Salle)informations.get(0);
                // si on a un contrôle on cherche une éventuelle deuxième salle
                if (plage.IsControle()) {
                    idSalle2=plage.getIdSalle2();
                    if (idSalle2>0)
                        salle2 = edtAppli.getControleurDb().getSalle(idSalle2);
                }
                else idSalle2=0;
                if (idProf==0 && idProfAppel!=0) { // on cherche la salle du prof qui fait l'appel
                    String nomSalleDuProfQuiFaitAppel=edtAppli.getControleurDb().getSalleAppel((int)(dateDebut.getTimeInMillis()/1000), (int)(dateFin.getTimeInMillis()/1000), idProfAppel);
                    if (nomSalleDuProfQuiFaitAppel.length()>0)
                    	if (idSalle>0)
                    		salle.setNom(salle.getNom()+" (" + nomSalleDuProfQuiFaitAppel + ")");
                    	else salle.setNom(" (" + nomSalleDuProfQuiFaitAppel + ")");
                }

//System.out.println("BOUCLE3 " + new GregorianCalendar().get(Calendar.SECOND) + ":" + new GregorianCalendar().get(Calendar.MILLISECOND));
                groupe = (Groupe)informations.get(1);
                matiere = (Matiere)informations.get(2);
                prof = (Prof)informations.get(3);
                profAppel = (Prof)informations.get(4);
//System.out.println(groupe + " " + matiere + " " + prof + " " + salle);
                // calcul des coordonnées
                int numero_jour = dateDebut.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
                // 0 pour lundi, ..., 4 pour vendredi
                float heure_debut = dateDebut.get(Calendar.HOUR_OF_DAY) + dateDebut.get(Calendar.MINUTE) / (float)60;
                float heure_fin = dateFin.get(Calendar.HOUR_OF_DAY) + dateFin.get(Calendar.MINUTE) / (float)60;
//System.out.println(Utilitaire.afficherDate(dateDebut, dateFin));
//System.out.println("heures = " + heure_debut + " " + heure_fin);
                // attention aux jours fériés qui n'ont pas de groupe !!!!
                int larg;
                float posx;
                if (idGroupe>0) {
                    posx=groupe.getIndexColonne() * nbTP / (float)nbColonnesVirtuelles;
                    larg=(int) (0.5+groupe.getNbColonnes() * largeurPlageTP * nbTP / (float)nbColonnesVirtuelles);
                }
                else {
                    posx=0;
                    larg=largeurJour; //edtAppli.getControleurDb().getNbColonnesVirtuelles(promo);
                }
                int x = largeurHeures + numero_jour * largeurJour + (int)(posx * largeurPlageTP);
                int y = (int) (hauteurLigne * (1 + 2*(heure_debut - 8)));
//System.out.println(y + " " + hauteurLigne + " " + heure_debut);
                int width = larg;
                int height = (int) ((heure_fin - heure_debut) * 2*hauteurLigne);
                if (groupe.getIndexColonne()==0) { // bord gauche du jour : on décale vers la droite
                    x+=1;
                    width-=1;
                }
                if (groupe.getIndexColonne()+groupe.getNbColonnes()==nbColonnesVirtuelles) // bord droit du jour : on décale vers la gauche
                        width-=2;
//System.out.println("coord=" + x + " " + y + " " + width + " " + height);
                g.setColor(new Color(matiere.getRouge(), matiere.getVert(), matiere.getBleu()));
                g.fillRect(x, y, width, height);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, width, height);
                g.setFont(font_cours);
                String hautGauche="", basDroite="", ligne1="", ligne2="";
                if (idSalle>0 || (idProf==0 && idProfAppel!=0)) // on affiche dans 2 cas : soit on a une salle, soit pas de prof mais un prof qui fait l'appel
                    if (!salle.getNom().equals("SALLE VIDE"))
                    	basDroite=salle.getNom();
                if (idSalle2>0)
                    basDroite+=" "+salle2.getNom();
                if (plage.accesInternet()) basDroite+="@";
                try {
	                if (idProf>0) { // il y a un prof
	                    hautGauche=prof.getInitiale();
	                	if (idProfAppel>0) // et un prof qui fait l'appel
	                		hautGauche+=" (" + profAppel.getInitiale()+")";
	                } else if (idProfAppel>0) // il y a juste un prof qui fait l'appel
	            		hautGauche="(" + profAppel.getInitiale()+")";
                } catch (NullPointerException e) {}; // au cas où on a mal récupéré les informations
                if (!memo.equals(""))
                    hautGauche+="*";
                if (plage.IsControle()) {
                    ligne1="Contrôle";
                    ligne2=groupe.getNom()+" "+matiere.getNom()+" "+matiere.getNomCompl();
                }
                else {
                    ligne1=matiere.getNom();
                    if (matiere.getNomCompl()!=null && !matiere.getNomCompl().equals(""))
                        if (ligne1.equals("JOURFERIE")) ligne1=matiere.getNomCompl(); // on affiche le type de jour férié (et pas JOURFERIE)
                        else ligne2=groupe.getNom()+" "+matiere.getNomCompl();
                    else ligne2=groupe.getNom();
                }
                afficherInformations(painter, g,hautGauche, basDroite, ligne1, ligne2, x, y, width, height, plage.IsControle());
			}
		}
	}
	public void traitGrasHorizontal(Graphics g, int x, int y, int longueur) {
		g.fillRect(x, y - 1, longueur, 3);
	}
	public void traitGrasVertical(Graphics g, int x, int y, int longueur) {
		g.fillRect(x - 1, y, 3, longueur);
	}
	public void traitFinHorizontal(Graphics g, int x, int y, int longueur) {
		g.fillRect(x, y, longueur, 1);
	}
	public void traitFinVertical(Graphics g, int x, int y, int longueur) {
		g.fillRect(x, y, 1, longueur);
	}
	public void traitHorizontal(Graphics g, int x, int y, int longueur) {
		g.fillRect(x, y, longueur, 2);
	}

	/**
	 * affiche la chaine dans le rectangle indiqué, avec l'alignement indiqué
	 * @param s la chaine
	 * @param x,y,width,height le rectangle
	 * @param alignement 'c' ou 'g' ou 'd'
	 * @param promoVisible : vrai -> noir ; faux -> rouge
	 */
	private void afficherChaine(WPainter painter, Graphics g, String s, int x, int y, int width, int height, char alignement, boolean promoVisible) {
		// on laisse des contours blancs
		x+=(int)(width*0.1);
		width*=0.8;
		y+=(int)(height*0.1);
		height*=0.8;
		int largeurChaine, hauteurChaine; // dimensions de la chaine
		Font font = g.getFont(); // fonte originale
		int taille = font.getSize(); // taille de la fonte
		int reduction = 0; // réduction de taille
		
		
		/*largeurChaine = g.getFontMetrics(font).stringWidth(s); // largeur de la chaine de caractères*/
		WFont fonte = new WFont();
		fonte.setFamily(WFont.GenericFamily.SansSerif);
		fonte.setSize(new WLength(taille - reduction));		
		painter.setFont(fonte);
		largeurChaine = (int)painter.getDevice().measureText(s).getWidth();
		hauteurChaine = g.getFontMetrics(font).getHeight(); // hauteur d'une ligne
		/*
		while (largeurChaine != width && hauteurChaine != height) { // tant que la chaine est trop grande
			reduction++; // on réduit un peu plus
			fonte = new WFont();
			fonte.setFamily(WFont.GenericFamily.SansSerif);
			fonte.setSize(new WLength(taille - reduction));		
			painter.setFont(fonte);
			largeurChaine = (int)painter.getDevice().measureText(s).getWidth()-reduction;			
			hauteurChaine = g.getFontMetrics(g.getFont()).getHeight()-reduction;
		}
		*/
		if (promoVisible)
		    g.setColor(Color.BLACK);
		int position_horizontale;
		switch (alignement) {
			case 'g' :
				position_horizontale=x;
				break;
			case 'd' :
				position_horizontale=x + width - largeurChaine;
				break;
			default : position_horizontale=x + (width - largeurChaine) / 2;
		}
		/*g.drawString(s, position_horizontale, y + (height - hauteurChaine) / 2 + g.getFontMetrics().getAscent());*/
		g.drawString(s, position_horizontale, y + (height + hauteurChaine) / 2 +	1);		
		g.setFont(font); // on rétablit la fonte d'origine
	}
	
	
	// affiche les informations dans le rectangle
	private void afficherInformations(WPainter painter, Graphics g, String hautGauche, String basDroite, String ligne1, String ligne2, int x, int y, int width, int height, boolean controle) {
		int largeurChaine, hauteurChaine; // dimensions de la chaine
		Font font_originale = g.getFont(); // fonte originale
		int taille_max_fonte = font_originale.getSize(); // taille de la fonte
		int reduction = 0; // réduction de taille de fonte
		/*//int x_controle, y_controle, width_controle, height_controle; // rectangle à tracer en cas de controle*/ 
		
		// affichage de la ligne1
		/*largeurChaine = g.getFontMetrics(font_originale).stringWidth(ligne1); // largeur de la chaine de caractères*/
		WFont fonte = new WFont();
		fonte.setFamily(WFont.GenericFamily.SansSerif);
		//fonte.setVariant(Font.PLAIN);
		fonte.setSize(new WLength(taille_max_fonte - reduction));		
		painter.setFont(fonte);
		largeurChaine = (int)painter.getDevice().measureText(""+ligne1).getWidth();
		hauteurChaine = g.getFontMetrics(font_originale).getHeight(); // hauteur d'une ligne
		while (largeurChaine >= width || hauteurChaine >= height/3) { // tant que la chaine est trop grande
			reduction++; // on réduit un peu plus
			g.setFont(font_originale.deriveFont((float) (taille_max_fonte - reduction))); // on réduit la taille de la fonte
			/*largeurChaine = g.getFontMetrics(g.getFont()).stringWidth(ligne1);*/
			fonte = new WFont();
			fonte.setFamily(WFont.GenericFamily.SansSerif);
			fonte.setSize(new WLength(taille_max_fonte - reduction));		
			painter.setFont(fonte);
			largeurChaine = (int)painter.getDevice().measureText(""+ligne1).getWidth();
			hauteurChaine = g.getFontMetrics(g.getFont()).getHeight();
		}
		g.setColor(Color.BLACK);
		int x_affichage= x + (width - largeurChaine)/2;
		int y_affichage;
		/*//if (height/hauteurLigne/2<1) // plage horaire de moins d'une heure*/
			y_affichage=y+(height-hauteurChaine)/2+g.getFontMetrics().getAscent();
		/*//else y_affichage=;*/
		g.drawString(ligne1, x_affichage, y_affichage);
		if (controle)
			g.drawRect(x_affichage, y_affichage-hauteurChaine, largeurChaine, hauteurChaine);	
		

		// affichage de la ligne2
		g.setFont(font_originale);

		if (reduction<=3)
			/*reduction=3; // ligne 2 : au moins trois points de moins en taille de fonte*/
			reduction=1; // ligne 2 : au moins trois points de moins en taille de fonte
		/*g.setFont(font_originale.deriveFont((float) (taille_max_fonte - reduction))); // on règle la taille de la fonte*/
		g.setFont(new Font("Serif", Font.PLAIN, taille_max_fonte - reduction));

		fonte = new WFont();
		fonte.setFamily(WFont.GenericFamily.SansSerif);
		fonte.setSize(new WLength(taille_max_fonte - reduction));		
		painter.setFont(fonte);
		
		/*largeurChaine = g.getFontMetrics(g.getFont()).stringWidth(ligne2); // largeur de la chaine de caractères*/
		largeurChaine = (int)painter.getDevice().measureText(""+ligne2).getWidth();
		hauteurChaine = g.getFontMetrics(g.getFont()).getHeight(); // hauteur d'une ligne

		while (largeurChaine >= (width) || hauteurChaine >= height/3) { // tant que la chaine est trop grande
			reduction++; // on réduit un peu plus
			/*g.setFont(font_originale.deriveFont((float) (taille_max_fonte - reduction))); // on réduit la taille de la fonte	*/	
			g.setFont(new Font("Serif", Font.TRUETYPE_FONT, taille_max_fonte - reduction));	
			
			//largeurChaine = g.getFontMetrics(g.getFont()).stringWidth(""+ligne2);
			fonte = new WFont();
			fonte.setFamily(WFont.GenericFamily.SansSerif);
			fonte.setSize(new WLength(taille_max_fonte - reduction));		
			painter.setFont(fonte);
			largeurChaine = (int)painter.getDevice().measureText(""+ligne2).getWidth();
			/*largeurChaine = g.getFontMetrics().stringWidth(""+ligne2+":"+reduction);*/
			hauteurChaine = g.getFontMetrics(g.getFont()).getHeight();
		}
		x_affichage= x + (width - largeurChaine)/2;
		y_affichage+=hauteurChaine;

		//g.setFont(new Font("Serif", Font.TRUETYPE_FONT, taille_max_fonte - (reduction+3)));	
		//g.getFontMetrics().getFontRenderContext().
		//painter.		

		/*
		g.drawString("("+width+"-"+largeurChaine+")/2="+(width-largeurChaine)/2, 			x_affichage, y_affichage);
		g.drawString(x+"+"+(width - largeurChaine)/2+"="+x_affichage, 						x_affichage, y_affichage+hauteurChaine);
		
		g.drawString(taille_max_fonte+"-"+reduction+"="+(taille_max_fonte-reduction), 		x_affichage, y_affichage+hauteurChaine*2);
		
		//g.drawString(""+ligne2, x_affichage, y_affichage+hauteurChaine*3);/** /
		g.drawString((x_affichage+largeurChaine)+"["+(x+width)+"]", 						x_affichage, y_affichage+hauteurChaine*3);
		
		
		//g.setFont(new Font("Serif", Font.PLAIN, (taille_max_fonte - reduction)));
		//g.setClip(0, 0, 400,400);
		*/	
		g.drawString(""+ligne2, 															x_affichage, y_affichage+hauteurChaine*1);//4);
		
		g.setFont(font_originale);		
		
			

		// affichage en haut à gauche
		reduction=2;
		g.setFont(font_originale.deriveFont((float) (taille_max_fonte - reduction))); // on règle la taille de la fonte
		fonte = new WFont();
		fonte.setFamily(WFont.GenericFamily.SansSerif);
		fonte.setSize(new WLength(taille_max_fonte - reduction));
		
		painter.setFont(fonte);
		largeurChaine = (int)painter.getDevice().measureText(hautGauche).getWidth();
		//largeurChaine = g.getFontMetrics(g.getFont()).stringWidth(hautGauche); // largeur de la chaine de caractères
		hauteurChaine = g.getFontMetrics(g.getFont()).getHeight(); // hauteur d'une ligne
		while (largeurChaine >= width || hauteurChaine >= height/3) { // tant que la chaine est trop grande
			reduction++; // on réduit un peu plus
			g.setFont(font_originale.deriveFont((float) (taille_max_fonte - reduction))); // on réduit la taille de la fonte

			/*largeurChaine = g.getFontMetrics(g.getFont()).stringWidth(hautGauche);*/
			fonte = new WFont();
			fonte.setFamily(WFont.GenericFamily.SansSerif);
			fonte.setSize(new WLength(taille_max_fonte - reduction));		
			painter.setFont(fonte);
			largeurChaine = (int)painter.getDevice().measureText(hautGauche).getWidth();			
			hauteurChaine = g.getFontMetrics(g.getFont()).getHeight();
		}
		x_affichage=x+2;
		y_affichage=y+g.getFontMetrics(g.getFont()).getAscent()		+2;
		g.drawString(hautGauche, x_affichage, y_affichage);		
		

		// affichage en bas à droite
		reduction=2;
		g.setFont(font_originale.deriveFont((float) (taille_max_fonte - reduction))); // on règle la taille de la fonte
		largeurChaine = g.getFontMetrics(g.getFont()).stringWidth(basDroite); // largeur de la chaine de caractères
		hauteurChaine = g.getFontMetrics(g.getFont()).getHeight(); // hauteur d'une ligne
		while (largeurChaine >= width || hauteurChaine >= height/3) { // tant que la chaine est trop grande
			reduction++; // on réduit un peu plus
			g.setFont(font_originale.deriveFont((float) (taille_max_fonte - reduction))); // on réduit la taille de la fonte
			/*g.setFont(new Font("Serif", Font.PLAIN, (taille_max_fonte - reduction)));*/
			largeurChaine = g.getFontMetrics(g.getFont()).stringWidth(basDroite);
			hauteurChaine = g.getFontMetrics(g.getFont()).getHeight();
		}
		x_affichage=x+width-largeurChaine 								-2;
		y_affichage=y+height-g.getFontMetrics(g.getFont()).getDescent()	+2;
		g.drawString(basDroite, x_affichage, y_affichage);
		g.setFont(font_originale); // on rétablit la fonte d'origine
	}	
	
	
	/**
	 * @param x,y coordonnées de la souris
	 * @return retrouver une plage horaire à partir des coordonnées d'un clic
	 */
	public PlageHoraire trouverPlageHoraire(int x, int y) {
		GregorianCalendar cal = getDateHeure(x,y,0,0);
	    if (edtAppli.getControleurDb().isAdmin()) // on cherche uniquement les jours fériés
	        return edtAppli.getControleurDb().getPlageHoraire(cal, "", 0);
		if (edtAppli.getListePromotions().getCurrentIndex()==0)//SelectedIndex()==0) // on n'a pas sélectionné de promo
			return null;			
		int position_edt=getPositionEDT(x,y);
		/*//System.out.println("test = " + ((String)(edtAppli.getListePromotions().getSelectedItem())).equals(""));*/
		// on renvoie la plage sur laquelle on a cliqué
		return edtAppli.getControleurDb().getPlageHoraire(cal, (String)(edtAppli.getListePromotions().getValueText()), position_edt);//SelectedItem()), position_edt);
	}	
	
	
	/**
	 * récupère le jour et l'heure (à 10 minutes près) aux coordonnées souris indiquées (sans que le rectangle ne dépasse la vue)
	 * @param x,y coordonnées de la souris
	 * @param nbColonnes nombre de colonnes de TP occupées sur la vue
	 * @param duree exprimée en secondes
	 */
	public GregorianCalendar getDateHeure(int x, int y, int nbColonnes, long duree) {
		int width=nbColonnes*largeurPlageTP;
		int height=(int)(duree*hauteurLigne/1800);
		GregorianCalendar cal=Utilitaire.calculerDate((String)edtAppli.getListeSemaines().getValueText());//getSelectedItem());
		if (x<=largeurHeures) x=largeurHeures+1; // on ne peut pas dépasser à gauche dans les heures
		if (x+width>=largeurHeures+5*largeurJour) x=largeurHeures+5*largeurJour-width-1; // on ne peut pas dépasser à droite dans les heures
		if (y<=hauteurLigne) y=(int)hauteurLigne+1; // on ne peut pas dépasser en haut dans les jours
		if (y+height>=22*hauteurLigne) y=(int)(22*hauteurLigne)-height; // on ne peut pas dépasser en bas
		int jour = (x - largeurHeures+1) /largeurJour; // jour de la semaine, entre 0 et 4 (+1 pour les promos d'une colonne, sinon bug le vendredi)
		/*//System.out.println("largeurVue=" + largeurVue + " largeurHeures=" + largeurHeures + "x=" + x);*/
		float heure_reelle = (y - hauteurLigne) / (2 * hauteurLigne) + 8;
		int heure=(int)heure_reelle;
		int minute=(((int)((heure_reelle-heure)*60))/10)*10; // à 10 minutes près
		/*//System.out.println("" + heure_reelle + " " + heure + " " + (int)(heure_reelle-heure)*60 + " " + minute);*/
		cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + jour); // on avance dans la semaine en cours
		cal.set(Calendar.HOUR_OF_DAY, heure); // partie entière de l'heure
		cal.set(Calendar.MINUTE, minute); // nombre de minutes
		/*//System.out.println("on a cliqué à " + cal.get(Calendar.DAY_OF_WEEK-Calendar.MONDAY) + " " + cal.get(Calendar.HOUR_OF_DAY) + " " + cal.get(Calendar.MINUTE));*/
		return cal;
	}	
	
	 
	/**
	 * @param x,y coordonnées de la souris
	 * @return renvoie le numéro de colonne (virtuelle, et à partir de 0) sur lequel on a cliqué 
	 */
	public int getPositionEDT(int x, int y) {
		int jour = (x - largeurHeures) / largeurJour; // jour de la semaine, entre 0 et 4
		return (x - largeurHeures - jour * largeurJour) * nbColonnesVirtuelles / (nbTP * largeurPlageTP); // numéro de colonne virtuelle de TP dans laquelle on a cliqué
	}
	
	
	public EDTSemaine getEdtSemaine() {
		return edtSemaine;
	}
	
	
	/**
	 * mise à jour à partir de la base de données
	 */
	public void mettreAJour() {
		edtSemaine = new EDTSemaine(edtAppli);
		/*repaint();*/
		this.update();
	}
}//683