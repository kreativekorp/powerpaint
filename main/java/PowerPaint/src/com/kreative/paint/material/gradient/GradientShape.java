package com.kreative.paint.material.gradient;

public abstract class GradientShape {
	public static final GradientShape SIMPLE_LINEAR = new GradientShape.Linear(
		0.0, 0.5, 1.0, 0.5, false, false, false, "Simple Linear"
	);
	public static final GradientShape REVERSE_LINEAR = new GradientShape.Linear(
		0.0, 0.5, 1.0, 0.5, false, false, true, "Reverse Linear"
	);
	public static final GradientShape SIMPLE_ANGULAR = new GradientShape.Angular(
		0.5, 0.5, 1.0, 0.5, false, false, false, "Simple Angular"
	);
	public static final GradientShape REVERSE_ANGULAR = new GradientShape.Angular(
		0.5, 0.5, 1.0, 0.5, false, false, true, "Reverse Angular"
	);
	
	public final boolean repeat;
	public final boolean reflect;
	public final boolean reverse;
	public final String name;
	
	protected GradientShape(boolean repeat, boolean reflect, boolean reverse, String name) {
		this.repeat = repeat;
		this.reflect = reflect;
		this.reverse = reverse;
		this.name = name;
	}
	
	public abstract double getGradientPosition(double x, double y);
	public abstract double[] getGradientPositions(double[] x, double[] y, int npoints);
	protected abstract boolean equalsImpl(GradientShape that);
	protected abstract int hashCodeImpl();
	
	@Override
	public final boolean equals(Object that) {
		if (that instanceof GradientShape) {
			return this.equals((GradientShape)that, false);
		} else {
			return false;
		}
	}
	
	public final boolean equals(GradientShape that, boolean withName) {
		if (!this.equalsImpl(that)) return false;
		if (this.repeat != that.repeat) return false;
		if (this.reflect != that.reflect) return false;
		if (this.reverse != that.reverse) return false;
		if (!withName) return true;
		if (this.name != null) return this.name.equals(that.name);
		if (that.name != null) return that.name.equals(this.name);
		return true;
	}
	
	@Override
	public final int hashCode() {
		int hashCode = hashCodeImpl();
		if (repeat ) hashCode ^= 0x11111111;
		if (reflect) hashCode ^= 0x22222222;
		if (reverse) hashCode ^= 0x44444444;
		return hashCode;
	}
	
	public static class Linear extends GradientShape {
		public final double x0, y0, x1, y1;
		public Linear(
			double x0, double y0, double x1, double y1,
			boolean repeat, boolean reflect, boolean reverse, String name
		) {
			super(repeat, reflect, reverse, name);
			this.x0 = x0;
			this.y0 = y0;
			this.x1 = x1;
			this.y1 = y1;
		}
		@Override
		public double getGradientPosition(double x, double y) {
			double dX = x1-x0;
			double dY = y1-y0;
			double div = dX*dX + dY*dY;
			return ((x-x0)*dX + (y-y0)*dY) / div;
		}
		@Override
		public double[] getGradientPositions(double[] x, double[] y, int npoints) {
			double dX = x1-x0;
			double dY = y1-y0;
			double div = dX*dX + dY*dY;
			double[] ret = new double[npoints];
			for (int i = 0; i < npoints; i++) {
				ret[i] = ((x[i]-x0)*dX + (y[i]-y0)*dY) / div;
			}
			return ret;
		}
		@Override
		protected boolean equalsImpl(GradientShape that) {
			if (that instanceof Linear) {
				if (this.x0 != ((Linear)that).x0) return false;
				if (this.y0 != ((Linear)that).y0) return false;
				if (this.x1 != ((Linear)that).x1) return false;
				if (this.y1 != ((Linear)that).y1) return false;
				return true;
			} else {
				return false;
			}
		}
		@Override
		protected int hashCodeImpl() {
			int hashCode = 0;
			hashCode ^= Double.valueOf(x0).hashCode();
			hashCode ^= Double.valueOf(y0).hashCode();
			hashCode ^= Double.valueOf(x1).hashCode();
			hashCode ^= Double.valueOf(y1).hashCode();
			return hashCode;
		}
	}
	
	public static class Angular extends GradientShape {
		public final double cx, cy, px, py;
		public Angular(
			double cx, double cy, double px, double py,
			boolean repeat, boolean reflect, boolean reverse, String name
		) {
			super(repeat, reflect, reverse, name);
			this.cx = cx;
			this.cy = cy;
			this.px = px;
			this.py = py;
		}
		@Override
		public double getGradientPosition(double x, double y) {
			double amin = Math.atan2(py-cy, px-cx);
			double adist = 2.0*Math.PI;
			double a = Math.atan2(y-cy, x-cx);
			a -= amin;
			a -= adist*Math.floor(a/adist);
			return a / adist;
		}
		@Override
		public double[] getGradientPositions(double[] x, double[] y, int npoints) {
			double amin = Math.atan2(py-cy, px-cx);
			double adist = 2.0*Math.PI;
			double[] ret = new double[npoints];
			for (int i = 0; i < npoints; i++) {
				double a = Math.atan2(y[i]-cy, x[i]-cx);
				a -= amin;
				a -= adist*Math.floor(a/adist);
				ret[i] =  a / adist;
			}
			return ret;
		}
		@Override
		protected boolean equalsImpl(GradientShape that) {
			if (that instanceof Angular) {
				if (this.cx != ((Angular)that).cx) return false;
				if (this.cy != ((Angular)that).cy) return false;
				if (this.px != ((Angular)that).px) return false;
				if (this.py != ((Angular)that).py) return false;
				return true;
			} else {
				return false;
			}
		}
		@Override
		protected int hashCodeImpl() {
			int hashCode = 0;
			hashCode ^= Double.valueOf(cx).hashCode();
			hashCode ^= Double.valueOf(cy).hashCode();
			hashCode ^= Double.valueOf(px).hashCode();
			hashCode ^= Double.valueOf(py).hashCode();
			return hashCode;
		}
	}
	
	public static class Radial extends GradientShape {
		public double cx, cy, x0, y0, x1, y1;
		public Radial(
			double cx, double cy, double x0, double y0, double x1, double y1,
			boolean repeat, boolean reflect, boolean reverse, String name
		) {
			super(repeat, reflect, reverse, name);
			this.cx = cx;
			this.cy = cy;
			this.x0 = x0;
			this.y0 = y0;
			this.x1 = x1;
			this.y1 = y1;
		}
		@Override
		public double getGradientPosition(double x, double y) {
			double r0 = Math.hypot(y0-cy, x0-cx);
			double r1 = Math.hypot(y1-cy, x1-cx);
			double r = Math.hypot(y-cy, x-cx);
			return (r-r0)/(r1-r0);
		}
		@Override
		public double[] getGradientPositions(double[] x, double[] y, int npoints) {
			double r0 = Math.hypot(y0-cy, x0-cx);
			double r1 = Math.hypot(y1-cy, x1-cx);
			double[] ret = new double[npoints];
			for (int i = 0; i < npoints; i++) {
				double r = Math.hypot(y[i]-cy, x[i]-cx);
				ret[i] = (r-r0)/(r1-r0);
			}
			return ret;
		}
		@Override
		protected boolean equalsImpl(GradientShape that) {
			if (that instanceof Radial) {
				if (this.cx != ((Radial)that).cx) return false;
				if (this.cy != ((Radial)that).cy) return false;
				if (this.x0 != ((Radial)that).x0) return false;
				if (this.y0 != ((Radial)that).y0) return false;
				if (this.x1 != ((Radial)that).x1) return false;
				if (this.y1 != ((Radial)that).y1) return false;
				return true;
			} else {
				return false;
			}
		}
		@Override
		protected int hashCodeImpl() {
			int hashCode = 0;
			hashCode ^= Double.valueOf(cx).hashCode();
			hashCode ^= Double.valueOf(cy).hashCode();
			hashCode ^= Double.valueOf(x0).hashCode();
			hashCode ^= Double.valueOf(y0).hashCode();
			hashCode ^= Double.valueOf(x1).hashCode();
			hashCode ^= Double.valueOf(y1).hashCode();
			return hashCode;
		}
	}
	
	public static class Rectangular extends GradientShape {
		public double l0, t0, r0, b0, l1, t1, r1, b1;
		public Rectangular(
			double l0, double t0, double r0, double b0,
			double l1, double t1, double r1, double b1,
			boolean repeat, boolean reflect, boolean reverse, String name
		) {
			super(repeat, reflect, reverse, name);
			this.l0 = l0;
			this.t0 = t0;
			this.r0 = r0;
			this.b0 = b0;
			this.l1 = l1;
			this.t1 = t1;
			this.r1 = r1;
			this.b1 = b1;
		}
		@Override
		public double getGradientPosition(double x, double y) {
			double tlm = (t1-t0) / (l1-l0);
			double tlb =     t0  - tlm*l0 ;
			double trm = (t1-t0) / (r1-r0);
			double trb =     t0  - trm*r0 ;
			double blm = (b1-b0) / (l1-l0);
			double blb =     b0  - blm*l0 ;
			double brm = (b1-b0) / (r1-r0);
			double brb =     b0  - brm*r0 ;
			double lrx = xintersection(l0, t0, l1, t1, r0, t0, r1, t1);
			double tby = yintersection(l0, t0, l1, t1, l0, b0, l1, b1);
			boolean tl = (y < tlm*x + tlb);
			boolean tr = (y < trm*x + trb);
			boolean bl = (y < blm*x + blb);
			boolean br = (y < brm*x + brb);
			boolean t = ( tl &&  tr);
			boolean l = ( bl && !tl);
			boolean b = (!bl && !br);
			boolean r = ( br && !tr);
			if (t && b) {
				t = (y < tby);
				b = (y > tby);
			}
			if (l && r) {
				l = (x < lrx);
				r = (x > lrx);
			}
			if (t && l || t && b || t && r || l && b || l && r || b && r) {
				System.err.println(
					"Notice: Assert failed on GradientShape.Rectangular.getGradientPosition. " +
					"Point " + x + "," + y + " was determined to be in multiple quadrants. " +
					"T=" + t + " L=" + l + " B=" + b + " R=" + r
				);
			}
			if (t) return (y-t0) / (t1-t0);
			if (b) return (y-b0) / (b1-b0);
			if (l) return (x-l0) / (l1-l0);
			if (r) return (x-r0) / (r1-r0);
			System.err.println(
				"Notice: Assert failed on GradientShape.Rectangular.getGradientPosition. " +
				"Point " + x + "," + y + " was determined to be in no quadrants."
			);
			return 0;
		}
		@Override
		public double[] getGradientPositions(double[] x, double[] y, int npoints) {
			double tlm = (t1-t0) / (l1-l0);
			double tlb =     t0  - tlm*l0 ;
			double trm = (t1-t0) / (r1-r0);
			double trb =     t0  - trm*r0 ;
			double blm = (b1-b0) / (l1-l0);
			double blb =     b0  - blm*l0 ;
			double brm = (b1-b0) / (r1-r0);
			double brb =     b0  - brm*r0 ;
			double lrx = xintersection(l0, t0, l1, t1, r0, t0, r1, t1);
			double tby = yintersection(l0, t0, l1, t1, l0, b0, l1, b1);
			double[] ret = new double[npoints];
			for (int i = 0; i < npoints; i++) {
				boolean tl = (y[i] < tlm*x[i] + tlb);
				boolean tr = (y[i] < trm*x[i] + trb);
				boolean bl = (y[i] < blm*x[i] + blb);
				boolean br = (y[i] < brm*x[i] + brb);
				boolean t = (tl && tr);
				boolean l = (bl && !tl);
				boolean b = (!bl && !br);
				boolean r = (br && !tr);
				if (t && b) {
					t = (y[i] < tby);
					b = (y[i] > tby);
				}
				if (l && r) {
					l = (x[i] < lrx);
					r = (x[i] > lrx);
				}
				if (t && l || t && b || t && r || l && b || l && r || b && r) {
					System.err.println(
						"Notice: Assert failed on GradientShape.Rectangular.getGradientPosition. " +
						"Point " + x[i] + "," + y[i] + " was determined to be in multiple quadrants. " +
						"T=" + t + " L=" + l + " B=" + b + " R=" + r
					);
				}
				if (t) { ret[i] = (y[i]-t0) / (t1-t0); continue; }
				if (b) { ret[i] = (y[i]-b0) / (b1-b0); continue; }
				if (l) { ret[i] = (x[i]-l0) / (l1-l0); continue; }
				if (r) { ret[i] = (x[i]-r0) / (r1-r0); continue; }
				System.err.println(
					"Notice: Assert failed on GradientShape.Rectangular.getGradientPosition. " +
					"Point " + x[i] + "," + y[i] + " was determined to be in no quadrants."
				);
				ret[i] = 0;
			}
			return ret;
		}
		@Override
		protected boolean equalsImpl(GradientShape that) {
			if (that instanceof Rectangular) {
				if (this.l0 != ((Rectangular)that).l0) return false;
				if (this.t0 != ((Rectangular)that).t0) return false;
				if (this.r0 != ((Rectangular)that).r0) return false;
				if (this.b0 != ((Rectangular)that).b0) return false;
				if (this.l1 != ((Rectangular)that).l1) return false;
				if (this.t1 != ((Rectangular)that).t1) return false;
				if (this.r1 != ((Rectangular)that).r1) return false;
				if (this.b1 != ((Rectangular)that).b1) return false;
				return true;
			} else {
				return false;
			}
		}
		@Override
		protected int hashCodeImpl() {
			int hashCode = 0;
			hashCode ^= Double.valueOf(l0).hashCode();
			hashCode ^= Double.valueOf(t0).hashCode();
			hashCode ^= Double.valueOf(r0).hashCode();
			hashCode ^= Double.valueOf(b0).hashCode();
			hashCode ^= Double.valueOf(l1).hashCode();
			hashCode ^= Double.valueOf(t1).hashCode();
			hashCode ^= Double.valueOf(r1).hashCode();
			hashCode ^= Double.valueOf(b1).hashCode();
			return hashCode;
		}
		private double xintersection(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
			double u = (((x4-x3)*(y1-y3))-((y4-y3)*(x1-x3)))/(((y4-y3)*(x2-x1))-((x4-x3)*(y2-y1)));
			return x1+u*(x2-x1);
		}
		private double yintersection(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
			double u = (((x4-x3)*(y1-y3))-((y4-y3)*(x1-x3)))/(((y4-y3)*(x2-x1))-((x4-x3)*(y2-y1)));
			return y1+u*(y2-y1);
		}
	}
}
