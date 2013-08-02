package call;

import java.net.Socket;
import java.util.List;


public class Calls {

	public static Call createCall() {
		return new Call();
	}

	public static Peer registerPeer(Socket socket, List<String> headers) {
		Call call = createCall();
		Peer peer = new Peer(call, socket, headers);
		call.add(peer);
		return peer;
	}

}
