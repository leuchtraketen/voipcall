package call.debug;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.sound.sampled.LineUnavailableException;

import call.AbstractId;
import call.CallRecorder;
import call.ContactList;
import call.UnknownDefaultValueException;

public class TestFileSave extends AbstractId {
	public static void main(String[] args) {
		new TestFileSave();
	}

	public TestFileSave() {
		try {
			OutputStream os = new FileOutputStream(new File("test.pcm"));
			new Thread(new CallRecorder(ContactList.me(), os)).start();
		} catch (LineUnavailableException | FileNotFoundException | UnknownDefaultValueException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getId() {
		return "TestFileSave";
	}
}
