package call;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import call.SocketUtil.RequestType;
import call.gui.Resources;

public class ChatClient extends AbstractClient implements Runnable {

	private final PrintWriter out;
	private final List<PrintWriter> capture = new ArrayList<PrintWriter>();

	public ChatClient(String host, int port) throws UnknownHostException, IOException {
		super(host, port, RequestType.Chat);
		out = new PrintWriter(socket.getOutputStream());
	}

	public ChatClient(Contact contact) throws UnknownHostException, IOException {
		super(contact, RequestType.Chat);
		out = new PrintWriter(socket.getOutputStream());
	}

	public ChatClient(Contact contact, Socket socket, List<String> headers) throws IOException {
		super(contact, socket, headers, RequestType.Chat);
		out = new PrintWriter(socket.getOutputStream());
	}

	@Override
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String line;
			while (!socket.isClosed() && (line = in.readLine()) != null) {
				if (line.length() <= 1)
					break;
				recievedMessage(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
			close();
		}
	}

	private void recievedMessage(String msg) {
		Util.log(this, "recieved: " + msg);
		CallUi.openChat(contact);
		Util.msg(contact).println(contact, Resources.COLOR_CHAT_PEER, msg);
		Util.msg(capture).println(contact, Resources.COLOR_CHAT_PEER, msg);
	}

	public void sendMessage(String msg) throws IOException {
		Util.log(this, "sending: " + msg);
		if (socket.isClosed()) {
			throw new IOException("Socket is closed!");
		}
		out.println(msg);
		out.flush();
		Util.msg(contact).println(ContactList.me(), Resources.COLOR_CHAT_ME, msg);
		Util.msg(capture).println(ContactList.me(), Resources.COLOR_CHAT_ME, msg);
	}

	public void saveTo(Capture capture) {
		try {
			OutputStream out = capture.getCaptureOutputStream();
			if (out != null) {
				this.capture.add(new PrintWriter(out));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		super.close();
		for (PrintWriter pw : capture) {
			try {
				pw.close();
			} catch (Exception e) {}
		}
	}

	@Override
	public String getId() {
		return "ChatClient<" + contact.getId() + ">";
	}
}
