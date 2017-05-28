package me.tuke.sktuke.manager.gui.v2;

import ch.njol.skript.SkriptEventHandler;
import ch.njol.skript.lang.*;
import me.tuke.sktuke.listeners.GUIListener;
import me.tuke.sktuke.util.ReflectionUtils;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * @author Tuke_Nuke on 15/03/2017
 */
public class SkriptGUIEvent extends SkriptEvent {

	private static SkriptGUIEvent instance;
	public static SkriptGUIEvent getInstance() {
		if (instance == null)
			instance = new SkriptGUIEvent();
		return instance;
	}

	private final Map<Class, List<Trigger>> triggers = ReflectionUtils.getField(SkriptEventHandler.class, null, "triggers");
	private final List<GUIListener> listeners = new ArrayList<>();
	private SkriptGUIEvent() {
		// This is a safe Trigger. Even using null values, it won't cause any issue.
		// It will be used to load as "SkriptListener" instead of Bukkit one,
		// So, when cancelling this event, it will still calling all scripts events too.
		// It will basically be like parsing this:
		// on inventory click:
		//     #TuSKe check here if it is a proper GUI.
		//     stop
		Trigger t = new Trigger(null, "gui inventory click", this, new ArrayList<>());
		//Those will be added before all triggers to cancel it before them.
		addTrigger(t, 0 , InventoryClickEvent.class, InventoryDragEvent.class);
		//It will add for the last one
		addTrigger(t, 1 , InventoryCloseEvent.class);
		ReflectionUtils.invokeMethod(SkriptEventHandler.class, "registerBukkitEvents", null);
	}
	@Override
	public boolean check(Event event) {
		List<GUIListener> current = new ArrayList<>(listeners);
		current.forEach(gui -> gui.onEvent(event));
		return false; // It needs to be false to not call Trigger#execute(e).
	}
	public void register(GUIListener gui) {
		listeners.add(gui);
	}
	public void unregister(GUIListener gui) {
		listeners.remove(gui);
	}
	/**
	 * Removes all current open GUIInventory
	 */
	public void unregisterAll(){
		listeners.forEach(GUIListener::finalize);
		listeners.clear();
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
	@Override
	public boolean init(Literal<?>[] literals, int i, SkriptParser.ParseResult parseResult) {
		return true;
	}
	@Override
	public String toString(Event event, boolean b) {
		return event != null ? "gui event: " + event.getEventName() : "gui event";
	}
}
