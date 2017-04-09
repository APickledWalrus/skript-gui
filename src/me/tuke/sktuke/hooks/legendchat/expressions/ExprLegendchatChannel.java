package me.tuke.sktuke.hooks.legendchat.expressions;

import org.bukkit.event.Event;
import javax.annotation.Nullable;

import br.com.devpaulo.legendchat.api.Legendchat;
import br.com.devpaulo.legendchat.api.events.ChatMessageEvent;
import br.com.devpaulo.legendchat.channels.ChannelManager;
import br.com.devpaulo.legendchat.channels.types.Channel;
import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;

public class ExprLegendchatChannel extends SimpleExpression<Channel> {
	

	@Override
	public Class<? extends Channel> getReturnType() {
		return Channel.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public boolean init(Expression<?>[] arg0, int arg1, Kleenean arg2, SkriptParser.ParseResult arg3) {
		if (!ScriptLoader. isCurrentEvent(ChatMessageEvent.class)){
			Skript.error("legendchat channel can only be used in a Legendchat chat event.", ErrorQuality.SEMANTIC_ERROR);
			return false;
		}		
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "l[egend]c[hat] channel";
	}

	@Override
	@Nullable
	protected Channel[] get(Event e) {
		ChatMessageEvent chat = (ChatMessageEvent)e;
		ChannelManager cm = Legendchat.getChannelManager();
		return new Channel[]{cm.getChannelByName(chat.getChannel().getName())};
	}

}
