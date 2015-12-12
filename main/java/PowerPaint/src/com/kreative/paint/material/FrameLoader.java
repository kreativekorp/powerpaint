package com.kreative.paint.material;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;
import javax.imageio.ImageIO;
import com.kreative.paint.material.frame.FRNXParser;
import com.kreative.paint.material.frame.Frame;
import com.kreative.paint.material.frame.FrameReader;

public class FrameLoader {
	private final MaterialLoader loader;
	private final MaterialList<Frame> frames;
	
	public FrameLoader(MaterialLoader loader) {
		this.loader = loader;
		this.frames = new MaterialList<Frame>();
	}
	
	public MaterialList<Frame> getFrames() {
		if (frames.isEmpty()) loadResources();
		if (frames.isEmpty()) createFrames();
		return frames;
	}
	
	private void loadResources() {
		for (MaterialResource r : loader.listResources()) {
			if (r.isFormat("png", false)) {
				if (r.getBranchName().toLowerCase().contains("frames")) {
					try {
						InputStream in = r.getInputStream();
						BufferedImage image = ImageIO.read(in);
						in.close();
						in = r.getInputStream();
						Frame frame = FrameReader.readFrame(r.getResourceName(), in, image);
						in.close();
						String name = (frame.name != null) ? frame.name : r.getResourceName();
						frames.add(name, frame);
					} catch (IOException e) {
						System.err.println("Warning: Ignoring invalid image: " + r.getResourceName());
						e.printStackTrace();
					}
				}
			} else if (r.isFormat("frnd", true)) {
				Map<String,MaterialResource> pngs = new TreeMap<String,MaterialResource>();
				Map<String,MaterialResource> frnxs = new TreeMap<String,MaterialResource>();
				for (MaterialResource child : r.listChildren()) {
					String name = child.getResourceName().replaceFirst("\\.[a-zA-Z0-9]+$", "").trim();
					if (child.isFormat("png", false)) pngs.put(name, child);
					if (child.isFormat("frnx", false)) frnxs.put(name, child);
				}
				for (Map.Entry<String,MaterialResource> e : pngs.entrySet()) {
					MaterialResource png = e.getValue();
					MaterialResource frnx = frnxs.get(e.getKey());
					try {
						InputStream in = png.getInputStream();
						BufferedImage image = ImageIO.read(in);
						in.close();
						Frame frame;
						if (frnx == null) {
							frame = new Frame(image, r.getResourceName());
						} else {
							in = frnx.getInputStream();
							frame = FRNXParser.parse(e.getKey(), in, image);
							in.close();
						}
						String name = (frame.name != null) ? frame.name : r.getResourceName();
						frames.add(name, frame);
					} catch (IOException ex) {
						System.err.println("Warning: Ignoring invalid frame: " + e.getKey());
						ex.printStackTrace();
					}
				}
			}
		}
	}
	
	private void createFrames() {
		System.err.println("Notice: No frames found. Generating generic frames.");
		BufferedImage i1 = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
		BufferedImage i2 = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
		BufferedImage i3 = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
		for (int j = 0; j < 4; j++) {
			i1.setRGB(0, j, 0xFF000000);
			i1.setRGB(3, j, 0xFF000000);
			i1.setRGB(j, 0, 0xFF000000);
			i1.setRGB(j, 3, 0xFF000000);
			i2.setRGB(0, j, 0xFF808080);
			i2.setRGB(3, j, 0xFF808080);
			i2.setRGB(j, 0, 0xFF808080);
			i2.setRGB(j, 3, 0xFF808080);
			i3.setRGB(0, j, 0xFFFFFFFF);
			i3.setRGB(3, j, 0xFFFFFFFF);
			i3.setRGB(j, 0, 0xFFFFFFFF);
			i3.setRGB(j, 3, 0xFFFFFFFF);
		}
		frames.add("Simple Black", new Frame(i1, "Simple Black"));
		frames.add("Simple Gray", new Frame(i2, "Simple Gray"));
		frames.add("Simple White", new Frame(i3, "Simple White"));
	}
}
