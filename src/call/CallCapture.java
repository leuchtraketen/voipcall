package call;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class CallCapture implements Capture {

	private final File outputFile;

	public CallCapture(long time, String user, String channel) {
		File directory = new File(System.getProperty("user.home"), "Calls/Calls with " + user);
		directory.mkdirs();
		String filename = "Call with " + user + ", " + Util.formatDateTime(time) + ", " + channel + ".pcm";
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
