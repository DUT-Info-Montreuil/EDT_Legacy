package EDT;

import eu.webtoolkit.jwt.WDialog;
import eu.webtoolkit.jwt.WMessageBox;
import eu.webtoolkit.jwt.Icon;
import eu.webtoolkit.jwt.StandardButton;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.Signal.Listener;

import java.awt.Container;
import java.awt.Dimension;
import java.util.*;

import javax.swing.Box;

import com.jgoodies.forms.layout.CellConstraints;

/*
 * Created on 1 juil. 2004
 *
 */
/**
 * @author dubrulle
 *
 */
public class Utilitaire {
	/**
	 * 
	 * @param date chaine de caractère sous la forme jj/mm/aaaa
	 * @return renvoie date sous forme de GregorianCalendar
	 */
	public static GregorianCalendar calculerDate(String date) {
		String[] tab=date.split("/");
		int jour=1,mois=1,annee=1970;
		if (tab.length==3) {
			jour=Integer.parseInt(tab[0]);
			mois=Integer.parseInt(tab[1]);
			annee=Integer.parseInt(tab[2]);
		}
		SimpleTimeZone tz=new SimpleTimeZone(1*60*60*1000, "CET"); // GMT+1
		tz.setStartRule(Calendar.MARCH, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
		tz.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
		GregorianCalendar g=new GregorianCalendar(tz);
		g.clear();
		g.set(annee,mois-1,jour); // -1 car janvier=0
		return g;
	}
	/**
	 * @return une chaine de caractère affichant la date et les heures de début et fin d'une plage
	 */
	public static String afficherDate(GregorianCalendar debut, GregorianCalendar fin) {
		return "" + debut.get(Calendar.DAY_OF_MONTH) + "/" + (debut.get(Calendar.MONTH)+1) + "/" + debut.get(Calendar.YEAR) + " de " + debut.get(Calendar.HOUR_OF_DAY) + "h" + debut.get(Calendar.MINUTE) + " à " + fin.get(Calendar.HOUR_OF_DAY) + "h" + fin.get(Calendar.MINUTE); 
	}
	
	public static int pgcd(int a,int b) {
		if (b!=0)
			return pgcd(b,a%b);
		else return a;
	}
	/**
	 * vérifie si la chaine est bien de la forme jj/mm/aaaa avec 1<=jj<=31 && 1<=mm<=12 && aa>=1970 et pas un samedi ou un dimanche et dans l'année scolaire en cours et datant de moins de 3 mois
	 */
	public static boolean dateCorrecte(String date,String loginEDT) {
		try {
			String[] tab=date.split("/");
			if (tab==null) return false;
			int j=Integer.parseInt(tab[0]), m=Integer.parseInt(tab[1]), a=Integer.parseInt(tab[2]);
			if (tab.length==3 && 0<j && j<=31 && 0<m && m<=12 && a>=1970) {
				GregorianCalendar cal=calculerDate(date);
			    if (cal.get(Calendar.DAY_OF_WEEK)!=Calendar.SATURDAY && cal.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY && anneeScolaireEnCours2(date,loginEDT))
			        return true;
			    else return false;
			}
			else return false;
		} catch (Exception e) {
			return false;
		}
	}
	/**
	 * vérifie si la chaine est bien de la forme xxh ou xxhmm avec 8<=xx<=18 && 0<=mm<=59 et entre 8h et 18h30) 
	 */
	public static boolean heureCorrecte(String date) {
		try {
			/*if (date.indexOf('h')==-1) // il manque le h
			return false;
			String[] tab=date.split("h");*/
			if (date.indexOf(':')==-1) // il manque le h
			return false;
			String[] tab=date.split(":");

			if (tab==null) return false;
			int h=Integer.parseInt(tab[0]);
			if (tab.length==1 && 8<=h && h<=18)
				return true;
			int m=Integer.parseInt("0"+tab[1]);
			if (tab.length==2 && 8<=h && h<=18 && 0<=m && m<=59 && !(h==18 && m>30))
				return true;
			else return false;
		}
		catch (Exception e) {
			return false;
		}
	}
    /**
     * vérifie si la chaine est bien une date de l'année scolaire en cours et pas trop ancienne (<3mois)
     */
    public static boolean anneeScolaireEnCours(String date) {
        SimpleTimeZone tz=new SimpleTimeZone(1*60*60*1000, "CET"); // GMT+1
        tz.setStartRule(Calendar.MARCH, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
        tz.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
        GregorianCalendar aujourdhui=new GregorianCalendar(tz);
        GregorianCalendar ilYa3mois=new GregorianCalendar(tz);
        ilYa3mois.set(Calendar.MONTH, aujourdhui.get(Calendar.MONTH)-3);
        GregorianCalendar cal=calculerDate(date);
        // modif datant de plus de 3 mois ?
        boolean moinsDe3mois=cal.after(ilYa3mois);
        // avant avril, on ne peut modifier que l'année scolaire en cours
        boolean avantAvril=aujourdhui.get(Calendar.MONTH)<Calendar.APRIL && ( (cal.get(Calendar.YEAR)==aujourdhui.get(Calendar.YEAR) && cal.get(Calendar.MONTH)<Calendar.AUGUST) || (cal.get(Calendar.YEAR)==aujourdhui.get(Calendar.YEAR)-1 && cal.get(Calendar.MONTH)>=Calendar.AUGUST) );
        // en avril, mai, juin et juillet, on peut modifier l'année scolaire en cours et la suivante
        boolean avrilMaiJuinJuillet=(aujourdhui.get(Calendar.MONTH)==Calendar.APRIL || aujourdhui.get(Calendar.MONTH)==Calendar.MAY || aujourdhui.get(Calendar.MONTH)==Calendar.JUNE || aujourdhui.get(Calendar.MONTH)==Calendar.JULY) && ( cal.get(Calendar.YEAR)==aujourdhui.get(Calendar.YEAR) || (cal.get(Calendar.YEAR)==aujourdhui.get(Calendar.YEAR)-1 && cal.get(Calendar.MONTH)>=Calendar.AUGUST) || (cal.get(Calendar.YEAR)==aujourdhui.get(Calendar.YEAR)+1 && cal.get(Calendar.MONTH)<Calendar.AUGUST) );
        // après août, on ne peux modifier que l'année scolaire en cours
        boolean apresAout=aujourdhui.get(Calendar.MONTH)>=Calendar.AUGUST && ( (cal.get(Calendar.YEAR)==aujourdhui.get(Calendar.YEAR) && cal.get(Calendar.MONTH)>=Calendar.AUGUST) || (cal.get(Calendar.YEAR)==aujourdhui.get(Calendar.YEAR)+1 && cal.get(Calendar.MONTH)<Calendar.AUGUST) );
        return moinsDe3mois && ( avantAvril || avrilMaiJuinJuillet || apresAout );
    }
    
    public static boolean anneeScolaireEnCours2(String date,String loginEdt) {
        SimpleTimeZone tz=new SimpleTimeZone(1*60*60*1000, "CET"); // GMT+1
        tz.setStartRule(Calendar.MARCH, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
        tz.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
        GregorianCalendar aujourdhui=new GregorianCalendar(tz);
        GregorianCalendar ilYa3mois=new GregorianCalendar(tz);
        //if (loginEdt.compareTo("hassoun")==0 || loginEdt.compareTo("fondraz")==0 || loginEdt.compareTo("kaiser")==0 || loginEdt.compareTo("baboulall")==0 || loginEdt.compareTo("lamolle")==0 )
        //    ilYa3mois.set(Calendar.MONTH, aujourdhui.get(Calendar.MONTH)-12);
        if (loginEdt.compareTo("hassoun")==0 ) ilYa3mois.set(Calendar.MONTH, aujourdhui.get(Calendar.MONTH)-12);
        else ilYa3mois.set(Calendar.MONTH, aujourdhui.get(Calendar.MONTH)-3);
		
        GregorianCalendar cal=calculerDate(date);
        // modif datant de plus de 3 mois ?
        boolean moinsDe3mois=cal.after(ilYa3mois);
        // avant avril, on ne peut modifier que l'année scolaire en cours
        boolean avantAvril=aujourdhui.get(Calendar.MONTH)<Calendar.APRIL && ( (cal.get(Calendar.YEAR)==aujourdhui.get(Calendar.YEAR) && cal.get(Calendar.MONTH)<Calendar.AUGUST) || (cal.get(Calendar.YEAR)==aujourdhui.get(Calendar.YEAR)-1 && cal.get(Calendar.MONTH)>=Calendar.AUGUST) );
        // en avril, mai, juin et juillet, on peut modifier l'année scolaire en cours et la suivante
        boolean avrilMaiJuinJuillet=(aujourdhui.get(Calendar.MONTH)==Calendar.APRIL || aujourdhui.get(Calendar.MONTH)==Calendar.MAY || aujourdhui.get(Calendar.MONTH)==Calendar.JUNE || aujourdhui.get(Calendar.MONTH)==Calendar.JULY) && ( cal.get(Calendar.YEAR)==aujourdhui.get(Calendar.YEAR) || (cal.get(Calendar.YEAR)==aujourdhui.get(Calendar.YEAR)-1 && cal.get(Calendar.MONTH)>=Calendar.AUGUST) || (cal.get(Calendar.YEAR)==aujourdhui.get(Calendar.YEAR)+1 && cal.get(Calendar.MONTH)<Calendar.AUGUST) );
        // après août, on ne peux modifier que l'année scolaire en cours
        boolean apresAout=aujourdhui.get(Calendar.MONTH)>=Calendar.AUGUST && ( (cal.get(Calendar.YEAR)==aujourdhui.get(Calendar.YEAR) && cal.get(Calendar.MONTH)>=Calendar.AUGUST) || (cal.get(Calendar.YEAR)==aujourdhui.get(Calendar.YEAR)+1 && cal.get(Calendar.MONTH)<Calendar.AUGUST) );
        return moinsDe3mois && ( avantAvril || avrilMaiJuinJuillet || apresAout );
    }
    
    
    
    /**
     * Adds fill components to empty cells in the first row and first column of the grid.
     * This ensures that the grid spacing will be the same as shown in the designer.
     * @param cols an array of column indices in the first row where fill components should be added.
     * @param rows an array of row indices in the first column where fill components should be added.
     */
    static void addFillComponents( Container panel, int[] cols, int[] rows )
    {
       Dimension filler = new Dimension(10,10);

       boolean filled_cell_11 = false;
       CellConstraints cc = new CellConstraints();
       if ( cols.length > 0 && rows.length > 0 )
       {
          if ( cols[0] == 1 && rows[0] == 1 )
          {
             /** add a rigid area  */
             panel.add( Box.createRigidArea( filler ), cc.xy(1,1) );
             filled_cell_11 = true;
          }
       }

       for( int index = 0; index < cols.length; index++ )
       {
          if ( cols[index] == 1 && filled_cell_11 )
          {
             continue;
          }
          panel.add( Box.createRigidArea( filler ), cc.xy(cols[index],1) );
       }

       for( int index = 0; index < rows.length; index++ )
       {
          if ( rows[index] == 1 && filled_cell_11 )
          {
             continue;
          }
          panel.add( Box.createRigidArea( filler ), cc.xy(1,rows[index]) );
       }

    }
    
	public static void showMessageDialog(WDialog dialog, String contenu) {
		if (dialog!= null) {
			showMessageDialog(dialog.getWindowTitle().toString(), contenu);
		}
	}

	public static void showMessageDialog(String titre, String contenu) {
		WMessageBox msgBox = new WMessageBox(titre, contenu, 
											Icon.Warning, 
											EnumSet.of(StandardButton.Ok));
		msgBox.setModal(true);
		msgBox.setFocus();
		msgBox.buttonClicked().addListener(msgBox, 
			new Signal.Listener() {
				public void trigger() {
					msgBox.remove();
				}			
		});
		msgBox.show();
	} 
	
	
}
