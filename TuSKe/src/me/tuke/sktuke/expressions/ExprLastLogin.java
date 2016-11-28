package me.tuke.sktuke.expressions;

import org.bukkit.entity.Player;
import javax.annotation.Nullable;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Date;

public class ExprLastLogin extends SimplePropertyExpression<Player, Date>{

	@Override
	public Class<? extends Date> getReturnType() {
		return Date.class;
	}

	@Override
	@Nullable
	public Date convert(Player p) {
		if (p != null)
			return new Date(p.getLastPlayed());
		return null;
	}

	@Override
	protected String getPropertyName() {
		return "last login";
	}

}
