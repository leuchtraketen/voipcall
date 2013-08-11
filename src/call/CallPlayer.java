package call;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class CallPlayer extends AbstractCallConnection implements Runnable {

	private final SourceDataLine line;
	private final InputStream in;
	private final List<OutputStream> captureStreams = new ArrayList<>();
	private int buffersize;
	private long bytesSent = 0;

	public CallPlayer(Contact contact, InputStream in, PcmFormat format, int buffersize)
			throws LineUnavailableException, UnsupportedAudioFileException, IOException,
			UnknownDefaultValueException {
		super(contact);
		this.in = in;
		this.buffersize = buffersize;

		AudioFormat audioFormat = format.getAudioFormat();
		Util.log(contact, "Player: start.");
		Util.log(contact, "Player: source audio format: " + audioFormat);

		Speaker speaker = Speakers.getCurrentSpeaker();
		Util.log(contact, "Player: speaker: " + speaker);

		line = (SourceDataLine) speaker.getLine();
		line.open(audioFormat, buffersize);
		line.start();
	}

	@Override
	public void run() {

		CallFactory.openCall(contact);

		byte[] buffer = new byte[buffersize];
		bytesSent = 0;
		final long startTime = System.currentTimeMillis();

		Thread statsThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (isCallOpen()) {
					long now = System.currentTimeMillis();
					long diffTime = now - startTime;
					if (diffTime > 0) {
						float speed = bytesSent / diffTime * 1000;
						CallUi.updateCallStats(contact, speed, bytesSent, -1, -1);
						Util.sleep(5_000);
					}
				}
			}
		});
		statsThread.start();
		statsThread.setPriority(Thread.MIN_PRIORITY);

		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

		try {
			int cnt;
			// Keep looping until the input read method returns -1 for empty
			// stream.
			while ((cnt = in.read(buffer, 0, buffer.length)) != -1 && isCallOpen()) {
				if (cnt > 0) {
					// Write data to the internal buffer of the data line where
					// it will be delivered to the speaker.
					line.write(buffer, 0, cnt);
					for (OutputStream out : captureStreams) {
						out.write(buffer, 0, cnt);
					}

					bytesSent += cnt;
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

		CallFactory.closeCall(contact);
	}

	@Override
	public void onCallClose() {
		Util.log(contact, "Player: stop.");
		if (line.isRunning())
			line.stop();
		if (line.isOpen())
			line.close();
		CallUi.updateCallStats(contact, 0, 0, -1, -1);
		super.onCallClose();
	}

	public void saveTo(Capture capture) {
		OutputStream out = capture.getCaptureOutputStream();
		if (out != null) {
			captureStreams.add(new BufferedOutputStream(out, 256 * 1024));
		}
	}

	@Override
	public String getId() {
		return "CallPlayer<" + contact.getId() + ">";
	}

}
