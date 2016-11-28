package me.tuke.sktuke.hooks.simpleclans;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

public class EffPlacePlayerInClan  extends Effect{
	private Expression<Player> p;
	private Expression<Clan> c;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		this.p = (Expression<Player>) arg[0];
		this.c = (Expression<Clan>) arg[1];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "place " + this.p + " in[ clan] " + this.c;
	}

	@Override
	protected void execute(Event e) {
		Player p = this.p.getSingle(e);
		Clan c = this.c.getSingle(e);
		ClanPlayer cp = SimpleClans.getInstance().getClanManager().getClanPlayer(p);
		if (cp == null)
			cp = SimpleClans.getInstance().getClanManager().getCreateClanPlayerUUID(p.getName());
		c.addPlayerToClan(cp);
		
		
	}

}