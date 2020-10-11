package io.github.apickledwalrus.skriptgui.elements.sections;

import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

import io.github.apickledwalrus.skriptgui.SkriptGUI;
import io.github.apickledwalrus.skriptgui.gui.GUI;
import io.github.apickledwalrus.skriptgui.util.EffectSection;
import io.github.apickledwalrus.skriptgui.util.VariableUtils;

@Name("Set GUI Slots")
@Description("Set or clear GUI slots.")
@Examples({"create a gui with virtual chest inventory with 3 rows named \"My GUI\"",
			"\tmake gui next gui with dirt # Formats the next available GUI slot with dirt. Doesn't do anything when clicked on.",
			"\tmake gui 10 with water bucket:",
			"\t\t#code here is run when the gui slot is clicked",
			"\tunformat gui 10 # Removes the GUI item at slot 10",
			"\tunformat the next gui # Removes the GUI item at the slot before the next available slot."
})
@Since("1.0.0, 1.2.0 (making specific slots stealable)")
public class SecMakeGUI extends EffectSection {

	static {
		Skript.registerCondition(SecMakeGUI.class,
				"(make|format) [the] next gui [slot] (with|to) [(1¦(moveable|stealable))] %itemtype%",
				"(make|format) gui [slot[s]] %strings/numbers% (with|to) [(1¦(moveable|stealable))] %itemtype%",
				"(un(make|format)|remove) [the] next gui [slot]",
				"(un(make|format)|remove) gui [slot[s]] %strings/numbers%",
				"(un(make|format)|remove) all [of the] gui [slots]"
		);
	}

	public static SecMakeGUI lastInstance = null;

	private Expression<Object> slots; // Can be number or a string
	private Expression<ItemType> item;

	private int pattern;
	private boolean stealable;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, ParseResult parseResult) {
		if (checkIfCondition())
			return false;

		if (!isCurrentSection(SecCreateGUI.class)) {
			Skript.error("You can't make a GUI slot outside of a GUI creation or editing section.");
			return false;
		}

		pattern = matchedPattern;
		if (matchedPattern < 2)
			item = (Expression<ItemType>) exprs[matchedPattern];
		if (matchedPattern == 1 || matchedPattern == 3)
			slots = (Expression<Object>) exprs[0];

		stealable = parseResult.mark == 1;

		if (hasSection())
			loadSection("gui effect", false, InventoryClickEvent.class);

		return true;
	}

	@Override
	public void execute(Event e) {

		GUI gui = SkriptGUI.getGUIManager().getGUIEvent(e);

		if (gui == null)
			return;

		switch (pattern) {
			case 0: // Set the next slot
			case 1: // Set the input slots
				ItemType itemType = this.item.getSingle(e);
				if (itemType == null)
					break;
				ItemStack item = itemType.getRandom();
				for (Object slot : slots != null ? slots.getArray(e) : new Object[]{gui.nextSlot()}) {
					if (hasSection()) {
						final Object variables = VariableUtils.getInstance().copyVariables(e);
						gui.setItem(slot, item, stealable, event -> {
							VariableUtils.getInstance().pasteVariables(event, variables);
							SkriptGUI.getGUIManager().setGUIEvent(event, gui);
							runSection(event);
						});
					} else {
						gui.setItem(slot, item, stealable, null);
					}
				}
				break;
			case 2: // Clear the next slot
				gui.clearSlots(gui.nextInvertedSlot());
				break;
			case 3: // Clear the input slots
				gui.clearSlots(slots.getArray(e));
				break;
			case 4: // Clear all slots
				gui.clear();
				break;
		}
	}

	@Override
	public String toString(Event e, boolean debug) {
		switch (pattern) {
			case 0:
				return "make next gui slot with " + item.toString(e, debug);
			case 1:
				return "make gui slot(s) " + slots.toString(e, debug) + " with " + item.toString(e, debug);
			case 2:
				return "remove the next gui slot";
			case 3:
				return "remove gui slot(s) " + slots.toString(e, debug);
			case 4:
				return "remove all of the gui slots";
			default:
				return "make gui";
		}
	}
}
