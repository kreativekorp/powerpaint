package test;

import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import com.kreative.paint.pict.*;

public class rgntopng {
	public static void main(String[] args) throws IOException {
		for (String arg : args) {
			File f = new File(arg);
			FileInputStream in = new FileInputStream(f);
			Region r = Region.read(new DataInputStream(in));
			in.close();
			BufferedImage img = r.toBufferedImage();
			ImageIO.write(img, "png", new File(f.getParentFile(), f.getName()+".png"));
		}
	}
}
