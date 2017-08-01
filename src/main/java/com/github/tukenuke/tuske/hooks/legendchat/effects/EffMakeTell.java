package com.github.tukenuke.tuske.hooks.legendchat.effects;

import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import br.com.devpaulo.legendchat.api.Legendchat;
import br.com.devpaulo.legendchat.privatemessages.PrivateMessageManager;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffMakeTell extends Effect{
	static {
		Registry.newEffect(EffMakeTell.class, "[make] %player% [send] tell %string% to %player%");
	}

	private Expression<Player> s;
	private Expression<Player> r;
	private Expression<String> m;
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		s = (Expression<Player>) arg[0];
		m = (Expression<String>) arg[1];
		r = (Expression<Player>) arg[2];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return null;
	}

	@Override
	protected void execute(Event e) {
		Player sender = (Player)this.s.getSingle(e);
		String msg = (String)this.m.getSingle(e);
		Player receiver = (Player)this.r.getSingle(e);
		PrivateMessageManager pm = Legendchat.getPrivateMessageManager();
		pm.tellPlayer(sender, receiver, msg);
	}

}
