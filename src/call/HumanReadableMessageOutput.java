package call;

import java.awt.Color;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HumanReadableMessageOutput implements MessageOutput {

	private final List<PrintWriter> printWriters = new ArrayList<>();

	public HumanReadableMessageOutput(PrintWriter pw) {
		this.printWriters.add(pw);
	}

	public HumanReadableMessageOutput(Collection<PrintWriter> printWriters) {
		this.printWriters.addAll(printWriters);
	}

	public HumanReadableMessageOutput(OutputStream out) {
		this.printWriters.add(new PrintWriter(out));
	}

	@Override
	public void append(String str, Color c) {
		for (PrintWriter pw : printWriters) {
			pw.print(str);
			pw.flush();
		}
	}

	@Override
	public void close() {
		for (PrintWriter pw : printWriters) {
			pw.close();
		}
	}

}
