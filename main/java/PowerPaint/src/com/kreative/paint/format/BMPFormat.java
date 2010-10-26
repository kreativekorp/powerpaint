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

package com.kreative.paint.format;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import com.kreative.paint.Canvas;
import com.kreative.paint.form.Form;
import com.kreative.paint.io.Monitor;

public class BMPFormat implements Format {
	public String getName() { return "BMP"; }
	public String getExpandedName() { return "Bitmap"; }
	public String getExtension() { return "bmp"; }
	public int getMacFileType() { return 0x424D5020; }
	public int getMacResourceType() { return 0x424D5020; }
	public long getDFFType() { return 0x496D616765424D50L; }
	
	public MediaType getMediaType() { return MediaType.IMAGE; }
	public GraphicType getGraphicType() { return GraphicType.BITMAP; }
	public SizeType getSizeType() { return SizeType.ARBITRARY; }
	public ColorType getColorType() { return ColorType.RGB_8; }
	public AlphaType getAlphaType() { return AlphaType.OPAQUE; }
	public LayerType getLayerType() { return LayerType.FLAT; }
	
	public boolean onlyUponRequest() { return false; }
	public int usesMagic() { return 14; }
	public boolean acceptsMagic(byte[] start, long length) {
		try {
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(start));
			if (in.readShort() != 0x424D) return false;
			in.readInt();
			if (in.readShort() != 0) return false;
			if (in.readShort() != 0) return false;
			in.readInt();
			in.close();
			return true;
		} catch (IOException ioe) {
			return false;
		}
	}
	public boolean acceptsExtension(String ext) { return ext.equalsIgnoreCase("bmp") || ext.equalsIgnoreCase("dib"); }
	public boolean acceptsMacFileType(int type) { return type == 0x424D5020 || type == 0x424D5066 || type == 0x424D5070 || type == 0x44494220 || type == 0x44494266 || type == 0x44494262; }
	public boolean acceptsMacResourceType(int type) { return type == 0x424D5020 || type == 0x424D5066 || type == 0x424D5070 || type == 0x44494220 || type == 0x44494266 || type == 0x44494262; }
	public boolean acceptsDFFType(long type) { return type == 0x496D616765424D50L || type == 0x496D6720424D5020L || type == 0x496D616765444942L || type == 0x496D672044494220L; }
	
	public boolean supportsRead() {
		for (String s : ImageIO.getReaderFormatNames()) {
			if (s.equalsIgnoreCase("bmp")) return true;
		}
		return false;
	}
	public boolean usesReadOptionForm() { return false; }
	public Form getReadOptionForm() { return null; }
	public Canvas read(DataInputStream in, Monitor m) throws IOException {
		BufferedImage bi = ImageIO.read(in);
		if (bi == null) {
			throw new IOException();
		} else {
			Canvas c = new Canvas(bi.getWidth(), bi.getHeight());
			Graphics2D g = c.get(0).createPaintGraphics();
			g.drawImage(bi, null, 0, 0);
			g.dispose();
			return c;
		}
	}
	
	public boolean supportsWrite() {
		for (String s : ImageIO.getWriterFormatNames()) {
			if (s.equalsIgnoreCase("bmp")) return true;
		}
		return false;
	}
	public boolean usesWriteOptionForm() { return false; }
	public Form getWriteOptionForm() { return null; }
	public int approximateFileSize(Canvas c) {
		return c.getWidth()*c.getHeight()*3;
	}
	public void write(Canvas c, DataOutputStream out, Monitor m) throws IOException {
		BufferedImage bi = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g = bi.createGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, c.getWidth(), c.getHeight());
		c.paint(g);
		g.dispose();
		ImageIO.write(bi, "bmp", out);
	}
}
