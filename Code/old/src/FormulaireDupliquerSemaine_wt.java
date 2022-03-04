package EDT;

import eu.webtoolkit.jwt.WDialog;
import eu.webtoolkit.jwt.WComboBox;
import eu.webtoolkit.jwt.WPanel;
import eu.webtoolkit.jwt.WGridLayout;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WBorderLayout;
import eu.webtoolkit.jwt.WCheckBox;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WProgressBar;
import eu.webtoolkit.jwt.WTimer;
import eu.webtoolkit.jwt.Side;
import eu.webtoolkit.jwt.WAbstractToggleButton;
import eu.webtoolkit.jwt.WLabel;
import eu.webtoolkit.jwt.CheckState;
import eu.webtoolkit.jwt.Signal;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.JScrollPane;
import javax.swing.Timer;

import javax.swing.*;

/**
 * formulaire de duplication d'une semaine
 */
/*public class FormulaireDupliquerSemaine extends JDialog implements ActionListener {*/
public class FormulaireDupliquerSemaine_wt extends WDialog {
	EDTApplication_wt edtAppli;
	/*JCheckBox[] semaines;*/
	WCheckBox[] semaines;
	WPushButton b_selTout;
	WPushButton b_sel1sur2;
	WPushButton b_inverser;
	/*JToggleButton b_desactHorsDates;*/
	WPushButton b_valider;
	WPushButton b_annuler;
	boolean desactHorsDates;
	int indexDebutEnabled, indexFinEnabled; // index de la première et de la dernière semaines autorisées
	JProgressBar progBar;
	int avancement;
	Timer timer;
	String erreursDuplication;
	
	WTimer intervalTimer;
	WProgressBar bar;
	double maxValue;
	Thread t;
	
	FormulaireDupliquerSemaine_wt(EDTApplication_wt _edtAppli) {
		/*super(_edtAppli, "Duplication de semaine", true);*/
		super("Duplication de semaine");
		
		/*//procedure qui masque et ferme la fenetre
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose(); // on ferme la fenêtre
			}
		});*/
		edtAppli=_edtAppli;
		/*JComboBox sem=edtAppli.getListeSemaines();*/
		WComboBox sem=edtAppli.getListeSemaines();
		// on ne va garder que les semaines de l'année scolaire en cours (en cas de retour en arrière il y a d'autres semaines)
        int nbSemaines=0;
        for (int i = 0; i < sem.getCount(); i++)	//.getItemCount()
            if (Utilitaire.anneeScolaireEnCours(sem.getItemText(i).toString()))	//At(i)))
                nbSemaines++;
		/*semaines=new JCheckBox[nbSemaines];*/
		semaines=new WCheckBox[nbSemaines];
        int index=0;
        for (int i = 0; i < sem.getCount(); i++)	//.getItemCount()
            if (Utilitaire.anneeScolaireEnCours((String)sem.getItemText(i).toString()))	//At(i))) 
       			semaines[index++]=new WCheckBox((String)sem.getItemText(i).toString());	//At(i)))
		// création des boutons
		b_selTout=new WPushButton("Sélectionner Tout");
		b_sel1sur2=new WPushButton("Sélectionner 1 sur 2");
		b_inverser=new WPushButton("Inverser la sélection");
		/*b_desactHorsDates=new JToggleButton("Désactiver hors dates cochées");*/
		b_valider=new WPushButton("Valider");
		b_valider.setStyleClass("btn-success");
		b_annuler=new WPushButton("Annuler");
		b_annuler.setStyleClass("btn-primary");		
		
		/*b_selTout.addActionListener(this);
		b_sel1sur2.addActionListener(this);
		b_inverser.addActionListener(this);
		b_desactHorsDates.addActionListener(this);
		b_valider.addActionListener(this);
		b_annuler.addActionListener(this);*/		
		b_selTout.clicked().addListener(b_selTout, new Signal.Listener() {
			public void trigger() {
					bar.setHidden(true);
					for (int i = indexDebutEnabled; i<= indexFinEnabled; i++)
						if (semaines[i].isEnabled())
							semaines[i].setChecked(true);
			}	
		});
		b_sel1sur2.clicked().addListener(b_sel1sur2, new Signal.Listener() {
				public void trigger() {
					bar.setHidden(true);					
					for (int i = indexDebutEnabled; i<= indexFinEnabled; i++)
						if (semaines[i].isEnabled())
							semaines[i].setChecked(false);

					for (int i = indexDebutEnabled; i<= indexFinEnabled; i+=2)
						if (semaines[i].isEnabled())
							semaines[i].setChecked(true);
				}
		});
		b_inverser.clicked().addListener(b_inverser, new Signal.Listener() {
				public void trigger() {
					bar.setHidden(true);
					for (int i = indexDebutEnabled; i<= indexFinEnabled; i++)
						if (semaines[i].isEnabled())
							semaines[i].setChecked(!semaines[i].isChecked());
				}
		});
		b_valider.clicked().addListener(b_valider, new Signal.Listener() {
				public void trigger() {
					dupliquerSemaine();
					if (maxValue>0) {
						//b_selTout.setEnabled(false);
						//b_sel1sur2.setEnabled(false);
						//b_inverser.setEnabled(false);
						//b_valider.setEnabled(false);
						////b_annuler.removeStyleClass("btn-info");
						////b_annuler.setStyleClass("btn-success");
						getTitleBar().setDisabled(true);
						getContents().setDisabled(true);
						b_valider.setEnabled(false);
						b_annuler.setText("Arrêter");
						avancement=0;
						bar.setValue(avancement);
						bar.setHidden(false);						
						intervalTimer.start();
					} else {
						Utilitaire.showMessageDialog("Duplication semaine","Aucun cours à dupliquer !");
					}	
				}
		});
		b_annuler.clicked().addListener(b_annuler, new Signal.Listener() {
				public void trigger() {
					if (t!=null) {
						t.stop();
					}
					intervalTimer.stop();
					if (!b_annuler.getText().equals("Arrêter")) {
						reject();
					} else {
						getTitleBar().setDisabled(false);
						getContents().setDisabled(false);
						b_valider.setEnabled(true);
						b_annuler.setText("Fermer");
						//bar.setHidden(true);
						//avancement=0;
						//bar.setValue(avancement);
					}
				}
		});
		erreursDuplication="";
		
		// toutes les checkbox sont actives au départ
		desactHorsDates=false;
		indexDebutEnabled=0;
		indexFinEnabled=semaines.length-1;
		
		// barre de progression
		/*progBar=new JProgressBar();
		progBar.setStringPainted(true);
		progBar.setOrientation(JProgressBar.VERTICAL);*/
		bar = new WProgressBar();
		bar.setValue(0);		
		bar.setHidden(true);
		
		avancement=0;

		/*timer = new Timer(200, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				progBar.setValue(avancement);
				if (avancement >= progBar.getMaximum()) {
					timer.stop();
					progBar.setValue(progBar.getMaximum());
					if (!erreursDuplication.equals("")) {
						JScrollPane erreurs=new JScrollPane(new JTextArea(erreursDuplication));
						erreurs.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
						erreurs.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				        erreurs.getVerticalScrollBar().setUnitIncrement(20);
						erreurs.setMaximumSize(new Dimension(800,800));
						JOptionPane.showMessageDialog(edtAppli, erreurs);
					}
					dispose();
				}
			}
		});*/
		intervalTimer = new WTimer(this);
		intervalTimer.setInterval(100);
		intervalTimer.timeout().addListener(this, new Signal.Listener() {
			public void trigger() {
				//Utilitaire.showMessageDialog("this", "avancement="+avancement+", maxvalue="+ maxValue);
				if (avancement == maxValue) {
					Utilitaire.showMessageDialog("Duplication de semaine","Duplication terminée !");
					getTitleBar().setDisabled(false);
					getContents().setDisabled(false);				
					b_valider.setEnabled(true);
					b_annuler.setText("Fermer");
					bar.setHidden(true);
					intervalTimer.stop();
					avancement=0;
				}	
				bar.setValue(avancement);
			}				
		});				
		afficher();
	}
	
	/*public void actionPerformed(ActionEvent e) {
		AbstractButton b = (AbstractButton)e.getSource();
		if (b.equals(b_selTout))
			for (int i = indexDebutEnabled; i <= indexFinEnabled; i++)
				semaines[i].setSelected(true);
		else if (b.equals(b_sel1sur2))
			for (int i = indexDebutEnabled; i <= indexFinEnabled; i+=2)
				semaines[i].setSelected(true);
		else if (b.equals(b_desactHorsDates)) {
			if (desactHorsDates) { // on déselectionne le bouton : on réactive toutes les checkbox
				for (int i = 0; i < semaines.length; i++)
					semaines[i].setEnabled(true);
				indexDebutEnabled=0;
				indexFinEnabled=semaines.length-1;
			}
			else { // on sélectionne le bouton : on désactive certaines semaines
				while (indexDebutEnabled<semaines.length && !semaines[indexDebutEnabled].isSelected())
					semaines[indexDebutEnabled++].setEnabled(false);
				while (indexFinEnabled>=0 && !semaines[indexFinEnabled].isSelected())
					semaines[indexFinEnabled--].setEnabled(false);
			}
			desactHorsDates=!desactHorsDates; // on inverse la position du bouton
//			validate(); // on valide l'affichage
		}
		else if (b.equals(b_inverser))
			for (int i = indexDebutEnabled; i <= indexFinEnabled; i++)
				semaines[i].setSelected(!semaines[i].isSelected());
		else if (b.equals(b_valider)) {
			dupliquerSemaine();
		}
		else if (b.equals(b_annuler)) {
			dispose();
		}
	}*/

	private void dupliquerSemaine() {
		// recherche de toutes les plages horaires du groupe sélectionné et de la semaine sélectionnée
		String semaineAdupliquer=(String)edtAppli.getListeSemaines().getValueText();	//.getSelectedItem();
		String promoAdupliquer=(String)edtAppli.getListePromotions().getValueText();	//.getSelectedItem();
		//Utilitaire.showMessageDialog(this, "semaineAdupliquer="+ semaineAdupliquer + ", promoAdupliquer="+ promoAdupliquer);
		final Vector<PlageHoraire> plagesAdupliquer=edtAppli.getControleurDb().getPlagesHoraires(semaineAdupliquer, promoAdupliquer);

		// nombre de duplications à faire
		final ArrayList<String> lundiDestination=new ArrayList<String>(); // les dates destinations
		for (int i = indexDebutEnabled; i <= indexFinEnabled; i++)
		if (semaines[i].isChecked() && semaines[i].isEnabled()) {
			lundiDestination.add(semaines[i].getText().toString());
			//Utilitaire.showMessageDialog("duplicSemaine",""+semaines[i].getText().toString());
			/*if (semaines[i].isChecked()) lundiDestination.add(semaines[i].getText().toString());*/
		}
		/*progBar.setMaximum(lundiDestination.size()*plagesAdupliquer.size()); // nombre de semaines sélectionnées * nb de plages dans la semaine à dupliquer*/
		maxValue = lundiDestination.size()*plagesAdupliquer.size();
		if (maxValue>0) {
				bar.setMaximum(maxValue);
				//Utilitaire.showMessageDialog(this, "semaineAdupliquer="+ semaineAdupliquer + ", max="+ lundiDestination.size()*plagesAdupliquer.size());
				// on duplique dans un thread à part
				Runnable doConstruct = new Runnable() {
					public void run() {
						for (Iterator<String> iterLundi = lundiDestination.iterator(); iterLundi.hasNext();) {
							String lundiDest=iterLundi.next();
							String lundiSource=(String)edtAppli.getListeSemaines().getValueText();	//.getSelectedItem();
							int nbJoursDecalage=Math.round((Utilitaire.calculerDate(lundiDest).getTimeInMillis()-Utilitaire.calculerDate(lundiSource).getTimeInMillis())/(float)86400000);
					//System.out.println("src=" + lundiSource + " dest="+lundiDest + " décalage=" + nbJoursDecalage + "long="+(Utilitaire.calculerDate(lundiDest).getTimeInMillis()+"-"+Utilitaire.calculerDate(lundiSource).getTimeInMillis()));
							for (Iterator<PlageHoraire> iterPlage = plagesAdupliquer.iterator(); iterPlage.hasNext();) {
								PlageHoraire plage=iterPlage.next();
								dupliquerPlageHoraire(plage, nbJoursDecalage);						
							}
						}
					}
				};
				/*Thread t = new Thread(doConstruct);*/
				t = new Thread(doConstruct);
				/*setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				b_selTout.removeActionListener(this);
				b_sel1sur2.removeActionListener(this);;
				b_inverser.removeActionListener(this);;
				b_desactHorsDates.removeActionListener(this);;
				b_valider.removeActionListener(this);;
				b_annuler.removeActionListener(this);;*/
				t.start();
				/*timer.start();*/		
		}
	}
	
	/*
	 * duplique une plage horaire en la décalant de nbJoursDecalage jours
	 */
	private void dupliquerPlageHoraire(PlageHoraire plage, int nbJoursDecalage) {
//System.out.println(plage);
		int idPlage=edtAppli.getControleurDb().getIdPlageHoraire();
		SimpleTimeZone tz=new SimpleTimeZone(1*60*60*1000, "CET"); // GMT+1
		tz.setStartRule(Calendar.MARCH, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
		tz.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
		GregorianCalendar debut=new GregorianCalendar(tz);
		debut.setTimeInMillis(plage.getDateDebut().getTimeInMillis());
		GregorianCalendar fin=new GregorianCalendar(tz);
		fin.setTimeInMillis(plage.getDateFin().getTimeInMillis());
		debut.add(Calendar.DAY_OF_YEAR, nbJoursDecalage);
		fin.add(Calendar.DAY_OF_YEAR, nbJoursDecalage);
		PlageHoraire nouvellePlage=new PlageHoraire(idPlage, debut, fin, plage.getMemo(), plage.getIdSalle(), plage.getIdGroupe(), plage.getIdMatiere(), plage.getIdProf(), plage.accesInternet(), plage.IsControle(), plage.getIdSalle2(), plage.getIdProf2(), plage.getIdProf3(), plage.getIdProf4(), plage.getIdProfQuiFaitAppel());
//System.out.println(nouvellePlage);
		if (edtAppli.getControleurDb().getPlageHoraire(nouvellePlage.getDateDebut(), "", 0)==null) { // ce n'est pas un jour férié
			String erreur=edtAppli.getControleurDb().ajouterPlageHoraire(nouvellePlage);
			if (!erreur.equals(""))
				erreursDuplication+=Utilitaire.afficherDate(nouvellePlage.getDateDebut(),nouvellePlage.getDateFin()) + " :\n" + erreur;
		}
		avancement++;
	}
		
	public void afficher() {
		/*dispose();*/
		
		/*JPanel panel=new JPanel();
		panel.setLayout(new BorderLayout());*/
		WPanel panel = new WPanel();
		WBorderLayout border = new WBorderLayout();
		
		/*JPanel panelDates=new JPanel();
		panelDates.setLayout(new GridLayout(1+Math.max(1,(semaines.length-1)/4),4));*/
		WContainerWidget panelDates=new WContainerWidget();
		WGridLayout grid = new WGridLayout();
		panelDates.setLayout(grid);
		
		for (int i = 0; i < semaines.length; i++) {
			/*panelDates.add(semaines[i]);*/
			grid.addWidget(semaines[i], i,0);
			
			String textSemaine = ((WCheckBox)semaines[i]).getText().toString();
			if (textSemaine.equals(edtAppli.getListeSemaines().getValueText())) {
				//pour suriligner la semaine à dupliquer
				((WCheckBox)semaines[i]).setEnabled(false);
				((WCheckBox)semaines[i]).setTristate(true);
				((WCheckBox)semaines[i]).setCheckState(CheckState.PartiallyChecked);	//= "-"
				((WCheckBox)semaines[i]).setStyleClass("btn-success");
			}
		}
		/*JScrollPane scroll=new JScrollPane(panelDates);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
		panel.add(scroll, BorderLayout.CENTER);*/
		
		
		/*JPanel panelBoutons=new JPanel();
		panelBoutons.setLayout(new GridLayout(2,3));*/		
		WContainerWidget panelBoutons=new WContainerWidget();
		grid = new WGridLayout();
		panelBoutons.setLayout(grid);
		
		/*panelBoutons.add(b_selTout);
		panelBoutons.add(b_sel1sur2);
		panelBoutons.add(b_inverser);
		panelBoutons.add(b_desactHorsDates);
		panelBoutons.add(b_valider);
		panelBoutons.add(b_annuler);
		panel.add(panelBoutons, BorderLayout.SOUTH);
		panel.add(progBar, BorderLayout.EAST);*/
		//grid.addWidget(b_selTout, 0, 0);
		//grid.addWidget(b_sel1sur2, 0, 1);
		//grid.addWidget(b_inverser, 0, 2);
		//grid.addWidget(b_valider, 1, 1);
		//grid.addWidget(b_annuler, 1, 2);
		getTitleBar().addWidget(b_selTout);
		getTitleBar().addWidget(b_sel1sur2);
		getTitleBar().addWidget(b_inverser);
		//grid.addWidget(b_desactHorsDates);//		
		getFooter().addWidget(b_valider);
		getFooter().addWidget(b_annuler);
		getFooter().addWidget(bar);	
		
		// on ajoute tout dans la fenetre
		/*getContentPane().removeAll();
		getContentPane().add(panel);*/		
		getContents().addWidget(panelDates);
		//getContents().addWidget(panelBoutons);		
		
		// positionner au centre de l'écran
		/*pack();
		setSize(Math.min(800, getWidth()), Math.min(800, getHeight()));
		setResizable(true);
		Rectangle rect=edtAppli.getBounds(); // getToolkit().getScreenSize();
		setLocation(Math.max(0,(int)(rect.x+(rect.width-getWidth())/2)), Math.max(0,(int)(rect.y+(rect.height-getHeight())/2)));
		setVisible(true);*/
		this.setResizable(true);
		//this.setClosable(true);	//clique sur "x" non-gérée
		this.resize(500, 500);
		this.show();
	}
}

