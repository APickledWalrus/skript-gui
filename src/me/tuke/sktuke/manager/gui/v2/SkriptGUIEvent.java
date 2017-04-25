package me.tuke.sktuke.manager.gui.v2;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptEventHandler;
import ch.njol.skript.lang.*;
import me.tuke.sktuke.TuSKe;
import me.tuke.sktuke.util.InventoryUtils;
import me.tuke.sktuke.util.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Tuke_Nuke on 15/03/2017
 */
public class SkriptGUIEvent extends SkriptEvent {

	private static final Map<Class, List<Trigger>> triggers = ReflectionUtils.getField(SkriptEventHandler.class, null, "triggers");
	private static boolean firstInstance = true;
	private Trigger t;
	private GUIInventory gui;
	public SkriptGUIEvent(GUIInventory gui){
		this.gui = gui;
		// This is a safe Trigger. Even using null values, it won't cause any issue.
		// It will be used to load as "SkriptListener" instead of Bukkit one,
		// So, when cancelling this event, it will still calling all scripts events too.
		// It will basically be like parsing this:
		// on inventory click:
		//     #TuSKe check here if it is a proper GUI.
		//     stop
		t = new Trigger(null, "gui inventory click", this, new ArrayList<>());
		//Those will be added before all triggers to cancel it before them.
		addTrigger(t, 0 , InventoryClickEvent.class, InventoryDragEvent.class);
		//It will add for the last one
		addTrigger(t, 1 , InventoryCloseEvent.class);

		//It register the bukkit listener for the class event in case it isn't yet.
		if (firstInstance) {
			ReflectionUtils.invokeMethod(SkriptEventHandler.class, "registerBukkitEvents", null);
			firstInstance = false;
		}
	}
	@Override
	public boolean init(Literal<?>[] literals, int i, SkriptParser.ParseResult parseResult) {
		return true;
	}
	@Override
	public boolean check(Event event) {
		if (event instanceof InventoryClickEvent && !((InventoryClickEvent) event).isCancelled()) {
			InventoryClickEvent e = (InventoryClickEvent) event;
			if (isAllowedType(e.getClick())){
				Inventory click = InventoryUtils.getClickedInventory(e);
				if (click != null) {
					Inventory op = InventoryUtils.getOpositiveInventory(e.getView(), click);
					if (op == null || !click.equals(gui.getInventory()) && !op.equals(gui.getInventory()))
						return false;
					Integer slot = e.getSlot();
					switch (e.getAction()) {
						case MOVE_TO_OTHER_INVENTORY:
							if (gui.getInventory().equals(op)) {
								click = op;
								slot = InventoryUtils.getSlotTo(op, e.getCurrentItem());
							}
							break;
						case COLLECT_TO_CURSOR:
							click = gui.getInventory();
							slot = InventoryUtils.getSlotTo(click, e.getCursor());
							break;
						case HOTBAR_SWAP:
						case HOTBAR_MOVE_AND_READD:
							if (gui.getInventory().getType().equals(InventoryType.PLAYER)) {
								slot = e.getHotbarButton();
								click = gui.getInventory();
							}
							break;

					}
					if (click.equals(gui.getInventory())) {
						Consumer<InventoryClickEvent> run = gui.getSlot(slot);
						if (run != null && slot == e.getSlot() && click.equals(InventoryUtils.getClickedInventory(e)))
							run.accept(e);
						e.setCancelled(run != null || gui.isSlotsLocked());
					}
				}
			}
		} else if (event instanceof InventoryCloseEvent) {
			InventoryCloseEvent e = (InventoryCloseEvent) event;
			if (e.getInventory().equals(gui.getInventory())){
				if (gui.hasOnClose()){
					try {
						gui.getOnClose().accept(e);
					} catch (Exception ex){
						if (TuSKe.debug())
							Skript.exception(ex, "A error occurred while closing a Gui");
					}
				}
				if (e.getViewers().size() == 1) {//Only clear when the last one close.
					Bukkit.getScheduler().runTask(TuSKe.getInstance(), () -> unregister());
				}

				//	gui.clear();
			}

		} else if (event instanceof InventoryDragEvent) {
			//event.getRawSlot() < event.getView().getTopInventory().getSize()
			if (((InventoryDragEvent) event).getInventory().equals(gui.getInventory()))
				for (Integer slot : ((InventoryDragEvent) event).getRawSlots())
					if (slot < ((InventoryDragEvent) event).getInventory().getSize()) {
						slot = ((InventoryDragEvent) event).getView().convertSlot(slot);
						if (gui.getSlot(slot) != null) {
							((InventoryDragEvent) event).setCancelled(true);
							break;
						}
					}
		}
		return false; // It needs to be false to not call Trigger#execute(e) and continue to the next trigger.
	}

	@Override
	public String toString(Event event, boolean b) {
		return event != null ? "gui event: " + event.getEventName() : "gui event";
	}
	private boolean isAllowedType(ClickType ct){
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

	/**
	 * Removes all current open GUIInventory
	 */
	public static void unregisterAll(){
		Map<Class<? extends Event>, List<Trigger>> triggers = ReflectionUtils.getField(SkriptEventHandler.class, null, "triggers");
		//Using 'InventoryDragEvent' since it is less used and the amount of its triggers are low.
		if (triggers.containsKey(InventoryDragEvent.class)) {
			List<Trigger> list =  triggers.get(InventoryDragEvent.class);
			for (Trigger t : list)
				if (t.getEvent() instanceof SkriptGUIEvent) {
					((SkriptGUIEvent)t.getEvent()).gui.clear();
			}
		}

	}
	/**
	 * It will remove this current event from all triggers.
	 */
	public void unregister(){
		//Using some reflections to access all triggers.
		Map<Class<? extends Event>, List<Trigger>> triggers = ReflectionUtils.getField(SkriptEventHandler.class, null, "triggers");
		for (Class<?> clz : new Class[]{InventoryClickEvent.class, InventoryCloseEvent.class, InventoryDragEvent.class}) {
			if (triggers.containsKey(clz)) {
				List<Trigger> list =  triggers.get(clz);
				if (list != null && list.contains(t))
					list.remove(t);
				if (list.isEmpty())
					triggers.remove(clz);
			}
		}
		gui.setListener(null);
	}
	private void addTrigger(Trigger t, int priority, Class<? extends Event>... clzz) {
		if (priority == 0) {
			for (Class clz : clzz) {
				List<Trigger> current = triggers.get(clz);
				List<Trigger> newList = new ArrayList<>();
				if (current == null) {
					//It will add a new array in case it doesn't have the event.
					newList.add(t);
					triggers.put(clz, newList);
				} else {
					//It will put this trigger at first index
					//Then adding the rest all again.
					//This little workaround needed just to not
					//have conflicts between different objects.
					newList.addAll(current);
					current.clear();
					current.add(t);
					current.addAll(newList);
				}
			}
		} else {
			Method m = ReflectionUtils.getMethod(SkriptEventHandler.class, "addTrigger", clzz.getClass(), Trigger.class);
			ReflectionUtils.invokeMethod(m, null, clzz, t);
		}
	}
}
