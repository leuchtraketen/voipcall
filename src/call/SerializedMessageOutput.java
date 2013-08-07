package call;

import java.awt.Color;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;

public class SerializedMessageOutput implements MessageOutput {

	private final List<PrintWriter> printWriters = new ArrayList<>();

	public SerializedMessageOutput(PrintWriter pw) {
		this.printWriters.add(pw);
	}

	public SerializedMessageOutput(Collection<PrintWriter> printWriters) {
		this.printWriters.addAll(printWriters);
	}

	public SerializedMessageOutput(OutputStream out) {
		this.printWriters.add(new PrintWriter(out));
	}

	@Override
	public void append(String str, Color c) {
		for (PrintWriter pw : printWriters) {
			pw.println(System.currentTimeMillis() + "," + Integer.toString(c.getRGB()) + ","
					+ str.replaceAll("\r", "").replaceAll("\n", Matcher.quoteReplacement("\\n")));
			pw.flush();
		}
	}

	@Override
	public void close() {
		for (PrintWriter pw : printWriters) {
			pw.close();
		}
	}

	public static void deserialize(List<String> lines, MessageOutput messageoutput) {
		for (String line : lines) {
			final String[] parts = line.split("[,]", 3);
			if (parts.length == 3) {
				@SuppressWarnings("unused")
				final String timestr = parts[0];
				final String colorstr = parts[1];
				final String textstr = parts[2].replaceAll("\\\\n", "\n");

				try {
					Color c = new Color(Integer.parseInt(colorstr));
					messageoutput.append(textstr, c);

				} catch (NumberFormatException e) {}
			}
		}
	}
}
