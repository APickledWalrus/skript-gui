package me.tuke.sktuke.expressions.recipe;

import me.tuke.sktuke.util.Registry;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import com.google.common.collect.Lists;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import me.tuke.sktuke.TuSKe;
import me.tuke.sktuke.manager.recipe.RecipeManager;

public class ExprRecipeFromItems extends SimpleExpression<Recipe>{
	static {
		Registry.newSimple(ExprRecipeFromItems.class, "recipe from ingredients %itemstacks%");
	}

	private Expression<ItemStack> items;
	@Override
	public Class<? extends Recipe> getReturnType() {
		return Recipe.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		items = (Expression<ItemStack>) arg[0]; 
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "recipe from ingredients " + items.toString(arg0, arg1);
	}

	@Override
	@Nullable
	protected Recipe[] get(Event e) {
		RecipeManager rm = TuSKe.getRecipeManager();
		ItemStack[] items = this.items.getAll(e);
		for (Recipe r : Lists.newArrayList(Bukkit.recipeIterator()))
			if (rm.equalsRecipe(r, items) > 0)
				return new Recipe[]{r};
		return null;
	}

}
