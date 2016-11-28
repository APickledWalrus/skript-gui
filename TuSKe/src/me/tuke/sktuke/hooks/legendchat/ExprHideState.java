package me.tuke.sktuke.hooks.legendchat;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import br.com.devpaulo.legendchat.api.Legendchat;
import br.com.devpaulo.legendchat.players.PlayerManager;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;

public class ExprHideState extends SimplePropertyExpression<Player, Boolean>{

	@Override
	public Class<? extends Boolean> getReturnType() {
		return Boolean.class;
	}

	@Override
	@Nullable
	public Boolean convert(Player p) {
		PlayerManager pm = Legendchat.getPlayerManager();
		return pm.isPlayerHiddenFromRecipients(p);
	}

	@Override
	protected String getPropertyName() {
		return "hide state";
	}
	
	public void change(Event e, Object[] delta, Changer.ChangeMode mode){
	    Object[] ob = getExpr().getArray(e);
		PlayerManager pm = Legendchat.getPlayerManager();
		Player p = (Player) ob[0];
		if (mode == ChangeMode.SET){
			if (pm.isPlayerHiddenFromRecipients(p) && (Boolean) delta[0] == false)
				pm.showPlayerToRecipients(p);
			else if ((Boolean) delta[0] == true)
				pm.hidePlayerFromRecipients(p);;
		}
	}

	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode == ChangeMode.SET)
			return CollectionUtils.array(Boolean.class);
		return null;
		
	}

}