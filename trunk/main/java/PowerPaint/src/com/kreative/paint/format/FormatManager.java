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

package com.kreative.paint.format;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import com.kreative.paint.res.MaterialsManager;
import com.kreative.paint.util.OSUtils;

public class FormatManager {
	private static final long serialVersionUID = 1L;
	
	private Collection<Format> formats;
	
	public FormatManager(MaterialsManager mm) {
		formats = new HashSet<Format>();
		formats.add(new PowerPaintFormat());
		formats.add(new PNGFormat());
		formats.add(new GIFFormat());
		formats.add(new JPEGFormat());
		formats.add(new BMPFormat());
		formats.add(new WBMPFormat());
		formats.add(new PICTFormat());
		formats.add(new MacPaintFormat());
		formats.add(new SuperPaintFormat());
		formats.add(new PBMFormat());
		formats.add(new PGMFormat());
		formats.add(new PPMFormat());
		formats.add(new PNMFormat());
		formats.add(new PAMFormat());
		formats.add(new RaaBitsFormat());
		formats.add(new WOBAFormat());
		formats.add(new SICFormat());
		formats.addAll(mm.getPluginFormats());
	}
	
	public List<Format> toSortedList() {
		Vector<Format> v = new Vector<Format>();
		v.addAll(formats);
		Collections.sort(v, new Comparator<Format>() {
			public int compare(Format o1, Format o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			}
		});
		return v;
	}
	
	public int getMaximumMagicLength() {
		int ml = 0;
		for (Format f : formats) {
			if (f.usesMagic() > ml) ml = f.usesMagic();
		}
		return ml;
	}
	
	public Format getFormatForMagic(byte[] start, long length, boolean write) {
		for (Format f : formats) {
			if ((write ? f.supportsWrite() : f.supportsRead()) && !f.onlyUponRequest() && f.usesMagic() > 0 && f.acceptsMagic(start, length)) return f;
		}
		return null;
	}
	
	public Format getFormatForExtension(String ext, boolean write) {
		ext = ext.trim().toLowerCase();
		if (ext.startsWith(".")) {
			ext = ext.substring(1).trim();
		}
		for (Format f : formats) {
			if ((write ? f.supportsWrite() : f.supportsRead()) && !f.onlyUponRequest() && f.acceptsExtension(ext)) return f;
		}
		return null;
	}
	
	public Format getFormatForMacFileType(int type, boolean write) {
		for (Format f : formats) {
			if ((write ? f.supportsWrite() : f.supportsRead()) && !f.onlyUponRequest() && f.acceptsMacFileType(type)) return f;
		}
		return null;
	}
	
	public Format getFormatForMacResourceType(int type, boolean write) {
		for (Format f : formats) {
			if ((write ? f.supportsWrite() : f.supportsRead()) && !f.onlyUponRequest() && f.acceptsMacResourceType(type)) return f;
		}
		return null;
	}
	
	public Format getFormatForDFFType(long type, boolean write) {
		for (Format f : formats) {
			if ((write ? f.supportsWrite() : f.supportsRead()) && !f.onlyUponRequest() && f.acceptsDFFType(type)) return f;
		}
		return null;
	}
	
	public Format getFormatForData(byte[] data, boolean write) {
		return getFormatForMagic(data, data.length, write);
	}
	
	public Format getFormatForFileName(String fn, boolean write) {
		String[] cmp = fn.replaceAll("^\\.+","").split("\\.");
		String ext = (cmp.length > 1) ? cmp[cmp.length-1].trim().toLowerCase() : null;
		if (ext != null && ext.length() > 0) return getFormatForExtension(ext, write);
		else return null;
	}
	
	public Format getFormatForFile(File f, boolean write) {
		try {
			byte[] magic = new byte[getMaximumMagicLength()];
			FileInputStream in = new FileInputStream(f);
			in.read(magic);
			in.close();
			for (Format fmt : formats) {
				if ((write ? fmt.supportsWrite() : fmt.supportsRead()) && !fmt.onlyUponRequest() && fmt.usesMagic() > 0 && fmt.acceptsMagic(magic, f.length())) return fmt;
			}
			String[] cmp = f.getName().replaceAll("^\\.+","").split("\\.");
			String ext = (cmp.length > 1) ? cmp[cmp.length-1].trim().toLowerCase() : null;
			if (ext != null && ext.length() > 0) {
				for (Format fmt : formats) {
					if ((write ? fmt.supportsWrite() : fmt.supportsRead()) && !fmt.onlyUponRequest() && fmt.usesMagic() <= 0 && fmt.acceptsExtension(ext)) return fmt;
				}
			}
			try {
				if (OSUtils.isMacOS()) {
					Class<?> fm = Class.forName("com.apple.eio.FileManager");
					java.lang.reflect.Method gft = fm.getDeclaredMethod("getFileType", String.class);
					int type = ((Number)gft.invoke(null, f.getAbsolutePath())).intValue();
					if (type != 0) {
						for (Format fmt : formats) {
							if ((write ? fmt.supportsWrite() : fmt.supportsRead()) && !fmt.onlyUponRequest() && fmt.acceptsMacFileType(type)) return fmt;
						}
					}
				}
			} catch (Exception e) {}
			return null;
		} catch (IOException ioe) {
			return null;
		}
	}
	
	public void postProcess(File f, Format fmt) {
		try {
			if (OSUtils.isMacOS()) {
				Class<?> fm = Class.forName("com.apple.eio.FileManager");
				java.lang.reflect.Method sft = fm.getDeclaredMethod("setFileType", String.class, int.class);
				sft.invoke(null, f.getAbsolutePath(), fmt.getMacFileType());
				java.lang.reflect.Method sfc = fm.getDeclaredMethod("setFileCreator", String.class, int.class);
				sfc.invoke(null, f.getAbsolutePath(), 0x4B504E54);
			}
		} catch (Exception e) {}
	}
}
