package EDT;

import eu.webtoolkit.jwt.WEnvironment;
import eu.webtoolkit.jwt.WApplication;
import eu.webtoolkit.jwt.WMouseEvent;//ajouté le 15 mai 2019
import eu.webtoolkit.jwt.WCssDecorationStyle;
import eu.webtoolkit.jwt.WColor;
import eu.webtoolkit.jwt.WPaintedWidget;
import eu.webtoolkit.jwt.WPaintDevice;
import eu.webtoolkit.jwt.utils.WebGraphics2D;
import eu.webtoolkit.jwt.WPainter;
import eu.webtoolkit.jwt.WLength;
import eu.webtoolkit.jwt.WFont;
import eu.webtoolkit.jwt.Signal1;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class VueEDT_wt extends WPaintedWidget {


	private WebGraphics2D g;

	private EDTApplication_wt edtAppli; 
	private EDTSemaine edtSemaine; 
	private int largeurJour, largeurPlageTP;
	private float hauteurLigne;
	private int hauteurVue, largeurVue;
	private int largeurHeures;
	private int nbTP;
	private int nbColonnesVirtuelles;
	private Font font_jours;
	private Font font_heures;
	private Font font_cours;

	VueEDT_wt(EDTApplication_wt appli) {
		edtAppli = appli;
		hauteurVue = 600;	
		largeurVue = 800;	

		WEnvironment env = WApplication.getInstance().getEnvironment();

		hauteurVue = env.getScreenHeight() - env.getScreenHeight()/6;	


		largeurVue = env.getScreenWidth();

		this.resize(new WLength(largeurVue), new WLength(hauteurVue));

		this.setMargin(0);

		nbTP = 1; // nombre de groupes de TP par dÃ©faut
		nbColonnesVirtuelles = 1; // nombre de colonnes virtuelles par dÃ©faut
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
			font_cours = new Font("Serif", Font.BOLD, 20);
		} catch (IOException e) {
			e.printStackTrace();
		}	
		edtSemaine = new EDTSemaine(edtAppli);
	}

	public void dessinerGrille(WPainter painter, Graphics g, boolean promoVisible) {
		hauteurVue = Math.max(hauteurVue,(int)getHeight().toPixels());	//(320,
		largeurVue = Math.max(largeurVue,(int)getWidth().toPixels());	//(200,

		// on rÃ©cupÃ¨re le nombre de groupes de TP dans la promo
		nbTP = edtAppli.getControleurDb().getNbTP((String) (edtAppli.getListePromotions().getValueText()));//SelectedItem()));
		// calcul de la largeur d'une colonne et hauteur d'une ligne
		largeurJour = (int) ((largeurVue - 2 * largeurHeures) / (float)5); // 5 jours + 2 colonnes d'heures
		hauteurLigne = hauteurVue / (float)22; // 21 lignes + ligne des jours
		if (nbTP!=0) {
			largeurPlageTP = (int) (largeurJour / (float)nbTP);
			largeurJour=largeurPlageTP*nbTP; // pour que Ã§a tombre juste Ã  l'affichage
		}
		else
			largeurPlageTP=largeurJour;
		// on dessine le quadrillage
		traitGrasVertical(g, 0, (int)hauteurLigne, (int) (hauteurVue - hauteurLigne));
		traitGrasVertical(g, (int)largeurVue, (int)hauteurLigne, (int) (hauteurVue - hauteurLigne));
		// traÃ§age des lignes horizontales		
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
				traitFinVertical(g, (int) (largeurHeures + largeurJour * jour + largeurPlageTP * col), (int)hauteurLigne, (int) (hauteurVue - hauteurLigne));
			}
		}
		traitGrasHorizontal(g, (int)largeurHeures, 0, (int) (5 * largeurJour));
		traitGrasHorizontal(g, 0, (int)hauteurLigne, (int)largeurVue);		
		traitGrasHorizontal(g, 0, (int)hauteurVue, (int)largeurVue);


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
		afficherChaine(painter, g, "Lundi "+numeroJour, largeurHeures, 0, largeurJour, (int)hauteurLigne,'c', promoVisible);
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


	public void paintEvent(WPaintDevice paintDevice) {
		WPainter painter = new WPainter(paintDevice);
		g = new WebGraphics2D(painter);
		g.setColor(Color.black);

		// on met Ã  jour la liste des cours de la semaine
		WCssDecorationStyle cds = new WCssDecorationStyle();
		cds.setBackgroundColor(WColor.white);
		setDecorationStyle(cds);


		dessinerGrille(painter, g, edtAppli.getControleurDb().getVisibilitePromo(((String)edtAppli.getListePromotions().getValueText())));//SelectedItem())));
		// variables pour les diffÃ©rentes donnÃ©es
		int idGroupe, idMatiere, idProf, idProfAppel, idSalle, idSalle2=0; //, largeurChaine, hauteurChaine;
		GregorianCalendar dateDebut, dateFin;
		String memo="";
		Groupe groupe=null;
		Matiere matiere=null;
		Prof prof=null, profAppel=null;
		Salle salle=null, salle2=null;
		// on rÃ©cupÃ¨re les plages horaires de la semaine et les jours fÃ©riÃ©s
		Vector<PlageHoraire> listeJoursFeries=edtSemaine.getListeJoursFeries();
		Vector<PlageHoraire> listePlages = edtSemaine.getListePlages();
		// on ajuste la "taille" du jour fÃ©riÃ© Ã  celle de la promo pour l'affichage
		for (Iterator<PlageHoraire> iter = listeJoursFeries.iterator(); iter.hasNext();) {
			if (listePlages==null) listePlages=new Vector<PlageHoraire>();
			listePlages.add(iter.next());
		}
		if (listePlages != null) { // s'il y en a Ã  afficher
			nbColonnesVirtuelles = edtAppli.getControleurDb().getNbColonnesVirtuelles((String)edtAppli.getListePromotions().getValueText());//SelectedItem());
			for (Iterator<PlageHoraire> i = listePlages.iterator(); i.hasNext(); ) {
				// rÃ©cupÃ©ration des donnÃ©es
				PlageHoraire plage = (PlageHoraire)i.next();
				dateDebut = plage.getDateDebut();
				dateFin = plage.getDateFin();
				memo=plage.getMemo();
				idSalle = plage.getIdSalle();
				idGroupe = plage.getIdGroupe();
				idMatiere = plage.getIdMatiere();
				idProf = plage.getIdProf();
				idProfAppel = plage.getIdProfQuiFaitAppel();
				/* APRES OPTIMISATION */
				// on rÃ©cupÃ¨re les informations sur la plage horaire  Ã   afficher
				@SuppressWarnings("rawtypes")
				ArrayList informations=edtAppli.getControleurDb().getInformationsPourVue((String)(edtAppli.getListePromotions().getValueText()),idSalle, idGroupe, idMatiere, idProf, idProfAppel);//SelectedItem()),idSalle, idGroupe, idMatiere, idProf, idProfAppel);
				salle = (Salle)informations.get(0);
				// si on a un contrÃ´le on cherche une Ã©ventuelle deuxiÃ¨me salle
				//if (plage.IsControle()) { //debug 25/02 (afficher 2 salles même si ce n'est pas un contrôle )
					idSalle2=plage.getIdSalle2();
					if (idSalle2>0)
						salle2 = edtAppli.getControleurDb().getSalle(idSalle2);
				//}
				//else idSalle2=0; //debug 25/02

				if (idProf==0 && idProfAppel!=0) { // on cherche la salle du prof qui fait l'appel
					String nomSalleDuProfQuiFaitAppel=edtAppli.getControleurDb().getSalleAppel((int)(dateDebut.getTimeInMillis()/1000), (int)(dateFin.getTimeInMillis()/1000), idProfAppel);
					if (nomSalleDuProfQuiFaitAppel.length()>0)
						if (idSalle>0)
							salle.setNom(salle.getNom()+" (" + nomSalleDuProfQuiFaitAppel + ")");
						else salle.setNom(" (" + nomSalleDuProfQuiFaitAppel + ")");
				}
				if(informations.size()>1) {
					groupe = (Groupe)informations.get(1);
					if(informations.size()>2) {
						matiere = (Matiere)informations.get(2);
						if(informations.size()>3) {
							prof = (Prof)informations.get(3);
							if(informations.size()>4) {
								profAppel = (Prof)informations.get(4);
							}
						}
					}
				}
				// calcul des coordonnÃ©es
				int numero_jour = dateDebut.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
				// 0 pour lundi, ..., 4 pour vendredi
				float heure_debut = dateDebut.get(Calendar.HOUR_OF_DAY) + dateDebut.get(Calendar.MINUTE) / (float)60;
				float heure_fin = dateFin.get(Calendar.HOUR_OF_DAY) + dateFin.get(Calendar.MINUTE) / (float)60;
				// attention aux jours fÃ©riÃ©s qui n'ont pas de groupe !!!!
				int larg;
				float posx;
				if (idGroupe>0) {
					posx=groupe.getIndexColonne() * nbTP / (float)nbColonnesVirtuelles;
					larg=(int) (0.5+groupe.getNbColonnes() * largeurPlageTP * nbTP / (float)nbColonnesVirtuelles);
				}
				else {
					posx=0;
					larg=largeurJour; 
				}
				int x = largeurHeures + numero_jour * largeurJour + (int)(posx * largeurPlageTP);
				int y = (int) (hauteurLigne * (1 + 2*(heure_debut - 8)));
				int width = larg;
				int height = (int) ((heure_fin - heure_debut) * 2*hauteurLigne);
				if (groupe.getIndexColonne()==0) { // bord gauche du jour : on dÃ©cale vers la droite
					x+=1;
					width-=1;
				}
				if (groupe.getIndexColonne()+groupe.getNbColonnes()==nbColonnesVirtuelles) // bord droit du jour : on dÃ©cale vers la gauche
					width-=2;
				g.setColor(new Color(matiere.getRouge(), matiere.getVert(), matiere.getBleu()));
				g.fillRect(x, y, width, height);
				g.setColor(Color.BLACK);
				g.drawRect(x, y, width, height);
				g.setFont(font_cours);
				String hautGauche="", basDroite="", ligne1="", ligne2="",hautDroite="";
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
				} catch (NullPointerException e) {}; // au cas oÃ¹ on a mal rÃ©cupÃ©rÃ© les informations
				if (!memo.equals(""))
					hautGauche+="*";
				if (plage.IsControle()) {
					ligne1="Controle";
					ligne2=matiere.getNom()+" "+matiere.getNomCompl();
				}
				else {
					ligne1=matiere.getNom();
					if (matiere.getNomCompl()!=null && !matiere.getNomCompl().equals(""))
						if (ligne1.equals("JOURFERIE")) ligne1=matiere.getNomCompl(); // on affiche le type de jour fÃ©riÃ© (et pas JOURFERIE)
						else ligne2=matiere.getNomCompl();
					else {
						ligne2=groupe.getNom();
					}
				}
				if(!ligne2.startsWith(groupe.getNom())) {
					hautDroite=groupe.getNom();
				}
				afficherInformations(painter, g,hautGauche, hautDroite,basDroite, ligne1, ligne2, x, y, width, height, plage.IsControle());
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

	private void afficherChaine(WPainter painter, Graphics g, String s, int x, int y, int width, int height, char alignement, boolean promoVisible) {
		// on laisse des contours blancs
		x+=(int)(width*0.1);
		width*=0.8;
		y+=(int)(height*0.1);
		height*=0.8;
		int largeurChaine, hauteurChaine; // dimensions de la chaine
		Font font = g.getFont(); // fonte originale
		int taille = font.getSize(); // taille de la fonte
		int reduction = 0; // rÃ©duction de taille


		WFont fonte = new WFont();
		fonte.setFamily(WFont.GenericFamily.SansSerif);
		fonte.setSize(new WLength(taille - reduction));		
		painter.setFont(fonte);
		largeurChaine = (int)painter.getDevice().measureText(s).getWidth();
		hauteurChaine = g.getFontMetrics(font).getHeight(); // hauteur d'une ligne

		while (largeurChaine > width && hauteurChaine > height) { // tant que la chaine est trop grande
			reduction++; // on réduit un peu plus
			fonte = new WFont();
			fonte.setFamily(WFont.GenericFamily.SansSerif);
			fonte.setSize(new WLength(taille - reduction));		
			painter.setFont(fonte);
			largeurChaine = (int)painter.getDevice().measureText(s).getWidth()-reduction;			
			hauteurChaine = g.getFontMetrics(g.getFont()).getHeight()-reduction;
		}

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
		g.drawString(s, position_horizontale, y + (height + hauteurChaine) / 2 +	1);		
		g.setFont(font); // on rÃ©tablit la fonte d'origine
	}
	// affiche les informations dans le rectangle
	private void afficherInformations(WPainter painter, Graphics g, String hautGauche, String hautDroite, String basDroite, String ligne1, String ligne2, int x, int y, int width, int height, boolean controle) {
		int largeurChaine, hauteurChaine; // dimensions de la chaine
		Font font_originale = g.getFont(); // fonte originale
		int taille_max_fonte = font_originale.getSize(); // taille de la fonte
		int reduction = 0; // réduction de taille de fonte

		// affichage de la ligne1
		WFont fonte = new WFont();
		fonte.setFamily(WFont.GenericFamily.SansSerif);
		fonte.setSize(new WLength(taille_max_fonte - reduction));		
		painter.setFont(fonte);
		largeurChaine = (int)painter.getDevice().measureText(""+ligne1).getWidth();
		hauteurChaine = g.getFontMetrics(font_originale).getHeight(); // hauteur d'une ligne
				
		if(height>15) {
			while (largeurChaine >= width || hauteurChaine >= height/3) { // tant que la chaine est trop grande
				reduction++; // on réduit un peu plus
				g.setFont(font_originale.deriveFont((float) (taille_max_fonte - reduction))); // on réduit la taille de la fonte
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
			y_affichage=y+(height-hauteurChaine)/2+g.getFontMetrics().getAscent();
			if((height<100 && height>50 && width<110 &&  width>80 && controle && ligne2.length()>15 && ligne2.length()<30) || (controle && height>100 && ligne2.length()>25)) {
				y_affichage-=hauteurChaine;
			}
			if(height>100 && ligne2.length()>20 && width<100) {
				y_affichage-=hauteurChaine;
			}
			g.drawString(ligne1, x_affichage, y_affichage);
			if (controle)
				g.drawRect(x_affichage, y_affichage-hauteurChaine, largeurChaine, hauteurChaine);	

			// affichage de la ligne2
			g.setFont(font_originale);

			if (reduction<=3)
				reduction=1; // ligne 2 : au moins trois points de moins en taille de fonte

			g.setFont(new Font("Serif", Font.PLAIN, taille_max_fonte - reduction));

			fonte = new WFont();
			fonte.setFamily(WFont.GenericFamily.SansSerif);
			fonte.setSize(new WLength(taille_max_fonte - reduction));		
			painter.setFont(fonte);

			/*largeurChaine = g.getFontMetrics(g.getFont()).stringWidth(ligne2); // largeur de la chaine de caractères*/
			largeurChaine = (int)painter.getDevice().measureText(""+ligne2).getWidth();
			hauteurChaine = g.getFontMetrics(g.getFont()).getHeight(); // hauteur d'une ligne

			boolean groupeD=false;
			if(width<60 && hautGauche.length()>6) {
				hautDroite+=" "+ligne2;
				ligne2=hautDroite;
			}else {
				groupeD=true;
			}

			
			//TESTER LE PLUS GRAND MOT
			String[] parts = ligne2.split(" +");
			int tailleString=0;
			int i=0;
			while(i<parts.length) {
				if(tailleString<parts[i].length()) {
					tailleString=parts[i].length();
				}
				i++;
			}
			
			//System.out.println("width="+width+"height="+height);
			if( ligne2.length()>15 && ligne2.length()<25 && width>50 && tailleString<9|| height>120 && tailleString<9 )  {
				g.setFont(new Font("Serif", Font.TRUETYPE_FONT, taille_max_fonte));	
				fonte = new WFont();
				fonte.setFamily(WFont.GenericFamily.SansSerif);
				fonte.setSize(new WLength(taille_max_fonte));		
				painter.setFont(fonte);
				largeurChaine = (int)painter.getDevice().measureText(""+ligne2).getWidth();
				hauteurChaine = g.getFontMetrics(g.getFont()).getHeight();
				nextLine(ligne2, largeurChaine, painter, g, x_affichage, y_affichage, width, hauteurChaine, x, y, height,taille_max_fonte,1);
			}else {
				while (largeurChaine >= (width) || hauteurChaine >= height/3) { // tant que la chaine est trop grande
					reduction++; // on réduit un peu plus
					g.setFont(new Font("Serif", Font.TRUETYPE_FONT, taille_max_fonte - reduction));	

					//largeurChaine = g.getFontMetrics(g.getFont()).stringWidth(""+ligne2);
					fonte = new WFont();
					fonte.setFamily(WFont.GenericFamily.SansSerif);
					fonte.setSize(new WLength(taille_max_fonte - reduction));		
					painter.setFont(fonte);
					largeurChaine = (int)painter.getDevice().measureText(""+ligne2).getWidth();
					hauteurChaine = g.getFontMetrics(g.getFont()).getHeight();
				}
				x_affichage= x + (width - largeurChaine)/2;
				y_affichage+=hauteurChaine;
				g.drawString(""+ ligne2, x_affichage, y_affichage);//4);
			}

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

			if(groupeD) {
				// affichage en haut à droite
				reduction=2;
				g.setFont(font_originale.deriveFont((float) (taille_max_fonte - reduction))); // on règle la taille de la fonte
				fonte = new WFont();
				fonte.setFamily(WFont.GenericFamily.SansSerif);
				fonte.setSize(new WLength(taille_max_fonte - reduction));

				painter.setFont(fonte);
				largeurChaine = (int)painter.getDevice().measureText(hautDroite).getWidth();
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
					largeurChaine = (int)painter.getDevice().measureText(hautDroite).getWidth();			
					hauteurChaine = g.getFontMetrics(g.getFont()).getHeight();
				}
				x_affichage=x+width-largeurChaine-2;
				y_affichage=y+g.getFontMetrics(g.getFont()).getAscent()		+2;
				g.drawString(hautDroite, x_affichage, y_affichage);		
			}

			reduction=2;

			// affichage en bas à droite
			g.setFont(font_originale.deriveFont((float) (taille_max_fonte - reduction))); // on règle la taille de la fonte
			largeurChaine = g.getFontMetrics(g.getFont()).stringWidth(basDroite); // largeur de la chaine de caractères
			hauteurChaine = g.getFontMetrics(g.getFont()).getHeight(); // hauteur d'une ligne

			if(basDroite.length()>6)  {
				reduction=3;
				while (largeurChaine >= width || hauteurChaine >= height/3) { // tant que la chaine est trop grande
					reduction++; // on réduit un peu plus
					g.setFont(font_originale.deriveFont((float) (taille_max_fonte - reduction))); // on réduit la taille de la fonte
					fonte = new WFont();
					fonte.setFamily(WFont.GenericFamily.SansSerif);
					fonte.setSize(new WLength(taille_max_fonte - reduction));		
					painter.setFont(fonte);
					largeurChaine = (int)painter.getDevice().measureText(basDroite).getWidth();			
					hauteurChaine = g.getFontMetrics(g.getFont()).getHeight();
				}
				x_affichage=x+width-largeurChaine-2;
				y_affichage=y+height-g.getFontMetrics(g.getFont()).getDescent()	+2;
				g.drawString(basDroite, x_affichage, y_affichage);
				g.setFont(font_originale); // on rétablit la fonte d'origine
			}else {
				while (largeurChaine >= width || hauteurChaine >= height/3) { // tant que la chaine est trop grande
					reduction++; // on réduit un peu plus
					g.setFont(font_originale.deriveFont((float) (taille_max_fonte - reduction))); // on réduit la taille de la fonte
					fonte = new WFont();
					fonte.setFamily(WFont.GenericFamily.SansSerif);
					fonte.setSize(new WLength(taille_max_fonte - reduction));		
					painter.setFont(fonte);
					largeurChaine = (int)painter.getDevice().measureText(basDroite).getWidth();			
					hauteurChaine = g.getFontMetrics(g.getFont()).getHeight();
				}
				x_affichage=x+width-largeurChaine-2;
				y_affichage=y+height-g.getFontMetrics(g.getFont()).getDescent()	+2;
				g.drawString(basDroite, x_affichage, y_affichage);
				g.setFont(font_originale); // on rétablit la fonte d'origine
			}	
		}
	}	


	/**
	 * @param x,y coordonnÃ©es de la souris
	 * @return retrouver une plage horaire Ã  partir des coordonnÃ©es d'un clic
	 */
	public PlageHoraire trouverPlageHoraire(int x, int y) {
		GregorianCalendar cal = getDateHeure(x,y,0,0);
		if (edtAppli.getControleurDb().isAdmin()) // on cherche uniquement les jours fÃ©riÃ©s
			return edtAppli.getControleurDb().getPlageHoraire(cal, "", 0);
		if (edtAppli.getListePromotions().getCurrentIndex()==0)//SelectedIndex()==0) // on n'a pas sÃ©lectionnÃ© de promo
			return null;			
		int position_edt=getPositionEDT(x,y);
		// on renvoie la plage sur laquelle on a cliquÃ©
		return edtAppli.getControleurDb().getPlageHoraire(cal, (String)(edtAppli.getListePromotions().getValueText()), position_edt);//SelectedItem()), position_edt);
	}	




	/**
	 * rÃ©cupÃ¨re le jour et l'heure (Ã  10 minutes prÃ¨s) aux coordonnÃ©es souris indiquÃ©es (sans que le rectangle ne dÃ©passe la vue)
	 * @param x,y coordonnÃ©es de la souris
	 * @param nbColonnes nombre de colonnes de TP occupÃ©es sur la vue
	 * @param duree exprimÃ©e en secondes
	 */
	public GregorianCalendar getDateHeure(int x, int y, int nbColonnes, long duree) {
		int width=nbColonnes*largeurPlageTP;
		int height=(int)(duree*hauteurLigne/1800);
		GregorianCalendar cal=Utilitaire.calculerDate((String)edtAppli.getListeSemaines().getValueText());//getSelectedItem());
		if (x<=largeurHeures) x=largeurHeures+1; // on ne peut pas dÃ©passer Ã  gauche dans les heures
		if (x+width>=largeurHeures+5*largeurJour) x=largeurHeures+5*largeurJour-width-1; // on ne peut pas dÃ©passer Ã  droite dans les heures
		if (y<=hauteurLigne) y=(int)hauteurLigne+1; // on ne peut pas dÃ©passer en haut dans les jours
		if (y+height>=22*hauteurLigne) y=(int)(22*hauteurLigne)-height; // on ne peut pas dÃ©passer en bas
		int jour = (x - largeurHeures+1) /largeurJour; // jour de la semaine, entre 0 et 4 (+1 pour les promos d'une colonne, sinon bug le vendredi)
		float heure_reelle = (y - hauteurLigne) / (2 * hauteurLigne) + 8;
		int heure=(int)heure_reelle;
		int minute=(((int)((heure_reelle-heure)*60))/10)*10; // Ã  10 minutes prÃ¨s
		cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + jour); // on avance dans la semaine en cours
		cal.set(Calendar.HOUR_OF_DAY, heure); // partie entiÃ¨re de l'heure
		cal.set(Calendar.MINUTE, minute); // nombre de minutes
		return cal;
	}	


	public int getPositionEDT(int x, int y) {
		int jour = (x - largeurHeures) / largeurJour; // jour de la semaine, entre 0 et 4
		return (x - largeurHeures - jour * largeurJour) * nbColonnesVirtuelles / (nbTP * largeurPlageTP); // numÃ©ro de colonne virtuelle de TP dans laquelle on a cliquÃ©
	}


	public EDTSemaine getEdtSemaine() {
		return edtSemaine;
	}


	public void mettreAJour() {
		edtSemaine = new EDTSemaine(edtAppli);
		/*repaint();*/
		this.update();
	}



	public void nextLine(String ligne2, int largeurChaine,WPainter painter, Graphics g, int x_affichage, int y_affichage, int width, int hauteurChaine, int x,int y,int height, int taille_max_fonte,int red) {
		if(largeurChaine > width) {
			String[] parts = ligne2.split(" +");
			String l1 = "";
			String l2 = "";
			WFont fonte = new WFont();
			int i=0;
			g.setFont(new Font("Serif", Font.TRUETYPE_FONT, taille_max_fonte-red));	
			fonte.setFamily(WFont.GenericFamily.SansSerif);
			fonte.setSize(new WLength(taille_max_fonte-red));		
			painter.setFont(fonte);

			//Ligne 1 Matiere
			//on remet largeur a zero
			largeurChaine = (int)painter.getDevice().measureText(""+l1).getWidth();
			//on rajoute des mots tant que c'est pas trop grand pour la largeur du bloc
			while(largeurChaine < width && parts.length>i) {
				l1+=parts[i];
				largeurChaine = (int)painter.getDevice().measureText(""+l1).getWidth();
				l1+=" ";
				i++;
			}
			//On enleve le dernier espace
			if(l1.length()>0 && l1.charAt(l1.length()-1)==' ') {
				l1 = l1.substring(0, l1.length() - 1);
			}

			//On verifie si c'est trop grand
			if(largeurChaine>width) {
				//Si c'est le cas on enleve des mots tant que c'est grand
				i--;
				while(largeurChaine>width) {
					//On enleve la taille du mot du string
					l1 = l1.substring(0,l1.length()-parts[i].length());
					//Faut pas oublier l'espace entre les deux mots
					if(l1.length()>0 && l1.charAt(l1.length()-1)==' ') {
						l1 = l1.substring(0, l1.length() - 1);
					}
					largeurChaine = (int)painter.getDevice().measureText(""+l1).getWidth();
					i--;
				}
				i++;
				//faut remmetre l'indexe a la bonne place
			}

			largeurChaine = (int)painter.getDevice().measureText(""+l1).getWidth();
			x_affichage= x + (width - largeurChaine)/2;
			y_affichage+=hauteurChaine;
			g.drawString(""+l1,x_affichage, y_affichage);

			//Ligne 2 Matiere
			largeurChaine = (int)painter.getDevice().measureText(""+l2).getWidth();
			while(largeurChaine < width && parts.length>i) {
				l2+=parts[i];
				largeurChaine = (int)painter.getDevice().measureText(""+l2).getWidth();
				l2+=" ";
				i++;
			}

			if(l2.length()>0 && l2.charAt(l2.length()-1)==' ') {
				l2 = l2.substring(0, l2.length() - 1);
			}
			//On remet le reste si c'est trop grand et on rappelle la methode
			if(largeurChaine>width) {
				while(i<parts.length){
					l2+=" "+parts[i];
					i++;
				}
				String[] parts2=l2.split(" ");
				if(parts2.length==1) {
					nextLine(l2,largeurChaine, painter, g, x_affichage, y_affichage, width, hauteurChaine, x, y, height,taille_max_fonte,red+2);
				}else {
					nextLine(l2,largeurChaine, painter, g, x_affichage, y_affichage, width, hauteurChaine, x, y, height,taille_max_fonte,red);
				}
			}else {
				largeurChaine = (int)painter.getDevice().measureText(""+l2).getWidth();
				x_affichage= x + (width - largeurChaine)/2; 
				y_affichage+=hauteurChaine;
				g.drawString(""+l2,x_affichage, y_affichage);
			}


		}else {
			x_affichage= x + (width - largeurChaine)/2;
			y_affichage+=hauteurChaine;
			g.drawString(""+ligne2,x_affichage, y_affichage);
		}
	}
}
