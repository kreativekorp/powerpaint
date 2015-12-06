package com.kreative.paint.material.dither;

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

public class DitherAlgorithmParser {
	public static DitherAlgorithmList parse(String name, InputStream in) throws IOException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(true); // make sure the XML is valid
			factory.setExpandEntityReferences(false); // don't allow custom entities
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setEntityResolver(new DITXEntityResolver());
			builder.setErrorHandler(new DITXErrorHandler(name));
			Document document = builder.parse(new InputSource(in));
			return parseDocument(document);
		} catch (ParserConfigurationException pce) {
			throw new IOException(pce);
		} catch (SAXException saxe) {
			throw new IOException(saxe);
		}
	}
	
	private static DitherAlgorithmList parseDocument(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("#document")) {
			for (Node child : getChildren(node)) {
				String ctype = child.getNodeName();
				if (ctype.equalsIgnoreCase("dither-algorithms")) {
					if (child.hasAttributes() || child.hasChildNodes()) {
						return parseDitherAlgorithms(child);
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
	
	private static DitherAlgorithmList parseDitherAlgorithms(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("dither-algorithms")) {
			NamedNodeMap attr = node.getAttributes();
			String name = parseString(attr, "name");
			DitherAlgorithmList list = new DitherAlgorithmList(name);
			for (Node child : getChildren(node)) {
				list.add(parseDitherAlgorithm(child));
			}
			return list;
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static DitherAlgorithm parseDitherAlgorithm(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("dither-algorithm")) {
			NamedNodeMap attr = node.getAttributes();
			String dat = parseString(attr, "type");
			if (dat == null) {
				throw new IOException("Unknown dither algorithm type.");
			}
			String name = parseString(attr, "name");
			int rows = parseInt(attr, "rows", 1);
			int columns = parseInt(attr, "cols", 1);
			int denominator = parseInt(attr, "d", 1);
			String text = node.getTextContent();
			if (text == null) text = "";
			if (dat.equalsIgnoreCase("diffusion")) {
				return new DiffusionDitherAlgorithm(rows, columns, denominator, text, name);
			} else if (dat.equalsIgnoreCase("ordered")) {
				return new OrderedDitherAlgorithm(rows, columns, denominator, text, name);
			} else {
				throw new IOException("Unknown dither algorithm type: " + dat);
			}
		} else {
			throw new IOException("Unknown element: " + type);
		}
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
	
	private static class DITXEntityResolver implements EntityResolver {
		@Override
		public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
			if (publicId.contains("PowerDither") || systemId.contains("ditx.dtd")) {
				return new InputSource(DitherAlgorithmParser.class.getResourceAsStream("ditx.dtd"));
			} else {
				return null;
			}
		}
	}
	
	private static class DITXErrorHandler implements ErrorHandler {
		private final String name;
		public DITXErrorHandler(String name) {
			this.name = name;
		}
		@Override
		public void error(SAXParseException e) throws SAXException {
			System.err.print("Warning: Failed to compile dither algorithm set " + name + ": ");
			System.err.println("ERROR on "+e.getLineNumber()+":"+e.getColumnNumber()+": "+e.getMessage());
		}
		@Override
		public void fatalError(SAXParseException e) throws SAXException {
			System.err.print("Warning: Failed to compile dither algorithm set " + name + ": ");
			System.err.println("FATAL ERROR on "+e.getLineNumber()+":"+e.getColumnNumber()+": "+e.getMessage());
		}
		@Override
		public void warning(SAXParseException e) throws SAXException {
			System.err.print("Warning: Failed to compile dither algorithm set " + name + ": ");
			System.err.println("WARNING on "+e.getLineNumber()+":"+e.getColumnNumber()+": "+e.getMessage());
		}
	}
}
