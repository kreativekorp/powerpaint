package com.kreative.paint.material.shape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ExpressionLexer {
	private final List<String> reservedTokens;
	private final String s;
	private final int n;
	private int i;
	
	public ExpressionLexer(String s) {
		this.reservedTokens = new ArrayList<String>();
		this.reservedTokens.addAll(Function.functions.keySet());
		for (Map<String,Operator> ops : Operator.operators.values()) {
			this.reservedTokens.addAll(ops.keySet());
		}
		Collections.sort(this.reservedTokens, new Comparator<String>() {
			@Override
			public int compare(String a, String b) {
				int cmp = b.length() - a.length();
				if (cmp != 0) return cmp;
				return a.compareTo(b);
			}
		});
		this.s = s;
		this.n = s.length();
		this.i = 0;
	}
	
	public int currentIndex() {
		return i;
	}
	
	public boolean hasNext() {
		while (i < n && Character.isWhitespace(s.charAt(i))) i++;
		return (i < n);
	}
	
	public String lookNext() {
		while (i < n && Character.isWhitespace(s.charAt(i))) i++;
		if (i >= n) return null;
		final char ch = s.charAt(i);
		final int i = this.i;
		int j = i + 1;
		if (isNumberPart(ch)) {
			while (j < n && isNumberPart(s.charAt(j))) j++;
		} else if (isIdentifierPart(ch)) {
			while (j < n && isIdentifierPart(s.charAt(j))) j++;
		} else {
			String rest = s.substring(i);
			for (String token : reservedTokens) {
				if (rest.startsWith(token)) {
					j = i + token.length();
					break;
				}
			}
		}
		return s.substring(i, j);
	}
	
	public String getNext() {
		while (i < n && Character.isWhitespace(s.charAt(i))) i++;
		if (i >= n) return null;
		final char ch = s.charAt(i);
		final int i = this.i;
		int j = i + 1;
		if (isNumberPart(ch)) {
			while (j < n && isNumberPart(s.charAt(j))) j++;
		} else if (isIdentifierPart(ch)) {
			while (j < n && isIdentifierPart(s.charAt(j))) j++;
		} else {
			String rest = s.substring(i);
			for (String token : reservedTokens) {
				if (rest.startsWith(token)) {
					j = i + token.length();
					break;
				}
			}
		}
		this.i = j;
		return s.substring(i, j);
	}
	
	private static boolean isNumberPart(char ch) {
		return ch == '.' || Character.isDigit(ch);
	}
	
	private static boolean isIdentifierPart(char ch) {
		return ch == '.' || Character.isDigit(ch) || Character.isLetter(ch);
	}
}
