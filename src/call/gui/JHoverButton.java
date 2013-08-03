package call.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JButton;

public class JHoverButton extends JButton {

	private static final long serialVersionUID = 6769411301997276970L;

	private Icon iconHovered;
	private Icon icon;
	private boolean hovered;

	public JHoverButton(Icon icon, Icon iconHovered, String text) {
		super(icon);
		this.iconHovered = iconHovered;
		this.icon = icon;
		this.setText(text);
		this.addHoverMouseListener();
	}

	public JHoverButton(Icon icon, Icon iconHovered) {
		super(icon);
		this.iconHovered = iconHovered;
		this.icon = icon;
		this.addHoverMouseListener();
	}

	@Override
	public void setIcon(Icon icon) {
		setIcon(icon, icon);
	}

	public void setCurrentIcon(Icon icon) {
		super.setIcon(icon);
	}

	public void setIcon(Icon icon, Icon iconHovered) {
		this.iconHovered = iconHovered;
		this.icon = icon;
		if (hovered) {
			setCurrentIcon(iconHovered);
		} else {
			setCurrentIcon(icon);
		}
	}

	public Icon getImageIconHovered() {
		return iconHovered;
	}

	public Icon getImageIcon() {
		return icon;
	}

	private void addHoverMouseListener() {
		this.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				hovered = true;
				JHoverButton button = ((JHoverButton) e.getSource());
				button.setCurrentIcon(button.getImageIconHovered());
			}

			public void mouseExited(MouseEvent e) {
				JHoverButton button = ((JHoverButton) e.getSource());
				button.setCurrentIcon(button.getImageIcon());
				hovered = false;
			}
		});
	}

}
