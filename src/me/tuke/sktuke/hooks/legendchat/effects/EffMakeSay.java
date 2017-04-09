package me.tuke.sktuke.hooks.legendchat.effects;

import me.tuke.sktuke.util.NewRegister;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;


import javax.annotation.Nullable;

import br.com.devpaulo.legendchat.api.Legendchat;
import br.com.devpaulo.legendchat.channels.types.Channel;
import br.com.devpaulo.legendchat.players.PlayerManager;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffMakeSay extends Effect{
	static {
		NewRegister.newEffect(EffMakeSay.class, "make %player% say %string% in [channel] %channel%");
	}

	private Expression<Player> p;
	private Expression<String> m;
	private Expression<Channel> c;
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		p = (Expression<Player>) arg[0];
		m = (Expression<String>) arg[1];
		c = (Expression<Channel>) arg[2];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "make player say string in channel channel";
	}

	@Override
	protected void execute(Event e) {
		PlayerManager pm = Legendchat.getPlayerManager();
		Player p = (Player)this.p.getSingle(e);
		String m = (String)this.m.getSingle(e);
		Channel ch = (Channel)this.c.getSingle(e);
		Channel dc = pm.getPlayerFocusedChannel(p);
		pm.setPlayerFocusedChannel(p, ch, false);
		p.chat(m);
		pm.setPlayerFocusedChannel(p, dc, false);
			
	}

}
