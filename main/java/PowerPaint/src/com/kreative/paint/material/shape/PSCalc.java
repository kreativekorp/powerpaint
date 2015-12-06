package com.kreative.paint.material.shape;

import java.util.HashMap;
import java.util.Map;

public class PSCalc {
	public static void main(String[] args) {
		Bindings bindings = new Bindings() {
			private Map<String,Double> map = new HashMap<String,Double>();
			@Override
			public double get(String key) {
				return map.containsKey(key) ? map.get(key) : Double.NaN;
			}
			@Override
			public void set(String key, double value) {
				map.put(key, value);
			}
			@Override
			public void remove(String key) {
				map.remove(key);
			}
		};
		StringBuffer s = new StringBuffer();
		for (String arg : args) {
			if (arg.startsWith("-D")) {
				if (s.length() > 0) {
					parseEvalPrint(s.toString(), bindings);
					s = new StringBuffer();
				}
				arg = arg.substring(2);
				int i = arg.indexOf('=');
				if (i < 0) {
					bindings.remove(arg);
				} else {
					String key = arg.substring(0, i);
					String valueString = arg.substring(i + 1);
					try {
						double value = Double.parseDouble(valueString);
						bindings.set(key, value);
					} catch (NumberFormatException e) {
						bindings.remove(key);
					}
				}
			} else {
				if (s.length() > 0) s.append(" ");
				s.append(arg);
			}
		}
		if (s.length() > 0) parseEvalPrint(s.toString(), bindings);
	}
	
	private static void parseEvalPrint(String s, Bindings bindings) {
		ExpressionLexer lexer = new ExpressionLexer(s);
		ExpressionParser parser = new ExpressionParser(lexer);
		while (lexer.hasNext()) {
			try {
				Expression e = parser.parseExpression();
				double v = e.eval(bindings);
				System.out.println(v);
			} catch (ExpressionParserException e) {
				System.err.println("Error: " + e.getMessage());
			}
		}
	}
}
