package me.tuke.sktuke.customenchantment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CustomEnchantment {
	private static List<CustomEnchantment> all = new ArrayList<CustomEnchantment>();
	static boolean reg;
	private String id;
	private String name;
	private int MaxLevel;
	private int Rarity;
	private List<AcceptedItems> items;
	private boolean enabledOnTable;
	private boolean enabledOnAnvil;
	private String[] conflicts;
	private List<CustomEnchantment> conflict =  new ArrayList<CustomEnchantment>();
	
	private CustomEnchantment(String id, String name, int MaxLevel, int Rarity,  List<AcceptedItems> items, boolean enabledOnTable, boolean enabledOnAnvil, String[] conflicts){
		this.id = id;
		this.name = name;
		this.MaxLevel = MaxLevel;
		this.Rarity = Rarity;
		this.items = items;
		this.enabledOnTable = enabledOnTable;
		this.enabledOnAnvil = enabledOnAnvil;
		this.conflicts = conflicts;
		
	}
	public String getId(){
		return id;
	}
	public String getName(){
		return name;
	}
	public void setName(String name){
		this.name = name;
	}
	public int getMaxLevel(){
		return MaxLevel;
	}
	public void setMaxLevel(int i){
		this.MaxLevel = i;
	}
	public int getRarity(){
		return Rarity;
	}
	public void setRarity(int i){
		Rarity = i;
	}
	public  List<AcceptedItems> getAcceptedItems(){
		return items;
	}
	public void setAcceptedItems(List<AcceptedItems> ai){
		items = ai;
	}
	public boolean isEnabledOnTable(){
		return enabledOnTable;
	}
	public void setEnabledOnTable(boolean value){
		enabledOnTable = value;
	}
	public boolean isEnabledOnAnvil(){
		return enabledOnAnvil;
	}
	public void setEnabledOnAnvil(boolean value){
		enabledOnAnvil = value;
	}
	public List<CustomEnchantment> getConflicts(){
		return conflict;
	}
	public void setConflicts(List<CustomEnchantment> value){
		conflict = value;
	}
	public boolean isCompatible(ItemStack item){
		for (AcceptedItems ai: getAcceptedItems()){
			if (ai.accepts(item))
				return true;
		}
		return false;
	}
	public int getLevel(ItemStack item){
		Map<CustomEnchantment, Integer> list = getCustomEnchants(item);
		if (list.containsKey(this))
			return list.get(this);
		return 0;
	}
	public boolean equalsById(CustomEnchantment ce){
		return getId().equalsIgnoreCase(ce.getId());
	}
	public boolean equalsByName(CustomEnchantment ce){
		return getName().equalsIgnoreCase(ce.getName());
	}
	public static void acceptRegistration(boolean value){
		reg = value;
	}
	public static boolean isAcceptingRegistration(){
		return reg;
	}
	public static void unregisterEnchantment(CustomEnchantment ce){
		if (all.contains(ce))
			all.remove(ce);
	}
	public static void registerNewEnchantment(String id, String name, int MaxLevel, int Rarity, List<AcceptedItems> items, boolean enabledOnTable, boolean enabledOnAnvil, String[] conflicts){
		if (items.size() == 0)
			items.add(AcceptedItems.ALL);
		name = ChatColor.translateAlternateColorCodes('&', name);
		CustomEnchantment ce = new CustomEnchantment(id, name, MaxLevel, Rarity, items, enabledOnTable, enabledOnAnvil, conflicts);
		all.add(ce);
	}
	public static CustomEnchantment getByID(String enchant){
		for (CustomEnchantment ce : all)
			if (ce.getId().equalsIgnoreCase(enchant))
				return ce;
		return null;
	}
	public static CustomEnchantment getByName(String enchant){
		for (CustomEnchantment ce : all)
			if (ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', ce.getName())).equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', enchant))))
				return ce;
		return null;
		
	}
	public static void clear(){
		all.clear();
	}
	public static void stopRegistration(){
		acceptRegistration(false);
		for (CustomEnchantment ce : all){
			List<CustomEnchantment> conf = new ArrayList<CustomEnchantment>();
			for (int x = 0; x < ce.conflicts.length; x++){
				CustomEnchantment ce2 = getByID(ce.conflicts[x]);
				if (ce2 != null && !ce2.equals(ce))
					conf.add(ce2);
			}
			ce.conflict = conf;
			ce.conflicts = null;
		}
		bubleSort();
		
	}

	public static void bubleSort(){
		for (int x = 0; x < all.size(); x++)
			for (int y = 0; y < all.size()-1; y++)
				if (all.get(y).getRarity() > all.get(y+1).getRarity()){
					CustomEnchantment cc = all.get(y);
					all.set(y, all.get(y+1));
					all.set(y+1, cc);
				}
	}
	public static List<CustomEnchantment> getEnchantments(){
		return all;
	}

	public static Map<CustomEnchantment, Integer> getCustomEnchants(ItemStack item){
		Map<CustomEnchantment, Integer> list = new HashMap<CustomEnchantment, Integer>();
		List<String> lore = new ArrayList<String>();
		if (item != null && item.getType() != Material.AIR && item.getItemMeta().getLore() != null)
			lore = item.getItemMeta().getLore();
		for (int x = 0; x < lore.size(); x++){
			CustomEnchantment ce = CustomEnchantment.getByName(EnchantManager.getCorrectName(lore.get(x)));
			if (ce != null){
				Integer i = Integer.valueOf(EnchantManager.getStringLevel(lore.get(x)));
				list.put(ce, i);
			}
		}
		return list;
	}
	

}
