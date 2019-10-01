package amw.opos.labelling.gui.utils;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

public class GuiUtils {
	
	
	public static void displayCenter(Container container) {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		container.setLocation(dim.width/2-container.getSize().width/2, dim.height/2-container.getSize().height/2);
	}
	
	public static void displayCenter(Container container, Container parent) {
		Point point = parent.getLocation();
		Dimension dim = parent.getSize();
		container.setLocation(dim.width/2-container.getSize().width/2 + point.x, 
				dim.height/2-container.getSize().height/2 + point.y);
	}

}
