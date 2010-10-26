package test;

import java.awt.*;
import javax.swing.*;

public class ovaltest {
	public static void main(String[] args) {
		JComponent fc = new JComponent() {
			private static final long serialVersionUID = 1L;
			protected void paintComponent(Graphics g) {
				g.setColor(Color.red);
				((Graphics2D)g).setStroke(new BasicStroke(0));
				g.fillOval(0, 0, getWidth()-1, getHeight()-1);
			}
		};
		JFrame ff = new JFrame("FillOval");
		ff.setContentPane(fc);
		ff.pack();
		ff.setSize(100, 100);
		ff.setLocation(100, 100);
		ff.setVisible(true);
		JComponent dc = new JComponent() {
			private static final long serialVersionUID = 1L;
			protected void paintComponent(Graphics g) {
				g.setColor(Color.red);
				((Graphics2D)g).setStroke(new BasicStroke(1));
				g.drawOval(0, 0, getWidth(), getHeight());
			}
		};
		JFrame df = new JFrame("DrawOval");
		df.setContentPane(dc);
		df.pack();
		df.setSize(100, 100);
		df.setLocation(100, 100);
		df.setVisible(true);
	}
}
