/*
 * Copyright &copy; 2009-2021 Rebecca G. Bettencourt / Kreative Software
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

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.*;
import javax.swing.*;
import com.kreative.paint.io.MonitoredInputStream;
import com.kreative.paint.material.MaterialLocator;
import com.kreative.paint.material.MaterialPacker;
import com.kreative.paint.ui.about.SplashScreen;
import com.kreative.paint.ui.menu.CKPMenuBar;
import com.kreative.paint.ui.progress.UnpackProgressDialog;
import com.kreative.paint.util.OSUtils;
import com.kreative.paint.util.SwingUtils;

public class Main {
	public static void main(String[] args) {
		try { System.setProperty("com.apple.mrj.application.apple.menu.about.name", "PowerPaint"); } catch (Exception e) {}
		try { System.setProperty("apple.awt.graphics.UseQuartz", "false"); } catch (Exception e) {}
		try { System.setProperty("apple.laf.useScreenMenuBar", "true"); } catch (Exception e) {}
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
		
		try {
			Method getModule = Class.class.getMethod("getModule");
			Object javaDesktop = getModule.invoke(Toolkit.getDefaultToolkit().getClass());
			Object allUnnamed = getModule.invoke(Main.class);
			Class<?> module = Class.forName("java.lang.Module");
			Method addOpens = module.getMethod("addOpens", String.class, module);
			addOpens.invoke(javaDesktop, "sun.awt.X11", allUnnamed);
		} catch (Exception e) {}
		
		try {
			Toolkit tk = Toolkit.getDefaultToolkit();
			Field aacn = tk.getClass().getDeclaredField("awtAppClassName");
			aacn.setAccessible(true);
			aacn.set(tk, "PowerPaint");
		} catch (Exception e) {}
		
		MaterialLocator mloc = new MaterialLocator("Kreative", "PowerPaint");
		File root = mloc.getFirstAvailableRoot();
		if (root == null || !root.exists()) {
			SwingUtilities.invokeLater(new Loader(args));
		} else {
			SwingUtilities.invokeLater(new Launcher(args));
		}
	}
	
	private static class Loader implements Runnable {
		private String[] args;
		public Loader(String[] args) {
			this.args = args;
		}
		public void run() {
			final MaterialLocator mloc = new MaterialLocator("Kreative", "PowerPaint");
			final JDialog promptdlg = new JDialog((JFrame)null, UIUtilities.messages.getString("main.unpack.title"), true);
			final JLabel promptlbl = new JLabel(UIUtilities.messages.getString("main.unpack.prompt"));
			final JComboBox promptpop = new JComboBox(mloc.listRoots().toArray());
			promptpop.setEditable(false);
			promptpop.setMaximumRowCount(32);
			final JButton promptok = new JButton(UIUtilities.messages.getString("main.unpack.ok"));
			promptok.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (promptpop.getSelectedItem() == null) System.exit(0);
					else promptdlg.dispose();
				}
			});
			final JButton promptcan = new JButton(UIUtilities.messages.getString("main.unpack.cancel"));
			promptcan.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
			final JPanel promptcontent = new JPanel(new GridLayout(2,1,0,0));
			promptcontent.add(promptlbl);
			promptcontent.add(promptpop);
			final JPanel promptbuttons = new JPanel(new FlowLayout());
			promptbuttons.add(promptok);
			promptbuttons.add(promptcan);
			final JPanel promptmain = new JPanel(new BorderLayout());
			promptmain.add(promptcontent, BorderLayout.CENTER);
			promptmain.add(promptbuttons, BorderLayout.PAGE_END);
			promptmain.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
			promptdlg.setContentPane(promptmain);
			SwingUtils.setDefaultButton(promptdlg.getRootPane(), promptok);
			SwingUtils.setCancelButton(promptdlg.getRootPane(), promptcan);
			promptdlg.pack();
			promptdlg.setLocationRelativeTo(null);
			promptdlg.setVisible(true);
			final File root = (File)promptpop.getSelectedItem();
			if (root == null) System.exit(0);
			if (!root.exists()) root.mkdirs();
			
			final UnpackProgressDialog dlg = new UnpackProgressDialog(null, 0, 8000000);
			dlg.setVisible(true);
			Thread thr = new Thread() {
				public void run() {
					try {
						URL u = Main.class.getResource("materials.pmz");
						URLConnection uc = u.openConnection();
						if (uc.getContentLength() > 0) {
							dlg.setMaximum(uc.getContentLength());
						}
						InputStream uin = uc.getInputStream();
						MonitoredInputStream in = new MonitoredInputStream(dlg, uin, false);
						MaterialPacker.unzip(in, root);
						in.close();
						uin.close();
					} catch (IOException ex) {
						ex.printStackTrace();
						dlg.dispose();
						JOptionPane.showMessageDialog(null, UIUtilities.messages.getString("main.unpack.failed"));
					}
					SwingUtilities.invokeLater(new Launcher(args));
				}
			};
			thr.start();
		}
	}
	
	private static class Launcher implements Runnable {
		private String[] args;
		public Launcher(String[] args) {
			this.args = args;
		}
		public void run() {
			Thread thr1 = (new Thread() {
				public void run() {
					final SplashScreen ss = new SplashScreen();
					Thread thr2 = (new Thread() {
						public void run() {
							CKPApplication app = new CKPApplication(ss);
							ss.dispose();
							app.show();
							if (OSUtils.isMacOS()) {
								try {
									Class<?> dwc = Class.forName("com.kreative.paint.ui.mac.MacDummyWindow");
									Constructor<?> dwk = dwc.getConstructor(JMenuBar.class);
									dwk.newInstance(new CKPMenuBar(app, null));
								} catch (Exception e) {
									e.printStackTrace();
								}
							} else if (args.length == 0) {
								app.doNew(false);
							}
							for (String arg : args) {
								app.doOpen(new File(arg));
							}
							if (OSUtils.isMacOS()) {
								try {
									Class<?> alc = Class.forName("com.kreative.paint.ui.mac.CKPApplicationListener");
									Constructor<?> alk = alc.getConstructor(CKPApplication.class);
									alk.newInstance(app);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					});
					thr2.start();
				}
			});
			thr1.start();
		}
	}
}
