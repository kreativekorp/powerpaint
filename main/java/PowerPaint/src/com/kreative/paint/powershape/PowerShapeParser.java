package com.kreative.paint.powershape;

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

public class PowerShapeParser {
	public static PowerShapeList parse(String name, InputStream in) throws IOException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(true); // make sure the XML is valid
			factory.setExpandEntityReferences(false); // don't allow custom entities
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setEntityResolver(new SHPXEntityResolver());
			builder.setErrorHandler(new SHPXErrorHandler(name));
			Document document = builder.parse(new InputSource(in));
			return parseDocument(document);
		} catch (ParserConfigurationException pce) {
			throw new IOException(pce);
		} catch (SAXException saxe) {
			throw new IOException(saxe);
		}
	}
	
	private static PowerShapeList parseDocument(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("#document")) {
			for (Node child : getChildren(node)) {
				String ctype = child.getNodeName();
				if (ctype.equalsIgnoreCase("shapes")) {
					if (child.hasAttributes() || child.hasChildNodes()) {
						return parseShapes(child);
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
	
	private static PowerShapeList parseShapes(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("shapes")) {
			NamedNodeMap attr = node.getAttributes();
			String name = parseString(attr, "name");
			PowerShapeList list = new PowerShapeList(name);
			for (Node child : getChildren(node)) {
				list.add(parseShape(child));
			}
			return list;
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static PowerShape parseShape(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("shape")) {
			NamedNodeMap attr = node.getAttributes();
			String name = parseString(attr, "name");
			WindingRule winding = parseWindingRule(attr, "winding", WindingRule.NON_ZERO);
			PowerShape shape = new PowerShape(winding, name);
			for (Node child : getChildren(node)) {
				String ctype = child.getNodeName();
				if (ctype.equalsIgnoreCase("param")) {
					shape.addParameter(parseParameter(child));
				} else {
					shape.addShape(parseSubshape(child));
				}
			}
			return shape;
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static Parameter parseParameter(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("param")) {
			NamedNodeMap attr = node.getAttributes();
			return new Parameter(
				parseString(attr, "name"),
				parseDouble(attr, "origin-x", 0.0),
				parseDouble(attr, "origin-y", 0.0),
				parseBoolean(attr, "coords", "polar", "rectangular", false),
				parseDouble(attr, "min-x", 0.0),
				parseDouble(attr, "min-y", 0.0),
				parseDouble(attr, "min-r", 0.0),
				parseDouble(attr, "min-a", 0.0),
				parseDouble(attr, "def-x", 0.0),
				parseDouble(attr, "def-y", 0.0),
				parseDouble(attr, "def-r", 0.0),
				parseDouble(attr, "def-a", 0.0),
				parseDouble(attr, "max-x", 0.0),
				parseDouble(attr, "max-y", 0.0),
				parseDouble(attr, "max-r", 0.0),
				parseDouble(attr, "max-a", 0.0)
			);
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static ParameterizedShape parseSubshape(Node node) throws IOException {
		String type = node.getNodeName();
		NamedNodeMap attr = node.getAttributes();
		if (type.equalsIgnoreCase("arc")) {
			return new ParameterizedShape.Arc(
				parseValue(attr, "cx", 0.0),
				parseValue(attr, "cy", 0.0),
				parseValue(attr, "rx", 0.0),
				parseValue(attr, "ry", 0.0),
				parseValue(attr, "start", 0.0),
				parseValue(attr, "extent", 0.0),
				parseArcType(node)
			);
		} else if (type.equalsIgnoreCase("circle")) {
			return new ParameterizedShape.Circle(
				parseValue(attr, "cx", 0.0),
				parseValue(attr, "cy", 0.0),
				parseValue(attr, "r", 0.0)
			);
		} else if (type.equalsIgnoreCase("ellipse")) {
			return new ParameterizedShape.Ellipse(
				parseValue(attr, "cx", 0.0),
				parseValue(attr, "cy", 0.0),
				parseValue(attr, "rx", 0.0),
				parseValue(attr, "ry", 0.0)
			);
		} else if (type.equalsIgnoreCase("line")) {
			return new ParameterizedShape.Line(
				parseValue(attr, "x1", 0.0),
				parseValue(attr, "y1", 0.0),
				parseValue(attr, "x2", 0.0),
				parseValue(attr, "y2", 0.0)
			);
		} else if (type.equalsIgnoreCase("path")) {
			return parsePath(node);
		} else if (type.equalsIgnoreCase("polygon")) {
			return new ParameterizedShape.Polygon(
				parseValues(attr, "points")
			);
		} else if (type.equalsIgnoreCase("polyline")) {
			return new ParameterizedShape.PolyLine(
				parseValues(attr, "points")
			);
		} else if (type.equalsIgnoreCase("rect")) {
			return new ParameterizedShape.Rect(
				parseValue(attr, "x", 0.0),
				parseValue(attr, "y", 0.0),
				parseValue(attr, "width", 0.0),
				parseValue(attr, "height", 0.0),
				parseValue(attr, "rx", 0.0),
				parseValue(attr, "ry", 0.0)
			);
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static ParameterizedPath parsePath(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("path")) {
			NamedNodeMap attr = node.getAttributes();
			String d = parseString(attr, "d");
			ParameterizedPath p = new ParameterizedPath();
			if (d != null) {
				try {
					ExpressionLexer lexer = new ExpressionLexer(d);
					ExpressionParser parser = new ExpressionParser(lexer);
					while (lexer.hasNext()) {
						char inst = lexer.getNext().charAt(0);
						int n = ParameterizedPath.operandCount(inst);
						ParameterizedValue[] v = new ParameterizedValue[n];
						for (int i = 0; i < n; i++) {
							int start = lexer.currentIndex();
							Expression expr = parser.parseExpression();
							int end = lexer.currentIndex();
							String source = d.substring(start, end);
							v[i] = new ParameterizedValue(source, expr);
						}
						p.add(inst, v);
					}
				} catch (ExpressionParserException e) {}
			}
			return p;
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static ArcType parseArcType(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("arc")) {
			NamedNodeMap attr = node.getAttributes();
			String arcType = parseString(attr, "type");
			if (arcType == null || arcType.equalsIgnoreCase("open")) {
				return ArcType.OPEN;
			} else if (arcType.equalsIgnoreCase("chord")) {
				return ArcType.CHORD;
			} else if (arcType.equalsIgnoreCase("pie")) {
				return ArcType.PIE;
			} else {
				return null;
			}
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static ParameterizedValue[] parseValues(NamedNodeMap attr, String key) {
		if (attr == null) return new ParameterizedValue[0];
		Node node = attr.getNamedItem(key);
		if (node == null) return new ParameterizedValue[0];
		String text = node.getTextContent();
		if (text == null) return new ParameterizedValue[0];
		try {
			List<ParameterizedValue> values = new ArrayList<ParameterizedValue>();
			ExpressionLexer lexer = new ExpressionLexer(text);
			ExpressionParser parser = new ExpressionParser(lexer);
			while (lexer.hasNext()) {
				int start = lexer.currentIndex();
				Expression expr = parser.parseExpression();
				int end = lexer.currentIndex();
				String source = text.substring(start, end);
				values.add(new ParameterizedValue(source, expr));
			}
			return values.toArray(new ParameterizedValue[values.size()]);
		} catch (ExpressionParserException e) {
			return new ParameterizedValue[0];
		}
	}
	
	private static ParameterizedValue parseValue(NamedNodeMap attr, String key, double def) {
		if (attr == null) return new ParameterizedValue(def);
		Node node = attr.getNamedItem(key);
		if (node == null) return new ParameterizedValue(def);
		String text = node.getTextContent();
		if (text == null) return new ParameterizedValue(def);
		try {
			ExpressionParser parser = new ExpressionParser(text);
			Expression expr = parser.parse();
			return new ParameterizedValue(text, expr);
		} catch (ExpressionParserException e) {
			return new ParameterizedValue(def);
		}
	}
	
	private static WindingRule parseWindingRule(NamedNodeMap attr, String key, WindingRule def) {
		if (attr == null) return def;
		Node node = attr.getNamedItem(key);
		if (node == null) return def;
		String text = node.getTextContent();
		if (text == null) return def;
		if (
			text.equalsIgnoreCase("eo") ||
			text.equalsIgnoreCase("evenodd") ||
			text.equalsIgnoreCase("even-odd")
		) {
			return WindingRule.EVEN_ODD;
		}
		if (
			text.equalsIgnoreCase("nz") ||
			text.equalsIgnoreCase("nonzero") ||
			text.equalsIgnoreCase("non-zero")
		) {
			return WindingRule.NON_ZERO;
		}
		return def;
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
	
	private static double parseDouble(NamedNodeMap attr, String key, double def) {
		if (attr == null) return def;
		Node node = attr.getNamedItem(key);
		if (node == null) return def;
		String text = node.getTextContent();
		if (text == null) return def;
		try { return Double.parseDouble(text.trim()); }
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
	
	private static class SHPXEntityResolver implements EntityResolver {
		@Override
		public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
			if (publicId.contains("PowerShape") || systemId.contains("shpx.dtd")) {
				return new InputSource(PowerShapeParser.class.getResourceAsStream("shpx.dtd"));
			} else {
				return null;
			}
		}
	}
	
	private static class SHPXErrorHandler implements ErrorHandler {
		private final String name;
		public SHPXErrorHandler(String name) {
			this.name = name;
		}
		@Override
		public void error(SAXParseException e) throws SAXException {
			System.err.print("Warning: Failed to compile shape set " + name + ": ");
			System.err.println("ERROR on "+e.getLineNumber()+":"+e.getColumnNumber()+": "+e.getMessage());
		}
		@Override
		public void fatalError(SAXParseException e) throws SAXException {
			System.err.print("Warning: Failed to compile shape set " + name + ": ");
			System.err.println("FATAL ERROR on "+e.getLineNumber()+":"+e.getColumnNumber()+": "+e.getMessage());
		}
		@Override
		public void warning(SAXParseException e) throws SAXException {
			System.err.print("Warning: Failed to compile shape set " + name + ": ");
			System.err.println("WARNING on "+e.getLineNumber()+":"+e.getColumnNumber()+": "+e.getMessage());
		}
	}
}
