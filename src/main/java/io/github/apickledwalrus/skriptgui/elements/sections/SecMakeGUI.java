package io.github.apickledwalrus.skriptgui.elements.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.EffectSection;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import io.github.apickledwalrus.skriptgui.SkriptGUI;
import io.github.apickledwalrus.skriptgui.SkriptUtils;
import io.github.apickledwalrus.skriptgui.gui.GUI;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Name("Set GUI Slots")
@Description("Set or clear GUI slots.")
@Examples({"create a gui with virtual chest inventory with 3 rows named \"My GUI\"",
			"\tmake next gui with dirt # Formats the next available GUI slot with dirt. Doesn't do anything when clicked on.",
			"\tmake gui 10 with water bucket:",
			"\t\t#code here is run when the gui slot is clicked",
			"\tunformat gui 10 # Removes the GUI item at slot 10",
			"\tunformat the next gui # Removes the GUI item at the slot before the next available slot."
})
@Since("1.0.0, 1.2.0 (making specific slots stealable)")
public class SecMakeGUI extends EffectSection {

	static {
		Skript.registerSection(SecMakeGUI.class,
				"(make|format) [the] next gui [slot] (with|to) [removable:([re]mov[e]able|stealable)] %itemtype%",
				"(make|format) gui [slot[s]] %strings/numbers% (with|to) [removable:([re]mov[e]able|stealable)] %itemtype%",
				"(un(make|format)|remove) [the] next gui [slot]",
				"(un(make|format)|remove) gui [slot[s]] %strings/numbers%",
				"(un(make|format)|remove) all [[of] the] gui [slots]"
		);
	}

	private @Nullable Trigger trigger;

	private @Nullable Expression<Object> slots; // Can be number or a string
	private @Nullable Expression<ItemType> item;

	private int pattern;
	private boolean removable;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, ParseResult parseResult,
						@Nullable SectionNode sectionNode, @Nullable List<TriggerItem> items) {
		if (!SkriptUtils.isSection(SecCreateGUI.class, SecMakeGUI.class, SecGUIOpenClose.class)) {
			Skript.error("You can't make a GUI slot outside of a GUI creation or editing section.");
			return false;
		}

		pattern = matchedPattern;
		if (matchedPattern < 2) {
			item = (Expression<ItemType>) exprs[matchedPattern];
		}
		if (matchedPattern == 1 || matchedPattern == 3) {
			slots = (Expression<Object>) exprs[0];
		}

		removable = parseResult.hasTag("removable");

		if (hasSection()) {
			assert sectionNode != null;
			trigger = loadCode(sectionNode, "inventory click", InventoryClickEvent.class);
		}

		return true;
	}

	@Override
	public @Nullable TriggerItem walk(Event event) {
		GUI gui = SkriptGUI.getGUIManager().getGUI(event);

		if (gui == null) { // We aren't going to do anything with this section
			return walk(event, false);
		}

		switch (pattern) {
			case 0: // Set the next slot
			case 1: // Set the input slots
				assert item != null;
				ItemType itemType = item.getSingle(event);
				if (itemType == null)
					break;
				ItemStack item = itemType.getRandom();
				if (hasSection()) {
					assert trigger != null;
					Object variables = Variables.copyLocalVariables(event);
					if (variables != null) {
						for (Object slot : slots != null ? slots.getArray(event) : new Object[]{gui.nextSlot()}) {
							gui.setItem(slot, item, removable, clickEvent -> {
								Variables.setLocalVariables(clickEvent, variables);
								trigger.execute(clickEvent);
							});
						}
					} else { // Don't paste variables if there are none to paste
						for (Object slot : slots != null ? slots.getArray(event) : new Object[]{gui.nextSlot()}) {
							gui.setItem(slot, item, removable, trigger::execute);
						}
					}
				} else {
					for (Object slot : slots != null ? slots.getArray(event) : new Object[]{gui.nextSlot()}) {
						gui.setItem(slot, item, removable, null);
					}
				}
				break;
			case 2: // Clear the next slot
				gui.clear(gui.nextSlotInverted());
				break;
			case 3: // Clear the input slots
				assert slots != null;
				for (Object slot : slots.getArray(event)) {
					gui.clear(slot);
				}
				break;
			case 4: // Clear all slots
				gui.clear();
				break;
		}

		// We don't want to execute this section
		return walk(event, false);
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		switch (pattern) {
			case 0:
				assert item != null;
				return "make next gui slot with " + item.toString(event, debug);
			case 1:
				assert slots != null && item != null;
				return "make gui slot(s) " + slots.toString(event, debug) + " with " + item.toString(event, debug);
			case 2:
				return "remove the next gui slot";
			case 3:
				assert slots != null;
				return "remove gui slot(s) " + slots.toString(event, debug);
			case 4:
				return "remove all of the gui slots";
			default:
				return "make gui";
		}
	}

}
