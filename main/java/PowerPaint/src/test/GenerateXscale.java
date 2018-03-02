package test;

public class GenerateXscale {
	public static void main(String[] args) {
		int n = 256;
		int br = 28784;
		int bg = 16962;
		int bb = 5140;
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			if (arg.equalsIgnoreCase("-n") && argi < args.length) {
				n = Integer.parseInt(args[argi++]);
			} else if (arg.equals("-R") && argi < args.length) {
				br = Integer.parseInt(args[argi++]);
			} else if (arg.equals("-r") && argi < args.length) {
				br = Integer.parseInt(args[argi++]) * 257;
			} else if (arg.equals("-G") && argi < args.length) {
				bg = Integer.parseInt(args[argi++]);
			} else if (arg.equals("-g") && argi < args.length) {
				bg = Integer.parseInt(args[argi++]) * 257;
			} else if (arg.equals("-B") && argi < args.length) {
				bb = Integer.parseInt(args[argi++]);
			} else if (arg.equals("-b") && argi < args.length) {
				bb = Integer.parseInt(args[argi++]) * 257;
			}
		}
		int blackRange = 30 * br + 59 * bg + 11 * bb;
		int whiteRange = 6553500 - blackRange;
		for (int i = 0; i < n; i++) {
			double level = (double)(6553500 * i) / (double)(n - 1);
			int r, g, b;
			if (level <= blackRange) {
				r = (int)Math.round(br * level / blackRange);
				g = (int)Math.round(bg * level / blackRange);
				b = (int)Math.round(bb * level / blackRange);
			} else {
				r = br + (int)Math.round((65535 - br) * (level - blackRange) / whiteRange);
				g = bg + (int)Math.round((65535 - bg) * (level - blackRange) / whiteRange);
				b = bb + (int)Math.round((65535 - bb) * (level - blackRange) / whiteRange);
			}
			String rs = "     \"" + r + "\"";
			String gs = "     \"" + g + "\"";
			String bs = "     \"" + b + "\"";
			rs = " r=" + rs.substring(rs.length() - 7);
			gs = " g=" + gs.substring(gs.length() - 7);
			bs = " b=" + bs.substring(bs.length() - 7);
			String s = "\t\t<rgb16" + rs + gs + bs + "/>";
			System.out.println(s);
		}
	}
}
