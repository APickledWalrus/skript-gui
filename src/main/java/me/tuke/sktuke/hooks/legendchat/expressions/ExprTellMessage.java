package me.tuke.sktuke.hooks.legendchat.expressions;

import me.tuke.sktuke.util.Registry;
import org.bukkit.event.Event;
import javax.annotation.Nullable;

import br.com.devpaulo.legendchat.api.events.PrivateMessageEvent;
import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

public class ExprTellMessage extends SimpleExpression<String> {
	static {
		Registry.newSimple(ExprTellMessage.class, 0, "tell message");
	}
	private boolean noWait = true;

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public boolean init(Expression<?>[] arg0, int arg1, Kleenean arg2, ParseResult arg3) {
		if (!ScriptLoader.isCurrentEvent(PrivateMessageEvent.class)){
			Skript.error("tell message can only be used in a Tell event.", ErrorQuality.SEMANTIC_ERROR);
			return false;
		}
		if (arg2 == Kleenean.TRUE)
			noWait= false;
		
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "tell message";
	}

	@Override
	@Nullable
	protected String[] get(Event e) {
		PrivateMessageEvent tell = (PrivateMessageEvent)e;
		return new String[]{tell.getMessage()};
	}

	
	public void change(Event e, Object[] delta, Changer.ChangeMode mode){
		PrivateMessageEvent tell = (PrivateMessageEvent)e;
			if (mode == ChangeMode.SET)
				tell.setMessage((String)delta[0]);
			if (mode == ChangeMode.ADD)
				tell.setMessage(tell.getMessage() + (String)delta[0]);
	}
	
	
	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (!noWait){
			Skript.error("Can't add/set tell message to anything after the event has already passed", ErrorQuality.SEMANTIC_ERROR);
			return null;
		}
		if (mode == ChangeMode.SET || mode == ChangeMode.ADD)
			return CollectionUtils.array(String.class);
		return null;
	}

}