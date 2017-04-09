package me.tuke.sktuke.conditions;

import me.tuke.sktuke.util.NewRegister;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Tameable;
import org.bukkit.event.Event;
import javax.annotation.Nullable;

import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Checker;
import ch.njol.util.Kleenean;

public class CondIsTameable extends Condition{
	static {
		NewRegister.newCondition(CondIsTameable.class, "%entities% (is|are) tameable", "%entities% (is|are)(n't| not) tameable");
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
		return ent.check(e, new Checker<Entity>(){

			@Override
			public boolean check(Entity et) {
				
				return et instanceof Tameable;
			}}, isNegated());
	}

}
