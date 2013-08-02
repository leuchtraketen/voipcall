package call;

public class TestServer {
	public static void main(String[] args) {
		new TestServer();
	}

	public TestServer() {
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
