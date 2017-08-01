package com.github.tukenuke.tuske.hooks.legendchat.expressions;

import ch.njol.skript.util.Timespan;
import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.entity.Player;

import br.com.devpaulo.legendchat.api.Legendchat;
import br.com.devpaulo.legendchat.mutes.MuteManager;

import javax.annotation.Nullable;

import ch.njol.skript.expressions.base.SimplePropertyExpression;

public class ExprMuteLeftTime extends SimplePropertyExpression<Player, Timespan>{
	static {
		Registry.newProperty(ExprMuteLeftTime.class, "mute (left|remaining) time", "player");
	}
	@Override
	public Class<? extends Timespan> getReturnType() {
		return Timespan.class;
	}

	@Override
	@Nullable
	public Timespan convert(Player p) {
		MuteManager mm = Legendchat.getMuteManager();
		if (mm.isPlayerMuted(p.getName()))
			return new Timespan(mm.getPlayerMuteTimeLeft(p.getName()) * 60000); // = 1 minute
		return null;
	}

	@Override
	protected String getPropertyName() {
		return "mute left time";
	}

}
