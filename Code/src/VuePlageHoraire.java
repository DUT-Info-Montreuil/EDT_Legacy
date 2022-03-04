package EDT;
//PB

import eu.webtoolkit.jwt.WPaintedWidget;
import eu.webtoolkit.jwt.WPaintDevice;
import eu.webtoolkit.jwt.WPainter;
import eu.webtoolkit.jwt.utils.WebGraphics2D;

public class VuePlageHoraire{

	private PlageHoraire plage;
	private EDTApplication_wt edtAppli;
	private WebGraphics2D g;
	private int largeurJour, largeurPlageTP;
	
	public VuePlageHoraire(EDTApplication_wt edtAppli,PlageHoraire plage){
		this.plage=plage;
		this.edtAppli=edtAppli;
	}	
}