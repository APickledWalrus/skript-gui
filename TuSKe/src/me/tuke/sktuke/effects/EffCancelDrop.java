package me.tuke.sktuke.effects;

import org.bukkit.event.Event;
import org.bukkit.event.entity.PlayerDeathEvent;

import javax.annotation.Nullable;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;

public class EffCancelDrop extends Effect{
	
	private int Cancel = 1;

	@Override
	public boolean init(Expression<?>[] arg0, int arg1, Kleenean arg2, ParseResult arg3) {
		if (!ScriptLoader.isCurrentEvent(PlayerDeathEvent.class)){
			Skript.error("Cannot use '" + arg3.expr + "' outside of death event", ErrorQuality.SEMANTIC_ERROR);
			return false;
		}
		if (arg3.expr.toLowerCase().contains("xp"))
			Cancel--;
		else if (arg3.expr.toLowerCase().contains("inventory"))
			Cancel++;
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "cancel drop";
	}

	@Override
	protected void execute(Event e) {
		PlayerDeathEvent pde = (PlayerDeathEvent)e;
		if (!pde.getKeepLevel() && Cancel <= 1){
			pde.setKeepLevel(true);
			pde.setDroppedExp(0);
		}
		if (!pde.getKeepInventory() && Cancel >= 1)
			pde.setKeepInventory(true);		
	}
}
