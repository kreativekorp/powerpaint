package com.kreative.paint.material.colorpalette;

import java.util.List;

public class PaletteDimensions {
	public static PaletteDimensions forColorCount(int n) {
		/* Early Installment Weirdness */
		if (n <=          1) return new PaletteDimensions(n,     1,     1, false,     1,     1, false,     1,     1, true );
		if (n <=          2) return new PaletteDimensions(n,     2,     1, false,     2,     1, false,     1,     2, true );
		if (n <=          4) return new PaletteDimensions(n,     4,     1, false,     2,     2, false,     1,     4, true );
		if (n <=          8) return new PaletteDimensions(n,     4,     2, false,     2,     4, false,     2,     4, true );
		if (n <=         16) return new PaletteDimensions(n,     8,     2, false,     4,     4, false,     2,     8, true );
		if (n <=         32) return new PaletteDimensions(n,     8,     4, false,     4,     8, false,     4,     8, true );
		/* Growing the Beard */
		if (n <=         64) return new PaletteDimensions(n,    16,     4, false,     8,     8, false,     4,    16, false);
		if (n <=        128) return new PaletteDimensions(n,    16,     8, false,    16,     8, false,     8,    16, false);
		if (n <=        256) return new PaletteDimensions(n,    32,     8, false,    16,    16, false,     8,    32, false);
		if (n <=        512) return new PaletteDimensions(n,    32,    16, false,    32,    16, false,    16,    32, false);
		if (n <=       1024) return new PaletteDimensions(n,    64,    16, false,    32,    32, false,    16,    64, false);
		if (n <=       2048) return new PaletteDimensions(n,    64,    32, false,    64,    32, false,    32,    64, false);
		if (n <=       4096) return new PaletteDimensions(n,   128,    32, false,    64,    64, false,    32,   128, false);
		if (n <=       8192) return new PaletteDimensions(n,   128,    64, false,   128,    64, false,    64,   128, false);
		if (n <=      16384) return new PaletteDimensions(n,   256,    64, false,   128,   128, false,    64,   256, false);
		if (n <=      32768) return new PaletteDimensions(n,   256,   128, false,   256,   128, false,   128,   256, false);
		if (n <=      65536) return new PaletteDimensions(n,   512,   128, false,   256,   256, false,   128,   512, false);
		if (n <=     131072) return new PaletteDimensions(n,   512,   256, false,   512,   256, false,   256,   512, false);
		if (n <=     262144) return new PaletteDimensions(n,  1024,   256, false,   512,   512, false,   256,  1024, false);
		if (n <=     524288) return new PaletteDimensions(n,  1024,   512, false,  1024,   512, false,   512,  1024, false);
		if (n <=    1048576) return new PaletteDimensions(n,  2048,   512, false,  1024,  1024, false,   512,  2048, false);
		if (n <=    2097152) return new PaletteDimensions(n,  2048,  1024, false,  2048,  1024, false,  1024,  2048, false);
		if (n <=    4194304) return new PaletteDimensions(n,  4096,  1024, false,  2048,  2048, false,  1024,  4096, false);
		if (n <=    8388608) return new PaletteDimensions(n,  4096,  2048, false,  4096,  2048, false,  2048,  4096, false);
		if (n <=   16777216) return new PaletteDimensions(n,  8192,  2048, false,  4096,  4096, false,  2048,  8192, false);
		if (n <=   33554432) return new PaletteDimensions(n,  8192,  4096, false,  8192,  4096, false,  4096,  8192, false);
		if (n <=   67108864) return new PaletteDimensions(n, 16384,  4096, false,  8192,  8192, false,  4096, 16384, false);
		if (n <=  134217728) return new PaletteDimensions(n, 16384,  8192, false, 16384,  8192, false,  8192, 16384, false);
		if (n <=  268435456) return new PaletteDimensions(n, 32768,  8192, false, 16384, 16384, false,  8192, 32768, false);
		if (n <=  536870912) return new PaletteDimensions(n, 32768, 16384, false, 32768, 16384, false, 16384, 32768, false);
		if (n <= 1073741824) return new PaletteDimensions(n, 65536, 16384, false, 32768, 32768, false, 16384, 65536, false);
		/* n ² 2147483648 */ return new PaletteDimensions(n, 65536, 32768, false, 65536, 32768, false, 32768, 65536, false);
	}
	
	public final int colorCount;
	public final int horizWidth;
	public final int horizHeight;
	public final boolean horizColumns;
	public final int squareWidth;
	public final int squareHeight;
	public final boolean squareColumns;
	public final int vertWidth;
	public final int vertHeight;
	public final boolean vertColumns;
	
	public PaletteDimensions(
		int colorCount,
		int horizWidth,
		int horizHeight,
		boolean horizColumns,
		int squareWidth,
		int squareHeight,
		boolean squareColumns,
		int vertWidth,
		int vertHeight,
		boolean vertColumns
	) {
		this.colorCount = colorCount;
		this.horizWidth = horizWidth;
		this.horizHeight = horizHeight;
		this.horizColumns = horizColumns;
		this.squareWidth = squareWidth;
		this.squareHeight = squareHeight;
		this.squareColumns = squareColumns;
		this.vertWidth = vertWidth;
		this.vertHeight = vertHeight;
		this.vertColumns = vertColumns;
	}
	
	public RCPXPalette createPalette(
		String name, RCPXOrientation orientation,
		List<RCPXColor> colors, boolean colorsOrdered
	) {
		int hcw = Math.max(288 / horizWidth  , 8);
		int hch = Math.max( 72 / horizHeight , 8);
		int scw = Math.max(144 / squareWidth , 8);
		int sch = Math.max(144 / squareHeight, 8);
		int vcw = Math.max( 72 / vertWidth   , 8);
		int vch = Math.max(288 / vertHeight  , 8);
		int hpw =  horizWidth * hcw + 1, hph =  horizHeight * hch + 1;
		int spw = squareWidth * scw + 1, sph = squareHeight * sch + 1;
		int vpw =   vertWidth * vcw + 1, vph =   vertHeight * vch + 1;
		return new RCPXPalette(
			name, orientation, hpw, hph, spw, sph, vpw, vph,
			colors, colorsOrdered, createLayout()
		);
	}
	
	public RCPXLayout createLayout() {
		RCPXLayout h = createLayout(colorCount,  horizWidth,  horizHeight,  horizColumns);
		RCPXLayout s = createLayout(colorCount, squareWidth, squareHeight, squareColumns);
		RCPXLayout v = createLayout(colorCount,   vertWidth,   vertHeight,   vertColumns);
		return new RCPXLayout.Oriented(h, s, v);
	}
	
	private static RCPXLayout createLayout(int n, int w, int h, boolean d) {
		RCPXLayout.RowOrColumn ol = d ? new RCPXLayout.Row() : new RCPXLayout.Column();
		for (int on = (d ? w : h), in = (d ? h : w), o = 0, i = 0; o < on; o++, i += in) {
			RCPXLayout.RowOrColumn il = d ? new RCPXLayout.Column() : new RCPXLayout.Row();
			if (i >= n) {
				il.add(new RCPXSwatch.Empty(in));
			} else if (i + in > n) {
				il.add(new RCPXSwatch.Range(
					i, n,
					RCPXBorder.ALL, RCPXBorder.ALL,
					RCPXBorder.ALL, RCPXBorder.ALL
				));
				il.add(new RCPXSwatch.Empty(i + in - n));
			} else {
				il.add(new RCPXSwatch.Range(
					i, i + in,
					RCPXBorder.ALL, RCPXBorder.ALL,
					RCPXBorder.ALL, RCPXBorder.ALL
				));
			}
			ol.add(il);
		}
		return ol;
	}
}
