package me.tuke.sktuke.manager.recipe;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

/**
 * @author Tuke_Nuke on 06/07/2017
 */
public interface CustomRecipe extends Recipe {

	ItemStack[] getIngredients();
}
