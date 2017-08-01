package com.github.tukenuke.tuske.hooks.simpleclans.expressions;

import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.event.Event;
import javax.annotation.Nullable;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.events.AllyClanAddEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.AllyClanRemoveEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.RivalClanAddEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.RivalClanRemoveEvent;

public class ExprClanTwo extends SimpleExpression<Clan>{
	static {
		Registry.newSimple(ExprClanTwo.class, 0,"[event-]clan-two");
	}
	
	private Integer e;

	@Override
	public Class<? extends Clan> getReturnType() {
		return Clan.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public boolean init(Expression<?>[] arg0, int arg1, Kleenean arg2, ParseResult arg3) {
		if (!(ScriptLoader.isCurrentEvent(AllyClanAddEvent.class) || ScriptLoader.isCurrentEvent(AllyClanRemoveEvent.class) || ScriptLoader.isCurrentEvent(RivalClanAddEvent.class) || ScriptLoader.isCurrentEvent(RivalClanRemoveEvent.class))){
			Skript.error("'event-clan-two' can only be used in 'On (Ally|Rival) clan (add|remove)' event.", ErrorQuality.SEMANTIC_ERROR);
			return false;
		}
		if (ScriptLoader.isCurrentEvent(AllyClanAddEvent.class))
			this.e = 1;
		else if (ScriptLoader.isCurrentEvent(AllyClanRemoveEvent.class))
			this.e = 2;
		else if (ScriptLoader.isCurrentEvent(RivalClanAddEvent.class))
			this.e = 3;
		else if (ScriptLoader.isCurrentEvent(RivalClanRemoveEvent.class))
			this.e = 4;
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "event-clan-two";
	}

	@Override
	@Nullable
	protected Clan[] get(Event e) {
		switch (this.e){
		case 1: return new Clan[] {((AllyClanAddEvent)e).getClanSecond()};
		case 2: return new Clan[] {((AllyClanRemoveEvent)e).getClanSecond()};
		case 3: return new Clan[] {((RivalClanAddEvent)e).getClanSecond()};
		case 4: return new Clan[] {((RivalClanRemoveEvent)e).getClanSecond()};
		}
		return null;
	}

}
