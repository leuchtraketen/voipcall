package call;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class CallPlayer extends AbstractConnection implements Runnable {

	private final SourceDataLine line;
	private final InputStream in;
	private final List<OutputStream> captureStreams = new ArrayList<OutputStream>();

	public CallPlayer(Contact contact, InputStream in) throws LineUnavailableException, UnsupportedAudioFileException,
			IOException {
		super(contact);
		this.in = in;

		int nChannels = Config.DEFAULT_CHANNELS;
		float fRate = Config.DEFAULT_RATE;

		AudioFormat.Encoding encoding = Config.DEFAULT_ENCODING;

		int nFrameSize = (Config.DEFAULT_SAMPLE_SIZE / 8) * nChannels;
		AudioFormat audioFormat = new AudioFormat(encoding, fRate, Config.DEFAULT_SAMPLE_SIZE, nChannels,
				nFrameSize, fRate, Config.DEFAULT_BIG_ENDIAN);
		Util.log(contact, "Player: start.");
		Util.log(contact, "Player: source audio format: " + audioFormat);

		// in = AudioSystem.getAudioInputStream(inputStream);
		DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
		line = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
		line.open(audioFormat);
		line.start();
	}

	@Override
	public void run() {
		long sent = 0;
		long startTime = System.currentTimeMillis();
		long lastTime = startTime;

		byte[] buffer = new byte[1024 * 16];
		try {
			int cnt;
			// Keep looping until the input read method returns -1 for empty
			// stream.
			while ((cnt = in.read(buffer, 0, buffer.length)) != -1 && isConnected()) {
				if (cnt > 0) {
					// Write data to the internal buffer of the data line where
					// it will be delivered to the speaker.
					line.write(buffer, 0, cnt);
					for (OutputStream out : captureStreams) {
						out.write(buffer, 0, cnt);
					}

					sent += cnt;
					// System.out.println("Buffer (receive): " + (100.0 /
					// buffer.length * cnt) + "% (" + cnt + " of " +
					// buffer.length + " bytes used)");
					long now = System.currentTimeMillis();
					if (now > lastTime + 3000) {
						long diffTime = now - startTime;
						double speed = sent / diffTime * 1000;
						Util.log(contact, "Speed (receive): " + speed + " bytes/s (total: " + (sent / 1024)
								+ " KB)");
						lastTime = now;
					}
				}
			}

			// Block and wait for internal buffer of the data line to empty.
			line.drain();
			line.close();
		} catch (SocketException e) {
			Util.log(contact, "Player: " + e.getLocalizedMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}

	@Override
	public void close() {
		if (isConnected()) {
			Util.log(contact, "Player: stop.");
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
		return "CallPlayer<" + contact.getId() + ">";
	}

}
