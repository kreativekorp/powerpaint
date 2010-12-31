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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import com.kreative.paint.Canvas;
import com.kreative.paint.form.Form;
import com.kreative.paint.io.Monitor;
import com.kreative.paint.io.SerializationManager;

public class PowerPaintFormat implements Format {
	public String getName() { return "PowerPaint"; }
	public String getExpandedName() { return "PowerPaint"; }
	public String getExtension() { return "ckp"; }
	public int getMacFileType() { return 0x434B5020; }
	public int getMacResourceType() { return 0x434B5020; }
	public long getDFFType() { return 0x496D616765434B50L; }
	
	public MediaType getMediaType() { return MediaType.IMAGE; }
	public GraphicType getGraphicType() { return GraphicType.METAFILE; }
	public SizeType getSizeType() { return SizeType.ARBITRARY; }
	public ColorType getColorType() { return ColorType.RGB_8; }
	public AlphaType getAlphaType() { return AlphaType.CHANNEL; }
	public LayerType getLayerType() { return LayerType.POWERPAINT; }
	
	public boolean onlyUponRequest() { return false; }
	public int usesMagic() { return 16; }
	public boolean acceptsMagic(byte[] start, long length) {
		try {
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(start));
			if (in.readInt() != 0x25636B70) return false;
			if (in.readInt() != 0x0D0A1A04) return false;
			if (in.readInt() != 0xFF0A960D) return false;
			if (in.readInt() != 0x12EBECCA) return false;
			in.close();
			return true;
		} catch (IOException ioe) {
			return false;
		}
	}
	public boolean acceptsExtension(String ext) { return ext.equalsIgnoreCase("ckp"); }
	public boolean acceptsMacFileType(int type) { return type == 0x434B5020 || type == 0x434B5066 || type == 0x434B5070; }
	public boolean acceptsMacResourceType(int type) { return type == 0x434B5020 || type == 0x434B5066 || type == 0x434B5070; }
	public boolean acceptsDFFType(long type) { return type == 0x496D616765434B50L || type == 0x496D6720434B5020L; }
	
	public boolean supportsRead() { return true; }
	public boolean usesReadOptionForm() { return false; }
	public Form getReadOptionForm() { return null; }
	public Canvas read(DataInputStream in, Monitor m) throws IOException {
		if (in.readInt() != 0x25636B70) throw new NotThisFormatException();
		if (in.readInt() != 0x0D0A1A04) throw new NotThisFormatException();
		if (in.readInt() != 0xFF0A960D) throw new NotThisFormatException();
		if (in.readInt() != 0x12EBECCA) throw new NotThisFormatException();
		try {
			SerializationManager.open(in, m);
			Canvas c = (Canvas)SerializationManager.readObject(in);
			SerializationManager.close(in);
			return c;
		} catch (IOException ioe) {
			throw ioe;
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException();
		}
	}
	
	public boolean supportsWrite() { return true; }
	public boolean usesWriteOptionForm() { return false; }
	public Form getWriteOptionForm() { return null; }
	public int approximateFileSize(Canvas c) {
		return c.getWidth()*c.getHeight()/8;
	}
	public void write(Canvas c, DataOutputStream out, Monitor m) throws IOException {
		out.writeInt(0x25636B70);
		out.writeInt(0x0D0A1A04);
		out.writeInt(0xFF0A960D);
		out.writeInt(0x12EBECCA);
		SerializationManager.open(out, m);
		SerializationManager.writeObject(c, out);
		SerializationManager.close(out);
	}
}
