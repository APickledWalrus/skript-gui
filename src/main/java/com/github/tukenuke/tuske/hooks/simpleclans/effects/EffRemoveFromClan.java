package com.github.tukenuke.tuske.hooks.simpleclans.effects;

import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import javax.annotation.Nullable;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

public class EffRemoveFromClan extends Effect{
	static {
		Registry.newEffect(EffRemoveFromClan.class, "(remove|kick) %player% from his clan", "[make] %player% resign from his clan");
	}

	private Expression<Player> p;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		this.p = (Expression<Player>) arg[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "remove " + this.p + " from his clan";
	}

	@Override
	protected void execute(Event e) {
		Player p = this.p.getSingle(e);
		SimpleClans.getInstance().getClanManager().getClanPlayer(p).getClan().removePlayerFromClan(p.getUniqueId());
		
		
	}

}
