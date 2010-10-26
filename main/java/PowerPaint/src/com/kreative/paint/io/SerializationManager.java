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
import java.util.*;

public class SerializationManager {
	private SerializationManager() {}
	
	private static Map<Integer,Serializer> typeSerializers = new HashMap<Integer,Serializer>();
	private static Map<Integer,Class<?>> typeClasses = new HashMap<Integer,Class<?>>();
	private static Map<Class<?>,Serializer> classSerializers = new HashMap<Class<?>,Serializer>();
	private static Map<Class<?>,Integer> classTypes = new HashMap<Class<?>,Integer>();
	private static Map<Class<?>,Integer> classVersions = new HashMap<Class<?>,Integer>();
	private static IdentityHashMap<DataInputStream,StreamInfo> inputStreamInfo = new IdentityHashMap<DataInputStream,StreamInfo>();
	private static IdentityHashMap<DataOutputStream,StreamInfo> outputStreamInfo = new IdentityHashMap<DataOutputStream,StreamInfo>();
	
	static {
		registerSerializer(new PrimitiveSerializer());
		registerSerializer(new CollectionSerializer());
		registerSerializer(new UtilitySerializer());
		registerSerializer(new ShapeSerializer());
		registerSerializer(new AWTSerializer());
		registerSerializer(new CKPaintSerializer());
		registerSerializer(new CKPAWTSerializer());
		registerSerializer(new CKPGeomSerializer());
		registerSerializer(new CKPGradientSerializer());
		registerSerializer(new CKPObjectsSerializer());
		registerSerializer(new CKPPowerBrushSerializer());
		registerSerializer(new CKPUtilitySerializer());
	}
	
	private static String fccs(int i) {
		char[] ch = new char[4];
		ch[0] = (char)((i >> 24) & 0xFF);
		ch[1] = (char)((i >> 16) & 0xFF);
		ch[2] = (char)((i >> 8) & 0xFF);
		ch[3] = (char)(i & 0xFF);
		return new String(ch);
	}
	
	public static void registerSerializer(Serializer sz) {
		for (Map.Entry<Integer, Class<?>> e : sz.getRecognizedTypes().entrySet()) {
			int type = e.getKey();
			Class<?> clazz = e.getValue();
			if (typeSerializers.containsKey(type) || typeClasses.containsKey(type)) {
				System.err.println("Error: Duplicate serialization type: "+fccs(type));
			} else {
				typeSerializers.put(type, sz);
				typeClasses.put(type, clazz);
			}
		}
		for (Map.Entry<Class<?>, Integer> e : sz.getRecognizedClasses().entrySet()) {
			Class<?> clazz = e.getKey();
			int type = e.getValue();
			if (classSerializers.containsKey(clazz) || classTypes.containsKey(clazz)) {
				System.err.println("Error: Duplicate serialization class: "+clazz.getCanonicalName());
			} else {
				classSerializers.put(clazz, sz);
				classTypes.put(clazz, type);
			}
		}
		for (Map.Entry<Class<?>, Integer> e : sz.getClassVersions().entrySet()) {
			Class<?> clazz = e.getKey();
			int vers = e.getValue();
			classVersions.put(clazz, vers);
		}
	}
	
	public static void open(DataInputStream stream) {
		inputStreamInfo.put(stream, new StreamInfo());
	}
	
	public static void open(DataInputStream stream, Monitor m) {
		StreamInfo i = new StreamInfo();
		i.setMonitor(m);
		inputStreamInfo.put(stream, i);
	}
	
	public static void open(DataOutputStream stream) {
		outputStreamInfo.put(stream, new StreamInfo());
	}
	
	public static void open(DataOutputStream stream, Monitor m) {
		StreamInfo i = new StreamInfo();
		i.setMonitor(m);
		outputStreamInfo.put(stream, i);
	}
	
	public static void writeObject(Object o, DataOutputStream stream) throws IOException {
		StreamInfo si = outputStreamInfo.get(stream);
		if (si == null) throw new IOException("Stream not open for use by SerializationManager.");
		
		if (o == null) {
			stream.writeInt(0);
			stream.writeInt(0);
			stream.writeInt(0);
			stream.writeInt(0);
		}
		else if (si.hasObject(o)) {
			stream.writeInt(1);
			stream.writeInt(0);
			stream.writeInt(si.getObjectRef(o));
			stream.writeInt(0);
		}
		else if (classSerializers.containsKey(o.getClass()) && classTypes.containsKey(o.getClass()) && classVersions.containsKey(o.getClass())) {
			int type = classTypes.get(o.getClass());
			int vers = classVersions.get(o.getClass());
			int ref = si.nextRef();
			int mvs = 0;
			if (si.getMonitor() != null) {
				mvs = si.getMonitor().getValue();
			}
			si.setObjectRef(o, ref);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			OutputStream mos = (si.getMonitor() != null) ? new MonitoredOutputStream(si.getMonitor(), bos, true) : bos;
			DataOutputStream dos = new DataOutputStream(mos);
			outputStreamInfo.put(dos, si);
			Serializer sz = classSerializers.get(o.getClass());
			sz.serializeObject(o, dos);
			outputStreamInfo.remove(dos);
			dos.close();
			mos.close();
			bos.close();
			byte[] data = bos.toByteArray();
			int pad = (4 - (data.length & 3)) & 3;
			if (si.getMonitor() != null) {
				si.getMonitor().setValue(mvs);
			}
			stream.writeInt(type);
			stream.writeInt(vers);
			stream.writeInt(ref);
			stream.writeInt(data.length);
			stream.write(data);
			stream.write(new byte[pad]);
		}
		else {
			System.err.println("Error: Unknown serialization class: "+o.getClass().getCanonicalName());
		}
	}
	
	public static Object readObject(DataInputStream stream) throws IOException {
		StreamInfo si = inputStreamInfo.get(stream);
		if (si == null) throw new IOException("Stream not open for use by SerializationManager.");
		
		int type = stream.readInt();
		int vers = stream.readInt();
		int ref = stream.readInt();
		int len = stream.readInt();
		int mvs = 0, mve = 0;
		Object ret = null;
		if (si.getMonitor() != null) {
			mvs = si.getMonitor().getValue();
		}
		byte[] data = new byte[len];
		int pad = (4 - (len & 3)) & 3;
		stream.read(data);
		stream.read(new byte[pad]);
		if (si.getMonitor() != null) {
			mve = si.getMonitor().getValue();
			si.getMonitor().setValue(mvs);
		}
		
		if (type == 0) {
			ret = null;
		}
		else if (type == 1) {
			ret = si.getRefObject(ref);
		}
		else if (typeSerializers.containsKey(type) && typeClasses.containsKey(type)) {
			ByteArrayInputStream bis = new ByteArrayInputStream(data);
			InputStream mis = (si.getMonitor() != null) ? new MonitoredInputStream(si.getMonitor(), bis, true) : bis;
			DataInputStream dis = new DataInputStream(mis);
			inputStreamInfo.put(dis, si);
			Serializer sz = typeSerializers.get(type);
			Object o = sz.deserializeObject(type, vers, dis);
			inputStreamInfo.remove(dis);
			dis.close();
			mis.close();
			bis.close();
			si.setRefObject(ref, o);
			ret = o;
		}
		else {
			System.err.println("Error: Unknown serialization type: "+fccs(type));
			ret = null;
		}

		if (si.getMonitor() != null) {
			si.getMonitor().setValue(mve);
		}
		return ret;
	}
	
	public static void close(DataInputStream stream) {
		inputStreamInfo.remove(stream);
	}
	
	public static void close(DataOutputStream stream) {
		outputStreamInfo.remove(stream);
	}
	
	public static byte[] serializeObject(Object o) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		open(dos, null);
		writeObject(o, dos);
		close(dos);
		dos.close();
		bos.close();
		return bos.toByteArray();
	}
	
	public static byte[] serializeObject(Object o, Monitor mon) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		open(dos, mon);
		writeObject(o, dos);
		close(dos);
		dos.close();
		bos.close();
		return bos.toByteArray();
	}
	
	public static Object deserializeObject(byte[] data) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bis);
		open(dis, null);
		Object o = readObject(dis);
		close(dis);
		dis.close();
		bis.close();
		return o;
	}
	
	public static Object deserializeObject(byte[] data, Monitor mon) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bis);
		open(dis, mon);
		Object o = readObject(dis);
		close(dis);
		dis.close();
		bis.close();
		return o;
	}
	
	public static void reportKnownTypes() {
		Integer[] types = typeClasses.keySet().toArray(new Integer[0]);
		Arrays.sort(types);
		for (int type : types) {
			System.out.println(fccs(type) + "\t" + typeClasses.get(type).getCanonicalName());
		}
	}
	
	public static void main(String[] args) {
		reportKnownTypes();
	}
}
