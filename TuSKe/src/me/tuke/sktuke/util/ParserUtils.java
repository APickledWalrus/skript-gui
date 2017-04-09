package me.tuke.sktuke.util;

import java.lang.reflect.Constructor;
import java.util.List;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Statement;
import ch.njol.skript.lang.TriggerSection;
import ch.njol.skript.lang.util.SimpleExpression;

/**
 * A class to handle some simple stuffs between old parser x new parser
 * <b>Deprecated</b>: The new parser was abandoned and it might be gone too.
 */
@Deprecated()
public class ParserUtils {
	
	private static Constructor<SkriptParser> newParser = null; 
	private static Object newParserInstance = null;
	static {
		Class<?> parserInstanceClass = ReflectionUtils.getClass("ch.njol.skript.lang.parser.ParserInstance");
		if (parserInstanceClass != null){
			newParser = (Constructor<SkriptParser>) ReflectionUtils.getConstructor(SkriptParser.class, parserInstanceClass, String.class, int.class, ParseContext.class);
			newParserInstance = ReflectionUtils.getField(parserInstanceClass, null, "DUMMY");
		}
	}

	public static List<TriggerSection> getTriggerSection(Object from){
		if (newParser != null){
			if (from != null || newParserInstance == null)
				newParserInstance = getParserInstance(from);
			return ReflectionUtils.getField(newParserInstance.getClass(), newParserInstance, "currentSections");
			
		}
		return ScriptLoader.currentSections;
	}
	/**
	 * 
	 * @param expr
	 * @param from
	 * @param flag
	 * @param context
	 * @return
	 */
	public static SkriptParser getSkriptParser(Object from, String expr, int flag, ParseContext context){
		if (newParser != null){
			if (from != null || newParserInstance == null)
				newParserInstance = getParserInstance(from);
			return ReflectionUtils.newInstance(newParser, newParserInstance, expr, flag, context);
		}
		return new SkriptParser(expr, flag, context);
		
	}
	public static Object getParserInstance(Object from){
		if (from != null){
			if (from instanceof Statement)
				return ReflectionUtils.getField(Statement.class, from, "pi");
			else if (from instanceof SimpleExpression<?>)
				return ReflectionUtils.getField(SimpleExpression.class, from, "pi");
		}
		return newParserInstance = ReflectionUtils.newInstance(ReflectionUtils.getClass("ch.njol.skript.lang.parser.ParserInstance"));
		
	}
}
