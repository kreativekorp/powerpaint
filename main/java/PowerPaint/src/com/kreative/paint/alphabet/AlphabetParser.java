package com.kreative.paint.alphabet;

import java.awt.Font;
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

public class AlphabetParser {
	public static AlphabetList parse(String name, InputStream in) throws IOException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(true); // make sure the XML is valid
			factory.setExpandEntityReferences(false); // don't allow custom entities
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setEntityResolver(new ALPXEntityResolver());
			builder.setErrorHandler(new ALPXErrorHandler(name));
			Document document = builder.parse(new InputSource(in));
			return parseDocument(document);
		} catch (ParserConfigurationException pce) {
			throw new IOException(pce);
		} catch (SAXException saxe) {
			throw new IOException(saxe);
		}
	}
	
	private static AlphabetList parseDocument(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("#document")) {
			for (Node child : getChildren(node)) {
				String ctype = child.getNodeName();
				if (ctype.equalsIgnoreCase("alphabets")) {
					if (child.hasAttributes() || child.hasChildNodes()) {
						return parseAlphabets(child);
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
	
	private static AlphabetList parseAlphabets(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("alphabets")) {
			NamedNodeMap attr = node.getAttributes();
			String name = parseString(attr, "name");
			int width = parseInt(attr, "width", Alphabet.DEFAULT_WIDTH);
			String fn = parseString(attr, "font-family");
			if (fn == null || fn.equalsIgnoreCase("inherit")) fn = Alphabet.DEFAULT_FONT.getFamily();
			int fs = parseInt(attr, "font-size", Alphabet.DEFAULT_FONT.getSize());
			boolean fb = parseBoolean(attr, "font-weight", "bold", "normal", Alphabet.DEFAULT_FONT.isBold());
			boolean fi = parseBoolean(attr, "font-style", "italic", "normal", Alphabet.DEFAULT_FONT.isItalic());
			Font font = new Font(fn, ((fb ? Font.BOLD : 0) | (fi ? Font.ITALIC : 0)), fs);
			AlphabetList list = new AlphabetList(name, width, font);
			for (Node child : getChildren(node)) {
				list.add(parseAlphabet(child, list));
			}
			return list;
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static Alphabet parseAlphabet(Node node, AlphabetList parent) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("alphabet")) {
			NamedNodeMap attr = node.getAttributes();
			String name = parseString(attr, "name");
			int width = parseInt(attr, "width", parent.width);
			String fn = parseString(attr, "font-family");
			if (fn == null || fn.equalsIgnoreCase("inherit")) fn = parent.font.getFamily();
			int fs = parseInt(attr, "font-size", parent.font.getSize());
			boolean fb = parseBoolean(attr, "font-weight", "bold", "normal", parent.font.isBold());
			boolean fi = parseBoolean(attr, "font-style", "italic", "normal", parent.font.isItalic());
			Font font = new Font(fn, ((fb ? Font.BOLD : 0) | (fi ? Font.ITALIC : 0)), fs);
			String text = node.getTextContent();
			List<Integer> letters = new ArrayList<Integer>();
			if (text != null) {
				int i = 0, n = text.length();
				while (i < n) {
					int ch = text.codePointAt(i);
					if (isPrintable(ch)) letters.add(ch);
					i += Character.charCount(ch);
				}
			}
			return new Alphabet(name, width, font, letters);
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static boolean isPrintable(int ch) {
		// Low end filter.
		if (ch < 0x21) return false;
		if (ch < 0x7F) return true;
		if (ch < 0xA1) return false;
		// High end filter.
		if (ch >= 0x10FFFE) return false;
		if ((ch & 0xFFFF) >= 0xFFFE) return false;
		if (ch == 0xFFEF) return false;
		// The rest.
		if (Character.isWhitespace(ch)) return false;
		if (Character.isSpaceChar(ch)) return false;
		return true;
	}
	
	private static boolean parseBoolean(NamedNodeMap attr, String key, String trueValue, String falseValue, boolean def) {
		if (attr == null) return def;
		Node node = attr.getNamedItem(key);
		if (node == null) return def;
		String text = node.getTextContent();
		if (text == null) return def;
		text = text.trim();
		if (text.equalsIgnoreCase(trueValue)) return true;
		if (text.equalsIgnoreCase(falseValue)) return false;
		return def;
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
	
	private static class ALPXEntityResolver implements EntityResolver {
		@Override
		public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
			if (publicId.contains("PowerAlphabet") || systemId.contains("alpx.dtd")) {
				return new InputSource(AlphabetParser.class.getResourceAsStream("alpx.dtd"));
			} else {
				return null;
			}
		}
	}
	
	private static class ALPXErrorHandler implements ErrorHandler {
		private final String name;
		public ALPXErrorHandler(String name) {
			this.name = name;
		}
		@Override
		public void error(SAXParseException e) throws SAXException {
			System.err.print("Warning: Failed to compile alphabet set " + name + ": ");
			System.err.println("ERROR on "+e.getLineNumber()+":"+e.getColumnNumber()+": "+e.getMessage());
		}
		@Override
		public void fatalError(SAXParseException e) throws SAXException {
			System.err.print("Warning: Failed to compile alphabet set " + name + ": ");
			System.err.println("FATAL ERROR on "+e.getLineNumber()+":"+e.getColumnNumber()+": "+e.getMessage());
		}
		@Override
		public void warning(SAXParseException e) throws SAXException {
			System.err.print("Warning: Failed to compile alphabet set " + name + ": ");
			System.err.println("WARNING on "+e.getLineNumber()+":"+e.getColumnNumber()+": "+e.getMessage());
		}
	}
}
