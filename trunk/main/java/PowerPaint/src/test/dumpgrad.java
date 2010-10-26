package test;

import java.io.*;

public class dumpgrad {
	public static void main(String[] args) throws IOException {
		for (String arg : args) {
			System.out.println("***** "+arg+":");
			RandomAccessFile raf = new RandomAccessFile(arg, "r");
			int numgrads = raf.readInt();
			for (int i = 0; i < numgrads; i++) {
				System.out.println("*** Gradient #"+i+":");
				int len = raf.readInt();
				byte[] data = new byte[len];
				raf.read(data);
				for (int r = 0; r < data.length; r += 16) {
					for (int g = 0; g < 16 && r+g < data.length; g += 4) {
						for (int b = 0; b < 4 && r+g+b < data.length; b++) {
							String h = "00"+Integer.toHexString(data[r+g+b]).toUpperCase();
							h = h.substring(h.length()-2);
							System.out.print(h+" ");
						}
						System.out.print(" ");
					}
					System.out.println();
				}
				ByteArrayInputStream gs = new ByteArrayInputStream(data);
				DataInputStream gds = new DataInputStream(gs);
				for (int j = 0; j < 15; j++) {
					int whatever = gds.readUnsignedShort();
					System.out.println("????: "+whatever);
				}
				int numSegs = gds.readShort();
				System.out.println("NumSegments: "+numSegs);
				for (int j = 0; j < numSegs; j++) {
					System.out.println("* Segment #"+j+":");
					int sr = gds.readUnsignedShort();
					int sg = gds.readUnsignedShort();
					int sb = gds.readUnsignedShort();
					System.out.println("Starting Color: "+sr+","+sg+","+sb);
					int er = gds.readUnsignedShort();
					int eg = gds.readUnsignedShort();
					int eb = gds.readUnsignedShort();
					System.out.println("Ending Color: "+er+","+eg+","+eb);
					int sl = gds.readUnsignedShort();
					System.out.println("Length: "+sl);
				}
			}
			raf.close();
		}
	}
}
