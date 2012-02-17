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

public class UIMessages extends ListResourceBundle {
	private Object[][] contents = {
			// This is the name and version number of the PowerPaint program.
			{ "program.longname", "Kreative PowerPaint" },
			{ "program.shortname", "PowerPaint" },
			{ "program.longversion", "Version 1.0" },
			{ "program.shortversion", "1.0" },
			// This is the copyright notice.
			{ "program.copyright1", "\u00A9 2009-2012 Rebecca G. Bettencourt / Kreative Software<br>and individual PowerPaint contributors." },
			{ "program.copyright2", "PowerPaint and the PowerPaint logos are trademarks<br>of Kreative Software. All rights reserved." },
			{ "program.copyright3", "Melissa Superpaint \u00A9 1999-2012 Rebecca G. Bettencourt /<br>Kreative Entertainment. All rights reserved." },
			// This is the default color palette, the default texture palette, and the default pattern palette.
			{ "program.pdef.colors", "SuperPaint" },
			{ "program.pdef.textures", "SuperPaint" },
			{ "program.pdef.patterns", "SuperPaint" },
			// This is the credits roll.
			{ "about.credits",
				"<html><center><font color=\"#FFFFFF\">" +
				"<br>" +
				"<br>" +
				"<b>Kreative PowerPaint 1.0</b><br>" +
				"The Best of<br>" +
				"Painting & Drawing<br>" +
				"<br>" +
				"<br>" +
				"by<br>" +
				"Kreative Software<br>" +
				"<br>" +
				"<br>" +
				"<br>" +
				"<br>" +
				"Conceived and Programmed by<br>" +
				"Rebecca Bettencourt<br>" +
				"<br>" +
				"<br>" +
				/* Uncomment when there are more names.
				"The PowerPaint 1.0 <i>\u201CWay Cool\u201D</i> Programming Team:<br>" +
				"Rebecca Bettencourt<br>" +
				"<br>" +
				"<br>" +
				*/
				/* Uncomment when there are more names.
				"Plug-in Programming by<br>" +
				"Rebecca Bettencourt<br>" +
				"<br>" +
				"<br>" +
				*/
				"Inspired by<br>" +
				"MacPaint by<br>" +
				"Bill Atkinson<br>" +
				"<small>and</small><br>" +
				"SuperPaint by<br>" +
				"Jonathan Gay, Dana Gregory,<br>" +
				"Marie L. Hughes, Charlie Jackson,<br>" +
				"Linda McLennan, Bill Snider,<br>" +
				"Eric Zocher, and Paul Holland<br>" +
				"<small>and</small><br>" +
				"Kid Pix by<br>" +
				"Craig Hickman<br>" +
				"<br>" +
				"<b>PowerPaint is 100% Original Code</b><br>" +
				"<br>" +
				"<br>" +
				/* Uncomment when there are names.
				"Outstanding testing by<br>" +
				"...our terrific beta testers!<br>" +
				"<br>" +
				"<br>" +
				*/
				/* Uncomment when there are names.
				"Special thanks to:<br>" +
				"<br>" +
				"<br>" +
				*/
				"The programming and design tools...<br>" +
				"Eclipse<br>" +
				"BBEdit<br>" +
				"Photoshop *<br>" +
				"Illustrator *<br>" +
				"GraphicConverter<br>" +
				"HexEdit<br>" +
				"Resplendence **<br>" +
				"X11<br>" +
				"<br>" +
				"* Wait, if you used Photoshop and Illustrator<br>" +
				"in making PowerPaint, doesn't that mean<br>" +
				"the universe is about to implode?<br>" +
				"<br>" +
				"** No, that's not the Resplendence found<br>" +
				"on the Kreative Legacy Software page.<br>" +
				"It's actually :D a much newer :D<br>" +
				"completely rewritten :D version. :D<br>" +
				"<br>" +
				"<br>" +
				"Give me a \u201C<b>P</b>\u201D<br>" +
				"<br>" +
				"Give me a \u201C<b>O</b>\u201D<br>" +
				"<br>" +
				"Give me a \u201C<b>WER</b>\u201D<br>" +
				"<br>" +
				"Give me a <b>PowerPaint PowerPaint</b> ra ra ra!!<br>" +
				"<br>" +
				"<br>" +
				"<br>" +
				"<br>" +
				"<br>" +
				"<br>" +
				"<br>" +
				"<br>" +
				"<br>" +
				"<br>" +
				"<br>" +
				"<br>" +
				"<big><b><i>Time for a mohawk?</i></b></big><br>" +
				"<br>" +
				"<br>" +
				"</font></center></html>" },
			// These are labels and titles used by the about box.
			{ "about.title", "About Kreative PowerPaint" },
			{ "about.creditsbtn", "Credits" },
			{ "about.pluginsbtn", "Plugins" },
			{ "about.ok", "OK" },
			{ "about.freemem", "Free Memory: $" },
			{ "about.plugins.formats", "File Formats" },
			{ "about.plugins.filters", "Filters" },
			{ "about.plugins.tools", "Tools" },
			// These are labels and titles used by the splash screen.
			{ "about.loading", "Loading..." },
			{ "about.loading.COLORS", "Loading color palettes..." },
			{ "about.loading.FONTSETS", "Loading font lists..." },
			{ "about.loading.PATTERNS", "Loading patterns..." },
			{ "about.loading.FONTS", "Loading fonts..." },
			{ "about.loading.ALPHABETS", "Loading alphabets..." },
			{ "about.loading.BRUSHES", "Loading brushes..." },
			{ "about.loading.CALLIGRAPHY", "Loading calligraphy brushes..." },
			{ "about.loading.CHARCOALS", "Loading charcoal brushes..." },
			{ "about.loading.DITHERERS", "Loading dither algorithms..." },
			{ "about.loading.FORMATS", "Loading file formats..." },
			{ "about.loading.FILTERS", "Loading filters..." },
			{ "about.loading.FRAMES", "Loading frames..." },
			{ "about.loading.GRADIENTS", "Loading gradients..." },
			{ "about.loading.LINES", "Loading lines..." },
			{ "about.loading.SHAPES", "Loading shapes..." },
			{ "about.loading.SPRINKLES", "Loading sprinkles..." },
			{ "about.loading.STAMPS", "Loading rubber stamps..." },
			{ "about.loading.TEXTURES", "Loading textures..." },
			{ "about.loading.TOOLS", "Loading tools..." },
			{ "about.loading.OPTIONS", "Loading tool options..." },
			{ "about.loading.RESOURCES", "Loading resources..." },
			{ "about.loading.PALETTES", "Loading palettes..." },
			{ "about.loading.MENUS", "Loading menus..." },
			// These are labels and titles used by the initial PowerPaint setup.
			{ "main.unpack.title", "PowerPaint Setup" },
			{ "main.unpack.prompt", "Please choose a location for PowerPaint's paint materials:" },
			{ "main.unpack.ok", "OK" },
			{ "main.unpack.cancel", "Cancel" },
			{ "main.unpack.text", "Unpacking paint materials..." },
			{ "main.unpack.failed", "<html>PowerPaint could not unpack its paint materials.<br>Make sure the location you chose is writable.</html>" },
			// This is the default title of a document not saved.
			{ "defaultTitle", "Untitled" },
			// These are titles and text strings used in open and save dialogs.
			{ "opendlg.title", "Open" },
			{ "opendlg.progress", "Reading $ format..." },
			{ "opendlg.unknown", "This file could not be opened because it is not in a recognized file format." },
			{ "opendlg.error", "This file appears to be in the $ format but it could not be opened because an error occurred." },
			{ "savedlg.title", "Save" },
			{ "savedlg.progress", "Writing $ format..." },
			{ "savedlg.unknown", "Either no file extension was specified, or the specified file extension was not recognized. Assuming PowerPaint proprietary format (CKP)." },
			{ "savedlg.error", "The file could not be saved in the $ format because an error occurred." },
			// These are titles and labels used in the Save Changes dialog.
			{ "savechanges.title", "Save Changes" },
			{ "savechanges.cta", "<html><b>Do you want to save the changes you made in<br>the document \u201C$\u201D?</b></html>" },
			{ "savechanges.scta", "<html><small>Your changes will be lost if you don\u2019t save them.</small></html>" },
			{ "savechanges.dontsave", "Don\u2019t Save" },
			{ "savechanges.cancel", "Cancel" },
			{ "savechanges.save", "Save" },
			// These are titles and labels used in the New Image dialog.
			{ "newimage.title", "New Image" },
			{ "newimage.width", "Width:" },
			{ "newimage.height", "Height:" },
			{ "newimage.units.pixels", "pixels" },
			{ "newimage.units.inches", "inches" },
			{ "newimage.units.cm", "centimeters" },
			{ "newimage.units.mm", "millimeters" },
			{ "newimage.dpix", "H. Resolution:" },
			{ "newimage.dpiy", "V. Resolution:" },
			{ "newimage.contents", "Contents:" },
			{ "newimage.contents.transparent", "Transparent" },
			{ "newimage.contents.white", "White" },
			{ "newimage.contents.black", "Black" },
			{ "newimage.contents.clipboard", "Clipboard" },
			{ "newimage.ok", "OK" },
			{ "newimage.cancel", "Cancel" },
			{ "newimage.error", "Please enter a positive number in this field." },
			// These are titles and labels used elsewhere.
			{ "print.title", "Print" },
			{ "print.error", "The image could not be printed because an error occurred. Make sure to select a printer in the Chooser. ;)" },
	};
	
	protected Object[][] getContents() {
		return contents;
	}
}
