package call.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

import call.AbstractId;
import call.AudioDevice;
import call.Config;
import call.Config.Option;
import call.ConfigListener;
import call.Id;
import call.Microphone;
import call.Microphones;
import call.Speaker;
import call.Speakers;

public class SettingsAudioTab extends AbstractId implements ConfigListener, ActionListener {

	@SuppressWarnings("unused")
	private final MainWindow main;
	private final JScrollPane scrollpane;

	private final JComboBox<AudioDevice> comboboxMicrophones;
	private final JComboBox<AudioDevice> comboboxSpeakers;

	public SettingsAudioTab(MainWindow main) {
		this.main = main;

		// area
		JPanel settingspanel = new JPanel(new SpringLayout());
		JPanel mainpanel = new JPanel(new BorderLayout());
		mainpanel.add(settingspanel, BorderLayout.NORTH);
		mainpanel.add(new JPanel(), BorderLayout.CENTER);
		scrollpane = new JScrollPane(mainpanel);

		settingspanel.add(new JLabel(Resources.LABEL_SETTINGS_AUDIO_DEFAULT_MICROPHONE));
		comboboxMicrophones = new JComboBox<>();
		comboboxMicrophones.setModel(new AudioDeviceListModel(Microphones.getInstance()));
		comboboxMicrophones.addActionListener(this);
		settingspanel.add(comboboxMicrophones);
		settingspanel.add(new JLabel(Resources.LABEL_SETTINGS_AUDIO_DEFAULT_SPEAKER));
		comboboxSpeakers = new JComboBox<>();
		comboboxSpeakers.setModel(new AudioDeviceListModel(Speakers.getInstance()));
		settingspanel.add(comboboxSpeakers);
		// petList.addActionListener(this);

		SpringUtilities.makeCompactGrid(settingspanel, // panel
				2, 2, // rows, cols
				5, 5, // initialX, initialY
				5, 5);// xPad, yPad

		// config listener
		Config.addConfigListener(this);
		Config.notifyConfigListener(this);
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
	}

	@Override
	public void onConfigUpdate(Option option, float value) {}

	@Override
	public void onConfigUpdate(Option option, int value) {}

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

	@Override
	public String getId() {
		return "SettingsCodecsTab";
	}

}