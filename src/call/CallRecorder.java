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

public class CallRecorder extends AbstractConnection implements Runnable {

	private final Id id;
	private final TargetDataLine line;
	private final OutputStream out;
	private final List<OutputStream> captureStreams = new ArrayList<OutputStream>();

	public CallRecorder(Id id, OutputStream out) throws LineUnavailableException {
		this.id = id;
		this.out = out;

		int nChannels = Config.DEFAULT_CHANNELS;
		float fRate = Config.DEFAULT_RATE;

		AudioFormat.Encoding encoding = Config.DEFAULT_ENCODING;

		int nFrameSize = (Config.DEFAULT_SAMPLE_SIZE / 8) * nChannels;
		AudioFormat audioFormat = new AudioFormat(encoding, fRate, Config.DEFAULT_SAMPLE_SIZE, nChannels,
				nFrameSize, fRate, Config.DEFAULT_BIG_ENDIAN);
		Util.log(id, "Recorder: start.");
		Util.log(id, "Recorder: target audio format: " + audioFormat);

		DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
		Util.log(id, "Recorder: line: " + dataLineInfo);

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

		setConnected(true);
		try {
			while (isConnected() && line.isOpen()) {
				//System.out.println("connected = " + isConnected() + ", line.isOpen() = " + line.isOpen());
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
						Util.log(id, "Speed (send):    " + speed + " bytes/s (total: " + (sent / 1024)
								+ " KB)");
						lastTime = now;
					}
				}
			}
			//System.out.println("connected = " + isConnected() + ", line.isOpen() = " + line.isOpen() + " (2)");
			out.flush();
		} catch (SocketException e) {
			Util.log(id, "Recorder: " + e.getLocalizedMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}

	@Override
	public void close() {
		if (isConnected()) {
			Util.log(id, "Recorder: stop.");
		}
		if (line.isRunning())
			line.stop();
		if (line.isOpen())
			line.close();
		super.close();
	}

	public void saveTo(Capture capture) {
		OutputStream out = capture.getCaptureOutputStream();
		if (out != null) {
			captureStreams.add(out);
		}
	}

	@Override
	public String getId() {
		return "CallRecorder<" + id.getId() + ">";
	}
}
