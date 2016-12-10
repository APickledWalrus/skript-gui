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
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "register new recipe";
	}

	@Override
	protected void execute(Event e) {
		if (result.getSingle(e) != null){
			switch (type){
			case 0:
				if (ingredients.getArray(e).length <= 0)
					return;
				String[] shapes = shape != null && shape.getArray(e).length <= 3 ? shape.getArray(e) : new String[]{"abc", "def", "ghi"};
				TuSKe.getRecipeManager().registerRecipe(new CustomShapedRecipe(result.getSingle(e), ingredients.getArray(e), shapes));
				break;
			case 1:
				if (ingredients.getArray(e).length <= 0)
					return;
				TuSKe.getRecipeManager().registerRecipe(new CustomShapelessRecipe(result.getSingle(e), ingredients.getArray(e)));
				break;
			case 2:
				if (ingredients.getSingle(e) == null)
					return;
				float n = exp != null && exp.getSingle(e).floatValue() > 0 ? exp.getSingle(e).floatValue() : 0F;
				TuSKe.getRecipeManager().registerRecipe(new CustomFurnaceRecipe(result.getSingle(e), ingredients.getSingle(e), n));
				break;
			}
		}
		
	}
}
