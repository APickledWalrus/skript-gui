package me.tuke.sktuke.expressions;

import me.tuke.sktuke.util.NewRegister;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;

import java.util.UUID;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprUUIDOfflinePlayer extends SimpleExpression<OfflinePlayer>{
	static {
		NewRegister.newSimple(ExprUUIDOfflinePlayer.class, "offline player from [uuid] %string%");
	}

	private Expression<String> uuid;
	@Override
	public Class<? extends OfflinePlayer> getReturnType() {
		// TODO Auto-generated method stub
		return OfflinePlayer.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		uuid = (Expression<String>) arg[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return null;
	}

	@Override
	@Nullable
	protected OfflinePlayer[] get(Event e) {
		String uuid = this.uuid.getSingle(e);
		return uuid != null && uuid.matches("(?i)[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}")? new OfflinePlayer[]{Bukkit.getOfflinePlayer(UUID.fromString(uuid))} : null;
	}

}
