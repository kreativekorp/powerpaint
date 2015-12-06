package com.kreative.paint.io;

import java.awt.geom.Rectangle2D;
import java.io.*;
import com.kreative.paint.material.gradient.*;

public class CKPGradientSerializer extends Serializer {
	private static final int TYPE_GRADIENT_COLOR_RGB = fcc("GC03");
	private static final int TYPE_GRADIENT_COLOR_RGB16 = fcc("GC06");
	private static final int TYPE_GRADIENT_COLOR_RGBA = fcc("GC04");
	private static final int TYPE_GRADIENT_COLOR_RGBA16 = fcc("GC08");
	private static final int TYPE_GRADIENT_COLOR_HSV = fcc("GC12");
	private static final int TYPE_GRADIENT_COLOR_HSVA = fcc("GC16");
	private static final int TYPE_GRADIENT_COLOR_MAP = fcc("GCMp");
	private static final int TYPE_GRADIENT_COLOR_STOP = fcc("GCSp");
	private static final int TYPE_GRADIENT_LIST = fcc("GLst");
	private static final int TYPE_GRADIENT_PAINT_2 = fcc("GPn2");
	private static final int TYPE_GRADIENT_PRESET = fcc("Grd2");
	private static final int TYPE_GRADIENT_SHAPE_LINEAR = fcc("$Lin");
	private static final int TYPE_GRADIENT_SHAPE_ANGULAR = fcc("$Ang");
	private static final int TYPE_GRADIENT_SHAPE_RADIAL = fcc("$Rad");
	private static final int TYPE_GRADIENT_SHAPE_RECTANGULAR = fcc("$Rec");
	
	protected void loadRecognizedTypesAndClasses() {
		addTypeAndClass(TYPE_GRADIENT_COLOR_RGB, 1, GradientColor.RGB.class);
		addTypeAndClass(TYPE_GRADIENT_COLOR_RGB16, 1, GradientColor.RGB16.class);
		addTypeAndClass(TYPE_GRADIENT_COLOR_RGBA, 1, GradientColor.RGBA.class);
		addTypeAndClass(TYPE_GRADIENT_COLOR_RGBA16, 1, GradientColor.RGBA16.class);
		addTypeAndClass(TYPE_GRADIENT_COLOR_HSV, 1, GradientColor.HSV.class);
		addTypeAndClass(TYPE_GRADIENT_COLOR_HSVA, 1, GradientColor.HSVA.class);
		addTypeAndClass(TYPE_GRADIENT_COLOR_MAP, 2, GradientColorMap.class);
		addTypeAndClass(TYPE_GRADIENT_COLOR_STOP, 1, GradientColorStop.class);
		addTypeAndClass(TYPE_GRADIENT_LIST, 1, GradientList.class);
		addTypeAndClass(TYPE_GRADIENT_PAINT_2, 1, GradientPaint2.class);
		addTypeAndClass(TYPE_GRADIENT_PRESET, 1, GradientPreset.class);
		addTypeAndClass(TYPE_GRADIENT_SHAPE_LINEAR, 2, GradientShape.Linear.class);
		addTypeAndClass(TYPE_GRADIENT_SHAPE_ANGULAR, 2, GradientShape.Angular.class);
		addTypeAndClass(TYPE_GRADIENT_SHAPE_RADIAL, 2, GradientShape.Radial.class);
		addTypeAndClass(TYPE_GRADIENT_SHAPE_RECTANGULAR, 2, GradientShape.Rectangular.class);
	}
	
	public void serializeObject(Object o, DataOutputStream stream) throws IOException {
		if (o instanceof GradientColor.RGB) {
			GradientColor.RGB v = (GradientColor.RGB)o;
			stream.writeByte(v.r);
			stream.writeByte(v.g);
			stream.writeByte(v.b);
		} else if (o instanceof GradientColor.RGB16) {
			GradientColor.RGB16 v = (GradientColor.RGB16)o;
			stream.writeShort(v.r);
			stream.writeShort(v.g);
			stream.writeShort(v.b);
		} else if (o instanceof GradientColor.RGBA) {
			GradientColor.RGBA v = (GradientColor.RGBA)o;
			stream.writeByte(v.r);
			stream.writeByte(v.g);
			stream.writeByte(v.b);
			stream.writeByte(v.a);
		} else if (o instanceof GradientColor.RGBA16) {
			GradientColor.RGBA16 v = (GradientColor.RGBA16)o;
			stream.writeShort(v.r);
			stream.writeShort(v.g);
			stream.writeShort(v.b);
			stream.writeShort(v.a);
		} else if (o instanceof GradientColor.HSV) {
			GradientColor.HSV v = (GradientColor.HSV)o;
			stream.writeFloat(v.h);
			stream.writeFloat(v.s);
			stream.writeFloat(v.v);
		} else if (o instanceof GradientColor.HSVA) {
			GradientColor.HSVA v = (GradientColor.HSVA)o;
			stream.writeFloat(v.h);
			stream.writeFloat(v.s);
			stream.writeFloat(v.v);
			stream.writeFloat(v.a);
		} else if (o instanceof GradientColorMap) {
			GradientColorMap v = (GradientColorMap)o;
			stream.writeUTF((v.name != null) ? v.name : "");
			stream.writeInt(v.size());
			for (GradientColorStop stop : v) {
				SerializationManager.writeObject(stop, stream);
			}
		} else if (o instanceof GradientColorStop) {
			GradientColorStop v = (GradientColorStop)o;
			stream.writeDouble(v.position);
			SerializationManager.writeObject(v.color, stream);
		} else if (o instanceof GradientList) {
			GradientList v = (GradientList)o;
			stream.writeUTF((v.name != null) ? v.name : "");
			stream.writeInt(v.presets.size());
			stream.writeInt(v.shapes.size());
			stream.writeInt(v.colorMaps.size());
			for (GradientPreset preset : v.presets) {
				SerializationManager.writeObject(preset, stream);
			}
			for (GradientShape shape : v.shapes) {
				SerializationManager.writeObject(shape, stream);
			}
			for (GradientColorMap colorMap : v.colorMaps) {
				SerializationManager.writeObject(colorMap, stream);
			}
		} else if (o instanceof GradientPaint2) {
			GradientPaint2 v = (GradientPaint2)o;
			SerializationManager.writeObject(v.shape, stream);
			SerializationManager.writeObject(v.colorMap, stream);
			SerializationManager.writeObject(v.boundingRect, stream);
		} else if (o instanceof GradientPreset) {
			GradientPreset v = (GradientPreset)o;
			stream.writeUTF((v.name != null) ? v.name : "");
			SerializationManager.writeObject(v.shape, stream);
			SerializationManager.writeObject(v.colorMap, stream);
		} else if (o instanceof GradientShape.Linear) {
			GradientShape.Linear v = (GradientShape.Linear)o;
			stream.writeDouble(v.x0);
			stream.writeDouble(v.y0);
			stream.writeDouble(v.x1);
			stream.writeDouble(v.y1);
			stream.writeBoolean(v.repeat);
			stream.writeBoolean(v.reflect);
			stream.writeBoolean(v.reverse);
			stream.writeUTF((v.name != null) ? v.name : "");
		} else if (o instanceof GradientShape.Angular) {
			GradientShape.Angular v = (GradientShape.Angular)o;
			stream.writeDouble(v.cx);
			stream.writeDouble(v.cy);
			stream.writeDouble(v.px);
			stream.writeDouble(v.py);
			stream.writeBoolean(v.repeat);
			stream.writeBoolean(v.reflect);
			stream.writeBoolean(v.reverse);
			stream.writeUTF((v.name != null) ? v.name : "");
		} else if (o instanceof GradientShape.Radial) {
			GradientShape.Radial v = (GradientShape.Radial)o;
			stream.writeDouble(v.cx);
			stream.writeDouble(v.cy);
			stream.writeDouble(v.x0);
			stream.writeDouble(v.y0);
			stream.writeDouble(v.x1);
			stream.writeDouble(v.y1);
			stream.writeBoolean(v.repeat);
			stream.writeBoolean(v.reflect);
			stream.writeBoolean(v.reverse);
			stream.writeUTF((v.name != null) ? v.name : "");
		} else if (o instanceof GradientShape.Rectangular) {
			GradientShape.Rectangular v = (GradientShape.Rectangular)o;
			stream.writeDouble(v.l0);
			stream.writeDouble(v.t0);
			stream.writeDouble(v.r0);
			stream.writeDouble(v.b0);
			stream.writeDouble(v.l1);
			stream.writeDouble(v.t1);
			stream.writeDouble(v.r1);
			stream.writeDouble(v.b1);
			stream.writeBoolean(v.repeat);
			stream.writeBoolean(v.reflect);
			stream.writeBoolean(v.reverse);
			stream.writeUTF((v.name != null) ? v.name : "");
		}
	}
	
	public Object deserializeObject(int type, int version, DataInputStream stream) throws IOException {
		if (type == TYPE_GRADIENT_COLOR_RGB) {
			if (version != 1) throw new IOException("Invalid version number.");
			int r = stream.readUnsignedByte();
			int g = stream.readUnsignedByte();
			int b = stream.readUnsignedByte();
			return new GradientColor.RGB(r, g, b);
		} else if (type == TYPE_GRADIENT_COLOR_RGB16) {
			if (version != 1) throw new IOException("Invalid version number.");
			int r = stream.readUnsignedShort();
			int g = stream.readUnsignedShort();
			int b = stream.readUnsignedShort();
			return new GradientColor.RGB16(r, g, b);
		} else if (type == TYPE_GRADIENT_COLOR_RGBA) {
			if (version != 1) throw new IOException("Invalid version number.");
			int r = stream.readUnsignedByte();
			int g = stream.readUnsignedByte();
			int b = stream.readUnsignedByte();
			int a = stream.readUnsignedByte();
			return new GradientColor.RGBA(r, g, b, a);
		} else if (type == TYPE_GRADIENT_COLOR_RGBA16) {
			if (version != 1) throw new IOException("Invalid version number.");
			int r = stream.readUnsignedShort();
			int g = stream.readUnsignedShort();
			int b = stream.readUnsignedShort();
			int a = stream.readUnsignedShort();
			return new GradientColor.RGBA16(r, g, b, a);
		} else if (type == TYPE_GRADIENT_COLOR_HSV) {
			if (version != 1) throw new IOException("Invalid version number.");
			float h = stream.readFloat();
			float s = stream.readFloat();
			float v = stream.readFloat();
			return new GradientColor.HSV(h, s, v);
		} else if (type == TYPE_GRADIENT_COLOR_HSVA) {
			if (version != 1) throw new IOException("Invalid version number.");
			float h = stream.readFloat();
			float s = stream.readFloat();
			float v = stream.readFloat();
			float a = stream.readFloat();
			return new GradientColor.HSVA(h, s, v, a);
		} else if (type == TYPE_GRADIENT_COLOR_MAP) {
			GradientColorMap gcm;
			int n;
			switch (version) {
			case 1:
				gcm = new GradientColorMap(null);
				n = stream.readInt();
				for (int i = 0; i < n; i++) {
					double p = stream.readDouble();
					int r = (int)Math.round(stream.readFloat() * 65535.0f);
					int g = (int)Math.round(stream.readFloat() * 65535.0f);
					int b = (int)Math.round(stream.readFloat() * 65535.0f);
					int a = (int)Math.round(stream.readFloat() * 65535.0f);
					gcm.add(new GradientColorStop(p, new GradientColor.RGBA16(r,g,b,a)));
				}
				return gcm;
			case 2:
				String name = stream.readUTF();
				gcm = new GradientColorMap((name.length() > 0) ? name : null);
				n = stream.readInt();
				for (int i = 0; i < n; i++) {
					gcm.add((GradientColorStop)SerializationManager.readObject(stream));
				}
				return gcm;
			default:
				throw new IOException("Invalid version number.");
			}
		} else if (type == TYPE_GRADIENT_COLOR_STOP) {
			if (version != 1) throw new IOException("Invalid version number.");
			double position = stream.readDouble();
			GradientColor color = (GradientColor)SerializationManager.readObject(stream);
			return new GradientColorStop(position, color);
		} else if (type == TYPE_GRADIENT_LIST) {
			if (version != 1) throw new IOException("Invalid version number.");
			String name = stream.readUTF();
			GradientList list = new GradientList((name.length() > 0) ? name : null);
			int numPresets = stream.readInt();
			int numShapes = stream.readInt();
			int numColorMaps = stream.readInt();
			for (int i = 0; i < numPresets; i++) {
				list.presets.add((GradientPreset)SerializationManager.readObject(stream));
			}
			for (int i = 0; i < numShapes; i++) {
				list.shapes.add((GradientShape)SerializationManager.readObject(stream));
			}
			for (int i = 0; i < numColorMaps; i++) {
				list.colorMaps.add((GradientColorMap)SerializationManager.readObject(stream));
			}
			return list;
		} else if (type == TYPE_GRADIENT_PAINT_2) {
			if (version != 1) throw new IOException("Invalid version number.");
			GradientShape gs = (GradientShape)SerializationManager.readObject(stream);
			GradientColorMap gc = (GradientColorMap)SerializationManager.readObject(stream);
			Rectangle2D gb = (Rectangle2D)SerializationManager.readObject(stream);
			return new GradientPaint2(gs, gc, gb);
		} else if (type == TYPE_GRADIENT_PRESET) {
			if (version != 1) throw new IOException("Invalid version number.");
			String name = stream.readUTF();
			GradientShape gs = (GradientShape)SerializationManager.readObject(stream);
			GradientColorMap gc = (GradientColorMap)SerializationManager.readObject(stream);
			return new GradientPreset(gs, gc, (name.length() > 0) ? name : null);
		} else if (type == TYPE_GRADIENT_SHAPE_LINEAR) {
			if (version < 1 || version > 2) throw new IOException("Invalid version number.");
			double zx = stream.readDouble();
			double zy = stream.readDouble();
			double ox = stream.readDouble();
			double oy = stream.readDouble();
			boolean rep = stream.readBoolean();
			boolean ref = stream.readBoolean();
			boolean rev = stream.readBoolean();
			String name = (version >= 2) ? stream.readUTF() : "";
			return new GradientShape.Linear(
				zx, zy, ox, oy, rep, ref, rev,
				(name.length() > 0) ? name : null
			);
		} else if (type == TYPE_GRADIENT_SHAPE_ANGULAR) {
			if (version < 1 || version > 2) throw new IOException("Invalid version number.");
			double cx = stream.readDouble();
			double cy = stream.readDouble();
			double px = stream.readDouble();
			double py = stream.readDouble();
			boolean rep = stream.readBoolean();
			boolean ref = stream.readBoolean();
			boolean rev = stream.readBoolean();
			String name = (version >= 2) ? stream.readUTF() : "";
			return new GradientShape.Angular(
				cx, cy, px, py, rep, ref, rev,
				(name.length() > 0) ? name : null
			);
		} else if (type == TYPE_GRADIENT_SHAPE_RADIAL) {
			if (version < 1 || version > 2) throw new IOException("Invalid version number.");
			double cx = stream.readDouble();
			double cy = stream.readDouble();
			double zx = stream.readDouble();
			double zy = stream.readDouble();
			double ox = stream.readDouble();
			double oy = stream.readDouble();
			boolean rep = stream.readBoolean();
			boolean ref = stream.readBoolean();
			boolean rev = stream.readBoolean();
			String name = (version >= 2) ? stream.readUTF() : "";
			return new GradientShape.Radial(
				cx, cy, zx, zy, ox, oy, rep, ref, rev,
				(name.length() > 0) ? name : null
			);
		} else if (type == TYPE_GRADIENT_SHAPE_RECTANGULAR) {
			if (version < 1 || version > 2) throw new IOException("Invalid version number.");
			double zx1 = stream.readDouble();
			double zy1 = stream.readDouble();
			double zx2 = stream.readDouble();
			double zy2 = stream.readDouble();
			double ox1 = stream.readDouble();
			double oy1 = stream.readDouble();
			double ox2 = stream.readDouble();
			double oy2 = stream.readDouble();
			boolean rep = stream.readBoolean();
			boolean ref = stream.readBoolean();
			boolean rev = stream.readBoolean();
			String name = (version >= 2) ? stream.readUTF() : "";
			return new GradientShape.Rectangular(
				zx1, zy1, zx2, zy2,
				ox1, oy1, ox2, oy2,
				rep, ref, rev,
				(name.length() > 0) ? name : null
			);
		} else {
			return null;
		}
	}
}
