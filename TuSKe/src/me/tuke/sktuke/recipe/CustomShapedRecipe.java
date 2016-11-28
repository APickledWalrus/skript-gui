package me.tuke.sktuke.recipe;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class CustomShapedRecipe extends ShapedRecipe{
	
	private ItemStack[] items = null;
	
	public CustomShapedRecipe(ItemStack r, ItemStack[] items){
		super(r);
		shape("abc", "def", "ghi");
		char c = 'a';
		for (int x = 0; x < items.length; x++){
			if (items[x].getType() != Material.AIR)
				setIngredient(c, items[x].getData());
			if (items[x] != null && items[x].isSimilar(new ItemStack(items[x].getType(), items[x].getAmount(), items[x].getDurability())))
				items[x] = null;
			c++;
		}
		this.items = items;
	}
	public ItemStack[] getCustomIngredients(){
		return items;
	}
	
	public ItemStack[] getIngredients(){
		ItemStack[] item1 = items.clone();
		ItemStack[] item2 = this.getIngredientMap().values().toArray(new ItemStack[this.getIngredientMap().size()]);
		for (int x = 0; x < item1.length; x++)
			if (item1[x] == null)
				item1[x] = item2[x];
		return item1;
	}
}
