package com.github.tukenuke.tuske.conditions;

import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Tameable;
import org.bukkit.event.Event;
import javax.annotation.Nullable;

import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class CondIsTameable extends Condition{
	static {
		Registry.newCondition(CondIsTameable.class, "%entities% (is|are) tameable", "%entities% (is|are)(n't| not) tameable");
	}

	private Expression<Entity> ent;
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		ent = (Expression<Entity>) arg[0];
		setNegated(arg1 == 1);
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return null;
	}

	@Override
	public boolean check(Event e) {
		return ent.check(e, et -> et instanceof Tameable, isNegated());
	}

}
