package com.kreative.paint.material.colorpalette;

import java.util.List;

public class PaletteDimensions {
	public static PaletteDimensions forColorCount(int n) {
		/* Early Installment Weirdness */
		if (n <= 1) return new PaletteDimensions(n, 1, 1, false, 1, 1, false, 1, 1, true);
		if (n <= 2) return new PaletteDimensions(n, 2, 1, false, 2, 1, false, 1, 2, true);
		if (n <= 3) return new PaletteDimensions(n, 3, 1, false, 3, 1, false, 1, 3, true);
		if (n <= 4) return new PaletteDimensions(n, 4, 1, false, 2, 2, false, 1, 4, true);
		/* Growing the Beard */
		int minhw = (int)Math.round(Math.sqrt(n * 2.0));
		int opthw = (int)Math.round(Math.sqrt(n * 4.0));
		int maxhw = (int)Math.round(Math.sqrt(n * 8.0));
		int opthh = (n + opthw - 1) / opthw;
		int opthe = opthw * opthh - n;
		for (int hw = minhw; hw <= maxhw; hw++) {
			int hh = (n + hw - 1) / hw;
			int he = hw * hh - n;
			if (he < opthe) { opthw = hw; opthh = hh; opthe = he; }
		}
		int minsw = (int)Math.round(Math.sqrt(n / 2.0));
		int optsw = (int)Math.round(Math.sqrt(n      ));
		int maxsw = (int)Math.round(Math.sqrt(n * 2.0));
		int optsh = (n + optsw - 1) / optsw;
		int optse = optsw * optsh - n;
		if (n > 64) {
			for (int sw = maxsw; sw >= minsw; sw--) {
				int sh = (n + sw - 1) / sw;
				int se = sw * sh - n;
				if (se < optse) { optsw = sw; optsh = sh; optse = se; }
			}
		} else {
			for (int sw = minsw; sw <= maxsw; sw++) {
				int sh = (n + sw - 1) / sw;
				int se = sw * sh - n;
				if (se < optse) { optsw = sw; optsh = sh; optse = se; }
			}
		}
		return new PaletteDimensions(n, opthw, opthh, false, optsw, optsh, false, opthh, opthw, n < 64);
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
		while (spw > (sph + sph / 2)) sph += squareHeight;
		while (sph > (spw + spw / 2)) spw += squareWidth;
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
