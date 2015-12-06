package com.kreative.paint.material.sprite;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpriteIntent {
	public static final int UNKNOWN           = 0x00;
	public static final int COLLECTION        = 0x01;
	public static final int OBJECT            = 0x02;
	public static final int CHARACTER         = 0x03;
	public static final int CURSOR            = 0x40;
	public static final int ICON              = 0x41;
	public static final int FONT              = 0x42;
	public static final int PATTERN           = 0x43;
	public static final int STATIC_BRUSH      = 0x80;
	public static final int ACCELERATED_BRUSH = 0x81;
	public static final int SPRAYED_BRUSH     = 0x82;
	public static final int STAMPED_BRUSH     = 0x83;
	public static final int RUBBER_STAMPS     = 0xC0;
	public static final int ANIMATED_STAMPS   = 0xC1;
	
	public static String toString(int intent) {
		switch (intent) {
			case UNKNOWN          : return "unknown"          ;
			case COLLECTION       : return "collection"       ;
			case OBJECT           : return "object"           ;
			case CHARACTER        : return "character"        ;
			case CURSOR           : return "cursor"           ;
			case ICON             : return "icon"             ;
			case FONT             : return "font"             ;
			case PATTERN          : return "pattern"          ;
			case STATIC_BRUSH     : return "static-brush"     ;
			case ACCELERATED_BRUSH: return "accelerated-brush";
			case SPRAYED_BRUSH    : return "sprayed-brush"    ;
			case STAMPED_BRUSH    : return "stamped-brush"    ;
			case RUBBER_STAMPS    : return "rubber-stamps"    ;
			case ANIMATED_STAMPS  : return "animated-stamps"  ;
			default: return Integer.toString(intent);
		}
	}
	
	private static final Pattern DECIMAL_PATTERN = Pattern.compile("([0-9]+)");
	private static final Pattern HEXADECIMAL_PATTERN = Pattern.compile("0[Xx]([0-9A-Fa-f]+)");
	
	private static final String SPP = "(\\s|[!-~&&[^0-9A-Za-z]])?";
	private static final Pattern COLLECTION_PATTERN        = Pattern.compile("[Cc]oll(ection)?");
	private static final Pattern OBJECT_PATTERN            = Pattern.compile("[Oo]bj(ect)?"    );
	private static final Pattern CHARACTER_PATTERN         = Pattern.compile("[Cc]har(acter)?" );
	private static final Pattern CURSOR_PATTERN            = Pattern.compile("[Cc]ursor"       );
	private static final Pattern ICON_PATTERN              = Pattern.compile("[Ii]con"         );
	private static final Pattern FONT_PATTERN              = Pattern.compile("[Ff]ont"         );
	private static final Pattern PATTERN_PATTERN           = Pattern.compile("[Pp]at(t(ern)?)?");
	private static final Pattern STATIC_BRUSH_PATTERN      = Pattern.compile("[Ss]tatic"         + SPP + "[Bb]rush"  );
	private static final Pattern ACCELERATED_BRUSH_PATTERN = Pattern.compile("[Aa]ccel(erated)?" + SPP + "[Bb]rush"  );
	private static final Pattern SPRAYED_BRUSH_PATTERN     = Pattern.compile("[Ss]prayed"        + SPP + "[Bb]rush"  );
	private static final Pattern STAMPED_BRUSH_PATTERN     = Pattern.compile("[Ss]tamped"        + SPP + "[Bb]rush"  );
	private static final Pattern RUBBER_STAMPS_PATTERN     = Pattern.compile("[Rr]ubber"         + SPP + "[Ss]tamps?");
	private static final Pattern ANIMATED_STAMPS_PATTERN   = Pattern.compile("[Aa]nim(ated)?"    + SPP + "[Ss]tamps?");
	
	public static int fromString(String s) {
		Matcher m;
		s = s.trim().toLowerCase();
		m = DECIMAL_PATTERN.matcher(s);
		if (m.matches()) {
			try { return Integer.parseInt(m.group(1), 10); }
			catch (NumberFormatException e) { return 0; }
		}
		m = HEXADECIMAL_PATTERN.matcher(s);
		if (m.matches()) {
			try { return Integer.parseInt(m.group(1), 16); }
			catch (NumberFormatException e) { return 0; }
		}
		if (COLLECTION_PATTERN       .matcher(s).matches()) return COLLECTION       ;
		if (OBJECT_PATTERN           .matcher(s).matches()) return OBJECT           ;
		if (CHARACTER_PATTERN        .matcher(s).matches()) return CHARACTER        ;
		if (CURSOR_PATTERN           .matcher(s).matches()) return CURSOR           ;
		if (ICON_PATTERN             .matcher(s).matches()) return ICON             ;
		if (FONT_PATTERN             .matcher(s).matches()) return FONT             ;
		if (PATTERN_PATTERN          .matcher(s).matches()) return PATTERN          ;
		if (STATIC_BRUSH_PATTERN     .matcher(s).matches()) return STATIC_BRUSH     ;
		if (ACCELERATED_BRUSH_PATTERN.matcher(s).matches()) return ACCELERATED_BRUSH;
		if (SPRAYED_BRUSH_PATTERN    .matcher(s).matches()) return SPRAYED_BRUSH    ;
		if (STAMPED_BRUSH_PATTERN    .matcher(s).matches()) return STAMPED_BRUSH    ;
		if (RUBBER_STAMPS_PATTERN    .matcher(s).matches()) return RUBBER_STAMPS    ;
		if (ANIMATED_STAMPS_PATTERN  .matcher(s).matches()) return ANIMATED_STAMPS  ;
		return UNKNOWN;
	}
	
	private SpriteIntent() {}
}
