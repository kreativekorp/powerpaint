package test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class ImageCat {
	public static void main(String[] args) {
		List<BufferedImage> images = new ArrayList<BufferedImage>();
		int maxWidth = 0, totalWidth = 0, maxHeight = 0, totalHeight = 0;
		int rows = -1, columns = -1, halign = 0, valign = 0;
		File output = null;
		boolean processingFlags = true;
		int i = 0; while (i < args.length) {
			String arg = args[i++];
			if (processingFlags && arg.startsWith("-")) {
				if (arg.equals("--")) {
					processingFlags = false;
				} else if (arg.equals("-R") && i < args.length) {
					rows = parseInt(args[i++]);
				} else if (arg.equals("-C") && i < args.length) {
					columns = parseInt(args[i++]);
				} else if (arg.equals("--halign-left")) {
					halign = 0;
				} else if (arg.equals("--halign-center")) {
					halign = 1;
				} else if (arg.equals("--halign-right")) {
					halign = 2;
				} else if (arg.equals("--valign-top")) {
					valign = 0;
				} else if (arg.equals("--valign-middle")) {
					valign = 1;
				} else if (arg.equals("--valign-bottom")) {
					valign = 2;
				} else if (arg.equals("-o") && i < args.length) {
					output = new File(args[i++]);
				} else {
					System.err.println("Ignoring unknown option: " + arg);
				}
			} else {
				try {
					BufferedImage image = ImageIO.read(new File(arg));
					images.add(image);
					maxWidth = Math.max(maxWidth, image.getWidth());
					totalWidth += image.getWidth();
					maxHeight = Math.max(maxHeight, image.getHeight());
					totalHeight += image.getHeight();
				} catch (IOException e) {
					System.err.println("Error processing " + arg + ": " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
		if (images.isEmpty()) return;
		BufferedImage dst;
		if (columns < 0) {
			// horizontal
			if (rows <= 0) {
				dst = new BufferedImage(totalWidth, maxHeight, BufferedImage.TYPE_INT_ARGB);
				int x = 0;
				for (BufferedImage src : images) {
					int y = (maxHeight - src.getHeight()) * valign / 2;
					blit(src, dst, x, y);
					x += src.getWidth();
				}
			} else {
				columns = (images.size() + rows - 1) / rows;
				dst = new BufferedImage(maxWidth * columns, maxHeight * rows, BufferedImage.TYPE_INT_ARGB);
				for (int a = 0, y = 0; a < images.size() && y < rows; y++) {
					for (int x = 0; a < images.size() && x < columns; a++, x++) {
						BufferedImage src = images.get(a);
						int rx = x * maxWidth + (maxWidth - src.getWidth()) * halign / 2;
						int ry = y * maxHeight + (maxHeight - src.getHeight()) * valign / 2;
						blit(src, dst, rx, ry);
					}
				}
			}
		} else if (rows < 0) {
			// vertical
			if (columns <= 0) {
				dst = new BufferedImage(maxWidth, totalHeight, BufferedImage.TYPE_INT_ARGB);
				int y = 0;
				for (BufferedImage src : images) {
					int x = (maxWidth - src.getWidth()) * halign / 2;
					blit(src, dst, x, y);
					y += src.getHeight();
				}
			} else {
				rows = (images.size() + columns - 1) / columns;
				dst = new BufferedImage(maxWidth * columns, maxHeight * rows, BufferedImage.TYPE_INT_ARGB);
				for (int a = 0, x = 0; a < images.size() && x < columns; x++) {
					for (int y = 0; a < images.size() && y < rows; a++, y++) {
						BufferedImage src = images.get(a);
						int rx = x * maxWidth + (maxWidth - src.getWidth()) * halign / 2;
						int ry = y * maxHeight + (maxHeight - src.getHeight()) * valign / 2;
						blit(src, dst, rx, ry);
					}
				}
			}
		} else {
			// neither
			dst = new BufferedImage(maxWidth * columns, maxHeight * rows, BufferedImage.TYPE_INT_ARGB);
			for (int a = 0, y = 0; a < images.size() && y < rows; y++) {
				for (int x = 0; a < images.size() && x < columns; a++, x++) {
					BufferedImage src = images.get(a);
					int rx = x * maxWidth + (maxWidth - src.getWidth()) * halign / 2;
					int ry = y * maxHeight + (maxHeight - src.getHeight()) * valign / 2;
					blit(src, dst, rx, ry);
				}
			}
		}
		try {
			if (output != null) {
				ImageIO.write(dst, "png", output);
			} else {
				ImageIO.write(dst, "png", System.out);
			}
		} catch (IOException e) {
			System.err.println("Error writing image: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private static int parseInt(String s) {
		try { return Integer.parseInt(s); }
		catch (NumberFormatException e) { return 0; }
	}
	
	private static void blit(BufferedImage src, BufferedImage dst, int x, int y) {
		int w = src.getWidth();
		int h = src.getHeight();
		int[] rgb = new int[w * h];
		src.getRGB(0, 0, w, h, rgb, 0, w);
		dst.setRGB(x, y, w, h, rgb, 0, w);
	}
}
