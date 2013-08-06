package update;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

public class Main {
	private static PrintWriter logout;

	public static void main(String[] args) {
		File dotgit = new File(".git");
		File dotproject = new File(".project");
		if (dotgit.exists() || dotproject.exists()) {
			log("Running from a git repository.");
			log("Auto-update disabled.");
		} else {
			autoupdate("compiled.zip");
			autoupdate("dependencies.zip");
		}
	}

	private static void log(Exception e) {
		e.printStackTrace();
		if (logout != null)
			e.printStackTrace(logout);
	}

	private static void log(String str) {
		if (logout == null) {
			try {
				logout = new PrintWriter(new FileOutputStream("update.log"));
			} catch (FileNotFoundException e) {
				logout = null;
				e.printStackTrace();
				waitForEnter();
			}
		}
		System.out.println(str);
		if (logout != null) {
			logout.println(str);
		}
	}

	private static void waitForEnter() {
		Console c = System.console();
		if (c != null) {
			c.format("\nPress ENTER to proceed.\n");
			c.readLine();
		}
	}

	private static void autoupdate(String zipname) {
		setTrustManager();
		try {
			URL url = new URL("https://github.com/tobiasschulz/voipcall/raw/master/" + zipname);
			HttpURLConnection connection;
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();

			InputStream stream = connection.getInputStream();
			File zipfile = new File(zipname);
			if (zipfile.exists() && !zipfile.isFile() || !zipfile.canRead() || !zipfile.canWrite()) {
				zipfile.delete();
			}
			if (zipfile.exists()) {
				long oldsize = zipfile.length();
				log("Found previous " + zipname + ".");
				log("Old size: " + oldsize);
				long newsize = copy(stream, new FileOutputStream(zipname));
				log("New size: " + newsize);
				if (oldsize != newsize) {
					log("Different files: extracting...");
					extractFolder(zipfile, new File("."));
				} else {
					log("No changes...");
				}
			} else {
				log("No previous " + zipname + " was found.");
				long newsize = copy(stream, new FileOutputStream(zipname));
				log("Size: " + newsize);
				log("Extracting...");
				extractFolder(zipfile, new File("."));
			}

		} catch (Exception e) {
			log(e);
		}
	}

	private static void setTrustManager() {
		// Create a new trust manager that trust all certificates
		final X509TrustManager[] trustAllCerts = new X509TrustManager[] { new X509TrustManager() {
			@Override
			public void checkClientTrusted(final X509Certificate[] chain, final String authType) {}

			@Override
			public void checkServerTrusted(final X509Certificate[] chain, final String authType) {}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		} };

		// Activate the new trust manager
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {}

	}

	private static long copy(InputStream fis, OutputStream fos) {
		long size = 0;

		try {
			byte[] buffer = new byte[0xFFFF];
			for (int len; (len = fis.read(buffer)) != -1;) {
				fos.write(buffer, 0, len);
				size += len;
			}
		} catch (IOException e) {
			System.err.println(e);
		} finally {
			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					log(e);
				}
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					log(e);
				}
		}
		return size;
	}

	private static void extractFolder(File zipfile, File extractpath) throws ZipException, IOException {
		log("unzip: " + zipfile);
		int BUFFER = 2048;

		ZipFile zip = new ZipFile(zipfile);

		extractpath.mkdirs();
		Enumeration<? extends ZipEntry> zipFileEntries = zip.entries();

		// Process each entry
		while (zipFileEntries.hasMoreElements()) {
			// grab a zip file entry
			ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
			String currentEntry = entry.getName();
			File destFile = new File(extractpath, currentEntry);
			// destFile = new File(newPath, destFile.getName());
			destFile.getParentFile().mkdirs();

			if (!entry.isDirectory()) {
				BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry));
				int currentByte;
				// establish buffer for writing file
				byte data[] = new byte[BUFFER];

				log("extract: " + destFile);
				// write the current file to disk
				FileOutputStream fos = new FileOutputStream(destFile);
				BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);

				// read and write until last byte is encountered
				while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
					dest.write(data, 0, currentByte);
				}
				dest.flush();
				dest.close();
				is.close();

			} else {
				destFile.mkdirs();
			}

			if (currentEntry.endsWith(".zip")) {
				// found a zip file, try to open
				try {
					extractFolder(destFile, destFile.getParentFile());
				} catch (Exception e) {
					log(e);
				}
			}
		}

		zip.close();
	}
}
