package call;

import java.io.IOException;
import java.net.UnknownHostException;

import call.SocketUtil.RequestType;

public class StatusClient extends AbstractClient {

	public StatusClient(String host, int port) throws UnknownHostException, IOException {
		super(host, port, RequestType.Status);
	}

	public StatusClient(Contact contact) throws UnknownHostException, IOException {
		super(contact, RequestType.Status);
	}

	@Override
	public String getId() {
		return "StatusClient<" + contact.getId() + ">";
	}
}
