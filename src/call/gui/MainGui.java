package call.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import call.CallFactory;
import call.CallUi;
import call.gui.ChatTab.ChatPanel;

public class MainGui {

	private static final String WINDOW_TITLE = "Call";

	private final JFrame window;
	private final JTabbedPane tabs;
	private final JSplitPane horizontalSplitPane;

	private static MainGui instance;
	private final ContactsBar contactsGui;

	public static MainGui getInstance() {
		if (instance == null) {
			instance = new MainGui();
			CallUi.register(new GuiAdapter());
		}
		return instance;
	}

	private MainGui() {
		window = new JFrame();
		window.setTitle(WINDOW_TITLE);
		tabs = new JTabbedPane();

		contactsGui = new ContactsBar();
		JComponent contactsPanel = contactsGui.getComponent();

		horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		horizontalSplitPane.setOneTouchExpandable(true);
		horizontalSplitPane.setDividerLocation(contactsPanel.getPreferredSize().width);
		horizontalSplitPane.add(contactsPanel);
		horizontalSplitPane.add(tabs);
		window.add(BorderLayout.CENTER, horizontalSplitPane);

		addTab("Terminal Output", new ConsoleTab().getComponent());

		tabs.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JTabbedPane pane = (JTabbedPane) e.getSource();
				int index = pane.getSelectedIndex();
				Object component = tabs.getComponentAt(index);
				if (component instanceof ChatPanel) {
					final ChatTab chatgui = ((ChatPanel) component).getChatGui();
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							chatgui.focus();
						}
					});
				}
			}
		});
	}

	public void addTab(String name, JComponent component, Icon icon) {
		if (tabs.indexOfTab(name) == -1) {
			addClosableTab(tabs, component, name, icon);
		}
	}

	public void addTab(String name, JComponent component) {
		if (tabs.indexOfTab(name) == -1) {
			addClosableTab(tabs, component, name, null);
			// tabs.addTab(name, component);
		}
	}

	public void showTab(String name) {
		int index = tabs.indexOfTab(name);
		if (index != -1) {
			tabs.setSelectedIndex(index);
		}
	}

	public void closeInactiveTabsExcept(Set<String> except) {
		for (int i = tabs.getTabCount() - 1; i >= 0; --i) {
			if (!except.contains(tabs.getTitleAt(i))) {
				closeInactiveTab(i);
			}
		}
	}

	private void closeInactiveTab(int i) {
		Object component = tabs.getComponentAt(i);
		if (component instanceof ChatPanel) {
			ChatTab chatgui = ((ChatPanel) component).getChatGui();
			/*
			 * Connection connection = chatgui.getCallaction().getConnection();
			 * if (connection == null || connection.isFinished()) {
			 * tabs.remove((Component) component); }
			 */
			if (!CallFactory.existsCall(chatgui.getContact())) {
				tabs.remove((Component) component);
			}
		}
	}

	public void runGui() {
		window.setSize(750, 350);
		window.pack();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - window.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - window.getHeight()) / 2);
		window.setLocation(x, y);
		window.setIconImage(new ImageIcon("img/icon.png").getImage());
		window.setVisible(true);
	}

	/**
	 * Adds a component to a JTabbedPane with a little "close tab" button on the
	 * right side of the tab.
	 * 
	 * @param tabbedPane
	 *        the JTabbedPane
	 * @param c
	 *        any JComponent
	 * @param title
	 *        the title for the tab
	 * @param icon
	 *        the icon for the tab, if desired
	 */
	public static void addClosableTab(final JTabbedPane tabbedPane, final JComponent c, final String title,
			final Icon icon) {

		// Add the tab to the pane without any label
		tabbedPane.addTab(null, c);
		int pos = tabbedPane.indexOfComponent(c);

		// Create a FlowLayout that will space things 5px apart
		FlowLayout f = new FlowLayout(FlowLayout.CENTER, 5, 0);

		// Make a small JPanel with the layout and make it non-opaque
		JPanel pnlTab = new JPanel(f);
		pnlTab.setOpaque(false);

		// Add a JLabel with title and the left-side tab icon
		JLabel lblTitle = new JLabel(title);
		lblTitle.setFont(Resources.TEXT_FONT);
		if (icon != null) {
			lblTitle.setIcon(icon);
		}
		// Create a JButton for the close tab button
		JButton btnClose = new JButton();
		btnClose.setOpaque(false);

		// Configure icon and rollover icon for button
		btnClose.setRolloverIcon(Resources.ICON_TAB_CLOSE_HOVER);
		btnClose.setRolloverEnabled(true);
		btnClose.setIcon(Resources.ICON_TAB_CLOSE);

		// Set border null so the button doesn't make the tab too big
		btnClose.setBorder(null);

		// Make sure the button can't get focus, otherwise it looks funny
		btnClose.setFocusable(false);

		// Put the panel together
		pnlTab.add(lblTitle);
		pnlTab.add(btnClose);

		// Add a thin border to keep the image below the top edge of the tab
		// when the tab is selected
		pnlTab.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));

		// Now assign the component for the tab
		tabbedPane.setTabComponentAt(pos, pnlTab);

		// Add the listener that removes the tab
		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// The component parameter must be declared "final" so that it
				// can be
				// referenced in the anonymous listener class like this.
				tabbedPane.remove(c);
			}
		};
		btnClose.addActionListener(listener);

		// Optionally bring the new tab to the front
		tabbedPane.setSelectedComponent(c);

		// -------------------------------------------------------------
		// Bonus: Adding a <Ctrl-W> keystroke binding to close the tab
		// -------------------------------------------------------------
		AbstractAction closeTabAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				tabbedPane.remove(c);
			}
		};

		// Create a keystroke
		KeyStroke controlW = KeyStroke.getKeyStroke("control W");

		// Get the appropriate input map using the JComponent constants.
		// This one works well when the component is a container.
		InputMap inputMap = c.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		// Add the key binding for the keystroke to the action name
		inputMap.put(controlW, "closeTab");

		// Now add a single binding for the action name to the anonymous action
		c.getActionMap().put("closeTab", closeTabAction);
	}

}
