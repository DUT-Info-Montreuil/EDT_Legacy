package EDT;

import eu.webtoolkit.jwt.WDialog;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WLabel;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WBorderLayout;
import eu.webtoolkit.jwt.WGridLayout;
import eu.webtoolkit.jwt.Signal;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/**
 * formulaire de gestion des départements par l'admin principal
 */
@SuppressWarnings("serial")
/*public class FormulaireDepartements extends JDialog implements ActionListener {*/
public class FormulaireDepartements_wt extends WDialog {
	EDTApplication_wt edtAppli;
    ArrayList<String> departements;
	ArrayList<WLabel> labelDepartement;
	ArrayList<WPushButton> boutonModif;
	WPushButton b_ajouter;
	WDialog dialog = this;
	
	FormulaireDepartements_wt(EDTApplication_wt _edtAppli) {
		/*super(_edtAppli, "Gestion des départements", true);*/
		super("Gestion des départements");
		/*//procedure qui masque et ferme la fenetre
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				edtAppli.mettreAJour();
				dispose(); // on ferme la fenêtre
			}
		});*/
		edtAppli=_edtAppli;
		afficher();
	}

	public void afficher() {
		/*dispose();*/
		departements=new ArrayList<String>(edtAppli.getControleurDb().getListeDepartements());		
		
		/*JPanel panel=new JPanel();
		panel.setLayout(new BorderLayout());
		b_ajouter=new JButton("Ajouter un département");
		b_ajouter.addActionListener(this);
		panel.add(b_ajouter, BorderLayout.NORTH);*/
		WContainerWidget panel=new WContainerWidget();
		WBorderLayout border = new WBorderLayout();
		panel.setLayout(border);		
		b_ajouter=new WPushButton("Ajouter");
		b_ajouter.setStyleClass("btn-success");
		b_ajouter.clicked().addListener(b_ajouter, new Signal.Listener() {
			public void trigger() {
					accept();
					FormulaireSaisieDepartement_wt form = new FormulaireSaisieDepartement_wt(edtAppli, null, dialog);				
			}			
		});
		getFooter().addWidget(b_ajouter);		

		/*JPanel panelDepartements=new JPanel();
		panelDepartements.setLayout(new GridLayout(departements.size(),2));
		labelDepartement=new ArrayList<JLabel>();
		boutonModif=new ArrayList<JButton>();
		JButton b;*/
		WContainerWidget panelDepartements=new WContainerWidget();
		WGridLayout grid = new WGridLayout();
		panelDepartements.setLayout(grid);		
		labelDepartement=new ArrayList<WLabel>();
		boutonModif=new ArrayList<WPushButton>();
		WPushButton b;
		for (int i=0; i<departements.size(); i++) {
			/*labelDepartement.add(new JLabel(""+(departements.get(i))));*/
			String dept = departements.get(i);
			labelDepartement.add(new WLabel(""+(dept)));
			
			/*b=new JButton("Modifier");
			b.addActionListener(this);
			boutonModif.add(b);*/			
			b=new WPushButton("Modifier");
			b.clicked().addListener(b, new Signal.Listener() {
					public void trigger() {
						accept();
						FormulaireSaisieDepartement_wt form = new FormulaireSaisieDepartement_wt(edtAppli, dept, dialog);
					}
			});
			b.setStyleClass("btn-info");
			boutonModif.add(b);
		}
		// on place le nom du département, un bouton modifier
		for (int index=0; index<departements.size(); index++) {
			/*panelDepartements.add((JLabel)labelDepartement.get(index));
			panelDepartements.add((JButton)boutonModif.get(index));*/
			
			grid.addWidget((WLabel)labelDepartement.get(index), index, 0);
			grid.addWidget((WPushButton)boutonModif.get(index), index, 1);			
		}
		/*JScrollPane scroll=new JScrollPane(panelDepartements);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
		panel.add(scroll, BorderLayout.CENTER);*/
		
		// on ajoute tout dans la fenetre
		/*getContentPane().removeAll();
		getContentPane().add(panel);*/
		getContents().addWidget(panel);
		getContents().addWidget(panelDepartements);
		
		// positionner au centre de l'écran
		/*pack();
		setSize(Math.min(800, getWidth()), Math.min(800, getHeight()));
		setResizable(true);
		Rectangle rect=edtAppli.getBounds(); // getToolkit().getScreenSize();
		setLocation(Math.max(0,(int)(rect.x+(rect.width-getWidth())/2)), Math.max(0,(int)(rect.y+(rect.height-getHeight())/2)));
		setVisible(true);*/
		this.setClosable(true);
		this.setResizable(true);
		this.rejectWhenEscapePressed(true);
		this.resize(400, 400);
		this.show();
	}
	
	/*public void actionPerformed(ActionEvent e) {
		JButton b = (JButton)e.getSource();
		if (b.equals(b_ajouter)) {
            FormulaireSaisieDepartement form=new FormulaireSaisieDepartement(edtAppli, null, this);
            form.setVisible(true);
        }
		else if (boutonModif.contains(b)) {
			int index=boutonModif.indexOf(b);
			String nom_dpt=departements.get(index);
            FormulaireSaisieDepartement form=new FormulaireSaisieDepartement(edtAppli, nom_dpt, this);
            form.setVisible(true);
        }
		afficher();
	}*/
}
