package io.github.apickledwalrus.skriptgui.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.njol.skript.Skript;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import ch.njol.skript.SkriptEventHandler;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;

import io.github.apickledwalrus.skriptgui.util.ReflectionUtils;

public class SkriptGUIEvent extends SkriptEvent {

	private static SkriptGUIEvent instance;
	public static SkriptGUIEvent getInstance() {
		if (instance == null)
			instance = new SkriptGUIEvent();
		return instance;
	}

	private final Map<Class<?>, List<Trigger>> triggers = ReflectionUtils.getField(SkriptEventHandler.class, null, "triggers");
	private final List<GUIListener> listeners = new ArrayList<>();
	private boolean registered = false;

	private SkriptGUIEvent() {
		register();
	}

	@Override
	public boolean check(Event event) {
		List<GUIListener> current = new ArrayList<>(listeners);
		for (GUIListener gui : current) {
			gui.onEvent(event);
			if (event instanceof Cancellable && ((Cancellable) event).isCancelled())
				break; // A matching GUI was already found, so let's stop it.
		}
		return false; // It needs to be false to not call Trigger#execute(e).
	}

	@SuppressWarnings("unchecked")
	public void register() {
		if (!registered) {

			// Make sure it only execute this once when necessary.
			registered = true;

			// A listener to know when Skript will remove all listeners.
			new TriggerUnregisterListener().register();

			/*
			 *  This is a safe Trigger, so using null values won't cause any issues
			 *  It will be used to load as "SkriptListener" instead of the Bukkit one,
			 *  so, that, when cancelling this event, it will still call all script events too.
			 *  It parses like this:
			 *  on inventory click:
			 *    # Check here if it's a proper GUI.
			 *    stop
			 */
			Trigger t = new Trigger(null, "gui inventory click", this, new ArrayList<>());

			// These will be added before all triggers to cancel it before them.
			addTrigger(t, 0 , InventoryClickEvent.class, InventoryDragEvent.class);

			// It will add for the last one
			addTrigger(t, 1 , InventoryCloseEvent.class, InventoryOpenEvent.class);

			ReflectionUtils.invokeMethod(SkriptEventHandler.class, "registerBukkitEvents", null);
		}
	}

	public void register(GUIListener gui) {
		// Just in case it didn't enable the listener before opening a GUI.
		register();
		listeners.add(gui);
	}

	public void unregister(GUIListener gui) {
		listeners.remove(gui);
	}

	/**
	 * Removes all currently open {@link GUI}s.
	 */
	public void unregisterAll(){
		listeners.forEach(GUIListener::finalize);
		listeners.clear();
		// When running '/skript reload all', it removes this object from Skript's event listener.
		registered = false;
	}

	@SuppressWarnings("unchecked")
	private void addTrigger(Trigger t, int priority, Class<? extends Event>... classes) {
		if (priority == 0) {
			for (Class<?> clazz : classes) {
				List<Trigger> current = null;
				try {
					current = triggers.get(clazz);
				} catch (NullPointerException ex) {
					Skript.exception(ex, "An error occured while trying to add triggers. If you are unsure why this occured, please report the error on the skript-gui GitHub.");
				}
				List<Trigger> newList = new ArrayList<>();
				if (current == null) {
					// It will add a new array in case it doesn't have the event.
					newList.add(t);
					triggers.put(clazz, newList);
				} else {
					/*
					 * This trigger will be put at the first index
					 * Then, the rest will be added again.
					 * This workaround is needed to not have conflicts between different objects.
					 */
					newList.addAll(current);
					current.clear();
					current.add(t);
					current.addAll(newList);
				}
			}
		} else {
			SkriptEventHandler.addTrigger(classes, t);
		}
	}

	@Override
	public boolean init(Literal<?>[] literals, int i, ParseResult parseResult) {
		return true;
	}

	@Override
	public String toString(Event event, boolean b) {
		return event != null ? "gui event: " + event.getEventName() : "gui event";
	}

}
