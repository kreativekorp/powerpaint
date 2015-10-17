package test;

public class GenerateWebSafe {
	public static void main(String[] args) {
		for (int r = 0; r < 256; r += 51) {
			String rs = "   \"" + r + "\""; rs = rs.substring(rs.length() - 5);
			for (int g = 0; g < 256; g += 51) {
				String gs = "   \"" + g + "\""; gs = gs.substring(gs.length() - 5);
				for (int b = 0; b < 256; b += 51) {
					String bs = "   \"" + b + "\""; bs = bs.substring(bs.length() - 5);
					System.out.println("\t\t<rgb r=" + rs + " g=" + gs + " b=" + bs + "/>");
				}
			}
		}
	}
}
