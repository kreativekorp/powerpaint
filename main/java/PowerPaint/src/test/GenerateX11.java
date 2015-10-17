package test;

import java.io.IOException;
import java.net.URL;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenerateX11 {
	private static final String RGB_TXT_URL = "http://cgit.freedesktop.org/xorg/app/rgb/plain/rgb.txt";
	private static final Pattern RGB_TXT_LINE = Pattern.compile("^\\s*([0-9]+)\\s*([0-9]+)\\s*([0-9]+)\\s*([A-Za-z0-9 ]+)\\s*$");
	
	public static void main(String[] args) throws IOException {
		LinkedHashMap<String,int[]> complete = new LinkedHashMap<String,int[]>();
		LinkedHashMap<String,int[]> normalized = new LinkedHashMap<String,int[]>();
		LinkedHashMap<String,int[]> simple = new LinkedHashMap<String,int[]>();
		
		Scanner in = new Scanner(new URL(RGB_TXT_URL).openStream());
		while (in.hasNextLine()) {
			Matcher m = RGB_TXT_LINE.matcher(in.nextLine());
			if (m.matches()) {
				int r = Integer.parseInt(m.group(1));
				int g = Integer.parseInt(m.group(2));
				int b = Integer.parseInt(m.group(3));
				int[] rgb = new int[]{r,g,b};
				String name = capitalize(m.group(4).trim());
				name = name.replaceAll("([a-z])([A-Z])", "$1 $2");
				name = name.replaceAll("([a-z])([0-9])", "$1 $2");
				name = name.replaceAll("([0-9])([A-Z])", "$1 $2");
				complete.put(name, rgb);
				name = name.replaceAll("Grey", "Gray");
				name = name.replaceAll("X11 ", "");
				normalized.put(name, rgb);
				char last = name.charAt(name.length() - 1);
				if (Character.isDigit(last)) continue;
				simple.put(name, rgb);
			}
		}
		in.close();

		System.out.println("========COMPLETE========");
		printColorList(complete);
		System.out.println("=========NORMAL=========");
		printColorList(normalized);
		System.out.println("=========SIMPLE=========");
		printColorList(simple);
		System.out.println("==========DONE==========");
	}
	
	private static void printColorList(LinkedHashMap<String,int[]> colors) {
		for (Map.Entry<String,int[]> e : colors.entrySet()) {
			String name = "\"" + e.getKey() + "\"";
			while (name.length() < 24) name += " ";
			int[] rgb = e.getValue();
			String r = "   \"" + rgb[0] + "\""; r = r.substring(r.length() - 5);
			String g = "   \"" + rgb[1] + "\""; g = g.substring(g.length() - 5);
			String b = "   \"" + rgb[2] + "\""; b = b.substring(b.length() - 5);
			String line = "\t\t<rgb r=" + r + " g=" + g + " b=" + b + " name=" + name + "/>";
			System.out.println(line);
		}
	}
	
	private static String capitalize(String oldstr) {
		StringBuffer newstr = new StringBuffer(oldstr.length());
		CharacterIterator ci = new StringCharacterIterator(oldstr);
		for (char pch = ' ', ch = ci.first(); ch != CharacterIterator.DONE; pch = ch, ch = ci.next()) {
			if (!Character.isLetter(pch)) newstr.append(Character.toTitleCase(ch));
			else newstr.append(ch);
		}
		return newstr.toString();
	}
}
