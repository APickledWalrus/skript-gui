package me.tuke.sktuke.expressions;

import java.util.List;

import me.tuke.sktuke.util.NewRegister;
import org.bukkit.event.Event;

import com.mifmif.common.regex.Generex;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import me.tuke.sktuke.TuSKe;
import me.tuke.sktuke.util.Regex;

public class ExprRegexRandom extends SimpleExpression<String>{
	static {
		NewRegister.newSimple(ExprRegexRandom.class,
				"(first|random) string matching [pattern] %regex/string%",
				"random strings matching [pattern] %regex/string%");
	}
	
	private static boolean warn = false;

	private Expression<?> regex;
	private int mode = 1;
	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	public boolean isSingle() {
		return mode != 3;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		regex = arg[0].getConvertedExpression(Object.class);
		if (arg1 > 0) {
			mode = 3;
			if (TuSKe.getInstance().getConfig().getBoolean("warn_unsafe_expressions") && !warn){
				warn = true;
				Skript.warning("§4Warning: §cThe expression '§4" + arg3.expr + "§c' has possibility to stop your server"
						+ " if you use pattern that can return a huge possibilities, like '§4.+§c' or '§4.{1,10000}§c'."
						+ " Consider to make it safely or use 'MundoSK async' function. If you already know about this,"
						+ " disable this message in configs.");
			}
		} else if (arg3.expr.toLowerCase().startsWith("random"))
			mode = 2;
		else
			mode = 1;
		
		return true;
	}

	@Override
	public String toString(Event arg0, boolean arg1) {
		return "regex random with regex \"" + regex.toString(arg0, arg1) + "\"";
	}
	
	@Override
	protected String[] get(Event e) {
		Object expr = regex.getSingle(e);
		if (expr != null){
			Regex reg;
			if (expr instanceof String) {
				reg = new Regex((String)expr);
				if (!reg.isPatternParsed())
					return null;
			} else
				reg = (Regex)expr;
			Generex gen = new Generex(reg.getRegex());
			switch (mode) {
				case 1: return new String[]{gen.getFirstMatch()};
				case 2: return new String[]{gen.random()};
				case 3:  if (!gen.isInfinite()) {
					try {
						List<String> matches = gen.getMatchedStrings(100);
						return matches.toArray(new String[matches.size()]);
					} catch (StackOverflowError er) {
						
					}
				}
			}
			
		}
	
		return null;
	}

}
