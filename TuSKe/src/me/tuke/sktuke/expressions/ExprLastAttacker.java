package me.tuke.sktuke.expressions;

import org.bukkit.entity.Entity;

import ch.njol.skript.expressions.base.SimplePropertyExpression;

public class ExprLastAttacker extends SimplePropertyExpression<Entity, Entity> {

	@Override
	public Class<? extends Entity> getReturnType() {
		return Entity.class;
	}

	@Override
	public Entity convert(Entity ent) {
		return ent.getLastDamageCause() != null ? ent.getLastDamageCause().getEntity() : null;
	}

	@Override
	protected String getPropertyName() {
		return "last attacker";
	}

}
