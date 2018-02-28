package com.kreative.paint.material.colorpalette;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Based on MEUnarchiver.m by Frank Illenberger (github.com/depth42).

public class MEUnarchiver {
	private static final byte SHORT = -127;
	private static final byte INT   = -126;
	private static final byte REAL  = -125;
	private static final byte NEW   = -124;
	private static final byte NULL  = -123;
	private static final byte END   = -122;
	private static final byte BIAS  = -110;
	
	private DataInputStream in;
	private byte version;
	private boolean swap;
	private int system;
	private List<String> strings;
	private Map<Integer,Object> objects;
	private int objectCounter;
	private Map<String,Integer> classVersions;
	
	public MEUnarchiver(InputStream in) throws IOException {
		this.in = new DataInputStream(in);
		this.strings = new ArrayList<String>();
		this.objects = new HashMap<Integer,Object>();
		this.classVersions = new HashMap<String,Integer>();
		readHeader();
	}
	
	private void readHeader() throws IOException {
		version = in.readByte();
		if (version != 4) throw new IOException("Bad header");
		String h = readRawString();
		if (h == null) throw new IOException("Bad header");
		swap = h.equals("streamtyped");
		if (!(swap || h.equals("typedstream"))) throw new IOException("Bad header");
		system = readInt();
		if (system != 1000) throw new IOException("Bad header");
	}
	
	private Object readObject() throws IOException {
		byte b = in.readByte();
		if (b == NULL) {
			return null;
		} else if (b == NEW) {
			int label = objectCounter++;
			String cls = readClass();
			Object o;
			if (cls == null) {
				throw new IOException("Null object class");
			} else if (cls.equals("NSString")) {
				byte[] data = readBytes();
				o = new String(data, "UTF-8");
			} else if (cls.equals("NSColor")) {
				try {
					o = MEColor.unarchiveFrom(this);
				} catch (IOException e) {
					String msg = e.getMessage();
					if (msg.startsWith("Unknown color space")) {
						msg += " requests " + readSharedString();
						throw new IOException(msg);
					} else {
						throw e;
					}
				}
			} else {
				throw new IOException("Unknown object class " + cls);
			}
			objects.put(label, o);
			b = in.readByte();
			if (b != END) throw new IOException("Expected end of object but found " + b);
			return o;
		} else {
			int label = finishInt(b) - BIAS;
			return objects.get(label);
		}
	}
	
	private String readClass() throws IOException {
		byte b = in.readByte();
		if (b == NULL) {
			return null;
		} else if (b == NEW) {
			String name = readSharedString();
			int version = readInt();
			classVersions.put(name, version);
			objects.put(objectCounter++, name);
			readClass(); // superclass
			return name;
		} else {
			int label = finishInt(b) - BIAS;
			Object o = objects.get(label);
			return (o == null) ? null : o.toString();
		}
	}
	
	private float readFloat() throws IOException {
		byte b = in.readByte();
		if (b != REAL) return finishInt(b);
		int bits = in.readInt();
		if (swap) bits = Integer.reverseBytes(bits);
		return Float.intBitsToFloat(bits);
	}
	
	private double readDouble() throws IOException {
		byte b = in.readByte();
		if (b != REAL) return finishInt(b);
		long bits = in.readLong();
		if (swap) bits = Long.reverseBytes(bits);
		return Double.longBitsToDouble(bits);
	}
	
	private String readRawString() throws IOException {
		byte b = in.readByte();
		if (b == NULL) {
			return null;
		} else {
			int length = finishInt(b);
			if (length < 0) return null;
			byte[] data = new byte[length];
			in.readFully(data);
			return new String(data, "UTF-8");
		}
	}
	
	private String readString() throws IOException {
		byte b = in.readByte();
		if (b == NULL) {
			return null;
		} else if (b == NEW) {
			String s = readSharedString();
			objects.put(objectCounter++, s);
			return s;
		} else {
			int label = finishInt(b) - BIAS;
			Object o = objects.get(label);
			return (o == null) ? null : o.toString();
		}
	}
	
	private String readSharedString() throws IOException {
		byte b = in.readByte();
		if (b == NULL) {
			return null;
		} else if (b == NEW) {
			String s = readRawString();
			strings.add(s);
			return s;
		} else {
			int index = finishInt(b) - BIAS;
			if (index < 0 || index >= strings.size()) return null;
			return strings.get(index);
		}
	}
	
	private short readShort() throws IOException {
		byte b = in.readByte();
		if (b != SHORT) return b;
		short s = in.readShort();
		return swap ? Short.reverseBytes(s) : s;
	}
	
	private int readInt() throws IOException {
		byte b = in.readByte();
		return finishInt(b);
	}
	
	private int finishInt(byte b) throws IOException {
		switch (b) {
			case SHORT:
				short s = in.readShort();
				return swap ? Short.reverseBytes(s) : s;
			case INT:
				int i = in.readInt();
				return swap ? Integer.reverseBytes(i) : i;
			default:
				return b;
		}
	}
	
	private Object readType(char type) throws IOException {
		switch (type) {
			case 'C': case 'c': return in.readByte();
			case 'S': case 's': return readShort();
			case 'I': case 'i': return readInt();
			case 'L': case 'l': return readInt();
			case 'f': return readFloat();
			case 'd': return readDouble();
			case '@': return readObject();
			case '*': return readString();
			default: throw new IOException("Bad archiving type " + type);
		}
	}
	
	public Object readValueOfType(String type) throws IOException {
		String s = readSharedString();
		if (s == null || s.length() == 0) throw new IOException("Expected " + type + " but found null");
		if (!s.equals(type)) throw new IOException("Expected " + type + " but found " + s);
		return readType(s.charAt(0));
	}
	
	public Object[] readValuesOfTypes(String types) throws IOException {
		String s = readSharedString();
		if (s == null || s.length() == 0) throw new IOException("Expected " + types + " but found null");
		if (!s.equals(types)) throw new IOException("Expected " + types + " but found " + s);
		int n = types.length();
		Object[] objects = new Object[n];
		for (int i = 0; i < n; i++) objects[i] = readType(s.charAt(i));
		return objects;
	}
	
	public byte[] readBytes() throws IOException {
		String s = readSharedString();
		if (s == null || !s.equals("+")) throw new IOException("Expected + but found " + s);
		int length = readInt();
		if (length < 0) return null;
		byte[] data = new byte[length];
		in.readFully(data);
		return data;
	}
	
	public static void main(String[] args) {
		for (String arg : args) {
			try {
				File file = new File(arg);
				FileInputStream in = new FileInputStream(file);
				MEUnarchiver u = new MEUnarchiver(in);
				while (in.available() > 0) {
					String s = u.readSharedString();
					System.out.println(s);
					int n = s.length();
					for (int i = 0; i < n; i++) {
						Object o = u.readType(s.charAt(i));
						System.out.println("\t" + o);
					}
				}
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
