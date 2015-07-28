package test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class ImageSplit {
	public static void main(String[] args) {
		int cellWidth = -1, cellHeight = -1;
		File outDir = null;
		String outPrefix = null;
		boolean processingFlags = true;
		int i = 0; while (i < args.length) {
			String arg = args[i++];
			if (processingFlags && arg.startsWith("-")) {
				if (arg.equals("--")) {
					processingFlags = false;
				} else if (arg.equals("-W") && i < args.length) {
					cellWidth = parseInt(args[i++]);
				} else if (arg.equals("-H") && i < args.length) {
					cellHeight = parseInt(args[i++]);
				} else if (arg.equals("-D") && i < args.length) {
					outDir = new File(args[i++]);
				} else if (arg.equals("-P") && i < args.length) {
					outPrefix = args[i++];
				} else {
					System.err.println("Ignoring unknown option: " + arg);
				}
			} else {
				try {
					File file = new File(arg);
					BufferedImage src = ImageIO.read(file);
					int w = src.getWidth();
					int h = src.getHeight();
					int cw = (cellWidth > 0) ? cellWidth : Math.min(w, h);
					int ch = (cellHeight > 0) ? cellHeight : Math.min(w, h);
					List<BufferedImage> images = new ArrayList<BufferedImage>();
					for (int y = 0; y + ch <= h; y += ch) {
						for (int x = 0; x + cw <= w; x += cw) {
							BufferedImage dst = new BufferedImage(cw, ch, BufferedImage.TYPE_INT_ARGB);
							int[] rgb = new int[cw * ch];
							src.getRGB(x, y, cw, ch, rgb, 0, cw);
							dst.setRGB(0, 0, cw, ch, rgb, 0, cw);
							images.add(dst);
						}
					}
					String name = file.getName().replaceFirst("\\.[a-zA-Z0-9]+$", "");
					for (int a = 0, n = images.size() - 1; a <= n; a++) {
						File out = new File(
							(outDir != null) ? outDir : file.getParentFile(),
							((outPrefix != null) ? outPrefix : (name + ".")) +
							intToString(a, n) + ".png"
						);
						ImageIO.write(images.get(a), "png", out);
					}
				} catch (IOException e) {
					System.err.println("Error processing " + arg + ": " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}
	
	private static int parseInt(String s) {
		try { return Integer.parseInt(s); }
		catch (NumberFormatException e) { return 0; }
	}
	
	private static String intToString(int v, int max) {
		String pad = Integer.toString(max).replaceAll(".", "0");
		String s = pad + Integer.toString(v);
		return s.substring(s.length() - pad.length());
	}
}
