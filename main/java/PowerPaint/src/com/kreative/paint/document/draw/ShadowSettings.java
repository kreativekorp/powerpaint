package com.kreative.paint.document.draw;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;

public class ShadowSettings {
	public static final int SHADOW_TYPE_NONE = 0;
	public static final int SHADOW_TYPE_STROKE = 1;
	public static final int SHADOW_TYPE_FILL = 2;
	public static final int SHADOW_TYPE_BLACK = 3;
	public static final int SHADOW_TYPE_GRAY = 4;
	public static final int SHADOW_TYPE_WHITE = 5;
	
	public final int shadowType;
	public final int shadowOpacity;
	public final int xOffset;
	public final int yOffset;
	
	public ShadowSettings(int type, int opacity, int x, int y) {
		this.shadowType = type;
		this.shadowOpacity = opacity;
		this.xOffset = x;
		this.yOffset = y;
	}
	
	public boolean isShadowed() {
		if (shadowType == 0 || shadowOpacity == 0) return false;
		if (xOffset == 0 && yOffset == 0) return false;
		return true;
	}
	
	public ShadowSettings deriveShadowType(int shadowType) {
		return new ShadowSettings(shadowType, shadowOpacity, xOffset, yOffset);
	}
	
	public ShadowSettings deriveShadowOpacity(int shadowOpacity) {
		return new ShadowSettings(shadowType, shadowOpacity, xOffset, yOffset);
	}
	
	public ShadowSettings deriveShadowXOffset(int xOffset) {
		return new ShadowSettings(shadowType, shadowOpacity, xOffset, yOffset);
	}
	
	public ShadowSettings deriveShadowYOffset(int yOffset) {
		return new ShadowSettings(shadowType, shadowOpacity, xOffset, yOffset);
	}
	
	public void apply(Graphics2D g, PaintSettings ps) {
		switch (shadowType) {
			case SHADOW_TYPE_NONE:
				g.setPaint(new Color(0, true));
				g.setComposite(AlphaComposite.SrcOver);
				break;
			case SHADOW_TYPE_STROKE:
				ps.applyDraw(g);
				if (shadowOpacity < 255 && g.getComposite() instanceof AlphaComposite) {
					AlphaComposite cx = (AlphaComposite)g.getComposite();
					g.setComposite(AlphaComposite.getInstance(cx.getRule(), cx.getAlpha() * shadowOpacity / 255f));
				}
				break;
			case SHADOW_TYPE_FILL:
				ps.applyFill(g);
				if (shadowOpacity < 255 && g.getComposite() instanceof AlphaComposite) {
					AlphaComposite cx = (AlphaComposite)g.getComposite();
					g.setComposite(AlphaComposite.getInstance(cx.getRule(), cx.getAlpha() * shadowOpacity / 255f));
				}
				break;
			case SHADOW_TYPE_BLACK:
				g.setPaint(new Color((shadowOpacity << 24), true));
				g.setComposite(AlphaComposite.SrcOver);
				break;
			case SHADOW_TYPE_GRAY:
				g.setPaint(new Color((shadowOpacity << 24) | 0x808080, true));
				g.setComposite(AlphaComposite.SrcOver);
				break;
			case SHADOW_TYPE_WHITE:
				g.setPaint(new Color((shadowOpacity << 24) | 0xFFFFFF, true));
				g.setComposite(AlphaComposite.SrcOver);
				break;
			default:
				int a = ((shadowType >> 24) & 0xFF) * shadowOpacity / 255;
				g.setPaint(new Color((a << 24) | (shadowType & 0xFFFFFF), true));
				g.setComposite(AlphaComposite.SrcOver);
				break;
		}
		g.translate(xOffset, yOffset);
	}
	
	@Override
	public boolean equals(Object that) {
		if (that instanceof ShadowSettings) {
			return (this.shadowType == ((ShadowSettings)that).shadowType)
			    && (this.shadowOpacity == ((ShadowSettings)that).shadowOpacity)
			    && (this.xOffset == ((ShadowSettings)that).xOffset)
			    && (this.yOffset == ((ShadowSettings)that).yOffset);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return shadowType + shadowOpacity + xOffset + yOffset;
	}
}
