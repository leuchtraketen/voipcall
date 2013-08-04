package call;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import call.SocketUtil.RequestType;
import call.gui.Resources;

public class ChatClient extends AbstractClient implements Runnable {

	private final PrintWriter out;

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
		}
	}

	private void recievedMessage(String msg) {
		Util.log(this, "recieved: " + msg);
		CallUi.openChat(contact);
		Util.msg(contact).println(contact, Resources.COLOR_CHAT_PEER, msg);
	}

	public void sendMessage(String msg) throws IOException {
		Util.log(this, "sending: " + msg);
		if (socket.isClosed()) {
			throw new IOException("Socket is closed!");
		}
		out.println(msg);
		out.flush();
		Util.msg(contact).println(ContactList.me(), Resources.COLOR_CHAT_ME, msg);
	}

	@Override
	public String getId() {
		return "ChatClient<" + contact.getId() + ">";
	}
}
