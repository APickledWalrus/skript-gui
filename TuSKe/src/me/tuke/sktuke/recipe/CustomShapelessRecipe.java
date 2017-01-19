package me.tuke.sktuke.recipe;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;


public class CustomShapelessRecipe extends ShapelessRecipe{
	
	private ItemStack[] items = null;
	
	public CustomShapelessRecipe(ItemStack r, ItemStack[] items) {
		super(r);
		for (int x = 0; x < items.length; x++){
			if (items[x] != null){
				if (items[x].getType() != Material.AIR)
					this.addIngredient(items[x].getAmount(), items[x].getData());
			}
		}
		this.items = items;
	}
	@Override
	public List<ItemStack> getIngredientList(){
		return Arrays.asList(items);
	}
	public ItemStack[] getIngredients(){
		return items;
	}
}
