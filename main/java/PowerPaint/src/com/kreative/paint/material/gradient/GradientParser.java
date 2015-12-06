package com.kreative.paint.material.gradient;

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

public class GradientParser {
	public static GradientList parse(String name, InputStream in) throws IOException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(true); // make sure the XML is valid
			factory.setExpandEntityReferences(false); // don't allow custom entities
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setEntityResolver(new GRDXEntityResolver());
			builder.setErrorHandler(new GRDXErrorHandler(name));
			Document document = builder.parse(new InputSource(in));
			return parseDocument(document);
		} catch (ParserConfigurationException pce) {
			throw new IOException(pce);
		} catch (SAXException saxe) {
			throw new IOException(saxe);
		}
	}
	
	private static GradientList parseDocument(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("#document")) {
			for (Node child : getChildren(node)) {
				String ctype = child.getNodeName();
				if (ctype.equalsIgnoreCase("gradients")) {
					if (child.hasAttributes() || child.hasChildNodes()) {
						return parseGradients(child);
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
	
	private static GradientList parseGradients(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("gradients")) {
			NamedNodeMap attr = node.getAttributes();
			String name = parseString(attr, "name");
			GradientList list = new GradientList(name);
			for (Node child : getChildren(node)) {
				String ctype = child.getNodeName();
				if (ctype.equalsIgnoreCase("gradient")) {
					GradientPreset gp = parseGradient(child);
					if (gp != null) list.presets.add(gp);
				} else if (ctype.equalsIgnoreCase("shape")) {
					GradientShape gs = parseShape(child);
					if (gs != null) list.shapes.add(gs);
				} else if (ctype.equalsIgnoreCase("map")) {
					GradientColorMap gm = parseColorMap(child);
					if (gm != null) list.colorMaps.add(gm);
				} else {
					throw new IOException("Unknown element: " + ctype);
				}
			}
			return list;
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static GradientPreset parseGradient(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("gradient")) {
			NamedNodeMap attr = node.getAttributes();
			String name = parseString(attr, "name");
			GradientShape shape = null;
			GradientColorMap map = null;
			for (Node child : getChildren(node)) {
				String ctype = child.getNodeName();
				if (ctype.equalsIgnoreCase("shape")) {
					shape = parseShape(child);
				} else if (ctype.equalsIgnoreCase("map")) {
					map = parseColorMap(child);
				} else {
					throw new IOException("Unknown element: " + ctype);
				}
			}
			return new GradientPreset(shape, map, name);
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static GradientShape parseShape(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("shape")) {
			NamedNodeMap attr = node.getAttributes();
			String name = parseString(attr, "name");
			boolean repeat = false;
			boolean reflect = false;
			boolean reverse = false;
			GradientShape shape = null;
			for (Node child : getChildren(node)) {
				String ctype = child.getNodeName();
				NamedNodeMap cattr = child.getAttributes();
				repeat = parseBoolean(cattr, "repeat", repeat);
				reflect = parseBoolean(cattr, "reflect", reflect);
				reverse = parseBoolean(cattr, "reverse", reverse);
				if (ctype.equalsIgnoreCase("linear")) {
					shape = new GradientShape.Linear(
						parseDouble(cattr, "x0", 0.0),
						parseDouble(cattr, "y0", 0.0),
						parseDouble(cattr, "x1", 0.0),
						parseDouble(cattr, "y1", 0.0),
						repeat, reflect, reverse, name
					);
				} else if (ctype.equalsIgnoreCase("angular")) {
					shape = new GradientShape.Angular(
						parseDouble(cattr, "cx", 0.0),
						parseDouble(cattr, "cy", 0.0),
						parseDouble(cattr, "px", 0.0),
						parseDouble(cattr, "py", 0.0),
						repeat, reflect, reverse, name
					);
				} else if (ctype.equalsIgnoreCase("radial")) {
					shape = new GradientShape.Radial(
						parseDouble(cattr, "cx", 0.0),
						parseDouble(cattr, "cy", 0.0),
						parseDouble(cattr, "x0", 0.0),
						parseDouble(cattr, "y0", 0.0),
						parseDouble(cattr, "x1", 0.0),
						parseDouble(cattr, "y1", 0.0),
						repeat, reflect, reverse, name
					);
				} else if (ctype.equalsIgnoreCase("rectangular")) {
					shape = new GradientShape.Rectangular(
						parseDouble(cattr, "l0", 0.0),
						parseDouble(cattr, "t0", 0.0),
						parseDouble(cattr, "r0", 0.0),
						parseDouble(cattr, "b0", 0.0),
						parseDouble(cattr, "l1", 0.0),
						parseDouble(cattr, "t1", 0.0),
						parseDouble(cattr, "r1", 0.0),
						parseDouble(cattr, "b1", 0.0),
						repeat, reflect, reverse, name
					);
				} else if (ctype.equalsIgnoreCase("diamond")) {
					System.err.println(
						"Warning: Unimplemented gradient shape <" + ctype + ">. " +
						"Ignoring gradient \"" + name + "\"."
					);
				} else {
					throw new IOException("Unknown element: " + ctype);
				}
			}
			return shape;
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static GradientColorMap parseColorMap(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("map")) {
			NamedNodeMap attr = node.getAttributes();
			String name = parseString(attr, "name");
			GradientColorMap map = new GradientColorMap(name);
			for (Node child : getChildren(node)) {
				map.add(parseColorStop(child));
			}
			return map;
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static GradientColorStop parseColorStop(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("stop")) {
			NamedNodeMap attr = node.getAttributes();
			double position = parseDouble(attr, "at", 0.0);
			GradientColor color = null;
			for (Node child : getChildren(node)) {
				color = parseColor(child);
			}
			return new GradientColorStop(position, color);
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static GradientColor parseColor(Node node) throws IOException {
		String type = node.getNodeName();
		NamedNodeMap attr = node.getAttributes();
		if (type.equalsIgnoreCase("rgb")) {
			return new GradientColor.RGB(
				parseInt(attr, "r", 0),
				parseInt(attr, "g", 0),
				parseInt(attr, "b", 0)
			);
		} else if (type.equalsIgnoreCase("rgb16")) {
			return new GradientColor.RGB16(
				parseInt(attr, "r", 0),
				parseInt(attr, "g", 0),
				parseInt(attr, "b", 0)
			);
		} else if (type.equalsIgnoreCase("rgbd")) {
			return new GradientColor.RGBD(
				parseRGBD(attr, "r", 0.0f),
				parseRGBD(attr, "g", 0.0f),
				parseRGBD(attr, "b", 0.0f)
			);
		} else if (type.equalsIgnoreCase("rgba")) {
			return new GradientColor.RGBA(
				parseInt(attr, "r", 0),
				parseInt(attr, "g", 0),
				parseInt(attr, "b", 0),
				parseInt(attr, "a", 0)
			);
		} else if (type.equalsIgnoreCase("rgba16")) {
			return new GradientColor.RGBA16(
				parseInt(attr, "r", 0),
				parseInt(attr, "g", 0),
				parseInt(attr, "b", 0),
				parseInt(attr, "a", 0)
			);
		} else if (type.equalsIgnoreCase("rgbad")) {
			return new GradientColor.RGBAD(
				parseRGBD(attr, "r", 0.0f),
				parseRGBD(attr, "g", 0.0f),
				parseRGBD(attr, "b", 0.0f),
				parseRGBD(attr, "a", 0.0f)
			);
		} else if (type.equalsIgnoreCase("hsv")) {
			return new GradientColor.HSV(
				parseFloat(attr, "h", 0.0f),
				parseFloat(attr, "s", 0.0f),
				parseFloat(attr, "v", 0.0f)
			);
		} else if (type.equalsIgnoreCase("hsva")) {
			return new GradientColor.HSVA(
				parseFloat(attr, "h", 0.0f),
				parseFloat(attr, "s", 0.0f),
				parseFloat(attr, "v", 0.0f),
				parseFloat(attr, "a", 0.0f)
			);
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static float parseRGBD(NamedNodeMap attr, String key, float def) {
		if (attr == null) return def;
		Node node = attr.getNamedItem(key);
		if (node == null) return def;
		String text = node.getTextContent();
		if (text == null) return def;
		try {
			int o = text.indexOf("/");
			if (o >= 0) {
				float n = Float.parseFloat(text.substring(0, o).trim());
				float d = Float.parseFloat(text.substring(o + 1).trim());
				return n / d;
			}
			text = text.trim();
			if (text.endsWith("%")) {
				float p = Float.parseFloat(text.substring(0, text.length() - 1).trim());
				return p / 100.0f;
			}
			return Float.parseFloat(text);
		} catch (NumberFormatException nfe) {
			return def;
		}
	}
	
	private static boolean parseBoolean(NamedNodeMap attr, String key, boolean def) {
		if (attr == null) return def;
		Node node = attr.getNamedItem(key);
		if (node == null) return def;
		String text = node.getTextContent();
		if (text == null) return def;
		text = text.trim();
		if (text.equalsIgnoreCase("yes")) return true;
		if (text.equalsIgnoreCase("no")) return false;
		return def;
	}
	
	private static double parseDouble(NamedNodeMap attr, String key, double def) {
		if (attr == null) return def;
		Node node = attr.getNamedItem(key);
		if (node == null) return def;
		String text = node.getTextContent();
		if (text == null) return def;
		try { return Double.parseDouble(text.trim()); }
		catch (NumberFormatException nfe) { return def; }
	}
	
	private static float parseFloat(NamedNodeMap attr, String key, float def) {
		if (attr == null) return def;
		Node node = attr.getNamedItem(key);
		if (node == null) return def;
		String text = node.getTextContent();
		if (text == null) return def;
		try { return Float.parseFloat(text.trim()); }
		catch (NumberFormatException nfe) { return def; }
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
	
	private static class GRDXEntityResolver implements EntityResolver {
		@Override
		public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
			if (publicId.contains("PowerGradient") || systemId.contains("grdx.dtd")) {
				return new InputSource(GradientParser.class.getResourceAsStream("grdx.dtd"));
			} else {
				return null;
			}
		}
	}
	
	private static class GRDXErrorHandler implements ErrorHandler {
		private final String name;
		public GRDXErrorHandler(String name) {
			this.name = name;
		}
		@Override
		public void error(SAXParseException e) throws SAXException {
			System.err.print("Warning: Failed to compile gradient set " + name + ": ");
			System.err.println("ERROR on "+e.getLineNumber()+":"+e.getColumnNumber()+": "+e.getMessage());
		}
		@Override
		public void fatalError(SAXParseException e) throws SAXException {
			System.err.print("Warning: Failed to compile gradient set " + name + ": ");
			System.err.println("FATAL ERROR on "+e.getLineNumber()+":"+e.getColumnNumber()+": "+e.getMessage());
		}
		@Override
		public void warning(SAXParseException e) throws SAXException {
			System.err.print("Warning: Failed to compile gradient set " + name + ": ");
			System.err.println("WARNING on "+e.getLineNumber()+":"+e.getColumnNumber()+": "+e.getMessage());
		}
	}
}
