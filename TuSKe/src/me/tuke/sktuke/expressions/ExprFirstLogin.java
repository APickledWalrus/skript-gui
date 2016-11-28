package me.tuke.sktuke.expressions;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Date;

public class ExprFirstLogin extends SimplePropertyExpression<Player, Date>{

	@Override
	public Class<? extends Date> getReturnType() {
		return Date.class;
	}

	@Override
	@Nullable
	public Date convert(Player p) {
		return new Date(p.getFirstPlayed());
	}

	@Override
	protected String getPropertyName() {
		return "first login";
	}

}
