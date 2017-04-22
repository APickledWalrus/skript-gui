package me.tuke.sktuke.manager.recipe;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class CustomShapedRecipe extends ShapedRecipe{
	
	private Map<Character, ItemStack> map = new HashMap<>();
	
	public CustomShapedRecipe(ItemStack r, ItemStack[] items, String...shapes){
		super(r);
		for (int x = 0; x < shapes.length; x++){
			shapes[x] = shapes[x].toLowerCase();
			if (!shapes[x].matches("[a-i\\s]{1,3}"))
				return;			
		}
		shape(shapes);		
		char c = 'a';
		for (String shape : shapes)
			for (int x = 0;x < shape.length(); x++){
				int index = shape.charAt(x) - 97;
				if (index < items.length){
					map.put(c, items[index]);
					setIngredient(c, items[index].getData());
				}
				c++;
			}
	}
	@Override
	public Map<Character, ItemStack> getIngredientMap(){
		return map;
	}
	public ItemStack[] getIngredients(){
		return map.values().toArray(new ItemStack[map.size()]);
	}
}
