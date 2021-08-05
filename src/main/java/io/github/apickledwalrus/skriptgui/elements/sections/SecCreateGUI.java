package io.github.apickledwalrus.skriptgui.elements.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.EffectSection;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import io.github.apickledwalrus.skriptgui.SkriptGUI;
import io.github.apickledwalrus.skriptgui.elements.expressions.ExprVirtualInventory;
import io.github.apickledwalrus.skriptgui.gui.GUI;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.eclipse.jdt.annotation.Nullable;

import java.util.List;

@Name("Create / Edit GUI")
@Description("The base of creating and editing GUIs.")
@Examples({
		"create a gui with virtual chest inventory with 3 rows named \"My GUI\"",
		"edit gui last gui:",
		"\tset the gui-inventory-name to \"New GUI Name!\"",
})
@Since("1.0.0")
public class SecCreateGUI extends EffectSection {

	static {
		Skript.registerSection(SecCreateGUI.class,
				"create [a] [new] gui [[with id[entifier]] %-string%] with %inventory% [(1¦(and|with) (moveable|stealable) items)] [(and|with) shape %-strings%]",
				"(change|edit) [gui] %guiinventory%"
		);
	}

	@SuppressWarnings("NotNullFieldNotInitialized")
	private Expression<Inventory> inv;
	@Nullable
	private Expression<String> shape, id;
	private boolean stealableItems;

	@Nullable
	private Expression<GUI> gui;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, ParseResult parseResult, @Nullable SectionNode sectionNode, @Nullable List<TriggerItem> triggerItems) {
		if (matchedPattern == 1) {
			if (!hasSection()) {
				Skript.error("You can't edit a gui inventory using an empty section, you need to change at least a slot or a property.");
				return false;
			}
			gui = (Expression<GUI>) exprs[0];
		} else {
			id = (Expression<String>) exprs[0];
			inv = (Expression<Inventory>) exprs[1];
			shape = (Expression<String>) exprs[2];
			stealableItems = parseResult.mark == 1;
		}

		if (hasSection()) {
			assert sectionNode != null;
			loadOptionalCode(sectionNode);
		}

		return true;
	}

	@Override
	@Nullable
	public TriggerItem walk(Event e) {
		if (gui == null) { // Creating a new GUI.
			Inventory inv = this.inv.getSingle(e);
			if (inv != null) {

				InventoryType invType = inv.getType();
				if (invType == InventoryType.CRAFTING || invType == InventoryType.PLAYER) { // We don't want to run this section as this is an invalid GUI type
					SkriptGUI.getInstance().getLogger().warning("Unable to create an inventory of type: " + invType.name());
					return walk(e, false);
				}

				GUI gui;
				if (this.inv instanceof ExprVirtualInventory) { // Try to set the name
					gui = new GUI(inv, stealableItems, ((ExprVirtualInventory) this.inv).getName());
				} else {
					gui = new GUI(inv, stealableItems, null);
				}

				if (shape == null) {
					gui.resetShape();
				} else {
					gui.setShape(shape.getArray(e));
				}

				String id = this.id != null ? this.id.getSingle(e) : null;
				if (id != null && !id.isEmpty()) {
					gui.setID(id);
				}

				SkriptGUI.getGUIManager().setGUI(e, gui);
			}
		} else { // Editing the given GUI.
			GUI gui = this.gui.getSingle(e);
			SkriptGUI.getGUIManager().setGUI(e, gui);
		}

		// 'first' will be null if no section is present
		return walk(e, true);
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		if (gui != null) {
			return "edit gui " + gui.toString(e, debug);
		} else {
			StringBuilder creation = new StringBuilder("create a gui");
			if (id != null) {
				creation.append(" with id ").append(id.toString(e, debug));
			}
			creation.append(" with ").append(inv.toString(e, debug));
			if (stealableItems) {
				creation.append(" with stealable items");
			}
			if (shape != null) {
				creation.append(" and shape ").append(shape.toString(e, debug));
			}
			return creation.toString();
		}
	}

}
