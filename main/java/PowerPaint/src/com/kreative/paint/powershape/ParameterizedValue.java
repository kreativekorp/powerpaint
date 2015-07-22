package com.kreative.paint.powershape;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class ParameterizedValue {
	public static final NumberFormat NUMBER_FORMAT = new DecimalFormat("0.################################");
	
	public final String source;
	public final Expression expr;
	
	public ParameterizedValue(String source, Expression expr) {
		this.source = source;
		this.expr = expr;
	}
	
	public ParameterizedValue(String source) {
		this.source = source;
		this.expr = new ExpressionParser(source).parse().optimize();
	}
	
	public ParameterizedValue(double value) {
		this.source = NUMBER_FORMAT.format(value);
		this.expr = new Expression.Value(value);
	}
	
	public double value(Bindings bindings) {
		return expr.eval(bindings);
	}
	
	@Override
	public boolean equals(Object that) {
		return (that instanceof ParameterizedValue)
		    && (this.source.equals(((ParameterizedValue)that).source));
	}
	
	@Override
	public int hashCode() {
		return source.hashCode();
	}
}
