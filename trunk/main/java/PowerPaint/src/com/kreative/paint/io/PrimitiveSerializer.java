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

import java.io.*;

/* This more or less covers the java.lang package. */
public class PrimitiveSerializer extends Serializer {
	private static final int TYPE_BYTE_PRIM = fcc("byte");
	private static final int TYPE_SHORT_PRIM = fcc("shor");
	private static final int TYPE_INT_PRIM = fcc("inte");
	private static final int TYPE_LONG_PRIM = fcc("long");
	private static final int TYPE_FLOAT_PRIM = fcc("floa");
	private static final int TYPE_DOUBLE_PRIM = fcc("doub");
	private static final int TYPE_BOOLEAN_PRIM = fcc("bool");
	private static final int TYPE_CHAR_PRIM = fcc("char");
	private static final int TYPE_BYTE_OBJ = fcc("Byte");
	private static final int TYPE_SHORT_OBJ = fcc("Shor");
	private static final int TYPE_INTEGER_OBJ = fcc("Inte");
	private static final int TYPE_LONG_OBJ = fcc("Long");
	private static final int TYPE_FLOAT_OBJ = fcc("Floa");
	private static final int TYPE_DOUBLE_OBJ = fcc("Doub");
	private static final int TYPE_BOOLEAN_OBJ = fcc("Bool");
	private static final int TYPE_CHARACTER_OBJ = fcc("Char");
	private static final int TYPE_STRING_OBJ = fcc("Stri");
	private static final int TYPE_STRING_BUFFER = fcc("StBf");
	private static final int TYPE_CLASS = fcc("Clas");
	//private static final int TYPE_ENUM = fcc("Enum");
	private static final int TYPE_BYTE_PRIM_ARRAY = fcc("byt#");
	private static final int TYPE_SHORT_PRIM_ARRAY = fcc("sho#");
	private static final int TYPE_INT_PRIM_ARRAY = fcc("int#");
	private static final int TYPE_LONG_PRIM_ARRAY = fcc("lon#");
	private static final int TYPE_FLOAT_PRIM_ARRAY = fcc("flo#");
	private static final int TYPE_DOUBLE_PRIM_ARRAY = fcc("dou#");
	private static final int TYPE_BOOLEAN_PRIM_ARRAY = fcc("boo#");
	private static final int TYPE_CHAR_PRIM_ARRAY = fcc("cha#");
	private static final int TYPE_BYTE_OBJ_ARRAY = fcc("Byt#");
	private static final int TYPE_SHORT_OBJ_ARRAY = fcc("Sho#");
	private static final int TYPE_INTEGER_OBJ_ARRAY = fcc("Int#");
	private static final int TYPE_LONG_OBJ_ARRAY = fcc("Lon#");
	private static final int TYPE_FLOAT_OBJ_ARRAY = fcc("Flo#");
	private static final int TYPE_DOUBLE_OBJ_ARRAY = fcc("Dou#");
	private static final int TYPE_BOOLEAN_OBJ_ARRAY = fcc("Boo#");
	private static final int TYPE_CHARACTER_OBJ_ARRAY = fcc("Cha#");
	private static final int TYPE_STRING_OBJ_ARRAY = fcc("Str#");
	private static final int TYPE_STRING_BUFFER_ARRAY = fcc("StB#");
	private static final int TYPE_CLASS_ARRAY = fcc("Cla#");
	//private static final int TYPE_ENUM_ARRAY = fcc("Enu#");
	
	protected void loadRecognizedTypesAndClasses() {
		addTypeAndClass(TYPE_BYTE_PRIM, 1, byte.class);
		addTypeAndClass(TYPE_SHORT_PRIM, 1, short.class);
		addTypeAndClass(TYPE_INT_PRIM, 1, int.class);
		addTypeAndClass(TYPE_LONG_PRIM, 1, long.class);
		addTypeAndClass(TYPE_FLOAT_PRIM, 1, float.class);
		addTypeAndClass(TYPE_DOUBLE_PRIM, 1, double.class);
		addTypeAndClass(TYPE_BOOLEAN_PRIM, 1, boolean.class);
		addTypeAndClass(TYPE_CHAR_PRIM, 1, char.class);
		addTypeAndClass(TYPE_BYTE_OBJ, 1, Byte.class);
		addTypeAndClass(TYPE_SHORT_OBJ, 1, Short.class);
		addTypeAndClass(TYPE_INTEGER_OBJ, 1, Integer.class);
		addTypeAndClass(TYPE_LONG_OBJ, 1, Long.class);
		addTypeAndClass(TYPE_FLOAT_OBJ, 1, Float.class);
		addTypeAndClass(TYPE_DOUBLE_OBJ, 1, Double.class);
		addTypeAndClass(TYPE_BOOLEAN_OBJ, 1, Boolean.class);
		addTypeAndClass(TYPE_CHARACTER_OBJ, 1, Character.class);
		addTypeAndClass(TYPE_STRING_OBJ, 1, String.class);
		addTypeAndClass(TYPE_STRING_BUFFER, 1, StringBuffer.class);
		addTypeAndClass(TYPE_CLASS, 1, Class.class);
		//addTypeAndClass(TYPE_ENUM, 1, Enum.class);
		addTypeAndClass(TYPE_BYTE_PRIM_ARRAY, 1, byte[].class);
		addTypeAndClass(TYPE_SHORT_PRIM_ARRAY, 1, short[].class);
		addTypeAndClass(TYPE_INT_PRIM_ARRAY, 1, int[].class);
		addTypeAndClass(TYPE_LONG_PRIM_ARRAY, 1, long[].class);
		addTypeAndClass(TYPE_FLOAT_PRIM_ARRAY, 1, float[].class);
		addTypeAndClass(TYPE_DOUBLE_PRIM_ARRAY, 1, double[].class);
		addTypeAndClass(TYPE_BOOLEAN_PRIM_ARRAY, 1, boolean[].class);
		addTypeAndClass(TYPE_CHAR_PRIM_ARRAY, 1, char[].class);
		addTypeAndClass(TYPE_BYTE_OBJ_ARRAY, 1, Byte[].class);
		addTypeAndClass(TYPE_SHORT_OBJ_ARRAY, 1, Short[].class);
		addTypeAndClass(TYPE_INTEGER_OBJ_ARRAY, 1, Integer[].class);
		addTypeAndClass(TYPE_LONG_OBJ_ARRAY, 1, Long[].class);
		addTypeAndClass(TYPE_FLOAT_OBJ_ARRAY, 1, Float[].class);
		addTypeAndClass(TYPE_DOUBLE_OBJ_ARRAY, 1, Double[].class);
		addTypeAndClass(TYPE_BOOLEAN_OBJ_ARRAY, 1, Boolean[].class);
		addTypeAndClass(TYPE_CHARACTER_OBJ_ARRAY, 1, Character[].class);
		addTypeAndClass(TYPE_STRING_OBJ_ARRAY, 1, String[].class);
		addTypeAndClass(TYPE_STRING_BUFFER_ARRAY, 1, StringBuffer[].class);
		addTypeAndClass(TYPE_CLASS_ARRAY, 1, Class[].class);
		//addTypeAndClass(TYPE_ENUM_ARRAY, 1, Enum[].class);
	}

	public void serializeObject(Object o, DataOutputStream stream) throws IOException {
		if (o instanceof Byte) stream.writeByte((Byte)o);
		else if (o instanceof Short) stream.writeShort((Short)o);
		else if (o instanceof Integer) stream.writeInt((Integer)o);
		else if (o instanceof Long) stream.writeLong((Long)o);
		else if (o instanceof Float) stream.writeFloat((Float)o);
		else if (o instanceof Double) stream.writeDouble((Double)o);
		else if (o instanceof Boolean) stream.writeBoolean((Boolean)o);
		else if (o instanceof Character) stream.writeChar((Character)o);
		else if (o instanceof String) stream.writeUTF((String)o);
		else if (o instanceof StringBuffer) stream.writeUTF(((StringBuffer)o).toString());
		else if (o instanceof Class) stream.writeUTF(((Class<?>)o).getCanonicalName());
		else if (o instanceof byte[]) {
			stream.writeInt(((byte[])o).length);
			for (byte v : (byte[])o) stream.writeByte(v);
		}
		else if (o instanceof short[]) {
			stream.writeInt(((short[])o).length);
			for (short v : (short[])o) stream.writeShort(v);
		}
		else if (o instanceof int[]) {
			stream.writeInt(((int[])o).length);
			for (int v : (int[])o) stream.writeInt(v);
		}
		else if (o instanceof long[]) {
			stream.writeInt(((long[])o).length);
			for (long v : (long[])o) stream.writeLong(v);
		}
		else if (o instanceof float[]) {
			stream.writeInt(((float[])o).length);
			for (float v : (float[])o) stream.writeFloat(v);
		}
		else if (o instanceof double[]) {
			stream.writeInt(((double[])o).length);
			for (double v : (double[])o) stream.writeDouble(v);
		}
		else if (o instanceof boolean[]) {
			stream.writeInt(((boolean[])o).length);
			for (boolean v : (boolean[])o) stream.writeBoolean(v);
		}
		else if (o instanceof char[]) {
			stream.writeInt(((char[])o).length);
			for (char v : (char[])o) stream.writeChar(v);
		}
		else if (o instanceof Byte[]) {
			stream.writeInt(((Byte[])o).length);
			for (byte v : (Byte[])o) stream.writeByte(v);
		}
		else if (o instanceof Short[]) {
			stream.writeInt(((Short[])o).length);
			for (short v : (Short[])o) stream.writeShort(v);
		}
		else if (o instanceof Integer[]) {
			stream.writeInt(((Integer[])o).length);
			for (int v : (Integer[])o) stream.writeInt(v);
		}
		else if (o instanceof Long[]) {
			stream.writeInt(((Long[])o).length);
			for (long v : (Long[])o) stream.writeLong(v);
		}
		else if (o instanceof Float[]) {
			stream.writeInt(((Float[])o).length);
			for (float v : (Float[])o) stream.writeFloat(v);
		}
		else if (o instanceof Double[]) {
			stream.writeInt(((Double[])o).length);
			for (double v : (Double[])o) stream.writeDouble(v);
		}
		else if (o instanceof Boolean[]) {
			stream.writeInt(((Boolean[])o).length);
			for (boolean v : (Boolean[])o) stream.writeBoolean(v);
		}
		else if (o instanceof Character[]) {
			stream.writeInt(((Character[])o).length);
			for (char v : (Character[])o) stream.writeChar(v);
		}
		else if (o instanceof String[]) {
			stream.writeInt(((String[])o).length);
			for (String s : (String[])o) stream.writeUTF(s);
		}
		else if (o instanceof StringBuffer[]) {
			stream.writeInt(((StringBuffer[])o).length);
			for (StringBuffer s : (StringBuffer[])o) stream.writeUTF(s.toString());
		}
		else if (o instanceof Class[]) {
			stream.writeInt(((Class[])o).length);
			for (Class<?> c : (Class[])o) stream.writeUTF(c.getCanonicalName());
		}
	}
	
	public Object deserializeObject(int type, int version, DataInputStream stream) throws IOException {
		if (version != 1) throw new IOException("Invalid version number.");
		else if (type == TYPE_BYTE_PRIM) return stream.readByte();
		else if (type == TYPE_SHORT_PRIM) return stream.readShort();
		else if (type == TYPE_INT_PRIM) return stream.readInt();
		else if (type == TYPE_LONG_PRIM) return stream.readLong();
		else if (type == TYPE_FLOAT_PRIM) return stream.readFloat();
		else if (type == TYPE_DOUBLE_PRIM) return stream.readDouble();
		else if (type == TYPE_BOOLEAN_PRIM) return stream.readBoolean();
		else if (type == TYPE_CHAR_PRIM) return stream.readChar();
		else if (type == TYPE_BYTE_OBJ) return stream.readByte();
		else if (type == TYPE_SHORT_OBJ) return stream.readShort();
		else if (type == TYPE_INTEGER_OBJ) return stream.readInt();
		else if (type == TYPE_LONG_OBJ) return stream.readLong();
		else if (type == TYPE_FLOAT_OBJ) return stream.readFloat();
		else if (type == TYPE_DOUBLE_OBJ) return stream.readDouble();
		else if (type == TYPE_BOOLEAN_OBJ) return stream.readBoolean();
		else if (type == TYPE_CHARACTER_OBJ) return stream.readChar();
		else if (type == TYPE_STRING_OBJ) return stream.readUTF();
		else if (type == TYPE_STRING_BUFFER) return new StringBuffer(stream.readUTF());
		else if (type == TYPE_CLASS) try { return Class.forName(stream.readUTF()); } catch (ClassNotFoundException nfe) { throw new IOException(nfe.getMessage()); }
		else if (type == TYPE_BYTE_PRIM_ARRAY) {
			int n = stream.readInt();
			byte[] stuff = new byte[n];
			for (int i = 0; i < n; i++) stuff[i] = stream.readByte();
			return stuff;
		}
		else if (type == TYPE_SHORT_PRIM_ARRAY) {
			int n = stream.readInt();
			short[] stuff = new short[n];
			for (int i = 0; i < n; i++) stuff[i] = stream.readShort();
			return stuff;
		}
		else if (type == TYPE_INT_PRIM_ARRAY) {
			int n = stream.readInt();
			int[] stuff = new int[n];
			for (int i = 0; i < n; i++) stuff[i] = stream.readInt();
			return stuff;
		}
		else if (type == TYPE_LONG_PRIM_ARRAY) {
			int n = stream.readInt();
			long[] stuff = new long[n];
			for (int i = 0; i < n; i++) stuff[i] = stream.readLong();
			return stuff;
		}
		else if (type == TYPE_FLOAT_PRIM_ARRAY) {
			int n = stream.readInt();
			float[] stuff = new float[n];
			for (int i = 0; i < n; i++) stuff[i] = stream.readFloat();
			return stuff;
		}
		else if (type == TYPE_DOUBLE_PRIM_ARRAY) {
			int n = stream.readInt();
			double[] stuff = new double[n];
			for (int i = 0; i < n; i++) stuff[i] = stream.readDouble();
			return stuff;
		}
		else if (type == TYPE_BOOLEAN_PRIM_ARRAY) {
			int n = stream.readInt();
			boolean[] stuff = new boolean[n];
			for (int i = 0; i < n; i++) stuff[i] = stream.readBoolean();
			return stuff;
		}
		else if (type == TYPE_CHAR_PRIM_ARRAY) {
			int n = stream.readInt();
			char[] stuff = new char[n];
			for (int i = 0; i < n; i++) stuff[i] = stream.readChar();
			return stuff;
		}
		else if (type == TYPE_BYTE_OBJ_ARRAY) {
			int n = stream.readInt();
			Byte[] stuff = new Byte[n];
			for (int i = 0; i < n; i++) stuff[i] = stream.readByte();
			return stuff;
		}
		else if (type == TYPE_SHORT_OBJ_ARRAY) {
			int n = stream.readInt();
			Short[] stuff = new Short[n];
			for (int i = 0; i < n; i++) stuff[i] = stream.readShort();
			return stuff;
		}
		else if (type == TYPE_INTEGER_OBJ_ARRAY) {
			int n = stream.readInt();
			Integer[] stuff = new Integer[n];
			for (int i = 0; i < n; i++) stuff[i] = stream.readInt();
			return stuff;
		}
		else if (type == TYPE_LONG_OBJ_ARRAY) {
			int n = stream.readInt();
			Long[] stuff = new Long[n];
			for (int i = 0; i < n; i++) stuff[i] = stream.readLong();
			return stuff;
		}
		else if (type == TYPE_FLOAT_OBJ_ARRAY) {
			int n = stream.readInt();
			Float[] stuff = new Float[n];
			for (int i = 0; i < n; i++) stuff[i] = stream.readFloat();
			return stuff;
		}
		else if (type == TYPE_DOUBLE_OBJ_ARRAY) {
			int n = stream.readInt();
			Double[] stuff = new Double[n];
			for (int i = 0; i < n; i++) stuff[i] = stream.readDouble();
			return stuff;
		}
		else if (type == TYPE_BOOLEAN_OBJ_ARRAY) {
			int n = stream.readInt();
			Boolean[] stuff = new Boolean[n];
			for (int i = 0; i < n; i++) stuff[i] = stream.readBoolean();
			return stuff;
		}
		else if (type == TYPE_CHARACTER_OBJ_ARRAY) {
			int n = stream.readInt();
			Character[] stuff = new Character[n];
			for (int i = 0; i < n; i++) stuff[i] = stream.readChar();
			return stuff;
		}
		else if (type == TYPE_STRING_OBJ_ARRAY) {
			int n = stream.readInt();
			String[] stuff = new String[n];
			for (int i = 0; i < n; i++) stuff[i] = stream.readUTF();
			return stuff;
		}
		else if (type == TYPE_STRING_BUFFER_ARRAY) {
			int n = stream.readInt();
			StringBuffer[] stuff = new StringBuffer[n];
			for (int i = 0; i < n; i++) stuff[i] = new StringBuffer(stream.readUTF());
			return stuff;
		}
		else if (type == TYPE_CLASS_ARRAY) {
			int n = stream.readInt();
			Class<?>[] stuff = new Class[n];
			for (int i = 0; i < n; i++) try { stuff[i] = Class.forName(stream.readUTF()); } catch (ClassNotFoundException nfe) { throw new IOException(nfe.getMessage()); }
			return stuff;
		}
		else return null;
	}
}
