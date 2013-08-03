package call.debug;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import call.AbstractId;
import call.CallPlayer;
import call.CallRecorder;
import call.ContactList;
import call.Util;

public class TestLoop extends AbstractId {

	public static void main(String[] args) {
		new TestLoop();
	}

	public TestLoop() {
		PipedInputStream pis = new PipedInputStream();
		PipedOutputStream pos = new PipedOutputStream();
		try {
			pos.connect(pis);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			new Thread(new CallRecorder(ContactList.me(), pos)).start();
			Util.sleep(2000);
			new Thread(new CallPlayer(ContactList.me(), new BufferedInputStream(pis))).start();
		} catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getId() {
		return "TestLoop";
	}
}
