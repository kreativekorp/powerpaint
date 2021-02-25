/*
 * Copyright &copy; 2010-2021 Rebecca G. Bettencourt / Kreative Software
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

package com.kreative.paint.ui.mac;

import java.awt.desktop.AboutEvent;
import java.awt.desktop.AboutHandler;
import java.awt.desktop.OpenFilesEvent;
import java.awt.desktop.OpenFilesHandler;
import java.awt.desktop.PrintFilesEvent;
import java.awt.desktop.PrintFilesHandler;
import java.awt.desktop.QuitEvent;
import java.awt.desktop.QuitHandler;
import java.awt.desktop.QuitResponse;
import java.io.File;
import java.lang.reflect.Method;
import com.kreative.paint.ui.CKPApplication;

public class CKPApplicationListener {
	private static final String[][] classAndMethodNames = {
		{ "java.awt.Desktop", "getDesktop" },
		{ "com.kreative.ual.eawt.NewApplicationAdapter", "getInstance" },
		{ "com.kreative.ual.eawt.OldApplicationAdapter", "getInstance" },
	};
	
	private final CKPApplication app;
	
	public CKPApplicationListener(final CKPApplication app) {
		this.app = app;
		for (String[] classAndMethodName : classAndMethodNames) {
			try {
				Class<?> cls = Class.forName(classAndMethodName[0]);
				Method getInstance = cls.getMethod(classAndMethodName[1]);
				Object instance = getInstance.invoke(null);
				cls.getMethod("setAboutHandler", AboutHandler.class).invoke(instance, about);
				cls.getMethod("setOpenFileHandler", OpenFilesHandler.class).invoke(instance, open);
				cls.getMethod("setPrintFileHandler", PrintFilesHandler.class).invoke(instance, print);
				cls.getMethod("setQuitHandler", QuitHandler.class).invoke(instance, quit);
				System.out.println("Registered app event handlers through " + classAndMethodName[0]);
				return;
			} catch (Exception e) {
				System.out.println("Failed to register app event handlers through " + classAndMethodName[0] + ": " + e);
			}
		}
	}
	
	private final AboutHandler about = new AboutHandler() {
		@Override
		public void handleAbout(final AboutEvent e) {
			new Thread() {
				public void run() {
					app.doAbout();
				}
			}.start();
		}
	};
	
	private final OpenFilesHandler open = new OpenFilesHandler() {
		@Override
		public void openFiles(final OpenFilesEvent e) {
			new Thread() {
				public void run() {
					for (Object o : e.getFiles()) {
						app.doOpen((File)o);
					}
				}
			}.start();
		}
	};
	
	private final PrintFilesHandler print = new PrintFilesHandler() {
		@Override
		public void printFiles(final PrintFilesEvent e) {
			new Thread() {
				public void run() {
					for (Object o : e.getFiles()) {
						app.doPrint((File)o);
					}
				}
			}.start();
		}
	};
	
	private final QuitHandler quit = new QuitHandler() {
		@Override
		public void handleQuitRequestWith(final QuitEvent e, final QuitResponse r) {
			new Thread() {
				public void run() {
					app.doQuit();
					r.cancelQuit();
				}
			}.start();
		}
	};
}
