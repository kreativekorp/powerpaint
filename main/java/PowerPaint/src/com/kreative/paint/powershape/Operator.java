package com.kreative.paint.powershape;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class Operator {
	public final int precedence;
	
	private Operator(int precedence) {
		this.precedence = precedence;
	}
	
	public abstract double eval(double a, double b);
	
	public static final Operator or = new Operator(0) {
		@Override
		public double eval(double a, double b) {
			return (a != 0 || b != 0) ? 1 : 0;
		}
	};
	
	public static final Operator and = new Operator(5) {
		@Override
		public double eval(double a, double b) {
			return (a != 0 && b != 0) ? 1 : 0;
		}
	};
	
	public static final Operator cmp = new Operator(10) {
		@Override
		public double eval(double a, double b) {
			return Double.compare(a, b);
		}
	};
	
	public static final Operator eq = new Operator(10) {
		@Override
		public double eval(double a, double b) {
			return (a == b) ? 1 : 0;
		}
	};
	
	public static final Operator ne = new Operator(10) {
		@Override
		public double eval(double a, double b) {
			return (a != b) ? 1 : 0;
		}
	};
	
	public static final Operator lt = new Operator(15) {
		@Override
		public double eval(double a, double b) {
			return (a < b) ? 1 : 0;
		}
	};
	
	public static final Operator gt = new Operator(15) {
		@Override
		public double eval(double a, double b) {
			return (a > b) ? 1 : 0;
		}
	};
	
	public static final Operator le = new Operator(15) {
		@Override
		public double eval(double a, double b) {
			return (a <= b) ? 1 : 0;
		}
	};
	
	public static final Operator ge = new Operator(15) {
		@Override
		public double eval(double a, double b) {
			return (a >= b) ? 1 : 0;
		}
	};
	
	public static final Operator add = new Operator(20) {
		@Override
		public double eval(double a, double b) {
			return a + b;
		}
	};
	
	public static final Operator sub = new Operator(20) {
		@Override
		public double eval(double a, double b) {
			return a - b;
		}
	};
	
	public static final Operator hypot = new Operator(25) {
		@Override
		public double eval(double a, double b) {
			return Math.hypot(a, b);
		}
	};
	
	public static final Operator atan2 = new Operator(25) {
		@Override
		public double eval(double a, double b) {
			return Math.atan2(a, b);
		}
	};
	
	public static final Operator cos2 = new Operator(25) {
		@Override
		public double eval(double a, double b) {
			return b * Math.cos(a);
		}
	};
	
	public static final Operator sin2 = new Operator(25) {
		@Override
		public double eval(double a, double b) {
			return b * Math.sin(a);
		}
	};
	
	public static final Operator mul = new Operator(30) {
		@Override
		public double eval(double a, double b) {
			return a * b;
		}
	};
	
	public static final Operator div = new Operator(30) {
		@Override
		public double eval(double a, double b) {
			return a / b;
		}
	};
	
	public static final Operator idiv = new Operator(30) {
		@Override
		public double eval(double a, double b) {
			return Math.floor(a / b);
		}
	};
	
	public static final Operator mod = new Operator(30) {
		@Override
		public double eval(double a, double b) {
			return a - b * Math.floor(a / b);
		}
	};
	
	public static final Operator quot = new Operator(30) {
		@Override
		public double eval(double a, double b) {
			double q = a / b;
			if (q < 0) return Math.ceil(q);
			else return Math.floor(q);
		}
	};
	
	public static final Operator rem = new Operator(30) {
		@Override
		public double eval(double a, double b) {
			double q = a / b;
			if (q < 0) q = Math.ceil(q);
			else q = Math.floor(q);
			return a - b * q;
		}
	};
	
	public static final Operator pow = new Operator(40) {
		@Override
		public double eval(double a, double b) {
			return Math.pow(a, b);
		}
	};
	
	public static final Operator root = new Operator(40) {
		@Override
		public double eval(double a, double b) {
			return Math.pow(a, 1 / b);
		}
	};
	
	public static final Operator log = new Operator(40) {
		@Override
		public double eval(double a, double b) {
			return Math.log(a) / Math.log(b);
		}
	};
	
	public static final Map<Integer,Map<String,Operator>> operators;
	static {
		Map<Integer,Map<String,Operator>> ops1 = new HashMap<Integer,Map<String,Operator>>();
		add(ops1, or, "|", "||", "or");
		add(ops1, and, "&", "&&", "and");
		add(ops1, cmp, "#", "<=>", "cmp");
		add(ops1, eq, "=", "==");
		add(ops1, ne, "\u2260", "!=", "<>");
		add(ops1, lt, "<");
		add(ops1, gt, ">");
		add(ops1, le, "\u2264", "<=");
		add(ops1, ge, "\u2265", ">=");
		add(ops1, add, "+");
		add(ops1, sub, "-");
		add(ops1, hypot, "@", "hypot");
		add(ops1, atan2, "$", "atan2");
		add(ops1, cos2, "cos2");
		add(ops1, sin2, "sin2");
		add(ops1, mul, "*");
		add(ops1, div, "/");
		add(ops1, idiv, "\\", "//", "div");
		add(ops1, mod, "%", "mod");
		add(ops1, quot, "quot");
		add(ops1, rem, "rem");
		add(ops1, pow, "^", "**", "pow");
		add(ops1, root, "!", "root");
		add(ops1, log, "_", "log");
		Map<Integer,Map<String,Operator>> ops2 = new HashMap<Integer,Map<String,Operator>>();
		for (Map.Entry<Integer,Map<String,Operator>> e : ops1.entrySet()) {
			ops2.put(e.getKey(), Collections.unmodifiableMap(e.getValue()));
		}
		operators = Collections.unmodifiableMap(ops2);
	};
	
	private static void add(Map<Integer,Map<String,Operator>> ops, Operator op, String... names) {
		if (ops.containsKey(op.precedence)) {
			Map<String,Operator> o = ops.get(op.precedence);
			for (String name : names) o.put(name, op);
		} else {
			Map<String,Operator> o = new HashMap<String,Operator>();
			for (String name : names) o.put(name, op);
			ops.put(op.precedence, o);
		}
	}
}
