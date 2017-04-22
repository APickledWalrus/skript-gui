package me.tuke.sktuke.conditions;

import ch.njol.skript.aliases.ItemType;
import me.tuke.sktuke.util.Registry;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class CondCanEat extends Condition{
	static {
		Registry.newCondition(CondCanEat.class,
				"%itemtypes% is edible",
				"%itemtypes% is(n't| not) edible");
	}

	private Expression<ItemType> o;
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		this.o = (Expression<ItemType>) arg[0];
		setNegated(arg1 == 1);
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return this.o + " is edible";
	}

	@Override
	public boolean check(Event e) {
		return o.check(e, item -> item.getRandom().getType().isEdible(), isNegated());
	}
	

}
