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

import java.io.*;
import java.util.*;

/* This and UtilitySerializer more or less cover the java.util package. */
public class CollectionSerializer extends Serializer {
	private static final int TYPE_HASH_SET = fcc("HSet");
	private static final int TYPE_TREE_SET = fcc("TSet");
	private static final int TYPE_LINKED_HASH_SET = fcc("LSet");
	//private static final int TYPE_ENUM_SET = fcc("ESet");
	private static final int TYPE_ARRAY_LIST = fcc("ALst");
	private static final int TYPE_LINKED_LIST = fcc("LLst");
	private static final int TYPE_HASH_MAP = fcc("HMap");
	private static final int TYPE_TREE_MAP = fcc("TMap");
	private static final int TYPE_LINKED_HASH_MAP = fcc("LMap");
	//private static final int TYPE_ENUM_MAP = fcc("EMap");
	private static final int TYPE_IDENTITY_HASH_MAP = fcc("IMap");
	private static final int TYPE_WEAK_HASH_MAP = fcc("WMap");
	private static final int TYPE_PRIORITY_QUEUE = fcc("PQue");
	private static final int TYPE_VECTOR = fcc("Vect");
	private static final int TYPE_STACK = fcc("Stac");
	private static final int TYPE_HASH_TABLE = fcc("Hash");
	
	protected void loadRecognizedTypesAndClasses() {
		addTypeAndClass(TYPE_HASH_SET, 1, HashSet.class);
		addTypeAndClass(TYPE_TREE_SET, 1, TreeSet.class);
		addTypeAndClass(TYPE_LINKED_HASH_SET, 1, LinkedHashSet.class);
		//addTypeAndClass(TYPE_ENUM_SET, 1, EnumSet.class);
		addTypeAndClass(TYPE_ARRAY_LIST, 1, ArrayList.class);
		addTypeAndClass(TYPE_LINKED_LIST, 1, LinkedList.class);
		addTypeAndClass(TYPE_HASH_MAP, 1, HashMap.class);
		addTypeAndClass(TYPE_TREE_MAP, 1, TreeMap.class);
		addTypeAndClass(TYPE_LINKED_HASH_MAP, 1, LinkedHashMap.class);
		//addTypeAndClass(TYPE_ENUM_MAP, 1, EnumMap.class);
		addTypeAndClass(TYPE_IDENTITY_HASH_MAP, 1, IdentityHashMap.class);
		addTypeAndClass(TYPE_WEAK_HASH_MAP, 1, WeakHashMap.class);
		addTypeAndClass(TYPE_PRIORITY_QUEUE, 1, PriorityQueue.class);
		addTypeAndClass(TYPE_VECTOR, 1, Vector.class);
		addTypeAndClass(TYPE_STACK, 1, Stack.class);
		addTypeAndClass(TYPE_HASH_TABLE, 1, Hashtable.class);
	}

	public void serializeObject(Object o, DataOutputStream stream) throws IOException {
		if (o instanceof Collection) {
			Collection<?> c = (Collection<?>)o;
			stream.writeInt(c.size());
			for (Object i : c) {
				SerializationManager.writeObject(i, stream);
			}
		}
		else if (o instanceof Map) {
			Map<?,?> m = (Map<?,?>)o;
			stream.writeInt(m.size());
			for (Map.Entry<?,?> e : m.entrySet()) {
				SerializationManager.writeObject(e.getKey(), stream);
				SerializationManager.writeObject(e.getValue(), stream);
			}
		}
	}
	
	public Object deserializeObject(int type, int version, DataInputStream stream) throws IOException {
		if (version != 1) throw new IOException("Invalid version number.");
		else if (type == TYPE_HASH_SET) {
			Collection<Object> c = new HashSet<Object>();
			int size = stream.readInt();
			for (int i = 0; i < size; i++) {
				c.add(SerializationManager.readObject(stream));
			}
			return c;
		}
		else if (type == TYPE_TREE_SET) {
			Collection<Object> c = new TreeSet<Object>();
			int size = stream.readInt();
			for (int i = 0; i < size; i++) {
				c.add(SerializationManager.readObject(stream));
			}
			return c;
		}
		else if (type == TYPE_LINKED_HASH_SET) {
			Collection<Object> c = new LinkedHashSet<Object>();
			int size = stream.readInt();
			for (int i = 0; i < size; i++) {
				c.add(SerializationManager.readObject(stream));
			}
			return c;
		}
		else if (type == TYPE_ARRAY_LIST) {
			Collection<Object> c = new ArrayList<Object>();
			int size = stream.readInt();
			for (int i = 0; i < size; i++) {
				c.add(SerializationManager.readObject(stream));
			}
			return c;
		}
		else if (type == TYPE_LINKED_LIST) {
			Collection<Object> c = new LinkedList<Object>();
			int size = stream.readInt();
			for (int i = 0; i < size; i++) {
				c.add(SerializationManager.readObject(stream));
			}
			return c;
		}
		else if (type == TYPE_HASH_MAP) {
			Map<Object,Object> m = new HashMap<Object,Object>();
			int size = stream.readInt();
			for (int i = 0; i < size; i++) {
				Object k = SerializationManager.readObject(stream);
				Object v = SerializationManager.readObject(stream);
				m.put(k,v);
			}
			return m;
		}
		else if (type == TYPE_TREE_MAP) {
			Map<Object,Object> m = new TreeMap<Object,Object>();
			int size = stream.readInt();
			for (int i = 0; i < size; i++) {
				Object k = SerializationManager.readObject(stream);
				Object v = SerializationManager.readObject(stream);
				m.put(k,v);
			}
			return m;
		}
		else if (type == TYPE_LINKED_HASH_MAP) {
			Map<Object,Object> m = new LinkedHashMap<Object,Object>();
			int size = stream.readInt();
			for (int i = 0; i < size; i++) {
				Object k = SerializationManager.readObject(stream);
				Object v = SerializationManager.readObject(stream);
				m.put(k,v);
			}
			return m;
		}
		else if (type == TYPE_IDENTITY_HASH_MAP) {
			Map<Object,Object> m = new IdentityHashMap<Object,Object>();
			int size = stream.readInt();
			for (int i = 0; i < size; i++) {
				Object k = SerializationManager.readObject(stream);
				Object v = SerializationManager.readObject(stream);
				m.put(k,v);
			}
			return m;
		}
		else if (type == TYPE_WEAK_HASH_MAP) {
			Map<Object,Object> m = new WeakHashMap<Object,Object>();
			int size = stream.readInt();
			for (int i = 0; i < size; i++) {
				Object k = SerializationManager.readObject(stream);
				Object v = SerializationManager.readObject(stream);
				m.put(k,v);
			}
			return m;
		}
		else if (type == TYPE_PRIORITY_QUEUE) {
			Collection<Object> c = new PriorityQueue<Object>();
			int size = stream.readInt();
			for (int i = 0; i < size; i++) {
				c.add(SerializationManager.readObject(stream));
			}
			return c;
		}
		else if (type == TYPE_VECTOR) {
			Collection<Object> c = new Vector<Object>();
			int size = stream.readInt();
			for (int i = 0; i < size; i++) {
				c.add(SerializationManager.readObject(stream));
			}
			return c;
		}
		else if (type == TYPE_STACK) {
			Collection<Object> c = new Stack<Object>();
			int size = stream.readInt();
			for (int i = 0; i < size; i++) {
				c.add(SerializationManager.readObject(stream));
			}
			return c;
		}
		else if (type == TYPE_HASH_TABLE) {
			Map<Object,Object> m = new Hashtable<Object,Object>();
			int size = stream.readInt();
			for (int i = 0; i < size; i++) {
				Object k = SerializationManager.readObject(stream);
				Object v = SerializationManager.readObject(stream);
				m.put(k,v);
			}
			return m;
		}
		else return null;
	}
}
