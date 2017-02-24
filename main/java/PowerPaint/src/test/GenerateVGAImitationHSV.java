package test;

public class GenerateVGAImitationHSV {
	public static void main(String[] args) {
		for (int v = 3; v > 0; v--) {
			for (int s = 3; s > 0; s--) {
				for (int h = 16; h < 40; h++) {
					float[] a = toRGB(h / 4f, s / 3f, v / 3f);
					String r = "     \"" + (int)Math.ceil(a[0] * 65535.0f) + "\""; r = r.substring(r.length() - 7);
					String g = "     \"" + (int)Math.ceil(a[1] * 65535.0f) + "\""; g = g.substring(g.length() - 7);
					String b = "     \"" + (int)Math.ceil(a[2] * 65535.0f) + "\""; b = b.substring(b.length() - 7);
					System.out.println("\t\t<rgb16 r=" + r + " g=" + g + " b=" + b + "/>");
				}
			}
		}
	}
	
	private static float[] toRGB(float h, float s, float v) {
		float[] rgb = new float[3];
		if (s == 0f) {
			rgb[0] = rgb[1] = rgb[2] = v;
		} else {
			int i = (int)Math.floor(h);
			float f = h - i;
			float p = v * (1f - s);
			float q = v * (1f - s * f);
			float t = v * (1f - s * (1f - f));
			switch (i % 6) {
				case 0: rgb[0] = v; rgb[1] = t; rgb[2] = p; break;
				case 1: rgb[0] = q; rgb[1] = v; rgb[2] = p; break;
				case 2: rgb[0] = p; rgb[1] = v; rgb[2] = t; break;
				case 3: rgb[0] = p; rgb[1] = q; rgb[2] = v; break;
				case 4: rgb[0] = t; rgb[1] = p; rgb[2] = v; break;
				case 5: rgb[0] = v; rgb[1] = p; rgb[2] = q; break;
			}
		}
		return rgb;
	}
}
