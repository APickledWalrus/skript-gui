package me.tuke.sktuke.expressions.gui;

import me.tuke.sktuke.util.NewRegister;
import org.bukkit.Bukkit;
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
		NewRegister.newSimple(ExprVirtualInv.class,
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
			return new Inventory[]{getInventory(type, size, name)};
		}
		return null;
	}
	
	public static Inventory getInventory(InventoryType type, Integer size, String name){
		if (size == null)
			size = type.getDefaultSize();
		else if (size % 9 != 0)
			size *= 9;
		if (name == null)
			name = type.getDefaultTitle();
		else if (name.length() > 32)
			name = name.substring(0, 32);
		switch (type){
		case BEACON:
		case MERCHANT:
		case CRAFTING:
		case CREATIVE: return null;
		case CHEST:	return Bukkit.getServer().createInventory(null, size, name);
		case DROPPER: type = InventoryType.DISPENSER;
		default: return Bukkit.getServer().createInventory(null, type, name);
		}
	}

}
