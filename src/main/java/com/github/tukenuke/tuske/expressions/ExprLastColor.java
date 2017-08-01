package com.github.tukenuke.tuske.expressions;

import javax.annotation.Nullable;

import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.ChatColor;

import ch.njol.skript.expressions.base.SimplePropertyExpression;

public class ExprLastColor extends SimplePropertyExpression<String, String>{
	static {
		Registry.newProperty(ExprLastColor.class, "last color", "string");
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	@Nullable
	public String convert(String str) {
		return ChatColor.getLastColors(str);
	}

	@Override
	protected String getPropertyName() {
		return "lastInstance color";
	}

}
