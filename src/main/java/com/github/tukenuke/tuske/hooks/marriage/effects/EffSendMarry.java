package com.github.tukenuke.tuske.hooks.marriage.effects;

import javax.annotation.Nullable;

import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.lenis0012.bukkit.marriage2.Marriage;
import com.lenis0012.bukkit.marriage2.MarriageAPI;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffSendMarry extends Effect {
	static {
		Registry.newEffect(EffSendMarry.class, "[make] %player% invite %player% to marry", "send invite of marry from %player% to %player%");
	}

	private Expression<Player> p1;
	private Expression<Player> p2;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		p1 = (Expression<Player>) arg[0];
		p2 = (Expression<Player>) arg[1];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return null;
	}

	@Override
	protected void execute(Event e) {
		Player p1 = (Player)this.p1.getSingle(e);
		Player p2 = (Player)this.p2.getSingle(e);
		Marriage marry = (Marriage) MarriageAPI.getInstance();
		if (p1 != null || p2 !=null)
		marry.getMPlayer(p2.getUniqueId()).requestMarriage(p1.getUniqueId());;
		
	}

}