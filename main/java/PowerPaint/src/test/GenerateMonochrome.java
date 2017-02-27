package test;

public class GenerateMonochrome {
	public static void main(String[] args) {
		int n = 256;
		int br = 0xFFFF;
		int bg = 0xFFFF;
		int bb = 0xFFFF;
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
		for (int i = 0; i < n; i++) {
			int r = br * i / (n - 1);
			int g = bg * i / (n - 1);
			int b = bb * i / (n - 1);
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
