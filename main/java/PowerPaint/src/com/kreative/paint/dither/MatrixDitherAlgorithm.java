package com.kreative.paint.dither;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class MatrixDitherAlgorithm extends DitherAlgorithm {
	public final int rows;
	public final int columns;
	public final int denominator;
	public final int[][] values;
	
	protected MatrixDitherAlgorithm(int rows, int columns, int denominator, String name) {
		super(name);
		this.rows = (rows < 1) ? 1 : rows;
		this.columns = (columns < 1) ? 1 : columns;
		this.denominator = (denominator < 1) ? 1 : denominator;
		this.values = new int[this.rows][this.columns];
	}
	
	private static final Pattern VALUE_PATTERN = Pattern.compile("[0-9]+");
	
	protected MatrixDitherAlgorithm(int rows, int columns, int denominator, String text, String name) {
		this(rows, columns, denominator, name);
		Matcher m = VALUE_PATTERN.matcher(text);
		for (int y = 0; y < this.rows; y++) {
			for (int x = 0; x < this.columns; x++) {
				if (m.find()) {
					try {
						int value = Integer.parseInt(m.group());
						this.values[y][x] = value;
					} catch (NumberFormatException e) {
						this.values[y][x] = 0;
					}
				} else {
					this.values[y][x] = 0;
				}
			}
		}
	}
}
