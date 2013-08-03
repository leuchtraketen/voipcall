package call;

import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CallFactory {

	private static final Map<Contact, Call> calls = new HashMap<Contact, Call>();

	public static synchronized CallThread createCall(Contact contact, Socket socket, List<String> headers) {
		CallThread callthread = new CallThread(contact, socket, headers);
		Call call = new Call(contact, socket, headers);
		call.addConnection(callthread);
		CallUi.openChat(contact);
		for (Connection conn : CallUi.getUiListeners(contact)) {
			call.addConnection(conn);
		}
		calls.put(contact, call);
		return callthread;
	}

	public static Call getCall(Contact contact) {
		if (calls.containsKey(contact)) {
			Call call = calls.get(contact);
			System.out.println("getCall: " + contact.getId() + " = " + call);
			return call;
		} else {
			System.out.println("getCall: " + contact.getId() + " = null ");
			return null;
		}
	}

	public static synchronized void closeCall(Contact contact) {
		if (calls.containsKey(contact)) {
			calls.get(contact).close();
			calls.remove(contact);
		}
	}

	public static void openCall(Contact contact) {
		if (calls.containsKey(contact)) {
			calls.get(contact).open();
		}
	}

}
