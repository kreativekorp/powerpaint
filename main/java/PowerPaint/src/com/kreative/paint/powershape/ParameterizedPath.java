package com.kreative.paint.powershape;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ParameterizedPath extends ParameterizedShape {
	private final List<Character> opcodes;
	private final List<List<ParameterizedValue>> operands;
	
	public ParameterizedPath() {
		this.opcodes = new ArrayList<Character>();
		this.operands = new ArrayList<List<ParameterizedValue>>();
	}
	
	public void add(char opcode, ParameterizedValue... operands) {
		List<ParameterizedValue> operandsIntl = new ArrayList<ParameterizedValue>();
		if (operands != null) operandsIntl.addAll(Arrays.asList(operands));
		operandsIntl = Collections.unmodifiableList(operandsIntl);
		this.operands.add(operandsIntl);
		this.opcodes.add(opcode);
	}
	
	public void add(char opcode, List<ParameterizedValue> operands) {
		List<ParameterizedValue> operandsIntl = new ArrayList<ParameterizedValue>();
		if (operands != null) operandsIntl.addAll(operands);
		operandsIntl = Collections.unmodifiableList(operandsIntl);
		this.operands.add(operandsIntl);
		this.opcodes.add(opcode);
	}
	
	public void clear() {
		this.opcodes.clear();
		this.operands.clear();
	}
	
	public char getOpcode(int index) {
		return this.opcodes.get(index);
	}
	
	public List<ParameterizedValue> getOperands(int index) {
		return this.operands.get(index);
	}
	
	public boolean isEmpty() {
		return this.opcodes.isEmpty() && this.operands.isEmpty();
	}
	
	public int size() {
		return Math.min(this.opcodes.size(), this.operands.size());
	}
	
	public static int operandCount(char opcode) {
		switch (opcode) {
			case 'Z': case 'z':
				return 0;
			case 'H': case 'h':
			case 'V': case 'v':
				return 1;
			case 'M': case 'm':
			case 'L': case 'l':
			case 'T': case 't':
				return 2;
			case 'Q': case 'q':
			case 'S': case 's':
				return 4;
			case 'C': case 'c':
			case 'R': case 'r':
				return 6;
			case 'A': case 'a':
			case 'E': case 'e':
				return 7;
			default:
				return -1;
		}
	}
	
	@Override
	public Shape awtShape(Bindings bindings) {
		GeneralPath p = new GeneralPath();
		double lcx = 0.0, lcy = 0.0, lx = 0.0, ly = 0.0;
		double ccx, ccy, arx, ary, aa;
		boolean large, sweep;
		double rx, ry, rw, rh, rrx, rry, ras, rae;
		int rat;
		for (int i = 0, n = size(); i < n; i++) {
			char inst = getOpcode(i);
			List<ParameterizedValue> args = getOperands(i);
			switch (inst) {
				case 'M':
					lcx = lx = args.get(0).value(bindings);
					lcy = ly = args.get(1).value(bindings);
					p.moveTo(lx, ly);
					break;
				case 'm':
					lcx = lx += args.get(0).value(bindings);
					lcy = ly += args.get(1).value(bindings);
					p.moveTo(lx, ly);
					break;
				case 'H':
					lcx = lx = args.get(0).value(bindings);
					p.lineTo(lx, lcy = ly);
					break;
				case 'h':
					lcx = lx += args.get(0).value(bindings);
					p.lineTo(lx, lcy = ly);
					break;
				case 'V':
					lcy = ly = args.get(0).value(bindings);
					p.lineTo(lcx = lx, ly);
					break;
				case 'v':
					lcy = ly += args.get(0).value(bindings);
					p.lineTo(lcx = lx, ly);
					break;
				case 'L':
					lcx = lx = args.get(0).value(bindings);
					lcy = ly = args.get(1).value(bindings);
					p.lineTo(lx, ly);
					break;
				case 'l':
					lcx = lx += args.get(0).value(bindings);
					lcy = ly += args.get(1).value(bindings);
					p.lineTo(lx, ly);
					break;
				case 'Q':
					lcx = args.get(0).value(bindings);
					lcy = args.get(1).value(bindings);
					lx = args.get(2).value(bindings);
					ly = args.get(3).value(bindings);
					p.quadTo(lcx, lcy, lx, ly);
					break;
				case 'q':
					lcx = lx + args.get(0).value(bindings);
					lcy = ly + args.get(1).value(bindings);
					lx += args.get(2).value(bindings);
					ly += args.get(3).value(bindings);
					p.quadTo(lcx, lcy, lx, ly);
					break;
				case 'T':
					lcx = lx + lx - lcx;
					lcy = ly + ly - lcy;
					lx = args.get(0).value(bindings);
					ly = args.get(1).value(bindings);
					p.quadTo(lcx, lcy, lx, ly);
					break;
				case 't':
					lcx = lx + lx - lcx;
					lcy = ly + ly - lcy;
					lx += args.get(0).value(bindings);
					ly += args.get(1).value(bindings);
					p.quadTo(lcx, lcy, lx, ly);
					break;
				case 'C':
					ccx = args.get(0).value(bindings);
					ccy = args.get(1).value(bindings);
					lcx = args.get(2).value(bindings);
					lcy = args.get(3).value(bindings);
					lx = args.get(4).value(bindings);
					ly = args.get(5).value(bindings);
					p.curveTo(ccx, ccy, lcx, lcy, lx, ly);
					break;
				case 'c':
					ccx = lx + args.get(0).value(bindings);
					ccy = ly + args.get(1).value(bindings);
					lcx = lx + args.get(2).value(bindings);
					lcy = ly + args.get(3).value(bindings);
					lx = args.get(4).value(bindings);
					ly = args.get(5).value(bindings);
					p.curveTo(ccx, ccy, lcx, lcy, lx, ly);
					break;
				case 'S':
					ccx = lx + lx - lcx;
					ccy = ly + ly - lcy;
					lcx = args.get(0).value(bindings);
					lcy = args.get(1).value(bindings);
					lx = args.get(2).value(bindings);
					ly = args.get(3).value(bindings);
					p.curveTo(ccx, ccy, lcx, lcy, lx, ly);
					break;
				case 's':
					ccx = lx + lx - lcx;
					ccy = ly + ly - lcy;
					lcx = lx + args.get(0).value(bindings);
					lcy = ly + args.get(1).value(bindings);
					lx = args.get(2).value(bindings);
					ly = args.get(3).value(bindings);
					p.curveTo(ccx, ccy, lcx, lcy, lx, ly);
					break;
				case 'A':
					arx = args.get(0).value(bindings);
					ary = args.get(1).value(bindings);
					aa = args.get(2).value(bindings);
					large = (args.get(3).value(bindings) != 0);
					sweep = (args.get(4).value(bindings) != 0);
					lcx = lx = args.get(5).value(bindings);
					lcy = ly = args.get(6).value(bindings);
					arcTo(p, arx, ary, aa, large, sweep, lx, ly);
					break;
				case 'a':
					arx = args.get(0).value(bindings);
					ary = args.get(1).value(bindings);
					aa = args.get(2).value(bindings);
					large = (args.get(3).value(bindings) != 0);
					sweep = (args.get(4).value(bindings) != 0);
					lcx = lx += args.get(5).value(bindings);
					lcy = ly += args.get(6).value(bindings);
					arcTo(p, arx, ary, aa, large, sweep, lx, ly);
					break;
				case 'Z':
				case 'z':
					lcx = lx; lcy = ly;
					p.closePath();
					break;
				case 'R':
					rx = args.get(0).value(bindings);
					ry = args.get(1).value(bindings);
					rw = args.get(2).value(bindings);
					rh = args.get(3).value(bindings);
					rrx = args.get(4).value(bindings);
					rry = args.get(5).value(bindings);
					if (rrx == 0 || rry == 0) {
						p.append(new Rectangle2D.Double(rx, ry, rw, rh), false);
					} else {
						p.append(new RoundRectangle2D.Double(rx, ry, rw, rh, rrx, rry), false);
					}
					p.moveTo(lcx = lx, lcy = ly);
					break;
				case 'r':
					rx = lx + args.get(0).value(bindings);
					ry = ly + args.get(1).value(bindings);
					rw = args.get(2).value(bindings);
					rh = args.get(3).value(bindings);
					rrx = args.get(4).value(bindings);
					rry = args.get(5).value(bindings);
					if (rrx == 0 || rry == 0) {
						p.append(new Rectangle2D.Double(rx, ry, rw, rh), false);
					} else {
						p.append(new RoundRectangle2D.Double(rx, ry, rw, rh, rrx, rry), false);
					}
					p.moveTo(lcx = lx, lcy = ly);
					break;
				case 'E':
					rx = args.get(0).value(bindings);
					ry = args.get(1).value(bindings);
					rw = args.get(2).value(bindings);
					rh = args.get(3).value(bindings);
					ras = args.get(4).value(bindings);
					rae = args.get(5).value(bindings);
					rat = (Math.abs((int)Math.round(args.get(6).value(bindings))) % 3);
					if (rae <= -360 || rae >= 360) {
						p.append(new Ellipse2D.Double(rx, ry, rw, rh), false);
					} else {
						p.append(new Arc2D.Double(rx, ry, rw, rh, ras, rae, rat), false);
					}
					p.moveTo(lcx = lx, lcy = ly);
					break;
				case 'e':
					rx = lx + args.get(0).value(bindings);
					ry = ly + args.get(1).value(bindings);
					rw = args.get(2).value(bindings);
					rh = args.get(3).value(bindings);
					ras = args.get(4).value(bindings);
					rae = args.get(5).value(bindings);
					rat = (Math.abs((int)Math.round(args.get(6).value(bindings))) % 3);
					if (rae <= -360 || rae >= 360) {
						p.append(new Ellipse2D.Double(rx, ry, rw, rh), false);
					} else {
						p.append(new Arc2D.Double(rx, ry, rw, rh, ras, rae, rat), false);
					}
					p.moveTo(lcx = lx, lcy = ly);
					break;
			}
		}
		return p;
	}
	
	@Override
	public boolean equals(Object that) {
		if (that instanceof ParameterizedPath) {
			List<Character> thisOpcodes = this.opcodes;
			List<List<ParameterizedValue>> thisOperands = this.operands;
			List<Character> thatOpcodes = ((ParameterizedPath)that).opcodes;
			List<List<ParameterizedValue>> thatOperands = ((ParameterizedPath)that).operands;
			return thisOpcodes.equals(thatOpcodes) && thisOperands.equals(thatOperands);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return opcodes.hashCode() ^ operands.hashCode();
	}
	
	private static void arcTo(
		GeneralPath p, double rx, double ry, double a,
		boolean large, boolean sweep, double x, double y
	) {
		Point2D p0 = p.getCurrentPoint();
		double x0 = p0.getX();
		double y0 = p0.getY();
		if (x0 == x && y0 == y) return;
		if (rx == 0 || ry == 0) { p.lineTo(x, y); return; }
		double dx2 = (x0 - x) / 2;
		double dy2 = (y0 - y) / 2;
		a = Math.toRadians(a % 360);
		double ca = Math.cos(a);
		double sa = Math.sin(a);
		double x1 = sa * dy2 + ca * dx2;
		double y1 = ca * dy2 - sa * dx2;
		rx = Math.abs(rx);
		ry = Math.abs(ry);
		double Prx = rx * rx;
		double Pry = ry * ry;
		double Px1 = x1 * x1;
		double Py1 = y1 * y1;
		double rc = Px1/Prx + Py1/Pry;
		if (rc > 1) {
			rx = Math.sqrt(rc) * rx;
			ry = Math.sqrt(rc) * ry;
			Prx = rx * rx;
			Pry = ry * ry;
		}
		double s = (large == sweep) ? -1 : 1;
		double sq = ((Prx*Pry)-(Prx*Py1)-(Pry*Px1)) / ((Prx*Py1)+(Pry*Px1));
		if (sq < 0) sq = 0;
		double m = s * Math.sqrt(sq);
		double cx1 = m *  ((rx * y1) / ry);
		double cy1 = m * -((ry * x1) / rx);
		double sx2 = (x0 + x) / 2;
		double sy2 = (y0 + y) / 2;
		double cx = sx2 + ca * cx1 - sa * cy1;
		double cy = sy2 + sa * cx1 + ca * cy1;
		double ux = (x1 - cx1) / rx;
		double uy = (y1 - cy1) / ry;
		double vx = (-x1 -cx1) / rx;
		double vy = (-y1 -cy1) / ry;
		double sn = Math.sqrt(ux*ux + uy*uy);
		double sp = ux;
		double ss = (uy < 0) ? -1 : 1;
		double as = Math.toDegrees(ss * Math.acos(sp / sn));
		double en = Math.sqrt((ux*ux + uy*uy) * (vx*vx + vy*vy));
		double ep = ux * vx + uy * vy;
		double es = (ux * vy - uy * vx < 0) ? -1 : 1;
		double ae = Math.toDegrees(es * Math.acos(ep / en));
		if (!sweep && ae > 0) ae -= 360;
		if (sweep && ae < 0) ae += 360;
		ae %= 360;
		as %= 360;
		Arc2D.Double arc = new Arc2D.Double();
		arc.x = cx - rx;
		arc.y = cy - ry;
		arc.width = rx * 2;
		arc.height = ry * 2;
		arc.start = -as;
		arc.extent = -ae;
		double acx = arc.getCenterX();
		double acy = arc.getCenterY();
		AffineTransform t = AffineTransform.getRotateInstance(a, acx, acy);
		p.append(t.createTransformedShape(arc), true);
	}
}
