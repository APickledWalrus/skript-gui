package me.tuke.sktuke.recipe;

import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;

public class CustomFurnaceRecipe extends FurnaceRecipe{

	private ItemStack source = null;
	
	public CustomFurnaceRecipe(ItemStack result, ItemStack source, float experience) {
		super(result, source.getData(), experience);
		source.setAmount(1);
		if (source.isSimilar(new ItemStack(source.getType(), source.getAmount(), source.getDurability())))
			this.source = source;
	}
	public ItemStack getSource(){
		return source != null ? source : getInput();
	}
	public ItemStack getCustomSource(){
		return source;
	}

}
