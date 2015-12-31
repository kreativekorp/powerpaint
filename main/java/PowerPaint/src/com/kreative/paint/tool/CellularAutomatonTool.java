package com.kreative.paint.tool;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.util.Random;
import com.kreative.paint.ToolContext;
import com.kreative.paint.document.draw.PaintSettings;
import com.kreative.paint.form.EnumOption;
import com.kreative.paint.form.Form;
import com.kreative.paint.form.IntegerOption;
import com.kreative.paint.form.PreviewGenerator;
import com.kreative.paint.util.Bitmap;

public class CellularAutomatonTool extends AbstractPaintTool
implements ToolOptions.DrawFromCenter, ToolOptions.DrawFilled, ToolOptions.Custom {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					K,K,K,K,K,K,K,K,K,K,K,K,K,K,K,K,
					K,0,0,0,0,0,0,0,0,0,0,0,0,0,K,0,
					K,0,0,0,0,0,0,0,0,0,0,0,0,K,K,K,
					K,0,0,0,0,0,0,0,0,0,0,0,K,0,K,0,
					K,0,0,0,0,0,0,0,0,0,0,K,K,K,K,K,
					K,0,0,0,0,0,0,0,0,0,K,0,K,0,K,0,
					K,0,0,0,0,0,0,0,0,K,K,K,K,K,K,K,
					K,0,0,0,0,0,0,0,K,0,K,0,K,0,K,0,
					K,0,0,0,0,0,0,K,K,K,K,K,K,K,K,K,
					K,0,0,0,0,0,K,0,K,0,0,0,0,0,K,0,
					K,0,0,0,0,K,K,K,K,0,0,0,0,K,K,K,
					K,0,0,0,K,0,K,0,K,0,0,0,K,0,K,0,
					K,0,0,K,K,K,K,K,K,0,0,K,K,K,K,K,
					K,0,K,0,K,0,K,0,K,0,K,0,K,0,K,0,
					K,K,K,K,K,K,K,K,K,K,K,K,K,K,K,K,
					K,0,K,0,K,0,K,0,K,0,K,0,K,0,K,0,
			}
	);
	private static final Cursor curs = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
	
	public ToolCategory getCategory() {
		return ToolCategory.MISC;
	}
	
	protected Image getBWIcon() {
		return icon;
	}
	
	public static enum InitialCondition {
		EMPTY,
		SIMPLE,
		GRAY,
		SIMPLE_COMPLEMENT,
		BLACK,
		RANDOM;
		private static final Random random = new Random();
		public void fillRow(int[] rgb, int width, int center, int fill) {
			switch (this) {
			case SIMPLE: rgb[center] = fill; break;
			case GRAY: for (int i = 0; i < width; i+=2) rgb[i] = fill; break;
			case SIMPLE_COMPLEMENT: for (int i = 0; i < width; i++) if (i != center) rgb[i] = fill; break;
			case BLACK: for (int i = 0; i < width; i++) rgb[i] = fill; break;
			case RANDOM: for (int i = 0; i < width; i++) if (random.nextBoolean()) rgb[i] = fill; break;
			default: rgb[center] = fill; break;
			}
		}
	}
	
	private static void drawCA(Graphics2D g, InitialCondition init, int rule, int x, int y, int w, int h) {
		if (w > 0 && h > 0) {
			Shape sc = g.getClip();
			g.clipRect(x, y, w, h);
			int cah = h;
			int caw = (h+h+w+3)|1;
			int cac = caw/2;
			int[] rgb = new int[caw*cah];
			init.fillRow(rgb, caw, cac, 0xFF000000);
			for (int cay = 1, caba = caw; cay < cah && caba < rgb.length; cay++, caba += caw) {
				{
					boolean l1 = rgb[caba-caw] < 0;
					boolean l2 = rgb[caba-caw+1] < 0;
					boolean r1 = rgb[caba-2] < 0;
					boolean r2 = rgb[caba-1] < 0;
					int il = (r2?4:0)|(l1?2:0)|(l2?1:0);
					int ir = (r1?4:0)|(r2?2:0)|(l1?1:0);
					if (((rule >> il) & 1) != 0) {
						rgb[caba] = 0xFF000000;
					}
					if (((rule >> ir) & 1) != 0) {
						rgb[caba+caw-1] = 0xFF000000;
					}
				}
				for (int cax = 1, caa0 = caba-caw+1, caa1 = caba+1; cax < caw-1; cax++, caa0++, caa1++) {
					boolean l = rgb[caa0-1] < 0;
					boolean c = rgb[caa0] < 0;
					boolean r = rgb[caa0+1] < 0;
					int i = (l?4:0)|(c?2:0)|(r?1:0);
					if (((rule >> i) & 1) != 0) {
						rgb[caa1] = 0xFF000000;
					}
				}
			}
			new Bitmap(caw, cah, rgb).paint(g, x+(w-caw)/2, y);
			g.setClip(sc);
		}
	}
	
	public boolean paintIntermediate(ToolEvent e, Graphics2D g) {
		if (e.isMouseDown()) {
			PaintSettings ps = e.getPaintSettings();
			Rectangle r = new Rectangle2D.Float(
					Math.min(e.getX(), e.getPreviousClickedX()),
					Math.min(e.getY(), e.getPreviousClickedY()),
					Math.abs(e.getX()-e.getPreviousClickedX()),
					Math.abs(e.getY()-e.getPreviousClickedY())
			).getBounds();
			if (e.tc().drawFromCenter() != e.isAltDown()) {
				if (e.getX() >= e.getPreviousClickedX()) {
					r.x -= r.width;
				}
				r.width += r.width;
			}
			if (e.tc().drawFilled() != e.isCtrlDown()) {
				ps.applyFill(g);
				g.fill(r);
			}
			InitialCondition caInit = e.tc().getCustom(CellularAutomatonTool.class, "caInit", InitialCondition.class, InitialCondition.SIMPLE);
			int caRule = e.tc().getCustom(CellularAutomatonTool.class, "caRule", Integer.class, 30);
			ps.applyDraw(g);
			drawCA(g, caInit, caRule, r.x, r.y, r.width, r.height);
			g.setComposite(AlphaComposite.SrcOver);
			g.setColor(Color.gray);
			g.setFont(new Font("SansSerif", Font.PLAIN, 10));
			String s = "(Rule "+caRule+")";
			g.drawString(s, r.x+(r.width-g.getFontMetrics().stringWidth(s))/2, r.y-4);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean mouseReleased(ToolEvent e) {
		e.beginTransaction(getName());
		Graphics2D g = e.getPaintGraphics();
		PaintSettings ps = e.getPaintSettings();
		Rectangle r = new Rectangle2D.Float(
				Math.min(e.getX(), e.getPreviousClickedX()),
				Math.min(e.getY(), e.getPreviousClickedY()),
				Math.abs(e.getX()-e.getPreviousClickedX()),
				Math.abs(e.getY()-e.getPreviousClickedY())
		).getBounds();
		if (e.tc().drawFromCenter() != e.isAltDown()) {
			if (e.getX() >= e.getPreviousClickedX()) {
				r.x -= r.width;
			}
			r.width += r.width;
		}
		if (e.tc().drawFilled() != e.isCtrlDown()) {
			ps.applyFill(g);
			g.fill(r);
		}
		InitialCondition caInit = e.tc().getCustom(CellularAutomatonTool.class, "caInit", InitialCondition.class, InitialCondition.SIMPLE);
		int caRule = e.tc().getCustom(CellularAutomatonTool.class, "caRule", Integer.class, 30);
		ps.applyDraw(g);
		drawCA(g, caInit, caRule, r.x, r.y, r.width, r.height);
		e.commitTransaction();
		return true;
	}
	
	public boolean keyPressed(ToolEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_DOWN:
			e.tc().decrementCustom(CellularAutomatonTool.class, "caRule", Integer.class, 30, 0);
			break;
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_UP:
			e.tc().incrementCustom(CellularAutomatonTool.class, "caRule", Integer.class, 30, 255);
			break;
		case KeyEvent.VK_1:
			e.tc().setCustom(CellularAutomatonTool.class, "caInit", InitialCondition.EMPTY);
			break;
		case KeyEvent.VK_2:
			e.tc().setCustom(CellularAutomatonTool.class, "caInit", InitialCondition.SIMPLE);
			break;
		case KeyEvent.VK_3:
			e.tc().setCustom(CellularAutomatonTool.class, "caInit", InitialCondition.GRAY);
			break;
		case KeyEvent.VK_4:
			e.tc().setCustom(CellularAutomatonTool.class, "caInit", InitialCondition.SIMPLE_COMPLEMENT);
			break;
		case KeyEvent.VK_5:
			e.tc().setCustom(CellularAutomatonTool.class, "caInit", InitialCondition.BLACK);
			break;
		case KeyEvent.VK_6:
			e.tc().setCustom(CellularAutomatonTool.class, "caInit", InitialCondition.RANDOM);
			break;
		case KeyEvent.VK_7:
			e.tc().setCustom(CellularAutomatonTool.class, "caRule", 30);
			break;
		case KeyEvent.VK_8:
			e.tc().setCustom(CellularAutomatonTool.class, "caRule", 90);
			break;
		case KeyEvent.VK_9:
			e.tc().setCustom(CellularAutomatonTool.class, "caRule", 110);
			break;
		case KeyEvent.VK_0:
			e.tc().setCustom(CellularAutomatonTool.class, "caRule", 184);
			break;
		}
		return false;
	}
	
	public boolean doubleClickForOptions() {
		return true;
	}

	public Cursor getCursor(ToolEvent e) {
		return curs;
	}
	
	public Form getCustomOptionsForm(final ToolContext tc) {
		Form f = new Form();
		f.add(new PreviewGenerator() {
			public String getName() { return null; }
			public void generatePreview(Graphics2D g, Rectangle r) {
				InitialCondition caInit = tc.getCustom(CellularAutomatonTool.class, "caInit", InitialCondition.class, InitialCondition.SIMPLE);
				int caRule = tc.getCustom(CellularAutomatonTool.class, "caRule", Integer.class, 30);
				drawCA(g, caInit, caRule, r.x, r.y, r.width, r.height);
			}
		});
		f.add(new IntegerOption() {
			public String getName() { return ToolUtilities.messages.getString("cellauto.options.Rule"); }
			public int getMaximum() { return 255; }
			public int getMinimum() { return 0; }
			public int getStep() { return 1; }
			public int getValue() { return tc.getCustom(CellularAutomatonTool.class, "caRule", Integer.class, 30); }
			public void setValue(int v) { tc.setCustom(CellularAutomatonTool.class, "caRule", v); }
			public boolean useSlider() { return false; }
		});
		f.add(new EnumOption<InitialCondition>() {
			public String getName() { return ToolUtilities.messages.getString("cellauto.options.Init"); }
			public InitialCondition getValue() { return tc.getCustom(CellularAutomatonTool.class, "caInit", InitialCondition.class, InitialCondition.SIMPLE); }
			public void setValue(InitialCondition v) { tc.setCustom(CellularAutomatonTool.class, "caInit", v); }
			public InitialCondition[] values() { return InitialCondition.values(); }
			public String getLabel(InitialCondition v) { return ToolUtilities.messages.getString("cellauto.options.Init."+v.name()); }
		});
		return f;
	}
}
