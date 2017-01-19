package me.tuke.sktuke.recipe;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import me.tuke.sktuke.TuSKe;

public class RecipeManager implements Listener{

	private HashSet<Recipe> recipes = new HashSet<>();
	public boolean equals = true; //Every time it checks the items, it will set it to true if the items are the same but have different item meta 
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPrepare(PrepareItemCraftEvent e){
		Recipe rec = getIfContainsCustomRecipe(e.getRecipe().getResult(), e.getInventory().getMatrix());
		if (rec != null){
			if (!equals){
				equals = true;
				e.getInventory().setResult(new ItemStack(Material.AIR)); //workaround to cancel the event				
			}
		}
	}
	public void registerRecipe(Recipe rec){
		if (rec instanceof CustomShapedRecipe || rec instanceof CustomShapelessRecipe || rec instanceof CustomFurnaceRecipe){
			if (getIfContainsCustomRecipe(rec.getResult(), getIngredients(rec)) != null || !Bukkit.addRecipe(rec))
				return;
			recipes.add(rec);
			if (recipes.size() == 1)
				Bukkit.getPluginManager().registerEvents(this, TuSKe.getInstance());
		}
	}
	
	public Recipe getIfContainsCustomRecipe(ItemStack result, ItemStack[] items){
		for (Recipe recipe : recipes)
			if (result.equals(recipe.getResult()) && equalsRecipe(recipe, items))
				return recipe;
		return null;
	}
	public boolean equalsRecipe(Recipe rec, ItemStack[] items){
		equals = true;
		if (rec == null || items == null || items.length == 0)
			return false;
		if (rec instanceof ShapedRecipe){
			ShapedRecipe sr = (ShapedRecipe) rec;
			Map<Character, ItemStack> map = sr.getIngredientMap();
			boolean found = false;
			int first = 3-  sr.getShape()[0].length(); 
			if (items.length < 9)
				first-=1;//simple fix in case the crafting is player's crafting inventory
			char ch = 'a';
			for(int x = 0; x < items.length; x++){
				Character c = Character.valueOf(ch);
				if (map.containsKey(c)){
					if (areEqual(map.get(c), items[x])){
						ch++;
						found = true;
						if ((x + 1)/3 > x/3 ){ // it will check if the pointer reached at the end of line
							x += first; //it moves the pointer to the next line and to the right columm
						}
					} else if (found){
						return false;
					}
				} else {
					break;//reached at end of check and found all items from recipe.
				}
			}
			return true;
		} else if (rec instanceof ShapelessRecipe){
			int itemsFound = 0;
			List<ItemStack> ingredients = ((ShapelessRecipe)rec).getIngredientList();
			label1: for (ItemStack item1 : ingredients)
				for (ItemStack item2 : items)
					if (!isAir(item1) && !isAir(item2))
						if (areEqual(item1, item2)){
							itemsFound++;
							break label1;
						}
			return itemsFound == ingredients.size();
		}
		return false;
	}
	public boolean isAir(ItemStack item){
		return item == null || item.getType().equals(Material.AIR);
	}
	public boolean areEqual(ItemStack item1, ItemStack item2){
		//some recipes and items uses null as air item.
		if (item1 == null)
			item1 = new ItemStack(Material.AIR);
		if (item2 == null)
			item2 = new ItemStack(Material.AIR);//                                                      the item recipe can be equal or less the amount on crafting slot
		if (item1.getType().equals(item2.getType()) && item1.getDurability() == item2.getDurability() && item1.getAmount() <= item2.getAmount()){
			if (equals && !item1.isSimilar(item2))
				equals = false;
			return true;
		
		}
		return false;
		
		
	}
	public ItemStack[] getIngredients(Recipe rec){
		if (rec instanceof CustomShapedRecipe)
			return ((CustomShapedRecipe) rec).getIngredients();
		else if (rec instanceof CustomShapelessRecipe)
			return ((CustomShapelessRecipe) rec).getIngredients();
		else if (rec instanceof CustomFurnaceRecipe)
			return new ItemStack[]{((CustomFurnaceRecipe) rec).getSource()};
		else if (rec instanceof ShapedRecipe){
			Map<Character, ItemStack> map = ((ShapedRecipe) rec).getIngredientMap();
			return map.values().toArray(new ItemStack[map.size()]);
		}else if (rec instanceof ShapelessRecipe){
			List<ItemStack> items = ((ShapelessRecipe) rec).getIngredientList();
			return items.toArray(new ItemStack[items.size()]);
		}else
			return new ItemStack[]{((FurnaceRecipe)rec).getInput()};
	}
	public ItemStack[] fixIngredients(ItemStack[] items){
		for (int x = 0; x < items.length; x++){
			if (items[x] == null)
				items[x] = new ItemStack(Material.AIR);
			else if (items[x].getDurability() == (short) 32767){
				items[x].setDurability((short)0);
			}
			if (items[x].getAmount() <= 0)
				items[x].setAmount(1);
		}
		return items;
		
	}
	public void clearRecipes(){
		HandlerList.unregisterAll(this);
		recipes.clear();
	}
}
