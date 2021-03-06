package EDT;

import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WLabel;
import eu.webtoolkit.jwt.WCssDecorationStyle;
import eu.webtoolkit.jwt.WColor;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

/*class PreviewPanel extends JPanel implements ChangeListener {*/
class PreviewPanel_wt extends WContainerWidget implements ChangeListener {
  protected JColorChooser m_chooser;
  protected WLabel labelPreview;
  WCssDecorationStyle cds;
  
  public PreviewPanel_wt(JColorChooser chooser, String nomMat, Color background) {
    m_chooser = chooser;
	chooser.getSelectionModel().addChangeListener(this);
	labelPreview = new WLabel(nomMat);	
	/*labelPreview.setBackground(background);	
	//labelPreview.setFont(new Font("Arial",Font.BOLD, 50));
	labelPreview.setForeground(Color.BLACK);
	labelPreview.setOpaque(true);*/
	
	
	cds = new WCssDecorationStyle();
	labelPreview.setDecorationStyle(cds);
	//cds.setBackgroundColor(new WColor(background));
	cds.setForegroundColor(new WColor("black"));
	
	
	/*add(labelPreview);*/
	addWidget(labelPreview);
	/*setSize(100,50);*/
	this.resize(500, 500);
	m_chooser.setColor(background);
  }
  public void stateChanged(ChangeEvent evt) {
	Color c = m_chooser.getColor();
	if (c != null) {
		/*labelPreview.setBackground(c);*/
		cds = new WCssDecorationStyle();
		cds.setBackgroundColor(new WColor(c.getRed(),c.getGreen(),c.getBlue()));
		labelPreview.setDecorationStyle(cds);
	}
  }

}