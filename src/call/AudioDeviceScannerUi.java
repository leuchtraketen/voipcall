package call;

import javax.sound.sampled.Mixer.Info;

public interface AudioDeviceScannerUi extends Id {

	void open(int maxSteps);

	void close();

	void setCurrentLine(Info mixerinfo, javax.sound.sampled.Line.Info lineinfo);

	void nextProgressStep();

}
