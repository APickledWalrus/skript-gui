package me.tuke.sktuke.effects;

import me.tuke.sktuke.util.NewRegister;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import me.tuke.sktuke.util.EvalFunction;

public class EffEvaluateFunction extends Effect{
	static {
		NewRegister.newEffect(EffEvaluateFunction.class, "evaluate function %strings% [with <.+?>]", "evaluate function %strings%\\(<.+?>\\)");
	}

	private Expression<String> func;
	private String exprs = "";
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		func = (Expression<String>) arg[0];
		if (arg3.regexes.size() > 0)
			exprs = arg3.regexes.get(0).group(0);
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "evaluate function " +func.toString(e, arg1)+ "(" + (exprs != null ? exprs : "") + ")";
	}

	@SuppressWarnings("unused")
	@Override
	protected void execute(Event e) {
		String[] funcs = func.getArray(e);
		if (funcs != null && funcs.length > 0){
			for (int x = 0; x < funcs.length; x++)
				if (funcs[x] != null){
					Expression<?>[] params = null;
					EvalFunction.setParserInstance(this);
					EvalFunction func;
					if (params == null){
						func = new EvalFunction(funcs[x], exprs);
						params = func.getParameters();
					} else {
						func = new EvalFunction(funcs[x], params);						
					}
					func.getParemetersValues(e).run();
				}
			
		}
		
	}

}
