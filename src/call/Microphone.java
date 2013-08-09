package call;

import java.util.List;

import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

public class Microphone extends AbstractId implements AudioDevice {

	private final Mixer.Info mixerinfo;
	private final Mixer mixer;
	private final Line.Info lineinfo;
	private final TargetDataLine line;
	private final List<PcmFormat> formats;

	public Microphone(Info info, List<PcmFormat> formats) {
		this.mixerinfo = info.getMixerinfo();
		this.mixer = info.getMixer();
		this.lineinfo = info.getLineinfo();
		this.line = info.getLine();
		this.formats = formats;
	}

	@Override
	public Mixer.Info getMixerinfo() {
		return mixerinfo;
	}

	@Override
	public Mixer getMixer() {
		return mixer;
	}

	@Override
	public Line.Info getLineinfo() {
		return lineinfo;
	}

	@Override
	public DataLine getLine() {
		return line;
	}

	@Override
	public List<PcmFormat> getFormats() {
		return formats;
	}

	@Override
	public String getId() {
		return "Microphone<" + mixerinfo.getName() + ">";
	}

	@Override
	public String toString() {
		return mixerinfo.getName() + " (" + mixerinfo.getVendor() + ")";
	}

	public static class Info extends AudioDevice.Info<TargetDataLine> {

		public Info(Mixer.Info mixerinfo, Mixer mixer, Line.Info lineinfo, TargetDataLine line) {
			super(mixerinfo, mixer, lineinfo, line);
		}

	}

}