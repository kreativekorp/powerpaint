package test;

import java.awt.Color;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import com.kreative.paint.BufferedImagePaintSurface;
import com.kreative.paint.PaintSurface;
import com.kreative.paint.geom.BitmapShape;

public class PaintBitmapTest {
	public static void main(String[] args) throws IOException {
		BufferedImage bi = new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_ARGB);
		PaintSurface srf = new BufferedImagePaintSurface(bi);
		Graphics2D g = bi.createGraphics();
		int[] pata = new int[] {
				0xFFFF8080, 0xFF80FF80, 0xFF8080FF,
				0xFFFF0000, 0xFF00FF00, 0xFF0000FF,
				0xFF800000, 0xFF008000, 0xFF000080,
		};
		BufferedImage pati = new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB);
		pati.setRGB(0, 0, 3, 3, pata, 0, 3);
		TexturePaint patp = new TexturePaint(pati, new Rectangle(0, 0, 3, 3));
		int K = 0xFF000000;
		int[] rgb = new int[] {
				0,0,K,K,K,K,K,K,K,K,K,K,K,K,0,0,
				K,K,K,0,0,0,0,0,0,0,0,0,K,K,K,K,
				K,K,0,K,K,K,K,K,K,K,K,K,0,0,K,K,
				0,K,K,K,K,K,0,0,0,0,K,K,K,K,K,0,
				0,0,0,0,0,K,K,K,K,K,K,K,0,0,0,0,
				0,0,0,0,0,0,0,K,K,K,K,K,0,0,0,0,
				0,0,0,0,0,0,0,0,0,K,K,K,K,0,0,0,
				0,0,0,0,0,0,0,0,0,0,K,K,K,0,0,0,
				0,0,0,0,0,0,0,0,0,0,0,K,K,0,0,0,
				0,0,0,0,0,0,0,0,0,0,0,K,K,0,0,0,
				0,0,0,0,0,0,0,0,0,0,K,K,0,0,0,0,
				0,0,0,0,0,0,0,0,0,K,K,0,0,0,0,0,
				0,0,0,0,0,0,0,0,K,K,0,0,0,0,0,0,
				0,0,0,0,0,0,0,0,K,0,0,0,0,0,0,0,
				0,0,0,0,0,0,0,0,K,0,0,0,0,0,0,0,
				0,0,0,0,0,0,0,0,0,K,0,0,0,0,0,0,
		};
		long time;
		
		//prep
		for (int i = 0; i < 4; i++) {
			paintBmp1(srf, g, 50+i, 50+i, 16, 16, rgb);
			paintBmp2(g, 60+i, 60+i, 16, 16, rgb);
			paintBmp3(g, 70+i, 70+i, 16, 16, rgb);
			paintBmp4(srf, g, 80+i, 80+i, 16, 16, rgb);
		}
		
		g.setPaint(Color.yellow);
		g.fillRect(0, 0, 1024, 1024);
		g.setPaint(patp);
		time = -System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			paintBmp1(srf, g, (i & 0x3F) << 4, (i & 0xFC0) >>> 2, 16, 16, rgb);
		}
		time += System.currentTimeMillis();
		time /= 1L;
		System.out.println("#1 PaintContext: " + time + " us");
		ImageIO.write(bi, "png", new File("PaintBitmapTest1.png"));

		g.setPaint(Color.yellow);
		g.fillRect(0, 0, 1024, 1024);
		g.setPaint(patp);
		time = -System.currentTimeMillis();
		for (int i = 0; i < 4; i++) {
			paintBmp2(g, (i & 0x3F) << 4, (i & 0xFC0) >>> 2, 16, 16, rgb);
		}
		time += System.currentTimeMillis();
		time /= 4L;
		System.out.println("#2 FillRect: " + time + " ms");
		ImageIO.write(bi, "png", new File("PaintBitmapTest2.png"));

		g.setPaint(Color.yellow);
		g.fillRect(0, 0, 1024, 1024);
		g.setPaint(patp);
		time = -System.currentTimeMillis();
		for (int i = 0; i < 8; i++) {
			paintBmp3(g, (i & 0x3F) << 4, (i & 0xFC0) >>> 2, 16, 16, rgb);
		}
		time += System.currentTimeMillis();
		time /= 8L;
		System.out.println("#3 BitmapShape: " + time + " ms");
		ImageIO.write(bi, "png", new File("PaintBitmapTest3.png"));

		g.setPaint(Color.yellow);
		g.fillRect(0, 0, 1024, 1024);
		g.setPaint(patp);
		time = -System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			paintBmp4(srf, g, (i & 0x3F) << 4, (i & 0xFC0) >>> 2, 16, 16, rgb);
		}
		time += System.currentTimeMillis();
		time /= 1L;
		System.out.println("#4 BufferedImage: " + time + " us");
		ImageIO.write(bi, "png", new File("PaintBitmapTest4.png"));
	}
	
	public static void paintBmp1(PaintSurface srf, Graphics2D g, int x, int y, int width, int height, int[] rgb) {
		Shape clip = g.getClip();
		Paint p = g.getPaint();
		PaintContext pc = p.createContext(
				null,
				new Rectangle(x,y,width,height),
				new Rectangle(x,y,width,height),
				g.getTransform(),
				g.getRenderingHints()
		);
		ColorModel cm = pc.getColorModel();
		Raster r = pc.getRaster(x, y, width, height);
		int[] ergb = new int[width*height];
		srf.getRGB(x, y, width, height, ergb, 0, width);
		for (int ay = 0, ry = 0; ay < rgb.length; ay += width, ry++) {
			for (int ax = 0, rx = 0; ax < width; ax++, rx++) {
				int c = rgb[ay+ax];
				if (((c & 0xFF000000) < 0) && ((c & 0xFF0000) < 0x800000) && ((c & 0xFF00) < 0x8000) && ((c & 0xFF) < 0x80)) {
					if (clip == null || clip.contains(rx+x, ry+y)) {
						ergb[ay+ax] = cm.getRGB(r.getDataElements(rx, ry, null));
					}
				}
			}
		}
		srf.setRGB(x, y, width, height, ergb, 0, width);
	}
	
	public static void paintBmp2(Graphics2D g, int x, int y, int width, int height, int[] rgb) {
		for (int ay = 0, gy = y; ay < rgb.length; ay += width, gy++) {
			int w = -1;
			int e = -1;
			for (int ax = 0, gx = x; ax < width; ax++, gx++) {
				int c = rgb[ay+ax];
				if (((c & 0xFF000000) < 0) && ((c & 0xFF0000) < 0x800000) && ((c & 0xFF00) < 0x8000) && ((c & 0xFF) < 0x80)) {
					if (w < 0) w = ax;
					e = ax;
				}
				else {
					if (w >= 0 && e >= 0) {
						g.fillRect(x+w, gy, e-w+1, 1);
					}
					w = -1;
					e = -1;
				}
			}
			if (w >= 0 && e >= 0) {
				g.fillRect(x+w, gy, e-w+1, 1);
			}
		}
	}
	
	public static void paintBmp3(Graphics2D g, int x, int y, int width, int height, int[] rgb) {
		RenderingHints h = g.getRenderingHints();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g.fill(new BitmapShape(rgb, x, y, width, height));
		g.setRenderingHints(h);
	}
	
	public static void paintBmp4(PaintSurface srf, Graphics2D g, int x, int y, int width, int height, int[] rgb) {
		Paint p = g.getPaint();
		Composite c = g.getComposite();
		RenderingHints h = g.getRenderingHints();
		Shape s = g.getClip();
		AffineTransform t = g.getTransform();
		
		Rectangle b = new Rectangle(x, y, width, height);
		PaintContext pc = p.createContext(null, b, b, t, h);
		ColorModel cm = pc.getColorModel();
		Raster r = pc.getRaster(x, y, width, height);
		
		boolean fastBlit = (srf != null && (t == null || t.isIdentity()) && srf.contains(x, y, width, height));
		
		BufferedImage srci = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		int[] srca = new int[width * height];
		for (int iy = 0, gy = y, i = 0; iy < height; iy++, gy++) {
			for (int ix = 0, gx = x; ix < width; ix++, gx++, i++) {
				srca[i] = cm.getRGB(r.getDataElements(ix, iy, null));
				if (!fastBlit || s == null || s.contains(gx, gy)) {
					srca[i] = (srca[i] & 0xFFFFFF) | (((srca[i] >>> 24) * (rgb[i] >>> 24) / 255) << 24);
				} else {
					srca[i] = (srca[i] & 0xFFFFFF);
				}
			}
		}
		srci.setRGB(0, 0, width, height, srca, 0, width);
		
		if (fastBlit) {
			BufferedImage dsti = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			int[] dsta = new int[width * height];
			srf.getRGB(x, y, width, height, dsta, 0, width);
			dsti.setRGB(0, 0, width, height, dsta, 0, width);
			CompositeContext cc = c.createContext(srci.getColorModel(), dsti.getColorModel(), h);
			cc.compose(srci.getData(), dsti.getData(), dsti.getRaster());
			dsti.getRGB(0, 0, width, height, dsta, 0, width);
			srf.setRGB(x, y, width, height, dsta, 0, width);
		} else {
			g.drawImage(srci, null, x, y);
		}
	}
}
