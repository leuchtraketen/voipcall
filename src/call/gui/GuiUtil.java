package call.gui;

import javax.swing.UIManager;

public class GuiUtil {

	public static void setNativeLookAndFeel() {
		UIManager.put("Slider.paintValue", false);
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");

			return;
		} catch (Exception e) {}
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {}
	}
}
