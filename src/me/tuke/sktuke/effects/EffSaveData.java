package me.tuke.sktuke.effects;

import me.tuke.sktuke.util.NewRegister;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffSaveData extends Effect{
	static {
		NewRegister.newEffect(EffSaveData.class, "save [player] data of %player%");
	}

	private Expression<Player> p;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		this.p = (Expression<Player>) arg[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "save data of " + this.p;
	}

	@Override
	protected void execute(Event e) {
		
		Player p = this.p.getSingle(e);
		if (p != null && !p.isOnline())
			p.saveData();
		
	}

}
