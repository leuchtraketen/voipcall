package call.gui;

import javax.swing.Icon;
import javax.swing.JButton;

public class JHoverButton extends JButton {

	private static final long serialVersionUID = 6769411301997276970L;

	public JHoverButton(Icon icon, Icon iconHovered, String text) {
		super(icon);
		setIcon(icon, iconHovered);
		this.setText(text);
	}

	public JHoverButton(Icon icon, Icon iconHovered) {
		super(icon);
		setIcon(icon, iconHovered);
	}

	public void setIcon(Icon icon, Icon iconHovered) {
		setIcon(icon);
		setPressedIcon(iconHovered);
		setRolloverIcon(iconHovered);
		setRolloverEnabled(true);
		setSelectedIcon(icon);
		setDisabledIcon(icon);
		setDisabledSelectedIcon(icon);
		setRolloverSelectedIcon(icon);
	}
}
