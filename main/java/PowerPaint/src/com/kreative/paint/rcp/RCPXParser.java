package com.kreative.paint.rcp;

import java.io.IOException;
import java.io.InputStream;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
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

public class RCPXParser {
	public static RCPXPalette parse(String name, InputStream in) throws IOException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(true); // make sure the XML is valid
			factory.setExpandEntityReferences(false); // don't allow custom entities
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setEntityResolver(new RCPXEntityResolver());
			builder.setErrorHandler(new RCPXErrorHandler(name));
			Document document = builder.parse(new InputSource(in));
			return parseDocument(document);
		} catch (ParserConfigurationException pce) {
			throw new IOException(pce);
		} catch (SAXException saxe) {
			throw new IOException(saxe);
		}
	}
	
	private static RCPXPalette parseDocument(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("#document")) {
			for (Node child : getChildren(node)) {
				String ctype = child.getNodeName();
				if (ctype.equalsIgnoreCase("palette")) {
					if (child.hasAttributes() || child.hasChildNodes()) {
						return parsePalette(child);
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
	
	private static RCPXPalette parsePalette(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("palette")) {
			NamedNodeMap attr = node.getAttributes();
			String name = parseString(attr, "name");
			RCPXOrientation orientation = parseOrientation(attr, "orientation");
			int hwidth = parseInt(attr, "hwidth", 289);
			int hheight = parseInt(attr, "hheight", 73);
			int swidth = parseInt(attr, "swidth", 145);
			int sheight = parseInt(attr, "sheight", 145);
			int vwidth = parseInt(attr, "vwidth", 73);
			int vheight = parseInt(attr, "vheight", 289);
			List<RCPXColor> colors = new ArrayList<RCPXColor>();
			boolean colorsOrdered = false;
			RCPXLayout layout = null;
			for (Node child : getChildren(node)) {
				String ctype = child.getNodeName();
				if (ctype.equalsIgnoreCase("colors")) {
					NamedNodeMap cattr = child.getAttributes();
					colorsOrdered = parseBoolean(cattr, "ordered", "ordered", "unordered", false);
					for (Node gc : getChildren(child)) {
						colors.add(parseColor(gc));
					}
				} else if (ctype.equalsIgnoreCase("layout")) {
					for (Node gc : getChildren(child)) {
						layout = parseLayout(gc);
					}
				} else {
					throw new IOException("Unknown element: " + ctype);
				}
			}
			return new RCPXPalette(
				name, orientation,
				hwidth, hheight,
				swidth, sheight,
				vwidth, vheight,
				colors, colorsOrdered, layout
			);
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static RCPXLayout parseLayout(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("oriented")) {
			RCPXLayoutOrSwatch h = null, s = null, v = null;
			for (Node child : getChildren(node)) {
				String ctype = child.getNodeName();
				if (ctype.equalsIgnoreCase("horizontal")) {
					for (Node gc : getChildren(child)) {
						h = parseLayoutOrSwatch(gc);
					}
				} else if (ctype.equalsIgnoreCase("square")) {
					for (Node gc : getChildren(child)) {
						s = parseLayoutOrSwatch(gc);
					}
				} else if (ctype.equalsIgnoreCase("vertical")) {
					for (Node gc : getChildren(child)) {
						v = parseLayoutOrSwatch(gc);
					}
				} else {
					throw new IOException("Unknown element: " + ctype);
				}
			}
			return new RCPXLayout.Oriented(h, s, v);
		} else if (type.equalsIgnoreCase("row")) {
			RCPXLayout.Row row = new RCPXLayout.Row();
			for (Node child : getChildren(node)) {
				if (child.getNodeName().equalsIgnoreCase("weighted")) {
					int weight = parseInt(child.getAttributes(), "weight", 1);
					for (Node gc : getChildren(child)) {
						row.add(parseLayoutOrSwatch(gc), weight);
					}
				} else {
					row.add(parseLayoutOrSwatch(child));
				}
			}
			return row;
		} else if (type.equalsIgnoreCase("column")) {
			RCPXLayout.Column column = new RCPXLayout.Column();
			for (Node child : getChildren(node)) {
				if (child.getNodeName().equalsIgnoreCase("weighted")) {
					int weight = parseInt(child.getAttributes(), "weight", 1);
					for (Node gc : getChildren(child)) {
						column.add(parseLayoutOrSwatch(gc), weight);
					}
				} else {
					column.add(parseLayoutOrSwatch(child));
				}
			}
			return column;
		} else if (type.equalsIgnoreCase("diagonal")) {
			NamedNodeMap attr = node.getAttributes();
			RCPXLayout.Diagonal diagonal = new RCPXLayout.Diagonal(
				parseInt(attr, "cols", 2),
				parseInt(attr, "rows", 2),
				parseBoolean(attr, "aspect", "square", "auto", false)
			);
			for (Node child : getChildren(node)) {
				diagonal.add(parseSwatch(child));
			}
			return diagonal;
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static RCPXLayoutOrSwatch parseLayoutOrSwatch(Node node) throws IOException {
		try {
			return parseSwatch(node);
		} catch (IOException e) {
			return parseLayout(node);
		}
	}
	
	private static RCPXSwatch parseSwatch(Node node) throws IOException {
		String type = node.getNodeName();
		NamedNodeMap attr = node.getAttributes();
		if (type.equalsIgnoreCase("empty")) {
			return new RCPXSwatch.Empty(
				parseInt(attr, "repeat", 1)
			);
		} else if (type.equalsIgnoreCase("index")) {
			RCPXBorder defBorder = parseBorder(attr, "border", RCPXBorder.ALL);
			return new RCPXSwatch.Index(
				parseInt(attr, "i", 0),
				parseInt(attr, "repeat", 1),
				parseBorder(attr, "border-only", defBorder),
				parseBorder(attr, "border-first", defBorder),
				parseBorder(attr, "border-middle", defBorder),
				parseBorder(attr, "border-last", defBorder)
			);
		} else if (type.equalsIgnoreCase("range")) {
			RCPXBorder defBorder = parseBorder(attr, "border", RCPXBorder.ALL);
			return new RCPXSwatch.Range(
				parseInt(attr, "start", 0),
				parseInt(attr, "end", 0),
				parseBorder(attr, "border-only", defBorder),
				parseBorder(attr, "border-first", defBorder),
				parseBorder(attr, "border-middle", defBorder),
				parseBorder(attr, "border-last", defBorder)
			);
		} else if (type.equalsIgnoreCase("rgb-sweep")) {
			return new RCPXSwatch.RGBSweep(
				parseRGBChannel(attr, "xchan"),
				parseInt(attr, "xmin", 0),
				parseInt(attr, "xmax", 255),
				parseRGBChannel(attr, "ychan"),
				parseInt(attr, "ymin", 0),
				parseInt(attr, "ymax", 255),
				parseInt(attr, "r", 0),
				parseInt(attr, "g", 0),
				parseInt(attr, "b", 0),
				parseBorder(attr, "border", RCPXBorder.ALL)
			);
		} else if (type.equalsIgnoreCase("hsv-sweep")) {
			HSVChannel xchan = parseHSVChannel(attr, "xchan");
			HSVChannel ychan = parseHSVChannel(attr, "ychan");
			return new RCPXSwatch.HSVSweep(
				xchan,
				parseFloat(attr, "xmin", 0.0f),
				parseFloat(attr, "xmax", ((xchan == HSVChannel.HUE) ? 360.0f : 100.0f)),
				ychan,
				parseFloat(attr, "ymin", 0.0f),
				parseFloat(attr, "ymax", ((ychan == HSVChannel.HUE) ? 360.0f : 100.0f)),
				parseFloat(attr, "h", 0.0f),
				parseFloat(attr, "s", 0.0f),
				parseFloat(attr, "v", 0.0f),
				parseBorder(attr, "border", RCPXBorder.ALL)
			);
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static RCPXColor parseColor(Node node) throws IOException {
		String type = node.getNodeName();
		NamedNodeMap attr = node.getAttributes();
		if (type.equalsIgnoreCase("rgb")) {
			return new RCPXColor.RGB(
				parseInt(attr, "r", 0),
				parseInt(attr, "g", 0),
				parseInt(attr, "b", 0),
				parseString(attr, "name")
			);
		} else if (type.equalsIgnoreCase("rgb16")) {
			return new RCPXColor.RGB16(
				parseInt(attr, "r", 0),
				parseInt(attr, "g", 0),
				parseInt(attr, "b", 0),
				parseString(attr, "name")
			);
		} else if (type.equalsIgnoreCase("rgbd")) {
			return new RCPXColor.RGBD(
				parseRGBD(attr, "r", 0.0f),
				parseRGBD(attr, "g", 0.0f),
				parseRGBD(attr, "b", 0.0f),
				parseString(attr, "name")
			);
		} else if (type.equalsIgnoreCase("rgba")) {
			return new RCPXColor.RGBA(
				parseInt(attr, "r", 0),
				parseInt(attr, "g", 0),
				parseInt(attr, "b", 0),
				parseInt(attr, "a", 0),
				parseString(attr, "name")
			);
		} else if (type.equalsIgnoreCase("rgba16")) {
			return new RCPXColor.RGBA16(
				parseInt(attr, "r", 0),
				parseInt(attr, "g", 0),
				parseInt(attr, "b", 0),
				parseInt(attr, "a", 0),
				parseString(attr, "name")
			);
		} else if (type.equalsIgnoreCase("rgbad")) {
			return new RCPXColor.RGBAD(
				parseRGBD(attr, "r", 0.0f),
				parseRGBD(attr, "g", 0.0f),
				parseRGBD(attr, "b", 0.0f),
				parseRGBD(attr, "a", 0.0f),
				parseString(attr, "name")
			);
		} else if (type.equalsIgnoreCase("hsv")) {
			return new RCPXColor.HSV(
				parseFloat(attr, "h", 0.0f),
				parseFloat(attr, "s", 0.0f),
				parseFloat(attr, "v", 0.0f),
				parseString(attr, "name")
			);
		} else if (type.equalsIgnoreCase("hsva")) {
			return new RCPXColor.HSVA(
				parseFloat(attr, "h", 0.0f),
				parseFloat(attr, "s", 0.0f),
				parseFloat(attr, "v", 0.0f),
				parseFloat(attr, "a", 0.0f),
				parseString(attr, "name")
			);
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static RCPXBorder parseBorder(NamedNodeMap attr, String key, RCPXBorder def) {
		if (attr == null) return def;
		Node node = attr.getNamedItem(key);
		if (node == null) return def;
		String text = node.getTextContent();
		if (text == null) return def;
		boolean t = false, l = false, b = false, r = false;
		CharacterIterator it = new StringCharacterIterator(text);
		for (char ch = it.first(); ch != CharacterIterator.DONE; ch = it.next()) {
			switch (ch) {
			case 'T': case 't': case 'N': case 'n': case '~': t = true; break;
			case 'L': case 'l': case 'W': case 'w': case '[': l = true; break;
			case 'B': case 'b': case 'S': case 's': case '_': b = true; break;
			case 'R': case 'r': case 'E': case 'e': case ']': r = true; break;
			case 'H': case 'h': case '=': t = b = true; break;
			case 'V': case 'v': case '|': l = r = true; break;
			case 'D': case 'd': case '*': return def;
			case 'A': case 'a': case '#': return RCPXBorder.ALL;
			case 'Z': case 'z': case '0': return RCPXBorder.NONE;
			}
		}
		return new RCPXBorder(t, l, b, r);
	}
	
	private static RCPXOrientation parseOrientation(NamedNodeMap attr, String key) {
		if (attr == null) return RCPXOrientation.HORIZONTAL;
		Node node = attr.getNamedItem(key);
		if (node == null) return RCPXOrientation.HORIZONTAL;
		String text = node.getTextContent();
		if (text == null) return RCPXOrientation.HORIZONTAL;
		try { return RCPXOrientation.valueOf(text.trim().toUpperCase()); }
		catch (IllegalArgumentException e) { return RCPXOrientation.HORIZONTAL; }
	}
	
	private static HSVChannel parseHSVChannel(NamedNodeMap attr, String key) {
		if (attr == null) return HSVChannel.NONE;
		Node node = attr.getNamedItem(key);
		if (node == null) return HSVChannel.NONE;
		String text = node.getTextContent();
		if (text == null) return HSVChannel.NONE;
		text = text.trim();
		if (text.equalsIgnoreCase("h") || text.equalsIgnoreCase("hue")) return HSVChannel.HUE;
		if (text.equalsIgnoreCase("s") || text.equalsIgnoreCase("saturation")) return HSVChannel.SATURATION;
		if (text.equalsIgnoreCase("v") || text.equalsIgnoreCase("value")) return HSVChannel.VALUE;
		if (text.equalsIgnoreCase("a") || text.equalsIgnoreCase("alpha")) return HSVChannel.ALPHA;
		return HSVChannel.NONE;
	}
	
	private static RGBChannel parseRGBChannel(NamedNodeMap attr, String key) {
		if (attr == null) return RGBChannel.NONE;
		Node node = attr.getNamedItem(key);
		if (node == null) return RGBChannel.NONE;
		String text = node.getTextContent();
		if (text == null) return RGBChannel.NONE;
		text = text.trim();
		if (text.equalsIgnoreCase("r") || text.equalsIgnoreCase("red")) return RGBChannel.RED;
		if (text.equalsIgnoreCase("g") || text.equalsIgnoreCase("green")) return RGBChannel.GREEN;
		if (text.equalsIgnoreCase("b") || text.equalsIgnoreCase("blue")) return RGBChannel.BLUE;
		if (text.equalsIgnoreCase("a") || text.equalsIgnoreCase("alpha")) return RGBChannel.ALPHA;
		return RGBChannel.NONE;
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
	
	private static class RCPXEntityResolver implements EntityResolver {
		@Override
		public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
			if (publicId.contains("ResplendentColor") || systemId.contains("rcpx.dtd")) {
				return new InputSource(RCPXParser.class.getResourceAsStream("rcpx.dtd"));
			} else {
				return null;
			}
		}
	}
	
	private static class RCPXErrorHandler implements ErrorHandler {
		private final String name;
		public RCPXErrorHandler(String name) {
			this.name = name;
		}
		@Override
		public void error(SAXParseException e) throws SAXException {
			System.err.print("Warning: Failed to compile color palette " + name + ": ");
			System.err.println("ERROR on "+e.getLineNumber()+":"+e.getColumnNumber()+": "+e.getMessage());
		}
		@Override
		public void fatalError(SAXParseException e) throws SAXException {
			System.err.print("Warning: Failed to compile color palette " + name + ": ");
			System.err.println("FATAL ERROR on "+e.getLineNumber()+":"+e.getColumnNumber()+": "+e.getMessage());
		}
		@Override
		public void warning(SAXParseException e) throws SAXException {
			System.err.print("Warning: Failed to compile color palette " + name + ": ");
			System.err.println("WARNING on "+e.getLineNumber()+":"+e.getColumnNumber()+": "+e.getMessage());
		}
	}
}
