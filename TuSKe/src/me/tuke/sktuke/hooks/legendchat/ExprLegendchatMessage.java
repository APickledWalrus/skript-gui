package me.tuke.sktuke.hooks.legendchat;

import org.bukkit.event.Event;
import javax.annotation.Nullable;

import br.com.devpaulo.legendchat.api.events.ChatMessageEvent;
import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

public class ExprLegendchatMessage extends SimpleExpression<String> {
	private Kleenean delay = Kleenean.FALSE;
	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public boolean init(Expression<?>[] arg0, int arg1, Kleenean arg2, SkriptParser.ParseResult arg3) {
		if (!ScriptLoader.isCurrentEvent(ChatMessageEvent.class)){
			Skript.error("legendchat message can only be used in a Legendchat chat event.", ErrorQuality.SEMANTIC_ERROR);
			return false;
		}
		delay = arg2;
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "l[egend]c[hat] message";
	}

	@Override
	@Nullable
	protected String[] get(Event e) {
		ChatMessageEvent chat = (ChatMessageEvent)e;
		return new String[]{chat.getMessage()};
	}
	
	public void change(Event e, Object[] delta, Changer.ChangeMode mode){
		ChatMessageEvent chat = (ChatMessageEvent)e;
		if (mode == ChangeMode.SET)
			chat.setMessage((String)delta[0]);
		if (mode == ChangeMode.ADD)
			chat.setMessage(chat.getMessage() + (String)delta[0]);
	}
	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (delay.equals(Kleenean.TRUE)){
			Skript.error("'legendchat message' can't be edited after the event has already passed.", ErrorQuality.SEMANTIC_ERROR);
			return null;
		}
		if (mode == ChangeMode.SET || mode == ChangeMode.ADD)
			return CollectionUtils.array(String.class);
		return null;
	}

}
