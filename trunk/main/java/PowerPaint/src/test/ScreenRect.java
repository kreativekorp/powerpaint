package test;

import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

public class ScreenRect {
	public static void main(String[] args) {
		Rectangle r = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getBounds();
		System.out.println(r);
	}
}
