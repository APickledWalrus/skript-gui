package me.tuke.sktuke.hooks.legendchat;

import org.bukkit.entity.Player;

import br.com.devpaulo.legendchat.api.Legendchat;
import br.com.devpaulo.legendchat.mutes.MuteManager;

import javax.annotation.Nullable;

import ch.njol.skript.expressions.base.SimplePropertyExpression;

public class ExprMuteLeftTime extends SimplePropertyExpression<Player, Integer>{

	@Override
	public Class<? extends Integer> getReturnType() {
		return Integer.class;
	}

	@Override
	@Nullable
	public Integer convert(Player p) {
		MuteManager mm = Legendchat.getMuteManager();
		if (mm.isPlayerMuted(p.getName()))
			return mm.getPlayerMuteTimeLeft(p.getName());
		return null;
	}

	@Override
	protected String getPropertyName() {
		return "mute left time";
	}

}
