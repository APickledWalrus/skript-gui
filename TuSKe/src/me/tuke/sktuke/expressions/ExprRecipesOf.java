package me.tuke.sktuke.expressions;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprRecipesOf extends SimpleExpression<Recipe>{

	private Expression<ItemStack> i;

	@Override
	public Class<? extends Recipe> getReturnType() {
		return Recipe.class;
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		this.i = (Expression<ItemStack>) arg[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "recipe of " + this.i;
	}

	@Override
	@Nullable
	protected Recipe[] get(Event e) {
		ItemStack i = this.i.getSingle(e);
		if (i == null)
			return null;
		return Bukkit.getRecipesFor(i).toArray(new Recipe[Bukkit.getRecipesFor(i).size()]);
	}
	
}
