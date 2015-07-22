package com.kreative.paint.powershape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ExpressionParser {
	private final List<Integer> precedences;
	private final ExpressionLexer lexer;
	
	public ExpressionParser(String s) {
		this(new ExpressionLexer(s));
	}
	
	public ExpressionParser(ExpressionLexer lexer) {
		this.precedences = new ArrayList<Integer>();
		this.precedences.addAll(Operator.operators.keySet());
		Collections.sort(this.precedences);
		this.lexer = lexer;
	}
	
	private Expression parseFactor() {
		String s = lexer.getNext();
		if (s == null) {
			throw ExpressionParserException.expected("factor", s);
		} else if (s.equals("(")) {
			Expression e = parseExpression();
			String z = lexer.getNext();
			if (z.equals(")")) return e;
			throw ExpressionParserException.expected("\")\"", z);
		} else if (s.equals("[")) {
			Expression e = parseExpression();
			String z = lexer.getNext();
			if (z.equals("]")) return e;
			throw ExpressionParserException.expected("\"]\"", z);
		} else if (s.equals("{")) {
			Expression e = parseExpression();
			String z = lexer.getNext();
			if (z.equals("}")) return e;
			throw ExpressionParserException.expected("\"}\"", z);
		} else if (Function.functions.containsKey(s.toLowerCase())) {
			Function f = Function.functions.get(s.toLowerCase());
			Expression e = parseFactor();
			return new Expression.Unary(f, e);
		} else if (s.equals("\u221E") || s.equalsIgnoreCase("inf") || s.equalsIgnoreCase("infinity")) {
			return new Expression.Value(Double.POSITIVE_INFINITY);
		} else if (s.equals("\u03A6") || s.equalsIgnoreCase("nan")) {
			return new Expression.Value(Double.NaN);
		} else if (s.equals("\u03C0") || s.equalsIgnoreCase("pi")) {
			return new Expression.Value(Math.PI);
		} else if (s.equals("\u212E") || s.equalsIgnoreCase("e")) {
			return new Expression.Value(Math.E);
		} else if (isNumberPart(s.charAt(0))) {
			try {
				double d = Double.parseDouble(s);
				return new Expression.Value(d);
			} catch (NumberFormatException e) {
				return new Expression.Binding(s);
			}
		} else if (isIdentifierPart(s.charAt(0))) {
			return new Expression.Binding(s);
		} else {
			throw ExpressionParserException.expected("factor", s);
		}
	}
	
	private Expression parseExpressionRA(int precedenceIndex) {
		if (precedenceIndex >= precedences.size()) return parseFactor();
		int precedence = precedences.get(precedenceIndex);
		Map<String,Operator> ops = Operator.operators.get(precedence);
		Expression e = parseExpressionRA(precedenceIndex + 1);
		if (lexer.hasNext() && ops.containsKey(lexer.lookNext().toLowerCase())) {
			Operator op = ops.get(lexer.getNext().toLowerCase());
			e = new Expression.Binary(op, e, parseExpressionRA(precedenceIndex));
		}
		return e;
	}
	
	private Expression parseExpressionLA(int precedenceIndex) {
		if (precedenceIndex >= precedences.size()) return parseFactor();
		int precedence = precedences.get(precedenceIndex);
		if (precedence >= Operator.pow.precedence) return parseExpressionRA(precedenceIndex);
		Map<String,Operator> ops = Operator.operators.get(precedence);
		Expression e = parseExpressionLA(precedenceIndex + 1);
		while (lexer.hasNext() && ops.containsKey(lexer.lookNext().toLowerCase())) {
			Operator op = ops.get(lexer.getNext().toLowerCase());
			e = new Expression.Binary(op, e, parseExpressionLA(precedenceIndex + 1));
		}
		return e;
	}
	
	public Expression parseExpression() {
		Expression e = parseExpressionLA(0);
		if (lexer.hasNext() && lexer.lookNext().equals("?")) {
			lexer.getNext();
			Expression t = parseExpression();
			if (lexer.hasNext() && lexer.lookNext().equals(":")) {
				lexer.getNext();
				Expression f = parseExpression();
				e = new Expression.Conditional(e, t, f);
			} else {
				Expression f = new Expression.Value(Double.NaN);
				e = new Expression.Conditional(e, t, f);
			}
		}
		return e;
	}
	
	public Expression parse() {
		Expression e = parseExpression();
		if (!lexer.hasNext()) return e;
		String s = lexer.lookNext();
		throw ExpressionParserException.expected(null, s);
	}
	
	private static boolean isNumberPart(char ch) {
		return ch == '.' || Character.isDigit(ch);
	}
	
	private static boolean isIdentifierPart(char ch) {
		return ch == '.' || Character.isDigit(ch) || Character.isLetter(ch);
	}
}
