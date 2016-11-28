package me.tuke.sktuke.expressions;

import org.bukkit.entity.LivingEntity;

import javax.annotation.Nullable;

import ch.njol.skript.expressions.base.SimplePropertyExpression;

public class ExprLastDamage extends SimplePropertyExpression<LivingEntity, Number>{

	@Override
	public Class<? extends Number> getReturnType() {
		return Number.class;
	}

	@Override
	@Nullable
	public Number convert(LivingEntity e) {
		if (e.getLastDamageCause() != null)
			return e.getLastDamageCause().getDamage();
		return null;
	}

	@Override
	protected String getPropertyName() {
		return "last damage";
	}

}
