package me.tuke.sktuke.hooks.legendchat.conditions;

import me.tuke.sktuke.util.Registry;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import br.com.devpaulo.legendchat.api.Legendchat;
import br.com.devpaulo.legendchat.channels.types.Channel;
import br.com.devpaulo.legendchat.players.PlayerManager;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class CondCanSayChannel extends Condition{

	static {
		Registry.newCondition(CondCanSayChannel.class, "%player% can (see|say in) [channel] %channel%", "%player% can't (see|say in) [channel] %channel%");
	}
	private Expression<Player> p;
	private Expression<Channel> c;
	private boolean IsNeg = false;
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		this.p = (Expression<Player>) arg[0];
		this.c = (Expression<Channel>) arg[1];
		if (arg1 == 1)
			this.IsNeg = true;
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return this.p + " can say in channel " + this.c;
	}

	@Override
	public boolean check(Event e) {
		PlayerManager pm = Legendchat.getPlayerManager();
		Player p = this.p.getSingle(e);
		Channel c = this.c.getSingle(e);
		if (IsNeg)
			return !pm.canPlayerSeeChannel(p, c);
		return pm.canPlayerSeeChannel(p, c);
	}

}
