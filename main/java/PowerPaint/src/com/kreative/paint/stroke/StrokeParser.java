package com.kreative.paint.stroke;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class StrokeParser {
	public static StrokeSet parse(String name, InputStream in) throws IOException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(true); // make sure the XML is valid
			factory.setExpandEntityReferences(false); // don't allow custom entities
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setEntityResolver(new LNSXEntityResolver());
			builder.setErrorHandler(new LNSXErrorHandler(name));
			Document document = builder.parse(new InputSource(in));
			return parseDocument(document);
		} catch (ParserConfigurationException pce) {
			throw new IOException(pce);
		} catch (SAXException saxe) {
			throw new IOException(saxe);
		}
	}
	
	private static StrokeSet parseDocument(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("#document")) {
			for (Node child : getChildren(node)) {
				String ctype = child.getNodeName();
				if (ctype.equalsIgnoreCase("strokes")) {
					if (child.hasAttributes() || child.hasChildNodes()) {
						return parseStrokes(child);
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
	
	private static StrokeSet parseStrokes(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("strokes")) {
			NamedNodeMap attr = node.getAttributes();
			String name = parseString(attr, "name");
			StrokeSet ss = new StrokeSet(name);
			for (Node child : getChildren(node)) {
				String ctype = child.getNodeName();
				if (ctype.equalsIgnoreCase("stroke")) {
					ss.strokes.add(parseStroke(child));
				} else if (ctype.equalsIgnoreCase("width")) {
					ss.widths.add(parseWidth(child));
				} else if (ctype.equalsIgnoreCase("multiplicity")) {
					ss.multiplicities.add(parseMultiplicity(child));
				} else if (ctype.equalsIgnoreCase("dashes")) {
					ss.dashes.add(parseDashes(child).dashArray);
				} else if (ctype.equalsIgnoreCase("arrowhead")) {
					ss.arrowheads.add(parseArrowhead(child));
				} else {
					throw new IOException("Unknown element: " + ctype);
				}
			}
			return ss;
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static PowerStroke parseStroke(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("stroke")) {
			NamedNodeMap attr = node.getAttributes();
			String name = parseString(attr, "name");
			float lineWidth = 1.0f;
			int multiplicity = 1;
			float[] dashArray = null;
			float dashPhase = 0.0f;
			boolean firstArrow = true;
			Arrowhead arrowOnStart = null;
			Arrowhead arrowOnEnd = null;
			EndCap endCap = EndCap.SQUARE;
			LineJoin lineJoin = LineJoin.MITER;
			float miterLimit = 10.0f;
			for (Node child : getChildren(node)) {
				String ctype = child.getNodeName();
				if (ctype.equalsIgnoreCase("width")) {
					lineWidth = parseWidth(child);
				} else if (ctype.equalsIgnoreCase("multiplicity")) {
					multiplicity = parseMultiplicity(child);
				} else if (ctype.equalsIgnoreCase("dashes")) {
					Dashes dashes = parseDashes(child);
					dashArray = dashes.dashArray;
					dashPhase = dashes.dashPhase;
				} else if (ctype.equalsIgnoreCase("arrowhead")) {
					Arrowhead arrowhead = parseArrowhead(child);
					if (firstArrow) {
						arrowOnStart = arrowhead;
						firstArrow = false;
					} else {
						arrowOnEnd = arrowhead;
					}
				} else if (ctype.equalsIgnoreCase("cap")) {
					endCap = parseEndCap(child);
				} else if (ctype.equalsIgnoreCase("join")) {
					LineJoinMiterLimit ljml = parseLineJoin(child);
					lineJoin = ljml.lineJoin;
					miterLimit = ljml.miterLimit;
				} else {
					throw new IOException("Unknown element: " + ctype);
				}
			}
			return new PowerStroke(
				lineWidth, multiplicity,
				dashArray, dashPhase,
				arrowOnStart, arrowOnEnd,
				endCap, lineJoin, miterLimit,
				name
			);
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static float parseWidth(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("width")) {
			NamedNodeMap attr = node.getAttributes();
			return parseFloat(attr, "w", 1.0f);
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static int parseMultiplicity(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("multiplicity")) {
			NamedNodeMap attr = node.getAttributes();
			return parseInt(attr, "m", 1);
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static class Dashes {
		public final float[] dashArray;
		public final float dashPhase;
		public Dashes(float[] dashArray, float dashPhase) {
			this.dashArray = dashArray;
			this.dashPhase = dashPhase;
		}
	}
	private static Dashes parseDashes(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("dashes")) {
			NamedNodeMap attr = node.getAttributes();
			float[] dashArray = parseFloats(attr, "lengths");
			float dashPhase = parseFloat(attr, "phase", 0.0f);
			return new Dashes(dashArray, dashPhase);
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static Arrowhead parseArrowhead(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("arrowhead")) {
			NamedNodeMap attr = node.getAttributes();
			boolean scale = parseBoolean(attr, "scale", false);
			Arrowhead a = new Arrowhead(scale);
			for (Node child : getChildren(node)) {
				a.add(parseArrowheadShape(child));
			}
			if (a.isEmpty()) return null;
			return a;
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static ArrowheadShape parseArrowheadShape(Node node) throws IOException {
		EndCap endCap = EndCap.SQUARE;
		LineJoin lineJoin = LineJoin.MITER;
		float miterLimit = 10.0f;
		for (Node child : getChildren(node)) {
			String ctype = child.getNodeName();
			if (ctype.equalsIgnoreCase("cap")) {
				endCap = parseEndCap(child);
			} else if (ctype.equalsIgnoreCase("join")) {
				LineJoinMiterLimit ljml = parseLineJoin(child);
				lineJoin = ljml.lineJoin;
				miterLimit = ljml.miterLimit;
			} else {
				throw new IOException("Unknown element: " + ctype);
			}
		}
		String type = node.getNodeName();
		NamedNodeMap attr = node.getAttributes();
		if (type.equalsIgnoreCase("circle")) {
			return new ArrowheadShape.Circle(
				parseFloat(attr, "cx", 0.0f),
				parseFloat(attr, "cy", 0.0f),
				parseFloat(attr, "r", 0.0f),
				parseBoolean(attr, "stroke", true),
				parseBoolean(attr, "fill", false)
			);
		} else if (type.equalsIgnoreCase("ellipse")) {
			return new ArrowheadShape.Ellipse(
				parseFloat(attr, "cx", 0.0f),
				parseFloat(attr, "cy", 0.0f),
				parseFloat(attr, "rx", 0.0f),
				parseFloat(attr, "ry", 0.0f),
				parseBoolean(attr, "stroke", true),
				parseBoolean(attr, "fill", false)
			);
		} else if (type.equalsIgnoreCase("line")) {
			return new ArrowheadShape.Line(
				parseFloat(attr, "x1", 0.0f),
				parseFloat(attr, "y1", 0.0f),
				parseFloat(attr, "x2", 0.0f),
				parseFloat(attr, "y2", 0.0f),
				endCap,
				parseBoolean(attr, "stroke", true),
				parseBoolean(attr, "fill", false)
			);
		} else if (type.equalsIgnoreCase("path")) {
			return new ArrowheadShape.Path(
				parseString(attr, "d"),
				endCap, lineJoin, miterLimit,
				parseBoolean(attr, "stroke", true),
				parseBoolean(attr, "fill", false)
			);
		} else if (type.equalsIgnoreCase("polygon")) {
			return new ArrowheadShape.Polygon(
				parseString(attr, "points"),
				endCap, lineJoin, miterLimit,
				parseBoolean(attr, "stroke", true),
				parseBoolean(attr, "fill", false)
			);
		} else if (type.equalsIgnoreCase("polyline")) {
			return new ArrowheadShape.PolyLine(
				parseString(attr, "points"),
				endCap, lineJoin, miterLimit,
				parseBoolean(attr, "stroke", true),
				parseBoolean(attr, "fill", false)
			);
		} else if (type.equalsIgnoreCase("rect")) {
			return new ArrowheadShape.Rect(
				parseFloat(attr, "x", 0.0f),
				parseFloat(attr, "y", 0.0f),
				parseFloat(attr, "width", 0.0f),
				parseFloat(attr, "height", 0.0f),
				parseFloat(attr, "rx", 0.0f),
				parseFloat(attr, "ry", 0.0f),
				lineJoin, miterLimit,
				parseBoolean(attr, "stroke", true),
				parseBoolean(attr, "fill", false)
			);
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static EndCap parseEndCap(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("cap")) {
			NamedNodeMap attr = node.getAttributes();
			String endCap = parseString(attr, "style");
			if (endCap == null || endCap.equalsIgnoreCase("square")) {
				return EndCap.SQUARE;
			} else if (endCap.equalsIgnoreCase("round")) {
				return EndCap.ROUND;
			} else if (endCap.equalsIgnoreCase("butt")) {
				return EndCap.BUTT;
			} else {
				return null;
			}
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static class LineJoinMiterLimit {
		public final LineJoin lineJoin;
		public final float miterLimit;
		public LineJoinMiterLimit(LineJoin lineJoin, float miterLimit) {
			this.lineJoin = lineJoin;
			this.miterLimit = miterLimit;
		}
	}
	private static LineJoinMiterLimit parseLineJoin(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("join")) {
			NamedNodeMap attr = node.getAttributes();
			LineJoin lineJoin;
			String lineJoinString = parseString(attr, "style");
			if (lineJoinString == null || lineJoinString.equalsIgnoreCase("miter")) {
				lineJoin = LineJoin.MITER;
			} else if (lineJoinString.equalsIgnoreCase("round")) {
				lineJoin = LineJoin.ROUND;
			} else if (lineJoinString.equalsIgnoreCase("bevel")) {
				lineJoin = LineJoin.BEVEL;
			} else {
				lineJoin = null;
			}
			float miterLimit = parseFloat(attr, "limit", 10.0f);
			return new LineJoinMiterLimit(lineJoin, miterLimit);
		} else {
			throw new IOException("Unknown element: " + type);
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
	
	private static final Pattern NUMBER_PATTERN = Pattern.compile("([+-]?)([0-9]+([.][0-9]*)?|[.][0-9]+)");
	private static final float[] parseFloats(NamedNodeMap attr, String key) {
		if (attr == null) return null;
		Node node = attr.getNamedItem(key);
		if (node == null) return null;
		String text = node.getTextContent();
		if (text == null) return null;
		List<Float> floats = new ArrayList<Float>();
		Matcher m = NUMBER_PATTERN.matcher(text);
		while (m.find()) {
			try {
				float f = Float.parseFloat(m.group());
				floats.add(f);
			} catch (NumberFormatException nfe) {
				// ignored
			}
		}
		if (floats.isEmpty()) return null;
		int i = 0, n = floats.size();
		float[] a = new float[n];
		for (float f : floats) a[i++] = f;
		return a;
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
	
	private static class LNSXEntityResolver implements EntityResolver {
		@Override
		public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
			if (publicId.contains("PowerStroke") || systemId.contains("lnsx.dtd")) {
				return new InputSource(StrokeParser.class.getResourceAsStream("lnsx.dtd"));
			} else {
				return null;
			}
		}
	}
	
	private static class LNSXErrorHandler implements ErrorHandler {
		private final String name;
		public LNSXErrorHandler(String name) {
			this.name = name;
		}
		@Override
		public void error(SAXParseException e) throws SAXException {
			System.err.print("Warning: Failed to compile stroke set " + name + ": ");
			System.err.println("ERROR on "+e.getLineNumber()+":"+e.getColumnNumber()+": "+e.getMessage());
		}
		@Override
		public void fatalError(SAXParseException e) throws SAXException {
			System.err.print("Warning: Failed to compile stroke set " + name + ": ");
			System.err.println("FATAL ERROR on "+e.getLineNumber()+":"+e.getColumnNumber()+": "+e.getMessage());
		}
		@Override
		public void warning(SAXParseException e) throws SAXException {
			System.err.print("Warning: Failed to compile stroke set " + name + ": ");
			System.err.println("WARNING on "+e.getLineNumber()+":"+e.getColumnNumber()+": "+e.getMessage());
		}
	}
}
