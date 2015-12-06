package com.kreative.paint.material.shape;

public class ExpressionParserException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public ExpressionParserException(String s) {
		super(s);
	}
	
	public static ExpressionParserException expected(String e, String a) {
		if (e == null) e = "end of line";
		if (a == null) a = "end of line";
		else a = "\"" + a + "\"";
		return new ExpressionParserException(
			"Expected " + e + " but found " + a + "."
		);
	}
}
