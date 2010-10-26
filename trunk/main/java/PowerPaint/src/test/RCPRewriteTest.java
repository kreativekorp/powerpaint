package test;

import java.io.*;
import java.util.*;

import com.kreative.paint.rcp.*;

public class RCPRewriteTest {
	public static void main(String[] args) {
		int pass = 0;
		int fail = 0;
		int err = 0;
		for (String arg : args) {
			File f = new File(arg);
			System.out.print(f.getName()+"... ");
			try {
				RandomAccessFile raf = new RandomAccessFile(f, "r");
				byte[] indata = new byte[(int)raf.length()];
				raf.readFully(indata);
				raf.close();
				
				ByteArrayInputStream bin1 = new ByteArrayInputStream(indata);
				DataInputStream din1 = new DataInputStream(bin1);
				RCPSwatch sw = new RCPSwatch(din1);
				din1.close();
				din1.close();
				bin1.close();
				
				ByteArrayOutputStream bout2 = new ByteArrayOutputStream();
				DataOutputStream dout2 = new DataOutputStream(bout2);
				sw.write(dout2);
				dout2.flush();
				bout2.flush();
				dout2.close();
				bout2.close();
				byte[] outdata = bout2.toByteArray();
				
				if (Arrays.equals(indata, outdata)) {
					System.out.println("PASSED");
					pass++;
				} else {
					System.out.println("FAILED");
					fail++;
				}
			} catch (IOException e) {
				System.out.println("ERROR ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
				err++;
			}
		}
		System.out.println("PASSED: "+pass+"    FAILED: "+fail+"    ERRORS: "+err);
	}
}
