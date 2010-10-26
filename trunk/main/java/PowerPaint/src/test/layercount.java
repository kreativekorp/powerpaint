package test;

import java.io.*;
import com.kreative.paint.Canvas;
import com.kreative.paint.io.SerializationManager;

public class layercount {
	public static void main(String[] args) {
		for (String arg : args) {
			System.out.print(arg+": ");
			try {
				File f = new File(arg);
				FileInputStream fis = new FileInputStream(f);
				DataInputStream dis = new DataInputStream(fis);
				int m1 = dis.readInt();
				int m2 = dis.readInt();
				int m3 = dis.readInt();
				int m4 = dis.readInt();
				if (m1 != 0x25636B70 || m2 != 0x0D0A1A04 || m3 != 0xFF0A960D || m4 != 0x12EBECCA) {
					dis.close();
					fis.close();
					System.out.println("NOT PP");
				} else {
					SerializationManager.open(dis);
					Canvas c = (Canvas)SerializationManager.readObject(dis);
					SerializationManager.close(dis);
					System.out.println(c.size());
					dis.close();
					fis.close();
				}
			} catch (IOException ioe) {
				System.out.println("IO ERR");
			}
		}
	}
}
