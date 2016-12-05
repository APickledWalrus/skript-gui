package me.tuke.sktuke.expressions;

import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import me.tuke.sktuke.TuSKe;

public class ExprItemsOfRecipe extends SimpleExpression<ItemStack>{
	
	private Expression<Object> r;

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
		this.r = (Expression<Object>) arg[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "ingredients of " + this.r;
	}

	@Override
	@Nullable
	protected ItemStack[] get(Event e) {
		if (this.r.getSingle(e) == null)
			return null;
		Object r = this.r.getSingle(e);
		TuSKe.debug(r, TuSKe.getRecipeManager().getItems((Recipe)r));
		if (r instanceof ShapelessRecipe)
			return  ((ShapelessRecipe) r).getIngredientList().toArray(new ItemStack[((ShapelessRecipe) r).getIngredientList().size()]);
		else if (r instanceof ShapedRecipe){
			ArrayList<ItemStack> ai = new ArrayList<ItemStack>();
			for (char a = 'a'; a < 'j'; a++)
				if (((ShapedRecipe) r).getIngredientMap().get(a) == null)
					ai.add(new ItemStack(Material.AIR));
				else if (((ShapedRecipe) r).getIngredientMap().get(a).getDurability() > ((ShapedRecipe) r).getIngredientMap().get(a).getType().getMaxDurability()){
					ItemStack i = ((ShapedRecipe) r).getIngredientMap().get(a);
					i.setDurability((short)0);
					ai.add(i);
				} else
					ai.add(((ShapedRecipe) r).getIngredientMap().get(a));
					
			return ai.toArray(new ItemStack[ai.size()]);
		}
		else if (r instanceof FurnaceRecipe)
			return new ItemStack[] {((FurnaceRecipe) r).getInput()};
		return null;
	}

}
