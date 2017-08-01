package com.github.tukenuke.tuske.hooks.simpleclans.expressions;

import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;


import javax.annotation.Nullable;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

public class ExprDeathsOfPlayer extends SimplePropertyExpression<Player, Number>{
	static {
		Registry.newProperty(ExprDeathsOfPlayer.class, "clan deaths", "player");
	}

	@Override
	public Class<? extends Number> getReturnType() {
		return Number.class;
	}

	@Override
	@Nullable
	public Number convert(Player p) {
		if (p != null){
			ClanPlayer cp = SimpleClans.getInstance().getClanManager().getClanPlayer(p);
			if (cp != null){
				return cp.getDeaths();
			}
		}
		return null;
	}

	@Override
	protected String getPropertyName() {
		return "clan deaths";
	}
	
	public void change(Event e, Object[] delta, Changer.ChangeMode mode){
		Player p = (Player) getExpr().getArray(e)[0];
		int d = 0;
		if (delta != null)
			d = ((Number) delta[0]).intValue();
		if (p != null){
			ClanPlayer cp = SimpleClans.getInstance().getClanManager().getClanPlayer(p);
			if (cp != null){
				switch(mode){
					case ADD: 
						d += cp.getDeaths(); break;
					case REMOVE:
						d = cp.getDeaths() - d; break;
					default: break;
				}
				if (d < 0)
					d = 0;
				cp.setDeaths(d);
				SimpleClans.getInstance().getStorageManager().updateClanPlayerAsync(cp);
			}
		}
	}
	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode != ChangeMode.REMOVE_ALL)
			return CollectionUtils.array(Number.class);
		return null;
	}

}
