package test;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.TexturePaint;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JComponent;
import javax.swing.JFrame;
import com.kreative.paint.document.draw.DrawObject;
import com.kreative.paint.document.draw.PaintSettings;
import com.kreative.paint.document.draw.ShapeDrawObject;
import com.kreative.paint.document.layer.DrawLayer;
import com.kreative.paint.document.layer.FillLayer;
import com.kreative.paint.document.layer.GroupLayer;
import com.kreative.paint.document.layer.Layer;
import com.kreative.paint.document.layer.PaintLayer;
import com.kreative.paint.material.MaterialLocator;
import com.kreative.paint.material.TextureLoader;

public class NewLayerTest {
	private static MyComponent comp = null;
	private static Layer layer = null;
	private static int gx = 0, gy = 0, gw = 100, gh = 100, tx = 0, ty = 0;
	
	public static void main(String[] args) {
		final MaterialLocator mloc = new MaterialLocator("Kreative", "PowerPaint");
		final TextureLoader tl = new TextureLoader(mloc.getMaterialLoader());
		final TexturePaint tp = tl.getTextures().getValue("SuperPaint").getValue("Berkeley");
		layer = new FillLayer("Berkeley", tp, AlphaComposite.SrcOver);
		JFrame f = new JFrame("Hello World");
		f.setContentPane(comp = new MyComponent());
		f.setSize(200, 200);
		f.setLocationRelativeTo(null);
		f.setVisible(true);
		f.addKeyListener(new KeyListener() {
			private int mode = 0;
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
					case KeyEvent.VK_F:
						{
							FillLayer l = new FillLayer("Berkeley", tp, AlphaComposite.SrcOver);
							layer = l;
						}
						comp.repaint();
						break;
					case KeyEvent.VK_D:
						{
							DrawLayer l = new DrawLayer("Layer 1");
							DrawObject d = new ShapeDrawObject.Ellipse(new PaintSettings(tp, Color.black), 0, 0, 100, 100);
							l.add(d);
							layer = l;
						}
						comp.repaint();
						break;
					case KeyEvent.VK_P:
						{
							PaintLayer l = new PaintLayer("Layer 1", 0xFFFFFF);
							Graphics2D g = l.createPaintGraphics();
							g.setPaint(tp);
							g.fillOval( 0,  0, 50, 50);
							g.fillOval(50,  0, 50, 50);
							g.fillOval( 0, 50, 50, 50);
							g.fillOval(50, 50, 50, 50);
							g.dispose();
							layer = l;
						}
						comp.repaint();
						break;
					case KeyEvent.VK_G:
						{
							GroupLayer gl = new GroupLayer("Group 1");
							{
								FillLayer l = new FillLayer("White", Color.white, AlphaComposite.SrcOver);
								gl.add(l);
							}
							{
								DrawLayer l = new DrawLayer("Layer 1");
								DrawObject d = new ShapeDrawObject.Ellipse(new PaintSettings(tp, Color.black), 0, 0, 100, 100);
								l.add(d);
								gl.add(l);
							}
							{
								PaintLayer l = new PaintLayer("Layer 1", 0xFFFFFF);
								Graphics2D g = l.createPaintGraphics();
								g.setPaint(Color.gray);
								g.fillOval( 0,  0, 50, 50);
								g.fillOval(50,  0, 50, 50);
								g.fillOval( 0, 50, 50, 50);
								g.fillOval(50, 50, 50, 50);
								g.dispose();
								gl.add(l);
							}
							layer = gl;
						}
						comp.repaint();
						break;
					case KeyEvent.VK_V: mode = 0; break;
					case KeyEvent.VK_S: mode = 1; break;
					case KeyEvent.VK_T: mode = 2; break;
					case KeyEvent.VK_L: mode = 3; break;
					case KeyEvent.VK_LEFT:
						switch (mode) {
							case 0: gx--; break;
							case 1: gw--; if (gw < 1) gw = 1; break;
							case 2: tx--; break;
							case 3: layer.setX(layer.getX() - 1); break;
						}
						comp.repaint();
						break;
					case KeyEvent.VK_RIGHT:
						switch (mode) {
							case 0: gx++; break;
							case 1: gw++; break;
							case 2: tx++; break;
							case 3: layer.setX(layer.getX() + 1); break;
						}
						comp.repaint();
						break;
					case KeyEvent.VK_UP:
						switch (mode) {
							case 0: gy--; break;
							case 1: gh--; if (gh < 1) gh = 1; break;
							case 2: ty--; break;
							case 3: layer.setY(layer.getY() - 1); break;
						}
						comp.repaint();
						break;
					case KeyEvent.VK_DOWN:
						switch (mode) {
							case 0: gy++; break;
							case 1: gh++; break;
							case 2: ty++; break;
							case 3: layer.setY(layer.getY() + 1); break;
						}
						comp.repaint();
						break;
					case KeyEvent.VK_CLEAR:
					case KeyEvent.VK_ESCAPE:
						switch (mode) {
							case 0: gx = gy = 0; break;
							case 1: gw = gh = 100; break;
							case 2: tx = ty = 0; break;
							case 3: layer.setLocation(0,0); break;
						}
						comp.repaint();
						break;
				}
			}
			@Override public void keyReleased(KeyEvent e) {}
			@Override public void keyTyped(KeyEvent e) {}
		});
	}
	
	private static class MyComponent extends JComponent {
		private static final long serialVersionUID = 1L;
		@Override
		protected void paintComponent(Graphics g) {
			layer.paint((Graphics2D)g, gx, gy, gw, gh, tx, ty);
		}
	}
}
