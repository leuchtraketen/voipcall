package call;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class CallThread extends AbstractConnection implements Runnable {

	private final Contact contact;
	private final Socket socket;
	private final long time;
	private CallRecorder out;
	private CallPlayer in;

	public CallThread(Contact contact, Socket socket, List<String> headers) {
		this.socket = socket;
		this.time = System.currentTimeMillis();
		this.contact = contact;
	}

	@Override
	public void close() {
		super.close();
	}

	@Override
	public void run() {
		try {
			setConnected(true);

			OutputStream outstream = socket.getOutputStream();
			out = new CallRecorder(this, outstream);
			out.saveTo(new CallCapture(time, contact.getHost(), "output"));
			out.addCloseListener(this);
			this.addCloseListener(out);
			new Thread(out).start();

			InputStream instream = socket.getInputStream();
			in = new CallPlayer(this, new BufferedInputStream(instream));
			in.saveTo(new CallCapture(time, contact.getHost(), "input"));
			in.addCloseListener(this);
			this.addCloseListener(in);
			new Thread(in).start();

		} catch (LineUnavailableException | UnsupportedAudioFileException e) {
			e.printStackTrace();
			close();
		} catch (IOException e) {
			e.printStackTrace();
			close();
		}
	}

	public Socket getSocket() {
		return socket;
	}

	public Contact getContact() {
		return contact;
	}

	@Override
	public String getId() {
		return "CallThread<" + contact.getId() + ">";
	}
}