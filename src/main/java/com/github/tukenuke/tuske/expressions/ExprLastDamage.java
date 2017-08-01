package com.github.tukenuke.tuske.expressions;

import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nullable;

import ch.njol.skript.expressions.base.SimplePropertyExpression;

public class ExprLastDamage extends SimplePropertyExpression<LivingEntity, Number>{
	static {
		Registry.newProperty(ExprLastDamage.class, "last damage", "livingentity");
	}

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
		return "lastInstance damage";
	}

}
