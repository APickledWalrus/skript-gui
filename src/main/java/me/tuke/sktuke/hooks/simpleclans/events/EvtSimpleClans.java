package me.tuke.sktuke.hooks.simpleclans.events;

import me.tuke.sktuke.util.Registry;
import net.sacredlabyrinth.phaed.simpleclans.events.*;
import org.bukkit.event.Event;
import javax.annotation.Nullable;

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;

public class EvtSimpleClans extends SkriptEvent{
	static {
		Registry.newEvent(EvtSimpleClans.class, CreateClanEvent.class, "Clan create", "clan create");
		Registry.newEvent(EvtSimpleClans.class, DisbandClanEvent.class, "Clan disband", "clan disband" );
		Registry.newEvent(EvtSimpleClans.class, AllyClanAddEvent.class, "Ally clan add", "ally clan add" );
		Registry.newEvent(EvtSimpleClans.class, AllyClanRemoveEvent.class, "Ally clan remove", "ally clan remove");
		Registry.newEvent(EvtSimpleClans.class, RivalClanAddEvent.class, "Rival clan add", "rival clan add");
		Registry.newEvent(EvtSimpleClans.class, RivalClanRemoveEvent.class, "Rival clan remove",  "rival clan remove");
		Registry.newEvent(EvtSimpleClans.class, PlayerPromoteEvent.class, "Clan promote player", "[clan] promote player");
		Registry.newEvent(EvtSimpleClans.class, PlayerDemoteEvent.class, "Clan demote player", "[clan] demote player");
		Registry.newEvent(EvtSimpleClans.class, PlayerJoinedClanEvent.class, "Clan join player", "player join");
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "clan event";
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
