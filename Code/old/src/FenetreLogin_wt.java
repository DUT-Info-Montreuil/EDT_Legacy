package EDT;

import eu.webtoolkit.jwt.WApplication;
import eu.webtoolkit.jwt.WEnvironment;

import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.EventSignal;
import eu.webtoolkit.jwt.WMouseEvent;

import eu.webtoolkit.jwt.WDialog;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WLabel;
import eu.webtoolkit.jwt.WTextEdit;
import eu.webtoolkit.jwt.WLineEdit;
import eu.webtoolkit.jwt.WPanel;
import eu.webtoolkit.jwt.WGridLayout;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WLength;
import eu.webtoolkit.jwt.WComboBox;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WInPlaceEdit;
import eu.webtoolkit.jwt.WBreak;
import eu.webtoolkit.jwt.Side;


import eu.webtoolkit.jwt.utils.MathUtils;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

import java.util.Random;

/*
 * Created on 26 juin 2004
 *
 */

/**
 * @author fredphil
 *
 */
@SuppressWarnings("serial")
public class FenetreLogin_wt extends WDialog {
/*public class FenetreLogin extends JFrame implements ActionListener {
	private EDTApplication edtAppli;
	private JButton boutonConnection;
	private JButton boutonQuitter;
	private JTextField nomUtilisateur;
	private JTextField nomBaseDeDonnee;
	private JPasswordField password;*/ 

    private EDTApplication_wt edtAppli;
    private WPushButton boutonConnection;
    private WLineEdit nomUtilisateur;
    private WInPlaceEdit nomBaseDeDonnee;
    private WLineEdit password;
    
    // Affirmation
    private int nbr1, nbr2, result;
    private WComboBox resultCB;
     

    public FenetreLogin_wt(EDTApplication_wt _edtAppli) {
    	// affichage du titre
    	super(_edtAppli.getTitle());
    	    	
         // Création de la relation entre l'objet application et FenetreLogin
        this.edtAppli = _edtAppli;
        
        // Allocation en mémoire des composants graphiques
        WLabel labelBase = new WLabel("Base de données :");
        //nomBaseDeDonnee = new WInPlaceEdit("localhost/adminedt");
        nomBaseDeDonnee = new WInPlaceEdit("database/writeedt");
        nomBaseDeDonnee.setButtonsEnabled(false);
        nomBaseDeDonnee.setWidth(new WLength("auto"));
        
        WLabel labelNom = new WLabel("Utilisateur :");
        nomUtilisateur = new WLineEdit("");
        //nomUtilisateur = new WLineEdit("hassoun");
        
        WLabel labelPassword = new WLabel("Mot de passe :&nbsp;");
        password = new WLineEdit("");
        password.setEchoMode(WLineEdit.EchoMode.Password);
        
        // Affirmation
        resultCB = new WComboBox();
        int nbr1 = Math.abs(new Random().nextInt()%8)+2;
        int nbr2 = Math.abs(new Random().nextInt()%8)+2;        
        result=nbr1*nbr2;
        WLabel label = new WLabel(nbr1+" x "+nbr2+" = ");        
        for (int i=0; i<100; i++)
        	resultCB.addItem(label.getText()+" "+i);
        
        boutonConnection = new WPushButton("Se connecter");
        
        // ajout des composants dans les panneaux
        WGridLayout grid = new WGridLayout();
        this.getContents().setLayout(grid);

        //grid.setVerticalSpacing(20);
        //grid.setContentsMargins(30, 30, 30, 30);
        
        grid.addWidget(labelNom, 1, 0);
        grid.addWidget(nomUtilisateur, 1, 1);
        grid.addWidget(labelPassword, 2, 0);
        grid.addWidget(password, 2, 1);
        
        //grid.addWidget(new WLabel("Captcha :"), 3, 0);
        //grid.addWidget(resultCB, 3, 1);
        
        // Affiche le nom de la base de données dans la fenêtre de login
        /*nomBaseDeDonnee.setAttributeValue("style", "float:left;font-size:0.8em;font-style:italic;color:grey");
        nomBaseDeDonnee.setFloatSide(Side.Bottom);
        this.getFooter().addWidget(nomBaseDeDonnee);*/
        boutonConnection.setStyleClass("btn-success");
        this.getFooter().addWidget(boutonConnection);
       
        boutonConnection.clicked().addListener(this, new Signal.Listener() {
        		public void trigger() {
        			validation();
          		}
        });
        password.enterPressed().addListener(this, new Signal.Listener() {
        		public void trigger() {
        			//Utilitaire.showMessageDialog("this","Valeur de login : boutonEntree appuyer");
        			//boutonConnection.clicked().trigger();
        			validation();
       }});
        // rendre la fenetre non redimensionable
        setResizable(false);
        //password.setFocus();
        nomUtilisateur.setFocus();
        this.show();
        
        // positionner au centre de l'ecran
		/*setSize(Math.min(800, getWidth()), Math.min(800, getHeight()));
		this.setResizable(true);
		this.setClosable(true);
		this.rejectWhenEscapePressed(true);*/
		//this.resize(365, 200);
		
		
        /*
        // configuration de la fenetre
		getContentPane().setLayout(new BorderLayout());
		// ajout des panneaux dans la fenetre
		getContentPane().add(panneauChamps, BorderLayout.CENTER);
		getContentPane().add(panneauBouton, BorderLayout.SOUTH);
		getContentPane().add(panneauImage, BorderLayout.EAST);
		// ajout des ecouteurs sur les boutons
		boutonConnection.addActionListener(this);
		boutonQuitter.addActionListener(this);
		// affichage du titre
		setTitle("EDT version "+edtAppli.version);
		// rendre la fenetre non redimensionable
		setResizable(false);
		// affichage de la fenetre
		pack();        
        // gestion de l'appui sur ENTREE
        getRootPane().setDefaultButton(boutonConnection);
        
        // ajout de l'ecouteur aux boutons de la frame
        addWindowListener(new WindowAdapter() {
            // procedure qui masque et ferme la fenetre
            public void windowClosing(WindowEvent e) {
                // Quitter la session
                quitter();
            }
        });*/
    }

    /*public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(boutonQuitter)) // on quitte
            quitter();
        else if (e.getSource().equals(boutonConnection)) { // on se connecte*/
    public boolean verifNomPassword() {        	
            Connection db=connecter();
            // on cherche quel administrateur essaie de se connecter
            int idAdmin= -1;
            Statement requeteRecherche;
            
            // Affirmation
            //if (resultCB.getCurrentIndex()!=result) {	
            //	Utilitaire.showMessageDialog(this, "L'affirmation \""+resultCB.getValueText()+"\" n'est pas correct !");
            //	return false;
            //}            
            try {
                requeteRecherche= db.createStatement();
                ResultSet resultatTemporaire= requeteRecherche.executeQuery("SELECT id_admin FROM admin where login_admin='" + nomUtilisateur.getText() + "'");
                
                if (resultatTemporaire.next()) {
                    idAdmin= resultatTemporaire.getInt("id_admin");
                }
                // fermeture du flux et de la requete
                resultatTemporaire.close();
                requeteRecherche.close();
                if (idAdmin!=-1) {// l'administrateur qui se connecte existe dans la BD
                    edtAppli.setControleurDb(new ControleurDB(db, idAdmin));
                	return true;
                } else {
                    /*JOptionPane.showMessageDialog(edtAppli, "Vous n'êtes pas administrateur d'emploi du temps...");
                    System.exit(0);*/
                    Utilitaire.showMessageDialog(this, "Vous n'êtes pas administrateur d'emploi du temps...");
                    return false;
                }
            } catch (SQLException exc) {
                /*exc.printStackTrace();
                JScrollPane erreurs=new JScrollPane(new JTextArea(exc.toString()));
                erreurs.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                erreurs.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                erreurs.getVerticalScrollBar().setUnitIncrement(20);
                erreurs.setMaximumSize(new Dimension(800,800));
                JOptionPane.showMessageDialog(edtAppli, erreurs);*/
                //Utilitaire.showMessageDialog(this,"Erreur inconnue !:"+nomUtilisateur.getText()+":"+password.getText()+"="+ exc.toString());
                Utilitaire.showMessageDialog(this,"Erreur inconnue : "+nomUtilisateur.getText()+"="+ exc.toString());
                return false;                
            } catch (Exception e) {            	
            	//Utilitaire.showMessageDialog(this,"Mauvais identifiant et/ou mot de passe !");
            	//Utilitaire.showMessageDialog(this,"Mauvais identifiant et/ou mot de passe !:"+nomUtilisateur.getText()+":"+password.getText()+"="+ e.toString());
            	Utilitaire.showMessageDialog(this,"Mauvais identifiant et/ou mot de passe : "+nomUtilisateur.getText()+"="+ e.toString());
            	return false;
            }
    }

    public Connection connecter() {
        //connection à la base de donnée
        Connection db=null;
        try {
        	/*String dm = "jdbc:postgresql://" + nomBaseDeDonnee.getText().toString() +
                    	",user=" + nomUtilisateur.getText() +
                    	",pword=" + new String(password.getDisplayText());
        	this.getContents().addWidget(new WLabel("dm="+dm));
        	//Utilitaire.showMessageDialog("dm",dm);*/
        	
            // pour que ça fonctionne avec le JAR et JNLP
            ClassLoader cl = this.getClass().getClassLoader(); 
            Class.forName("org.postgresql.Driver", true, cl);
            db = DriverManager.getConnection(
                    "jdbc:postgresql://" + nomBaseDeDonnee.getText(),
                    nomUtilisateur.getText(),
                    new String((password.getText())));//getPassword())));
            Statement requeteRecherche = db.createStatement();
            String encod=System.getProperty("file.encoding");
            if (encod.substring(0, 2).toUpperCase().equals("CP"))
                encod="ISO-8859-1";
            requeteRecherche.executeUpdate("SET CLIENT_ENCODING='" + encod  +"'");
            requeteRecherche.close();
            // fermeture de la fenetre
            /*dispose();*/
            //passage de la valeur par reference pour l'application
        } catch (Exception e) {
            /*e.printStackTrace();
            JScrollPane erreurs=new JScrollPane(new JTextArea("Erreur de connection : vérifier votre login et votre mot de passe...")); //  + nomBaseDeDonnee.getText() + "user=" +  nomUtilisateur.getText( )+ "pass=" + new String((password.getPassword()))));
            erreurs.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            erreurs.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            erreurs.getVerticalScrollBar().setUnitIncrement(20);
            erreurs.setMaximumSize(new Dimension(800,800));*/
            /*JOptionPane.showMessageDialog(this, erreurs);*/
            /*System.exit(1);*/
            Utilitaire.showMessageDialog(this,"Erreur de connection : vérifier votre login et votre mot de passe..."+e.toString());
        }
        return db;
    }

    public String getLogin() {
        return "Login : "+nomUtilisateur.getText();
    }
    
    public void validation() {
    	if (verifNomPassword()==true) {
        				//Utilitaire.showMessageDialog("this","Valeur de login : "+nomUtilisateur.getText());
        				edtAppli.setLoginEDT(nomUtilisateur.getText());
        			    
        				
        				edtAppli.afficher();
						// le panel du centre (EDT)//////////////////////////////////////////////////////////
						//edtAppli.getRoot().addWidget(vueEDT_wt);
						VueEDT_wt vueEDT_wt=new VueEDT_wt(edtAppli);
						ControleurGUI_wt controleurGUI = new ControleurGUI_wt(edtAppli);
						vueEDT_wt.mouseWentDown().addListener(vueEDT_wt, new Signal1.Listener<WMouseEvent>(){
							public void trigger(WMouseEvent e1) {
									//Utilitaire.showMessageDialog("this","mouseWentDown");
									//System.out.println("mouseWentDown");
									controleurGUI.mousePressed(e1);
							}        					
						});
						vueEDT_wt.mouseWentUp().addListener(vueEDT_wt, new Signal1.Listener<WMouseEvent>(){
								public void trigger(WMouseEvent e1) {
									//Utilitaire.showMessageDialog("this","mouseWentUp");
									//System.out.println("mouseWentUp");
									controleurGUI.mouseReleased(e1);
								}
						});
						vueEDT_wt.mouseDragged().addListener(vueEDT_wt, new Signal1.Listener<WMouseEvent>(){
								public void trigger(WMouseEvent e1) {
									//System.out.println("mouseDragged");
									//controleurGUI.mouseDragged(e1);									
								}
						});
						/*vueEDT_wt.mouseMoved().addListener(vueEDT_wt, new Signal1.Listener<WMouseEvent>(){
								public void trigger(WMouseEvent e1) {
									//System.out.println("mouseMoved");
									//controleurGUI.mouseMoved(e1);	
								}
						});*/   	
						
						edtAppli.setVueEDT(vueEDT_wt);
						edtAppli.mettreAJour();
						
						edtAppli.getRoot().addWidget(vueEDT_wt);
						accept();
					//Utilitaire.showMessageDialog("this","Valeur de login dans edtAppli: "+_edtAppli.getLoginEDT());	
					}
    }
    
    /*public void quitter() {
        dispose(); //la fenetre est masquée
        System.gc(); //vider la mémoire
        System.exit(0); //on quitte le programme
    }*/
}//350