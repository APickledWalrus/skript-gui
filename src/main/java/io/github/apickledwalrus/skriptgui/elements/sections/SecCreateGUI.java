package io.github.apickledwalrus.skriptgui.elements.sections;

import io.github.apickledwalrus.skriptgui.elements.expressions.ExprVirtualInventory;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

import io.github.apickledwalrus.skriptgui.SkriptGUI;
import io.github.apickledwalrus.skriptgui.gui.GUI;
import io.github.apickledwalrus.skriptgui.gui.SkriptGUIEvent;
import io.github.apickledwalrus.skriptgui.gui.GUI.ShapeMode;
import io.github.apickledwalrus.skriptgui.util.EffectSection;

@Name("Create / Edit GUI")
@Description("The base of creating and editing GUIs.")
@Examples({"create a gui with virtual chest inventory with 3 rows named \"My GUI\"",
			"edit gui last gui:",
			"\tset the gui-inventory-name to \"New GUI Name!\"",
})
@Since("1.0.0")
public class SecCreateGUI extends EffectSection {

	static {
		Skript.registerCondition(SecCreateGUI.class,
				"create [a] [new] gui [[with id] %-string%] with %inventory% [(1¦(and|with) (moveable|stealable) items)] [(and|with) shape %-strings%]",
				"(change|edit) [gui] %guiinventory%"
		);
	}

	private Expression<GUI> exprGUI;
	private Expression<Inventory> inv;
	private Expression<String> shape, id;

	private boolean moveableItems;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, ParseResult parseResult) {
		if (checkIfCondition())
			return false;

		if (matchedPattern == 1) {
			if (!hasSection()) {
				Skript.error("You can't edit a gui inventory using an empty section, you need to change at least a slot or a property.");
				return false;
			}
			exprGUI = (Expression<GUI>) exprs[0];
		} else {
			id = (Expression<String>) exprs[0];
			inv = (Expression<Inventory>) exprs[1];
			shape = (Expression<String>) exprs[2];
			moveableItems = parseResult.mark == 1;
		}

		if (hasSection())
			loadSection(true);

		// Just a safe check, to make sure the listener was registered when this is loaded
		SkriptGUIEvent.getInstance().register();

		return true;
	}

	@Override
	public void execute(Event e) {
		if (exprGUI == null) { // Creating a new GUI.
			Inventory inv = this.inv.getSingle(e);
			if (inv != null) {

				GUI gui;
				if (this.inv instanceof ExprVirtualInventory) { // Try to set the name
					gui = new GUI(inv, moveableItems, ((ExprVirtualInventory) this.inv).getName());
				} else {
					gui = new GUI(inv, moveableItems);
				}

				if (shape == null) {
					gui.setShape(true, null);
				} else {
					gui.setShape(false, ShapeMode.ACTIONS, shape.getArray(e));
				}

				String id = this.id != null ? this.id.getSingle(e) : null;
				if (id != null && !id.isEmpty())
					SkriptGUI.getGUIManager().addGlobalGUI(id, gui);

				SkriptGUI.getGUIManager().setGUIEvent(e, gui);
			}
		} else { // Editing the given GUI.
			GUI gui = exprGUI.getSingle(e);
			SkriptGUI.getGUIManager().setGUIEvent(e, gui);
		}

		if (hasSection())
			runSection(e);

	}

	@Override
	public String toString(Event e, boolean debug) {
		if (exprGUI != null)
			return "edit GUI " + exprGUI.toString(e, debug);
		return "create gui";
	}

}
