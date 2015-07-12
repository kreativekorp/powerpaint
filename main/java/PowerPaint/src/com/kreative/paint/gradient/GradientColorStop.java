package com.kreative.paint.gradient;

import java.awt.Color;

public class GradientColorStop implements Comparable<GradientColorStop> {
	public final double position;
	public final GradientColor color;
	
	public GradientColorStop(double position, GradientColor color) {
		this.position = position;
		this.color = color;
	}
	
	@Override
	public int compareTo(GradientColorStop that) {
		if (this.position < that.position) return -1;
		if (this.position > that.position) return +1;
		// For some godforsaken reason TreeSet breaks if
		// compareTo returns zero when equals returns false.
		Color thisColor = (this.color != null) ? this.color.awtColor() : null;
		Color thatColor = (that.color != null) ? that.color.awtColor() : null;
		int thisAlpha = (thisColor != null) ? thisColor.getAlpha() : 0;
		int thatAlpha = (thatColor != null) ? thatColor.getAlpha() : 0;
		if (thisAlpha != thatAlpha) return thisAlpha - thatAlpha;
		int thisRed = (thisColor != null) ? thisColor.getRed() : 0;
		int thatRed = (thatColor != null) ? thatColor.getRed() : 0;
		if (thisRed != thatRed) return thisRed - thatRed;
		int thisGreen = (thisColor != null) ? thisColor.getGreen() : 0;
		int thatGreen = (thatColor != null) ? thatColor.getGreen() : 0;
		if (thisGreen != thatGreen) return thisGreen - thatGreen;
		int thisBlue = (thisColor != null) ? thisColor.getBlue() : 0;
		int thatBlue = (thatColor != null) ? thatColor.getBlue() : 0;
		if (thisBlue != thatBlue) return thisBlue - thatBlue;
		return 0;
	}
	
	@Override
	public boolean equals(Object that) {
		if (that instanceof GradientColorStop) {
			double thisPosition = this.position;
			double thatPosition = ((GradientColorStop)that).position;
			if (thisPosition != thatPosition) return false;
			GradientColor thisColor = this.color;
			GradientColor thatColor = ((GradientColorStop)that).color;
			if (thisColor != null) return thisColor.equals(thatColor);
			if (thatColor != null) return thatColor.equals(thisColor);
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		Double thisPosition = this.position;
		int hashCode = thisPosition.hashCode();
		if (color != null) hashCode ^= color.hashCode();
		return hashCode;
	}
}
