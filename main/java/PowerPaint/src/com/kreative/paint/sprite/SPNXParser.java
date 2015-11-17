package com.kreative.paint.sprite;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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

public class SPNXParser {
	public static SpriteSheet parse(String name, InputStream in, BufferedImage image) throws IOException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(true); // make sure the XML is valid
			factory.setExpandEntityReferences(false); // don't allow custom entities
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setEntityResolver(new SPNXEntityResolver());
			builder.setErrorHandler(new SPNXErrorHandler(name));
			Document document = builder.parse(new InputSource(in));
			return parseDocument(document, image);
		} catch (ParserConfigurationException pce) {
			throw new IOException(pce);
		} catch (SAXException saxe) {
			throw new IOException(saxe);
		}
	}
	
	private static SpriteSheet parseDocument(Node node, BufferedImage image) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("#document")) {
			for (Node child : getChildren(node)) {
				String ctype = child.getNodeName();
				if (ctype.equalsIgnoreCase("sprite-sheet")) {
					if (child.hasAttributes() || child.hasChildNodes()) {
						return parseSpriteSheet(child, image);
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
	
	private static SpriteSheet parseSpriteSheet(Node node, BufferedImage image) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("sprite-sheet")) {
			NamedNodeMap attr = node.getAttributes();
			String name = parseString(attr, "name");
			int intent = parseIntent(attr, "intent");
			int cols = parseInt(attr, "cols", 0);
			int rows = parseInt(attr, "rows", 0);
			ArrayOrdering order = parseOrder(attr, "order");
			SpriteSheet sheet = new SpriteSheet(image, name, intent, cols, rows, order);
			for (Node child : getChildren(node)) {
				String ctype = child.getNodeName();
				if (ctype.equalsIgnoreCase("slice")) {
					sheet.slices.add(parseSlice(child, image));
				} else if (ctype.equalsIgnoreCase("sprite-set")) {
					sheet.root.children.add(parseSpriteSet(child));
				} else if (ctype.equalsIgnoreCase("sprite")) {
					sheet.root.children.add(parseSprite(child));
				} else {
					throw new IOException("Unknown element: " + ctype);
				}
			}
			return sheet;
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static SpriteSheetSlice parseSlice(Node node, BufferedImage image) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("slice")) {
			NamedNodeMap attr = node.getAttributes();
			int w = image.getWidth();
			int h = image.getHeight();
			int sx = parseInt(attr, "sx", 0);
			int sy = parseInt(attr, "sy", 0);
			int cw = parseInt(attr, "cw", Math.min(w - sx, h - sy));
			int ch = parseInt(attr, "ch", Math.min(w - sx, h - sy));
			int chx = parseInt(attr, "chx", cw / 2);
			int chy = parseInt(attr, "chy", ch / 2);
			int cdx = parseInt(attr, "cdx", cw);
			int cdy = parseInt(attr, "cdy", ch);
			int cols = parseInt(attr, "cols", (w - sx + cdx - cw) / cdx);
			int rows = parseInt(attr, "rows", (h - sy + cdy - ch) / cdy);
			ArrayOrdering order = parseOrder(attr, "order");
			ColorTransform transform = parseTransform(attr, "color-transform");
			return new SpriteSheetSlice(
				sx, sy, cw, ch, chx, chy, cdx, cdy,
				cols, rows, order, transform
			);
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static SpriteTreeNode.Branch parseSpriteSet(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("sprite-set")) {
			NamedNodeMap attr = node.getAttributes();
			String name = parseString(attr, "name");
			int index = parseInt(attr, "index", 0);
			int duration = parseInt(attr, "duration", 0);
			SpriteTreeNode.Branch branch = new SpriteTreeNode.Branch(name, index, duration);
			for (Node child : getChildren(node)) {
				String ctype = child.getNodeName();
				if (ctype.equalsIgnoreCase("sprite-set")) {
					branch.children.add(parseSpriteSet(child));
				} else if (ctype.equalsIgnoreCase("sprite")) {
					branch.children.add(parseSprite(child));
				} else {
					throw new IOException("Unknown element: " + ctype);
				}
			}
			return branch;
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static SpriteTreeNode.Leaf parseSprite(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("sprite")) {
			NamedNodeMap attr = node.getAttributes();
			String name = parseString(attr, "name");
			int index = parseInt(attr, "index", 0);
			int duration = parseInt(attr, "duration", 0);
			int count = parseInt(attr, "count", 1);
			return new SpriteTreeNode.Leaf(name, index, duration, count);
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static int parseIntent(NamedNodeMap attr, String key) {
		if (attr == null) return 0;
		Node node = attr.getNamedItem(key);
		if (node == null) return 0;
		String text = node.getTextContent();
		if (text == null) return 0;
		return SpriteIntent.fromString(text.trim());
	}
	
	private static ArrayOrdering parseOrder(NamedNodeMap attr, String key) {
		if (attr == null) return ArrayOrdering.LTR_TTB;
		Node node = attr.getNamedItem(key);
		if (node == null) return ArrayOrdering.LTR_TTB;
		String text = node.getTextContent();
		if (text == null) return ArrayOrdering.LTR_TTB;
		return ArrayOrdering.fromString(text.trim());
	}
	
	private static ColorTransform parseTransform(NamedNodeMap attr, String key) {
		if (attr == null) return ColorTransform.NONE;
		Node node = attr.getNamedItem(key);
		if (node == null) return ColorTransform.NONE;
		String text = node.getTextContent();
		if (text == null) return ColorTransform.NONE;
		return ColorTransform.fromString(text.trim());
	}
	
	private static int parseInt(NamedNodeMap attr, String key, int def) {
		if (attr == null) return def;
		Node node = attr.getNamedItem(key);
		if (node == null) return def;
		String text = node.getTextContent();
		if (text == null) return def;
		try { return Integer.parseInt(text.trim()); }
		catch (NumberFormatException nfe) { return def; }
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
	
	private static class SPNXEntityResolver implements EntityResolver {
		@Override
		public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
			if (publicId.contains("SpriteInfo") || systemId.contains("spnx.dtd")) {
				return new InputSource(SPNXParser.class.getResourceAsStream("spnx.dtd"));
			} else {
				return null;
			}
		}
	}
	
	private static class SPNXErrorHandler implements ErrorHandler {
		private final String name;
		public SPNXErrorHandler(String name) {
			this.name = name;
		}
		@Override
		public void error(SAXParseException e) throws SAXException {
			System.err.print("Warning: Failed to compile sprite info " + name + ": ");
			System.err.println("ERROR on "+e.getLineNumber()+":"+e.getColumnNumber()+": "+e.getMessage());
		}
		@Override
		public void fatalError(SAXParseException e) throws SAXException {
			System.err.print("Warning: Failed to compile sprite info " + name + ": ");
			System.err.println("FATAL ERROR on "+e.getLineNumber()+":"+e.getColumnNumber()+": "+e.getMessage());
		}
		@Override
		public void warning(SAXParseException e) throws SAXException {
			System.err.print("Warning: Failed to compile sprite info " + name + ": ");
			System.err.println("WARNING on "+e.getLineNumber()+":"+e.getColumnNumber()+": "+e.getMessage());
		}
	}
}
