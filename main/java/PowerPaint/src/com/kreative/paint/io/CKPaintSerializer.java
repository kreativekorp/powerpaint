package com.kreative.paint.io;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Collection;
import java.util.zip.*;
import com.kreative.paint.*;
import com.kreative.paint.Canvas;
import com.kreative.paint.document.draw.DrawObject;
import com.kreative.paint.document.draw.PaintSettings;
import com.kreative.paint.document.draw.TextAlignment;
import com.kreative.paint.document.tile.Tile;
import com.kreative.paint.document.tile.TileSurface;

public class CKPaintSerializer extends Serializer {
	private static final int TYPE_CANVAS = fcc("Canv");
	private static final int TYPE_LAYER = fcc("Layr");
	private static final int TYPE_PAINT_SETTINGS = fcc("PtSt");
	private static final int TYPE_TILE = fcc("Tile");
	private static final int TYPE_TILE_SURFACE = fcc("TSrf");
	
	private static final int BI_TYPE_INT_ARGB = fcc("argb");
	
	protected void loadRecognizedTypesAndClasses() {
		addTypeAndClass(TYPE_CANVAS, 1, Canvas.class);
		addTypeAndClass(TYPE_LAYER, 1, Layer.class);
		addTypeAndClass(TYPE_PAINT_SETTINGS, 3, PaintSettings.class);
		addTypeAndClass(TYPE_TILE, 2, Tile.class);
		addTypeAndClass(TYPE_TILE_SURFACE, 1, TileSurface.class);
	}
	
	public void serializeObject(Object o, DataOutputStream stream) throws IOException {
		if (o instanceof Canvas) {
			Canvas v = (Canvas)o;
			stream.writeInt(v.getWidth());
			stream.writeInt(v.getHeight());
			stream.writeInt(v.getDPIX());
			stream.writeInt(v.getDPIY());
			stream.writeInt(-1);
			stream.writeInt(v.getHotspotX());
			stream.writeInt(v.getHotspotY());
			SerializationManager.writeObject(v.getPaintSelection(), stream);
			stream.writeInt(v.size());
			for (Layer l : v) {
				SerializationManager.writeObject(l, stream);
			}
		} else if (o instanceof Layer) {
			Layer v = (Layer)o;
			stream.writeUTF(v.getName());
			stream.writeBoolean(v.isVisible());
			stream.writeBoolean(v.isLocked());
			stream.writeBoolean(v.isSelected());
			stream.writeBoolean(true);
			stream.writeInt(v.getX());
			stream.writeInt(v.getY());
			stream.writeInt(v.getMatte());
			stream.writeInt(BI_TYPE_INT_ARGB);
			stream.writeInt(v.getTileSize());
			Collection<Tile> tiles = v.getTiles();
			stream.writeInt(tiles.size());
			for (Tile tile : tiles) {
				SerializationManager.writeObject(tile, stream);
			}
			stream.writeInt(v.size());
			for (DrawObject d : v) {
				SerializationManager.writeObject(d, stream);
			}
			SerializationManager.writeObject(v.getClip(), stream);
			SerializationManager.writeObject(v.getPoppedImage(), stream);
			SerializationManager.writeObject(v.getPoppedImageTransform(), stream);
		} else if (o instanceof PaintSettings) {
			PaintSettings v = (PaintSettings)o;
			SerializationManager.writeObject(v.fillPaint, stream);
			SerializationManager.writeObject(v.fillComposite, stream);
			SerializationManager.writeObject(v.fillAntiAliased, stream);
			SerializationManager.writeObject(v.drawPaint, stream);
			SerializationManager.writeObject(v.drawComposite, stream);
			SerializationManager.writeObject(v.drawStroke, stream);
			SerializationManager.writeObject(v.drawAntiAliased, stream);
			SerializationManager.writeObject(v.textFont, stream);
			SerializationManager.writeObject(v.textAlignment.awtValue, stream);
			SerializationManager.writeObject(v.textAntiAliased, stream);
		} else if (o instanceof Tile) {
			Tile v = (Tile)o;
			int x = v.getX();
			int y = v.getY();
			int width = v.getWidth();
			int height = v.getHeight();
			int matte = v.getMatte();
			stream.writeInt(x);
			stream.writeInt(y);
			stream.writeInt(width);
			stream.writeInt(height);
			stream.writeInt(matte);
			int[] rgb = new int[width * height];
			v.getRGB(x, y, width, height, rgb, 0, width);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DeflaterOutputStream dos = new DeflaterOutputStream(bos);
			for (int p : rgb) dos.write((p >> 24) & 0xFF);
			for (int p : rgb) dos.write((p >> 16) & 0xFF);
			for (int p : rgb) dos.write((p >> 8) & 0xFF);
			for (int p : rgb) dos.write(p & 0xFF);
			dos.finish();
			dos.close();
			bos.close();
			byte crgb[] = bos.toByteArray();
			stream.writeInt(crgb.length);
			stream.write(crgb);
		} else if (o instanceof TileSurface) {
			TileSurface v = (TileSurface)o;
			stream.writeInt(v.getX());
			stream.writeInt(v.getY());
			stream.writeInt(v.getTileWidth());
			stream.writeInt(v.getTileHeight());
			stream.writeInt(v.getMatte());
			Collection<Tile> tiles = v.getTiles();
			stream.writeInt(tiles.size());
			for (Tile t : tiles) SerializationManager.writeObject(t, stream);
		}
	}
	
	public Object deserializeObject(int type, int version, DataInputStream stream) throws IOException {
		if (type == TYPE_CANVAS) {
			if (version != 1) throw new IOException("Invalid version number.");
			int w = stream.readInt();
			int h = stream.readInt();
			int dx = stream.readInt();
			int dy = stream.readInt();
			Canvas o = new Canvas(w,h,dx,dy);
			o.clear();
			int n = stream.readInt();
			if (n == -1) {
				int hsx = stream.readInt();
				int hsy = stream.readInt();
				Shape s = (Shape)SerializationManager.readObject(stream);
				o.setHotspot(hsx, hsy);
				o.setPaintSelection(s);
				n = stream.readInt();
			} else {
				o.setHotspot(0, 0);
				o.setPaintSelection(null);
			}
			for (int i = 0; i < n; i++) {
				Layer l = (Layer)SerializationManager.readObject(stream);
				o.add(l);
			}
			return o;
		} else if (type == TYPE_LAYER) {
			if (version != 1) throw new IOException("Invalid version number.");
			String name = stream.readUTF();
			boolean vis = stream.readBoolean();
			boolean lock = stream.readBoolean();
			boolean sel = stream.readBoolean();
			boolean v2 = stream.readBoolean();
			int x = stream.readInt();
			int y = stream.readInt();
			int matte = stream.readInt();
			stream.readInt();
			int tsize = stream.readInt();
			Layer l = new Layer(tsize, matte);
			l.setName(name);
			l.setVisible(vis);
			l.setLocked(lock);
			l.setSelected(sel);
			l.setX(x);
			l.setY(y);
			int ntiles = stream.readInt();
			for (int i = 0; i < ntiles; i++) {
				Tile tile = (Tile)SerializationManager.readObject(stream);
				l.addTile(tile);
			}
			int nobjs = stream.readInt();
			for (int i = 0; i < nobjs; i++) {
				DrawObject obj = (DrawObject)SerializationManager.readObject(stream);
				l.add(obj);
			}
			if (v2) {
				Shape clip = (Shape)SerializationManager.readObject(stream);
				BufferedImage pimg = (BufferedImage)SerializationManager.readObject(stream);
				AffineTransform ptx = (AffineTransform)SerializationManager.readObject(stream);
				l.setClip(clip);
				l.setPoppedImage(pimg, ptx);
			} else {
				l.setClip(null);
				l.setPoppedImage(null, null);
			}
			return l;
		} else if (type == TYPE_PAINT_SETTINGS) {
			if (version < 1 || version > 3) throw new IOException("Invalid version number.");
			if (version < 3) {
				Composite dc = (Composite)SerializationManager.readObject(stream);
				Paint dp = (Paint)SerializationManager.readObject(stream);
				Composite fc = (Composite)SerializationManager.readObject(stream);
				Paint fp = (Paint)SerializationManager.readObject(stream);
				Stroke st = (Stroke)SerializationManager.readObject(stream);
				Font fn = (Font)SerializationManager.readObject(stream);
				TextAlignment ta = TextAlignment.forAWTValue((Integer)SerializationManager.readObject(stream));
				boolean aa = (version > 1) ? (Boolean)SerializationManager.readObject(stream) : false;
				return new PaintSettings(fp, fc, aa, dp, dc, st, aa, fn, ta, aa);
			} else {
				Paint fp = (Paint)SerializationManager.readObject(stream);
				Composite fc = (Composite)SerializationManager.readObject(stream);
				boolean fa = (Boolean)SerializationManager.readObject(stream);
				Paint dp = (Paint)SerializationManager.readObject(stream);
				Composite dc = (Composite)SerializationManager.readObject(stream);
				Stroke ds = (Stroke)SerializationManager.readObject(stream);
				boolean da = (Boolean)SerializationManager.readObject(stream);
				Font tf = (Font)SerializationManager.readObject(stream);
				TextAlignment ta = TextAlignment.forAWTValue((Integer)SerializationManager.readObject(stream));
				boolean taa = (Boolean)SerializationManager.readObject(stream);
				return new PaintSettings(fp, fc, fa, dp, dc, ds, da, tf, ta, taa);
			}
		} else if (type == TYPE_TILE) {
			if (version < 1 || version > 2) throw new IOException("Invalid version number.");
			int x = stream.readInt();
			int y = stream.readInt();
			int width = stream.readInt();
			int height = stream.readInt();
			int matte = stream.readInt();
			int clen = stream.readInt();
			byte[] crgb = new byte[clen];
			stream.read(crgb);
			int[] rgb = new int[width * height];
			ByteArrayInputStream bis = new ByteArrayInputStream(crgb);
			InflaterInputStream iis = new InflaterInputStream(bis);
			for (int i = 0; i < rgb.length; i++) rgb[i] |= ((iis.read() << 24) & 0xFF000000);
			for (int i = 0; i < rgb.length; i++) rgb[i] |= ((iis.read() << 16) & 0x00FF0000);
			for (int i = 0; i < rgb.length; i++) rgb[i] |= ((iis.read() << 8) & 0xFF00);
			for (int i = 0; i < rgb.length; i++) rgb[i] |= (iis.read() & 0xFF);
			iis.close();
			bis.close();
			Tile t = new Tile(x, y, width, height, (version < 2) ? 0x00FFFFFF : matte);
			t.setRGB(x, y, width, height, rgb, 0, width);
			return t;
		} else if (type == TYPE_TILE_SURFACE) {
			if (version != 1) throw new IOException("Invalid version number.");
			int x = stream.readInt();
			int y = stream.readInt();
			int width = stream.readInt();
			int height = stream.readInt();
			int matte = stream.readInt();
			int tiles = stream.readInt();
			TileSurface ts = new TileSurface(x, y, width, height, matte);
			for (int i = 0; i < tiles; i++) {
				Tile t = (Tile)SerializationManager.readObject(stream);
				ts.addTile(t);
			}
			return ts;
		} else {
			return null;
		}
	}
}
