package me.tuke.sktuke.hooks.simpleclans;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import javax.annotation.Nullable;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

public class EffInvitePlayerToClan extends Effect{

	private Expression<Player> ps;
	private Expression<Player> pr;
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		this.ps = (Expression<Player>) arg[0];
		this.pr = (Expression<Player>) arg[1];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "send invite of clan from " + this.pr + " to " + this.ps;
	}

	@Override
	protected void execute(Event e) {
		Player ps = this.ps.getSingle(e);
		Player pr = this.pr.getSingle(e);
		ClanPlayer cps = SimpleClans.getInstance().getClanManager().getClanPlayer(ps);
		ClanPlayer cpr = SimpleClans.getInstance().getClanManager().getClanPlayer(pr);
		if (cps != null && cps.isLeader() && cpr == null)
			SimpleClans.getInstance().getRequestManager().addInviteRequest(cps, pr.getName(), cps.getClan());
			
		
	}

}
