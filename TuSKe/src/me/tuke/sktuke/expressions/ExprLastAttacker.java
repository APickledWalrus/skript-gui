package me.tuke.sktuke.expressions;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprLastAttacker extends SimpleExpression<Object> {

	private Expression<Entity> ent;
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
		ent = (Expression<Entity>) arg[0];
		return true;
	}

	@Override
	public String toString(Event arg0, boolean arg1) {
		return "last attacker of " + ent.toString(arg0, arg1);
	}

	@Override
	protected Object[] get(Event e) {
		Entity entity = ent.getSingle(e);
		if (entity != null && entity.getLastDamageCause() != null){
			if (entity.getLastDamageCause() instanceof EntityDamageByBlockEvent)
				return new Block[]{((EntityDamageByBlockEvent)entity.getLastDamageCause()).getDamager()};
			if (entity.getLastDamageCause() instanceof EntityDamageByEntityEvent)
				return new Entity[]{((EntityDamageByEntityEvent)entity.getLastDamageCause()).getDamager()};
		}
		return null;
	}

}
