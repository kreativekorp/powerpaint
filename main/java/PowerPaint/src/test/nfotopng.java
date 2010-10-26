package test;
import java.io.*;
import java.util.Scanner;

public class nfotopng {
	public static void main(String[] args) {
		for (String arg : args) {
			try {
				System.out.print("Processing "+arg+"...");
				File inf = new File(arg);
				File pngf = new File((arg.endsWith(".nfo") ? arg.substring(0, arg.length()-4) : arg) + ".png");
				File outf = new File((arg.endsWith(".nfo") ? arg.substring(0, arg.length()-4) : arg) + ".2.png");
				
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
				
				DataInputStream png = new DataInputStream(new FileInputStream(pngf));
				DataOutputStream out = new DataOutputStream(new FileOutputStream(outf));
				
				long magic = png.readLong();
				out.writeLong(magic);
				while (true) {
					try {
						int cl = png.readInt();
						int ct = png.readInt();
						byte[] cd = new byte[cl]; png.read(cd);
						int cc = png.readInt();
						if (ct == 0x49454E44) {
							System.out.print(" frNF");
							ByteArrayOutputStream co = new ByteArrayOutputStream();
							DataOutputStream cod = new DataOutputStream(co);
							cod.writeInt(0x66724E46);
							cod.writeShort(o1);
							cod.writeShort(o2);
							cod.writeShort(o3);
							cod.writeShort(o4);
							cod.writeShort(i1);
							cod.writeShort(i2);
							cod.writeShort(i3);
							cod.writeShort(i4);
							cod.writeShort(r1);
							cod.writeShort(r2);
							cod.writeShort(r3);
							cod.writeShort(r4);
							cod.close();
							co.close();
							byte[] chunk = co.toByteArray();
							out.writeInt(chunk.length-4);
							out.write(chunk);
							out.writeInt(crc(chunk));
						}
						System.out.print(new String(new char[]{' ', (char)((ct >>> 24) & 0xFF), (char)((ct >>> 16) & 0xFF), (char)((ct >>> 8) & 0xFF), (char)((ct >>> 0) & 0xFF)}));
						out.writeInt(cl);
						out.writeInt(ct);
						out.write(cd);
						out.writeInt(cc);
					} catch (EOFException oef) {
						break;
					}
				}
				
				png.close();
				out.close();
				
				System.out.println();
			} catch (IOException ioe) {
				System.out.println();
				System.err.println("Error processing "+arg);
				ioe.printStackTrace();
			}
		}
	}
	
	private static int[] crc_table = null;
	
	private static void make_crc_table() {
		int c, n, k;
		crc_table = new int[256];
		for (n = 0; n < 256; n++) {
			c = n;
			for (k = 0; k < 8; k++) {
				if ((c & 1) != 0) {
					c = 0xEDB88320 ^ (c >>> 1);
				} else {
					c = c >>> 1;
				}
			}
			crc_table[n] = c;
		}
	}
	
	private static int update_crc(int crc, byte[] buf) {
		int c = crc;
		int n;
		if (crc_table == null) make_crc_table();
		for (n = 0; n < buf.length; n++) {
			c = crc_table[(c ^ buf[n]) & 0xFF] ^ (c >>> 8);
		}
		return c;
	}
	
	private static int crc(byte[] buf) {
		return update_crc(-1, buf) ^ -1;
	}
}
