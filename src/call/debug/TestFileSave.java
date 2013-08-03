package call.debug;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.sound.sampled.LineUnavailableException;

import call.CallRecorder;
import call.Id;

public class TestFileSave implements Id {
	public static void main(String[] args) {
		new TestFileSave();
	}

	public TestFileSave() {
		try {
			OutputStream os = new FileOutputStream(new File("test.pcm"));
			new Thread(new CallRecorder(this, os)).start();
		} catch (LineUnavailableException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getId() {
		return "TestFileSave";
	}
}
