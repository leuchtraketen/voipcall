package update;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class Main {
	public static void main(String[] args) {
		autoupdate();
	}

	private static void autoupdate() {
		try {
			URL url = new URL("http://github.com/tobiasschulz/voipcall/raw/master/compiled.zip");
			HttpURLConnection connection;
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();

			InputStream stream = connection.getInputStream();
			File zipfile = new File("compiled.zip");
			if (zipfile.exists() && !zipfile.isFile() || !zipfile.canRead() || !zipfile.canWrite()) {
				zipfile.delete();
			}
			if (zipfile.exists()) {
				long oldsize = zipfile.length();
				System.out.println("Found previous compiled.zip.");
				System.out.println("Old size: " + oldsize);
				long newsize = copy(stream, new FileOutputStream("compiled.zip"));
				System.out.println("New size: " + newsize);
				if (oldsize != newsize) {
					System.out.println("Different files: extracting...");
					extractFolder(zipfile, new File("."));
				} else {
					System.out.println("No changes...");
				}
			} else {
				System.out.println("No previous compiled.zip was found.");
				copy(stream, new FileOutputStream("compiled.zip"));
				System.out.println("Extracting...");
				extractFolder(zipfile, new File("."));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
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
					e.printStackTrace();
				}
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return size;
	}

	private static void extractFolder(File zipfile, File extractpath) throws ZipException, IOException {
		System.out.println("unzip: " + zipfile);
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

				System.out.println("extract: " + destFile);
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
					e.printStackTrace();
				}
			}
		}

		zip.close();
	}
}
