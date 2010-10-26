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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import com.kreative.paint.Canvas;
import com.kreative.paint.form.Form;
import com.kreative.paint.io.Monitor;

public class PNMFormat implements Format {
	public String getName() { return "PNM"; }
	public String getExpandedName() { return "Portable Anymap"; }
	public String getExtension() { return "pnm"; }
	public int getMacFileType() { return 0x504E4D20; }
	public int getMacResourceType() { return 0x504E4D20; }
	public long getDFFType() { return 0x496D616765504E4DL; }
	
	public MediaType getMediaType() { return MediaType.IMAGE; }
	public GraphicType getGraphicType() { return GraphicType.BITMAP; }
	public SizeType getSizeType() { return SizeType.ARBITRARY; }
	public ColorType getColorType() { return ColorType.RGB_8; }
	public AlphaType getAlphaType() { return AlphaType.OPAQUE; }
	public LayerType getLayerType() { return LayerType.FLAT; }
	
	public boolean onlyUponRequest() { return false; }
	public int usesMagic() { return 0; }
	public boolean acceptsMagic(byte[] start, long length) { return false; }
	public boolean acceptsExtension(String ext) { return ext.equalsIgnoreCase("pnm"); }
	public boolean acceptsMacFileType(int type) { return type == 0x504E4D20 || type == 0x504E4D66 || type == 0x504E4D6D; }
	public boolean acceptsMacResourceType(int type) { return type == 0x504E4D20 || type == 0x504E4D66 || type == 0x504E4D6D; }
	public boolean acceptsDFFType(long type) { return type == 0x496D616765504E4DL || type == 0x496D6720504E4D20L; }
	
	public boolean supportsRead() { return true; }
	public boolean usesReadOptionForm() { return false; }
	public Form getReadOptionForm() { return null; }
	public Canvas read(DataInputStream in, Monitor m) throws IOException {
		BufferedInputStream in2 = new BufferedInputStream(in);
		in2.mark(4);
		short magic = new DataInputStream(in2).readShort();
		in2.reset();
		switch (magic) {
		case (short)0x5031:
		case (short)0x5034:
			return PBM.read(new DataInputStream(in2), m);
		case (short)0x5032:
		case (short)0x5035:
			return PGM.read(new DataInputStream(in2), m);
		case (short)0x5033:
		case (short)0x5036:
			return PPM.read(new DataInputStream(in2), m);
		case (short)0x5037:
			return PAM.read(new DataInputStream(in2), m);
		default:
			throw new NotThisFormatException();
		}
	}
	
	public boolean supportsWrite() { return true; }
	public boolean usesWriteOptionForm() { return false; }
	public Form getWriteOptionForm() { return null; }
	public int approximateFileSize(Canvas c) {
		return PPM.approximateFileSize(c);
	}
	public void write(Canvas c, DataOutputStream out, Monitor m) throws IOException {
		PPM.write(c, out, m);
	}
	
	private static final Format PBM = new PBMFormat();
	private static final Format PGM = new PGMFormat();
	private static final Format PPM = new PPMFormat();
	private static final Format PAM = new PAMFormat();
}
