package com.kreative.paint.material;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import com.kreative.paint.material.texture.Texture;
import com.kreative.paint.material.texture.TextureList;
import com.kreative.paint.material.texture.TextureParser;

public class TextureLoader {
	private final MaterialLoader loader;
	private final MaterialList<MaterialList<TexturePaint>> textures;
	
	public TextureLoader(MaterialLoader loader) {
		this.loader = loader;
		this.textures = new MaterialList<MaterialList<TexturePaint>>();
	}
	
	public MaterialList<MaterialList<TexturePaint>> getTextures() {
		if (textures.isEmpty()) loadResources();
		if (textures.isEmpty()) createTextures();
		return textures;
	}
	
	private void loadResources() {
		for (MaterialResource r : loader.listResources()) {
			if (r.isFormat("png", false)) {
				if (r.getBranchName().toLowerCase().contains("textures")) {
					try {
						InputStream in = r.getInputStream();
						BufferedImage image = ImageIO.read(in);
						in.close();
						String name = r.getParentName();
						Texture t = new Texture(r.getResourceName(), image);
						if (textures.containsName(name)) {
							MaterialList<TexturePaint> category = textures.getValue(name);
							category.add(t.name, t.paint);
						} else {
							MaterialList<TexturePaint> category = new MaterialList<TexturePaint>();
							category.add(t.name, t.paint);
							textures.add(name, category);
						}
					} catch (IOException e) {
						System.err.println("Warning: Ignoring invalid image: " + r.getResourceName());
						e.printStackTrace();
					}
				}
			} else if (r.isFormat("txrd", true)) {
				ResourceHrefResolver hr = new ResourceHrefResolver(r);
				for (MaterialResource child : r.listChildren()) {
					if (child.isFormat("txrx", false)) {
						try {
							InputStream in = child.getInputStream();
							TextureList list = TextureParser.parse(hr, r.getResourceName(), in);
							in.close();
							String name = (list.name != null) ? list.name : r.getResourceName();
							if (textures.containsName(name)) {
								MaterialList<TexturePaint> category = textures.getValue(name);
								for (Texture t : list) category.add(t.name, t.paint);
							} else {
								MaterialList<TexturePaint> category = new MaterialList<TexturePaint>();
								for (Texture t : list) category.add(t.name, t.paint);
								textures.add(name, category);
							}
						} catch (IOException e) {
							System.err.println("Warning: Failed to compile texture list " + r.getResourceName() + ".");
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	private static class ResourceHrefResolver implements TextureParser.HrefResolver {
		private final MaterialResource root;
		public ResourceHrefResolver(MaterialResource root) {
			this.root = root;
		}
		@Override
		public BufferedImage resolveHref(String href) throws IOException {
			MaterialResource child = root.getChild(href);
			InputStream in = child.getInputStream();
			BufferedImage image = ImageIO.read(in);
			in.close();
			return image;
		}
	}
	
	private void createTextures() {
		System.err.println("Notice: No textures found. Generating generic textures.");
		BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, 8, 8);
		g.fillRect(8, 8, 8, 8);
		g.setColor(Color.white);
		g.fillRect(8, 0, 8, 8);
		g.fillRect(0, 8, 8, 8);
		g.dispose();
		Texture t = new Texture("Checkerboard", image);
		MaterialList<TexturePaint> category = new MaterialList<TexturePaint>();
		category.add(t.name, t.paint);
		textures.add("Simple", category);
	}
}
