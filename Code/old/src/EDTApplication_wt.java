package EDT;

import eu.webtoolkit.jwt.WApplication;
import eu.webtoolkit.jwt.WEnvironment;

import eu.webtoolkit.jwt.WBorderLayout;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WComboBox;
import eu.webtoolkit.jwt.WDialog;
import eu.webtoolkit.jwt.WLabel;
import eu.webtoolkit.jwt.WMenu;
import eu.webtoolkit.jwt.WMenuItem;
import eu.webtoolkit.jwt.WNavigationBar;
import eu.webtoolkit.jwt.WPopupMenu;
import eu.webtoolkit.jwt.WBootstrapTheme;
import eu.webtoolkit.jwt.WLabel;
import eu.webtoolkit.jwt.AlignmentFlag;
import eu.webtoolkit.jwt.WDefaultLoadingIndicator;
import eu.webtoolkit.jwt.Side;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WApplication;
import eu.webtoolkit.jwt.WCheckBox;
import eu.webtoolkit.jwt.WEnvironment;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WText;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTimer;
import eu.webtoolkit.jwt.WLength;
import eu.webtoolkit.jwt.WCheckBox;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.*;


/*
 * Created on 26 juin 2004
 *
 */

// Pour les mises à jour de version : changer la date et le numéro de version ci-dessous
// et changer l'historique dans Controleur Menu

/**
 * @author fredphil
 *
 */
public class EDTApplication_wt extends WApplication {
/*public class EDTApplication_wt extends JFrame {*/
    String version,dateVersion;
    String loginEDT;
	private WComboBox listeSemaines;
	private WComboBox listePromotions;
	WCheckBox copypaste;
	/*JCheckBoxMenuItem menuCopierColler;*/
	/*WCheckBox menuCopierColler;*/
    /*JCheckBoxMenuItem menuHistoriquePlagesHoraires;*/
	private VueEDT_wt vueEDT_wt;
	private ControleurDB controleurDB;
	/*private ControleurGUI controleurGUI;*/
	private boolean copierColler; // vrai quand on est en train de copier/coller une plage
    private boolean historiquePlagesHoraires; // vrai quand on veut afficher l'historique du nombre d'heures par plage horaire
	WContainerWidget panneauHaut = new WContainerWidget();
	WContainerWidget container = new WContainerWidget();
	
    EDTApplication_wt edtAppli = this;
    
	public EDTApplication_wt(WEnvironment env) {
		super(env);
        setTheme(new WBootstrapTheme());
        /*super("Editeur d'emploi du temps version 1.18");*/
        version="2.0";
        setTitle("Editeur d'emploi du temps version "+version);
        
        //version="1.18";
        //dateVersion="Septembre 2011";
        //ToolTipManager.sharedInstance().setInitialDelay(0); // apparition immédiate des bulles d'aide
        // 4 lignes à décommenter pour interdire le lancement de l'éditeur (pendant une phase d'administration par exemple)
		/*
        JOptionPane.showMessageDialog(this, new JScrollPane(new JTextArea("Accès momentanément impossible, merci de réessayer ultérieurement")));
        dispose(); //la fenetre est masquée
        System.gc(); //vider la mémoire
        System.exit(0); //on quitte le programme
        */
        
        // on lance la fenetre de login
		/*@SuppressWarnings("unused") FenetreLogin f_login=new FenetreLogin(this);
		// tant qu'on n'est pas connecté on attend
		while (controleurDB==null) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}*/		
		FenetreLogin_wt f_login=new FenetreLogin_wt(this);
	}
	public void afficher() {	
		
		String js="document.oncontextmenu = function(e){\n" + 
				"		 var evt = new Object({keyCode:93});\n" + 
				"		 stopEvent(e);\n" + 
				"		}\n" + 
				"		function stopEvent(event){\n" + 
				"		 if(event.preventDefault != undefined)\n" + 
				"		  event.preventDefault();\n" + 
				"		 if(event.stopPropagation != undefined)\n" + 
				"		  event.stopPropagation();\n" + 
				"		}";
		edtAppli.doJavaScript(js);
		
		edtAppli.globalKeyWentDown().addListener(edtAppli, new ControleurGUI_wt(edtAppli));
		
		WTimer timer = new WTimer(this);
		timer.setSingleShot(true);
		timer.setInterval(60000*60*1);	//cnx:12hmax
		timer.start();
		timer.timeout().addListener(this, new Signal.Listener() {
			public void trigger() {
				if (getControleurDb()!=null) {
					Utilitaire.showMessageDialog("this","Vous êtes déconnecter!");
					quitterEDT();
				}		
			}				
		});
		
		// l'écouteur de boutons et de listes déroulantes
		/*controleurGUI=new ControleurGUI(this);*/
		
		// on cherche les lundis de l'année et les promos existantes
		/*listeSemaines = new JComboBox();*/
		listeSemaines = new WComboBox();
		listeSemaines.changed().addListener(listeSemaines, new Signal.Listener() {
				public void trigger() {					
					mettreAJour();
				}
		});		
		//listeSemaines.clicked().addListener(listeSemaines, new Signal.Listener() {
		//		public void trigger() {					
		//			mettreAJour();		
		//		}
		//});
		
		rechercherLundis();		
		
		listePromotions = new WComboBox();
		listePromotions.changed().addListener(listePromotions, new Signal.Listener() {
				public void trigger() {
					mettreAJour();				
				}
		});		
		
		/*listeSemaines.addItemListener(controleurGUI);
		listePromotions.addItemListener(controleurGUI);*/
		//mettreAJourPromotions(); //dans fenetrelogin.verifNom();
		
		//procedure qui masque et ferme la fenetre
		/*addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				//Quitter la session
				//dispose();
				System.gc();
				System.exit(0);
			}
		});		
		// mise en forme de la fenetre principale
		getContentPane().setLayout(new BorderLayout());
		// le panel du haut (listes déroulantes)
		WPanel panneauHaut=new WPanel();
		// les boutons de changement de semaine
		JButton b_prec=new JButton("Semaine précédente");
		JButton b_suiv=new JButton("Semaine suivante");
		b_prec.addActionListener(controleurGUI);
		b_suiv.addActionListener(controleurGUI);*/
		WPushButton b_prec=new WPushButton("<");	/*Semaine précédente*/
		WPushButton b_suiv=new WPushButton(">");	/*Semaine suivante*/
		b_prec.clicked().addListener(b_prec, new Signal.Listener() {
			public void trigger() {
				ControleurGUI_wt controleurGUI_wt = new ControleurGUI_wt(edtAppli);
				controleurGUI_wt.action("Semaine précédente");				
			}				
		});
		b_suiv.clicked().addListener(b_suiv, new Signal.Listener() {
			public void trigger() {
				ControleurGUI_wt controleurGUI_wt = new ControleurGUI_wt(edtAppli);
				controleurGUI_wt.action("Semaine suivante");
			}
		});	
		           
		
		/*if (controleurDB.isAdmin()) // on n'affiche pas les promos pour l'administrateur*/
		    panneauHaut.addWidget(listePromotions);
		panneauHaut.addWidget(new WLabel(" "));
	    panneauHaut.addWidget(b_prec);
		panneauHaut.addWidget(listeSemaines);
		panneauHaut.addWidget(b_suiv);
		
		
		// le panel du centre (EDT)
        /*vueEDT_wt=new VueEDT_wt(this);
		vueEDT.addMouseListener(controleurGUI);
		vueEDT.addMouseMotionListener(controleurGUI);
		getContentPane().add(vueEDT, BorderLayout.CENTER);*/
		// pour les copier/coller des plages horaires
		copierColler=false;
        // pour afficher l'historique du nombre d'heure par plage horaire
        historiquePlagesHoraires=false;	
		
		
		
		////////////////////////////////////////////////////////////
		//////////////////MENU PRINCIPAL////////////////////////////
		////////////////////////////////////////////////////////////
		WNavigationBar navigation = new WNavigationBar(container);
		navigation.setTitle("EditorEDT"+version);
		

		//MENU Administration//////////////////////////////
		//Utilitaire.showMessageDialog("this","Valeur de login dans edtAppli: "+this.getLoginEDT());
		
		if (loginEDT.compareTo("hassoun")==0)
		//if (controleurDB.isAdmin()) // on n'affiche pas les promos pour l'administrateur
		{
			WMenu menuAdmin = new WMenu();
			WMenuItem menuAdminContainer = new WMenuItem("Administration");
			WPopupMenu menuAdminPopup = new WPopupMenu();
			menuAdminPopup.addItem("Administrateurs");
			menuAdminPopup.addItem("Départements");
			menuAdminPopup.addItem("Promotions");
			menuAdminPopup.addItem("Professeurs");
			menuAdminPopup.addItem("Matières");

			menuAdminContainer.setMenu(menuAdminPopup);
			menuAdmin.addItem(menuAdminContainer);
			navigation.addMenu(menuAdmin, AlignmentFlag.AlignLeft);
		
			menuAdminPopup.itemSelected().addListener(this,
				new Signal1.Listener<WMenuItem>() {
					public void trigger(WMenuItem item) {
						if (item.getText().equals("Administrateurs")) {
							@SuppressWarnings("unused") FormulaireAdministrateurs_wt form = new FormulaireAdministrateurs_wt(edtAppli);	
						}
						if (item.getText().equals("Départements")) {
							@SuppressWarnings("unused") FormulaireDepartements_wt form = new FormulaireDepartements_wt(edtAppli);
						}
						if (item.getText().equals("Promotions")) {
							new FormulairePromotion_wt(edtAppli);
						}
						if (item.getText().equals("Professeurs")) {
							new FormulaireProfAdmin_wt(edtAppli);
						}
						if (item.getText().equals("Matières")) {
							if (getListePromotions().getValueText()!=null && getListePromotions().getValueText()!="") {
                            new FormulaireMatieres_wt(edtAppli);
                        } else Utilitaire.showMessageDialog("Gestion des Matières", "Vous devez d'abord sélectionner une promotion !"); 
							new FormulaireMatiereAdmin_wt(edtAppli);
						}

					}
				});
		}
		//MENU Edition//////////////////////////////////////
		WMenu menuEdition = new WMenu();
		WMenuItem menuEditionContainer = new WMenuItem("Edition");
		WPopupMenu menuEditionPopup = new WPopupMenu();
		//menuEditionPopup.addItem("Copier/Coller");
		menuEditionPopup.addItem("Ajouter une plage horaire");
		menuEditionPopup.addItem("Dupliquer la semaine");
		menuEditionPopup.addItem("Supprimer la semaine");
		menuEditionContainer.setMenu(menuEditionPopup);
		menuEdition.addItem(menuEditionContainer);
		navigation.addMenu(menuEdition, AlignmentFlag.AlignLeft);
		
		menuEditionPopup.itemSelected().addListener(this,
			new Signal1.Listener<WMenuItem>() {
				public void trigger(WMenuItem item) {
					if (item.getText().equals("Copier/Coller")) {
						boolean copierColler=!edtAppli.getCopierColler();
						edtAppli.setCopierColler(copierColler);
						
						/*menuCopierColler=new JCheckBoxMenuItem("Copier/Coller");
						menuCopierColler.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
						menuCopierColler.setActionCommand("copierColler");
						menuCopierColler.addActionListener(controleurMenu);
						menuCopierColler.setSelected(copierColler);
						menu_edition.add(menuCopierColler);*/
					}
					if (item.getText().equals("Ajouter une plage horaire")) {					
						if (getListePromotions().getValueText()!=null && getListePromotions().getValueText()!="") {
								GregorianCalendar debut=Utilitaire.calculerDate((String)edtAppli.getListeSemaines().getValueText());
								GregorianCalendar fin=Utilitaire.calculerDate((String)edtAppli.getListeSemaines().getValueText());
								PlageHoraire plage = new PlageHoraire(-1, debut, fin, "", 0, 0, 0, 0, false, 0);
								@SuppressWarnings("unused") FormulairePlageHoraire_wt form = new FormulairePlageHoraire_wt(edtAppli, plage);
						} else 	Utilitaire.showMessageDialog("Ajout de plage horaire", "Vous devez d'abord sélectionner une promotion !");					
					}					
					if (item.getText().equals("Dupliquer la semaine")) {					
						if (getListePromotions().getValueText()!=null && getListePromotions().getValueText()!="") {					
								@SuppressWarnings("unused") FormulaireDupliquerSemaine_wt form = new FormulaireDupliquerSemaine_wt(edtAppli);
						} else 	Utilitaire.showMessageDialog("Duplication de semaine", "Vous devez d'abord sélectionner une promotion !");
					}
					if (item.getText().equals("Supprimer la semaine")) {					
						if (getListePromotions().getValueText()!=null && getListePromotions().getValueText()!="") {
								WDialog confirmerSuppr = new WDialog("Suppression de semaine");								
								//confirmerSuppr.getContents().addWidget(Icon.Warning);
								WLabel	confirmerLabel = new WLabel("Êtes-vous sûr de vouloir supprimer tous les cours de la semaine pour la promotion sélectionnée ?", confirmerSuppr.getContents());
								WPushButton ok = new WPushButton("Oui", confirmerSuppr.getFooter());
								WPushButton annuler = new WPushButton("Non", confirmerSuppr.getFooter());
								
								ok.setStyleClass("btn-danger");
								annuler.setStyleClass("btn-primary");
								
								ok.clicked().addListener(ok, new Signal.Listener() {
										public void trigger() {
											String semaine= (String)edtAppli.getListeSemaines().getValueText();//.getSelectedItem();
											String promotion= (String)edtAppli.getListePromotions().getValueText();//.getSelectedItem();
											edtAppli.getControleurDb().supprimerPlagesHorairesSemaine(semaine, promotion);
											edtAppli.mettreAJour();
											confirmerSuppr.accept();
										}									
								});
								annuler.clicked().addListener(annuler, new Signal.Listener() {
										public void trigger() {
											confirmerSuppr.reject();		
										}
								});
								confirmerSuppr.setClosable(true);
								confirmerSuppr.rejectWhenEscapePressed();
								confirmerSuppr.show();
						} else 	Utilitaire.showMessageDialog("Suppression de semaine", "Vous devez d'abord sélectionner une promotion !");
					}
				}
			}
		);
		menuEditionPopup.itemSelected().trigger(menuEditionContainer);
		

		//MENU Gestion//////////////////////////////////////
		WMenu menuGestion = new WMenu();
		WMenuItem item = new WMenuItem("Gestion");
			
		WPopupMenu popup = new WPopupMenu();
		popup.addItem("Etudiants");
		popup.addItem("Matières");
		popup.addItem("Profs");
		popup.addItem("Salles");
		popup.addSeparator();
		popup.addItem("Promos");
		
		setLoadingIndicator(new WDefaultLoadingIndicator());
		popup.itemSelected().addListener(this,
			new Signal1.Listener<WMenuItem>() {
				public void trigger(WMenuItem item) {
					String itemstr = "";
					
					if (item.getText().equals("Etudiants")) {
						if (getListePromotions().getValueText()!=null && getListePromotions().getValueText()!="") {
							@SuppressWarnings("unused") FormulaireEtudiants_wt form = new FormulaireEtudiants_wt(edtAppli);
						} else Utilitaire.showMessageDialog("Gestion des Etudiants", "Vous devez d'abord sélectionner une promotion !");	
					}
					if (item.getText().equals("Matières")) {
						@SuppressWarnings("unused") FormulaireMatieres_wt form = new FormulaireMatieres_wt(edtAppli);						
					}
					if (item.getText().equals("Profs")) {
						if (getListePromotions().getValueText()!=null && getListePromotions().getValueText()!="") {
							@SuppressWarnings("unused") FormulaireProfs_wt form = new FormulaireProfs_wt(edtAppli);
						} else Utilitaire.showMessageDialog("Gestion des Profs", "Vous devez d'abord sélectionner une promotion !");
					}
					if (item.getText().equals("Salles")) {
						@SuppressWarnings("unused") FormulaireSalles_wt form = new FormulaireSalles_wt(edtAppli);
					}
					if (item.getText().equals("Promos")) {
						@SuppressWarnings("unused") FormulaireGroupes_wt form = new FormulaireGroupes_wt(edtAppli);	
					}					
				}
		});		
	
		item.setMenu(popup);
		menuGestion.addItem(item);
		navigation.addMenu(menuGestion, AlignmentFlag.AlignLeft);
		
		copypaste = new WCheckBox("Copier/Coller");
		navigation.addWidget(copypaste);

		
		
		
		getRoot().addWidget(container);
		
		WPushButton deconexion = new WPushButton("Déconnexion");
		deconexion.setStyleClass("btn-inverse");
		deconexion.clicked().addListener(this, new Signal.Listener() {
			public void trigger() {
				WDialog confirmerDeconexion = new WDialog("Déconnexion");
				WLabel confirmerLabel = new WLabel("Êtes-vous sûr de vouloir vous déconnecter ?", confirmerDeconexion.getContents());
				WPushButton ok = new WPushButton("Oui", confirmerDeconexion.getFooter());
				WPushButton annuler = new WPushButton("Non", confirmerDeconexion.getFooter());
				
				ok.setStyleClass("btn-danger");
				annuler.setStyleClass("btn-primary");
				
				ok.clicked().addListener(ok, new Signal.Listener() {
						public void trigger() {
							confirmerDeconexion.accept();	
							quitterEDT();							
						}
				});
				annuler.clicked().addListener(annuler, new Signal.Listener() {
						public void trigger() {
							confirmerDeconexion.reject();
						}
				});
				
				confirmerDeconexion.setClosable(true);
				confirmerDeconexion.rejectWhenEscapePressed();
				confirmerDeconexion.show();				
			}				
		});		

		navigation.addWidget(deconexion, AlignmentFlag.AlignRight);
		panneauHaut.setMargin(new WLength(20), Side.Right);
		navigation.addWidget(panneauHaut, AlignmentFlag.AlignRight);
		navigation.setMargin(0);

		
		
		// le menu
		/*ControleurMenu controleurMenu=new ControleurMenu(this);
		JMenuBar menuBar=new JMenuBar();
		// le menu GESTION
		/*WMenu menu_gestion=new WMenu("Gestion");
		menuBar.add(menu_gestion);
		WMenuItem item=new WMenuItem("Salles");
		/*item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));*/
        /*item.setActionCommand("salles");*/
		/*item.addActionListener(controleurMenu);*/
		/*menu_gestion.add(item);*/
		//getRoot().addWidget(menu_gestion);
		/*item=new JMenuItem("Groupes");
		//item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.ALT_MASK));
        item.setActionCommand("groupes");
		/ *item.addActionListener(controleurMenu);* /
		menu_gestion.add(item);
		item=new JMenuItem("Matières");
		//item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.ALT_MASK));
        item.setActionCommand("matières");
		/ *item.addActionListener(controleurMenu);* /
		menu_gestion.add(item);
		item=new JMenuItem("Profs");
		//item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.ALT_MASK));
        item.setActionCommand("profs");
		/ *item.addActionListener(controleurMenu);* /
		menu_gestion.add(item);
		menu_gestion.addSeparator();
        item=new JMenuItem("Etudiants");
        //item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.ALT_MASK));
        item.setActionCommand("étudiants");
        / *item.addActionListener(controleurMenu);* /
        menu_gestion.add(item);
        // le menu EDITION
		JMenu menu_edition=new JMenu("Edition");
		menuBar.add(menu_edition);
		menuCopierColler=new JCheckBoxMenuItem("Copier/Coller");
		menuCopierColler.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        menuCopierColler.setActionCommand("copierColler");
		/ *menuCopierColler.addActionListener(controleurMenu); * /
		menuCopierColler.setSelected(copierColler);
		menu_edition.add(menuCopierColler);
        menuHistoriquePlagesHoraires=new JCheckBoxMenuItem("Afficher historique plages horaires");
        menuHistoriquePlagesHoraires.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK));
        menuHistoriquePlagesHoraires.setActionCommand("historiquePlagesHoraires");
        / *menuHistoriquePlagesHoraires.addActionListener(controleurMenu);* /
        menuHistoriquePlagesHoraires.setSelected(historiquePlagesHoraires);
        menu_edition.add(menuHistoriquePlagesHoraires);
		item=new JMenuItem("Dupliquer semaine");
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
        item.setActionCommand("dupliquerSemaine");
		/ *item.addActionListener(controleurMenu);* /
		menu_edition.add(item);
        item=new JMenuItem("Supprimer semaine");
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        item.setActionCommand("supprimerSemaine");
        / *item.addActionListener(controleurMenu);* /
        menu_edition.add(item);
        // le menu AIDE
        JMenu menu_aide=new JMenu("Aide");
        menuBar.add(menu_aide);
        item=new JMenuItem("À propos");
        / *item.addActionListener(controleurMenu);* /
        item.setActionCommand("aPropos");
        menu_aide.add(item);
        item=new JMenuItem("Historique");
        / *item.addActionListener(controleurMenu);* /
        item.setActionCommand("historique");
        menu_aide.add(item);
        item=new JMenuItem("SOS/question");
        / *item.addActionListener(controleurMenu);* /
        item.setActionCommand("sos");
        menu_aide.add(item);
        / *if (controleurDB.isAdmin()) {* /
            menuBar=new JMenuBar();
            JMenu m_gestion=new JMenu("Gestion");
            menuBar.add(m_gestion);
            // gestion des administrateurs
            item=new JMenuItem("Administrateurs");
            //item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.ALT_MASK));
            item.setActionCommand("administrateurs");
            / *item.addActionListener(controleurMenu);* /
            m_gestion.add(item);
            // gestion des départements
            item=new JMenuItem("Départements");
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.ALT_MASK));
            item.setActionCommand("départements");
            / *item.addActionListener(controleurMenu);* /
            m_gestion.add(item);
        / *}* /
        setJMenuBar(menuBar);        
		
      	// affichage de la fenetre			
		pack();
		Dimension d=getToolkit().getScreenSize();
        int tailleHorizontale=Math.max(800,(int)(d.getWidth())/2); // au moins 600 pixels de large
        int tailleVerticale=Math.max(600,(int)(d.getHeight())/2); // au moins 500 pixels de haut
		setSize(tailleHorizontale, tailleVerticale);
		setLocation(((int)(d.getWidth())-tailleHorizontale)/2, ((int)(d.getHeight())-tailleVerticale)/2);
		// setResizable(false);
		setVisible(true);*/	
	}

	public void rechercherLundis() {
		// date d'aujourd'hui
		SimpleTimeZone tz=new SimpleTimeZone(1*60*60*1000, "CET"); // GMT+1
		tz.setStartRule(Calendar.MARCH, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
		tz.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
		GregorianCalendar calendrier = new GregorianCalendar(tz);
		// on cherche le lundi de la semaine en cours
		while (calendrier.get(Calendar.DAY_OF_WEEK)!=Calendar.MONDAY) // on cherche le lundi qui vient de passer
			calendrier.setTimeInMillis(calendrier.getTimeInMillis()-86400000); // en retirant 24 heures en millisecondes
		String aujourdhui=""+calendrier.get(Calendar.DATE)+"/"+(calendrier.get(Calendar.MONTH) + 1)+"/"+calendrier.get(Calendar.YEAR);
		// on cherche la fin de l'année scolaire
		int annee = calendrier.get(Calendar.YEAR); // année en cours
		int mois = calendrier.get(Calendar.MONTH); // mois en cours
		if (mois>=Calendar.JULY) // on est aujourd'hui entre juillet et décembre
			annee++; // la fin de l'année scolaire en cours est l'an prochain
		calendrier.set(annee, Calendar.AUGUST, 31, 12, 0); // on se place le 31 aout à la fin de l'année scolaire en cours à midi
		while (calendrier.get(Calendar.DAY_OF_WEEK)!=Calendar.MONDAY) // on cherche le lundi précédent
			calendrier.setTimeInMillis(calendrier.getTimeInMillis()-86400000); // en retirant 24 heures
		String finannee1=""+calendrier.get(Calendar.DATE)+"/"+(calendrier.get(Calendar.MONTH) + 1)+"/"+calendrier.get(Calendar.YEAR);
		calendrier.setTimeInMillis(calendrier.getTimeInMillis()+86400000*7); // on avance de 7 jours
		String finannee2=""+calendrier.get(Calendar.DATE)+"/"+(calendrier.get(Calendar.MONTH) + 1)+"/"+calendrier.get(Calendar.YEAR);
		calendrier.setTimeInMillis(calendrier.getTimeInMillis()+86400000*7); // on avance de 7 jours
		String finannee3=""+calendrier.get(Calendar.DATE)+"/"+(calendrier.get(Calendar.MONTH) + 1)+"/"+calendrier.get(Calendar.YEAR);
		// on cherche le début de l'année scolaire
        // if (mois>=Calendar.JULY) // on est aujourd'hui entre juillet et décembre
            annee--; //on revient en début d'année scolaire
        // on était ci-dessus en fin d'année scolaire, on revient en début d'année scolaire
		calendrier.set(annee, Calendar.AUGUST, 31, 12, 0); // on se place le 31 aout au début de l'année scolaire en cours
		while (calendrier.get(Calendar.DAY_OF_WEEK)!=Calendar.MONDAY) // on cherche le lundi précédent
			calendrier.setTimeInMillis(calendrier.getTimeInMillis()-86400000); // en retirant 24 heures
		// boucle de recherche des lundis de l'année scolaire, jusqu'à fin juin
		String jour;
		while (calendrier.get(Calendar.MONTH)!=Calendar.JULY) { // on affiche jusqu'au premier lundi de juillet
			//ajout des semaines
			jour=""+calendrier.get(Calendar.DAY_OF_MONTH)+"/"+(calendrier.get(Calendar.MONTH) + 1)+"/"+calendrier.get(Calendar.YEAR);
			listeSemaines.addItem(jour);
			calendrier.setTimeInMillis(calendrier.getTimeInMillis() + 604800000); // on avance d'une semaine
		}
        if (mois>=Calendar.MAY && mois<=Calendar.AUGUST) { // on est en fin d'année scolaire (après mai), on affiche en plus l'année scolaire suivante
            annee++; // on était ci-dessus en début d'année scolaire, on passe en fin d'année scolaire
            calendrier.set(annee, Calendar.AUGUST, 31, 12, 0); // on se place le 31 aout à la fin de l'année scolaire en cours à midi
            while (calendrier.get(Calendar.DAY_OF_WEEK)!=Calendar.MONDAY) // on cherche le lundi précédent
                calendrier.setTimeInMillis(calendrier.getTimeInMillis()-86400000); // en retirant 24 heures
            while (calendrier.get(Calendar.MONTH)!=Calendar.JULY) { // on affiche jusqu'au dernier lundi de juin suivant (année scolaire suivante)
                //ajout des semaines
                jour=""+calendrier.get(Calendar.DAY_OF_MONTH)+"/"+(calendrier.get(Calendar.MONTH) + 1)+"/"+calendrier.get(Calendar.YEAR);
                listeSemaines.addItem(jour);
                calendrier.setTimeInMillis(calendrier.getTimeInMillis() + 604800000); // on avance d'une semaine
            }
        }
        else { // on ajoute la fin de l'année scolaire (fin aout)
            listeSemaines.addItem(finannee1);
            listeSemaines.addItem(finannee2);
            listeSemaines.addItem(finannee3);
        }
		// on se place sur la semaine en cours
		/*listeSemaines.setSelectedItem(aujourdhui);*/
		listeSemaines.setValueText(aujourdhui);
	}

	public void mettreAJourPromotions() {
		if (controleurDB!=null) {
			Vector<String> listePromo=controleurDB.getPromotions(controleurDB.getIdAdmin());
			if (listePromo.size()+1!=listePromotions.getCount()) { // les promos ont changé (+1 pour la promo vide) .getItemCount
				/*listePromotions.removeItemListener(controleurGUI);*/
				String temp=(String)listePromotions.getValueText(); //SelectedItem();
				if (temp==null) temp=""; // si rien n'est sélectionné, on considère que la ligne vide l'est (la première ligne)
				listePromotions.clear(); //removeAllItems();
				for (Iterator<String> iter = listePromo.iterator(); iter.hasNext();)
					listePromotions.addItem(iter.next());
				listePromotions.setValueText(temp);	//setSelectedItem(temp);
				/*listePromotions.addItemListener(controleurGUI);*/
			}
		}
	}
	
	public ControleurDB getControleurDb() {
		return controleurDB;
	}
	public void setControleurDb(ControleurDB _db) {
		controleurDB=_db;
	}

	public WComboBox getListePromotions() {
		return listePromotions;
	}

	public WComboBox getListeSemaines() {
		return listeSemaines;
	}

	public VueEDT_wt getVueEDT() {
		return vueEDT_wt;
	}

	public void setVueEDT(VueEDT_wt vueEDT_wt) {
		this.vueEDT_wt = vueEDT_wt;
	}

	public void setCopierColler(boolean val) {
		copierColler=val;
	}

	public void setLoginEDT(String val) {
		loginEDT=val;
	}

	public String getLoginEDT() {
		return loginEDT;
	}

	public boolean getCopierColler() {
		return copierColler;
	}

    public void setHistoriquePlagesHoraires(boolean val) {
        historiquePlagesHoraires=val;
    }

    public boolean getHistoriquePlagesHoraires() {
        return historiquePlagesHoraires;
    }
    
    // mise à jour à partir de la base de données
	public void mettreAJour() {
		mettreAJourPromotions();
		//System.out.println("dans rafraichir : " + (PlageHoraire)(vueEDT.getEdtSemaine().getListePlages().get(0)));
		vueEDT_wt.mettreAJour();
	}

    /*public static void main(String[] args) {
        @SuppressWarnings("unused") EDTApplication edtAppli=new EDTApplication();
    }*/
    
    public WContainerWidget getPanneauHaut() {
    	return panneauHaut;	
    }
    
    public WContainerWidget getContainer() {
    	return container;	
    }
    
    public WCheckBox getCopypaste() {
    	return copypaste;	
    }
    
    private void quitterEDT() {
		if (getControleurDb() != null)
			getControleurDb().closeDB();
		getRoot().setHidden(true);
		quit();
		System.gc();							
		edtAppli.redirect("https://edt.iut.univ-paris8.fr");
    }
} //835