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
 * formulaire de gestion des administrateurs par l'admin principal
 */
/*public class FormulaireAdministrateurs extends JDialog implements ActionListener {*/
public class FormulaireAdministrateurs_wt extends WDialog {
	EDTApplication_wt edtAppli;
    ArrayList<String> administrateurs;
	ArrayList<WLabel> labelAdministrateur;
	ArrayList<WPushButton> boutonModif;
	ArrayList<WPushButton> boutonSuppr;
	WPushButton b_ajouter;
	WDialog dialog = this;
	
	FormulaireAdministrateurs_wt(EDTApplication_wt _edtAppli) {
		/*super(_edtAppli, "Gestion des administrateurs", true);*/
		super("Gestion des administrateurs");		
		/*//procedure qui masque et ferme la fenetre
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				edtAppli.mettreAJour();
				dispose(); // on ferme la fenetre
			}
		});*/
		edtAppli=_edtAppli;
		afficher();
	}

	public void afficher() {
		/*dispose();*/
		administrateurs=new ArrayList<String>(edtAppli.getControleurDb().getListeAdministrateurs());
		
		/*JPanel panel=new JPanel();
		panel.setLayout(new BorderLayout());*/
		WContainerWidget panel=new WContainerWidget();
		WBorderLayout border = new WBorderLayout();
		panel.setLayout(border);		

		/*b_ajouter=new JButton("Ajouter un administrateur");
		b_ajouter.addActionListener(this);
		panel.add(b_ajouter, BorderLayout.NORTH);*/
		b_ajouter=new WPushButton("Ajouter");
		b_ajouter.clicked().addListener(b_ajouter, new Signal.Listener() {
				public void trigger() {
					accept();
					FormulaireSaisieAdministrateur_wt form = new FormulaireSaisieAdministrateur_wt(edtAppli, null, dialog);					
				}				
		});
		b_ajouter.setStyleClass("btn-success");
		getFooter().addWidget(b_ajouter);
		
		/*JPanel panelAdmins=new JPanel();
		panelAdmins.setLayout(new GridLayout(administrateurs.size(),3));
		labelAdministrateur=new ArrayList<JLabel>();
		boutonModif=new ArrayList<JButton>();
		boutonSuppr=new ArrayList<JButton>();
		JButton b;*/
		WContainerWidget panelAdmins=new WContainerWidget();
		WGridLayout grid = new WGridLayout();
		panelAdmins.setLayout(grid);		
		labelAdministrateur=new ArrayList<WLabel>();
		boutonModif=new ArrayList<WPushButton>();
		boutonSuppr=new ArrayList<WPushButton>();
		WPushButton b, bs;
		for (int i=0; i<administrateurs.size(); i++) {
			/*labelAdministrateur.add(new JLabel(""+(administrateurs.get(i))));*/
			String admin = administrateurs.get(i);
			labelAdministrateur.add(new WLabel(""+admin));
			
			/*b=new JButton("Modifier");
			b.addActionListener(this);
			boutonModif.add(b);*/
			b=new WPushButton("Modifier");
			b.clicked().addListener(b, new Signal.Listener() {
				public void trigger() {
					accept();
					FormulaireSaisieAdministrateur_wt form = new FormulaireSaisieAdministrateur_wt(edtAppli, admin, dialog);						
				}
			});
			b.setStyleClass("btn-info");			
			boutonModif.add(b);
			
			/*b=new JButton("Supprimer");
			b.addActionListener(this);
			boutonSuppr.add(b);*/	
			bs=new WPushButton("Supprimer");
			bs.clicked().addListener(bs, new Signal.Listener() {
				public void trigger() {
					WDialog confirmerSuppr = new WDialog("Suppression d'administrateur");
					WLabel confirmerLabel = new WLabel("Confirmez-vous la suppression de l'administrateur '"+admin+"' ?", confirmerSuppr.getContents());
					WPushButton ok = new WPushButton("Oui", confirmerSuppr.getFooter());
					WPushButton annuler = new WPushButton("Non", confirmerSuppr.getFooter());
					
					ok.setStyleClass("btn-danger");
					annuler.setStyleClass("btn-primary");
					
					ok.clicked().addListener(ok, new Signal.Listener() {
							public void trigger() {
								confirmerSuppr.accept();
								if(edtAppli.getControleurDb().supprimerAdministrateur(admin)==true) {
									accept();
									FormulaireAdministrateurs_wt form = new FormulaireAdministrateurs_wt(edtAppli);
								} //else "Suppression impossible : l'administrateur 'admin' est encore utilisé ! "
							}
					});					
					annuler.clicked().addListener(annuler, new Signal.Listener() {
							public void trigger() {
								confirmerSuppr.reject();
							}
					});
					
					confirmerSuppr.setClosable(true);
					confirmerSuppr.rejectWhenEscapePressed(true);
					confirmerSuppr.show();
				}
			});
			bs.setStyleClass("btn-warning");			
			boutonSuppr.add(bs);
		}
		// on place le nom de l'administrateur, un bouton modifier, un bouton supprimer
		for (int index=0; index<administrateurs.size(); index++) {
			/*panelAdmins.add((JLabel)labelAdministrateur.get(index));
			panelAdmins.add((JButton)boutonModif.get(index));
			panelAdmins.add((JButton)boutonSuppr.get(index));*/
			
			grid.addWidget((WLabel)labelAdministrateur.get(index), index, 0);
			grid.addWidget((WPushButton)boutonModif.get(index), index, 1);
			grid.addWidget((WPushButton)boutonSuppr.get(index), index, 2);			
		}
		/*JScrollPane scroll=new JScrollPane(panelAdmins);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
		panel.add(scroll, BorderLayout.CENTER);*/
		
		// on ajoute tout dans la fenetre
		/*getContentPane().removeAll();
		getContentPane().add(panel);*/
		getContents().addWidget(panel);
		getContents().addWidget(panelAdmins);
		
		// positionner au centre de l'ecran
		/*pack();
		setSize(Math.min(800, getWidth()), Math.min(800, getHeight()));
		setResizable(true);
		Rectangle rect=edtAppli.getBounds(); // getToolkit().getScreenSize();
		setLocation(Math.max(0,(int)(rect.x+(rect.width-getWidth())/2)), Math.max(0,(int)(rect.y+(rect.height-getHeight())/2)));
		setVisible(true);*/
		this.setClosable(true);
		this.setResizable(true);
		this.rejectWhenEscapePressed(true);
		this.resize(500, 500);
		this.show();
	}
	
	/*public void actionPerformed(ActionEvent e) {
		JButton b = (JButton)e.getSource();
		if (b.equals(b_ajouter)) {
            FormulaireSaisieAdministrateur form=new FormulaireSaisieAdministrateur(edtAppli, null, this);
            form.setVisible(true);
        }
		else if (boutonModif.contains(b)) {
			int index=boutonModif.indexOf(b);
			String admin=administrateurs.get(index);
            FormulaireSaisieAdministrateur form=new FormulaireSaisieAdministrateur(edtAppli, admin, this);
            form.setVisible(true);
        }
		else if (boutonSuppr.contains(b)) {
			int index=boutonSuppr.indexOf(b);
			String admin=administrateurs.get(index);
			int reponse=JOptionPane.showConfirmDialog(this, "Confirmer la suppression de "+admin, "Suppression d'un administrateur", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (reponse==JOptionPane.YES_OPTION) { // on supprime l'etudiant
				edtAppli.getControleurDb().supprimerAdministrateur(admin);
			}
		}
		afficher();
	}*/
	
}
