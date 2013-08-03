package call.debug;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import call.CallPlayer;
import call.CallRecorder;
import call.Id;
import call.Util;

public class TestLoop implements Id {
	
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
			new Thread(new CallRecorder(this, pos)).start();
			Util.sleep(2000);
			new Thread(new CallPlayer(this, new BufferedInputStream(pis))).start();
		} catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getId() {
		return "TestLoop";
	}
}
