package call;

import java.util.List;

import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;

public interface AudioDevice extends Id {

	Mixer.Info getMixerinfo();

	Mixer getMixer();

	javax.sound.sampled.Line.Info getLineinfo();

	DataLine getLine();

	List<PcmFormat> getFormats();

	public abstract static class Info<L extends DataLine> extends AbstractId {

		private final Mixer.Info mixerinfo;
		private final Mixer mixer;
		private final javax.sound.sampled.Line.Info lineinfo;
		private final L line;

		public Info(Mixer.Info mixerinfo, Mixer mixer, Line.Info lineinfo, L line) {
			this.mixerinfo = mixerinfo;
			this.mixer = mixer;
			this.lineinfo = lineinfo;
			this.line = line;
		}

		public Mixer.Info getMixerinfo() {
			return mixerinfo;
		}

		public Mixer getMixer() {
			return mixer;
		}

		public javax.sound.sampled.Line.Info getLineinfo() {
			return lineinfo;
		}

		public L getLine() {
			return line;
		}

		@Override
		public String getId() {
			return "AudioDevice.Info<" + mixerinfo + "," + mixer + "," + lineinfo + "," + line + ">";
		}
	}

}
