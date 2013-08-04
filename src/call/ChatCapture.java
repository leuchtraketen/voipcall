package call;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class ChatCapture implements Capture {

	private final File outputFile;

	public ChatCapture(Contact contact) {
		outputFile = new File(System.getProperty("user.home"), "Calls/Chat with " + contact.getUser()
				+ ".txt");
	}

	@Override
	public OutputStream getCaptureOutputStream() {
		try {
			return new FileOutputStream(outputFile, true);
		} catch (FileNotFoundException e) {
			return null;
		}
	}

}
