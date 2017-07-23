package me.tuke.sktuke.hooks.legendchat.expressions;

import me.tuke.sktuke.util.Registry;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import br.com.devpaulo.legendchat.api.Legendchat;
import br.com.devpaulo.legendchat.players.PlayerManager;

import javax.annotation.Nullable;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;

public class ExprSpyState extends SimplePropertyExpression<Player, Boolean>{
	static {
		Registry.newProperty(ExprSpyState.class, "spy state", "player");
	}

	@Override
	public Class<? extends Boolean> getReturnType() {
		return Boolean.class;
	}

	@Override
	@Nullable
	public Boolean convert(Player p) {
		return Legendchat.getPlayerManager().isSpy(p);
	}

	@Override
	protected String getPropertyName() {
		return "spy state";
	}
	
	public void change(Event e, Object[] delta, Changer.ChangeMode mode){
		PlayerManager pm = Legendchat.getPlayerManager();
		Player[] players = getExpr().getArray(e);
		Boolean b = (Boolean) delta[0];
		if (mode == ChangeMode.SET && b != null){
			for (Player p : players) {
				if (pm.isSpy(p) && !b)
					pm.removeSpy(p);
				else if (b)
					pm.addSpy(p);
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

