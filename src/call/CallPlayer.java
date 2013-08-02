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

public class CallPlayer implements Runnable, Activatable {

	private final Id id;
	private final SourceDataLine line;
	private final InputStream in;
	private boolean stop = true;
	private final List<OutputStream> captureStreams = new ArrayList<OutputStream>();

	public CallPlayer(Id id, InputStream in) throws LineUnavailableException, UnsupportedAudioFileException,
			IOException {
		this.id = id;
		this.in = in;

		int nChannels = CallConfig.DEFAULT_CHANNELS;
		float fRate = CallConfig.DEFAULT_RATE;

		AudioFormat.Encoding encoding = CallConfig.DEFAULT_ENCODING;

		int nFrameSize = (CallConfig.DEFAULT_SAMPLE_SIZE / 8) * nChannels;
		AudioFormat audioFormat = new AudioFormat(encoding, fRate, CallConfig.DEFAULT_SAMPLE_SIZE, nChannels,
				nFrameSize, fRate, CallConfig.DEFAULT_BIG_ENDIAN);
		Util.log(id, "Player: start.");
		Util.log(id, "Player: source audio format: " + audioFormat);

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
		stop = false;
		try {
			int cnt;
			// Keep looping until the input read method returns -1 for empty
			// stream.
			while ((cnt = in.read(buffer, 0, buffer.length)) != -1 && !stop) {
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
						Util.log(id, "Speed (receive): " + speed + " bytes/s (total: " + (sent / 1024)
								+ " KB)");
						lastTime = now;
					}
				}
			}

			// Block and wait for internal buffer of the data line to empty.
			line.drain();
			line.close();
		} catch (SocketException e) {
			Util.log(id, "Player: " + e.getLocalizedMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}

	@Override
	public void close() {
		if (stop == false)
			Util.log(id, "Player: stop.");
		stop = true;
		if (line.isRunning())
			line.stop();
		if (line.isOpen())
			line.close();
	}

	@Override
	public boolean isActive() {
		return !stop;
	}

	public void saveTo(Capture capture) {
		OutputStream out = capture.getCaptureOutputStream();
		if (out != null) {
			captureStreams.add(out);
		}
	}

}
