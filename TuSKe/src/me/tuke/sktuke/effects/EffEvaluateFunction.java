package me.tuke.sktuke.effects;

import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.function.Function;
import ch.njol.skript.lang.function.Functions;
import ch.njol.util.Kleenean;
import me.tuke.sktuke.TuSKe;

public class EffEvaluateFunction extends Effect{

	private Expression<String> func;
	private List<Expression<?>> ob = new ArrayList<Expression<?>>();
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
	public String toString(@Nullable Event e, boolean arg1) {
		return "evaluate function " + func + ((ob.size() > 0) ? " with objects" : "");
	}

	@Override
	protected void execute(Event e) {
		if (this.func.getArray(e) != null){
			String[] funcs = func.getArray(e);
			for (int x = 0; x < funcs.length; x++)
				if (funcs[x] != null){
					Function<?> f = Functions.getFunction(funcs[x]);
					if (f == null)
						return;
					f.execute(TuSKe.getGUIManager().getParam(f, ob, e));
				}
			
		}
		
	}

}
