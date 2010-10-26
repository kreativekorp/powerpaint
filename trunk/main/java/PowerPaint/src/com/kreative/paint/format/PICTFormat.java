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

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Stack;
import javax.swing.JLabel;
import com.kreative.paint.Canvas;
import com.kreative.paint.Layer;
import com.kreative.paint.Tile;
import com.kreative.paint.draw.DrawObject;
import com.kreative.paint.draw.GroupDrawObject;
import com.kreative.paint.form.CustomOption;
import com.kreative.paint.form.Form;
import com.kreative.paint.io.Monitor;
import com.kreative.paint.io.SerializationManager;
import com.kreative.paint.pict.PICTGraphics;
import com.kreative.paint.pict.PICTInputStream;
import com.kreative.paint.pict.PICTInstruction;
import com.kreative.paint.pict.PICTOutputStream;
import com.kreative.paint.pict.PICTUtilities;
import com.kreative.paint.pict.Point;
import com.kreative.paint.pict.Rect;
import com.kreative.paint.pict.Region;

public class PICTFormat implements Format {
	private static final int KPNT = 0x4B504E54;
	private static final int Canv = 0x43616E76;
	private static final int Layr = 0x4C617972;
	private static final int Tile = 0x54696C65;
	private static final int $$AA = 0x24244141;
	private static final int $$AM = 0x2424414D;
	private static final int dGrp = 0x64477270;
	private static final int dObj = 0x644F626A;
	private static final int $$DO = 0x2424444F;
	
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
		PICTInputStream pin = new PICTInputStream(in);
		pin.skipBytes(512);
		pin.readShort();
		Rect bounds = pin.readRect();
		Canvas c = new Canvas(bounds.right-bounds.left, bounds.bottom-bounds.top);
		Layer l = c.get(0);
		Tile t = null;
		PICTGraphics pg = new PICTGraphics();
		ByteArrayOutputStream alphaMaskDataBuffer = null;
		Stack<GroupDrawObject> gdo = new Stack<GroupDrawObject>();
		Stack<Integer> gdt = new Stack<Integer>();
		int nextGrpType = 0;
		boolean deserialized = false;
		boolean paintMode = true;
		boolean objVisible = true;
		boolean objLocked = false;
		boolean objSelected = false;
		while (true) {
			PICTInstruction inst = pin.readInstruction();
			if (inst.opcode == 0xFF) {
				break;
			} else if (inst.opcode >= 0xA2) {
				// ignore all the reserved opcodes
			} else if (inst.opcode >= 0x00 && inst.opcode < 0x20) {
				pg.executeInstruction(inst);
			} else if (inst.opcode >= 0x2C && inst.opcode < 0x30) {
				pg.executeInstruction(inst);
			} else switch (inst.opcode) {
			case PICTInstruction.Line.OPCODE:
				if (!deserialized) {
					paintMode = false;
					Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
					pg.setPenPaint(graphics, false);
					Point pnLoc = ((PICTInstruction.Line)inst).pnLoc;
					Point newPt = ((PICTInstruction.Line)inst).newPt;
					graphics.drawLine(pnLoc.x-l.getX(), pnLoc.y-l.getY(), newPt.x-l.getX(), newPt.y-l.getY());
					graphics.dispose();
				}
				pg.penX = ((PICTInstruction.Line)inst).newPt.x;
				pg.penY = ((PICTInstruction.Line)inst).newPt.y;
				break;
			case PICTInstruction.LineFrom.OPCODE:
				if (!deserialized) {
					paintMode = false;
					Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
					pg.setPenPaint(graphics, false);
					Point newPt = ((PICTInstruction.LineFrom)inst).newPt;
					graphics.drawLine(pg.penX-l.getX(), pg.penY-l.getY(), newPt.x-l.getX(), newPt.y-l.getY());
					graphics.dispose();
				}
				pg.penX = ((PICTInstruction.LineFrom)inst).newPt.x;
				pg.penY = ((PICTInstruction.LineFrom)inst).newPt.y;
				break;
			case PICTInstruction.ShortLine.OPCODE:
				if (!deserialized) {
					paintMode = false;
					Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
					pg.setPenPaint(graphics, false);
					Point pnLoc = ((PICTInstruction.ShortLine)inst).pnLoc;
					int dx = ((PICTInstruction.ShortLine)inst).dh;
					int dy = ((PICTInstruction.ShortLine)inst).dv;
					graphics.drawLine(pnLoc.x-l.getX(), pnLoc.y-l.getY(), pnLoc.x+dx-l.getX(), pnLoc.y+dy-l.getY());
					graphics.dispose();
				}
				pg.penX = ((PICTInstruction.ShortLine)inst).pnLoc.x + ((PICTInstruction.ShortLine)inst).dh;
				pg.penY = ((PICTInstruction.ShortLine)inst).pnLoc.y + ((PICTInstruction.ShortLine)inst).dv;
				break;
			case PICTInstruction.ShortLineFrom.OPCODE:
				if (!deserialized) {
					paintMode = false;
					Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
					pg.setPenPaint(graphics, false);
					int dx = ((PICTInstruction.ShortLineFrom)inst).dh;
					int dy = ((PICTInstruction.ShortLineFrom)inst).dv;
					graphics.drawLine(pg.penX-l.getX(), pg.penY-l.getY(), pg.penX+dx-l.getX(), pg.penY+dy-l.getY());
					graphics.dispose();
				}
				pg.penX += ((PICTInstruction.ShortLineFrom)inst).dh;
				pg.penY += ((PICTInstruction.ShortLineFrom)inst).dv;
				break;
			case PICTInstruction.LongText.OPCODE:
				if (!deserialized) {
					paintMode = false;
					Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
					pg.setPenPaint(graphics, false);
					graphics.drawString(
							((PICTInstruction.LongText)inst).text,
							((PICTInstruction.LongText)inst).txLoc.x - l.getX(),
							((PICTInstruction.LongText)inst).txLoc.y - l.getY()
					);
					graphics.dispose();
				}
				pg.penX = ((PICTInstruction.LongText)inst).txLoc.x;
				pg.penY = ((PICTInstruction.LongText)inst).txLoc.y;
				break;
			case PICTInstruction.DHText.OPCODE:
				if (!deserialized) {
					paintMode = false;
					Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
					pg.setPenPaint(graphics, false);
					graphics.drawString(
							((PICTInstruction.DHText)inst).text,
							pg.penX + ((PICTInstruction.DHText)inst).dh - l.getX(),
							pg.penY - l.getY()
					);
					graphics.dispose();
				}
				pg.penX += ((PICTInstruction.DHText)inst).dh;
				break;
			case PICTInstruction.DVText.OPCODE:
				if (!deserialized) {
					paintMode = false;
					Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
					pg.setPenPaint(graphics, false);
					graphics.drawString(
							((PICTInstruction.DVText)inst).text,
							pg.penX - l.getX(),
							pg.penY + ((PICTInstruction.DVText)inst).dv - l.getY()
					);
					graphics.dispose();
				}
				pg.penY += ((PICTInstruction.DVText)inst).dv;
				break;
			case PICTInstruction.DHDVText.OPCODE:
				if (!deserialized) {
					paintMode = false;
					Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
					pg.setPenPaint(graphics, false);
					graphics.drawString(
							((PICTInstruction.DHDVText)inst).text,
							pg.penX + ((PICTInstruction.DHDVText)inst).dh - l.getX(),
							pg.penY + ((PICTInstruction.DHDVText)inst).dv - l.getY()
					);
					graphics.dispose();
				}
				pg.penX += ((PICTInstruction.DHDVText)inst).dh;
				pg.penY += ((PICTInstruction.DHDVText)inst).dv;
				break;
			case PICTInstruction.FrameRect.OPCODE:
				pg.lastRect = ((PICTInstruction.FrameRect)inst).rect;
			case PICTInstruction.FrameSameRect.OPCODE:
				if (!deserialized) {
					paintMode = false;
					Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
					pg.setPenPaint(graphics, false);
					graphics.drawRect(pg.lastRect.left-l.getX(), pg.lastRect.top-l.getY(), pg.lastRect.right-pg.lastRect.left, pg.lastRect.bottom-pg.lastRect.top);
					graphics.dispose();
				}
				break;
			case PICTInstruction.PaintRect.OPCODE:
				pg.lastRect = ((PICTInstruction.PaintRect)inst).rect;
			case PICTInstruction.PaintSameRect.OPCODE:
				if (!deserialized) {
					paintMode = false;
					Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
					pg.setPenPaint(graphics, true);
					graphics.fillRect(pg.lastRect.left-l.getX(), pg.lastRect.top-l.getY(), pg.lastRect.right-pg.lastRect.left, pg.lastRect.bottom-pg.lastRect.top);
					graphics.dispose();
				}
				break;
			case PICTInstruction.EraseRect.OPCODE:
				pg.lastRect = ((PICTInstruction.EraseRect)inst).rect;
			case PICTInstruction.EraseSameRect.OPCODE:
				if (!deserialized) {
					paintMode = false;
					Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
					pg.setBackgroundPaint(graphics, true);
					graphics.fillRect(pg.lastRect.left-l.getX(), pg.lastRect.top-l.getY(), pg.lastRect.right-pg.lastRect.left, pg.lastRect.bottom-pg.lastRect.top);
					graphics.dispose();
				}
				break;
			case PICTInstruction.InvertRect.OPCODE:
				pg.lastRect = ((PICTInstruction.InvertRect)inst).rect;
			case PICTInstruction.InvertSameRect.OPCODE:
				if (!deserialized) {
					paintMode = false;
					Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
					pg.setInvertPaint(graphics, true);
					graphics.fillRect(pg.lastRect.left-l.getX(), pg.lastRect.top-l.getY(), pg.lastRect.right-pg.lastRect.left, pg.lastRect.bottom-pg.lastRect.top);
					graphics.dispose();
				}
				break;
			case PICTInstruction.FillRect.OPCODE:
				pg.lastRect = ((PICTInstruction.FillRect)inst).rect;
			case PICTInstruction.FillSameRect.OPCODE:
				if (!deserialized) {
					paintMode = false;
					Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
					pg.setFillPaint(graphics, true);
					graphics.fillRect(pg.lastRect.left-l.getX(), pg.lastRect.top-l.getY(), pg.lastRect.right-pg.lastRect.left, pg.lastRect.bottom-pg.lastRect.top);
					graphics.dispose();
				}
				break;
			case PICTInstruction.FrameRRect.OPCODE:
				pg.lastRect = ((PICTInstruction.FrameRRect)inst).rect;
			case PICTInstruction.FrameSameRRect.OPCODE:
				if (!deserialized) {
					paintMode = false;
					Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
					pg.setPenPaint(graphics, false);
					graphics.drawRoundRect(pg.lastRect.left-l.getX(), pg.lastRect.top-l.getY(), pg.lastRect.right-pg.lastRect.left, pg.lastRect.bottom-pg.lastRect.top, pg.cornerRadiusW, pg.cornerRadiusH);
					graphics.dispose();
				}
				break;
			case PICTInstruction.PaintRRect.OPCODE:
				pg.lastRect = ((PICTInstruction.PaintRRect)inst).rect;
			case PICTInstruction.PaintSameRRect.OPCODE:
				if (!deserialized) {
					paintMode = false;
					Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
					pg.setPenPaint(graphics, true);
					graphics.fillRoundRect(pg.lastRect.left-l.getX(), pg.lastRect.top-l.getY(), pg.lastRect.right-pg.lastRect.left, pg.lastRect.bottom-pg.lastRect.top, pg.cornerRadiusW, pg.cornerRadiusH);
					graphics.dispose();
				}
				break;
			case PICTInstruction.EraseRRect.OPCODE:
				pg.lastRect = ((PICTInstruction.EraseRRect)inst).rect;
			case PICTInstruction.EraseSameRRect.OPCODE:
				if (!deserialized) {
					paintMode = false;
					Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
					pg.setBackgroundPaint(graphics, true);
					graphics.fillRoundRect(pg.lastRect.left-l.getX(), pg.lastRect.top-l.getY(), pg.lastRect.right-pg.lastRect.left, pg.lastRect.bottom-pg.lastRect.top, pg.cornerRadiusW, pg.cornerRadiusH);
					graphics.dispose();
				}
				break;
			case PICTInstruction.InvertRRect.OPCODE:
				pg.lastRect = ((PICTInstruction.InvertRRect)inst).rect;
			case PICTInstruction.InvertSameRRect.OPCODE:
				if (!deserialized) {
					paintMode = false;
					Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
					pg.setInvertPaint(graphics, true);
					graphics.fillRoundRect(pg.lastRect.left-l.getX(), pg.lastRect.top-l.getY(), pg.lastRect.right-pg.lastRect.left, pg.lastRect.bottom-pg.lastRect.top, pg.cornerRadiusW, pg.cornerRadiusH);
					graphics.dispose();
				}
				break;
			case PICTInstruction.FillRRect.OPCODE:
				pg.lastRect = ((PICTInstruction.FillRRect)inst).rect;
			case PICTInstruction.FillSameRRect.OPCODE:
				if (!deserialized) {
					paintMode = false;
					Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
					pg.setFillPaint(graphics, true);
					graphics.fillRoundRect(pg.lastRect.left-l.getX(), pg.lastRect.top-l.getY(), pg.lastRect.right-pg.lastRect.left, pg.lastRect.bottom-pg.lastRect.top, pg.cornerRadiusW, pg.cornerRadiusH);
					graphics.dispose();
				}
				break;
			case PICTInstruction.FrameOval.OPCODE:
				pg.lastRect = ((PICTInstruction.FrameOval)inst).rect;
			case PICTInstruction.FrameSameOval.OPCODE:
				if (!deserialized) {
					paintMode = false;
					Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
					pg.setPenPaint(graphics, false);
					graphics.drawOval(pg.lastRect.left-l.getX(), pg.lastRect.top-l.getY(), pg.lastRect.right-pg.lastRect.left, pg.lastRect.bottom-pg.lastRect.top);
					graphics.dispose();
				}
				break;
			case PICTInstruction.PaintOval.OPCODE:
				pg.lastRect = ((PICTInstruction.PaintOval)inst).rect;
			case PICTInstruction.PaintSameOval.OPCODE:
				if (!deserialized) {
					paintMode = false;
					Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
					pg.setPenPaint(graphics, true);
					graphics.fillOval(pg.lastRect.left-l.getX(), pg.lastRect.top-l.getY(), pg.lastRect.right-pg.lastRect.left, pg.lastRect.bottom-pg.lastRect.top);
					graphics.dispose();
				}
				break;
			case PICTInstruction.EraseOval.OPCODE:
				pg.lastRect = ((PICTInstruction.EraseOval)inst).rect;
			case PICTInstruction.EraseSameOval.OPCODE:
				if (!deserialized) {
					paintMode = false;
					Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
					pg.setBackgroundPaint(graphics, true);
					graphics.fillOval(pg.lastRect.left-l.getX(), pg.lastRect.top-l.getY(), pg.lastRect.right-pg.lastRect.left, pg.lastRect.bottom-pg.lastRect.top);
					graphics.dispose();
				}
				break;
			case PICTInstruction.InvertOval.OPCODE:
				pg.lastRect = ((PICTInstruction.InvertOval)inst).rect;
			case PICTInstruction.InvertSameOval.OPCODE:
				if (!deserialized) {
					paintMode = false;
					Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
					pg.setInvertPaint(graphics, true);
					graphics.fillOval(pg.lastRect.left-l.getX(), pg.lastRect.top-l.getY(), pg.lastRect.right-pg.lastRect.left, pg.lastRect.bottom-pg.lastRect.top);
					graphics.dispose();
				}
				break;
			case PICTInstruction.FillOval.OPCODE:
				pg.lastRect = ((PICTInstruction.FillOval)inst).rect;
			case PICTInstruction.FillSameOval.OPCODE:
				if (!deserialized) {
					paintMode = false;
					Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
					pg.setFillPaint(graphics, true);
					graphics.fillOval(pg.lastRect.left-l.getX(), pg.lastRect.top-l.getY(), pg.lastRect.right-pg.lastRect.left, pg.lastRect.bottom-pg.lastRect.top);
					graphics.dispose();
				}
				break;
			case PICTInstruction.FrameArc.OPCODE:
				pg.lastRect = ((PICTInstruction.FrameArc)inst).rect;
			case PICTInstruction.FrameSameArc.OPCODE:
				if (!deserialized) {
					paintMode = false;
					Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
					pg.setPenPaint(graphics, false);
					int startAngle = (inst instanceof PICTInstruction.FrameSameArc)
							? ((PICTInstruction.FrameSameArc)inst).startAngle
							: ((PICTInstruction.FrameArc)inst).startAngle;
					int arcAngle = (inst instanceof PICTInstruction.FrameSameArc)
							? ((PICTInstruction.FrameSameArc)inst).arcAngle
							: ((PICTInstruction.FrameArc)inst).arcAngle;
					graphics.drawArc(pg.lastRect.left-l.getX(), pg.lastRect.top-l.getY(), pg.lastRect.right-pg.lastRect.left, pg.lastRect.bottom-pg.lastRect.top, startAngle, arcAngle);
					graphics.dispose();
				}
				break;
			case PICTInstruction.PaintArc.OPCODE:
				pg.lastRect = ((PICTInstruction.PaintArc)inst).rect;
			case PICTInstruction.PaintSameArc.OPCODE:
				if (!deserialized) {
					paintMode = false;
					Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
					pg.setPenPaint(graphics, true);
					int startAngle = (inst instanceof PICTInstruction.FrameSameArc)
							? ((PICTInstruction.FrameSameArc)inst).startAngle
							: ((PICTInstruction.FrameArc)inst).startAngle;
					int arcAngle = (inst instanceof PICTInstruction.FrameSameArc)
							? ((PICTInstruction.FrameSameArc)inst).arcAngle
							: ((PICTInstruction.FrameArc)inst).arcAngle;
					graphics.fillArc(pg.lastRect.left-l.getX(), pg.lastRect.top-l.getY(), pg.lastRect.right-pg.lastRect.left, pg.lastRect.bottom-pg.lastRect.top, startAngle, arcAngle);
					graphics.dispose();
				}
				break;
			case PICTInstruction.EraseArc.OPCODE:
				pg.lastRect = ((PICTInstruction.EraseArc)inst).rect;
			case PICTInstruction.EraseSameArc.OPCODE:
				if (!deserialized) {
					paintMode = false;
					Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
					pg.setBackgroundPaint(graphics, true);
					int startAngle = (inst instanceof PICTInstruction.FrameSameArc)
							? ((PICTInstruction.FrameSameArc)inst).startAngle
							: ((PICTInstruction.FrameArc)inst).startAngle;
					int arcAngle = (inst instanceof PICTInstruction.FrameSameArc)
							? ((PICTInstruction.FrameSameArc)inst).arcAngle
							: ((PICTInstruction.FrameArc)inst).arcAngle;
					graphics.fillArc(pg.lastRect.left-l.getX(), pg.lastRect.top-l.getY(), pg.lastRect.right-pg.lastRect.left, pg.lastRect.bottom-pg.lastRect.top, startAngle, arcAngle);
					graphics.dispose();
				}
				break;
			case PICTInstruction.InvertArc.OPCODE:
				pg.lastRect = ((PICTInstruction.InvertArc)inst).rect;
			case PICTInstruction.InvertSameArc.OPCODE:
				if (!deserialized) {
					paintMode = false;
					Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
					pg.setInvertPaint(graphics, true);
					int startAngle = (inst instanceof PICTInstruction.FrameSameArc)
							? ((PICTInstruction.FrameSameArc)inst).startAngle
							: ((PICTInstruction.FrameArc)inst).startAngle;
					int arcAngle = (inst instanceof PICTInstruction.FrameSameArc)
							? ((PICTInstruction.FrameSameArc)inst).arcAngle
							: ((PICTInstruction.FrameArc)inst).arcAngle;
					graphics.fillArc(pg.lastRect.left-l.getX(), pg.lastRect.top-l.getY(), pg.lastRect.right-pg.lastRect.left, pg.lastRect.bottom-pg.lastRect.top, startAngle, arcAngle);
					graphics.dispose();
				}
				break;
			case PICTInstruction.FillArc.OPCODE:
				pg.lastRect = ((PICTInstruction.FillArc)inst).rect;
			case PICTInstruction.FillSameArc.OPCODE:
				if (!deserialized) {
					paintMode = false;
					Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
					pg.setFillPaint(graphics, true);
					int startAngle = (inst instanceof PICTInstruction.FrameSameArc)
							? ((PICTInstruction.FrameSameArc)inst).startAngle
							: ((PICTInstruction.FrameArc)inst).startAngle;
					int arcAngle = (inst instanceof PICTInstruction.FrameSameArc)
							? ((PICTInstruction.FrameSameArc)inst).arcAngle
							: ((PICTInstruction.FrameArc)inst).arcAngle;
					graphics.fillArc(pg.lastRect.left-l.getX(), pg.lastRect.top-l.getY(), pg.lastRect.right-pg.lastRect.left, pg.lastRect.bottom-pg.lastRect.top, startAngle, arcAngle);
					graphics.dispose();
				}
				break;
			case PICTInstruction.FramePoly.OPCODE:
				pg.lastPoly = ((PICTInstruction.FramePoly)inst).poly;
			case PICTInstruction.FrameSamePoly.OPCODE:
				if (!deserialized) {
					paintMode = false;
					Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
					pg.setPenPaint(graphics, false);
					graphics.draw(AffineTransform.getTranslateInstance(-l.getX(), -l.getY()).createTransformedShape(pg.lastPoly.toPolygon()));
					graphics.dispose();
				}
				break;
			case PICTInstruction.PaintPoly.OPCODE:
				pg.lastPoly = ((PICTInstruction.PaintPoly)inst).poly;
			case PICTInstruction.PaintSamePoly.OPCODE:
				if (!deserialized) {
					paintMode = false;
					Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
					pg.setPenPaint(graphics, true);
					graphics.fill(AffineTransform.getTranslateInstance(-l.getX(), -l.getY()).createTransformedShape(pg.lastPoly.toPolygon()));
					graphics.dispose();
				}
				break;
			case PICTInstruction.ErasePoly.OPCODE:
				pg.lastPoly = ((PICTInstruction.ErasePoly)inst).poly;
			case PICTInstruction.EraseSamePoly.OPCODE:
				if (!deserialized) {
					paintMode = false;
					Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
					pg.setBackgroundPaint(graphics, true);
					graphics.fill(AffineTransform.getTranslateInstance(-l.getX(), -l.getY()).createTransformedShape(pg.lastPoly.toPolygon()));
					graphics.dispose();
				}
				break;
			case PICTInstruction.InvertPoly.OPCODE:
				pg.lastPoly = ((PICTInstruction.InvertPoly)inst).poly;
			case PICTInstruction.InvertSamePoly.OPCODE:
				if (!deserialized) {
					paintMode = false;
					Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
					pg.setInvertPaint(graphics, true);
					graphics.fill(AffineTransform.getTranslateInstance(-l.getX(), -l.getY()).createTransformedShape(pg.lastPoly.toPolygon()));
					graphics.dispose();
				}
				break;
			case PICTInstruction.FillPoly.OPCODE:
				pg.lastPoly = ((PICTInstruction.FillPoly)inst).poly;
			case PICTInstruction.FillSamePoly.OPCODE:
				if (!deserialized) {
					paintMode = false;
					Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
					pg.setFillPaint(graphics, true);
					graphics.fill(AffineTransform.getTranslateInstance(-l.getX(), -l.getY()).createTransformedShape(pg.lastPoly.toPolygon()));
					graphics.dispose();
				}
				break;
			case PICTInstruction.FrameRgn.OPCODE:
				pg.lastRgn = ((PICTInstruction.FrameRgn)inst).rgn;
			case PICTInstruction.FrameSameRgn.OPCODE:
				if (!deserialized) {
					paintMode = false;
					Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
					pg.setPenPaint(graphics, false);
					graphics.draw(AffineTransform.getTranslateInstance(-l.getX(), -l.getY()).createTransformedShape(pg.lastRgn.toArea()));
					graphics.dispose();
				}
				break;
			case PICTInstruction.PaintRgn.OPCODE:
				pg.lastRgn = ((PICTInstruction.PaintRgn)inst).rgn;
			case PICTInstruction.PaintSameRgn.OPCODE:
				if (!deserialized) {
					paintMode = false;
					Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
					pg.setPenPaint(graphics, true);
					graphics.fill(AffineTransform.getTranslateInstance(-l.getX(), -l.getY()).createTransformedShape(pg.lastRgn.toArea()));
					graphics.dispose();
				}
				break;
			case PICTInstruction.EraseRgn.OPCODE:
				pg.lastRgn = ((PICTInstruction.EraseRgn)inst).rgn;
			case PICTInstruction.EraseSameRgn.OPCODE:
				if (!deserialized) {
					paintMode = false;
					Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
					pg.setBackgroundPaint(graphics, true);
					graphics.fill(AffineTransform.getTranslateInstance(-l.getX(), -l.getY()).createTransformedShape(pg.lastRgn.toArea()));
					graphics.dispose();
				}
				break;
			case PICTInstruction.InvertRgn.OPCODE:
				pg.lastRgn = ((PICTInstruction.InvertRgn)inst).rgn;
			case PICTInstruction.InvertSameRgn.OPCODE:
				if (!deserialized) {
					paintMode = false;
					Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
					pg.setInvertPaint(graphics, true);
					graphics.fill(AffineTransform.getTranslateInstance(-l.getX(), -l.getY()).createTransformedShape(pg.lastRgn.toArea()));
					graphics.dispose();
				}
				break;
			case PICTInstruction.FillRgn.OPCODE:
				pg.lastRgn = ((PICTInstruction.FillRgn)inst).rgn;
			case PICTInstruction.FillSameRgn.OPCODE:
				if (!deserialized) {
					paintMode = false;
					Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
					pg.setFillPaint(graphics, true);
					graphics.fill(AffineTransform.getTranslateInstance(-l.getX(), -l.getY()).createTransformedShape(pg.lastRgn.toArea()));
					graphics.dispose();
				}
				break;
			case PICTInstruction.BitsRect.OPCODE:
				{
					PICTInstruction.BitsRect binst = (PICTInstruction.BitsRect)inst;
					int bx = binst.pixMap.bounds.left;
					int by = binst.pixMap.bounds.top;
					BufferedImage bimg = PICTUtilities.pixmapToImage(binst.pixMap, binst.colorTable, binst.pixData, pg.bgColor, pg.fgColor, false, true);
					if (paintMode) {
						Graphics2D graphics = l.createPaintGraphics();
						graphics.translate(-l.getX(), -l.getY());
						pg.setPenPaint(graphics, true);
						graphics.drawImage(
								bimg,
								binst.dstRect.left, binst.dstRect.top,
								binst.dstRect.right, binst.dstRect.bottom,
								binst.srcRect.left-bx, binst.srcRect.top-by,
								binst.srcRect.right-bx, binst.srcRect.bottom-by,
								null
						);
						graphics.dispose();
					} else if (!deserialized) {
						Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
						pg.setPenPaint(graphics, true);
						graphics.drawImage(
								bimg,
								binst.dstRect.left-l.getX(), binst.dstRect.top-l.getY(),
								binst.dstRect.right-l.getX(), binst.dstRect.bottom-l.getY(),
								binst.srcRect.left-bx, binst.srcRect.top-by,
								binst.srcRect.right-bx, binst.srcRect.bottom-by,
								null
						);
						graphics.dispose();
						l.get(l.size()-1).setVisible(objVisible);
						l.get(l.size()-1).setLocked(objLocked);
						l.get(l.size()-1).setSelected(objSelected);
					}
				}
				break;
			case PICTInstruction.BitsRgn.OPCODE:
				{
					PICTInstruction.BitsRgn binst = (PICTInstruction.BitsRgn)inst;
					int bx = binst.pixMap.bounds.left;
					int by = binst.pixMap.bounds.top;
					BufferedImage bimg = PICTUtilities.pixmapToImage(binst.pixMap, binst.colorTable, binst.pixData, pg.bgColor, pg.fgColor, false, true);
					if (paintMode) {
						Graphics2D graphics = l.createPaintGraphics();
						graphics.translate(-l.getX(), -l.getY());
						pg.setPenPaint(graphics, true);
						graphics.clip(binst.maskRgn.toArea());
						graphics.drawImage(
								bimg,
								binst.dstRect.left, binst.dstRect.top,
								binst.dstRect.right, binst.dstRect.bottom,
								binst.srcRect.left-bx, binst.srcRect.top-by,
								binst.srcRect.right-bx, binst.srcRect.bottom-by,
								null
						);
						graphics.dispose();
					} else if (!deserialized) {
						Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
						pg.setPenPaint(graphics, true);
						graphics.drawImage(
								bimg,
								binst.dstRect.left-l.getX(), binst.dstRect.top-l.getY(),
								binst.dstRect.right-l.getX(), binst.dstRect.bottom-l.getY(),
								binst.srcRect.left-bx, binst.srcRect.top-by,
								binst.srcRect.right-bx, binst.srcRect.bottom-by,
								null
						);
						graphics.dispose();
						l.get(l.size()-1).setVisible(objVisible);
						l.get(l.size()-1).setLocked(objLocked);
						l.get(l.size()-1).setSelected(objSelected);
					}
				}
				break;
			case PICTInstruction.PackBitsRect.OPCODE:
				{
					PICTInstruction.PackBitsRect binst = (PICTInstruction.PackBitsRect)inst;
					int bx = binst.pixMap.bounds.left;
					int by = binst.pixMap.bounds.top;
					BufferedImage bimg = PICTUtilities.pixmapToImage(binst.pixMap, binst.colorTable, binst.pixData, pg.bgColor, pg.fgColor, false, true);
					if (paintMode) {
						Graphics2D graphics = l.createPaintGraphics();
						graphics.translate(-l.getX(), -l.getY());
						pg.setPenPaint(graphics, true);
						graphics.drawImage(
								bimg,
								binst.dstRect.left, binst.dstRect.top,
								binst.dstRect.right, binst.dstRect.bottom,
								binst.srcRect.left-bx, binst.srcRect.top-by,
								binst.srcRect.right-bx, binst.srcRect.bottom-by,
								null
						);
						graphics.dispose();
					} else if (!deserialized) {
						Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
						pg.setPenPaint(graphics, true);
						graphics.drawImage(
								bimg,
								binst.dstRect.left-l.getX(), binst.dstRect.top-l.getY(),
								binst.dstRect.right-l.getX(), binst.dstRect.bottom-l.getY(),
								binst.srcRect.left-bx, binst.srcRect.top-by,
								binst.srcRect.right-bx, binst.srcRect.bottom-by,
								null
						);
						graphics.dispose();
						l.get(l.size()-1).setVisible(objVisible);
						l.get(l.size()-1).setLocked(objLocked);
						l.get(l.size()-1).setSelected(objSelected);
					}
				}
				break;
			case PICTInstruction.PackBitsRgn.OPCODE:
				{
					PICTInstruction.PackBitsRgn binst = (PICTInstruction.PackBitsRgn)inst;
					int bx = binst.pixMap.bounds.left;
					int by = binst.pixMap.bounds.top;
					BufferedImage bimg = PICTUtilities.pixmapToImage(binst.pixMap, binst.colorTable, binst.pixData, pg.bgColor, pg.fgColor, false, true);
					if (paintMode) {
						Graphics2D graphics = l.createPaintGraphics();
						graphics.translate(-l.getX(), -l.getY());
						pg.setPenPaint(graphics, true);
						graphics.clip(binst.maskRgn.toArea());
						graphics.drawImage(
								bimg,
								binst.dstRect.left, binst.dstRect.top,
								binst.dstRect.right, binst.dstRect.bottom,
								binst.srcRect.left-bx, binst.srcRect.top-by,
								binst.srcRect.right-bx, binst.srcRect.bottom-by,
								null
						);
						graphics.dispose();
					} else if (!deserialized) {
						Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
						pg.setPenPaint(graphics, true);
						graphics.drawImage(
								bimg,
								binst.dstRect.left-l.getX(), binst.dstRect.top-l.getY(),
								binst.dstRect.right-l.getX(), binst.dstRect.bottom-l.getY(),
								binst.srcRect.left-bx, binst.srcRect.top-by,
								binst.srcRect.right-bx, binst.srcRect.bottom-by,
								null
						);
						graphics.dispose();
						l.get(l.size()-1).setVisible(objVisible);
						l.get(l.size()-1).setLocked(objLocked);
						l.get(l.size()-1).setSelected(objSelected);
					}
				}
				break;
			case PICTInstruction.DirectBitsRect.OPCODE:
				{
					PICTInstruction.DirectBitsRect binst = (PICTInstruction.DirectBitsRect)inst;
					int bx = binst.pixMap.bounds.left;
					int by = binst.pixMap.bounds.top;
					BufferedImage bimg = PICTUtilities.pixmapToImage(binst.pixMap, null, binst.pixData, pg.bgColor, pg.fgColor, false, true);
					if (paintMode) {
						Graphics2D graphics = l.createPaintGraphics();
						graphics.translate(-l.getX(), -l.getY());
						pg.setPenPaint(graphics, true);
						graphics.drawImage(
								bimg,
								binst.dstRect.left, binst.dstRect.top,
								binst.dstRect.right, binst.dstRect.bottom,
								binst.srcRect.left-bx, binst.srcRect.top-by,
								binst.srcRect.right-bx, binst.srcRect.bottom-by,
								null
						);
						graphics.dispose();
					} else if (!deserialized) {
						Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
						pg.setPenPaint(graphics, true);
						graphics.drawImage(
								bimg,
								binst.dstRect.left-l.getX(), binst.dstRect.top-l.getY(),
								binst.dstRect.right-l.getX(), binst.dstRect.bottom-l.getY(),
								binst.srcRect.left-bx, binst.srcRect.top-by,
								binst.srcRect.right-bx, binst.srcRect.bottom-by,
								null
						);
						graphics.dispose();
						l.get(l.size()-1).setVisible(objVisible);
						l.get(l.size()-1).setLocked(objLocked);
						l.get(l.size()-1).setSelected(objSelected);
					}
				}
				break;
			case PICTInstruction.DirectBitsRgn.OPCODE:
				{
					PICTInstruction.DirectBitsRgn binst = (PICTInstruction.DirectBitsRgn)inst;
					int bx = binst.pixMap.bounds.left;
					int by = binst.pixMap.bounds.top;
					BufferedImage bimg = PICTUtilities.pixmapToImage(binst.pixMap, null, binst.pixData, pg.bgColor, pg.fgColor, false, true);
					if (paintMode) {
						Graphics2D graphics = l.createPaintGraphics();
						graphics.translate(-l.getX(), -l.getY());
						pg.setPenPaint(graphics, true);
						graphics.clip(binst.maskRgn.toArea());
						graphics.drawImage(
								bimg,
								binst.dstRect.left, binst.dstRect.top,
								binst.dstRect.right, binst.dstRect.bottom,
								binst.srcRect.left-bx, binst.srcRect.top-by,
								binst.srcRect.right-bx, binst.srcRect.bottom-by,
								null
						);
						graphics.dispose();
					} else if (!deserialized) {
						Graphics2D graphics = (gdo.isEmpty() ? l.createDrawGraphics() : gdo.peek().createDrawGraphics());
						pg.setPenPaint(graphics, true);
						graphics.drawImage(
								bimg,
								binst.dstRect.left-l.getX(), binst.dstRect.top-l.getY(),
								binst.dstRect.right-l.getX(), binst.dstRect.bottom-l.getY(),
								binst.srcRect.left-bx, binst.srcRect.top-by,
								binst.srcRect.right-bx, binst.srcRect.bottom-by,
								null
						);
						graphics.dispose();
						l.get(l.size()-1).setVisible(objVisible);
						l.get(l.size()-1).setLocked(objLocked);
						l.get(l.size()-1).setSelected(objSelected);
					}
				}
				break;
			case PICTInstruction.ShortComment.OPCODE:
				switch (((PICTInstruction.ShortComment)inst).kind) {
				case PICTInstruction.CommentConstants.GROUP_BEGIN:
					switch (nextGrpType) {
					case 2:
						// came from GroupDrawObject
						// GroupDrawObject is already on stack
						// next group, if any, will not come from PowerPaint
						// unless prefixed with a dGrp comment
						gdt.push(2);
						nextGrpType = 0;
						break;
					case 1:
						// came from PowerPaint layer
						// don't create a GroupDrawObject
						// next group, if any, will not come from PowerPaint
						// unless prefixed with a dGrp comment
						gdt.push(1);
						nextGrpType = 0;
						break;
					default:
						// came from something else
						// must create new GroupDrawObject
						// next group, if any, will not come from PowerPaint
						// unless prefixed with a dGrp comment
						GroupDrawObject grp = new GroupDrawObject();
						grp.setVisible(objVisible);
						grp.setLocked(objLocked);
						grp.setSelected(objSelected);
						if (gdo.isEmpty()) l.add(grp);
						else gdo.peek().add(grp);
						gdo.push(grp);
						gdt.push(0);
						nextGrpType = 0;
						break;
					}
					break;
				case PICTInstruction.CommentConstants.GROUP_END:
					switch (gdt.pop()) {
					case 2:
						// came from GroupDrawObject
						gdo.pop();
						break;
					case 1:
						// came from PowerPaint layer
						// GroupDrawObject was never created, so don't pop it
						break;
					default:
						// came from something else
						gdo.pop();
						break;
					}
					break;
				}
				break;
			case PICTInstruction.LongComment.OPCODE:
				PICTInstruction.LongComment cmt = (PICTInstruction.LongComment)inst;
				if (cmt.kind == PICTInstruction.CommentConstants.APPLICATION_COMMENT && cmt.appID == KPNT) {
					ByteArrayInputStream cdbi = new ByteArrayInputStream(cmt.data);
					DataInputStream cddi = new DataInputStream(cdbi);
					switch (cmt.ppcID) {
					case Canv:
						{
							int w = cddi.readInt();
							int h = cddi.readInt();
							int dpix = cddi.readInt();
							int dpiy = cddi.readInt();
							int hsx = cddi.readInt();
							int hsy = cddi.readInt();
							c.clear();
							c.setSize(w, h);
							c.setDPI(dpix, dpiy);
							c.setHotspot(hsx, hsy);
							l = null;
							t = null;
							nextGrpType = 0;
							deserialized = false;
							paintMode = true;
						}
						break;
					case Layr:
						{
							boolean vis = cddi.readBoolean();
							boolean lok = cddi.readBoolean();
							boolean sel = cddi.readBoolean();
							cddi.readBoolean();
							int x = cddi.readInt();
							int y = cddi.readInt();
							int matte = cddi.readInt();
							int ts = cddi.readInt();
							int nl = cddi.readInt();
							StringBuffer n = new StringBuffer(nl);
							while (nl-->0) n.append(cddi.readChar());
							l = new Layer(ts);
							l.setVisible(vis);
							l.setLocked(lok);
							l.setSelected(sel);
							l.setX(x);
							l.setY(y);
							l.setMatte(matte);
							l.setName(n.toString());
							c.add(l);
							t = null;
							nextGrpType = 1;
							deserialized = false;
							paintMode = true;
						}
						break;
					case Tile:
						{
							int x = cddi.readInt();
							int y = cddi.readInt();
							int w = cddi.readInt();
							int h = cddi.readInt();
							t = new Tile(x, y, w, h);
							l.addTile(t);
							deserialized = false;
							paintMode = true;
						}
						break;
					case $$AA:
						{
							if (alphaMaskDataBuffer == null) {
								alphaMaskDataBuffer = new ByteArrayOutputStream();
							}
							alphaMaskDataBuffer.write(cmt.data, 8, cmt.data.length-8);
						}
						break;
					case $$AM:
						{
							Rect abounds = Rect.read(cddi);
							if (alphaMaskDataBuffer == null) {
								alphaMaskDataBuffer = new ByteArrayOutputStream();
							}
							alphaMaskDataBuffer.write(cmt.data, 8, cmt.data.length-8);
							alphaMaskDataBuffer.close();
							byte[] adata = PICTUtilities.unpackBits(alphaMaskDataBuffer.toByteArray());
							int aptr = 0;
							int[] pixels = new int[(abounds.right-abounds.left)*(abounds.bottom-abounds.top)];
							l.getRGB(abounds.left-l.getX(), abounds.top-l.getY(), abounds.right-abounds.left, abounds.bottom-abounds.top, pixels, 0, abounds.right-abounds.left);
							for (int i = 0; i < pixels.length; i++) {
								pixels[i] = (pixels[i] & 0xFFFFFF) | ((adata[aptr++] & 0xFF) << 24);
							}
							for (int i = 0; i < pixels.length; i++) {
								if (adata[i] >= 0) {
									pixels[i] = (pixels[i] & 0xFF00FFFF) | ((adata[aptr++] & 0xFF) << 16);
								}
							}
							for (int i = 0; i < pixels.length; i++) {
								if (adata[i] >= 0) {
									pixels[i] = (pixels[i] & 0xFFFF00FF) | ((adata[aptr++] & 0xFF) << 8);
								}
							}
							for (int i = 0; i < pixels.length; i++) {
								if (adata[i] >= 0) {
									pixels[i] = (pixels[i] & 0xFFFFFF00) | (adata[aptr++] & 0xFF);
								}
							}
							l.setRGB(abounds.left-l.getX(), abounds.top-l.getY(), abounds.right-abounds.left, abounds.bottom-abounds.top, pixels, 0, abounds.right-abounds.left);
							alphaMaskDataBuffer = null;
						}
						break;
					case dGrp:
						{
							boolean vis = cddi.readBoolean();
							boolean lok = cddi.readBoolean();
							boolean sel = cddi.readBoolean();
							cddi.readBoolean();
							GroupDrawObject grp = new GroupDrawObject();
							grp.setVisible(vis);
							grp.setLocked(lok);
							grp.setSelected(sel);
							if (gdo.isEmpty()) l.add(grp);
							else gdo.peek().add(grp);
							gdo.push(grp);
							nextGrpType = 2;
							deserialized = false;
							paintMode = false;
						}
						break;
					case dObj:
						{
							objVisible = cddi.readBoolean();
							objLocked = cddi.readBoolean();
							objSelected = cddi.readBoolean();
							cddi.readBoolean();
							deserialized = false;
							paintMode = false;
						}
						break;
					case $$DO:
						{
							DrawObject o = (DrawObject)SerializationManager.deserializeObject(cmt.data);
							if (gdo.isEmpty()) l.add(o);
							else gdo.peek().add(o);
							deserialized = true;
							paintMode = false;
						}
						break;
					}
					cddi.close();
					cdbi.close();
				}
				break;
			}
		}
		c.clearPaintSelection();
		return c;
	}
	
	public boolean supportsWrite() { return true; }
	public boolean usesWriteOptionForm() { return true; }
	public Form getWriteOptionForm() {
		Form f = new Form();
		f.add(new CustomOption<JLabel>() {
			public String getName() { return null; }
			public JLabel makeUI(boolean mini) {
				return new JLabel("<html>NOTICE: The PICT writer is currently incomplete.<br>The resulting file is acceptable for low-resolution devices only.<br>Shapes are approximated with bitmaps rather than real QuickDraw shapes.</html>");
			}
			public void update(JLabel ui) {}
		});
		return f;
	}
	public int approximateFileSize(Canvas c) {
		return c.getWidth() * c.getHeight() * c.size() / 4;
	}
	
	public void write(Canvas c, DataOutputStream out, Monitor m) throws IOException {
		c.clearPaintSelection();
		PICTOutputStream pout = new PICTOutputStream(out);
		pout.write(new byte[512]);
		pout.writeShort(14);
		pout.writeRect(new Rect(0, 0, c.getWidth(), c.getHeight()));
		pout.writeInstruction(new PICTInstruction.VersionOp(2));
		pout.writeInstruction(new PICTInstruction.HeaderOp(-2, c.getDPIX(), c.getDPIY(), new Rect(0, 0, c.getWidth(), c.getHeight())));
		pout.writeInstruction(new PICTInstruction.DefHilite());
		pout.writeInstruction(new PICTInstruction.Clip(new Region(0, 0, c.getWidth(), c.getHeight())));
		pout.writeInstruction(new PICTInstruction.LongComment(
				PICTInstruction.CommentConstants.APPLICATION_COMMENT, KPNT, Canv,
				PICTUtilities.makeByteArray(c.getWidth(), c.getHeight(), c.getDPIX(), c.getDPIY(), c.getHotspotX(), c.getHotspotY())
		));
		for (Layer l : c) {
			pout.writeInstruction(new PICTInstruction.LongComment(
					PICTInstruction.CommentConstants.APPLICATION_COMMENT, KPNT, Layr,
					PICTUtilities.makeByteArray(l.isVisible(), l.isLocked(), l.isSelected(), (l.size() > 0),
					l.getX(), l.getY(), l.getMatte(), l.getTileSize(), l.getName())
			));
			if (l.getTiles().size() > 0) {
				pout.writeInstruction(new PICTInstruction.ShortComment(PICTInstruction.CommentConstants.BITMAP_BEGIN));
				for (Tile t : l.getTiles()) {
					int left = l.getX() + t.getX();
					int top = l.getY() + t.getY();
					int width = t.getWidth();
					int height = t.getHeight();
					int[] pixels = new int[width*height];
					t.getRGB(0, 0, width, height, pixels, 0, width);
					boolean empty = true;
					boolean hasAlpha = false;
					for (int pixel : pixels) if (((pixel >>> 24) & 0xFF) != 0x00) { empty = false; break; }
					for (int pixel : pixels) if (((pixel >>> 24) & 0xFF) != 0xFF) { hasAlpha = true; break; }
					if (empty) continue;
					pout.writeInstruction(new PICTInstruction.LongComment(
							PICTInstruction.CommentConstants.APPLICATION_COMMENT, KPNT, Tile,
							PICTUtilities.makeByteArray(t.getX(), t.getY(), t.getWidth(), t.getHeight())
					));
					if (hasAlpha) {
						pout.writeInstruction(PICTUtilities.makeBitsRgn(left, top, t.getImage(), -1, c.getDPIX(), c.getDPIY()));
						// begin PowerPaint alpha mask
						Rect abounds = new Rect(left, top, width, height);
						ByteArrayOutputStream aout = new ByteArrayOutputStream();
						for (int pixel : pixels) aout.write((pixel >>> 24) & 0xFF);
						for (int pixel : pixels) if (pixel >= 0) aout.write((pixel >>> 16) & 0xFF);
						for (int pixel : pixels) if (pixel >= 0) aout.write((pixel >>> 8) & 0xFF);
						for (int pixel : pixels) if (pixel >= 0) aout.write(pixel & 0xFF);
						aout.close();
						byte[] adata = PICTUtilities.packBits(aout.toByteArray());
						int aptr = 0;
						while ((adata.length - aptr) > 16384) {
							ByteArrayOutputStream iout = new ByteArrayOutputStream();
							DataOutputStream idout = new DataOutputStream(iout);
							abounds.write(idout);
							idout.write(adata, aptr, 16384);
							idout.close();
							iout.close();
							aptr += 16384;
							pout.writeInstruction(new PICTInstruction.LongComment(
									PICTInstruction.CommentConstants.APPLICATION_COMMENT, KPNT, $$AA,
									iout.toByteArray()
							));
						}
						ByteArrayOutputStream iout = new ByteArrayOutputStream();
						DataOutputStream idout = new DataOutputStream(iout);
						abounds.write(idout);
						idout.write(adata, aptr, adata.length-aptr);
						idout.close();
						iout.close();
						pout.writeInstruction(new PICTInstruction.LongComment(
								PICTInstruction.CommentConstants.APPLICATION_COMMENT, KPNT, $$AM,
								iout.toByteArray()
						));
						// end PowerPaint alpha mask
					} else {
						pout.writeInstruction(PICTUtilities.makeBitsRect(left, top, t.getImage(), -1, c.getDPIX(), c.getDPIY()));
					}
				}
				pout.writeInstruction(new PICTInstruction.ShortComment(PICTInstruction.CommentConstants.BITMAP_END));
			}
			if (l.size() > 0) {
				pout.writeInstruction(new PICTInstruction.ShortComment(PICTInstruction.CommentConstants.GROUP_BEGIN));
				for (DrawObject o : l) writeDrawObject(c, l.getX(), l.getY(), o, pout);
				pout.writeInstruction(new PICTInstruction.ShortComment(PICTInstruction.CommentConstants.GROUP_END));
			}
		}
		pout.writeInstruction(new PICTInstruction.OpEndPic());
	}
	
	private static void writeDrawObject(Canvas c, int lx, int ly, DrawObject o, PICTOutputStream pout) throws IOException {
		if (o instanceof GroupDrawObject) {
			pout.writeInstruction(new PICTInstruction.LongComment(
					PICTInstruction.CommentConstants.APPLICATION_COMMENT, KPNT, dGrp,
					PICTUtilities.makeByteArray(o.isVisible(), o.isLocked(), o.isSelected(), false)
			));
			pout.writeInstruction(new PICTInstruction.ShortComment(PICTInstruction.CommentConstants.GROUP_BEGIN));
			for (DrawObject oo : (GroupDrawObject)o) writeDrawObject(c, lx, ly, oo, pout);
			pout.writeInstruction(new PICTInstruction.ShortComment(PICTInstruction.CommentConstants.GROUP_END));
		} else {
			pout.writeInstruction(new PICTInstruction.LongComment(
					PICTInstruction.CommentConstants.APPLICATION_COMMENT, KPNT, dObj,
					PICTUtilities.makeByteArray(o.isVisible(), o.isLocked(), o.isSelected(), false)
			));
			pout.writeInstruction(new PICTInstruction.LongComment(
					PICTInstruction.CommentConstants.APPLICATION_COMMENT, KPNT, $$DO,
					SerializationManager.serializeObject(o)
			));
			writeDrawObjectBitmap(c, lx, ly, o, pout);
		}
	}
	
	private static void writeDrawObjectBitmap(Canvas c, int lx, int ly, DrawObject o, PICTOutputStream pout) throws IOException {
		Rectangle bounds = o.getBounds();
		if (bounds.width > 0 && bounds.height > 0) {
			BufferedImage bimg = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D bg = bimg.createGraphics();
			o.paint(bg, -bounds.x, -bounds.y);
			bg.dispose();
			pout.writeInstruction(PICTUtilities.makeBitsRgn(lx+bounds.x, ly+bounds.y, bimg, -1, c.getDPIX(), c.getDPIY()));
		}
	}
}
