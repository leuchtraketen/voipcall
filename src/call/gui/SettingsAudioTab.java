package call.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import call.AbstractId;
import call.AudioDevice;
import call.Config;
import call.Config.Option;
import call.ConfigListener;
import call.Id;
import call.Microphone;
import call.Microphones;
import call.PcmFormat;
import call.Speaker;
import call.Speakers;
import call.Util;

public class SettingsAudioTab extends AbstractId implements ConfigListener, ActionListener, ChangeListener {

	private final int[] pcmRates = Util.reverse(Config.PCM_RATES);

	@SuppressWarnings("unused")
	private final MainWindow main;
	private final JScrollPane scrollpane;

	private final JComboBox<AudioDevice> comboboxMicrophones;
	private final JComboBox<AudioDevice> comboboxSpeakers;
	private final JSlider sliderCallBuffer;
	private final JRadioButton radioSampleSize8;
	private final JRadioButton radioSampleSize16;
	private final JSlider sliderPcmRate;
	private final JCheckBox checkboxStereo;
	private final JLabel textSelectedFormat;

	private PcmFormat format;

	public SettingsAudioTab(MainWindow main) {
		this.main = main;

		// area
		JPanel settingspanel = new JPanel(new GridBagLayout());

		// labels
		JLabel labelMicrophones = new JLabel(Resources.LABEL_SETTINGS_AUDIO_DEFAULT_MICROPHONE);
		JLabel labelSpeakers = new JLabel(Resources.LABEL_SETTINGS_AUDIO_DEFAULT_SPEAKER);
		JLabel labelPcmRate = new JLabel(Resources.LABEL_SETTINGS_AUDIO_SAMPLING_RATE);
		JLabel labelCallBuffer = new JLabel(Resources.LABEL_SETTINGS_AUDIO_CALL_BUFFER);
		JLabel labelSampleSize = new JLabel(Resources.LABEL_SETTINGS_AUDIO_SAMPLE_SIZE);
		JLabel labelSelectedFormat = new JLabel(Resources.LABEL_SETTINGS_AUDIO_SELECTED_ENCODING);

		// elements
		comboboxMicrophones = createComboboxMicrophones();
		comboboxSpeakers = createComboboxSpeakers();
		sliderPcmRate = createSliderPcmRate();
		sliderCallBuffer = createSliderCallBuffer();
		JRadioButton[] radioSampleSize = createRadioButtonsSampleSize();
		radioSampleSize8 = radioSampleSize[0];
		radioSampleSize16 = radioSampleSize[1];
		checkboxStereo = new JCheckBox("Stereo", false);
		textSelectedFormat = new JLabel("");

		final int NONE = GridBagConstraints.NONE;
		final int HORIZONTAL = GridBagConstraints.HORIZONTAL;
		@SuppressWarnings("unused")
		final int VERTICAL = GridBagConstraints.VERTICAL;
		final int BOTH = GridBagConstraints.BOTH;

		add(settingspanel, 0, 0, 0, 0, 0.0, 0.0, NONE, labelMicrophones);
		add(settingspanel, 1, 0, 4, 0, 0.0, 0.0, HORIZONTAL, comboboxMicrophones);
		add(settingspanel, 0, 1, 0, 0, 0.0, 0.0, NONE, labelSpeakers);
		add(settingspanel, 1, 1, 4, 0, 0.0, 0.0, HORIZONTAL, comboboxSpeakers);
		add(settingspanel, 0, 2, 0, 0, 0.0, 0.0, NONE, labelCallBuffer);
		add(settingspanel, 1, 2, 4, 0, 0.0, 0.0, HORIZONTAL, sliderCallBuffer);
		add(settingspanel, 0, 3, 0, 0, 0.0, 0.0, NONE, labelSampleSize);
		add(settingspanel, 1, 3, 0, 0, 0.0, 0.0, HORIZONTAL, radioSampleSize8);
		add(settingspanel, 2, 3, 0, 0, 0.0, 0.0, HORIZONTAL, radioSampleSize16);
		add(settingspanel, 3, 3, 0, 0, 0.0, 0.0, HORIZONTAL, checkboxStereo);
		add(settingspanel, 0, 4, 0, 0, 0.0, 0.0, NONE, labelPcmRate);
		add(settingspanel, 1, 4, 4, 0, 0.0, 0.0, HORIZONTAL, sliderPcmRate);
		add(settingspanel, 0, 5, 0, 0, 0.0, 0.0, NONE, labelSelectedFormat);
		add(settingspanel, 1, 5, 4, 0, 0.0, 0.0, NONE, textSelectedFormat);

		add(settingspanel, 0, 6, 0, 0, 0.0, 1.0, BOTH, empty());
		add(settingspanel, 1, 6, 0, 0, 0.0, 1.0, BOTH, empty());
		add(settingspanel, 2, 6, 0, 0, 0.0, 1.0, BOTH, empty());
		add(settingspanel, 3, 6, 0, 0, 0.0, 1.0, BOTH, empty());
		add(settingspanel, 4, 6, 0, 0, 0.0, 1.0, BOTH, empty());
		add(settingspanel, 5, 6, 0, 0, 0.8, 1.0, BOTH, empty());

		// JPanel mainpanel = new JPanel(new BorderLayout());
		// mainpanel.add(settingspanel, BorderLayout.CENTER);
		// mainpanel.add(new JPanel(), BorderLayout.CENTER);
		settingspanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		scrollpane = new JScrollPane(settingspanel);

		// config listener
		Config.addConfigListener(this);
		Config.notifyConfigListener(this);
		updateSelectedFormat();
	}

	private GridBagConstraints add(JPanel panel, int x, int y, int w, int h, double weightx, double weighty,
			int fill, JComponent comp) {
		GridBagConstraints c = new GridBagConstraints();
		c.fill = fill;
		c.gridx = x;
		c.gridy = y;
		c.gridwidth = 1 + w;
		c.gridheight = 1 + h;
		c.anchor = GridBagConstraints.LINE_START;
		c.weightx = weightx != 0 ? weightx : 0.0;
		c.weighty = weighty != 0 ? weighty : 0.0;
		c.ipadx = 5;
		panel.add(comp, c);
		return c;
	}

	private JComponent empty() {
		return new JLabel();
	}

	private JRadioButton[] createRadioButtonsSampleSize() {
		JRadioButton radio1 = new JRadioButton("8 bit");
		radio1.setActionCommand("8");
		radio1.addActionListener(this);
		radio1.setSelected(true);

		JRadioButton radio2 = new JRadioButton("16 bit");
		radio2.setActionCommand("16");
		radio2.addActionListener(this);

		ButtonGroup group = new ButtonGroup();
		group.add(radio1);
		group.add(radio2);

		return new JRadioButton[] { radio1, radio2 };
	}

	private JSlider createSliderCallBuffer() {
		JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 256, 16);
		slider.addChangeListener(this);
		slider.setMajorTickSpacing(32);
		slider.setMinorTickSpacing(8);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		return slider;
	}

	private JSlider createSliderPcmRate() {
		int pcmRateMin = 0;
		int pcmRateMax = pcmRates.length - 1;
		int pcmRateDefault = Util.indexOf(pcmRates, Config.PCM_DEFAULT_RATE);

		JSlider slider = new JSlider(JSlider.HORIZONTAL, pcmRateMin, pcmRateMax, pcmRateDefault);
		slider.addChangeListener(this);
		slider.setMajorTickSpacing(pcmRateDefault);
		slider.setMinorTickSpacing(1);
		slider.setPaintTicks(true);
		// Create the label table
		Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
		int pcmRateGsm = Util.indexOf(pcmRates, 8000);
		int pcmRateCd = Util.indexOf(pcmRates, 44100);
		labelTable.put(new Integer(pcmRateGsm), new JLabel("GSM"));
		labelTable.put(new Integer(pcmRateCd), new JLabel("CD"));
		labelTable.put(new Integer(pcmRateMax), new JLabel("max"));
		slider.setLabelTable(labelTable);
		slider.setPaintLabels(true);
		return slider;
	}

	private JComboBox<AudioDevice> createComboboxMicrophones() {
		JComboBox<AudioDevice> combobox = new JComboBox<>();
		combobox.setModel(new AudioDeviceListModel(Microphones.getInstance()));
		combobox.addActionListener(this);
		return combobox;
	}

	private JComboBox<AudioDevice> createComboboxSpeakers() {
		JComboBox<AudioDevice> combobox = new JComboBox<>();
		combobox.setModel(new AudioDeviceListModel(Speakers.getInstance()));
		combobox.addActionListener(this);
		return combobox;
	}

	public JComponent getComponent() {
		return scrollpane;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(comboboxMicrophones)) {
			if (comboboxMicrophones.getSelectedIndex() != -1) {
				Microphone selected = (Microphone) comboboxMicrophones.getSelectedItem();
				Microphones.setCurrentMicrophone(selected);
			}
		}
		if (event.getSource().equals(comboboxSpeakers)) {
			if (comboboxSpeakers.getSelectedIndex() != -1) {
				Speaker selected = (Speaker) comboboxSpeakers.getSelectedItem();
				Speakers.setCurrentSpeaker(selected);
			}
		}
		if (event.getSource().equals(radioSampleSize8) || event.getSource().equals(radioSampleSize16)) {
			int samplesize = Config.SELECTED_PCM_SAMPLE_SIZE.getIntegerValue();
			if (radioSampleSize8.isSelected() && samplesize != 8) {
				Config.SELECTED_PCM_SAMPLE_SIZE.setIntegerValue(8);
			}
			if (radioSampleSize16.isSelected() && samplesize != 16) {
				Config.SELECTED_PCM_SAMPLE_SIZE.setIntegerValue(16);
			}
		}
		updateSelectedFormat();
	}

	@Override
	public void stateChanged(ChangeEvent event) {
		if (event.getSource().equals(sliderCallBuffer)) {
			int buffersize = sliderCallBuffer.getValue();
			if (buffersize == 0) {
				buffersize = 1;
				sliderCallBuffer.setValue(buffersize);
			}
			if (!sliderCallBuffer.getValueIsAdjusting()) {
				buffersize *= 1024;
				if (Config.BUFFER_SIZE_CALLS.getIntegerValue() != buffersize)
					Config.BUFFER_SIZE_CALLS.setIntegerValue(buffersize);
			}
		}
		if (event.getSource().equals(sliderPcmRate)) {
			if (!sliderPcmRate.getValueIsAdjusting()) {
				int rate = getSliderPcmRate();
				if (Config.SELECTED_PCM_RATE.getIntegerValue() != rate)
					Config.SELECTED_PCM_RATE.setIntegerValue(rate);

			}
		}
		updateSelectedFormat();
	}

	private int getSliderPcmRate() {
		int rateIndex = sliderPcmRate.getValue();
		if (rateIndex >= 0 && rateIndex < pcmRates.length) {
			int rate = pcmRates[sliderPcmRate.getValue()];
			return rate;
		} else {
			return Config.PCM_DEFAULT_RATE;
		}
	}

	@Override
	public void onConfigUpdate(Option option, float value) {}

	@Override
	public void onConfigUpdate(Option option, int value) {
		if (option.equals(Config.BUFFER_SIZE_CALLS)) {
			value /= 1024;
			if (sliderCallBuffer.getValue() != value) {
				sliderCallBuffer.setValue(value);
			}
		}
		if (option.equals(Config.SELECTED_PCM_RATE)) {
			int rateindex = Util.indexOf(pcmRates, value);
			if (rateindex >= sliderPcmRate.getMinimum() && rateindex <= sliderPcmRate.getMaximum()
					&& rateindex != sliderPcmRate.getValue()) {
				sliderPcmRate.setValue(rateindex);
			}
		}
		if (option.equals(Config.SELECTED_PCM_SAMPLE_SIZE)) {
			if (value == 8)
				radioSampleSize8.setSelected(true);
			else
				radioSampleSize16.setSelected(true);
		}
		updateSelectedFormat();
	}

	@Override
	public void onConfigUpdate(Option option, boolean value) {}

	@Override
	public void onConfigUpdate(Option option, String value) {}

	@Override
	public void onConfigUpdate(Option option, Id value) {
		if (option.equals(Config.SELECTED_MICROPHONE)) {
			comboboxMicrophones.setSelectedItem(value);
		}
		if (option.equals(Config.SELECTED_SPEAKER)) {
			comboboxSpeakers.setSelectedItem(value);
		}
	}

	public void updateSelectedFormat() {
		int rate = getSliderPcmRate();
		int samplesize = Config.SELECTED_PCM_SAMPLE_SIZE.getIntegerValue();
		int channels = Config.SELECTED_PCM_CHANNELS.getIntegerValue();
		format = new PcmFormat(rate, samplesize, channels);

		float kHz = (float) (rate) / 1000;
		int buffersize = sliderCallBuffer.getValue();
		int callbuffer = sliderCallBuffer.getValue() * 1024;
		float bitrate = format.getBitrate();
		float byterate = format.getByterate();
		int latency = (int) (1000.0f * callbuffer / byterate);

		updateCallBufferMaximum(format.getByterate());

		String text = "PCM <b>" + (channels == 8 ? "mono" : "stereo") + "</b>";
		text += ", sampling rate: <b>" + kHz + " kHz</b>";
		text += ", bits per sample: <b>" + samplesize + " bit</b>";
		text += ", buffer size: <b>" + buffersize + " KB</b>";
		text += "<br>\n";

		text += "Bitrate: <b>" + (int) (bitrate / 1024) + " kbit/s</b> (= <b>" + (int) (byterate / 1024)
				+ " KB/s</b>), ";
		text += "Latency: <b>" + latency + " ms</b>";
		textSelectedFormat.setText("<html>" + text + "</html>");
	}

	private void updateCallBufferMaximum(float byterate) {
		int max = ((int) (byterate / 1024 / 32) + 1) * 32;
		sliderCallBuffer.setMaximum(max);
		if (sliderCallBuffer.getValue() > max) {
			sliderCallBuffer.setValue(max);
		}
		sliderCallBuffer.setMajorTickSpacing(max / 8);
		sliderCallBuffer.setMinorTickSpacing(max / 32);
	}

	@Override
	public String getId() {
		return "SettingsCodecsTab";
	}

}