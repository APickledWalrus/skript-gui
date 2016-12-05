package me.tuke.sktuke.expressions;

import javax.annotation.Nullable;

import org.bukkit.OfflinePlayer;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Date;

public class ExprFirstLogin extends SimplePropertyExpression<OfflinePlayer, Date>{

	@Override
	public Class<? extends Date> getReturnType() {
		return Date.class;
	}

	@Override
	@Nullable
	public Date convert(OfflinePlayer p) {
		return new Date(p.getFirstPlayed());
	}

	@Override
	protected String getPropertyName() {
		return "first login";
	}

}
