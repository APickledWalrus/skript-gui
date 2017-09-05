package com.github.tukenuke.tuske.manager.gui.v2;

import ch.njol.skript.SkriptEventHandler;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SelfRegisteringSkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Trigger;
import com.github.tukenuke.tuske.TuSKe;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;

import java.io.File;
import java.util.ArrayList;

/**
 * This is called only when someone uses /sk reload all, so it will register {@link SkriptGUIEvent} again.
 * @author Tuke_Nuke on 03/09/2017
 */
public class TriggerUnregisterListener extends SelfRegisteringSkriptEvent {

	private Trigger t;

	public TriggerUnregisterListener() {
	}

	public void register() {
		if (t == null) //A file object, just to not use null
			t = new Trigger(new File("TuSKe"), getClass().getName(), this, new ArrayList<>());
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
	public boolean init(Literal<?>[] args, int matchedPattern, SkriptParser.ParseResult parseResult) {
		return true;
	}

	@Override
	public String toString(Event e, boolean debug) {
		return getClass().getName();
	}
}
