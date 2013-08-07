package call;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ChatCapture extends AbstractId implements Capture {

	private final File outputFileHumanReadable;
	private final File outputFileSerialized;

	public ChatCapture(Contact contact) {
		File directory = new File(System.getProperty("user.home"), "Calls");
		directory.mkdirs();

		String id = contact.getUser() + " at " + contact.getHost();
		if (contact.isLoop()) {
			id += " (loop)";
		}

		outputFileHumanReadable = new File(directory, "Chat with " + id + ".txt");
		outputFileSerialized = new File(directory, "Chat with " + id + ".serialized");
	}

	@Override
	public OutputStream getCaptureOutputStream() {
		try {
			return new FileOutputStream(outputFileHumanReadable, true);
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	public MessageOutput getMessageOutput() {
		MultiMessageOutput msgouts = new MultiMessageOutput();
		try {
			msgouts.add(new HumanReadableMessageOutput(new FileOutputStream(outputFileHumanReadable, true)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			msgouts.add(new SerializedMessageOutput(new FileOutputStream(outputFileSerialized, true)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return msgouts;
	}

	public void deserialize(MessageOutput messageoutput) {
		List<String> lines = new ArrayList<>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(outputFileSerialized));
			String line;
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		SerializedMessageOutput.deserialize(lines, messageoutput);
	}

	@Override
	public String getId() {
		return "ChatCapture";
	}
}
