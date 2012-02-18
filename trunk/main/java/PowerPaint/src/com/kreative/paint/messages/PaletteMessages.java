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

package com.kreative.paint.messages;

import java.util.ListResourceBundle;

public class PaletteMessages extends ListResourceBundle {
	private Object[][] contents = {
			// These are the titles of palettes.
			{ "ColorCubePalette", "Color Cube" },
			{ "ColorListPalette", "Color Lists" },
			{ "ColorMixerPalette", "Color Mixer" },
			{ "ColorPalettePalette", "Color Palettes" },
			{ "ColorSliderPalette", "Color Sliders" },
			{ "ColorWheelPalette", "Color Wheel" },
			{ "CompositePalette", "Composite" },
			{ "FontPalette", "Fonts" },
			{ "GradientPalette", "Gradients" },
			{ "PatternPalette", "Patterns" },
			{ "SNFPalette", "Stroke & Fill" },
			{ "StrokePalette", "Stroke" },
			{ "TexturePalette", "Textures" },
			{ "ToolOptionsPalette", "Tool Options" },
			{ "ToolPalette", "Tools" },
			// These are labels for specific palettes.
			{ "composite.show", "Show Composite" },
			{ "composite.Src", "Source" },
			{ "composite.SrcOver", "Source Over" },
			{ "composite.SrcIn", "Source In" },
			{ "composite.SrcOut", "Source Out" },
			{ "composite.SrcAtop", "Source Atop" },
			{ "composite.Dst", "Dest" },
			{ "composite.DstOver", "Dest Over" },
			{ "composite.DstIn", "Dest In" },
			{ "composite.DstOut", "Dest Out" },
			{ "composite.DstAtop", "Dest Atop" },
			{ "composite.Xor", "Xor" },
			{ "composite.Clear", "Clear" },
			{ "composite.Opacity", "Opacity:" },
			{ "fonts.show", "Show Fonts" },
			{ "fonts.all", "All Fonts" },
			{ "fonts.plain", "Plain" },
			{ "fonts.bold", "Bold" },
			{ "fonts.italic", "Italic" },
			{ "fonts.bolditalic", "Bold Italic" },
			{ "fonts.other", "Other..." },
			{ "fonts.other.prompt", "Other Font Size:" },
			{ "gradients.presets", "Gradient Presets:" },
			{ "gradients.shapes", "Gradient Shapes:" },
			{ "gradients.colors", "Gradient Colors:" },
			{ "stroke.show", "Show Stroke" },
			{ "stroke.tooltip.width", "Line Width $" },
			{ "stroke.tooltip.multiplicity", "Multiplicity $" },
			{ "stroke.width", "Width:" },
			{ "stroke.cap.butt", "Butt" },
			{ "stroke.cap.round", "Round" },
			{ "stroke.cap.square", "Square" },
			{ "stroke.join.miter", "Miter" },
			{ "stroke.join.round", "Round" },
			{ "stroke.join.bevel", "Bevel" },
			{ "stroke.miterlimit", "Miter Limit:" },
			{ "tools.paint", "Paint Mode" },
			{ "tools.draw", "Draw Mode" },
			{ "textures.defaultcategory", "Miscellaneous" },
			{ "snf.stroke", "Stroke" },
			{ "snf.fill", "Fill" },
			{ "snf.white", "White" },
			{ "snf.black", "Black" },
			{ "snf.none", "None" },
			{ "snf.swapsnf", "Swap Stroke & Fill" },
			{ "snf.swapfgbg", "Swap Foreground & Background" },
			{ "snf.fgcolor", "Foreground Color" },
			{ "snf.fgtexture", "Foreground Texture" },
			{ "snf.fggradient", "Foreground Gradient" },
			{ "snf.bgcolor", "Background Color" },
			{ "snf.bgtexture", "Background Texture" },
			{ "snf.bggradient", "Background Gradient" },
			{ "snf.color.palettes", "Color Palettes" },
			{ "snf.color.lists", "Color Lists" },
			{ "snf.color.cube", "Color Cube" },
			{ "snf.color.sliders", "Color Sliders" },
			{ "snf.color.wheel", "Color Wheel" },
			{ "snf.color.mixer", "Color Mixer" },
			{ "snf.pattern", "Pattern" },
			{ "snf.composite", "Composite" },
			{ "snf.font", "Text Font" },
			{ "snf.align", "Text Alignment" },
			{ "snf.align.left", "Text Align Left" },
			{ "snf.align.center", "Text Align Center" },
			{ "snf.align.right", "Text Align Right" },
			{ "snf.align.justified", "Text Justified" },
			{ "snf.aa", "Anti-Aliasing" },
			{ "snf.aa.on", "Anti-Aliasing is On (Click to Turn Off)" },
			{ "snf.aa.off", "Anti-Aliasing is Off (Click to Turn On)" },
			{ "snf.custom.empty", "Click to save current paint." },
			{ "snf.custom.filled", "Click to use this paint. Ctrl-click to delete." },
	};
	
	protected Object[][] getContents() {
		return contents;
	}
}
