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

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.*;

public abstract class PICTInstruction {
	/* * * * * * * * * * * *
	 * BASE ABSTRACT CLASS *
	 * * * * * * * * * * * */
	
	public int opcode;
	
	public static final PICTInstruction read(DataInputStream in, int v) throws IOException {
		int opcode = (v < 2) ? in.readUnsignedByte() : in.readUnsignedShort();
		PICTInstruction op = null;
		switch (opcode) {
		case NOP.OPCODE: op = new NOP(); break;
		case Clip.OPCODE: op = new Clip(); break;
		case BkPat.OPCODE: op = new BkPat(); break;
		case TxFont.OPCODE: op = new TxFont(); break;
		case TxFace.OPCODE: op = new TxFace(); break;
		case TxMode.OPCODE: op = new TxMode(); break;
		case SpExtra.OPCODE: op = new SpExtra(); break;
		case PnSize.OPCODE: op = new PnSize(); break;
		case PnMode.OPCODE: op = new PnMode(); break;
		case PnPat.OPCODE: op = new PnPat(); break;
		case FillPat.OPCODE: op = new FillPat(); break;
		case OvSize.OPCODE: op = new OvSize(); break;
		case Origin.OPCODE: op = new Origin(); break;
		case TxSize.OPCODE: op = new TxSize(); break;
		case FgColor.OPCODE: op = new FgColor(); break;
		case BkColor.OPCODE: op = new BkColor(); break;
		case TxRatio.OPCODE: op = new TxRatio(); break;
		case VersionOp.OPCODE: op = new VersionOp(); break;
		case BkPixPat.OPCODE: op = new BkPixPat(); break;
		case PnPixPat.OPCODE: op = new PnPixPat(); break;
		case FillPixPat.OPCODE: op = new FillPixPat(); break;
		case PnLocHFrac.OPCODE: op = new PnLocHFrac(); break;
		case ChExtra.OPCODE: op = new ChExtra(); break;
		case Reserved0017.OPCODE: op = new Reserved0017(); break;
		case Reserved0018.OPCODE: op = new Reserved0018(); break;
		case Reserved0019.OPCODE: op = new Reserved0019(); break;
		case RGBFgCol.OPCODE: op = new RGBFgCol(); break;
		case RGBBkCol.OPCODE: op = new RGBBkCol(); break;
		case HiliteMode.OPCODE: op = new HiliteMode(); break;
		case HiliteColor.OPCODE: op = new HiliteColor(); break;
		case DefHilite.OPCODE: op = new DefHilite(); break;
		case OpColor.OPCODE: op = new OpColor(); break;
		case Line.OPCODE: op = new Line(); break;
		case LineFrom.OPCODE: op = new LineFrom(); break;
		case ShortLine.OPCODE: op = new ShortLine(); break;
		case ShortLineFrom.OPCODE: op = new ShortLineFrom(); break;
		case Reserved0024.OPCODE: op = new Reserved0024(); break;
		case Reserved0025.OPCODE: op = new Reserved0025(); break;
		case Reserved0026.OPCODE: op = new Reserved0026(); break;
		case Reserved0027.OPCODE: op = new Reserved0027(); break;
		case LongText.OPCODE: op = new LongText(); break;
		case DHText.OPCODE: op = new DHText(); break;
		case DVText.OPCODE: op = new DVText(); break;
		case DHDVText.OPCODE: op = new DHDVText(); break;
		case FontName.OPCODE: op = new FontName(); break;
		case LineJustify.OPCODE: op = new LineJustify(); break;
		case GlyphState.OPCODE: op = new GlyphState(); break;
		case Reserved002F.OPCODE: op = new Reserved002F(); break;
		case FrameRect.OPCODE: op = new FrameRect(); break;
		case PaintRect.OPCODE: op = new PaintRect(); break;
		case EraseRect.OPCODE: op = new EraseRect(); break;
		case InvertRect.OPCODE: op = new InvertRect(); break;
		case FillRect.OPCODE: op = new FillRect(); break;
		case Reserved0035.OPCODE: op = new Reserved0035(); break;
		case Reserved0036.OPCODE: op = new Reserved0036(); break;
		case Reserved0037.OPCODE: op = new Reserved0037(); break;
		case FrameSameRect.OPCODE: op = new FrameSameRect(); break;
		case PaintSameRect.OPCODE: op = new PaintSameRect(); break;
		case EraseSameRect.OPCODE: op = new EraseSameRect(); break;
		case InvertSameRect.OPCODE: op = new InvertSameRect(); break;
		case FillSameRect.OPCODE: op = new FillSameRect(); break;
		case Reserved003D.OPCODE: op = new Reserved003D(); break;
		case Reserved003E.OPCODE: op = new Reserved003E(); break;
		case Reserved003F.OPCODE: op = new Reserved003F(); break;
		case FrameRRect.OPCODE: op = new FrameRRect(); break;
		case PaintRRect.OPCODE: op = new PaintRRect(); break;
		case EraseRRect.OPCODE: op = new EraseRRect(); break;
		case InvertRRect.OPCODE: op = new InvertRRect(); break;
		case FillRRect.OPCODE: op = new FillRRect(); break;
		case Reserved0045.OPCODE: op = new Reserved0045(); break;
		case Reserved0046.OPCODE: op = new Reserved0046(); break;
		case Reserved0047.OPCODE: op = new Reserved0047(); break;
		case FrameSameRRect.OPCODE: op = new FrameSameRRect(); break;
		case PaintSameRRect.OPCODE: op = new PaintSameRRect(); break;
		case EraseSameRRect.OPCODE: op = new EraseSameRRect(); break;
		case InvertSameRRect.OPCODE: op = new InvertSameRRect(); break;
		case FillSameRRect.OPCODE: op = new FillSameRRect(); break;
		case Reserved004D.OPCODE: op = new Reserved004D(); break;
		case Reserved004E.OPCODE: op = new Reserved004E(); break;
		case Reserved004F.OPCODE: op = new Reserved004F(); break;
		case FrameOval.OPCODE: op = new FrameOval(); break;
		case PaintOval.OPCODE: op = new PaintOval(); break;
		case EraseOval.OPCODE: op = new EraseOval(); break;
		case InvertOval.OPCODE: op = new InvertOval(); break;
		case FillOval.OPCODE: op = new FillOval(); break;
		case Reserved0055.OPCODE: op = new Reserved0055(); break;
		case Reserved0056.OPCODE: op = new Reserved0056(); break;
		case Reserved0057.OPCODE: op = new Reserved0057(); break;
		case FrameSameOval.OPCODE: op = new FrameSameOval(); break;
		case PaintSameOval.OPCODE: op = new PaintSameOval(); break;
		case EraseSameOval.OPCODE: op = new EraseSameOval(); break;
		case InvertSameOval.OPCODE: op = new InvertSameOval(); break;
		case FillSameOval.OPCODE: op = new FillSameOval(); break;
		case Reserved005D.OPCODE: op = new Reserved005D(); break;
		case Reserved005E.OPCODE: op = new Reserved005E(); break;
		case Reserved005F.OPCODE: op = new Reserved005F(); break;
		case FrameArc.OPCODE: op = new FrameArc(); break;
		case PaintArc.OPCODE: op = new PaintArc(); break;
		case EraseArc.OPCODE: op = new EraseArc(); break;
		case InvertArc.OPCODE: op = new InvertArc(); break;
		case FillArc.OPCODE: op = new FillArc(); break;
		case Reserved0065.OPCODE: op = new Reserved0065(); break;
		case Reserved0066.OPCODE: op = new Reserved0066(); break;
		case Reserved0067.OPCODE: op = new Reserved0067(); break;
		case FrameSameArc.OPCODE: op = new FrameSameArc(); break;
		case PaintSameArc.OPCODE: op = new PaintSameArc(); break;
		case EraseSameArc.OPCODE: op = new EraseSameArc(); break;
		case InvertSameArc.OPCODE: op = new InvertSameArc(); break;
		case FillSameArc.OPCODE: op = new FillSameArc(); break;
		case Reserved006D.OPCODE: op = new Reserved006D(); break;
		case Reserved006E.OPCODE: op = new Reserved006E(); break;
		case Reserved006F.OPCODE: op = new Reserved006F(); break;
		case FramePoly.OPCODE: op = new FramePoly(); break;
		case PaintPoly.OPCODE: op = new PaintPoly(); break;
		case ErasePoly.OPCODE: op = new ErasePoly(); break;
		case InvertPoly.OPCODE: op = new InvertPoly(); break;
		case FillPoly.OPCODE: op = new FillPoly(); break;
		case Reserved0075.OPCODE: op = new Reserved0075(); break;
		case Reserved0076.OPCODE: op = new Reserved0076(); break;
		case Reserved0077.OPCODE: op = new Reserved0077(); break;
		case FrameSamePoly.OPCODE: op = new FrameSamePoly(); break;
		case PaintSamePoly.OPCODE: op = new PaintSamePoly(); break;
		case EraseSamePoly.OPCODE: op = new EraseSamePoly(); break;
		case InvertSamePoly.OPCODE: op = new InvertSamePoly(); break;
		case FillSamePoly.OPCODE: op = new FillSamePoly(); break;
		case Reserved007D.OPCODE: op = new Reserved007D(); break;
		case Reserved007E.OPCODE: op = new Reserved007E(); break;
		case Reserved007F.OPCODE: op = new Reserved007F(); break;
		case FrameRgn.OPCODE: op = new FrameRgn(); break;
		case PaintRgn.OPCODE: op = new PaintRgn(); break;
		case EraseRgn.OPCODE: op = new EraseRgn(); break;
		case InvertRgn.OPCODE: op = new InvertRgn(); break;
		case FillRgn.OPCODE: op = new FillRgn(); break;
		case Reserved0085.OPCODE: op = new Reserved0085(); break;
		case Reserved0086.OPCODE: op = new Reserved0086(); break;
		case Reserved0087.OPCODE: op = new Reserved0087(); break;
		case FrameSameRgn.OPCODE: op = new FrameSameRgn(); break;
		case PaintSameRgn.OPCODE: op = new PaintSameRgn(); break;
		case EraseSameRgn.OPCODE: op = new EraseSameRgn(); break;
		case InvertSameRgn.OPCODE: op = new InvertSameRgn(); break;
		case FillSameRgn.OPCODE: op = new FillSameRgn(); break;
		case Reserved008D.OPCODE: op = new Reserved008D(); break;
		case Reserved008E.OPCODE: op = new Reserved008E(); break;
		case Reserved008F.OPCODE: op = new Reserved008F(); break;
		case BitsRect.OPCODE: op = new BitsRect(); break;
		case BitsRgn.OPCODE: op = new BitsRgn(); break;
		case Reserved0092.OPCODE: op = new Reserved0092(); break;
		case Reserved0093.OPCODE: op = new Reserved0093(); break;
		case Reserved0094.OPCODE: op = new Reserved0094(); break;
		case Reserved0095.OPCODE: op = new Reserved0095(); break;
		case Reserved0096.OPCODE: op = new Reserved0096(); break;
		case Reserved0097.OPCODE: op = new Reserved0097(); break;
		case PackBitsRect.OPCODE: op = new PackBitsRect(); break;
		case PackBitsRgn.OPCODE: op = new PackBitsRgn(); break;
		case DirectBitsRect.OPCODE: op = new DirectBitsRect(); break;
		case DirectBitsRgn.OPCODE: op = new DirectBitsRgn(); break;
		case Reserved009C.OPCODE: op = new Reserved009C(); break;
		case Reserved009D.OPCODE: op = new Reserved009D(); break;
		case Reserved009E.OPCODE: op = new Reserved009E(); break;
		case Reserved009F.OPCODE: op = new Reserved009F(); break;
		case ShortComment.OPCODE: op = new ShortComment(); break;
		case LongComment.OPCODE: op = new LongComment(); break;
		case OpEndPic.OPCODE: op = new OpEndPic(); break;
		case HeaderOp.OPCODE: op = new HeaderOp(); break;
		case CompressedQuickTime.OPCODE: op = new CompressedQuickTime(); break;
		case UncompressedQuickTime.OPCODE: op = new UncompressedQuickTime(); break;
		default:
			if (opcode >= 0x8100) op = new Reserved8100toFFFF();
			else if (opcode >= 0x8000) op = new Reserved8000to80FF();
			else if (opcode >= 0x0100) op = new Reserved0100to7FFF();
			else if (opcode >= 0x00D0) op = new Reserved00D0to00FF();
			else if (opcode >= 0x00B0) op = new Reserved00B0to00CF();
			else if (opcode >= 0x00A2) op = new Reserved00A2to00AF();
			else if (opcode >= 0x0020) op = new ShortDataInstruction() {}; // should not happen
			else op = new ImpliedInstruction() {}; // should not happen
			break;
		}
		op.opcode = opcode;
		op.readImpl(in, v >= 2);
		return op;
	}
	
	public final void write(DataOutputStream out, int v) throws IOException {
		if (v < 2) out.writeByte(opcode); else out.writeShort(opcode);
		writeImpl(out, v >= 2);
	}
	
	public final String toString() {
		return (this.getClass().getSimpleName() + " " + toStringImpl()).trim();
	}
	
	protected abstract void readImpl(DataInputStream in, boolean v2) throws IOException;
	protected abstract void writeImpl(DataOutputStream out, boolean v2) throws IOException;
	protected abstract String toStringImpl();
	
	/* * * * * * * * * * * * * * * * * * * * * * * * *
	 * ABSTRACT CLASSES FOR COMMON INSTRUCTION TYPES *
	 * * * * * * * * * * * * * * * * * * * * * * * * */
	
	public static abstract class ImpliedInstruction extends PICTInstruction {
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			// nothing
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			// nothing
		}
		protected String toStringImpl() {
			return "";
		}
	}
	
	public static abstract class RegionInstruction extends PICTInstruction {
		public Region rgn;
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			rgn = Region.read(in);
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			rgn.write(out);
		}
		protected String toStringImpl() {
			return rgn.toString();
		}
	}
	
	public static interface PatternConstants {
		public static final long BLACK = 0xFFFFFFFFFFFFFFFFL;
		public static final long DKGRAY = 0xDD77DD77DD77DD77L;
		public static final long GRAY = 0xAA55AA55AA55AA55L;
		public static final long LTGRAY = 0x8822882288228822L;
		public static final long WHITE = 0x0000000000000000L;
	}
	
	public static String patternToString(long pat) {
		String h = "0000000000000000" + Long.toHexString(pat).toUpperCase();
		return h.substring(h.length()-16);
	}
	
	public static abstract class PatternInstruction extends PICTInstruction implements PatternConstants {
		public long pat;
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			pat = in.readLong();
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			out.writeLong(pat);
		}
		protected String toStringImpl() {
			return patternToString(pat);
		}
	}
	
	public static interface ModeConstants {
		public static final int SRC_COPY = 0;
		public static final int SRC_OR = 1;
		public static final int SRC_XOR = 2;
		public static final int SRC_BIC = 3;
		public static final int NOT_SRC_COPY = 4;
		public static final int NOT_SRC_OR = 5;
		public static final int NOT_SRC_XOR = 6;
		public static final int NOT_SRC_BIC = 7;
		public static final int BLEND = 32;
		public static final int ADD_PIN = 33;
		public static final int ADD_OVER = 34;
		public static final int SUB_PIN = 35;
		public static final int TRANSPARENT = 36;
		public static final int ADD_MAX = 37;
		public static final int SUB_OVER = 38;
		public static final int ADD_MIN = 39;
		public static final int GRAYISH_TEXT_OR = 49;
		public static final int HILITE = 50;
		public static final int DITHER_COPY = 64;
	}
	
	public static String modeToString(int m) {
		String suffix = "";
		if (m >= ModeConstants.DITHER_COPY) {
			m -= ModeConstants.DITHER_COPY;
			suffix = " ditherCopy" + suffix;
		}
		if (m >= ModeConstants.HILITE) {
			m -= ModeConstants.HILITE;
			suffix = " hilite" + suffix;
		}
		switch (m) {
		case ModeConstants.SRC_COPY: return "srcCopy" + suffix;
		case ModeConstants.SRC_OR: return "srcOr" + suffix;
		case ModeConstants.SRC_XOR: return "srcXor" + suffix;
		case ModeConstants.SRC_BIC: return "srcBic" + suffix;
		case ModeConstants.NOT_SRC_COPY: return "notSrcCopy" + suffix;
		case ModeConstants.NOT_SRC_OR: return "notSrcOr" + suffix;
		case ModeConstants.NOT_SRC_XOR: return "notSrcXor" + suffix;
		case ModeConstants.NOT_SRC_BIC: return "notSrcBic" + suffix;
		case ModeConstants.BLEND: return "blend" + suffix;
		case ModeConstants.ADD_PIN: return "addPin" + suffix;
		case ModeConstants.ADD_OVER: return "addOver" + suffix;
		case ModeConstants.SUB_PIN: return "subPin" + suffix;
		case ModeConstants.TRANSPARENT: return "transparent" + suffix;
		case ModeConstants.ADD_MAX: return "addMax" + suffix;
		case ModeConstants.SUB_OVER: return "subOver" + suffix;
		case ModeConstants.ADD_MIN: return "addMin" + suffix;
		case ModeConstants.GRAYISH_TEXT_OR: return "grayishTextOr" + suffix;
		default: return m + suffix;
		}
	}
	
	public static abstract class ModeInstruction extends PICTInstruction implements ModeConstants {
		public int mode;
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			mode = in.readShort();
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			out.writeShort(mode);
		}
		protected String toStringImpl() {
			return modeToString(mode);
		}
	}
	
	public static interface ColorConstants {
		public static final int WHITE = 30;
		public static final int BLACK = 33;
		public static final int YELLOW = 69;
		public static final int MAGENTA = 137;
		public static final int RED = 205;
		public static final int CYAN = 273;
		public static final int GREEN = 341;
		public static final int BLUE = 409;
	}
	
	public static String colorToString(int color) {
		switch (color) {
		case ColorConstants.WHITE: return "white";
		case ColorConstants.BLACK: return "black";
		case ColorConstants.YELLOW: return "yellow";
		case ColorConstants.MAGENTA: return "magenta";
		case ColorConstants.RED: return "red";
		case ColorConstants.CYAN: return "cyan";
		case ColorConstants.GREEN: return "green";
		case ColorConstants.BLUE: return "blue";
		default: return Integer.toString(color);
		}
	}
	
	public static int colorToRGB(int color) {
		switch (color) {
		case 0: return 0xFFFFFFFF;
		case 1: return 0xFF000000;
		case ColorConstants.WHITE: return 0xFFFFFFFF;
		case ColorConstants.BLACK: return 0xFF000000;
		case ColorConstants.YELLOW: return 0xFFFBF205;
		case ColorConstants.MAGENTA: return 0xFFF10884;
		case ColorConstants.RED: return 0xFFDC0806;
		case ColorConstants.CYAN: return 0xFF02AAEA;
		case ColorConstants.GREEN: return 0xFF007F11;
		case ColorConstants.BLUE: return 0xFF0000D3;
		default: return 0xFF000000;
		}
	}
	
	public static abstract class ColorInstruction extends PICTInstruction implements ColorConstants {
		public int color;
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			color = in.readInt();
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			out.writeInt(color);
		}
		protected String toStringImpl() {
			return colorToString(color);
		}
		public int toRGB() {
			return colorToRGB(color);
		}
	}
	
	public static abstract class PixelPatternInstruction extends PICTInstruction {
		public static final int DITHER_PAT = 2;
		public static final int PIXEL_PAT = 1;
		public static final int BW_PAT = 0;
		public int patType;
		public long pat1Data;
		public RGBColor rgb;
		public PixMap pixMap;
		public ColorTable colorTable;
		public byte[] pixData;
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			patType = in.readShort();
			pat1Data = in.readLong();
			switch (patType) {
			case DITHER_PAT:
				rgb = RGBColor.read(in);
				pixMap = null;
				colorTable = null;
				pixData = null;
				break;
			case PIXEL_PAT:
				rgb = null;
				pixMap = PixMap.read(in, false);
				colorTable = pixMap.hasColorTable() ? ColorTable.read(in) : null;
				pixData = pixMap.readPixData(in, false);
				if (v2 && (pixData.length & 1) == 1) in.readByte();
				break;
			default:
				rgb = null;
				pixMap = null;
				colorTable = null;
				pixData = null;
				break;
			}
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			out.writeShort(patType);
			out.writeLong(pat1Data);
			switch (patType) {
			case DITHER_PAT:
				rgb.write(out);
				break;
			case PIXEL_PAT:
				pixMap.write(out, false);
				if (colorTable != null) colorTable.write(out);
				pixMap.writePixData(out, pixData);
				if (v2 && (pixData.length & 1) == 1) out.writeByte(0);
				break;
			}
		}
		protected String toStringImpl() {
			String s = (patType == DITHER_PAT) ? "ditherPat" : (patType == PIXEL_PAT) ? "pixelPat" : "bwPat";
			s += " " + patternToString(pat1Data);
			switch (patType) {
			case DITHER_PAT:
				s += " " + rgb.toString();
				break;
			case PIXEL_PAT:
				s += " " + pixMap.toString();
				if (colorTable != null) s += " " + colorTable.toString();
				break;
			}
			return s;
		}
	}
	
	public static abstract class RGBColorInstruction extends PICTInstruction {
		public RGBColor color;
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			color = RGBColor.read(in);
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			color.write(out);
		}
		protected String toStringImpl() {
			return color.toString();
		}
	}
	
	public static abstract class ShortDataInstruction extends PICTInstruction {
		public byte[] data;
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			data = new byte[in.readUnsignedShort()];
			in.readFully(data);
			if (v2 && (data.length & 1) == 1) in.readByte();
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			out.writeShort(data.length);
			out.write(data);
			if (v2 && (data.length & 1) == 1) out.writeByte(0);
		}
		protected String toStringImpl() {
			StringBuffer sb = new StringBuffer();
			if (data.length <= 64) {
				for (byte b : data) {
					String h = "00" + Integer.toHexString(b).toUpperCase();
					sb.append(h.substring(h.length()-2));
				}
			} else {
				sb.append("Data["+data.length+"]");
			}
			return sb.toString();
		}
	}
	
	public static abstract class RectangleInstruction extends PICTInstruction {
		public Rect rect;
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			rect = Rect.read(in);
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			rect.write(out);
		}
		protected String toStringImpl() {
			return rect.toString();
		}
	}
	
	public static abstract class ArcInstruction extends PICTInstruction {
		public Rect rect;
		public int startAngle;
		public int arcAngle;
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			rect = Rect.read(in);
			startAngle = in.readShort();
			arcAngle = in.readShort();
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			rect.write(out);
			out.writeShort(startAngle);
			out.writeShort(arcAngle);
		}
		protected String toStringImpl() {
			return rect.toString()+" "+startAngle+" "+arcAngle;
		}
	}
	
	public static abstract class SameArcInstruction extends PICTInstruction {
		public int startAngle;
		public int arcAngle;
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			startAngle = in.readShort();
			arcAngle = in.readShort();
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			out.writeShort(startAngle);
			out.writeShort(arcAngle);
		}
		protected String toStringImpl() {
			return startAngle+" "+arcAngle;
		}
	}
	
	public static abstract class PolygonInstruction extends PICTInstruction {
		public Polygon poly;
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			poly = Polygon.read(in);
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			poly.write(out);
		}
		protected String toStringImpl() {
			return poly.toString();
		}
	}
	
	public static abstract class CopyBitsRectInstruction extends PICTInstruction implements ModeConstants {
		public PixMap pixMap;
		public ColorTable colorTable;
		public Rect srcRect;
		public Rect dstRect;
		public int mode;
		public byte[] pixData;
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			pixMap = PixMap.read(in, false);
			colorTable = pixMap.hasColorTable() ? ColorTable.read(in) : null;
			srcRect = Rect.read(in);
			dstRect = Rect.read(in);
			mode = in.readShort();
			pixData = pixMap.readPixData(in, true);
			if (v2 && (pixData.length & 1) == 1) in.readByte();
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			pixMap.write(out, false);
			if (colorTable != null) colorTable.write(out);
			srcRect.write(out);
			dstRect.write(out);
			out.writeShort(mode);
			pixMap.writePixData(out, pixData);
			if (v2 && (pixData.length & 1) == 1) out.writeByte(0);
		}
		protected String toStringImpl() {
			StringBuffer sb = new StringBuffer();
			sb.append(pixMap.toString());
			if (colorTable != null) sb.append(" "+colorTable.toString());
			sb.append(" "+srcRect.toString());
			sb.append(" "+dstRect.toString());
			sb.append(" "+modeToString(mode));
			if (pixData.length <= 64) {
				sb.append(" ");
				for (byte b : pixData) {
					String h = "00" + Integer.toHexString(b).toUpperCase();
					sb.append(h.substring(h.length()-2));
				}
			} else {
				sb.append(" Data["+pixData.length+"]");
			}
			return sb.toString();
		}
	}
	
	public static abstract class CopyBitsRegionInstruction extends PICTInstruction implements ModeConstants {
		public PixMap pixMap;
		public ColorTable colorTable;
		public Rect srcRect;
		public Rect dstRect;
		public int mode;
		public Region maskRgn;
		public byte[] pixData;
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			pixMap = PixMap.read(in, false);
			colorTable = pixMap.hasColorTable() ? ColorTable.read(in) : null;
			srcRect = Rect.read(in);
			dstRect = Rect.read(in);
			mode = in.readShort();
			maskRgn = Region.read(in);
			pixData = pixMap.readPixData(in, true);
			if (v2 && (pixData.length & 1) == 1) in.readByte();
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			pixMap.write(out, false);
			if (colorTable != null) colorTable.write(out);
			srcRect.write(out);
			dstRect.write(out);
			out.writeShort(mode);
			maskRgn.write(out);
			pixMap.writePixData(out, pixData);
			if (v2 && (pixData.length & 1) == 1) out.writeByte(0);
		}
		protected String toStringImpl() {
			StringBuffer sb = new StringBuffer();
			sb.append(pixMap.toString());
			if (colorTable != null) sb.append(" "+colorTable.toString());
			sb.append(" "+srcRect.toString());
			sb.append(" "+dstRect.toString());
			sb.append(" "+modeToString(mode));
			sb.append(" "+maskRgn.toString());
			if (pixData.length <= 64) {
				sb.append(" ");
				for (byte b : pixData) {
					String h = "00" + Integer.toHexString(b).toUpperCase();
					sb.append(h.substring(h.length()-2));
				}
			} else {
				sb.append(" Data["+pixData.length+"]");
			}
			return sb.toString();
		}
	}
	
	public static abstract class DirectBitsRectInstruction extends PICTInstruction implements ModeConstants {
		public PixMap pixMap;
		public Rect srcRect;
		public Rect dstRect;
		public int mode;
		public byte[] pixData;
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			pixMap = PixMap.read(in, true);
			srcRect = Rect.read(in);
			dstRect = Rect.read(in);
			mode = in.readShort();
			pixData = pixMap.readPixData(in, true);
			if (v2 && (pixData.length & 1) == 1) in.readByte();
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			pixMap.write(out, true);
			srcRect.write(out);
			dstRect.write(out);
			out.writeShort(mode);
			pixMap.writePixData(out, pixData);
			if (v2 && (pixData.length & 1) == 1) out.writeByte(0);
		}
		protected String toStringImpl() {
			StringBuffer sb = new StringBuffer();
			sb.append(pixMap.toString());
			sb.append(" "+srcRect.toString());
			sb.append(" "+dstRect.toString());
			sb.append(" "+modeToString(mode));
			if (pixData.length <= 64) {
				sb.append(" ");
				for (byte b : pixData) {
					String h = "00" + Integer.toHexString(b).toUpperCase();
					sb.append(h.substring(h.length()-2));
				}
			} else {
				sb.append(" Data["+pixData.length+"]");
			}
			return sb.toString();
		}
	}
	
	public static abstract class DirectBitsRegionInstruction extends PICTInstruction implements ModeConstants {
		public PixMap pixMap;
		public Rect srcRect;
		public Rect dstRect;
		public int mode;
		public Region maskRgn;
		public byte[] pixData;
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			pixMap = PixMap.read(in, true);
			srcRect = Rect.read(in);
			dstRect = Rect.read(in);
			mode = in.readShort();
			maskRgn = Region.read(in);
			pixData = pixMap.readPixData(in, true);
			if (v2 && (pixData.length & 1) == 1) in.readByte();
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			pixMap.write(out, true);
			srcRect.write(out);
			dstRect.write(out);
			out.writeShort(mode);
			maskRgn.write(out);
			pixMap.writePixData(out, pixData);
			if (v2 && (pixData.length & 1) == 1) out.writeByte(0);
		}
		protected String toStringImpl() {
			StringBuffer sb = new StringBuffer();
			sb.append(pixMap.toString());
			sb.append(" "+srcRect.toString());
			sb.append(" "+dstRect.toString());
			sb.append(" "+modeToString(mode));
			sb.append(" "+maskRgn.toString());
			if (pixData.length <= 64) {
				sb.append(" ");
				for (byte b : pixData) {
					String h = "00" + Integer.toHexString(b).toUpperCase();
					sb.append(h.substring(h.length()-2));
				}
			} else {
				sb.append(" Data["+pixData.length+"]");
			}
			return sb.toString();
		}
	}
	
	public static interface CommentConstants {
		public static final int APPLICATION_COMMENT = 100;
		public static final int DRAWING_BEGIN = 130;
		public static final int DRAWING_END = 131;
		public static final int GROUP_BEGIN = 140;
		public static final int GROUP_END = 141;
		public static final int BITMAP_BEGIN = 142;
		public static final int BITMAP_END = 143;
		public static final int TEXT_BEGIN = 150;
		public static final int TEXT_END = 151;
		public static final int STRING_BEGIN = 152;
		public static final int STRING_END = 153;
		public static final int TEXT_CENTER = 154;
		public static final int LINE_LAYOUT_OFF = 155;
		public static final int LINE_LAYOUT_ON = 156;
		public static final int CLIENT_LINE_LAYOUT = 157;
		public static final int POLY_BEGIN = 160;
		public static final int POLY_END = 161;
		public static final int POLY_IGNORE = 163;
		public static final int POLY_SMOOTH = 164;
		public static final int POLY_CLOSE = 165;
		public static final int ARROW_BEGIN_START = 170;
		public static final int ARROW_BEGIN_END = 171;
		public static final int ARROW_BEGIN_BOTH = 172;
		public static final int ARROW_END = 173;
		public static final int DASHED_LINE = 180;
		public static final int DASHED_STOP = 181;
		public static final int SET_LINE_WIDTH = 182;
		public static final int POSTSCRIPT_BEGIN = 190;
		public static final int POSTSCRIPT_END = 191;
		public static final int POSTSCRIPT_HANDLE = 192;
		public static final int POSTSCRIPT_FILE = 193;
		public static final int TEXT_IS_POSTSCRIPT = 194;
		public static final int RESOURCE_PS = 195;
		public static final int PS_BEGIN_NO_SAVE = 196;
		public static final int SET_GRAY_LEVEL = 197;
		public static final int ROTATE_BEGIN = 200;
		public static final int ROTATE_END = 201;
		public static final int ROTATE_CENTER = 202;
		public static final int FORMS_PRINTING = 210;
		public static final int END_FORMS_PRINTING = 211;
		public static final int CM_BEGIN_PROFILE = 220;
		public static final int CM_END_PROFILE = 221;
		public static final int CM_ENABLE_MATCHING = 222;
		public static final int CM_DISABLE_MATHING = 223;
		public static final int LASSO = 12345;
		public static final int APPLICATION_SUPERPAINT = 0x53504E54;
		public static final int APPLICATION_POWERPAINT = 0x4B504E54;
	}
	
	public static String commentToString(int kind) {
		switch (kind) {
		case CommentConstants.APPLICATION_COMMENT: return "ApplicationComment";
		case CommentConstants.DRAWING_BEGIN: return "DrawingBegin";
		case CommentConstants.DRAWING_END: return "DrawingEnd";
		case CommentConstants.GROUP_BEGIN: return "GroupBegin";
		case CommentConstants.GROUP_END: return "GroupEnd";
		case CommentConstants.BITMAP_BEGIN: return "BitmapBegin";
		case CommentConstants.BITMAP_END: return "BitmapEnd";
		case CommentConstants.TEXT_BEGIN: return "TextBegin";
		case CommentConstants.TEXT_END: return "TextEnd";
		case CommentConstants.STRING_BEGIN: return "StringBegin";
		case CommentConstants.STRING_END: return "StringEnd";
		case CommentConstants.TEXT_CENTER: return "TextCenter";
		case CommentConstants.LINE_LAYOUT_OFF: return "LineLayoutOff";
		case CommentConstants.LINE_LAYOUT_ON: return "LineLayoutOn";
		case CommentConstants.CLIENT_LINE_LAYOUT: return "ClientLineLayout";
		case CommentConstants.POLY_BEGIN: return "PolyBegin";
		case CommentConstants.POLY_END: return "PolyEnd";
		case CommentConstants.POLY_IGNORE: return "PolyIgnore";
		case CommentConstants.POLY_SMOOTH: return "PolySmooth";
		case CommentConstants.POLY_CLOSE: return "PolyClose";
		case CommentConstants.ARROW_BEGIN_START: return "ArrowBeginStart";
		case CommentConstants.ARROW_BEGIN_END: return "ArrowBeginEnd";
		case CommentConstants.ARROW_BEGIN_BOTH: return "ArrowBeginBoth";
		case CommentConstants.ARROW_END: return "ArrowEnd";
		case CommentConstants.DASHED_LINE: return "DashedLine";
		case CommentConstants.DASHED_STOP: return "DashedStop";
		case CommentConstants.SET_LINE_WIDTH: return "SetLineWidth";
		case CommentConstants.POSTSCRIPT_BEGIN: return "PostScriptBegin";
		case CommentConstants.POSTSCRIPT_END: return "PostScriptEnd";
		case CommentConstants.POSTSCRIPT_HANDLE: return "PostScriptHandle";
		case CommentConstants.POSTSCRIPT_FILE: return "PostScriptFile";
		case CommentConstants.TEXT_IS_POSTSCRIPT: return "TextIsPostScript";
		case CommentConstants.RESOURCE_PS: return "ResourcePS";
		case CommentConstants.PS_BEGIN_NO_SAVE: return "PSBeginNoSave";
		case CommentConstants.SET_GRAY_LEVEL: return "SetGrayLevel";
		case CommentConstants.ROTATE_BEGIN: return "RotateBegin";
		case CommentConstants.ROTATE_END: return "RotateEnd";
		case CommentConstants.ROTATE_CENTER: return "RotateCenter";
		case CommentConstants.FORMS_PRINTING: return "FormsPrinting";
		case CommentConstants.END_FORMS_PRINTING: return "EndFormsPrinting";
		case CommentConstants.CM_BEGIN_PROFILE: return "CMBeginProfile";
		case CommentConstants.CM_END_PROFILE: return "CMEndProfile";
		case CommentConstants.CM_ENABLE_MATCHING: return "CMEnableMatching";
		case CommentConstants.CM_DISABLE_MATHING: return "CMDisableMatching";
		case CommentConstants.LASSO: return "Lasso";
		default: return Integer.toString(kind);
		}
	}
	
	public static String osTypeToString(int osType) {
		byte[] d = new byte[] {
				(byte)((osType >>> 24) & 0xFF),
				(byte)((osType >>> 16) & 0xFF),
				(byte)((osType >>> 8) & 0xFF),
				(byte)(osType & 0xFF)
		};
		return PICTUtilities.decodeString(d);
	}
	
	public static abstract class LongDataInstruction extends PICTInstruction {
		public byte[] data;
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			data = new byte[in.readInt()];
			in.readFully(data);
			if (v2 && (data.length & 1) == 1) in.readByte();
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			out.writeInt(data.length);
			out.write(data);
			if (v2 && (data.length & 1) == 1) out.writeByte(0);
		}
		protected String toStringImpl() {
			StringBuffer sb = new StringBuffer();
			if (data.length <= 64) {
				for (byte b : data) {
					String h = "00" + Integer.toHexString(b).toUpperCase();
					sb.append(h.substring(h.length()-2));
				}
			} else {
				sb.append("Data["+data.length+"]");
			}
			return sb.toString();
		}
	}
	
	public static abstract class FixedDataInstruction extends PICTInstruction {
		public byte[] data;
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			data = new byte[((opcode & 0xFF00) >>> 7)];
			in.readFully(data);
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			byte[] outdata = new byte[((opcode & 0xFF00) >>> 7)];
			for (int i = 0; i < data.length && i < outdata.length; i++) {
				outdata[i] = data[i];
			}
			out.write(outdata);
		}
		protected String toStringImpl() {
			StringBuffer sb = new StringBuffer();
			if (data.length <= 64) {
				for (byte b : data) {
					String h = "00" + Integer.toHexString(b).toUpperCase();
					sb.append(h.substring(h.length()-2));
				}
			} else {
				sb.append("Data["+data.length+"]");
			}
			return sb.toString();
		}
	}
	
	/* * * * * * * * * * * * * * * * * * *
	 * CONCRETE CLASSES FOR INSTRUCTIONS *
	 * * * * * * * * * * * * * * * * * * */
	
	public static class NOP extends ImpliedInstruction {
		public static final int OPCODE = 0x0000;
		public NOP() { opcode = OPCODE; }
	}
	
	public static class Clip extends RegionInstruction {
		public static final int OPCODE = 0x0001;
		public Clip() { opcode = OPCODE; }
		public Clip(Region rgn) { opcode = OPCODE; this.rgn = rgn; }
	}
	
	public static class BkPat extends PatternInstruction {
		public static final int OPCODE = 0x0002;
		public BkPat() { opcode = OPCODE; }
		public BkPat(long pat) { opcode = OPCODE; this.pat = pat; }
	}
	
	public static interface FontConstants {
		public static final int CHICAGO = 0;
		public static final int NEW_YORK = 2;
		public static final int GENEVA = 3;
		public static final int MONACO = 4;
		public static final int VENICE = 5;
		public static final int LONDON = 6;
		public static final int ATHENS = 7;
		public static final int SAN_FRANCISCO = 8;
		public static final int TORONTO = 9;
		public static final int CAIRO = 11;
		public static final int LOS_ANGELES = 12;
		public static final int ZAPF_DINGBATS = 13;
		public static final int BOOKMAN = 14;
		public static final int HELVETICA_NARROW = 15;
		public static final int PALATINO = 16;
		public static final int ZAPF_CHANCERY = 18;
		public static final int TIMES = 20;
		public static final int HELVETICA = 21;
		public static final int COURIER = 22;
		public static final int SYMBOL = 23;
		public static final int MOBILE = 24;
		public static final int AVANT_GARDE = 33;
		public static final int NEW_CENTURY_SCHOOLBOOK = 34;
		public static final int KEYBOARD = 98;
		public static final int LAST_RESORT = 99;
		public static final int ZAPFINO = 642;
		public static final int LUCIDA_GRANDE = 1024;
		public static final int TREBUCHET_MS = 1109;
		public static final int ITC_ZAPF_DINGBATS = 1236;
		public static final int ITC_ZAPF_CHANCERY = 1237;
		public static final int ITC_BOOKMAN = 1238;
		public static final int ARIAL_NARROW = 2000;
		public static final int ARIAL = 2001;
		public static final int CHARCOAL = 2002;
		public static final int CAPITALS = 2003;
		public static final int SAND = 2004;
		public static final int COURIER_NEW = 2005;
		public static final int TECHNO = 2006;
		public static final int MONOTYPE_SORTS = 2007;
		public static final int CENTURY_SCHOOLBOOK = 2008;
		public static final int MONOTYPE_CORSIVA = 2009;
		public static final int TIMES_NEW_ROMAN = 2010;
		public static final int WINGDINGS = 2011;
		public static final int HOEFLER_TEXT = 2013;
		public static final int HOEFLER_TEXT_ORNAMENTS = 2018;
		public static final int IMPACT = 2039;
		public static final int SKIA = 2040;
		public static final int WINGDINGS_3 = 2052;
		public static final int TEXTILE = 2305;
		public static final int GADGET = 2307;
		public static final int APPLE_CHANCERY = 2311;
		public static final int COMIC_SANS_MS = 4513;
		public static final int ANDALE_MONO = 7102;
		public static final int VERDANA = 7203;
		public static final int ARIAL_BLACK = 12077;
		public static final int GEORGIA = 12171;
		public static final int WEBDINGS = 14213;
		public static final int OSAKA = 16384;
		public static final int TAIPEI = 16896;
		public static final int BIAUKAI = 17082;
		public static final int APPLE_LIGOTHIC = 17168;
		public static final int APPLE_LISUNG = 17170;
		public static final int SEOUL = 17408;
		public static final int APPLE_MYUNGJO = 17409;
		public static final int APPLE_GOTHIC = 17410;
		public static final int GENEVA_CY = 19459;
		public static final int MONACO_CY = 19460;
		public static final int CHARCOAL_CY = 19461;
		public static final int HELVETICA_CY = 19492;
		public static final int TIMES_CY = 19540;
		public static final int BEIJING = 28672;
		public static final int SONG = 28929;
		public static final int HEI = 28930;
		public static final int KAI = 28931;
		public static final int FANG_SONG = 28932;
		public static final int GENEVA_CE = 30723;
		public static final int MONACO_CE = 30724;
		public static final int TIMES_CE = 30740;
		public static final int HELVETICA_CE = 30741;
		public static final int COURIER_CE = 30742;
	}
	
	public static String fontToString(int fontID) {
		switch (fontID) {
		case 0: return "Chicago";
		case 1: return "Geneva";
		case 2: return "New York";
		case 3: return "Geneva";
		case 4: return "Monaco";
		case 5: return "Venice";
		case 6: return "London";
		case 7: return "Athens";
		case 8: return "San Francisco";
		case 9: return "Toronto";
		case 11: return "Cairo";
		case 12: return "Los Angeles";
		case 13: return "Zapf Dingbats";
		case 14: return "Bookman";
		case 15: return "Helvetica Narrow";
		case 16: return "Palatino";
		case 18: return "Zapf Chancery";
		case 20: return "Times";
		case 21: return "Helvetica";
		case 22: return "Courier";
		case 23: return "Symbol";
		case 24: return "Mobile";
		case 33: return "Avant Garde";
		case 34: return "New Century Schoolbook";
		case 98: return ".Keyboard";
		case 99: return ".Last Resort";
		case 642: return "Zapfino";
		case 1024: return "Lucida Grande";
		case 1109: return "Trebuchet MS";
		case 1236: return "ITC Zapf Dingbats";
		case 1237: return "ITC Zapf Chancery";
		case 1238: return "ITC Bookman";
		case 2000: return "Arial Narrow";
		case 2001: return "Arial";
		case 2002: return "Charcoal";
		case 2003: return "Capitals";
		case 2004: return "Sand";
		case 2005: return "Courier New";
		case 2006: return "Techno";
		case 2007: return "Monotype Sorts";
		case 2008: return "Century Schoolbook";
		case 2009: return "Monotype Corsiva";
		case 2010: return "Times New Roman";
		case 2011: return "Wingdings";
		case 2013: return "Hoefler Text";
		case 2018: return "Hoefler Text Ornaments";
		case 2039: return "Impact";
		case 2040: return "Skia";
		case 2052: return "Wingdings 3";
		case 2305: return "Textile";
		case 2307: return "Gadget";
		case 2311: return "Apple Chancery";
		case 4513: return "Comic Sans MS";
		case 7102: return "Andale Mono";
		case 7203: return "Verdana";
		case 12077: return "Arial Black";
		case 12171: return "Georgia";
		case 14213: return "Webdings";
		case 16383: return "Chicago";
		case 16384: return "Osaka";
		case 16896: return "Taipei";
		case 17082: return "BiauKai";
		case 17168: return "Apple LiGothic";
		case 17170: return "Apple LiSung";
		case 17408: return "Seoul";
		case 17409: return "Apple Myungjo";
		case 17410: return "Apple Gothic";
		case 19459: return "Geneva CY";
		case 19460: return "Monaco CY";
		case 19461: return "Charcoal CY";
		case 19492: return "Helvetica CY";
		case 19540: return "Times CY";
		case 28672: return "Beijing";
		case 28929: return "Song";
		case 28930: return "Hei";
		case 28931: return "Kai";
		case 28932: return "Fang Song";
		case 30723: return "Geneva CE";
		case 30724: return "Monaco CE";
		case 30740: return "Times CE";
		case 30741: return "Helvetica CE";
		case 30742: return "Courier CE";
		default: return "Geneva";
		}
	}
	
	public static class TxFont extends PICTInstruction implements FontConstants {
		public static final int OPCODE = 0x0003;
		public int fontID;
		public TxFont() { opcode = OPCODE; }
		public TxFont(int fontID) { opcode = OPCODE; this.fontID = fontID; }
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			fontID = in.readShort();
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			out.writeShort(fontID);
		}
		protected String toStringImpl() {
			return fontID + " (" + toFontName() + ")";
		}
		public String toFontName() {
			return toFontName(fontID);
		}
		public static String toFontName(int fontID) {
			return fontToString(fontID);
		}
	}
	
	public static interface StyleConstants {
		public static final int BOLD = 0x01;
		public static final int ITALIC = 0x02;
		public static final int UNDERLINE = 0x04;
		public static final int OUTLINE = 0x08;
		public static final int SHADOW = 0x10;
		public static final int CONDENSE = 0x20;
		public static final int EXTEND = 0x40;
		public static final int GROUP = 0x80;
	}
	
	public static String styleToString(int style) {
		if ((style & 0xFF) == 0) return "plain";
		else {
			StringBuffer sb = new StringBuffer();
			if ((style & StyleConstants.BOLD) != 0) sb.append(", bold");
			if ((style & StyleConstants.ITALIC) != 0) sb.append(", italic");
			if ((style & StyleConstants.UNDERLINE) != 0) sb.append(", underline");
			if ((style & StyleConstants.OUTLINE) != 0) sb.append(", outline");
			if ((style & StyleConstants.SHADOW) != 0) sb.append(", shadow");
			if ((style & StyleConstants.CONDENSE) != 0) sb.append(", condense");
			if ((style & StyleConstants.EXTEND) != 0) sb.append(", extend");
			if ((style & StyleConstants.GROUP) != 0) sb.append(", group");
			return sb.toString().substring(2);
		}
	}
	
	public static class TxFace extends PICTInstruction implements StyleConstants {
		public static final int OPCODE = 0x0004;
		public int style;
		public TxFace() { opcode = OPCODE; }
		public TxFace(int style) { opcode = OPCODE; this.style = style; }
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			style = in.readUnsignedByte();
			if (v2) in.readByte();
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			out.writeByte(style);
			if (v2) out.writeByte(0);
		}
		protected String toStringImpl() {
			return styleToString(style);
		}
	}
	
	public static class TxMode extends ModeInstruction {
		public static final int OPCODE = 0x0005;
		public TxMode() { opcode = OPCODE; }
		public TxMode(int mode) { opcode = OPCODE; this.mode = mode; }
	}
	
	public static class SpExtra extends PICTInstruction {
		public static final int OPCODE = 0x0006;
		public float spExtra;
		public SpExtra() { opcode = OPCODE; }
		public SpExtra(float spExtra) { opcode = OPCODE; this.spExtra = spExtra; }
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			spExtra = in.readInt() / 65536.0f;
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			out.writeInt((int)(spExtra * 65536.0f));
		}
		protected String toStringImpl() {
			return Float.toString(spExtra);
		}
	}
	
	public static class PnSize extends PICTInstruction {
		public static final int OPCODE = 0x0007;
		public int penHeight;
		public int penWidth;
		public PnSize() { opcode = OPCODE; }
		public PnSize(int w, int h) { opcode = OPCODE; penHeight = h; penWidth = w; }
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			penHeight = in.readShort();
			penWidth = in.readShort();
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			out.writeShort(penHeight);
			out.writeShort(penWidth);
		}
		protected String toStringImpl() {
			return penWidth+"*"+penHeight;
		}
	}
	
	public static class PnMode extends ModeInstruction {
		public static final int OPCODE = 0x0008;
		public PnMode() { opcode = OPCODE; }
		public PnMode(int mode) { opcode = OPCODE; this.mode = mode; }
	}
	
	public static class PnPat extends PatternInstruction {
		public static final int OPCODE = 0x0009;
		public PnPat() { opcode = OPCODE; }
		public PnPat(long pat) { opcode = OPCODE; this.pat = pat; }
	}
	
	public static class FillPat extends PatternInstruction {
		public static final int OPCODE = 0x000A;
		public FillPat() { opcode = OPCODE; }
		public FillPat(long pat) { opcode = OPCODE; this.pat = pat; }
	}
	
	public static class OvSize extends PICTInstruction {
		public static final int OPCODE = 0x000B;
		public int ovalHeight;
		public int ovalWidth;
		public OvSize() { opcode = OPCODE; }
		public OvSize(int w, int h) { opcode = OPCODE; ovalWidth = w; ovalHeight = h; }
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			ovalHeight = in.readShort();
			ovalWidth = in.readShort();
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			out.writeShort(ovalHeight);
			out.writeShort(ovalWidth);
		}
		protected String toStringImpl() {
			return ovalWidth+"*"+ovalHeight;
		}
	}
	
	public static class Origin extends PICTInstruction {
		public static final int OPCODE = 0x000C;
		public int dh;
		public int dv;
		public Origin() { opcode = OPCODE; }
		public Origin(int dh, int dv) { opcode = OPCODE; this.dh = dh; this.dv = dv; }
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			dh = in.readShort();
			dv = in.readShort();
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			out.writeShort(dh);
			out.writeShort(dv);
		}
		protected String toStringImpl() {
			return ((dh<0)?dh:("+"+dh))+","+((dv<0)?dv:("+"+dv));
		}
	}
	
	public static class TxSize extends PICTInstruction {
		public static final int OPCODE = 0x000D;
		public int size;
		public TxSize() { opcode = OPCODE; }
		public TxSize(int size) { opcode = OPCODE; this.size = size; }
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			size = in.readShort();
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			out.writeShort(size);
		}
		protected String toStringImpl() {
			return Integer.toString(size);
		}
	}
	
	public static class FgColor extends ColorInstruction {
		public static final int OPCODE = 0x000E;
		public FgColor() { opcode = OPCODE; }
		public FgColor(int color) { opcode = OPCODE; this.color = color; }
	}
	
	public static class BkColor extends ColorInstruction {
		public static final int OPCODE = 0x000F;
		public BkColor() { opcode = OPCODE; }
		public BkColor(int color) { opcode = OPCODE; this.color = color; }
	}
	
	public static class TxRatio extends PICTInstruction {
		public static final int OPCODE = 0x0010;
		public int vnum;
		public int hnum;
		public int vdenom;
		public int hdenom;
		public TxRatio() { opcode = OPCODE; }
		public TxRatio(int xn, int xd, int yn, int yd) { opcode = OPCODE; vnum = yn; hnum = xn; vdenom = yd; hdenom = xd; }
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			vnum = in.readShort();
			hnum = in.readShort();
			vdenom = in.readShort();
			hdenom = in.readShort();
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			out.writeShort(vnum);
			out.writeShort(hnum);
			out.writeShort(vdenom);
			out.writeShort(hdenom);
		}
		protected String toStringImpl() {
			return hnum+"/"+hdenom+" * "+vnum+"/"+vdenom;
		}
	}
	
	public static class VersionOp extends PICTInstruction {
		public static final int OPCODE = 0x0011;
		public int v;
		public VersionOp() { opcode = OPCODE; }
		public VersionOp(int v) { opcode = OPCODE; this.v = v; }
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			v = in.readUnsignedByte();
			if (v >= 2) in.readByte();
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			out.writeByte(v);
			if (v >= 2) out.writeByte(0xFF);
		}
		protected String toStringImpl() {
			return Integer.toString(v);
		}
	}
	
	public static class BkPixPat extends PixelPatternInstruction {
		public static final int OPCODE = 0x0012;
		public BkPixPat() { opcode = OPCODE; }
		public BkPixPat(long pat) { opcode = OPCODE; patType = BW_PAT; pat1Data = pat; rgb = null; pixMap = null; colorTable = null; pixData = null; }
		public BkPixPat(long pat, RGBColor color) { opcode = OPCODE; patType = DITHER_PAT; pat1Data = pat; rgb = color; pixMap = null; colorTable = null; pixData = null; }
		public BkPixPat(long pat, PixMap pm, ColorTable ct, byte[] data) { opcode = OPCODE; patType = PIXEL_PAT; pat1Data = pat; rgb = null; pixMap = pm; colorTable = ct; pixData = data; }
	}
	
	public static class PnPixPat extends PixelPatternInstruction {
		public static final int OPCODE = 0x0013;
		public PnPixPat() { opcode = OPCODE; }
		public PnPixPat(long pat) { opcode = OPCODE; patType = BW_PAT; pat1Data = pat; rgb = null; pixMap = null; colorTable = null; pixData = null; }
		public PnPixPat(long pat, RGBColor color) { opcode = OPCODE; patType = DITHER_PAT; pat1Data = pat; rgb = color; pixMap = null; colorTable = null; pixData = null; }
		public PnPixPat(long pat, PixMap pm, ColorTable ct, byte[] data) { opcode = OPCODE; patType = PIXEL_PAT; pat1Data = pat; rgb = null; pixMap = pm; colorTable = ct; pixData = data; }
	}
	
	public static class FillPixPat extends PixelPatternInstruction {
		public static final int OPCODE = 0x0014;
		public FillPixPat() { opcode = OPCODE; }
		public FillPixPat(long pat) { opcode = OPCODE; patType = BW_PAT; pat1Data = pat; rgb = null; pixMap = null; colorTable = null; pixData = null; }
		public FillPixPat(long pat, RGBColor color) { opcode = OPCODE; patType = DITHER_PAT; pat1Data = pat; rgb = color; pixMap = null; colorTable = null; pixData = null; }
		public FillPixPat(long pat, PixMap pm, ColorTable ct, byte[] data) { opcode = OPCODE; patType = PIXEL_PAT; pat1Data = pat; rgb = null; pixMap = pm; colorTable = ct; pixData = data; }
	}
	
	public static class PnLocHFrac extends PICTInstruction {
		public static final int OPCODE = 0x0015;
		public float hfrac;
		public PnLocHFrac() { opcode = OPCODE; }
		public PnLocHFrac(float hfrac) { opcode = OPCODE; this.hfrac = hfrac; }
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			hfrac = in.readUnsignedShort() / 65536.0f;
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			out.writeShort((int)(hfrac * 65536.0f));
		}
		protected String toStringImpl() {
			return Float.toString(hfrac);
		}
	}
	
	public static class ChExtra extends PICTInstruction {
		public static final int OPCODE = 0x0016;
		public int chExtra;
		public ChExtra() { opcode = OPCODE; }
		public ChExtra(int chExtra) { opcode = OPCODE; this.chExtra = chExtra; }
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			chExtra = in.readShort();
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			out.writeShort(chExtra);
		}
		protected String toStringImpl() {
			return Integer.toString(chExtra);
		}
	}
	
	public static class Reserved0017 extends ImpliedInstruction {
		public static final int OPCODE = 0x0017;
		public Reserved0017() { opcode = OPCODE; }
	}
	
	public static class Reserved0018 extends ImpliedInstruction {
		public static final int OPCODE = 0x0018;
		public Reserved0018() { opcode = OPCODE; }
	}
	
	public static class Reserved0019 extends ImpliedInstruction {
		public static final int OPCODE = 0x0019;
		public Reserved0019() { opcode = OPCODE; }
	}
	
	public static class RGBFgCol extends RGBColorInstruction {
		public static final int OPCODE = 0x001A;
		public RGBFgCol() { opcode = OPCODE; }
		public RGBFgCol(RGBColor color) { opcode = OPCODE; this.color = color; }
	}
	
	public static class RGBBkCol extends RGBColorInstruction {
		public static final int OPCODE = 0x001B;
		public RGBBkCol() { opcode = OPCODE; }
		public RGBBkCol(RGBColor color) { opcode = OPCODE; this.color = color; }
	}
	
	public static class HiliteMode extends ImpliedInstruction {
		public static final int OPCODE = 0x001C;
		public HiliteMode() { opcode = OPCODE; }
	}
	
	public static class HiliteColor extends RGBColorInstruction {
		public static final int OPCODE = 0x001D;
		public HiliteColor() { opcode = OPCODE; }
		public HiliteColor(RGBColor color) { opcode = OPCODE; this.color = color; }
	}
	
	public static class DefHilite extends ImpliedInstruction {
		public static final int OPCODE = 0x001E;
		public DefHilite() { opcode = OPCODE; }
	}
	
	public static class OpColor extends RGBColorInstruction {
		public static final int OPCODE = 0x001F;
		public OpColor() { opcode = OPCODE; }
		public OpColor(RGBColor color) { opcode = OPCODE; this.color = color; }
	}
	
	public static class Line extends PICTInstruction {
		public static final int OPCODE = 0x0020;
		public Point pnLoc;
		public Point newPt;
		public Line() { opcode = OPCODE; }
		public Line(Point p1, Point p2) { opcode = OPCODE; pnLoc = p1; newPt = p2; }
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			pnLoc = Point.read(in);
			newPt = Point.read(in);
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			pnLoc.write(out);
			newPt.write(out);
		}
		protected String toStringImpl() {
			return pnLoc.toString() + " " + newPt.toString();
		}
	}
	
	public static class LineFrom extends PICTInstruction {
		public static final int OPCODE = 0x0021;
		public Point newPt;
		public LineFrom() { opcode = OPCODE; }
		public LineFrom(Point p2) { opcode = OPCODE; newPt = p2; }
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			newPt = Point.read(in);
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			newPt.write(out);
		}
		protected String toStringImpl() {
			return newPt.toString();
		}
	}
	
	public static class ShortLine extends PICTInstruction {
		public static final int OPCODE = 0x0022;
		public Point pnLoc;
		public int dh;
		public int dv;
		public ShortLine() { opcode = OPCODE; }
		public ShortLine(Point p1, int dh, int dv) { opcode = OPCODE; pnLoc = p1; this.dh = dh; this.dv = dv; }
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			pnLoc = Point.read(in);
			dh = in.readByte();
			dv = in.readByte();
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			pnLoc.write(out);
			out.writeByte(dh);
			out.writeByte(dv);
		}
		protected String toStringImpl() {
			return pnLoc.toString()+" "+((dh<0)?dh:("+"+dh))+","+((dv<0)?dv:("+"+dv));
		}
	}
	
	public static class ShortLineFrom extends PICTInstruction {
		public static final int OPCODE = 0x0023;
		public int dh;
		public int dv;
		public ShortLineFrom() { opcode = OPCODE; }
		public ShortLineFrom(int dh, int dv) { opcode = OPCODE; this.dh = dh; this.dv = dv; }
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			dh = in.readByte();
			dv = in.readByte();
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			out.writeByte(dh);
			out.writeByte(dv);
		}
		protected String toStringImpl() {
			return ((dh<0)?dh:("+"+dh))+","+((dv<0)?dv:("+"+dv));
		}
	}
	
	public static class Reserved0024 extends ShortDataInstruction {
		public static final int OPCODE = 0x0024;
		public Reserved0024() { opcode = OPCODE; }
		public Reserved0024(byte[] data) { opcode = OPCODE; this.data = data; }
	}
	
	public static class Reserved0025 extends ShortDataInstruction {
		public static final int OPCODE = 0x0025;
		public Reserved0025() { opcode = OPCODE; }
		public Reserved0025(byte[] data) { opcode = OPCODE; this.data = data; }
	}
	
	public static class Reserved0026 extends ShortDataInstruction {
		public static final int OPCODE = 0x0026;
		public Reserved0026() { opcode = OPCODE; }
		public Reserved0026(byte[] data) { opcode = OPCODE; this.data = data; }
	}
	
	public static class Reserved0027 extends ShortDataInstruction {
		public static final int OPCODE = 0x0027;
		public Reserved0027() { opcode = OPCODE; }
		public Reserved0027(byte[] data) { opcode = OPCODE; this.data = data; }
	}
	
	public static class LongText extends PICTInstruction {
		public static final int OPCODE = 0x0028;
		public Point txLoc;
		public String text;
		public LongText() { opcode = OPCODE; }
		public LongText(Point p, String s) { opcode = OPCODE; txLoc = p; text = s; }
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			txLoc = Point.read(in);
			byte[] td = new byte[in.readUnsignedByte()];
			in.readFully(td);
			text = PICTUtilities.decodeString(td);
			if (v2 && (td.length & 1) == 0) in.readByte();
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			txLoc.write(out);
			byte[] td = PICTUtilities.encodeString(text);
			out.writeByte(td.length);
			out.write(td);
			if (v2 && (td.length & 1) == 0) out.writeByte(0);
		}
		protected String toStringImpl() {
			return txLoc.toString()+" "+text.trim();
		}
	}
	
	public static class DHText extends PICTInstruction {
		public static final int OPCODE = 0x0029;
		public int dh;
		public String text;
		public DHText() { opcode = OPCODE; }
		public DHText(int dh, String s) { opcode = OPCODE; this.dh = dh; text = s; }
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			dh = in.readByte();
			byte[] td = new byte[in.readUnsignedByte()];
			in.readFully(td);
			text = PICTUtilities.decodeString(td);
			if (v2 && (td.length & 1) == 1) in.readByte();
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			out.writeByte(dh);
			byte[] td = PICTUtilities.encodeString(text);
			out.writeByte(td.length);
			out.write(td);
			if (v2 && (td.length & 1) == 1) out.writeByte(0);
		}
		protected String toStringImpl() {
			return ((dh<0)?dh:("+"+dh))+" "+text.trim();
		}
	}
	
	public static class DVText extends PICTInstruction {
		public static final int OPCODE = 0x002A;
		public int dv;
		public String text;
		public DVText() { opcode = OPCODE; }
		public DVText(int dv, String s) { opcode = OPCODE; this.dv = dv; text = s; }
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			dv = in.readByte();
			byte[] td = new byte[in.readUnsignedByte()];
			in.readFully(td);
			text = PICTUtilities.decodeString(td);
			if (v2 && (td.length & 1) == 1) in.readByte();
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			out.writeByte(dv);
			byte[] td = PICTUtilities.encodeString(text);
			out.writeByte(td.length);
			out.write(td);
			if (v2 && (td.length & 1) == 1) out.writeByte(0);
		}
		protected String toStringImpl() {
			return ((dv<0)?dv:("+"+dv))+" "+text.trim();
		}
	}
	
	public static class DHDVText extends PICTInstruction {
		public static final int OPCODE = 0x002B;
		public int dh;
		public int dv;
		public String text;
		public DHDVText() { opcode = OPCODE; }
		public DHDVText(int dh, int dv, String s) { opcode = OPCODE; this.dh = dh; this.dv = dv; text = s; }
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			dh = in.readByte();
			dv = in.readByte();
			byte[] td = new byte[in.readUnsignedByte()];
			in.readFully(td);
			text = PICTUtilities.decodeString(td);
			if (v2 && (td.length & 1) == 0) in.readByte();
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			out.writeByte(dh);
			out.writeByte(dv);
			byte[] td = PICTUtilities.encodeString(text);
			out.writeByte(td.length);
			out.write(td);
			if (v2 && (td.length & 1) == 0) out.writeByte(0);
		}
		protected String toStringImpl() {
			return ((dh<0)?dh:("+"+dh))+","+((dv<0)?dv:("+"+dv))+" "+text.trim();
		}
	}
	
	public static class FontName extends PICTInstruction implements FontConstants {
		public static final int OPCODE = 0x002C;
		public int fontID;
		public String fontName;
		public FontName() { opcode = OPCODE; }
		public FontName(int fontID, String fontName) { opcode = OPCODE; this.fontID = fontID; this.fontName = fontName; }
		public FontName(String fontName) {
			opcode = OPCODE;
			if (fontName.equalsIgnoreCase("Chicago")) {
				this.fontID = 0;
			} else {
				boolean found = false;
				for (int i = 2; i < 32768; i++) {
					if (fontToString(i).equalsIgnoreCase(fontName)) {
						this.fontID = i;
						found = true;
						break;
					}
				}
				if (!found) {
					this.fontID = -(int)Math.ceil(Math.random() * 32767 + 1);
				}
			}
			this.fontName = fontName;
		}
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			in.readShort();
			fontID = in.readShort();
			byte[] fnd = new byte[in.readUnsignedByte()];
			in.readFully(fnd);
			fontName = PICTUtilities.decodeString(fnd);
			if (v2 && (fnd.length & 1) == 0) in.readByte();
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			byte[] fnd = PICTUtilities.encodeString(fontName);
			out.writeShort(3+fnd.length);
			out.writeShort(fontID);
			out.writeByte(fnd.length);
			out.write(fnd);
			if (v2 && (fnd.length & 1) == 0) out.writeByte(0);
		}
		protected String toStringImpl() {
			return fontID+" "+fontName.trim();
		}
	}
	
	public static class LineJustify extends PICTInstruction {
		public static final int OPCODE = 0x002D;
		public float spacing;
		public float spExtra;
		public LineJustify() { opcode = OPCODE; }
		public LineJustify(float spacing, float spExtra) { opcode = OPCODE; this.spacing = spacing; this.spExtra = spExtra; }
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			in.readShort();
			spacing = in.readInt() / 65536.0f;
			spExtra = in.readInt() / 65536.0f;
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			out.writeShort(8);
			out.writeInt((int)(spacing * 65536.0f));
			out.writeInt((int)(spExtra * 65536.0f));
		}
		protected String toStringImpl() {
			return spacing+" "+spExtra;
		}
	}
	
	public static class GlyphState extends PICTInstruction {
		public static final int OPCODE = 0x002E;
		public boolean outlinePreferred;
		public boolean preserveGlyph;
		public boolean fractionalWidths;
		public boolean scalingDisabled;
		public boolean[] extra;
		public GlyphState() { opcode = OPCODE; extra = new boolean[2]; }
		public GlyphState(boolean op, boolean pg, boolean fw, boolean sd) { opcode = OPCODE; outlinePreferred = op; preserveGlyph = pg; fractionalWidths = fw; scalingDisabled = sd; extra = new boolean[2]; }
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			extra = new boolean[in.readShort()-4];
			outlinePreferred = in.readBoolean();
			preserveGlyph = in.readBoolean();
			fractionalWidths = in.readBoolean();
			scalingDisabled = in.readBoolean();
			for (int i = 0; i < extra.length; i++) {
				extra[i] = in.readBoolean();
			}
			if (v2 && (extra.length & 1) == 1) in.readByte();
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			out.writeShort(4+extra.length);
			out.writeBoolean(outlinePreferred);
			out.writeBoolean(preserveGlyph);
			out.writeBoolean(fractionalWidths);
			out.writeBoolean(scalingDisabled);
			for (boolean b : extra) {
				out.writeBoolean(b);
			}
			if (v2 & (extra.length & 1) == 1) out.writeByte(0);
		}
		protected String toStringImpl() {
			StringBuffer sb = new StringBuffer();
			if (outlinePreferred) sb.append(", outlinePreferred");
			if (preserveGlyph) sb.append(", preserveGlyph");
			if (fractionalWidths) sb.append(", fractionalWidths");
			if (scalingDisabled) sb.append(", scalingDisabled");
			for (int i = 0; i < extra.length; i++) {
				if (extra[i]) sb.append(", "+(i+4));
			}
			if (sb.length() >= 2) return sb.toString().substring(2);
			else return "";
		}
	}
	
	public static class Reserved002F extends ShortDataInstruction {
		public static final int OPCODE = 0x002F;
		public Reserved002F() { opcode = OPCODE; }
		public Reserved002F(byte[] data) { opcode = OPCODE; this.data = data; }
	}
	
	public static class FrameRect extends RectangleInstruction {
		public static final int OPCODE = 0x0030;
		public FrameRect() { opcode = OPCODE; }
		public FrameRect(Rect rect) { opcode = OPCODE; this.rect = rect; }
	}
	
	public static class PaintRect extends RectangleInstruction {
		public static final int OPCODE = 0x0031;
		public PaintRect() { opcode = OPCODE; }
		public PaintRect(Rect rect) { opcode = OPCODE; this.rect = rect; }
	}
	
	public static class EraseRect extends RectangleInstruction {
		public static final int OPCODE = 0x0032;
		public EraseRect() { opcode = OPCODE; }
		public EraseRect(Rect rect) { opcode = OPCODE; this.rect = rect; }
	}
	
	public static class InvertRect extends RectangleInstruction {
		public static final int OPCODE = 0x0033;
		public InvertRect() { opcode = OPCODE; }
		public InvertRect(Rect rect) { opcode = OPCODE; this.rect = rect; }
	}
	
	public static class FillRect extends RectangleInstruction {
		public static final int OPCODE = 0x0034;
		public FillRect() { opcode = OPCODE; }
		public FillRect(Rect rect) { opcode = OPCODE; this.rect = rect; }
	}
	
	public static class Reserved0035 extends RectangleInstruction {
		public static final int OPCODE = 0x0035;
		public Reserved0035() { opcode = OPCODE; }
		public Reserved0035(Rect rect) { opcode = OPCODE; this.rect = rect; }
	}
	
	public static class Reserved0036 extends RectangleInstruction {
		public static final int OPCODE = 0x0036;
		public Reserved0036() { opcode = OPCODE; }
		public Reserved0036(Rect rect) { opcode = OPCODE; this.rect = rect; }
	}
	
	public static class Reserved0037 extends RectangleInstruction {
		public static final int OPCODE = 0x0037;
		public Reserved0037() { opcode = OPCODE; }
		public Reserved0037(Rect rect) { opcode = OPCODE; this.rect = rect; }
	}
	
	public static class FrameSameRect extends ImpliedInstruction {
		public static final int OPCODE = 0x0038;
		public FrameSameRect() { opcode = OPCODE; }
	}
	
	public static class PaintSameRect extends ImpliedInstruction {
		public static final int OPCODE = 0x0039;
		public PaintSameRect() { opcode = OPCODE; }
	}
	
	public static class EraseSameRect extends ImpliedInstruction {
		public static final int OPCODE = 0x003A;
		public EraseSameRect() { opcode = OPCODE; }
	}
	
	public static class InvertSameRect extends ImpliedInstruction {
		public static final int OPCODE = 0x003B;
		public InvertSameRect() { opcode = OPCODE; }
	}
	
	public static class FillSameRect extends ImpliedInstruction {
		public static final int OPCODE = 0x003C;
		public FillSameRect() { opcode = OPCODE; }
	}
	
	public static class Reserved003D extends ImpliedInstruction {
		public static final int OPCODE = 0x003D;
		public Reserved003D() { opcode = OPCODE; }
	}
	
	public static class Reserved003E extends ImpliedInstruction {
		public static final int OPCODE = 0x003E;
		public Reserved003E() { opcode = OPCODE; }
	}
	
	public static class Reserved003F extends ImpliedInstruction {
		public static final int OPCODE = 0x003F;
		public Reserved003F() { opcode = OPCODE; }
	}
	
	public static class FrameRRect extends RectangleInstruction {
		public static final int OPCODE = 0x0040;
		public FrameRRect() { opcode = OPCODE; }
		public FrameRRect(Rect rect) { opcode = OPCODE; this.rect = rect; }
	}
	
	public static class PaintRRect extends RectangleInstruction {
		public static final int OPCODE = 0x0041;
		public PaintRRect() { opcode = OPCODE; }
		public PaintRRect(Rect rect) { opcode = OPCODE; this.rect = rect; }
	}
	
	public static class EraseRRect extends RectangleInstruction {
		public static final int OPCODE = 0x0042;
		public EraseRRect() { opcode = OPCODE; }
		public EraseRRect(Rect rect) { opcode = OPCODE; this.rect = rect; }
	}
	
	public static class InvertRRect extends RectangleInstruction {
		public static final int OPCODE = 0x0043;
		public InvertRRect() { opcode = OPCODE; }
		public InvertRRect(Rect rect) { opcode = OPCODE; this.rect = rect; }
	}
	
	public static class FillRRect extends RectangleInstruction {
		public static final int OPCODE = 0x0044;
		public FillRRect() { opcode = OPCODE; }
		public FillRRect(Rect rect) { opcode = OPCODE; this.rect = rect; }
	}
	
	public static class Reserved0045 extends RectangleInstruction {
		public static final int OPCODE = 0x0045;
		public Reserved0045() { opcode = OPCODE; }
		public Reserved0045(Rect rect) { opcode = OPCODE; this.rect = rect; }
	}
	
	public static class Reserved0046 extends RectangleInstruction {
		public static final int OPCODE = 0x0046;
		public Reserved0046() { opcode = OPCODE; }
		public Reserved0046(Rect rect) { opcode = OPCODE; this.rect = rect; }
	}
	
	public static class Reserved0047 extends RectangleInstruction {
		public static final int OPCODE = 0x0047;
		public Reserved0047() { opcode = OPCODE; }
		public Reserved0047(Rect rect) { opcode = OPCODE; this.rect = rect; }
	}
	
	public static class FrameSameRRect extends ImpliedInstruction {
		public static final int OPCODE = 0x0048;
		public FrameSameRRect() { opcode = OPCODE; }
	}
	
	public static class PaintSameRRect extends ImpliedInstruction {
		public static final int OPCODE = 0x0049;
		public PaintSameRRect() { opcode = OPCODE; }
	}
	
	public static class EraseSameRRect extends ImpliedInstruction {
		public static final int OPCODE = 0x004A;
		public EraseSameRRect() { opcode = OPCODE; }
	}
	
	public static class InvertSameRRect extends ImpliedInstruction {
		public static final int OPCODE = 0x004B;
		public InvertSameRRect() { opcode = OPCODE; }
	}
	
	public static class FillSameRRect extends ImpliedInstruction {
		public static final int OPCODE = 0x004C;
		public FillSameRRect() { opcode = OPCODE; }
	}
	
	public static class Reserved004D extends ImpliedInstruction {
		public static final int OPCODE = 0x004D;
		public Reserved004D() { opcode = OPCODE; }
	}
	
	public static class Reserved004E extends ImpliedInstruction {
		public static final int OPCODE = 0x004E;
		public Reserved004E() { opcode = OPCODE; }
	}
	
	public static class Reserved004F extends ImpliedInstruction {
		public static final int OPCODE = 0x004F;
		public Reserved004F() { opcode = OPCODE; }
	}
	
	public static class FrameOval extends RectangleInstruction {
		public static final int OPCODE = 0x0050;
		public FrameOval() { opcode = OPCODE; }
		public FrameOval(Rect rect) { opcode = OPCODE; this.rect = rect; }
	}
	
	public static class PaintOval extends RectangleInstruction {
		public static final int OPCODE = 0x0051;
		public PaintOval() { opcode = OPCODE; }
		public PaintOval(Rect rect) { opcode = OPCODE; this.rect = rect; }
	}
	
	public static class EraseOval extends RectangleInstruction {
		public static final int OPCODE = 0x0052;
		public EraseOval() { opcode = OPCODE; }
		public EraseOval(Rect rect) { opcode = OPCODE; this.rect = rect; }
	}
	
	public static class InvertOval extends RectangleInstruction {
		public static final int OPCODE = 0x0053;
		public InvertOval() { opcode = OPCODE; }
		public InvertOval(Rect rect) { opcode = OPCODE; this.rect = rect; }
	}
	
	public static class FillOval extends RectangleInstruction {
		public static final int OPCODE = 0x0054;
		public FillOval() { opcode = OPCODE; }
		public FillOval(Rect rect) { opcode = OPCODE; this.rect = rect; }
	}
	
	public static class Reserved0055 extends RectangleInstruction {
		public static final int OPCODE = 0x0055;
		public Reserved0055() { opcode = OPCODE; }
		public Reserved0055(Rect rect) { opcode = OPCODE; this.rect = rect; }
	}
	
	public static class Reserved0056 extends RectangleInstruction {
		public static final int OPCODE = 0x0056;
		public Reserved0056() { opcode = OPCODE; }
		public Reserved0056(Rect rect) { opcode = OPCODE; this.rect = rect; }
	}
	
	public static class Reserved0057 extends RectangleInstruction {
		public static final int OPCODE = 0x0057;
		public Reserved0057() { opcode = OPCODE; }
		public Reserved0057(Rect rect) { opcode = OPCODE; this.rect = rect; }
	}
	
	public static class FrameSameOval extends ImpliedInstruction {
		public static final int OPCODE = 0x0058;
		public FrameSameOval() { opcode = OPCODE; }
	}
	
	public static class PaintSameOval extends ImpliedInstruction {
		public static final int OPCODE = 0x0059;
		public PaintSameOval() { opcode = OPCODE; }
	}
	
	public static class EraseSameOval extends ImpliedInstruction {
		public static final int OPCODE = 0x005A;
		public EraseSameOval() { opcode = OPCODE; }
	}
	
	public static class InvertSameOval extends ImpliedInstruction {
		public static final int OPCODE = 0x005B;
		public InvertSameOval() { opcode = OPCODE; }
	}
	
	public static class FillSameOval extends ImpliedInstruction {
		public static final int OPCODE = 0x005C;
		public FillSameOval() { opcode = OPCODE; }
	}
	
	public static class Reserved005D extends ImpliedInstruction {
		public static final int OPCODE = 0x005D;
		public Reserved005D() { opcode = OPCODE; }
	}
	
	public static class Reserved005E extends ImpliedInstruction {
		public static final int OPCODE = 0x005E;
		public Reserved005E() { opcode = OPCODE; }
	}
	
	public static class Reserved005F extends ImpliedInstruction {
		public static final int OPCODE = 0x005F;
		public Reserved005F() { opcode = OPCODE; }
	}
	
	public static class FrameArc extends ArcInstruction {
		public static final int OPCODE = 0x0060;
		public FrameArc() { opcode = OPCODE; }
		public FrameArc(Rect rect, int startAngle, int arcAngle) { opcode = OPCODE; this.rect = rect; this.startAngle = startAngle; this.arcAngle = arcAngle; }
	}
	
	public static class PaintArc extends ArcInstruction {
		public static final int OPCODE = 0x0061;
		public PaintArc() { opcode = OPCODE; }
		public PaintArc(Rect rect, int startAngle, int arcAngle) { opcode = OPCODE; this.rect = rect; this.startAngle = startAngle; this.arcAngle = arcAngle; }
	}
	
	public static class EraseArc extends ArcInstruction {
		public static final int OPCODE = 0x0062;
		public EraseArc() { opcode = OPCODE; }
		public EraseArc(Rect rect, int startAngle, int arcAngle) { opcode = OPCODE; this.rect = rect; this.startAngle = startAngle; this.arcAngle = arcAngle; }
	}
	
	public static class InvertArc extends ArcInstruction {
		public static final int OPCODE = 0x0063;
		public InvertArc() { opcode = OPCODE; }
		public InvertArc(Rect rect, int startAngle, int arcAngle) { opcode = OPCODE; this.rect = rect; this.startAngle = startAngle; this.arcAngle = arcAngle; }
	}
	
	public static class FillArc extends ArcInstruction {
		public static final int OPCODE = 0x0064;
		public FillArc() { opcode = OPCODE; }
		public FillArc(Rect rect, int startAngle, int arcAngle) { opcode = OPCODE; this.rect = rect; this.startAngle = startAngle; this.arcAngle = arcAngle; }
	}
	
	public static class Reserved0065 extends ArcInstruction {
		public static final int OPCODE = 0x0065;
		public Reserved0065() { opcode = OPCODE; }
		public Reserved0065(Rect rect, int startAngle, int arcAngle) { opcode = OPCODE; this.rect = rect; this.startAngle = startAngle; this.arcAngle = arcAngle; }
	}
	
	public static class Reserved0066 extends ArcInstruction {
		public static final int OPCODE = 0x0066;
		public Reserved0066() { opcode = OPCODE; }
		public Reserved0066(Rect rect, int startAngle, int arcAngle) { opcode = OPCODE; this.rect = rect; this.startAngle = startAngle; this.arcAngle = arcAngle; }
	}
	
	public static class Reserved0067 extends ArcInstruction {
		public static final int OPCODE = 0x0067;
		public Reserved0067() { opcode = OPCODE; }
		public Reserved0067(Rect rect, int startAngle, int arcAngle) { opcode = OPCODE; this.rect = rect; this.startAngle = startAngle; this.arcAngle = arcAngle; }
	}
	
	public static class FrameSameArc extends SameArcInstruction {
		public static final int OPCODE = 0x0068;
		public FrameSameArc() { opcode = OPCODE; }
		public FrameSameArc(int startAngle, int arcAngle) { opcode = OPCODE; this.startAngle = startAngle; this.arcAngle = arcAngle; }
	}
	
	public static class PaintSameArc extends SameArcInstruction {
		public static final int OPCODE = 0x0069;
		public PaintSameArc() { opcode = OPCODE; }
		public PaintSameArc(int startAngle, int arcAngle) { opcode = OPCODE; this.startAngle = startAngle; this.arcAngle = arcAngle; }
	}
	
	public static class EraseSameArc extends SameArcInstruction {
		public static final int OPCODE = 0x006A;
		public EraseSameArc() { opcode = OPCODE; }
		public EraseSameArc(int startAngle, int arcAngle) { opcode = OPCODE; this.startAngle = startAngle; this.arcAngle = arcAngle; }
	}
	
	public static class InvertSameArc extends SameArcInstruction {
		public static final int OPCODE = 0x006B;
		public InvertSameArc() { opcode = OPCODE; }
		public InvertSameArc(int startAngle, int arcAngle) { opcode = OPCODE; this.startAngle = startAngle; this.arcAngle = arcAngle; }
	}
	
	public static class FillSameArc extends SameArcInstruction {
		public static final int OPCODE = 0x006C;
		public FillSameArc() { opcode = OPCODE; }
		public FillSameArc(int startAngle, int arcAngle) { opcode = OPCODE; this.startAngle = startAngle; this.arcAngle = arcAngle; }
	}
	
	public static class Reserved006D extends SameArcInstruction {
		public static final int OPCODE = 0x006D;
		public Reserved006D() { opcode = OPCODE; }
		public Reserved006D(int startAngle, int arcAngle) { opcode = OPCODE; this.startAngle = startAngle; this.arcAngle = arcAngle; }
	}
	
	public static class Reserved006E extends SameArcInstruction {
		public static final int OPCODE = 0x006E;
		public Reserved006E() { opcode = OPCODE; }
		public Reserved006E(int startAngle, int arcAngle) { opcode = OPCODE; this.startAngle = startAngle; this.arcAngle = arcAngle; }
	}
	
	public static class Reserved006F extends SameArcInstruction {
		public static final int OPCODE = 0x006F;
		public Reserved006F() { opcode = OPCODE; }
		public Reserved006F(int startAngle, int arcAngle) { opcode = OPCODE; this.startAngle = startAngle; this.arcAngle = arcAngle; }
	}
	
	public static class FramePoly extends PolygonInstruction {
		public static final int OPCODE = 0x0070;
		public FramePoly() { opcode = OPCODE; }
		public FramePoly(Polygon poly) { opcode = OPCODE; this.poly = poly; }
	}
	
	public static class PaintPoly extends PolygonInstruction {
		public static final int OPCODE = 0x0071;
		public PaintPoly() { opcode = OPCODE; }
		public PaintPoly(Polygon poly) { opcode = OPCODE; this.poly = poly; }
	}
	
	public static class ErasePoly extends PolygonInstruction {
		public static final int OPCODE = 0x0072;
		public ErasePoly() { opcode = OPCODE; }
		public ErasePoly(Polygon poly) { opcode = OPCODE; this.poly = poly; }
	}
	
	public static class InvertPoly extends PolygonInstruction {
		public static final int OPCODE = 0x0073;
		public InvertPoly() { opcode = OPCODE; }
		public InvertPoly(Polygon poly) { opcode = OPCODE; this.poly = poly; }
	}
	
	public static class FillPoly extends PolygonInstruction {
		public static final int OPCODE = 0x0074;
		public FillPoly() { opcode = OPCODE; }
		public FillPoly(Polygon poly) { opcode = OPCODE; this.poly = poly; }
	}
	
	public static class Reserved0075 extends PolygonInstruction {
		public static final int OPCODE = 0x0075;
		public Reserved0075() { opcode = OPCODE; }
		public Reserved0075(Polygon poly) { opcode = OPCODE; this.poly = poly; }
	}
	
	public static class Reserved0076 extends PolygonInstruction {
		public static final int OPCODE = 0x0076;
		public Reserved0076() { opcode = OPCODE; }
		public Reserved0076(Polygon poly) { opcode = OPCODE; this.poly = poly; }
	}
	
	public static class Reserved0077 extends PolygonInstruction {
		public static final int OPCODE = 0x0077;
		public Reserved0077() { opcode = OPCODE; }
		public Reserved0077(Polygon poly) { opcode = OPCODE; this.poly = poly; }
	}
	
	public static class FrameSamePoly extends ImpliedInstruction {
		public static final int OPCODE = 0x0078;
		public FrameSamePoly() { opcode = OPCODE; }
	}
	
	public static class PaintSamePoly extends ImpliedInstruction {
		public static final int OPCODE = 0x0079;
		public PaintSamePoly() { opcode = OPCODE; }
	}
	
	public static class EraseSamePoly extends ImpliedInstruction {
		public static final int OPCODE = 0x007A;
		public EraseSamePoly() { opcode = OPCODE; }
	}
	
	public static class InvertSamePoly extends ImpliedInstruction {
		public static final int OPCODE = 0x007B;
		public InvertSamePoly() { opcode = OPCODE; }
	}
	
	public static class FillSamePoly extends ImpliedInstruction {
		public static final int OPCODE = 0x007C;
		public FillSamePoly() { opcode = OPCODE; }
	}
	
	public static class Reserved007D extends ImpliedInstruction {
		public static final int OPCODE = 0x007D;
		public Reserved007D() { opcode = OPCODE; }
	}
	
	public static class Reserved007E extends ImpliedInstruction {
		public static final int OPCODE = 0x007E;
		public Reserved007E() { opcode = OPCODE; }
	}
	
	public static class Reserved007F extends ImpliedInstruction {
		public static final int OPCODE = 0x007F;
		public Reserved007F() { opcode = OPCODE; }
	}
	
	public static class FrameRgn extends RegionInstruction {
		public static final int OPCODE = 0x0080;
		public FrameRgn() { opcode = OPCODE; }
		public FrameRgn(Region rgn) { opcode = OPCODE; this.rgn = rgn; }
	}
	
	public static class PaintRgn extends RegionInstruction {
		public static final int OPCODE = 0x0081;
		public PaintRgn() { opcode = OPCODE; }
		public PaintRgn(Region rgn) { opcode = OPCODE; this.rgn = rgn; }
	}
	
	public static class EraseRgn extends RegionInstruction {
		public static final int OPCODE = 0x0082;
		public EraseRgn() { opcode = OPCODE; }
		public EraseRgn(Region rgn) { opcode = OPCODE; this.rgn = rgn; }
	}
	
	public static class InvertRgn extends RegionInstruction {
		public static final int OPCODE = 0x0083;
		public InvertRgn() { opcode = OPCODE; }
		public InvertRgn(Region rgn) { opcode = OPCODE; this.rgn = rgn; }
	}
	
	public static class FillRgn extends RegionInstruction {
		public static final int OPCODE = 0x0084;
		public FillRgn() { opcode = OPCODE; }
		public FillRgn(Region rgn) { opcode = OPCODE; this.rgn = rgn; }
	}
	
	public static class Reserved0085 extends RegionInstruction {
		public static final int OPCODE = 0x0085;
		public Reserved0085() { opcode = OPCODE; }
		public Reserved0085(Region rgn) { opcode = OPCODE; this.rgn = rgn; }
	}
	
	public static class Reserved0086 extends RegionInstruction {
		public static final int OPCODE = 0x0086;
		public Reserved0086() { opcode = OPCODE; }
		public Reserved0086(Region rgn) { opcode = OPCODE; this.rgn = rgn; }
	}
	
	public static class Reserved0087 extends RegionInstruction {
		public static final int OPCODE = 0x0087;
		public Reserved0087() { opcode = OPCODE; }
		public Reserved0087(Region rgn) { opcode = OPCODE; this.rgn = rgn; }
	}
	
	public static class FrameSameRgn extends ImpliedInstruction {
		public static final int OPCODE = 0x0088;
		public FrameSameRgn() { opcode = OPCODE; }
	}
	
	public static class PaintSameRgn extends ImpliedInstruction {
		public static final int OPCODE = 0x0089;
		public PaintSameRgn() { opcode = OPCODE; }
	}
	
	public static class EraseSameRgn extends ImpliedInstruction {
		public static final int OPCODE = 0x008A;
		public EraseSameRgn() { opcode = OPCODE; }
	}
	
	public static class InvertSameRgn extends ImpliedInstruction {
		public static final int OPCODE = 0x008B;
		public InvertSameRgn() { opcode = OPCODE; }
	}
	
	public static class FillSameRgn extends ImpliedInstruction {
		public static final int OPCODE = 0x008C;
		public FillSameRgn() { opcode = OPCODE; }
	}
	
	public static class Reserved008D extends ImpliedInstruction {
		public static final int OPCODE = 0x008D;
		public Reserved008D() { opcode = OPCODE; }
	}
	
	public static class Reserved008E extends ImpliedInstruction {
		public static final int OPCODE = 0x008E;
		public Reserved008E() { opcode = OPCODE; }
	}
	
	public static class Reserved008F extends ImpliedInstruction {
		public static final int OPCODE = 0x008F;
		public Reserved008F() { opcode = OPCODE; }
	}
	
	public static class BitsRect extends CopyBitsRectInstruction {
		public static final int OPCODE = 0x0090;
		public BitsRect() { opcode = OPCODE; }
		public BitsRect(PixMap pm, ColorTable ct, Rect src, Rect dst, int mode, byte[] data) { opcode = OPCODE; pixMap = pm; colorTable = ct; srcRect = src; dstRect = dst; this.mode = mode; pixData = data; }
	}
	
	public static class BitsRgn extends CopyBitsRegionInstruction {
		public static final int OPCODE = 0x0091;
		public BitsRgn() { opcode = OPCODE; }
		public BitsRgn(PixMap pm, ColorTable ct, Rect src, Rect dst, int mode, Region rgn, byte[] data) { opcode = OPCODE; pixMap = pm; colorTable = ct; srcRect = src; dstRect = dst; this.mode = mode; maskRgn = rgn; pixData = data; }
	}
	
	public static class Reserved0092 extends ShortDataInstruction {
		public static final int OPCODE = 0x0092;
		public Reserved0092() { opcode = OPCODE; }
		public Reserved0092(byte[] data) { opcode = OPCODE; this.data = data; }
	}
	
	public static class Reserved0093 extends ShortDataInstruction {
		public static final int OPCODE = 0x0093;
		public Reserved0093() { opcode = OPCODE; }
		public Reserved0093(byte[] data) { opcode = OPCODE; this.data = data; }
	}
	
	public static class Reserved0094 extends ShortDataInstruction {
		public static final int OPCODE = 0x0094;
		public Reserved0094() { opcode = OPCODE; }
		public Reserved0094(byte[] data) { opcode = OPCODE; this.data = data; }
	}
	
	public static class Reserved0095 extends ShortDataInstruction {
		public static final int OPCODE = 0x0095;
		public Reserved0095() { opcode = OPCODE; }
		public Reserved0095(byte[] data) { opcode = OPCODE; this.data = data; }
	}
	
	public static class Reserved0096 extends ShortDataInstruction {
		public static final int OPCODE = 0x0096;
		public Reserved0096() { opcode = OPCODE; }
		public Reserved0096(byte[] data) { opcode = OPCODE; this.data = data; }
	}
	
	public static class Reserved0097 extends ShortDataInstruction {
		public static final int OPCODE = 0x0097;
		public Reserved0097() { opcode = OPCODE; }
		public Reserved0097(byte[] data) { opcode = OPCODE; this.data = data; }
	}
	
	public static class PackBitsRect extends CopyBitsRectInstruction {
		public static final int OPCODE = 0x0098;
		public PackBitsRect() { opcode = OPCODE; }
		public PackBitsRect(PixMap pm, ColorTable ct, Rect src, Rect dst, int mode, byte[] data) { opcode = OPCODE; pixMap = pm; colorTable = ct; srcRect = src; dstRect = dst; this.mode = mode; pixData = data; }
	}
	
	public static class PackBitsRgn extends CopyBitsRegionInstruction {
		public static final int OPCODE = 0x0099;
		public PackBitsRgn() { opcode = OPCODE; }
		public PackBitsRgn(PixMap pm, ColorTable ct, Rect src, Rect dst, int mode, Region rgn, byte[] data) { opcode = OPCODE; pixMap = pm; colorTable = ct; srcRect = src; dstRect = dst; this.mode = mode; maskRgn = rgn; pixData = data; }
	}
	
	public static class DirectBitsRect extends DirectBitsRectInstruction {
		public static final int OPCODE = 0x009A;
		public DirectBitsRect() { opcode = OPCODE; }
		public DirectBitsRect(PixMap pm, Rect src, Rect dst, int mode, byte[] data) { opcode = OPCODE; pixMap = pm; srcRect = src; dstRect = dst; this.mode = mode; pixData = data; }
	}
	
	public static class DirectBitsRgn extends DirectBitsRegionInstruction {
		public static final int OPCODE = 0x009B;
		public DirectBitsRgn() { opcode = OPCODE; }
		public DirectBitsRgn(PixMap pm, Rect src, Rect dst, int mode, Region rgn, byte[] data) { opcode = OPCODE; pixMap = pm; srcRect = src; dstRect = dst; this.mode = mode; maskRgn = rgn; pixData = data; }
	}
	
	public static class Reserved009C extends ShortDataInstruction {
		public static final int OPCODE = 0x009C;
		public Reserved009C() { opcode = OPCODE; }
		public Reserved009C(byte[] data) { opcode = OPCODE; this.data = data; }
	}
	
	public static class Reserved009D extends ShortDataInstruction {
		public static final int OPCODE = 0x009D;
		public Reserved009D() { opcode = OPCODE; }
		public Reserved009D(byte[] data) { opcode = OPCODE; this.data = data; }
	}
	
	public static class Reserved009E extends ShortDataInstruction {
		public static final int OPCODE = 0x009E;
		public Reserved009E() { opcode = OPCODE; }
		public Reserved009E(byte[] data) { opcode = OPCODE; this.data = data; }
	}
	
	public static class Reserved009F extends ShortDataInstruction {
		public static final int OPCODE = 0x009F;
		public Reserved009F() { opcode = OPCODE; }
		public Reserved009F(byte[] data) { opcode = OPCODE; this.data = data; }
	}
	
	public static class ShortComment extends PICTInstruction implements CommentConstants {
		public static final int OPCODE = 0x00A0;
		public int kind;
		public ShortComment() { opcode = OPCODE; }
		public ShortComment(int kind) { opcode = OPCODE; this.kind = kind; }
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			kind = in.readShort();
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			out.writeShort(kind);
		}
		protected String toStringImpl() {
			return commentToString(kind);
		}
	}
	
	public static class LongComment extends PICTInstruction implements CommentConstants {
		public static final int OPCODE = 0x00A1;
		public int kind;
		public int appID;
		public int ppcID;
		public byte[] data;
		public LongComment() { opcode = OPCODE; }
		public LongComment(int kind, byte[] data) { opcode = OPCODE; this.kind = kind; this.appID = 0; this.ppcID = 0; this.data = data; }
		public LongComment(int kind, int appID, byte[] data) { opcode = OPCODE; this.kind = kind; this.appID = appID; this.ppcID = 0; this.data = data; }
		public LongComment(int kind, int appID, int ppcID, byte[] data) { opcode = OPCODE; this.kind = kind; this.appID = appID; this.ppcID = ppcID; this.data = data; }
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			kind = in.readShort();
			int size = in.readUnsignedShort();
			if (kind == APPLICATION_COMMENT) {
				appID = in.readInt(); size -= 4;
				if (appID == APPLICATION_POWERPAINT) {
					ppcID = in.readInt(); size -= 4;
				} else {
					ppcID = 0;
				}
			} else {
				appID = 0;
				ppcID = 0;
			}
			data = new byte[size];
			in.readFully(data);
			if (v2 & (data.length & 1) == 1) in.readByte();
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			out.writeShort(kind);
			if (kind == APPLICATION_COMMENT) {
				if (appID == APPLICATION_POWERPAINT) {
					out.writeShort(8+data.length);
					out.writeInt(appID);
					out.writeInt(ppcID);
				} else {
					out.writeShort(4+data.length);
					out.writeInt(appID);
				}
			} else {
				out.writeShort(data.length);
			}
			out.write(data);
			if (v2 & (data.length & 1) == 1) out.writeByte(0);
		}
		protected String toStringImpl() {
			StringBuffer sb = new StringBuffer();
			sb.append(commentToString(kind));
			if (kind == APPLICATION_COMMENT) {
				sb.append(" "+osTypeToString(appID));
				if (appID == APPLICATION_POWERPAINT) {
					sb.append(" "+osTypeToString(ppcID));
				}
			}
			if (data.length <= 64) {
				sb.append(" ");
				for (byte b : data) {
					String h = "00" + Integer.toHexString(b).toUpperCase();
					sb.append(h.substring(h.length()-2));
				}
			} else {
				sb.append(" Data["+data.length+"]");
			}
			return sb.toString();
		}
	}
	
	public static class Reserved00A2to00AF extends ShortDataInstruction {
		public static final int OPCODE_MIN = 0x00A2;
		public static final int OPCODE_MAX = 0x00AF;
		public Reserved00A2to00AF() { opcode = OPCODE_MIN; }
		public Reserved00A2to00AF(int opcode, byte[] data) { this.opcode = opcode; this.data = data; }
	}
	
	public static class Reserved00B0to00CF extends ImpliedInstruction {
		public static final int OPCODE_MIN = 0x00B0;
		public static final int OPCODE_MAX = 0x00CF;
		public Reserved00B0to00CF() { opcode = OPCODE_MIN; }
		public Reserved00B0to00CF(int opcode) { this.opcode = opcode; }
	}
	
	public static class Reserved00D0to00FF extends LongDataInstruction {
		public static final int OPCODE_MIN = 0x00D0;
		public static final int OPCODE_MAX = 0x00FF;
		public Reserved00D0to00FF() { opcode = OPCODE_MIN; }
		public Reserved00D0to00FF(int opcode, byte[] data) { this.opcode = opcode; this.data = data; }
	}
	
	public static class OpEndPic extends ImpliedInstruction {
		public static final int OPCODE = 0x00FF;
		public OpEndPic() { opcode = OPCODE; }
	}
	
	public static class Reserved0100to7FFF extends FixedDataInstruction {
		public static final int OPCODE_MIN = 0x0100;
		public static final int OPCODE_MAX = 0x7FFF;
		public Reserved0100to7FFF() { opcode = OPCODE_MIN; }
		public Reserved0100to7FFF(int opcode, byte[] data) { this.opcode = opcode; this.data = data; }
	}
	
	public static class HeaderOp extends PICTInstruction {
		public static final int OPCODE = 0x0C00;
		public int version;
		public int reserved1;
		public float hRes;
		public float vRes;
		public Rectangle2D srcRect;
		public int reserved2;
		public HeaderOp() { opcode = OPCODE; }
		public HeaderOp(int v, float hRes, float vRes, Rectangle2D rect) { opcode = OPCODE; version = v; reserved1 = 0; this.hRes = hRes; this.vRes = vRes; srcRect = rect; reserved2 = 0; }
		protected void readImpl(DataInputStream in, boolean v2) throws IOException {
			version = in.readShort();
			if (version <= -2) {
				// extended version 2
				reserved1 = in.readShort();
				hRes = in.readInt() / 65536.0f;
				vRes = in.readInt() / 65536.0f;
				int top = in.readShort();
				int left = in.readShort();
				int bottom = in.readShort();
				int right = in.readShort();
				srcRect = new Rectangle(left, top, right-left, bottom-top);
				reserved2 = in.readInt();
			} else {
				// version 2
				version = ((version & 0xFFFF) << 16) | (in.readShort() & 0xFFFF);
				reserved1 = 0;
				hRes = 72;
				vRes = 72;
				float left = in.readInt() / 65536.0f;
				float top = in.readInt() / 65536.0f;
				float right = in.readInt() / 65536.0f;
				float bottom = in.readInt() / 65536.0f;
				srcRect = new Rectangle2D.Float(left, top, right-left, bottom-top);
				reserved2 = in.readInt();
			}
		}
		protected void writeImpl(DataOutputStream out, boolean v2) throws IOException {
			if (version <= -2) {
				// extended version 2
				out.writeShort(version);
				out.writeShort(reserved1);
				out.writeInt((int)(hRes * 65536.0f));
				out.writeInt((int)(vRes * 65536.0f));
				out.writeShort((short)srcRect.getMinY());
				out.writeShort((short)srcRect.getMinX());
				out.writeShort((short)srcRect.getMaxY());
				out.writeShort((short)srcRect.getMaxX());
				out.writeInt(reserved2);
			} else {
				// version 2
				out.writeInt(version);
				out.writeInt((int)(srcRect.getMinY() * 65536.0f));
				out.writeInt((int)(srcRect.getMinX() * 65536.0f));
				out.writeInt((int)(srcRect.getMaxY() * 65536.0f));
				out.writeInt((int)(srcRect.getMaxX() * 65536.0f));
				out.writeInt(reserved2);
			}
		}
		protected String toStringImpl() {
			return version + " " + hRes + "*" + vRes + " " + srcRect.getMinX() + "," + srcRect.getMinY() + "," + srcRect.getMaxX() + "," + srcRect.getMaxY();
		}
	}
	
	public static class Reserved8000to80FF extends ImpliedInstruction {
		public static final int OPCODE_MIN = 0x8000;
		public static final int OPCODE_MAX = 0x80FF;
		public Reserved8000to80FF() { opcode = OPCODE_MIN; }
		public Reserved8000to80FF(int opcode) { this.opcode = opcode; }
	}
	
	public static class Reserved8100toFFFF extends LongDataInstruction {
		public static final int OPCODE_MIN = 0x8100;
		public static final int OPCODE_MAX = 0xFFFF;
		public Reserved8100toFFFF() { opcode = OPCODE_MIN; }
		public Reserved8100toFFFF(int opcode, byte[] data) { this.opcode = opcode; this.data = data; }
	}
	
	public static class CompressedQuickTime extends LongDataInstruction {
		public static final int OPCODE = 0x8200;
		public CompressedQuickTime() { opcode = OPCODE; }
		public CompressedQuickTime(byte[] data) { opcode = OPCODE; this.data = data; }
	}
	
	public static class UncompressedQuickTime extends LongDataInstruction {
		public static final int OPCODE = 0x8201;
		public UncompressedQuickTime() { opcode = OPCODE; }
		public UncompressedQuickTime(byte[] data) { opcode = OPCODE; this.data = data; }
	}
}
