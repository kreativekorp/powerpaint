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

package com.kreative.paint.pict;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

public class PICTGraphics {
	public Graphics2D graphics = null;
	public Area clip = null;
	public int fgColor = 0xFF000000;
	public int bgColor = 0xFFFFFFFF;
	public boolean hiliteMode = false;
	public int hiliteColor = 0xFFCCCCFF;
	public int opColor = 0xFF000000;
	public long bgPat = 0L;
	public Paint bgPPat = null;
	public int penX = 0;
	public int penY = 0;
	public int penW = 1;
	public int penH = 1;
	public int penMode = 0;
	public long penPat = -1L;
	public Paint penPPat = null;
	public long fillPat = -1L;
	public Paint fillPPat = null;
	public int cornerRadiusW = 16;
	public int cornerRadiusH = 16;
	public String fontName = "Geneva";
	public int fontStyle = 0;
	public int fontSize = 12;
	public int fontMode = 0;
	public float spExtra = 0;
	public int chExtra = 0;
	public float txRatioX = 1;
	public float txRatioY = 1;
	public float penHFrac = 0.5f;
	public float chSpacing = 0;
	public float jsSpace = 0;
	public boolean outlinePreferred = false;
	public boolean preserveGlyph = false;
	public boolean fractionalWidths = false;
	public boolean scalingDisabled = false;
	public Rect lastRect = null;
	public Polygon lastPoly = null;
	public Region lastRgn = null;
	
	public PICTGraphics() {
		this.graphics = null;
	}
	
	public PICTGraphics(Graphics2D g) {
		this.graphics = g;
	}
	
	public Paint getBackgroundPaint() {
		if (hiliteMode) return new Color(hiliteColor);
		else if (bgPPat != null) return bgPPat;
		else if (bgPat == 0L) return new Color(bgColor);
		else if (bgPat == -1L) return new Color(fgColor);
		else {
			BufferedImage bi = new BufferedImage(8, 8, BufferedImage.TYPE_INT_RGB);
			long m = 0x8000000000000000L;
			for (int y = 0; y < 8; y++) {
				for (int x = 0; x < 8; x++) {
					bi.setRGB(x, y, ((bgPat & m) == 0) ? bgColor : fgColor);
					m >>>= 1L;
				}
			}
			return new TexturePaint(bi, new Rectangle(0, 0, 8, 8));
		}
	}
	
	public Paint getPenPaint() {
		if (hiliteMode) return new Color(hiliteColor);
		else if (penPPat != null) return penPPat;
		else if (penPat == 0L) return new Color(bgColor);
		else if (penPat == -1L) return new Color(fgColor);
		else {
			BufferedImage bi = new BufferedImage(8, 8, BufferedImage.TYPE_INT_RGB);
			long m = 0x8000000000000000L;
			for (int y = 0; y < 8; y++) {
				for (int x = 0; x < 8; x++) {
					bi.setRGB(x, y, ((penPat & m) == 0) ? bgColor : fgColor);
					m >>>= 1L;
				}
			}
			return new TexturePaint(bi, new Rectangle(0, 0, 8, 8));
		}
	}
	
	public Paint getFillPaint() {
		if (hiliteMode) return new Color(hiliteColor);
		else if (fillPPat != null) return fillPPat;
		else if (fillPat == 0L) return new Color(bgColor);
		else if (fillPat == -1L) return new Color(fgColor);
		else {
			BufferedImage bi = new BufferedImage(8, 8, BufferedImage.TYPE_INT_RGB);
			long m = 0x8000000000000000L;
			for (int y = 0; y < 8; y++) {
				for (int x = 0; x < 8; x++) {
					bi.setRGB(x, y, ((fillPat & m) == 0) ? bgColor : fgColor);
					m >>>= 1L;
				}
			}
			return new TexturePaint(bi, new Rectangle(0, 0, 8, 8));
		}
	}
	
	public Stroke getStroke() {
		return new BasicStroke((penW+penH)/2.0f);
	}
	
	public Font getFont() {
		switch (fontStyle & 3) {
		case 0: return new Font(fontName, Font.PLAIN, fontSize);
		case 1: return new Font(fontName, Font.BOLD, fontSize);
		case 2: return new Font(fontName, Font.ITALIC, fontSize);
		case 3: return new Font(fontName, Font.BOLD|Font.ITALIC, fontSize);
		default: return new Font(fontName, Font.PLAIN, fontSize);
		}
	}
	
	public void setBackgroundPaint(Graphics2D graphics, boolean fillMode) {
		if (graphics != null) {
			graphics.setClip(clip);
			graphics.setPaintMode();
			graphics.setPaint(getBackgroundPaint());
			graphics.setStroke(fillMode ? new BasicStroke(0) : getStroke());
			graphics.setFont(getFont());
		}
	}
	
	public void setPenPaint(Graphics2D graphics, boolean fillMode) {
		if (graphics != null) {
			graphics.setClip(clip);
			graphics.setPaintMode();
			graphics.setPaint(getPenPaint());
			graphics.setStroke(fillMode ? new BasicStroke(0) : getStroke());
			graphics.setFont(getFont());
		}
	}
	
	public void setFillPaint(Graphics2D graphics, boolean fillMode) {
		if (graphics != null) {
			graphics.setClip(clip);
			graphics.setPaintMode();
			graphics.setPaint(getFillPaint());
			graphics.setStroke(fillMode ? new BasicStroke(0) : getStroke());
			graphics.setFont(getFont());
		}
	}
	
	public void setInvertPaint(Graphics2D graphics, boolean fillMode) {
		if (graphics != null) {
			graphics.setClip(clip);
			graphics.setPaint(Color.white);
			graphics.setXORMode(Color.black);
			graphics.setStroke(fillMode ? new BasicStroke(0) : getStroke());
			graphics.setFont(getFont());
		}
	}
	
	public void executeInstruction(PICTInstruction inst) {
		switch (inst.opcode) {
		case PICTInstruction.Clip.OPCODE:
			clip = ((PICTInstruction.Clip)inst).rgn.toArea();
			break;
		case PICTInstruction.BkPat.OPCODE:
			bgPat = ((PICTInstruction.BkPat)inst).pat;
			bgPPat = null;
			break;
		case PICTInstruction.TxFont.OPCODE:
			fontName = ((PICTInstruction.TxFont)inst).toFontName();
			break;
		case PICTInstruction.TxFace.OPCODE:
			fontStyle = ((PICTInstruction.TxFace)inst).style;
			break;
		case PICTInstruction.TxMode.OPCODE:
			fontMode = ((PICTInstruction.TxMode)inst).mode;
			break;
		case PICTInstruction.SpExtra.OPCODE:
			spExtra = ((PICTInstruction.SpExtra)inst).spExtra;
			break;
		case PICTInstruction.PnSize.OPCODE:
			penW = ((PICTInstruction.PnSize)inst).penWidth;
			penH = ((PICTInstruction.PnSize)inst).penHeight;
			break;
		case PICTInstruction.PnMode.OPCODE:
			penMode = ((PICTInstruction.PnMode)inst).mode;
			break;
		case PICTInstruction.PnPat.OPCODE:
			penPat = ((PICTInstruction.PnPat)inst).pat;
			penPPat = null;
			break;
		case PICTInstruction.FillPat.OPCODE:
			fillPat = ((PICTInstruction.FillPat)inst).pat;
			fillPPat = null;
			break;
		case PICTInstruction.OvSize.OPCODE:
			cornerRadiusW = ((PICTInstruction.OvSize)inst).ovalWidth;
			cornerRadiusH = ((PICTInstruction.OvSize)inst).ovalHeight;
			break;
		case PICTInstruction.Origin.OPCODE:
			penX += ((PICTInstruction.Origin)inst).dh;
			penY += ((PICTInstruction.Origin)inst).dv;
			break;
		case PICTInstruction.TxSize.OPCODE:
			fontSize = ((PICTInstruction.TxSize)inst).size;
			break;
		case PICTInstruction.FgColor.OPCODE:
			fgColor = ((PICTInstruction.FgColor)inst).toRGB();
			break;
		case PICTInstruction.BkColor.OPCODE:
			bgColor = ((PICTInstruction.BkColor)inst).toRGB();
			break;
		case PICTInstruction.TxRatio.OPCODE:
			txRatioX = ((PICTInstruction.TxRatio)inst).hnum;
			txRatioY = ((PICTInstruction.TxRatio)inst).vnum;
			txRatioX /= ((PICTInstruction.TxRatio)inst).hdenom;
			txRatioY /= ((PICTInstruction.TxRatio)inst).vdenom;
			break;
		case PICTInstruction.BkPixPat.OPCODE:
			PICTInstruction.BkPixPat bkpp = (PICTInstruction.BkPixPat)inst;
			bgPat = bkpp.pat1Data;
			if (bkpp.pixMap != null) {
				BufferedImage bi = PICTUtilities.pixmapToImage(bkpp.pixMap, bkpp.colorTable, bkpp.pixData, bgColor, fgColor, true, false);
				bgPPat = new TexturePaint(bi, new Rectangle(0, 0, bi.getWidth(), bi.getHeight()));
			} else if (bkpp.rgb != null) {
				bgPPat = bkpp.rgb.toColor();
			} else {
				bgPPat = null;
			}
			break;
		case PICTInstruction.PnPixPat.OPCODE:
			PICTInstruction.PnPixPat pnpp = (PICTInstruction.PnPixPat)inst;
			penPat = pnpp.pat1Data;
			if (pnpp.pixMap != null) {
				BufferedImage bi = PICTUtilities.pixmapToImage(pnpp.pixMap, pnpp.colorTable, pnpp.pixData, bgColor, fgColor, true, false);
				penPPat = new TexturePaint(bi, new Rectangle(0, 0, bi.getWidth(), bi.getHeight()));
			} else if (pnpp.rgb != null) {
				penPPat = pnpp.rgb.toColor();
			} else {
				penPPat = null;
			}
			break;
		case PICTInstruction.FillPixPat.OPCODE:
			PICTInstruction.FillPixPat fillpp = (PICTInstruction.FillPixPat)inst;
			fillPat = fillpp.pat1Data;
			if (fillpp.pixMap != null) {
				BufferedImage bi = PICTUtilities.pixmapToImage(fillpp.pixMap, fillpp.colorTable, fillpp.pixData, bgColor, fgColor, true, false);
				fillPPat = new TexturePaint(bi, new Rectangle(0, 0, bi.getWidth(), bi.getHeight()));
			} else if (fillpp.rgb != null) {
				fillPPat = fillpp.rgb.toColor();
			} else {
				fillPPat = null;
			}
			break;
		case PICTInstruction.PnLocHFrac.OPCODE:
			penHFrac = ((PICTInstruction.PnLocHFrac)inst).hfrac;
			break;
		case PICTInstruction.ChExtra.OPCODE:
			chExtra = ((PICTInstruction.ChExtra)inst).chExtra;
			break;
		case PICTInstruction.RGBFgCol.OPCODE:
			fgColor = ((PICTInstruction.RGBFgCol)inst).color.toRGB();
			break;
		case PICTInstruction.RGBBkCol.OPCODE:
			bgColor = ((PICTInstruction.RGBBkCol)inst).color.toRGB();
			break;
		case PICTInstruction.HiliteMode.OPCODE:
			hiliteMode = true;
			break;
		case PICTInstruction.HiliteColor.OPCODE:
			hiliteColor = ((PICTInstruction.HiliteColor)inst).color.toRGB();
			break;
		case PICTInstruction.DefHilite.OPCODE:
			hiliteColor = 0xFFCCCCFF;
			break;
		case PICTInstruction.OpColor.OPCODE:
			opColor = ((PICTInstruction.OpColor)inst).color.toRGB();
			break;
		case PICTInstruction.Line.OPCODE:
			if (graphics != null) {
				setPenPaint(graphics, false);
				Point pnLoc = ((PICTInstruction.Line)inst).pnLoc;
				Point newPt = ((PICTInstruction.Line)inst).newPt;
				graphics.drawLine(pnLoc.x, pnLoc.y, newPt.x, newPt.y);
			}
			penX = ((PICTInstruction.Line)inst).newPt.x;
			penY = ((PICTInstruction.Line)inst).newPt.y;
			break;
		case PICTInstruction.LineFrom.OPCODE:
			if (graphics != null) {
				setPenPaint(graphics, false);
				Point newPt = ((PICTInstruction.LineFrom)inst).newPt;
				graphics.drawLine(penX, penY, newPt.x, newPt.y);
			}
			penX = ((PICTInstruction.LineFrom)inst).newPt.x;
			penY = ((PICTInstruction.LineFrom)inst).newPt.y;
			break;
		case PICTInstruction.ShortLine.OPCODE:
			if (graphics != null) {
				setPenPaint(graphics, false);
				Point pnLoc = ((PICTInstruction.ShortLine)inst).pnLoc;
				int dx = ((PICTInstruction.ShortLine)inst).dh;
				int dy = ((PICTInstruction.ShortLine)inst).dv;
				graphics.drawLine(pnLoc.x, pnLoc.y, pnLoc.x+dx, pnLoc.y+dy);
			}
			penX = ((PICTInstruction.ShortLine)inst).pnLoc.x + ((PICTInstruction.ShortLine)inst).dh;
			penY = ((PICTInstruction.ShortLine)inst).pnLoc.y + ((PICTInstruction.ShortLine)inst).dv;
			break;
		case PICTInstruction.ShortLineFrom.OPCODE:
			if (graphics != null) {
				setPenPaint(graphics, false);
				int dx = ((PICTInstruction.ShortLineFrom)inst).dh;
				int dy = ((PICTInstruction.ShortLineFrom)inst).dv;
				graphics.drawLine(penX, penY, penX+dx, penY+dy);
			}
			penX += ((PICTInstruction.ShortLineFrom)inst).dh;
			penY += ((PICTInstruction.ShortLineFrom)inst).dv;
			break;
		case PICTInstruction.LongText.OPCODE:
			if (graphics != null) {
				setPenPaint(graphics, false);
				graphics.drawString(
						((PICTInstruction.LongText)inst).text,
						((PICTInstruction.LongText)inst).txLoc.x,
						((PICTInstruction.LongText)inst).txLoc.y
				);
			}
			penX = ((PICTInstruction.LongText)inst).txLoc.x;
			penY = ((PICTInstruction.LongText)inst).txLoc.y;
			break;
		case PICTInstruction.DHText.OPCODE:
			if (graphics != null) {
				setPenPaint(graphics, false);
				graphics.drawString(
						((PICTInstruction.DHText)inst).text,
						penX + ((PICTInstruction.DHText)inst).dh,
						penY
				);
			}
			penX += ((PICTInstruction.DHText)inst).dh;
			break;
		case PICTInstruction.DVText.OPCODE:
			if (graphics != null) {
				setPenPaint(graphics, false);
				graphics.drawString(
						((PICTInstruction.DVText)inst).text,
						penX,
						penY + ((PICTInstruction.DVText)inst).dv
				);
			}
			penY += ((PICTInstruction.DVText)inst).dv;
			break;
		case PICTInstruction.DHDVText.OPCODE:
			if (graphics != null) {
				setPenPaint(graphics, false);
				graphics.drawString(
						((PICTInstruction.DHDVText)inst).text,
						penX + ((PICTInstruction.DHDVText)inst).dh,
						penY + ((PICTInstruction.DHDVText)inst).dv
				);
			}
			penX += ((PICTInstruction.DHDVText)inst).dh;
			penY += ((PICTInstruction.DHDVText)inst).dv;
			break;
		case PICTInstruction.FontName.OPCODE:
			fontName = ((PICTInstruction.FontName)inst).fontName;
			break;
		case PICTInstruction.LineJustify.OPCODE:
			chSpacing = ((PICTInstruction.LineJustify)inst).spacing;
			jsSpace = ((PICTInstruction.LineJustify)inst).spExtra;
			break;
		case PICTInstruction.GlyphState.OPCODE:
			outlinePreferred = ((PICTInstruction.GlyphState)inst).outlinePreferred;
			preserveGlyph = ((PICTInstruction.GlyphState)inst).preserveGlyph;
			fractionalWidths = ((PICTInstruction.GlyphState)inst).fractionalWidths;
			scalingDisabled = ((PICTInstruction.GlyphState)inst).scalingDisabled;
			break;
		case PICTInstruction.FrameRect.OPCODE:
			lastRect = ((PICTInstruction.FrameRect)inst).rect;
		case PICTInstruction.FrameSameRect.OPCODE:
			if (graphics != null) {
				setPenPaint(graphics, false);
				graphics.drawRect(lastRect.left, lastRect.top, lastRect.right-lastRect.left, lastRect.bottom-lastRect.top);
			}
			break;
		case PICTInstruction.PaintRect.OPCODE:
			lastRect = ((PICTInstruction.PaintRect)inst).rect;
		case PICTInstruction.PaintSameRect.OPCODE:
			if (graphics != null) {
				setPenPaint(graphics, true);
				graphics.fillRect(lastRect.left, lastRect.top, lastRect.right-lastRect.left, lastRect.bottom-lastRect.top);
			}
			break;
		case PICTInstruction.EraseRect.OPCODE:
			lastRect = ((PICTInstruction.EraseRect)inst).rect;
		case PICTInstruction.EraseSameRect.OPCODE:
			if (graphics != null) {
				setBackgroundPaint(graphics, true);
				graphics.fillRect(lastRect.left, lastRect.top, lastRect.right-lastRect.left, lastRect.bottom-lastRect.top);
			}
			break;
		case PICTInstruction.InvertRect.OPCODE:
			lastRect = ((PICTInstruction.InvertRect)inst).rect;
		case PICTInstruction.InvertSameRect.OPCODE:
			if (graphics != null) {
				setInvertPaint(graphics, true);
				graphics.fillRect(lastRect.left, lastRect.top, lastRect.right-lastRect.left, lastRect.bottom-lastRect.top);
			}
			break;
		case PICTInstruction.FillRect.OPCODE:
			lastRect = ((PICTInstruction.FillRect)inst).rect;
		case PICTInstruction.FillSameRect.OPCODE:
			if (graphics != null) {
				setFillPaint(graphics, true);
				graphics.fillRect(lastRect.left, lastRect.top, lastRect.right-lastRect.left, lastRect.bottom-lastRect.top);
			}
			break;
		case PICTInstruction.FrameRRect.OPCODE:
			lastRect = ((PICTInstruction.FrameRRect)inst).rect;
		case PICTInstruction.FrameSameRRect.OPCODE:
			if (graphics != null) {
				setPenPaint(graphics, false);
				graphics.drawRoundRect(lastRect.left, lastRect.top, lastRect.right-lastRect.left, lastRect.bottom-lastRect.top, cornerRadiusW, cornerRadiusH);
			}
			break;
		case PICTInstruction.PaintRRect.OPCODE:
			lastRect = ((PICTInstruction.PaintRRect)inst).rect;
		case PICTInstruction.PaintSameRRect.OPCODE:
			if (graphics != null) {
				setPenPaint(graphics, true);
				graphics.fillRoundRect(lastRect.left, lastRect.top, lastRect.right-lastRect.left, lastRect.bottom-lastRect.top, cornerRadiusW, cornerRadiusH);
			}
			break;
		case PICTInstruction.EraseRRect.OPCODE:
			lastRect = ((PICTInstruction.EraseRRect)inst).rect;
		case PICTInstruction.EraseSameRRect.OPCODE:
			if (graphics != null) {
				setBackgroundPaint(graphics, true);
				graphics.fillRoundRect(lastRect.left, lastRect.top, lastRect.right-lastRect.left, lastRect.bottom-lastRect.top, cornerRadiusW, cornerRadiusH);
			}
			break;
		case PICTInstruction.InvertRRect.OPCODE:
			lastRect = ((PICTInstruction.InvertRRect)inst).rect;
		case PICTInstruction.InvertSameRRect.OPCODE:
			if (graphics != null) {
				setInvertPaint(graphics, true);
				graphics.fillRoundRect(lastRect.left, lastRect.top, lastRect.right-lastRect.left, lastRect.bottom-lastRect.top, cornerRadiusW, cornerRadiusH);
			}
			break;
		case PICTInstruction.FillRRect.OPCODE:
			lastRect = ((PICTInstruction.FillRRect)inst).rect;
		case PICTInstruction.FillSameRRect.OPCODE:
			if (graphics != null) {
				setFillPaint(graphics, true);
				graphics.fillRoundRect(lastRect.left, lastRect.top, lastRect.right-lastRect.left, lastRect.bottom-lastRect.top, cornerRadiusW, cornerRadiusH);
			}
			break;
		case PICTInstruction.FrameOval.OPCODE:
			lastRect = ((PICTInstruction.FrameOval)inst).rect;
		case PICTInstruction.FrameSameOval.OPCODE:
			if (graphics != null) {
				setPenPaint(graphics, false);
				graphics.drawOval(lastRect.left, lastRect.top, lastRect.right-lastRect.left, lastRect.bottom-lastRect.top);
			}
			break;
		case PICTInstruction.PaintOval.OPCODE:
			lastRect = ((PICTInstruction.PaintOval)inst).rect;
		case PICTInstruction.PaintSameOval.OPCODE:
			if (graphics != null) {
				setPenPaint(graphics, true);
				graphics.fillOval(lastRect.left, lastRect.top, lastRect.right-lastRect.left, lastRect.bottom-lastRect.top);
			}
			break;
		case PICTInstruction.EraseOval.OPCODE:
			lastRect = ((PICTInstruction.EraseOval)inst).rect;
		case PICTInstruction.EraseSameOval.OPCODE:
			if (graphics != null) {
				setBackgroundPaint(graphics, true);
				graphics.fillOval(lastRect.left, lastRect.top, lastRect.right-lastRect.left, lastRect.bottom-lastRect.top);
			}
			break;
		case PICTInstruction.InvertOval.OPCODE:
			lastRect = ((PICTInstruction.InvertOval)inst).rect;
		case PICTInstruction.InvertSameOval.OPCODE:
			if (graphics != null) {
				setInvertPaint(graphics, true);
				graphics.fillOval(lastRect.left, lastRect.top, lastRect.right-lastRect.left, lastRect.bottom-lastRect.top);
			}
			break;
		case PICTInstruction.FillOval.OPCODE:
			lastRect = ((PICTInstruction.FillOval)inst).rect;
		case PICTInstruction.FillSameOval.OPCODE:
			if (graphics != null) {
				setFillPaint(graphics, true);
				graphics.fillOval(lastRect.left, lastRect.top, lastRect.right-lastRect.left, lastRect.bottom-lastRect.top);
			}
			break;
		case PICTInstruction.FrameArc.OPCODE:
			lastRect = ((PICTInstruction.FrameArc)inst).rect;
		case PICTInstruction.FrameSameArc.OPCODE:
			if (graphics != null) {
				setPenPaint(graphics, false);
				int startAngle = (inst instanceof PICTInstruction.FrameSameArc)
						? ((PICTInstruction.FrameSameArc)inst).startAngle
						: ((PICTInstruction.FrameArc)inst).startAngle;
				int arcAngle = (inst instanceof PICTInstruction.FrameSameArc)
						? ((PICTInstruction.FrameSameArc)inst).arcAngle
						: ((PICTInstruction.FrameArc)inst).arcAngle;
				graphics.drawArc(lastRect.left, lastRect.top, lastRect.right-lastRect.left, lastRect.bottom-lastRect.top, startAngle, arcAngle);
			}
			break;
		case PICTInstruction.PaintArc.OPCODE:
			lastRect = ((PICTInstruction.PaintArc)inst).rect;
		case PICTInstruction.PaintSameArc.OPCODE:
			if (graphics != null) {
				setPenPaint(graphics, true);
				int startAngle = (inst instanceof PICTInstruction.FrameSameArc)
						? ((PICTInstruction.FrameSameArc)inst).startAngle
						: ((PICTInstruction.FrameArc)inst).startAngle;
				int arcAngle = (inst instanceof PICTInstruction.FrameSameArc)
						? ((PICTInstruction.FrameSameArc)inst).arcAngle
						: ((PICTInstruction.FrameArc)inst).arcAngle;
				graphics.fillArc(lastRect.left, lastRect.top, lastRect.right-lastRect.left, lastRect.bottom-lastRect.top, startAngle, arcAngle);
			}
			break;
		case PICTInstruction.EraseArc.OPCODE:
			lastRect = ((PICTInstruction.EraseArc)inst).rect;
		case PICTInstruction.EraseSameArc.OPCODE:
			if (graphics != null) {
				setBackgroundPaint(graphics, true);
				int startAngle = (inst instanceof PICTInstruction.FrameSameArc)
						? ((PICTInstruction.FrameSameArc)inst).startAngle
						: ((PICTInstruction.FrameArc)inst).startAngle;
				int arcAngle = (inst instanceof PICTInstruction.FrameSameArc)
						? ((PICTInstruction.FrameSameArc)inst).arcAngle
						: ((PICTInstruction.FrameArc)inst).arcAngle;
				graphics.fillArc(lastRect.left, lastRect.top, lastRect.right-lastRect.left, lastRect.bottom-lastRect.top, startAngle, arcAngle);
			}
			break;
		case PICTInstruction.InvertArc.OPCODE:
			lastRect = ((PICTInstruction.InvertArc)inst).rect;
		case PICTInstruction.InvertSameArc.OPCODE:
			if (graphics != null) {
				setInvertPaint(graphics, true);
				int startAngle = (inst instanceof PICTInstruction.FrameSameArc)
						? ((PICTInstruction.FrameSameArc)inst).startAngle
						: ((PICTInstruction.FrameArc)inst).startAngle;
				int arcAngle = (inst instanceof PICTInstruction.FrameSameArc)
						? ((PICTInstruction.FrameSameArc)inst).arcAngle
						: ((PICTInstruction.FrameArc)inst).arcAngle;
				graphics.fillArc(lastRect.left, lastRect.top, lastRect.right-lastRect.left, lastRect.bottom-lastRect.top, startAngle, arcAngle);
			}
			break;
		case PICTInstruction.FillArc.OPCODE:
			lastRect = ((PICTInstruction.FillArc)inst).rect;
		case PICTInstruction.FillSameArc.OPCODE:
			if (graphics != null) {
				setFillPaint(graphics, true);
				int startAngle = (inst instanceof PICTInstruction.FrameSameArc)
						? ((PICTInstruction.FrameSameArc)inst).startAngle
						: ((PICTInstruction.FrameArc)inst).startAngle;
				int arcAngle = (inst instanceof PICTInstruction.FrameSameArc)
						? ((PICTInstruction.FrameSameArc)inst).arcAngle
						: ((PICTInstruction.FrameArc)inst).arcAngle;
				graphics.fillArc(lastRect.left, lastRect.top, lastRect.right-lastRect.left, lastRect.bottom-lastRect.top, startAngle, arcAngle);
			}
			break;
		case PICTInstruction.FramePoly.OPCODE:
			lastPoly = ((PICTInstruction.FramePoly)inst).poly;
		case PICTInstruction.FrameSamePoly.OPCODE:
			if (graphics != null) {
				setPenPaint(graphics, false);
				graphics.drawPolygon(lastPoly.toPolygon());
			}
			break;
		case PICTInstruction.PaintPoly.OPCODE:
			lastPoly = ((PICTInstruction.PaintPoly)inst).poly;
		case PICTInstruction.PaintSamePoly.OPCODE:
			if (graphics != null) {
				setPenPaint(graphics, true);
				graphics.fillPolygon(lastPoly.toPolygon());
			}
			break;
		case PICTInstruction.ErasePoly.OPCODE:
			lastPoly = ((PICTInstruction.ErasePoly)inst).poly;
		case PICTInstruction.EraseSamePoly.OPCODE:
			if (graphics != null) {
				setBackgroundPaint(graphics, true);
				graphics.fillPolygon(lastPoly.toPolygon());
			}
			break;
		case PICTInstruction.InvertPoly.OPCODE:
			lastPoly = ((PICTInstruction.InvertPoly)inst).poly;
		case PICTInstruction.InvertSamePoly.OPCODE:
			if (graphics != null) {
				setInvertPaint(graphics, true);
				graphics.fillPolygon(lastPoly.toPolygon());
			}
			break;
		case PICTInstruction.FillPoly.OPCODE:
			lastPoly = ((PICTInstruction.FillPoly)inst).poly;
		case PICTInstruction.FillSamePoly.OPCODE:
			if (graphics != null) {
				setFillPaint(graphics, true);
				graphics.fillPolygon(lastPoly.toPolygon());
			}
			break;
		case PICTInstruction.FrameRgn.OPCODE:
			lastRgn = ((PICTInstruction.FrameRgn)inst).rgn;
		case PICTInstruction.FrameSameRgn.OPCODE:
			if (graphics != null) {
				setPenPaint(graphics, false);
				graphics.draw(lastRgn.toArea());
			}
			break;
		case PICTInstruction.PaintRgn.OPCODE:
			lastRgn = ((PICTInstruction.PaintRgn)inst).rgn;
		case PICTInstruction.PaintSameRgn.OPCODE:
			if (graphics != null) {
				setPenPaint(graphics, true);
				graphics.fill(lastRgn.toArea());
			}
			break;
		case PICTInstruction.EraseRgn.OPCODE:
			lastRgn = ((PICTInstruction.EraseRgn)inst).rgn;
		case PICTInstruction.EraseSameRgn.OPCODE:
			if (graphics != null) {
				setBackgroundPaint(graphics, true);
				graphics.fill(lastRgn.toArea());
			}
			break;
		case PICTInstruction.InvertRgn.OPCODE:
			lastRgn = ((PICTInstruction.InvertRgn)inst).rgn;
		case PICTInstruction.InvertSameRgn.OPCODE:
			if (graphics != null) {
				setInvertPaint(graphics, true);
				graphics.fill(lastRgn.toArea());
			}
			break;
		case PICTInstruction.FillRgn.OPCODE:
			lastRgn = ((PICTInstruction.FillRgn)inst).rgn;
		case PICTInstruction.FillSameRgn.OPCODE:
			if (graphics != null) {
				setFillPaint(graphics, true);
				graphics.fill(lastRgn.toArea());
			}
			break;
		case PICTInstruction.BitsRect.OPCODE:
			if (graphics != null) {
				setFillPaint(graphics, true);
				PICTInstruction.BitsRect binst = (PICTInstruction.BitsRect)inst;
				int bx = binst.pixMap.bounds.left;
				int by = binst.pixMap.bounds.top;
				BufferedImage bimg = PICTUtilities.pixmapToImage(binst.pixMap, binst.colorTable, binst.pixData, bgColor, fgColor, false, true);
				graphics.drawImage(
						bimg,
						binst.dstRect.left, binst.dstRect.top,
						binst.dstRect.right, binst.dstRect.bottom,
						binst.srcRect.left-bx, binst.srcRect.top-by,
						binst.srcRect.right-bx, binst.srcRect.bottom-by,
						null
				);
			}
			break;
		case PICTInstruction.BitsRgn.OPCODE:
			if (graphics != null) {
				setFillPaint(graphics, true);
				PICTInstruction.BitsRgn binst = (PICTInstruction.BitsRgn)inst;
				graphics.clip(binst.maskRgn.toArea());
				int bx = binst.pixMap.bounds.left;
				int by = binst.pixMap.bounds.top;
				BufferedImage bimg = PICTUtilities.pixmapToImage(binst.pixMap, binst.colorTable, binst.pixData, bgColor, fgColor, false, true);
				graphics.drawImage(
						bimg,
						binst.dstRect.left, binst.dstRect.top,
						binst.dstRect.right, binst.dstRect.bottom,
						binst.srcRect.left-bx, binst.srcRect.top-by,
						binst.srcRect.right-bx, binst.srcRect.bottom-by,
						null
				);
			}
			break;
		case PICTInstruction.PackBitsRect.OPCODE:
			if (graphics != null) {
				setFillPaint(graphics, true);
				PICTInstruction.PackBitsRect binst = (PICTInstruction.PackBitsRect)inst;
				int bx = binst.pixMap.bounds.left;
				int by = binst.pixMap.bounds.top;
				BufferedImage bimg = PICTUtilities.pixmapToImage(binst.pixMap, binst.colorTable, binst.pixData, bgColor, fgColor, false, true);
				graphics.drawImage(
						bimg,
						binst.dstRect.left, binst.dstRect.top,
						binst.dstRect.right, binst.dstRect.bottom,
						binst.srcRect.left-bx, binst.srcRect.top-by,
						binst.srcRect.right-bx, binst.srcRect.bottom-by,
						null
				);
			}
			break;
		case PICTInstruction.PackBitsRgn.OPCODE:
			if (graphics != null) {
				setFillPaint(graphics, true);
				PICTInstruction.PackBitsRgn binst = (PICTInstruction.PackBitsRgn)inst;
				graphics.clip(binst.maskRgn.toArea());
				int bx = binst.pixMap.bounds.left;
				int by = binst.pixMap.bounds.top;
				BufferedImage bimg = PICTUtilities.pixmapToImage(binst.pixMap, binst.colorTable, binst.pixData, bgColor, fgColor, false, true);
				graphics.drawImage(
						bimg,
						binst.dstRect.left, binst.dstRect.top,
						binst.dstRect.right, binst.dstRect.bottom,
						binst.srcRect.left-bx, binst.srcRect.top-by,
						binst.srcRect.right-bx, binst.srcRect.bottom-by,
						null
				);
			}
			break;
		case PICTInstruction.DirectBitsRect.OPCODE:
			if (graphics != null) {
				setFillPaint(graphics, true);
				PICTInstruction.DirectBitsRect binst = (PICTInstruction.DirectBitsRect)inst;
				int bx = binst.pixMap.bounds.left;
				int by = binst.pixMap.bounds.top;
				BufferedImage bimg = PICTUtilities.pixmapToImage(binst.pixMap, null, binst.pixData, bgColor, fgColor, false, true);
				graphics.drawImage(
						bimg,
						binst.dstRect.left, binst.dstRect.top,
						binst.dstRect.right, binst.dstRect.bottom,
						binst.srcRect.left-bx, binst.srcRect.top-by,
						binst.srcRect.right-bx, binst.srcRect.bottom-by,
						null
				);
			}
			break;
		case PICTInstruction.DirectBitsRgn.OPCODE:
			if (graphics != null) {
				setFillPaint(graphics, true);
				PICTInstruction.DirectBitsRgn binst = (PICTInstruction.DirectBitsRgn)inst;
				graphics.clip(binst.maskRgn.toArea());
				int bx = binst.pixMap.bounds.left;
				int by = binst.pixMap.bounds.top;
				BufferedImage bimg = PICTUtilities.pixmapToImage(binst.pixMap, null, binst.pixData, bgColor, fgColor, false, true);
				graphics.drawImage(
						bimg,
						binst.dstRect.left, binst.dstRect.top,
						binst.dstRect.right, binst.dstRect.bottom,
						binst.srcRect.left-bx, binst.srcRect.top-by,
						binst.srcRect.right-bx, binst.srcRect.bottom-by,
						null
				);
			}
			break;
		}
	}
}
