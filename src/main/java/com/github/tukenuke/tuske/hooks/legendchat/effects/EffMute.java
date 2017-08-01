package com.github.tukenuke.tuske.hooks.legendchat.effects;

import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import br.com.devpaulo.legendchat.api.Legendchat;
import br.com.devpaulo.legendchat.mutes.MuteManager;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffMute extends Effect{
	static {
		Registry.newEffect(EffMute.class, "mute %player% [for %number% minute[s]]");
	}

	private Expression<Player> p;
	private Expression<Number> i;
	private boolean pr;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		p = (Expression<Player>) arg[0];
		i = (Expression<Number>) arg[1];
		pr = (arg3.expr.toLowerCase().contains(" for ") && arg3.expr.toLowerCase().contains(" minute"));
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "mute " + p +" for " + i + "minutes";
	}

	@Override
	protected void execute(Event e) {
		Player p = (Player)this.p.getSingle(e);
		MuteManager mm = Legendchat.getMuteManager();
		int time = Integer.valueOf(0);
		if (this.pr){
			time = ((Number)this.i.getSingle(e)).intValue();
			if (time < 0)
				time = Integer.valueOf(0);
		}
		if(mm.isPlayerMuted(p.getName()))
			mm.unmutePlayer(p.getName());
	    mm.mutePlayer(p.getName(), time);
	}
}