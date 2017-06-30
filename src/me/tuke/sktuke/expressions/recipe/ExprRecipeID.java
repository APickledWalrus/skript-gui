package me.tuke.sktuke.expressions.recipe;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import me.tuke.sktuke.util.Registry;
import org.bukkit.Keyed;
import org.bukkit.inventory.Recipe;

/**
 * @author Tuke_Nuke on 30/06/2017
 */
@Name("Recipe ID")
@Description("Starting from minecraft 1.18, the recipes now has a unique id. Recipes registered by minecraft will have " +
		"a id `minecraft:%the result item's name%`, an old recipe plugin will be `bukkit:%random uuid%`, while TuSKe " +
		"will have its id as `tuske:%last recipe id + 1%`. Other plugins may have a different format, but always following `<plugin>:<key>`.")
@Examples({
		"loop recipes of held item:",
		"\tadd \"{recipeBook:{recipes:[\"%recipe id of loop-recipe%\"]}}\" to player's nbt"})
@Since("1.8")
public class ExprRecipeID extends SimplePropertyExpression<Recipe, String> {
	static {
		Registry.newProperty(ExprRecipeID.class, "recipe (id|name|key)", "recipes");
	}

	@Override
	protected String getPropertyName() {
		return "recipe id";
	}

	@Override
	public String convert(Recipe recipe) {
		return recipe instanceof Keyed ? ((Keyed) recipe).getKey() + "" : null;
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}
}
