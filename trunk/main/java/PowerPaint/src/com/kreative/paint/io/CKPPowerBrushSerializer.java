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

package com.kreative.paint.io;

import java.io.*;
import com.kreative.paint.powerbrush.*;

public class CKPPowerBrushSerializer extends Serializer {
	private static final int TYPE_BRUSH_SETTINGS = fcc("BrSt");
	private static final int TYPE_DIAMOND_BS = fcc("@Dia");
	private static final int TYPE_HORIZONTAL_BS = fcc("@Hrz");
	private static final int TYPE_ROUND_BS = fcc("@Rnd");
	private static final int TYPE_ROUNDRECT_BS = fcc("@RRt");
	private static final int TYPE_SQUARE_BS = fcc("@Sqr");
	private static final int TYPE_VERTICAL_BS = fcc("@Vrt");
	private static final int TYPE_X_BS = fcc("@Xxx");
	private static final int TYPE_YNXDIAG_BS = fcc("@YNX");
	private static final int TYPE_YXDIAG_BS = fcc("@YXD");
	
	protected void loadRecognizedTypesAndClasses() {
		addTypeAndClass(TYPE_BRUSH_SETTINGS, 2, BrushSettings.class);
		addTypeAndClass(TYPE_DIAMOND_BS, 1, DiamondBrushShape.class);
		addTypeAndClass(TYPE_HORIZONTAL_BS, 1, HorizontalBrushShape.class);
		addTypeAndClass(TYPE_ROUND_BS, 1, RoundBrushShape.class);
		addTypeAndClass(TYPE_ROUNDRECT_BS, 1, RoundRectBrushShape.class);
		addTypeAndClass(TYPE_SQUARE_BS, 1, SquareBrushShape.class);
		addTypeAndClass(TYPE_VERTICAL_BS, 1, VerticalBrushShape.class);
		addTypeAndClass(TYPE_X_BS, 1, XBrushShape.class);
		addTypeAndClass(TYPE_YNXDIAG_BS, 1, YNXDiagonalBrushShape.class);
		addTypeAndClass(TYPE_YXDIAG_BS, 1, YXDiagonalBrushShape.class);
	}
	
	public void serializeObject(Object o, DataOutputStream stream) throws IOException {
		if (o instanceof BrushSettings) {
			BrushSettings v = (BrushSettings)o;
			SerializationManager.writeObject(v.getBrushShape(), stream);
			stream.writeFloat(v.getOuterWidth());
			stream.writeFloat(v.getOuterHeight());
			stream.writeFloat(v.getInnerWidth());
			stream.writeFloat(v.getInnerHeight());
			stream.writeInt(v.getFlowRate());
		}
	}
	
	public Object deserializeObject(int type, int version, DataInputStream stream) throws IOException {
		if (!(version == 1 || (type == TYPE_BRUSH_SETTINGS && version == 2))) throw new IOException("Invalid version number.");
		else if (type == TYPE_BRUSH_SETTINGS) {
			BrushShape bs = (BrushShape)SerializationManager.readObject(stream);
			float ow = stream.readFloat();
			float oh = stream.readFloat();
			float iw = stream.readFloat();
			float ih = stream.readFloat();
			int fr = (version > 1) ? stream.readInt() : 20;
			return new BrushSettings(bs, ow, oh, iw, ih, fr);
		}
		else if (type == TYPE_DIAMOND_BS) return DiamondBrushShape.instance;
		else if (type == TYPE_HORIZONTAL_BS) return HorizontalBrushShape.instance;
		else if (type == TYPE_ROUND_BS) return RoundBrushShape.instance;
		else if (type == TYPE_ROUNDRECT_BS) return RoundRectBrushShape.instance;
		else if (type == TYPE_SQUARE_BS) return SquareBrushShape.instance;
		else if (type == TYPE_VERTICAL_BS) return VerticalBrushShape.instance;
		else if (type == TYPE_X_BS) return XBrushShape.instance;
		else if (type == TYPE_YNXDIAG_BS) return YNXDiagonalBrushShape.instance;
		else if (type == TYPE_YXDIAG_BS) return YXDiagonalBrushShape.instance;
		else return null;
	}
}
