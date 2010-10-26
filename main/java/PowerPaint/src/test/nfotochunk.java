package test;
import java.io.*;
import java.util.Scanner;

public class nfotochunk {
	public static void main(String[] args) {
		for (String arg : args) {
			try {
				File inf = new File(arg);
				File outf = new File((arg.endsWith(".nfo") ? arg.substring(0, arg.length()-4) : arg) + ".chunk");
				
				short o1=0, o2=0, o3=0, o4=0;
				short i1=0, i2=0, i3=0, i4=0;
				short r1=0, r2=0, r3=0, r4=0;
				
				Scanner in = new Scanner(inf);
				while (in.hasNextLine()) {
					String[] things = in.nextLine().split("[ ,]");
					if (things[0].equalsIgnoreCase("OuterRect")) {
						o1 = Short.parseShort(things[1]);
						o2 = Short.parseShort(things[2]);
						o3 = Short.parseShort(things[3]);
						o4 = Short.parseShort(things[4]);
					}
					if (things[0].equalsIgnoreCase("InnerRect")) {
						i1 = Short.parseShort(things[1]);
						i2 = Short.parseShort(things[2]);
						i3 = Short.parseShort(things[3]);
						i4 = Short.parseShort(things[4]);
					}
					if (things[0].equalsIgnoreCase("RoundOff")) {
						r1 = Short.parseShort(things[1]);
						r2 = Short.parseShort(things[2]);
						r3 = Short.parseShort(things[3]);
						r4 = Short.parseShort(things[4]);
					}
				}
				in.close();
				
				DataOutputStream out = new DataOutputStream(new FileOutputStream(outf));
				out.writeBytes("frNF");
				out.writeShort(o1);
				out.writeShort(o2);
				out.writeShort(o3);
				out.writeShort(o4);
				out.writeShort(i1);
				out.writeShort(i2);
				out.writeShort(i3);
				out.writeShort(i4);
				out.writeShort(r1);
				out.writeShort(r2);
				out.writeShort(r3);
				out.writeShort(r4);
				out.close();
			} catch (IOException ioe) {
				System.err.println("Error processing "+arg);
			}
		}
	}
}
