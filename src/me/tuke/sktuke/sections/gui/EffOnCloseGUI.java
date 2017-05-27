package me.tuke.sktuke.sections.gui;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import me.tuke.sktuke.util.EffectSection;
import me.tuke.sktuke.util.Registry;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 * @author Tuke_Nuke on 23/04/2017
 */
@Name("Close GUI")
@Description(
		"It should be used in {{effects|CreateEditGUI|creating or editting a gui}}, it is just an extra option " +
		"to run a code before it closes. This is optinal."
)
@Examples({
		"create new gui with id \"Backpack.%player%\" with virtual chest:",
		"\trun when close:",
		"\t\tsaveInventoryItems(player, gui-inventory)",
		"open last gui to player"
})
@Since("1.7.5")
public class EffOnCloseGUI extends EffectSection {
	static {
		Registry.newEffect(EffOnCloseGUI.class, "run (when|while) clos(e|ing) [[the] gui]");
	}

	private EffCreateGUI currentSection;
	@Override
	public void execute(Event e) {
		if (hasSection())
			currentSection.gui.onClose(this::runSection);
	}

	@Override
	public String toString(Event event, boolean b) {
		return "run when close";
	}

	@Override
	public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
		if (checkIfCondition()) {
			return false;
		}
		if (EffCreateGUI.lastInstance == null) {
			Skript.error("You can't make a gui close action outside of 'create/edit gui' effect.");
			return false;
		}
		if (!hasSection()) {
			Skript.error("An empty action can't be executed when the gui is closing.");
			return false;
		}
		currentSection = EffCreateGUI.lastInstance;
		loadSection("gui close", InventoryCloseEvent.class);
		return true;
	}
}
