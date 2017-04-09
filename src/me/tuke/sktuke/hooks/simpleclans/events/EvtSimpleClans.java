package me.tuke.sktuke.hooks.simpleclans.events;

import me.tuke.sktuke.util.NewRegister;
import net.sacredlabyrinth.phaed.simpleclans.events.*;
import org.bukkit.event.Event;
import javax.annotation.Nullable;

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;

public class EvtSimpleClans extends SkriptEvent{
	static {
		NewRegister.newEvent(EvtSimpleClans.class, CreateClanEvent.class, "Clan create", "clan create");
		NewRegister.newEvent(EvtSimpleClans.class, DisbandClanEvent.class, 1, "Clan disband", "clan disband" );
		NewRegister.newEvent(EvtSimpleClans.class, AllyClanAddEvent.class, 1, "Ally clan add", "ally clan add" );
		NewRegister.newEvent(EvtSimpleClans.class, AllyClanRemoveEvent.class, 1, "Ally clan remove", "ally clan remove");
		NewRegister.newEvent(EvtSimpleClans.class, RivalClanAddEvent.class, 1,"Rival clan add", "rival clan add");
		NewRegister.newEvent(EvtSimpleClans.class, RivalClanRemoveEvent.class, 1,"Rival clan remove",  "rival clan remove");
		NewRegister.newEvent(EvtSimpleClans.class, PlayerPromoteEvent.class, 1, "Clan promote player", "[clan] promote player");
		NewRegister.newEvent(EvtSimpleClans.class, PlayerDemoteEvent.class, 1, "Clan demote player", "[clan] demote player");
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
