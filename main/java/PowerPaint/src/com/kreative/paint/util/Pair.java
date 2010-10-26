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

package com.kreative.paint.util;

public class Pair<A,B> {
	private A a;
	private B b;
	
	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}
	
	public A getFormer() {
		return a;
	}
	
	public B getLatter() {
		return b;
	}
	
	public int hashCode() {
		return ((a == null) ? 0 : a.hashCode()) ^ ((b == null) ? 0 : b.hashCode());
	}
	
	public boolean equals(Object o) {
		if (o instanceof Pair) {
			Pair<?,?> other = (Pair<?,?>)o;
			return ((this.a == null) ? (other.a == null) : (other.a == null) ? (this.a == null) : this.a.equals(other.a))
				&& ((this.b == null) ? (other.b == null) : (other.b == null) ? (this.b == null) : this.b.equals(other.b));
		} else {
			return false;
		}
	}
	
	public String toString() {
		return a + "," + b;
	}
}
