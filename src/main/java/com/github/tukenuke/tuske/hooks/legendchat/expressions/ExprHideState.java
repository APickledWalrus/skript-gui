package com.github.tukenuke.tuske.hooks.legendchat.expressions;

import javax.annotation.Nullable;

import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import br.com.devpaulo.legendchat.api.Legendchat;
import br.com.devpaulo.legendchat.players.PlayerManager;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;

public class ExprHideState extends SimplePropertyExpression<Player, Boolean>{
	static {
		Registry.newProperty(ExprHideState.class, "hide state", "player");
	}

	@Override
	public Class<? extends Boolean> getReturnType() {
		return Boolean.class;
	}

	@Override
	@Nullable
	public Boolean convert(Player p) {
		return Legendchat.getPlayerManager().isPlayerHiddenFromRecipients(p);
	}

	@Override
	protected String getPropertyName() {
		return "hide state";
	}
	
	public void change(Event e, Object[] delta, Changer.ChangeMode mode){
		PlayerManager pm = Legendchat.getPlayerManager();
		Player[] players = getExpr().getArray(e);
		Boolean b = (Boolean) delta[0];
		if (mode == ChangeMode.SET && b != null){
			for (Player p : players) {
				if (pm.isPlayerHiddenFromRecipients(p) && !b)
					pm.showPlayerToRecipients(p);
				else if (b)
					pm.hidePlayerFromRecipients(p);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode == ChangeMode.SET)
			return CollectionUtils.array(Boolean.class);
		return null;
		
	}

}