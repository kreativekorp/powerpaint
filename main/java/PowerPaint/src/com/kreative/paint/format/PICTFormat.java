package com.kreative.paint.format;

import java.awt.Graphics2D;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import com.kreative.paint.Canvas;
import com.kreative.paint.form.Form;
import com.kreative.paint.io.Monitor;
import com.kreative.paint.pict.PICTGraphics;
import com.kreative.paint.pict.PICTInputStream;
import com.kreative.paint.pict.PICTInstruction;
import com.kreative.paint.pict.Rect;

public class PICTFormat implements Format {
	public String getName() { return "PICT"; }
	public String getExpandedName() { return "QuickDraw Picture"; }
	public String getExtension() { return "pict"; }
	public int getMacFileType() { return 0x50494354; }
	public int getMacResourceType() { return 0x50494354; }
	public long getDFFType() { return 0x496D672050494354L; }
	
	public MediaType getMediaType() { return MediaType.IMAGE; }
	public GraphicType getGraphicType() { return GraphicType.METAFILE; }
	public SizeType getSizeType() { return SizeType.ARBITRARY; }
	public ColorType getColorType() { return ColorType.RGB_8; }
	public AlphaType getAlphaType() { return AlphaType.OPAQUE_AND_TRANSPARENT; }
	public LayerType getLayerType() { return LayerType.POWERPAINT; }
	
	public boolean onlyUponRequest() { return false; }
	public int usesMagic() { return 0; }
	public boolean acceptsMagic(byte[] start, long length) { return false; }
	public boolean acceptsExtension(String ext) { return ext.equalsIgnoreCase("pict") || ext.equalsIgnoreCase("pic") || ext.equalsIgnoreCase("pct"); }
	public boolean acceptsMacFileType(int type) { return type == 0x50494354; }
	public boolean acceptsMacResourceType(int type) { return type == 0x50494354; }
	public boolean acceptsDFFType(long type) { return type == 0x496D672050494354L || type == 0x4D61632050494354L; }
	
	public boolean supportsRead() { return true; }
	public boolean usesReadOptionForm() { return false; }
	public Form getReadOptionForm() { return null; }
	
	public Canvas read(DataInputStream in, Monitor m) throws IOException {
		@SuppressWarnings("resource")
		PICTInputStream pin = new PICTInputStream(in);
		pin.skipBytes(512);
		pin.readShort();
		Rect bounds = pin.readRect();
		Canvas c = new Canvas(bounds.right-bounds.left, bounds.bottom-bounds.top);
		Graphics2D g = c.get(0).createDrawGraphics();
		PICTGraphics pg = new PICTGraphics(g);
		while (true) {
			PICTInstruction inst = pin.readInstruction();
			if (inst.opcode == 0xFF) break;
			pg.executeInstruction(inst);
		}
		g.dispose();
		return c;
	}
	
	public boolean supportsWrite() { return false; }
	public boolean usesWriteOptionForm() { return false; }
	public Form getWriteOptionForm() { return null; }
	public int approximateFileSize(Canvas c) { return 0; }
	public void write(Canvas c, DataOutputStream out, Monitor m) throws IOException {}
}
