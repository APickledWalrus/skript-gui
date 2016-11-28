package me.tuke.sktuke.events;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;

public class EvtAnvilCombine  extends SkriptEvent{

	@Override
	public String toString(@Nullable Event e, boolean b) {
		return "anvil prepare";
	}

	@Override
	public boolean check(Event e) {
		return true;
	}

	@Override
	public boolean init(Literal<?>[] arg, int arg1, ParseResult arg2) {
		return true;
	}

}
