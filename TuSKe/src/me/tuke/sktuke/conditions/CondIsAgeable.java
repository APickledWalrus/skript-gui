package me.tuke.sktuke.conditions;

import me.tuke.sktuke.util.NewRegister;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import javax.annotation.Nullable;

import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Checker;
import ch.njol.util.Kleenean;

public class CondIsAgeable extends Condition{
	static {
		NewRegister.newCondition(CondIsAgeable.class, "%entities% ((is|are) ageable|can grow up)", "%entities% ((is|are)(n't| not) ageable|can(n't| not) grow up)");

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
				
				return et instanceof Ageable;
			}}, isNegated());
	}

}
