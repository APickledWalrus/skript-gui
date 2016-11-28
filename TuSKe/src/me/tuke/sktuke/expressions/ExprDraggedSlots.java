package me.tuke.sktuke.expressions;

import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryDragEvent;
import javax.annotation.Nullable;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprDraggedSlots extends SimpleExpression<Integer>{

	@Override
	public Class<? extends Integer> getReturnType() {
		return Integer.class;
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		if (!ScriptLoader.isCurrentEvent(InventoryDragEvent.class)){
			Skript.error("The expression '" + arg3.expr + "' can only be used in Inventory Drag event.");
			return false;
		}
			
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "event-integers";
	}

	@Override
	@Nullable
	protected Integer[] get(Event e) {
		return ((InventoryDragEvent)e).getInventorySlots().toArray(new Integer[((InventoryDragEvent)e).getInventorySlots().size()]);
	}

}
