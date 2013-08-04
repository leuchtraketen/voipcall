package call;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class CallCapture implements Capture {

	private final File outputFile;

	public CallCapture(long time, Contact contact, String channel) {
		File directory = new File(System.getProperty("user.home"), "Calls/Calls with " + contact.getUser());
		directory.mkdirs();
		String filename = "Call with " + contact.getUser() + " at " + contact.getHost() + ", "
				+ Util.formatDateTime(time) + ", " + channel + ".pcm";
		if (Util.isWindows()) {
			filename = filename.replace(':', '-').replace(',', '-');
		}
		outputFile = new File(directory, filename);
	}

	@Override
	public OutputStream getCaptureOutputStream() {
		try {
			return new FileOutputStream(outputFile);
		} catch (FileNotFoundException e) {
			return null;
		}
	}

}
