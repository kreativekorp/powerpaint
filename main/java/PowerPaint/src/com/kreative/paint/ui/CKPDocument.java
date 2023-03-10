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

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import com.kreative.paint.Canvas;
import com.kreative.paint.CanvasController;
import com.kreative.paint.CanvasView;
import com.kreative.paint.Layer;
import com.kreative.paint.document.undo.History;
import com.kreative.paint.document.undo.HistoryEvent;
import com.kreative.paint.document.undo.HistoryListener;
import com.kreative.paint.filter.Filter;
import com.kreative.paint.filter.FilterUI;
import com.kreative.paint.format.Format;
import com.kreative.paint.format.FormatUI;
import com.kreative.paint.format.PowerPaintFormat;
import com.kreative.paint.io.MonitoredInputStream;
import com.kreative.paint.io.MonitoredOutputStream;
import com.kreative.paint.tool.ToolCommand;
import com.kreative.paint.ui.dialog.SaveChangesDialog;
import com.kreative.paint.ui.menu.CKPMenuBar;
import com.kreative.paint.ui.progress.IOProgressDialog;
import com.kreative.paint.util.ImageUtils;

public class CKPDocument {
	private static String lastSaveDirectory = null;
	private CKPApplication application;
	private File documentFile;
	private Format documentFormat;
	private boolean changed;
	private History history;
	private Canvas canvas;
	private CanvasView canvasView;
	private JScrollPane canvasScrollPane;
	private CanvasController canvasController;
	private JFrame documentWindow;
	private PageFormat pageSetup;
	private Filter lastFilter;
	
	public CKPDocument(CKPApplication app, File file, Format format, Canvas c) {
		application = app;
		documentFile = file;
		documentFormat = format;
		changed = false;
		if (c.getHistory() == null) {
			c.setHistory(history = new History());
		} else {
			history = c.getHistory();
		}
		canvas = c;
		canvasView = new CanvasView(c);
		canvasView.setOpaque(true);
		canvasView.setBackground(Color.white);
		canvasScrollPane = new JScrollPane(canvasView, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		canvasScrollPane.getViewport().setOpaque(true);
		canvasScrollPane.getViewport().setBackground(Color.gray);
		canvasScrollPane.setOpaque(true);
		canvasScrollPane.setBackground(Color.gray);
		JPanel p1 = new JPanel();
		p1.setBackground(Color.white);
		p1.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 0, Color.white));
		canvasScrollPane.setCorner(JScrollPane.LOWER_RIGHT_CORNER, p1);
		canvasController = new CanvasController(canvas, canvasView, app.getToolContext(), app.getPaintContext());
		documentWindow = new JFrame(file == null ? UIUtilities.messages.getString("defaultTitle") : file.getName());
		documentWindow.setContentPane(canvasScrollPane);
		documentWindow.setJMenuBar(new CKPMenuBar(app, this));
		documentWindow.getRootPane().putClientProperty("Window.documentFile", file);
		documentWindow.getRootPane().putClientProperty("Window.documentModified", false);
		documentWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		pageSetup = PrinterJob.getPrinterJob().defaultPage();
		lastFilter = null;
		
		history.addHistoryListener(new HistoryListener() {
			public void transactionBegan(HistoryEvent e) {}
			public void transactionCommitted(HistoryEvent e) {
				changed = true;
				documentWindow.getRootPane().putClientProperty("Window.documentModified", true);
			}
			public void transactionContinued(HistoryEvent e) {}
			public void transactionLimitChanged(HistoryEvent e) {}
			public void transactionRedone(HistoryEvent e) {
				changed = true;
				documentWindow.getRootPane().putClientProperty("Window.documentModified", true);
			}
			public void transactionRenamed(HistoryEvent e) {}
			public void transactionRolledBack(HistoryEvent e) {}
			public void transactionUndone(HistoryEvent e) {
				changed = true;
				documentWindow.getRootPane().putClientProperty("Window.documentModified", true);
			}
		});
		
		documentWindow.addWindowListener(new WindowListener() {
			public void windowActivated(WindowEvent e) {}
			public void windowClosed(WindowEvent e) {}
			public void windowClosing(WindowEvent e) { doClose(); }
			public void windowDeactivated(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowOpened(WindowEvent e) {}
		});
	}
	
	public void show() {
		documentWindow.pack();
		documentWindow.setLocationRelativeTo(null);
		documentWindow.setVisible(true);
	}

	public CKPApplication getApplication() {
		return application;
	}

	public File getDocumentFile() {
		return documentFile;
	}
	
	public Format getDocumentFormat() {
		return documentFormat;
	}
	
	public boolean isChanged() {
		return changed;
	}

	public History getHistory() {
		return history;
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public CanvasView getCanvasView() {
		return canvasView;
	}

	public JScrollPane getCanvasScrollPane() {
		return canvasScrollPane;
	}

	public CanvasController getCanvasController() {
		return canvasController;
	}

	public JFrame getDocumentWindow() {
		return documentWindow;
	}

	public PageFormat getPageSetup() {
		return pageSetup;
	}

	public Filter getLastFilter() {
		return lastFilter;
	}
	
	public boolean doClose() {
		if (changed) {
			String document = (documentFile == null ? UIUtilities.messages.getString("defaultTitle") : documentFile.getName());
			SaveChangesDialog.Action a = new SaveChangesDialog(documentWindow, document).showDialog();
			switch (a) {
			case DONT_SAVE:
				documentWindow.dispose();
				application.removeDocument(this);
				return true;
			case CANCEL:
				return false;
			case SAVE:
				doSave();
				documentWindow.dispose();
				application.removeDocument(this);
				return true;
			default:
				return false;
			}
		} else {
			documentWindow.dispose();
			application.removeDocument(this);
			return true;
		}
	}
	
	public void doMinimize() {
		if (documentWindow.getState() == Frame.ICONIFIED)
			documentWindow.setState(Frame.NORMAL);
		else
			documentWindow.setState(Frame.ICONIFIED);
	}
	
	public void doZoom() {
		if (documentWindow.getExtendedState() == Frame.MAXIMIZED_BOTH)
			documentWindow.setExtendedState(Frame.NORMAL);
		else
			documentWindow.setExtendedState(Frame.MAXIMIZED_BOTH);
	}
	
	public boolean enableSave() {
		return changed;
	}
	
	public void doSave() {
		doSaveAs(documentFile, documentFormat);
	}
	
	public void doSaveAs(File f, Format fmt) {
		if (f == null) {
			Frame frame = new Frame();
			FileDialog fd = new FileDialog(
					frame,
					UIUtilities.messages.getString("savedlg.title"),
					FileDialog.SAVE
			);
			if (lastSaveDirectory != null) fd.setDirectory(lastSaveDirectory);
			fd.setVisible(true);
			String ds = fd.getDirectory(), fs = fd.getFile();
			fd.dispose();
			frame.dispose();
			if (ds == null || fs == null) return;
			f = new File((lastSaveDirectory = ds), fs);
		}
		if (fmt == null) {
			fmt = application.getFormatManager().getFormatForFileName(f.getName(), true);
			if (fmt == null) {
				JOptionPane.showMessageDialog(
						null,
						UIUtilities.messages.getString("savedlg.unknown"),
						UIUtilities.messages.getString("savedlg.title"),
						JOptionPane.INFORMATION_MESSAGE
				);
				fmt = new PowerPaintFormat();
			}
		}
		if (!fmt.usesWriteOptionForm() || new FormatUI(null, fmt, true).showOptions()) {
			final File f2 = f;
			final Format fmt2 = fmt;
			final IOProgressDialog dlg = new IOProgressDialog((Frame)null, fmt, 0, fmt.approximateFileSize(canvas), true);
			dlg.setVisible(true);
			try {
				FileOutputStream fos = new FileOutputStream(f2);
				MonitoredOutputStream mos = new MonitoredOutputStream(dlg, fos, false);
				BufferedOutputStream bos = new BufferedOutputStream(mos);
				DataOutputStream dos = new DataOutputStream(bos);
				fmt2.write(canvas, dos, dlg);
				dos.flush();
				bos.flush();
				mos.flush();
				fos.flush();
				dos.close();
				bos.close();
				mos.close();
				fos.close();
				application.getFormatManager().postProcess(f2, fmt2);

				documentFile = f2;
				documentFormat = fmt2;
				changed = false;
				documentWindow.setTitle(f2.getName());
				documentWindow.getRootPane().putClientProperty("Window.documentFile", f2);
				documentWindow.getRootPane().putClientProperty("Window.documentModified", false);
			} catch (Exception ex) {
				ex.printStackTrace();
				dlg.dispose();
				JOptionPane.showMessageDialog(
						null,
						UIUtilities.messages.getString("savedlg.error").replace("$", fmt2.getName()),
						UIUtilities.messages.getString("savedlg.title"),
						JOptionPane.ERROR_MESSAGE
				);
			}
		}
	}
	
	public boolean enableRevert() {
		return (documentFile != null && documentFormat != null);
	}
	
	public void doRevert() {
		if (documentFile != null && documentFormat != null) {
			final IOProgressDialog dlg = new IOProgressDialog((Frame)null, documentFormat, 0, (int)documentFile.length(), false);
			dlg.setVisible(true);
			try {
				FileInputStream fis = new FileInputStream(documentFile);
				MonitoredInputStream mis = new MonitoredInputStream(dlg, fis, false);
				BufferedInputStream bis = new BufferedInputStream(mis);
				DataInputStream dis = new DataInputStream(bis);
				Canvas c = documentFormat.read(dis, dlg);
				dis.close();
				bis.close();
				mis.close();
				fis.close();

				history = new History();
				canvas = c;
				canvas.setHistory(history);
				canvasView.setCanvas(canvas);
				canvasController.setCanvas(canvas);
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						canvasScrollPane.repaint();
						canvasController.notifyCanvasControllerListeners();
					}
				});

				history.addHistoryListener(new HistoryListener() {
					public void transactionBegan(HistoryEvent e) {}
					public void transactionCommitted(HistoryEvent e) {
						changed = true;
						documentWindow.getRootPane().putClientProperty("Window.documentModified", true);
					}
					public void transactionContinued(HistoryEvent e) {}
					public void transactionLimitChanged(HistoryEvent e) {}
					public void transactionRedone(HistoryEvent e) {
						changed = true;
						documentWindow.getRootPane().putClientProperty("Window.documentModified", true);
					}
					public void transactionRenamed(HistoryEvent e) {}
					public void transactionRolledBack(HistoryEvent e) {}
					public void transactionUndone(HistoryEvent e) {
						changed = true;
						documentWindow.getRootPane().putClientProperty("Window.documentModified", true);
					}
				});

				changed = false;
				documentWindow.getRootPane().putClientProperty("Window.documentModified", false);
			} catch (Exception ex) {
				ex.printStackTrace();
				dlg.dispose();
				JOptionPane.showMessageDialog(
						null,
						UIUtilities.messages.getString("opendlg.error").replace("$", documentFormat.getName()),
						UIUtilities.messages.getString("opendlg.title"),
						JOptionPane.ERROR_MESSAGE
				);
			}
		}
	}
	
	public void doPageSetup() {
		PrinterJob pj = PrinterJob.getPrinterJob();
		pageSetup = pj.pageDialog(pageSetup == null ? pj.defaultPage() : pageSetup);
	}
	
	public void doPrint(boolean withDialog) {
		PrinterJob pj = PrinterJob.getPrinterJob();
		pj.setPrintable(canvas, pageSetup == null ? pj.defaultPage() : pageSetup);
		if (!withDialog || pj.printDialog()) {
			try {
				pj.print();
			} catch (PrinterException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(
						null,
						UIUtilities.messages.getString("print.error"),
						UIUtilities.messages.getString("print.title"),
						JOptionPane.ERROR_MESSAGE
				);
			}
		}
	}
	
	public String undoName() {
		return history.canUndo() ? history.getUndoName() : "";
	}
	
	public boolean enableUndo() {
		return history.canUndo();
	}
	
	public void doUndo() {
		if (history.canUndo()) {
			history.undo();
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					canvasScrollPane.repaint();
					canvasController.notifyCanvasControllerListeners();
				}
			});
		}
	}
	
	public String redoName() {
		return history.canRedo() ? history.getRedoName() : "";
	}
	
	public boolean enableRedo() {
		return history.canRedo();
	}
	
	public void doRedo() {
		if (history.canRedo()) {
			history.redo();
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					canvasScrollPane.repaint();
					canvasController.notifyCanvasControllerListeners();
				}
			});
		}
	}
	
	public boolean enableToolCommand(ToolCommand cmd) {
		return canvasController.enableCommand(cmd);
	}
	
	public void doToolCommand(ToolCommand cmd) {
		if (canvasController.enableCommand(cmd)) {
			canvasController.doCommand(cmd);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					canvasScrollPane.repaint();
					canvasController.notifyCanvasControllerListeners();
				}
			});
		}
	}
	
	public void doActualSize() {
		canvasView.setScale(1.0f);
		canvasScrollPane.repaint();
	}
	
	public void doZoomIn() {
		canvasView.setScale(canvasView.getScale()*2.0f);
		canvasScrollPane.repaint();
	}
	
	public void doZoomOut() {
		canvasView.setScale(canvasView.getScale()/2.0f);
		canvasScrollPane.repaint();
	}
	
	public void doFilter(Filter f) {
		history.begin(f.getName());
		if (canvas.isPaintSelectionPopped()) {
			Layer l = canvas.getPaintDrawLayer();
			Image src = l.getPoppedImage();
			if (!f.usesOptionForm() || new FilterUI(documentWindow, f, src).showOptions()) {
				lastFilter = f;
				Image dst = f.filter(src);
				if (dst != null && dst != src) {
					l.setPoppedImage(ImageUtils.toBufferedImage(dst, true), l.getPoppedImageTransform());
				}
			}
		} else if (canvas.getPaintSelection() != null) {
			canvas.popPaintSelection(false, false);
			Layer l = canvas.getPaintDrawLayer();
			Image src = l.getPoppedImage();
			if (!f.usesOptionForm() || new FilterUI(documentWindow, f, src).showOptions()) {
				lastFilter = f;
				Image dst = f.filter(src);
				if (dst != null && dst != src) {
					l.setPoppedImage(ImageUtils.toBufferedImage(dst, true), l.getPoppedImageTransform());
				}
			}
			canvas.pushPaintSelection();
		} else {
			int w = canvas.getWidth();
			int h = canvas.getHeight();
			BufferedImage src = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Layer l = canvas.getPaintDrawLayer();
			int[] pixels = new int[w*h];
			l.getRGB(-l.getX(), -l.getY(), w, h, pixels, 0, w);
			src.setRGB(0, 0, w, h, pixels, 0, w);
			if (!f.usesOptionForm() || new FilterUI(documentWindow, f, src).showOptions()) {
				lastFilter = f;
				Image dst = f.filter(src);
				if (dst != null && dst != src) {
					BufferedImage db = ImageUtils.toBufferedImage(dst, false);
					db.getRGB(0, 0, w, h, pixels, 0, w);
					l.setRGB(-l.getX(), -l.getY(), w, h, pixels, 0, w);
				}
			}
		}
		history.commit();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				canvasScrollPane.repaint();
				canvasController.notifyCanvasControllerListeners();
			}
		});
	}
	
	public String lastFilterName() {
		return (lastFilter == null) ? "" : lastFilter.getName();
	}
	
	public boolean enableLastFilter() {
		return lastFilter != null;
	}
	
	public void doLastFilter() {
		if (lastFilter == null) return;
		history.begin(lastFilter.getName());
		if (canvas.isPaintSelectionPopped()) {
			Layer l = canvas.getPaintDrawLayer();
			Image src = l.getPoppedImage();
			Image dst = lastFilter.filter(src);
			if (dst != null && dst != src) {
				l.setPoppedImage(ImageUtils.toBufferedImage(dst, true), l.getPoppedImageTransform());
			}
		} else if (canvas.getPaintSelection() != null) {
			canvas.popPaintSelection(false, false);
			Layer l = canvas.getPaintDrawLayer();
			Image src = l.getPoppedImage();
			Image dst = lastFilter.filter(src);
			if (dst != null && dst != src) {
				l.setPoppedImage(ImageUtils.toBufferedImage(dst, true), l.getPoppedImageTransform());
			}
			canvas.pushPaintSelection();
		} else {
			int w = canvas.getWidth();
			int h = canvas.getHeight();
			BufferedImage src = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Layer l = canvas.getPaintDrawLayer();
			int[] pixels = new int[w*h];
			l.getRGB(-l.getX(), -l.getY(), w, h, pixels, 0, w);
			src.setRGB(0, 0, w, h, pixels, 0, w);
			Image dst = lastFilter.filter(src);
			if (dst != null && dst != src) {
				BufferedImage db = ImageUtils.toBufferedImage(dst, false);
				db.getRGB(0, 0, w, h, pixels, 0, w);
				l.setRGB(-l.getX(), -l.getY(), w, h, pixels, 0, w);
			}
		}
		history.commit();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				canvasScrollPane.repaint();
				canvasController.notifyCanvasControllerListeners();
			}
		});
	}
}
