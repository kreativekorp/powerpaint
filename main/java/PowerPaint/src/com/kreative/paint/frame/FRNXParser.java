package com.kreative.paint.frame;

import java.awt.Rectangle;
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

public class FRNXParser {
	public static Frame parse(String name, InputStream in, BufferedImage image) throws IOException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(true); // make sure the XML is valid
			factory.setExpandEntityReferences(false); // don't allow custom entities
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setEntityResolver(new FRNXEntityResolver());
			builder.setErrorHandler(new FRNXErrorHandler(name));
			Document document = builder.parse(new InputSource(in));
			return parseDocument(document, image);
		} catch (ParserConfigurationException pce) {
			throw new IOException(pce);
		} catch (SAXException saxe) {
			throw new IOException(saxe);
		}
	}
	
	private static Frame parseDocument(Node node, BufferedImage image) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("#document")) {
			for (Node child : getChildren(node)) {
				String ctype = child.getNodeName();
				if (ctype.equalsIgnoreCase("frame")) {
					if (child.hasAttributes() || child.hasChildNodes()) {
						return parseFrame(child, image);
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
	
	private static Frame parseFrame(Node node, BufferedImage image) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("frame")) {
			NamedNodeMap attr = node.getAttributes();
			String name = parseString(attr, "name");
			int consx = parseInt(attr, "content-start-x", 0);
			int consy = parseInt(attr, "content-start-y", 0);
			int conex = parseInt(attr, "content-extent-x", 0);
			int coney = parseInt(attr, "content-extent-y", 0);
			Rectangle con = (
				(consx == 0 && consy == 0 && conex == 0 & coney == 0) ? null :
				new Rectangle(consx, consy, conex, coney)
			);
			int repsx = parseInt(attr, "repeat-start-x", 0);
			int repsy = parseInt(attr, "repeat-start-y", 0);
			int repex = parseInt(attr, "repeat-extent-x", 0);
			int repey = parseInt(attr, "repeat-extent-y", 0);
			Rectangle rep = (
				(repsx == 0 && repsy == 0 && repex == 0 && repey == 0) ? null :
				new Rectangle(repsx, repsy, repex, repey)
			);
			int reswm = parseInt(attr, "restrict-content-width-multiplier", 0);
			int reswb = parseInt(attr, "restrict-content-width-base", 0);
			int reshm = parseInt(attr, "restrict-content-height-multiplier", 0);
			int reshb = parseInt(attr, "restrict-content-height-base", 0);
			Rectangle res = (
				(reswm == 0 && reswb == 0 && reshm == 0 && reshb == 0) ? null :
				new Rectangle(reswb, reshb, reswm, reshm)
			);
			return new Frame(image, con, rep, res, name);
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
	
	private static class FRNXEntityResolver implements EntityResolver {
		@Override
		public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
			if (publicId.contains("FrameInfo") || systemId.contains("frnx.dtd")) {
				return new InputSource(FRNXParser.class.getResourceAsStream("frnx.dtd"));
			} else {
				return null;
			}
		}
	}
	
	private static class FRNXErrorHandler implements ErrorHandler {
		private final String name;
		public FRNXErrorHandler(String name) {
			this.name = name;
		}
		@Override
		public void error(SAXParseException e) throws SAXException {
			System.err.print("Warning: Failed to compile frame info " + name + ": ");
			System.err.println("ERROR on "+e.getLineNumber()+":"+e.getColumnNumber()+": "+e.getMessage());
		}
		@Override
		public void fatalError(SAXParseException e) throws SAXException {
			System.err.print("Warning: Failed to compile frame info " + name + ": ");
			System.err.println("FATAL ERROR on "+e.getLineNumber()+":"+e.getColumnNumber()+": "+e.getMessage());
		}
		@Override
		public void warning(SAXParseException e) throws SAXException {
			System.err.print("Warning: Failed to compile frame info " + name + ": ");
			System.err.println("WARNING on "+e.getLineNumber()+":"+e.getColumnNumber()+": "+e.getMessage());
		}
	}
}
