package me.tuke.sktuke.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import me.tuke.sktuke.util.Registry;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.HashMap;

import javax.annotation.Nullable;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Date;
import ch.njol.skript.util.Timespan;
import ch.njol.util.coll.CollectionUtils;
import me.tuke.sktuke.listeners.OnlineStatusCheck;
@Name("Online Time")
@Description("Returns a {{types|Timespan|timespan}} of online time of {{types|Player|player}} or of the server. Only the online time of player can be edited.")
@Examples({
		"command /online:",
		"\ttrigger:",
		"\t\tsend \"You're online for %online time of player% and the server is online for %online time of server%\""})
@Since("1.0 (player), 1.5.8 (server)")
public class ExprOnlineTime extends SimplePropertyExpression<Player, Timespan>{
	static {
		Registry.newProperty(ExprOnlineTime.class, "online time", "player");
	}

	@Override
	public Class<? extends Timespan> getReturnType() {
		return Timespan.class;
	}

	@Override
	@Nullable
	public Timespan convert(Player p) {
		if (p == null || !p.isOnline())
			return null;
		return new Date(OnlineStatusCheck.getTime(p)).difference(new Date (System.currentTimeMillis()));
	}

	@Override
	protected String getPropertyName() {
		return "online time";
	}

	public void change(Event e, Object[] delta, Changer.ChangeMode mode){
	    Player p = getExpr().getArray(e)[0];
	    Long t = 0L;
	    if (!(mode == ChangeMode.DELETE || mode == ChangeMode.RESET) && delta != null)
	    	t =((Timespan)delta[0]).getMilliSeconds();
		switch (mode){
			case RESET: 
			case DELETE: 
			case SET: OnlineStatusCheck.setTime(p, System.currentTimeMillis() - t); break;
			case ADD: OnlineStatusCheck.setTime(p, OnlineStatusCheck.getTime(p) - t); break;
			case REMOVE: {
				if (OnlineStatusCheck.getTime(p) > System.currentTimeMillis() - t){
					OnlineStatusCheck.setTime(p, System.currentTimeMillis());
					break;
				}
				OnlineStatusCheck.setTime(p, OnlineStatusCheck.getTime(p) + t);
				break;
			}
		default:
			break;
			
	    }
	}

	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode != ChangeMode.REMOVE_ALL)
			return CollectionUtils.array(Timespan.class);
		return null;
		
	}
}
