package call.gui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

public class CloseableTab extends JPanel {

	private static final long serialVersionUID = 2770093778823348453L;

	private final String title;

	private CloseableTab(String title) {
		super();
		this.title = title;
	}

	public static String getTitleAt(JTabbedPane tabs, int index) {
		Component component = tabs.getTabComponentAt(index);
		if (component instanceof CloseableTab) {
			return ((CloseableTab) component).title;
		} else {
			return tabs.getTitleAt(index);
		}
	}

	public static int indexOfTab(JTabbedPane tabs, String title) {
		for (int i = 0; i < tabs.getTabCount(); ++i) {
			if (title.equals(getTitleAt(tabs, i))) {
				return i;
			}
		}
		return -1;
	}

	public String getTitle() {
		return title;
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
		FlowLayout layout = new FlowLayout(FlowLayout.CENTER, 5, 0);

		// Make a small JPanel with the layout and make it non-opaque
		CloseableTab closeabletab = new CloseableTab(title);
		closeabletab.setLayout(layout);
		closeabletab.setOpaque(false);

		// Add a JLabel with title and the left-side tab icon
		JLabel lblTitle = new JLabel(title);
		lblTitle.setFont(Resources.FONT_TABTITLE);
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
		closeabletab.add(lblTitle);
		closeabletab.add(btnClose);

		// Add a thin border to keep the image below the top edge of the tab
		// when the tab is selected
		closeabletab.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));

		// Now assign the component for the tab
		tabbedPane.setTabComponentAt(pos, closeabletab);

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