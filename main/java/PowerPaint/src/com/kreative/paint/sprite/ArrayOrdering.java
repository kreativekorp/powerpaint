package com.kreative.paint.sprite;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum ArrayOrdering {
	LTR_TTB              (false, false, false, false,  0),
	LTR_TTB_BOUSTROPHEDON(false, false, false, true ,  1),
	TTB_LTR              (false, false, true , false,  2),
	TTB_LTR_BOUSTROPHEDON(false, false, true , true ,  3),
	RTL_TTB              (false, true , false, false,  4),
	RTL_TTB_BOUSTROPHEDON(false, true , false, true ,  5),
	TTB_RTL              (false, true , true , false,  6),
	TTB_RTL_BOUSTROPHEDON(false, true , true , true ,  7),
	LTR_BTT              (true , false, false, false,  8),
	LTR_BTT_BOUSTROPHEDON(true , false, false, true ,  9),
	BTT_LTR              (true , false, true , false, 10),
	BTT_LTR_BOUSTROPHEDON(true , false, true , true , 11),
	RTL_BTT              (true , true , false, false, 12),
	RTL_BTT_BOUSTROPHEDON(true , true , false, true , 13),
	BTT_RTL              (true , true , true , false, 14),
	BTT_RTL_BOUSTROPHEDON(true , true , true , true , 15);
	
	public final boolean bottomToTop;
	public final boolean rightToLeft;
	public final boolean vertical;
	public final boolean boustrophedon;
	public final int intValue;
	
	private ArrayOrdering(
		boolean bottomToTop,
		boolean rightToLeft,
		boolean vertical,
		boolean boustrophedon,
		int intValue
	) {
		this.bottomToTop = bottomToTop;
		this.rightToLeft = rightToLeft;
		this.vertical = vertical;
		this.boustrophedon = boustrophedon;
		this.intValue = intValue;
	}
	
	public int[] getYX(int rows, int columns, int index, int[] yx) {
		int y, x;
		if (vertical) {
			x = (index / rows) % columns;
			y = (index % rows);
			if (boustrophedon && ((x & 1) != 0)) {
				y = (rows-1) - y;
			}
		} else {
			y = (index / columns) % rows;
			x = (index % columns);
			if (boustrophedon && ((y & 1) != 0)) {
				x = (columns-1) - x;
			}
		}
		if (bottomToTop) {
			y = (rows-1) - y;
		}
		if (rightToLeft) {
			x = (columns-1) - x;
		}
		if (yx == null) {
			return new int[]{y,x};
		} else {
			yx[0] = y;
			yx[1] = x;
			return yx;
		}
	}
	
	public int getIndex(int rows, int columns, int y, int x) {
		y %= rows;
		x %= columns;
		if (bottomToTop) {
			y = (rows-1) - y;
		}
		if (rightToLeft) {
			x = (columns-1) - x;
		}
		if (vertical) {
			if (boustrophedon && ((x & 1) != 0)) {
				y = (rows-1) - y;
			}
			return x * rows + y;
		} else {
			if (boustrophedon && ((y & 1) != 0)) {
				x = (columns-1) - x;
			}
			return y * columns + x;
		}
	}
	
	@Override
	public String toString() {
		String verticalDirection = bottomToTop ? "Bottom to Top" : "Top to Bottom";
		String horizontalDirection = rightToLeft ? "Right to Left" : "Left to Right";
		String lineDirection = vertical ? verticalDirection : horizontalDirection;
		String pageDirection = vertical ? horizontalDirection : verticalDirection;
		String direction = lineDirection + ", " + pageDirection;
		if (boustrophedon) direction += ", Boustrophedon";
		return direction;
	}
	
	public static ArrayOrdering fromIntValue(int intValue) {
		switch (intValue) {
			case  0: return LTR_TTB              ;
			case  1: return LTR_TTB_BOUSTROPHEDON;
			case  2: return TTB_LTR              ;
			case  3: return TTB_LTR_BOUSTROPHEDON;
			case  4: return RTL_TTB              ;
			case  5: return RTL_TTB_BOUSTROPHEDON;
			case  6: return TTB_RTL              ;
			case  7: return TTB_RTL_BOUSTROPHEDON;
			case  8: return LTR_BTT              ;
			case  9: return LTR_BTT_BOUSTROPHEDON;
			case 10: return BTT_LTR              ;
			case 11: return BTT_LTR_BOUSTROPHEDON;
			case 12: return RTL_BTT              ;
			case 13: return RTL_BTT_BOUSTROPHEDON;
			case 14: return BTT_RTL              ;
			case 15: return BTT_RTL_BOUSTROPHEDON;
			default: return null;
		}
	}
	
	public static ArrayOrdering fromBooleans(
		boolean bottomToTop, boolean rightToLeft,
		boolean vertical, boolean boustrophedon
	) {
		if (bottomToTop) {
			if (rightToLeft) {
				if (vertical) {
					return boustrophedon ? BTT_RTL_BOUSTROPHEDON : BTT_RTL;
				} else {
					return boustrophedon ? RTL_BTT_BOUSTROPHEDON : RTL_BTT;
				}
			} else {
				if (vertical) {
					return boustrophedon ? BTT_LTR_BOUSTROPHEDON : BTT_LTR;
				} else {
					return boustrophedon ? LTR_BTT_BOUSTROPHEDON : LTR_BTT;
				}
			}
		} else {
			if (rightToLeft) {
				if (vertical) {
					return boustrophedon ? TTB_RTL_BOUSTROPHEDON : TTB_RTL;
				} else {
					return boustrophedon ? RTL_TTB_BOUSTROPHEDON : RTL_TTB;
				}
			} else {
				if (vertical) {
					return boustrophedon ? TTB_LTR_BOUSTROPHEDON : TTB_LTR;
				} else {
					return boustrophedon ? LTR_TTB_BOUSTROPHEDON : LTR_TTB;
				}
			}
		}
	}
	
	private static final Pattern DECIMAL_PATTERN = Pattern.compile("([0-9]+)");
	private static final Pattern HEXADECIMAL_PATTERN = Pattern.compile("0[Xx]([0-9A-Fa-f]+)");
	
	private static final String SPP = "(\\s|[!-~&&[^0-9A-Za-z]])?";
	private static final Pattern VERTICAL_DIRECTION_PATTERN = Pattern.compile(
		"[Tt](op)?" + SPP + "[Tt]o?" + SPP + "[Bb](ottom)?" + "|" +
		"[Bb](ottom)?" + SPP + "[Tt]o?" + SPP + "[Tt](op)?"
	);
	private static final Pattern HORIZONTAL_DIRECTION_PATTERN = Pattern.compile(
		"[Ll](eft)?" + SPP + "[Tt]o?" + SPP + "[Rr](ight)?" + "|" +
		"[Rr](ight)?" + SPP + "[Tt]o?" + SPP + "[Ll](eft)?"
	);
	private static final Pattern BOUSTROPHEDON_PATTERN = Pattern.compile(
		"[Bb]oust(rophedon)?"
	);
	
	public static ArrayOrdering fromString(String s) {
		Matcher m;
		s = s.trim().toLowerCase();
		m = DECIMAL_PATTERN.matcher(s);
		if (m.matches()) {
			try {
				int i = Integer.parseInt(m.group(1), 10);
				return fromIntValue(i);
			} catch (NumberFormatException e) {
				return null;
			}
		}
		m = HEXADECIMAL_PATTERN.matcher(s);
		if (m.matches()) {
			try {
				int i = Integer.parseInt(m.group(1), 16);
				return fromIntValue(i);
			} catch (NumberFormatException e) {
				return null;
			}
		}
		m = VERTICAL_DIRECTION_PATTERN.matcher(s);
		if (!m.find()) return null;
		boolean bottomToTop = m.group().startsWith("b");
		int verticalIndex = m.start();
		m = HORIZONTAL_DIRECTION_PATTERN.matcher(s);
		if (!m.find()) return null;
		boolean rightToLeft = m.group().startsWith("r");
		int horizontalIndex = m.start();
		boolean vertical = verticalIndex < horizontalIndex;
		m = BOUSTROPHEDON_PATTERN.matcher(s);
		boolean boustrophedon = m.find();
		return fromBooleans(bottomToTop, rightToLeft, vertical, boustrophedon);
	}
	
	public static void main(String[] args) {
		if (args.length > 0) {
			for (String arg : args) {
				ArrayOrdering o = fromString(arg);
				if (o != null) {
					System.out.println(o.intValue + "\t" + o.toString());
				} else {
					System.out.println("?\t(null)");
				}
			}
		} else {
			for (ArrayOrdering o : values()) {
				System.out.println(o.intValue + "\t" + o.toString());
				assert(o.ordinal() == o.intValue);
				assert(values()[o.ordinal()] == o);
				assert(values()[o.intValue] == o);
				assert(fromIntValue(o.ordinal()) == o);
				assert(fromIntValue(o.intValue) == o);
				assert(fromBooleans(o.bottomToTop, o.rightToLeft, o.vertical, o.boustrophedon) == o);
				assert(fromString(Integer.toString(o.ordinal())) == o);
				assert(fromString(Integer.toString(o.intValue)) == o);
				assert(fromString("0x" + Integer.toHexString(o.ordinal())) == o);
				assert(fromString("0x" + Integer.toHexString(o.intValue)) == o);
				assert(fromString(o.name()) == o);
				assert(fromString(o.toString()) == o);
			}
			for (int i = 0; i < 16; i++) {
				assert(values()[i].ordinal() == i);
				assert(values()[i].intValue == i);
				assert(fromIntValue(i).ordinal() == i);
				assert(fromIntValue(i).intValue == i);
				assert(fromString(Integer.toString(i)).ordinal() == i);
				assert(fromString(Integer.toString(i)).intValue == i);
				assert(fromString("0x" + Integer.toHexString(i)).ordinal() == i);
				assert(fromString("0x" + Integer.toHexString(i)).intValue == i);
			}
		}
	}
}
