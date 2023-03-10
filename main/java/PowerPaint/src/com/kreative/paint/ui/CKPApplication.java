/*
 * Copyright &copy; 2010-2011 Rebecca G. Bettencourt / Kreative Software
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

package com.kreative.paint.ui;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Vector;
import javax.swing.JOptionPane;
import com.kreative.paint.Canvas;
import com.kreative.paint.PaintContext;
import com.kreative.paint.ToolContext;
import com.kreative.paint.datatransfer.ClipboardUtilities;
import com.kreative.paint.filter.FilterManager;
import com.kreative.paint.format.Format;
import com.kreative.paint.format.FormatManager;
import com.kreative.paint.format.FormatUI;
import com.kreative.paint.io.MonitoredInputStream;
import com.kreative.paint.material.MaterialLoader;
import com.kreative.paint.material.MaterialLocator;
import com.kreative.paint.material.MaterialManager;
import com.kreative.paint.palette.PaletteManager;
import com.kreative.paint.tool.ToolManager;
import com.kreative.paint.ui.about.AboutBox;
import com.kreative.paint.ui.about.SplashScreen;
import com.kreative.paint.ui.dialog.NewImageDialog;
import com.kreative.paint.ui.progress.IOProgressDialog;
import com.kreative.paint.util.ImageUtils;
import com.kreative.paint.util.OSUtils;

public class CKPApplication {
	private static String lastOpenDirectory = null;
	private MaterialManager mm;
	private ToolContext tc;
	private PaintContext pc;
	private ToolManager tm;
	private FilterManager ftrm;
	private FormatManager fmtm;
	private PaletteManager pm;
	private Collection<CKPDocument> docs;
	
	public CKPApplication(SplashScreen ss) {
		if (ss != null) ss.setLoadingMessage(UIUtilities.messages.getString("about.loading.RESOURCES"));
		MaterialLocator mloc = new MaterialLocator("Kreative", "PowerPaint");
		MaterialLoader ml = mloc.getMaterialLoader();
		mm = new MaterialManager(ml);
		
		if (ss != null) ss.setLoadingMessage(UIUtilities.messages.getString("about.loading.ALPHABETS"));
		mm.alphabetLoader().getAlphabets();
		if (ss != null) ss.setLoadingMessage(UIUtilities.messages.getString("about.loading.BRUSHES"));
		mm.spriteLoader().getBrushes();
		if (ss != null) ss.setLoadingMessage(UIUtilities.messages.getString("about.loading.CALLIGRAPHY"));
		mm.spriteLoader().getCalligraphyBrushes();
		if (ss != null) ss.setLoadingMessage(UIUtilities.messages.getString("about.loading.CHARCOALS"));
		mm.spriteLoader().getCharcoalBrushes();
		if (ss != null) ss.setLoadingMessage(UIUtilities.messages.getString("about.loading.FRAMES"));
		mm.frameLoader().getFrames();
		if (ss != null) ss.setLoadingMessage(UIUtilities.messages.getString("about.loading.STAMPS"));
		mm.spriteLoader().getRubberStamps();
		if (ss != null) ss.setLoadingMessage(UIUtilities.messages.getString("about.loading.SHAPES"));
		mm.shapeLoader().getShapes();
		if (ss != null) ss.setLoadingMessage(UIUtilities.messages.getString("about.loading.SPRINKLES"));
		mm.spriteLoader().getSprinkles();
		if (ss != null) ss.setLoadingMessage(UIUtilities.messages.getString("about.loading.RESOURCES"));
		tc = new ToolContext(mm);
		pc = new PaintContext();
		
		if (ss != null) ss.setLoadingMessage(UIUtilities.messages.getString("about.loading.TOOLS"));
		tm = new ToolManager(mm);
		
		if (ss != null) ss.setLoadingMessage(UIUtilities.messages.getString("about.loading.DITHERERS"));
		mm.ditherLoader().getDitherAlgorithms();
		if (ss != null) ss.setLoadingMessage(UIUtilities.messages.getString("about.loading.FILTERS"));
		ftrm = new FilterManager(mm);
		
		if (ss != null) ss.setLoadingMessage(UIUtilities.messages.getString("about.loading.FORMATS"));
		fmtm = new FormatManager(mm);
		
		if (ss != null) ss.setLoadingMessage(UIUtilities.messages.getString("about.loading.COLORS"));
		mm.colorPaletteLoader().getColorPalettes();
		mm.colorPaletteLoader().getColorLists();
		mm.colorPaletteLoader().getColorArrays();
		if (ss != null) ss.setLoadingMessage(UIUtilities.messages.getString("about.loading.TEXTURES"));
		mm.textureLoader().getTextures();
		if (ss != null) ss.setLoadingMessage(UIUtilities.messages.getString("about.loading.GRADIENTS"));
		mm.gradientLoader().getGradientPresets();
		mm.gradientLoader().getGradientShapes();
		mm.gradientLoader().getGradientColorMaps();
		if (ss != null) ss.setLoadingMessage(UIUtilities.messages.getString("about.loading.PATTERNS"));
		mm.patternLoader().getPatternLists();
		if (ss != null) ss.setLoadingMessage(UIUtilities.messages.getString("about.loading.LINES"));
		mm.strokeLoader().getLineWidths();
		mm.strokeLoader().getLineMultiplicities();
		mm.strokeLoader().getLineDashes();
		mm.strokeLoader().getLineArrowheads();
		if (ss != null) ss.setLoadingMessage(UIUtilities.messages.getString("about.loading.FONTSETS"));
		mm.fontLoader().getFontLists();
		if (ss != null) ss.setLoadingMessage(UIUtilities.messages.getString("about.loading.PROFILES"));
		mm.colorProfileLoader().getColorProfiles();
		mm.colorProfileLoader().getColorModels();
		if (ss != null) ss.setLoadingMessage(UIUtilities.messages.getString("about.loading.PALETTES"));
		pm = new PaletteManager(
				tc, pc, tm, mm,
				UIUtilities.messages.getString("program.pdef.colors"),
				UIUtilities.messages.getString("program.pdef.colorlist"),
				UIUtilities.messages.getString("program.pdef.textures"),
				UIUtilities.messages.getString("program.pdef.patterns")
		);
		
		docs = new HashSet<CKPDocument>();
	}
	
	public void show() {
		Rectangle screenRect = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getBounds();
		int y = screenRect.y + (OSUtils.isMacOS() ? 32 : 8);
		pm.getToolPalette().setLocation(screenRect.x + 8, y);
		pm.getSNFPalette().setLocation(screenRect.x + pm.getToolPalette().getWidth() + 16, y);
		pm.getToolOptionsPalette().setLocation(screenRect.x + screenRect.width - pm.getToolOptionsPalette().getWidth() - 8, y);
		pm.getToolPalette().setVisible(true);
		pm.getSNFPalette().setVisible(true);
		pm.getToolOptionsPalette().setVisible(true);
	}
	
	public MaterialManager getMaterialManager() {
		return mm;
	}
	
	public ToolContext getToolContext() {
		return tc;
	}
	
	public PaintContext getPaintContext() {
		return pc;
	}
	
	public ToolManager getToolManager() {
		return tm;
	}
	
	public FilterManager getFilterManager() {
		return ftrm;
	}
	
	public FormatManager getFormatManager() {
		return fmtm;
	}
	
	public PaletteManager getPaletteManager() {
		return pm;
	}
	
	public Collection<CKPDocument> getDocuments() {
		return docs;
	}
	
	public void addDocument(CKPDocument doc) {
		docs.add(doc);
	}
	
	public void removeDocument(CKPDocument doc) {
		docs.remove(doc);
		if (docs.isEmpty() && !OSUtils.isMacOS()) {
			System.exit(0);
		}
	}
	
	public void doAbout() {
		new AboutBox((Frame)null, true, fmtm, ftrm, tm).setVisible(true);
	}
	
	public void doNew(boolean withDialog) {
		Canvas c = withDialog ? new NewImageDialog((Frame)null).showDialog() : new Canvas(800, 600);
		if (c != null) {
			CKPDocument doc = new CKPDocument(this, null, null, c);
			addDocument(doc);
			doc.show();
		}
	}
	
	public boolean enableNewFromClipboard() {
		return (ClipboardUtilities.clipboardHasImage());
	}
	
	public void doNewFromClipboard() {
		if (ClipboardUtilities.clipboardHasImage()) {
			Image cbi = ClipboardUtilities.getClipboardImage();
			ImageUtils.prepImage(cbi);
			int w = cbi.getWidth(null);
			int h = cbi.getHeight(null);
			Canvas c = new Canvas(w, h);
			if (ClipboardUtilities.clipboardHasDrawObjects()) {
				c.get(0).addAll(ClipboardUtilities.getClipboardDrawObjects());
			} else {
				Graphics2D g = c.get(0).createPaintGraphics();
				while (!g.drawImage(cbi, 0, 0, null));
				g.dispose();
			}
			CKPDocument doc = new CKPDocument(this, null, null, c);
			addDocument(doc);
			doc.show();
		}
	}
	
	public void doOpen(File f) {
		if (f == null) {
			Frame frame = new Frame();
			FileDialog fd = new FileDialog(
					frame,
					UIUtilities.messages.getString("opendlg.title"),
					FileDialog.LOAD
			);
			if (lastOpenDirectory != null) fd.setDirectory(lastOpenDirectory);
			fd.setVisible(true);
			String ds = fd.getDirectory(), fs = fd.getFile();
			fd.dispose();
			frame.dispose();
			if (ds == null || fs == null) return;
			f = new File((lastOpenDirectory = ds), fs);
		}
		Format fmt = fmtm.getFormatForFile(f, false);
		if (fmt == null) {
			JOptionPane.showMessageDialog(
					null,
					UIUtilities.messages.getString("opendlg.unknown"),
					UIUtilities.messages.getString("opendlg.title"),
					JOptionPane.ERROR_MESSAGE
			);
		} else if (!fmt.usesReadOptionForm() || new FormatUI((Frame)null, fmt, false).showOptions()) {
			final File f2 = f;
			final Format fmt2 = fmt;
			final IOProgressDialog dlg = new IOProgressDialog((Frame)null, fmt, 0, (int)f.length(), false);
			dlg.setVisible(true);
			try {
				FileInputStream fis = new FileInputStream(f2);
				MonitoredInputStream mis = new MonitoredInputStream(dlg, fis, false);
				BufferedInputStream bis = new BufferedInputStream(mis);
				DataInputStream dis = new DataInputStream(bis);
				Canvas c = fmt2.read(dis, dlg);
				CKPDocument doc = new CKPDocument(CKPApplication.this, f2, fmt2, c);
				addDocument(doc);
				doc.show();
				dis.close();
				bis.close();
				mis.close();
				fis.close();
			} catch (Exception ex) {
				ex.printStackTrace();
				dlg.dispose();
				JOptionPane.showMessageDialog(
						null,
						UIUtilities.messages.getString("opendlg.error").replace("$", fmt2.getName()),
						UIUtilities.messages.getString("opendlg.title"),
						JOptionPane.ERROR_MESSAGE
				);
			}
		}
	}
	
	public void doPrint(File f) {
		if (f == null) {
			Frame frame = new Frame();
			FileDialog fd = new FileDialog(
					frame,
					UIUtilities.messages.getString("opendlg.title"),
					FileDialog.LOAD
			);
			if (lastOpenDirectory != null) fd.setDirectory(lastOpenDirectory);
			fd.setVisible(true);
			String ds = fd.getDirectory(), fs = fd.getFile();
			fd.dispose();
			frame.dispose();
			if (ds == null || fs == null) return;
			f = new File((lastOpenDirectory = ds), fs);
		}
		Format fmt = fmtm.getFormatForFile(f, false);
		if (fmt == null) {
			JOptionPane.showMessageDialog(
					null,
					UIUtilities.messages.getString("opendlg.unknown"),
					UIUtilities.messages.getString("opendlg.title"),
					JOptionPane.ERROR_MESSAGE
			);
		} else if (!fmt.usesReadOptionForm() || new FormatUI((Frame)null, fmt, false).showOptions()) {
			final File f2 = f;
			final Format fmt2 = fmt;
			final IOProgressDialog dlg = new IOProgressDialog((Frame)null, fmt, 0, (int)f.length(), false);
			dlg.setVisible(true);
			try {
				FileInputStream fis = new FileInputStream(f2);
				MonitoredInputStream mis = new MonitoredInputStream(dlg, fis, false);
				BufferedInputStream bis = new BufferedInputStream(mis);
				DataInputStream dis = new DataInputStream(bis);
				Canvas c = fmt2.read(dis, dlg);
				CKPDocument doc = new CKPDocument(CKPApplication.this, f2, fmt2, c);
				addDocument(doc);
				doc.show();
				doc.doPrint(true);
				dis.close();
				bis.close();
				mis.close();
				fis.close();
			} catch (Exception ex) {
				ex.printStackTrace();
				dlg.dispose();
				JOptionPane.showMessageDialog(
						null,
						UIUtilities.messages.getString("opendlg.error").replace("$", fmt2.getName()),
						UIUtilities.messages.getString("opendlg.title"),
						JOptionPane.ERROR_MESSAGE
				);
			}
		}
	}
	
	public void doQuit() {
		Vector<CKPDocument> d = new Vector<CKPDocument>();
		d.addAll(docs);
		for (CKPDocument doc : d) {
			if (!doc.doClose()) return;
		}
		System.exit(0);
	}
}
