package com.kreative.paint.material.texture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class TextureParser {
	public static interface HrefResolver {
		public BufferedImage resolveHref(String href) throws IOException;
	}
	
	public static class FileHrefResolver implements HrefResolver {
		private final File root;
		public FileHrefResolver(File root) {
			this.root = root;
		}
		@Override
		public BufferedImage resolveHref(String href) throws IOException {
			return ImageIO.read(new File(root, href));
		}
	}
	
	public static TextureList parse(File root, String name, InputStream in) throws IOException {
		return parse(new FileHrefResolver(root), name, in);
	}
	
	public static TextureList parse(HrefResolver hr, String name, InputStream in) throws IOException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(true); // make sure the XML is valid
			factory.setExpandEntityReferences(false); // don't allow custom entities
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setEntityResolver(new TXRXEntityResolver());
			builder.setErrorHandler(new TXRXErrorHandler(name));
			Document document = builder.parse(new InputSource(in));
			return parseDocument(hr, document);
		} catch (ParserConfigurationException pce) {
			throw new IOException(pce);
		} catch (SAXException saxe) {
			throw new IOException(saxe);
		}
	}
	
	private static TextureList parseDocument(HrefResolver hr, Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("#document")) {
			for (Node child : getChildren(node)) {
				String ctype = child.getNodeName();
				if (ctype.equalsIgnoreCase("textures")) {
					if (child.hasAttributes() || child.hasChildNodes()) {
						return parseTextures(hr, child);
					}
				} else {
					throw new IOException("Unknown element: " + ctype);
				}
			}
			throw new IOException("Empty document.");
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static TextureList parseTextures(HrefResolver hr, Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("textures")) {
			NamedNodeMap attr = node.getAttributes();
			String name = parseString(attr, "name");
			TextureList list = new TextureList(name);
			for (Node child : getChildren(node)) {
				Texture texture = parseTexture(hr, child);
				if (texture != null) list.add(texture);
			}
			return list;
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static Texture parseTexture(HrefResolver hr, Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("texture")) {
			NamedNodeMap attr = node.getAttributes();
			String name = parseString(attr, "name");
			String href = parseString(attr, "href");
			if (href != null && href.length() > 0) {
				BufferedImage image = hr.resolveHref(href);
				return new Texture(name, image);
			} else {
				return null;
			}
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static String parseString(NamedNodeMap attr, String key) {
		if (attr == null) return null;
		Node node = attr.getNamedItem(key);
		if (node == null) return null;
		String text = node.getTextContent();
		if (text == null) return null;
		return text.trim();
	}
	
	private static List<Node> getChildren(Node node) {
		List<Node> list = new ArrayList<Node>();
		if (node != null) {
			NodeList children = node.getChildNodes();
			if (children != null) {
				int count = children.getLength();
				for (int i = 0; i < count; i++) {
					Node child = children.item(i);
					if (child != null) {
						String type = child.getNodeName();
						if (type.equalsIgnoreCase("#text") || type.equalsIgnoreCase("#comment")) {
							continue;
						} else {
							list.add(child);
						}
					}
				}
			}
		}
		return list;
	}
	
	private static class TXRXEntityResolver implements EntityResolver {
		@Override
		public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
			if (publicId.contains("PowerTexture") || systemId.contains("txrx.dtd")) {
				return new InputSource(TextureParser.class.getResourceAsStream("txrx.dtd"));
			} else {
				return null;
			}
		}
	}
	
	private static class TXRXErrorHandler implements ErrorHandler {
		private final String name;
		public TXRXErrorHandler(String name) {
			this.name = name;
		}
		@Override
		public void error(SAXParseException e) throws SAXException {
			System.err.print("Warning: Failed to compile texture list " + name + ": ");
			System.err.println("ERROR on "+e.getLineNumber()+":"+e.getColumnNumber()+": "+e.getMessage());
		}
		@Override
		public void fatalError(SAXParseException e) throws SAXException {
			System.err.print("Warning: Failed to compile texture list " + name + ": ");
			System.err.println("FATAL ERROR on "+e.getLineNumber()+":"+e.getColumnNumber()+": "+e.getMessage());
		}
		@Override
		public void warning(SAXParseException e) throws SAXException {
			System.err.print("Warning: Failed to compile texture list " + name + ": ");
			System.err.println("WARNING on "+e.getLineNumber()+":"+e.getColumnNumber()+": "+e.getMessage());
		}
	}
}
