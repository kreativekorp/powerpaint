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

package com.kreative.paint.format;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import com.kreative.paint.Canvas;
import com.kreative.paint.form.Form;
import com.kreative.paint.io.Monitor;

public interface Format {
	public String getName();
	public String getExpandedName();
	public String getExtension();
	public int getMacFileType();
	public int getMacResourceType();
	public long getDFFType();
	
	public MediaType getMediaType();
	public GraphicType getGraphicType();
	public SizeType getSizeType();
	public ColorType getColorType();
	public AlphaType getAlphaType();
	public LayerType getLayerType();
	
	public boolean onlyUponRequest();
	public int usesMagic();
	public boolean acceptsMagic(byte[] start, long length);
	public boolean acceptsExtension(String ext);
	public boolean acceptsMacFileType(int type);
	public boolean acceptsMacResourceType(int type);
	public boolean acceptsDFFType(long type);
	
	public boolean supportsRead();
	public boolean usesReadOptionForm();
	public Form getReadOptionForm();
	public Canvas read(DataInputStream in, Monitor m) throws IOException;
	
	public boolean supportsWrite();
	public boolean usesWriteOptionForm();
	public Form getWriteOptionForm();
	public int approximateFileSize(Canvas c);
	public void write(Canvas c, DataOutputStream out, Monitor m) throws IOException;
}
