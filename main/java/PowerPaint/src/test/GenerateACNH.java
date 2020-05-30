package test;

public class GenerateACNH {
	public static void main(String[] args) {
		for (String arg : args) {
			System.out.println(codeToTag(arg));
		}
	}
	
	public static String codeToTag(String code) {
		char[] cc = code.toCharArray();
		int ach = (cc.length > 0) ? Character.digit(cc[0], 30) : 0;
		int acs = (cc.length > 1) ? Character.digit(cc[1], 15) : 0;
		int acv = (cc.length > 2) ? Character.digit(cc[2], 15) : 0;
		float h = 360f * ach / 30f;
		float s = 100f * acs / 15f;
		float v = 100f * (acv * 3 + 4) / 51f;
		String hs = "h=\"" + h + "\""; while (hs.length() <  9) hs += " ";
		String ss = "s=\"" + s + "\""; while (ss.length() < 13) ss += " ";
		String vs = "v=\"" + v + "\""; while (vs.length() < 13) vs += " ";
		return "\t\t<hsv " + hs + " " + ss + " " + vs + "/>";
	}
}
