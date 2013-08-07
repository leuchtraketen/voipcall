package call.gui;

import javax.swing.JComponent;

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
	public void openCall(Contact contact) {
		@SuppressWarnings("unused")
		Call call = CallFactory.getCall(contact);
		ChatTab.getInstance(contact).getCallaction().openCall();
	}

	@Override
	public void openChat(Contact contact) {
		// open and select the tab
		final String tabName = contact.getId();
		final JComponent tabContent = ChatTab.getInstance(contact).getComponent();
		// final Icon tabIcon = Resources.getIcon(contact);

		MainTabs tabs = main.getTabs();
		tabs.closeInactiveChatTabsExcept(Util.asSet(new String[] { tabName }));
		tabs.addTab(tabName, tabContent);
		tabs.showTab(tabName);
	}

}
