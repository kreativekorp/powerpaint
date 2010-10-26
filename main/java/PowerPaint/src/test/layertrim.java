package test;

import java.io.*;
import com.kreative.paint.Canvas;
import com.kreative.paint.io.SerializationManager;

public class layertrim {
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
					dis.close();
					fis.close();
					System.out.print(c.size()+" -> ");
					for (int i = c.size()-1; i >= 0; i--) {
						if (c.get(i).size() == 0 && c.get(i).getTiles().size() == 0) c.remove(i);
					}
					System.out.println(c.size());
					FileOutputStream fos = new FileOutputStream(f);
					DataOutputStream dos = new DataOutputStream(fos);
					dos.writeInt(0x25636B70);
					dos.writeInt(0x0D0A1A04);
					dos.writeInt(0xFF0A960D);
					dos.writeInt(0x12EBECCA);
					SerializationManager.open(dos);
					SerializationManager.writeObject(c, dos);
					SerializationManager.close(dos);
					dos.close();
					fos.close();
				}
			} catch (IOException ioe) {
				System.out.println("IO ERR");
			}
		}
	}
}
