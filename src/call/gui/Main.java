package call.gui;

import call.ContactScanThread;
import call.Server;
import call.Util;

public class Main {
	public static void main(String[] args) {
		new Main();
	}

	public Main() {
		Util.setNativeLookAndFeel();

		MainGui.getInstance().runGui();
		ContactScanThread.start();

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
