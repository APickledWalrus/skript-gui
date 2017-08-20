package com.github.tukenuke.tuske.expressions;

import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprDraggedSlots extends SimpleExpression<Integer>{
	static {
		Registry.newSimple(ExprDraggedSlots.class, "[event-]dragged(-| )(top|bottom)(-| )slots");
	}

	private boolean isTop = false;
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
		isTop = arg3.expr.toLowerCase().contains("top");
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "inventory dragged " + (isTop ? "top" : "bottom") + " slots";
	}

	@Override
	@Nullable
	protected Integer[] get(Event e) {
		
		List<Integer> slots = new ArrayList<>();
		if (e instanceof InventoryDragEvent) {
			for (Integer slot : ((InventoryDragEvent) e).getRawSlots()){
				int max = ((InventoryDragEvent) e).getView().getTopInventory() != null ? ((InventoryDragEvent) e).getView().getTopInventory().getSize() : 0;
				if (isTop ? slot < max : slot >= max)
					slots.add(((InventoryDragEvent) e).getView().convertSlot(slot));
			}
		}
		return slots.toArray(new Integer[slots.size()]);
	}

}
