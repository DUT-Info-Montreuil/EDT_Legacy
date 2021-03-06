package EDT;

import eu.webtoolkit.jwt.WDialog;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WLabel;
import eu.webtoolkit.jwt.WLineEdit;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WComboBox;
import eu.webtoolkit.jwt.WBorderLayout;
import eu.webtoolkit.jwt.WGridLayout;
import eu.webtoolkit.jwt.Signal;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/**
 * formulaire d'ajout ou de modification de département
 */
/*public class FormulaireSaisieDepartement extends JDialog implements ActionListener {*/
public class FormulaireSaisieDepartement_wt extends WDialog {
	EDTApplication_wt edtAppli;
	String nom_departement;	
    WLineEdit tf_departement;
	WPushButton b_valider, b_annuler;
    TreeSet<String>listeAdminsNonChefs;
    WComboBox cb_admins;

    
	FormulaireSaisieDepartement_wt(EDTApplication_wt _edtAppli, String _nom_departement, WDialog dial) {
		/*super(dial, "Ajout/modif de département", true);*/
		super("Ajout/modif de département");
		
		/*//procedure qui masque et ferme la fenetre
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose(); // on ferme la fenêtre
			}
		});*/
		edtAppli=_edtAppli;
        nom_departement=_nom_departement;
        
		/*JPanel panel=new JPanel();
		panel.setLayout(new BorderLayout());*/        
		WContainerWidget panel=new WContainerWidget();
		WBorderLayout border = new WBorderLayout();
		panel.setLayout(border);
		
		// les informations sur le département
        /*JPanel panelDepartement=new JPanel();
        panelDepartement.setLayout(new GridLayout(2,2));*/		
		WContainerWidget panelDepartement=new WContainerWidget();
        WGridLayout gridDepartement = new WGridLayout();
        panelDepartement.setLayout(gridDepartement);
        
        /*JLabel l_departement=new JLabel("Nom du département :");
        tf_departement=new JTextField();*/
        WLabel l_departement=new WLabel("Nom :");
        tf_departement=new WLineEdit();
		if (nom_departement!=null)
            tf_departement.setText(nom_departement);
        /*panelDepartement.add(l_departement);
        panelDepartement.add(tf_departement);*/        
        gridDepartement.addWidget(l_departement, 0, 0);
        gridDepartement.addWidget(tf_departement, 0, 1);
        
        // la liste des administrateurs existants
        /*JLabel l_chef=new JLabel("Chef :");
        cb_admins=new JComboBox();*/
        WLabel l_chef=new WLabel("Chef :");
        cb_admins=new WComboBox();
        
        listeAdminsNonChefs=edtAppli.getControleurDb().getListeAdministrateursNonChefs();
        String chefActuel="";
        if (nom_departement!=null) {
            chefActuel=edtAppli.getControleurDb().getChefDepartement(nom_departement);
            listeAdminsNonChefs.add(chefActuel);
        }
        for (Iterator<String> iter = listeAdminsNonChefs.iterator(); iter.hasNext();)
            cb_admins.addItem(iter.next());
        if (nom_departement!=null)
            cb_admins.setValueText(chefActuel);
        /*panelDepartement.add((l_chef));
        panelDepartement.add(cb_admins);*/        
        gridDepartement.addWidget((l_chef), 1, 0);
        gridDepartement.addWidget(cb_admins, 1, 1);
        
		// les boutons
		/*JPanel panelBoutons=new JPanel();
		panelBoutons.setLayout(new GridLayout(1,2));*/		
		WContainerWidget panelBoutons=new WContainerWidget();
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
        			remove();
        			FormulaireDepartements_wt form = new FormulaireDepartements_wt(edtAppli);
        		}
        });
		b_valider.setStyleClass("btn-info");
		b_annuler.setStyleClass("btn-primary");
        
		/*panelBoutons.add(b_valider);
		panelBoutons.add(b_annuler);*/
		getFooter().addWidget(b_valider);
		getFooter().addWidget(b_annuler);
		
		// on ajoute tout dans la fenetre
        /*JScrollPane scroll=new JScrollPane(panelDepartement);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        panel.add(scroll, BorderLayout.CENTER);
		panel.add(panelBoutons, BorderLayout.SOUTH);
		getContentPane().add(panel);*/
		getContents().addWidget(panelDepartement);
		getContents().addWidget(panelBoutons);
		
		// positionner au centre de l'écran
		/*pack();
        setSize(Math.min(800, getWidth()), Math.min(800, getHeight()));
        setResizable(true);
		Rectangle rect=edtAppli.getBounds(); // getToolkit().getScreenSize();
		setLocation(Math.max(0,(int)(rect.x+(rect.width-getWidth())/2)), Math.max(0,(int)(rect.y+(rect.height-getHeight())/2)));*/
		this.resize(400, 400);
		this.show();
	}
    
	/*public void actionPerformed(ActionEvent e) {
		String action = ((AbstractButton)e.getSource()).getActionCommand();
		if (action.equals("valider")) {*/
		public void actionValider() {
            String nouveau_nom_departement=tf_departement.getText();
            // on cherche l'admin sélectionné
            String nouveau_chef=(String)cb_admins.getValueText();	//SelectedItem();
            // on vérifie que le département n'existe pas (et a un nom !)
            TreeSet<String>listeDptsExistants=edtAppli.getControleurDb().getListeDepartements();
            if (nom_departement!=null) listeDptsExistants.remove(nom_departement);
            if (nouveau_nom_departement==null || nouveau_nom_departement.equals("")) {
                /*JOptionPane.showMessageDialog(this, "Il faut saisir un nom de département !");*/
            	Utilitaire.showMessageDialog(this, "Il faut saisir un nom de département !");            	
            } else if (listeDptsExistants.contains(nouveau_nom_departement)) {
                /*JOptionPane.showMessageDialog(this, "Ce département existe déjà")*/;
                Utilitaire.showMessageDialog(this, "Ce département existe déjà !");
            } else {
                edtAppli.getControleurDb().modifierDepartement(nom_departement, nouveau_nom_departement, nouveau_chef);
                /*dispose();*/
                accept();
                FormulaireDepartements_wt form = new FormulaireDepartements_wt(edtAppli);
            }
        }
        /*}
		else if (action.equals("annuler"))
			dispose();
	}*/
}
