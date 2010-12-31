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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnpackPMZ {
	private static boolean verbose = false;
	
	public static void main(String[] args) throws IOException {
		String path = "materials.pmz";
		for (String arg : args) {
			if (arg.startsWith("-")) {
				if (arg.equals("-V")) {
					verbose = true;
				} else {
					System.err.println("Unknown option: " + arg);
				}
			} else {
				path = arg;
			}
		}
		unpack(new File(path));
	}
	
	public static void unpack(URL url) throws IOException {
		URLConnection uc = url.openConnection();
		InputStream in = uc.getInputStream();
		unpack(in);
		in.close();
	}
	
	public static void unpack(File input) throws IOException {
		FileInputStream fis = new FileInputStream(input);
		unpack(fis);
		fis.close();
	}
	
	public static void unpack(InputStream input) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(input);
		ZipInputStream zis = new ZipInputStream(bis);
		File root = FileResourceManager.instance.getResourcesRoot();
		if (!root.exists()) root.mkdirs();
		while (unzip(zis, root));
		zis.close();
		bis.close();
	}
	
	private static boolean unzip(ZipInputStream zis, File base) throws IOException {
		ZipEntry z = zis.getNextEntry();
		if (z == null) return false;
		if (!z.isDirectory()) {
			if (verbose) System.out.println(z.getName());
			File file = base;
			String[] names = z.getName().split("/");
			for (String name : names) {
				if (!file.exists()) file.mkdir();
				file = new File(file, name);
			}
			if (file.getName().equalsIgnoreCase(".icon.rsrc")) {
				if (osName().toLowerCase().contains("mac os")) {
					File theFolder = file.getParentFile();
					File theIconFile = new File(theFolder, "Icon\r");
					theIconFile.createNewFile();
					unzipdata(zis, new File(new File(theIconFile, "..namedfork"), "rsrc"));
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
			} else if (file.getName().equalsIgnoreCase(".desktop.ini") || file.getName().equalsIgnoreCase(".desktop.ico")) {
				if (osName().toLowerCase().contains("windows")) {
					File theFile = new File(file.getParentFile(), file.getName().substring(1));
					unzipdata(zis, theFile);
					try {
						Runtime.getRuntime().exec("attrib +h +s " + theFile.getAbsolutePath()).waitFor();
					} catch (InterruptedException ignored) {}
				}
			} else if (file.getName().equalsIgnoreCase(".directory") || file.getName().equalsIgnoreCase(".icon.png")) {
				if (!(osName().toLowerCase().contains("mac os") || osName().toLowerCase().contains("windows"))) {
					unzipdata(zis, file);
				}
			} else {
				unzipdata(zis, file);
				if (osName().toLowerCase().contains("mac os")) {
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
		return true;
	}
	
	private static void unzipdata(ZipInputStream zis, File file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		byte[] buf = new byte[65536];
		int len = 0;
		while ((len = zis.read(buf, 0, buf.length)) >= 0) {
			bos.write(buf, 0, len);
		}
		bos.flush();
		fos.flush();
		bos.close();
		fos.close();
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
