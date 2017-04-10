package me.tuke.sktuke.manager.recipe;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;

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
import me.tuke.sktuke.util.ReflectionUtils;

public class RecipeManager implements Listener{

	private Set<Recipe> recipes = new HashSet<>();
	public boolean equals = true; //Every time it checks the items, it will set it to false if the items are the same but have different item meta 
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPrepare(PrepareItemCraftEvent e){
		Recipe rec = getIfContainsCustomRecipe(e.getRecipe().getResult(), e.getInventory().getMatrix());
		if (rec != null){
			if (!equals){
				equals = true;
				TuSKe.debug("Clear");
				e.getInventory().setResult(new ItemStack(Material.AIR)); //workaround to cancel the event				
			} 
		}
	}
	public void registerRecipe(Recipe rec, boolean uniqueIngredients){
		if (uniqueIngredients && (rec instanceof CustomShapedRecipe || rec instanceof CustomShapelessRecipe || rec instanceof CustomFurnaceRecipe)){
			if (getIfContainsCustomRecipe(rec.getResult(), getIngredients(rec)) != null || !Bukkit.addRecipe(rec))
				return;
			recipes.add(rec);
			if (recipes.size() == 1)
				Bukkit.getPluginManager().registerEvents(this, TuSKe.getInstance());
		} else {
			Bukkit.addRecipe(rec);
		}
	}
	public Recipe getCustomRecipe(Recipe rec){
		for (Recipe recipe : recipes)
			if (rec.getClass() == recipe.getClass() && rec.getResult().equals(recipe.getResult()) && equalsRecipe(rec, getIngredients(recipe))){	
				equals = true;
				return recipe;
			}
		return null;		
	}
	@SuppressWarnings("unchecked")
	public void removeRecipe(Recipe... recipes){
		if (recipes == null || recipes.length == 0)
			return;
		String v = ReflectionUtils.packageVersion;
		Class<?> nmsItemClass = ReflectionUtils.getClass("net.minecraft.server.v" + v +".ItemStack");
		Method method = ReflectionUtils.getMethod(ReflectionUtils.getClass("org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack"), "asNMSCopy", ItemStack.class);
		Object craftingManager = ReflectionUtils.invokeMethod(ReflectionUtils.getClass("net.minecraft.server.v" + v + ".CraftingManager"), "getInstance", null, null);
		for (Recipe rec : recipes){
			if (rec == null)
				continue;
			Recipe custom = getCustomRecipe(rec);
			if (custom != null)
				this.recipes.remove(custom);
			Object nmsRecipe = null;
			if (rec instanceof ShapedRecipe){
				Class<?> shapedClass = ReflectionUtils.getClass("net.minecraft.server.v" + v +".ShapedRecipes");
				int height = ((ShapedRecipe) rec).getShape().length;
				int width = ((ShapedRecipe) rec).getShape()[0].length();
				ItemStack[] ingredients = getIngredients(rec);
				Object[] nmsItems = (Object[]) Array.newInstance(nmsItemClass, ingredients.length);
				int x = 0;
				for (ItemStack item : ingredients)
					nmsItems[x++] = ReflectionUtils.invokeMethod(method, null, item);
				Constructor<?> c = shapedClass.getDeclaredConstructors()[0];
				TuSKe.debug(c, shapedClass);
				//nmsRecipe = ReflectionUtils.newInstance(c, width, height, nmsItems ,ReflectionUtils.invokeMethod(method, null, rec.getResult()));
			} else if (rec instanceof ShapelessRecipe){
				Class<?> shapelessClass = ReflectionUtils.getClass("net.minecraft.server.v" + v +".ShapelessRecipes");
				ItemStack[] ingredients = getIngredients(rec);
				List<Object> list2 = new ArrayList<Object>();
				for (ItemStack item : ingredients)
					list2.add(ReflectionUtils.invokeMethod(method, null, item));
				List<Object> nmsRecipes = ((List<Object>)ReflectionUtils.getField(craftingManager.getClass(), craftingManager, "recipes"));
				label1: for (Object nmsRecipe2 : nmsRecipes){
					Object result1 = ReflectionUtils.invokeMethod(shapelessClass, "b", nmsRecipe2, null);
					Object result2 = ReflectionUtils.invokeMethod(method, null, rec.getResult());					
					if (!(result1 + "").equalsIgnoreCase(result2 + ""))
						continue label1;
					List<Object> list1 = ReflectionUtils.invokeMethod(shapelessClass, "getIngredients", nmsRecipe2, null);
					for (int x = 0; x < list1.size() && x < list2.size(); x++){
						if (!(list1.get(x) + "").equalsIgnoreCase(list2.get(x) + ""))
							continue label1;
					}
					nmsRecipe = nmsRecipe2;
					break;
				}
			} else if (rec instanceof FurnaceRecipe){
				Object furnaceRecipes = ReflectionUtils.invokeMethod(ReflectionUtils.getClass("net.minecraft.server.v"+v+".RecipesFurnace"), "getInstance", null, null);
				Object input = ReflectionUtils.invokeMethod(method, null, ((FurnaceRecipe) rec).getInput());
				Object output = ReflectionUtils.invokeMethod(method, null, rec.getResult());
				Map<?, ?> recs = ReflectionUtils.getField(furnaceRecipes.getClass(), furnaceRecipes, "recipes");
				for (Entry<?, ?> entry : recs.entrySet()){
					if (entry.getKey().toString().equals(input.toString()) &&  entry.getValue().toString().equalsIgnoreCase(output.toString())){
						input = entry.getKey();
						output = entry.getValue();
						break;
					}
				}			
				((Map<?, ?>) ReflectionUtils.getField(furnaceRecipes.getClass(), furnaceRecipes, "customRecipes")).remove(input);
				((Map<?, ?>) ReflectionUtils.getField(furnaceRecipes.getClass(), furnaceRecipes, "recipes")).remove(input);
				((Map<?, ?>) ReflectionUtils.getField(furnaceRecipes.getClass(), furnaceRecipes, "c")).remove(output);			
				return;
			}
			if (nmsRecipe != null){
				((List<?>)ReflectionUtils.getField(craftingManager.getClass(), craftingManager, "recipes")).remove(nmsRecipe);
			}
			
		}
		
	}
	
	public Recipe getIfContainsCustomRecipe(ItemStack result, ItemStack[] items){
		Recipe rec = null; //to set the last matched recipe
		for (Recipe recipe : recipes)
			if (result.equals(recipe.getResult()) && equalsRecipe(recipe, items)){		
				rec = recipe;
				if (equals) //in case the ingredient matches and have same item metas,
					break;
				else //else it will check if there is another recipe similiar,
					continue;
			}
		return rec;
	}
	public boolean equalsRecipe(Recipe rec, ItemStack[] items){
		equals = true;
		if (rec == null || items == null || items.length == 0)
			return false;
		if (rec instanceof ShapedRecipe){
			ShapedRecipe sr = (ShapedRecipe) rec;
			Map<Character, ItemStack> map = sr.getIngredientMap();
			int first = 3-  sr.getShape()[0].length(); 
			if (items.length < 9)
				first-=1;//simple fix in case the crafting is player's crafting inventory
			char ch = 'a';
			for(int x = 0; x < items.length; x++){
				Character c = Character.valueOf(ch);
				if (map.containsKey(c)){
					if (areEqual(map.get(c), items[x])){
						ch++;
						if ((x + 1)/3 > x/3 ){ // it will check if the pointer reached at the end of line
							x += first; //it moves the pointer to the next line and to the right columm
						}
					} else if (ch > 'a'){
						return false;
					}
				} else if (ch > 'a' && !isAir(items[x])){
					return false;
				} else {
					break;//reached at end of check and found all items from recipe.
				}
			}
			return map.size() == (int)ch - 97 ;
		} else if (rec instanceof ShapelessRecipe){ //need to improve this
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
			item2 = new ItemStack(Material.AIR);
		if (item1.getDurability() == 32767)
			item1.setDurability(item2.getDurability());
		//                                                                                the item recipe can be equal or less the amount on crafting slot
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
	public ItemStack[] getShapedIngredients(ShapedRecipe sr) {
		Map<Character, ItemStack> map = sr.getIngredientMap();
		if (map.size() < 9) {
			ItemStack[] items = new ItemStack[9];
			String[] shape = sr.getShape();
			int x = 0;
			for (String str : shape) {
				for (Character ch : str.toCharArray()) {
					items[x++] = map.get(ch);
				}
				x += 3 - str.length();
			}
			return fixIngredients(items);
		} else
			return map.values().toArray(new ItemStack[map.size()]);
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
		if (item.getDurability() == (short) 32767){
			item.setDurability((short)0);
		}
		if (item.getAmount() <= 0)
			item.setAmount(1);
		return item;
	}
	public void clearRecipes(){
		HandlerList.unregisterAll(this);
		recipes.clear();
	}
}
