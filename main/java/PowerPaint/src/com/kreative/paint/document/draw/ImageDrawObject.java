package com.kreative.paint.document.draw;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ImageDrawObject extends DrawObject {
	private BufferedImage image;
	private double x1, y1, x2, y2;
	
	public ImageDrawObject(
		PaintSettings ps,
		BufferedImage image,
		double x, double y,
		double width, double height
	) {
		super(ps);
		this.image = image;
		this.x1 = x;
		this.y1 = y;
		this.x2 = x + width;
		this.y2 = y + width;
	}
	
	private ImageDrawObject(ImageDrawObject o) {
		super(o);
		this.image = o.image;
		this.x1 = o.x1;
		this.y1 = o.y1;
		this.x2 = o.x2;
		this.y2 = o.y2;
	}
	
	@Override
	public ImageDrawObject clone() {
		return new ImageDrawObject(this);
	}
	
	public BufferedImage getImage() { return image; }
	public double getX() { return x1; }
	public double getY() { return y1; }
	public double getWidth() { return x2 - x1; }
	public double getHeight() { return y2 - y1; }
	
	@Override
	protected Shape getBoundaryImpl() {
		return new Rectangle2D.Double(
			Math.min(x1, x2), Math.min(y1, y2),
			Math.abs(x2 - x1), Math.abs(y2 - y1)
		);
	}
	
	@Override
	protected Shape getHitAreaImpl() {
		return new Rectangle2D.Double(
			Math.min(x1, x2), Math.min(y1, y2),
			Math.abs(x2 - x1), Math.abs(y2 - y1)
		);
	}
	
	@Override
	protected Object getControlState() {
		return new double[]{ x1, y1, x2, y2 };
	}
	
	@Override
	protected void setControlState(Object o) {
		double[] state = (double[])o;
		x1 = state[0]; y1 = state[1];
		x2 = state[2]; y2 = state[3];
	}
	
	@Override
	public int getControlPointCount() {
		return 9;
	}
	
	@Override
	protected ControlPoint getControlPointImpl(int i) {
		switch (i) {
			case 0: return new ControlPoint(ControlPointType.CENTER, (x1 + x2) / 2, (y1 + y2) / 2);
			case 1: return new ControlPoint(ControlPointType.NORTHWEST, x1, y1);
			case 2: return new ControlPoint(ControlPointType.NORTHEAST, x2, y1);
			case 3: return new ControlPoint(ControlPointType.SOUTHWEST, x1, y2);
			case 4: return new ControlPoint(ControlPointType.SOUTHEAST, x2, y2);
			case 5: return new ControlPoint(ControlPointType.NORTH, (x1 + x2) / 2, y1);
			case 6: return new ControlPoint(ControlPointType.SOUTH, (x1 + x2) / 2, y2);
			case 7: return new ControlPoint(ControlPointType.WEST, x1, (y1 + y2) / 2);
			case 8: return new ControlPoint(ControlPointType.EAST, x2, (y1 + y2) / 2);
			default: return null;
		}
	}
	
	@Override
	protected List<ControlPoint> getControlPointsImpl() {
		List<ControlPoint> cpts = new ArrayList<ControlPoint>();
		cpts.add(new ControlPoint(ControlPointType.CENTER, (x1 + x2) / 2, (y1 + y2) / 2));
		cpts.add(new ControlPoint(ControlPointType.NORTHWEST, x1, y1));
		cpts.add(new ControlPoint(ControlPointType.NORTHEAST, x2, y1));
		cpts.add(new ControlPoint(ControlPointType.SOUTHWEST, x1, y2));
		cpts.add(new ControlPoint(ControlPointType.SOUTHEAST, x2, y2));
		cpts.add(new ControlPoint(ControlPointType.NORTH, (x1 + x2) / 2, y1));
		cpts.add(new ControlPoint(ControlPointType.SOUTH, (x1 + x2) / 2, y2));
		cpts.add(new ControlPoint(ControlPointType.WEST, x1, (y1 + y2) / 2));
		cpts.add(new ControlPoint(ControlPointType.EAST, x2, (y1 + y2) / 2));
		return cpts;
	}
	
	@Override
	protected Collection<Line2D> getControlLinesImpl() {
		return null;
	}
	
	@Override
	protected int setControlPointImpl(int i, double x, double y) {
		switch (i) {
			case 0:
				double width2 = (x2 - x1) / 2;
				double height2 = (y2 - y1) / 2;
				x1 = x - width2;
				y1 = y - height2;
				x2 = x + width2;
				y2 = y + height2;
				break;
			case 1: x1 = x; y1 = y; break;
			case 2: x2 = x; y1 = y; break;
			case 3: x1 = x; y2 = y; break;
			case 4: x2 = x; y2 = y; break;
			case 5: y1 = y; break;
			case 6: y2 = y; break;
			case 7: x1 = x; break;
			case 8: x2 = x; break;
		}
		return i;
	}
	
	@Override
	protected Point2D getLocationImpl() {
		return new Point2D.Double(x1, y1);
	}
	
	@Override
	protected void setLocationImpl(double x, double y) {
		this.x2 = x + (this.x2 - this.x1);
		this.y2 = y + (this.y2 - this.y1);
		this.x1 = x;
		this.y1 = y;
	}
	
	@Override
	protected void paintImpl(Graphics2D g) {
		Shape ca = new Rectangle2D.Double(
			Math.min(x1, x2), Math.min(y1, y2),
			Math.abs(x2 - x1), Math.abs(y2 - y1)
		);
		
		if (ps.isFilled()) {
			ps.applyFill(g);
			g.fill(ca);
		}
		
		AffineTransform t = g.getTransform();
		g.translate(x1, y1);
		g.scale((x2 - x1) / image.getWidth(), (y2 - y1) / image.getHeight());
		g.drawImage(image, null, 0, 0);
		g.setTransform(t);
		
		if (ps.isDrawn()) {
			ps.applyDraw(g);
			g.draw(ca);
		}
	}
	
	public static ImageDrawObject forGraphicsDrawImage(PaintSettings ps, Image image, int x, int y) {
		return forGraphicsDrawImage(ps, image, x, y, null);
	}
	
	public static ImageDrawObject forGraphicsDrawImage(PaintSettings ps, Image image, int x, int y, Color bgcolor) {
		Dimension d = prepareImage(image); if (d == null) return null;
		BufferedImage i = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
		i.createGraphics().drawImage(image, 0, 0, null);
		return new ImageDrawObject(ps.deriveFillPaint(bgcolor), i, x, y, d.width, d.height);
	}
	
	public static ImageDrawObject forGraphicsDrawImage(PaintSettings ps, Image image, int x, int y, int width, int height) {
		return forGraphicsDrawImage(ps, image, x, y, width, height, null);
	}
	
	public static ImageDrawObject forGraphicsDrawImage(PaintSettings ps, Image image, int x, int y, int width, int height, Color bgcolor) {
		Dimension d = prepareImage(image); if (d == null) return null;
		BufferedImage i = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
		i.createGraphics().drawImage(image, 0, 0, null);
		return new ImageDrawObject(ps.deriveFillPaint(bgcolor), i, x, y, width, height);
	}
	
	public static ImageDrawObject forGraphicsDrawImage(PaintSettings ps, Image image, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2) {
		return forGraphicsDrawImage(ps, image, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
	}
	
	public static ImageDrawObject forGraphicsDrawImage(PaintSettings ps, Image image, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor) {
		if (prepareImage(image) == null) return null;
		int width = Math.abs(sx2 - sx1), height = Math.abs(sy2 - sy1);
		BufferedImage i = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		i.createGraphics().drawImage(image, 0, 0, width, height, sx1, sy1, sx2, sy2, null);
		return new ImageDrawObject(ps.deriveFillPaint(bgcolor), i, dx1, dy1, dx2 - dx1, dy2 - dy1);
	}
	
	public static ImageDrawObject forGraphicsDrawImage(PaintSettings ps, Image image, AffineTransform tx) {
		ImageDrawObject ido = forGraphicsDrawImage(ps, image, 0, 0);
		ido.setTransform(tx);
		return ido;
	}
	
	public static ImageDrawObject forGraphicsDrawImage(PaintSettings ps, BufferedImage image, BufferedImageOp op, int x, int y) {
		Rectangle b = (op != null) ? op.getBounds2D(image).getBounds()
			: new Rectangle(0, 0, image.getWidth(), image.getHeight());
		BufferedImage i = new BufferedImage(b.width, b.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D ig = i.createGraphics();
		ig.drawImage(image, op, -b.x, -b.y);
		ig.dispose();
		return new ImageDrawObject(ps, i, x + b.x, y + b.y, b.width, b.height);
	}
	
	public static ImageDrawObject forGraphicsDrawRenderableImage(PaintSettings ps, RenderableImage image, AffineTransform tx) {
		int width = (int)Math.ceil(image.getWidth());
		int height = (int)Math.ceil(image.getHeight());
		BufferedImage i = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D ig = i.createGraphics();
		ig.drawRenderableImage(image, null);
		ig.dispose();
		ImageDrawObject ido = new ImageDrawObject(ps, i, 0, 0, width, height);
		ido.setTransform(tx);
		return ido;
	}
	
	public static ImageDrawObject forGraphicsDrawRenderedImage(PaintSettings ps, RenderedImage image, AffineTransform tx) {
		int width = image.getWidth();
		int height = image.getHeight();
		BufferedImage i = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D ig = i.createGraphics();
		ig.drawRenderedImage(image, null);
		ig.dispose();
		ImageDrawObject ido = new ImageDrawObject(ps, i, 0, 0, width, height);
		ido.setTransform(tx);
		return ido;
	}
	
	private static Dimension prepareImage(Image image) {
		long startTime = System.currentTimeMillis();
		for (;;) {
			int width = image.getWidth(null);
			int height = image.getHeight(null);
			if (width >= 0 && height >= 0) return new Dimension(width, height);
			long endTime = System.currentTimeMillis();
			if ((endTime - startTime) > 1000) return null;
		}
	}
}
