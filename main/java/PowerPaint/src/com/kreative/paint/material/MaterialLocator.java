package com.kreative.paint.material;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MaterialLocator {
	public static final String ROOT_VENDOR = "Kreative";
	public static final String ROOT_NAME = "Paint Materials";
	
	private final String appVendor;
	private final String appNameFull;
	private final String appNameNormalized;
	private final String rootVendor;
	private final String rootNameFull;
	private final String rootNameNormalized;
	private final List<File> roots;
	
	public MaterialLocator(String appVendor, String appName) {
		this.appVendor = appVendor;
		this.appNameFull = appName;
		this.appNameNormalized = appName.replaceAll("\\P{L}+", "").toLowerCase();
		this.rootVendor = ROOT_VENDOR;
		this.rootNameFull = ROOT_NAME;
		this.rootNameNormalized = ROOT_NAME.replaceAll("\\P{L}+", "").toLowerCase();
		this.roots = new ArrayList<File>();
	}
	
	private static final String MAC_OS = "mac os";
	private static final String MAC_OS_LIBRARY = "Library";
	private static final String MAC_OS_APP_SUPPORT = "Application Support";
	private static final String WINDOWS = "windows";
	private static final String WINDOWS_DNS = "Documents and Settings";
	private static final String WINDOWS_USERS = "Users";
	private static final String WINDOWS_ALL_USERS = "All Users";
	private static final String WINDOWS_APP_DATA = "Application Data";
	private static final String LINUX_ETC = "etc";
	
	public List<File> listRoots() {
		if (roots.isEmpty()) {
			try {
				String osName = System.getProperty("os.name").toLowerCase();
				File home = new File(System.getProperty("user.home"));
				if (osName.contains(MAC_OS)) {
					File lib = new File(home, MAC_OS_LIBRARY);
					File as = new File(lib, MAC_OS_APP_SUPPORT);
					roots.add(new File(as, appNameFull));
					roots.add(new File(as, rootNameFull));
					roots.add(new File(lib, appNameFull));
					roots.add(new File(lib, rootNameFull));
					roots.add(new File(home, appNameFull));
					roots.add(new File(home, rootNameFull));
					roots.add(new File(home, "." + appNameNormalized));
					roots.add(new File(home, "." + rootNameNormalized));
					for (File root : File.listRoots()) {
						lib = new File(root, MAC_OS_LIBRARY);
						as = new File(lib, MAC_OS_APP_SUPPORT);
						roots.add(new File(as, appNameFull));
						roots.add(new File(as, rootNameFull));
						roots.add(new File(lib, appNameFull));
						roots.add(new File(lib, rootNameFull));
					}
				} else if (osName.contains(WINDOWS)) {
					File ad = new File(home, WINDOWS_APP_DATA);
					roots.add(new File(new File(ad, appVendor), appNameFull));
					roots.add(new File(new File(ad, rootVendor), rootNameFull));
					roots.add(new File(ad, appNameFull));
					roots.add(new File(ad, rootNameFull));
					roots.add(new File(home, appNameFull));
					roots.add(new File(home, rootNameFull));
					for (File root : File.listRoots()) {
						File docs = new File(root, WINDOWS_DNS);
						if (docs.exists()) {
							File all = new File(docs, WINDOWS_ALL_USERS);
							ad = new File(all, WINDOWS_APP_DATA);
							roots.add(new File(new File(ad, appVendor), appNameFull));
							roots.add(new File(new File(ad, rootVendor), rootNameFull));
							roots.add(new File(ad, appNameFull));
							roots.add(new File(ad, rootNameFull));
						}
						File users = new File(root, WINDOWS_USERS);
						if (users.exists()) {
							File all = new File(users, WINDOWS_ALL_USERS);
							ad = new File(all, WINDOWS_APP_DATA);
							roots.add(new File(new File(ad, appVendor), appNameFull));
							roots.add(new File(new File(ad, rootVendor), rootNameFull));
							roots.add(new File(ad, appNameFull));
							roots.add(new File(ad, rootNameFull));
						}
					}
				} else {
					roots.add(new File(home, appNameFull));
					roots.add(new File(home, rootNameFull));
					roots.add(new File(home, "." + appNameNormalized));
					roots.add(new File(home, "." + rootNameNormalized));
					for (File root : File.listRoots()) {
						File etc = new File(root, LINUX_ETC);
						roots.add(new File(etc, appNameFull));
						roots.add(new File(etc, rootNameFull));
						roots.add(new File(etc, appNameNormalized));
						roots.add(new File(etc, rootNameNormalized));
					}
				}
			} catch (Exception ignored) {
				System.err.println("Warning: Could not determine platform-specific locations of materials. Defaulting to current directory.");
			}
			roots.add(new File(ROOT_NAME).getAbsoluteFile());
		}
		return roots;
	}
	
	public File getFirstAvailableRoot() {
		for (File root : listRoots()) {
			if (root.exists() && root.isDirectory()) {
				return root;
			}
		}
		return null;
	}
	
	public MaterialLoader getMaterialLoader() {
		File root = getFirstAvailableRoot();
		if (root == null) return null;
		return new MaterialLoader.FromFile(root);
	}
}
