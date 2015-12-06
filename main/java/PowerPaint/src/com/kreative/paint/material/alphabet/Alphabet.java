package com.kreative.paint.material.alphabet;

import java.awt.Font;
import java.util.Arrays;
import java.util.List;

public class Alphabet {
	public static final int DEFAULT_WIDTH = 14;
	public static final Font DEFAULT_FONT = new Font("Helvetica", Font.BOLD, 36);
	public static final Alphabet DEFAULT_ALPHABET = new Alphabet(
		"Latin", DEFAULT_WIDTH, DEFAULT_FONT, new int[] {
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
			'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '!', '?',
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '-', '=', '&',
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
			'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '\u00A1', '\u00BF',
			';', ':', ',', '.', '@', '#', '\'', '"', '*', '/', '(', ')', '[', ']',
			'\u00C0', '\u00C1', '\u00C2', '\u00C3', '\u00C4', '\u00C5', '\u00C6',
			'\u00C7', '\u00C8', '\u00C9', '\u00CA', '\u00CB', '\u00CC', '\u00CD',
			'\u00CE', '\u00CF', '\u00D1', '\u00D2', '\u00D3', '\u00D4', '\u00D5',
			'\u00D6', '\u00D8', '\u00D9', '\u00DA', '\u00DB', '\u00DC', '\u00DD',
			'\u00E0', '\u00E1', '\u00E2', '\u00E3', '\u00E4', '\u00E5', '\u00E6',
			'\u00E7', '\u00E8', '\u00E9', '\u00EA', '\u00EB', '\u00EC', '\u00ED',
			'\u00EE', '\u00EF', '\u00F1', '\u00F2', '\u00F3', '\u00F4', '\u00F5',
			'\u00F6', '\u00F8', '\u00F9', '\u00FA', '\u00FB', '\u00FC', '\u00FD',
			'\u00D0', '\u00F0', '\u00DE', '\u00FE', '\u0152', '\u0153', '\u0178',
			'\u00FF', '\u00DF', '\u00D7', '\u00F7', '%', '^', '_', '$', '\u00A2',
			'\u00A3', '\u00A5', '\u00A7', '\u00B6', '\u00A9', '\u00AE', '{', '}',
			'<', '>', '\u00AB', '\u00BB', '\\', '`', '~', '\u00A8', '|', '\u00A6', '\u00A4',
			'\u00AA', '\u00BA', '\u00B0', '\u00B1', '\u00B5', '\u00AC', '\u00B7'
		}
	);
	
	public final String name;
	public final int width;
	public final Font font;
	public final int[] letters;
	
	public Alphabet(String name, int width, Font font, int[] letters) {
		this.name = name;
		this.width = width;
		this.font = font;
		this.letters = letters;
	}
	
	public Alphabet(String name, int width, Font font, List<Integer> letters) {
		this.name = name;
		this.width = width;
		this.font = font;
		this.letters = new int[letters.size()];
		for (int i = 0, n = letters.size(); i < n; i++) {
			this.letters[i] = letters.get(i);
		}
	}
	
	@Override
	public boolean equals(Object that) {
		if (that instanceof Alphabet) {
			return this.equals((Alphabet)that, false);
		} else {
			return false;
		}
	}
	
	public boolean equals(Alphabet that, boolean withName) {
		if (!Arrays.equals(this.letters, that.letters)) return false;
		if (!withName) return true;
		if (this.name != null) return this.name.equals(that.name);
		if (that.name != null) return that.name.equals(this.name);
		return true;
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(letters);
	}
}
