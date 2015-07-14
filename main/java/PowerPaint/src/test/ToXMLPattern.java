package test;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ToXMLPattern {
	public static void main(String[] args) throws IOException {
		for (String arg : args) {
			File file = new File(arg);
			InputStream in = new FileInputStream(file);
			DataInputStream d = new DataInputStream(in);
			int n = d.readUnsignedShort();
			long[] pats = new long[n];
			for (int i = 0; i < n; i++) {
				pats[i] = d.readLong();
			}
			d.close();
			System.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			System.out.println("<!DOCTYPE patterns PUBLIC \"-//Kreative//DTD PowerPattern 1.0//EN\" \"patx.dtd\">");
			String name = file.getName()
					.replaceAll("^#[0-9]+\\s+", "")
					.replaceAll("\\.pat$", "");
			System.out.println("<patterns name=\"" + xmls(name) + "\">");
			for (long pat : pats) {
				System.out.println("\t<pattern w=\"8\" h=\"8\" d=\"1\">");
				for (int y = 0; y < 8; y++) {
					for (int x = 0; x < 8; x++) {
						System.out.print(x == 0 ? "\t\t" : " ");
						System.out.print(pat < 0 ? "#" : ".");
						pat <<= 1;
					}
					System.out.println();
				}
				System.out.println("\t</pattern>");
			}
			System.out.println("</patterns>");
		}
	}
	
	private static String xmls(String s) {
		return s.replaceAll("&", "&amp;")
		        .replaceAll("<", "&lt;")
		        .replaceAll(">", "&gt;")
		        .replaceAll("\"", "&quot;");
	}
}
