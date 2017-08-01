package com.github.tukenuke.tuske.effects;

import com.github.tukenuke.tuske.TuSKe;
import com.github.tukenuke.tuske.manager.recipe.RecipeManager;
import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.github.tukenuke.tuske.manager.recipe.CustomFurnaceRecipe;

public class EffRegisterRecipe extends Effect{
	static {
		Registry.newEffect(EffRegisterRecipe.class,
				"(create|register) [new] [custom] shaped recipe with (return|result) %itemstack% using [ingredients] %itemstacks% [with shape %-strings%]",
				"(create|register) [new] [custom] shapeless recipe with (return|result) %itemstack% using [ingredients] %itemstacks%",
				"(create|register) [new] [custom] furnace recipe with (return|result) %itemstack% using [source] %itemstack% [[and] with experience %-number%]");
	}

	private int type;
	private Expression<ItemStack> result;
	private Expression<ItemStack> ingredients;
	private Expression<Number> exp = null;
	private Expression<String> shape = null;
	private boolean isCustom = true;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		type = arg1;
		result = (Expression<ItemStack>) arg[0];
		ingredients = (Expression<ItemStack>) arg[1];
		if (arg1 > 2)
			exp = (Expression<Number>) arg[2];
		else if (arg1 == 0){
			shape = (Expression<String>) arg[2];
		}
		isCustom = arg3.expr.matches("^(create|register)\\s+(new)?\\s+custom.*$");
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "register new recipe";
	}

	@Override
	protected void execute(Event e) {
		if (result.getSingle(e) != null){
			ItemStack[] ingredients = this.ingredients.getArray(e);

			if (ingredients == null || ingredients.length == 0 || ingredients.length > 9)
				return;
			for (ItemStack item : ingredients)
				if (item == null)
					return;
			RecipeManager rm = TuSKe.getRecipeManager();
			switch (type){
				case 0:
					String[] shapes = shape != null && shape.getArray(e).length <= 3 ? shape.getArray(e) : new String[]{"abc", "def", "ghi"};
					rm.registerRecipe(rm.newShapedRecipe(result.getSingle(e), ingredients, shapes), isCustom);
					break;
				case 1:
					rm.registerRecipe(rm.newShapelessRecipe(result.getSingle(e), ingredients), isCustom);
					break;
				case 2:
					float n = exp != null && exp.getSingle(e).floatValue() > 0 ? exp.getSingle(e).floatValue() : 0F;
					rm.registerRecipe(new CustomFurnaceRecipe(result.getSingle(e), ingredients[0], n), isCustom);
					break;
			}
		}
		
	}
}
