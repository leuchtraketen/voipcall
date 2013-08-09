package call;

import java.io.OutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class CallRecorder extends AbstractCallConnection implements Runnable {

	private final TargetDataLine line;
	private final OutputStream out;
	private final List<OutputStream> captureStreams = new ArrayList<>();

	public CallRecorder(Contact contact, OutputStream out) throws LineUnavailableException, UnknownDefaultValueException {
		super(contact);
		this.out = out;

		AudioFormat audioFormat = Microphones.getSelectedFormat().getAudioFormat();
		Util.log(contact, "Recorder: start.");
		Util.log(contact, "Recorder: target audio format: " + audioFormat);

		// DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class,
		// audioFormat);
		// throws LineUnavailableException
		// line = (TargetDataLine) AudioSystem.getLine(dataLineInfo);

		Microphone microphone = Microphones.getCurrentMicrophone();
		Util.log(contact, "Recorder: microphone: " + microphone);
		line = (TargetDataLine) microphone.getLine();
		line.open(audioFormat, Config.BUFFER_SIZE_CALLS.getIntegerValue());

		line.start();
	}

	@Override
	public void run() {
		long sent = 0;
		long startTime = System.currentTimeMillis();
		long lastTime = startTime;

		byte buffer[] = new byte[Config.BUFFER_SIZE_CALLS.getIntegerValue()];

		CallFactory.openCall(contact);

		try {
			while (isCallOpen() && line.isOpen()) {
				int cnt = line.read(buffer, 0, buffer.length);
				if (cnt > 0) {
					// Save data in output stream object.
					out.write(buffer, 0, cnt);
					for (OutputStream out : captureStreams) {
						out.write(buffer, 0, cnt);
					}
					out.flush();

					sent += cnt;
					long now = System.currentTimeMillis();
					if (now > lastTime + 3000) {
						long diffTime = now - startTime;
						float speed = sent / diffTime * 1000;
						// Util.log(contact, "Speed (send):    " + speed +
						// " bytes/s (total: " + (sent / 1024) + " KB)");
						CallUi.updateCallStats(contact, -1, -1, speed, sent);
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
		CallUi.updateCallStats(contact, -1, -1, 0, 0);
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
