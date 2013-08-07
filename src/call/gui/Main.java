package call.gui;

import call.ContactScanner;
import call.PingScanner;
import call.Server;
import call.Util;

public class Main {
	public static void main(String[] args) {
		new Main();
	}

	public Main() {
		Util.initOutputBuffer();
		GuiUtil.setNativeLookAndFeel();

		Server server = new Server();
		Thread thr = new Thread(server);
		thr.start();

		new MainWindow().runGui();
		new Thread(new ContactScanner()).start();
		new Thread(new PingScanner()).start();
		try {
			thr.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
