package com.github.tukenuke.tuske.manager.customenchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import ch.njol.skript.Skript;
import ch.njol.skript.util.Version;

public class EnchantManager {
	
	public static boolean isCustomByID(String enchant){
		return CustomEnchantment.getByID(enchant) != null;
	}
	public static boolean isCustomByName(String enchant){
		return CustomEnchantment.getByName(enchant) != null;
	}
	public static String getCorrectName(String enchant){
		enchant = ChatColor.stripColor(enchant);
		if (enchant.contains(" ")){
			String[] ench = enchant.split(" ");
			try {
				if (toArabic(ench[ench.length-1]) >= 0)
					enchant = enchant.replace(" " + ench[ench.length-1], "");
			} catch (Exception e){}
		}
		return enchant;
	}
	public int getLevel(CustomEnchantment ce,ItemStack item){
		return 0;
	}
	public static String getStringEnchant(CustomEnchantment ce,int Level){
		if (Skript.getMinecraftVersion().isLargerThan(new Version("1.9")) && Level == 1)
			return ChatColor.GRAY + ce.getName();
		return ChatColor.GRAY + ce.getName() + " " + toRoman(Level);
	}
	public static boolean addToItem(ItemStack item, CustomEnchantment enchant, int Level, boolean NoEnchant){
		//TuSKe.log("AddToItem value: Compatible = " + (enchant.isCompatible(item)) + " && " + getNumberOfEnchants(item, enchant) +" < "+getMaxPerItem()+" = " + (getNumberOfEnchants(item, enchant) < getMaxPerItem()));
		if (enchant.isCompatible(item) && getNumberOfEnchants(item, enchant) < getMaxPerItem()){
			List<String> lore = new ArrayList<String>();
			if (Level > enchant.getMaxLevel())
				Level = enchant.getMaxLevel();
			if (NoEnchant && item.getEnchantments().size() == 0){
				switch (item.getType()){
				case BOOK:
					item.setType(Material.ENCHANTED_BOOK);
				case ENCHANTED_BOOK:
					EnchantmentStorageMeta aa = (EnchantmentStorageMeta) item.getItemMeta();
					if (aa.getStoredEnchants().size() == 0){
						aa.addStoredEnchant(Enchantment.DURABILITY, 1, false);
						item.setItemMeta(aa);
					}
					break;
				default: item.addEnchantment(Enchantment.DURABILITY, 1); break;
				}
			}
			if (item.getItemMeta().getLore() != null)
					lore = item.getItemMeta().getLore();
			int equals = -1;
			for (int x = 0; x < lore.size(); x++){
				String s = getCorrectName(lore.get(x));
				if (isCustomByName(s)){
					CustomEnchantment cc = CustomEnchantment.getByName(s);
					if (cc.equals(enchant))
						equals = x;
					if (item.getType() != Material.ENCHANTED_BOOK && (enchant.getConflicts().contains(cc) || cc.getConflicts().contains(enchant))){
						return false;
					}
				}
			}
			if (equals < 0 || lore.size() == 0)
				lore.add(getStringEnchant(enchant, Level));
			else 
				lore.set(equals, getStringEnchant(enchant, Level));
			ItemMeta meta = item.getItemMeta();
			meta.setLore(lore);
			item.setItemMeta(meta);
			return true;
		}
		return false;
	}
	public static int getMaxPerItem(){
		return EnchantConfig.y.getInt("Config.MaxEnchantmentsPerItem");
	}
	public static boolean removeFromItem(ItemStack item, CustomEnchantment enchant){
		if (CustomEnchantment.getCustomEnchants(item).containsKey(enchant)){
			List<String> lore = item.getItemMeta().getLore();
			for (int x = 0; x < lore.size(); x++){
				if (getCorrectName(lore.get(x)).equalsIgnoreCase(enchant.getName())){				
					lore.remove(x);
					ItemMeta im = item.getItemMeta();
					im.setLore(lore);
					item.setItemMeta(im);
				}
			}
		}
			
		return false;
	}
	public static int getStringLevel(String enchant){
		enchant = ChatColor.stripColor(enchant);
		String ec = getCorrectName(enchant);
		enchant = enchant.replaceAll(ec, "").replaceAll(" ", "");
		if (enchant.equals(""))
			return 1;
		else{
			//TuSKe.log("strlevel: -"+ec+ "-"+ enchant+ "---- " +name+ " === " + getCorrectName(CustomEnchantment.getByID("Emeraude").getName() + " I") );
			int i = toArabic(enchant);
			int max = CustomEnchantment.getByName(ec).getMaxLevel();
			return (i <= max) ? i : max;
		}
	}

	public static int getNumberOfEnchants(ItemStack item, CustomEnchantment enchant){
		int x = 0;
		Map<CustomEnchantment, Integer> enchs = CustomEnchantment.getCustomEnchants(item);
		if (enchs.containsKey(enchant))
			x++;
		if (EnchantConfig.y.getBoolean("Config.CompatibilityMode")){
			if (item.getItemMeta().getLore() == null)
				return 0;
			return item.getItemMeta().getLore().size() - x;
		} 
		return enchs.size() - x;
	}
	
	final static TreeMap<Integer, String> map = new TreeMap<Integer, String>();	
    static {
    	map.put(1000, "M");
    	map.put(900, "CM");
    	map.put(500, "D");
    	map.put(400, "CD");
    	map.put(100, "C");
    	map.put(90, "XC");
    	map.put(50, "L");
    	map.put(40, "XL");
        map.put(10, "X");
        map.put(9, "IX");
        map.put(5, "V");
        map.put(4, "IV");
        map.put(1, "I");
    }

    public final static String toRoman(int number) {
    	if (number <= 0)
    		return "";
        int l =  map.floorKey(number);
        if ( number == l ) {
            return map.get(number);
        }
        return map.get(l) + toRoman(number-l);
    }

    public final static int toArabic(String number){
    	for (Integer key : map.keySet()){
    		String[] s = number.split("");
    		if (s.length > 1){
    			if (map.get(key).equals(s[0]) && key >= toArabic(s[1]))
    				return key + toArabic(number.replaceFirst(map.get(key), ""));
    			if (map.get(key).equals(s[0]))
    				return toArabic(number.replaceFirst(map.get(key), "")) - key;
    		}
    		if (map.get(key).equals(number))
    			return key;
    	}
    	return 0;
    }

}