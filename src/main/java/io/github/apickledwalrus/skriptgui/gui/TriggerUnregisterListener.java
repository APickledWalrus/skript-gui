package io.github.apickledwalrus.skriptgui.gui;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.event.Event;

import ch.njol.skript.SkriptEventHandler;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SelfRegisteringSkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;

/**
 * This is called only when someone uses /sk reload all, so it will register {@link SkriptGUIEvent} again.
 */
public class TriggerUnregisterListener extends SelfRegisteringSkriptEvent {

	private Trigger t;

	public TriggerUnregisterListener() {
	}

	public void register() {

		if (t == null) // A file object, just to not use null
			t = new Trigger(new File("SkriptGUI"), getClass().getName(), this, new ArrayList<>());

		SkriptEventHandler.addSelfRegisteringTrigger(t);
	}

	@Override
	public void register(Trigger t) {

	}

	@Override
	public void unregister(Trigger t) {

	}

	@Override
	public void unregisterAll() {
		SkriptGUIEvent.getInstance().unregisterAll();
	}

	@Override
	public boolean init(Literal<?>[] args, int matchedPattern, ParseResult parseResult) {
		return true;
	}

	@Override
	public String toString(Event e, boolean debug) {
		return getClass().getName();
	}
}
