package EDT;

import eu.webtoolkit.jwt.WDialog;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WScrollEvent;
import eu.webtoolkit.jwt.Signal1.Listener;
import eu.webtoolkit.jwt.WBoxLayout.Direction;
import eu.webtoolkit.jwt.WLabel;
import eu.webtoolkit.jwt.WBorderLayout;
import eu.webtoolkit.jwt.WBoxLayout;
import eu.webtoolkit.jwt.WComboBox;
import eu.webtoolkit.jwt.WCompositeWidget;
import eu.webtoolkit.jwt.WGridLayout;
import eu.webtoolkit.jwt.Signal;
import java.util.*;

public class FormulaireProfs_wt extends WDialog{
	EDTApplication_wt edtAppli;
	private TreeMap<String,Prof> profs;
	WPushButton b_ajouter,b_ldap;
	WDialog dialog = this;
	private WComboBox listeProfs;
	private TreeSet<String> nomsProfs;
	private Prof prof;
	private WPushButton b_supprimer;
	private WPushButton b_modifier;

	FormulaireProfs_wt(EDTApplication_wt _edtAppli) {
		super("Gestion des profs");		
		edtAppli=_edtAppli;
		afficher();
	}

	public void afficher() {
		getFooter().clear();
		getContents().clear();

		WContainerWidget panelProfs = new WContainerWidget();
		WBoxLayout grid = new WBoxLayout(Direction.TopToBottom);
		panelProfs.setLayout(grid);

		b_ldap=new WPushButton("Ajouter des profs via LDAP");
		b_ldap.clicked().addListener(b_ldap, new Signal.Listener() {
			public void trigger() {
				accept();
				new FormulaireLdapProfs_wt(edtAppli);
			}
		});
		b_ldap.setStyleClass("btn-info"); 
		getFooter().addWidget(b_ldap);

		String semaineEnCours=(String)(edtAppli.getListeSemaines().getValueText());
		String[]tab=semaineEnCours.split("/");
		int debutAnneeScolaire=Integer.parseInt(tab[2].substring(tab[2].length()-4,4));
		if (Integer.parseInt(tab[1])<7)
			debutAnneeScolaire--;

		listeProfs = new WComboBox();
		listeProfs.setAttributeValue("style", "text-align:center;");
		profs=edtAppli.getControleurDb().getProfsAdmin2(edtAppli.getControleurDb().getIdAdmin(),debutAnneeScolaire,true);
		nomsProfs = new TreeSet<String>();
		for(String m: profs.keySet()) {
			if(!nomsProfs.contains(m)) {
				listeProfs.addItem(m);   
				nomsProfs.add(m);
			}
		}
		prof=profs.get(listeProfs.getCurrentText().getValue());
		grid.addWidget(listeProfs);
		listeProfs.changed().addListener(listeProfs, new Signal.Listener() {
			public void trigger() {
				prof=profs.get(listeProfs.getCurrentText().getValue());
			}
		});	


		b_modifier=new WPushButton("Modifier");
		b_modifier.clicked().addListener(b_modifier, new Signal.Listener() {
			public void trigger() {
				accept();
				new FormulaireSaisieProf_wt(edtAppli, prof, dialog);
			}            		
		});
		b_modifier.setStyleClass("btn-info");            
		b_modifier.setAttributeValue("style", "margin-right:50px;margin-left:50px;margin-top:10px;");
        grid.addWidget(b_modifier);

		b_supprimer=new WPushButton("Supprimer");
		b_supprimer.clicked().addListener( b_supprimer, new Signal.Listener() {
			public void trigger() {
				WDialog confirmerSuppr = new WDialog("Suppression de prof");
				new WLabel("Confirmez-vous la suppression de "+prof.getPrenom()+" "+prof.getNom()+" ?", confirmerSuppr.getContents());
				WPushButton ok = new WPushButton("Oui", confirmerSuppr.getFooter());
				WPushButton annuler = new WPushButton("Non", confirmerSuppr.getFooter());

				ok.setStyleClass("btn-danger");
				annuler.setStyleClass("btn-primary");

				ok.clicked().addListener(ok, new Signal.Listener() {
					public void trigger() {
						confirmerSuppr.accept();
						String res = edtAppli.getControleurDb().supprimerProf2(prof);
						if(res==null) {
							Utilitaire.showMessageDialog("ControleurDB : supprimerProf()", "Le prof a été supprimé.");
							afficher();

						}else {
							Utilitaire.showMessageDialog("ControleurDB : supprimerProf()", res);
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
		b_supprimer.setStyleClass("btn-warning");
        b_supprimer.setAttributeValue("style", "margin-right:50px;margin-left:50px;margin-top:10px;");
        grid.addWidget(b_supprimer);

		getContents().addWidget(panelProfs);
		this.setResizable(true);
		this.setClosable(true);
		this.rejectWhenEscapePressed(true);
		this.resize(500, 300);
		this.show();	
	}

}
