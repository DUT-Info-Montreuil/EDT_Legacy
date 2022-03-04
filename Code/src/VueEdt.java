package EDT;
//PB 

import eu.webtoolkit.jwt.WEnvironment;
import eu.webtoolkit.jwt.WApplication;
import eu.webtoolkit.jwt.WMouseEvent;//ajouté le 15 mai 2019
import eu.webtoolkit.jwt.WCssDecorationStyle;
import eu.webtoolkit.jwt.WColor;
import eu.webtoolkit.jwt.WPaintedWidget;
import eu.webtoolkit.jwt.WPaintDevice;
import eu.webtoolkit.jwt.utils.WebGraphics2D;
import eu.webtoolkit.jwt.WPainter;
import eu.webtoolkit.jwt.WLength;
import eu.webtoolkit.jwt.WFont;
import eu.webtoolkit.jwt.Signal1;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class VueEdt{


	private WebGraphics2D g;
	
	private EDTApplication_wt edtAppli; 
	private EDTSemaine edtSemaine; 
	private int largeurJour, largeurPlageTP;
	private float hauteurLigne;
	private int hauteurVue, largeurVue;
	private int largeurHeures;
	private int nbTP;
	private int nbColonnesVirtuelles;
	private Font font_jours;
	private Font font_heures;
	private Font font_cours;

	
}
