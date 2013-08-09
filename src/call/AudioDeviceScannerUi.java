package call;

import javax.sound.sampled.Mixer.Info;

public interface AudioDeviceScannerUi extends Id {

	void open();

	void close();

	void setCurrentLine(Info mixerinfo, javax.sound.sampled.Line.Info lineinfo);

	void nextProgressStep();

	void setMaxSteps(int maxSteps);

}
