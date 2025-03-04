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
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import io.github.apickledwalrus.skriptgui.SkriptGUI;
import io.github.apickledwalrus.skriptgui.elements.expressions.ExprVirtualInventory;
import io.github.apickledwalrus.skriptgui.gui.GUI;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

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
				"create [a] [new] gui [[with id[entifier]] %-string%] with %inventory% [removable:(and|with) ([re]move[e]able|stealable) items] [(and|with) shape %-strings%]",
				"(change|edit) [gui] %guiinventory%"
		);
	}

	private Expression<Inventory> inventory;
	private @Nullable Expression<String> shape;
	private @Nullable Expression<String> id;
	private boolean removableItems;

	private @Nullable Expression<GUI> gui;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, ParseResult parseResult,
						@Nullable SectionNode sectionNode, @Nullable List<TriggerItem> triggerItems) {
		if (matchedPattern == 1) {
			if (sectionNode == null) {
				Skript.error("An 'edit gui' line must have a section (i.e. change something)");
				return false;
			}
			gui = (Expression<GUI>) exprs[0];
		} else {
			id = (Expression<String>) exprs[0];
			inventory = (Expression<Inventory>) exprs[1];
			shape = (Expression<String>) exprs[2];
			removableItems = parseResult.hasTag("removable");
		}

		if (sectionNode != null) {
			loadOptionalCode(sectionNode);
		}

		return true;
	}

	@Override
	@Nullable
	public TriggerItem walk(Event event) {
		GUI gui;
		if (this.gui == null) { // Creating a new GUI.
			Inventory inv = this.inventory.getSingle(event);
			if (inv == null) { // Don't run the section if the GUI can't be created
				return walk(event, false);
			}

			InventoryType invType = inv.getType();
			if (!invType.isCreatable()) { // We don't want to run this section as this is an invalid GUI type
				SkriptGUI.getInstance().getLogger().warning("Unable to create an inventory of type: " + invType.name());
				return walk(event, false);
			}

			if (this.inventory instanceof ExprVirtualInventory exprVirtualInventory) { // Try to set the name
				gui = new GUI(inv, removableItems, exprVirtualInventory.getName());
			} else {
				gui = new GUI(inv, removableItems, null);
			}

			if (shape == null) {
				gui.resetShape();
			} else {
				gui.setShape(shape.getArray(event));
			}

			String id = this.id != null ? this.id.getSingle(event) : null;
			if (id != null && !id.isEmpty()) {
				GUI old = SkriptGUI.getGUIManager().getGUI(id);
				if (old != null) { // We are making a new GUI with this ID (see https://github.com/APickledWalrus/skript-gui/issues/72)
					SkriptGUI.getGUIManager().unregister(old);
				}
				gui.setID(id);
			}

		} else { // Editing the given GUI
			gui = this.gui.getSingle(event);
			if (gui == null) { // can't edit a GUI that doesn't exist
				return walk(event, false);
			}
		}

		// We need to switch the event GUI for the creation section if nesting has occurred
		GUI currentGUI = SkriptGUI.getGUIManager().getGUI(event);

		if (currentGUI == null) { // No nesting, treat as normal
			SkriptGUI.getGUIManager().setGUI(event, gui);
			return walk(event, true);
		}

		if (!hasSection()) { // No section to run, we can skip the code below (no code to run with "new" gui)
			return walk(event, false);
		}

		SkriptGUI.getGUIManager().setGUI(event, gui);

		assert first != null && last != null;
		TriggerItem lastNext = last.getNext();
		last.setNext(null);
		TriggerItem.walk(first, event);
		last.setNext(lastNext);

		// Switch back to the old GUI since we are returning to the previous GUI section
		// TODO the downside here is that "open last gui" may not always work as expected!
		// Unsurprisingly, creation section nesting is annoying!
		SkriptGUI.getGUIManager().setGUI(event, currentGUI);

		// Don't run the section, we ran it above if needed
		return walk(event, false);
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		if (gui != null) {
			return "edit gui " + gui.toString(event, debug);
		}

		SyntaxStringBuilder builder = new SyntaxStringBuilder(event, debug);
		builder.append("create a gui");
		if (id != null) {
			builder.append("with id", id);
		}
		builder.append("with").append(inventory);
		if (removableItems) {
			builder.append("with removable items");
		}
		if (shape != null) {
			builder.append("and shape", shape);
		}

		return builder.toString();
	}

}
