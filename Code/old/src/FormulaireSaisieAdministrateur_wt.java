package EDT;

import eu.webtoolkit.jwt.WDialog;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WLabel;
import eu.webtoolkit.jwt.WLineEdit;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WCheckBox;
import eu.webtoolkit.jwt.WBorderLayout;
import eu.webtoolkit.jwt.WGridLayout;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.Side;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/**
 * formulaire d'ajout ou de modification d'administrateur
 */
/*public class FormulaireSaisieAdministrateur extends JDialog implements ActionListener {*/
public class FormulaireSaisieAdministrateur_wt extends WDialog {
	EDTApplication_wt edtAppli;
	String admin;	
    WLabel l_admin;
    WLineEdit tf_admin;
	WPushButton b_valider, b_annuler;
    ArrayList<String>listePromosExistantes;
    WCheckBox[] cb_promos;

	FormulaireSaisieAdministrateur_wt(EDTApplication_wt _edtAppli, String _admin, WDialog dial) {
		/*super(dial, "Ajout/modif d'administrateur", true);*/
		super("Ajout/modif d'administrateur");		
		
		/*//procedure qui masque et ferme la fenetre
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose(); // on ferme la fenêtre
			}
		});*/
		edtAppli=_edtAppli;
		admin=_admin;
		
		/*JPanel panel=new JPanel();
		panel.setLayout(new BorderLayout());*/				
		WContainerWidget panel=new WContainerWidget();
		WBorderLayout border = new WBorderLayout();
		panel.setLayout(border);
		// les informations sur l'administrateur
		/*JPanel panelAdmin=new JPanel();*/
		WContainerWidget panelAdmin=new WContainerWidget();
        // la liste des promotions existantes
        listePromosExistantes=new ArrayList<String>(edtAppli.getControleurDb().getPromotions(false));
        /*panelAdmin.setLayout(new GridLayout(1+listePromosExistantes.size(),2)); // login + les groupes*/        
        WGridLayout gridAdmin = new WGridLayout();
        panelAdmin.setLayout(gridAdmin);

        /*l_admin=new JLabel("Login :");
        cb_promos=new JCheckBox[listePromosExistantes.size()];
        tf_admin=new JTextField();*/
        l_admin=new WLabel("Login : ");
        cb_promos=new WCheckBox[listePromosExistantes.size()];
        tf_admin=new WLineEdit();
		if (admin!=null)
            tf_admin.setText(admin);
        /*panelAdmin.add(l_admin);
        panelAdmin.add(tf_admin);*/        
        getFooter().addWidget(tf_admin);
        tf_admin.setAttributeValue("style", "float:left;");
        tf_admin.setPlaceholderText("login");
        
        // on récupère les groupes auxquels l'admin a accès
        TreeSet<String>listePromosAdmin=null;
        if (admin!=null)
            listePromosAdmin=edtAppli.getControleurDb().getListePromosAdmin(_admin);
		// affichage des groupes
        int index=0;
        for (Iterator<String> iter = listePromosExistantes.iterator(); iter.hasNext();) {
            String nom_promo=iter.next();
            /*cb_promos[index]=new JCheckBox(nom_promo);*/
            cb_promos[index]=new WCheckBox(nom_promo);
            if (admin!=null && listePromosAdmin.contains(nom_promo))
            	cb_promos[index].setChecked(true);
            	/*cb_promos[index].setSelected(true);*/                
            gridAdmin.addWidget(cb_promos[index++], index+1, 0);
            /*panelAdmin.add(new JLabel(""));*/
            gridAdmin.addWidget(new WLabel(""), index+1, 1);
        }
		// les boutons
		/*JPanel panelBoutons=new JPanel();
		panelBoutons.setLayout(new GridLayout(1,2));*/
		WContainerWidget panelBoutons=new WContainerWidget();
		
		/*panelBoutons.setLayout(new GridLayout(1,2));*/
		WGridLayout gridBoutons = new WGridLayout();
		panelBoutons.setLayout(gridBoutons);
		
		/*b_valider=new JButton("Valider");
		b_annuler=new JButton("Annuler");*/		
		b_valider=new WPushButton("Valider");
		b_annuler=new WPushButton("Annuler");
		
		/*b_valider.addActionListener(this);
		b_annuler.addActionListener(this);
        b_valider.setActionCommand("valider");
        b_annuler.setActionCommand("annuler");*/
        b_valider.clicked().addListener(b_valider, new Signal.Listener() {
        	public void trigger() {
        		actionValider();
        	}
        });
        b_annuler.clicked().addListener(b_annuler, new Signal.Listener() {
        	public void trigger() {
        		//remove();
        		reject();
        		FormulaireAdministrateurs_wt form = new FormulaireAdministrateurs_wt(edtAppli);
        	}
        });
        b_valider.setStyleClass("btn-info");
        b_annuler.setStyleClass("btn-primary");
        
		/*panelBoutons.add(b_valider);
		panelBoutons.add(b_annuler);*/
		getFooter().addWidget(b_valider);
		getFooter().addWidget(b_annuler);
		
		// on ajoute tout dans la fenetre
        /*JScrollPane scroll=new JScrollPane(panelAdmin);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        panel.add(scroll, BorderLayout.CENTER);
		panel.add(panelBoutons, BorderLayout.SOUTH);
		getContentPane().add(panel);*/
		getContents().addWidget(panelAdmin);
		getContents().addWidget(panelBoutons);
		
		// positionner au centre de l'écran
		/*pack();
        setSize(Math.min(800, getWidth()), Math.min(800, getHeight()));
        setResizable(true);
		Rectangle rect=edtAppli.getBounds(); // getToolkit().getScreenSize();
		setLocation(Math.max(0,(int)(rect.x+(rect.width-getWidth())/2)), Math.max(0,(int)(rect.y+(rect.height-getHeight())/2)));*/
		this.resize(500, 500);
		tf_admin.setFocus();
		this.show();
	}
    
	/*public void actionPerformed(ActionEvent e) {
		String action = ((AbstractButton)e.getSource()).getActionCommand();
		if (action.equals("valider")) {*/
	public void actionValider() {	
            String login=tf_admin.getText();
            // on cherche les groupes cochés
            ArrayList<String>listePromosAdmin=new ArrayList<String>();
            for (int index=0;index<cb_promos.length;index++)
                if (cb_promos[index].isChecked())
                    listePromosAdmin.add(listePromosExistantes.get((index)));
            // on récupère la liste des admins
            TreeSet<String> liste=edtAppli.getControleurDb().getListeAdministrateurs();
            // on vérifie que l'administrateur n'existe pas
            if (admin!=null) liste.remove(admin); // on enlève l'admin de la liste (mais pas de la BD !)
            if (liste.contains(login)) {
                    Utilitaire.showMessageDialog(this, "Cet administrateur existe déjà");
                    if (admin!=null) liste.add(admin); // on remet l'ancien admin
                } else if (login.equals("")) {
                	tf_admin.setFocus(true);
                    Utilitaire.showMessageDialog(this, "Il faut saisir un login !");
                    if (admin!=null) liste.add(admin); // on remet l'ancien admin                    
                } else {
                    if (admin!=null) edtAppli.getControleurDb().modifierAdmin(admin, login, listePromosAdmin);
                    else edtAppli.getControleurDb().ajouterAdmin(login,listePromosAdmin);
                    liste.add(login); // on met le nouvel admin dans la liste à la place de l'ancien
                    /*dispose();*/
                    accept();
                    FormulaireAdministrateurs_wt form = new FormulaireAdministrateurs_wt(edtAppli);
                }
            }
		/*else if (action.equals("annuler"))
			dispose();
	}*/
}
