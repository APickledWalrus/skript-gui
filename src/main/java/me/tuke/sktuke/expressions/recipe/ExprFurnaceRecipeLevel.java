package me.tuke.sktuke.expressions.recipe;

import me.tuke.sktuke.util.Registry;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Recipe;
import javax.annotation.Nullable;

import ch.njol.skript.expressions.base.SimplePropertyExpression;

public class ExprFurnaceRecipeLevel extends SimplePropertyExpression<Recipe, Number>{
	static {
		Registry.newProperty(ExprFurnaceRecipeLevel.class, "furnace level", "recipe");
	}

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
