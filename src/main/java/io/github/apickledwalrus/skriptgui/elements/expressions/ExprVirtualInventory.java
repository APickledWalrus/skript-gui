package io.github.apickledwalrus.skriptgui.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.Kleenean;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

@Name("Virtual Inventory")
@Description("An expression to create inventories that can be used with GUIs.")
@Examples("create a gui with virtual chest inventory with 3 rows named \"My GUI\"")
@Since("1.0.0")
public class ExprVirtualInventory extends SimpleExpression<Inventory>{

	static {
		String common = "virtual (1:(crafting [table]|workbench)|2:chest|3:anvil|4:hopper|5:dropper|6:dispenser|%-inventorytype%)";
		Skript.registerExpression(ExprVirtualInventory.class, Inventory.class, ExpressionType.COMBINED,
				common + " [with size %-number%] [(named|with (name|title)) %-string%]",
				common + " [with %-number% row[s]] [(named|with (name|title)) %-string%]",
				common + " [(named|with (name|title)) %-string%] with size %-number%",
				common + " [(named|with (name|title)) %-string%] with %-number% row[s]"
		);
	}

	private @Nullable InventoryType specifiedType;
	private @Nullable Expression<InventoryType> inventoryType;
	private @Nullable Expression<Number> rows;
	private @Nullable Expression<String> name;

	// The last executed name. Used for runtime context purposes.
	private @Nullable String lastInventoryName;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, ParseResult parseResult) {
		inventoryType = (Expression<InventoryType>) exprs[0];
		if (inventoryType == null) { // They must be using a specific one
			specifiedType = switch (parseResult.mark) {
				case 1 -> InventoryType.WORKBENCH;
				case 2 -> InventoryType.CHEST;
				case 3 -> InventoryType.ANVIL;
				case 4 -> InventoryType.HOPPER;
				case 5 -> InventoryType.DROPPER;
				case 6 -> InventoryType.DISPENSER;
				default -> throw new IllegalStateException("Unexpected value: " + parseResult.mark);
			};
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
	protected Inventory[] get(Event event) {
		InventoryType type = inventoryType != null ? inventoryType.getSingle(event) : specifiedType;
		if (type == null || !type.isCreatable()) {
			return new Inventory[0];
		} else if (type == InventoryType.CRAFTING) { // Make it a valid inventory. It's not the same, but it's likely what the user wants.
			type = InventoryType.WORKBENCH;
		}

		String name = this.name != null ? this.name.getSingle(event) : null;
		lastInventoryName = name != null ? name : type.getDefaultTitle();

		Inventory inventory;
		if (type == InventoryType.CHEST) {
			int size = -1;
			if (rows != null) {
				Number rows = this.rows.getSingle(event);
				if (rows != null) {
					size = rows.intValue();
					if (size <= 6) {
						size *= 9;
					}
				}
			}
			if (size < 9 || size > 54 || size % 9 != 0) { // Invalid inventory size
				size = type.getDefaultSize();
			}
			inventory = Bukkit.getServer().createInventory(null, size, lastInventoryName);
		} else {
			inventory = Bukkit.getServer().createInventory(null, type, lastInventoryName);
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
	public String toString(@Nullable Event event, boolean debug) {
		SyntaxStringBuilder builder = new SyntaxStringBuilder(event, debug);
		builder.append("virtual");

		if (inventoryType != null) {
			builder.append(inventoryType);
		} else if (specifiedType != null) {
			builder.append(Classes.toString(specifiedType));
		} else {
			builder.append("inventory");
		}

		if (name != null) {
			builder.append("with name", name);
		}

		if (rows != null) {
			builder.append("with", rows, "rows");
		}

		return builder.toString();
	}

	/**
	 * @return The name of this inventory. If {@link #lastInventoryName} is null
	 * when this method is called, an empty string will be returned.
	 */
	public String getName() {
		return lastInventoryName != null ? lastInventoryName : "";
	}

}
