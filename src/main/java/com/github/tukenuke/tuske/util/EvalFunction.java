package com.github.tukenuke.tuske.util;

import java.lang.reflect.Constructor;

import org.bukkit.event.Event;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionList;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Statement;
import ch.njol.skript.lang.function.Function;
import ch.njol.skript.lang.function.Functions;
import ch.njol.skript.lang.function.Parameter;
import ch.njol.skript.lang.util.SimpleExpression;

/**
 * A util class to evaluate functions. It's useful when you want to get a function ready,
 * and run the function in another event but using values of the caller event.
 * It requires {@link ReflectionUtils}
 * 
 * @author Tuke_Nuke
 */
@SuppressWarnings("unchecked")
public class EvalFunction {
	
	private Object func;
	private Expression<?>[] parameters;
	private Object[][] values;
	//Used to make compatible with bensku's new parser (even tho the latest version doesn't have it anymore).
	private static Constructor<SkriptParser> newParser = null; 
	private static Object newParserInstance = null;
	static {
		Class<?> parserInstanceClass = ReflectionUtils.getClass("ch.njol.skript.lang.parser.ParserInstance");
		if (parserInstanceClass != null){
			newParser = (Constructor<SkriptParser>) ReflectionUtils.getConstructor(SkriptParser.class, parserInstanceClass, String.class, int.class, ParseContext.class);
			//newParserInstance = ReflectionUtils.getField(parserInstanceClass, null, "DUMMY");
		}
	}

	public EvalFunction(String name, String exprs){
		func = name;
		parseParemeters(exprs);
	}
	public EvalFunction(String name, Expression<?>[] exprs){
		func = name;
		parameters = exprs;
		
	}
	public EvalFunction(Function<?> function, String exprs){
		func = function;
		parseParemeters(exprs);
	}
	public EvalFunction(Function<?> func, Expression<?>[] exprs){
		this.func = func;
		parameters = exprs;
	}
	
	/**
	 * It get the values of a function from the event. You need to {@link #run()} to call the functions after get
	 * these values. 
	 * @param e - The Event
	 * @return Its own instance
	 */
	public EvalFunction getParemetersValues(Event e){
		if (func instanceof String)
			func = Functions.getFunction((String)func);
		if (func != null){
			Function<?> f = (Function<?>)func;
			int max = f.getParameters().length < parameters.length ? f.getParameters().length : parameters.length < 1 ? 1: parameters.length;
			if (values != null) {//In case it run in a loop, it will return a new function to each one have it own parameters.
				return new EvalFunction(f, parameters).getParemetersValues(e);
			}
			values = new Object[max][];
			if (parameters.length > 0)
				for (int x = 0; x < max; x++)
					if (parameters[x] != null){
						ClassInfo<?> returnType = ReflectionUtils.getField(Parameter.class, f.getParameter(x), "type");
						parameters[x] = parameters[x].getConvertedExpression(returnType.getC());
						if (parameters[x] != null)
							values[x] = parameters[x].getArray(e);
					}
			if (values[0] == null){
				ClassInfo<?> returnType = ReflectionUtils.getField(Parameter.class, f.getParameter(0), "type");
				Expression<?> def = ReflectionUtils.getField(Parameter.class, f.getParameter(0), "def");
				if (def != null)
					values[0] = def.getConvertedExpression(returnType.getC()).getArray(e);
			}
		}
		
		return this;
	}
	
	/**
	 * Run the function. You need to {@link #getParemetersValues(Event)} before running this.
	 * @return The return value of function, null if a void function.
	 */
	public Object[] run(){
		if (func != null && func instanceof Function<?>) //In case a function doesn't exists, it will prevent.
			return ((Function<?>)func).execute(values);
		return null;
	}
	
	/** 
	 * Returns the parameters of a function.
	 * @return Array of Expression<?>
	 */
	public Expression<?>[] getParameters(){
		return parameters;
	}
	/**
	 * It gets a ParserInstance from a effect/condition/expression.
	 * It is necessary in case it is running the new parser and it can't
	 * parse some objects with dummy ParserInstance (e.g loop-value).
	 * 
	 * It won't throw any exeception in case it isn't running the new parser.
	 * @param from - An instance of calling effect/condition/expression.
	 */
	public static void setParserInstance(Statement from){
		if (newParserInstance == null)
			newParserInstance = ReflectionUtils.getField(Statement.class, from, "pi");
	}
	
	/**
	 * It gets a ParserInstance from a effect/condition/expression.
	 * It is necessary in case it is running the new parser and it can't
	 * parse some objects with dummy ParserInstance (e.g loop-value).
	 * 
	 * It won't throw any exeception in case it isn't running the new parser.
	 * @param from - An instance of calling effect/condition/expression.{@}
	 */
	public static void setParserInstance(SimpleExpression<?> from){
		if (newParserInstance == null)
			newParserInstance = ReflectionUtils.getField(SimpleExpression.class, from, "pi");
	}
	private void parseParemeters(String expr){
		SkriptParser parser = getSkriptParser(expr);
		ReflectionUtils.setField(SkriptParser.class, parser, "suppressMissingAndOrWarnings", true);
		Expression<?> expression = parser.parseExpression(Object.class);
		if (expression != null) {
			if (!expression.isSingle())
				parameters = ((ExpressionList<?>)expression).getExpressions();
			else
				parameters = new Expression<?>[]{expression};
		} else
			parameters = new Expression<?>[0];
		
	}
	
	private SkriptParser getSkriptParser(String expr){
		if (newParser != null){
			if (newParserInstance == null)
				newParserInstance = ReflectionUtils.newInstance(ReflectionUtils.getClass("ch.njol.skript.lang.parser.ParserInstance"));
			return ReflectionUtils.newInstance(newParser, newParserInstance, expr, SkriptParser.ALL_FLAGS, ParseContext.DEFAULT);
		}
		return new SkriptParser(expr, SkriptParser.ALL_FLAGS, ParseContext.DEFAULT);
		
	}
}
