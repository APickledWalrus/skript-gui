package com.github.tukenuke.tuske.events;

import javax.annotation.Nullable;

import com.github.tukenuke.tuske.listeners.PlayerMovesCheck;
import org.bukkit.event.Event;

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;

public class EvtPlayerStopsMove extends SkriptEvent{

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "player stops moving";
	}

	@Override
	public boolean check(Event arg0) {
		return true;
	}

	@Override
	public boolean init(Literal<?>[] arg0, int arg1, ParseResult arg2) {
		PlayerMovesCheck.setLoaded(true);
		return true;
	}

}