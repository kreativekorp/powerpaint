/*
 * Copyright &copy; 2009-2010 Rebecca G. Bettencourt / Kreative Software
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

package com.kreative.paint.geom;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class ParameterizedPoint implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final NumberFormat FORMAT = new DecimalFormat("0.################################");
	
	private String xexpr;
	private String yexpr;
	private Expression cxexpr;
	private Expression cyexpr;
	
	public ParameterizedPoint(String x, String y) {
		this.xexpr = x;
		this.yexpr = y;
		this.cxexpr = new Parser(x).parse().optimize();
		this.cyexpr = new Parser(y).parse().optimize();
	}
	
	public ParameterizedPoint(double x, double y) {
		this.xexpr = FORMAT.format(x);
		this.yexpr = FORMAT.format(y);
		this.cxexpr = new NumericExpression(x);
		this.cyexpr = new NumericExpression(y);
	}
	
	public ParameterizedPoint clone() {
		return new ParameterizedPoint(xexpr, yexpr);
	}
	
	public String getXExpression() {
		return xexpr;
	}
	
	public String getYExpression() {
		return yexpr;
	}
	
	public boolean hasXValue() {
		return (cxexpr instanceof NumericExpression);
	}
	
	public boolean hasYValue() {
		return (cyexpr instanceof NumericExpression);
	}
	
	public double getXValue() {
		return (cxexpr instanceof NumericExpression) ? ((NumericExpression)cxexpr).v : Double.NaN;
	}
	
	public double getYValue() {
		return (cyexpr instanceof NumericExpression) ? ((NumericExpression)cyexpr).v : Double.NaN;
	}
	
	public Point2D getLocation(Map<String,ParameterPoint> formalParameters, Map<String,Point2D> actualParameters) {
		double x = cxexpr.evaluate(formalParameters, actualParameters);
		double y = cyexpr.evaluate(formalParameters, actualParameters);
		return new Point2D.Double(x, y);
	}
	
	private static interface Expression {
		public double evaluate(Map<String,ParameterPoint> fp, Map<String,Point2D> ap);
		public Expression optimize();
	}
	
	private static class NumericExpression implements Expression {
		private double v;
		public NumericExpression(double v) { this.v = v; }
		public double evaluate(Map<String,ParameterPoint> fp, Map<String,Point2D> ap) { return v; }
		public Expression optimize() { return this; }
	}
	
	private static class ParameterExpression implements Expression {
		private String param;
		private String prop;
		public ParameterExpression(String param, String prop) { this.param = param; this.prop = prop; }
		public double evaluate(Map<String,ParameterPoint> fp, Map<String,Point2D> ap) { return fp.get(param).getValue(ap, prop); }
		public Expression optimize() { return this; }
	}
	
	private static interface BinaryOperator { public double eval(double a, double b); }
	private static TreeMap<Integer,LinkedHashMap<String,BinaryOperator>> operators = new TreeMap<Integer,LinkedHashMap<String,BinaryOperator>>();
	static {
		// BOOLEAN OR
		operators.put(0, new LinkedHashMap<String,BinaryOperator>());
		operators.get(0).put("|", new BinaryOperator(){ public double eval(double a, double b){ return (a!=0 || b!=0)?1:0; } });
		// BOOLEAN XOR
		operators.put(1, new LinkedHashMap<String,BinaryOperator>());
		operators.get(1).put("`", new BinaryOperator(){ public double eval(double a, double b){ return ((a!=0) != (b!=0))?1:0; } });
		// BOOLEAN AND
		operators.put(2, new LinkedHashMap<String,BinaryOperator>());
		operators.get(2).put("&", new BinaryOperator(){ public double eval(double a, double b){ return (a!=0 && b!=0)?1:0; } });
		// EQUIVALENCE
		operators.put(10, new LinkedHashMap<String,BinaryOperator>());
		operators.get(10).put("#", new BinaryOperator(){ public double eval(double a, double b){ return Double.compare(a, b); } });
		operators.get(10).put("=", new BinaryOperator(){ public double eval(double a, double b){ return (a==b)?1:0; } });
		operators.get(10).put("\u2260", new BinaryOperator(){ public double eval(double a, double b){ return (a!=b)?1:0; } });
		// RELATIONAL
		operators.put(15, new LinkedHashMap<String,BinaryOperator>());
		operators.get(15).put("<", new BinaryOperator(){ public double eval(double a, double b){ return (a<b)?1:0; } });
		operators.get(15).put(">", new BinaryOperator(){ public double eval(double a, double b){ return (a>b)?1:0; } });
		operators.get(15).put("\u2264", new BinaryOperator(){ public double eval(double a, double b){ return (a<=b)?1:0; } });
		operators.get(15).put("\u2265", new BinaryOperator(){ public double eval(double a, double b){ return (a>=b)?1:0; } });
		// ADDITION
		operators.put(30, new LinkedHashMap<String,BinaryOperator>());
		operators.get(30).put("+", new BinaryOperator(){ public double eval(double a, double b){ return a+b; } });
		operators.get(30).put("-", new BinaryOperator(){ public double eval(double a, double b){ return a-b; } });
		operators.get(30).put("~", new BinaryOperator(){ public double eval(double a, double b){ return b-a; } });
		// HYPOT
		operators.put(32, new LinkedHashMap<String,BinaryOperator>());
		operators.get(32).put("@", new BinaryOperator(){ public double eval(double a, double b){ return Math.hypot(a,b); } });
		operators.get(32).put("$", new BinaryOperator(){ public double eval(double a, double b){ return Math.atan2(a,b); } });
		// MULTIPLICATION
		operators.put(35, new LinkedHashMap<String,BinaryOperator>());
		operators.get(35).put("*", new BinaryOperator(){ public double eval(double a, double b){ return a*b; } });
		operators.get(35).put("/", new BinaryOperator(){ public double eval(double a, double b){ return a/b; } });
		operators.get(35).put("\\", new BinaryOperator(){ public double eval(double a, double b){ return b/a; } });
		operators.get(35).put("%", new BinaryOperator(){ public double eval(double a, double b){ return a%b; } });
		// EXPONENTIATION
		operators.put(50, new LinkedHashMap<String,BinaryOperator>());
		operators.get(50).put("^", new BinaryOperator(){ public double eval(double a, double b){ return Math.pow(a,b); } });
		operators.get(50).put("\u221A", new BinaryOperator(){ public double eval(double a, double b){ return Math.pow(b,1/a); } });
		operators.get(50).put("_", new BinaryOperator(){ public double eval(double a, double b){ return Math.log(a)/Math.log(b); } });
	}
	
	private static class BinaryExpression implements Expression {
		private Expression e1;
		private Expression e2;
		private BinaryOperator op;
		public BinaryExpression(Expression e1, Expression e2, BinaryOperator op) { this.e1 = e1; this.e2 = e2; this.op = op; }
		public double evaluate(Map<String,ParameterPoint> fp, Map<String,Point2D> ap) { return op.eval(e1.evaluate(fp, ap), e2.evaluate(fp, ap)); }
		public Expression optimize() {
			e1 = e1.optimize();
			e2 = e2.optimize();
			if (e1 instanceof NumericExpression && e2 instanceof NumericExpression) {
				return new NumericExpression(op.eval(((NumericExpression)e1).v, ((NumericExpression)e2).v));
			} else {
				return this;
			}
		}
	}
	
	private static interface Function { public double eval(double v); }
	private static Map<String,Function> functions = new HashMap<String,Function>();
	static {
		functions.put("abs", new Function(){ public double eval(double v){ return Math.abs(v); } });
		functions.put("sgn", new Function(){ public double eval(double v){ return Math.signum(v); } });
		functions.put("ceil", new Function(){ public double eval(double v){ return Math.ceil(v); } });
		functions.put("floor", new Function(){ public double eval(double v){ return Math.floor(v); } });
		functions.put("round", new Function(){ public double eval(double v){ return Math.round(v); } });
		functions.put("trunc", new Function(){ public double eval(double v){ return (v<0)?Math.ceil(v):Math.floor(v); } });
		functions.put("sqrt", new Function(){ public double eval(double v){ return Math.sqrt(v); } });
		functions.put("cbrt", new Function(){ public double eval(double v){ return Math.cbrt(v); } });
		functions.put("qtrt", new Function(){ public double eval(double v){ return Math.sqrt(Math.sqrt(v)); } });
		functions.put("ln", new Function(){ public double eval(double v){ return Math.log(v); } });
		functions.put("log", new Function(){ public double eval(double v){ return Math.log(v); } });
		functions.put("log2", new Function(){ public double eval(double v){ return Math.log(v)/Math.log(2); } });
		functions.put("log10", new Function(){ public double eval(double v){ return Math.log10(v); } });
		functions.put("exp", new Function(){ public double eval(double v){ return Math.exp(v); } });
		functions.put("exp2", new Function(){ public double eval(double v){ return Math.pow(2,v); } });
		functions.put("exp10", new Function(){ public double eval(double v){ return Math.pow(10,v); } });
		functions.put("sin", new Function(){ public double eval(double v){ return Math.sin(v); } });
		functions.put("cos", new Function(){ public double eval(double v){ return Math.cos(v); } });
		functions.put("tan", new Function(){ public double eval(double v){ return Math.tan(v); } });
		functions.put("asin", new Function(){ public double eval(double v){ return Math.asin(v); } });
		functions.put("acos", new Function(){ public double eval(double v){ return Math.acos(v); } });
		functions.put("atan", new Function(){ public double eval(double v){ return Math.atan(v); } });
		functions.put("sinh", new Function(){ public double eval(double v){ return Math.sinh(v); } });
		functions.put("cosh", new Function(){ public double eval(double v){ return Math.cosh(v); } });
		functions.put("tanh", new Function(){ public double eval(double v){ return Math.tanh(v); } });
		functions.put("neg", new Function(){ public double eval(double v){ return -v; } });
		functions.put("inv", new Function(){ public double eval(double v){ return 1/v; } });
		functions.put("sq", new Function(){ public double eval(double v){ return v*v; } });
		functions.put("cb", new Function(){ public double eval(double v){ return v*v*v; } });
		functions.put("bool", new Function(){ public double eval(double v){ return (v!=0)?1:0; } });
		functions.put("not", new Function(){ public double eval(double v){ return (v!=0)?0:1; } });
		functions.put("+", new Function(){ public double eval(double v){ return v; } });
		functions.put("-", new Function(){ public double eval(double v){ return -v; } });
		functions.put("!", new Function(){ public double eval(double v){ return (v!=0)?0:1; } });
		functions.put("\u221A", new Function(){ public double eval(double v){ return Math.sqrt(v); } });
		functions.put("\u221B", new Function(){ public double eval(double v){ return Math.cbrt(v); } });
		functions.put("\u221C", new Function(){ public double eval(double v){ return Math.sqrt(Math.sqrt(v)); } });
	}
	
	private static class FunctionExpression implements Expression {
		private Expression arg;
		private Function func;
		public FunctionExpression(Expression arg, Function func) { this.arg = arg; this.func = func; }
		public double evaluate(Map<String,ParameterPoint> fp, Map<String,Point2D> ap) { return func.eval(arg.evaluate(fp, ap)); }
		public Expression optimize() {
			arg = arg.optimize();
			if (arg instanceof NumericExpression) {
				return new NumericExpression(func.eval(((NumericExpression)arg).v));
			} else {
				return this;
			}
		}
	}
	
	private static class ConditionalExpression implements Expression {
		private Expression ifex, thenex, elseex;
		public ConditionalExpression(Expression ifex, Expression thenex, Expression elseex) { this.ifex = ifex; this.thenex = thenex; this.elseex = elseex; }
		public double evaluate(Map<String,ParameterPoint> fp, Map<String,Point2D> ap) { return (ifex.evaluate(fp,ap) != 0) ? thenex.evaluate(fp,ap) : elseex.evaluate(fp,ap); }
		public Expression optimize() {
			ifex = ifex.optimize();
			thenex = thenex.optimize();
			elseex = elseex.optimize();
			if (ifex instanceof NumericExpression) {
				return (((NumericExpression)ifex).v != 0) ? thenex : elseex;
			} else {
				return this;
			}
		}
	}
	
	private static class Lexer {
		private char[] s;
		private int i;
		public Lexer(String s) {
			this.s = s.toCharArray();
			this.i = 0;
		}
		public boolean hasNext() {
			while (i < s.length && Character.isWhitespace(s[i])) i++;
			return (i < s.length);
		}
		public String getNext() {
			while (i < s.length && Character.isWhitespace(s[i])) i++;
			if (i >= s.length) {
				return null;
			} else if (Character.isDigit(s[i]) || s[i] == '.') {
				int start = i;
				while (i < s.length && (Character.isDigit(s[i]) || s[i] == '.')) i++;
				return new String(s, start, i-start);
			} else if (Character.isLetter(s[i])) {
				int start = i;
				while (i < s.length && (Character.isLetter(s[i]) || Character.isDigit(s[i]) || s[i] == '.')) i++;
				return new String(s, start, i-start);
			} else {
				return new String(s, i++, 1);
			}
		}
		public String lookNext() {
			while (i < s.length && Character.isWhitespace(s[i])) i++;
			if (i >= s.length) {
				return null;
			} else if (Character.isDigit(s[i]) || s[i] == '.') {
				int end = i;
				while (end < s.length && (Character.isDigit(s[end]) || s[end] == '.')) end++;
				return new String(s, i, end-i);
			} else if (Character.isLetter(s[i])) {
				int end = i;
				while (end < s.length && (Character.isLetter(s[end]) || Character.isDigit(s[end]) || s[end] == '.')) end++;
				return new String(s, i, end-i);
			} else {
				return new String(s, i, 1);
			}
		}
	}
	
	private static class Parser {
		private Lexer lex;
		private int[] prec;
		public Parser(String s) {
			lex = new Lexer(s);
			Integer[] preprec = operators.keySet().toArray(new Integer[0]);
			prec = new int[preprec.length];
			for (int i = 0; i < preprec.length; i++) prec[i] = preprec[i].intValue();
			Arrays.sort(prec);
		}
		private Expression parseFactor() {
			String s = lex.getNext();
			if (s == null) {
				throw new RuntimeException("Expected factor here.");
			} else if (s.equals("(")) {
				Expression e = parseExpression();
				String z = lex.getNext();
				if (z == null || !z.equals(")")) throw new RuntimeException("Expected closing parenthesis here.");
				else return e;
			} else if (s.equals("[")) {
				Expression e = parseExpression();
				String z = lex.getNext();
				if (z == null || !z.equals("]")) throw new RuntimeException("Expected closing bracket here.");
				else return e;
			} else if (s.equals("{")) {
				Expression e = parseExpression();
				String z = lex.getNext();
				if (z == null || !z.equals("}")) throw new RuntimeException("Expected closing brace here.");
				else return e;
			} else if (functions.containsKey(s)) {
				return new FunctionExpression(parseFactor(), functions.get(s));
			} else if (s.equals("\u221E") || s.equalsIgnoreCase("inf") || s.equalsIgnoreCase("infinity")) {
				return new NumericExpression(Double.POSITIVE_INFINITY);
			} else if (s.equals("\u03A6") || s.equalsIgnoreCase("nan")) {
				return new NumericExpression(Double.NaN);
			} else if (s.equals("\u03C0") || s.equalsIgnoreCase("pi")) {
				return new NumericExpression(Math.PI);
			} else if (s.equals("\u212E") || s.equalsIgnoreCase("e")) {
				return new NumericExpression(Math.E);
			} else if (Character.isDigit(s.charAt(0)) || s.charAt(0) == '.') {
				try {
					return new NumericExpression(Double.parseDouble(s));
				} catch (NumberFormatException nfe) {
					String[] ss = s.split("\\.", 2);
					return new ParameterExpression(ss[0], ((ss.length > 1) ? ss[1] : ""));
				}
			} else if (Character.isLetter(s.charAt(0))) {
				String[] ss = s.split("\\.", 2);
				return new ParameterExpression(ss[0], ((ss.length > 1) ? ss[1] : ""));
			} else {
				throw new RuntimeException("Expected factor here.");
			}
		}
		private Expression parseBinaryExpression(int preci) {
			if (preci < prec.length) {
				Expression left = parseBinaryExpression(preci+1);
				while (lex.hasNext() && operators.get(prec[preci]).containsKey(lex.lookNext())) {
					BinaryOperator op = operators.get(prec[preci]).get(lex.getNext());
					Expression right = parseBinaryExpression(preci+1);
					left = new BinaryExpression(left, right, op);
				}
				return left;
			} else {
				return parseFactor();
			}
		}
		private Expression parseExpression() {
			Expression ifex = parseBinaryExpression(0);
			if (lex.hasNext() && lex.lookNext().equals("?")) {
				lex.getNext();
				Expression thenex = parseExpression();
				if (lex.hasNext() && lex.lookNext().equals(":")) {
					lex.getNext();
					Expression elseex = parseExpression();
					return new ConditionalExpression(ifex, thenex, elseex);
				} else {
					return new ConditionalExpression(ifex, thenex, new NumericExpression(Double.NaN));
				}
			} else {
				return ifex;
			}
		}
		public Expression parse() {
			Expression e = parseExpression();
			if (lex.hasNext()) {
				throw new RuntimeException("Expected end of expression here.");
			}
			return e;
		}
	}
}
