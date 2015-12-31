package com.kreative.paint.document.draw;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;

public class PaintSettings {
	public static PaintSettings forGraphicsFill(Graphics2D g) {
		Object aa = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		Object taa = g.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
		return new PaintSettings(
			g.getPaint(),
			g.getComposite(),
			(aa == RenderingHints.VALUE_ANTIALIAS_ON),
			null,
			g.getComposite(),
			g.getStroke(),
			(aa == RenderingHints.VALUE_ANTIALIAS_ON),
			g.getFont(),
			TextAlignment.LEFT,
			(taa == RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
		);
	}
	
	public static PaintSettings forGraphicsDraw(Graphics2D g) {
		Object aa = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		Object taa = g.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
		return new PaintSettings(
			null,
			g.getComposite(),
			(aa == RenderingHints.VALUE_ANTIALIAS_ON),
			g.getPaint(),
			g.getComposite(),
			g.getStroke(),
			(aa == RenderingHints.VALUE_ANTIALIAS_ON),
			g.getFont(),
			TextAlignment.LEFT,
			(taa == RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
		);
	}
	
	public static PaintSettings forGraphicsClear(Graphics2D g) {
		Object aa = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		Object taa = g.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
		return new PaintSettings(
			g.getBackground(),
			AlphaComposite.SrcOver,
			false,
			null,
			g.getComposite(),
			g.getStroke(),
			(aa == RenderingHints.VALUE_ANTIALIAS_ON),
			g.getFont(),
			TextAlignment.LEFT,
			(taa == RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
		);
	}
	
	public static PaintSettings forGraphicsDrawImage(Graphics2D g) {
		Object aa = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		Object taa = g.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
		return new PaintSettings(
			null,
			g.getComposite(),
			(aa == RenderingHints.VALUE_ANTIALIAS_ON),
			null,
			g.getComposite(),
			g.getStroke(),
			(aa == RenderingHints.VALUE_ANTIALIAS_ON),
			g.getFont(),
			TextAlignment.LEFT,
			(taa == RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
		);
	}
	
	public final Paint fillPaint;
	public final Composite fillComposite;
	public final boolean fillAntiAliased;
	public final Paint drawPaint;
	public final Composite drawComposite;
	public final Stroke drawStroke;
	public final boolean drawAntiAliased;
	public final Font textFont;
	public final TextAlignment textAlignment;
	public final boolean textAntiAliased;
	
	public PaintSettings(Paint fill, Paint draw) {
		this(fill, draw, new BasicStroke(1));
	}
	
	public PaintSettings(Paint fill, Paint draw, Stroke stroke) {
		this(fill, draw, stroke, new Font("SansSerif", Font.PLAIN, 12));
	}
	
	public PaintSettings(Paint fill, Paint draw, Stroke stroke, Font font) {
		this(
			fill, AlphaComposite.SrcOver, false,
			draw, AlphaComposite.SrcOver, stroke, false,
			font, TextAlignment.LEFT, false
		);
	}
	
	public PaintSettings(
		Paint fillPaint,
		Composite fillComposite,
		boolean fillAntiAliased,
		Paint drawPaint,
		Composite drawComposite,
		Stroke drawStroke,
		boolean drawAntiAliased,
		Font textFont,
		TextAlignment textAlignment,
		boolean textAntiAliased
	) {
		this.fillPaint = fillPaint;
		this.fillComposite = fillComposite;
		this.fillAntiAliased = fillAntiAliased;
		this.drawPaint = drawPaint;
		this.drawComposite = drawComposite;
		this.drawStroke = drawStroke;
		this.drawAntiAliased = drawAntiAliased;
		this.textFont = textFont;
		this.textAlignment = textAlignment;
		this.textAntiAliased = textAntiAliased;
	}
	
	public boolean isFilled() {
		return fillPaint != null && fillComposite != null;
	}
	
	public boolean isDrawn() {
		return drawPaint != null && drawComposite != null && drawStroke != null;
	}
	
	public PaintSettings deriveFillPaint(Paint fillPaint) {
		return new PaintSettings(
			fillPaint, fillComposite, fillAntiAliased,
			drawPaint, drawComposite, drawStroke, drawAntiAliased,
			textFont, textAlignment, textAntiAliased
		);
	}
	
	public PaintSettings deriveFillComposite(Composite fillComposite) {
		return new PaintSettings(
			fillPaint, fillComposite, fillAntiAliased,
			drawPaint, drawComposite, drawStroke, drawAntiAliased,
			textFont, textAlignment, textAntiAliased
		);
	}
	
	public PaintSettings deriveFillAntiAliased(boolean fillAntiAliased) {
		return new PaintSettings(
			fillPaint, fillComposite, fillAntiAliased,
			drawPaint, drawComposite, drawStroke, drawAntiAliased,
			textFont, textAlignment, textAntiAliased
		);
	}
	
	public PaintSettings deriveDrawPaint(Paint drawPaint) {
		return new PaintSettings(
			fillPaint, fillComposite, fillAntiAliased,
			drawPaint, drawComposite, drawStroke, drawAntiAliased,
			textFont, textAlignment, textAntiAliased
		);
	}
	
	public PaintSettings deriveDrawComposite(Composite drawComposite) {
		return new PaintSettings(
			fillPaint, fillComposite, fillAntiAliased,
			drawPaint, drawComposite, drawStroke, drawAntiAliased,
			textFont, textAlignment, textAntiAliased
		);
	}
	
	public PaintSettings deriveDrawStroke(Stroke drawStroke) {
		return new PaintSettings(
			fillPaint, fillComposite, fillAntiAliased,
			drawPaint, drawComposite, drawStroke, drawAntiAliased,
			textFont, textAlignment, textAntiAliased
		);
	}
	
	public PaintSettings deriveDrawAntiAliased(boolean drawAntiAliased) {
		return new PaintSettings(
			fillPaint, fillComposite, fillAntiAliased,
			drawPaint, drawComposite, drawStroke, drawAntiAliased,
			textFont, textAlignment, textAntiAliased
		);
	}
	
	public PaintSettings deriveTextFont(Font textFont) {
		return new PaintSettings(
			fillPaint, fillComposite, fillAntiAliased,
			drawPaint, drawComposite, drawStroke, drawAntiAliased,
			textFont, textAlignment, textAntiAliased
		);
	}
	
	public PaintSettings deriveTextAlignment(TextAlignment textAlignment) {
		return new PaintSettings(
			fillPaint, fillComposite, fillAntiAliased,
			drawPaint, drawComposite, drawStroke, drawAntiAliased,
			textFont, textAlignment, textAntiAliased
		);
	}
	
	public PaintSettings deriveTextAntiAliased(boolean textAntiAliased) {
		return new PaintSettings(
			fillPaint, fillComposite, fillAntiAliased,
			drawPaint, drawComposite, drawStroke, drawAntiAliased,
			textFont, textAlignment, textAntiAliased
		);
	}
	
	public void applyFill(Graphics2D g) {
		g.setPaint(fillPaint);
		g.setComposite(fillComposite);
		g.setRenderingHint(
			RenderingHints.KEY_ANTIALIASING,
			fillAntiAliased
			? RenderingHints.VALUE_ANTIALIAS_ON
			: RenderingHints.VALUE_ANTIALIAS_OFF
		);
		g.setFont(textFont);
		g.setRenderingHint(
			RenderingHints.KEY_TEXT_ANTIALIASING,
			textAntiAliased
			? RenderingHints.VALUE_TEXT_ANTIALIAS_ON
			: RenderingHints.VALUE_TEXT_ANTIALIAS_OFF
		);
	}
	
	public void applyDraw(Graphics2D g) {
		g.setPaint(drawPaint);
		g.setComposite(drawComposite);
		g.setStroke(drawStroke);
		g.setRenderingHint(
			RenderingHints.KEY_ANTIALIASING,
			drawAntiAliased
			? RenderingHints.VALUE_ANTIALIAS_ON
			: RenderingHints.VALUE_ANTIALIAS_OFF
		);
		g.setFont(textFont);
		g.setRenderingHint(
			RenderingHints.KEY_TEXT_ANTIALIASING,
			textAntiAliased
			? RenderingHints.VALUE_TEXT_ANTIALIAS_ON
			: RenderingHints.VALUE_TEXT_ANTIALIAS_OFF
		);
	}
	
	@Override
	public boolean equals(Object that) {
		if (that instanceof PaintSettings) {
			return equals(this.fillPaint, ((PaintSettings)that).fillPaint)
			    && equals(this.fillComposite, ((PaintSettings)that).fillComposite)
			    && (this.fillAntiAliased == ((PaintSettings)that).fillAntiAliased)
			    && equals(this.drawPaint, ((PaintSettings)that).drawPaint)
			    && equals(this.drawComposite, ((PaintSettings)that).drawComposite)
			    && equals(this.drawStroke, ((PaintSettings)that).drawStroke)
			    && (this.drawAntiAliased == ((PaintSettings)that).drawAntiAliased)
			    && equals(this.textFont, ((PaintSettings)that).textFont)
			    && (this.textAlignment == ((PaintSettings)that).textAlignment)
			    && (this.textAntiAliased == ((PaintSettings)that).textAntiAliased);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return ((this.fillPaint != null) ? this.fillPaint.hashCode() : 0)
		     ^ ((this.fillComposite != null) ? this.fillComposite.hashCode() : 0)
		     ^ (this.fillAntiAliased ? 0x11111111 : 0)
		     ^ ((this.drawPaint != null) ? this.drawPaint.hashCode() : 0)
		     ^ ((this.drawComposite != null) ? this.drawComposite.hashCode() : 0)
		     ^ ((this.drawStroke != null) ? this.drawStroke.hashCode() : 0)
		     ^ (this.drawAntiAliased ? 0x22222222 : 0)
		     ^ ((this.textFont != null) ? this.textFont.hashCode() : 0)
		     ^ ((this.textAlignment != null) ? this.textAlignment.hashCode() : 0)
		     ^ (this.textAntiAliased ? 0x44444444 : 0);
	}
	
	private static boolean equals(Object dis, Object dat) {
		if (dis == null) return (dat == null);
		if (dat == null) return (dis == null);
		return dis.equals(dat);
	}
}
