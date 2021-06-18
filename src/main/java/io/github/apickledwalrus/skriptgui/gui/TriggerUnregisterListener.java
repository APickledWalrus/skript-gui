package io.github.apickledwalrus.skriptgui.gui;

import ch.njol.skript.SkriptEventHandler;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SelfRegisteringSkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * This is called only when someone uses /sk reload all, so it will register {@link SkriptGUIEvent} again.
 */
public class TriggerUnregisterListener extends SelfRegisteringSkriptEvent {

	private Trigger t;

	public TriggerUnregisterListener() {
	}

	public void register() {
		if (t == null)
			t = new Trigger(null, getClass().getName(), this, new ArrayList<>());
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
	@NotNull
	public String toString(@Nullable Event e, boolean debug) {
		return getClass().getName();
	}

}
