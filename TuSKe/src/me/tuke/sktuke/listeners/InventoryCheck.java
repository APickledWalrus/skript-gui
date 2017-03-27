package me.tuke.sktuke.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import me.tuke.sktuke.TuSKe;
import me.tuke.sktuke.events.customevent.AnvilCombineEvent;
import me.tuke.sktuke.events.customevent.AnvilRenameEvent;
import me.tuke.sktuke.events.customevent.InventoryMoveEvent;
import me.tuke.sktuke.gui.GUI;
import me.tuke.sktuke.gui.GUIActionEvent;
import me.tuke.sktuke.gui.GUIManager;
import me.tuke.sktuke.util.ReflectionUtils;

//TODO Separate all this events into parts and only active them when is used in scripts.
public class InventoryCheck implements Listener{
	private TuSKe instance;
	private GUIManager gm = TuSKe.getGUIManager();
	public InventoryCheck(TuSKe tuske){
		instance = tuske;
	}

	@EventHandler
	public void InventoryEvent(final InventoryClickEvent e) {
		if (getClickedInventory(e) != null && gm.isAllowedType(e.getClick()) && (gm.hasGUI(getClickedInventory(e)) || gm.hasGUI(e.getInventory()))){
			final Inventory click = getClickedInventory(e);
			Inventory inv = click;
			Integer slot = e.getSlot();
			if ((e.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) && !inv.getType().equals(e.getInventory().getType())) || e.getAction().equals(InventoryAction.COLLECT_TO_CURSOR)){
				inv = e.getInventory();
				ItemStack i = (e.getAction().equals(InventoryAction.COLLECT_TO_CURSOR)) ? e.getCursor() : click.getItem(e.getSlot()) ; 
				slot = getSlotTo(inv, i);
			}
			if (gm.isGUI(inv, slot)){
				e.setCancelled(true);
				final GUI gui = gm.getGUI(click, e.getSlot(), e.getClick());
				if (gui != null && e.getInventory().getItem(e.getSlot()) != null && gui.runOnlyWith(e.getCursor())){
					if (gui.toCallEvent()){
						GUIActionEvent guie = new GUIActionEvent(e);
						Bukkit.getPluginManager().callEvent(guie);
						e.setCancelled(!guie.isCancelled());
					} else if(gui.toClose())
						Bukkit.getScheduler().scheduleSyncDelayedTask(instance, new Runnable(){
	
							@Override
							public void run() {
								//gm.removeAll(click);
								if (gui.getInventory() != null)
									e.getWhoClicked().openInventory(gui.getInventory());
								else
									e.getWhoClicked().closeInventory();
								if (gui.toRun())
									gui.getRunnable().run();
							}}, 0L);
					else if (gui.toRun())
						gui.getRunnable().run();
				}
			}
			
		}
		if (e.getInventory().getType().equals(InventoryType.ANVIL) && e.getRawSlot() == 2 && !e.isCancelled()){
			if (!(e.getAction().equals(InventoryAction.NOTHING) || e.getAction().equals(InventoryAction.UNKNOWN) || e.getAction().equals(InventoryAction.PLACE_ALL) || e.getAction().equals(InventoryAction.PLACE_ONE) || e.getAction().equals(InventoryAction.PLACE_SOME))){
				Player p = (Player) e.getWhoClicked();
				Inventory inv = e.getInventory();
				String name1 = e.getInventory().getItem(0).getItemMeta().getDisplayName();
				String name2 = e.getInventory().getItem(2).getItemMeta().getDisplayName();
				if (inv.getItem(0) != null && inv.getItem(1) != null){
					AnvilCombineEvent ac = new AnvilCombineEvent(p, inv);
					Bukkit.getServer().getPluginManager().callEvent(ac);
					if (ac.isCancelled())
						e.setCancelled(true);
				}
				if (name1 != name2){
					AnvilRenameEvent ap = new AnvilRenameEvent(p, inv);
					Bukkit.getServer().getPluginManager().callEvent(ap);
					if (ap.isCancelled())
						e.setCancelled(true);
				}
				
			}
		} else if (/*!instance.isPixelmon() &&*/ !e.isCancelled() && (e.getAction().equals(InventoryAction.HOTBAR_SWAP) || e.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) || e.getAction().equals(InventoryAction.HOTBAR_MOVE_AND_READD)) && (e.getClick().equals(ClickType.SHIFT_LEFT) || e.getClick().equals(ClickType.SHIFT_RIGHT) || e.getClick().equals(ClickType.NUMBER_KEY)) ){
			if (!(e.getInventory().getType().equals(InventoryType.CHEST) || e.getInventory().getType().equals(InventoryType.DISPENSER) || e.getInventory().getType().equals(InventoryType.DISPENSER) || e.getInventory().getType().equals(InventoryType.HOPPER) || e.getInventory().getType().equals(InventoryType.ENDER_CHEST)))
				return;
			Inventory invTo;
			Inventory invFrom;
			Inventory click = getClickedInventory(e);
			int slotTo;
			int slotFrom;
			
			ItemStack i = e.getCurrentItem();;
			if (e.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)){
				invTo = (e.getWhoClicked().getInventory().equals(click) ? e.getInventory() : e.getWhoClicked().getInventory());
				invFrom = click;
				slotTo = (invTo.equals(e.getInventory())) ? getSlotTo(invTo, e.getCurrentItem()) : getInvertedSlotTo(invTo, e.getCurrentItem());
				slotFrom = e.getSlot();
				
			} else {
				if (e.getWhoClicked().getInventory().equals(click))
					return;
				invTo = click.getItem(e.getSlot()) != null ? e.getWhoClicked().getInventory() : e.getInventory();
				invFrom = invTo.equals(click) ? e.getWhoClicked().getInventory(): e.getInventory();		
				slotTo = invTo.equals(e.getInventory()) ? e.getSlot() : e.getHotbarButton();		
				slotFrom = invTo.equals(e.getInventory()) ? e.getHotbarButton() : e.getSlot();
				i = invFrom.getItem(slotFrom);
				
				
			}
			if (slotTo < 0)
				return;
			InventoryMoveEvent im = new InventoryMoveEvent((Player)e.getWhoClicked(), i, e.getClick().toString().toLowerCase().replaceAll("_", " "), invFrom, invTo, slotFrom, slotTo);
			Bukkit.getPluginManager().callEvent(im);
			e.setCancelled(im.isCancelled());
		}
	}
	
	@EventHandler
	public void InventoryClose(final InventoryCloseEvent e){
		if (TuSKe.getGUIManager().hasGUI(e.getInventory())){
			TuSKe.getGUIManager().removeAll(e.getInventory());
			Bukkit.getScheduler().runTaskLater(instance, new Runnable(){
	
				@Override
				public void run() {
					((Player)e.getPlayer()).updateInventory();				
				}}, 0L);
		}
	}
	@EventHandler
	public void InventoryDrag(InventoryDragEvent e){
		if (e.getInventory() != null && e.getInventorySlots() != null && e.getInventorySlots().size() > 0 && e.getWhoClicked() instanceof Player)
			for (Integer slot : e.getInventorySlots())
				if (TuSKe.getGUIManager().isGUI(e.getInventory(), slot)){
					e.setCancelled(true);
					return;
				}
				
		
	}
	
	private int getSlotTo(Inventory invTo, ItemStack i){
		if (i != null && invTo.first(i.getType()) >= 0)
			for (int x = invTo.first(i.getType()); x < invTo.getSize(); x++)
				if (invTo.getItem(x) != null && invTo.getItem(x).getData().equals(i.getData()) && invTo.getItem(x).getAmount() < invTo.getItem(x).getMaxStackSize())
					return x;
				
		return invTo.firstEmpty();
		
	}
	private int getInvertedSlotTo(Inventory invTo, ItemStack i){
		for (int x = 8; x >= 0; x--)
			if ((invTo.getItem(x) == null) || (invTo.getItem(x) != null && invTo.getItem(x).getData().equals(i.getDurability()) && invTo.getItem(x).getAmount() < invTo.getItem(x).getMaxStackSize()))
				return x;
		for (int x = invTo.getSize() -1; x > 8; x--)
			if ((invTo.getItem(x) == null) || (invTo.getItem(x) != null && invTo.getItem(x).getData().equals(i.getDurability()) && invTo.getItem(x).getAmount() < invTo.getItem(x).getMaxStackSize()))
				return x;
		return -1;
			
		
	}
	@SuppressWarnings("unused")
	private boolean isAllowedTo(Inventory inv, ItemStack i, int slot){
		
		switch (inv.getType()){
		case ANVIL: return slot < 2;
		case BEACON: return i.getType().equals(Material.DIAMOND) || i.getType().equals(Material.IRON_INGOT) || i.getType().equals(Material.GOLD_INGOT) || i.getType().equals(Material.EMERALD);
		case BREWING: return i.getItemMeta() instanceof PotionMeta;
		default:
			break;
		}
		return true;
	}
	private Inventory getClickedInventory(InventoryClickEvent e){
		if (ReflectionUtils.hasMethod(InventoryClickEvent.class, "getClickedInventory"))
			return e.getClickedInventory();
		else if (e.getRawSlot() < 0)
			return null;
		else if ((e.getView().getTopInventory() != null) && (e.getRawSlot() < e.getView().getTopInventory().getSize()))
			return e.getView().getTopInventory();
		else 
			return e.getView().getBottomInventory();
	}
}
