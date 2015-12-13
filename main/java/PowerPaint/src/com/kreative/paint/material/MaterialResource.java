package com.kreative.paint.material;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class MaterialResource {
	private final String branchName;
	private final String parentName;
	private final String resourceName;
	private final String format;
	
	protected MaterialResource(
		String branchName,
		String parentName,
		String resourceName,
		String format
	) {
		this.branchName = branchName;
		this.parentName = parentName;
		this.resourceName = resourceName;
		this.format = format;
	}
	
	public final String getBranchName() { return branchName; }
	public final String getParentName() { return parentName; }
	public final String getResourceName() { return resourceName; }
	public final String getFormat() { return format; }
	
	public final boolean isFormat(String format) {
		return this.format.equalsIgnoreCase(format);
	}
	
	public final boolean isFormat(String format, boolean directory) {
		return this.format.equalsIgnoreCase(format)
		    && (this.isDirectory() == directory);
	}
	
	public abstract boolean exists();
	public abstract boolean isDirectory();
	public abstract InputStream getInputStream() throws IOException;
	public abstract MaterialResource getChild(String href);
	public abstract List<MaterialResource> listChildren();
	
	public static class FromFile extends MaterialResource {
		private static final Pattern EXTENSION_PATTERN = Pattern.compile("\\.([a-zA-Z0-9]+)$");
		
		private final File file;
		
		public FromFile(
			String branchName,
			String parentName,
			String resourceName,
			String format,
			File file
		) {
			super(branchName, parentName, resourceName, format);
			this.file = file;
		}
		
		@Override
		public boolean exists() {
			return file.exists();
		}
		
		@Override
		public boolean isDirectory() {
			return file.isDirectory();
		}
		
		@Override
		public InputStream getInputStream() throws IOException {
			return new FileInputStream(file);
		}
		
		@Override
		public MaterialResource getChild(String href) {
			FromFile child = this;
			if (href != null) {
				for (String name : href.split("/")) {
					if (name.length() > 0) {
						String branchName = child.getBranchName();
						String parentName = child.getResourceName();
						File childFile = new File(child.file, name);
						String childName = childFile.getName();
						Matcher m = EXTENSION_PATTERN.matcher(childName);
						String childFormat = m.find() ? m.group(1).toLowerCase() : "";
						child = new FromFile(branchName, parentName, childName, childFormat, childFile);
					}
				}
			}
			return child;
		}
		
		@Override
		public List<MaterialResource> listChildren() {
			List<MaterialResource> children = new ArrayList<MaterialResource>();
			String branchName = this.getBranchName();
			String parentName = this.getResourceName();
			File[] files = this.file.listFiles();
			Arrays.sort(files, new FileComparator());
			for (File childFile : files) {
				String childName = childFile.getName();
				Matcher m = EXTENSION_PATTERN.matcher(childName);
				String childFormat = m.find() ? m.group(1).toLowerCase() : "";
				FromFile child = new FromFile(branchName, parentName, childName, childFormat, childFile);
				children.add(child);
			}
			return children;
		}
		
		private static class FileComparator implements Comparator<File> {
			@Override
			public int compare(File a, File b) {
				return a.getName().compareTo(b.getName());
			}
		}
	}
}
