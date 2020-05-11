package io.github.apickledwalrus.skriptgui.elements.expressions;

import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import io.github.apickledwalrus.skriptgui.util.InventoryUtils;

@Name("Virtual Inventory")
@Description("An expression to create inventories that can be used with GUIs.")
@Examples("create a gui with virtual chest inventory with 3 rows named \"My GUI\"")
@Since("1.0.0")
public class ExprVirtualInventory extends SimpleExpression<Inventory>{

	static {
		Skript.registerExpression(ExprVirtualInventory.class, Inventory.class, ExpressionType.SIMPLE,
				"virtual %inventorytype% [with size %-number%] [(named|with (name|title)) %-string%]",
				"virtual %inventorytype% [with %-number% row[s]] [(named|with (name|title)) %-string%]",
				"virtual %inventorytype% [(named|with (name|title)) %-string%] with size %-number%",
				"virtual %inventorytype% [(named|with (name|title)) %-string%] with %-number% row[s]"
		);
	}

	private Expression<InventoryType> inventoryType;
	private Expression<Number> size;
	private Expression<String> name;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, ParseResult parseResult) {
		inventoryType = (Expression<InventoryType>) exprs[0];
		if (matchedPattern > 1) {
			name = (Expression<String>) exprs[1];
			size = (Expression<Number>) exprs[2];
		} else {
			name = (Expression<String>) exprs[2];
			size = (Expression<Number>) exprs[1];
		}
		return true;
	}

	@Override
	protected Inventory[] get(Event e) {
		InventoryType type = inventoryType.getSingle(e);
		if (type == null)
			return new Inventory[]{};
		Number size = this.size != null ? this.size.getSingle(e) : null;
		String name = this.name != null ? this.name.getSingle(e) : null;
		return new Inventory[]{InventoryUtils.newInventory(type, (size != null ? size.intValue() : 0), name)};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends Inventory> getReturnType() {
		return Inventory.class;
	}

	@Override
	public String toString(Event e, boolean debug) {
		return "virtual " + inventoryType.toString(e, debug)
			+ (name != null ? " with name" + name.toString(e, debug) : "")
			+ (size != null ? " with " + size.toString(e, debug) + " rows" : "");
	}

}
