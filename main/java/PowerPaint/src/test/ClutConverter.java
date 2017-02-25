package test;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ClutConverter {
	public static void main(String[] args) throws IOException {
		boolean simplify = true;
		for (String arg : args) {
			if (arg.equals("--rgb8")) { simplify = true; continue; }
			if (arg.equals("--rgb16")) { simplify = false; continue; }
			File inFile = new File(arg);
			DataInputStream in = new DataInputStream(new FileInputStream(inFile));
			in.readUnsignedShort();
			in.readUnsignedShort();
			in.readUnsignedShort();
			int count = in.readUnsignedShort();
			for (int i = 0; i <= count; i++) {
				in.readUnsignedShort();
				int r = in.readUnsignedShort();
				int g = in.readUnsignedShort();
				int b = in.readUnsignedShort();
				if (simplify && r % 257 == 0 && g % 257 == 0 && b % 257 == 0) {
					String rs = "   \"" + (r / 257) + "\"";
					String gs = "   \"" + (g / 257) + "\"";
					String bs = "   \"" + (b / 257) + "\"";
					rs = " r=" + rs.substring(rs.length() - 5);
					gs = " g=" + gs.substring(gs.length() - 5);
					bs = " b=" + bs.substring(bs.length() - 5);
					String s = "\t\t<rgb" + rs + gs + bs + "/>";
					System.out.println(s);
				} else {
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
			in.close();
		}
	}
}
