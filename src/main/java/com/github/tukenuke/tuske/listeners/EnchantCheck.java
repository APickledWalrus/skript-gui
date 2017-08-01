package com.github.tukenuke.tuske.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import com.github.tukenuke.tuske.TuSKe;
import com.github.tukenuke.tuske.manager.customenchantment.EnchantManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.github.tukenuke.tuske.manager.customenchantment.CustomEnchantment;
import com.github.tukenuke.tuske.manager.customenchantment.EnchantConfig;

public class EnchantCheck implements Listener{
	
	private TuSKe instance;
	public EnchantCheck(TuSKe instance){
		this.instance = instance;
	}
	@EventHandler
	public void AnvilPrepare(final InventoryClickEvent e) {
		InventoryAction action = e.getAction();
		if (CustomEnchantment.getEnchantments().size() > 0 && !e.isCancelled() && e.getInventory().getType() == InventoryType.ANVIL && (action == InventoryAction.MOVE_TO_OTHER_INVENTORY || action == InventoryAction.PLACE_ALL || action == InventoryAction.PLACE_ONE || action == InventoryAction.PLACE_SOME || action == InventoryAction.SWAP_WITH_CURSOR || action == InventoryAction.HOTBAR_SWAP)){
			
			final Inventory inv = e.getInventory();
			if ((action == InventoryAction.MOVE_TO_OTHER_INVENTORY || action == InventoryAction.HOTBAR_SWAP) && e.getClickedInventory().getType() == InventoryType.ANVIL)
				return;
			int slot = e.getRawSlot();
			ClickType clicktype = e.getClick();
			if (action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)){
				if (inv.getItem(0) != null)
					slot = 1;
				else if (inv.getItem(1) != null)
					slot = 0;
				else
					slot = 2;
			}
			if ((inv.getItem(0) != null || inv.getItem(1) != null) && slot <= 1 && (clicktype.equals(ClickType.LEFT) || clicktype.equals(ClickType.RIGHT) || clicktype.equals(ClickType.NUMBER_KEY) || clicktype.equals(ClickType.SHIFT_LEFT) || clicktype.equals(ClickType.SHIFT_RIGHT))){
				final long L = ((EnchantConfig.y.getBoolean("Config.CompatibilityMode")) ? 1L : 0L);
				final Map<CustomEnchantment, Integer> e3 = (inv.getItem(0) != null) ? CustomEnchantment.getCustomEnchants(inv.getItem(0)) : new HashMap<CustomEnchantment, Integer>();
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(instance, new Runnable(){
					@Override
					public void run() {
						if ((inv.getItem(2) == null))
							return;
						ItemStack i1 = inv.getItem(0);
						ItemStack i2 = inv.getItem(1);							
						Map<CustomEnchantment, Integer> e2 = CustomEnchantment.getCustomEnchants(i2);
						Map<CustomEnchantment, Integer> e1 = e3;
						if (e3.isEmpty())
							e1 = CustomEnchantment.getCustomEnchants(inv.getItem(0));
						Map<CustomEnchantment, Integer> add = new HashMap<CustomEnchantment, Integer>();
						for (CustomEnchantment ce1: e1.keySet()){
							for (CustomEnchantment ce2: e2.keySet()){
								if (ce2.isCompatible(i1) && add.size() <= EnchantManager.getMaxPerItem()){
									if (ce1 == ce2 && ce1.getLevel(i1) > ce2.getLevel(i2))
										add.put(ce1, ce1.getLevel(i1));
									else if (ce1 == ce2 && ce1.getLevel(i1) == ce2.getLevel(i2))
										add.put(ce1, ce1.getLevel(i1) + 1);
									else if (ce1 == ce2)
										add.put(ce1, ce2.getLevel(i2));
									if (!e1.containsKey(ce2))
										add.put (ce2, ce2.getLevel(i2));
								}
							}
							if (L == 1L && !add.containsKey(ce1))
								add.put(ce1, ce1.getLevel(i1));
						}
						if (add.size() != (e1.size() + e2.size()))
							for (CustomEnchantment ce: e2.keySet()){
								if (!add.containsKey(ce) && ce.isCompatible(i1) && add.size() <= EnchantManager.getMaxPerItem())
									add.put(ce, ce.getLevel(i2));
							}
						for (CustomEnchantment c: add.keySet()){
							EnchantManager.removeFromItem(inv.getItem(2), c);
							if (c.isEnabledOnAnvil())
								EnchantManager.addToItem(inv.getItem(2), c, add.get(c), true);
						}
					}}, L);
				

			}
		}
	}
	@EventHandler
	public void Enchant(EnchantItemEvent e){
		if (CustomEnchantment.getEnchantments().size() > 0 && !e.isCancelled()){
			Map<CustomEnchantment, Integer> add = new HashMap<CustomEnchantment, Integer>();
			int i = 2;
			if (e.getExpLevelCost() <= 7) 
				i = 3;
			else if (e.getExpLevelCost() == 30)
				i = 1;		
			for (CustomEnchantment ce : CustomEnchantment.getEnchantments()){
				//TuSKe.log(Math.random() + " >= " + minRarity(EnchantConfig.getGlobalRarity()) + " && " + add.size() + " <= " + EnchantManager.getMaxPerItem() + " && " + ce.isEnabledOnTable());
				if (Math.random() >= minRarity(EnchantConfig.getGlobalRarity()) && ce.isCompatible(e.getItem()) && add.size() <= EnchantManager.getMaxPerItem() && ce.isEnabledOnTable()){
					//TuSKe.log("Passou");
					double x = Math.random();
					double rarity = minRarity(ce.getRarity()+ 1);
					//TuSKe.log("Rarity: " + x + " >= " + rarity + " = "+ (x >= rarity) + " && " + x + " < " + maxRarity(ce.getRarity()) + " = " + ( x < maxRarity(ce.getRarity())) + " && " + ce.getMaxLevel()+ " && "+i);
					if ( x >= rarity && x < maxRarity(ce.getRarity()) && !(ce.getMaxLevel() == 1 && i > 1)){
						add.put(ce, getRandomMax(ce.getMaxLevel(), i));
					}
				}
			}
			
			for (CustomEnchantment cc: add.keySet())
				EnchantManager.addToItem(e.getInventory().getItem(0), cc, add.get(cc), false);
			
		}
		
	}

	public static double getMath(int i){
		return (i <= 0 || i > 5) ? 1D : Double.valueOf(i)/15;
	}
	public int getRandomMax(int max, int level){;
		return (max == 1) ? 1: ThreadLocalRandom.current().nextInt(1, Math.round((Float.valueOf(max)/Float.valueOf(level)) + 1));
	}
	public static double minRarity(int i){
		return (i <= 0 || i > 5) ? 0D : (i == 5) ? getMath(i) : getMath(i) + minRarity(i+1);
	}
	public static double maxRarity(int i){
		return (i <= 0) ? 0D : (i == 1) ? 1D : (i >= 5) ? getMath(i): minRarity(i+1) + getMath(i);
	}
	
}
