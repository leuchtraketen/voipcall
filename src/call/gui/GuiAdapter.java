package call.gui;

import javax.swing.JComponent;

import call.CallUi;
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
	public void updateCall(Contact contact) {
		ChatTab.getInstance(contact).getCallaction().updateGui();
		Util.log("fuck", "GuiAdapter.updateCall()");

	}

}
