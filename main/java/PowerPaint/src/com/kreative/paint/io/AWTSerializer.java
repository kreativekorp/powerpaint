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

package com.kreative.paint.io;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.zip.*;

/* This and ShapeSerializer more or less cover the java.awt package. */
/* This more or less covers the java.awt.font package. */
public class AWTSerializer extends Serializer {
	private static final int TYPE_ALPHA_COMPOSITE = fcc("AlCx");
	private static final int TYPE_BASIC_STROKE = fcc("BStk");
	private static final int TYPE_BUFFERED_IMAGE = fcc("BImg");
	private static final int TYPE_COLOR = fcc("Colr");
	private static final int TYPE_FONT = fcc("Font");
	private static final int TYPE_GRADIENT_PAINT = fcc("GPnt");
	//private static final int TYPE_IMAGE_GRAPHIC_ATTRIBUTE = fcc("IGAt");
	private static final int TYPE_INSETS = fcc("Inse");
	private static final int TYPE_NUMERIC_SHAPER = fcc("NmSh");
	private static final int TYPE_RENDERING_HINTS = fcc("RdHt");
	//private static final int TYPE_SHAPE_GRAPHIC_ATTRIBUTE = fcc("SGAt");
	private static final int TYPE_SYSTEM_COLOR = fcc("SClr");
	private static final int TYPE_TEXTURE_PAINT = fcc("TPnt");
	private static final int TYPE_TRANSFORM_ATTRIBUTE = fcc("TxAt");
	
	private static final int BI_TYPE_3BYTE_BGR = fcc("3bgr");
	private static final int BI_TYPE_4BYTE_ABGR = fcc("4abg");
	private static final int BI_TYPE_4BYTE_ABGR_PRE = fcc("4abp");
	private static final int BI_TYPE_BYTE_BINARY = fcc("bbin");
	private static final int BI_TYPE_BYTE_GRAY = fcc("bgry");
	private static final int BI_TYPE_BYTE_INDEXED = fcc("bidx");
	private static final int BI_TYPE_CUSTOM = fcc("cust");
	private static final int BI_TYPE_INT_ARGB = fcc("argb");
	private static final int BI_TYPE_INT_ARGB_PRE = fcc("argp");
	private static final int BI_TYPE_INT_BGR = fcc("ibgr");
	private static final int BI_TYPE_INT_RGB = fcc("irgb");
	private static final int BI_TYPE_USHORT_555_RGB = fcc("u555");
	private static final int BI_TYPE_USHORT_565_RGB = fcc("u565");
	private static final int BI_TYPE_USHORT_GRAY = fcc("ugry");
	
	private static final int KEY_ALPHA_INTERPOLATION = fcc("aint");
	private static final int KEY_ANTIALIASING = fcc("aals");
	private static final int KEY_COLOR_RENDERING = fcc("cren");
	private static final int KEY_DITHERING = fcc("dith");
	private static final int KEY_FRACTIONALMETRICS = fcc("fmtx");
	private static final int KEY_INTERPOLATION = fcc("inte");
	private static final int KEY_RENDERING = fcc("rndr");
	private static final int KEY_STROKE_CONTROL = fcc("stct");
	private static final int KEY_TEXT_ANTIALIASING = fcc("taal");
	
	private static final int VALUE_ALPHA_INTERPOLATION_DEFAULT = fcc("aidf");
	private static final int VALUE_ALPHA_INTERPOLATION_QUALITY = fcc("aiql");
	private static final int VALUE_ALPHA_INTERPOLATION_SPEED = fcc("aisp");
	private static final int VALUE_ANTIALIAS_DEFAULT = fcc("aadf");
	private static final int VALUE_ANTIALIAS_OFF = fcc("aaof");
	private static final int VALUE_ANTIALIAS_ON = fcc("aaon");
	private static final int VALUE_COLOR_RENDER_DEFAULT = fcc("crdf");
	private static final int VALUE_COLOR_RENDER_QUALITY = fcc("crql");
	private static final int VALUE_COLOR_RENDER_SPEED = fcc("crsp");
	private static final int VALUE_DITHER_DEFAULT = fcc("didf");
	private static final int VALUE_DITHER_DISABLE = fcc("didi");
	private static final int VALUE_DITHER_ENABLE = fcc("dien");
	private static final int VALUE_FRACTIONALMETRICS_DEFAULT = fcc("fmdf");
	private static final int VALUE_FRACTIONALMETRICS_OFF = fcc("fmof");
	private static final int VALUE_FRACTIONALMETRICS_ON = fcc("fmon");
	private static final int VALUE_INTERPOLATION_BICUBIC = fcc("inbc");
	private static final int VALUE_INTERPOLATION_BILINEAR = fcc("inbl");
	private static final int VALUE_INTERPOLATION_NEAREST_NEIGHBOR = fcc("innn");
	private static final int VALUE_RENDER_DEFAULT = fcc("rndf");
	private static final int VALUE_RENDER_QUALITY = fcc("rnql");
	private static final int VALUE_RENDER_SPEED = fcc("rnsp");
	private static final int VALUE_STROKE_DEFAULT = fcc("stdf");
	private static final int VALUE_STROKE_NORMALIZE = fcc("stnm");
	private static final int VALUE_STROKE_PURE = fcc("stpr");
	private static final int VALUE_TEXT_ANTIALIAS_DEFAULT = fcc("tadf");
	private static final int VALUE_TEXT_ANTIALIAS_OFF = fcc("taof");
	private static final int VALUE_TEXT_ANTIALIAS_ON = fcc("taon");
	
	private static final int SYSTEM_COLOR_ACTIVECAPTION = fcc("acap");
	private static final int SYSTEM_COLOR_ACTIVECAPTIONBORDER = fcc("acbd");
	private static final int SYSTEM_COLOR_ACTIVECAPTIONTEXT = fcc("actx");
	private static final int SYSTEM_COLOR_CONTROL = fcc("ctrl");
	private static final int SYSTEM_COLOR_CONTROLDKSHADOW = fcc("cdks");
	private static final int SYSTEM_COLOR_CONTROLHIGHLIGHT = fcc("chlt");
	private static final int SYSTEM_COLOR_CONTROLLTHIGHLIGHT = fcc("clth");
	private static final int SYSTEM_COLOR_CONTROLSHADOW = fcc("csdw");
	private static final int SYSTEM_COLOR_CONTROLTEXT = fcc("ctxt");
	private static final int SYSTEM_COLOR_DESKTOP = fcc("dktp");
	private static final int SYSTEM_COLOR_INACTIVECAPTION = fcc("icap");
	private static final int SYSTEM_COLOR_INACTIVECAPTIONBORDER = fcc("icbd");
	private static final int SYSTEM_COLOR_INACTIVECAPTIONTEXT = fcc("ictx");
	private static final int SYSTEM_COLOR_INFO = fcc("info");
	private static final int SYSTEM_COLOR_INFOTEXT = fcc("itxt");
	private static final int SYSTEM_COLOR_MENU = fcc("menu");
	private static final int SYSTEM_COLOR_MENUTEXT = fcc("mtxt");
	private static final int SYSTEM_COLOR_SCROLLBAR = fcc("sbar");
	private static final int SYSTEM_COLOR_TEXT = fcc("text");
	private static final int SYSTEM_COLOR_TEXTHIGHLIGHT = fcc("thlt");
	private static final int SYSTEM_COLOR_TEXTHIGHLIGHTTEXT = fcc("thtx");
	private static final int SYSTEM_COLOR_TEXTINACTIVETEXT = fcc("titx");
	private static final int SYSTEM_COLOR_TEXTTEXT = fcc("ttxt");
	private static final int SYSTEM_COLOR_WINDOW = fcc("wind");
	private static final int SYSTEM_COLOR_WINDOWBORDER = fcc("wbdr");
	private static final int SYSTEM_COLOR_WINDOWTEXT = fcc("wtxt");
	
	private static final int TEXT_ATTRIBUTE_BACKGROUND = fcc("bkgd");
	private static final int TEXT_ATTRIBUTE_BIDI_EMBEDDING = fcc("bidi");
	private static final int TEXT_ATTRIBUTE_CHAR_REPLACEMENT = fcc("chrp");
	private static final int TEXT_ATTRIBUTE_FAMILY = fcc("ffam");
	private static final int TEXT_ATTRIBUTE_FONT = fcc("font");
	private static final int TEXT_ATTRIBUTE_FOREGROUND = fcc("frgd");
	private static final int TEXT_ATTRIBUTE_INPUT_METHOD_HIGHLIGHT = fcc("imhl");
	private static final int TEXT_ATTRIBUTE_INPUT_METHOD_UNDERLINE = fcc("imul");
	private static final int TEXT_ATTRIBUTE_JUSTIFICATION = fcc("jstf");
	private static final int TEXT_ATTRIBUTE_NUMERIC_SHAPING = fcc("nmsh");
	private static final int TEXT_ATTRIBUTE_POSTURE = fcc("pstr");
	private static final int TEXT_ATTRIBUTE_RUN_DIRECTION = fcc("rdir");
	private static final int TEXT_ATTRIBUTE_SIZE = fcc("fsiz");
	private static final int TEXT_ATTRIBUTE_STRIKETHROUGH = fcc("strk");
	private static final int TEXT_ATTRIBUTE_SUPERSCRIPT = fcc("supr");
	private static final int TEXT_ATTRIBUTE_SWAP_COLORS = fcc("swap");
	private static final int TEXT_ATTRIBUTE_TRANSFORM = fcc("trfm");
	private static final int TEXT_ATTRIBUTE_UNDERLINE = fcc("undl");
	private static final int TEXT_ATTRIBUTE_WEIGHT = fcc("wght");
	private static final int TEXT_ATTRIBUTE_WIDTH = fcc("wdth");
	
	protected void loadRecognizedTypesAndClasses() {
		addTypeAndClass(TYPE_ALPHA_COMPOSITE, 1, AlphaComposite.class);
		addTypeAndClass(TYPE_BASIC_STROKE, 1, BasicStroke.class);
		addTypeAndClass(TYPE_BUFFERED_IMAGE, 1, BufferedImage.class);
		addTypeAndClass(TYPE_COLOR, 1, Color.class);
		addTypeAndClass(TYPE_FONT, 1, Font.class);
		addTypeAndClass(TYPE_GRADIENT_PAINT, 1, GradientPaint.class);
		//addTypeAndClass(TYPE_IMAGE_GRAPHIC_ATTRIBUTE, 1, ImageGraphicAttribute.class);
		addTypeAndClass(TYPE_INSETS, 1, Insets.class);
		addTypeAndClass(TYPE_NUMERIC_SHAPER, 1, NumericShaper.class);
		addTypeAndClass(TYPE_RENDERING_HINTS, 1, RenderingHints.class);
		//addTypeAndClass(TYPE_SHAPE_GRAPHIC_ATTRIBUTE, 1, ShapeGraphicAttribute.class);
		addTypeAndClass(TYPE_SYSTEM_COLOR, 1, SystemColor.class);
		addTypeAndClass(TYPE_TEXTURE_PAINT, 1, TexturePaint.class);
		addTypeAndClass(TYPE_TRANSFORM_ATTRIBUTE, 1, TransformAttribute.class);
	}
	
	public void serializeObject(Object o, DataOutputStream stream) throws IOException {
		if (o instanceof AlphaComposite) {
			AlphaComposite v = (AlphaComposite)o;
			stream.writeInt(v.getRule());
			stream.writeFloat(v.getAlpha());
		}
		else if (o instanceof BasicStroke) {
			BasicStroke v = (BasicStroke)o;
			stream.writeFloat(v.getLineWidth());
			stream.writeInt(v.getEndCap());
			stream.writeInt(v.getLineJoin());
			stream.writeFloat(v.getMiterLimit());
			stream.writeFloat(v.getDashPhase());
			float[] dash = v.getDashArray();
			if (dash == null) stream.writeInt(-1);
			else {
				stream.writeInt(dash.length);
				for (int i = 0; i < dash.length; i++) {
					stream.writeFloat(dash[i]);
				}
			}
		}
		else if (o instanceof BufferedImage) {
			BufferedImage v = (BufferedImage)o;
			stream.writeInt(v.getWidth());
			stream.writeInt(v.getHeight());
			if (v.getType() == BufferedImage.TYPE_3BYTE_BGR) stream.writeInt(BI_TYPE_3BYTE_BGR);
			else if (v.getType() == BufferedImage.TYPE_4BYTE_ABGR) stream.writeInt(BI_TYPE_4BYTE_ABGR);
			else if (v.getType() == BufferedImage.TYPE_4BYTE_ABGR_PRE) stream.writeInt(BI_TYPE_4BYTE_ABGR_PRE);
			else if (v.getType() == BufferedImage.TYPE_BYTE_BINARY) stream.writeInt(BI_TYPE_BYTE_BINARY);
			else if (v.getType() == BufferedImage.TYPE_BYTE_GRAY) stream.writeInt(BI_TYPE_BYTE_GRAY);
			else if (v.getType() == BufferedImage.TYPE_BYTE_INDEXED) stream.writeInt(BI_TYPE_BYTE_INDEXED);
			else if (v.getType() == BufferedImage.TYPE_CUSTOM) stream.writeInt(BI_TYPE_CUSTOM);
			else if (v.getType() == BufferedImage.TYPE_INT_ARGB) stream.writeInt(BI_TYPE_INT_ARGB);
			else if (v.getType() == BufferedImage.TYPE_INT_ARGB_PRE) stream.writeInt(BI_TYPE_INT_ARGB_PRE);
			else if (v.getType() == BufferedImage.TYPE_INT_BGR) stream.writeInt(BI_TYPE_INT_BGR);
			else if (v.getType() == BufferedImage.TYPE_INT_RGB) stream.writeInt(BI_TYPE_INT_RGB);
			else if (v.getType() == BufferedImage.TYPE_USHORT_555_RGB) stream.writeInt(BI_TYPE_USHORT_555_RGB);
			else if (v.getType() == BufferedImage.TYPE_USHORT_565_RGB) stream.writeInt(BI_TYPE_USHORT_565_RGB);
			else if (v.getType() == BufferedImage.TYPE_USHORT_GRAY) stream.writeInt(BI_TYPE_USHORT_GRAY);
			else stream.writeInt(0x3F3F3F3F);
			int[] rgb = new int[v.getWidth()*v.getHeight()];
			v.getRGB(0, 0, v.getWidth(), v.getHeight(), rgb, 0, v.getWidth());
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DeflaterOutputStream dos = new DeflaterOutputStream(bos);
			for (int p : rgb) dos.write((p >> 24) & 0xFF);
			for (int p : rgb) dos.write((p >> 16) & 0xFF);
			for (int p : rgb) dos.write((p >> 8) & 0xFF);
			for (int p : rgb) dos.write(p & 0xFF);
			dos.finish();
			dos.close();
			bos.close();
			byte crgb[] = bos.toByteArray();
			stream.writeInt(crgb.length);
			stream.write(crgb);
		}
		else if (o instanceof Color) {
			Color v = (Color)o;
			float[] rgb = v.getRGBComponents(null);
			stream.writeFloat(rgb[0]);
			stream.writeFloat(rgb[1]);
			stream.writeFloat(rgb[2]);
			stream.writeFloat(rgb[3]);
		}
		else if (o instanceof Font) {
			Font v = (Font)o;
			Map<TextAttribute,?> att = v.getAttributes();
			stream.writeInt(att.size());
			for (Map.Entry<TextAttribute, ?> e : att.entrySet()) {
				if (e.getKey() == TextAttribute.BACKGROUND) stream.writeInt(TEXT_ATTRIBUTE_BACKGROUND);
				else if (e.getKey() == TextAttribute.BIDI_EMBEDDING) stream.writeInt(TEXT_ATTRIBUTE_BIDI_EMBEDDING);
				else if (e.getKey() == TextAttribute.CHAR_REPLACEMENT) stream.writeInt(TEXT_ATTRIBUTE_CHAR_REPLACEMENT);
				else if (e.getKey() == TextAttribute.FAMILY) stream.writeInt(TEXT_ATTRIBUTE_FAMILY);
				else if (e.getKey() == TextAttribute.FONT) stream.writeInt(TEXT_ATTRIBUTE_FONT);
				else if (e.getKey() == TextAttribute.FOREGROUND) stream.writeInt(TEXT_ATTRIBUTE_FOREGROUND);
				else if (e.getKey() == TextAttribute.INPUT_METHOD_HIGHLIGHT) stream.writeInt(TEXT_ATTRIBUTE_INPUT_METHOD_HIGHLIGHT);
				else if (e.getKey() == TextAttribute.INPUT_METHOD_UNDERLINE) stream.writeInt(TEXT_ATTRIBUTE_INPUT_METHOD_UNDERLINE);
				else if (e.getKey() == TextAttribute.JUSTIFICATION) stream.writeInt(TEXT_ATTRIBUTE_JUSTIFICATION);
				else if (e.getKey() == TextAttribute.NUMERIC_SHAPING) stream.writeInt(TEXT_ATTRIBUTE_NUMERIC_SHAPING);
				else if (e.getKey() == TextAttribute.POSTURE) stream.writeInt(TEXT_ATTRIBUTE_POSTURE);
				else if (e.getKey() == TextAttribute.RUN_DIRECTION) stream.writeInt(TEXT_ATTRIBUTE_RUN_DIRECTION);
				else if (e.getKey() == TextAttribute.SIZE) stream.writeInt(TEXT_ATTRIBUTE_SIZE);
				else if (e.getKey() == TextAttribute.STRIKETHROUGH) stream.writeInt(TEXT_ATTRIBUTE_STRIKETHROUGH);
				else if (e.getKey() == TextAttribute.SUPERSCRIPT) stream.writeInt(TEXT_ATTRIBUTE_SUPERSCRIPT);
				else if (e.getKey() == TextAttribute.SWAP_COLORS) stream.writeInt(TEXT_ATTRIBUTE_SWAP_COLORS);
				else if (e.getKey() == TextAttribute.TRANSFORM) stream.writeInt(TEXT_ATTRIBUTE_TRANSFORM);
				else if (e.getKey() == TextAttribute.UNDERLINE) stream.writeInt(TEXT_ATTRIBUTE_UNDERLINE);
				else if (e.getKey() == TextAttribute.WEIGHT) stream.writeInt(TEXT_ATTRIBUTE_WEIGHT);
				else if (e.getKey() == TextAttribute.WIDTH) stream.writeInt(TEXT_ATTRIBUTE_WIDTH);
				else stream.writeInt(0x3F3F3F3F);
				if (e.getValue() instanceof Font) SerializationManager.writeObject(null, stream);
				else SerializationManager.writeObject(e.getValue(), stream);
			}
		}
		else if (o instanceof GradientPaint) {
			GradientPaint v = (GradientPaint)o;
			SerializationManager.writeObject(v.getColor1(), stream);
			SerializationManager.writeObject(v.getColor2(), stream);
			SerializationManager.writeObject(v.getPoint1(), stream);
			SerializationManager.writeObject(v.getPoint2(), stream);
			stream.writeBoolean(v.isCyclic());
		}
		else if (o instanceof Insets) {
			Insets v = (Insets)o;
			stream.writeInt(v.top);
			stream.writeInt(v.left);
			stream.writeInt(v.bottom);
			stream.writeInt(v.right);
		}
		else if (o instanceof NumericShaper) {
			NumericShaper v = (NumericShaper)o;
			stream.writeInt(v.getRanges());
			stream.writeBoolean(v.isContextual());
		}
		else if (o instanceof RenderingHints) {
			RenderingHints v = (RenderingHints)o;
			stream.writeInt(v.size());
			for (Map.Entry<Object,Object> e : v.entrySet()) {
				if (e.getKey() == RenderingHints.KEY_ALPHA_INTERPOLATION) stream.writeInt(KEY_ALPHA_INTERPOLATION);
				else if (e.getKey() == RenderingHints.KEY_ANTIALIASING) stream.writeInt(KEY_ANTIALIASING);
				else if (e.getKey() == RenderingHints.KEY_COLOR_RENDERING) stream.writeInt(KEY_COLOR_RENDERING);
				else if (e.getKey() == RenderingHints.KEY_DITHERING) stream.writeInt(KEY_DITHERING);
				else if (e.getKey() == RenderingHints.KEY_FRACTIONALMETRICS) stream.writeInt(KEY_FRACTIONALMETRICS);
				else if (e.getKey() == RenderingHints.KEY_INTERPOLATION) stream.writeInt(KEY_INTERPOLATION);
				else if (e.getKey() == RenderingHints.KEY_RENDERING) stream.writeInt(KEY_RENDERING);
				else if (e.getKey() == RenderingHints.KEY_STROKE_CONTROL) stream.writeInt(KEY_STROKE_CONTROL);
				else if (e.getKey() == RenderingHints.KEY_TEXT_ANTIALIASING) stream.writeInt(KEY_TEXT_ANTIALIASING);
				else stream.writeInt(0x3F3F3F3F);
				if (e.getValue() == RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT) stream.writeInt(VALUE_ALPHA_INTERPOLATION_DEFAULT);
				else if (e.getValue() == RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY) stream.writeInt(VALUE_ALPHA_INTERPOLATION_QUALITY);
				else if (e.getValue() == RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED) stream.writeInt(VALUE_ALPHA_INTERPOLATION_SPEED);
				else if (e.getValue() == RenderingHints.VALUE_ANTIALIAS_DEFAULT) stream.writeInt(VALUE_ANTIALIAS_DEFAULT);
				else if (e.getValue() == RenderingHints.VALUE_ANTIALIAS_OFF) stream.writeInt(VALUE_ANTIALIAS_OFF);
				else if (e.getValue() == RenderingHints.VALUE_ANTIALIAS_ON) stream.writeInt(VALUE_ANTIALIAS_ON);
				else if (e.getValue() == RenderingHints.VALUE_COLOR_RENDER_DEFAULT) stream.writeInt(VALUE_COLOR_RENDER_DEFAULT);
				else if (e.getValue() == RenderingHints.VALUE_COLOR_RENDER_QUALITY) stream.writeInt(VALUE_COLOR_RENDER_QUALITY);
				else if (e.getValue() == RenderingHints.VALUE_COLOR_RENDER_SPEED) stream.writeInt(VALUE_COLOR_RENDER_SPEED);
				else if (e.getValue() == RenderingHints.VALUE_DITHER_DEFAULT) stream.writeInt(VALUE_DITHER_DEFAULT);
				else if (e.getValue() == RenderingHints.VALUE_DITHER_DISABLE) stream.writeInt(VALUE_DITHER_DISABLE);
				else if (e.getValue() == RenderingHints.VALUE_DITHER_ENABLE) stream.writeInt(VALUE_DITHER_ENABLE);
				else if (e.getValue() == RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT) stream.writeInt(VALUE_FRACTIONALMETRICS_DEFAULT);
				else if (e.getValue() == RenderingHints.VALUE_FRACTIONALMETRICS_OFF) stream.writeInt(VALUE_FRACTIONALMETRICS_OFF);
				else if (e.getValue() == RenderingHints.VALUE_FRACTIONALMETRICS_ON) stream.writeInt(VALUE_FRACTIONALMETRICS_ON);
				else if (e.getValue() == RenderingHints.VALUE_INTERPOLATION_BICUBIC) stream.writeInt(VALUE_INTERPOLATION_BICUBIC);
				else if (e.getValue() == RenderingHints.VALUE_INTERPOLATION_BILINEAR) stream.writeInt(VALUE_INTERPOLATION_BILINEAR);
				else if (e.getValue() == RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR) stream.writeInt(VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				else if (e.getValue() == RenderingHints.VALUE_RENDER_DEFAULT) stream.writeInt(VALUE_RENDER_DEFAULT);
				else if (e.getValue() == RenderingHints.VALUE_RENDER_QUALITY) stream.writeInt(VALUE_RENDER_QUALITY);
				else if (e.getValue() == RenderingHints.VALUE_RENDER_SPEED) stream.writeInt(VALUE_RENDER_SPEED);
				else if (e.getValue() == RenderingHints.VALUE_STROKE_DEFAULT) stream.writeInt(VALUE_STROKE_DEFAULT);
				else if (e.getValue() == RenderingHints.VALUE_STROKE_NORMALIZE) stream.writeInt(VALUE_STROKE_NORMALIZE);
				else if (e.getValue() == RenderingHints.VALUE_STROKE_PURE) stream.writeInt(VALUE_STROKE_PURE);
				else if (e.getValue() == RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT) stream.writeInt(VALUE_TEXT_ANTIALIAS_DEFAULT);
				else if (e.getValue() == RenderingHints.VALUE_TEXT_ANTIALIAS_OFF) stream.writeInt(VALUE_TEXT_ANTIALIAS_OFF);
				else if (e.getValue() == RenderingHints.VALUE_TEXT_ANTIALIAS_ON) stream.writeInt(VALUE_TEXT_ANTIALIAS_ON);
				else stream.writeInt(0x3F3F3F3F);
			}
		}
		else if (o instanceof SystemColor) {
			SystemColor v = (SystemColor)o;
			if (v == SystemColor.activeCaption) stream.writeInt(SYSTEM_COLOR_ACTIVECAPTION);
			else if (v == SystemColor.activeCaptionBorder) stream.writeInt(SYSTEM_COLOR_ACTIVECAPTIONBORDER);
			else if (v == SystemColor.activeCaptionText) stream.writeInt(SYSTEM_COLOR_ACTIVECAPTIONTEXT);
			else if (v == SystemColor.control) stream.writeInt(SYSTEM_COLOR_CONTROL);
			else if (v == SystemColor.controlDkShadow) stream.writeInt(SYSTEM_COLOR_CONTROLDKSHADOW);
			else if (v == SystemColor.controlHighlight) stream.writeInt(SYSTEM_COLOR_CONTROLHIGHLIGHT);
			else if (v == SystemColor.controlLtHighlight) stream.writeInt(SYSTEM_COLOR_CONTROLLTHIGHLIGHT);
			else if (v == SystemColor.controlShadow) stream.writeInt(SYSTEM_COLOR_CONTROLSHADOW);
			else if (v == SystemColor.controlText) stream.writeInt(SYSTEM_COLOR_CONTROLTEXT);
			else if (v == SystemColor.desktop) stream.writeInt(SYSTEM_COLOR_DESKTOP);
			else if (v == SystemColor.inactiveCaption) stream.writeInt(SYSTEM_COLOR_INACTIVECAPTION);
			else if (v == SystemColor.inactiveCaptionBorder) stream.writeInt(SYSTEM_COLOR_INACTIVECAPTIONBORDER);
			else if (v == SystemColor.inactiveCaptionText) stream.writeInt(SYSTEM_COLOR_INACTIVECAPTIONTEXT);
			else if (v == SystemColor.info) stream.writeInt(SYSTEM_COLOR_INFO);
			else if (v == SystemColor.infoText) stream.writeInt(SYSTEM_COLOR_INFOTEXT);
			else if (v == SystemColor.menu) stream.writeInt(SYSTEM_COLOR_MENU);
			else if (v == SystemColor.menuText) stream.writeInt(SYSTEM_COLOR_MENUTEXT);
			else if (v == SystemColor.scrollbar) stream.writeInt(SYSTEM_COLOR_SCROLLBAR);
			else if (v == SystemColor.text) stream.writeInt(SYSTEM_COLOR_TEXT);
			else if (v == SystemColor.textHighlight) stream.writeInt(SYSTEM_COLOR_TEXTHIGHLIGHT);
			else if (v == SystemColor.textHighlightText) stream.writeInt(SYSTEM_COLOR_TEXTHIGHLIGHTTEXT);
			else if (v == SystemColor.textInactiveText) stream.writeInt(SYSTEM_COLOR_TEXTINACTIVETEXT);
			else if (v == SystemColor.textText) stream.writeInt(SYSTEM_COLOR_TEXTTEXT);
			else if (v == SystemColor.window) stream.writeInt(SYSTEM_COLOR_WINDOW);
			else if (v == SystemColor.windowBorder) stream.writeInt(SYSTEM_COLOR_WINDOWBORDER);
			else if (v == SystemColor.windowText) stream.writeInt(SYSTEM_COLOR_WINDOWTEXT);
			else stream.writeInt(0x3F3F3F3F);
		}
		else if (o instanceof TexturePaint) {
			TexturePaint v = (TexturePaint)o;
			SerializationManager.writeObject(v.getAnchorRect(), stream);
			SerializationManager.writeObject(v.getImage(), stream);
		}
		else if (o instanceof TransformAttribute) {
			AffineTransform v = ((TransformAttribute)o).getTransform();
			double[] m = new double[6];
			v.getMatrix(m);
			for (int i = 0; i < 6; i++) {
				stream.writeDouble(m[i]);
			}
		}
	}
	
	public Object deserializeObject(int type, int version, DataInputStream stream) throws IOException {
		if (version != 1) throw new IOException("Invalid version number.");
		else if (type == TYPE_ALPHA_COMPOSITE) {
			int r = stream.readInt();
			float a = stream.readFloat();
			return AlphaComposite.getInstance(r,a);
		}
		else if (type == TYPE_BASIC_STROKE) {
			float lw = stream.readFloat();
			int ec = stream.readInt();
			int lj = stream.readInt();
			float ml = stream.readFloat();
			float dp = stream.readFloat();
			int n = stream.readInt();
			float[] dash;
			if (n < 0) {
				dash = null;
			} else {
				dash = new float[n];
				for (int i = 0; i < n; i++) {
					dash[i] = stream.readFloat();
				}
			}
			return new BasicStroke(lw, ec, lj, ml, dash, dp);
		}
		else if (type == TYPE_BUFFERED_IMAGE) {
			int w = stream.readInt();
			int h = stream.readInt();
			int t = stream.readInt();
			int bt;
			if (t == BI_TYPE_3BYTE_BGR) bt = BufferedImage.TYPE_3BYTE_BGR;
			else if (t == BI_TYPE_4BYTE_ABGR) bt = BufferedImage.TYPE_4BYTE_ABGR;
			else if (t == BI_TYPE_4BYTE_ABGR_PRE) bt = BufferedImage.TYPE_4BYTE_ABGR_PRE;
			else if (t == BI_TYPE_BYTE_BINARY) bt = BufferedImage.TYPE_BYTE_BINARY;
			else if (t == BI_TYPE_BYTE_GRAY) bt = BufferedImage.TYPE_BYTE_GRAY;
			else if (t == BI_TYPE_BYTE_INDEXED) bt = BufferedImage.TYPE_BYTE_INDEXED;
			else if (t == BI_TYPE_CUSTOM) bt = BufferedImage.TYPE_CUSTOM;
			else if (t == BI_TYPE_INT_ARGB) bt = BufferedImage.TYPE_INT_ARGB;
			else if (t == BI_TYPE_INT_ARGB_PRE) bt = BufferedImage.TYPE_INT_ARGB_PRE;
			else if (t == BI_TYPE_INT_BGR) bt = BufferedImage.TYPE_INT_BGR;
			else if (t == BI_TYPE_INT_RGB) bt = BufferedImage.TYPE_INT_RGB;
			else if (t == BI_TYPE_USHORT_555_RGB) bt = BufferedImage.TYPE_USHORT_555_RGB;
			else if (t == BI_TYPE_USHORT_565_RGB) bt = BufferedImage.TYPE_USHORT_565_RGB;
			else if (t == BI_TYPE_USHORT_GRAY) bt = BufferedImage.TYPE_USHORT_GRAY;
			else bt = BufferedImage.TYPE_INT_ARGB;
			int l = stream.readInt();
			byte[] crgb = new byte[l];
			stream.read(crgb);
			int[] rgb = new int[w*h];
			ByteArrayInputStream bis = new ByteArrayInputStream(crgb);
			InflaterInputStream iis = new InflaterInputStream(bis);
			for (int i = 0; i < rgb.length; i++) rgb[i] |= ((iis.read() << 24) & 0xFF000000);
			for (int i = 0; i < rgb.length; i++) rgb[i] |= ((iis.read() << 16) & 0x00FF0000);
			for (int i = 0; i < rgb.length; i++) rgb[i] |= ((iis.read() << 8) & 0xFF00);
			for (int i = 0; i < rgb.length; i++) rgb[i] |= (iis.read() & 0xFF);
			iis.close();
			bis.close();
			BufferedImage bi = new BufferedImage(w,h,bt);
			bi.setRGB(0, 0, w, h, rgb, 0, w);
			return bi;
		}
		else if (type == TYPE_COLOR) {
			float r = stream.readFloat();
			float g = stream.readFloat();
			float b = stream.readFloat();
			float a = stream.readFloat();
			return new Color(r,g,b,a);
		}
		else if (type == TYPE_FONT) {
			int n = stream.readInt();
			Map<TextAttribute,Object> att = new HashMap<TextAttribute,Object>();
			for (int i = 0; i < n; i++) {
				int k = stream.readInt();
				Object v = SerializationManager.readObject(stream);
				TextAttribute ka;
				if (k == TEXT_ATTRIBUTE_BACKGROUND) ka = TextAttribute.BACKGROUND;
				else if (k == TEXT_ATTRIBUTE_BIDI_EMBEDDING) ka = TextAttribute.BIDI_EMBEDDING;
				else if (k == TEXT_ATTRIBUTE_CHAR_REPLACEMENT) ka = TextAttribute.CHAR_REPLACEMENT;
				else if (k == TEXT_ATTRIBUTE_FAMILY) ka = TextAttribute.FAMILY;
				else if (k == TEXT_ATTRIBUTE_FONT) ka = TextAttribute.FONT;
				else if (k == TEXT_ATTRIBUTE_FOREGROUND) ka = TextAttribute.FOREGROUND;
				else if (k == TEXT_ATTRIBUTE_INPUT_METHOD_HIGHLIGHT) ka = TextAttribute.INPUT_METHOD_HIGHLIGHT;
				else if (k == TEXT_ATTRIBUTE_INPUT_METHOD_UNDERLINE) ka = TextAttribute.INPUT_METHOD_UNDERLINE;
				else if (k == TEXT_ATTRIBUTE_JUSTIFICATION) ka = TextAttribute.JUSTIFICATION;
				else if (k == TEXT_ATTRIBUTE_NUMERIC_SHAPING) ka = TextAttribute.NUMERIC_SHAPING;
				else if (k == TEXT_ATTRIBUTE_POSTURE) ka = TextAttribute.POSTURE;
				else if (k == TEXT_ATTRIBUTE_RUN_DIRECTION) ka = TextAttribute.RUN_DIRECTION;
				else if (k == TEXT_ATTRIBUTE_SIZE) ka = TextAttribute.SIZE;
				else if (k == TEXT_ATTRIBUTE_STRIKETHROUGH) ka = TextAttribute.STRIKETHROUGH;
				else if (k == TEXT_ATTRIBUTE_SUPERSCRIPT) ka = TextAttribute.SUPERSCRIPT;
				else if (k == TEXT_ATTRIBUTE_SWAP_COLORS) ka = TextAttribute.SWAP_COLORS;
				else if (k == TEXT_ATTRIBUTE_TRANSFORM) ka = TextAttribute.TRANSFORM;
				else if (k == TEXT_ATTRIBUTE_UNDERLINE) ka = TextAttribute.UNDERLINE;
				else if (k == TEXT_ATTRIBUTE_WEIGHT) ka = TextAttribute.WEIGHT;
				else if (k == TEXT_ATTRIBUTE_WIDTH) ka = TextAttribute.WIDTH;
				else continue;
				att.put(ka, v);
			}
			try {
				return new Font(att);
			} catch (NullPointerException npe) {
				return new Font("SansSerif", Font.PLAIN, 12);
			}
		}
		else if (type == TYPE_GRADIENT_PAINT) {
			Color c1 = (Color)SerializationManager.readObject(stream);
			Color c2 = (Color)SerializationManager.readObject(stream);
			Point2D p1 = (Point2D)SerializationManager.readObject(stream);
			Point2D p2 = (Point2D)SerializationManager.readObject(stream);
			boolean c = stream.readBoolean();
			return new GradientPaint(p1,c1,p2,c2,c);
		}
		else if (type == TYPE_INSETS) {
			int t = stream.readInt();
			int l = stream.readInt();
			int b = stream.readInt();
			int r = stream.readInt();
			return new Insets(t,l,b,r);
		}
		else if (type == TYPE_NUMERIC_SHAPER) {
			int r = stream.readInt();
			boolean c = stream.readBoolean();
			if (c) return NumericShaper.getContextualShaper(r);
			else return NumericShaper.getShaper(r);
		}
		else if (type == TYPE_RENDERING_HINTS) {
			RenderingHints rh = new RenderingHints(null);
			int n = stream.readInt();
			for (int i = 0; i < n; i++) {
				int k = stream.readInt();
				int v = stream.readInt();
				Object ko;
				Object vo;
				if (k == KEY_ALPHA_INTERPOLATION) ko = RenderingHints.KEY_ALPHA_INTERPOLATION;
				else if (k == KEY_ANTIALIASING) ko = RenderingHints.KEY_ANTIALIASING;
				else if (k == KEY_COLOR_RENDERING) ko = RenderingHints.KEY_COLOR_RENDERING;
				else if (k == KEY_DITHERING) ko = RenderingHints.KEY_DITHERING;
				else if (k == KEY_FRACTIONALMETRICS) ko = RenderingHints.KEY_FRACTIONALMETRICS;
				else if (k == KEY_INTERPOLATION) ko = RenderingHints.KEY_INTERPOLATION;
				else if (k == KEY_RENDERING) ko = RenderingHints.KEY_RENDERING;
				else if (k == KEY_STROKE_CONTROL) ko = RenderingHints.KEY_STROKE_CONTROL;
				else if (k == KEY_TEXT_ANTIALIASING) ko = RenderingHints.KEY_TEXT_ANTIALIASING;
				else continue;
				if (v == VALUE_ALPHA_INTERPOLATION_DEFAULT) vo = RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT;
				else if (v == VALUE_ALPHA_INTERPOLATION_QUALITY) vo = RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY;
				else if (v == VALUE_ALPHA_INTERPOLATION_SPEED) vo = RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED;
				else if (v == VALUE_ANTIALIAS_DEFAULT) vo = RenderingHints.VALUE_ANTIALIAS_DEFAULT;
				else if (v == VALUE_ANTIALIAS_OFF) vo = RenderingHints.VALUE_ANTIALIAS_OFF;
				else if (v == VALUE_ANTIALIAS_ON) vo = RenderingHints.VALUE_ANTIALIAS_ON;
				else if (v == VALUE_COLOR_RENDER_DEFAULT) vo = RenderingHints.VALUE_COLOR_RENDER_DEFAULT;
				else if (v == VALUE_COLOR_RENDER_QUALITY) vo = RenderingHints.VALUE_COLOR_RENDER_QUALITY;
				else if (v == VALUE_COLOR_RENDER_SPEED) vo = RenderingHints.VALUE_COLOR_RENDER_SPEED;
				else if (v == VALUE_DITHER_DEFAULT) vo = RenderingHints.VALUE_DITHER_DEFAULT;
				else if (v == VALUE_DITHER_DISABLE) vo = RenderingHints.VALUE_DITHER_DISABLE;
				else if (v == VALUE_DITHER_ENABLE) vo = RenderingHints.VALUE_DITHER_ENABLE;
				else if (v == VALUE_FRACTIONALMETRICS_DEFAULT) vo = RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT;
				else if (v == VALUE_FRACTIONALMETRICS_OFF) vo = RenderingHints.VALUE_FRACTIONALMETRICS_OFF;
				else if (v == VALUE_FRACTIONALMETRICS_ON) vo = RenderingHints.VALUE_FRACTIONALMETRICS_ON;
				else if (v == VALUE_INTERPOLATION_BICUBIC) vo = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
				else if (v == VALUE_INTERPOLATION_BILINEAR) vo = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
				else if (v == VALUE_INTERPOLATION_NEAREST_NEIGHBOR) vo = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
				else if (v == VALUE_RENDER_DEFAULT) vo = RenderingHints.VALUE_RENDER_DEFAULT;
				else if (v == VALUE_RENDER_QUALITY) vo = RenderingHints.VALUE_RENDER_QUALITY;
				else if (v == VALUE_RENDER_SPEED) vo = RenderingHints.VALUE_RENDER_SPEED;
				else if (v == VALUE_STROKE_DEFAULT) vo = RenderingHints.VALUE_STROKE_DEFAULT;
				else if (v == VALUE_STROKE_NORMALIZE) vo = RenderingHints.VALUE_STROKE_NORMALIZE;
				else if (v == VALUE_STROKE_PURE) vo = RenderingHints.VALUE_STROKE_PURE;
				else if (v == VALUE_TEXT_ANTIALIAS_DEFAULT) vo = RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT;
				else if (v == VALUE_TEXT_ANTIALIAS_OFF) vo = RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
				else if (v == VALUE_TEXT_ANTIALIAS_ON) vo = RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
				else continue;
				rh.put(ko, vo);
			}
			return rh;
		}
		else if (type == TYPE_SYSTEM_COLOR) {
			int i = stream.readInt();
			if (i == SYSTEM_COLOR_ACTIVECAPTION) return SystemColor.activeCaption;
			else if (i == SYSTEM_COLOR_ACTIVECAPTIONBORDER) return SystemColor.activeCaptionBorder;
			else if (i == SYSTEM_COLOR_ACTIVECAPTIONTEXT) return SystemColor.activeCaptionText;
			else if (i == SYSTEM_COLOR_CONTROL) return SystemColor.control;
			else if (i == SYSTEM_COLOR_CONTROLDKSHADOW) return SystemColor.controlDkShadow;
			else if (i == SYSTEM_COLOR_CONTROLHIGHLIGHT) return SystemColor.controlHighlight;
			else if (i == SYSTEM_COLOR_CONTROLLTHIGHLIGHT) return SystemColor.controlLtHighlight;
			else if (i == SYSTEM_COLOR_CONTROLSHADOW) return SystemColor.controlShadow;
			else if (i == SYSTEM_COLOR_CONTROLTEXT) return SystemColor.controlText;
			else if (i == SYSTEM_COLOR_DESKTOP) return SystemColor.desktop;
			else if (i == SYSTEM_COLOR_INACTIVECAPTION) return SystemColor.inactiveCaption;
			else if (i == SYSTEM_COLOR_INACTIVECAPTIONBORDER) return SystemColor.inactiveCaptionBorder;
			else if (i == SYSTEM_COLOR_INACTIVECAPTIONTEXT) return SystemColor.inactiveCaptionText;
			else if (i == SYSTEM_COLOR_INFO) return SystemColor.info;
			else if (i == SYSTEM_COLOR_INFOTEXT) return SystemColor.infoText;
			else if (i == SYSTEM_COLOR_MENU) return SystemColor.menu;
			else if (i == SYSTEM_COLOR_MENUTEXT) return SystemColor.menuText;
			else if (i == SYSTEM_COLOR_SCROLLBAR) return SystemColor.scrollbar;
			else if (i == SYSTEM_COLOR_TEXT) return SystemColor.text;
			else if (i == SYSTEM_COLOR_TEXTHIGHLIGHT) return SystemColor.textHighlight;
			else if (i == SYSTEM_COLOR_TEXTHIGHLIGHTTEXT) return SystemColor.textHighlightText;
			else if (i == SYSTEM_COLOR_TEXTINACTIVETEXT) return SystemColor.textInactiveText;
			else if (i == SYSTEM_COLOR_TEXTTEXT) return SystemColor.textText;
			else if (i == SYSTEM_COLOR_WINDOW) return SystemColor.window;
			else if (i == SYSTEM_COLOR_WINDOWBORDER) return SystemColor.windowBorder;
			else if (i == SYSTEM_COLOR_WINDOWTEXT) return SystemColor.windowText;
			else return null;
		}
		else if (type == TYPE_TEXTURE_PAINT) {
			Rectangle2D a = (Rectangle2D)SerializationManager.readObject(stream);
			BufferedImage b = (BufferedImage)SerializationManager.readObject(stream);
			return new TexturePaint(b,a);
		}
		else if (type == TYPE_TRANSFORM_ATTRIBUTE) {
			double[] m = new double[6];
			for (int i = 0; i < 6; i++) {
				m[i] = stream.readDouble();
			}
			return new TransformAttribute(new AffineTransform(m));
		}
		else return null;
	}
}
