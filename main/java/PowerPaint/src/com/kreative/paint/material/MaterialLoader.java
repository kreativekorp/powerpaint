package com.kreative.paint.material;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class MaterialLoader {
	public abstract List<MaterialResource> listResources();
	
	public static class FromFile extends MaterialLoader {
		private static final Pattern ORDINAL_PATTERN = Pattern.compile("^#([0-9]+) ");
		private static final Pattern EXTENSION_PATTERN = Pattern.compile("\\.([a-zA-Z0-9]+)$");
		
		private final File root;
		private final List<MaterialResource> resources;
		
		public FromFile(File root) {
			this.root = root;
			this.resources = new ArrayList<MaterialResource>();
		}
		
		@Override
		public List<MaterialResource> listResources() {
			if (resources.isEmpty()) loadDirectory(resources, "", "", root);
			return resources;
		}
		
		private static void loadDirectory(
			List<MaterialResource> resources,
			String branchName,
			String parentName,
			File parent
		) {
			File[] files = parent.listFiles();
			Arrays.sort(files, new FileComparator());
			for (File file : files) {
				String name = file.getName();
				if (ignoreFileName(name)) continue;
				Matcher om = ORDINAL_PATTERN.matcher(name);
				if (om.find()) name = name.substring(om.end());
				Matcher em = EXTENSION_PATTERN.matcher(name);
				if (em.find()) {
					String format = em.group(1).toLowerCase();
					name = name.substring(0, em.start());
					resources.add(new MaterialResource.FromFile(branchName, parentName, name, format, file));
				} else if (file.isDirectory()) {
					loadDirectory(resources, ((branchName.length() > 0) ? branchName : name), name, file);
				} else {
					resources.add(new MaterialResource.FromFile(branchName, parentName, name, "", file));
				}
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
	}
}
