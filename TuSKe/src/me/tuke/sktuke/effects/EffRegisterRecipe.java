package me.tuke.sktuke.effects;

import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import me.tuke.sktuke.TuSKe;
import me.tuke.sktuke.recipe.CustomFurnaceRecipe;
import me.tuke.sktuke.recipe.CustomShapedRecipe;
import me.tuke.sktuke.recipe.CustomShapelessRecipe;

public class EffRegisterRecipe extends Effect{

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
			switch (type){
			case 0:
				String[] shapes = shape != null && shape.getArray(e).length <= 3 ? shape.getArray(e) : new String[]{"abc", "def", "ghi"};
				TuSKe.getRecipeManager().registerRecipe(new CustomShapedRecipe(result.getSingle(e), ingredients, shapes), isCustom);
				break;
			case 1:
				TuSKe.getRecipeManager().registerRecipe(new CustomShapelessRecipe(result.getSingle(e), ingredients), isCustom);
				break;
			case 2:
				float n = exp != null && exp.getSingle(e).floatValue() > 0 ? exp.getSingle(e).floatValue() : 0F;
				TuSKe.getRecipeManager().registerRecipe(new CustomFurnaceRecipe(result.getSingle(e), ingredients[0], n), isCustom);
				break;
			}
		}
		
	}
}
