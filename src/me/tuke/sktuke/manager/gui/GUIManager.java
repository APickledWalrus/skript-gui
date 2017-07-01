package me.tuke.sktuke.manager.gui;

import java.util.HashMap;
import java.util.Map;

import me.tuke.sktuke.listeners.GUIListener;
import me.tuke.sktuke.listeners.InventoryCheck;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ch.njol.skript.Skript;
import me.tuke.sktuke.TuSKe;

public class GUIManager {

	private TuSKe tuske;
	public GUIManager(TuSKe tuske) {
		this.tuske = tuske;
	}
	private HashMap<Inventory, HashMap<Integer, GUI[]>> invs = new HashMap<>();
	
	public boolean isGUI(Inventory inv, int slot){
		return invs.containsKey(inv) && invs.get(inv).containsKey(slot);
	}
	public boolean hasGUI(Inventory inv){
		return invs.containsKey(inv);
	}
	public GUI getGUI(Inventory inv, int slot, ClickType ct){
		return isGUI(inv, slot) ? getGUI(invs.get(inv).get(slot), ct) : null;
	}
	public void newGUI(Inventory inv, int slot, ItemStack item, GUI gui){
		addToListener(inv, slot, item, gui);
	}	
	private GUI getGUI(GUI[] guis, ClickType ct){
		int index = getIndex(ct);
		return guis[index] != null? guis[index] : guis[0];
	}
	private void addToListener(Inventory inv, int slot, ItemStack item, GUI gui){
		GUI[] guis2 = null;
		HashMap<Integer,GUI[]> guislot2 = new HashMap<>();
		boolean firstSlot = invs.containsKey(inv);
		if (firstSlot && (guislot2 = invs.get(inv)).containsKey(slot)){
			guislot2.get(slot)[getIndex(gui.getClickType())] = gui;
		} else {
			if (!firstSlot)
				registerListener(inv);
			guis2 = new GUI[ClickType.values().length - 2];
			guis2[getIndex(gui.getClickType())] = gui;
			guislot2.put(slot, guis2);
		}
		invs.put(inv, guislot2);
		inv.setItem(slot, item);
	}
	public void remove(Inventory inv, int slot){
		inv.setItem(slot, new ItemStack(Material.AIR));
		HashMap<Integer, GUI[]> map = invs.get(inv);
		map.remove(slot);
		if (map.size() > 0)
			invs.put(inv, map);
		else
			invs.remove(inv);
	}
	public void removeAll(Inventory inv){
		Map<Integer, GUI[]> map = invs.get(inv);
		if (map != null) {
			for (int slot : map.keySet())
				inv.setItem(slot, new ItemStack(Material.AIR));
			invs.remove(inv);
		}
		
	}
	public void clearAll(){
		for (Inventory inv : invs.keySet())
			for (Integer slot : invs.get(inv).keySet())
				inv.setItem(slot, new ItemStack(Material.AIR));
		invs.clear();
	}
	public boolean isAllowedType(ClickType ct){
		if (ct != null)
			switch(ct){
			case UNKNOWN:
			case WINDOW_BORDER_RIGHT:
			case WINDOW_BORDER_LEFT:
			case CREATIVE:
				return false;
			default:
				break;
		}
		return true;
	}
	public void runCommand(final CommandSender sender, String cmd, String perm){
		if (sender != null && cmd != null){
			if (sender instanceof Player && perm != null && !sender.isOp()){
				sender.addAttachment(TuSKe.getInstance(), perm, true, 0);
			}
			if (cmd.startsWith("/"))
				cmd = cmd.substring(1);
			Skript.dispatchCommand(sender, cmd);
		}
	}
	private int getIndex(ClickType ct){
		if (ct == null)
			return 0;
		int index = ct.ordinal() + 1;
		if (index > 6)
			index -=2;
		return index;
	}

	private void registerListener(Inventory inv) {
		new GUIListener(inv) {
			@Override
			public void onClick(InventoryClickEvent e, int slot) {
				if (isGUI(inv, slot)){
					e.setCancelled(true);
					final GUI gui = getGUI(inv, e.getSlot(), e.getClick());
					if (gui != null && e.getInventory().getItem(e.getSlot()) != null && gui.runOnlyWith(e.getCursor())){
						if (gui.toCallEvent()){
							GUIActionEvent guie = new GUIActionEvent(e);
							Bukkit.getPluginManager().callEvent(guie);
							e.setCancelled(!guie.isCancelled());
						} else if(gui.toClose())
							Bukkit.getScheduler().scheduleSyncDelayedTask(tuske, () -> {
								//gm.removeAll(click);
								if (gui.getInventory() != null)
									e.getWhoClicked().openInventory(gui.getInventory());
								else
									e.getWhoClicked().closeInventory();
								if (gui.toRun())
									gui.run(e);
							}, 0L);
						else if (gui.toRun())
							gui.run(e);
					}
				}
			}

			@Override
			public void onClose(InventoryCloseEvent e) {
				Bukkit.getScheduler().runTaskLater(tuske, () -> {
					removeAll(e.getInventory());
					((Player)e.getPlayer()).updateInventory();}, 0L);
			}

			@Override
			public void onDrag(InventoryDragEvent e, int slot) {
				if (isGUI(e.getInventory(), slot))
					e.setCancelled(true);
			}
		}.start();
	}
}
