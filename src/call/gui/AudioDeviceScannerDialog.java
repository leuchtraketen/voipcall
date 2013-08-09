package call.gui;

import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.swing.SwingUtilities;

import call.AbstractId;
import call.AudioDeviceScannerUi;

public class AudioDeviceScannerDialog extends AbstractId implements AudioDeviceScannerUi {

	private ProgressMonitor progressMonitor;
	private int progress = 0;

	@Override
	public void open() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				progress = 0;
				progressMonitor = new ProgressMonitor(null, "Searching for audio devices...", "", progress,
						100);
				progressMonitor.setMillisToDecideToPopup(0);
				progressMonitor.setMillisToPopup(0);
			}
		});
	}

	@Override
	public void setMaxSteps(final int maxSteps) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				progressMonitor.setMaximum(maxSteps + 1);
			}
		});
	}

	@Override
	public void setCurrentLine(final Mixer.Info mixerinfo, final Line.Info lineinfo) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				progressMonitor.setNote(mixerinfo.getName());
			}
		});
	}

	@Override
	public void nextProgressStep() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				progressMonitor.setProgress(++progress);
			}
		});
	}

	@Override
	public void close() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				progressMonitor.close();
			}
		});
	}

	@Override
	public String getId() {
		return "AudioDeviceScannerDialog";
	}

}
