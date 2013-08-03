package call;

import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CallFactory {

	private static final Map<Contact, Call> calls = new HashMap<Contact, Call>();

	public static synchronized CallThread createCall(Contact contact, Socket socket, List<String> headers) {
		// initialize call map
		Call call = new Call(contact, socket, headers);
		calls.put(contact, call);

		// do call
		CallThread callthread = new CallThread(contact, socket, headers);
		CallUi.openChat(contact);
		CallUi.openCall(contact);
		return callthread;
	}

	public static synchronized Call getCall(Contact contact) {
		if (calls.containsKey(contact)) {
			Call call = calls.get(contact);
			return call;
		} else {
			System.out.println("getCall: " + contact.getId() + " = null ");
			return null;
		}
	}

	public static synchronized boolean existsCall(Contact contact) {
		Call call = getCall(contact);
		if (call != null) {
			return call.getState().equals(ConnectionState.OPEN)
					|| call.getState().equals(ConnectionState.CONNECTING);
		} else {
			return false;
		}
	}

	public static synchronized ConnectionState getCallState(Contact contact) {
		Call call = getCall(contact);
		if (call != null) {
			return call.getState();
		} else {
			return ConnectionState.CLOSED;
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
