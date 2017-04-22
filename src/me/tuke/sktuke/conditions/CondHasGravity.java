package me.tuke.sktuke.conditions;

import javax.annotation.Nullable;

import ch.njol.skript.aliases.ItemType;
import me.tuke.sktuke.util.Registry;
import org.bukkit.event.Event;

import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class CondHasGravity extends Condition{
	static {
		Registry.newCondition(CondHasGravity.class,
				"%itemtypes% has gravity",
				"%itemtypes% has(n't| not) gravity");
	}

	private Expression<ItemType> o;
	private int neg;
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		this.o = (Expression<ItemType>) arg[0];
		setNegated(arg1 == 1);
		return true;
	}
	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return this.o + " has gravity";
	}

	@Override
	public boolean check(Event e) {
		return o.check(e, item -> item.getRandom().getType().hasGravity(), isNegated());
	}
	
}