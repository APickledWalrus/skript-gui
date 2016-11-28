package me.tuke.sktuke.hooks.marriage;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.lenis0012.bukkit.marriage2.config.Settings;

import javax.annotation.Nullable;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;
import me.tuke.sktuke.events.customevent.MarryEvent;

public class ExprThePartners extends SimpleExpression<Player>{

	private int i =0;
	@Override
	public Class<? extends Player> getReturnType() {
		return Player.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public boolean init(Expression<?>[] arg0, int arg1, Kleenean arg2, ParseResult arg3) {
		if (ScriptLoader.isCurrentEvent(MarryEvent.class)){
			if (arg3.expr.toLowerCase().contains("partner-two"))
				this.i = 1;
			else if (arg3.expr.toLowerCase().contains("priest")){
				if (!Settings.ENABLE_PRIEST.value().booleanValue()){
					Skript.error("'" + arg3.expr + "' can only be used if it's enabled in config of Marriage", ErrorQuality.SEMANTIC_ERROR);
					return false;
				}
				this.i = 2;
			}
			return true;
		}
		Skript.error("'" + arg3.expr + "' can only be used in marry event", ErrorQuality.SEMANTIC_ERROR);
		return false;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "event-partner";
	}

	@Override
	@Nullable
	protected Player[] get(Event e) {
		MarryEvent m = (MarryEvent)e;
		if (this.i == 2)
			return new Player[] {m.getPriest()};
		else if (this.i == 1)
			return new Player[] {m.getPlayer2()};
		else
			return new Player[] {m.getPlayer1()};
	}

}
