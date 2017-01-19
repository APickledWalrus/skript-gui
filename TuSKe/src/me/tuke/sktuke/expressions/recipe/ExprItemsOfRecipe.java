package me.tuke.sktuke.expressions.recipe;

import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import me.tuke.sktuke.TuSKe;

public class ExprItemsOfRecipe extends SimpleExpression<ItemStack>{
	
	private Expression<Recipe> recipe;

	@Override
	public Class<? extends ItemStack> getReturnType() {
		return ItemStack.class;
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		recipe = (Expression<Recipe>) arg[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "ingredients of " + recipe.toString(e, arg1);
	}

	@Override
	@Nullable
	protected ItemStack[] get(Event e) {
		Recipe r = recipe.getSingle(e);
		if (r != null){
			return TuSKe.getRecipeManager().fixIngredients(TuSKe.getRecipeManager().getIngredients(r));
		}
		return null;
	}

}
