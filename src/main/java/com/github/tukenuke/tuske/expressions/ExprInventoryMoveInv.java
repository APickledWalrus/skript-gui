package com.github.tukenuke.tuske.expressions;

import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import javax.annotation.Nullable;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.github.tukenuke.tuske.events.customevent.InventoryMoveEvent;

public class ExprInventoryMoveInv extends SimpleExpression<Inventory>{
	static {
		Registry.newSimple(ExprInventoryMoveInv.class, 0,"[event-]inventory-(one|two)");
	}

	private boolean isFrom;
	@Override
	public Class<? extends Inventory> getReturnType() {
		return Inventory.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public boolean init(Expression<?>[] arg0, int arg1, Kleenean arg2, ParseResult arg3) {
		if (!ScriptLoader.isCurrentEvent(InventoryMoveEvent.class)){
			Skript.error("The '" + arg3.expr + "' expression can only be used in Inventory Move event.");
			return false;
		}
		isFrom = arg3.expr.contains("one");
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "event-inventory-move";
	}

	@Override
	@Nullable
	protected Inventory[] get(Event e) {
		return new Inventory[]{ (isFrom) ? ((InventoryMoveEvent)e).getInventoryFrom() : ((InventoryMoveEvent)e).getInventoryTo()};
	}

}
