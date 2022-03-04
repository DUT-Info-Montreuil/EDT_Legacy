package EDT;

import eu.webtoolkit.jwt.WDialog;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WLabel;
import eu.webtoolkit.jwt.WGridLayout;
import eu.webtoolkit.jwt.WBorderLayout;
import eu.webtoolkit.jwt.Signal;
import java.util.*;

/**
 * formulaire de gestion des salles
 */
/*public class FormulaireSalles extends JDialog implements ActionListener {*/ 
public class FormulaireSalles_wt extends WDialog { 
	EDTApplication_wt edtAppli;
	ArrayList<Salle> salles;
	ArrayList<WLabel> labelSalle;
	ArrayList<WPushButton> boutonModif;
	ArrayList<WPushButton> boutonSuppr;
	WPushButton b_ajoutSalle;
	
	WPushButton b;	
	WDialog dialog = this;
	
	FormulaireSalles_wt(EDTApplication_wt _edtAppli) {
		/*super(_edtAppli, "Gestion des salles", true);*/
		super("Gestion des salles");		
		
		/*//procedure qui masque et ferme la fenetre
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				edtAppli.mettreAJour();
				/ *dispose(); // on ferme la fenetre* /
			}
		});*/
		edtAppli=_edtAppli;
		afficher();
	}
	
	public void afficher() {
		/*dispose();*/
		salles=new ArrayList<Salle>(edtAppli.getControleurDb().getSalles());
		
		/*JPanel panel=new JPanel();
		panel.setLayout(new BorderLayout());*/		
		WContainerWidget panel = new WContainerWidget();
		WBorderLayout border = new WBorderLayout();
		panel.setLayout(border);		

		/*b_ajoutSalle=new JButton("Ajouter une nouvelle salle");
		b_ajoutSalle.addActionListener(this);
		panel.add(b_ajoutSalle, BorderLayout.NORTH);*/
		b_ajoutSalle=new WPushButton("Ajouter");
		b_ajoutSalle.clicked().addListener(b_ajoutSalle, new Signal.Listener() {
				public void trigger() {
					accept();
					new FormulaireSaisieSalle_wt(edtAppli, null, dialog);
				}				
		});
		b_ajoutSalle.setStyleClass("btn-success");
		getFooter().addWidget(b_ajoutSalle);
			
		/*JPanel panelSalles=new JPanel();
		panelSalles.setLayout(new GridLayout(salles.size(),3));*/
		WContainerWidget panelSalles=new WContainerWidget();
		WGridLayout grid = new WGridLayout();
		panelSalles.setLayout(grid);
		
		/*labelSalle=new ArrayList<JLabel>();
		boutonModif=new ArrayList<JButton>();
		boutonSuppr=new ArrayList<JButton>();
		JButton b;*/
		labelSalle=new ArrayList<WLabel>();
		boutonModif=new ArrayList<WPushButton>();
		boutonSuppr=new ArrayList<WPushButton>();
		
		for (int i=0; i<salles.size(); i++) {
			Salle salle = (Salle)salles.get(i);
			labelSalle.add(new WLabel(salle.getNom()));
			
			
			b=new WPushButton("Modifier");
			//Elevez pour laisser les users
			if (edtAppli.loginEDT.compareTo("hassoun")!=0 ) {
				b.disable();
			}
			b.clicked().addListener(b, new Signal.Listener() {
				public void trigger() {
					accept();
					new FormulaireSaisieSalle_wt(edtAppli, salle, dialog);					
				}				
			});
			b.setStyleClass("btn-info");			
			boutonModif.add(b);
			

			b=new WPushButton("Supprimer");
			//Elevez pour laisser les users

			if (edtAppli.loginEDT.compareTo("hassoun")!=0 ) {
				b.disable();
			}
			b.clicked().addListener(b, new Signal.Listener() {
					public void trigger() {
						WDialog confirmerSuppr = new WDialog("Suppression de salle");
						new WLabel("Confirmez-vous la suppression de "+salle.getNom()+" ?", confirmerSuppr.getContents());
						WPushButton ok = new WPushButton("Oui", confirmerSuppr.getFooter());
						WPushButton annuler = new WPushButton("Non", confirmerSuppr.getFooter());
						
						ok.setStyleClass("btn-danger");
						annuler.setStyleClass("btn-primary");
						
						ok.clicked().addListener(ok, new Signal.Listener() {
								public void trigger() {
									confirmerSuppr.accept();
									boolean l = edtAppli.getControleurDb().supprimerSalle(salle);
									if(l==true){								
										accept();
										new FormulaireSalles_wt(edtAppli);
									}
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
			b.setStyleClass("btn-warning");
			boutonSuppr.add(b);		
			
			
		}
		// on place le nom de la salle, un bouton modifier, un bouton supprimer
		for (int index=0; index<salles.size(); index++) {
			grid.addWidget((WLabel)labelSalle.get(index), index, 0);
			grid.addWidget((WPushButton)boutonModif.get(index), index, 1);
			grid.addWidget((WPushButton)boutonSuppr.get(index), index, 2);
		}		
				
		/*JScrollPane scroll=new JScrollPane(panelSalles);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        panel.add(scroll, BorderLayout.CENTER);*/
        
		// on ajoute tout dans la fenetre
		/*getContentPane().removeAll();
		getContentPane().add(panel);*/
		getContents().addWidget(panel);
		getContents().addWidget(panelSalles);
		
		//Elevez pour laisser les users
		if (edtAppli.loginEDT.compareTo("hassoun")!=0 ) {
			b_ajoutSalle.disable();
			b_ajoutSalle.setText("Ajouter une Salle : Veuillez Contacter le ccri.");
		}
		
		// positionner au centre de l'ecran
		/*setSize(Math.min(800, getWidth()), Math.min(800, getHeight()));*/
		this.setResizable(true);
		this.setClosable(true);
		this.rejectWhenEscapePressed(true);
		this.resize(500, 500);
		this.show();
	}
		
	/*public void actionPerformed(ActionEvent e) {
		JButton b = (JButton)e.getSource();
		if (b.equals(b_ajoutSalle)) { // label.equals("Ajouter une nouvelle salle")) {
			FormulaireSaisieSalle form=new FormulaireSaisieSalle(edtAppli, null, this);
			form.setVisible(true);
		} else if (boutonModif.contains(b)) { // label=="Modifier") {
			int index=boutonModif.indexOf(b);
			Salle s=(Salle)salles.get(index);
			FormulaireSaisieSalle form=new FormulaireSaisieSalle(edtAppli, s, this);
			form.setVisible(true);
		}
		else if (boutonSuppr.contains(b)) { // label=="Supprimer") {
			int index=boutonSuppr.indexOf(b);
			Salle s=(Salle)salles.get(index);
			int reponse=JOptionPane.showConfirmDialog(this, "Confirmer la suppression de "+s.getNom(), "Suppression d'une salle", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (reponse==JOptionPane.YES_OPTION) { // on supprime la salle
				edtAppli.getControleurDb().supprimerSalle(s);
			}
		}
		afficher();
		//getGlassPane().validate();
	}*/	
	
}
