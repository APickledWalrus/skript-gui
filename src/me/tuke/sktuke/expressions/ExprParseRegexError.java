package me.tuke.sktuke.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import me.tuke.sktuke.util.Registry;
import org.bukkit.event.Event;
import javax.annotation.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

@Name("Regex Error")
@Description("It contains the last error of a regex. It is setted when there is a error while parsing a string as regex, {{effects|RegexReplace|replacing}}/{{effects|RegexSplit|spliting}} a string or checking in the {{conditions|RegexMatches|condition}}")
@Examples({
		"set {_regex} to \"(\\d+(\\.\\d+)*\" parsed as regex",
		"if regex error is set: #It will case there is a missing parentheses at the end.",
		"\tsend \"A error occurred with the regex pattern. Details:\"",
		"\tsend last regex parser error",
		"\t#It will send a formatted strings like showing the errors. For example:",
		"\t#Unclosed group near index 12",
		"\t#(\\d+(\\.\\d+)*",
		"\t#             ^"})
@Since("1.7.1")
public class ExprParseRegexError extends SimpleExpression<String>{
	static {
		Registry.newSimple(ExprParseRegexError.class, "[last] regex [parser] error");
	}

	public static String parserError = null;
	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public boolean init(Expression<?>[] arg0, int arg1, Kleenean arg2, ParseResult arg3) {
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "lastInstance regex parser error";
	}

	@Override
	@Nullable
	protected String[] get(Event arg0) {
		return new String[]{parserError};
	}

}
