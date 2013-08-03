package call.gui;

import call.ContactScanner;
import call.Server;

public class Main {
	public static void main(String[] args) {
		new Main();
	}

	public Main() {
		GuiUtil.setNativeLookAndFeel();

		MainGui.getInstance().runGui();
		new Thread(new ContactScanner()).start();

		Server server = new Server();
		Thread thr = new Thread(server);
		thr.start();
		try {
			thr.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
