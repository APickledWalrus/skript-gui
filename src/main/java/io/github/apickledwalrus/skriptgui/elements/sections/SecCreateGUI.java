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
				"create [a] [new] gui [[with id[entifier]] %-string%] with %inventory% [(removable:(and|with) ([re]moveable|stealable) items)] [(and|with) shape %-strings%]",
				"(change|edit) [gui] %guiinventory%"
		);
	}

	@SuppressWarnings("NotNullFieldNotInitialized")
	private Expression<Inventory> inv;
	@Nullable
	private Expression<String> shape, id;
	private boolean removableItems;

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
			removableItems = parseResult.hasTag("removable");
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
		GUI gui;
		if (this.gui == null) { // Creating a new GUI.
			Inventory inv = this.inv.getSingle(e);
			if (inv != null) {

				InventoryType invType = inv.getType();
				if (invType == InventoryType.CRAFTING || invType == InventoryType.PLAYER) { // We don't want to run this section as this is an invalid GUI type
					SkriptGUI.getInstance().getLogger().warning("Unable to create an inventory of type: " + invType.name());
					return walk(e, false);
				}

				if (this.inv instanceof ExprVirtualInventory) { // Try to set the name
					gui = new GUI(inv, removableItems, ((ExprVirtualInventory) this.inv).getName());
				} else {
					gui = new GUI(inv, removableItems, null);
				}

				if (shape == null) {
					gui.resetShape();
				} else {
					gui.setShape(shape.getArray(e));
				}

				String id = this.id != null ? this.id.getSingle(e) : null;
				if (id != null && !id.isEmpty()) {
					GUI old = SkriptGUI.getGUIManager().getGUI(id);
					if (old != null) { // We are making a new GUI with this ID (see https://github.com/APickledWalrus/skript-gui/issues/72)
						SkriptGUI.getGUIManager().unregister(old);
					}
					gui.setID(id);
				}
			} else {
				return walk(e, false); // Don't run the section if the GUI can't be created
			}

		} else { // Editing the given GUI
			gui = this.gui.getSingle(e);
		}

		if (!(hasSection())) { // Don't bother updating the "current" event GUI - we'd end up switching right back to the old one
			return walk(e, false);
		}

		// We need to switch the event GUI for the creation section
		GUI currentGUI = SkriptGUI.getGUIManager().getGUI(e);

		SkriptGUI.getGUIManager().setGUI(e, gui);
		if (currentGUI == null) { // We're not within another creation section
			return walk(e, true);
		}

		assert first != null && last != null;
		TriggerItem lastNext = last.getNext();
		last.setNext(null);
		TriggerItem.walk(first, e);
		last.setNext(lastNext);

		// Switch back to the old GUI since we are returning to the previous GUI section
		// TODO the downside here is that "open last gui" may not work as expected!
		// Unsurprisingly, creation section inception is annoying!
		SkriptGUI.getGUIManager().setGUI(e, currentGUI);

		// Don't run the section, we ran it above if needed
		return walk(e, false);
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
			if (removableItems) {
				creation.append(" with removable items");
			}
			if (shape != null) {
				creation.append(" and shape ").append(shape.toString(e, debug));
			}
			return creation.toString();
		}
	}

}
