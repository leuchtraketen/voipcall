package call.gui;

import java.awt.Component;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;

import call.AbstractId;
import call.Util;

public abstract class AbstractTabs extends AbstractId {

	protected final JTabbedPane tabs;
	protected final Lock lock;

	public AbstractTabs() {
		this.tabs = new JTabbedPane();
		this.lock = new ReentrantLock();
	}

	public Component getComponent() {
		return tabs;
	}

	public void addTab(String name, JComponent component, Icon icon) {
		lock.lock();
		if (tabs.indexOfTab(name) == -1) {
			CloseableTab.addClosableTab(tabs, component, name, icon);
		}
		lock.unlock();
	}

	public void addTab(String name, JComponent component) {
		lock.lock();
		if (tabs.indexOfTab(name) == -1) {
			CloseableTab.addClosableTab(tabs, component, name, null);
		}
		lock.unlock();
	}

	public void showTab(String name) {
		lock.lock();
		int index = tabs.indexOfTab(name);
		if (index != -1 && tabs.getSelectedIndex() != index) {
			tabs.setSelectedIndex(index);
		}
		lock.unlock();
	}

	public void removeTab(String title) {
		lock.lock();
		int index = CloseableTab.indexOfTab(tabs, title);
		while (index != -1) {
			Util.log(this, "index = " + index);
			tabs.remove(index);
			index = CloseableTab.indexOfTab(tabs, title);
		}
		lock.unlock();
	}

}
