package com.kreative.paint.material;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class JarLoader extends ClassLoader {
	private final MaterialLoader loader;
	private final Map<String,MaterialResource> knownJars;
	private final Map<String,byte[]> knownData;
	private final Map<String,Class<?>> knownClasses;
	
	public JarLoader(MaterialLoader loader) {
		this.loader = loader;
		this.knownJars = new LinkedHashMap<String,MaterialResource>();
		this.knownData = new HashMap<String,byte[]>();
		this.knownClasses = new HashMap<String,Class<?>>();
	}
	
	public List<Class<?>> listClasses() {
		if (knownJars.isEmpty()) loadResources();
		List<Class<?>> classes = new ArrayList<Class<?>>();
		for (String path : knownJars.keySet()) {
			if (path.endsWith(".class") && !path.contains("$")) {
				String cname = path.substring(0, path.length() - 6).replace('/', '.');
				try { classes.add(loadClass(cname)); }
				catch (Throwable e) { e.printStackTrace(); }
			}
		}
		return classes;
	}
	
	public <T> List<Class<? extends T>> listClasses(Class<T> superclass) {
		if (knownJars.isEmpty()) loadResources();
		List<Class<? extends T>> classes = new ArrayList<Class<? extends T>>();
		for (String path : knownJars.keySet()) {
			if (path.endsWith(".class") && !path.contains("$")) {
				String cname = path.substring(0, path.length() - 6).replace('/', '.');
				try {
					Class<?> c = loadClass(cname);
					if (superclass.isAssignableFrom(c)) {
						classes.add(c.asSubclass(superclass));
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
		return classes;
	}
	
	@Override
	protected Class<?> findClass(String cname) throws ClassNotFoundException {
		String path = cname.replace('.', '/') + ".class";
		if (knownJars.isEmpty()) loadResources();
		if (knownJars.containsKey(path)) {
			try { return getClass(path, cname); }
			catch (IOException e) {}
		}
		throw new ClassNotFoundException();
	}
	
	@Override
	protected URL findResource(String name) {
		if (knownJars.isEmpty()) loadResources();
		return (knownJars.containsKey(name)) ? getDataURL(name) : null;
	}
	
	@Override
	protected Enumeration<URL> findResources(String name) {
		if (knownJars.isEmpty()) loadResources();
		List<URL> urls = new ArrayList<URL>();
		if (knownJars.containsKey(name)) urls.add(getDataURL(name));
		return Collections.enumeration(urls);
	}
	
	private Class<?> getClass(String path, String cname) throws IOException {
		if (knownClasses.containsKey(path)) return knownClasses.get(path);
		byte[] data = getData(path);
		Class<?> c = defineClass(cname, data, 0, data.length);
		knownClasses.put(path, c);
		return c;
	}
	
	private URL getDataURL(String name) {
		URLStreamHandler ush = new InternalURLStreamHandler();
		try { return new URL(null, "material-jarloader:///" + name, ush); }
		catch (MalformedURLException e) { throw new RuntimeException(e); }
	}
	
	private class InternalURLStreamHandler extends URLStreamHandler {
		@Override
		protected URLConnection openConnection(URL url) {
			return new InternalURLConnection(url);
		}
	}
	
	private class InternalURLConnection extends URLConnection {
		public InternalURLConnection(URL url) { super(url); }
		@Override public void connect() throws IOException {}
		@Override
		public InputStream getInputStream() throws IOException {
			String name = getURL().getPath().substring(1);
			byte[] data = getData(name);
			return new ByteArrayInputStream(data);
		}
	}
	
	private byte[] getData(String name) throws IOException {
		if (knownData.containsKey(name)) return knownData.get(name);
		MaterialResource r = knownJars.get(name);
		if (r != null) {
			JarInputStream in = new JarInputStream(r.getInputStream());
			for (JarEntry e = in.getNextJarEntry(); e != null; e = in.getNextJarEntry()) {
				if (!e.isDirectory() && e.getName().equals(name)) {
					if (e.getSize() <= Integer.MAX_VALUE) {
						byte[] data = new byte[(int)e.getSize()];
						in.read(data, 0, data.length);
						in.close();
						knownData.put(name, data);
						return data;
					} else {
						in.close();
						throw new IOException("Too big: " + name);
					}
				}
			}
			in.close();
		}
		throw new IOException("Not found: " + name);
	}
	
	private void loadResources() {
		for (MaterialResource r : loader.listResources()) {
			if (r.isFormat("jar", false)) {
				try {
					JarInputStream in = new JarInputStream(r.getInputStream());
					for (JarEntry e = in.getNextJarEntry(); e != null; e = in.getNextJarEntry()) {
						if (!e.isDirectory()) {
							knownJars.put(e.getName(), r);
						}
					}
					in.close();
				} catch (IOException e) {
					System.err.println("Warning: Failed to load jar " + r.getResourceName() + ".");
					e.printStackTrace();
				}
			}
		}
	}
}
