package com.github.tukenuke.tuske.hooks.simpleclans.expressions;

import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

public class ExprKDRofPlayer extends SimplePropertyExpression<Player, Number>{
	static {
		Registry.newProperty(ExprKDRofPlayer.class, "clan K[ill ]D[eath ]R[atio]", "player");
	}

	@Override
	public Class<? extends Number> getReturnType() {
		return Number.class;
	}

	@Override
	@Nullable
	public Number convert(Player p) {
		return SimpleClans.getInstance().getClanManager().getClanPlayer(p).getKDR();
	}

	@Override
	protected String getPropertyName() {
		return "kdr";
	}
	public void change(Event e, Object[] delta, Changer.ChangeMode mode){
	    Object[] kdr = getExpr().getArray(e);
	    Player p = (Player) kdr[0];
		if (mode == ChangeMode.RESET || mode == ChangeMode.DELETE){
			SimpleClans.getInstance().getClanManager().getClanPlayer(p).setCivilianKills(0);
			SimpleClans.getInstance().getClanManager().getClanPlayer(p).setDeaths(0);
			SimpleClans.getInstance().getClanManager().getClanPlayer(p).setNeutralKills(0);
			SimpleClans.getInstance().getClanManager().getClanPlayer(p).setRivalKills(0);
		}
	}
	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode == ChangeMode.RESET || mode == ChangeMode.DELETE)
			return CollectionUtils.array(Number.class);
		return null;
	}

}
