package call;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class SocketUtil {

	public static enum RequestType {
		Status, Call, ServerCall, Chat
	};

	public static void writeHeaders(OutputStream out, RequestType request) {
		PrintWriter pw = new PrintWriter(out);
		pw.println("User: " + Util.getUserName());
		pw.println("UID: " + Config.UID_S);
		if (request.equals(RequestType.Status))
			pw.println("Request: Status");
		else if (request.equals(RequestType.Call))
			pw.println("Request: Call");
		else if (request.equals(RequestType.ServerCall))
			pw.println("Request: ServerRole");

		pw.println();
		pw.flush();
	}

	public static List<String> readHeaders(InputStream in) throws IOException {
		List<String> headers = new ArrayList<String>();
		String line;
		while ((line = readLine(in)) != null) {
			if (line.length() <= 2)
				break;
			headers.add(line);
		}
		return headers;
	}

	public static String getHeaderValue(List<String> headers, String key) {
		return getHeaderValue(headers, key, "");
	}

	public static String getHeaderValue(List<String> headers, String key, String defaultValue) {
		String value = defaultValue;
		for (String header : headers) {
			if (header.toLowerCase().startsWith(key.toLowerCase() + ":")) {
				value = header.split(":", 2)[1].trim();
				break;
			}
		}
		return value;
	}

	public static String readLine(InputStream in) throws IOException {
		final int _CR = 13;
		final int _LF = 10;
		int _ch = -1; // currently read char

		StringBuffer sb = new StringBuffer("");
		// try {
		_ch = in.read();
		while (_ch != _LF) {
			if (_ch != _CR)
				sb.append((char) _ch);
			_ch = in.read();
		}
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

		return (new String(sb));
	}
}
