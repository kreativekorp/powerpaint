package test;

import java.io.*;
import java.text.*;
import java.util.*;

public class x11torcp {
	public static void main(String[] args) throws IOException {
		for (String arg : args) {
			File f = new File(arg);
			Vector<Long> rgbs = new Vector<Long>();
			Vector<String> names = new Vector<String>();
			Scanner sc = new Scanner(f);
			while (sc.hasNextLine()) {
				String line = sc.nextLine().trim();
				if (line.length() > 0 && !line.startsWith("#")) {
					String[] fields = line.split("\\s+",4);
					if (fields.length >= 4) {
						int r = Integer.parseInt(fields[0])*257;
						int g = Integer.parseInt(fields[1])*257;
						int b = Integer.parseInt(fields[2])*257;
						long rgb = 0x0010000000000000l | ((long)r << 32l) | ((long)g << 16l) | (long)b;
						String name = tcase(
							fields[3]
							.replaceAll("[A-Z]", " $0")
							.replaceAll("[0-9]+", " $0")
							.toLowerCase()
							.replaceAll("(^|\\s)grey(\\s|$)", "$1gray$2")
							.replaceAll("\\s+"," ")
							.trim()
						);
						if (rgbs.isEmpty() || names.isEmpty() || !((rgbs.get(rgbs.size()-1) == rgb) && (names.get(names.size()-1).equals(name)))) {
							rgbs.add(rgb);
							names.add(name);
						}
					}
				}
			}
			sc.close();
			int w = (int)Math.floor(Math.sqrt(rgbs.size())*2);
			int h = (int)Math.ceil(Math.sqrt(rgbs.size())/2);
			int sw = (int)Math.ceil(Math.sqrt(rgbs.size()));
			int sh = (int)Math.floor(Math.sqrt(rgbs.size()));
			System.out.println(w+"x"+h+"="+(w*h)+", "+sw+"x"+sh+"="+(sw*sh));
			DataOutputStream out = new DataOutputStream(new FileOutputStream("X11 Colors.rcp"));
			out.writeLong(0xFE10000000000000L | ((long)(w*8+1) << 32L) | ((long)(h*8+1) << 16L));
			out.writeLong(0xFE20000000000000L | ((long)(w*8+1) << 32L) | ((long)(h*8+1) << 16L));
			out.writeLong(0xFE30000000000000L | ((long)(sw*8+1) << 32L) | ((long)(sh*8+1) << 16L));
			out.writeLong(0xFE40000000000000L | ((long)(h*8+1) << 32L) | ((long)(w*8+1) << 16L));
			out.writeLong(0xFE0000000000000AL);
			out.writeLong(0x58313120436F6C6FL);
			out.writeLong(0x7273000000000000L);
			out.writeLong(0x8050000000000000L);
			out.writeLong(0x8030000000000000L | ((long)w << 32L) | ((long)h << 16L));
			for (int i = 0; i < rgbs.size() && i < names.size(); i++) {
				byte[] b = names.get(i).getBytes("UTF-8");
				out.writeLong(0xFE00000000000000L | (long)b.length);
				out.write(b);
				if ((b.length % 8) != 0) {
					out.write(new byte[8-(b.length % 8)]);
				}
				out.writeLong(rgbs.get(i));
			}
			for (int i = Math.min(rgbs.size(), names.size()); i < w*h; i++) {
				out.writeLong(0);
			}
			out.writeLong(0x8030000000000000L | ((long)sw << 32L) | ((long)sh << 16L));
			for (int i = 0; i < rgbs.size() && i < names.size(); i++) {
				byte[] b = names.get(i).getBytes("UTF-8");
				out.writeLong(0xFE00000000000000L | (long)b.length);
				out.write(b);
				if ((b.length % 8) != 0) {
					out.write(new byte[8-(b.length % 8)]);
				}
				out.writeLong(rgbs.get(i));
			}
			for (int i = Math.min(rgbs.size(), names.size()); i < sw*sh; i++) {
				out.writeLong(0);
			}
			out.writeLong(0x8030000000000000L | ((long)h << 32L) | ((long)w << 16L));
			for (int i = 0; i < rgbs.size() && i < names.size(); i++) {
				byte[] b = names.get(i).getBytes("UTF-8");
				out.writeLong(0xFE00000000000000L | (long)b.length);
				out.write(b);
				if ((b.length % 8) != 0) {
					out.write(new byte[8-(b.length % 8)]);
				}
				out.writeLong(rgbs.get(i));
			}
			for (int i = Math.min(rgbs.size(), names.size()); i < w*h; i++) {
				out.writeLong(0);
			}
			out.close();
		}
	}
	
	private static String tcase(String oldstr) {
		StringBuffer newstr = new StringBuffer(oldstr.length());
		CharacterIterator ci = new StringCharacterIterator(oldstr);
		for (char pch = ' ', ch = ci.first(); ch != CharacterIterator.DONE; pch = ch, ch = ci.next()) {
			if (!Character.isLetter(pch)) newstr.append(Character.toTitleCase(ch));
			else newstr.append(Character.toLowerCase(ch));
		}
		return newstr.toString();
	}
}
