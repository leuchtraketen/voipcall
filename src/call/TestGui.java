package call;

import call.gui.MainGui;

public class TestGui {
	public static void main(String[] args) {
		new TestGui();
	}

	public TestGui() {
		Util.setNativeLookAndFeel();

		MainGui.getInstance().runGui();
		Contacts.start();

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
