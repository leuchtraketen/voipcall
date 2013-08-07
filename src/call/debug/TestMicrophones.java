package call.debug;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import call.Microphones;

public class TestMicrophones {
	public static void main(String[] args) {
		new TestMicrophones(args.length > 0 ? args[0] : null);
	}

	public TestMicrophones(String ip) {
		@SuppressWarnings("unused")
		Microphones micros = new Microphones();

		int rows = 20;
		int cols = 40;
		int cellWidth = 20;
		ColorGrid mainPanel = new ColorGrid(rows, cols, cellWidth);

		JFrame frame = new JFrame("Color Grid Example");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(mainPanel);
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
	}

	static class ColorGrid extends JPanel {
		private MyColor[][] myColors;
		private JLabel[][] myLabels;

		public ColorGrid(int rows, int cols, int cellWidth) {
			myColors = new MyColor[rows][cols];
			myLabels = new JLabel[rows][cols];

			MyMouseListener myListener = new MyMouseListener(this);
			Dimension labelPrefSize = new Dimension(cellWidth, cellWidth);
			setLayout(new GridLayout(rows, cols));
			for (int row = 0; row < myLabels.length; row++) {
				for (int col = 0; col < myLabels[row].length; col++) {
					JLabel myLabel = new JLabel();
					myLabel = new JLabel();
					myLabel.setOpaque(true);
					MyColor myColor = MyColor.GREEN;
					myColors[row][col] = myColor;
					myLabel.setBackground(myColor.getColor());
					myLabel.addMouseListener(myListener);
					myLabel.setPreferredSize(labelPrefSize);
					add(myLabel);
					myLabels[row][col] = myLabel;
				}
			}
		}

		public MyColor[][] getMyColors() {
			return myColors;
		}

		public void labelPressed(JLabel label) {
			for (int row = 0; row < myLabels.length; row++) {
				for (int col = 0; col < myLabels[row].length; col++) {
					if (label == myLabels[row][col]) {
						MyColor myColor = myColors[row][col].next();
						myColors[row][col] = myColor;
						myLabels[row][col].setBackground(myColor.getColor());
					}
				}
			}
		}
	}

	static enum MyColor {
		GREEN(Color.green, "Green", "g"), RED(Color.red, "Red", "r"), BLUE(Color.blue, "Blue", "b"), YELLOW(
				Color.yellow, "Yellow", "y");
		private Color color;
		private String name;
		private String shortName;

		private MyColor(Color color, String name, String shortName) {
			this.color = color;
			this.name = name;
			this.shortName = shortName;
		}

		public MyColor next() {
			int index = 0;
			for (int i = 0; i < MyColor.values().length; i++) {
				if (MyColor.values()[i] == this) {
					index = (i + 1) % MyColor.values().length;
				}
			}
			return MyColor.values()[index];
		}

		public Color getColor() {
			return color;
		}

		public String getName() {
			return name;
		}

		public String getShortName() {
			return shortName;
		}

		@Override
		public String toString() {
			return shortName;
		}

	}

	static class MyMouseListener extends MouseAdapter {
		private ColorGrid colorGrid;

		public MyMouseListener(ColorGrid colorGrid) {
			this.colorGrid = colorGrid;
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				colorGrid.labelPressed((JLabel) e.getSource());
			} else if (e.getButton() == MouseEvent.BUTTON3) {
				MyColor[][] myColors = colorGrid.getMyColors();
				for (int row = 0; row < myColors.length; row++) {
					for (int col = 0; col < myColors[row].length; col++) {
						System.out.print(myColors[row][col] + " ");
					}
					System.out.println();
				}
				System.out.println();
			}
		}
	}
}
