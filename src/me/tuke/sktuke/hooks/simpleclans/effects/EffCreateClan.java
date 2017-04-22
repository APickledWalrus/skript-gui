package me.tuke.sktuke.hooks.simpleclans.effects;

import me.tuke.sktuke.util.Registry;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import javax.annotation.Nullable;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

public class EffCreateClan extends Effect{
	static {
		Registry.newEffect(EffCreateClan.class, "create [a] [new] clan named %string% with tag %string% (to|for) %player%");
	}

	private Expression<Player> p;
	private Expression<String> t;  
	private Expression<String> n; 
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		this.p = (Expression<Player>) arg[2];
		this.t = (Expression<String>) arg[1];
		this.n = (Expression<String>) arg[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "create clan named " + this.n + " with tag " + this.t + " to " + this.p; 
	}

	@Override
	protected void execute(Event e) {
		Player p = (Player) this.p.getSingle(e);
		String t = (String) this.t.getSingle(e);
		String n = (String) this.n.getSingle(e);
		SimpleClans.getInstance().getClanManager().createClan(p, t.replaceAll("&", "�"), n.replaceAll("&", "").replaceAll("�", ""));
		
	}

}
