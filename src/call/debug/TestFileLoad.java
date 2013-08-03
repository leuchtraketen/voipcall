package call.debug;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import call.CallPlayer;
import call.Id;

public class TestFileLoad implements Id {
	public static void main(String[] args) {
		new TestFileLoad();
	}

	public TestFileLoad() {
		try {
			InputStream is = new FileInputStream(new File("test.pcm"));
			new Thread(new CallPlayer(this, new BufferedInputStream(is))).start();
		} catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getId() {
		return "TestFileLoad";
	}
}
