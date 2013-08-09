package call;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Util {

	private static SimpleDateFormat datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static String OS = System.getProperty("os.name").toLowerCase();
	@SuppressWarnings("unused")
	private static LogProvider currentLogProvider;
	private static ByteArrayOutputStream outputbuffer;
	private static final Map<Contact, MessageOutput> messageOutputs = new HashMap<>();

	public static final PrintStream STDOUT = new PrintStream(System.out);
	public static final PrintStream STDERR = new PrintStream(System.err);

	public static void initOutputBuffer() {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		setOutAndErr(new PrintStream(buf));
		outputbuffer = buf;
	}

	public static void setOutAndErr(PrintStream stream) {
		System.out.flush();
		System.err.flush();
		if (outputbuffer != null) {
			String cached = outputbuffer.toString();
			stream.print(cached);
			outputbuffer = null;
		}
		MultiOutputStream multistream;
		try {
			PrintStream logfile = new PrintStream(new FileOutputStream(getLogFile(), true));
			multistream = new MultiOutputStream(stream, STDOUT, logfile);
		} catch (FileNotFoundException e) {
			multistream = new MultiOutputStream(stream, STDERR);
		}
		System.setOut(new PrintStream(multistream));
		System.setErr(new PrintStream(multistream));
	}

	private static File getLogFile() {
		return new File(DefaultConfigStorage.findConfigDirectory(), "console.log");
	}

	public static void setLogProvider(LogProvider log) {
		Util.currentLogProvider = log;
	}

	public static void setMessageOutput(Contact contact, MessageOutput msg) {
		messageOutputs.put(contact, msg);
	}

	public static void log(String id, String msg) {
		System.out.println("[" + id + "] " + msg);
	}

	public static void log(Id id, String msg) {
		System.out.println("[" + id.getId() + "] " + msg);
	}

	public static MessageWriter msg(Contact contact) {
		MessageOutput msgout = messageOutputs.containsKey(contact) ? messageOutputs.get(contact) : null;
		return new MessageWriter(msgout);
	}

	public static MessageWriter msg(PrintWriter pw) {
		return new MessageWriter(new HumanReadableMessageOutput(pw));
	}

	public static MessageWriter msg(MessageOutput msgout) {
		return new MessageWriter(msgout);
	}

	public static MessageWriter msg(List<MessageOutput> msgout) {
		return new MessageWriter(new MultiMessageOutput(msgout));
	}

	public static boolean sleep(long sleepTime) {
		try {
			Thread.sleep(sleepTime);
			return true;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}
	}

	public static String firstToUpperCase(String str) {
		if (str.length() > 0) {
			if (str.length() > 1) {
				return Character.toUpperCase(str.charAt(0)) + str.substring(1);
			} else {
				return str.toUpperCase();
			}
		} else {
			return str;
		}
	}

	public static String formatDateTime(long millis) {
		Date resultdate = new Date(millis);
		return datetime.format(resultdate);
	}

	public static boolean isWindows() {
		return (OS.indexOf("win") >= 0);
	}

	public static boolean isUnix() {
		return !isWindows();
	}

	public static String getUserName() {
		return System.getProperty("user.name").split(" ")[0];
	}

	public static <T> Set<T> asSet(T[] array) {
		return new HashSet<>(Arrays.asList(array));
	}

	public static String join(Collection<String> strings, String separator) {
		StringBuilder sb = new StringBuilder();
		String sep = "";
		for (String s : strings) {
			if (s.length() > 0) {
				sb.append(sep).append(s);
				sep = separator;
			}
		}
		return sb.toString();
	}

	public static String[] split(String separator, String str) {
		if (str.length() == 0) {
			return new String[] {};
		} else {
			String[] splitted = str.split(separator);
			return splitted;
		}
	}

	public static String formatMilliSecondsHumanReadable(long x) {
		@SuppressWarnings("unused")
		long millis = 0;
		long seconds = 0;
		long minutes = 0;
		long hours = 0;
		long days = 0;

		seconds = (int) (x / 1000);
		millis = x % 1000;

		minutes = (int) (seconds / 60);
		seconds = seconds % 60;

		hours = (int) (minutes / 60);
		hours = hours % 60;

		days = (int) (hours / 24);
		days = days % 24;

		String str = seconds + "s";
		/*
		 * if (millis >= 100) str += "." + millis; else if (millis >= 10) str +=
		 * ".0" + millis; else if (millis >= 1) str += ".00" + millis; str +=
		 * " seconds";
		 */

		if (minutes > 0)
			str = minutes + "m " + str;
		if (hours > 0)
			str = hours + "h " + str;
		if (days > 0)
			str = days + "d " + str;

		return str;
	}

	public static String formatBytesHumanReadable(float number) {
		String unit = "bytes";
		if (number > 1024) {
			number /= 1024;
			unit = "KiB";
		}
		if (number > 1024) {
			number /= 1024;
			unit = "MiB";
		}
		number = (float) ((long) (number * 1000)) / 1000;
		return number + " " + unit;
	}

	public static boolean joinThreads(List<Thread> threads) {
		boolean interrupted = false;
		for (Thread curThread : threads) {
			try {
				curThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
				interrupted = true;
				break;
			}
		}
		return interrupted;
	}

	public static void interruptThreads(List<Thread> threads) {
		for (Thread curThread : threads) {
			try {
				curThread.interrupt();
			} catch (Exception e) {}
		}
	}

	public static int indexOf(int[] array, int searchFor) {
		for (int i = 0; i < array.length; ++i) {
			if (array[i] == searchFor) {
				return i;
			}
		}
		return -1;
	}

	public static int[] reverse(int[] array) {
		int[] reversed = new int[array.length];
		for (int i = 0; i < array.length; ++i) {
			reversed[i] = array[array.length - 1 - i];
		}
		return reversed;
	}

}
