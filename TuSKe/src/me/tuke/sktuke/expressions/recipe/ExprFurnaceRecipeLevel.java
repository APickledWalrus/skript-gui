package me.tuke.sktuke.expressions.recipe;

import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Recipe;
import javax.annotation.Nullable;

import ch.njol.skript.expressions.base.SimplePropertyExpression;

public class ExprFurnaceRecipeLevel extends SimplePropertyExpression<Recipe, Number>{

	@Override
	public Class<? extends Number> getReturnType() {
		return Number.class;
	}

	@Override
	@Nullable
	public Number convert(Recipe recipe) {
		return recipe instanceof FurnaceRecipe ? ((FurnaceRecipe)recipe).getExperience() : null;
	}

	@Override
	protected String getPropertyName() {
		return "furnace level";
	}

}
