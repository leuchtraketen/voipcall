package call;

import java.awt.Color;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PrintWriterMessageOutput implements MessageOutput {

	private final List<PrintWriter> printWriters = new ArrayList<PrintWriter>();

	public PrintWriterMessageOutput(PrintWriter pw) {
		this.printWriters.add(pw);
	}

	public PrintWriterMessageOutput(Collection<PrintWriter> printWriters) {
		this.printWriters.addAll(printWriters);
	}

	@Override
	public void append(String str, Color c) {
		for (PrintWriter pw : printWriters) {
			pw.print(str);
		}
	}

}
