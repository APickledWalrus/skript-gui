package me.tuke.sktuke.events;

import ch.njol.skript.SkriptEventHandler;
import ch.njol.skript.lang.SelfRegisteringSkriptEvent;
import ch.njol.skript.lang.Trigger;
import me.tuke.sktuke.TuSKe;
import me.tuke.sktuke.events.customevent.InventoryMoveEvent;
import me.tuke.sktuke.util.Registry;
import me.tuke.sktuke.util.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import javax.annotation.Nullable;

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

//TODO Improve the listener
public class EvtInventoryMove extends SelfRegisteringSkriptEvent{
	static {
		Registry.newEvent(EvtInventoryMove.class, InventoryMoveEvent.class, "Inventory move", "inventory move");
	}

	private static List<Trigger> triggers = new ArrayList<>();
	private static Listener listener = new Listener() {
		@EventHandler
		public void onEvent(InventoryClickEvent e){
			if (!e.isCancelled() && (e.getAction().equals(InventoryAction.HOTBAR_SWAP) || e.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) || e.getAction().equals(InventoryAction.HOTBAR_MOVE_AND_READD)) && (e.getClick().equals(ClickType.SHIFT_LEFT) || e.getClick().equals(ClickType.SHIFT_RIGHT) || e.getClick().equals(ClickType.NUMBER_KEY)) ){
				if (!(e.getInventory().getType().equals(InventoryType.CHEST) || e.getInventory().getType().equals(InventoryType.DISPENSER) || e.getInventory().getType().equals(InventoryType.DISPENSER) || e.getInventory().getType().equals(InventoryType.HOPPER) || e.getInventory().getType().equals(InventoryType.ENDER_CHEST)))
					return;
				Inventory invTo;
				Inventory invFrom;
				Inventory click = InventoryUtils.getClickedInventory(e);
				int slotTo;
				int slotFrom;
				ItemStack i = e.getCurrentItem();;
				if (e.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)){
					invTo = (e.getWhoClicked().getInventory().equals(click) ? e.getInventory() : e.getWhoClicked().getInventory());
					invFrom = click;
					slotTo = (invTo.equals(e.getInventory())) ? InventoryUtils.getSlotTo(invTo, e.getCurrentItem()) : InventoryUtils.getInvertedSlotTo(invTo, e.getCurrentItem());
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
				SkriptEventHandler.logEventStart(im);
				for (Trigger t : triggers) {
					SkriptEventHandler.logTriggerStart(t);
					t.execute(im);
					SkriptEventHandler.logTriggerEnd(t);
				}
				e.setCancelled(im.isCancelled());
				SkriptEventHandler.logEventEnd();
			}
		}
	};

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "inventory move event";
	}

	@Override
	public void register(Trigger trigger) {
		triggers.add(trigger);
		if (triggers.size() == 0) {
			Bukkit.getPluginManager().registerEvents(listener, TuSKe.getInstance());
		}
	}

	@Override
	public void unregister(Trigger trigger) {
		triggers.remove(trigger);
		if (triggers.size() == 0)
			unregisterAll();
	}

	@Override
	public void unregisterAll() {
		triggers.clear();
		HandlerList.unregisterAll(listener);
	}

	@Override
	public boolean init(Literal<?>[] arg0, int arg1, ParseResult arg2) {
		return true;
	}

}
