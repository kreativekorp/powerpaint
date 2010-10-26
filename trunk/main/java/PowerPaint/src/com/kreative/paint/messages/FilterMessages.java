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

package com.kreative.paint.messages;

import java.util.ListResourceBundle;

public class FilterMessages extends ListResourceBundle {
	private Object[][] contents = {
			// This is the name of the default category.
			{ "DefaultCategory", "Miscellaneous" },
			// These are the names of the filters and their categories.
			{ "BlurFilter", "Blur/Blur" },
			{ "BrightenFilter", "Color/Brighten" },
			{ "BrightnessContrastFilter", "Adjustments/Brightness & Contrast" },
			{ "ChannelMixerFilter", "Adjustments/Channel Mixer" },
			{ "ColorTransformFilter", "Color/Color Transform" },
			{ "DarkenFilter", "Color/Darken" },
			{ "DiffuseFilter", "Blur/Diffuse" },
			{ "DitherFilter", "Color/Dither" },
			{ "FauxDarkenFilter", "Overlay/Faux Darken" },
			{ "FauxDesaturateFilter", "Overlay/Faux Desaturate" },
			{ "FauxLightenFilter", "Overlay/Faux Lighten" },
			{ "FauxTranslucentFilter", "Overlay/Faux Translucency" },
			{ "GrayscaleFilter", "Color/Grayscale" },
			{ "HueSaturationFilter", "Adjustments/Hue & Saturation" },
			{ "InvertFilter", "Color/Invert" },
			{ "InvertGraysFilter", "Color/Invert Grays" },
			{ "LightenFilter", "Color/Lighten" },
			{ "MosaicFilter", "Pixelate/Mosaic" },
			{ "OffsetFilter", "Miscellaneous/Offset" },
			{ "RippleFilter", "Distort/Ripple" },
			// These are labels and titles for all filter option dialogs in general.
			{ "options.OK", "OK" },
			{ "options.Cancel", "Cancel" },
			{ "options.title", "$ Options" },
			// These are labels and titles for option dialogs for specific filters.
			{ "channel.Alpha", "Alpha:" },
			{ "channel.Red", "Red:" },
			{ "channel.Green", "Green:" },
			{ "channel.Blue", "Blue:" },
			{ "channel.Hue", "Hue:" },
			{ "channel.Saturation", "Saturation:" },
			{ "channel.Value", "Value:" },
			{ "channel.Brightness", "Brightness:" },
			{ "channel.Contrast", "Contrast:" },
			{ "channel.Affect", "Affect:" },
			{ "channel.Highlights", "Highlights" },
			{ "channel.Midtones", "Midtones" },
			{ "channel.Shadows", "Shadows" },
			{ "dither.Palette", "Palette:" },
			{ "dither.Algorithm", "Algorithm:" },
			{ "mosaic.Size", "Size:" },
			{ "mosaic.Grid", "Grid:" },
			{ "mosaic.Grid.None", "None" },
			{ "mosaic.Grid.White", "White" },
			{ "mosaic.Grid.LightGray", "Light Gray" },
			{ "mosaic.Grid.Gray", "Gray" },
			{ "mosaic.Grid.DarkGray", "Dark Gray" },
			{ "mosaic.Grid.Black", "Black" },
			{ "offset.Horiz", "Horizontal Offset:" },
			{ "offset.Vert", "Vertical Offset:" },
			{ "offset.Wrap", "Wrap Around" },
			{ "ripple.Amount", "Amount" },
	};
	
	protected Object[][] getContents() {
		return contents;
	}
}
