package com.github.tukenuke.tuske.events;

import javax.annotation.Nullable;

import ch.njol.skript.SkriptEventHandler;
import ch.njol.skript.lang.SelfRegisteringSkriptEvent;
import ch.njol.skript.lang.Trigger;
import com.github.tukenuke.tuske.TuSKe;
import com.github.tukenuke.tuske.events.customevent.AnvilCombineEvent;
import com.github.tukenuke.tuske.events.customevent.AnvilRenameEvent;
import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class EvtAnvil extends SelfRegisteringSkriptEvent{
	static {
		Registry.newEvent(EvtAnvil.class, AnvilRenameEvent.class, "Anvil Rename", "anvil [item] rename");
		Registry.newEvent(EvtAnvil.class, AnvilCombineEvent.class, "Anvil Combine", "anvil [item] (combine|merge)");
	}
	private static List<Trigger> rename = new ArrayList<>();
	private static List<Trigger> merge = new ArrayList<>();
	private static Listener listener = new Listener(){
		@EventHandler
		public void onEvent(InventoryClickEvent e) {
			if (e.getInventory().getType().equals(InventoryType.ANVIL) && e.getRawSlot() == 2 && !e.isCancelled()){
				if (!(e.getAction().equals(InventoryAction.NOTHING) || e.getAction().equals(InventoryAction.UNKNOWN) || e.getAction().equals(InventoryAction.PLACE_ALL) || e.getAction().equals(InventoryAction.PLACE_ONE) || e.getAction().equals(InventoryAction.PLACE_SOME))){
					Player p = (Player) e.getWhoClicked();
					Inventory inv = e.getInventory();
					String name1 = e.getInventory().getItem(0).getItemMeta().getDisplayName();
					String name2 = e.getInventory().getItem(2).getItemMeta().getDisplayName();
					if (name1 != name2){
						AnvilRenameEvent ap = new AnvilRenameEvent(p, inv);
						SkriptEventHandler.logEventStart(ap);
						for (Trigger t : rename) {
							SkriptEventHandler.logTriggerStart(t);
							t.execute(ap);
							SkriptEventHandler.logTriggerEnd(t);
						}
						if (ap.isCancelled())
							e.setCancelled(true);
						SkriptEventHandler.logEventEnd();
					}
					if (inv.getItem(0) != null && inv.getItem(1) != null){
						AnvilCombineEvent ac = new AnvilCombineEvent(p, inv);
						SkriptEventHandler.logEventStart(ac);
						for (Trigger t : merge) {
							SkriptEventHandler.logTriggerStart(t);
							t.execute(ac);
							SkriptEventHandler.logTriggerEnd(t);
						}
						if (ac.isCancelled())
							e.setCancelled(true);
						SkriptEventHandler.logEventEnd();
					}

				}
			}
		}
	};
	private int type = 0;
	@Override
	public String toString(@Nullable Event e, boolean b) {
		return "anvil event";
	}

	@Override
	public void register(Trigger trigger) {
		if (type == 0)
			rename.add(trigger);
		else
			merge.add(trigger);
		if (rename.size() + merge.size() == 1)
			Bukkit.getPluginManager().registerEvents(listener, TuSKe.getInstance());
	}

	@Override
	public void unregister(Trigger trigger) {
		rename.remove(trigger);
		merge.remove(trigger);
		if (rename.size() + merge.size() == 0)
			unregisterAll();
	}

	@Override
	public void unregisterAll() {
		rename.clear();
		merge.clear();
		HandlerList.unregisterAll(listener);
	}

	@Override
	public boolean init(Literal<?>[] arg, int arg1, ParseResult arg2) {
		if (!arg2.expr.toLowerCase().contains("rename"))
			type = 1;
		return true;
	}

}
