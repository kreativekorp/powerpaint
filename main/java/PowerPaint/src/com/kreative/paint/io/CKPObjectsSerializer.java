package com.kreative.paint.io;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.image.renderable.RenderableImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import com.kreative.paint.document.draw.*;
import com.kreative.paint.draw.*;
import com.kreative.paint.geom.*;
import com.kreative.paint.geom.draw.*;
import com.kreative.paint.material.shape.PowerShape;
import com.kreative.paint.material.sprite.ColorTransform;
import com.kreative.paint.material.sprite.Sprite;
import com.kreative.paint.powerbrush.BrushSettings;
import com.kreative.paint.util.Bitmap;

public class CKPObjectsSerializer extends Serializer {
	private static final int TYPE_BRUSH_STROKE_DO = fcc("dBSt");
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
	
	private static final int TYPE_LINE_DO = fcc("dLin");
	private static final int TYPE_RECTANGLE_DO = fcc("dRec");
	private static final int TYPE_ROUND_RECT_DO = fcc("dRRe");
	private static final int TYPE_ELLIPSE_DO = fcc("dEll");
	private static final int TYPE_ARC_DO = fcc("dArc");
	private static final int TYPE_QUAD_CURVE_DO = fcc("dQua");
	private static final int TYPE_CUBIC_CURVE_DO = fcc("dCub");
	private static final int TYPE_POLYGON_DO = fcc("dPly");
	private static final int TYPE_PATH_DO = fcc("dPth");
	private static final int TYPE_BITMAP_DO = fcc("dBmp");
	private static final int TYPE_CYCLOID_DO = fcc("dCyc");
	private static final int TYPE_FLOWER_DO = fcc("dFlw");
	private static final int TYPE_POWERSHAPE_DO = fcc("dPSh");
	private static final int TYPE_REG_POLY_DO = fcc("dRPl");
	private static final int TYPE_RIGHT_ARC_DO = fcc("dArR");
	private static final int TYPE_SPIRAL_DO = fcc("dSpi");
	
	private static final int IDO_TYPE_IMAGE = fcc("gnrl");
	private static final int IDO_TYPE_BUFFERED_IMAGE = fcc("bfrd");
	private static final int IDO_TYPE_RENDERABLE_IMAGE = fcc("rdbl");
	private static final int IDO_TYPE_RENDERED_IMAGE = fcc("rdrd");
	
	protected void loadRecognizedTypesAndClasses() {
		addTypeAndClass(TYPE_BRUSH_STROKE_DO, 2, BrushStrokeDrawObject.class);
		addTypeAndClass(TYPE_CROP_MARK_DO, 2, CropMarkDrawObject.class);
		addTypeAndClass(TYPE_GRID_DO, 2, GridDrawObject.class);
		addTypeAndClass(TYPE_GROUP_DO, 2, GroupDrawObject.class);
		addTypeAndClass(TYPE_IMAGE_DO, 2, ImageDrawObject.class);
		addTypeAndClass(TYPE_PENCIL_STROKE_DO, 1, PencilStrokeDrawObject.class);
		addTypeAndClass(TYPE_PERSPECTIVE_GRID_DO, 2, PerspectiveGridDrawObject.class);
		addTypeAndClass(TYPE_POWERBRUSH_STROKE_DO, 1, PowerBrushStrokeDrawObject.class);
		addTypeAndClass(TYPE_QUICKSHADOW_DO, 2, ShadowSettings.class);
		addTypeAndClass(TYPE_SHAPE_DO, 2, ShapeDrawObject.class);
		addTypeAndClass(TYPE_TEXT_DO, 2, TextDrawObject.class);
		addTypeAndClass(TYPE_THREEDBOX_DO, 1, ThreeDBoxDrawObject.class);
		
		addTypeAndClass(TYPE_LINE_DO, 1, ShapeDrawObject.Line.class);
		addTypeAndClass(TYPE_RECTANGLE_DO, 1, ShapeDrawObject.Rectangle.class);
		addTypeAndClass(TYPE_ROUND_RECT_DO, 1, ShapeDrawObject.RoundRectangle.class);
		addTypeAndClass(TYPE_ELLIPSE_DO, 1, ShapeDrawObject.Ellipse.class);
		addTypeAndClass(TYPE_ARC_DO, 1, ShapeDrawObject.Arc.class);
		addTypeAndClass(TYPE_QUAD_CURVE_DO, 1, ShapeDrawObject.QuadCurve.class);
		addTypeAndClass(TYPE_CUBIC_CURVE_DO, 1, ShapeDrawObject.CubicCurve.class);
		addTypeAndClass(TYPE_POLYGON_DO, 1, ShapeDrawObject.Polygon.class);
		addTypeAndClass(TYPE_PATH_DO, 1, PathDrawObject.class);
		addTypeAndClass(TYPE_BITMAP_DO, 1, BitmapDrawObject.class);
		addTypeAndClass(TYPE_CYCLOID_DO, 1, CycloidDrawObject.class);
		addTypeAndClass(TYPE_FLOWER_DO, 1, FlowerDrawObject.class);
		addTypeAndClass(TYPE_POWERSHAPE_DO, 1, PowerShapeDrawObject.class);
		addTypeAndClass(TYPE_REG_POLY_DO, 1, RegularPolygonDrawObject.class);
		addTypeAndClass(TYPE_RIGHT_ARC_DO, 1, RightArcDrawObject.class);
		addTypeAndClass(TYPE_SPIRAL_DO, 1, SpiralDrawObject.class);
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
			Sprite s = v.getBrush();
			SerializationManager.writeObject(s.getRawImage(), stream);
			stream.writeShort(s.getHotspotX());
			stream.writeShort(s.getHotspotY());
			stream.writeShort(s.getColorTransform().intValue);
			SerializationManager.writeObject(v.getPath(), stream);
		} else if (o instanceof CropMarkDrawObject) {
			CropMarkDrawObject v = (CropMarkDrawObject)o;
			SerializationManager.writeObject(v.getPaintSettings(), stream);
			SerializationManager.writeObject(v.getTransform(), stream);
			stream.writeBoolean(!v.isVisible());
			stream.writeBoolean(v.isLocked());
			stream.writeBoolean(v.isSelected());
			stream.writeByte(0);
			stream.writeDouble(v.getX1());
			stream.writeDouble(v.getY1());
			stream.writeDouble(v.getX2());
			stream.writeDouble(v.getY2());
			stream.writeInt(v.getHorizDivisions());
			stream.writeInt(v.getVertDivisions());
		} else if (o instanceof GridDrawObject) {
			GridDrawObject v = (GridDrawObject)o;
			SerializationManager.writeObject(v.getPaintSettings(), stream);
			SerializationManager.writeObject(v.getTransform(), stream);
			stream.writeBoolean(!v.isVisible());
			stream.writeBoolean(v.isLocked());
			stream.writeBoolean(v.isSelected());
			stream.writeByte(0);
			Rectangle2D b = v.getGridBounds();
			stream.writeDouble(b.getX());
			stream.writeDouble(b.getY());
			stream.writeDouble(b.getWidth());
			stream.writeDouble(b.getHeight());
			stream.writeInt(v.getHorizGridType());
			stream.writeDouble(v.getHorizGridSpacing());
			stream.writeInt(v.getVertGridType());
			stream.writeDouble(v.getVertGridSpacing());
		} else if (o instanceof GroupDrawObject) {
			GroupDrawObject v = (GroupDrawObject)o;
			SerializationManager.writeObject(v.getPaintSettings(), stream);
			SerializationManager.writeObject(v.getTransform(), stream);
			stream.writeBoolean(!v.isVisible());
			stream.writeBoolean(v.isLocked());
			stream.writeBoolean(v.isSelected());
			stream.writeByte(0);
			stream.writeDouble(v.getX());
			stream.writeDouble(v.getY());
			stream.writeDouble(v.getWidth());
			stream.writeDouble(v.getHeight());
			List<DrawObject> objects = v.getObjects();
			stream.writeInt(objects.size());
			for (DrawObject d : objects) {
				SerializationManager.writeObject(d, stream);
			}
		} else if (o instanceof ImageDrawObject) {
			ImageDrawObject v = (ImageDrawObject)o;
			SerializationManager.writeObject(v.getPaintSettings(), stream);
			SerializationManager.writeObject(v.getTransform(), stream);
			stream.writeBoolean(!v.isVisible());
			stream.writeBoolean(v.isLocked());
			stream.writeBoolean(v.isSelected());
			stream.writeByte(0);
			stream.writeDouble(v.getX());
			stream.writeDouble(v.getY());
			stream.writeDouble(v.getWidth());
			stream.writeDouble(v.getHeight());
			BufferedImage bi = v.getImage();
			SerializationManager.writeObject(bi, stream);
		} else if (o instanceof PencilStrokeDrawObject) {
			PencilStrokeDrawObject v = (PencilStrokeDrawObject)o;
			SerializationManager.writeObject(v.getPaintSettings(), stream);
			SerializationManager.writeObject(v.getTransform(), stream);
			stream.writeBoolean(!v.isVisible());
			stream.writeBoolean(v.isLocked());
			stream.writeBoolean(v.isSelected());
			stream.writeByte(0);
			SerializationManager.writeObject(v.getPath(), stream);
		} else if (o instanceof PerspectiveGridDrawObject) {
			PerspectiveGridDrawObject v = (PerspectiveGridDrawObject)o;
			SerializationManager.writeObject(v.getPaintSettings(), stream);
			SerializationManager.writeObject(v.getTransform(), stream);
			stream.writeBoolean(!v.isVisible());
			stream.writeBoolean(v.isLocked());
			stream.writeBoolean(v.isSelected());
			stream.writeByte(0);
			Rectangle2D b = v.getGridBounds();
			stream.writeDouble(b.getX());
			stream.writeDouble(b.getY());
			stream.writeDouble(b.getWidth());
			stream.writeDouble(b.getHeight());
			stream.writeInt(v.getGridWidthTop());
			stream.writeInt(v.getGridWidthBottom());
			stream.writeInt(v.getGridHeight());
		} else if (o instanceof PowerBrushStrokeDrawObject) {
			PowerBrushStrokeDrawObject v = (PowerBrushStrokeDrawObject)o;
			SerializationManager.writeObject(v.getPaintSettings(), stream);
			SerializationManager.writeObject(v.getTransform(), stream);
			stream.writeBoolean(!v.isVisible());
			stream.writeBoolean(v.isLocked());
			stream.writeBoolean(v.isSelected());
			stream.writeByte(0);
			SerializationManager.writeObject(v.getBrush(), stream);
			SerializationManager.writeObject(v.getPath(), stream);
		} else if (o instanceof ShadowSettings) {
			ShadowSettings v = (ShadowSettings)o;
			stream.writeInt(v.shadowType);
			stream.writeInt(v.shadowOpacity);
			stream.writeInt(v.xOffset);
			stream.writeInt(v.yOffset);
		} else if (o instanceof TextDrawObject) {
			TextDrawObject v = (TextDrawObject)o;
			SerializationManager.writeObject(v.getPaintSettings(), stream);
			SerializationManager.writeObject(v.getTransform(), stream);
			stream.writeBoolean(!v.isVisible());
			stream.writeBoolean(v.isLocked());
			stream.writeBoolean(v.isSelected());
			stream.writeByte(0);
			stream.writeDouble(v.getX());
			stream.writeDouble(v.getY());
			stream.writeDouble(v.getWrapWidth());
			stream.writeInt(v.getCursorStart());
			stream.writeInt(v.getCursorEnd());
			stream.writeUTF(v.getText());
		} else if (o instanceof ThreeDBoxDrawObject) {
			ThreeDBoxDrawObject v = (ThreeDBoxDrawObject)o;
			SerializationManager.writeObject(v.getPaintSettings(), stream);
			SerializationManager.writeObject(v.getTransform(), stream);
			stream.writeBoolean(!v.isVisible());
			stream.writeBoolean(v.isLocked());
			stream.writeBoolean(v.isSelected());
			stream.writeByte(0);
			stream.writeDouble(v.getX());
			stream.writeDouble(v.getY());
			stream.writeDouble(v.getWidth());
			stream.writeDouble(v.getHeight());
			stream.writeDouble(v.getDX());
			stream.writeDouble(v.getDY());
		} else if (o instanceof ShapeDrawObject.Line) {
			ShapeDrawObject.Line v = (ShapeDrawObject.Line)o;
			SerializationManager.writeObject(v.getPaintSettings(), stream);
			SerializationManager.writeObject(v.getTransform(), stream);
			stream.writeBoolean(!v.isVisible());
			stream.writeBoolean(v.isLocked());
			stream.writeBoolean(v.isSelected());
			stream.writeByte(0);
			SerializationManager.writeObject(v.getShadowSettings(), stream);
			Line2D s = v.getShape();
			stream.writeDouble(s.getX1());
			stream.writeDouble(s.getY1());
			stream.writeDouble(s.getX2());
			stream.writeDouble(s.getY2());
		} else if (o instanceof ShapeDrawObject.Rectangle) {
			ShapeDrawObject.Rectangle v = (ShapeDrawObject.Rectangle)o;
			SerializationManager.writeObject(v.getPaintSettings(), stream);
			SerializationManager.writeObject(v.getTransform(), stream);
			stream.writeBoolean(!v.isVisible());
			stream.writeBoolean(v.isLocked());
			stream.writeBoolean(v.isSelected());
			stream.writeByte(0);
			SerializationManager.writeObject(v.getShadowSettings(), stream);
			Rectangle2D s = v.getShape();
			stream.writeDouble(s.getX());
			stream.writeDouble(s.getY());
			stream.writeDouble(s.getWidth());
			stream.writeDouble(s.getHeight());
		} else if (o instanceof ShapeDrawObject.RoundRectangle) {
			ShapeDrawObject.RoundRectangle v = (ShapeDrawObject.RoundRectangle)o;
			SerializationManager.writeObject(v.getPaintSettings(), stream);
			SerializationManager.writeObject(v.getTransform(), stream);
			stream.writeBoolean(!v.isVisible());
			stream.writeBoolean(v.isLocked());
			stream.writeBoolean(v.isSelected());
			stream.writeByte(0);
			SerializationManager.writeObject(v.getShadowSettings(), stream);
			RoundRectangle2D s = v.getShape();
			stream.writeDouble(s.getX());
			stream.writeDouble(s.getY());
			stream.writeDouble(s.getWidth());
			stream.writeDouble(s.getHeight());
			stream.writeDouble(s.getArcWidth());
			stream.writeDouble(s.getArcHeight());
		} else if (o instanceof ShapeDrawObject.Ellipse) {
			ShapeDrawObject.Ellipse v = (ShapeDrawObject.Ellipse)o;
			SerializationManager.writeObject(v.getPaintSettings(), stream);
			SerializationManager.writeObject(v.getTransform(), stream);
			stream.writeBoolean(!v.isVisible());
			stream.writeBoolean(v.isLocked());
			stream.writeBoolean(v.isSelected());
			stream.writeByte(0);
			SerializationManager.writeObject(v.getShadowSettings(), stream);
			Ellipse2D s = v.getShape();
			stream.writeDouble(s.getX());
			stream.writeDouble(s.getY());
			stream.writeDouble(s.getWidth());
			stream.writeDouble(s.getHeight());
		} else if (o instanceof ShapeDrawObject.Arc) {
			ShapeDrawObject.Arc v = (ShapeDrawObject.Arc)o;
			SerializationManager.writeObject(v.getPaintSettings(), stream);
			SerializationManager.writeObject(v.getTransform(), stream);
			stream.writeBoolean(!v.isVisible());
			stream.writeBoolean(v.isLocked());
			stream.writeBoolean(v.isSelected());
			stream.writeByte(0);
			SerializationManager.writeObject(v.getShadowSettings(), stream);
			Arc2D s = v.getShape();
			stream.writeDouble(s.getX());
			stream.writeDouble(s.getY());
			stream.writeDouble(s.getWidth());
			stream.writeDouble(s.getHeight());
			stream.writeDouble(s.getAngleStart());
			stream.writeDouble(s.getAngleExtent());
			stream.writeInt(s.getArcType());
		} else if (o instanceof ShapeDrawObject.QuadCurve) {
			ShapeDrawObject.QuadCurve v = (ShapeDrawObject.QuadCurve)o;
			SerializationManager.writeObject(v.getPaintSettings(), stream);
			SerializationManager.writeObject(v.getTransform(), stream);
			stream.writeBoolean(!v.isVisible());
			stream.writeBoolean(v.isLocked());
			stream.writeBoolean(v.isSelected());
			stream.writeByte(0);
			SerializationManager.writeObject(v.getShadowSettings(), stream);
			QuadCurve2D s = v.getShape();
			stream.writeDouble(s.getX1());
			stream.writeDouble(s.getY1());
			stream.writeDouble(s.getCtrlX());
			stream.writeDouble(s.getCtrlY());
			stream.writeDouble(s.getX2());
			stream.writeDouble(s.getY2());
		} else if (o instanceof ShapeDrawObject.CubicCurve) {
			ShapeDrawObject.CubicCurve v = (ShapeDrawObject.CubicCurve)o;
			SerializationManager.writeObject(v.getPaintSettings(), stream);
			SerializationManager.writeObject(v.getTransform(), stream);
			stream.writeBoolean(!v.isVisible());
			stream.writeBoolean(v.isLocked());
			stream.writeBoolean(v.isSelected());
			stream.writeByte(0);
			SerializationManager.writeObject(v.getShadowSettings(), stream);
			CubicCurve2D s = v.getShape();
			stream.writeDouble(s.getX1());
			stream.writeDouble(s.getY1());
			stream.writeDouble(s.getCtrlX1());
			stream.writeDouble(s.getCtrlY1());
			stream.writeDouble(s.getCtrlX2());
			stream.writeDouble(s.getCtrlY2());
			stream.writeDouble(s.getX2());
			stream.writeDouble(s.getY2());
		} else if (o instanceof ShapeDrawObject.Polygon) {
			ShapeDrawObject.Polygon v = (ShapeDrawObject.Polygon)o;
			SerializationManager.writeObject(v.getPaintSettings(), stream);
			SerializationManager.writeObject(v.getTransform(), stream);
			stream.writeBoolean(!v.isVisible());
			stream.writeBoolean(v.isLocked());
			stream.writeBoolean(v.isSelected());
			stream.writeByte(0);
			SerializationManager.writeObject(v.getShadowSettings(), stream);
			int n = v.getPointCount();
			stream.writeInt(n);
			for (int i = 0; i < n; i++) {
				stream.writeDouble(v.getPointX(i));
				stream.writeDouble(v.getPointY(i));
			}
			stream.writeBoolean(v.isClosed());
		} else if (o instanceof PathDrawObject) {
			PathDrawObject v = (PathDrawObject)o;
			SerializationManager.writeObject(v.getPaintSettings(), stream);
			SerializationManager.writeObject(v.getTransform(), stream);
			stream.writeBoolean(!v.isVisible());
			stream.writeBoolean(v.isLocked());
			stream.writeBoolean(v.isSelected());
			stream.writeByte(0);
			SerializationManager.writeObject(v.getShadowSettings(), stream);
			SerializationManager.writeObject(v.getPath(), stream);
		} else if (o instanceof BitmapDrawObject) {
			BitmapDrawObject v = (BitmapDrawObject)o;
			SerializationManager.writeObject(v.getPaintSettings(), stream);
			SerializationManager.writeObject(v.getTransform(), stream);
			stream.writeBoolean(!v.isVisible());
			stream.writeBoolean(v.isLocked());
			stream.writeBoolean(v.isSelected());
			stream.writeByte(0);
			SerializationManager.writeObject(v.getShadowSettings(), stream);
			SerializationManager.writeObject(v.getShape(), stream);
		} else if (o instanceof CycloidDrawObject) {
			CycloidDrawObject v = (CycloidDrawObject)o;
			SerializationManager.writeObject(v.getPaintSettings(), stream);
			SerializationManager.writeObject(v.getTransform(), stream);
			stream.writeBoolean(!v.isVisible());
			stream.writeBoolean(v.isLocked());
			stream.writeBoolean(v.isSelected());
			stream.writeByte(0);
			SerializationManager.writeObject(v.getShadowSettings(), stream);
			Cycloid s = v.getShape();
			stream.writeDouble(s.getCenterX());
			stream.writeDouble(s.getCenterY());
			stream.writeDouble(s.getEndpointX());
			stream.writeDouble(s.getEndpointY());
			stream.writeDouble(s.getR());
			stream.writeDouble(s.getr());
			stream.writeDouble(s.getd());
			stream.writeInt(s.getBegin());
			stream.writeInt(s.getEnd());
			stream.writeInt(s.getSmoothness());
			stream.writeBoolean(s.isEpicycloid());
		} else if (o instanceof FlowerDrawObject) {
			FlowerDrawObject v = (FlowerDrawObject)o;
			SerializationManager.writeObject(v.getPaintSettings(), stream);
			SerializationManager.writeObject(v.getTransform(), stream);
			stream.writeBoolean(!v.isVisible());
			stream.writeBoolean(v.isLocked());
			stream.writeBoolean(v.isSelected());
			stream.writeByte(0);
			SerializationManager.writeObject(v.getShadowSettings(), stream);
			Flower s = v.getShape();
			stream.writeDouble(s.getCenterX());
			stream.writeDouble(s.getCenterY());
			stream.writeDouble(s.getEndpointX());
			stream.writeDouble(s.getEndpointY());
			stream.writeDouble(s.getWidth());
			stream.writeInt(s.getPetals());
			stream.writeInt(s.getSmoothness());
			stream.writeBoolean(s.getIncludeCenter());
		} else if (o instanceof PowerShapeDrawObject) {
			PowerShapeDrawObject v = (PowerShapeDrawObject)o;
			SerializationManager.writeObject(v.getPaintSettings(), stream);
			SerializationManager.writeObject(v.getTransform(), stream);
			stream.writeBoolean(!v.isVisible());
			stream.writeBoolean(v.isLocked());
			stream.writeBoolean(v.isSelected());
			stream.writeByte(0);
			SerializationManager.writeObject(v.getShadowSettings(), stream);
			SerializationManager.writeObject(v.getPowerShape(), stream);
			stream.writeDouble(v.getX());
			stream.writeDouble(v.getY());
			stream.writeDouble(v.getWidth());
			stream.writeDouble(v.getHeight());
		} else if (o instanceof RegularPolygonDrawObject) {
			RegularPolygonDrawObject v = (RegularPolygonDrawObject)o;
			SerializationManager.writeObject(v.getPaintSettings(), stream);
			SerializationManager.writeObject(v.getTransform(), stream);
			stream.writeBoolean(!v.isVisible());
			stream.writeBoolean(v.isLocked());
			stream.writeBoolean(v.isSelected());
			stream.writeByte(0);
			SerializationManager.writeObject(v.getShadowSettings(), stream);
			RegularPolygon s = v.getShape();
			SerializationManager.writeObject(s.getCenterInternal(), stream);
			SerializationManager.writeObject(s.getFirstVertexInternal(), stream);
			SerializationManager.writeObject(s.getSecondVertexInternal(), stream);
			stream.writeInt(s.getSides());
			stream.writeInt(s.getSkips());
		} else if (o instanceof RightArcDrawObject) {
			RightArcDrawObject v = (RightArcDrawObject)o;
			SerializationManager.writeObject(v.getPaintSettings(), stream);
			SerializationManager.writeObject(v.getTransform(), stream);
			stream.writeBoolean(!v.isVisible());
			stream.writeBoolean(v.isLocked());
			stream.writeBoolean(v.isSelected());
			stream.writeByte(0);
			SerializationManager.writeObject(v.getShadowSettings(), stream);
			RightArc s = v.getShape();
			stream.writeDouble(s.getX());
			stream.writeDouble(s.getY());
			stream.writeDouble(s.getWidth());
			stream.writeDouble(s.getHeight());
		} else if (o instanceof SpiralDrawObject) {
			SpiralDrawObject v = (SpiralDrawObject)o;
			SerializationManager.writeObject(v.getPaintSettings(), stream);
			SerializationManager.writeObject(v.getTransform(), stream);
			stream.writeBoolean(!v.isVisible());
			stream.writeBoolean(v.isLocked());
			stream.writeBoolean(v.isSelected());
			stream.writeByte(0);
			SerializationManager.writeObject(v.getShadowSettings(), stream);
			Spiral s = v.getShape();
			stream.writeDouble(s.getCenterX());
			stream.writeDouble(s.getCenterY());
			stream.writeDouble(s.getEndpointX());
			stream.writeDouble(s.getEndpointY());
			stream.writeDouble(s.getSpacing());
			stream.writeInt(s.getSides());
			stream.writeBoolean(s.getSpokes());
		}
	}
	
	public Object deserializeObject(int type, int version, DataInputStream stream) throws IOException {
		if (type == TYPE_BRUSH_STROKE_DO) {
			if (version < 1 || version > 2) throw new IOException("Invalid version number.");
			PaintSettings ps = (PaintSettings)SerializationManager.readObject(stream);
			AffineTransform tx = (AffineTransform)SerializationManager.readObject(stream);
			boolean vis = !stream.readBoolean();
			boolean lock = stream.readBoolean();
			boolean sel = stream.readBoolean();
			stream.readByte();
			BufferedImage raw;
			int hx, hy;
			ColorTransform ctx;
			if (version < 2) {
				Object tmp = SerializationManager.readObject(stream);
				raw = (tmp instanceof BufferedImage) ? (BufferedImage)tmp : ((Bitmap)tmp).getImage();
				hx = raw.getWidth() / 2;
				hy = raw.getHeight() / 2;
				ctx = ColorTransform.ALL;
			} else {
				raw = (BufferedImage)SerializationManager.readObject(stream);
				hx = stream.readShort();
				hy = stream.readShort();
				ctx = new ColorTransform(stream.readShort());
			}
			Sprite br = new Sprite(raw, hx, hy, ctx);
			Object path = SerializationManager.readObject(stream);
			BrushStrokeDrawObject o = (path instanceof Path)
				? new BrushStrokeDrawObject(ps, (Path)path, br)
				: new BrushStrokeDrawObject(ps, (Shape)path, br);
			o.setTransform(tx);
			o.setVisible(vis);
			o.setLocked(lock);
			o.setSelected(sel);
			return o;
		} else if (type == TYPE_CROP_MARK_DO) {
			if (version < 1 || version > 2) throw new IOException("Invalid version number.");
			PaintSettings ps = (PaintSettings)SerializationManager.readObject(stream);
			AffineTransform tx = (AffineTransform)SerializationManager.readObject(stream);
			boolean vis = !stream.readBoolean();
			boolean lock = stream.readBoolean();
			boolean sel = stream.readBoolean();
			stream.readByte();
			double x1 = (version < 2) ? stream.readFloat() : stream.readDouble();
			double y1 = (version < 2) ? stream.readFloat() : stream.readDouble();
			double x2 = (version < 2) ? stream.readFloat() : stream.readDouble();
			double y2 = (version < 2) ? stream.readFloat() : stream.readDouble();
			int hd = stream.readInt();
			int vd = stream.readInt();
			CropMarkDrawObject o = new CropMarkDrawObject(ps, x1, y1, x2, y2, hd, vd);
			o.setTransform(tx);
			o.setVisible(vis);
			o.setLocked(lock);
			o.setSelected(sel);
			return o;
		} else if (type == TYPE_GRID_DO) {
			if (version < 1 || version > 2) throw new IOException("Invalid version number.");
			PaintSettings ps = (PaintSettings)SerializationManager.readObject(stream);
			AffineTransform tx = (AffineTransform)SerializationManager.readObject(stream);
			boolean vis = !stream.readBoolean();
			boolean lock = stream.readBoolean();
			boolean sel = stream.readBoolean();
			stream.readByte();
			double x = (version < 2) ? stream.readFloat() : stream.readDouble();
			double y = (version < 2) ? stream.readFloat() : stream.readDouble();
			double w = (version < 2) ? stream.readFloat() : stream.readDouble();
			double h = (version < 2) ? stream.readFloat() : stream.readDouble();
			int hgt = stream.readInt();
			double hgs = (version < 2) ? stream.readFloat() : stream.readDouble();
			int vgt = stream.readInt();
			double vgs = (version < 2) ? stream.readFloat() : stream.readDouble();
			GridDrawObject o = new GridDrawObject(ps, x, y, w, h, hgt, hgs, vgt, vgs);
			o.setTransform(tx);
			o.setVisible(vis);
			o.setLocked(lock);
			o.setSelected(sel);
			return o;
		} else if (type == TYPE_GROUP_DO) {
			if (version < 1 || version > 2) throw new IOException("Invalid version number.");
			/* PaintSettings ps = (PaintSettings) */ SerializationManager.readObject(stream);
			AffineTransform tx = (AffineTransform)SerializationManager.readObject(stream);
			boolean vis = !stream.readBoolean();
			boolean lock = stream.readBoolean();
			boolean sel = stream.readBoolean();
			stream.readByte();
			if (version < 2) {
				List<DrawObject> objects = new ArrayList<DrawObject>();
				int n = stream.readInt();
				for (int i = 0; i < n; i++) {
					objects.add((DrawObject)SerializationManager.readObject(stream));
				}
				GroupDrawObject gr = new GroupDrawObject(objects);
				gr.setTransform(tx);
				gr.setVisible(vis);
				gr.setLocked(lock);
				gr.setSelected(sel);
				return gr;
			} else {
				double x = stream.readDouble();
				double y = stream.readDouble();
				double w = stream.readDouble();
				double h = stream.readDouble();
				List<DrawObject> objects = new ArrayList<DrawObject>();
				int n = stream.readInt();
				for (int i = 0; i < n; i++) {
					objects.add((DrawObject)SerializationManager.readObject(stream));
				}
				GroupDrawObject gr = new GroupDrawObject(objects, x, y, w, h);
				gr.setTransform(tx);
				gr.setVisible(vis);
				gr.setLocked(lock);
				gr.setSelected(sel);
				return gr;
			}
		} else if (type == TYPE_IMAGE_DO) {
			if (version < 1 || version > 2) throw new IOException("Invalid version number.");
			PaintSettings ps = (PaintSettings)SerializationManager.readObject(stream);
			AffineTransform tx = (AffineTransform)SerializationManager.readObject(stream);
			boolean vis = !stream.readBoolean();
			boolean lock = stream.readBoolean();
			boolean sel = stream.readBoolean();
			stream.readByte();
			if (version < 2) {
				int bx = stream.readInt();
				int by = stream.readInt();
				int bw = stream.readInt();
				int bh = stream.readInt();
				int ix = stream.readInt();
				int iy = stream.readInt();
				int iw = stream.readInt();
				int ih = stream.readInt();
				int t = stream.readInt();
				if (t == IDO_TYPE_IMAGE) {
					Image img = (Image)SerializationManager.readObject(stream);
					ImageDrawObject o = ImageDrawObject.forGraphicsDrawImage(
						ps, img,
						bx, by, bx + bw, by + bh,
						ix, iy, ix + iw, iy + ih
					);
					o.setTransform(tx);
					o.setVisible(vis);
					o.setLocked(lock);
					o.setSelected(sel);
					return o;
				} else if (t == IDO_TYPE_BUFFERED_IMAGE) {
					BufferedImage img = (BufferedImage)SerializationManager.readObject(stream);
					BufferedImageOp op = (BufferedImageOp)SerializationManager.readObject(stream);
					ImageDrawObject o = ImageDrawObject.forGraphicsDrawImage(ps, img, op, bx, by);
					o.setTransform(tx);
					o.setVisible(vis);
					o.setLocked(lock);
					o.setSelected(sel);
					return o;
				} else if (t == IDO_TYPE_RENDERABLE_IMAGE) {
					RenderableImage img = (RenderableImage)SerializationManager.readObject(stream);
					ImageDrawObject o = ImageDrawObject.forGraphicsDrawRenderableImage(ps, img, null);
					o.setTransform(tx);
					o.setVisible(vis);
					o.setLocked(lock);
					o.setSelected(sel);
					return o;
				} else if (t == IDO_TYPE_RENDERED_IMAGE) {
					RenderedImage img = (RenderedImage)SerializationManager.readObject(stream);
					ImageDrawObject o = ImageDrawObject.forGraphicsDrawRenderedImage(ps, img, null);
					o.setTransform(tx);
					o.setVisible(vis);
					o.setLocked(lock);
					o.setSelected(sel);
					return o;
				} else {
					return null;
				}
			} else {
				double x = stream.readDouble();
				double y = stream.readDouble();
				double w = stream.readDouble();
				double h = stream.readDouble();
				BufferedImage image = (BufferedImage)SerializationManager.readObject(stream);
				ImageDrawObject o = new ImageDrawObject(ps, image, x, y, w, h);
				o.setTransform(tx);
				o.setVisible(vis);
				o.setLocked(lock);
				o.setSelected(sel);
				return o;
			}
		} else if (type == TYPE_PENCIL_STROKE_DO) {
			if (version != 1) throw new IOException("Invalid version number.");
			PaintSettings ps = (PaintSettings)SerializationManager.readObject(stream);
			AffineTransform tx = (AffineTransform)SerializationManager.readObject(stream);
			boolean vis = !stream.readBoolean();
			boolean lock = stream.readBoolean();
			boolean sel = stream.readBoolean();
			stream.readByte();
			Object path = SerializationManager.readObject(stream);
			PencilStrokeDrawObject o = (path instanceof Path)
				? new PencilStrokeDrawObject(ps, (Path)path)
				: new PencilStrokeDrawObject(ps, (Shape)path);
			o.setTransform(tx);
			o.setVisible(vis);
			o.setLocked(lock);
			o.setSelected(sel);
			return o;
		} else if (type == TYPE_PERSPECTIVE_GRID_DO) {
			if (version < 1 || version > 2) throw new IOException("Invalid version number.");
			PaintSettings ps = (PaintSettings)SerializationManager.readObject(stream);
			AffineTransform tx = (AffineTransform)SerializationManager.readObject(stream);
			boolean vis = !stream.readBoolean();
			boolean lock = stream.readBoolean();
			boolean sel = stream.readBoolean();
			stream.readByte();
			double x = (version < 2) ? stream.readFloat() : stream.readDouble();
			double y = (version < 2) ? stream.readFloat() : stream.readDouble();
			double w = (version < 2) ? stream.readFloat() : stream.readDouble();
			double h = (version < 2) ? stream.readFloat() : stream.readDouble();
			int nt = stream.readInt();
			int nb = stream.readInt();
			int nh = stream.readInt();
			PerspectiveGridDrawObject o = new PerspectiveGridDrawObject(ps,x,y,w,h,nt,nb,nh);
			o.setTransform(tx);
			o.setVisible(vis);
			o.setLocked(lock);
			o.setSelected(sel);
			return o;
		} else if (type == TYPE_POWERBRUSH_STROKE_DO) {
			if (version != 1) throw new IOException("Invalid version number.");
			PaintSettings ps = (PaintSettings)SerializationManager.readObject(stream);
			AffineTransform tx = (AffineTransform)SerializationManager.readObject(stream);
			boolean vis = !stream.readBoolean();
			boolean lock = stream.readBoolean();
			boolean sel = stream.readBoolean();
			stream.readByte();
			BrushSettings br = (BrushSettings)SerializationManager.readObject(stream);
			Object path = SerializationManager.readObject(stream);
			PowerBrushStrokeDrawObject o = (path instanceof Path)
				? new PowerBrushStrokeDrawObject(ps, (Path)path, br)
				: new PowerBrushStrokeDrawObject(ps, (Shape)path, br);
			o.setTransform(tx);
			o.setVisible(vis);
			o.setLocked(lock);
			o.setSelected(sel);
			return o;
		} else if (type == TYPE_QUICKSHADOW_DO) {
			if (version < 1 || version > 2) throw new IOException("Invalid version number.");
			if (version < 2) {
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
				ShapeDrawObject o = drawObjectForShape(ps, sh);
				o.setTransform(tx);
				o.setVisible(vis);
				o.setLocked(lock);
				o.setSelected(sel);
				o.setShadowSettings(new ShadowSettings(st, so, xo, yo));
				return o;
			} else {
				int st = stream.readInt();
				int so = stream.readInt();
				int xo = stream.readInt();
				int yo = stream.readInt();
				return new ShadowSettings(st, so, xo, yo);
			}
		} else if (type == TYPE_SHAPE_DO) {
			if (version != 1) throw new IOException("Invalid version number.");
			PaintSettings ps = (PaintSettings)SerializationManager.readObject(stream);
			AffineTransform tx = (AffineTransform)SerializationManager.readObject(stream);
			boolean vis = !stream.readBoolean();
			boolean lock = stream.readBoolean();
			boolean sel = stream.readBoolean();
			stream.readByte();
			Shape sh = (Shape)SerializationManager.readObject(stream);
			ShapeDrawObject o = drawObjectForShape(ps, sh);
			o.setTransform(tx);
			o.setVisible(vis);
			o.setLocked(lock);
			o.setSelected(sel);
			return o;
		} else if (type == TYPE_TEXT_DO) {
			if (version < 1 || version > 2) throw new IOException("Invalid version number.");
			PaintSettings ps = (PaintSettings)SerializationManager.readObject(stream);
			AffineTransform tx = (AffineTransform)SerializationManager.readObject(stream);
			boolean vis = !stream.readBoolean();
			boolean lock = stream.readBoolean();
			boolean sel = stream.readBoolean();
			stream.readByte();
			double x = (version < 2) ? stream.readFloat() : stream.readDouble();
			double y = (version < 2) ? stream.readFloat() : stream.readDouble();
			double w = (version < 2) ? stream.readFloat() : stream.readDouble();
			int cs = stream.readInt();
			int ce = stream.readInt();
			String t = stream.readUTF();
			TextDrawObject o = new TextDrawObject(ps, x, y, w, t);
			o.setCursor(cs, ce);
			o.setTransform(tx);
			o.setVisible(vis);
			o.setLocked(lock);
			o.setSelected(sel);
			return o;
		} else if (type == TYPE_THREEDBOX_DO) {
			if (version != 1) throw new IOException("Invalid version number.");
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
			ThreeDBoxDrawObject o = new ThreeDBoxDrawObject(ps, x, y, w, h, dx, dy);
			o.setTransform(tx);
			o.setVisible(vis);
			o.setLocked(lock);
			o.setSelected(sel);
			return o;
		} else if (type == TYPE_LINE_DO) {
			if (version != 1) throw new IOException("Invalid version number.");
			PaintSettings ps = (PaintSettings)SerializationManager.readObject(stream);
			AffineTransform tx = (AffineTransform)SerializationManager.readObject(stream);
			boolean vis = !stream.readBoolean();
			boolean lock = stream.readBoolean();
			boolean sel = stream.readBoolean();
			stream.readByte();
			ShadowSettings shadow = (ShadowSettings)SerializationManager.readObject(stream);
			double x1 = stream.readDouble();
			double y1 = stream.readDouble();
			double x2 = stream.readDouble();
			double y2 = stream.readDouble();
			ShapeDrawObject.Line o = new ShapeDrawObject.Line(ps, x1, y1, x2, y2);
			o.setTransform(tx);
			o.setVisible(vis);
			o.setLocked(lock);
			o.setSelected(sel);
			o.setShadowSettings(shadow);
			return o;
		} else if (type == TYPE_RECTANGLE_DO) {
			if (version != 1) throw new IOException("Invalid version number.");
			PaintSettings ps = (PaintSettings)SerializationManager.readObject(stream);
			AffineTransform tx = (AffineTransform)SerializationManager.readObject(stream);
			boolean vis = !stream.readBoolean();
			boolean lock = stream.readBoolean();
			boolean sel = stream.readBoolean();
			stream.readByte();
			ShadowSettings shadow = (ShadowSettings)SerializationManager.readObject(stream);
			double x = stream.readDouble();
			double y = stream.readDouble();
			double w = stream.readDouble();
			double h = stream.readDouble();
			ShapeDrawObject.Rectangle o = new ShapeDrawObject.Rectangle(ps, x, y, w, h);
			o.setTransform(tx);
			o.setVisible(vis);
			o.setLocked(lock);
			o.setSelected(sel);
			o.setShadowSettings(shadow);
			return o;
		} else if (type == TYPE_ROUND_RECT_DO) {
			if (version != 1) throw new IOException("Invalid version number.");
			PaintSettings ps = (PaintSettings)SerializationManager.readObject(stream);
			AffineTransform tx = (AffineTransform)SerializationManager.readObject(stream);
			boolean vis = !stream.readBoolean();
			boolean lock = stream.readBoolean();
			boolean sel = stream.readBoolean();
			stream.readByte();
			ShadowSettings shadow = (ShadowSettings)SerializationManager.readObject(stream);
			double x = stream.readDouble();
			double y = stream.readDouble();
			double w = stream.readDouble();
			double h = stream.readDouble();
			double rx = stream.readDouble();
			double ry = stream.readDouble();
			ShapeDrawObject.RoundRectangle o = new ShapeDrawObject.RoundRectangle(ps, x, y, w, h, rx, ry);
			o.setTransform(tx);
			o.setVisible(vis);
			o.setLocked(lock);
			o.setSelected(sel);
			o.setShadowSettings(shadow);
			return o;
		} else if (type == TYPE_ELLIPSE_DO) {
			if (version != 1) throw new IOException("Invalid version number.");
			PaintSettings ps = (PaintSettings)SerializationManager.readObject(stream);
			AffineTransform tx = (AffineTransform)SerializationManager.readObject(stream);
			boolean vis = !stream.readBoolean();
			boolean lock = stream.readBoolean();
			boolean sel = stream.readBoolean();
			stream.readByte();
			ShadowSettings shadow = (ShadowSettings)SerializationManager.readObject(stream);
			double x = stream.readDouble();
			double y = stream.readDouble();
			double w = stream.readDouble();
			double h = stream.readDouble();
			ShapeDrawObject.Ellipse o = new ShapeDrawObject.Ellipse(ps, x, y, w, h);
			o.setTransform(tx);
			o.setVisible(vis);
			o.setLocked(lock);
			o.setSelected(sel);
			o.setShadowSettings(shadow);
			return o;
		} else if (type == TYPE_ARC_DO) {
			if (version != 1) throw new IOException("Invalid version number.");
			PaintSettings ps = (PaintSettings)SerializationManager.readObject(stream);
			AffineTransform tx = (AffineTransform)SerializationManager.readObject(stream);
			boolean vis = !stream.readBoolean();
			boolean lock = stream.readBoolean();
			boolean sel = stream.readBoolean();
			stream.readByte();
			ShadowSettings shadow = (ShadowSettings)SerializationManager.readObject(stream);
			double x = stream.readDouble();
			double y = stream.readDouble();
			double w = stream.readDouble();
			double h = stream.readDouble();
			double as = stream.readDouble();
			double ae = stream.readDouble();
			int at = stream.readInt();
			ShapeDrawObject.Arc o = new ShapeDrawObject.Arc(ps, x, y, w, h, as, ae, ArcType.forAWTValue(at));
			o.setTransform(tx);
			o.setVisible(vis);
			o.setLocked(lock);
			o.setSelected(sel);
			o.setShadowSettings(shadow);
			return o;
		} else if (type == TYPE_QUAD_CURVE_DO) {
			if (version != 1) throw new IOException("Invalid version number.");
			PaintSettings ps = (PaintSettings)SerializationManager.readObject(stream);
			AffineTransform tx = (AffineTransform)SerializationManager.readObject(stream);
			boolean vis = !stream.readBoolean();
			boolean lock = stream.readBoolean();
			boolean sel = stream.readBoolean();
			stream.readByte();
			ShadowSettings shadow = (ShadowSettings)SerializationManager.readObject(stream);
			double x1 = stream.readDouble();
			double y1 = stream.readDouble();
			double cx = stream.readDouble();
			double cy = stream.readDouble();
			double x2 = stream.readDouble();
			double y2 = stream.readDouble();
			ShapeDrawObject.QuadCurve o = new ShapeDrawObject.QuadCurve(ps, x1, y1, cx, cy, x2, y2);
			o.setTransform(tx);
			o.setVisible(vis);
			o.setLocked(lock);
			o.setSelected(sel);
			o.setShadowSettings(shadow);
			return o;
		} else if (type == TYPE_CUBIC_CURVE_DO) {
			if (version != 1) throw new IOException("Invalid version number.");
			PaintSettings ps = (PaintSettings)SerializationManager.readObject(stream);
			AffineTransform tx = (AffineTransform)SerializationManager.readObject(stream);
			boolean vis = !stream.readBoolean();
			boolean lock = stream.readBoolean();
			boolean sel = stream.readBoolean();
			stream.readByte();
			ShadowSettings shadow = (ShadowSettings)SerializationManager.readObject(stream);
			double x1 = stream.readDouble();
			double y1 = stream.readDouble();
			double cx1 = stream.readDouble();
			double cy1 = stream.readDouble();
			double cx2 = stream.readDouble();
			double cy2 = stream.readDouble();
			double x2 = stream.readDouble();
			double y2 = stream.readDouble();
			ShapeDrawObject.CubicCurve o = new ShapeDrawObject.CubicCurve(ps, x1, y1, cx1, cy1, cx2, cy2, x2, y2);
			o.setTransform(tx);
			o.setVisible(vis);
			o.setLocked(lock);
			o.setSelected(sel);
			o.setShadowSettings(shadow);
			return o;
		} else if (type == TYPE_POLYGON_DO) {
			if (version != 1) throw new IOException("Invalid version number.");
			PaintSettings ps = (PaintSettings)SerializationManager.readObject(stream);
			AffineTransform tx = (AffineTransform)SerializationManager.readObject(stream);
			boolean vis = !stream.readBoolean();
			boolean lock = stream.readBoolean();
			boolean sel = stream.readBoolean();
			stream.readByte();
			ShadowSettings shadow = (ShadowSettings)SerializationManager.readObject(stream);
			int n = stream.readInt();
			double[] x = new double[n];
			double[] y = new double[n];
			for (int i = 0; i < n; i++) {
				x[i] = stream.readDouble();
				y[i] = stream.readDouble();
			}
			boolean closed = stream.readBoolean();
			ShapeDrawObject.Polygon o = new ShapeDrawObject.Polygon(ps, x, y, n, closed);
			o.setTransform(tx);
			o.setVisible(vis);
			o.setLocked(lock);
			o.setSelected(sel);
			o.setShadowSettings(shadow);
			return o;
		} else if (type == TYPE_PATH_DO) {
			if (version != 1) throw new IOException("Invalid version number.");
			PaintSettings ps = (PaintSettings)SerializationManager.readObject(stream);
			AffineTransform tx = (AffineTransform)SerializationManager.readObject(stream);
			boolean vis = !stream.readBoolean();
			boolean lock = stream.readBoolean();
			boolean sel = stream.readBoolean();
			stream.readByte();
			ShadowSettings shadow = (ShadowSettings)SerializationManager.readObject(stream);
			Path path = (Path)SerializationManager.readObject(stream);
			PathDrawObject o = new PathDrawObject(ps, path);
			o.setTransform(tx);
			o.setVisible(vis);
			o.setLocked(lock);
			o.setSelected(sel);
			o.setShadowSettings(shadow);
			return o;
		} else if (type == TYPE_BITMAP_DO) {
			if (version != 1) throw new IOException("Invalid version number.");
			PaintSettings ps = (PaintSettings)SerializationManager.readObject(stream);
			AffineTransform tx = (AffineTransform)SerializationManager.readObject(stream);
			boolean vis = !stream.readBoolean();
			boolean lock = stream.readBoolean();
			boolean sel = stream.readBoolean();
			stream.readByte();
			ShadowSettings shadow = (ShadowSettings)SerializationManager.readObject(stream);
			BitmapShape s = (BitmapShape)SerializationManager.readObject(stream);
			BitmapDrawObject o = new BitmapDrawObject(ps, s.getBitmap(), s.getX(), s.getY(), s.getWidth(), s.getHeight());
			o.setTransform(tx);
			o.setVisible(vis);
			o.setLocked(lock);
			o.setSelected(sel);
			o.setShadowSettings(shadow);
			return o;
		} else if (type == TYPE_CYCLOID_DO) {
			if (version != 1) throw new IOException("Invalid version number.");
			PaintSettings ps = (PaintSettings)SerializationManager.readObject(stream);
			AffineTransform tx = (AffineTransform)SerializationManager.readObject(stream);
			boolean vis = !stream.readBoolean();
			boolean lock = stream.readBoolean();
			boolean sel = stream.readBoolean();
			stream.readByte();
			ShadowSettings shadow = (ShadowSettings)SerializationManager.readObject(stream);
			double cx = stream.readDouble();
			double cy = stream.readDouble();
			double ex = stream.readDouble();
			double ey = stream.readDouble();
			double R = stream.readDouble();
			double r = stream.readDouble();
			double d = stream.readDouble();
			int b = stream.readInt();
			int e = stream.readInt();
			int s = stream.readInt();
			boolean epi = stream.readBoolean();
			CycloidDrawObject o = new CycloidDrawObject(ps, epi, s, b, e, R, r, d, cx, cy, ex, ey);
			o.setTransform(tx);
			o.setVisible(vis);
			o.setLocked(lock);
			o.setSelected(sel);
			o.setShadowSettings(shadow);
			return o;
		} else if (type == TYPE_FLOWER_DO) {
			if (version != 1) throw new IOException("Invalid version number.");
			PaintSettings ps = (PaintSettings)SerializationManager.readObject(stream);
			AffineTransform tx = (AffineTransform)SerializationManager.readObject(stream);
			boolean vis = !stream.readBoolean();
			boolean lock = stream.readBoolean();
			boolean sel = stream.readBoolean();
			stream.readByte();
			ShadowSettings shadow = (ShadowSettings)SerializationManager.readObject(stream);
			double cx = stream.readDouble();
			double cy = stream.readDouble();
			double ex = stream.readDouble();
			double ey = stream.readDouble();
			double w = stream.readDouble();
			int p = stream.readInt();
			int s = stream.readInt();
			boolean ic = stream.readBoolean();
			FlowerDrawObject o = new FlowerDrawObject(ps, p, w, s, ic, cx, cy, ex, ey);
			o.setTransform(tx);
			o.setVisible(vis);
			o.setLocked(lock);
			o.setSelected(sel);
			o.setShadowSettings(shadow);
			return o;
		} else if (type == TYPE_POWERSHAPE_DO) {
			if (version != 1) throw new IOException("Invalid version number.");
			PaintSettings ps = (PaintSettings)SerializationManager.readObject(stream);
			AffineTransform tx = (AffineTransform)SerializationManager.readObject(stream);
			boolean vis = !stream.readBoolean();
			boolean lock = stream.readBoolean();
			boolean sel = stream.readBoolean();
			stream.readByte();
			ShadowSettings shadow = (ShadowSettings)SerializationManager.readObject(stream);
			PowerShape shape = (PowerShape)SerializationManager.readObject(stream);
			double x = stream.readDouble();
			double y = stream.readDouble();
			double w = stream.readDouble();
			double h = stream.readDouble();
			PowerShapeDrawObject o = new PowerShapeDrawObject(ps, shape, x, y, w, h);
			o.setTransform(tx);
			o.setVisible(vis);
			o.setLocked(lock);
			o.setSelected(sel);
			o.setShadowSettings(shadow);
			return o;
		} else if (type == TYPE_REG_POLY_DO) {
			if (version != 1) throw new IOException("Invalid version number.");
			PaintSettings ps = (PaintSettings)SerializationManager.readObject(stream);
			AffineTransform tx = (AffineTransform)SerializationManager.readObject(stream);
			boolean vis = !stream.readBoolean();
			boolean lock = stream.readBoolean();
			boolean sel = stream.readBoolean();
			stream.readByte();
			ShadowSettings shadow = (ShadowSettings)SerializationManager.readObject(stream);
			Point2D c = (Point2D)SerializationManager.readObject(stream);
			Point2D v1 = (Point2D)SerializationManager.readObject(stream);
			Point2D v2 = (Point2D)SerializationManager.readObject(stream);
			int sides = stream.readInt();
			int skips = stream.readInt();
			RegularPolygonDrawObject o = (c != null) ?
				new RegularPolygonDrawObject(ps, sides, skips, c.getX(), c.getY(), v1.getX(), v1.getY(), true) :
				new RegularPolygonDrawObject(ps, sides, skips, v1.getX(), v1.getY(), v2.getX(), v2.getY(), false);
			o.setTransform(tx);
			o.setVisible(vis);
			o.setLocked(lock);
			o.setSelected(sel);
			o.setShadowSettings(shadow);
			return o;
		} else if (type == TYPE_RIGHT_ARC_DO) {
			if (version != 1) throw new IOException("Invalid version number.");
			PaintSettings ps = (PaintSettings)SerializationManager.readObject(stream);
			AffineTransform tx = (AffineTransform)SerializationManager.readObject(stream);
			boolean vis = !stream.readBoolean();
			boolean lock = stream.readBoolean();
			boolean sel = stream.readBoolean();
			stream.readByte();
			ShadowSettings shadow = (ShadowSettings)SerializationManager.readObject(stream);
			double x = stream.readDouble();
			double y = stream.readDouble();
			double w = stream.readDouble();
			double h = stream.readDouble();
			RightArcDrawObject o = new RightArcDrawObject(ps, x, y, w, h);
			o.setTransform(tx);
			o.setVisible(vis);
			o.setLocked(lock);
			o.setSelected(sel);
			o.setShadowSettings(shadow);
			return o;
		} else if (type == TYPE_SPIRAL_DO) {
			if (version != 1) throw new IOException("Invalid version number.");
			PaintSettings ps = (PaintSettings)SerializationManager.readObject(stream);
			AffineTransform tx = (AffineTransform)SerializationManager.readObject(stream);
			boolean vis = !stream.readBoolean();
			boolean lock = stream.readBoolean();
			boolean sel = stream.readBoolean();
			stream.readByte();
			ShadowSettings shadow = (ShadowSettings)SerializationManager.readObject(stream);
			double cx = stream.readDouble();
			double cy = stream.readDouble();
			double ex = stream.readDouble();
			double ey = stream.readDouble();
			double spacing = stream.readDouble();
			int sides = stream.readInt();
			boolean spokes = stream.readBoolean();
			SpiralDrawObject o = new SpiralDrawObject(ps, sides, spacing, spokes, cx, cy, ex, ey);
			o.setTransform(tx);
			o.setVisible(vis);
			o.setLocked(lock);
			o.setSelected(sel);
			o.setShadowSettings(shadow);
			return o;
		} else {
			return null;
		}
	}
	
	private static ShapeDrawObject drawObjectForShape(PaintSettings ps, Shape s) {
		if (s instanceof BitmapShape) {
			BitmapShape b = (BitmapShape)s;
			return new BitmapDrawObject(ps, b.getBitmap(), b.getX(), b.getY(), b.getWidth(), b.getHeight());
		} else if (s instanceof Cycloid) {
			Cycloid c = (Cycloid)s;
			return new CycloidDrawObject(ps, c.isEpicycloid(), c.getSmoothness(), c.getBegin(), c.getEnd(), c.getR(), c.getr(), c.getd(), c.getCenterX(), c.getCenterY(), c.getEndpointX(), c.getEndpointY());
		} else if (s instanceof Flower) {
			Flower f = (Flower)s;
			return new FlowerDrawObject(ps, f.getPetals(), f.getWidth(), f.getSmoothness(), f.getIncludeCenter(), f.getCenterX(), f.getCenterY(), f.getEndpointX(), f.getEndpointY());
		} else if (s instanceof RegularPolygon) {
			RegularPolygon r = (RegularPolygon)s;
			Point2D c = r.getCenterInternal();
			Point2D v1 = r.getFirstVertexInternal();
			Point2D v2 = r.getSecondVertexInternal();
			return (c != null) ?
				new RegularPolygonDrawObject(ps, r.getSides(), r.getSkips(), c.getX(), c.getY(), v1.getX(), v1.getY(), true) :
				new RegularPolygonDrawObject(ps, r.getSides(), r.getSkips(), v1.getX(), v1.getY(), v2.getX(), v2.getY(), false);
		} else if (s instanceof RightArc) {
			RightArc r = (RightArc)s;
			return new RightArcDrawObject(ps, r.getX(), r.getY(), r.getWidth(), r.getHeight());
		} else if (s instanceof ScaledShape) {
			ScaledShape ss = (ScaledShape)s;
			PowerShape shape = (PowerShape)ss.getOriginalShape();
			return new PowerShapeDrawObject(ps, shape, ss.getX(), ss.getY(), ss.getWidth(), ss.getHeight());
		} else if (s instanceof Spiral) {
			Spiral ss = (Spiral)s;
			return new SpiralDrawObject(ps, ss.getSides(), ss.getSpacing(), ss.getSpokes(), ss.getCenterX(), ss.getCenterY(), ss.getEndpointX(), ss.getEndpointY());
		} else {
			return ShapeDrawObject.forShape(ps, s);
		}
	}
}
