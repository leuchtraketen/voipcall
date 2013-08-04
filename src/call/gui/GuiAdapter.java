package call.gui;

import javax.swing.JComponent;

import call.Call;
import call.CallFactory;
import call.CallUi;
import call.Contact;
import call.Util;

public class GuiAdapter implements CallUi.CallUiAdapter {

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
		//final Icon tabIcon = Resources.getIcon(contact);
		
		MainGui main = MainGui.getInstance();
		main.closeInactiveTabsExcept(Util.asSet(new String[] { tabName }));
		main.addTab(tabName, tabContent);
		main.showTab(tabName);
	}

}
