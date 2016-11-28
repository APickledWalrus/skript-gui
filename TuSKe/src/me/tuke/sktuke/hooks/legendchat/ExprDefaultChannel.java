package me.tuke.sktuke.hooks.legendchat;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import br.com.devpaulo.legendchat.api.Legendchat;
import br.com.devpaulo.legendchat.channels.types.Channel;
import br.com.devpaulo.legendchat.players.PlayerManager;

import javax.annotation.Nullable;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;

public class ExprDefaultChannel extends SimplePropertyExpression<Player, Channel>{

	@Override
	public Class<? extends Channel> getReturnType() {
		return Channel.class;
	}

	@Override
	@Nullable
	public Channel convert(Player e) {
		PlayerManager pm = Legendchat.getPlayerManager();
		if (pm.getPlayerFocusedChannel(e) != null)
			return  pm.getPlayerFocusedChannel(e);
		return null;
	}


	@Override
	protected String getPropertyName() {
		return "default channel of player";
	}

	public void change(Event e, Object[] delta, Changer.ChangeMode mode){
	    Object[] ob = getExpr().getArray(e);
		PlayerManager pm = Legendchat.getPlayerManager();
		Player p = (Player) ob[0];
		if (mode == ChangeMode.SET)
			pm.setPlayerFocusedChannel(p, (Channel)delta [0], false);
		}
	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode == ChangeMode.SET)
			return CollectionUtils.array(Channel.class);
		return null;
	}
}
