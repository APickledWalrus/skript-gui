package me.tuke.sktuke.hooks.legendchat;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;

import br.com.devpaulo.legendchat.api.events.PrivateMessageEvent;

import javax.annotation.Nullable;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprTellReceiver extends SimpleExpression<CommandSender>{

	//private boolean noWait = true;
	@Override
	public Class<? extends CommandSender> getReturnType() {
		return CommandSender.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public boolean init(Expression<?>[] arg0, int arg1, Kleenean arg2, ParseResult arg3) {
		if (!ScriptLoader.isCurrentEvent(PrivateMessageEvent.class)){
			Skript.error("Receiver can only be used on Legendchat tell event.");
			return false;
		}
		//if (arg2 == Kleenean.TRUE)
		//	noWait = false;
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "receiver";
	}

	@Override
	@Nullable
	protected CommandSender[] get(Event e) {
		PrivateMessageEvent tell = (PrivateMessageEvent)e;
		return new CommandSender[] { (CommandSender) tell.getReceiver() };
	}
/*	
public void change(Event e, Object[] delta, Changer.ChangeMode mode){
	PrivateMessageEvent tell = (PrivateMessageEvent)e;
	if (mode == ChangeMode.SET)
		tell.setReceiver((Player)delta[0]);
}
@SuppressWarnings("unchecked")
public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
	if (!noWait){
		Skript.error("Can't set tell receiver to anything after the event has already passed", ErrorQuality.SEMANTIC_ERROR);
		return null;
	}
	if (mode == ChangeMode.SET)
		return CollectionUtils.array(Player.class);
	return null;
}
*/
}