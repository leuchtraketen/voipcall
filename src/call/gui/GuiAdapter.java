package call.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import call.CallUi;
import call.Connection;
import call.Contact;
import call.Util;

public class GuiAdapter implements CallUi.CallUiAdapter {

	@Override
	public void openCall(Contact contact) {
		// open and select the tab
		final String tabName = contact.getId();
		final JComponent tabContent = ChatTab.getInstance(contact).getComponent();
		MainGui main = MainGui.getInstance();
		main.closeInactiveTabsExcept(Util.asSet(new String[] { tabName }));
		main.addTab(tabName, tabContent);
		main.showTab(tabName);
	}

	@Override
	public List<Connection> getUiListeners(Contact contact) {
		List<Connection> listeners = new ArrayList<Connection>();
		listeners.add(ChatTab.getInstance(contact).getCallaction());
		return listeners;
	}

}
