package io.github.apickledwalrus.skriptgui.elements.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import io.github.apickledwalrus.skriptgui.SkriptGUI;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.experiment.ExperimentSet;
import org.skriptlang.skript.lang.experiment.ExperimentalSyntax;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Menu Inventory")
@Description("""
	Obtains a menu bound to a player that can be used for creating a GUI.
	This expression is not intended to be used outside of GUI creation.
	Be sure to show the GUI using the 'Show GUI' effect.
	""")
@Example("""
	using menu guis
	command /getinput:
		trigger:
			create a gui with anvil menu named "Enter Input" bound to player:
				make gui slot 0 with barrier named "Enter Input" # Far Left Slot
				make gui 2 with air: # Output Slot, formatted with air to just run a click action
					set {_input} to anvil text input of gui
					close the player's inventory
					send title "You Entered" with subtitle {_input} to player
			show the last gui to the player
	""")
@Since("1.4.0")
public class ExprMenuInventory extends SimpleExpression<Inventory> implements ExperimentalSyntax {

	public static void register(SyntaxRegistry syntaxRegistry) {
		String common = "%inventorytype% menu ";
		String suffix = "bound to %player%";
		syntaxRegistry.register(SyntaxRegistry.EXPRESSION,
			SyntaxInfo.Expression.builder(ExprMenuInventory.class, Inventory.class)
				.supplier(ExprMenuInventory::new)
				.addPatterns(common + " [with %-number% row[s]] [(named|with (name|title)) %-textcomponent%] " + suffix,
					common + " [(named|with (name|title)) %-textcomponent%] with %-number% row[s] " + suffix)
				.build());
	}

	private Expression<InventoryType> inventoryType;
	private @Nullable Expression<Number> rows;
	private @Nullable Expression<Component> name;
	private Expression<Player> player;

	/**
	 * Inventory view created during the most recent execution.
	 */
	public @Nullable InventoryView lastInventoryView;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, ParseResult parseResult) {
		inventoryType = (Expression<InventoryType>) exprs[0];
		if (matchedPattern == 0) {
			name = (Expression<Component>) exprs[2];
			rows = (Expression<Number>) exprs[1];
		} else {
			name = (Expression<Component>) exprs[1];
			rows = (Expression<Number>) exprs[2];
		}
		player = (Expression<Player>) exprs[3];
		return true;
	}

	@Override
	protected Inventory[] get(Event event) {
		lastInventoryView = null;

		InventoryType type = inventoryType.getSingle(event);
		if (type == null) {
			return new Inventory[0];
		} else if (type == InventoryType.CRAFTING) { // Make it a valid inventory. It's not the same, but it's likely what the user wants.
			type = InventoryType.WORKBENCH;
		} else if (!type.isCreatable()) {
			return new Inventory[0];
		}

		Player player = this.player.getSingle(event);
		if (player == null) {
			return new Inventory[0];
		}

		Component name = this.name != null ? this.name.getSingle(event) : null;
		name = name != null ? name : type.defaultTitle();

		MenuType menuType = type.getMenuType();
		assert menuType != null;
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
			menuType = switch (size) {
				case 9 -> MenuType.GENERIC_9X1;
				case 18 -> MenuType.GENERIC_9X2;
				case 27 -> MenuType.GENERIC_9X3;
				case 36 -> MenuType.GENERIC_9X4;
				case 45 -> MenuType.GENERIC_9X5;
				case 54 -> MenuType.GENERIC_9X6;
				default -> throw new IllegalStateException("Unexpected value: " + size);
			};
		}

		lastInventoryView = menuType.create(player, name);

		return new Inventory[]{lastInventoryView.getTopInventory()};
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
		return new SyntaxStringBuilder(event, debug)
			.append(inventoryType, "menu")
			.append("menu")
			.appendIf(name != null, "with name", name)
			.appendIf(rows != null, "with", rows, "rows")
			.append("bound to", player)
			.toString();
	}

	@Override
	public boolean isSatisfiedBy(ExperimentSet experimentSet) {
		return experimentSet.hasExperiment(SkriptGUI.MENU_EXPERIMENT);
	}

}
