package me.tuke.sktuke.expressions;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import com.google.common.collect.Lists;

import java.util.List;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

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
		if (this.is.getAll(e)[0] instanceof Recipe){
			if (this.is.getAll(e)[0] == null)
				return null;
			ItemStack item = ((Recipe)this.is.getAll(e)[0]).getResult();
			if (item.getAmount() <= 0)
				item.setAmount(1);
			return new ItemStack[] {item};
		}
		if ( this.is.getAll(e) == null || this.is.getAll(e).length >9)
			return null;
		ItemStack[] i = (ItemStack[]) this.is.getAll(e);
		for (int x = 0; x < i.length; x++){
			if (i[x].getType() == Material.AIR)
				i[x] = null;
			}
		List<ItemStack> list = Lists.newArrayList(i);
		for (Recipe r : Lists.newArrayList(Bukkit.recipeIterator())){
			if (r instanceof ShapelessRecipe){
				if (areEqual(((ShapelessRecipe)r).getIngredientList(), list)){
					ItemStack i2 = r.getResult();
					i2.setAmount(1);
					return new ItemStack[] {i2};
				}
			}
			else if (r instanceof ShapedRecipe){
				
				if (areEqual(Lists.newArrayList(((ShapedRecipe) r).getIngredientMap().values()), list)){
					ItemStack i1 = r.getResult();
					i1.setAmount(1);
					return new ItemStack[] {i1};					
				}
			}
		}
		return null;		
	}
	
	
	private boolean areEqual(List<ItemStack> list1,List<ItemStack> list2){
		int max = list1.size();
		if (list2.size() < max)
			max = list2.size();
		for (int x = 0; x < max; x++){
			if (list1.get(x) != null && list2.get(x) != null){
				if (list1.get(x).getType().equals(list2.get(x).getType()) && list1.get(x).getDurability() == 32767)
					continue;
				else if (!list1.get(x).getData().equals(list2.get(x).getData()))
					return false;
			} else if (list1.get(x) == null ^ list2.get(x) == null)
				return false;
		}
		if (list1.size() != list2.size()){
			List<ItemStack> high = getHighiest(list1, list2);
			for (int x = max; x < high.size(); x++)
				if (high.get(x) != null)
					return false;
		}
		return true;
	}
	
	private List<ItemStack> getHighiest(List<ItemStack> list1,List<ItemStack> list2){
		if (list1.size() > list2.size())
			return list1;
		return list2;
		
	}
}
