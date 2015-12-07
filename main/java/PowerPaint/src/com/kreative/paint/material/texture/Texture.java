package com.kreative.paint.material.texture;

import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

public class Texture {
	public final String name;
	public final BufferedImage image;
	public final TexturePaint paint;
	
	public Texture(String name, BufferedImage image) {
		this.name = name;
		this.image = image;
		int w = image.getWidth();
		int h = image.getHeight();
		Rectangle r = new Rectangle(0, 0, w, h);
		this.paint = new TexturePaint(image, r);
	}
	
	@Override
	public boolean equals(Object that) {
		if (that instanceof Texture) {
			return this.equals((Texture)that, false);
		} else {
			return false;
		}
	}
	
	public boolean equals(Texture that, boolean withName) {
		if (!this.image.equals(that.image)) return false;
		if (!withName) return true;
		if (this.name != null) return this.name.equals(that.name);
		if (that.name != null) return that.name.equals(this.name);
		return true;
	}
	
	@Override
	public int hashCode() {
		return image.hashCode();
	}
}
