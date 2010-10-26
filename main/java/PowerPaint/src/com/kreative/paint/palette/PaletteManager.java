/*
 * Copyright &copy; 2010 Rebecca G. Bettencourt / Kreative Software
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

package com.kreative.paint.palette;

import com.kreative.paint.PaintContext;
import com.kreative.paint.ToolContext;
import com.kreative.paint.res.MaterialsManager;
import com.kreative.paint.tool.ToolManager;

public class PaletteManager {
	private ToolPalette toolPalette;
	private SNFPalette snfPalette;
	private ColorPalettePalette colorPalettePalette;
	private ColorListPalette colorListPalette;
	private ColorCubePalette colorCubePalette;
	private ColorSliderPalette colorSliderPalette;
	private ColorWheelPalette colorWheelPalette;
	private ColorMixerPalette colorMixerPalette;
	private TexturePalette texturePalette;
	private GradientPalette gradientPalette;
	private PatternPalette patternPalette;
	private CompositePalette compositePalette;
	private StrokePalette strokePalette;
	private FontPalette fontPalette;
	private ToolOptionsPalette toolOptionsPalette;
	
	public PaletteManager(ToolContext tc, PaintContext pc, ToolManager tm, MaterialsManager mm, String defCP, String defTP, String defPP) {
		toolPalette = new ToolPalette(tc, tm);
		snfPalette = new SNFPalette(
				pc,
				colorPalettePalette = new ColorPalettePalette(pc, mm, defCP),
				new PaintContextPalette[]{
					colorListPalette = new ColorListPalette(pc, mm),
					colorCubePalette = new ColorCubePalette(pc),
					colorSliderPalette = new ColorSliderPalette(pc),
					colorWheelPalette = new ColorWheelPalette(pc),
					colorMixerPalette = new ColorMixerPalette(pc)
				},
				texturePalette = new TexturePalette(pc, mm, defTP),
				gradientPalette = new GradientPalette(pc, mm),
				patternPalette = new PatternPalette(pc, mm, defPP),
				compositePalette = new CompositePalette(pc),
				strokePalette = new StrokePalette(pc, mm),
				fontPalette = new FontPalette(pc, mm)
		);
		toolOptionsPalette = new ToolOptionsPalette(tc);
	}

	public ToolPalette getToolPalette() {
		return toolPalette;
	}

	public SNFPalette getSNFPalette() {
		return snfPalette;
	}

	public ColorPalettePalette getColorPalettePalette() {
		return colorPalettePalette;
	}

	public ColorListPalette getColorListPalette() {
		return colorListPalette;
	}

	public ColorCubePalette getColorCubePalette() {
		return colorCubePalette;
	}

	public ColorSliderPalette getColorSliderPalette() {
		return colorSliderPalette;
	}

	public ColorWheelPalette getColorWheelPalette() {
		return colorWheelPalette;
	}

	public ColorMixerPalette getColorMixerPalette() {
		return colorMixerPalette;
	}

	public TexturePalette getTexturePalette() {
		return texturePalette;
	}

	public GradientPalette getGradientPalette() {
		return gradientPalette;
	}

	public PatternPalette getPatternPalette() {
		return patternPalette;
	}

	public CompositePalette getCompositePalette() {
		return compositePalette;
	}

	public StrokePalette getStrokePalette() {
		return strokePalette;
	}

	public FontPalette getFontPalette() {
		return fontPalette;
	}

	public ToolOptionsPalette getToolOptionsPalette() {
		return toolOptionsPalette;
	}
}
