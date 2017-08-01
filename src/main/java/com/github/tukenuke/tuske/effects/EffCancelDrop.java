package com.github.tukenuke.tuske.effects;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import javax.annotation.Nullable;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
@Name("Cancel Drops")
@Description("Cancels the drops of items, experiences or both in death events, where it won't drop the **player** items (like gamerule KeepInventory), " +
		"or cancel the item drop of break block event (for minecraft 1.12+ only).")
@Examples({
		"on death of player:",
		"\tcancel the drops #It won't drop the experience and items.",
		" ",
		"on break of diamond ore:",
		"\tcancel the drops of items #It won't drop the items only."})
@Since("1.0, 1.8.1 (block break event)")
public class EffCancelDrop extends Effect{
	public static final boolean canCancelBreakDrops = Skript.isRunningMinecraft(1, 12);
	static {
		Registry.newEffect(EffCancelDrop.class,
				"cancel [the] drops",
				"cancel [the] drops of [e]xp[perience][s]",
				"cancel [the] drops of (inventory|items)");
	}
	
	private int cancel = 1;

	@Override
	public boolean init(Expression<?>[] arg0, int arg1, Kleenean arg2, ParseResult arg3) {
		if ((canCancelBreakDrops && !ScriptLoader.isCurrentEvent(PlayerDeathEvent.class, BlockBreakEvent.class) ||
				(!canCancelBreakDrops && !ScriptLoader.isCurrentEvent(PlayerDeathEvent.class)))){
			Skript.error("Can't use '" + arg3.expr + "' outside of death" + (canCancelBreakDrops ? " or break" : "") + " event");
			return false;
		}
		if (arg2.isTrue()) {
			Skript.error("Can't " +arg3.expr+" anymore after the event has already passed. It should be used before any wait effect.");
			return false;
		}
		cancel = arg1;
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "cancel drop";
	}

	@Override
	protected void execute(Event e) {
		if (e instanceof PlayerDeathEvent){
			PlayerDeathEvent pde = (PlayerDeathEvent)e;
			if (!pde.getKeepLevel() && cancel <= 1){
				pde.setKeepLevel(true);
				pde.setDroppedExp(0);
			}
			if (cancel != 1)
				pde.setKeepInventory(true);		
		} else if (canCancelBreakDrops && e instanceof BlockBreakEvent) {
			if (cancel <= 1)
				((BlockBreakEvent) e).setExpToDrop(0);
			if (cancel != 1)
				((BlockBreakEvent) e).setDropItems(false);
		}
	}
}
