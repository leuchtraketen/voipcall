package call.debug;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * This code reads a file to an audio stream, captures a sample to an audio
 * stream, plays an audio stream, and writes an audio stream to a file. These
 * are the basic actions that one can carry out with an audio stream, hence this
 * code can serve as a tutorial example for how to do these basic audio tasks.
 * 
 * This code was stolen, copied, and otherwise cobbled together from the demo
 * sound code in Sun's sound example, JavaSoundDemo. See:
 * http://java.sun.com/products/java-media/sound/ and
 * http://java.sun.com/products/java-media/sound/samples/JavaSoundDemo/
 * 
 * If a file name is given as the command line argument, then that file is
 * assumed to be a *.wav file and it is opened, read, and the sound is played.
 * If no command line argument is given. A 20 second sound sample is captured,
 * played, and writen out to Capture.wav as a *.wav file.
 * 
 * @author Terry E. Weymouth
 * 
 *         SVN Source information... $LastChangedRevision$ $LastChangedDate$
 *         $HeadURL$ $LastChangedBy$
 */
public class TestLines {

	final int bufSize = 16384;
	static final String OUT_FILE = "Capture.wav";
	AudioFormat selectedFormat;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String filename = null;
		if (args.length > 0)
			filename = args[0];
		new TestLines().go(filename);
		System.exit(0);
	}

	public void go(String filename) {

		File fileIn = null;

		// check for input wav file
		if (filename != null) {
			fileIn = new File(filename);
			if (!fileIn.exists()) {
				System.out.println("Supplied input file, " + filename + ", does not exist.");
				System.out.println("Proceeding to capture sound live...");
				fileIn = null;
			}
			if (!fileIn.canRead()) {
				System.out.println("Supplied input file, " + filename + ", can not be read.");
				System.out.println("Proceeding to capture sound live...");
				fileIn = null;
			}
		}

		// get audio -- will also set selected format
		selectedFormat = null;
		AudioInputStream captureInputStream;
		if (fileIn != null)
			captureInputStream = getAudioFromFile(fileIn);
		else
			captureInputStream = captureLiveAudio();

		if (captureInputStream == null) {
			System.out.println("Unable to obtain audio. Quitting.");
			return;
		}

		if (selectedFormat == null) {
			System.out.println("Selected format is null (!??). Quitting.");
			return;
		}

		// play audio stream
		playback(captureInputStream);

		// if it was captured write it out
		if (fileIn == null)
			writeFileOut(captureInputStream);
	}

	private AudioInputStream getAudioFromFile(File file) {

		System.out.println("Reading audio from file " + file.getAbsolutePath());

		AudioInputStream audioInputStream = null;
		try {
			audioInputStream = AudioSystem.getAudioInputStream(file);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		selectedFormat = audioInputStream.getFormat();
		return audioInputStream;
	}

	/**
	 * Capture 20 seconds of live audio to a ByteArrayOutputStream in any
	 * failure case print a message and return null
	 */
	private AudioInputStream captureLiveAudio() {

		for (Mixer.Info mixerinfo : AudioSystem.getMixerInfo()) {
			System.out.println("mixerinfo: " + mixerinfo);
			Mixer mixer = AudioSystem.getMixer(mixerinfo);
			System.out.println("mixer:     " + mixer);
			System.out.println("mixerinfo: " + mixer.getLineInfo());
			for (Line.Info lineinfo : mixer.getTargetLineInfo()) {
				try {
					Line line;
					line = mixer.getLine(lineinfo);
					if (line instanceof DataLine) {
						System.out.println("    lineinfo:   " + lineinfo);
						System.out.println("    line:       " + line);
						System.out.println("    lineinfo:   " + line.getLineInfo());
						if (!mixer.isLineSupported(lineinfo)) {
							System.out.println("    NOT SUPPORTED!");
						}
					}
				} catch (LineUnavailableException e) {
					e.printStackTrace();
				}
			}
		}

		TargetDataLine inLine = null;
		// ----------------------------------------------------------------------
		// initialize input line

		DataLine.Info info = new DataLine.Info(TargetDataLine.class, null);

		if (!AudioSystem.isLineSupported(info)) {
			System.out.println("Did not find sound input line.");
			System.out.println("Line matching " + info + " not supported.");
			return null;
		}

		selectedFormat = findBestFormat();
		if (selectedFormat == null) {
			System.out.println("Unable to find sutable format for capture.");
			return null;
		}
		System.out.println("Format = " + selectedFormat);

		try {
			inLine = (TargetDataLine) AudioSystem.getLine(info);
			inLine.open(selectedFormat, inLine.getBufferSize());
		} catch (LineUnavailableException ex) {
			System.out.println("Unable to open the input line: " + ex);
			ex.printStackTrace();
			return null;
		} catch (SecurityException ex) {
			System.out.println("Security " + ex.toString());
			ex.printStackTrace();
			return null;
		} catch (Exception ex) {
			System.out.println("Some other exception" + ex.toString());
			ex.printStackTrace();
			return null;
		}

		System.out.println("Found and opened sound input line.");

		// ----------------------------------------------------------------------
		// capture

		ByteArrayOutputStream captureArrayStream = new ByteArrayOutputStream();
		int frameSizeInBytes = selectedFormat.getFrameSize();
		int bufferLengthInFrames = inLine.getBufferSize() / 8;
		int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
		byte[] data = new byte[bufferLengthInBytes];
		int numBytesRead;

		inLine.start();

		System.out.println("Capturing 20 seconds of sound.");
		System.out.println("Start Talking....");
		Long start = System.currentTimeMillis();
		Long time = start;
		while (time < (start + 20000)) // for 20 seconds
		{
			if ((numBytesRead = inLine.read(data, 0, bufferLengthInBytes)) == -1) {
				break;
			}
			captureArrayStream.write(data, 0, numBytesRead);
			System.out.print("*");
			time = System.currentTimeMillis();
		}
		System.out.println();
		System.out.println(" ...done.");

		// we reached the end of the stream. stop and close the line.
		inLine.stop();
		inLine.close();
		inLine = null;

		// stop and close the capture stream
		try {
			captureArrayStream.flush();
			captureArrayStream.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		// load bytes into the audio input stream for playback

		byte audioBytes[] = captureArrayStream.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
		AudioInputStream audioInputStream = new AudioInputStream(bais, selectedFormat, audioBytes.length
				/ frameSizeInBytes);

		long milliseconds = (long) ((audioInputStream.getFrameLength() * 1000) / selectedFormat
				.getFrameRate());
		double duration = milliseconds / 1000.0;

		try {
			audioInputStream.reset();
		} catch (Exception ex) {
			System.out.println("Failed to reset input to start.");
			ex.printStackTrace();
			return null; // it can't be used in this case.
		}

		System.out.println("Captured " + duration + " seconds of audio.");
		return audioInputStream;
	}

	private void playback(AudioInputStream streamToPlay) {

		SourceDataLine outLine = null;

		// ----------------------------------------------------------------------
		// initialize output audio line

		AudioInputStream playbackInputStream = AudioSystem.getAudioInputStream(selectedFormat, streamToPlay);

		if (playbackInputStream == null) {
			System.out.println("Unable to convert stream of format " + streamToPlay + " to format "
					+ selectedFormat);
			return;
		}

		DataLine.Info info = new DataLine.Info(SourceDataLine.class, selectedFormat);
		if (!AudioSystem.isLineSupported(info)) {
			System.out.println("Did not find sound output line.");
			System.out.println("Line matching " + info + " not supported.");
			return;
		}

		try {
			outLine = (SourceDataLine) AudioSystem.getLine(info);
			outLine.open(selectedFormat, bufSize);
		} catch (LineUnavailableException ex) {
			System.out.println("Unable to open the line: " + ex);
			return;
		}

		System.out.println("Found and opened sound output line.");

		// ----------------------------------------------------------------------
		// play back the supplied or captured audio data

		int frameSizeInBytes = selectedFormat.getFrameSize();
		int bufferLengthInFrames = outLine.getBufferSize() / 8;
		int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
		byte[] data = new byte[bufferLengthInBytes];
		int numBytesRead = 0;

		System.out.println("Starting playback.");
		// start the source data line
		outLine.start();

		while (true) {
			try {
				if ((numBytesRead = playbackInputStream.read(data)) == -1) {
					break;
				}
				int numBytesRemaining = numBytesRead;
				while (numBytesRemaining > 0) {
					numBytesRemaining -= outLine.write(data, 0, numBytesRemaining);
				}
				System.out.print("*");
			} catch (Exception e) {
				System.out.println("Error during playback: " + e);
				break;
			}
		}
		// we reached the end of the stream. let the data play out, then
		// stop and close the line.
		outLine.drain();
		outLine.stop();
		outLine.close();
		outLine = null;
		System.out.println();
		System.out.println("Done with playbock.");

	}

	private void writeFileOut(AudioInputStream streamToWrite) {
		// ----------------------------------------------------------------------
		// Write results out to a file (specified by the static varible
		// OUT_FILE)

		System.out.println("Attempting to write sound recording to file = " + OUT_FILE);
		File file = new File(OUT_FILE);

		if (file.exists()) {
			System.out.println("File " + OUT_FILE + " already exists.");
			return;
		}

		// AudioFileFormat.Type fileType = AudioFileFormat.Type.AU;
		// AudioFileFormat.Type fileType = AudioFileFormat.Type.AIFF;
		AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

		try {
			streamToWrite.reset();
		} catch (IOException e) {
			System.out.println("Write to file: Failed to reset input to start.");
			e.printStackTrace();
		}
		try {
			if (AudioSystem.write(streamToWrite, fileType, file) == -1) {
				System.out.println("Failed to write to audio file " + OUT_FILE);
			}
		} catch (IOException e) {
			System.out.println("Failed to write to audio file " + OUT_FILE);
			e.printStackTrace();
		}

		System.out.println("Wrote sound to file " + OUT_FILE);

	}

	AudioFormat.Encoding[] encodings = { AudioFormat.Encoding.PCM_SIGNED, AudioFormat.Encoding.PCM_UNSIGNED,
			AudioFormat.Encoding.ULAW, AudioFormat.Encoding.ALAW };

	float[] rates = { 2 * 88200.0f, 88200.0f, 44100.0f, 22050.0f, 16000.0f, 11025.0f, 8000.0f };
	int[] sampleSizes = { 16, 8 };

	private AudioFormat findBestFormat() {
		AudioFormat best = null;
		boolean bigEndian = true;

		AudioFormat format;
		boolean works;
		for (int e = 0; e < encodings.length; e++)
			for (int r = 0; r < rates.length; r++)
				for (int s = 0; s < sampleSizes.length; s++)
					for (int channels = 1; channels < 3; channels++) {
						format = new AudioFormat(encodings[e], rates[r], sampleSizes[s], channels,
								(sampleSizes[s] / 8) * channels, rates[r], bigEndian);
						works = probFormat(format);
						if (works) {
							if (best == null)
								best = format;
							System.out.println("works: encoding=" + encodings[e] + ", rate=" + rates[r]
									+ ", simplesize=" + sampleSizes[s] + ", channels=" + channels
									+ ", endian=big");
						} else {
							System.out.println("does NOT work: encoding=" + encodings[e] + ", rate="
									+ rates[r] + ", simplesize=" + sampleSizes[s] + ", channels=" + channels
									+ ", endian=big");
						}

						format = new AudioFormat(encodings[e], rates[r], sampleSizes[s], channels,
								(sampleSizes[s] / 8) * channels, rates[r], !bigEndian);
						works = probFormat(format);
						if (works) {
							if (best == null)
								best = format;
							System.out.println("works: encoding=" + encodings[e] + ", rate=" + rates[r]
									+ ", simplesize=" + sampleSizes[s] + ", channels=" + channels
									+ ", endian=little");
						} else {
							System.out.println("does NOT work: encoding=" + encodings[e] + ", rate="
									+ rates[r] + ", simplesize=" + sampleSizes[s] + ", channels=" + channels
									+ ", endian=little");
						}
					}
		return best;
	}

	private boolean probFormat(AudioFormat format) {
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		if (!AudioSystem.isLineSupported(info)) {
			return false;
		}
		try {
			TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
			line.open(format, line.getBufferSize());
			line.close();
			return true;
		} catch (Throwable t) {
			// t.printStackTrace();
		}
		return false;
	}

}