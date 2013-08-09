package call.gui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import call.Contact;

public class ContactListCellRenderer implements ListCellRenderer<Contact> {

	@Override
	public Component getListCellRendererComponent(JList<? extends Contact> list, Contact value, int index,
			boolean isSelected, boolean cellHasFocus) {

		JPanel cell = new JPanel();
		cell.setBorder(BorderFactory.createEmptyBorder());
		cell.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		BorderLayout layout = new BorderLayout();
		layout.setHgap(5);
		cell.setLayout(layout);
		setColors(cell, list, isSelected);

		JLabel textlabel = new JLabel();
		textlabel.setText(value.toString());
		textlabel.setFont(Resources.FONT_LIST);
		setColors(textlabel, list, isSelected);
		cell.add(textlabel, BorderLayout.CENTER);

		JLabel availabilitylabel = new JLabel();
		availabilitylabel.setIcon(Resources.getIcon(value));
		availabilitylabel.setToolTipText(Resources.getToolTipText(value));
		setColors(availabilitylabel, list, isSelected);
		cell.add(availabilitylabel, BorderLayout.WEST);

		return cell;
	}

	private void setColors(JComponent component, JList<? extends Contact> list, boolean isSelected) {
		component.setOpaque(true);
		if (isSelected) {
			component.setBackground(list.getSelectionBackground());
			component.setForeground(list.getSelectionForeground());
		} else {
			component.setBackground(list.getBackground());
			component.setForeground(list.getForeground());
		}
	}
}
