package me.tuke.sktuke.expressions;

import me.tuke.sktuke.util.Registry;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import javax.annotation.Nullable;

import ch.njol.skript.expressions.base.SimplePropertyExpression;

public class ExprLastDamageCause extends SimplePropertyExpression<LivingEntity, DamageCause>{
	static {
		Registry.newProperty(ExprLastDamageCause.class, "last damage cause", "livingentity");
	}

	@Override
	public Class<? extends DamageCause> getReturnType() {
		return DamageCause.class;
	}

	@Override
	@Nullable
	public DamageCause convert(LivingEntity e) {
		if (e.getLastDamageCause() != null)
			return e.getLastDamageCause().getCause();
		return null;
	}

	@Override
	protected String getPropertyName() {
		return "lastInstance damage cause";
	}

}
