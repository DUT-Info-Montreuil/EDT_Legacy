package EDT;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;


import eu.webtoolkit.jwt.Side;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WBoxLayout;
import eu.webtoolkit.jwt.WCheckBox;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WDialog;
import eu.webtoolkit.jwt.WGridLayout;
import eu.webtoolkit.jwt.WLabel;
import eu.webtoolkit.jwt.WLineEdit;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WBoxLayout.Direction;

public class FormulaireNotif_wt extends WDialog {

	EDTApplication_wt edtAppli;
	WPushButton b_valider, b_annuler;
	WDialog dialog = this;
	HashMap<Integer,Integer> groupes=null;
	WCheckBox[] cb_groupesNotifs;
	ArrayList<String> groupes_noms = new ArrayList<String>();
	ArrayList<String> profs_noms = new ArrayList<String>();
	String commentaire="";
	WLineEdit tf_comm;	
	
	FormulaireNotif_wt(EDTApplication_wt _edtAppli,HashMap<Integer,Integer> groupes) {
		super("Notifications");
		this.groupes = groupes;
		edtAppli = _edtAppli;
		afficher();

	}

	public void afficher() {
		WContainerWidget panel = new WContainerWidget();
		WGridLayout grid = new WGridLayout();
		panel.setLayout(grid);

		WContainerWidget panelEtudiants=new WContainerWidget();
		WGridLayout grid2 = new WGridLayout();
		panelEtudiants.setLayout(grid2);
		Groupe groupe=null;
		Prof prof = null;
		for(int g : groupes.keySet()) {
			groupe=edtAppli.getControleurDb().getGroupe(g);
			if(groupe!=null) {
				groupes_noms.add(groupe.getNom());
			}
			prof = edtAppli.getControleurDb().getProf(groupes.get(g));
			if(prof!=null)
				if(!profs_noms.contains(prof.getLogin()))
					profs_noms.add(prof.getLogin());
		}
		cb_groupesNotifs=new WCheckBox[groupes_noms.size()+profs_noms.size()];       
		int i=0;
		int j=0;

		if(!groupes_noms.isEmpty() && !profs_noms.isEmpty()) {
			for(String g : groupes_noms) {
				if(!g.isEmpty()) {
					cb_groupesNotifs[i]=new WCheckBox(g);
					grid2.addWidget(cb_groupesNotifs[i], i, 0);
					i++;
				}
			}			
			for(String g : profs_noms) {
				if(!g.isEmpty()) {
					cb_groupesNotifs[i]=new WCheckBox(g);
					grid2.addWidget(cb_groupesNotifs[i],j, 1);
					j++;
				}
			}
		}
		
		grid.addWidget(panelEtudiants,0,0);
		getContents().addWidget(panel);
		
		WContainerWidget panelGroupeG = new WContainerWidget();
		WBoxLayout gridF = new WBoxLayout(Direction.TopToBottom);		
		panelGroupeG.setLayout(gridF);
		this.getFooter().addWidget(panelGroupeG);   


		b_valider=new WPushButton("Valider");
		b_valider.clicked().addListener(b_valider, new Signal.Listener() {
			public void trigger() {
				action();
			}
		});
		
		b_valider.setStyleClass("btn-success");     
		
		tf_comm=new WLineEdit("");
		tf_comm.setPlaceholderText("Commentaire");
		if(groupes.isEmpty()) {
			b_valider.disable();
			tf_comm.disable();
		}
		tf_comm.setFloatSide(Side.Left);
		getFooter().addWidget(tf_comm);
		getFooter().addWidget(b_valider);

		this.rejectWhenEscapePressed(true);
		this.setResizable(true);
		this.setClosable(true);
		this.resize(430, 250);
		this.show();

	}

	public void action() {
		final WDialog confirmerNotif = new WDialog("Notif Email");
		new WLabel("Etes-vous s??r de vouloir envoyer une notifcation par mail ?",confirmerNotif.getContents());
		WPushButton ok = new WPushButton("Oui", confirmerNotif.getFooter());
		WPushButton annuler = new WPushButton("Non", confirmerNotif.getFooter());
		ok.setStyleClass("btn-danger");
		annuler.setStyleClass("btn-primary");
		ok.clicked().addListener(ok, new Signal.Listener() {
			public void trigger() {
				confirmerNotif.accept();
				ArrayList<String>selected= new ArrayList<String>();
				for (int i=0;i<cb_groupesNotifs.length;i++){
					if (!cb_groupesNotifs[i].equals(null))
						if (cb_groupesNotifs[i].isChecked())
							selected.add(cb_groupesNotifs[i].getText().getValue());
				}
				for(String ns : selected) {
					for(String ng : groupes_noms) {
						if(ns.equals(ng)) {
							emailGroupes(ns);
							break;
						}
					}
					for(String ng : profs_noms) {
						if(ns.equals(ng)) {
							emailProfs(ns);
							break;
						}
					}
				}	
			}
		});
		annuler.clicked().addListener(annuler, new Signal.Listener() {
			public void trigger() {
				confirmerNotif.reject();
			}
		});
		confirmerNotif.setClosable(true);
		confirmerNotif.rejectWhenEscapePressed(true);
		confirmerNotif.show();	
	}

	public void emailGroupes(String groupe) {
		String promotion = edtAppli.getListePromotions().getValueText();
		String[] sA = promotion.split(" ");
		String departement = sA[1].toLowerCase()+sA[2].toLowerCase()+groupe.toLowerCase(); 
		String to = "etud"+departement+"@iut.univ-paris8.fr";
		String from = edtAppli.getLoginEDT()+"@iut.univ-paris8.fr";
		email(to,from);
	}

	public void emailProfs(String nom) {
		String to = nom+"@iut.univ-paris8.fr";
		String from = edtAppli.getLoginEDT()+"@iut.univ-paris8.fr";
		email(to,from);
	}

	public void email(String to1,String from2) {
		String to = to1;
		String from = from2;
		String host = "smtp.iut.univ-paris8.fr";
		String port = "25";
		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", host);
		properties.setProperty("mail.smtp.port", port);
		Session session = Session.getDefaultInstance(properties);
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setSubject("Attention modification  EDT");
			if(tf_comm.getText()!="") {
				message.setContent(tf_comm.getText(),"text/plain; charset=utf-8");
			}else {
				message.setContent("Envoie automatique en cas des modifications veillez ne pas r??pondre.", "text/plain; charset=utf-8");
			}
			Transport.send(message);
			Utilitaire.showMessageDialog("Succ??s : ","Votre notification as ??t?? bien envoy??.");
		} catch (MessagingException mex) {
			Utilitaire.showMessageDialog("Erreur : ","Votre notification n'as pas ??t?? envoy??.");
		}
	}
}