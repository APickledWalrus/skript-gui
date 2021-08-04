package io.github.apickledwalrus.skriptgui.gui;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptEventHandler;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.util.NonNullPair;
import com.destroystokyo.paper.event.player.PlayerRecipeBookClickEvent;
import io.github.apickledwalrus.skriptgui.util.ReflectionUtils;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.eclipse.jdt.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SkriptGUIEvent extends SkriptEvent {

	public static final boolean HAS_RECIPE_EVENT = Skript.classExists("com.destroystokyo.paper.event.player.PlayerRecipeBookClickEvent");

	@Nullable
	private static SkriptGUIEvent instance;
	private final List<NonNullPair<Class<? extends Event>, Trigger>> triggers = ReflectionUtils.getField(SkriptEventHandler.class, null, "triggers");

	private final List<GUIEventHandler> listeners = new ArrayList<>();
	private boolean registered = false;

	private SkriptGUIEvent() {
		register();
	}

	public static SkriptGUIEvent getInstance() {
		if (instance == null)
			instance = new SkriptGUIEvent();
		return instance;
	}

	@Override
	public boolean init(Literal<?>[] literals, int i, ParseResult parseResult) {
		return true;
	}

	@Override
	public boolean check(Event event) {
		List<GUIEventHandler> current = new ArrayList<>(listeners);
		for (GUIEventHandler gui : current) {
			gui.onEvent(event);
			if (event instanceof Cancellable && ((Cancellable) event).isCancelled())
				break; // A matching GUI was already found, so let's stop it.
		}
		return false; // It needs to be false to not call Trigger#execute(e).
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return e != null ? "gui event: " + e.getEventName() : "gui event";
	}

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
			Trigger t = new Trigger(null, "gui inventory event", this, new ArrayList<>());
			ReflectionUtils.setField(SkriptEvent.class, this, "eventPriority", EventPriority.LOWEST); // Try to make sure these are called first

			// Add this trigger to the beginning of the triggers list for each event so that it can be cancelled first
			// Also add these events these events to the listenCancelled list so that they will still trigger for other listeners
			if (HAS_RECIPE_EVENT) {
				triggers.add(0, new NonNullPair<>(PlayerRecipeBookClickEvent.class, t));
				SkriptEventHandler.listenCancelled.add(PlayerRecipeBookClickEvent.class);
			}
			triggers.add(0, new NonNullPair<>(InventoryOpenEvent.class, t));
			SkriptEventHandler.listenCancelled.add(InventoryOpenEvent.class);
			triggers.add(0, new NonNullPair<>(InventoryCloseEvent.class, t));
			SkriptEventHandler.listenCancelled.add(InventoryCloseEvent.class);
			triggers.add(0, new NonNullPair<>(InventoryDragEvent.class, t));
			SkriptEventHandler.listenCancelled.add(InventoryDragEvent.class);
			triggers.add(0, new NonNullPair<>(InventoryClickEvent.class, t));
			SkriptEventHandler.listenCancelled.add(InventoryClickEvent.class);

			// Register these events with Skript
			ReflectionUtils.invokeMethod(SkriptEventHandler.class, "registerBukkitEvents", null);
		}
	}

	public void register(GUIEventHandler gui) {
		// Just in case it didn't enable the listener before opening a GUI
		register();
		listeners.add(gui);
	}

	public void unregister(GUIEventHandler gui) {
		listeners.remove(gui);
	}

	/**
	 * Removes all currently open {@link GUI}s.
	 */
	public void unregisterAll(){
		if (listeners.size() != 0) { // https://github.com/APickledWalrus/skript-gui/issues/23
			listeners.forEach(GUIEventHandler::finalize);
			listeners.clear();
		}
		// When running '/skript reload all', it removes this object from Skript's event listener
		registered = false;
	}

}
