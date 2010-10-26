package test;

import java.io.*;
import java.util.*;
import com.kreative.paint.rcp.*;

public class RCPAssemblyTest {
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
				ByteArrayOutputStream bout1 = new ByteArrayOutputStream();
				OutputStreamWriter swrit1 = new OutputStreamWriter(bout1, "UTF-8");
				PrintWriter pwrit1 = new PrintWriter(swrit1);
				RCPDisassembler.disassemble(din1, pwrit1);
				pwrit1.flush();
				swrit1.flush();
				bout1.flush();
				pwrit1.close();
				swrit1.close();
				bout1.close();
				din1.close();
				bin1.close();
				byte[] txtdata = bout1.toByteArray();
				
				ByteArrayInputStream bin2 = new ByteArrayInputStream(txtdata);
				Scanner scan2 = new Scanner(bin2, "UTF-8");
				ByteArrayOutputStream bout2 = new ByteArrayOutputStream();
				DataOutputStream dout2 = new DataOutputStream(bout2);
				RCPAssembler.assemble(scan2, dout2);
				dout2.flush();
				bout2.flush();
				dout2.close();
				bout2.close();
				scan2.close();
				bin2.close();
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
