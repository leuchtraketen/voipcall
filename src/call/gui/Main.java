package call.gui;

import java.util.Arrays;

import call.AudioDeviceScanner;
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

		AudioDeviceScanner audioScanner = new AudioDeviceScanner();
		audioScanner.setUi(new AudioDeviceScannerDialog());
		Thread audioScannerThread = new Thread(audioScanner);
		audioScannerThread.start();

		final MainWindow main = new MainWindow();

		Server server = new Server();
		Thread thr = new Thread(server);
		thr.start();

		new Thread(new ContactScanner()).start();
		new Thread(new PingScanner()).start();

		Util.joinThreads(Arrays.asList(new Thread[] { audioScannerThread }));

		main.runGui();

		try {
			thr.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
