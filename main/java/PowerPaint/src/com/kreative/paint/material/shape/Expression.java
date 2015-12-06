package com.kreative.paint.material.shape;

public abstract class Expression {
	public abstract double eval(Bindings bindings);
	public abstract Expression optimize();
	
	public static class Value extends Expression {
		public final double value;
		public Value(double value) {
			this.value = value;
		}
		@Override
		public double eval(Bindings bindings) {
			return value;
		}
		@Override
		public Expression optimize() {
			return this;
		}
	}
	
	public static class Binding extends Expression {
		public final String key;
		public Binding(String key) {
			this.key = key;
		}
		@Override
		public double eval(Bindings bindings) {
			return bindings.get(key);
		}
		@Override
		public Expression optimize() {
			return this;
		}
	}
	
	public static class Unary extends Expression {
		public final Function function;
		public final Expression argument;
		public Unary(Function function, Expression argument) {
			this.function = function;
			this.argument = argument;
		}
		@Override
		public double eval(Bindings bindings) {
			return function.eval(argument.eval(bindings));
		}
		@Override
		public Expression optimize() {
			Expression a = argument.optimize();
			if (a instanceof Value) {
				double v = ((Value)a).value;
				v = function.eval(v);
				return new Value(v);
			} else {
				return new Unary(function, a);
			}
		}
	}
	
	public static class Binary extends Expression {
		public final Operator operator;
		public final Expression left;
		public final Expression right;
		public Binary(Operator operator, Expression left, Expression right) {
			this.operator = operator;
			this.left = left;
			this.right = right;
		}
		@Override
		public double eval(Bindings bindings) {
			return operator.eval(left.eval(bindings), right.eval(bindings));
		}
		@Override
		public Expression optimize() {
			Expression l = left.optimize();
			Expression r = right.optimize();
			if (l instanceof Value && r instanceof Value) {
				double lv = ((Value)l).value;
				double rv = ((Value)r).value;
				double v = operator.eval(lv, rv);
				return new Value(v);
			} else {
				return new Binary(operator, l, r);
			}
		}
	}
	
	public static class Conditional extends Expression {
		public final Expression ifexpr;
		public final Expression thenexpr;
		public final Expression elseexpr;
		public Conditional(Expression ifexpr, Expression thenexpr, Expression elseexpr) {
			this.ifexpr = ifexpr;
			this.thenexpr = thenexpr;
			this.elseexpr = elseexpr;
		}
		@Override
		public double eval(Bindings bindings) {
			return ((ifexpr.eval(bindings) != 0) ? thenexpr : elseexpr).eval(bindings);
		}
		@Override
		public Expression optimize() {
			Expression ie = ifexpr.optimize();
			Expression te = thenexpr.optimize();
			Expression ee = elseexpr.optimize();
			if (ie instanceof Value) {
				return (((Value)ie).value != 0) ? te : ee;
			} else {
				return new Conditional(ie, te, ee);
			}
		}
	}
}
