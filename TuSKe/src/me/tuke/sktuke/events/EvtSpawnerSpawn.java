package me.tuke.sktuke.events;

import org.bukkit.event.Event;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;

public class EvtSpawnerSpawn extends SkriptEvent{

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "spawner spawn";
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
