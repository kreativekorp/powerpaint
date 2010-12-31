/*
 * Copyright &copy; 2009-2011 Rebecca G. Bettencourt / Kreative Software
 * <p>
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * <a href="http://www.mozilla.org/MPL/">http://www.mozilla.org/MPL/</a>
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Alternatively, the contents of this file may be used under the terms
 * of the GNU Lesser General Public License (the "LGPL License"), in which
 * case the provisions of LGPL License are applicable instead of those
 * above. If you wish to allow use of your version of this file only
 * under the terms of the LGPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the LGPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the LGPL License.
 * @since PowerPaint 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.paint.draw;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import com.kreative.paint.PaintSettings;

public class ImageDrawObject extends AbstractDrawObject {
	private Image img1;
	private BufferedImage img2;
	private RenderableImage img3;
	private RenderedImage img4;
	private BufferedImageOp op;
	private Rectangle ibounds;
	private Rectangle bounds;
	
	public ImageDrawObject(Image img, int x, int y, Color bgcolor) {
		super();
		setFillPaint(bgcolor);
		setDrawPaint(null);
		
		while (img.getHeight(null) < 0);
		while (img.getWidth(null) < 0);
		this.img1 = img;
		this.img2 = null;
		this.img3 = null;
		this.img4 = null;
		this.op = null;
		this.ibounds = new Rectangle(0, 0, img.getWidth(null), img.getHeight(null));
		this.bounds = new Rectangle(x, y, img.getWidth(null), img.getHeight(null));
	}
	
	public ImageDrawObject(Image img, int x, int y) {
		super();
		setFillPaint(null);
		setDrawPaint(null);
		
		while (img.getHeight(null) < 0);
		while (img.getWidth(null) < 0);
		this.img1 = img;
		this.img2 = null;
		this.img3 = null;
		this.img4 = null;
		this.op = null;
		this.ibounds = new Rectangle(0, 0, img.getWidth(null), img.getHeight(null));
		this.bounds = new Rectangle(x, y, img.getWidth(null), img.getHeight(null));
	}
	
	public ImageDrawObject(Image img, int x, int y, int width, int height, Color bgcolor) {
		super();
		setFillPaint(bgcolor);
		setDrawPaint(null);
		
		while (img.getHeight(null) < 0);
		while (img.getWidth(null) < 0);
		this.img1 = img;
		this.img2 = null;
		this.img3 = null;
		this.img4 = null;
		this.op = null;
		this.ibounds = new Rectangle(0, 0, img.getWidth(null), img.getHeight(null));
		this.bounds = new Rectangle(x, y, width, height);
	}
	
	public ImageDrawObject(Image img, int x, int y, int width, int height) {
		super();
		setFillPaint(null);
		setDrawPaint(null);
		
		while (img.getHeight(null) < 0);
		while (img.getWidth(null) < 0);
		this.img1 = img;
		this.img2 = null;
		this.img3 = null;
		this.img4 = null;
		this.op = null;
		this.ibounds = new Rectangle(0, 0, img.getWidth(null), img.getHeight(null));
		this.bounds = new Rectangle(x, y, width, height);
	}
	
	public ImageDrawObject(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor) {
		super();
		setFillPaint(bgcolor);
		setDrawPaint(null);
		
		while (img.getHeight(null) < 0);
		while (img.getWidth(null) < 0);
		this.img1 = img;
		this.img2 = null;
		this.img3 = null;
		this.img4 = null;
		this.op = null;
		this.ibounds = new Rectangle(sx1, sy1, sx2-sx1, sy2-sy1);
		this.bounds = new Rectangle(dx1, dy1, dx2-dx1, sy2-dy1);
	}
	
	public ImageDrawObject(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2) {
		super();
		setFillPaint(null);
		setDrawPaint(null);
		
		while (img.getHeight(null) < 0);
		while (img.getWidth(null) < 0);
		this.img1 = img;
		this.img2 = null;
		this.img3 = null;
		this.img4 = null;
		this.op = null;
		this.ibounds = new Rectangle(sx1, sy1, sx2-sx1, sy2-sy1);
		this.bounds = new Rectangle(dx1, dy1, dx2-dx1, sy2-dy1);
	}
	
	public ImageDrawObject(Image img, AffineTransform tx) {
		super();
		setFillPaint(null);
		setDrawPaint(null);
		setTransform(tx);
		
		while (img.getHeight(null) < 0);
		while (img.getWidth(null) < 0);
		this.img1 = img;
		this.img2 = null;
		this.img3 = null;
		this.img4 = null;
		this.op = null;
		this.ibounds = new Rectangle(0, 0, img.getWidth(null), img.getHeight(null));
		this.bounds = new Rectangle(0, 0, img.getWidth(null), img.getHeight(null));
	}
	
	public ImageDrawObject(BufferedImage img, BufferedImageOp op, int x, int y) {
		super();
		setFillPaint(null);
		setDrawPaint(null);
		
		if (op == null) {
			this.img1 = img;
			this.img2 = null;
			this.img3 = null;
			this.img4 = null;
			this.op = null;
			this.ibounds = new Rectangle(0, 0, img.getWidth(), img.getHeight());
			this.bounds = new Rectangle(x, y, img.getWidth(), img.getHeight());
		} else {
			this.img1 = null;
			this.img2 = img;
			this.img3 = null;
			this.img4 = null;
			this.op = op;
			this.ibounds = new Rectangle(0, 0, img.getWidth(), img.getHeight());
			this.bounds = new Rectangle(x, y, img.getWidth(), img.getHeight());
		}
	}
	
	public ImageDrawObject(RenderableImage img, AffineTransform tx) {
		super();
		setFillPaint(null);
		setDrawPaint(null);
		setTransform(tx);
		
		this.img1 = null;
		this.img2 = null;
		this.img3 = img;
		this.img4 = null;
		this.op = null;
		this.ibounds = new Rectangle(0, 0, (int)Math.ceil(img.getWidth()), (int)Math.ceil(img.getHeight()));
		this.bounds = new Rectangle(0, 0, (int)Math.ceil(img.getWidth()), (int)Math.ceil(img.getHeight()));
	}
	
	public ImageDrawObject(RenderedImage img, AffineTransform tx) {
		super();
		setFillPaint(null);
		setDrawPaint(null);
		setTransform(tx);
		
		this.img1 = null;
		this.img2 = null;
		this.img3 = null;
		this.img4 = img;
		this.op = null;
		this.ibounds = new Rectangle(0, 0, img.getWidth(), img.getHeight());
		this.bounds = new Rectangle(0, 0, img.getWidth(), img.getHeight());
	}
	
	public ImageDrawObject(PaintSettings ps, AffineTransform tx, Rectangle bounds, Rectangle ibounds, Image img) {
		super(ps);
		setTransform(tx);
		this.bounds = bounds;
		this.ibounds = ibounds;
		this.img1 = img;
		this.img2 = null;
		this.img3 = null;
		this.img4 = null;
		this.op = null;
	}
	
	public ImageDrawObject(PaintSettings ps, AffineTransform tx, Rectangle bounds, Rectangle ibounds, BufferedImage img, BufferedImageOp op) {
		super(ps);
		setTransform(tx);
		this.bounds = bounds;
		this.ibounds = ibounds;
		this.img1 = null;
		this.img2 = img;
		this.img3 = null;
		this.img4 = null;
		this.op = op;
	}
	
	public ImageDrawObject(PaintSettings ps, AffineTransform tx, Rectangle bounds, Rectangle ibounds, RenderableImage img) {
		super(ps);
		setTransform(tx);
		this.bounds = bounds;
		this.ibounds = ibounds;
		this.img1 = null;
		this.img2 = null;
		this.img3 = img;
		this.img4 = null;
		this.op = null;
	}
	
	public ImageDrawObject(PaintSettings ps, AffineTransform tx, Rectangle bounds, Rectangle ibounds, RenderedImage img) {
		super(ps);
		setTransform(tx);
		this.bounds = bounds;
		this.ibounds = ibounds;
		this.img1 = null;
		this.img2 = null;
		this.img3 = null;
		this.img4 = img;
		this.op = null;
	}
	
	private ImageDrawObject(ImageDrawObject other) {
		super(other.getPaintSettings());
		if (other.getTransform() != null) setTransform((AffineTransform)other.getTransform().clone());
		this.img1 = other.img1;
		this.img2 = other.img2;
		this.img3 = other.img3;
		this.img4 = other.img4;
		this.op = other.op;
		this.ibounds = (Rectangle)other.ibounds.clone();
		this.bounds = (Rectangle)other.bounds.clone();
	}
	
	public ImageDrawObject clone() {
		return new ImageDrawObject(this);
	}
	
	public Rectangle getBoundingRect() {
		return bounds;
	}
	
	public Rectangle getInnerBoundingRect() {
		return ibounds;
	}
	
	public Image getImage() {
		return img1;
	}
	
	public BufferedImage getBufferedImage() {
		return img2;
	}
	
	public RenderableImage getRenderableImage() {
		return img3;
	}
	
	public RenderedImage getRenderedImage() {
		return img4;
	}
	
	public BufferedImageOp getBufferedImageOp() {
		return op;
	}

	public void paint(Graphics2D g) {
		AffineTransform tr = g.getTransform();
		if (getTransform() != null) g.transform(getTransform());
		if (isFilled()) {
			applyFill(g);
			g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
		}
		g.setComposite(getFillComposite());
		if (img1 != null) g.drawImage(
				img1,
				bounds.x,
				bounds.y,
				bounds.x+bounds.width,
				bounds.y+bounds.height,
				ibounds.x,
				ibounds.y,
				ibounds.x+ibounds.width,
				ibounds.y+ibounds.height,
				null
		);
		if (img2 != null) g.drawImage(img2, op, bounds.x, bounds.y);
		if (img3 != null) g.drawRenderableImage(img3, AffineTransform.getTranslateInstance(bounds.x, bounds.y));
		if (img4 != null) g.drawRenderedImage(img4, AffineTransform.getTranslateInstance(bounds.x, bounds.y));
		if (isDrawn()) {
			applyDraw(g);
			g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
		}
		g.setTransform(tr);
	}

	public void paint(Graphics2D g, int tx, int ty) {
		AffineTransform tr = g.getTransform();
		g.translate(tx, ty);
		if (getTransform() != null) g.transform(getTransform());
		if (isFilled()) {
			applyFill(g);
			g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
		}
		g.setComposite(getFillComposite());
		if (img1 != null) g.drawImage(
				img1,
				bounds.x,
				bounds.y,
				bounds.x+bounds.width,
				bounds.y+bounds.height,
				ibounds.x,
				ibounds.y,
				ibounds.x+ibounds.width,
				ibounds.y+ibounds.height,
				null
		);
		if (img2 != null) g.drawImage(img2, op, bounds.x, bounds.y);
		if (img3 != null) g.drawRenderableImage(img3, AffineTransform.getTranslateInstance(bounds.x, bounds.y));
		if (img4 != null) g.drawRenderedImage(img4, AffineTransform.getTranslateInstance(bounds.x, bounds.y));
		if (isDrawn()) {
			applyDraw(g);
			g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
		}
		g.setTransform(tr);
	}
	
	private Shape getTransformedBounds() {
		int x = bounds.width < 0 ? bounds.x+bounds.width : bounds.x;
		int y = bounds.height < 0 ? bounds.y+bounds.height : bounds.y;
		int w = bounds.width < 0 ? -bounds.width : bounds.width;
		int h = bounds.height < 0 ? -bounds.height : bounds.height;
		Rectangle r = new Rectangle(x, y, w, h);
		AffineTransform tx = getTransform();
		return (tx == null) ? r : tx.createTransformedShape(r);
	}

	public boolean contains(Point2D p) {
		return getTransformedBounds().contains(p);
	}

	public boolean contains(Rectangle2D r) {
		return getTransformedBounds().contains(r);
	}

	public boolean contains(double x, double y) {
		return getTransformedBounds().contains(x, y);
	}

	public boolean contains(double x, double y, double w, double h) {
		return getTransformedBounds().contains(x, y, w, h);
	}

	public Rectangle getBounds() {
		return getTransformedBounds().getBounds();
	}

	public Rectangle2D getBounds2D() {
		return getTransformedBounds().getBounds2D();
	}

	public PathIterator getPathIterator(AffineTransform at) {
		return getTransformedBounds().getPathIterator(at);
	}

	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return getTransformedBounds().getPathIterator(at, flatness);
	}

	public boolean intersects(Rectangle2D r) {
		return getTransformedBounds().intersects(r);
	}

	public boolean intersects(double x, double y, double w, double h) {
		return getTransformedBounds().intersects(x, y, w, h);
	}
	
	public int getControlPointCount() {
		return 9;
	}
	
	protected ControlPoint getControlPointImpl(int i) {
		switch (i) {
		case 0: return new ControlPoint.Float(bounds.x+bounds.width/2.0f, bounds.y+bounds.height/2.0f, ControlPointType.CENTER);
		case 1: return new ControlPoint.Float(bounds.x, bounds.y, ControlPointType.NORTHWEST);
		case 2: return new ControlPoint.Float(bounds.x+bounds.width, bounds.y, ControlPointType.NORTHEAST);
		case 3: return new ControlPoint.Float(bounds.x, bounds.y+bounds.height, ControlPointType.SOUTHWEST);
		case 4: return new ControlPoint.Float(bounds.x+bounds.width, bounds.y+bounds.height, ControlPointType.SOUTHEAST);
		case 5: return new ControlPoint.Float(bounds.x+bounds.width/2.0f, bounds.y, ControlPointType.NORTH);
		case 6: return new ControlPoint.Float(bounds.x+bounds.width/2.0f, bounds.y+bounds.height, ControlPointType.SOUTH);
		case 7: return new ControlPoint.Float(bounds.x, bounds.y+bounds.height/2.0f, ControlPointType.WEST);
		case 8: return new ControlPoint.Float(bounds.x+bounds.width, bounds.y+bounds.height/2.0f, ControlPointType.EAST);
		default: return null;
		}
	}
	
	protected int setControlPointImpl(int i, Point2D p) {
		int x1 = bounds.x, y1 = bounds.y, x2 = bounds.x+bounds.width, y2 = bounds.y+bounds.height;
		switch (i) {
		case 0:
			x1 = (int)(p.getX() - bounds.width/2.0);
			y1 = (int)(p.getY() - bounds.height/2.0);
			x2 = x1+bounds.width;
			y2 = y1+bounds.height;
			break;
		case 1: x1 = (int)p.getX(); y1 = (int)p.getY(); break;
		case 2: x2 = (int)p.getX(); y1 = (int)p.getY(); break;
		case 3: x1 = (int)p.getX(); y2 = (int)p.getY(); break;
		case 4: x2 = (int)p.getX(); y2 = (int)p.getY(); break;
		case 5: y1 = (int)p.getY(); break;
		case 6: y2 = (int)p.getY(); break;
		case 7: x1 = (int)p.getX(); break;
		case 8: x2 = (int)p.getX(); break;
		}
		bounds = new Rectangle(x1, y1, x2-x1, y2-y1);
		return i;
	}
	
	protected Point2D getAnchorImpl() {
		return new Point2D.Float(bounds.x, bounds.y);
	}
	
	protected void setAnchorImpl(Point2D p) {
		bounds.x = (int)p.getX();
		bounds.y = (int)p.getY();
	}
	
	public String toString() {
		return "com.kreative.paint.objects.ImageDrawObject["+img1+","+img2+","+img3+","+img4+","+op+","+ibounds+","+bounds+","+super.toString()+"]";
	}
}
