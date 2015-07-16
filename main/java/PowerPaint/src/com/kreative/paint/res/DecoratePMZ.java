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

package com.kreative.paint.res;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class DecoratePMZ {
	private static boolean verbose = false;
	
	public static void main(String[] args) throws IOException {
		boolean doneOne = false;
		for (String arg : args) {
			if (arg.startsWith("-")) {
				if (arg.equals("-V")) {
					verbose = true;
				} else {
					System.err.println("Unknown option: " + arg);
				}
			} else {
				decorate(new File(arg));
				doneOne = true;
			}
		}
		if (!doneOne) decorate();
	}
	
	public static void decorate() throws IOException {
		File root = FileResourceManager.instance.getResourcesRoot();
		if (!root.exists()) root.mkdirs();
		decorate(root);
	}
	
	private static void decorate(File file) throws IOException {
		String name = file.getName();
		if (file.isDirectory()) {
			if (!(
					name.startsWith(".")
					|| name.contains("\r")
					|| name.contains("\n")
					|| name.contains("\uF00D")
					|| name.contains("\uF00A")
					|| name.equalsIgnoreCase("Thumbs.db")
					|| name.equalsIgnoreCase("Desktop.ini")
					|| name.equalsIgnoreCase("Desktop.ico")
			)) {
				for (File cf : file.listFiles()) {
					decorate(cf);
				}
			}
		} else {
			if (name.equalsIgnoreCase(".icon.rsrc")) {
				if (osName().toLowerCase().contains("mac os")) {
					if (verbose) System.out.println(file.getName());
					File theFolder = file.getParentFile();
					File theIconFile = new File(theFolder, "Icon\r");
					theIconFile.createNewFile();
					copydata(file, new File(new File(theIconFile, "..namedfork"), "rsrc"));
					try {
						Runtime.getRuntime().exec(new String[]{
								"/usr/bin/SetFile",
								"-a", "VC",
								theIconFile.getAbsolutePath()
						}).waitFor();
					} catch (InterruptedException ignored) {}
					try {
						Runtime.getRuntime().exec(new String[]{
								"/usr/bin/SetFile",
								"-a", "C",
								theFolder.getAbsolutePath()
						}).waitFor();
					} catch (InterruptedException ignored) {}
				}
			} else if (name.equalsIgnoreCase(".desktop.ini") || name.equalsIgnoreCase(".desktop.ico")) {
				if (osName().toLowerCase().contains("windows")) {
					if (verbose) System.out.println(file.getName());
					File theFile = new File(file.getParentFile(), file.getName().substring(1));
					copydata(file, theFile);
					try {
						Runtime.getRuntime().exec("attrib +h +s " + theFile.getAbsolutePath()).waitFor();
					} catch (InterruptedException ignored) {}
				}
			} else if (name.equalsIgnoreCase(".directory") || name.equalsIgnoreCase(".icon.png")) {
				if (!(osName().toLowerCase().contains("mac os") || osName().toLowerCase().contains("windows"))) {
					if (verbose) System.out.println(file.getName());
					// file is already named what it should be named
				}
			} else if (!(
					name.startsWith(".")
					|| name.contains("\r")
					|| name.contains("\n")
					|| name.contains("\uF00D")
					|| name.contains("\uF00A")
					|| name.equalsIgnoreCase("Thumbs.db")
					|| name.equalsIgnoreCase("Desktop.ini")
					|| name.equalsIgnoreCase("Desktop.ico")
			)) {
				if (osName().toLowerCase().contains("mac os")) {
					if (verbose) System.out.println(file.getName());
					try {
						String n = file.getName().toLowerCase();
						int macType =
							n.endsWith(".png") ? 0x504E4720 :
							n.endsWith(".ttf") ? 0x73666E74 :
							n.endsWith(".rcp") ? 0x52435020 :
							n.endsWith(".rfp") ? 0x52465020 :
							n.endsWith(".grd") ? 0x47524420 :
							n.endsWith(".pat") ? 0x50415420 :
							n.endsWith(".alph") ? 0x414C5048 :
							n.endsWith(".lines") ? 0x4C494E45 :
							n.endsWith(".shapes") ? 0x53485045 :
							n.endsWith(".dither") ? 0x44495448 :
							0;
						Class<?> fm = Class.forName("com.apple.eio.FileManager");
						java.lang.reflect.Method sft = fm.getDeclaredMethod("setFileType", String.class, int.class);
						sft.invoke(null, file.getAbsolutePath(), macType);
						java.lang.reflect.Method sfc = fm.getDeclaredMethod("setFileCreator", String.class, int.class);
						sfc.invoke(null, file.getAbsolutePath(), 0x4B504E54);
					} catch (Exception ignored) {}
				}
			}
		}
	}
	
	private static void copydata(File src, File dst) throws IOException {
		FileInputStream in = new FileInputStream(src);
		FileOutputStream out = new FileOutputStream(dst);
		byte[] buf = new byte[1048576];
		int len = 0;
		while ((len = in.read(buf)) >= 0) {
			out.write(buf, 0, len);
		}
		out.flush();
		out.close();
		in.close();
	}
	
	private static String osName = null;
	private static String osName() {
		if (osName != null) return osName;
		else try {
			osName = System.getProperty("os.name");
			return osName;
		} catch (Exception e) {
			osName = null;
			return "";
		}
	}
}
