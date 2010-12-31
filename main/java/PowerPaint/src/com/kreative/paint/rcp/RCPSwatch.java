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

package com.kreative.paint.rcp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Vector;
import javax.imageio.ImageIO;

public class RCPSwatch implements Serializable, List<RCPSwatch> {
	private static final long serialVersionUID = 1L;
	private static Image colorCurs;
	static {
		Class<?> cl = RCPSwatch.class;
		Toolkit tk = Toolkit.getDefaultToolkit();
		colorCurs = tk.createImage(cl.getResource("ColorCursor.png"));
		tk.prepareImage(colorCurs, -1, -1, null);
	}
	
	public static final long INST_TYPE_MASK = 0xFFF0000000000000l;
	public static final long INST_TYPE_SHIFT = 52l;
	public static final int INST_TYPE_EMPTY = 0x000;
	public static final int INST_TYPE_RGB = 0x001;
	public static final int INST_TYPE_HSV = 0x002;
	public static final int INST_TYPE_LAB = 0x003;
	public static final int INST_TYPE_XYZ = 0x004;
	public static final int INST_TYPE_ARGB = 0x005;
	public static final int INST_TYPE_IMAGE = 0x008;
	public static final int INST_TYPE_RGB_SWEEP = 0x009;
	public static final int INST_TYPE_HSV_SWEEP = 0x00A;
	public static final int INST_TYPE_LAB_SWEEP = 0x00B;
	public static final int INST_TYPE_XYZ_SWEEP = 0x00C;
	public static final int INST_TYPE_WRAPPER = 0x800;
	public static final int INST_TYPE_BORDERLAYOUT = 0x801;
	public static final int INST_TYPE_FRAME = 0x802;
	public static final int INST_TYPE_GRID = 0x803;
	public static final int INST_TYPE_DIAGONAL_GRID = 0x804;
	public static final int INST_TYPE_ORIENTATION = 0x805;
	public static final int INST_TYPE_SQUARE_GRID = 0x806;
	public static final int INST_TYPE_SQ_DIAGONAL = 0x807;
	public static final int INST_TYPE_HORIZ_DIVIDER = 0x808;
	public static final int INST_TYPE_VERT_DIVIDER = 0x809;
	public static final int INST_TYPE_NAME = 0xFE0;
	public static final int INST_TYPE_SIZE = 0xFE1;
	public static final int INST_TYPE_SIZE_HORIZ = 0xFE2;
	public static final int INST_TYPE_SIZE_SQUARE = 0xFE3;
	public static final int INST_TYPE_SIZE_VERT = 0xFE4;
	public static final int INST_TYPE_SPACER = 0xFFA;
	public static final int INST_TYPE_HORIZ_SPACER = 0xFFB;
	public static final int INST_TYPE_VERT_SPACER = 0xFFC;
	public static final int INST_TYPE_1PX_WIDTH = 0xFFD;
	public static final int INST_TYPE_1PX_HEIGHT = 0xFFE;
	public static final int INST_TYPE_NO_OP = 0xFFF;

	public static final long INST_BORDER_MASK = 0x000F000000000000l;
	public static final long INST_BORDER_SHIFT = 48l;
	public static final int INST_BORDER_NO_TOP = 0x0001;
	public static final int INST_BORDER_NO_LEFT = 0x0002;
	public static final int INST_BORDER_NO_BOTTOM = 0x0004;
	public static final int INST_BORDER_NO_RIGHT = 0x0008;
	public static final int INST_BORDER_NO_DTR = 0x0001;
	public static final int INST_BORDER_NO_DTL = 0x0002;
	public static final int INST_BORDER_NO_DBL = 0x0004;
	public static final int INST_BORDER_NO_DBR = 0x0008;
	public static final int INST_BORDER_TLBR = 0x0000;
	public static final int INST_BORDER_LBR = 0x0001;
	public static final int INST_BORDER_TBR = 0x0002;
	public static final int INST_BORDER_BR = 0x0003;
	public static final int INST_BORDER_TLR = 0x0004;
	public static final int INST_BORDER_LR = 0x0005;
	public static final int INST_BORDER_TR = 0x0006;
	public static final int INST_BORDER_R = 0x0007;
	public static final int INST_BORDER_TLB = 0x0008;
	public static final int INST_BORDER_LB = 0x0009;
	public static final int INST_BORDER_TB = 0x000A;
	public static final int INST_BORDER_B = 0x000B;
	public static final int INST_BORDER_TL = 0x000C;
	public static final int INST_BORDER_L = 0x000D;
	public static final int INST_BORDER_T = 0x000E;
	public static final int INST_BORDER_NONE = 0x000F;
	
	public static final Dimension DEFAULT_HORIZ_SIZE = new Dimension(289, 73);
	public static final Dimension DEFAULT_SQUARE_SIZE = new Dimension(145, 145);
	public static final Dimension DEFAULT_VERT_SIZE = new Dimension(73, 289);
	
	private long instruction = 0;
	private Vector<RCPSwatch> children = null;
	private Image image = null;
	private String name = null;
	private Dimension prefSize = null;
	private Dimension prefHSize = null;
	private Dimension prefSSize = null;
	private Dimension prefVSize = null;
	
	public RCPSwatch(long[] data) {
		this(new LongArrayIterator(data));
	}
	
	public RCPSwatch(byte[] data) {
		this(new ByteArrayIterator(data));
	}
	
	public RCPSwatch(File f) throws IOException {
		this(new LongStreamIterator(new DataInputStream(new FileInputStream(f))));
	}
	
	public RCPSwatch(InputStream in) throws IOException {
		this(new LongStreamIterator(new DataInputStream(in)));
	}
	
	public RCPSwatch(DataInputStream in) throws IOException {
		this(new LongStreamIterator(in));
	}
	
	public RCPSwatch(Iterator<Long> iter) {
		while (iter.hasNext()) {
			long i = iter.next();
			int op = (int)((i & INST_TYPE_MASK) >>> INST_TYPE_SHIFT);
			int x = (int)((i >>> 32l) & 0xFFFFl);
			int y = (int)((i >>> 16l) & 0xFFFFl);
			int b1 = (int)((i >>> 40l) & 0xFFl);
			int b2 = (int)((i >>> 32l) & 0xFFl);
			int b3 = (int)((i >>> 24l) & 0xFFl);
			int b4 = (int)((i >>> 16l) & 0xFFl);
			int b5 = (int)((i >>>  8l) & 0xFFl);
			int b6 = (int)((i >>>  0l) & 0xFFl);
			switch (op) {
			case INST_TYPE_NAME:
				byte[] namebytes = longsToBytes(iter, (int)(i & 0x7FFFFFFF));
				try {
					name = new String(namebytes, "UTF-8");
				} catch (UnsupportedEncodingException uee) {
					name = new String(namebytes);
				}
				break;
			case INST_TYPE_SIZE:
				prefSize = new Dimension(x,y);
				break;
			case INST_TYPE_SIZE_HORIZ:
				prefHSize = new Dimension(x,y);
				break;
			case INST_TYPE_SIZE_SQUARE:
				prefSSize = new Dimension(x,y);
				break;
			case INST_TYPE_SIZE_VERT:
				prefVSize = new Dimension(x,y);
				break;
			case INST_TYPE_NO_OP:
				// skip it
				break;
			default:
				instruction = i;
				switch (op) {
				case INST_TYPE_IMAGE:
					image = Toolkit.getDefaultToolkit().createImage(longsToBytes(iter, (int)(i & 0x7FFFFFFF)));
					break;
				case INST_TYPE_WRAPPER:
					children = new Vector<RCPSwatch>();
					children.add(new RCPSwatch(iter));
					break;
				case INST_TYPE_BORDERLAYOUT:
					children = new Vector<RCPSwatch>();
					for (int j=0; j<5; j++) children.add(new RCPSwatch(iter));
					break;
				case INST_TYPE_FRAME:
					children = new Vector<RCPSwatch>();
					for (int j=0; j<(x+x+y+y-4+1); j++) children.add(new RCPSwatch(iter));
					break;
				case INST_TYPE_GRID:
				case INST_TYPE_SQUARE_GRID:
					children = new Vector<RCPSwatch>();
					for (int j=0; j<(x*y); j++) children.add(new RCPSwatch(iter));
					break;
				case INST_TYPE_DIAGONAL_GRID:
				case INST_TYPE_SQ_DIAGONAL:
					children = new Vector<RCPSwatch>();
					for (int j=0; j<(((x+2)/2)*(y+1)); j++) children.add(new RCPSwatch(iter));
					break;
				case INST_TYPE_ORIENTATION:
					children = new Vector<RCPSwatch>();
					for (int j=0; j<3; j++) children.add(new RCPSwatch(iter));
					break;
				case INST_TYPE_HORIZ_DIVIDER:
				case INST_TYPE_VERT_DIVIDER:
					children = new Vector<RCPSwatch>();
					if (b1 != 0) children.add(new RCPSwatch(iter));
					if (b2 != 0) children.add(new RCPSwatch(iter));
					if (b3 != 0) children.add(new RCPSwatch(iter));
					if (b4 != 0) children.add(new RCPSwatch(iter));
					if (b5 != 0) children.add(new RCPSwatch(iter));
					if (b1+b2+b3+b4+b5 < b6) children.add(new RCPSwatch(iter));
					break;
				default:
					// no data
					break;
				}
				return;
			}
		}
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Dimension getPreferredSize() {
		return prefSize;
	}
	
	public void setPreferredSize(Dimension d) {
		this.prefSize = (Dimension)d.clone();
	}
	
	public Dimension getPreferredHorizontalSize() {
		return prefHSize;
	}
	
	public void setPreferredHorizontalSize(Dimension d) {
		this.prefHSize = (Dimension)d.clone();
	}
	
	public Dimension getPreferredSquareSize() {
		return prefSSize;
	}
	
	public void setPreferredSquareSize(Dimension d) {
		this.prefSSize = (Dimension)d.clone();
	}
	
	public Dimension getPreferredVerticalSize() {
		return prefVSize;
	}
	
	public void setPreferredVerticalSize(Dimension d) {
		this.prefVSize = (Dimension)d.clone();
	}
	
	public long getInstruction() {
		return instruction;
	}
	
	public void setInstruction(long inst) {
		this.instruction = inst;
	}
	
	public Image getImage() {
		return image;
	}
	
	public void setImage(Image i) {
		this.image = i;
	}
	
	public List<RCPSwatch> getChildren() {
		return children;
	}
	
	public void setChildren(Collection<? extends RCPSwatch> children) {
		this.children = new Vector<RCPSwatch>();
		this.children.addAll(children);
	}

	public boolean add(RCPSwatch o) {
		if (children == null) {
			children = new Vector<RCPSwatch>();
		}
		return children.add(o);
	}

	public void add(int index, RCPSwatch element) {
		if (children == null) {
			if (index != 0) {
				throw new IndexOutOfBoundsException(Integer.toString(index));
			} else {
				children = new Vector<RCPSwatch>();
			}
		}
		children.add(index, element);
	}

	public boolean addAll(Collection<? extends RCPSwatch> c) {
		if (children == null) {
			children = new Vector<RCPSwatch>();
		}
		return children.addAll(c);
	}

	public boolean addAll(int index, Collection<? extends RCPSwatch> c) {
		if (children == null) {
			if (index != 0) {
				throw new IndexOutOfBoundsException(Integer.toString(index));
			} else {
				children = new Vector<RCPSwatch>();
			}
		}
		return children.addAll(index, c);
	}

	public void clear() {
		children = null;
	}

	public boolean contains(Object o) {
		return (children != null) && children.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return (children != null) && children.containsAll(c);
	}

	public RCPSwatch get(int index) {
		if (children == null) {
			throw new IndexOutOfBoundsException(Integer.toString(index));
		} else {
			return children.get(index);
		}
	}

	public int indexOf(Object o) {
		if (children == null) {
			return -1;
		} else {
			return children.indexOf(o);
		}
	}

	public boolean isEmpty() {
		if (children == null) {
			return true;
		} else {
			return children.isEmpty();
		}
	}

	public Iterator<RCPSwatch> iterator() {
		if (children == null) {
			return new Iterator<RCPSwatch>() {
				public boolean hasNext() {
					return false;
				}
				public RCPSwatch next() {
					throw new NoSuchElementException();
				}
				public void remove() {
					throw new IllegalStateException();
				}
			};
		} else {
			return children.iterator();
		}
	}

	public int lastIndexOf(Object o) {
		if (children == null) {
			return -1;
		} else {
			return children.lastIndexOf(o);
		}
	}

	public ListIterator<RCPSwatch> listIterator() {
		if (children == null) {
			children = new Vector<RCPSwatch>();
		}
		return children.listIterator();
	}

	public ListIterator<RCPSwatch> listIterator(int index) {
		if (children == null) {
			children = new Vector<RCPSwatch>();
		}
		return children.listIterator(index);
	}

	public boolean remove(Object o) {
		if (children == null) {
			return false;
		} else {
			return children.remove(o);
		}
	}

	public RCPSwatch remove(int index) {
		if (children == null) {
			throw new IndexOutOfBoundsException(Integer.toString(index));
		} else {
			return children.remove(index);
		}
	}

	public boolean removeAll(Collection<?> c) {
		if (children == null) {
			return false;
		} else {
			return children.removeAll(c);
		}
	}

	public boolean retainAll(Collection<?> c) {
		if (children == null) {
			return false;
		} else {
			return children.retainAll(c);
		}
	}

	public RCPSwatch set(int index, RCPSwatch element) {
		if (children == null) {
			throw new IndexOutOfBoundsException(Integer.toString(index));
		} else {
			return children.set(index, element);
		}
	}

	public int size() {
		if (children == null) {
			return 0;
		} else {
			return children.size();
		}
	}

	public List<RCPSwatch> subList(int fromIndex, int toIndex) {
		if (children == null) {
			children = new Vector<RCPSwatch>();
		}
		return children.subList(fromIndex, toIndex);
	}

	public Object[] toArray() {
		if (children == null) {
			return new Object[0];
		} else {
			return children.toArray();
		}
	}

	public <T> T[] toArray(T[] a) {
		if (children == null) {
			return new Vector<RCPSwatch>().toArray(a);
		} else {
			return children.toArray(a);
		}
	}
	
	public void write(DataOutput out) throws IOException {
		if (prefSize != null) {
			out.writeLong(((long)INST_TYPE_SIZE << (long)INST_TYPE_SHIFT) | ((long)prefSize.width << 32L) | ((long)prefSize.height << 16L));
		}
		if (prefHSize != null) {
			out.writeLong(((long)INST_TYPE_SIZE_HORIZ << (long)INST_TYPE_SHIFT) | ((long)prefHSize.width << 32L) | ((long)prefHSize.height << 16L));
		}
		if (prefSSize != null) {
			out.writeLong(((long)INST_TYPE_SIZE_SQUARE << (long)INST_TYPE_SHIFT) | ((long)prefSSize.width << 32L) | ((long)prefSSize.height << 16L));
		}
		if (prefVSize != null) {
			out.writeLong(((long)INST_TYPE_SIZE_VERT << (long)INST_TYPE_SHIFT) | ((long)prefVSize.width << 32L) | ((long)prefVSize.height << 16L));
		}
		if (name != null) {
			byte[] nameBytes = name.getBytes("UTF-8");
			out.writeLong(((long)INST_TYPE_NAME << (long)INST_TYPE_SHIFT) | (long)nameBytes.length);
			out.write(nameBytes);
			if ((nameBytes.length % 8) != 0) out.write(new byte[8 - (nameBytes.length % 8)]);
		}
		int op = (int)((instruction & INST_TYPE_MASK) >>> INST_TYPE_SHIFT);
		if (op == INST_TYPE_IMAGE) {
			if (image == null) {
				instruction &=~ 0x0000FFFFFFFFFFFFL;
				out.writeLong(instruction);
			} else {
				while (!Toolkit.getDefaultToolkit().prepareImage(image, -1, -1, null));
				while (image.getWidth(null) < 0);
				while (image.getHeight(null) < 0);
				BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = bi.createGraphics();
				while (!g.drawImage(image, 0, 0, null));
				g.dispose();
				ByteArrayOutputStream iout = new ByteArrayOutputStream();
				ImageIO.write(bi, "png", iout);
				iout.flush();
				iout.close();
				byte[] idata = iout.toByteArray();
				instruction &=~ 0x0000FFFFFFFFFFFFL;
				instruction |= (long)idata.length;
				out.writeLong(instruction);
				out.write(idata);
				if ((idata.length % 8) != 0) out.write(new byte[8 - (idata.length % 8)]);
			}
		} else {
			out.writeLong(instruction);
		}
		int x = (int)((instruction >>> 32l) & 0xFFFFl);
		int y = (int)((instruction >>> 16l) & 0xFFFFl);
		int b1 = (int)((instruction >>> 40l) & 0xFFl);
		int b2 = (int)((instruction >>> 32l) & 0xFFl);
		int b3 = (int)((instruction >>> 24l) & 0xFFl);
		int b4 = (int)((instruction >>> 16l) & 0xFFl);
		int b5 = (int)((instruction >>>  8l) & 0xFFl);
		int b6 = (int)((instruction >>>  0l) & 0xFFl);
		int nc;
		switch (op) {
		case INST_TYPE_WRAPPER:
			nc = 1;
			break;
		case INST_TYPE_BORDERLAYOUT:
			nc = 5;
			break;
		case INST_TYPE_FRAME:
			nc = (x+x+y+y-4+1);
			break;
		case INST_TYPE_GRID:
		case INST_TYPE_SQUARE_GRID:
			nc = (x*y);
			break;
		case INST_TYPE_DIAGONAL_GRID:
		case INST_TYPE_SQ_DIAGONAL:
			nc = (((x+2)/2)*(y+1));
			break;
		case INST_TYPE_ORIENTATION:
			nc = 3;
			break;
		case INST_TYPE_HORIZ_DIVIDER:
		case INST_TYPE_VERT_DIVIDER:
			nc = 0;
			if (b1 != 0) nc++;
			if (b2 != 0) nc++;
			if (b3 != 0) nc++;
			if (b4 != 0) nc++;
			if (b5 != 0) nc++;
			if (b1+b2+b3+b4+b5 < b6) nc++;
			break;
		default:
			nc = 0;
			break;
		}
		if (children == null) {
			for (int i = 0; i < nc; i++) {
				out.writeLong(0l);
			}
		} else {
			for (int i = 0; i < children.size() && i < nc; i++) {
				children.get(i).write(out);
			}
			for (int i = children.size(); i < nc; i++) {
				out.writeLong(0l);
			}
		}
	}
	
	public Color getColor(Rectangle r, Point p) {
		if (!r.contains(p)) return null;
		int n1 = (int)((instruction >>> 44l) & 0xFl);
		int n2 = (int)((instruction >>> 40l) & 0xFl);
		int n3 = (int)((instruction >>> 36l) & 0xFl);
		int n4 = (int)((instruction >>> 32l) & 0xFl);
		int b1 = (int)((instruction >>> 40l) & 0xFFl);
		int b2 = (int)((instruction >>> 32l) & 0xFFl);
		int b3 = (int)((instruction >>> 24l) & 0xFFl);
		int b4 = (int)((instruction >>> 16l) & 0xFFl);
		int b5 = (int)((instruction >>>  8l) & 0xFFl);
		int b6 = (int)((instruction >>>  0l) & 0xFFl);
		int s1 = (int)((instruction >>> 32l) & 0xFFFFl);
		int s2 = (int)((instruction >>> 16l) & 0xFFFFl);
		int s3 = (int)((instruction >>>  0l) & 0xFFFFl);
		float c1, c2, c3, yv, xv, w, h;
		int north, west, east, south, x, y, cw, ch, cx, cy;
		int[] coords;
		Rectangle cr;
		switch ((int)((instruction & INST_TYPE_MASK) >>> INST_TYPE_SHIFT)) {
		case INST_TYPE_RGB:
			return new Color(s1/65535.0f, s2/65535.0f, s3/65535.0f);
		case INST_TYPE_HSV:
			return Color.getHSBColor(s1/65535.0f, s2/65535.0f, s3/65535.0f);
		case INST_TYPE_LAB:
			return null;
		case INST_TYPE_XYZ:
			return null;
		case INST_TYPE_ARGB:
			return new Color(b2/255.0f, b3/255.0f, b4/255.0f, b1/255.0f);
		case INST_TYPE_IMAGE:
			return null;
		case INST_TYPE_RGB_SWEEP:
			c1 = c2 = c3 = b6/255.0f;
			yv = ((float)(p.y - r.y) / (float)r.height);
			xv = ((float)(p.x - r.x) / (float)r.width);
			yv = (b5/255.0f)*yv + (b4/255.0f)*(1.0f-yv);
			xv = (b3/255.0f)*xv + (b2/255.0f)*(1.0f-xv);
			switch (n2) {
			case 0: c1 = yv; break;
			case 1: c2 = yv; break;
			case 2: c3 = yv; break;
			}
			switch (n1) {
			case 0: c1 = xv; break;
			case 1: c2 = xv; break;
			case 2: c3 = xv; break;
			}
			return new Color(c1, c2, c3);
		case INST_TYPE_HSV_SWEEP:
			c1 = c2 = c3 = b6/255.0f;
			yv = ((float)(p.y - r.y) / (float)r.height);
			xv = ((float)(p.x - r.x) / (float)r.width);
			yv = (b5/255.0f)*yv + (b4/255.0f)*(1.0f-yv);
			xv = (b3/255.0f)*xv + (b2/255.0f)*(1.0f-xv);
			switch (n2) {
			case 0: c1 = yv; break;
			case 1: c2 = yv; break;
			case 2: c3 = yv; break;
			}
			switch (n1) {
			case 0: c1 = xv; break;
			case 1: c2 = xv; break;
			case 2: c3 = xv; break;
			}
			return Color.getHSBColor(c1, c2, c3);
		case INST_TYPE_LAB_SWEEP:
			return null;
		case INST_TYPE_XYZ_SWEEP:
			return null;
		case INST_TYPE_WRAPPER:
			return children.get(0).getColor(r,p);
		case INST_TYPE_BORDERLAYOUT:
			north = ((n1 != 0) && (b3 != 0)) ? (((r.height-1) * n1) / b3) : 0;
			west  = ((n2 != 0) && (b4 != 0)) ? (((r.width -1) * n2) / b4) : 0;
			east  = ((n3 != 0) && (b5 != 0)) ? (((r.width -1) * n3) / b5) : 0;
			south = ((n4 != 0) && (b6 != 0)) ? (((r.height-1) * n4) / b6) : 0;
			if ((north != 0) && (p.y < r.y+north)) {
				cr = new Rectangle(r.x, r.y, r.width, north+1);
				return children.get(0).getColor(cr, p);
			}
			else if ((south != 0) && (p.y >= r.y+r.height-south)) {
				cr = new Rectangle(r.x, r.y+r.height-south-1, r.width, south+1);
				return children.get(3).getColor(cr, p);
			}
			else if ((west != 0) && (p.x < r.x+west)) {
				cr = new Rectangle(r.x, r.y+north, west+1, r.height-north-south);
				return children.get(1).getColor(cr, p);
			}
			else if ((east != 0) && (p.x >= r.x+r.width-east)) {
				cr = new Rectangle(r.x+r.width-east-1, r.y+north, east+1, r.height-north-south);
				return children.get(2).getColor(cr, p);
			}
			else {
				cr = new Rectangle(r.x+west, r.y+north, r.width-west-east, r.height-north-south);
				return children.get(4).getColor(cr, p);
			}
		case INST_TYPE_FRAME:
			w = (float)(r.width-1) / (float)s1;
			h = (float)(r.height-1) / (float)s2;
			x = (int)Math.floor((float)(p.x-r.x)*(float)s1/(float)r.width);
			y = (int)Math.floor((float)(p.y-r.y)*(float)s2/(float)r.height);
			if (x < 0) x = 0; else if (x >= s1) x = s1-1;
			if (y < 0) y = 0; else if (y >= s2) y = s2-1;
			cw = (int)Math.floor(w*(x+1)) - (int)Math.floor(w*x);
			ch = (int)Math.floor(h*(y+1)) - (int)Math.floor(h*y);
			cx = (int)Math.floor(w*x);
			cy = (int)Math.floor(h*y);
			cr = new Rectangle(r.x+cx, r.y+cy, cw+1, ch+1);
			if (y <= 0) {
				return children.get(x).getColor(cr, p);
			}
			else if (x >= s1-1) {
				return children.get(s1-1 + y).getColor(cr, p);
			}
			else if (y >= s2-1) {
				return children.get(s1+s2-2 + ((s1-1)-x)).getColor(cr, p);
			}
			else if (x <= 0) {
				return children.get(s1+s2+s1-3 + ((s2-1)-y)).getColor(cr, p);
			}
			else {
				cw = (int)Math.floor(w*(s1-1)) - (int)Math.floor(w);
				ch = (int)Math.floor(h*(s2-1)) - (int)Math.floor(h);
				cx = (int)Math.floor(w);
				cy = (int)Math.floor(h);
				cr = new Rectangle(r.x+cx, r.y+cy, cw+1, ch+1);
				return children.get(s1+s2+s1+s2-4).getColor(cr, p);
			}
		case INST_TYPE_GRID:
			w = (float)(r.width-1) / (float)s1;
			h = (float)(r.height-1) / (float)s2;
			x = (int)Math.floor((float)(p.x-r.x)*(float)s1/(float)r.width);
			y = (int)Math.floor((float)(p.y-r.y)*(float)s2/(float)r.height);
			if (x < 0) x = 0; else if (x >= s1) x = s1-1;
			if (y < 0) y = 0; else if (y >= s2) y = s2-1;
			cw = (int)Math.floor(w*(x+1)) - (int)Math.floor(w*x);
			ch = (int)Math.floor(h*(y+1)) - (int)Math.floor(h*y);
			cx = (int)Math.floor(w*x);
			cy = (int)Math.floor(h*y);
			cr = new Rectangle(r.x+cx, r.y+cy, cw+1, ch+1);
			return children.get(y*s1+x).getColor(cr, p);
		case INST_TYPE_DIAGONAL_GRID:
			w = (float)(r.width-1) / (float)s1;
			h = (float)(r.height-1) / (float)s2;
			x = (int)Math.floor((float)(p.x-r.x)*(float)s1/(float)r.width);
			y = (int)Math.floor((float)(p.y-r.y)*(float)s2/(float)r.height);
			if (x < 0) x = 0; else if (x >= s1) x = s1-1;
			if (y < 0) y = 0; else if (y >= s2) y = s2-1;
			cw = (int)Math.floor(w*(x+1)) - (int)Math.floor(w*x);
			ch = (int)Math.floor(h*(y+1)) - (int)Math.floor(h*y);
			cx = (int)Math.floor(w*x);
			cy = (int)Math.floor(h*y);
			cr = new Rectangle(r.x+cx, r.y+cy, cw+1, ch+1);
			{
				int i1 = ((s1+2)/2)*(y+((x+y)%2))+(x/2);
				int i2 = ((s1+2)/2)*(y+((x+y+1)%2))+((x+1)/2);
				boolean tlbr = (((x+y)%2)==1);
				long sw1 = children.get(i1).instruction;
				long sw2 = children.get(i2).instruction;
				return getColorDiagonal(cr, p, tlbr, sw1, sw2);
			}
		case INST_TYPE_ORIENTATION:
			if (r.width > r.height+r.height/2) {
				return children.get(0).getColor(r,p);
			}
			else if (r.height > r.width+r.width/2) {
				return children.get(2).getColor(r,p);
			}
			else {
				return children.get(1).getColor(r,p);
			}
		case INST_TYPE_SQUARE_GRID:
			w = (float)(r.width-1) / (float)s1;
			h = (float)(r.height-1) / (float)s2;
			if (w < h) {
				h = w;
				r.y += (r.height-(int)(h*s2+1))/2;
				r.height = (int)(h*s2+1);
			}
			else if (h < w) {
				w = h;
				r.x += (r.width-(int)(w*s1+1))/2;
				r.width = (int)(w*s1+1);
			}
			if (!r.contains(p)) return null;
			x = (int)Math.floor((float)(p.x-r.x)*(float)s1/(float)r.width);
			y = (int)Math.floor((float)(p.y-r.y)*(float)s2/(float)r.height);
			if (x < 0) x = 0; else if (x >= s1) x = s1-1;
			if (y < 0) y = 0; else if (y >= s2) y = s2-1;
			cw = (int)Math.floor(w*(x+1)) - (int)Math.floor(w*x);
			ch = (int)Math.floor(h*(y+1)) - (int)Math.floor(h*y);
			cx = (int)Math.floor(w*x);
			cy = (int)Math.floor(h*y);
			cr = new Rectangle(r.x+cx, r.y+cy, cw+1, ch+1);
			return children.get(y*s1+x).getColor(cr, p);
		case INST_TYPE_SQ_DIAGONAL:
			w = (float)Math.floor((float)(r.width-1) / (float)s1);
			h = (float)Math.floor((float)(r.height-1) / (float)s2);
			if (w < h) h = w;
			else if (h < w) w = h;
			r.x += (r.width-(int)(w*s1+1))/2;
			r.y += (r.height-(int)(h*s2+1))/2;
			r.width = (int)(w*s1+1);
			r.height = (int)(h*s2+1);
			if (!r.contains(p)) return null;
			x = (int)Math.floor((float)(p.x-r.x)*(float)s1/(float)r.width);
			y = (int)Math.floor((float)(p.y-r.y)*(float)s2/(float)r.height);
			if (x < 0) x = 0; else if (x >= s1) x = s1-1;
			if (y < 0) y = 0; else if (y >= s2) y = s2-1;
			cw = (int)Math.floor(w*(x+1)) - (int)Math.floor(w*x);
			ch = (int)Math.floor(h*(y+1)) - (int)Math.floor(h*y);
			cx = (int)Math.floor(w*x);
			cy = (int)Math.floor(h*y);
			cr = new Rectangle(r.x+cx, r.y+cy, cw+1, ch+1);
			{
				int i1 = ((s1+2)/2)*(y+((x+y)%2))+(x/2);
				int i2 = ((s1+2)/2)*(y+((x+y+1)%2))+((x+1)/2);
				boolean tlbr = (((x+y)%2)==1);
				long sw1 = children.get(i1).instruction;
				long sw2 = children.get(i2).instruction;
				return getColorDiagonal(cr, p, tlbr, sw1, sw2);
			}
			case INST_TYPE_HORIZ_DIVIDER:
				coords = getDividerCoords(r.width-1, b1, b2, b3, b4, b5, b6);
				for (int i = 0; i < children.size(); i++) {
					if (p.x >= r.x+coords[i] && p.x <= r.x+coords[i+1]) {
						cr = new Rectangle(r.x+coords[i], r.y, coords[i+1]-coords[i]+1, r.height);
						return children.get(i).getColor(cr, p);
					}
					
				}
				return null;
			case INST_TYPE_VERT_DIVIDER:
				coords = getDividerCoords(r.height-1, b1, b2, b3, b4, b5, b6);
				for (int i = 0; i < children.size(); i++) {
					if (p.y >= r.y+coords[i] && p.y <= r.y+coords[i+1]) {
						cr = new Rectangle(r.x, r.y+coords[i], r.width, coords[i+1]-coords[i]+1);
						return children.get(i).getColor(cr, p);
					}
				}
				return null;
		default:
			return null;
		}
	}
	
	public String getName(Rectangle r, Point p) {
		if (!r.contains(p)) return null;
		int n1 = (int)((instruction >>> 44l) & 0xFl);
		int n2 = (int)((instruction >>> 40l) & 0xFl);
		int n3 = (int)((instruction >>> 36l) & 0xFl);
		int n4 = (int)((instruction >>> 32l) & 0xFl);
		int b1 = (int)((instruction >>> 40l) & 0xFFl);
		int b2 = (int)((instruction >>> 32l) & 0xFFl);
		int b3 = (int)((instruction >>> 24l) & 0xFFl);
		int b4 = (int)((instruction >>> 16l) & 0xFFl);
		int b5 = (int)((instruction >>>  8l) & 0xFFl);
		int b6 = (int)((instruction >>>  0l) & 0xFFl);
		int s1 = (int)((instruction >>> 32l) & 0xFFFFl);
		int s2 = (int)((instruction >>> 16l) & 0xFFFFl);
		float w, h;
		int north, west, east, south, x, y, cw, ch, cx, cy;
		int[] coords;
		Rectangle cr;
		switch ((int)((instruction & INST_TYPE_MASK) >>> INST_TYPE_SHIFT)) {
		case INST_TYPE_WRAPPER:
			return children.get(0).getName(r,p);
		case INST_TYPE_BORDERLAYOUT:
			north = ((n1 != 0) && (b3 != 0)) ? (((r.height-1) * n1) / b3) : 0;
			west  = ((n2 != 0) && (b4 != 0)) ? (((r.width -1) * n2) / b4) : 0;
			east  = ((n3 != 0) && (b5 != 0)) ? (((r.width -1) * n3) / b5) : 0;
			south = ((n4 != 0) && (b6 != 0)) ? (((r.height-1) * n4) / b6) : 0;
			if ((north != 0) && (p.y < r.y+north)) {
				cr = new Rectangle(r.x, r.y, r.width, north+1);
				return children.get(0).getName(cr, p);
			}
			else if ((south != 0) && (p.y >= r.y+r.height-south)) {
				cr = new Rectangle(r.x, r.y+r.height-south-1, r.width, south+1);
				return children.get(3).getName(cr, p);
			}
			else if ((west != 0) && (p.x < r.x+west)) {
				cr = new Rectangle(r.x, r.y+north, west+1, r.height-north-south);
				return children.get(1).getName(cr, p);
			}
			else if ((east != 0) && (p.x >= r.x+r.width-east)) {
				cr = new Rectangle(r.x+r.width-east-1, r.y+north, east+1, r.height-north-south);
				return children.get(2).getName(cr, p);
			}
			else {
				cr = new Rectangle(r.x+west, r.y+north, r.width-west-east, r.height-north-south);
				return children.get(4).getName(cr, p);
			}
		case INST_TYPE_FRAME:
			w = (float)(r.width-1) / (float)s1;
			h = (float)(r.height-1) / (float)s2;
			x = (int)Math.floor((float)(p.x-r.x)*(float)s1/(float)r.width);
			y = (int)Math.floor((float)(p.y-r.y)*(float)s2/(float)r.height);
			if (x < 0) x = 0; else if (x >= s1) x = s1-1;
			if (y < 0) y = 0; else if (y >= s2) y = s2-1;
			cw = (int)Math.floor(w*(x+1)) - (int)Math.floor(w*x);
			ch = (int)Math.floor(h*(y+1)) - (int)Math.floor(h*y);
			cx = (int)Math.floor(w*x);
			cy = (int)Math.floor(h*y);
			cr = new Rectangle(r.x+cx, r.y+cy, cw+1, ch+1);
			if (y <= 0) {
				return children.get(x).getName(cr, p);
			}
			else if (x >= s1-1) {
				return children.get(s1-1 + y).getName(cr, p);
			}
			else if (y >= s2-1) {
				return children.get(s1+s2-2 + ((s1-1)-x)).getName(cr, p);
			}
			else if (x <= 0) {
				return children.get(s1+s2+s1-3 + ((s2-1)-y)).getName(cr, p);
			}
			else {
				cw = (int)Math.floor(w*(s1-1)) - (int)Math.floor(w);
				ch = (int)Math.floor(h*(s2-1)) - (int)Math.floor(h);
				cx = (int)Math.floor(w);
				cy = (int)Math.floor(h);
				cr = new Rectangle(r.x+cx, r.y+cy, cw+1, ch+1);
				return children.get(s1+s2+s1+s2-4).getName(cr, p);
			}
		case INST_TYPE_GRID:
			w = (float)(r.width-1) / (float)s1;
			h = (float)(r.height-1) / (float)s2;
			x = (int)Math.floor((float)(p.x-r.x)*(float)s1/(float)r.width);
			y = (int)Math.floor((float)(p.y-r.y)*(float)s2/(float)r.height);
			if (x < 0) x = 0; else if (x >= s1) x = s1-1;
			if (y < 0) y = 0; else if (y >= s2) y = s2-1;
			cw = (int)Math.floor(w*(x+1)) - (int)Math.floor(w*x);
			ch = (int)Math.floor(h*(y+1)) - (int)Math.floor(h*y);
			cx = (int)Math.floor(w*x);
			cy = (int)Math.floor(h*y);
			cr = new Rectangle(r.x+cx, r.y+cy, cw+1, ch+1);
			return children.get(y*s1+x).getName(cr, p);
		case INST_TYPE_DIAGONAL_GRID:
			w = (float)(r.width-1) / (float)s1;
			h = (float)(r.height-1) / (float)s2;
			x = (int)Math.floor((float)(p.x-r.x)*(float)s1/(float)r.width);
			y = (int)Math.floor((float)(p.y-r.y)*(float)s2/(float)r.height);
			if (x < 0) x = 0; else if (x >= s1) x = s1-1;
			if (y < 0) y = 0; else if (y >= s2) y = s2-1;
			cw = (int)Math.floor(w*(x+1)) - (int)Math.floor(w*x);
			ch = (int)Math.floor(h*(y+1)) - (int)Math.floor(h*y);
			cx = (int)Math.floor(w*x);
			cy = (int)Math.floor(h*y);
			cr = new Rectangle(r.x+cx, r.y+cy, cw+1, ch+1);
			{
				RCPSwatch sw1 = children.get(((s1+2)/2)*(y+((x+y)%2))+(x/2));
				RCPSwatch sw2 = children.get(((s1+2)/2)*(y+((x+y+1)%2))+((x+1)/2));
				boolean tlbr = (((x+y)%2)==1);
				float mxf = (float)(p.x-cr.x)/(float)(cr.width);
				float myf = (float)(p.y-cr.y)/(float)(cr.height);
				return ((tlbr ? (mxf >= myf) : (mxf+myf >= 1.0f)) ? sw2 : sw1).name;
			}
		case INST_TYPE_ORIENTATION:
			if (r.width > r.height+r.height/2) {
				return children.get(0).getName(r,p);
			}
			else if (r.height > r.width+r.width/2) {
				return children.get(2).getName(r,p);
			}
			else {
				return children.get(1).getName(r,p);
			}
		case INST_TYPE_SQUARE_GRID:
			w = (float)(r.width-1) / (float)s1;
			h = (float)(r.height-1) / (float)s2;
			if (w < h) {
				h = w;
				r.y += (r.height-(int)(h*s2+1))/2;
				r.height = (int)(h*s2+1);
			}
			else if (h < w) {
				w = h;
				r.x += (r.width-(int)(w*s1+1))/2;
				r.width = (int)(w*s1+1);
			}
			if (!r.contains(p)) return null;
			x = (int)Math.floor((float)(p.x-r.x)*(float)s1/(float)r.width);
			y = (int)Math.floor((float)(p.y-r.y)*(float)s2/(float)r.height);
			if (x < 0) x = 0; else if (x >= s1) x = s1-1;
			if (y < 0) y = 0; else if (y >= s2) y = s2-1;
			cw = (int)Math.floor(w*(x+1)) - (int)Math.floor(w*x);
			ch = (int)Math.floor(h*(y+1)) - (int)Math.floor(h*y);
			cx = (int)Math.floor(w*x);
			cy = (int)Math.floor(h*y);
			cr = new Rectangle(r.x+cx, r.y+cy, cw+1, ch+1);
			return children.get(y*s1+x).getName(cr, p);
		case INST_TYPE_SQ_DIAGONAL:
			w = (float)Math.floor((float)(r.width-1) / (float)s1);
			h = (float)Math.floor((float)(r.height-1) / (float)s2);
			if (w < h) h = w;
			else if (h < w) w = h;
			r.x += (r.width-(int)(w*s1+1))/2;
			r.y += (r.height-(int)(h*s2+1))/2;
			r.width = (int)(w*s1+1);
			r.height = (int)(h*s2+1);
			if (!r.contains(p)) return null;
			x = (int)Math.floor((float)(p.x-r.x)*(float)s1/(float)r.width);
			y = (int)Math.floor((float)(p.y-r.y)*(float)s2/(float)r.height);
			if (x < 0) x = 0; else if (x >= s1) x = s1-1;
			if (y < 0) y = 0; else if (y >= s2) y = s2-1;
			cw = (int)Math.floor(w*(x+1)) - (int)Math.floor(w*x);
			ch = (int)Math.floor(h*(y+1)) - (int)Math.floor(h*y);
			cx = (int)Math.floor(w*x);
			cy = (int)Math.floor(h*y);
			cr = new Rectangle(r.x+cx, r.y+cy, cw+1, ch+1);
			{
				RCPSwatch sw1 = children.get(((s1+2)/2)*(y+((x+y)%2))+(x/2));
				RCPSwatch sw2 = children.get(((s1+2)/2)*(y+((x+y+1)%2))+((x+1)/2));
				boolean tlbr = (((x+y)%2)==1);
				float mxf = (float)(p.x-cr.x)/(float)(cr.width);
				float myf = (float)(p.y-cr.y)/(float)(cr.height);
				return ((tlbr ? (mxf >= myf) : (mxf+myf >= 1.0f)) ? sw2 : sw1).name;
			}
		case INST_TYPE_HORIZ_DIVIDER:
			coords = getDividerCoords(r.width-1, b1, b2, b3, b4, b5, b6);
			for (int i = 0; i < children.size(); i++) {
				if (p.x >= r.x+coords[i] && p.x <= r.x+coords[i+1]) {
					cr = new Rectangle(r.x+coords[i], r.y, coords[i+1]-coords[i]+1, r.height);
					return children.get(i).getName(cr, p);
				}
				
			}
			return null;
		case INST_TYPE_VERT_DIVIDER:
			coords = getDividerCoords(r.height-1, b1, b2, b3, b4, b5, b6);
			for (int i = 0; i < children.size(); i++) {
				if (p.y >= r.y+coords[i] && p.y <= r.y+coords[i+1]) {
					cr = new Rectangle(r.x, r.y+coords[i], r.width, coords[i+1]-coords[i]+1);
					return children.get(i).getName(cr, p);
				}
			}
			return null;
		default:
			return name;
		}
	}
	
	public void paint(Graphics g, Rectangle r, Color currCol) {
		int n1 = (int)((instruction >>> 44l) & 0xFl);
		int n2 = (int)((instruction >>> 40l) & 0xFl);
		int n3 = (int)((instruction >>> 36l) & 0xFl);
		int n4 = (int)((instruction >>> 32l) & 0xFl);
		int b1 = (int)((instruction >>> 40l) & 0xFFl);
		int b2 = (int)((instruction >>> 32l) & 0xFFl);
		int b3 = (int)((instruction >>> 24l) & 0xFFl);
		int b4 = (int)((instruction >>> 16l) & 0xFFl);
		int b5 = (int)((instruction >>>  8l) & 0xFFl);
		int b6 = (int)((instruction >>>  0l) & 0xFFl);
		int s1 = (int)((instruction >>> 32l) & 0xFFFFl);
		int s2 = (int)((instruction >>> 16l) & 0xFFFFl);
		int s3 = (int)((instruction >>>  0l) & 0xFFFFl);
		Color c, lc;
		float c1, c2, c3, yv, xv, w, h;
		int north, west, east, south, x, y, cw, ch, cx, cy, lx, ly;
		int[] coords;
		Rectangle cr;
		switch ((int)((instruction & INST_TYPE_MASK) >>> INST_TYPE_SHIFT)) {
		case INST_TYPE_RGB:
			g.setColor(c = new Color(s1/65535.0f, s2/65535.0f, s3/65535.0f));
			g.fillRect(r.x, r.y, r.width, r.height);
			if (c.equals(currCol)) drawInnerBorder(g,r,instruction,c);
			drawOuterBorder(g,r,instruction);
			break;
		case INST_TYPE_HSV:
			g.setColor(c = Color.getHSBColor(s1/65535.0f, s2/65535.0f, s3/65535.0f));
			g.fillRect(r.x, r.y, r.width, r.height);
			if (c.equals(currCol)) drawInnerBorder(g,r,instruction,c);
			drawOuterBorder(g,r,instruction);
			break;
		case INST_TYPE_LAB:
			break;
		case INST_TYPE_XYZ:
			break;
		case INST_TYPE_ARGB:
			if (b1 < 255) {
				if (g instanceof Graphics2D) {
					((Graphics2D)g).setPaint(CheckerboardPaint.LIGHT);
					g.fillRect(r.x, r.y, r.width, r.height);
				} else {
					int x1 = r.x, x2 = x1+r.width/2, x3 = x1+r.width;
					int y1 = r.y, y2 = y1+r.height/2, y3 = y1+r.height;
					int[] xs1 = new int[]{x1, x3, x2};
					int[] ys1 = new int[]{y1, y1, y2};
					int[] xs2 = new int[]{x2, x1, x3};
					int[] ys2 = new int[]{y2, y3, y3};
					g.setColor(Color.white);
					g.fillRect(x1, y1, r.width, r.height);
					g.setColor(Color.gray);
					g.fillPolygon(xs1, ys1, 3);
					g.setColor(Color.black);
					g.fillPolygon(xs2, ys2, 3);
					g.setColor(Color.red);
					g.drawLine(x1, y1, x3-1, y3-1);
					g.drawLine(x3-1, y1, x1, y3-1);
				}
			}
			g.setColor(c = new Color(b2/255.0f, b3/255.0f, b4/255.0f, b1/255.0f));
			g.fillRect(r.x, r.y, r.width, r.height);
			if (c.equals(currCol)) drawInnerBorder(g,r,instruction,c);
			drawOuterBorder(g,r,instruction);
			break;
		case INST_TYPE_IMAGE:
			g.drawImage(image, r.x, r.y, r.width, r.height, null);
			drawOuterBorder(g,r,instruction);
			break;
		case INST_TYPE_RGB_SWEEP:
			c1 = c2 = c3 = b6/255.0f;
			lc = null; lx = ly = Integer.MIN_VALUE;
			for (y = 0; y < r.height; y++) {
				yv = (float)y/(float)r.height;
				yv = (b5/255.0f)*yv + (b4/255.0f)*(1.0f-yv);
				switch (n2) {
				case 0: c1 = yv; break;
				case 1: c2 = yv; break;
				case 2: c3 = yv; break;
				}
				for (x = 0; x < r.width; x++) {
					xv = (float)x/(float)r.width;
					xv = (b3/255.0f)*xv + (b2/255.0f)*(1.0f-xv);
					switch (n1) {
					case 0: c1 = xv; break;
					case 1: c2 = xv; break;
					case 2: c3 = xv; break;
					}
					g.setColor(c = new Color(c1, c2, c3));
					g.fillRect(r.x+x, r.y+y, 1, 1);
					if (c.equals(currCol)) {
						lc = c;
						lx = x;
						ly = y;
					}
				}
			}
			if (lc != null) {
				g.drawImage(colorCurs, r.x+lx-3, r.y+ly-3, null);
			}
			drawOuterBorder(g,r,instruction);
			break;
		case INST_TYPE_HSV_SWEEP:
			c1 = c2 = c3 = b6/255.0f;
			lc = null; lx = ly = Integer.MIN_VALUE;
			for (y = 0; y < r.height; y++) {
				yv = (float)y/(float)r.height;
				yv = (b5/255.0f)*yv + (b4/255.0f)*(1.0f-yv);
				switch (n2) {
				case 0: c1 = yv; break;
				case 1: c2 = yv; break;
				case 2: c3 = yv; break;
				}
				for (x = 0; x < r.width; x++) {
					xv = (float)x/(float)r.width;
					xv = (b3/255.0f)*xv + (b2/255.0f)*(1.0f-xv);
					switch (n1) {
					case 0: c1 = xv; break;
					case 1: c2 = xv; break;
					case 2: c3 = xv; break;
					}
					g.setColor(c = Color.getHSBColor(c1, c2, c3));
					g.fillRect(r.x+x, r.y+y, 1, 1);
					if (c.equals(currCol)) {
						lc = c;
						lx = x;
						ly = y;
					}
				}
			}
			if (lc != null) {
				g.drawImage(colorCurs, r.x+lx-3, r.y+ly-3, null);
			}
			drawOuterBorder(g,r,instruction);
			break;
		case INST_TYPE_LAB_SWEEP:
			break;
		case INST_TYPE_XYZ_SWEEP:
			break;
		case INST_TYPE_WRAPPER:
			children.get(0).paint(g,r,currCol);
			break;
		case INST_TYPE_BORDERLAYOUT:
			north = ((n1 != 0) && (b3 != 0)) ? (((r.height-1) * n1) / b3) : 0;
			west  = ((n2 != 0) && (b4 != 0)) ? (((r.width -1) * n2) / b4) : 0;
			east  = ((n3 != 0) && (b5 != 0)) ? (((r.width -1) * n3) / b5) : 0;
			south = ((n4 != 0) && (b6 != 0)) ? (((r.height-1) * n4) / b6) : 0;
			if (north != 0) {
				cr = new Rectangle(r.x, r.y, r.width, north+1);
				children.get(0).paint(g, cr, currCol);
			}
			if (south != 0) {
				cr = new Rectangle(r.x, r.y+r.height-south-1, r.width, south+1);
				children.get(3).paint(g, cr, currCol);
			}
			if (west != 0) {
				cr = new Rectangle(r.x, r.y+north, west+1, r.height-north-south);
				children.get(1).paint(g, cr, currCol);
			}
			if (east != 0) {
				cr = new Rectangle(r.x+r.width-east-1, r.y+north, east+1, r.height-north-south);
				children.get(2).paint(g, cr, currCol);
			}
			cr = new Rectangle(r.x+west, r.y+north, r.width-west-east, r.height-north-south);
			children.get(4).paint(g, cr, currCol);
			break;
		case INST_TYPE_FRAME:
			w = (float)(r.width-1) / (float)s1;
			h = (float)(r.height-1) / (float)s2;
			for (y = 0, x = 0; x < s1-1; x++) {
				cw = (int)Math.floor(w*(x+1)) - (int)Math.floor(w*x);
				ch = (int)Math.floor(h*(y+1)) - (int)Math.floor(h*y);
				cx = (int)Math.floor(w*x);
				cy = (int)Math.floor(h*y);
				cr = new Rectangle(r.x+cx, r.y+cy, cw+1, ch+1);
				children.get(x).paint(g, cr, currCol);
			}
			for (x = s1-1, y = 0; y < s2-1; y++) {
				cw = (int)Math.floor(w*(x+1)) - (int)Math.floor(w*x);
				ch = (int)Math.floor(h*(y+1)) - (int)Math.floor(h*y);
				cx = (int)Math.floor(w*x);
				cy = (int)Math.floor(h*y);
				cr = new Rectangle(r.x+cx, r.y+cy, cw+1, ch+1);
				children.get(s1-1+y).paint(g, cr, currCol);
			}
			for (y = s2-1, x = s1-1; x > 0; x--) {
				cw = (int)Math.floor(w*(x+1)) - (int)Math.floor(w*x);
				ch = (int)Math.floor(h*(y+1)) - (int)Math.floor(h*y);
				cx = (int)Math.floor(w*x);
				cy = (int)Math.floor(h*y);
				cr = new Rectangle(r.x+cx, r.y+cy, cw+1, ch+1);
				children.get(s1+s2-2+(s1-1-x)).paint(g, cr, currCol);
			}
			for (x = 0, y = s2-1; y > 0; y--) {
				cw = (int)Math.floor(w*(x+1)) - (int)Math.floor(w*x);
				ch = (int)Math.floor(h*(y+1)) - (int)Math.floor(h*y);
				cx = (int)Math.floor(w*x);
				cy = (int)Math.floor(h*y);
				cr = new Rectangle(r.x+cx, r.y+cy, cw+1, ch+1);
				children.get(s1+s2+s1-3+(s2-1-y)).paint(g, cr, currCol);
			}
			cw = (int)Math.floor(w*(s1-1)) - (int)Math.floor(w);
			ch = (int)Math.floor(h*(s2-1)) - (int)Math.floor(h);
			cx = (int)Math.floor(w);
			cy = (int)Math.floor(h);
			cr = new Rectangle(r.x+cx, r.y+cy, cw+1, ch+1);
			children.get(s1+s2+s1+s2-4).paint(g, cr, currCol);
			break;
		case INST_TYPE_GRID:
			w = (float)(r.width-1) / (float)s1;
			h = (float)(r.height-1) / (float)s2;
			for (y = 0; y < s2; y++) {
				for (x = 0; x < s1; x++) {
					cw = (int)Math.floor(w*(x+1)) - (int)Math.floor(w*x);
					ch = (int)Math.floor(h*(y+1)) - (int)Math.floor(h*y);
					cx = (int)Math.floor(w*x);
					cy = (int)Math.floor(h*y);
					cr = new Rectangle(r.x+cx, r.y+cy, cw+1, ch+1);
					children.get(y*s1+x).paint(g, cr, currCol);
				}
			}
			break;
		case INST_TYPE_DIAGONAL_GRID:
			w = (float)(r.width-1) / (float)s1;
			h = (float)(r.height-1) / (float)s2;
			for (y = 0; y < s2; y++) {
				for (x = 0; x < s1; x++) {
					cw = (int)Math.floor(w*(x+1)) - (int)Math.floor(w*x);
					ch = (int)Math.floor(h*(y+1)) - (int)Math.floor(h*y);
					cx = (int)Math.floor(w*x);
					cy = (int)Math.floor(h*y);
					cr = new Rectangle(r.x+cx, r.y+cy, cw+1, ch+1);
					int i1 = ((s1+2)/2)*(y+((x+y)%2))+(x/2);
					int i2 = ((s1+2)/2)*(y+((x+y+1)%2))+((x+1)/2);
					boolean tlbr = (((x+y)%2)==1);
					long sw1 = children.get(i1).instruction;
					long sw2 = children.get(i2).instruction;
					paintDiagonal(g, cr, currCol, tlbr, sw1, sw2);
				}
			}
			break;
		case INST_TYPE_ORIENTATION:
			if (r.width > r.height+r.height/2) {
				children.get(0).paint(g,r,currCol);
			}
			else if (r.height > r.width+r.width/2) {
				children.get(2).paint(g,r,currCol);
			}
			else {
				children.get(1).paint(g,r,currCol);
			}
			break;
		case INST_TYPE_SQUARE_GRID:
			w = (float)(r.width-1) / (float)s1;
			h = (float)(r.height-1) / (float)s2;
			if (w < h) {
				h = w;
				r.y += (r.height-(int)(h*s2+1))/2;
				r.height = (int)(h*s2+1);
			}
			else if (h < w) {
				w = h;
				r.x += (r.width-(int)(w*s1+1))/2;
				r.width = (int)(w*s1+1);
			}
			for (y = 0; y < s2; y++) {
				for (x = 0; x < s1; x++) {
					cw = (int)Math.floor(w*(x+1)) - (int)Math.floor(w*x);
					ch = (int)Math.floor(h*(y+1)) - (int)Math.floor(h*y);
					cx = (int)Math.floor(w*x);
					cy = (int)Math.floor(h*y);
					cr = new Rectangle(r.x+cx, r.y+cy, cw+1, ch+1);
					children.get(y*s1+x).paint(g, cr, currCol);
				}
			}
			break;
		case INST_TYPE_SQ_DIAGONAL:
			w = (float)Math.floor((float)(r.width-1) / (float)s1);
			h = (float)Math.floor((float)(r.height-1) / (float)s2);
			if (w < h) h = w;
			else if (h < w) w = h;
			r.x += (r.width-(int)(w*s1+1))/2;
			r.y += (r.height-(int)(h*s2+1))/2;
			r.width = (int)(w*s1+1);
			r.height = (int)(h*s2+1);
			for (y = 0; y < s2; y++) {
				for (x = 0; x < s1; x++) {
					cw = (int)Math.floor(w*(x+1)) - (int)Math.floor(w*x);
					ch = (int)Math.floor(h*(y+1)) - (int)Math.floor(h*y);
					cx = (int)Math.floor(w*x);
					cy = (int)Math.floor(h*y);
					cr = new Rectangle(r.x+cx, r.y+cy, cw+1, ch+1);
					int i1 = ((s1+2)/2)*(y+((x+y)%2))+(x/2);
					int i2 = ((s1+2)/2)*(y+((x+y+1)%2))+((x+1)/2);
					boolean tlbr = (((x+y)%2)==1);
					long sw1 = children.get(i1).instruction;
					long sw2 = children.get(i2).instruction;
					paintDiagonal(g, cr, currCol, tlbr, sw1, sw2);
				}
			}
			break;
		case INST_TYPE_HORIZ_DIVIDER:
			coords = getDividerCoords(r.width-1, b1, b2, b3, b4, b5, b6);
			for (int i = 0; i < children.size(); i++) {
				cr = new Rectangle(r.x+coords[i], r.y, coords[i+1]-coords[i]+1, r.height);
				children.get(i).paint(g, cr, currCol);
			}
			break;
		case INST_TYPE_VERT_DIVIDER:
			coords = getDividerCoords(r.height-1, b1, b2, b3, b4, b5, b6);
			for (int i = 0; i < children.size(); i++) {
				cr = new Rectangle(r.x, r.y+coords[i], r.width, coords[i+1]-coords[i]+1);
				children.get(i).paint(g, cr, currCol);
			}
			break;
		}
	}
	
	private static class LongArrayIterator implements Iterator<Long> {
		long[] arr;
		int pos;
		public LongArrayIterator(long[] arr) {
			this.arr = arr;
			this.pos = 0;
		}
		public boolean hasNext() {
			return (pos < arr.length);
		}
		public Long next() {
			return arr[pos++];
		}
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	private static class ByteArrayIterator implements Iterator<Long> {
		byte[] arr;
		int pos;
		public ByteArrayIterator(byte[] arr) {
			this.arr = arr;
			this.pos = 0;
		}
		public boolean hasNext() {
			return (pos+8 <= arr.length);
		}
		public Long next() {
			long l = 0;
			for (int i = 0; i < 8; i++) {
				l = (l << 8l) | (arr[pos+i] & 0xFFl);
			}
			pos += 8;
			return l;
		}
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	private static class LongStreamIterator implements Iterator<Long> {
		DataInputStream in;
		public LongStreamIterator(DataInputStream in) {
			this.in = in;
		}
		public boolean hasNext() {
			try {
				return in.available() >= 8;
			} catch (IOException ioe) {
				return false;
			}
		}
		public Long next() {
			try {
				return in.readLong();
			} catch (IOException ioe) {
				return 0l;
			}
		}
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	private static byte[] longsToBytes(Iterator<Long> l, int nbytes) {
		byte[] b = new byte[nbytes];
		for (int i=0, j=0; l.hasNext() && j<b.length; i++) {
			long ll = l.next();
			for (int k=56; k>=0 && j<b.length; k-=8, j++) {
				b[j] = (byte)(ll >> k);
			}
		}
		return b;
	}
	
	private static Color getColorDiagonal(Rectangle r, Point p, boolean tlbr, long sw1, long sw2) {
		float mxf = (float)(p.x-r.x)/(float)(r.width);
		float myf = (float)(p.y-r.y)/(float)(r.height);
		long sw = ((tlbr ? (mxf >= myf) : (mxf+myf >= 1.0f)) ? sw2 : sw1);
		int b1 = (int)((sw >>> 40l) & 0xFFl);
		int b2 = (int)((sw >>> 32l) & 0xFFl);
		int b3 = (int)((sw >>> 24l) & 0xFFl);
		int b4 = (int)((sw >>> 16l) & 0xFFl);
		int s1 = (int)((sw >>> 32l) & 0xFFFFl);
		int s2 = (int)((sw >>> 16l) & 0xFFFFl);
		int s3 = (int)((sw >>>  0l) & 0xFFFFl);
		switch ((int)((sw & INST_TYPE_MASK) >>> INST_TYPE_SHIFT)) {
		case INST_TYPE_RGB:
			return new Color(s1/65535.0f, s2/65535.0f, s3/65535.0f);
		case INST_TYPE_HSV:
			return Color.getHSBColor(s1/65535.0f, s2/65535.0f, s3/65535.0f);
		case INST_TYPE_LAB:
			return null;
		case INST_TYPE_XYZ:
			return null;
		case INST_TYPE_ARGB:
			return new Color(b2/255.0f, b3/255.0f, b4/255.0f, b1/255.0f);
		default:
			return null;
		}
	}
	
	private static void drawInnerBorder(Graphics g, Rectangle r, long inst, Color c) {
		int b = (int)((inst & INST_BORDER_MASK) >>> INST_BORDER_SHIFT);
		g.setColor((c.getRed()*0.3 + c.getGreen()*0.59 + c.getBlue()*0.11 < 127.5) ? Color.white : Color.black);
		if ((b & INST_BORDER_NO_TOP) == 0) {
			g.drawLine(r.x, r.y+1, r.x+r.width-1, r.y+1);
		}
		if ((b & INST_BORDER_NO_LEFT) == 0) {
			g.drawLine(r.x+1, r.y, r.x+1, r.y+r.height-1);
		}
		if ((b & INST_BORDER_NO_BOTTOM) == 0) {
			g.drawLine(r.x, r.y+r.height-2, r.x+r.width-1, r.y+r.height-2);
		}
		if ((b & INST_BORDER_NO_RIGHT) == 0) {
			g.drawLine(r.x+r.width-2, r.y, r.x+r.width-2, r.y+r.height-1);
		}
	}
	
	private static void drawOuterBorder(Graphics g, Rectangle r, long inst) {
		int b = (int)((inst & INST_BORDER_MASK) >>> INST_BORDER_SHIFT);
		g.setColor(Color.black);
		if ((b & INST_BORDER_NO_TOP) == 0) {
			g.drawLine(r.x, r.y, r.x+r.width-1, r.y);
		}
		if ((b & INST_BORDER_NO_LEFT) == 0) {
			g.drawLine(r.x, r.y, r.x, r.y+r.height-1);
		}
		if ((b & INST_BORDER_NO_BOTTOM) == 0) {
			g.drawLine(r.x, r.y+r.height-1, r.x+r.width-1, r.y+r.height-1);
		}
		if ((b & INST_BORDER_NO_RIGHT) == 0) {
			g.drawLine(r.x+r.width-1, r.y, r.x+r.width-1, r.y+r.height-1);
		}
	}
	
	private static void paintDiagonal(Graphics g, Rectangle r, Color currCol, boolean tlbr, long sw1, long sw2) {
		int type1 = (int)((sw1 & INST_TYPE_MASK) >>> INST_TYPE_SHIFT);
		int posn1 = (int)((sw1 & INST_BORDER_MASK) >>> INST_BORDER_SHIFT);
		int b11 = (int)((sw1 >>> 40l) & 0xFFl);
		int b21 = (int)((sw1 >>> 32l) & 0xFFl);
		int b31 = (int)((sw1 >>> 24l) & 0xFFl);
		int b41 = (int)((sw1 >>> 16l) & 0xFFl);
		int s11 = (int)((sw1 >>> 32l) & 0xFFFFl);
		int s21 = (int)((sw1 >>> 16l) & 0xFFFFl);
		int s31 = (int)((sw1 >>>  0l) & 0xFFFFl);
		int type2 = (int)((sw2 & INST_TYPE_MASK) >>> INST_TYPE_SHIFT);
		int posn2 = (int)((sw2 & INST_BORDER_MASK) >>> INST_BORDER_SHIFT);
		int b12 = (int)((sw2 >>> 40l) & 0xFFl);
		int b22 = (int)((sw2 >>> 32l) & 0xFFl);
		int b32 = (int)((sw2 >>> 24l) & 0xFFl);
		int b42 = (int)((sw2 >>> 16l) & 0xFFl);
		int s12 = (int)((sw2 >>> 32l) & 0xFFFFl);
		int s22 = (int)((sw2 >>> 16l) & 0xFFFFl);
		int s32 = (int)((sw2 >>>  0l) & 0xFFFFl);
		Color c;
		int[] xs1, ys1, xs2, ys2;
		int l1x1, l1y1, l1x2, l1y2;
		int l2x1, l2y1, l2x2, l2y2;
		int lx1, ly1, lx2, ly2;
		if (tlbr) {
			xs1 = new int[]{r.x, r.x, r.x+r.width-1};
			ys1 = new int[]{r.y, r.y+r.height-1, r.y+r.height-1};
			xs2 = new int[]{r.x, r.x+r.width-1, r.x+r.width-1};
			ys2 = new int[]{r.y, r.y, r.y+r.height-1};
			l1x1 = r.x; l1y1 = r.y+1; l1x2 = r.x+r.width-2; l1y2 = r.y+r.height-1;
			l2x1 = r.x+1; l2y1 = r.y; l2x2 = r.x+r.width-1; l2y2 = r.y+r.height-2;
			lx1 = r.x; ly1 = r.y; lx2 = r.x+r.width-1; ly2 = r.y+r.height-1;
		} else {
			xs1 = new int[]{r.x, r.x, r.x+r.width-1};
			ys1 = new int[]{r.y+r.height-1, r.y, r.y};
			xs2 = new int[]{r.x, r.x+r.width-1, r.x+r.width-1};
			ys2 = new int[]{r.y+r.height-1, r.y+r.height-1, r.y};
			l1x1 = r.x+r.width-2; l1y1 = r.y; l1x2 = r.x; l1y2 = r.y+r.height-2;
			l2x1 = r.x+r.width-1; l2y1 = r.y+1; l2x2 = r.x+1; l2y2 = r.y+r.height-1;
			lx1 = r.x+r.width-1; ly1 = r.y; lx2 = r.x; ly2 = r.y+r.height-1;
		}
		switch (type1) {
		case INST_TYPE_RGB:
			c = new Color(s11/65535.0f, s21/65535.0f, s31/65535.0f); break;
		case INST_TYPE_HSV:
			c = Color.getHSBColor(s11/65535.0f, s21/65535.0f, s31/65535.0f); break;
		case INST_TYPE_LAB:
			c = null; break;
		case INST_TYPE_XYZ:
			c = null; break;
		case INST_TYPE_ARGB:
			c = new Color(b21/255.0f, b31/255.0f, b41/255.0f, b11/255.0f); break;
		default:
			c = null; break;
		}
		if (c != null) {
			g.setColor(c);
			g.fillPolygon(xs1, ys1, 3);
			if (tlbr ? ((posn1 & INST_BORDER_NO_DTR) == 0) : ((posn1 & INST_BORDER_NO_DBR) == 0)) {
				if (c.equals(currCol)) {
					g.setColor((c.getRed()*0.3 + c.getGreen()*0.59 + c.getBlue()*0.11 < 127.5) ? Color.white : Color.black);
					g.drawLine(l1x1, l1y1, l1x2, l1y2);
				}
			}
		}
		switch (type2) {
		case INST_TYPE_RGB:
			c = new Color(s12/65535.0f, s22/65535.0f, s32/65535.0f); break;
		case INST_TYPE_HSV:
			c = Color.getHSBColor(s12/65535.0f, s22/65535.0f, s32/65535.0f); break;
		case INST_TYPE_LAB:
			c = null; break;
		case INST_TYPE_XYZ:
			c = null; break;
		case INST_TYPE_ARGB:
			c = new Color(b22/255.0f, b32/255.0f, b42/255.0f, b12/255.0f); break;
		default:
			c = null; break;
		}
		if (c != null) {
			g.setColor(c);
			g.fillPolygon(xs2, ys2, 3);
			if (tlbr ? ((posn2 & INST_BORDER_NO_DTR) == 0) : ((posn2 & INST_BORDER_NO_DBR) == 0)) {
				if (c.equals(currCol)) {
					g.setColor((c.getRed()*0.3 + c.getGreen()*0.59 + c.getBlue()*0.11 < 127.5) ? Color.white : Color.black);
					g.drawLine(l2x1, l2y1, l2x2, l2y2);
				}
			}
		}
		if (((type1 != 0) && (tlbr ? ((posn1 & INST_BORDER_NO_DTR) == 0) : ((posn1 & INST_BORDER_NO_DBR) == 0))) || ((type2 != 0) && (tlbr ? ((posn2 & INST_BORDER_NO_DBL) == 0) : ((posn2 & INST_BORDER_NO_DTL) == 0)))) {
			g.setColor(Color.black);
			g.drawLine(lx1, ly1, lx2, ly2);
		}
	}
	
	private static int[] getDividerCoords(int wh, int b1, int b2, int b3, int b4, int b5, int b6) {
		Vector<Integer> coords = new Vector<Integer>();
		coords.add(0);
		if (b1 != 0) coords.add((int)Math.round((float)wh*(float)(b1)/(float)b6));
		if (b2 != 0) coords.add((int)Math.round((float)wh*(float)(b1+b2)/(float)b6));
		if (b3 != 0) coords.add((int)Math.round((float)wh*(float)(b1+b2+b3)/(float)b6));
		if (b4 != 0) coords.add((int)Math.round((float)wh*(float)(b1+b2+b3+b4)/(float)b6));
		if (b5 != 0) coords.add((int)Math.round((float)wh*(float)(b1+b2+b3+b4+b5)/(float)b6));
		if (b1+b2+b3+b4+b5 < b6) coords.add(wh);
		int[] c = new int[coords.size()];
		for (int i = 0; i < coords.size(); i++) c[i] = coords.get(i);
		return c;
	}
	
	private static class CheckerboardPaint implements Paint {
		public static final CheckerboardPaint LIGHT = new CheckerboardPaint(0xFFFFFFFF, 0xFFCCCCCC);
		//public static final CheckerboardPaint DARK = new CheckerboardPaint(0xFF333333, 0xFF000000);
		
		private Paint pattern;
		
		public CheckerboardPaint(int light, int dark) {
			BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = img.createGraphics();
			g.setColor(new Color(light));
			g.fillRect(0, 0, 8, 8);
			g.fillRect(8, 8, 8, 8);
			g.setColor(new Color(dark));
			g.fillRect(8, 0, 8, 8);
			g.fillRect(0, 8, 8, 8);
			g.dispose();
			Rectangle2D rect = new Rectangle2D.Float(0, 0, 16, 16);
			pattern = new TexturePaint(img, rect);
		}

		public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
			return pattern.createContext(cm, deviceBounds, userBounds, xform, hints);
		}

		public int getTransparency() {
			return pattern.getTransparency();
		}
	}
}
