package com.github.tukenuke.tuske.hooks.simpleclans.expressions;

import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

import javax.annotation.Nullable;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

public class ExprKillsOfPlayer extends SimpleExpression<Number>{
	static {
		Registry.newProperty(ExprKillsOfPlayer.class, "clan (1¦rival|2¦neutral|3¦civilian) kills", "player");
	}
	
	private Expression<Player> p;
	private int i;

	@Override
	public Class<? extends Number> getReturnType() {
		return Number.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		this.p = (Expression<Player>) arg[0];
		this.i = arg3.mark;
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "kills of " + this.p;
	}

	@Override
	@Nullable
	protected Number[] get(Event e) {
		Player p = this.p.getSingle(e);
		if (p != null){
			ClanPlayer cp =  SimpleClans.getInstance().getClanManager().getClanPlayer(p);
			if (cp != null){
				switch (i){
					case 1: return new Number[] {cp.getRivalKills()};
					case 2: return new Number[] {cp.getNeutralKills()};
					case 3: return new Number[] {cp.getCivilianKills()};
				}
			}
			return null;
		}
		return null;
		
	}

	public void change(Event e, Object[] delta, Changer.ChangeMode mode){
		Player p = this.p.getSingle(e);
		if (p != null){
			ClanPlayer cp =  SimpleClans.getInstance().getClanManager().getClanPlayer(p);
			if (cp != null) {
				int k = 0;
				if (delta != null)
					k = ((Number) delta[0]).intValue();
				switch (mode){
					case ADD:
						switch (i){
							case 1: k += cp.getRivalKills();; break;
							case 2: k += cp.getNeutralKills(); break;
							case 3: k += cp.getCivilianKills(); break;
						}
						break;
					case REMOVE:
						switch (i){
							
							case 1: k = cp.getRivalKills() - k;; break;
							case 2: k = cp.getNeutralKills() - k; break;
							case 3: k = cp.getCivilianKills() - k; break;
						}
						break;
					default:
						break;
				}
				if (k < 0)
					k = 0;
				switch (i){
					case 1: cp.setRivalKills(k); break;
					case 2: cp.setNeutralKills(k); break;
					case 3: cp.setCivilianKills(k); break;
				}
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
