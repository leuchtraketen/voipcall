package call.gui;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import call.Call;
import call.CallFactory;
import call.CallUi;
import call.Contact;
import call.Util;

public class GuiAdapter implements CallUi.CallUiAdapter {

	private final MainWindow main;

	public GuiAdapter(MainWindow main) {
		this.main = main;
	}

	@Override
	public void openCall(final Contact contact) {
		@SuppressWarnings("unused")
		Call call = CallFactory.getCall(contact);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ChatTab.getInstance(contact).getCallaction().openCall();
			}
		});
	}

	@Override
	public void openChat(Contact contact) {
		// open and select the tab
		final String tabName = contact.toString();
		final JComponent tabContent = ChatTab.getInstance(contact).getComponent();
		// final Icon tabIcon = Resources.getIcon(contact);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainTabs tabs = main.getTabs();
				tabs.closeInactiveChatTabsExcept(Util.asSet(new String[] { tabName }));
				tabs.addTab(tabName, tabContent);
				tabs.showTab(tabName);
			}
		});
	}

	@Override
	public void updateCallStats(final Contact contact, final float incomingSpeed, final long incomingTotal,
			final float outgoingSpeed, final long outgoingTotal) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ChatTab.getInstance(contact).updateCallStats(incomingSpeed, incomingTotal, outgoingSpeed,
						outgoingTotal);
			}
		});
	}

}
