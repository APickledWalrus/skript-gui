package me.tuke.sktuke.manager.recipe;

import java.util.*;

import ch.njol.skript.Skript;
import javafx.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
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

	private Set<Recipe> recipes = new HashSet<>();
	//Since Bukkit doesn't provide any converter fro NamespacedKey -> Recipe, I made on my own
	//For 1.12+ only
	private Map<Object, Recipe> keys = Skript.isRunningMinecraft(1,12) ? new HashMap<>() : null;

	@EventHandler(priority = EventPriority.LOW)
	public void onPrepare(PrepareItemCraftEvent e){
		// 1 == Everything is matching correctly, let it work alone
		// 0 == Found a matching recipe but the ingredients doesn't have same item meta
		// -1 == No custom recipe found, let it work alone
		Pair<Integer, Recipe> pair = getMatchingRecipe(e.getInventory().getMatrix());
		if (pair.getKey() == 0) {
			e.getInventory().setResult(new ItemStack(Material.AIR)); // workaround to cancel the event
		} else if (pair.getKey() == 1) {
			e.getInventory().setResult(pair.getValue().getResult()); // workaround to make same recipes with different ingredients' nbts work.
			Bukkit.getScheduler().runTaskLater(TuSKe.getInstance(), () -> ((Player)e.getView().getPlayer()).updateInventory(), 12L);
		}
	}
	public void registerRecipe(Recipe rec, boolean uniqueIngredients){
		if (uniqueIngredients && (rec instanceof CustomShapedRecipe || rec instanceof CustomShapelessRecipe || rec instanceof CustomFurnaceRecipe)){
			if (getIfContainsCustomRecipe(rec.getResult(), getIngredients(rec)) != null || !Bukkit.addRecipe(rec) || rec instanceof CustomFurnaceRecipe)
				return;
			recipes.add(rec);
			if (recipes.size() == 1)
				Bukkit.getPluginManager().registerEvents(this, TuSKe.getInstance());
		} else {
			Bukkit.addRecipe(rec);
		}
		if (keys != null && keys.size() > 0 && rec instanceof Keyed)
			keys.put(((Keyed) rec).getKey(), rec);
	}

	/**
	 * Get a matching level of a recipe.
	 * -1 = Doesn't matching anything
	 * 0 = Match a recipe but only its type and data
	 * 1 = Fully matching recipe
	 *
	 * It will look for all custom recipes and try to find a recipe that has
	 * the matching level 1, if it doesn't, it will use the last matching 0,
	 * or -1 if didn't found nothing.
	 * It should be used to compare only.
	 *
	 * @param ingredients - The ingredients to match with a recipe
	 * @return The matching level
	 */
	public Pair<Integer, Recipe> getMatchingRecipe(ItemStack... ingredients) {
		int matchingLevel = -1;
		Recipe r = null;
		for (Recipe recipe : recipes) {
			int count = equalsRecipe(recipe, ingredients);
			if (count >= 0) {
				r = recipe;
				matchingLevel = count;
				if (matchingLevel > 0) //If matchingLevel is 1, it found a exactly match (wiht same ItemMeta)
					break;
				//Else it will try to find another matching one.
			}
		}
		return new Pair<>(matchingLevel, r);
	}
	public Recipe getCustomRecipe(Recipe rec){
		/**for (Recipe recipe : recipes)
			if (rec.getClass() == recipe.getClass() && rec.getResult().equals(recipe.getResult()) && equalsRecipe(rec, getIngredients(recipe))){
				return recipe;
			}*/
		return getIfContainsCustomRecipe(rec.getResult(), getIngredients(rec));
	}

	@SuppressWarnings("unchecked")
	public void removeRecipe(Recipe... recipes){
		if (recipes == null || recipes.length == 0)
			return;
		Iterator<Recipe> it = Bukkit.recipeIterator();
		while(it.hasNext()) {
			Recipe r = it.next();
			for (int x = 0; x < recipes.length; x++) {
				if (r.getResult().equals(recipes[x].getResult())) {
					ItemStack[] ingredients = getIngredients(recipes[x]);
					if (equalsRecipe(r, ingredients) == 1) {
						removeCustomRecipe(r);
						it.remove();
						recipes[x] = null;
						if (keys != null && r instanceof Keyed) //the keys will be always null in 1.11 and lower
							keys.remove(((Keyed) r).getKey());
						if (x == recipes.length)
							break;
					}
				}
			}
		}
	}
	
	public Recipe getIfContainsCustomRecipe(ItemStack result, ItemStack[] items){
		Recipe rec = null; //to set the last matched recipe
		int matchingLevel;
		for (Recipe recipe : recipes) {
			if (result.equals(recipe.getResult())) {
				matchingLevel = equalsRecipe(recipe, items);
				if (matchingLevel >= 0) {
					rec = recipe;
					if (matchingLevel > 0) //If matchingLevel is 1, it found a exactly match (wiht same ItemMeta)
						break;
					//Else it will try to find another matching one.
				}
			}
		}
		return rec;
	}
	public int equalsRecipe(Recipe rec, ItemStack[] items){
		if (rec == null || items == null || items.length == 0)
			return -1;
		if (rec instanceof ShapedRecipe) {
			Map<Character, ItemStack> map = rec instanceof CustomShapedRecipe ? ((CustomShapedRecipe) rec).getIngredientsMap() : ((ShapedRecipe) rec).getIngredientMap();
			int length = ((ShapedRecipe) rec).getShape()[0].length();
			boolean is2x2 = items.length < 9;
			char ch = 'a';
			int count = -1;
			int slot = 0;
			for(int x = 0; x < items.length; x++){
				if (x >= items.length)
					break;
				ItemStack item = map.get(ch);
				if (item != null){
					int y = areEqual(item, items[x]);
					if ((y == 1 && count == -1) || y == 0)
						count = y;
					if (y >= 0) {
						ch++;
						if (++slot == length) { // it will check if the pointer reached at the end of craft gride
							slot = 0;
							//it moves the pointer to the next gride
							if (is2x2)
								x += 2 - length;
							else
								x += 3 - length;
						}
					} else if (ch > 'a'){
						return -1;
					}
				} else if (ch > 'a' && !isAir(items[x])){ //In case it found all items already but there is some non-air item remaining
					return -1;
				}
			}
			return map.size() == (int)ch - 'a' ? count : -1;
		} else if (rec instanceof ShapelessRecipe){ //need to improve this
			int itemsFound = 0;
			List<ItemStack> ingredients = ((ShapelessRecipe)rec).getIngredientList();
			int count = 1;
			label1: for (ItemStack item1 : ingredients)
				for (ItemStack item2 : items)
					if (!isAir(item1) && !isAir(item2)) {
						int y = areEqual(item1, item2);
						if (y < count)
							count = 0;
						if (y >= 0) {
							itemsFound++;
							break label1;
						}
					}
			return itemsFound == ingredients.size() ? count : -1;
		}
		return -1;
	}
	public boolean isAir(ItemStack item){
		//some recipes and items uses null as air item.
		return item == null || item.getType().equals(Material.AIR);
	}
	public int areEqual(ItemStack item1, ItemStack item2){
		if (isAir(item1) && isAir(item2))
			return 1;
		if (item1 == null || item2 == null)
			return -1;
		if (item1.getDurability() == 32767 || item2.getDurability() == 32767)
			item1.setDurability(item2.getDurability());
		if (item1.getType().equals(item2.getType()) && item1.getDurability() == item2.getDurability() && item1.getAmount() <= item2.getAmount()){
			if ((!item1.hasItemMeta() && !item2.hasItemMeta()) || (item1.hasItemMeta() && item2.hasItemMeta() && Bukkit.getItemFactory().equals(item1.getItemMeta(), item2.getItemMeta())))
				return 1;
			/*else if (item1.hasItemMeta() && item1.getItemMeta() instanceof SkullMeta) {
				SkullMeta meta1 = (SkullMeta) item1.getItemMeta();
				SkullMeta meta2 = (SkullMeta) item1.getItemMeta();
				if (meta)
			}*/
			return 0;
		}
		return -1;
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
			//return getShapedIngredients((ShapedRecipe) rec);
		}else if (rec instanceof ShapelessRecipe){
			List<ItemStack> items = ((ShapelessRecipe) rec).getIngredientList();
			return items.toArray(new ItemStack[items.size()]);
		}else
			return new ItemStack[]{((FurnaceRecipe)rec).getInput()};
	}
	public ItemStack[] getShapedIngredients(ShapedRecipe sr) {
		Map<Character, ItemStack> map = sr.getIngredientMap();
		ItemStack[] items;
		if (map.size() < 9) {
			items = new ItemStack[9];
			String[] shape = sr.getShape();
			int x = 0;
			for (String str : shape) {
				for (Character ch : str.toCharArray()) {
					items[x++] = map.get(ch);
				}
				x += 3 - str.length();
			}
		} else
			items = map.values().toArray(new ItemStack[map.size()]);
		return fixIngredients(items);
	}
	public ItemStack[] fixIngredients(ItemStack[] items){
		for (int x = 0; x < items.length; x++) {
			items[x] = fixItem(items[x]);
		}
		return items;
		
	}
	private ItemStack fixItem(ItemStack item) {
		if (item == null)
			return new ItemStack(Material.AIR);
		if (item.getDurability() == (short) 32767)
			item.setDurability((short)0);
		if (item.getAmount() <= 0)
			item.setAmount(1);
		return item;
	}
	public void removeCustomRecipe(Recipe... recs) {
		for (Recipe r : recs) {
			Recipe rec = getCustomRecipe(r);
			if (rec != null)
				recipes.remove(rec);
		}
		if (recipes.size() == 0)
			HandlerList.unregisterAll(this);
	}
	public void clearRecipes(){
		HandlerList.unregisterAll(this);
		recipes.clear();
		if (keys != null)
			keys.clear();
	}
	public CustomShapedRecipe newShapedRecipe(ItemStack result, ItemStack[] items, String... shapes) {
		if (keys == null) // null = 1.11 and lower
			return new CustomShapedRecipe(result, items, UUID.randomUUID().toString(), shapes);
		else
			return new CustomShapedRecipe(result, items, shapes);
	}
	public CustomShapelessRecipe newShapelessRecipe(ItemStack result, ItemStack[] items) {
		if (keys == null) // null = 1.11 and lower
			return new CustomShapelessRecipe(result, items, UUID.randomUUID().toString());
		else
			return new CustomShapelessRecipe(result, items);
	}
	public Recipe getRecipeFromKey(Object key) {
		if (keys != null && key != null) {
			Recipe result = keys.get(key);
			if (result == null) { // Only happens in first usage or when a new recipe was registered by another plugin
				//So let's update that list
				Iterator<Recipe> recipes = Bukkit.recipeIterator();
				keys.clear();
				while (recipes.hasNext()) {
					Recipe r = recipes.next();
					if (r instanceof Keyed) {
						keys.put(((Keyed) r).getKey(), r);
						// while we are updating, let's save time and check the recipe
						if (((Keyed) r).getKey().equals(key))
							result = r;
					}
				}
			}
			return result;
		}
		return null;
	}
}
