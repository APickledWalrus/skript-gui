package me.tuke.sktuke.expressions.recipe;

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

public class ExprResultOfRecipe extends SimpleExpression<ItemStack>{

	private Expression<Object> is;
	@Override
	public Class<? extends ItemStack> getReturnType() {
		return ItemStack.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		this.is = (Expression<Object>) arg[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "result item of " + this.is;
	}

	@Override
	@Nullable
	protected ItemStack[] get(Event e) {
		Object[] objs = is.getAll(e);
		if (objs.length > 0 && objs[0] != null) {
			if (objs[0] instanceof Recipe)
				return TuSKe.getRecipeManager().fixIngredients(new ItemStack[]{((Recipe)objs[0]).getResult()});
			else if (objs.length <= 9){
				ItemStack[] items = (ItemStack[]) this.is.getAll(e);
				for (Recipe r : Lists.newArrayList(Bukkit.recipeIterator())){
					if (TuSKe.getRecipeManager().equalsRecipe(r, items)){
						TuSKe.getRecipeManager().equals = true;
						return TuSKe.getRecipeManager().fixIngredients(new ItemStack[]{r.getResult()});
					}
				}
			}
		}
		return null;		
	}
}
