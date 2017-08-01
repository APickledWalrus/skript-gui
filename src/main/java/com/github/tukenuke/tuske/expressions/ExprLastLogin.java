package com.github.tukenuke.tuske.expressions;

import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.OfflinePlayer;
import javax.annotation.Nullable;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Date;

public class ExprLastLogin extends SimplePropertyExpression<OfflinePlayer, Date>{
	static {
		Registry.newProperty(ExprLastLogin.class, "last login", "offlineplayer");
	}

	@Override
	public Class<? extends Date> getReturnType() {
		return Date.class;
	}

	@Override
	@Nullable
	public Date convert(OfflinePlayer p) {
		if (p != null)
			return new Date(p.getLastPlayed());
		return null;
	}

	@Override
	protected String getPropertyName() {
		return "lastInstance login";
	}

}
