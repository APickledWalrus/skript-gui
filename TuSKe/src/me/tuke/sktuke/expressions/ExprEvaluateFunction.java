package me.tuke.sktuke.expressions;

import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.function.Function;
import ch.njol.skript.lang.function.Functions;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import me.tuke.sktuke.TuSKe;

public class ExprEvaluateFunction extends SimpleExpression<Object>{

	private Expression<String> func;
	private List<Expression<?>> ob = new ArrayList<Expression<?>>();

	
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
		for (int x = 1; x < arg.length; x++)
			if (arg[x] != null)
				ob.add(arg[x]);
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "evaluate function";
	}

	@Override
	@Nullable
	protected Object[] get(Event e){
		if (this.func.getSingle(e) != null){
			Function<?> f = Functions.getFunction(this.func.getSingle(e));
			if (f != null){
				return f.execute(TuSKe.getGUIManager().getParam(f, ob, e));
			}
		}
		return null;
	}

}
