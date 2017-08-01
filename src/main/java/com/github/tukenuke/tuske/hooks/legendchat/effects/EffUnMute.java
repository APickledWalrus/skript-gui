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

public class EffUnMute extends Effect{
	static {
		Registry.newEffect(EffUnMute.class, 1, "unmute %player%");
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
		return "mute " + this.p;
	}

	@Override
	protected void execute(Event e) {
		Player p = this.p.getSingle(e);
		MuteManager mm = Legendchat.getMuteManager();
		mm.unmutePlayer(p.getName());
		
	}

}
