package me.tuke.sktuke.hooks.simpleclans;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;

public class EvtPromotePlayerClan extends SkriptEvent{

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "clan promote player";
	}

	@Override
	public boolean check(Event e) {
		return true;
	}

	@Override
	public boolean init(Literal<?>[] arg0, int arg1, ParseResult arg2) {
		return true;
	}

}
