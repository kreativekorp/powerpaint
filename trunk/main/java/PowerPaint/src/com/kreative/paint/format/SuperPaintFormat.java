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

public class SuperPaintFormat implements Format {
	public String getName() { return "SuperPaint"; }
	public String getExpandedName() { return "SuperPaint"; }
	public String getExtension() { return "spnt"; }
	public int getMacFileType() { return 0x53506E33; }
	public int getMacResourceType() { return 0x53506E33; } 
	public long getDFFType() { return 0x496D672053504E54L; }
	
	public MediaType getMediaType() { return MediaType.IMAGE; }
	public GraphicType getGraphicType() { return GraphicType.METAFILE; }
	public SizeType getSizeType() { return SizeType.ARBITRARY; }
	public ColorType getColorType() { return ColorType.RGB_8; }
	public AlphaType getAlphaType() { return AlphaType.OPAQUE; }
	public LayerType getLayerType() { return LayerType.SUPERPAINT; }
	
	public boolean onlyUponRequest() { return false; }
	public int usesMagic() { return 0; }
	public boolean acceptsMagic(byte[] start, long length) { return false; }
	public boolean acceptsExtension(String ext) { return ext.equalsIgnoreCase("spnt") || ext.equalsIgnoreCase("spt"); }
	public boolean acceptsMacFileType(int type) { return type == 0x53505447 || type == 0x53506E33; }
	public boolean acceptsMacResourceType(int type) { return type == 0x53505447 || type == 0x53506E33; }
	public boolean acceptsDFFType(long type) { return type == 0x496D672053504E54L; }
	
	public boolean supportsRead() { return true; }
	public boolean usesReadOptionForm() { return false; }
	public Form getReadOptionForm() { return null; }
	public Canvas read(DataInputStream in, Monitor m) throws IOException {
		Canvas c = new PICTFormat().read(in, m);
		int[] pixels = new int[c.getWidth() * c.getHeight()];
		c.get(0).getRGB(-c.get(0).getX(), -c.get(0).getY(), c.getWidth(), c.getHeight(), pixels, 0, c.getWidth());
		for (int i = 0; i < pixels.length; i++) pixels[i] |= 0xFF000000;
		c.get(0).setRGB(-c.get(0).getX(), -c.get(0).getY(), c.getWidth(), c.getHeight(), pixels, 0, c.getWidth());
		return c;
	}
	
	public boolean supportsWrite() { return false; }
	public boolean usesWriteOptionForm() { return false; }
	public Form getWriteOptionForm() { return null; }
	public int approximateFileSize(Canvas c) { return 0; }
	public void write(Canvas c, DataOutputStream out, Monitor m) throws IOException {}
}
