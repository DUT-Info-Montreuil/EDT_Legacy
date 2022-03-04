package EDT;

import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WPaintDevice;
import eu.webtoolkit.jwt.WPainter;
import eu.webtoolkit.jwt.WColor;
import eu.webtoolkit.jwt.WBrush;
import eu.webtoolkit.jwt.WLength;

import eu.webtoolkit.jwt.WPaintedWidget;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;


class PreviewRect extends WPaintedWidget {
	private int r_, v_, b_;
  
	PreviewRect(WContainerWidget parent) {
	  super(parent);
	  this.r_ = 255;
	  this.v_ = 255;
	  this.b_ = 255;
	  //this.resize(new WLength(400), new WLength(50));
	}
	
	public PreviewRect() {
	  this((WContainerWidget)null);
	}
	
	void setColor(int r, int v, int b) {
	  this.r_ = r;
	  this.v_ = v;
	  this.b_ = b;
	  this.update();
	}
	
	protected void paintEvent(WPaintDevice paintDevice) {
	  WPainter painter = new WPainter(paintDevice);
	  painter.setBrush(new WBrush(new WColor(r_, v_, b_)));
	  painter.drawRect(0, 0, 100, 100);
	}
}