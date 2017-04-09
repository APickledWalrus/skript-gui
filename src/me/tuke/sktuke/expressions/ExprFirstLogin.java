package me.tuke.sktuke.expressions;

import javax.annotation.Nullable;

import me.tuke.sktuke.util.NewRegister;
import org.bukkit.OfflinePlayer;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Date;

public class ExprFirstLogin extends SimplePropertyExpression<OfflinePlayer, Date>{
	static {
		NewRegister.newProperty(ExprFirstLogin.class, "first login", "offlineplayer");
	}

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
