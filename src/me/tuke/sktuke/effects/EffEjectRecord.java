package me.tuke.sktuke.effects;

import me.tuke.sktuke.util.NewRegister;
import org.bukkit.block.Block;
import org.bukkit.block.Jukebox;
import org.bukkit.event.Event;
import javax.annotation.Nullable;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffEjectRecord extends Effect{
	static {
		NewRegister.newEffect(EffEjectRecord.class, "eject record (of|from|) %block%");
	}

	private Expression<Block> block;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		block = (Expression<Block>) arg[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "eject record of " + block.toString(arg0, arg1);
	}

	@Override
	protected void execute(Event e) {
		Block b = block.getSingle(e);
		if (b != null)
			((Jukebox)b.getState()).eject();		
	}

}
