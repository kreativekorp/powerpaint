package test;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import javax.imageio.ImageIO;

public class ColorItPatternConverter {
	public static void main(String[] args) throws IOException {
		for (String arg : args) {
			File inFile = new File(arg);
			String outDirName = "ColorIt~" + inFile.getName().replaceAll("\\s", "") + ".txrd";
			File outDir = new File(inFile.getParentFile(), outDirName);
			outDir.mkdir();
			DataInputStream in = new DataInputStream(new FileInputStream(inFile));
			in.readShort();
			in.readShort();
			in.readShort();
			int count = in.readShort();
			String z = zeroes(count);
			for (int i = 0; i < count; i++) {
				in.readShort();
			}
			for (int i = 0; i < count; i++) {
				in.readInt();
				int w = in.readInt();
				int h = in.readInt();
				BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
				for (int y = 0; y < h; y++) {
					for (int x = 0; x < w; x++) {
						int rgb = in.readInt() | 0xFF000000;
						img.setRGB(x, y, rgb);
					}
				}
				String n = z + i;
				n = n.substring(n.length() - z.length());
				File outFile = new File(outDir, n + ".png");
				ImageIO.write(img, "png", outFile);
			}
			in.close();
			File outFile = new File(outDir, "index.txrx");
			PrintStream out = new PrintStream(new FileOutputStream(outFile));
			out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			out.println("<!DOCTYPE textures PUBLIC \"-//Kreative//DTD PowerTexture 1.0//EN\" \"http://www.kreativekorp.com/dtd/txrx.dtd\">");
			out.println("<textures name=\"Color It! " + inFile.getName() + "\">");
			for (int i = 0; i < count; i++) {
				String n = z + i;
				n = n.substring(n.length() - z.length());
				out.println("\t<texture href=\"" + n + ".png\"/>");
			}
			out.println("</textures>");
			out.close();
		}
	}
	
	private static String zeroes(int v) {
		String d = "0";
		while (v >= 10) {
			d += "0";
			v /= 10;
		}
		return d;
	}
}
