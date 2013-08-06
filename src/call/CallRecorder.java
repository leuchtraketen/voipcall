package call;

import java.io.OutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class CallRecorder extends AbstractCallConnection implements Runnable {

	private final TargetDataLine line;
	private final OutputStream out;
	private final List<OutputStream> captureStreams = new ArrayList<OutputStream>();

	public CallRecorder(Contact contact, OutputStream out) throws LineUnavailableException {
		super(contact);
		this.out = out;

		int nChannels = Config.PCM_DEFAULT_CHANNELS;
		float fRate = Config.PCM_DEFAULT_RATE;

		AudioFormat.Encoding encoding = Config.DEFAULT_ENCODING;

		int nFrameSize = (Config.PCM_DEFAULT_SAMPLE_SIZE / 8) * nChannels;
		AudioFormat audioFormat = new AudioFormat(encoding, fRate, Config.PCM_DEFAULT_SAMPLE_SIZE, nChannels,
				nFrameSize, fRate, Config.PCM_DEFAULT_BIG_ENDIAN);
		Util.log(contact, "Recorder: start.");
		Util.log(contact, "Recorder: target audio format: " + audioFormat);

		DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
		Util.log(contact, "Recorder: line: " + dataLineInfo);

		// throws LineUnavailableException
		line = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
		line.open(audioFormat);

		line.start();
	}

	@Override
	public void run() {
		long sent = 0;
		long startTime = System.currentTimeMillis();
		long lastTime = startTime;

		byte buffer[] = new byte[1024 * 16];

		CallFactory.openCall(contact);

		try {
			while (isCallOpen() && line.isOpen()) {
				// System.out.println("connected = " + isConnected() +
				// ", line.isOpen() = " + line.isOpen());
				// Read data from the internal buffer of the data line.
				int cnt = line.read(buffer, 0, buffer.length);
				// System.out.println("Buffer (send):    " + (100.0 /
				// buffer.length * cnt) + "% (" + cnt + " of " + buffer.length +
				// " bytes used)");
				if (cnt > 0) {
					// Save data in output stream object.
					out.write(buffer, 0, cnt);
					for (OutputStream out : captureStreams) {
						out.write(buffer, 0, cnt);
					}

					sent += cnt;
					long now = System.currentTimeMillis();
					if (now > lastTime + 3000) {
						long diffTime = now - startTime;
						double speed = sent / diffTime * 1000;
						Util.log(contact, "Speed (send):    " + speed + " bytes/s (total: " + (sent / 1024)
								+ " KB)");
						lastTime = now;
					}
				}
			}
			// System.out.println("connected = " + isConnected() +
			// ", line.isOpen() = " + line.isOpen() + " (2)");
			out.flush();
		} catch (SocketException e) {
			Util.log(contact, "Recorder: " + e.getLocalizedMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}

		CallFactory.closeCall(contact);
	}

	@Override
	public void onCallClose() {
		Util.log(contact, "Recorder: stop.");

		if (line.isRunning())
			line.stop();
		if (line.isOpen())
			line.close();
		super.onCallClose();
	}

	public void saveTo(Capture capture) {
		OutputStream out = capture.getCaptureOutputStream();
		if (out != null) {
			captureStreams.add(out);
		}
	}

	@Override
	public String getId() {
		return "CallRecorder<" + contact.getId() + ">";
	}
}
