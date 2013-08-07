package call.gui;

import java.awt.Component;
import java.util.Set;

import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import call.CallFactory;
import call.gui.ChatTab.ChatTabComponent;

public class MainTabs extends AbstractTabs implements ChangeListener {

	private final MainWindow main;

	public MainTabs(MainWindow main) {
		super();
		this.main = main;
		this.tabs.addChangeListener(this);
	}

	public void closeInactiveChatTabsExcept(Set<String> except) {
		lock.lock();
		for (int i = tabs.getTabCount() - 1; i >= 0; --i) {
			String title = CloseableTab.getTitleAt(tabs, i);
			if (title != null && !except.contains(title)) {
				closeInactiveChatTab(i);
			}
		}
		lock.unlock();
	}

	private void closeInactiveChatTab(int i) {
		Object component = tabs.getComponentAt(i);
		if (component instanceof ChatTabComponent) {
			ChatTab chatgui = ((ChatTabComponent) component).getChatTab();
			if (!CallFactory.existsCall(chatgui.getContact())) {
				tabs.remove((Component) component);
			}
		}
	}

	public void stateChanged(ChangeEvent e) {
		lock.lock();
		JTabbedPane pane = (JTabbedPane) e.getSource();
		int index = pane.getSelectedIndex();
		if (index != -1) {
			Object component = tabs.getComponentAt(index);
			if (component instanceof ChatTabComponent) {
				final ChatTab chattab = ((ChatTabComponent) component).getChatTab();
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						chattab.focus();
						main.getContacts().setSelectedContact(chattab.getContact());
					}
				});
			} else {
				main.getContacts().setSelectedContact(null);
			}
		}
		lock.unlock();
	}

	@Override
	public String getId() {
		return "MainTabs";
	}
}
