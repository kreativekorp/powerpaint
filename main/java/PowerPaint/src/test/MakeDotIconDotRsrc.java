package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MakeDotIconDotRsrc {
	public static void main(String[] args) {
		for (String arg : args) {
			process(new File(arg));
		}
	}
	
	private static void process(File f) {
		if (f.isDirectory()) {
			for (File cf : f.listFiles()) {
				process(cf);
			}
		} else if (f.getName().equals("Icon\r")) {
			File fh = new File(f, "..namedfork");
			File fr = new File(fh, "rsrc");
			File fp = f.getParentFile();
			File fs = new File(fp, ".icon.rsrc");
			System.out.print(fs.getAbsolutePath()+"...");
			try {
				FileInputStream in = new FileInputStream(fr);
				FileOutputStream out = new FileOutputStream(fs);
				byte[] buf = new byte[1048576];
				int len = 0;
				while ((len = in.read(buf)) >= 0) {
					out.write(buf, 0, len);
				}
				out.flush();
				out.close();
				in.close();
				System.out.println(" done");
			} catch (IOException ioe) {
				System.out.println(" FAILED");
			}
		}
	}
}
