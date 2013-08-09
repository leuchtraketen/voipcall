package call;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class SocketUtil {

	public static enum RequestType {
		Status, Call, ServerCall, Chat, Ping
	};

	public static void writeHeaders(OutputStream out, RequestType request) {
		PrintWriter pw = new PrintWriter(out);
		pw.println("User: " + Util.getUserName());
		pw.println("UID: " + Config.UID_S);
		pw.println("Uptime: " + Config.CURRENT_UPTIME);
		if (request.equals(RequestType.Status))
			pw.println("Request: Status");
		else if (request.equals(RequestType.Call))
			pw.println("Request: Call");
		else if (request.equals(RequestType.Chat))
			pw.println("Request: Chat");
		else if (request.equals(RequestType.Ping))
			pw.println("Request: Ping");
		else if (request.equals(RequestType.ServerCall))
			pw.println("Request: ServerRole");

		{
			final PcmFormat format = Microphones.getSelectedFormat();
			final String serialized = new PcmFormat.Serializer().serialize(format);
			pw.println("Microphone-Format: " + serialized);
		}

		if (request.equals(RequestType.Status)) {
			try {
				final List<PcmFormat> formats = Microphones.getCurrentMicrophone().getFormats();
				final String serialized = new PcmFormat.Serializer().serializeAll(formats);
				pw.println("Microphone-Formats: " + serialized);
			} catch (UnknownDefaultValueException e) {}
			try {
				final List<PcmFormat> formats = Speakers.getCurrentSpeaker().getFormats();
				final String serialized = new PcmFormat.Serializer().serializeAll(formats);
				pw.println("Speaker-Formats: " + serialized);
			} catch (UnknownDefaultValueException e) {}
		}

		pw.println();
		pw.flush();
	}

	public static PcmFormat extractFormat(List<String> headers) {
		String serialized = getHeaderValue(headers, "Microphone-Format", "");
		if (serialized != null && serialized.length() > 0) {
			try {
				return new PcmFormat.Serializer().deserialize(serialized);
			} catch (UnknownDefaultValueException e) {}
		}
		return Config.PCM_DEFAULT_FORMAT;
	}

	public static void writeLine(OutputStream out, String line) {
		PrintWriter pw = new PrintWriter(out);
		pw.println(line);
		pw.flush();
	}

	public static List<String> readHeaders(InputStream in) throws IOException {
		List<String> headers = new ArrayList<>();
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
			System.out.println(header);
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
		while (_ch != _LF && _ch != -1) {
			if (_ch != _CR) {
				sb.append((char) _ch);
			}
			_ch = in.read();
		}
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

		return (new String(sb));
	}
}
