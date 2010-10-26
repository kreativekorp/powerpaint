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

package com.kreative.paint.res;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class PackPMZ {
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
		pack(new File(path));
	}
	
	public static void pack(File output) throws IOException {
		FileOutputStream fos = new FileOutputStream(output);
		pack(fos);
		fos.close();
	}
	
	public static void pack(OutputStream output) throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(output);
		ZipOutputStream zos = new ZipOutputStream(bos);
		File root = FileResourceManager.instance.getResourcesRoot();
		for (File f : root.listFiles()) zip(zos, f, "");
		zos.close();
		bos.close();
	}
	
	private static void zip(ZipOutputStream zos, File file, String prefix) throws IOException {
		String name = file.getName();
		if ((name.equals("Icon\r") || name.equals("Icon\uF00D")) && !file.isDirectory()) {
			if (verbose) System.out.println(prefix + ".icon.rsrc");
			ZipEntry z = new ZipEntry(prefix + ".icon.rsrc");
			zos.putNextEntry(z);
			zipdata(zos, new File(new File(file, "..namedfork"), "rsrc"));
		} else if ((name.equalsIgnoreCase("Desktop.ini") || name.equalsIgnoreCase("Desktop.ico")) && !file.isDirectory()) {
			if (verbose) System.out.println(prefix + "." + name.toLowerCase());
			ZipEntry z = new ZipEntry(prefix + "." + name.toLowerCase());
			zos.putNextEntry(z);
			zipdata(zos, file);
		} else if ((name.equalsIgnoreCase(".directory") || name.equalsIgnoreCase(".icon.png")) && !file.isDirectory()) {
			if (verbose) System.out.println(prefix + name.toLowerCase());
			ZipEntry z = new ZipEntry(prefix + name.toLowerCase());
			zos.putNextEntry(z);
			zipdata(zos, file);
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
			if (file.isDirectory()) {
				File[] ff = file.listFiles();
				for (File f : ff) {
					zip(zos, f, prefix + name + "/");
				}
			} else {
				if (verbose) System.out.println(prefix + name);
				ZipEntry z = new ZipEntry(prefix + name);
				if (name.endsWith(".png")) {
					z.setMethod(ZipEntry.STORED);
					z.setSize(file.length());
					z.setCompressedSize(file.length());
					z.setCrc(crc32(file));
				}
				zos.putNextEntry(z);
				zipdata(zos, file);
			}
		}
	}
	
	private static void zipdata(ZipOutputStream zos, File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(fis);
		byte[] buf = new byte[65536];
		int len = 0;
		while ((len = bis.read(buf, 0, buf.length)) >= 0) {
			zos.write(buf, 0, len);
		}
		bis.close();
		fis.close();
	}
	
	private static long crc32(File file) throws IOException {
		CRC32 crc = new CRC32();
		FileInputStream fis = new FileInputStream(file);
		CheckedInputStream cis = new CheckedInputStream(fis, crc);
		byte[] buf = new byte[65536];
		while (cis.read(buf, 0, buf.length) >= 0);
		cis.close();
		fis.close();
		return crc.getValue();
	}
}
