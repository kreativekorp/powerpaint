/**
 * This package provides classes for reading and writing PowerPaint
 * data structures to files or to the clipboard. It implements a form of
 * serialization completely separate from the normal Java serialization
 * process. In fact, the only class in all of PowerPaint that is Serializable
 * using Java's process is GroupDrawObject, used when transferring draw
 * objects to and from the clipboard. All other classes must be serialized
 * using this package instead. The rationale for doing this is threefold:
 * first of all, we have much greater control over the serialization
 * process and know exactly what is being serialized; second of all, the
 * serialization process is no longer tied down to Java, so PowerPaint
 * files can still be read and written if PowerPaint is ported to another
 * language; third of all, we can serialize objects that are part of
 * AWT or Swing that are not serializable using Java's serialization process,
 * such as Shapes.
 * <p>
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
