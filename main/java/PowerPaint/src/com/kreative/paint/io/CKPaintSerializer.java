/*
 * Copyright &copy; 2009-2011 Rebecca G. Bettencourt / Kreative Software
 * <p>
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * <a href="http://www.mozilla.org/MPL/">http://www.mozilla.org/MPL/</a>
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Alternatively, the contents of this file may be used under the terms
 * of the GNU Lesser General Public License (the "LGPL License"), in which
 * case the provisions of LGPL License are applicable instead of those
 * above. If you wish to allow use of your version of this file only
 * under the terms of the LGPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the LGPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the LGPL License.
 * @since PowerPaint 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.paint.io;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Collection;
import java.util.zip.*;
import com.kreative.paint.*;
import com.kreative.paint.Canvas;
import com.kreative.paint.draw.DrawObject;

public class CKPaintSerializer extends Serializer {
	private static final int TYPE_CANVAS = fcc("Canv");
	private static final int TYPE_LAYER = fcc("Layr");
	private static final int TYPE_PAINT_SETTINGS = fcc("PtSt");
	private static final int TYPE_TILE = fcc("Tile");
	
	private static final int BI_TYPE_INT_ARGB = fcc("argb");
	
	protected void loadRecognizedTypesAndClasses() {
		addTypeAndClass(TYPE_CANVAS, 1, Canvas.class);
		addTypeAndClass(TYPE_LAYER, 1, Layer.class);
		addTypeAndClass(TYPE_PAINT_SETTINGS, 2, PaintSettings.class);
		addTypeAndClass(TYPE_TILE, 1, Tile.class);
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
		}
		else if (o instanceof Layer) {
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
		}
		else if (o instanceof PaintSettings) {
			PaintSettings v = (PaintSettings)o;
			SerializationManager.writeObject(v.getDrawComposite(), stream);
			SerializationManager.writeObject(v.getDrawPaint(), stream);
			SerializationManager.writeObject(v.getFillComposite(), stream);
			SerializationManager.writeObject(v.getFillPaint(), stream);
			SerializationManager.writeObject(v.getStroke(), stream);
			SerializationManager.writeObject(v.getFont(), stream);
			SerializationManager.writeObject(v.getTextAlignment(), stream);
			SerializationManager.writeObject(v.isAntiAliased(), stream);
		}
		else if (o instanceof Tile) {
			Tile v = (Tile)o;
			stream.writeInt(v.getX());
			stream.writeInt(v.getY());
			stream.writeInt(v.getWidth());
			stream.writeInt(v.getHeight());
			stream.writeInt(BI_TYPE_INT_ARGB);
			int[] rgb = new int[v.getWidth()*v.getHeight()];
			v.getRGB(0, 0, v.getWidth(), v.getHeight(), rgb, 0, v.getWidth());
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
		}
	}
	
	public Object deserializeObject(int type, int version, DataInputStream stream) throws IOException {
		if (!(version == 1 || (type == TYPE_PAINT_SETTINGS && version == 2))) throw new IOException("Invalid version number.");
		else if (type == TYPE_CANVAS) {
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
		}
		else if (type == TYPE_LAYER) {
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
			Layer l = new Layer(tsize);
			l.setName(name);
			l.setVisible(vis);
			l.setLocked(lock);
			l.setSelected(sel);
			l.setX(x);
			l.setY(y);
			l.setMatte(matte);
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
		}
		else if (type == TYPE_PAINT_SETTINGS) {
			Composite dc = (Composite)SerializationManager.readObject(stream);
			Paint dp = (Paint)SerializationManager.readObject(stream);
			Composite fc = (Composite)SerializationManager.readObject(stream);
			Paint fp = (Paint)SerializationManager.readObject(stream);
			Stroke st = (Stroke)SerializationManager.readObject(stream);
			Font fn = (Font)SerializationManager.readObject(stream);
			int ta = (Integer)SerializationManager.readObject(stream);
			boolean aa = (version > 1) ? (Boolean)SerializationManager.readObject(stream) : false;
			return new PaintSettings(dc,dp,fc,fp,st,fn,ta,aa);
		}
		else if (type == TYPE_TILE) {
			int x = stream.readInt();
			int y = stream.readInt();
			int w = stream.readInt();
			int h = stream.readInt();
			stream.readInt();
			int l = stream.readInt();
			byte[] crgb = new byte[l];
			stream.read(crgb);
			int[] rgb = new int[w*h];
			ByteArrayInputStream bis = new ByteArrayInputStream(crgb);
			InflaterInputStream iis = new InflaterInputStream(bis);
			for (int i = 0; i < rgb.length; i++) rgb[i] |= ((iis.read() << 24) & 0xFF000000);
			for (int i = 0; i < rgb.length; i++) rgb[i] |= ((iis.read() << 16) & 0x00FF0000);
			for (int i = 0; i < rgb.length; i++) rgb[i] |= ((iis.read() << 8) & 0xFF00);
			for (int i = 0; i < rgb.length; i++) rgb[i] |= (iis.read() & 0xFF);
			iis.close();
			bis.close();
			Tile bi = new Tile(x,y,w,h);
			bi.setRGB(0, 0, w, h, rgb, 0, w);
			return bi;
		}
		else return null;
	}
}
