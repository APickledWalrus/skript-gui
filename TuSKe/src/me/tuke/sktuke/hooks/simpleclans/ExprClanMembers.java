package me.tuke.sktuke.hooks.simpleclans;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;

public class ExprClanMembers extends SimpleExpression<OfflinePlayer>{
	
	private int x;
	private Expression<Clan> c;

	@Override
	public Class<? extends OfflinePlayer> getReturnType() {
		return OfflinePlayer.class;
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		this.c = (Expression<Clan>) arg[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "clan members";
	}
	@Override
	@Nullable
	protected OfflinePlayer[] get(Event e) {
		Clan c = this.c.getSingle(e);
		if (c != null){
			ArrayList<OfflinePlayer> players = new ArrayList<OfflinePlayer>();
			List<ClanPlayer> lcp = c.getMembers();
			for (x = 0; x < lcp.size(); x++){
				players.add(Bukkit.getOfflinePlayer(lcp.get(x).getUniqueId()));
			}
			return players.toArray(new OfflinePlayer[players.size()]);
			
		}
		return null;
	}
}
