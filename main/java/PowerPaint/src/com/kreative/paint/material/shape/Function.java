package com.kreative.paint.material.shape;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class Function {
	public abstract double eval(double v);
	
	public static final Function pos = new Function() {
		@Override
		public double eval(double v) {
			return +v;
		}
	};
	
	public static final Function neg = new Function() {
		@Override
		public double eval(double v) {
			return -v;
		}
	};
	
	public static final Function bool = new Function() {
		@Override
		public double eval(double v) {
			return (v != 0) ? 1 : 0;
		}
	};
	
	public static final Function not = new Function() {
		@Override
		public double eval(double v) {
			return (v != 0) ? 0 : 1;
		}
	};
	
	public static final Function abs = new Function() {
		@Override
		public double eval(double v) {
			return Math.abs(v);
		}
	};
	
	public static final Function signum = new Function() {
		@Override
		public double eval(double v) {
			return Math.signum(v);
		}
	};
	
	public static final Function ceil = new Function() {
		@Override
		public double eval(double v) {
			return Math.ceil(v);
		}
	};
	
	public static final Function floor = new Function() {
		@Override
		public double eval(double v) {
			return Math.floor(v);
		}
	};
	
	public static final Function round = new Function() {
		@Override
		public double eval(double v) {
			return Math.round(v);
		}
	};
	
	public static final Function trunc = new Function() {
		@Override
		public double eval(double v) {
			if (v < 0) return Math.ceil(v);
			else return Math.floor(v);
		}
	};
	
	public static final Function frac = new Function() {
		@Override
		public double eval(double v) {
			if (v < 0) return v - Math.ceil(v);
			else return v - Math.floor(v);
		}
	};
	
	public static final Function toDegrees = new Function() {
		@Override
		public double eval(double v) {
			return Math.toDegrees(v);
		}
	};
	
	public static final Function toRadians = new Function() {
		@Override
		public double eval(double v) {
			return Math.toRadians(v);
		}
	};
	
	public static final Function sqrt = new Function() {
		@Override
		public double eval(double v) {
			return Math.sqrt(v);
		}
	};
	
	public static final Function cbrt = new Function() {
		@Override
		public double eval(double v) {
			return Math.cbrt(v);
		}
	};
	
	public static final Function qtrt = new Function() {
		@Override
		public double eval(double v) {
			return Math.sqrt(Math.sqrt(v));
		}
	};
	
	public static final Function sq = new Function() {
		@Override
		public double eval(double v) {
			return v * v;
		}
	};
	
	public static final Function cb = new Function() {
		@Override
		public double eval(double v) {
			return v * v * v;
		}
	};
	
	public static final Function qt = new Function() {
		@Override
		public double eval(double v) {
			return v * v * v * v;
		}
	};
	
	public static final Function log = new Function() {
		@Override
		public double eval(double v) {
			return Math.log(v);
		}
	};
	
	public static final Function log2 = new Function() {
		@Override
		public double eval(double v) {
			return Math.log(v) / Math.log(2);
		}
	};
	
	public static final Function log10 = new Function() {
		@Override
		public double eval(double v) {
			return Math.log10(v);
		}
	};
	
	public static final Function log1p = new Function() {
		@Override
		public double eval(double v) {
			return Math.log1p(v);
		}
	};
	
	public static final Function exp = new Function() {
		@Override
		public double eval(double v) {
			return Math.exp(v);
		}
	};
	
	public static final Function exp2 = new Function() {
		@Override
		public double eval(double v) {
			return Math.pow(2, v);
		}
	};
	
	public static final Function exp10 = new Function() {
		@Override
		public double eval(double v) {
			return Math.pow(10, v);
		}
	};
	
	public static final Function expm1 = new Function() {
		@Override
		public double eval(double v) {
			return Math.expm1(v);
		}
	};
	
	public static final Function sin = new Function() {
		@Override
		public double eval(double v) {
			return Math.sin(v);
		}
	};
	
	public static final Function cos = new Function() {
		@Override
		public double eval(double v) {
			return Math.cos(v);
		}
	};
	
	public static final Function tan = new Function() {
		@Override
		public double eval(double v) {
			return Math.tan(v);
		}
	};
	
	public static final Function cot = new Function() {
		@Override
		public double eval(double v) {
			return 1 / Math.tan(v);
		}
	};
	
	public static final Function sec = new Function() {
		@Override
		public double eval(double v) {
			return 1 / Math.cos(v);
		}
	};
	
	public static final Function csc = new Function() {
		@Override
		public double eval(double v) {
			return 1 / Math.sin(v);
		}
	};
	
	public static final Function asin = new Function() {
		@Override
		public double eval(double v) {
			return Math.asin(v);
		}
	};
	
	public static final Function acos = new Function() {
		@Override
		public double eval(double v) {
			return Math.acos(v);
		}
	};
	
	public static final Function atan = new Function() {
		@Override
		public double eval(double v) {
			return Math.atan(v);
		}
	};
	
	public static final Function acot = new Function() {
		@Override
		public double eval(double v) {
			return Math.atan(1 / v);
		}
	};
	
	public static final Function asec = new Function() {
		@Override
		public double eval(double v) {
			return Math.acos(1 / v);
		}
	};
	
	public static final Function acsc = new Function() {
		@Override
		public double eval(double v) {
			return Math.asin(1 / v);
		}
	};
	
	public static final Function sinh = new Function() {
		@Override
		public double eval(double v) {
			return Math.sinh(v);
		}
	};
	
	public static final Function cosh = new Function() {
		@Override
		public double eval(double v) {
			return Math.cosh(v);
		}
	};
	
	public static final Function tanh = new Function() {
		@Override
		public double eval(double v) {
			return Math.tanh(v);
		}
	};
	
	public static final Function coth = new Function() {
		@Override
		public double eval(double v) {
			return 1 / Math.tanh(v);
		}
	};
	
	public static final Function sech = new Function() {
		@Override
		public double eval(double v) {
			return 1 / Math.cosh(v);
		}
	};
	
	public static final Function csch = new Function() {
		@Override
		public double eval(double v) {
			return 1 / Math.sinh(v);
		}
	};
	
	public static final Function asinh = new Function() {
		@Override
		public double eval(double v) {
			return Math.log(v + Math.sqrt(v * v + 1));
		}
	};
	
	public static final Function acosh = new Function() {
		@Override
		public double eval(double v) {
			return Math.log(v + Math.sqrt(v + 1) * Math.sqrt(v - 1));
		}
	};
	
	public static final Function atanh = new Function() {
		@Override
		public double eval(double v) {
			return (Math.log(1 + v) - Math.log(1 - v)) / 2;
		}
	};
	
	public static final Function acoth = new Function() {
		@Override
		public double eval(double v) {
			return (Math.log(1 + 1/v) - Math.log(1 - 1/v)) / 2;
		}
	};
	
	public static final Function asech = new Function() {
		@Override
		public double eval(double v) {
			return Math.log(1/v + Math.sqrt(1/v + 1) * Math.sqrt(1/v - 1));
		}
	};
	
	public static final Function acsch = new Function() {
		@Override
		public double eval(double v) {
			return Math.log(1/v + Math.sqrt(1 + 1 / (v * v)));
		}
	};
	
	public static final Map<String,Function> functions;
	static {
		Map<String,Function> f = new HashMap<String,Function>();
		f.put("pos", pos);
		f.put("neg", neg);
		f.put("bool", bool);
		f.put("not", not);
		f.put("abs", abs);
		f.put("sgn", signum);
		f.put("signum", signum);
		f.put("ceil", ceil);
		f.put("floor", floor);
		f.put("round", round);
		f.put("trunc", trunc);
		f.put("frac", frac);
		f.put("todeg", toDegrees);
		f.put("todegrees", toDegrees);
		f.put("torad", toRadians);
		f.put("toradians", toRadians);
		f.put("sqrt", sqrt);
		f.put("cbrt", cbrt);
		f.put("qtrt", qtrt);
		f.put("sq", sq);
		f.put("cb", cb);
		f.put("qt", qt);
		f.put("ln", log);
		f.put("log", log);
		f.put("log2", log);
		f.put("log10", log10);
		f.put("ln1", log1p);
		f.put("ln1p", log1p);
		f.put("log1", log1p);
		f.put("log1p", log1p);
		f.put("exp", exp);
		f.put("exp2", exp2);
		f.put("exp10", exp10);
		f.put("exp1", expm1);
		f.put("expm1", expm1);
		f.put("sin", sin);
		f.put("cos", cos);
		f.put("tan", tan);
		f.put("cot", cot);
		f.put("sec", sec);
		f.put("csc", csc);
		f.put("asin", asin);
		f.put("acos", acos);
		f.put("atan", atan);
		f.put("acot", acot);
		f.put("asec", asec);
		f.put("acsc", acsc);
		f.put("sinh", sinh);
		f.put("cosh", cosh);
		f.put("tanh", tanh);
		f.put("coth", coth);
		f.put("sech", sech);
		f.put("csch", csch);
		f.put("asinh", asinh);
		f.put("acosh", acosh);
		f.put("atanh", atanh);
		f.put("acoth", acoth);
		f.put("asech", asech);
		f.put("acsch", acsch);
		f.put("+", pos);
		f.put("-", neg);
		f.put("!!", bool);
		f.put("!", not);
		f.put("\u221A", sqrt);
		f.put("\u221B", cbrt);
		f.put("\u221C", qtrt);
		functions = Collections.unmodifiableMap(f);
	}
}
