package me.tuke.sktuke.expressions;

import javax.annotation.Nullable;

import org.bukkit.ChatColor;

import ch.njol.skript.expressions.base.SimplePropertyExpression;

public class ExprLastColor extends SimplePropertyExpression<String, String>{

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	@Nullable
	public String convert(String str) {
		if (str == null)
			return null;
		return ChatColor.getLastColors(str);
	}

	@Override
	protected String getPropertyName() {
		return "last color";
	}

}
