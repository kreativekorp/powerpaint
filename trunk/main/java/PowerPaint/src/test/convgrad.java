package test;

import java.io.*;

public class convgrad {
	public static void main(String[] args) throws IOException {
		for (String arg : args) {
			System.out.println("Converting "+arg+"...");
			RandomAccessFile raf = new RandomAccessFile(arg, "r");
			PrintWriter out = new PrintWriter(new FileOutputStream(arg+".grc"));
			int numgrads = raf.readInt();
			for (int i = 0; i < numgrads; i++) {
				out.println("Gradient "+i);
				int len = raf.readInt();
				byte[] data = new byte[len];
				raf.read(data);
				ByteArrayInputStream gs = new ByteArrayInputStream(data);
				DataInputStream gds = new DataInputStream(gs);
				for (int j = 0; j < 15; j++) {
					gds.readUnsignedShort();
				}
				int pos = 0;
				int numSegs = gds.readShort();
				for (int j = 0; j < numSegs; j++) {
					int sr = gds.readUnsignedShort();
					int sg = gds.readUnsignedShort();
					int sb = gds.readUnsignedShort();
					int er = gds.readUnsignedShort();
					int eg = gds.readUnsignedShort();
					int eb = gds.readUnsignedShort();
					int sl = gds.readUnsignedShort();
					if (j == 0) {
						out.println("@0.0 "+sr+","+sg+","+sb);
					}
					pos += sl;
					out.println("@"+(pos/1000.0)+" "+er+","+eg+","+eb);
				}
			}
			out.close();
			raf.close();
		}
	}
}
