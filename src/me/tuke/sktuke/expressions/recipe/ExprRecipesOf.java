package me.tuke.sktuke.expressions.recipe;

import me.tuke.sktuke.util.NewRegister;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.List;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprRecipesOf extends SimpleExpression<Recipe>{
	static {
		NewRegister.newProperty(ExprRecipesOf.class, "[all] recipes", "itemstack");
	}

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
		return "recipe of " + i.toString(e, arg1);
	}

	@Override
	@Nullable
	protected Recipe[] get(Event e) {
		ItemStack i = this.i.getSingle(e);
		if (i == null)
			return null;
		List<Recipe> recipes = Bukkit.getRecipesFor(i);
		return recipes.toArray(new Recipe[recipes.size()]);
	}
	
}
