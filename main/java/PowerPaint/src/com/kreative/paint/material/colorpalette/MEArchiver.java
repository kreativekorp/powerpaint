package com.kreative.paint.material.colorpalette;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

// Based on MEUnarchiver.m by Frank Illenberger (github.com/depth42).

public class MEArchiver {
	private static final byte SHORT = -127;
	private static final byte INT   = -126;
	private static final byte REAL  = -125;
	private static final byte NEW   = -124;
	private static final byte NULL  = -123;
	private static final byte END   = -122;
	private static final byte BIAS  = -110;
	
	private DataOutputStream out;
	private List<String> strings;
	private Map<Object,Integer> objects;
	private int objectCounter;
	private Map<String,Integer> classes;
	
	public MEArchiver(OutputStream out) throws IOException {
		this.out = new DataOutputStream(out);
		this.strings = new ArrayList<String>();
		this.objects = new IdentityHashMap<Object,Integer>();
		this.classes = new HashMap<String,Integer>();
		writeHeader();
	}
	
	private void writeHeader() throws IOException {
		out.writeByte(4);
		writeRawString("typedstream");
		writeInt(1000);
	}
	
	private void writeObject(Object o) throws IOException {
		if (o == null) {
			out.writeByte(NULL);
		} else if (objects.containsKey(o)) {
			int label = objects.get(o).intValue();
			writeInt(label + BIAS);
		} else {
			int label = objectCounter++;
			objects.put(o, label);
			out.writeByte(NEW);
			if (o instanceof String) {
				writeClass("NSString", 1, "NSObject", 0);
				byte[] data = o.toString().getBytes("UTF-8");
				writeBytes(data);
			} else if (o instanceof MEColor) {
				writeClass("NSColor", 0, "NSObject", 0);
				((MEColor)o).archiveTo(this);
			} else {
				throw new IOException("Unknown object class " + o.getClass());
			}
			out.writeByte(END);
		}
	}
	
	private void writeClass(Object... args) throws IOException {
		for (int i = 0, j = 1; j < args.length; j += 2, i += 2) {
			String cls = "[" + args[i].toString() + " " + args[j].toString() + "]";
			if (classes.containsKey(cls)) {
				int label = classes.get(cls).intValue();
				writeInt(label + BIAS);
				return;
			} else {
				int label = objectCounter++;
				classes.put(cls, label);
				out.writeByte(NEW);
				writeSharedString(args[i].toString());
				writeInt(((Number)args[j]).intValue());
				continue; // superclass
			}
		}
		out.writeByte(NULL);
	}
	
	private void writeFloat(float f) throws IOException {
		if (f == (int)f) {
			writeInt((int)f);
		} else {
			out.writeByte(REAL);
			out.writeFloat(f);
		}
	}
	
	private void writeDouble(double d) throws IOException {
		if (d == (int)d) {
			writeInt((int)d);
		} else {
			out.writeByte(REAL);
			out.writeDouble(d);
		}
	}
	
	private void writeRawString(String s) throws IOException {
		if (s == null) {
			out.writeByte(NULL);
		} else {
			byte[] data = s.getBytes("UTF-8");
			writeInt(data.length);
			out.write(data);
		}
	}
	
	private void writeString(String s) throws IOException {
		if (s == null) {
			out.writeByte(NULL);
		} else if (objects.containsKey(s)) {
			int label = objects.get(s).intValue();
			writeInt(label + BIAS);
		} else {
			int label = objectCounter++;
			objects.put(s, label);
			out.writeByte(NEW);
			writeSharedString(s);
		}
	}
	
	private void writeSharedString(String s) throws IOException {
		if (s == null) {
			out.writeByte(NULL);
		} else if (strings.contains(s)) {
			int index = strings.indexOf(s);
			writeInt(index + BIAS);
		} else {
			strings.add(s);
			out.writeByte(NEW);
			writeRawString(s);
		}
	}
	
	private void writeShort(short s) throws IOException {
		if (s > BIAS && s < 128) {
			out.writeByte(s);
		} else {
			out.writeByte(SHORT);
			out.writeShort(s);
		}
	}
	
	private void writeInt(int i) throws IOException {
		if (i > BIAS && i < 128) {
			out.writeByte(i);
		} else if (i >= Short.MIN_VALUE && i <= Short.MAX_VALUE) {
			out.writeByte(SHORT);
			out.writeShort(i);
		} else {
			out.writeByte(INT);
			out.writeInt(i);
		}
	}
	
	private void writeType(char type, Object o) throws IOException {
		switch (type) {
			case 'C': case 'c': out.writeByte(((Number)o).byteValue()); break;
			case 'S': case 's': writeShort(((Number)o).shortValue()); break;
			case 'I': case 'i': writeInt(((Number)o).intValue()); break;
			case 'L': case 'l': writeInt(((Number)o).intValue()); break;
			case 'f': writeFloat(((Number)o).floatValue()); break;
			case 'd': writeDouble(((Number)o).doubleValue()); break;
			case '@': writeObject(o); break;
			case '*': writeString(o.toString()); break;
			default: throw new IOException("Bad archiving type " + type);
		}
	}
	
	public void writeValueOfType(String type, Object o) throws IOException {
		writeSharedString(type);
		writeType(type.charAt(0), o);
	}
	
	public void writeValuesOfTypes(String types, Object... o) throws IOException {
		writeSharedString(types);
		int n = types.length();
		for (int i = 0; i < n; i++) writeType(types.charAt(i), o[i]);
	}
	
	public void writeBytes(byte[] data) throws IOException {
		writeSharedString("+");
		writeInt(data.length);
		out.write(data);
	}
}
