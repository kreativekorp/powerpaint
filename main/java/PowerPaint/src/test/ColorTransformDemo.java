package test;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.kreative.paint.rcp.CheckerboardPaint;
import com.kreative.paint.sprite.ColorTransform;

public class ColorTransformDemo {
	private static final int[] ALPHAS = {
		0x00000000, 0x11000000, 0x22000000, 0x33000000, 0x44000000, 0x55000000,
		0x66000000, 0x77000000, 0x88000000, 0x99000000, 0xAA000000, 0xBB000000,
		0xCC000000, 0xDD000000, 0xEE000000, 0xFF000000
	};
	private static final int[] COLORS = {
		0x000000, 0x111111, 0x222222, 0x333333, 0x444444, 0x555555,
		0x666666, 0x777777, 0x888888, 0x999999, 0xAAAAAA, 0xBBBBBB,
		0xCCCCCC, 0xDDDDDD, 0xEEEEEE, 0xFFFFFF,
		0xFF0000, 0xFF5500, 0xFFAA00, 0xFFFF00, 0xAAFF00, 0x55FF00,
		0x00FF00, 0x00FF55, 0x00FFAA, 0x00FFFF, 0x00AAFF, 0x0055FF,
		0x0000FF, 0x5500FF, 0xAA00FF, 0xFF00FF, 0xFF00AA, 0xFF0055
	};
	private static final int[] DEFAULT_PALETTE = {
		0xFF000000, 0xFFFFFFFF, 0xFFFF0000, 0xFFFFFF00,
		0xFF00FF00, 0xFF00FFFF, 0xFF0000FF, 0xFFFF00FF
	};
	private static final int[] REVERSE_PALETTE = {
		0xFFFFFFFF, 0xFF000000, 0xFF0000FF, 0xFF00FFFF,
		0xFF00FF00, 0xFFFFFF00, 0xFFFF0000, 0xFFFF00FF
	};
	private static final Random RANDOM = new Random();
	
	private static int[] makePixelArray() {
		int w = ALPHAS.length << 2;
		int h = COLORS.length << 1;
		int[] p = new int[w * h];
		for (int i = 0, y = 0; y < h; y++) {
			int c = COLORS[y >> 1];
			for (int x = 0; x < w; x++) {
				int a = ALPHAS[x >> 2];
				p[i++] = a | c;
			}
		}
		return p;
	}
	private static int[] makeRandomPalette(boolean alpha) {
		int[] p = new int[8];
		for (int i = 0; i < 8; i++) {
			p[i] = RANDOM.nextInt();
			if (!alpha) p[i] |= 0xFF000000;
		}
		return p;
	}
	private static BufferedImage[] makeImages(int[] palette) {
		BufferedImage[] images = new BufferedImage[0x40];
		int w = ALPHAS.length << 2;
		int h = COLORS.length << 1;
		int[] pt = makePixelArray();
		for (int i = 0; i < 0x40; i++) {
			ColorTransform ct = new ColorTransform(i);
			int[] p = new int[pt.length];
			ct.preparePixels(p, 0, pt, 0, pt.length);
			ct.replacePixels(
				p, 0, p, 0, pt.length,
				palette[0], palette[1], palette[2], palette[3],
				palette[4], palette[5], palette[6], palette[7]
			);
			images[i] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			images[i].setRGB(0, 0, w, h, p, 0, w);
		}
		return images;
	}
	
	private static abstract class PaletteButtonListener implements ActionListener {
		private final JPanel imagePanel;
		public PaletteButtonListener(JPanel imagePanel) {
			this.imagePanel = imagePanel;
		}
		public abstract int[] getPalette();
		@Override
		public final void actionPerformed(ActionEvent e) {
			imagePanel.removeAll();
			for (BufferedImage image : makeImages(getPalette())) {
				imagePanel.add(new JLabel(new ImageIcon(image)));
			}
			imagePanel.revalidate();
		}
	}
	private static JPanel makePanel() {
		JPanel imagePanel = new JPanel(new GridLayout(0, 16, 20, 20));
		imagePanel.setOpaque(false);
		for (BufferedImage image : makeImages(DEFAULT_PALETTE)) {
			imagePanel.add(new JLabel(new ImageIcon(image)));
		}
		JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 8, 8));
		buttonPanel.setOpaque(false);
		JButton db = new JButton("Default");
		db.addActionListener(new PaletteButtonListener(imagePanel) {
			@Override
			public int[] getPalette() {
				return DEFAULT_PALETTE;
			}
		});
		buttonPanel.add(db);
		JButton rb = new JButton("Reverse");
		rb.addActionListener(new PaletteButtonListener(imagePanel) {
			@Override
			public int[] getPalette() {
				return REVERSE_PALETTE;
			}
		});
		buttonPanel.add(rb);
		JButton rcb = new JButton("Random");
		rcb.addActionListener(new PaletteButtonListener(imagePanel) {
			@Override
			public int[] getPalette() {
				return makeRandomPalette(false);
			}
		});
		buttonPanel.add(rcb);
		JButton rab = new JButton("Alpha");
		rab.addActionListener(new PaletteButtonListener(imagePanel) {
			@Override
			public int[] getPalette() {
				return makeRandomPalette(true);
			}
		});
		buttonPanel.add(rab);
		JPanel mainPanel = new JPanel(new BorderLayout(20, 20)) {
			private static final long serialVersionUID = 1L;
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D)g;
				g2.setPaint(CheckerboardPaint.LIGHT);
				g2.fillRect(0, 0, getWidth(), getHeight());
			}
		};
		mainPanel.add(imagePanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.PAGE_END);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		return mainPanel;
	}
	private static JFrame makeFrame() {
		JFrame frame = new JFrame("Color Transform Demo");
		frame.setContentPane(makePanel());
		frame.pack();
		frame.setResizable(false);
		return frame;
	}
	
	public static void main(String[] args) {
		makeFrame().setVisible(true);
	}
}
