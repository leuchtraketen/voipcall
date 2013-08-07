package call;

import java.io.IOException;
import java.io.OutputStream;

public class MultiOutputStream extends OutputStream implements Id {

	private final OutputStream[] outputStreams;

	public MultiOutputStream(OutputStream... outputStreams) {
		this.outputStreams = outputStreams;
	}

	@Override
	public void write(int b) throws IOException {
		for (OutputStream out : outputStreams)
			out.write(b);
	}

	@Override
	public void write(byte[] b) throws IOException {
		for (OutputStream out : outputStreams)
			out.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		for (OutputStream out : outputStreams)
			out.write(b, off, len);
	}

	@Override
	public void flush() throws IOException {
		for (OutputStream out : outputStreams)
			out.flush();
	}

	@Override
	public void close() throws IOException {
		for (OutputStream out : outputStreams)
			out.close();
	}

	@Override
	public int compareTo(Id other) {
		return getId().compareTo(other.getId());
	}

	@Override
	public String getId() {
		return toString();
	}

}
