package io.github.apickledwalrus.skriptgui.elements.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
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
import net.kyori.adventure.text.Component;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.List;

@Name("Create/Edit GUI")
@Description("Creates a new GUI or edits an existing one.")
@Example("""
	create a gui with virtual chest inventory with 3 rows named "My GUI"
	""")
@Example("""
	create a gui with a virtual chest inventory with shape "xxxxxxxxx", "x-------x", and "xxxxxxxxx"
	""")
@Example("""
	edit the player's gui:
		make the next gui slot with a slime block named "Don't Touch!"
	""")
@Since("1.0.0")
public class SecCreateGUI extends EffectSection {

	public static void register(SyntaxRegistry syntaxRegistry) {
		syntaxRegistry.register(SyntaxRegistry.SECTION, SyntaxInfo.builder(SecCreateGUI.class)
			.supplier(SecCreateGUI::new)
			.addPatterns("create [a] [new] gui [[with id[entifier]] %-string%] with [a] %inventory% [removable:(and|with) ([re]mov[e]able|stealable) items] [(and|with) shape %-strings%]",
				"(change|edit) [gui] %guiinventory%")
			.build());
	}

	private @Nullable Expression<String> id;
	private Expression<Inventory> inventory;
	private boolean removableItems;
	private @Nullable Expression<String> shape;

	private @Nullable Expression<GUI> gui;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean kleenean, ParseResult parseResult,
						@Nullable SectionNode sectionNode, @Nullable List<TriggerItem> triggerItems) {
		if (matchedPattern == 1) {
			if (sectionNode == null) {
				Skript.error("'edit gui' can only be used as a section!");
				return false;
			}
			gui = (Expression<GUI>) expressions[0];
		} else {
			id = (Expression<String>) expressions[0];
			inventory = (Expression<Inventory>) expressions[1];
			shape = (Expression<String>) expressions[2];
			removableItems = parseResult.hasTag("removable");
		}

		if (sectionNode != null) {
			loadOptionalCode(sectionNode);
		}

		return true;
	}

	@Override
	public @Nullable TriggerItem walk(Event event) {
		GUI gui;
		if (this.gui == null) { // Creating a new GUI
			Inventory inventory = this.inventory.getSingle(event);
			if (inventory == null) { // Don't run the section if the GUI can't be created
				return walk(event, false);
			}
			if (SkriptGUI.getGUIManager().getGUI(inventory) != null) {
				error("Attempted to create a GUI using an inventory already associated with a GUI! This is not permitted.");
				return walk(event, false);
			}

			Component name = null;
			if (this.inventory instanceof ExprVirtualInventory exprVirtualInventory) {
				name = exprVirtualInventory.getName();
			}

			gui = new GUI(inventory, removableItems, name, shape == null ? null : shape.getArray(event));

			String id = this.id == null ? null : this.id.getSingle(event);
			if (id != null && !id.isEmpty()) {
				GUI old = SkriptGUI.getGUIManager().getGUI(id);
				if (old != null) { // We are making a new GUI with this ID (see https://github.com/APickledWalrus/skript-gui/issues/72)
					SkriptGUI.getGUIManager().unregister(old);
				}
				gui.setID(id);
			}
		} else { // Editing a GUI
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
		return new SyntaxStringBuilder(event, debug)
			.append("create a gui")
			.appendIf(id != null, "with id", id)
			.append("with", inventory)
			.appendIf(removableItems, "with removable items")
			.appendIf(shape != null, "and shape", shape)
			.toString();
	}

}
