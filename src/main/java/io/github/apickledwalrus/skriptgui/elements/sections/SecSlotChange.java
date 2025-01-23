package io.github.apickledwalrus.skriptgui.elements.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import io.github.apickledwalrus.skriptgui.SkriptGUI;
import io.github.apickledwalrus.skriptgui.gui.GUI;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Name("GUI Slot Change")
@Description("Sections that will run when a gui slot is changed. This section is optional.")
@Examples({
		"create a gui with virtual chest inventory with 3 rows named \"My GUI\"",
		"\trun when slot 12 changes:",
		"\t\tsend \"You changed slot 12!\" to player",
		"\trun on slot 14 changed:",
		"\t\tcancel event"
})
@Since("1.3")
public class SecSlotChange extends Section {

	static {
		Skript.registerSection(SecSlotChange.class,
				"run when [gui] slot[s] %integers% change[s]",
				"run when [gui] slot[s] %integers% [(are|is)] [being] changed",
				"run on change of [gui] slot[s] %integers%"
		);
	}

	private Trigger trigger;
	private Expression<Integer> guiSlots;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult,
						SectionNode sectionNode, List<TriggerItem> triggerItems) {
		if (!getParser().isCurrentSection(SecCreateGUI.class)) {
			Skript.error("You can't listen for changes of a slot outside of a GUI creation or editing section.");
			return false;
		}

		trigger = loadCode(sectionNode, "inventory click", InventoryClickEvent.class);
		guiSlots = (Expression<Integer>) exprs[0];

		return true;
	}

	@Override
	@Nullable
	public TriggerItem walk(Event event) {
		GUI gui = SkriptGUI.getGUIManager().getGUI(event);
		if (gui == null)
			return walk(event, false);

		Integer[] slots = guiSlots.getAll(event);

		for (Integer slot : slots) {
			if (slot >= 0 && slot + 1 <= gui.getInventory().getSize()) {
				Object variables = Variables.copyLocalVariables(event);
				GUI.SlotData slotData = gui.getSlotData(gui.convert(slot));
				if (slotData == null) {
					continue;
				}
				if (variables != null) {
					slotData.setRunOnChange(clickEvent -> {
						Variables.setLocalVariables(clickEvent, variables);
						trigger.execute(clickEvent);
					});
				} else {
					slotData.setRunOnChange(trigger::execute);
				}
			}
		}

		// We don't want to execute this section
		return walk(event, false);
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "run on change of slot " + guiSlots.toString(event, debug);
	}

}
