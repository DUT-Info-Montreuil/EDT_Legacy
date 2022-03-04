package EDT;

import eu.webtoolkit.jwt.WApplication;
import eu.webtoolkit.jwt.WEnvironment;
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
import eu.webtoolkit.jwt.AlignmentFlag;
import eu.webtoolkit.jwt.WDefaultLoadingIndicator;
import eu.webtoolkit.jwt.Side;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WTimer;
import eu.webtoolkit.jwt.WLength;
import eu.webtoolkit.jwt.WCheckBox;

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
	private WComboBox listeSemaines;
	private WComboBox listePromotions;
	private VueEDT_wt vueEDT_wt;
	private ControleurDB controleurDB;
	WContainerWidget panneauHaut = new WContainerWidget();
	WContainerWidget container = new WContainerWidget();
    EDTApplication_wt edtAppli = this;
	String version,dateVersion;
    String loginEDT;
    WCheckBox copypaste;
    WCheckBox historiquePlagesHoraires;
	HashMap<Integer,Integer> notifsGroups = new HashMap<Integer,Integer> ();
	//private VueBlanche_wt vueBlanche_wt;
	//private VueEDT_Tmp_wt vueEDT_Tmp_wt=null;
	
	public EDTApplication_wt(WEnvironment env) {
		super(env);
        setTheme(new WBootstrapTheme());
        version="2.0";
        setTitle("Editeur d'emploi du temps");// version "+version);
		new FenetreLogin_wt(this);
	}
	public void afficher() {
		//loginEDT="testauth";
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


		WTimer timer = new WTimer(this);
		timer.setSingleShot(true);
		timer.setInterval(60000*60*6);	//cnx:12hmax
		timer.start();
		timer.timeout().addListener(this, new Signal.Listener() {
			public void trigger() {
				if (getControleurDb()!=null) {
					Utilitaire.showMessageDialog("this","Vous êtes déconnecté!");
					quitterEDT();
				}		
			}				
		});
		

		listeSemaines = new WComboBox();
		listeSemaines.changed().addListener(listeSemaines, new Signal.Listener() {
				public void trigger() {					
					mettreAJour();
				}
		});		
	
		
		rechercherLundis();		
		
		listePromotions = new WComboBox();
		listePromotions.changed().addListener(listePromotions, new Signal.Listener() {
				public void trigger() {
					mettreAJour();				
				}
		});		
	
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
		
		edtAppli.globalKeyWentDown().addListener(edtAppli, new ControleurGUI_wt(edtAppli));
		
		/*if (controleurDB.isAdmin()) // on n'affiche pas les promos pour l'administrateur*/
		panneauHaut.addWidget(listePromotions);
		panneauHaut.addWidget(new WLabel(" "));
	    panneauHaut.addWidget(b_prec);
		panneauHaut.addWidget(listeSemaines);
		panneauHaut.addWidget(b_suiv);
		
		
		////////////////////////////////////////////////////////////
		//////////////////MENU PRINCIPAL////////////////////////////
		////////////////////////////////////////////////////////////
		WNavigationBar navigation = new WNavigationBar(container);
		navigation.setTitle("EditorEDT"+version);
		

		//MENU Administration//////////////////////////////
		
		if (loginEDT.compareTo("hassoun")==0 || loginEDT.compareTo("voliveira")==0 || loginEDT.compareTo("test03")==0)
		{
			WMenu menuAdmin = new WMenu();
			WMenuItem menuAdminContainer = new WMenuItem("Administration");
			WPopupMenu menuAdminPopup = new WPopupMenu();
			menuAdminPopup.addItem("Administrateurs");
			menuAdminPopup.addItem("Départements");
			menuAdminPopup.addItem("Promotions");
			menuAdminPopup.addItem("Professeurs");
			menuAdminPopup.addItem("Matières");
			menuAdminPopup.addItem("Cleaner");
			menuAdminContainer.setMenu(menuAdminPopup);
			menuAdmin.addItem(menuAdminContainer);
			navigation.addMenu(menuAdmin, AlignmentFlag.AlignLeft);
		
			menuAdminPopup.itemSelected().addListener(this,
				new Signal1.Listener<WMenuItem>() {
					@SuppressWarnings("unlikely-arg-type")
					public void trigger(WMenuItem item) {
						if (item.getText().equals("Administrateurs")) {
							new FormulaireAdministrateurs_wt(edtAppli);	
						}
						if (item.getText().equals("Départements")) {
							new FormulaireDepartements_wt(edtAppli);
						}	
						if (item.getText().equals("Promotions")) {
							new FormulairePromotion_wt(edtAppli);
						}
						if (item.getText().equals("Professeurs")) {
							new FormulaireProfAdmin_wt(edtAppli);
						}
						if (item.getText().equals("Matières")) {
							new FormulaireMatiereAdmin_wt(edtAppli);
						}
						if (item.getText().equals("Cleaner")) {
							new FormulaireCleanerAdmin_wt(edtAppli);
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
				@SuppressWarnings("unlikely-arg-type")
				public void trigger(WMenuItem item) {
					if (item.getText().equals("Ajouter une plage horaire")) {					
						if (getListePromotions().getValueText()!=null && getListePromotions().getValueText()!="") {
								GregorianCalendar debut=Utilitaire.calculerDate((String)edtAppli.getListeSemaines().getValueText());
								GregorianCalendar fin=Utilitaire.calculerDate((String)edtAppli.getListeSemaines().getValueText());
								PlageHoraire plage = new PlageHoraire(-1, debut, fin, "", 0, 0, 0, 0, false, 0);
								new FormulairePlageHoraire_wt(edtAppli, plage);
						} else 	Utilitaire.showMessageDialog("Ajout de plage horaire", "Vous devez d'abord sélectionner une promotion !");					
					}					
					if (item.getText().equals("Dupliquer la semaine")) {					
						if (getListePromotions().getValueText()!=null && getListePromotions().getValueText()!="") {					
								new FormulaireDupliquerSemaine_wt(edtAppli);
						} else 	Utilitaire.showMessageDialog("Duplication de semaine", "Vous devez d'abord sélectionner une promotion !");
					}
					if (item.getText().equals("Supprimer la semaine")) {					
						if (getListePromotions().getValueText()!=null && getListePromotions().getValueText()!="") {
								WDialog confirmerSuppr = new WDialog("Suppression de semaine");								
								new WLabel("Êtes-vous sûr de vouloir supprimer tous les cours de la semaine pour la promotion sélectionnée ?", confirmerSuppr.getContents());
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
				@SuppressWarnings("unlikely-arg-type")
				public void trigger(WMenuItem item) {
					if (item.getText().equals("Etudiants")) {
						if (getListePromotions().getValueText()!=null && getListePromotions().getValueText()!="") {
							new FormulaireEtudiants_wt(edtAppli);
						} else Utilitaire.showMessageDialog("Gestion des Etudiants", "Vous devez d'abord sélectionner une promotion !");	
					}
					if (item.getText().equals("Matières")) {
						if (getListePromotions().getValueText()!=null && getListePromotions().getValueText()!="") {
							new FormulaireMatieres_wt(edtAppli);		
						} else Utilitaire.showMessageDialog("Gestion des Matières", "Vous devez d'abord sélectionner une promotion !");
						
					}
					if (item.getText().equals("Profs")) {
						if (getListePromotions().getValueText()!=null && getListePromotions().getValueText()!="") {
							new FormulaireProfs_wt(edtAppli);
						} else Utilitaire.showMessageDialog("Gestion des Profs", "Vous devez d'abord sélectionner une promotion !");
					}
					if (item.getText().equals("Salles")) {
						new FormulaireSalles_wt(edtAppli);
					}
					if (item.getText().equals("Promos")) {
						new FormulaireGroupes_wt(edtAppli);	
					}					
				}
		});		
	
		item.setMenu(popup);
		menuGestion.addItem(item);
		navigation.addMenu(menuGestion, AlignmentFlag.AlignLeft);
		
		
		copypaste = new WCheckBox("Copier/Coller");
		navigation.addWidget(copypaste,AlignmentFlag.AlignLeft);

		
		WContainerWidget container3 = new WContainerWidget();
		container3.resize(30, 1);
		navigation.addWidget(container3, AlignmentFlag.AlignLeft);
		
		
		historiquePlagesHoraires = new WCheckBox("Historique");
		navigation.addWidget(historiquePlagesHoraires,AlignmentFlag.AlignLeft);
		
		
		WContainerWidget container4 = new WContainerWidget();
		container4.resize(30, 1);
		navigation.addWidget(container4, AlignmentFlag.AlignLeft);
		
		
		WPushButton b_notif = new WPushButton("NOTIF");
		b_notif.clicked().addListener(b_notif, new Signal.Listener() {
	    	public void trigger() {
	            new FormulaireNotif_wt(edtAppli,notifsGroups);
	        }
	    });
		b_notif.setStyleClass("btn-danger");     
		navigation.addWidget(b_notif, AlignmentFlag.AlignLeft);
		
		
		getRoot().addWidget(container);
		
		WPushButton deconexion = new WPushButton("Déconnexion");
		deconexion.setStyleClass("btn-inverse");
		deconexion.clicked().addListener(this, new Signal.Listener() {
			public void trigger() {
				WDialog confirmerDeconexion = new WDialog("Déconnexion");
				new WLabel("Êtes-vous sûr de vouloir vous déconnecter ?", confirmerDeconexion.getContents());
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

	public void setLoginEDT(String val) {
		loginEDT=val;
	}

	public String getLoginEDT() {
		return loginEDT;
	}

    
    // mise à jour à partir de la base de données
	public void mettreAJour() {
		mettreAJourPromotions();
		//System.out.println("dans rafraichir : " + (PlageHoraire)(vueEDT.getEdtSemaine().getListePlages().get(0)));
		vueEDT_wt.mettreAJour();
	}
	
    public WContainerWidget getPanneauHaut() {
    	return panneauHaut;	
    }
    
    public WContainerWidget getContainer() {
    	return container;	
    }
    
	public String getDateVersion() {
		return dateVersion;
	}
	
	public WCheckBox getCopypaste() {
		return copypaste;
	}
	
	public VueEDT_wt getVueEDT_wt() {
		return vueEDT_wt;
	}
	
	public ControleurDB getControleurDB() {
		return controleurDB;
	}
	
	public EDTApplication_wt getEdtAppli() {
		return edtAppli;
	}
	
	public String getVersion() {
		return version;
	}
	
	public WCheckBox getHistoriquePlagesHoraires() {
		return historiquePlagesHoraires;
	}

	public void setHistoriquePlagesHoraires(WCheckBox historiquePlagesHoraires) {
		this.historiquePlagesHoraires = historiquePlagesHoraires;
	}
 
	public HashMap<Integer,Integer> getNotifsGroups() {
		return notifsGroups;
	}
	
    private void quitterEDT() {
		if (getControleurDb() != null)
			getControleurDb().closeDB();
		getRoot().setHidden(true);
		quit();
		System.gc();							
		edtAppli.redirect("https://edt.iut.univ-paris8.fr");
    }

//	public VueBlanche_wt getVueBlanche_wt() {
//		return vueBlanche_wt;
//	}
//
//	public void setVueBlanche_wt(VueBlanche_wt vueBlanche_wt) {
//		this.vueBlanche_wt = vueBlanche_wt;
//	}
//	public void mettreAJourTmp() {
//	//System.out.println("dans rafraichir : " + (PlageHoraire)(vueEDT.getEdtSemaine().getListePlages().get(0)));
//	vueEDT_Tmp_wt.mettreAJour();
//}
//
//	public void mettreAJourBlanche(PlageHoraire e) {
//	//System.out.println("dans rafraichir : " + (PlageHoraire)(vueEDT.getEdtSemaine().getListePlages().get(0)));
//	vueBlanche_wt.mettreAJour(e);
//}
//	public VueEDT_Tmp_wt getVueEDT_Tmp_wt() {
//	return vueEDT_Tmp_wt;
//}
//
//public void setVueEDT_Tmp_wt(VueEDT_Tmp_wt vueEDT_Tmp_wt) {
//	this.vueEDT_Tmp_wt = vueEDT_Tmp_wt;
//}
    
    
} 
