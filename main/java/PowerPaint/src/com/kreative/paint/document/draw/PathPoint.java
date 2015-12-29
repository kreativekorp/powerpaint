package com.kreative.paint.document.draw;

import java.awt.geom.Point2D;

public class PathPoint extends Point2D implements Cloneable {
	private double pcdx;
	private double pcdy;
	private boolean pcQuadratic;
	private double x;
	private double y;
	private boolean lockAngle;
	private boolean lockRadius;
	private double ncdx;
	private double ncdy;
	private boolean ncQuadratic;
	
	public PathPoint(double x, double y) {
		this.pcdx = 0;
		this.pcdy = 0;
		this.pcQuadratic = false;
		this.x = x;
		this.y = y;
		this.lockAngle = false;
		this.lockRadius = false;
		this.ncdx = 0;
		this.ncdy = 0;
		this.ncQuadratic = false;
	}
	
	private PathPoint(PathPoint o) {
		this.pcdx = o.pcdx;
		this.pcdy = o.pcdy;
		this.pcQuadratic = o.pcQuadratic;
		this.x = o.x;
		this.y = o.y;
		this.lockAngle = o.lockAngle;
		this.lockRadius = o.lockRadius;
		this.ncdx = o.ncdx;
		this.ncdy = o.ncdy;
		this.ncQuadratic = o.ncQuadratic;
	}
	
	@Override
	public PathPoint clone() {
		return new PathPoint(this);
	}
	
	public Point2D getPreviousCtrl() {
		return new Point2D.Double(x + pcdx, y + pcdy);
	}
	
	public boolean isPreviousLinear() {
		return ((pcdx == 0) && (pcdy == 0));
	}
	
	public boolean isPreviousQuadratic() {
		return pcQuadratic;
	}
	
	@Override
	public double getX() {
		return x;
	}
	
	@Override
	public double getY() {
		return y;
	}
	
	public Point2D getLocation() {
		return new Point2D.Double(x, y);
	}
	
	public boolean isAngleLocked() {
		return lockAngle;
	}
	
	public boolean isRadiusLocked() {
		return lockRadius;
	}
	
	public Point2D getNextCtrl() {
		return new Point2D.Double(x + ncdx, y + ncdy);
	}
	
	public boolean isNextLinear() {
		return ((ncdx == 0) && (ncdy == 0));
	}
	
	public boolean isNextQuadratic() {
		return ncQuadratic;
	}
	
	public void setPreviousCtrl(Point2D p) {
		setPreviousCtrl(p.getX(), p.getY());
	}
	
	public void setPreviousCtrl(double pcx, double pcy) {
		this.pcdx = pcx - x;
		this.pcdy = pcy - y;
		if (lockAngle && lockRadius) {
			this.ncdx = -pcdx;
			this.ncdy = -pcdy;
		} else if (lockAngle || lockRadius) {
			double or = oppRadius(pcdy, pcdx, ncdy, ncdx);
			double oa = oppAngle(pcdy, pcdx, ncdy, ncdx);
			this.ncdx = or * Math.cos(oa);
			this.ncdy = or * Math.sin(oa);
		}
	}
	
	public void setPreviousLinear() {
		this.pcdx = 0;
		this.pcdy = 0;
		if (lockRadius) {
			this.ncdx = 0;
			this.ncdy = 0;
		}
	}
	
	public void setPreviousQuadratic(boolean quadratic) {
		this.pcQuadratic = quadratic;
	}
	
	@Override
	public void setLocation(Point2D p) {
		setLocation(p.getX(), p.getY());
	}
	
	@Override
	public void setLocation(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public void setAngleLocked(boolean locked) {
		this.lockAngle = locked;
	}
	
	public void setRadiusLocked(boolean locked) {
		this.lockRadius = locked;
	}
	
	public void setNextCtrl(Point2D p) {
		setNextCtrl(p.getX(), p.getY());
	}
	
	public void setNextCtrl(double ncx, double ncy) {
		this.ncdx = ncx - x;
		this.ncdy = ncy - y;
		if (lockAngle && lockRadius) {
			this.pcdx = -ncdx;
			this.pcdy = -ncdy;
		} else if (lockAngle || lockRadius) {
			double or = oppRadius(ncdy, ncdx, pcdy, pcdx);
			double oa = oppAngle(ncdy, ncdx, pcdy, pcdx);
			this.pcdx = or * Math.cos(oa);
			this.pcdy = or * Math.sin(oa);
		}
	}
	
	public void setNextLinear() {
		this.ncdx = 0;
		this.ncdy = 0;
		if (lockRadius) {
			this.pcdx = 0;
			this.pcdy = 0;
		}
	}
	
	public void setNextQuadratic(boolean quadratic) {
		this.ncQuadratic = quadratic;
	}
	
	@Override
	public boolean equals(Object that) {
		if (that instanceof PathPoint) {
			return (this.pcdx == ((PathPoint)that).pcdx)
			    && (this.pcdy == ((PathPoint)that).pcdy)
			    && (this.pcQuadratic == ((PathPoint)that).pcQuadratic)
			    && (this.x == ((PathPoint)that).x)
			    && (this.y == ((PathPoint)that).y)
			    && (this.lockAngle == ((PathPoint)that).lockAngle)
			    && (this.lockRadius == ((PathPoint)that).lockRadius)
			    && (this.ncdx == ((PathPoint)that).ncdx)
			    && (this.ncdy == ((PathPoint)that).ncdy)
			    && (this.ncQuadratic == ((PathPoint)that).ncQuadratic);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return java.lang.Float.floatToRawIntBits((float)pcdx)
		     ^ java.lang.Float.floatToRawIntBits((float)pcdy)
		     ^ (pcQuadratic ? 0x11111111 : 0)
		     ^ java.lang.Float.floatToRawIntBits((float)x)
		     ^ java.lang.Float.floatToRawIntBits((float)y)
		     ^ (lockAngle ? 0x22222222 : 0)
		     ^ (lockRadius ? 0x44444444 : 0)
		     ^ java.lang.Float.floatToRawIntBits((float)ncdx)
		     ^ java.lang.Float.floatToRawIntBits((float)ncdy)
		     ^ (ncQuadratic ? 0x88888888 : 0);
	}
	
	private double oppRadius(double dy, double dx, double ody, double odx) {
		if (lockRadius) {
			double r = Math.hypot(dy, dx);
			if (!(java.lang.Double.isNaN(r) ||
			      java.lang.Double.isInfinite(r))) return r;
		}
		double r = Math.hypot(ody, odx);
		if (!(java.lang.Double.isNaN(r) ||
		      java.lang.Double.isInfinite(r))) return r;
		return 0;
	}
	
	private double oppAngle(double dy, double dx, double ody, double odx) {
		if (lockAngle) {
			double a = Math.atan2(-dy, -dx);
			if (!(java.lang.Double.isNaN(a) ||
			      java.lang.Double.isInfinite(a))) return a;
		}
		double a = Math.atan2(ody, odx);
		if (!(java.lang.Double.isNaN(a) ||
		      java.lang.Double.isInfinite(a))) return a;
		return 0;
	}
}
