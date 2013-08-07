package call;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class MultiMessageOutput implements MessageOutput {

	private final List<MessageOutput> messageoutputs = new ArrayList<>();

	public MultiMessageOutput() {}

	public MultiMessageOutput(MessageOutput msgout1, MessageOutput msgout2) {
		add(msgout1);
		add(msgout2);
	}

	public MultiMessageOutput(List<MessageOutput> messageoutputs) {
		add(messageoutputs);
	}

	public void add(MessageOutput messageoutput) {
		this.messageoutputs.add(messageoutput);
	}

	public void add(List<MessageOutput> messageoutputs) {
		this.messageoutputs.addAll(messageoutputs);
	}

	@Override
	public void append(String str, Color c) {
		for (MessageOutput msgout : messageoutputs) {
			msgout.append(str, c);
		}
	}

	@Override
	public void close() {
		for (MessageOutput msgout : messageoutputs) {
			msgout.close();
		}
	}

}
