package call;

import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CallFactory {

	private static final Map<Contact, Call> calls = new HashMap<>();

	public static synchronized CallThread createCall(Contact contact, Socket socket, List<String> headers) {
		// initialize call map
		Call call = new Call(contact, socket, headers);
		synchronized (calls) {
			calls.put(contact, call);
		}

		// do call
		CallThread callthread = new CallThread(contact, socket, headers);
		CallUi.openChat(contact);
		CallUi.openCall(contact);
		return callthread;
	}

	public static synchronized Call getCall(Contact contact) {
		synchronized (calls) {
			if (calls.containsKey(contact)) {
				Call call = calls.get(contact);
				return call;
			} else {
				System.out.println("getCall: " + contact.getId() + " = null ");
				return null;
			}
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
		Call call = null;
		synchronized (calls) {
			if (calls.containsKey(contact)) {
				call = calls.get(contact);
			}
		}
		if (call != null) {
			call.close();
		}
		synchronized (calls) {
			if (calls.containsKey(contact)) {
				calls.remove(contact);
			}
		}
	}

	public static void openCall(Contact contact) {
		Call call = null;
		synchronized (calls) {
			if (calls.containsKey(contact)) {
				call = calls.get(contact);
			}
		}
		if (call != null)
			call.open();
	}

}
