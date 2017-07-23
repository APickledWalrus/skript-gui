package me.tuke.sktuke.manager.recipe;

import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;

public class CustomFurnaceRecipe extends FurnaceRecipe implements CustomRecipe {

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

	@Override
	public ItemStack[] getIngredients() {
		return new ItemStack[] {getSource()};
	}
}
