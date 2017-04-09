package me.tuke.sktuke.util;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import me.tuke.sktuke.expressions.ExprParseRegexError;

public class Regex{
	private String reg;
	public Regex(String regex){
		reg = regex;
		try {
			ExprParseRegexError.parserError = null;
			Pattern.compile(regex);
		} catch (PatternSyntaxException e){
			reg = null;
			ExprParseRegexError.parserError = e.getMessage().replace('\r', ' ');
		}
	}
	public String getRegex(){
		return reg;
	}
	public boolean isPatternParsed(){
		return reg != null;
	}

}
