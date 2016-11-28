package me.tuke.sktuke.recipe;

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
					this.addIngredient(items[x].getData());
				if (items[x].isSimilar(new ItemStack(items[x].getType(), items[x].getAmount(), items[x].getDurability())))
					items[x] = null;
			}
		}
		this.items = items;
	}
	public ItemStack[] getCustomIngredients(){
		return items;
	}
	public ItemStack[] getIngredients(){
		ItemStack[] item1 = items.clone();
		ItemStack[] item2 = this.getIngredientList().toArray(new ItemStack[item1.length]);
		for (int x = 0; x < item1.length; x++)
			if (item1[x] == null)
				item1[x] = item2[x];
		return item1;
	}
}
