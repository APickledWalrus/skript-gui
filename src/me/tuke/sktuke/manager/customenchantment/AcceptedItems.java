package me.tuke.sktuke.manager.customenchantment;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum AcceptedItems {    
	BOOTS (new Material[]{Material.DIAMOND_BOOTS}),    
	LEGGINGS  (new Material[]{Material.DIAMOND_LEGGINGS}),  
	CHESTPLATES (new Material[]{Material.DIAMOND_CHESTPLATE}),    
	HELMETS (new Material[]{Material.DIAMOND_HELMET}),    
	SWORDS (new Material[]{Material.DIAMOND_SWORD}),     
	BOW (new Material[]{Material.BOW}),  
	FISHINGROD (new Material[] {Material.FISHING_ROD}), 
	PICKAXES (new Material[]{Material.DIAMOND_PICKAXE}),  
	AXES (new Material[]{Material.DIAMOND_AXE}),  
	SHOVELS (new Material[]{Material.DIAMOND_SPADE}), 
	TOOLS (new Material[]{Material.DIAMOND_PICKAXE, Material.DIAMOND_AXE, Material.DIAMOND_SPADE}), 
	ARMOR(new Material[]{Material.DIAMOND_BOOTS, Material.DIAMOND_LEGGINGS, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_HELMET}), 
	ALL(new Material[]{Material.DIAMOND_BOOTS, Material.DIAMOND_LEGGINGS, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_HELMET, Material.DIAMOND_PICKAXE, Material.DIAMOND_AXE, Material.DIAMOND_SPADE, Material.FISHING_ROD, Material.BOW, Material.DIAMOND_SWORD});

	public Material[] valor;
	AcceptedItems(Material[] m){
		this.valor = m;
		
	}
	public boolean accepts(ItemStack i){ 
		if (i == null)
			return false;
		Material mat = i.getType();
		for (Material m : valor){
			if (m != Material.BOW && (mat.toString().endsWith("_" + m.toString().split("_")[m.toString().split("_").length - 1]) || mat == Material.ENCHANTED_BOOK || mat == Material.BOOK))
				return true;
			else if (mat == m)
				return true;
		}
		return false;
	}
	public static boolean isValue(String s){
		try {
			valueOf(s);
		} catch (Exception e){
			return false;
		}
		return true;
	}
	public static List<AcceptedItems> getArrayList(String[] str){
		List<AcceptedItems> ai = new ArrayList<AcceptedItems>();
		for (int x = 0; x < str.length; x++)
			if (str[x] != null && isValue(str[x].toUpperCase()))
				ai.add(valueOf(str[x].toUpperCase()));
		if (ai.size() == 0)
			ai.add(AcceptedItems.ALL);
		return ai;
		
	}
}
