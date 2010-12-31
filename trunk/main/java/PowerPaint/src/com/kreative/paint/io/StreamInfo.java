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

import java.util.HashMap;
import java.util.IdentityHashMap;

public class StreamInfo {
	private int refNum = 1;
	private IdentityHashMap<Object,Integer> objectRefs = new IdentityHashMap<Object,Integer>();
	private HashMap<Integer,Object> refObjects = new HashMap<Integer,Object>();
	private Monitor mon = null;
	
	public int nextRef() {
		return refNum++;
	}
	
	public boolean hasObject(Object o) {
		return objectRefs.containsKey(o);
	}
	
	public int getObjectRef(Object o) {
		return objectRefs.get(o);
	}
	
	public void setObjectRef(Object o, int ref) {
		objectRefs.put(o, ref);
		refObjects.put(ref, o);
	}
	
	public boolean hasRef(int ref) {
		return refObjects.containsKey(ref);
	}
	
	public Object getRefObject(int ref) {
		return refObjects.get(ref);
	}
	
	public void setRefObject(int ref, Object o) {
		refObjects.put(ref, o);
		objectRefs.put(o, ref);
	}
	
	public Monitor getMonitor() {
		return mon;
	}
	
	public void setMonitor(Monitor mon) {
		this.mon = mon;
	}
}
