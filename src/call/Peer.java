package call;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import call.CloseListener.CloseListeners;

public class Peer implements Runnable, Activatable, Id, CloseListener.CloseListening {

	private final Call call;
	private final Socket socket;
	private boolean active = false;
	private final String remotehost;
	private final int remoteport;
	private final String remoteuser;
	private final long time;
	private CallRecorder out;
	private CallPlayer in;

	public CloseListeners closeListeners = new CloseListeners();

	@Override
	public CloseListeners getCloseListeners() {
		return closeListeners;
	}

	public Peer(Call call, Socket socket, List<String> headers) {
		this.call = call;
		this.socket = socket;
		this.active = true;
		this.time = System.currentTimeMillis();
		this.remotehost = socket.getInetAddress().getCanonicalHostName();
		this.remoteport = socket.getPort();
		this.remoteuser = SocketUtil.getHeaderValue(headers, "User");
	}

	public boolean isActive() {
		return active;
	}

	@Override
	public void close() {
		active = false;
		call.peers.remove(this);
	}

	@Override
	public void run() {
		while (active) {
			try {
				if (out != null && !out.isActive()) {
					// unable to write to socket, close the call!
					Util.log(this, "ABCDEF 1");
					break;
				}
				if (in != null && !in.isActive()) {
					Util.log(this, "ABCDEF 2");
					break;
					//new Thread(in).start();
				}
				if (out == null) {
					OutputStream outstream = socket.getOutputStream();
					out = new CallRecorder(this, outstream);
					out.saveTo(new CallCapture(time, remotehost, "output"));
					new Thread(out).start();
				}
				if (in == null) {
					InputStream instream = socket.getInputStream();
					in = new CallPlayer(this, new BufferedInputStream(instream));
					in.saveTo(new CallCapture(time, remotehost, "input"));
					new Thread(in).start();
				}
			} catch (LineUnavailableException | UnsupportedAudioFileException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
			Util.sleep(500);
		}
		active = false;
		out.close();
		in.close();
		close();
		closeListeners.onClose();
	}

	public Call getCall() {
		return call;
	}

	public Socket getSocket() {
		return socket;
	}

	public String getRemoteHost() {
		return remotehost;
	}

	public int getRemotePort() {
		return remoteport;
	}

	public String getRemoteUser() {
		return remoteuser;
	}

	@Override
	public String toString() {
		return remoteuser + "@" + remotehost + ":" + remoteport;
	}

	@Override
	public String getId() {
		return remoteuser + "@" + remotehost + ":" + remoteport;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null) {
			return hashCode() == obj.hashCode();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}
}