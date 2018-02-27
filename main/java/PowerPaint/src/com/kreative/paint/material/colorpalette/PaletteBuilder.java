package com.kreative.paint.material.colorpalette;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaletteBuilder {
	public static void main(String[] args) {
		Options o = new Options();
		boolean processingArgs = true;
		boolean actionTaken = false;
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			if (processingArgs && arg.startsWith("-")) {
				if (arg.equals("--")) {
					processingArgs = false;
				} else if ((arg.equals("-n") || arg.equals("--name")) && argi < args.length) {
					o.name = args[argi++];
				} else if (arg.equals("-r") || arg.equals("--ordered")) {
					o.ordered = true;
				} else if (arg.equals("-u") || arg.equals("--unordered")) {
					o.ordered = false;
				} else if (arg.equals("-w") && argi < args.length) {
					o.hw = o.sw = o.vw = parseInt(args[argi++]);
				} else if (arg.equals("-h") && argi < args.length) {
					o.hh = o.sh = o.vh = parseInt(args[argi++]);
				} else if (arg.equals("-dh")) {
					o.hd = o.sd = o.vd = false;
				} else if (arg.equals("-dv")) {
					o.hd = o.sd = o.vd = true;
				} else if (arg.equals("-cw") && argi < args.length) {
					o.hcw = o.scw = o.vcw = parseInt(args[argi++]);
				} else if (arg.equals("-ch") && argi < args.length) {
					o.hch = o.sch = o.vch = parseInt(args[argi++]);
				} else if (arg.equals("-hw") && argi < args.length) {
					o.hw = parseInt(args[argi++]);
				} else if (arg.equals("-hh") && argi < args.length) {
					o.hh = parseInt(args[argi++]);
				} else if (arg.equals("-hdh")) {
					o.hd = false;
				} else if (arg.equals("-hdv")) {
					o.hd = true;
				} else if (arg.equals("-hcw") && argi < args.length) {
					o.hcw = parseInt(args[argi++]);
				} else if (arg.equals("-hch") && argi < args.length) {
					o.hch = parseInt(args[argi++]);
				} else if (arg.equals("-sw") && argi < args.length) {
					o.sw = parseInt(args[argi++]);
				} else if (arg.equals("-sh") && argi < args.length) {
					o.sh = parseInt(args[argi++]);
				} else if (arg.equals("-sdh")) {
					o.sd = false;
				} else if (arg.equals("-sdv")) {
					o.sd = true;
				} else if (arg.equals("-scw") && argi < args.length) {
					o.scw = parseInt(args[argi++]);
				} else if (arg.equals("-sch") && argi < args.length) {
					o.sch = parseInt(args[argi++]);
				} else if (arg.equals("-vw") && argi < args.length) {
					o.vw = parseInt(args[argi++]);
				} else if (arg.equals("-vh") && argi < args.length) {
					o.vh = parseInt(args[argi++]);
				} else if (arg.equals("-vdh")) {
					o.vd = false;
				} else if (arg.equals("-vdv")) {
					o.vd = true;
				} else if (arg.equals("-vcw") && argi < args.length) {
					o.vcw = parseInt(args[argi++]);
				} else if (arg.equals("-vch") && argi < args.length) {
					o.vch = parseInt(args[argi++]);
				} else if (arg.equals("-oh") || arg.equals("--horiz") || arg.equals("--horizontal")) {
					o.orientation = RCPXOrientation.HORIZONTAL;
				} else if (arg.equals("-os") || arg.equals("--square")) {
					o.orientation = RCPXOrientation.SQUARE;
				} else if (arg.equals("-ov") || arg.equals("--vert") || arg.equals("--vertical")) {
					o.orientation = RCPXOrientation.VERTICAL;
				} else if ((arg.equals("-f") || arg.equals("--format")) && argi < args.length) {
					String fmt = args[argi++];
					try { o.format = Format.valueOf(fmt.toUpperCase()); }
					catch (IllegalArgumentException e) {
						System.err.println("Unknown format: " + fmt.toLowerCase());
						actionTaken = true;
					}
				} else if ((arg.equals("-o") || arg.equals("--output")) && argi < args.length) {
					o.output = new File(args[argi++]);
				} else if (arg.equals("--help")) {
					printHelp();
					actionTaken = true;
				} else {
					System.err.println("Unknown option: " + arg);
					actionTaken = true;
				}
			} else {
				try {
					RCPXPalette pal = createPalette(o, arg);
					OutputStream out = (o.output != null) ? new FileOutputStream(o.output) : System.out;
					o.format.writer.write(pal, out);
					if (o.output != null) out.close();
				} catch (IllegalArgumentException e) {
					System.err.println(e.getMessage());
				} catch (IOException e) {
					System.err.println("Error writing " + o.output);
				}
				o.output = null;
				actionTaken = true;
			}
		}
		if (!actionTaken) printHelp();
	}
	
	private static class Options {
		public String name = null;
		public boolean ordered = false;
		public int hw = 0, hh = 0, hcw = 0, hch = 0;
		public int sw = 0, sh = 0, scw = 0, sch = 0;
		public int vw = 0, vh = 0, vcw = 0, vch = 0;
		public boolean hd = false, sd = false, vd = true;
		public RCPXOrientation orientation = RCPXOrientation.HORIZONTAL;
		public Format format = Format.RCPX;
		public File output = null;
	}
	
	private static int parseInt(String s) {
		try { return Integer.parseInt(s); }
		catch (NumberFormatException e) { return 0; }
	}
	
	private static void printHelp() {
		System.out.println();
		System.out.println("Usage:");
		System.out.println("  java com.kreative.paint.material.colorpalette.PaletteBuilder <options> <spec>");
		System.out.println();
		System.out.println("Options:");
		System.out.println("  -n <name>       Set palette name.");
		System.out.println("  -r              Set color ordering to ordered.");
		System.out.println("  -u              Set color ordering to unordered.");
		System.out.println("  -w <cells>      Set width of palette for all orientations.");
		System.out.println("  -h <cells>      Set height of palette for all orientations.");
		System.out.println("  -dh, -dv        Set direction of palette for all orientations.");
		System.out.println("  -cw <pixels>    Set width of palette cells for all orientations.");
		System.out.println("  -ch <pixels>    Set height of palette cells for all orientations.");
		System.out.println("  -hw <cells>     Set width of palette for horizontal orientation.");
		System.out.println("  -hh <cells>     Set height of palette for horizontal orientation.");
		System.out.println("  -hdh, -hdv      Set direction of palette for horizontal orientation.");
		System.out.println("  -hcw <pixels>   Set width of palette cells for horizontal orientation.");
		System.out.println("  -hch <pixels>   Set height of palette cells for horizontal orientation.");
		System.out.println("  -sw <cells>     Set width of palette for square orientation.");
		System.out.println("  -sh <cells>     Set height of palette for square orientation.");
		System.out.println("  -sdh, -sdv      Set direction of palette for square orientation.");
		System.out.println("  -scw <pixels>   Set width of palette cells for square orientation.");
		System.out.println("  -sch <pixels>   Set height of palette cells for square orientation.");
		System.out.println("  -vw <cells>     Set width of palette for vertical orientation.");
		System.out.println("  -vh <cells>     Set height of palette for vertical orientation.");
		System.out.println("  -vdh, -vdv      Set direction of palette for vertical orientation.");
		System.out.println("  -vcw <pixels>   Set width of palette cells for vertical orientation.");
		System.out.println("  -vch <pixels>   Set height of palette cells for vertical orientation.");
		System.out.println("  -oh             Set default orientation to horizontal.");
		System.out.println("  -os             Set default orientation to square.");
		System.out.println("  -ov             Set default orientation to vertical.");
		System.out.println("  -f <format>     Set the output format.");
		System.out.println("  -o <path>       Write output to the specified file.");
		System.out.println("  --              Process remaining arguments as palette specs.");
		System.out.println();
		System.out.println("Formats:");
		for (Format fmt : Format.values()) System.out.println("  " + fmt.description);
		System.out.println();
		System.out.println("Palette Spec Syntax Examples:");
		System.out.println("  R=255             single channel value (integer)");
		System.out.println("  R=4/5             single channel value (fraction)");
		System.out.println("  R=0.6             single channel value (decimal)");
		System.out.println("  R=40%             single channel value (percentage)");
		System.out.println("  R{0,85,170,255}   multiple channel values (using any number form given above)");
		System.out.println("  R4                ascending ramp of channel values ({0..2^n-1} / 2^n-1)");
		System.out.println("  R:4               descending ramp of channel values ({2^n-1..0} / 2^n-1)");
		System.out.println("  R/5               ascending ramp of channel values ({0..n} / n)");
		System.out.println("  R\\5               descending ramp of channel values ({n..0} / n)");
		System.out.println("  RGB(255,255,255)  single color (using any number form given above)");
		System.out.println("  R3G3B2            cross product of channel values");
		System.out.println("  R3*G3*B2          cross product of channel values");
		System.out.println("  R3G3B3 ^ R2G2B2   intersection of color sets");
		System.out.println("  R3G3B3 - R2G2B2   difference of color sets");
		System.out.println("  R3G3B3 + R2G2B2   union of color sets");
		System.out.println("  R3G3B3,R2G2B2     union of color sets");
		System.out.println();
	}
	
	private static RCPXPalette createPalette(Options o, String arg) {
		List<RCPXColor> colors = new ArrayList<RCPXColor>();
		for (ColorSpec spec : parse(arg).generate(null)) {
			ColorSystem cs = ColorSystem.resolve(spec);
			if (cs != null) colors.add(cs.rcpxColor(spec));
		}
		int hw = o.hw, hh = o.hh, hcw = o.hcw, hch = o.hch;
		int sw = o.sw, sh = o.sh, scw = o.scw, sch = o.sch;
		int vw = o.vw, vh = o.vh, vcw = o.vcw, vch = o.vch;
		boolean hd = o.hd, sd = o.sd, vd = o.vd;
		PaletteDimensions pd = PaletteDimensions.forColorCount(colors.size());
		if (hw <= 0 && hh <= 0) { hw = pd.horizWidth ; hh = pd.horizHeight ; hd = pd.horizColumns ; }
		if (sw <= 0 && sh <= 0) { sw = pd.squareWidth; sh = pd.squareHeight; sd = pd.squareColumns; }
		if (vw <= 0 && vh <= 0) { vw = pd.vertWidth  ; vh = pd.vertHeight  ; vd = pd.vertColumns  ; }
		if (hw <= 0) hw = (colors.size() + hh - 1) / hh;
		if (hh <= 0) hh = (colors.size() + hw - 1) / hw;
		if (sw <= 0) sw = (colors.size() + sh - 1) / sh;
		if (sh <= 0) sh = (colors.size() + sw - 1) / sw;
		if (vw <= 0) vw = (colors.size() + vh - 1) / vh;
		if (vh <= 0) vh = (colors.size() + vw - 1) / vw;
		pd = new PaletteDimensions(colors.size(), hw, hh, hd, sw, sh, sd, vw, vh, vd);
		if (hcw <= 0) hcw = Math.max(288 / hw, 8);
		if (hch <= 0) hch = Math.max( 72 / hh, 8);
		if (scw <= 0) scw = Math.max(144 / sw, 8);
		if (sch <= 0) sch = Math.max(144 / sh, 8);
		if (vcw <= 0) vcw = Math.max( 72 / vw, 8);
		if (vch <= 0) vch = Math.max(288 / vh, 8);
		int hpw = hw * hcw + 1, hph = hh * hch + 1;
		int spw = sw * scw + 1, sph = sh * sch + 1;
		int vpw = vw * vcw + 1, vph = vh * vch + 1;
		while (spw > (sph + sph / 2)) sph += sh;
		while (sph > (spw + spw / 2)) spw += sw;
		return new RCPXPalette(
			o.name, o.orientation, hpw, hph, spw, sph, vpw, vph,
			colors, o.ordered, pd.createLayout()
		);
	}
	
	private static final ColorGenerator parse(String s) {
		PaletteBuilder b = new PaletteBuilder(s);
		b.parseWhitespace();
		ColorGenerator g = b.parseExpression();
		b.parseWhitespace();
		b.parseEOF();
		return g;
	}
	
	private final CharacterIterator iter;
	
	private PaletteBuilder(String s) {
		this.iter = new StringCharacterIterator(s);
	}
	
	private ColorGenerator parseExpression() {
		return parseUnion();
	}
	
	private ColorGenerator parseUnion() {
		ColorGenerator g = parseDifference();
		parseWhitespace();
		char ch = iter.current();
		while (ch == ',' || ch == '+') {
			iter.next();
			parseWhitespace();
			g = new Union(g, parseDifference());
			parseWhitespace();
			ch = iter.current();
		}
		return g;
	}
	
	private ColorGenerator parseDifference() {
		ColorGenerator g = parseIntersection();
		parseWhitespace();
		while (iter.current() == '-') {
			iter.next();
			parseWhitespace();
			g = new Difference(g, parseIntersection());
			parseWhitespace();
		}
		return g;
	}
	
	private ColorGenerator parseIntersection() {
		ColorGenerator g = parseCrossProduct();
		parseWhitespace();
		while (iter.current() == '^') {
			iter.next();
			parseWhitespace();
			g = new Intersection(g, parseCrossProduct());
			parseWhitespace();
		}
		return g;
	}
	
	private ColorGenerator parseCrossProduct() {
		ColorGenerator g = parseFactor();
		parseWhitespace();
		char ch = iter.current();
		while (ch == '*' || Character.isUpperCase(ch) || Character.isLowerCase(ch)) {
			if (ch == '*') {
				iter.next();
				parseWhitespace();
			}
			g = new CrossProduct(g, parseFactor());
			parseWhitespace();
			ch = iter.current();
		}
		return g;
	}
	
	private ColorGenerator parseFactor() {
		if (iter.current() == '(') {
			iter.next();
			parseWhitespace();
			ColorGenerator g = parseExpression();
			parseWhitespace();
			parseChar(')', true);
			return g;
		} else {
			return parseValueGenerator();
		}
	}
	
	private ColorGenerator parseValueGenerator() {
		String name = parseChannelName();
		parseWhitespace();
		char ch = iter.current();
		if (ch == '(' || Character.isUpperCase(ch) || Character.isLowerCase(ch)) {
			List<String> names = new ArrayList<String>();
			while (Character.isUpperCase(ch) || Character.isLowerCase(ch)) {
				names.add(parseChannelName());
				parseWhitespace();
				ch = iter.current();
			}
			parseChar('(', true);
			parseWhitespace();
			ChannelValue value = parseChannelValue();
			ColorSpec spec = new ColorSpec(name, value);
			parseWhitespace();
			for (String n : names) {
				parseChar(',', false);
				parseWhitespace();
				ChannelValue v = parseChannelValue();
				spec = spec.set(n, v);
				parseWhitespace();
			}
			parseChar(')', true);
			return new Singleton(spec);
		} else if (ch == '=') {
			iter.next();
			parseWhitespace();
			ChannelValue value = parseChannelValue();
			ColorSpec spec = new ColorSpec(name, value);
			return new Singleton(spec);
		} else if (ch == '{') {
			iter.next();
			parseWhitespace();
			List<ChannelValue> values = new ArrayList<ChannelValue>();
			while (iter.current() != '}') {
				if (!values.isEmpty()) {
					parseChar(',', false);
					parseWhitespace();
				}
				values.add(parseChannelValue());
				parseWhitespace();
			}
			parseChar('}', true);
			return new ValueList(name, values);
		} else if (ch == '/' || ch == '\\' || ch == ':') {
			iter.next();
			parseWhitespace();
			int denom = parseInteger();
			if (denom == 0) throw new IllegalArgumentException("Division by zero at " + iter.getIndex());
			switch (ch) {
				case '/': return new ValueRange(name, denom, false);
				case '\\': return new ValueRange(name, denom, true);
				case ':': return new ValueRange(name, (1 << denom) - 1, true);
				default: throw new RuntimeException("Expected operator at " + iter.getIndex());
			}
		} else if (Character.digit(ch, 10) >= 0) {
			int denom = parseInteger();
			if (denom == 0) throw new IllegalArgumentException("Division by zero at " + iter.getIndex());
			return new ValueRange(name, (1 << denom) - 1, false);
		} else {
			throw new IllegalArgumentException("Expected number at " + iter.getIndex());
		}
	}
	
	private String parseChannelName() {
		char ch = iter.current();
		if (Character.isUpperCase(ch)) {
			StringBuffer nb = new StringBuffer();
			nb.append(ch);
			iter.next();
			ch = iter.current();
			while (Character.isLowerCase(ch)) {
				nb.append(ch);
				iter.next();
				ch = iter.current();
			}
			return nb.toString();
		} else if (Character.isLowerCase(ch)) {
			iter.next();
			return Character.toString(ch);
		} else {
			throw new IllegalArgumentException("Expected name at " + iter.getIndex());
		}
	}
	
	private ChannelValue parseChannelValue() {
		char ch = iter.current();
		if (ch == '$') {
			iter.next();
			return parseChannelValueInteger(16);
		} else if (ch == '0') {
			iter.next();
			ch = iter.current();
			if (ch == 'X' || ch == 'x' || ch == 'H' || ch == 'h') {
				iter.next();
				return parseChannelValueInteger(16);
			} else if (ch == 'O' || ch == 'o') {
				iter.next();
				return parseChannelValueInteger(8);
			} else if (ch == 'B' || ch == 'b') {
				iter.next();
				return parseChannelValueInteger(2);
			} else {
				return parseChannelValueDecimal("0");
			}
		} else if (ch == '.' || Character.digit(ch, 10) >= 0) {
			return parseChannelValueDecimal("");
		} else {
			throw new NumberFormatException("Expected number at " + iter.getIndex());
		}
	}
	
	private ChannelValue parseChannelValueInteger(int b) {
		int p = 0, q = 0;
		int d = Character.digit(iter.current(), b);
		while (d >= 0) {
			iter.next();
			p = p * b + d;
			q = q * b + b - 1;
			d = Character.digit(iter.current(), b);
		}
		if (q != 0) return new ChannelValue(p, q);
		throw new NumberFormatException("Expected digit at " + iter.getIndex());
	}
	
	private ChannelValue parseChannelValueDecimal(String prefix) {
		StringBuffer ps = new StringBuffer(prefix);
		char ch = iter.current();
		int d = Character.digit(ch, 10);
		while (ch == '.' || d >= 0) {
			iter.next();
			ps.append(ch);
			ch = iter.current();
			d = Character.digit(ch, 10);
		}
		if (ch == '%') {
			iter.next();
			try { return new ChannelValue(Integer.parseInt(ps.toString()), 100); }
			catch (NumberFormatException ignored) {}
			try { return new ChannelValue(Double.parseDouble(ps.toString()) / 100.0); }
			catch (NumberFormatException ignored) {}
			throw new NumberFormatException("Expected number near " + iter.getIndex());
		} else if (ch == '/') {
			iter.next();
			StringBuffer qs = new StringBuffer();
			ch = iter.current();
			d = Character.digit(ch, 10);
			while (ch == '.' || d >= 0) {
				iter.next();
				qs.append(ch);
				ch = iter.current();
				d = Character.digit(ch, 10);
			}
			try {
				int p = Integer.parseInt(ps.toString());
				int q = Integer.parseInt(qs.toString());
				if (q != 0) return new ChannelValue(p, q);
			} catch (NumberFormatException ignored) {}
			try {
				double p = Double.parseDouble(ps.toString());
				double q = Double.parseDouble(qs.toString());
				if (q != 0) return new ChannelValue(p / q);
			} catch (NumberFormatException ignored) {}
			throw new NumberFormatException("Expected number near " + iter.getIndex());
		} else {
			try { return new ChannelValue(ps.toString()); }
			catch (NumberFormatException ignored) {}
			try { return new ChannelValue(Double.parseDouble(ps.toString())); }
			catch (NumberFormatException ignored) {}
			throw new NumberFormatException("Expected number near " + iter.getIndex());
		}
	}
	
	private int parseInteger() {
		int d = Character.digit(iter.current(), 10);
		if (d >= 0) {
			int v = d;
			iter.next();
			d = Character.digit(iter.current(), 10);
			while (d >= 0) {
				v = v * 10 + d;
				iter.next();
				d = Character.digit(iter.current(), 10);
			}
			return v;
		} else {
			throw new NumberFormatException("Expected number at " + iter.getIndex());
		}
	}
	
	private void parseChar(char ch, boolean req) {
		if (iter.current() == ch) {
			iter.next();
		} else if (req) {
			throw new IllegalArgumentException("Expected " + ch + " at " + iter.getIndex());
		}
	}
	
	private void parseWhitespace() {
		while (Character.isWhitespace(iter.current())) iter.next();
	}
	
	private void parseEOF() {
		if (iter.current() != CharacterIterator.DONE) {
			throw new IllegalArgumentException("Expected end of expression at " + iter.getIndex());
		}
	}
	
	private static final ChannelValue B0 = new ChannelValue(0, 255);
	private static final ChannelValue B255 = new ChannelValue(255, 255);
	private static final ChannelValue H0 = new ChannelValue(0, 360);
	private static final ChannelValue C0 = new ChannelValue(0, 100);
	private static final ChannelValue C100 = new ChannelValue(100, 100);
	
	private static final class ChannelValue {
		private final Double d;
		private final Integer p;
		private final Integer q;
		private final Integer pl;
		public ChannelValue(double d) {
			this.d = d;
			this.p = null;
			this.q = null;
			this.pl = null;
		}
		public ChannelValue(int p, int q) {
			this.d = null;
			this.p = p;
			this.q = q;
			this.pl = null;
		}
		public ChannelValue(String p) {
			this.d = null;
			this.p = Integer.parseInt(p);
			this.q = null;
			this.pl = p.length();
		}
		public boolean isByteValue() {
			if (d != null) return ((d * 255.0) == (int)(d * 255.0));
			if (q != null) return (255L * p % q == 0L);
			return (p >= 0 && p <= 255 && pl <= 3);
		}
		public int byteValue() {
			if (d != null) return (int)(d * 255.0);
			if (q != null) return (int)(255L * p / q);
			return p;
		}
		public boolean isShortValue() {
			if (d != null) return ((d * 65535.0) == (int)(d * 65535.0));
			if (q != null) return (65535L * p % q == 0L);
			return (p >= 0 && p <= 65535 && pl <= 5);
		}
		public int shortValue() {
			if (d != null) return (int)(d * 65535.0);
			if (q != null) return (int)(65535L * p / q);
			return p;
		}
		public float floatValue(int... denominators) {
			if (d != null) return d.floatValue();
			if (q != null) return (float)p / (float)q;
			if (denominators == null) return Float.NaN;
			if (denominators.length == 0) return Float.NaN;
			for (int q : denominators) {
				int ql = Integer.toString(q).length();
				if (p <= q && pl <= ql) return (float)p / (float)q;
			}
			int q = denominators[denominators.length - 1];
			return (float)p / (float)q;
		}
		public float scaledFloatValue(int numerator, int... denominators) {
			if (d != null) return numerator * d.floatValue();
			if (q != null) return numerator * (float)p / (float)q;
			if (denominators == null) return Float.NaN;
			if (denominators.length == 0) return Float.NaN;
			for (int q : denominators) {
				int ql = Integer.toString(q).length();
				if (p <= q && pl <= ql) return numerator * (float)p / (float)q;
			}
			int q = denominators[denominators.length - 1];
			return numerator * (float)p / (float)q;
		}
		@Override
		public boolean equals(Object o) {
			if (o instanceof ChannelValue) {
				ChannelValue that = (ChannelValue)o;
				boolean c1 = (this.d != null || this.q != null);
				boolean c2 = (that.d != null || that.q != null);
				if (c1 && c2) return this.floatValue() == that.floatValue();
				if (c1) return this.floatValue() == 0 && that.p.intValue() == 0;
				if (c2) return this.p.intValue() == 0 && that.floatValue() == 0;
				return this.p.intValue() == that.p.intValue();
			} else {
				return false;
			}
		}
		@Override
		public int hashCode() {
			if (d != null) return Float.floatToIntBits(d.floatValue());
			if (q != null) return Float.floatToIntBits((float)p / (float)q);
			return p;
		}
		@Override
		public String toString() {
			if (d != null) return d.toString();
			if (q != null) return p + "/" + q;
			return p + "/?";
		}
	}
	
	private static final class ColorSpec {
		private final Map<String, ChannelValue> m;
		public ColorSpec(String name, ChannelValue value) {
			this.m = new HashMap<String, ChannelValue>();
			this.m.put(name, value);
		}
		private ColorSpec(Map<String, ChannelValue> m, String n, ChannelValue v) {
			this.m = new HashMap<String, ChannelValue>();
			this.m.putAll(m);
			this.m.put(n, v);
		}
		private ColorSpec(Map<String, ChannelValue> m1, Map<String, ChannelValue> m2) {
			this.m = new HashMap<String, ChannelValue>();
			this.m.putAll(m1);
			this.m.putAll(m2);
		}
		public List<String> getChannelNames() {
			List<String> keys = new ArrayList<String>();
			keys.addAll(m.keySet());
			Collections.sort(keys);
			return keys;
		}
		public ChannelValue get(String name, ChannelValue def) {
			if (m.containsKey(name)) return m.get(name);
			String uc = name.toUpperCase();
			if (m.containsKey(uc)) return m.get(uc);
			String lc = name.toLowerCase();
			if (m.containsKey(lc)) return m.get(lc);
			return def;
		}
		public ColorSpec set(String name, ChannelValue value) {
			return new ColorSpec(this.m, name, value);
		}
		public ColorSpec merge(ColorSpec that) {
			return new ColorSpec(this.m, that.m);
		}
		@Override
		public boolean equals(Object o) {
			if (o instanceof ColorSpec) {
				ColorSpec that = (ColorSpec)o;
				return this.m.equals(that.m);
			} else {
				return false;
			}
		}
		@Override
		public int hashCode() {
			return m.hashCode();
		}
		@Override
		public String toString() {
			List<String> keys = getChannelNames();
			StringBuffer sb = new StringBuffer();
			for (String key : keys) sb.append(key);
			sb.append("(");
			boolean first = true;
			for (String key : keys) {
				if (first) first = false;
				else sb.append(",");
				sb.append(m.get(key));
			}
			sb.append(")");
			return sb.toString();
		}
	}
	
	private static abstract class ColorGenerator {
		public abstract List<ColorSpec> generate(List<ColorSpec> specs);
	}
	
	private static final class Singleton extends ColorGenerator {
		private final ColorSpec spec;
		public Singleton(ColorSpec spec) {
			this.spec = spec;
		}
		public List<ColorSpec> generate(List<ColorSpec> specs) {
			if (specs == null) specs = new ArrayList<ColorSpec>();
			specs.add(spec);
			return specs;
		}
	}
	
	private static final class ValueList extends ColorGenerator {
		private final String name;
		private final ChannelValue[] values;
		public ValueList(String name, List<ChannelValue> values) {
			this.name = name;
			this.values = values.toArray(new ChannelValue[values.size()]);
		}
		public List<ColorSpec> generate(List<ColorSpec> specs) {
			if (specs == null) specs = new ArrayList<ColorSpec>();
			for (ChannelValue v : values) specs.add(new ColorSpec(name, v));
			return specs;
		}
	}
	
	private static final class ValueRange extends ColorGenerator {
		private final String name;
		private final int denom;
		private final boolean desc;
		public ValueRange(String name, int denom, boolean desc) {
			this.name = name;
			this.denom = denom;
			this.desc = desc;
		}
		public List<ColorSpec> generate(List<ColorSpec> specs) {
			if (specs == null) specs = new ArrayList<ColorSpec>();
			if (desc) {
				for (int i = denom; i >= 0; i--) {
					specs.add(new ColorSpec(name, new ChannelValue(i, denom)));
				}
			} else {
				for (int i = 0; i <= denom; i++) {
					specs.add(new ColorSpec(name, new ChannelValue(i, denom)));
				}
			}
			return specs;
		}
	}
	
	private static final class CrossProduct extends ColorGenerator {
		private final ColorGenerator a;
		private final ColorGenerator b;
		public CrossProduct(ColorGenerator a, ColorGenerator b) {
			this.a = a;
			this.b = b;
		}
		public List<ColorSpec> generate(List<ColorSpec> specs) {
			if (specs == null) specs = new ArrayList<ColorSpec>();
			List<ColorSpec> la = a.generate(null);
			List<ColorSpec> lb = b.generate(null);
			for (ColorSpec ca : la) {
				for (ColorSpec cb : lb) {
					specs.add(ca.merge(cb));
				}
			}
			return specs;
		}
	}
	
	private static final class Intersection extends ColorGenerator {
		private final ColorGenerator a;
		private final ColorGenerator b;
		public Intersection(ColorGenerator a, ColorGenerator b) {
			this.a = a;
			this.b = b;
		}
		public List<ColorSpec> generate(List<ColorSpec> specs) {
			if (specs == null) specs = new ArrayList<ColorSpec>();
			List<ColorSpec> s = a.generate(null);
			s.retainAll(b.generate(null));
			specs.addAll(s);
			return specs;
		}
	}
	
	private static final class Difference extends ColorGenerator {
		private final ColorGenerator a;
		private final ColorGenerator b;
		public Difference(ColorGenerator a, ColorGenerator b) {
			this.a = a;
			this.b = b;
		}
		public List<ColorSpec> generate(List<ColorSpec> specs) {
			if (specs == null) specs = new ArrayList<ColorSpec>();
			List<ColorSpec> s = a.generate(null);
			s.removeAll(b.generate(null));
			specs.addAll(s);
			return specs;
		}
	}
	
	private static final class Union extends ColorGenerator {
		private final ColorGenerator a;
		private final ColorGenerator b;
		public Union(ColorGenerator a, ColorGenerator b) {
			this.a = a;
			this.b = b;
		}
		public List<ColorSpec> generate(List<ColorSpec> specs) {
			specs = a.generate(specs);
			specs = b.generate(specs);
			return specs;
		}
	}
	
	private static enum ColorSystem {
		YA("Y","A") {
			@Override
			public RCPXColor rcpxColor(ColorSpec spec) {
				ChannelValue y = spec.get("Y", B0);
				ChannelValue a = spec.get("A", B255);
				if (a.floatValue(255, 65535) == 1.0) {
					if (y.isByteValue()) {
						int yv = y.byteValue();
						return new RCPXColor.RGB(yv, yv, yv, null);
					} else if (y.isShortValue()) {
						int yv = y.shortValue();
						return new RCPXColor.RGB16(yv, yv, yv, null);
					} else {
						float yv = y.floatValue(255, 65535);
						return new RCPXColor.RGBD(yv, yv, yv, null);
					}
				} else {
					if (y.isByteValue() && a.isByteValue()) {
						int yv = y.byteValue();
						int av = a.byteValue();
						return new RCPXColor.RGBA(yv, yv, yv, av, null);
					} else if (y.isShortValue() && a.isShortValue()) {
						int yv = y.shortValue();
						int av = a.shortValue();
						return new RCPXColor.RGBA16(yv, yv, yv, av, null);
					} else {
						float yv = y.floatValue(255, 65535);
						float av = a.floatValue(255, 65535);
						return new RCPXColor.RGBAD(yv, yv, yv, av, null);
					}
				}
			}
		},
		RGBA("R","G","B","A") {
			@Override
			public RCPXColor rcpxColor(ColorSpec spec) {
				ChannelValue r = spec.get("R", B0);
				ChannelValue g = spec.get("G", B0);
				ChannelValue b = spec.get("B", B0);
				ChannelValue a = spec.get("A", B255);
				if (a.floatValue(255, 65535) == 1.0) {
					if (r.isByteValue() && g.isByteValue() && b.isByteValue()) {
						int rv = r.byteValue();
						int gv = g.byteValue();
						int bv = b.byteValue();
						return new RCPXColor.RGB(rv, gv, bv, null);
					} else if (r.isShortValue() && g.isShortValue() && b.isShortValue()) {
						int rv = r.shortValue();
						int gv = g.shortValue();
						int bv = b.shortValue();
						return new RCPXColor.RGB16(rv, gv, bv, null);
					} else {
						float rv = r.floatValue(255, 65535);
						float gv = g.floatValue(255, 65535);
						float bv = b.floatValue(255, 65535);
						return new RCPXColor.RGBD(rv, gv, bv, null);
					}
				} else {
					if (r.isByteValue() && g.isByteValue() && b.isByteValue() && a.isByteValue()) {
						int rv = r.byteValue();
						int gv = g.byteValue();
						int bv = b.byteValue();
						int av = a.byteValue();
						return new RCPXColor.RGBA(rv, gv, bv, av, null);
					} else if (r.isShortValue() && g.isShortValue() && b.isShortValue() && a.isShortValue()) {
						int rv = r.shortValue();
						int gv = g.shortValue();
						int bv = b.shortValue();
						int av = a.shortValue();
						return new RCPXColor.RGBA16(rv, gv, bv, av, null);
					} else {
						float rv = r.floatValue(255, 65535);
						float gv = g.floatValue(255, 65535);
						float bv = b.floatValue(255, 65535);
						float av = a.floatValue(255, 65535);
						return new RCPXColor.RGBAD(rv, gv, bv, av, null);
					}
				}
			}
		},
		HSVA("H","S","V","A") {
			@Override
			public RCPXColor rcpxColor(ColorSpec spec) {
				ChannelValue h = spec.get("H", H0);
				ChannelValue s = spec.get("S", C0);
				ChannelValue v = spec.get("V", C0);
				ChannelValue a = spec.get("A", C100);
				float hv = h.scaledFloatValue(360, 360, 36000);
				float sv = s.scaledFloatValue(100, 100, 10000);
				float vv = v.scaledFloatValue(100, 100, 10000);
				float av = a.scaledFloatValue(100, 100, 10000);
				if (av == 100f) return new RCPXColor.HSV(hv, sv, vv, null);
				else return new RCPXColor.HSVA(hv, sv, vv, av, null);
			}
		},
		CMYK("C","M","Y","K") {
			@Override
			public RCPXColor rcpxColor(ColorSpec spec) {
				ChannelValue c = spec.get("C", C0);
				ChannelValue m = spec.get("M", C0);
				ChannelValue y = spec.get("Y", C0);
				ChannelValue k = spec.get("K", C0);
				float cv = c.scaledFloatValue(100, 100, 10000);
				float mv = m.scaledFloatValue(100, 100, 10000);
				float yv = y.scaledFloatValue(100, 100, 10000);
				float kv = k.scaledFloatValue(100, 100, 10000);
				return new RCPXColor.CMYK(cv, mv, yv, kv, null);
			}
		};
		private final String[] names;
		private ColorSystem(String... names) { this.names = names; }
		public abstract RCPXColor rcpxColor(ColorSpec spec);
		public final boolean canResolve(ColorSpec spec, boolean strict) {
			List<String> specNames = spec.getChannelNames();
			for (String name : names) {
				specNames.remove(name);
				if (!strict) specNames.remove(name.toUpperCase());
				if (!strict) specNames.remove(name.toLowerCase());
			}
			return specNames.isEmpty();
		}
		public static final ColorSystem resolve(ColorSpec spec) {
			for (ColorSystem cs : values()) {
				if (cs.canResolve(spec, true)) {
					return cs;
				}
			}
			for (ColorSystem cs : values()) {
				if (cs.canResolve(spec, false)) {
					return cs;
				}
			}
			return null;
		}
	}
	
	private static enum Format {
		RCPX("RCPX (PowerPaint)", new PaletteWriter.RCPXWriter()),
		ACT("ACT (Photoshop)", new PaletteWriter.ACTWriter()),
		ACO("ACO (Photoshop)", new PaletteWriter.ACOWriter()),
		ASE("ASE (Illustrator)", new PaletteWriter.ASEWriter()),
		ACB("ACB (Adobe Color Book)", new PaletteWriter.ACBWriter()),
		GPL("GPL (GIMP)", new PaletteWriter.GPLWriter()),
		PAL("PAL (PaintShop Pro)", new PaletteWriter.PALWriter()),
		CLUT("CLUT (Mac OS Classic)", new PaletteWriter.CLUTWriter()),
		PLTT("PLTT (Mac OS Classic)", new PaletteWriter.PLTTWriter());
		
		public final String description;
		public final PaletteWriter writer;
		
		private Format(String description, PaletteWriter writer) {
			this.description = description;
			this.writer = writer;
		}
	}
}
