/*
 * Copyright &copy; 2009-2010 Rebecca G. Bettencourt / Kreative Software
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
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.image.renderable.RenderableImage;
import java.io.*;
import com.kreative.paint.PaintSettings;
import com.kreative.paint.draw.*;
import com.kreative.paint.powerbrush.BrushSettings;
import com.kreative.paint.util.Bitmap;
import com.kreative.paint.util.ImageUtils;

public class CKPObjectsSerializer extends Serializer {
	private static final int TYPE_BRUSH_STROKE_DO = fcc("dBSt");
	private static final int TYPE_CONTROL_POINT_DOUBLE = fcc("CPoD");
	private static final int TYPE_CONTROL_POINT_FLOAT = fcc("CPoF");
	private static final int TYPE_CONTROL_POINT_TYPE = fcc("CPoT");
	private static final int TYPE_CROP_MARK_DO = fcc("dCMk");
	private static final int TYPE_GRID_DO = fcc("dGrd");
	private static final int TYPE_GROUP_DO = fcc("dGrp");
	private static final int TYPE_IMAGE_DO = fcc("dImg");
	private static final int TYPE_PENCIL_STROKE_DO = fcc("dPSt");
	private static final int TYPE_PERSPECTIVE_GRID_DO = fcc("dPGr");
	private static final int TYPE_POWERBRUSH_STROKE_DO = fcc("dPBS");
	private static final int TYPE_QUICKSHADOW_DO = fcc("dQSh");
	private static final int TYPE_SHAPE_DO = fcc("dShp");
	private static final int TYPE_TEXT_DO = fcc("dTxt");
	private static final int TYPE_THREEDBOX_DO = fcc("d3DB");
	
	private static final int IDO_TYPE_IMAGE = fcc("gnrl");
	private static final int IDO_TYPE_BUFFERED_IMAGE = fcc("bfrd");
	private static final int IDO_TYPE_RENDERABLE_IMAGE = fcc("rdbl");
	private static final int IDO_TYPE_RENDERED_IMAGE = fcc("rdrd");
	private static final int IDO_TYPE_NONE = fcc("null");
	
	protected void loadRecognizedTypesAndClasses() {
		addTypeAndClass(TYPE_BRUSH_STROKE_DO, 1, BrushStrokeDrawObject.class);
		addTypeAndClass(TYPE_CONTROL_POINT_DOUBLE, 1, ControlPoint.Double.class);
		addTypeAndClass(TYPE_CONTROL_POINT_FLOAT, 1, ControlPoint.Float.class);
		addTypeAndClass(TYPE_CONTROL_POINT_TYPE, 1, ControlPointType.class);
		addTypeAndClass(TYPE_CROP_MARK_DO, 1, CropMarkDrawObject.class);
		addTypeAndClass(TYPE_GRID_DO, 1, GridDrawObject.class);
		addTypeAndClass(TYPE_GROUP_DO, 1, GroupDrawObject.class);
		addTypeAndClass(TYPE_IMAGE_DO, 1, ImageDrawObject.class);
		addTypeAndClass(TYPE_PENCIL_STROKE_DO, 1, PencilStrokeDrawObject.class);
		addTypeAndClass(TYPE_PERSPECTIVE_GRID_DO, 1, PerspectiveGridDrawObject.class);
		addTypeAndClass(TYPE_POWERBRUSH_STROKE_DO, 1, PowerBrushStrokeDrawObject.class);
		addTypeAndClass(TYPE_QUICKSHADOW_DO, 1, QuickShadowDrawObject.class);
		addTypeAndClass(TYPE_SHAPE_DO, 1, ShapeDrawObject.class);
		addTypeAndClass(TYPE_TEXT_DO, 1, TextDrawObject.class);
		addTypeAndClass(TYPE_THREEDBOX_DO, 1, ThreeDBoxDrawObject.class);
	}
	
	public void serializeObject(Object o, DataOutputStream stream) throws IOException {
		if (o instanceof BrushStrokeDrawObject) {
			BrushStrokeDrawObject v = (BrushStrokeDrawObject)o;
			SerializationManager.writeObject(v.getPaintSettings(), stream);
			SerializationManager.writeObject(v.getTransform(), stream);
			stream.writeBoolean(!v.isVisible());
			stream.writeBoolean(v.isLocked());
			stream.writeBoolean(v.isSelected());
			stream.writeByte(0);
			SerializationManager.writeObject(v.getBrush(), stream);
			SerializationManager.writeObject(v.getPath(), stream);
		}
		else if (o instanceof ControlPoint.Double) {
			ControlPoint.Double v = (ControlPoint.Double)o;
			stream.writeDouble(v.x);
			stream.writeDouble(v.y);
			stream.writeInt(v.type.getSerializedForm());
		}
		else if (o instanceof ControlPoint.Float) {
			ControlPoint.Float v = (ControlPoint.Float)o;
			stream.writeFloat(v.x);
			stream.writeFloat(v.y);
			stream.writeInt(v.type.getSerializedForm());
		}
		else if (o instanceof ControlPointType) {
			ControlPointType v = (ControlPointType)o;
			stream.writeInt(v.getSerializedForm());
		}
		else if (o instanceof CropMarkDrawObject) {
			CropMarkDrawObject v = (CropMarkDrawObject)o;
			SerializationManager.writeObject(v.getPaintSettings(), stream);
			SerializationManager.writeObject(v.getTransform(), stream);
			stream.writeBoolean(!v.isVisible());
			stream.writeBoolean(v.isLocked());
			stream.writeBoolean(v.isSelected());
			stream.writeByte(0);
			stream.writeFloat(v.getX1());
			stream.writeFloat(v.getY1());
			stream.writeFloat(v.getX2());
			stream.writeFloat(v.getY2());
			stream.writeInt(v.getHorizDivisions());
			stream.writeInt(v.getVertDivisions());
		}
		else if (o instanceof GridDrawObject) {
			GridDrawObject v = (GridDrawObject)o;
			SerializationManager.writeObject(v.getPaintSettings(), stream);
			SerializationManager.writeObject(v.getTransform(), stream);
			stream.writeBoolean(!v.isVisible());
			stream.writeBoolean(v.isLocked());
			stream.writeBoolean(v.isSelected());
			stream.writeByte(0);
			Rectangle2D b = v.getGridBounds();
			stream.writeFloat((float)b.getX());
			stream.writeFloat((float)b.getY());
			stream.writeFloat((float)b.getWidth());
			stream.writeFloat((float)b.getHeight());
			stream.writeInt(v.getHorizGridType());
			stream.writeFloat(v.getHorizGridSpacing());
			stream.writeInt(v.getVertGridType());
			stream.writeFloat(v.getVertGridSpacing());
		}
		else if (o instanceof GroupDrawObject) {
			GroupDrawObject v = (GroupDrawObject)o;
			SerializationManager.writeObject(v.getPaintSettings(), stream);
			SerializationManager.writeObject(v.getTransform(), stream);
			stream.writeBoolean(!v.isVisible());
			stream.writeBoolean(v.isLocked());
			stream.writeBoolean(v.isSelected());
			stream.writeByte(0);
			stream.writeInt(v.size());
			for (DrawObject d : v) {
				SerializationManager.writeObject(d, stream);
			}
		}
		else if (o instanceof ImageDrawObject) {
			ImageDrawObject v = (ImageDrawObject)o;
			SerializationManager.writeObject(v.getPaintSettings(), stream);
			SerializationManager.writeObject(v.getTransform(), stream);
			stream.writeBoolean(!v.isVisible());
			stream.writeBoolean(v.isLocked());
			stream.writeBoolean(v.isSelected());
			stream.writeByte(0);
			Rectangle b = v.getBoundingRect();
			stream.writeInt(b.x);
			stream.writeInt(b.y);
			stream.writeInt(b.width);
			stream.writeInt(b.height);
			Rectangle i = v.getInnerBoundingRect();
			stream.writeInt(i.x);
			stream.writeInt(i.y);
			stream.writeInt(i.width);
			stream.writeInt(i.height);
			if (v.getImage() != null) {
				stream.writeInt(IDO_TYPE_IMAGE);
				BufferedImage bimg = ImageUtils.toBufferedImage(v.getImage(), false);
				SerializationManager.writeObject(bimg, stream);
			}
			else if (v.getBufferedImage() != null && v.getBufferedImageOp() != null) {
				stream.writeInt(IDO_TYPE_BUFFERED_IMAGE);
				SerializationManager.writeObject(v.getBufferedImage(), stream);
				SerializationManager.writeObject(v.getBufferedImageOp(), stream); // this WILL fail until we can serialize BufferedImageOps
			}
			else if (v.getBufferedImage() != null) {
				stream.writeInt(IDO_TYPE_IMAGE);
				SerializationManager.writeObject(v.getBufferedImage(), stream);
			}
			else if (v.getRenderableImage() != null) {
				stream.writeInt(IDO_TYPE_RENDERABLE_IMAGE);
				SerializationManager.writeObject(v.getRenderableImage(), stream); // this WILL fail until we can serialize RenderableImages
			}
			else if (v.getRenderedImage() != null) {
				stream.writeInt(IDO_TYPE_RENDERED_IMAGE);
				SerializationManager.writeObject(v.getRenderedImage(), stream); // this WILL fail until we can serialize RenderedImages
			}
			else {
				stream.writeInt(IDO_TYPE_NONE);
			}
		}
		else if (o instanceof PencilStrokeDrawObject) {
			PencilStrokeDrawObject v = (PencilStrokeDrawObject)o;
			SerializationManager.writeObject(v.getPaintSettings(), stream);
			SerializationManager.writeObject(v.getTransform(), stream);
			stream.writeBoolean(!v.isVisible());
			stream.writeBoolean(v.isLocked());
			stream.writeBoolean(v.isSelected());
			stream.writeByte(0);
			SerializationManager.writeObject(v.getPath(), stream);
		}
		else if (o instanceof PerspectiveGridDrawObject) {
			PerspectiveGridDrawObject v = (PerspectiveGridDrawObject)o;
			SerializationManager.writeObject(v.getPaintSettings(), stream);
			SerializationManager.writeObject(v.getTransform(), stream);
			stream.writeBoolean(!v.isVisible());
			stream.writeBoolean(v.isLocked());
			stream.writeBoolean(v.isSelected());
			stream.writeByte(0);
			Rectangle2D b = v.getGridBounds();
			stream.writeFloat((float)b.getX());
			stream.writeFloat((float)b.getY());
			stream.writeFloat((float)b.getWidth());
			stream.writeFloat((float)b.getHeight());
			stream.writeInt(v.getGridWidthTop());
			stream.writeInt(v.getGridWidthBottom());
			stream.writeInt(v.getGridHeight());
		}
		else if (o instanceof PowerBrushStrokeDrawObject) {
			PowerBrushStrokeDrawObject v = (PowerBrushStrokeDrawObject)o;
			SerializationManager.writeObject(v.getPaintSettings(), stream);
			SerializationManager.writeObject(v.getTransform(), stream);
			stream.writeBoolean(!v.isVisible());
			stream.writeBoolean(v.isLocked());
			stream.writeBoolean(v.isSelected());
			stream.writeByte(0);
			SerializationManager.writeObject(v.getBrush(), stream);
			SerializationManager.writeObject(v.getPath(), stream);
		}
		else if (o instanceof QuickShadowDrawObject) {
			QuickShadowDrawObject v = (QuickShadowDrawObject)o;
			SerializationManager.writeObject(v.getPaintSettings(), stream);
			SerializationManager.writeObject(v.getTransform(), stream);
			stream.writeBoolean(!v.isVisible());
			stream.writeBoolean(v.isLocked());
			stream.writeBoolean(v.isSelected());
			stream.writeByte(0);
			stream.writeInt(v.getShadowType());
			stream.writeInt(v.getShadowOpacity());
			stream.writeInt(v.getXOffset());
			stream.writeInt(v.getYOffset());
			SerializationManager.writeObject(v.getOriginalShape(), stream);
		}
		else if (o instanceof ShapeDrawObject) {
			ShapeDrawObject v = (ShapeDrawObject)o;
			SerializationManager.writeObject(v.getPaintSettings(), stream);
			SerializationManager.writeObject(v.getTransform(), stream);
			stream.writeBoolean(!v.isVisible());
			stream.writeBoolean(v.isLocked());
			stream.writeBoolean(v.isSelected());
			stream.writeByte(0);
			SerializationManager.writeObject(v.getOriginalShape(), stream);
		}
		else if (o instanceof TextDrawObject) {
			TextDrawObject v = (TextDrawObject)o;
			SerializationManager.writeObject(v.getPaintSettings(), stream);
			SerializationManager.writeObject(v.getTransform(), stream);
			stream.writeBoolean(!v.isVisible());
			stream.writeBoolean(v.isLocked());
			stream.writeBoolean(v.isSelected());
			stream.writeByte(0);
			stream.writeFloat(v.getX());
			stream.writeFloat(v.getY());
			stream.writeFloat(v.getWrapWidth());
			stream.writeInt(v.getCursorStart());
			stream.writeInt(v.getCursorEnd());
			stream.writeUTF(v.getText());
		}
		else if (o instanceof ThreeDBoxDrawObject) {
			ThreeDBoxDrawObject v = (ThreeDBoxDrawObject)o;
			SerializationManager.writeObject(v.getPaintSettings(), stream);
			SerializationManager.writeObject(v.getTransform(), stream);
			stream.writeBoolean(!v.isVisible());
			stream.writeBoolean(v.isLocked());
			stream.writeBoolean(v.isSelected());
			stream.writeByte(0);
			Rectangle2D f = v.getFrame();
			stream.writeDouble(f.getX());
			stream.writeDouble(f.getY());
			stream.writeDouble(f.getWidth());
			stream.writeDouble(f.getHeight());
			stream.writeDouble(v.getDX());
			stream.writeDouble(v.getDY());
		}
	}
	
	public Object deserializeObject(int type, int version, DataInputStream stream) throws IOException {
		if (version != 1) throw new IOException("Invalid version number.");
		else if (type == TYPE_BRUSH_STROKE_DO) {
			PaintSettings ps = (PaintSettings)SerializationManager.readObject(stream);
			AffineTransform tx = (AffineTransform)SerializationManager.readObject(stream);
			boolean vis = !stream.readBoolean();
			boolean lock = stream.readBoolean();
			boolean sel = stream.readBoolean();
			stream.readByte();
			Object tmp = SerializationManager.readObject(stream);
			Bitmap br = (tmp instanceof BufferedImage) ? new Bitmap((BufferedImage)tmp) : (Bitmap)tmp;
			GeneralPath pa = (GeneralPath)SerializationManager.readObject(stream);
			BrushStrokeDrawObject o = new BrushStrokeDrawObject(br, pa, ps);
			o.setTransform(tx);
			o.setVisible(vis);
			o.setLocked(lock);
			o.setSelected(sel);
			return o;
		}
		else if (type == TYPE_CONTROL_POINT_DOUBLE) {
			double x = stream.readDouble();
			double y = stream.readDouble();
			int t = stream.readInt();
			ControlPointType ty = ControlPointType.forSerializedForm(t);
			if (ty == null) ty = ControlPointType.GENERIC;
			return new ControlPoint.Double(x,y,ty);
		}
		else if (type == TYPE_CONTROL_POINT_FLOAT) {
			float x = stream.readFloat();
			float y = stream.readFloat();
			int t = stream.readInt();
			ControlPointType ty = ControlPointType.forSerializedForm(t);
			if (ty == null) ty = ControlPointType.GENERIC;
			return new ControlPoint.Float(x,y,ty);
		}
		else if (type == TYPE_CONTROL_POINT_TYPE) {
			int t = stream.readInt();
			ControlPointType ty = ControlPointType.forSerializedForm(t);
			if (ty == null) ty = ControlPointType.GENERIC;
			return ty;
		}
		else if (type == TYPE_CROP_MARK_DO) {
			PaintSettings ps = (PaintSettings)SerializationManager.readObject(stream);
			AffineTransform tx = (AffineTransform)SerializationManager.readObject(stream);
			boolean vis = !stream.readBoolean();
			boolean lock = stream.readBoolean();
			boolean sel = stream.readBoolean();
			stream.readByte();
			float x1 = stream.readFloat();
			float y1 = stream.readFloat();
			float x2 = stream.readFloat();
			float y2 = stream.readFloat();
			int hd = stream.readInt();
			int vd = stream.readInt();
			CropMarkDrawObject o = new CropMarkDrawObject(x1,y1,x2,y2,hd,vd,ps);
			o.setTransform(tx);
			o.setVisible(vis);
			o.setLocked(lock);
			o.setSelected(sel);
			return o;
		}
		else if (type == TYPE_GRID_DO) {
			PaintSettings ps = (PaintSettings)SerializationManager.readObject(stream);
			AffineTransform tx = (AffineTransform)SerializationManager.readObject(stream);
			boolean vis = !stream.readBoolean();
			boolean lock = stream.readBoolean();
			boolean sel = stream.readBoolean();
			stream.readByte();
			float x = stream.readFloat();
			float y = stream.readFloat();
			float w = stream.readFloat();
			float h = stream.readFloat();
			int hgt = stream.readInt();
			float hgs = stream.readFloat();
			int vgt = stream.readInt();
			float vgs = stream.readFloat();
			GridDrawObject o = new GridDrawObject(x,y,w,h,hgt,hgs,vgt,vgs,ps);
			o.setTransform(tx);
			o.setVisible(vis);
			o.setLocked(lock);
			o.setSelected(sel);
			return o;
		}
		else if (type == TYPE_GROUP_DO) {
			PaintSettings ps = (PaintSettings)SerializationManager.readObject(stream);
			AffineTransform tx = (AffineTransform)SerializationManager.readObject(stream);
			boolean vis = !stream.readBoolean();
			boolean lock = stream.readBoolean();
			boolean sel = stream.readBoolean();
			stream.readByte();
			GroupDrawObject gr = new GroupDrawObject();
			gr.setPaintSettings(ps);
			gr.setTransform(tx);
			gr.setVisible(vis);
			gr.setLocked(lock);
			gr.setSelected(sel);
			int nobjs = stream.readInt();
			for (int i = 0; i < nobjs; i++) {
				DrawObject obj = (DrawObject)SerializationManager.readObject(stream);
				gr.add(obj);
			}
			return gr;
		}
		else if (type == TYPE_IMAGE_DO) {
			PaintSettings ps = (PaintSettings)SerializationManager.readObject(stream);
			AffineTransform tx = (AffineTransform)SerializationManager.readObject(stream);
			boolean vis = !stream.readBoolean();
			boolean lock = stream.readBoolean();
			boolean sel = stream.readBoolean();
			stream.readByte();
			int bx = stream.readInt();
			int by = stream.readInt();
			int bw = stream.readInt();
			int bh = stream.readInt();
			Rectangle b = new Rectangle(bx, by, bw, bh);
			int ix = stream.readInt();
			int iy = stream.readInt();
			int iw = stream.readInt();
			int ih = stream.readInt();
			Rectangle i = new Rectangle(ix, iy, iw, ih);
			int t = stream.readInt();
			if (t == IDO_TYPE_IMAGE) {
				Image img = (Image)SerializationManager.readObject(stream);
				ImageDrawObject o = new ImageDrawObject(ps, tx, b, i, img);
				o.setVisible(vis);
				o.setLocked(lock);
				o.setSelected(sel);
				return o;
			}
			else if (t == IDO_TYPE_BUFFERED_IMAGE) {
				BufferedImage img = (BufferedImage)SerializationManager.readObject(stream);
				BufferedImageOp op = (BufferedImageOp)SerializationManager.readObject(stream);
				ImageDrawObject o = new ImageDrawObject(ps, tx, b, i, img, op);
				o.setVisible(vis);
				o.setLocked(lock);
				o.setSelected(sel);
				return o;
			}
			else if (t == IDO_TYPE_RENDERABLE_IMAGE) {
				RenderableImage img = (RenderableImage)SerializationManager.readObject(stream);
				ImageDrawObject o = new ImageDrawObject(ps, tx, b, i, img);
				o.setVisible(vis);
				o.setLocked(lock);
				o.setSelected(sel);
				return o;
			}
			else if (t == IDO_TYPE_RENDERED_IMAGE) {
				RenderedImage img = (RenderedImage)SerializationManager.readObject(stream);
				ImageDrawObject o = new ImageDrawObject(ps, tx, b, i, img);
				o.setVisible(vis);
				o.setLocked(lock);
				o.setSelected(sel);
				return o;
			}
			else return null;
		}
		else if (type == TYPE_PENCIL_STROKE_DO) {
			PaintSettings ps = (PaintSettings)SerializationManager.readObject(stream);
			AffineTransform tx = (AffineTransform)SerializationManager.readObject(stream);
			boolean vis = !stream.readBoolean();
			boolean lock = stream.readBoolean();
			boolean sel = stream.readBoolean();
			stream.readByte();
			GeneralPath pa = (GeneralPath)SerializationManager.readObject(stream);
			PencilStrokeDrawObject o = new PencilStrokeDrawObject(pa, ps);
			o.setTransform(tx);
			o.setVisible(vis);
			o.setLocked(lock);
			o.setSelected(sel);
			return o;
		}
		else if (type == TYPE_PERSPECTIVE_GRID_DO) {
			PaintSettings ps = (PaintSettings)SerializationManager.readObject(stream);
			AffineTransform tx = (AffineTransform)SerializationManager.readObject(stream);
			boolean vis = !stream.readBoolean();
			boolean lock = stream.readBoolean();
			boolean sel = stream.readBoolean();
			stream.readByte();
			float x = stream.readFloat();
			float y = stream.readFloat();
			float w = stream.readFloat();
			float h = stream.readFloat();
			int nt = stream.readInt();
			int nb = stream.readInt();
			int nh = stream.readInt();
			PerspectiveGridDrawObject o = new PerspectiveGridDrawObject(ps,x,y,w,h,nt,nb,nh);
			o.setTransform(tx);
			o.setVisible(vis);
			o.setLocked(lock);
			o.setSelected(sel);
			return o;
		}
		else if (type == TYPE_POWERBRUSH_STROKE_DO) {
			PaintSettings ps = (PaintSettings)SerializationManager.readObject(stream);
			AffineTransform tx = (AffineTransform)SerializationManager.readObject(stream);
			boolean vis = !stream.readBoolean();
			boolean lock = stream.readBoolean();
			boolean sel = stream.readBoolean();
			stream.readByte();
			BrushSettings br = (BrushSettings)SerializationManager.readObject(stream);
			GeneralPath pa = (GeneralPath)SerializationManager.readObject(stream);
			PowerBrushStrokeDrawObject o = new PowerBrushStrokeDrawObject(br, pa, ps);
			o.setTransform(tx);
			o.setVisible(vis);
			o.setLocked(lock);
			o.setSelected(sel);
			return o;
		}
		else if (type == TYPE_QUICKSHADOW_DO) {
			PaintSettings ps = (PaintSettings)SerializationManager.readObject(stream);
			AffineTransform tx = (AffineTransform)SerializationManager.readObject(stream);
			boolean vis = !stream.readBoolean();
			boolean lock = stream.readBoolean();
			boolean sel = stream.readBoolean();
			stream.readByte();
			int st = stream.readInt();
			int so = stream.readInt();
			int xo = stream.readInt();
			int yo = stream.readInt();
			Shape sh = (Shape)SerializationManager.readObject(stream);
			QuickShadowDrawObject o = new QuickShadowDrawObject(sh, st, so, xo, yo, ps);
			o.setTransform(tx);
			o.setVisible(vis);
			o.setLocked(lock);
			o.setSelected(sel);
			return o;
		}
		else if (type == TYPE_SHAPE_DO) {
			PaintSettings ps = (PaintSettings)SerializationManager.readObject(stream);
			AffineTransform tx = (AffineTransform)SerializationManager.readObject(stream);
			boolean vis = !stream.readBoolean();
			boolean lock = stream.readBoolean();
			boolean sel = stream.readBoolean();
			stream.readByte();
			Shape sh = (Shape)SerializationManager.readObject(stream);
			ShapeDrawObject o = new ShapeDrawObject(sh, ps);
			o.setTransform(tx);
			o.setVisible(vis);
			o.setLocked(lock);
			o.setSelected(sel);
			return o;
		}
		else if (type == TYPE_TEXT_DO) {
			PaintSettings ps = (PaintSettings)SerializationManager.readObject(stream);
			AffineTransform tx = (AffineTransform)SerializationManager.readObject(stream);
			boolean vis = !stream.readBoolean();
			boolean lock = stream.readBoolean();
			boolean sel = stream.readBoolean();
			stream.readByte();
			float x = stream.readFloat();
			float y = stream.readFloat();
			float w = stream.readFloat();
			int cs = stream.readInt();
			int ce = stream.readInt();
			String t = stream.readUTF();
			TextDrawObject o = new TextDrawObject(x,y,t,ps);
			o.setWrapWidth(w);
			o.setCursor(cs, ce);
			o.setTransform(tx);
			o.setVisible(vis);
			o.setLocked(lock);
			o.setSelected(sel);
			return o;
		}
		else if (type == TYPE_THREEDBOX_DO) {
			PaintSettings ps = (PaintSettings)SerializationManager.readObject(stream);
			AffineTransform tx = (AffineTransform)SerializationManager.readObject(stream);
			boolean vis = !stream.readBoolean();
			boolean lock = stream.readBoolean();
			boolean sel = stream.readBoolean();
			stream.readByte();
			double x = stream.readDouble();
			double y = stream.readDouble();
			double w = stream.readDouble();
			double h = stream.readDouble();
			double dx = stream.readDouble();
			double dy = stream.readDouble();
			ThreeDBoxDrawObject o = new ThreeDBoxDrawObject(x, y, w, h, dx, dy, ps);
			o.setTransform(tx);
			o.setVisible(vis);
			o.setLocked(lock);
			o.setSelected(sel);
			return o;
		}
		else return null;
	}
}
