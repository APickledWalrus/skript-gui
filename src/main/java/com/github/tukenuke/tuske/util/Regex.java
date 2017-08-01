package com.github.tukenuke.tuske.util;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Regex {
	private static final Regex instance = new Regex();

	private Regex() {

	}
	public static Regex getInstance() {
		return instance;
	}

	public String lastRegexError = null;

	public Pattern parse(String regex) {
		if (regex != null)
			try {
				error(null);
				return Pattern.compile(regex);
			} catch (PatternSyntaxException e){
				error(e);
			}
		else
			error("Null pattern.");
		return null;
	}

	public Pattern getPattern(Object obj) {
		if (obj instanceof String)
			return parse((String)obj);
		else if (obj instanceof Pattern)
			return (Pattern) obj;
		return null;
	}

	public String regexReplace(Pattern p, String with, String from, boolean first) {
		error(null);
		try {
			if (first)
				return p.matcher(from).replaceFirst(with);
			return p.matcher(from).replaceAll(with);
		} catch (Exception e) {
			error(e);
		}
		return null;
	}
	public String[] regexSplit(String from, Pattern with) {
		error(null);
		try {
			return with.split(from);
		} catch (Exception e) {
			error(e);
		}
		return null;
	}
	private void error(Object e) {
		if (e != null) {
			if (e instanceof Exception)
				lastRegexError = ((Exception) e).getMessage().replace('\n', ' ');
			else if (e instanceof String)
				lastRegexError = (String) e;
		} else
			lastRegexError = null;
	}
}
