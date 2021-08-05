package io.github.apickledwalrus.skriptgui.elements.expressions;

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
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.eclipse.jdt.annotation.Nullable;

@Name("Virtual Inventory")
@Description("An expression to create inventories that can be used with GUIs.")
@Examples("create a gui with virtual chest inventory with 3 rows named \"My GUI\"")
@Since("1.0.0")
public class ExprVirtualInventory extends SimpleExpression<Inventory>{

	static {
		Skript.registerExpression(ExprVirtualInventory.class, Inventory.class, ExpressionType.SIMPLE,
				"virtual (1¦(crafting [table]|workbench)|2¦chest|3¦anvil|4¦hopper|5¦dropper|6¦dispenser|%-inventorytype%) [with size %-number%] [(named|with (name|title)) %-string%]",
				"virtual (1¦(crafting [table]|workbench)|2¦chest|3¦anvil|4¦hopper|5¦dropper|6¦dispenser|%-inventorytype%) [with %-number% row[s]] [(named|with (name|title)) %-string%]",
				"virtual (1¦(crafting [table]|workbench)|2¦chest|3¦anvil|4¦hopper|5¦dropper|6¦dispenser|%-inventorytype%) [(named|with (name|title)) %-string%] with size %-number%",
				"virtual (1¦(crafting [table]|workbench)|2¦chest|3¦anvil|4¦hopper|5¦dropper|6¦dispenser|%-inventorytype%) [(named|with (name|title)) %-string%] with %-number% row[s]"
		);
	}

	@Nullable
	private InventoryType specifiedType;
	@Nullable
	private Expression<InventoryType> inventoryType;
	@Nullable
	private Expression<Number> rows;
	@Nullable
	private Expression<String> name;

	// The name of this inventory.
	@Nullable
	private String invName;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, ParseResult parseResult) {
		inventoryType = (Expression<InventoryType>) exprs[0];
		if (inventoryType == null) { // They must be using a specific one
			switch (parseResult.mark) {
				case 1:
					specifiedType = InventoryType.WORKBENCH;
					break;
				case 2:
					specifiedType = InventoryType.CHEST;
					break;
				case 3:
					specifiedType = InventoryType.ANVIL;
					break;
				case 4:
					specifiedType = InventoryType.HOPPER;
					break;
				case 5:
					specifiedType = InventoryType.DROPPER;
					break;
				case 6:
					specifiedType = InventoryType.DISPENSER;
					break;
			}
		}

		if (matchedPattern > 1) {
			name = (Expression<String>) exprs[1];
			rows = (Expression<Number>) exprs[2];
		} else {
			name = (Expression<String>) exprs[2];
			rows = (Expression<Number>) exprs[1];
		}

		return true;
	}

	@Override
	protected Inventory[] get(Event e) {
		InventoryType type = inventoryType != null ? inventoryType.getSingle(e) : specifiedType;
		if (type == null) {
			return new Inventory[0];
		} else if (type == InventoryType.CRAFTING) { // Make it a valid inventory. It's not the same, but it's likely what the user wants.
			type = InventoryType.WORKBENCH;
		}

		String name = this.name != null ? this.name.getSingle(e) : null;
		invName = name != null ? name : type.getDefaultTitle();

		Inventory inventory;
		if (type == InventoryType.CHEST) {
			int size = -1;
			if (rows != null) {
				Number rows = this.rows.getSingle(e);
				if (rows != null) {
					size = rows.intValue();
					if (size <= 6) {
						size *= 9;
					}
				}
			}
			if (size == 0 || size % 9 != 0) { // Invalid inventory size
				size = type.getDefaultSize();
			}
			inventory = Bukkit.getServer().createInventory(null, size, invName);
		} else {
			inventory = Bukkit.getServer().createInventory(null, type, invName);
		}

		return new Inventory[]{inventory};
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
	public String toString(@Nullable Event e, boolean debug) {
		return "virtual " + (inventoryType != null ? inventoryType.toString(e, debug) : specifiedType != null ? specifiedType.name().toLowerCase() : "unknown inventory type")
			+ (name != null ? " with name" + name.toString(e, debug) : "")
			+ (rows != null ? " with " + rows.toString(e, debug) + " rows" : "");
	}

	/**
	 * @return The name of this inventory. If {@link #invName} is null
	 * when this method is called, an empty string will be returned.
	 */
	public String getName() {
		return invName != null ? invName : "";
	}

}
