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

package com.kreative.paint.res;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

public class FileResourceManager extends ResourceManager {
	public static final FileResourceManager instance = new FileResourceManager();
	
	private List<File> resourcesRoots = null;
	private File resourcesRoot = null;
	
	private FileResourceManager() {}
	
	public List<File> getResourcesRoots() {
		if (resourcesRoots == null) {
			resourcesRoots = new Vector<File>();
			try {
				String osName = System.getProperty("os.name").toLowerCase();
				File home = new File(System.getProperty("user.home"));
				if (osName.contains("mac os")) {
					File lib = new File(home, "Library");
					File appsup = new File(lib, "Application Support");
					resourcesRoots.add(new File(appsup, "PowerPaint")); // In this user's Application Support directory.
					resourcesRoots.add(new File(appsup, "Paint Materials"));
					resourcesRoots.add(new File(lib, "PowerPaint")); // In this user's Library directory.
					resourcesRoots.add(new File(lib, "Paint Materials"));
					resourcesRoots.add(new File(home, "PowerPaint")); // Visible in this user's home directory.
					resourcesRoots.add(new File(home, "Paint Materials"));
					resourcesRoots.add(new File(home, ".powerpaint")); // Hidden in this user's home directory.
					resourcesRoots.add(new File(home, ".paintmaterials"));
				} else if (osName.contains("windows")) {
					File appdata = new File(home, "Application Data");
					File krea = new File(appdata, "Kreative");
					resourcesRoots.add(new File(krea, "PowerPaint")); // In this user's Application Data\Kreative directory.
					resourcesRoots.add(new File(krea, "Paint Materials"));
					resourcesRoots.add(new File(appdata, "PowerPaint")); // In this user's Application Data directory.
					resourcesRoots.add(new File(appdata, "Paint Materials"));
					resourcesRoots.add(new File(home, "PowerPaint")); // In this user's home directory.
					resourcesRoots.add(new File(home, "Paint Materials"));
				} else {
					resourcesRoots.add(new File(home, "PowerPaint")); // Visible in this user's home directory.
					resourcesRoots.add(new File(home, "Paint Materials"));
					resourcesRoots.add(new File(home, ".powerpaint")); // Hidden in this user's home directory.
					resourcesRoots.add(new File(home, ".paintmaterials"));
				}
				for (File root : File.listRoots()) {
					if (osName.contains("mac os")) {
						File lib = new File(root, "Library");
						File appsup = new File(lib, "Application Support");
						resourcesRoots.add(new File(appsup, "PowerPaint")); // In this computer's Application Support directory.
						resourcesRoots.add(new File(appsup, "Paint Materials"));
						resourcesRoots.add(new File(lib, "PowerPaint")); // In this computer's Library directory.
						resourcesRoots.add(new File(lib, "Paint Materials"));
					} else if (osName.contains("windows")) {
						File docs = new File(root, "Documents and Settings");
						File users = new File(root, "Users");
						if (docs.exists()) {
							File appdata = new File(new File(docs, "All Users"), "Application Data");
							File krea = new File(appdata, "Kreative");
							resourcesRoots.add(new File(krea, "PowerPaint")); // In this computer's Application Data\Kreative directory.
							resourcesRoots.add(new File(krea, "Paint Materials"));
							resourcesRoots.add(new File(appdata, "PowerPaint")); // In this computer's Application Data directory.
							resourcesRoots.add(new File(appdata, "Paint Materials"));
						}
						if (users.exists()) {
							File appdata = new File(new File(users, "All Users"), "Application Data");
							File krea = new File(appdata, "Kreative");
							resourcesRoots.add(new File(krea, "PowerPaint")); // In this computer's Application Data\Kreative directory.
							resourcesRoots.add(new File(krea, "Paint Materials"));
							resourcesRoots.add(new File(appdata, "PowerPaint")); // In this computer's Application Data directory.
							resourcesRoots.add(new File(appdata, "Paint Materials"));
						}
					} else {
						File etc = new File(root, "etc");
						resourcesRoots.add(new File(etc, "PowerPaint")); // In this computer's etc directory.
						resourcesRoots.add(new File(etc, "Paint Materials"));
						resourcesRoots.add(new File(etc, "powerpaint"));
						resourcesRoots.add(new File(etc, "paintmaterials"));
					}
				}
			} catch (Exception ignored) {}
			resourcesRoots.add(new File("Paint Materials").getAbsoluteFile()); // Alongside PowerPaint itself.
		}
		return resourcesRoots;
	}
	
	public File getResourcesRoot() {
		if (resourcesRoot == null) {
			try {
				String usd = System.getProperty("com.kreative.paint.materials");
				if (usd != null && usd.length() > 0) return (resourcesRoot = new File(usd));
			} catch (Exception ignored) {}
			List<File> locs = getResourcesRoots();
			for (File loc : locs)
				if (loc.exists())
					return (resourcesRoot = loc);
			return resourcesRoot = null;
		} else {
			return resourcesRoot;
		}
	}
	
	public void setResourcesRoot(File root) {
		resourcesRoot = root;
	}
	
	public List<String> getResourceNames(ResourceCategory category) {
		List<String> names = new Vector<String>();
		File root = getResourcesRoot(); if (!root.exists()) root.mkdir();
		File rroot = new File(root, category.getDirectoryName()); if (!rroot.exists()) rroot.mkdir();
		getResourceNames("", rroot, names);
		return names;
	}
	
	public List<Resource> getResources(ResourceCategory category) {
		List<Resource> resources = new Vector<Resource>();
		File root = getResourcesRoot(); if (!root.exists()) root.mkdir();
		File rroot = new File(root, category.getDirectoryName()); if (!rroot.exists()) rroot.mkdir();
		getResources("", rroot, resources);
		return resources;
	}
	
	private List<String> getResourceNames(String prefix, File rroot, List<String> names) {
		File[] files = rroot.listFiles();
		Arrays.sort(files, COMPARATOR);
		for (File f : files) {
			String name = f.getName();
			if (name.startsWith(".") || name.contains("\r") || name.contains("\n") || name.contains("\uF00D") || name.contains("\uF00A")) {
				continue;
			} else if (name.equalsIgnoreCase("Thumbs.db") || name.equalsIgnoreCase("Desktop.ini") || name.equalsIgnoreCase("Desktop.ico")) {
				continue;
			} else if (f.isDirectory()) {
				name = name.replaceFirst("^#[0-9]+ ", "");
				name = name.replaceFirst("\\.[a-zA-Z0-9]+$", "");
				getResourceNames(prefix+name.trim()+"/", f, names);
			} else {
				name = name.replaceFirst("^#[0-9]+ ", "");
				name = name.replaceFirst("\\.[a-zA-Z0-9]+$", "");
				names.add(prefix+name.trim());
			}
		}
		return names;
	}
	
	private List<Resource> getResources(String prefix, File rroot, List<Resource> resources) {
		File[] files = rroot.listFiles();
		Arrays.sort(files, COMPARATOR);
		for (File f : files) {
			String name = f.getName();
			if (name.startsWith(".") || name.contains("\r") || name.contains("\n") || name.contains("\uF00D") || name.contains("\uF00A")) {
				continue;
			} else if (name.equalsIgnoreCase("Thumbs.db") || name.equalsIgnoreCase("Desktop.ini") || name.equalsIgnoreCase("Desktop.ico")) {
				continue;
			} else if (f.isDirectory()) {
				name = name.replaceFirst("^#[0-9]+ ", "");
				name = name.replaceFirst("\\.[a-zA-Z0-9]+$", "");
				getResources(prefix+name.trim()+"/", f, resources);
			} else try {
				RandomAccessFile raf = new RandomAccessFile(f, "r");
				byte[] b = new byte[(int)raf.length()];
				raf.readFully(b);
				raf.close();
				name = name.replaceFirst("^#[0-9]+ ", "");
				name = name.replaceFirst("\\.[a-zA-Z0-9]+$", "");
				resources.add(new Resource(prefix+name.trim(), b));
			} catch (IOException e) {
				System.err.println("Could not read "+f.getName());
			}
		}
		return resources;
	}
	
	private static final Comparator<File> COMPARATOR = new Comparator<File>() {
		public int compare(File o1, File o2) {
			return o1.getName().compareTo(o2.getName());
		}
	};
}
