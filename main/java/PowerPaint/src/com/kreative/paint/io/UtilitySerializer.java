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

/* This and CollectionSerializer more or less cover the java.util package. */
/* This more or less covers the java.io package. */
public class UtilitySerializer extends Serializer {
	private static final int TYPE_BIT_SET = fcc("BSet");
	private static final int TYPE_CURRENCY = fcc("Curr");
	private static final int TYPE_DATE = fcc("Date");
	private static final int TYPE_FILE = fcc("File");
	private static final int TYPE_GREGORIAN_CALENDAR = fcc("GCal");
	private static final int TYPE_LOCALE = fcc("Loca");
	private static final int TYPE_PROPERTIES = fcc("Prop");
	private static final int TYPE_RANDOM = fcc("Rand");
	private static final int TYPE_SIMPLE_TIME_ZONE = fcc("STZo");
	private static final int TYPE_UUID = fcc("UUID");
	
	protected void loadRecognizedTypesAndClasses() {
		addTypeAndClass(TYPE_BIT_SET, 1, BitSet.class);
		addTypeAndClass(TYPE_CURRENCY, 1, Currency.class);
		addTypeAndClass(TYPE_DATE, 1, Date.class);
		addTypeAndClass(TYPE_FILE, 1, File.class);
		addTypeAndClass(TYPE_GREGORIAN_CALENDAR, 1, GregorianCalendar.class);
		addTypeAndClass(TYPE_LOCALE, 1, Locale.class);
		addTypeAndClass(TYPE_PROPERTIES, 1, Properties.class);
		addTypeAndClass(TYPE_RANDOM, 1, Random.class);
		addTypeAndClass(TYPE_SIMPLE_TIME_ZONE, 1, SimpleTimeZone.class);
		addTypeAndClass(TYPE_UUID, 1, UUID.class);
	}
	
	public void serializeObject(Object o, DataOutputStream stream) throws IOException {
		if (o instanceof BitSet) {
			BitSet v = (BitSet)o;
			byte[] b = new byte[(v.length() + 7) >>> 3];
			for (int i = v.nextSetBit(0); i >= 0; i = v.nextSetBit(i+1)) {
				b[i >>> 3] |= (1 << (i & 7));
			}
			stream.writeInt(b.length);
			stream.write(b);
		}
		else if (o instanceof Currency) {
			Currency v = (Currency)o;
			stream.writeUTF(v.getCurrencyCode());
		}
		else if (o instanceof Date) {
			Date v = (Date)o;
			stream.writeLong(v.getTime());
		}
		else if (o instanceof File) {
			File v = (File)o;
			stream.writeUTF(v.getPath());
		}
		else if (o instanceof GregorianCalendar) {
			GregorianCalendar v = (GregorianCalendar)o;
			stream.writeLong(v.getTimeInMillis());
			stream.writeLong(v.getGregorianChange().getTime());
			//SerializationManager.writeObject(v.getTimeZone(), stream);
		}
		else if (o instanceof Locale) {
			Locale v = (Locale)o;
			stream.writeUTF(v.toString());
		}
		else if (o instanceof Properties) {
			Properties v = (Properties)o;
			stream.writeInt(v.size());
			for (Map.Entry<Object,Object> e : v.entrySet()) {
				SerializationManager.writeObject(e.getKey(), stream);
				SerializationManager.writeObject(e.getValue(), stream);
			}
		}
		else if (o instanceof Random) {
			// nothing
		}
		else if (o instanceof SimpleTimeZone) {
			SimpleTimeZone v = (SimpleTimeZone)o;
			stream.writeUTF(v.getID());
		}
		else if (o instanceof UUID) {
			UUID v = (UUID)o;
			stream.writeLong(v.getMostSignificantBits());
			stream.writeLong(v.getLeastSignificantBits());
		}
	}
	
	public Object deserializeObject(int type, int version, DataInputStream stream) throws IOException {
		if (version != 1) throw new IOException("Invalid version number.");
		else if (type == TYPE_BIT_SET) {
			int n = stream.readInt();
			byte[] b = new byte[n];
			stream.read(b);
			BitSet bs = new BitSet(n << 3);
			for (int i = 0; i < n; i++) {
				for (int j = 0, m = 1; j < 8; j++, m <<= 1) {
					if ((b[i] & m) != 0) {
						bs.set((i << 3) | j);
					}
				}
			}
			return bs;
		}
		else if (type == TYPE_CURRENCY) {
			return Currency.getInstance(stream.readUTF());
		}
		else if (type == TYPE_DATE) {
			return new Date(stream.readLong());
		}
		else if (type == TYPE_FILE) {
			return new File(stream.readUTF());
		}
		else if (type == TYPE_GREGORIAN_CALENDAR) {
			long t = stream.readLong();
			long c = stream.readLong();
			//TimeZone tz = (TimeZone)SerializationManager.readObject(stream);
			GregorianCalendar gc = new GregorianCalendar();
			//gc.setTimeZone(tz);
			gc.setGregorianChange(new Date(c));
			gc.setTimeInMillis(t);
			return gc;
		}
		else if (type == TYPE_LOCALE) {
			String s = stream.readUTF();
			Locale[] ll = Locale.getAvailableLocales();
			for (Locale l : ll) {
				if (l.toString().equals(s)) {
					return l;
				}
			}
			return null;
		}
		else if (type == TYPE_PROPERTIES) {
			int n = stream.readInt();
			Properties p = new Properties();
			for (int i = 0; i < n; i++) {
				Object k = SerializationManager.readObject(stream);
				Object v = SerializationManager.readObject(stream);
				p.put(k,v);
			}
			return p;
		}
		else if (type == TYPE_RANDOM) {
			return new Random();
		}
		else if (type == TYPE_SIMPLE_TIME_ZONE) {
			return SimpleTimeZone.getTimeZone(stream.readUTF());
		}
		else if (type == TYPE_UUID) {
			long m = stream.readLong();
			long l = stream.readLong();
			return new UUID(m,l);
		}
		else return null;
	}
}
