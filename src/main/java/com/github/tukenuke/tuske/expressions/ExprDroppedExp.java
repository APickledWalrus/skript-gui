package com.github.tukenuke.tuske.expressions;

import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;

import javax.annotation.Nullable;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Experience;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

public class ExprDroppedExp extends SimpleExpression<Experience>{
	static {
		Registry.newSimple(ExprDroppedExp.class, "[the] dropped [e]xp[erience] [orb[s]]");
	}

	@Override
	public Class<? extends Experience> getReturnType() {
		return Experience.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg0, int arg1, Kleenean arg2, ParseResult arg3) {
		if (ScriptLoader.isCurrentEvent(BlockBreakEvent.class, EntityDeathEvent.class, FurnaceExtractEvent.class))
			return true;
		return false;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "dropped experience";
	}

	@Override
	@Nullable
	protected Experience[] get(Event e) {
		if (e instanceof BlockBreakEvent)
			return new Experience[]{new Experience(((BlockBreakEvent)e).getExpToDrop())};
		else if (e instanceof EntityDeathEvent)
			return new Experience[]{new Experience(((EntityDeathEvent) e).getDroppedExp())};
		else if (e instanceof FurnaceExtractEvent)
			return new Experience[]{new Experience(((FurnaceExtractEvent) e).getExpToDrop())};
		return null;
	}

	
	public void change(Event e, Object[] delta, Changer.ChangeMode mode){
		int b = 0;
		if (delta != null)
			b = (delta[0] instanceof Experience) ? ((Experience)delta[0]).getXP() : ((Number)delta[0]).intValue();
		else if (mode != ChangeMode.DELETE && mode != ChangeMode.RESET)
			return;
		int a = 0;
		if (e instanceof BlockBreakEvent)
			a = ((BlockBreakEvent) e).getExpToDrop();
		else if (e instanceof EntityDeathEvent)
			a = ((EntityDeathEvent) e).getDroppedExp();
		else if (e instanceof FurnaceExtractEvent)
			a = ((FurnaceExtractEvent) e).getExpToDrop();
		switch (mode){
			case ADD: a += b; break;
			case REMOVE: a -= b; break;
			default: a = b; break;
	    }
		if (e instanceof BlockBreakEvent)
			((BlockBreakEvent) e).setExpToDrop(a);
		else if (e instanceof EntityDeathEvent)
			((EntityDeathEvent) e).setDroppedExp(a);
		else if (e instanceof FurnaceExtractEvent)
			((FurnaceExtractEvent) e).setExpToDrop(a);
	}

	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode != ChangeMode.REMOVE_ALL)
			return CollectionUtils.array(Experience.class, Number.class);
		return null;
		
	}

}
