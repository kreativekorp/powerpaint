package test;

import javax.imageio.ImageIO;

public class KnownFormats {
	public static void main(String[] args) {
		System.out.println("Readers:");
		for (String s : ImageIO.getReaderFormatNames()) {
			System.out.println("\t"+s);
		}
		System.out.println("Writers:");
		for (String s : ImageIO.getWriterFormatNames()) {
			System.out.println("\t"+s);
		}
	}
}
