package EDT;

import eu.webtoolkit.jwt.WDialog;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WLabel;
import eu.webtoolkit.jwt.WLineEdit;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WCheckBox;
import eu.webtoolkit.jwt.WBorderLayout;
import eu.webtoolkit.jwt.WGridLayout;
import eu.webtoolkit.jwt.WRadioButton;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WObject;
import eu.webtoolkit.jwt.WButtonGroup;
import eu.webtoolkit.jwt.WText;
import eu.webtoolkit.jwt.WRegExpValidator;
import eu.webtoolkit.jwt.WValidator;
import eu.webtoolkit.jwt.WSuggestionPopup;
import eu.webtoolkit.jwt.WLength;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import eu.webtoolkit.jwt.WFileUpload;
import eu.webtoolkit.jwt.WFileResource;
import eu.webtoolkit.jwt.FileUtils;
import eu.webtoolkit.jwt.servlet.UploadedFile;
//import eu.webtoolkit.jwt.UploadedFile;


import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

/**
 * formulaire d'ajout ou de modification d'étudiant
 */
/*public class FormulaireSaisieEtudiant extends JDialog implements ActionListener { */
public class FormulaireSaisieEtudiant_wt extends WDialog {
	Etudiant etudiant;
	EDTApplication_wt edtAppli;
    WLabel l_prenom,l_nom,l_login;
    WLineEdit tf_prenom, tf_nom, tf_login, tf_nomfichier;
	WPushButton b_valider, b_annuler, b_parcourir;
    WRadioButton rb_fichier,rb_manuel;
    ArrayList<Groupe>listeGroupesPromo;
    WCheckBox[] cb_groupes;
    File fichier_etudiants;
    
    //UploadedFile fichier_etudiants;
    WFileUpload fu;
    //UploadedFile fuploaded;
    WFileResource fileResource;
    FileInputStream fileInputStream;
    WLabel label;
    WLineEdit loginEdit = new WLineEdit();
    WRegExpValidator validator = new WRegExpValidator("^[A-Za-z][A-Za-z][A-Za-z]*[0-9]*$");
    //WRegExpValidator validator = new WRegExpValidator("[A-Za-z][A-Za-z]");
    

	FormulaireSaisieEtudiant_wt(EDTApplication_wt _edtAppli, Etudiant _etudiant, WDialog dial) {
		/*super(dial, "Ajout/modif d'étudiant", true);*/
		super("Ajout/modif d'étudiant");
		
		/*//procedure qui masque et ferme la fenetre
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose(); // on ferme la fenêtre
			}
		});*/
		edtAppli=_edtAppli;
		etudiant=_etudiant;
		/*JPanel panel=new JPanel();
		panel.setLayout(new BorderLayout());*/
		WContainerWidget panel = new WContainerWidget();
		WBorderLayout grid = new WBorderLayout();
		panel.setLayout(grid);
		
		// les informations sur l'étudiant
		/*JPanel panelEtudiant=new JPanel();*/
		WContainerWidget panelEtudiant = new WContainerWidget();
		/*listeGroupesPromo=new ArrayList<Groupe>(edtAppli.getControleurDb().getListeGroupes((String)(edtAppli.getListePromotions().getSelectedItem())));*/
        listeGroupesPromo=new ArrayList<Groupe>(edtAppli.getControleurDb().getListeGroupes((String)(edtAppli.getListePromotions().getValueText())));
		/*panelEtudiant.setLayout(new GridLayout(5+listeGroupesPromo.size(),2)); // boutons radio, fichier, nom, prénom, login + les groupes*/
		WGridLayout gridEtudiant = new WGridLayout();
		panelEtudiant.setLayout(gridEtudiant);		
		
		/*l_prenom=new JLabel("Prénom :");
        l_nom=new JLabel("Nom");
        l_login=new JLabel("login");*/
        l_prenom=new WLabel("prénom :");
        l_nom=new WLabel("nom :");
        l_login=new WLabel("login :");
        
        /*cb_groupes=new JCheckBox[listeGroupesPromo.size()];
        rb_fichier=new JRadioButton("Fichier");
        rb_manuel=new JRadioButton("Manuel");
        rb_fichier.addActionListener(this);
        rb_manuel.addActionListener(this);
        rb_fichier.setActionCommand("fichier");
        rb_manuel.setActionCommand("manuel");
        rb_fichier.setSelected(true);*/
        cb_groupes=new WCheckBox[listeGroupesPromo.size()];
        rb_fichier=new WRadioButton("Fichier");
        rb_manuel=new WRadioButton("Manuel");
        rb_manuel.checked().addListener(rb_manuel, new Signal.Listener() {
        		public void trigger() {
        			action("manuel");
        		}        		
        });
        rb_fichier.checked().addListener(rb_fichier, new Signal.Listener() {
        		public void trigger() {
        			action("fichier");	
        		}
        });
        rb_fichier.setChecked(true);
		
		/*b_valider=new JButton("Valider");
		b_annuler=new JButton("Annuler");*/
		b_valider=new WPushButton("Valider");
		b_annuler=new WPushButton("Annuler");
		
		/*ButtonGroup gr=new ButtonGroup();*/
        WButtonGroup gr=new WButtonGroup();
        
        /*gr.add(rb_fichier);
        gr.add(rb_manuel);*/
        gr.addButton(rb_fichier);
        gr.addButton(rb_manuel);
        
        /*tf_nomfichier=new JTextField();
        tf_prenom=new JTextField();
        tf_nom=new JTextField();
        tf_login=new JTextField();*/
        tf_nomfichier=new WLineEdit();
        tf_prenom=new WLineEdit();        
        tf_nom=new WLineEdit();        
        tf_login=new WLineEdit();
        
        //comme ds la table Etudiant
        tf_prenom.setMaxLength(80);
        tf_nom.setMaxLength(80);
        tf_login.setMaxLength(30);
        
        fu = new WFileUpload();
        fu.setAttributeValue("label", "label");
        fu.setAttributeValue("text", "text");
        fu.setAttributeValue("caption", "caption");
        fu.setAttributeValue("value", "value");
		if (etudiant!=null) {
			/*tf_prenom.setText(etudiant.getPrenom());
			tf_nom.setText(etudiant.getNom());
            tf_login.setText(etudiant.getLogin());
            panelEtudiant.add(l_prenom);
            panelEtudiant.add(tf_prenom);
            panelEtudiant.add(l_nom);
            panelEtudiant.add(tf_nom);
            panelEtudiant.add(l_login);
            panelEtudiant.add(tf_login);
            rb_manuel.setSelected(true);*/
            tf_prenom.setText(etudiant.getPrenom());
			tf_nom.setText(etudiant.getNom());
            tf_login.setText(etudiant.getLogin());
            gridEtudiant.addWidget(l_prenom, 0, 0);
            gridEtudiant.addWidget(tf_prenom, 0, 1);
            gridEtudiant.addWidget(l_nom, 1, 0);
            gridEtudiant.addWidget(tf_nom, 1, 1);
            gridEtudiant.addWidget(l_login, 2, 0);
            gridEtudiant.addWidget(tf_login, 2, 1);
            rb_manuel.setChecked(true);
            
            b_valider.setDisabled(false);
		} else {
			/*l_prenom.setEnabled(false);
		    l_nom.setEnabled(false);
            l_login.setEnabled(false);
		    tf_prenom.setEnabled(false);
		    tf_nom.setEnabled(false);
            tf_login.setEnabled(false);
            panelEtudiant.add(rb_fichier);
            panelEtudiant.add(rb_manuel);
            panelEtudiant.add(l_prenom);
            panelEtudiant.add(tf_prenom);
            panelEtudiant.add(l_nom);
            panelEtudiant.add(tf_nom);
            panelEtudiant.add(l_login);
            panelEtudiant.add(tf_login);
            b_parcourir=new JButton("Fichier (prénom;nom)...");
            b_parcourir.addActionListener(this);
            b_parcourir.setActionCommand("parcourir");
            panelEtudiant.add(b_parcourir);
            panelEtudiant.add(tf_nomfichier);*/
		    l_prenom.setDisabled(true);
		    l_nom.setDisabled(true);
            l_login.setDisabled(true);
		    tf_prenom.setDisabled(true);
		    tf_nom.setDisabled(true);
            tf_login.setDisabled(true);
            gridEtudiant.addWidget(rb_fichier, 0, 0);
            gridEtudiant.addWidget(rb_manuel, 0, 1);
            gridEtudiant.addWidget(l_prenom, 1, 0);
            gridEtudiant.addWidget(tf_prenom, 1, 2);
            gridEtudiant.addWidget(l_nom, 2, 0);
            gridEtudiant.addWidget(tf_nom, 2, 2);
            gridEtudiant.addWidget(l_login, 3, 0);
            gridEtudiant.addWidget(tf_login, 3, 2);
            //gridEtudiant.addWidget(loginEdit, 3, 1);
            gridEtudiant.addWidget(new WLabel("fichier CSV :"), 4, 0);	//(avec chemin complet) 

            WText out = new WText();
            gridEtudiant.addWidget(fu, 4, 2);
            //fu.setAttributeValue("style", "text-align:left");
            fu.changed().addListener(fu, new Signal.Listener() {
            		public void trigger() {
            			fu.upload();
            		}
            });
            fu.uploaded().addListener(fu, new Signal.Listener() {
            		public void trigger() {
            			out.setText("File upload is changed");	
            		}
            });
            fu.fileTooLarge().addListener(fu, new Signal.Listener() {
            		public void trigger() {
            			out.setText("File is too large");	
            		}
            });
        }
//Suggestions        
WSuggestionPopup.Options loginOptions = new WSuggestionPopup.Options();
loginOptions.highlightBeginTag = "<span class=\"highlight\">";
loginOptions.highlightEndTag = "</span>";
loginOptions.wordSeparators = " ";
WSuggestionPopup sp = new WSuggestionPopup(
								WSuggestionPopup.generateMatcherJS(loginOptions),
								WSuggestionPopup.generateReplacerJS(loginOptions),
								this);
//WLineEdit loginEdit = new WLineEdit();
tf_login.setEmptyText("");
sp.forEdit(tf_login);
sp.addSuggestion("bsilva");
sp.addSuggestion("ctristan");
sp.addSuggestion("cizueut");
sp.addSuggestion("fcosta");
//gridEtudiant.addWidget(loginEdit, 4, 1);        
        
        validator.setMandatory(true);
        tf_login.setValidator(validator);
///////////        
        
        // on récupère les groupes auxquels appartient l'étudiant à modifier
        ArrayList<Groupe>listeGroupesEtudiant=null;
        if (etudiant!=null)
            listeGroupesEtudiant=new ArrayList<Groupe>(edtAppli.getControleurDb().getListeGroupesEtudiant(etudiant.getIdEtudiant()));
		// affichage des groupes de la promo
        int index=0;
        WContainerWidget panelGroupe = new WContainerWidget();
        for (Iterator<Groupe> iter = listeGroupesPromo.iterator(); iter.hasNext();) {
            Groupe g=iter.next();
            /*cb_groupes[index]=new JCheckBox(g.getNom());*/
            cb_groupes[index]=new WCheckBox(g.getNom());
            if (etudiant!=null && listeGroupesEtudiant.contains(g))
                cb_groupes[index].setChecked(true);
            panelGroupe.addWidget(cb_groupes[index++]);
            /*panelEtudiant.add(new JLabel(""));*/
        }
        // le cours doit toujours contenir l'étudiant !
        cb_groupes[0].setEnabled(false);
        cb_groupes[0].setChecked(true);
		// les boutons
		/*JPanel panelBoutons=new JPanel();
		panelBoutons.setLayout(new GridLayout(1,2));*/
		//WContainerWidget panelBoutons = new WContainerWidget();
		//WGridLayout gridBoutons = new WGridLayout();
		//panelBoutons.setLayout(gridBoutons);

			
		/*b_valider.addActionListener(this);
		b_annuler.addActionListener(this);
        b_valider.setActionCommand("valider");
        b_annuler.setActionCommand("annuler");
		panelBoutons.add(b_valider);
		panelBoutons.add(b_annuler);*/
		b_valider.clicked().addListener(b_valider, new Signal.Listener() {
				public void trigger() {
					if (tf_login.validate() == WValidator.State.Valid || tf_nomfichier.isEnabled()) {
						action("valider");
						//accept();					
						//FormulaireEtudiants_wt form = new FormulaireEtudiants_wt(edtAppli);
					} else {
						b_valider.setDisabled(true);	
					}
				}
		});
		b_annuler.clicked().addListener(b_annuler, new Signal.Listener() {
			public void trigger() {
					remove();
					FormulaireEtudiants_wt form = new FormulaireEtudiants_wt(edtAppli);
			}
		});
		b_valider.setStyleClass("btn-info");
		b_annuler.setStyleClass("btn-primary");
		getFooter().addWidget(b_valider);
		getFooter().addWidget(b_annuler);

		tf_login.keyWentUp().addListener(tf_login, new Signal.Listener() {
				public void trigger() {
					//Utilitaire.showMessageDialog("valider",loginEdit.validate().toString());
					b_valider.setDisabled(tf_login.validate() != WValidator.State.Valid);	
				}
		});
		tf_login.changed().addListener(tf_login, new Signal.Listener() {
				public void trigger() {
					//Utilitaire.showMessageDialog("valider",loginEdit.validate().toString());
					b_valider.setDisabled(tf_login.validate() != WValidator.State.Valid);	
				}
		});	

		
		// on ajoute tout dans la fenetre
		/*panel.add(panelEtudiant, BorderLayout.CENTER);
		panel.add(panelBoutons, BorderLayout.SOUTH);
		getContentPane().add(panel);*/
		grid.addWidget(panelEtudiant, WBorderLayout.Position.North);
		grid.addWidget(panelGroupe, WBorderLayout.Position.Center);
		//getFooter().addWidget(panelBoutons);
		//label = new WLabel("fichier:<input type='file'/>");
		//getContents().addWidget(label);		
		getContents().addWidget(panel);
		
		// positionner au centre de l'écran
		/*setResizable(false);
		pack();
		Rectangle rect=edtAppli.getBounds(); // getToolkit().getScreenSize();
		setLocation(Math.max(0,(int)(rect.x+(rect.width-getWidth())/2)), Math.max(0,(int)(rect.y+(rect.height-getHeight())/2)));*/
		this.setResizable(true);
		this.resize(700, 500);
		this.show();		
	}
	
	public void action(String action) {    
/*	public void actionPerformed(ActionEvent e) {
	String action = ((AbstractButton)e.getSource()).getActionCommand();*/
		
		if (action.equals("valider")) {
			String nom=null,prenom=null,login=null;
            TreeSet<Etudiant>listeEtudiants=new TreeSet<Etudiant>();
            // on vérifie les saisies
            if (rb_manuel.isChecked()) {
            	/*nom=tf_nom.getText();
                prenom=tf_prenom.getText();
                login=tf_login.getText();
                nom=nom.replaceAll("[ '\"]",""); // on enlève espaces et guillemets
                prenom.replaceAll("[ '\"]",""); // on enlève espaces et guillemets
                login=tf_login.getText();
                login.replaceAll("[a-zA-Z]",""); // on enlève les caractères non alphabétiques
                login=login.substring(0,Math.min(16,login.length())); // on tronque à 15 caractères*/
                prenom=tf_prenom.getText().trim().toLowerCase();
            	nom=tf_nom.getText().trim().toLowerCase();                
                login=tf_login.getText().toLowerCase();
                login=login.substring(0,Math.min(30,login.length())); // on tronque à 30 caractères(comme ds la table Etudiant)
                
                if (nom.length()>0 && prenom.length()>0) {
                	if (nom.replaceAll("[a-zA-Z -]","").length()+prenom.replaceAll("[a-zA-Z -]","").length()==0)
                    	listeEtudiants.add(new Etudiant(-1,prenom,nom,login));
                    else {
                    	Utilitaire.showMessageDialog(this, "Entrée manuelle incorrecte : nom et prénom ne doivent contenir que des caractèreres alphabétiques");
                    	return;
                    }
                } else {
                	Utilitaire.showMessageDialog(this, "Entrée manuelle incorrecte : nom et prénom ne peuvent être vides");
                	return;
                }
            } else { // on lit les noms/prénoms dans le fichier CSV
            	String erreur="";
            	String nomfichier=fu.getSpoolFileName();	//chmein fichier temp
                /*if (fichier_etudiants!=null) {*/
                if (nomfichier!=null && nomfichier!="") {
                    BufferedReader buf;
                    InputStream inputStream;
                    try {
                        /*buf = new BufferedReader(new FileReader(fichier_etudiants));*/
                        //buf = new BufferedReader(new UploadedFileReader(fichier_etudiants));
                        //InputStream inputStream = FileUtils.getResourceAsStream("/home/sysadmin/Téléchargements/events.vcs");                        
                        inputStream = FileUtils.getResourceAsStream(nomfichier);                        
                        buf = new BufferedReader(new InputStreamReader(inputStream));
                        
                        String line=buf.readLine();
                        while (line!=null) {
                            line=line.replaceAll("[ '\"]",""); // on enlève espaces et guillemets
                            String tab[]=line.split("[;,\t]");
                            /*if (tab.length==2) {
                                login=(""+tab[0].charAt(0)+tab[1]).toLowerCase();
                                login.replaceAll("[a-zA-Z]",""); // on enlève les caractères non alphabétiques
                                /*login=login.substring(0,Math.min(16,login.length())); // on tronque à 15 caractères* /
                                login=login.substring(0,Math.min(30,login.length())); // on tronque à 30 caractères(comme ds la table Etudiant)
                                if (tab[0].length()>0 && tab[1].length()>0) {
                                    Etudiant et=new Etudiant(-1,tab[0],tab[1],login);
                                    listeEtudiants.add(et);
                                } else erreur+="Entrée incorrecte dans le fichier CSV : le nom et/ou le prénom ne peuvent être vides";
                            }
                            else erreur+="Entrée incorrecte dans le fichier CSV : "+line+"\n";*/                            
                            if (tab.length==3) {
                            	if (tab[0].replaceAll("[a-zA-Z -]", "").length()+tab[1].replaceAll("[a-zA-Z -]", "").length()+tab[2].replaceAll("[a-zA-Z]", "").length()==0) {
                            		//Utilitaire.showMessageDialog("rAll 1",tab[0]);
									tab[0]=tab[0].toLowerCase();
									tab[1]=tab[1].toLowerCase();
									tab[2]=tab[2].toLowerCase();
									//Utilitaire.showMessageDialog("rAll 2",tab[0]);
									
									//Utilitaire.showMessageDialog("tab[0]",tab[0]);
									//Utilitaire.showMessageDialog("tab[1]",tab[1]);
									//Utilitaire.showMessageDialog("tab[2]",tab[2]);
									if (tab[0].length()>0 && tab[1].length()>0 && tab[2].length()>0) {
										Etudiant et=new Etudiant(-1,tab[0],tab[1],tab[2]);
										listeEtudiants.add(et);
										//Utilitaire.showMessageDialog("ajout d'étudiant","ajout de "+et.getPrenom());
									} else erreur+="Entrée incorrecte dans le fichier CSV ["+line+"] : nom, prénom et login ne peuvent être vides&";
                            		//} else	Utilitaire.showMessageDialog(this,"Entrée incorrecte dans le fichier CSV ["+line+"] : nom, prénom et login ne peuvent être vides");
                                } else	erreur+="Entrée incorrecte dans le fichier CSV ["+line+"] : nom, prénom et login ne doivent contenir que des caractèreres alphabétiques&";
                            } else	erreur+="Entrée incorrecte dans le fichier CSV ["+line+"] : l'entrée doit être au format \"nom,prénom,login\" ou \"nom;prénom;login\"&";
                            
                            line=buf.readLine();
                        }
                        buf.close();
                    	inputStream.close();
                    } catch (FileNotFoundException ex) {
                        Utilitaire.showMessageDialog(this,"Fichier inexistant "+nomfichier);	//fichier_etudiants.getName());
                        ex.printStackTrace();
                        return;
                    } catch (IOException ex) {
                        Utilitaire.showMessageDialog(this,"Problème de lecture dans le fichier "+nomfichier);	//.getName());
                        ex.printStackTrace();
                        return;
//                    } catch (NumberFormatException ex) {
//                        JOptionPane.showMessageDialog(this,"Mauvais format de nombre dans "+fichier_etudiants.getName());
//                        ex.printStackTrace();
                    } catch (Exception ex) {
                        Utilitaire.showMessageDialog(this,"Erreur indéterminée !!! Voir la console !");
                        ex.printStackTrace();
                        return;
                    } finally {

                    }                    
                    
                    if (!erreur.equals("")) {
                    	String tab[]=erreur.split("&");
                    	for(int i=0; i<tab.length; i++) 
                    		Utilitaire.showMessageDialog(this,tab[i]);
                        return;
                    }
                } else {
                	Utilitaire.showMessageDialog(this, "Entrée incorrecte pour le nom du fichier CSV : le nom du fichier ne peut être vide !");
                	return;
                }
            }
            // on cherche les groupes cochés
            ArrayList<Groupe>listeGroupesEtudiant=new ArrayList<Groupe>();
            for (int index=0;index<cb_groupes.length;index++)
                if (cb_groupes[index].isChecked())
                    listeGroupesEtudiant.add(listeGroupesPromo.get((index)));
            // on récupère la liste des étudiants déjà dans la promo
            TreeSet<Etudiant> liste=edtAppli.getControleurDb().getEtudiants(edtAppli.getControleurDb().getIdGroupeCours(edtAppli.getControleurDb().getIdPromotion((String)edtAppli.getListePromotions().getValueText())));//.getSelectedItem())));
            // on ajoute (ou modifie) les étudiants de la liste
            for (Iterator<Etudiant> iter = listeEtudiants.iterator(); iter.hasNext();) {
                Etudiant nouv_etud=iter.next();
                // on vérifie que l'étudiant n'existe pas dans cette promo
                if (etudiant!=null) liste.remove(etudiant); // on enlève l'étudiant de la liste (mais pas de la BD !)
                if (liste.contains(nouv_etud)) {
                    /*JOptionPane.showMessageDialog(this, "Cet étudiant existe déjà");*/
                    Utilitaire.showMessageDialog(this, "L'étudiant '"+nouv_etud.getNom()+" "+ nouv_etud.getPrenom() +"' existe déjà");
                    if (etudiant!=null) liste.add(etudiant); // on remet l'ancien étudiant
                    return;
                } else if (nouv_etud.getPrenom().equals("") || nouv_etud.getNom().equals("") || listeGroupesEtudiant.size()==0) {
                	/*JOptionPane.showMessageDialog(this, "Il faut saisir prénom et nom et au moins un groupe !");*/
                    Utilitaire.showMessageDialog(this, "Il faut saisir prénom et nom et au moins un groupe !");
                    if (etudiant!=null) liste.add(etudiant); // on remet l'ancien étudiant
                    return;
                } else {
                    if (etudiant!=null) edtAppli.getControleurDb().modifierEtudiant(etudiant, nouv_etud.getPrenom(), nouv_etud.getNom(), nouv_etud.getLogin(), listeGroupesEtudiant);
                    else edtAppli.getControleurDb().ajouterEtudiant(nouv_etud.getPrenom(),nouv_etud.getNom(),nouv_etud.getLogin(), listeGroupesEtudiant);
                    liste.add(nouv_etud); // on met le nouvel étudiant dans la liste à la place de l'ancien
                    /*dispose();*/             
                }
            }
            accept();
            FormulaireEtudiants_wt form = new FormulaireEtudiants_wt(edtAppli);            
        } else if (action.equals("fichier")) {
            l_prenom.setDisabled(true);	//Enabled(false);
            l_nom.setDisabled(true);
            l_login.setDisabled(true);
            tf_nom.setDisabled(true);
            tf_prenom.setDisabled(true);
            tf_login.setDisabled(true);
            /*tf_nomfichier.setDisabled(false);
            b_parcourir.setDisabled(false);*/
            fu.setDisabled(false);
            b_valider.setDisabled(false);
        }
        else if (action.equals("manuel")) {
            l_prenom.setDisabled(false);
            l_nom.setDisabled(false);
            l_login.setDisabled(false);
            tf_nom.setDisabled(false);
            tf_prenom.setDisabled(false);
            tf_login.setDisabled(false);
            /*tf_nomfichier.setDisabled(true);
            b_parcourir.setDisabled(true);*/
            fu.setDisabled(true);            
        }
        else if (action.equals("parcourir")) {
            /*JFileChooser jfc=new JFileChooser();
            jfc.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.getName().endsWith(".csv") || f.isDirectory();
                }
                @Override
                public String getDescription() {
                    return "Fichiers CSV";
                }
            });
            jfc.setMultiSelectionEnabled(false);
            if (jfc.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
                fichier_etudiants=jfc.getSelectedFile();
                tf_nomfichier.setText(fichier_etudiants.getName());
            }
            else fichier_etudiants=null;*/

            /////////////TROUVER CHEMIN:INUTILE: on passe par le fichier temp
            //Utilitaire.showMessageDialog(this, label.getChildren().get(0).gettoString());            
            //fu.upload();            
            //fu.setDisabled(true);
            //Utilitaire.showMessageDialog(this,"parcourir!"+ fu.getClientFileName()+"."+ fu.getObjectName());
            //if (!fu.getClientFileName().endsWith(".vcs"))
            	//Utilitaire.showMessageDialog(this, fu.getClientFileName()+": se terminine par vcs");
            //else
            	//Utilitaire.showMessageDialog(this, fu.getClientFileName()+": ne se terminine pas par vcs !"+ fu.SpoolFileName());
          //  Utilitaire.showMessageDialog("getClientFileName", fu.getClientFileName());
          //  Utilitaire.showMessageDialog("getUploadedFiles", fu.getUploadedFiles().get(0).toString());
          //  Utilitaire.showMessageDialog("tf_nomfichier", tf_nomfichier.getValueText());
           // Utilitaire.showMessageDialog("fu.AttributeValue text", fu.get);
           
            //UploadedFile fuploaded=fu.getUploadedFiles().get(0);
            //UploadedFile fichier_etudiants=fu.getUploadedFiles().get(0);
            
            //fileResource = new WFileResource("plain/text","event.vcs");
            //fileInputStream = new FileInputStream("event.vcs");*/
            

        }
		/*else if (action.equals("annuler"))
			dispose();*/
	}
}
