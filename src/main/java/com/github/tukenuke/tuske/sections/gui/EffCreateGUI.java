package com.github.tukenuke.tuske.sections.gui;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.tukenuke.tuske.manager.gui.v2.GUIInventory;
import com.github.tukenuke.tuske.util.LazyEffectSection;
import com.github.tukenuke.tuske.manager.gui.v2.GUIHandler;
import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;

/**
 * @author Tuke_Nuke on 01/04/2017
 */
@Name("Create a GUI")
@Description({"It creates a new gui with a given id (optional), using a base inventory, and a shape (optional)",
"For more info, read the [here](https://github.com/Tuke-Nuke/TuSKe/wiki/GUI-Manager)"}) //TODO Set the link tutorial
@Examples({
		"on skript load:",
		"\tcreate a gui with id \"LobbySelector\" with virtual chest with 4 rows named \"&4Lobby Selector\":",
		"\t\tmake gui slot 2 with diamond sword named \"PVP\":",
		"\t\t\texecute player command \"/server pvp\" #'on skript load' event doesn't have a 'player', but it will recognize it as it does have.",
		"\t\tmake gui slot 4 with grass named \"SkyBlock\":",
		"\t\t\texecute player command \"/server skyblock\"",
		" ",
		"command /lobby:",
		"\ttrigger:",
		"\t\topen gui \"LobbySelector\" to player"})
@Since("1.7.5")
public class EffCreateGUI extends LazyEffectSection {
	static {
		Registry.newEffect(EffCreateGUI.class,
				"create [a] [new] gui [[with id] %-string%] with %inventory% [and shape %-strings%]",
				"(change|edit) %guiinventory%");
	}

	//public static EffCreateGUI lastInstance = null;
	//public GUIInventory gui = null;
	private Expression<GUIInventory> exprGui;
	private Expression<Inventory> inv;
	private Expression<String> str, id;
	@Override
	public void execute(Event e) {
		GUIHandler.getInstance().setGUIEvent(e, null);
		if (exprGui == null) { //It will create a new one
			Inventory inv = this.inv.getSingle(e);
			if (inv != null /*&& inv.getType() != InventoryType.PLAYER && inv.getType() != InventoryType.CRAFTING*/) {
				GUIInventory gui = new GUIInventory(inv);
				if (str != null)
					gui.shape(str.getArray(e));
				else
					gui.shapeDefault();
				String id = this.id != null ? this.id.getSingle(e) : null;
				if (id != null && !id.isEmpty())
					GUIHandler.getInstance().setGUI(id, gui);
				GUIHandler.getInstance().setGUIEvent(e, gui);
			}
		} else { //It will edit one
			GUIInventory gui = exprGui.getSingle(e);
			if (gui != null) {
				GUIHandler.getInstance().setGUIEvent(e, gui);
			}
		}
	}

	@Override
	public String toString(Event event, boolean b) {
		return "create gui";
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] arg, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
		if (checkIfCondition()) {
			return false;
		}
		if (i > 0) {
			if (!hasSection()) {
				Skript.error("You can't edit a gui inventory using an empty section, you need to change at least a slot or a property.");
				return false;
			}
			exprGui = (Expression<GUIInventory>) arg[0];
		} else {
			id = (Expression<String>) arg[0];
			inv = (Expression<Inventory>) arg[1];
			str = (Expression<String>) arg[2];
		}
		return true;
	}
}
