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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class Serializer {
	private Map<Integer, Class<?>> types;
	private Map<Class<?>, Integer> classes;
	private Map<Class<?>, Integer> versions;
	
	public Serializer() {
		types = new HashMap<Integer, Class<?>>();
		classes = new HashMap<Class<?>, Integer>();
		versions = new HashMap<Class<?>, Integer>();
		loadRecognizedTypesAndClasses();
	}

	protected abstract void loadRecognizedTypesAndClasses();
	
	protected static final int fcc(String s) {
		int a = (s.length() > 0) ? (s.charAt(0) & 0xFF) : 0x20;
		int b = (s.length() > 1) ? (s.charAt(1) & 0xFF) : 0x20;
		int c = (s.length() > 2) ? (s.charAt(2) & 0xFF) : 0x20;
		int d = (s.length() > 3) ? (s.charAt(3) & 0xFF) : 0x20;
		return ((a << 24) | (b << 16) | (c << 8) | d);
	}
	
	protected final void addTypeAndClass(int type, int version, Class<?> clazz) {
		types.put(type, clazz);
		classes.put(clazz, type);
		versions.put(clazz, version);
	}
	
	public final Map<Integer, Class<?>> getRecognizedTypes() {
		return types;
	}

	public final Map<Class<?>, Integer> getRecognizedClasses() {
		return classes;
	}
	
	public Map<Class<?>,Integer> getClassVersions() {
		return versions;
	}
	
	public abstract void serializeObject(Object o, DataOutputStream stream) throws IOException;
	public abstract Object deserializeObject(int type, int version, DataInputStream stream) throws IOException;
}
