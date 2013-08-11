package call;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.List;

import call.SocketUtil.RequestType;

public class CallClient extends AbstractClient {

	public CallClient(String host, int port) throws UnknownHostException, IOException {
		super(host, port, RequestType.Call);
	}

	public CallClient(Contact contact) throws UnknownHostException, IOException {
		super(contact, RequestType.Call);
	}

	public CallClient(Contact contact, Socket socket, List<String> headers) {
		super(contact, socket, headers, RequestType.Call);
	}

	@Override
	protected Socket openSocket(String host, int port) throws IOException {
		try {
			return super.openSocket(host, port + Config.DEFAULT_PORT_OFFSET_CALL);
		} catch (IOException e) {
			return super.openSocket(host, port);
		}
	}

	public Thread startCall() {
		CallThread call = CallFactory.createCall(contact, socket, headers);
		Thread thr = new Thread(call);
		thr.start();
		return thr;
	}

	@Override
	public String getId() {
		return "CallClient<" + contact.getId() + ">";
	}
}
