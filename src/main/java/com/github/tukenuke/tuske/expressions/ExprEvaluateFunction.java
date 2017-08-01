package com.github.tukenuke.tuske.expressions;

import com.github.tukenuke.tuske.util.EvalFunction;
import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprEvaluateFunction extends SimpleExpression<Object>{
	static {
		Registry.newSimple(ExprEvaluateFunction.class, "result of function %string% [with <.+?>]", "result of function %string\\(<.+?>\\)");
	}

	private Expression<String> func;
	private String exprs;

	
	@Override
	public Class<? extends Object> getReturnType() {
		return Object.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		func = (Expression<String>) arg[0];
		if (arg3.regexes.size() > 0)
			exprs = arg3.regexes.get(0).group(0);
		EvalFunction.setParserInstance(this);
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "evaluate function";
	}

	@Override
	@Nullable
	protected Object[] get(Event e){
		String funcName = func.getSingle(e);
		if (funcName != null){
			EvalFunction ef = new EvalFunction(funcName, exprs == null? "": exprs).getParemetersValues(e);
			return ef.run();
		}
		return null;
	}

}
