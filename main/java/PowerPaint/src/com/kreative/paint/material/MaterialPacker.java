package com.kreative.paint.material;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class MaterialPacker {
	public static void main(String[] args) throws IOException {
		Iterator<String> a = Arrays.asList(args).iterator();
		if (a.hasNext()) {
			String verb = a.next();
			if (verb.equalsIgnoreCase("pack")) {
				if (a.hasNext()) {
					File out = new File(a.next());
					List<File> in = new ArrayList<File>();
					while (a.hasNext()) in.add(new File(a.next()));
					try { zip(out, in); }
					catch (IOException e) { e.printStackTrace(); }
					return;
				}
			} else if (verb.equalsIgnoreCase("unpack")) {
				if (a.hasNext()) {
					File in = new File(a.next());
					if (a.hasNext()) {
						File out = new File(a.next());
						if (!a.hasNext()) {
							try { unzip(in, out); }
							catch (IOException e) { e.printStackTrace(); }
							return;
						}
					}
				}
			} else if (verb.equalsIgnoreCase("decorate")) {
				if (a.hasNext()) {
					String creator = a.next();
					while (a.hasNext()) {
						File file = new File(a.next());
						decorate(file, creator);
					}
					return;
				}
			}
		}
		System.err.println("Usage:");
		System.err.println("  pack <zipfile> <file> ...");
		System.err.println("  unpack <zipfile> <directory>");
		System.err.println("  decorate <creator> <file> ...");
	}
	
	public static void zip(File out, List<File> files) throws IOException {
		FileOutputStream fos = new FileOutputStream(out);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		ZipOutputStream zos = new ZipOutputStream(bos);
		for (File f : files) zipFile(zos, f, "");
		zos.close();
		bos.close();
		fos.close();
	}
	
	public static void unzip(File file, File base) throws IOException {
		FileInputStream in = new FileInputStream(file);
		unzip(in, base);
		in.close();
	}
	
	public static void unzip(URL url, File base) throws IOException {
		URLConnection uc = url.openConnection();
		InputStream in = uc.getInputStream();
		unzip(in, base);
		in.close();
	}
	
	public static void unzip(InputStream in, File base) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(in);
		ZipInputStream zis = new ZipInputStream(bis);
		while (unzipFile(zis, base));
		zis.close();
		bis.close();
	}
	
	public static void decorate(File file, String creator) {
		int creatorInt = 0;
		if (creator != null && creator.length() > 0) {
			byte[] creatorBytes;
			try { creatorBytes = creator.getBytes("MacRoman"); }
			catch (IOException e) { creatorBytes = creator.getBytes(); }
			int n = Math.min(creatorBytes.length, 4);
			for (int i = 0, s = 24; i < n; i++, s -= 8)
				creatorInt |= ((creatorBytes[i] & 0xFF) << s);
			for (int i = n, s = 0; i < 4; i++, s += 8)
				creatorInt |= (0x20 << s);
		}
		decorateFile(file, creatorInt);
	}
	
	public static void decorate(File file, int creator) {
		decorateFile(file, creator);
	}
	
	private static void zipFile(ZipOutputStream zos, File file, String prefix) throws IOException {
		String name = file.getName();
		if (ignoreFileName(name)) return;
		if (file.isDirectory()) {
			String childPrefix = prefix + name + "/";
			File[] children = file.listFiles();
			Arrays.sort(children, new FileComparator());
			for (File child : children) zipFile(zos, child, childPrefix);
		} else {
			ZipEntry z = new ZipEntry(prefix + name);
			if (isIncompressible(name)) {
				z.setMethod(ZipEntry.STORED);
				z.setSize(file.length());
				z.setCompressedSize(file.length());
				z.setCrc(crc32(file));
			}
			zos.putNextEntry(z);
			zipdata(zos, file);
		}
	}
	
	private static boolean unzipFile(ZipInputStream zis, File base) throws IOException {
		ZipEntry z = zis.getNextEntry();
		if (z == null) return false;
		if (z.isDirectory()) return true;
		File file = base;
		String[] names = z.getName().split("/");
		for (String name : names) {
			if (!file.exists()) file.mkdir();
			file = new File(file, name);
		}
		unzipdata(zis, file);
		return true;
	}
	
	private static void decorateFile(File file, int creator) {
		String name = file.getName();
		if (ignoreFileName(name)) return;
		if (file.isDirectory()) {
			if (isBundle(name)) makeBundle(file);
			File[] children = file.listFiles();
			for (File child : children) decorateFile(child, creator);
		} else {
			int type = getMacType(name);
			setMacTypeCreator(file, type, creator);
		}
	}
	
	private static boolean ignoreFileName(String name) {
		return name.startsWith(".")
		    || name.contains("\r")
		    || name.contains("\n")
		    || name.contains("\uF00D")
		    || name.contains("\uF00A")
		    || name.equalsIgnoreCase("Thumbs.db")
		    || name.equalsIgnoreCase("Desktop.ini")
		    || name.equalsIgnoreCase("Desktop.ico");
	}
	
	private static class FileComparator implements Comparator<File> {
		@Override
		public int compare(File a, File b) {
			return a.getName().compareTo(b.getName());
		}
	}
	
	private static boolean isIncompressible(String name) {
		name = name.toLowerCase();
		return name.endsWith(".png")
		    || name.endsWith(".zip")
		    || name.endsWith(".jar")
		    || name.endsWith(".pmz");
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
	
	private static boolean isBundle(String name) {
		name = name.toLowerCase();
		return name.endsWith(".frnd")
		    || name.endsWith(".spnd")
		    || name.endsWith(".txrd");
	}
	
	private static void makeBundle(File file) {
		try {
			Runtime.getRuntime().exec(new String[]{
				"/usr/bin/SetFile", "-a", "B",
				file.getAbsolutePath()
			}).waitFor();
		} catch (Throwable e) {}
	}
	
	private static int getMacType(String name) {
		name = name.toLowerCase();
		if (name.endsWith(".png")) return 0x504E4720;
		if (name.endsWith(".zip")) return 0x5A495020;
		if (name.endsWith(".jar")) return 0x4A415220;
		if (name.endsWith(".pmz")) return 0x504D5A20;
		if (name.endsWith(".ttf")) return 0x73666E74;
		if (name.endsWith(".alpx")) return 0x414C5058;
		if (name.endsWith(".ditx")) return 0x44495458;
		if (name.endsWith(".frnx")) return 0x46524E58;
		if (name.endsWith(".grdx")) return 0x47524458;
		if (name.endsWith(".lnsx")) return 0x4C4E5358;
		if (name.endsWith(".patx")) return 0x50415458;
		if (name.endsWith(".rcpx")) return 0x52435058;
		if (name.endsWith(".rfpx")) return 0x52465058;
		if (name.endsWith(".shpx")) return 0x53485058;
		if (name.endsWith(".spnx")) return 0x53504E58;
		if (name.endsWith(".txrx")) return 0x54585258;
		return 0;
	}
	
	private static void setMacTypeCreator(File file, int type, int creator) {
		try {
			Class<?> fm = Class.forName("com.apple.eio.FileManager");
			Method sft = fm.getDeclaredMethod("setFileType", String.class, int.class);
			sft.invoke(null, file.getAbsolutePath(), type);
			Method sfc = fm.getDeclaredMethod("setFileCreator", String.class, int.class);
			sfc.invoke(null, file.getAbsolutePath(), creator);
		} catch (Throwable e) {}
	}
}
