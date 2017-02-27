package test;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ActConverter {
	public static void main(String[] args) throws IOException {
		for (String arg : args) {
			File inFile = new File(arg);
			DataInputStream in = new DataInputStream(new FileInputStream(inFile));
			while (in.available() > 0) {
				int r = in.readUnsignedByte();
				int g = in.readUnsignedByte();
				int b = in.readUnsignedByte();
				String rs = "   \"" + r + "\"";
				String gs = "   \"" + g + "\"";
				String bs = "   \"" + b + "\"";
				rs = " r=" + rs.substring(rs.length() - 5);
				gs = " g=" + gs.substring(gs.length() - 5);
				bs = " b=" + bs.substring(bs.length() - 5);
				String s = "\t\t<rgb" + rs + gs + bs + "/>";
				System.out.println(s);
			}
			in.close();
		}
	}
}
