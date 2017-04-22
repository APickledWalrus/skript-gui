package me.tuke.sktuke.expressions.gui;

import me.tuke.sktuke.util.InventoryUtils;
import me.tuke.sktuke.util.Registry;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprVirtualInv extends SimpleExpression<Inventory>{
	static {
		Registry.newSimple(ExprVirtualInv.class,
				"virtual %inventorytype% [inventory] [with size %-number%] [(named|with (name|title)) %-string%]",
				"virtual %inventorytype% [inventory] [with %-number% row[s]] [(named|with (name|title)) %-string%]");
	}

	private Expression<InventoryType> it;
	private Expression<Number> size;
	private Expression<String> name;
	
	@Override
	public Class<? extends Inventory> getReturnType() {
		return Inventory.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		it = (Expression<InventoryType>) arg[0];
		size = (Expression<Number>) arg[1];
		name = (Expression<String>) arg[2];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "virtual inventory";
	}

	@Override
	@Nullable
	protected Inventory[] get(Event e) {
		InventoryType type = it.getSingle(e);
		if (type != null){
			Integer size = this.size != null ? this.size.getSingle(e).intValue() : null;
			String name = this.name != null ? this.name.getSingle(e) : null;
			return new Inventory[]{InventoryUtils.newInventory(type, size, name)};
		}
		return null;
	}
}
