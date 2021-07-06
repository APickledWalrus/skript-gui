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
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Name("GUI Open/Close")
@Description("Sections that will run when a user opens or closes the GUI. This section is optional.")
@Examples({
		"create a gui with virtual chest inventory with 3 rows named \"My GUI\"",
		"\trun on gui open:",
		"\t\tsend \"You just opened this GUI!\" to player",
		"\trun on gui close:",
		"\t\tsend \"You just closed this GUI!\" to player"
})
@Since("1.0.0, 1.3 (open section)")
public class SecGUIOpenClose extends Section {

	static {
		Skript.registerSection(SecGUIOpenClose.class,
				"run (when|while) (open[ing]|1¦clos(e|ing)) [[the] gui]",
				"run (when|while) [the] gui (opens|1¦closes)",
				"run on gui (open[ing]|1¦clos(e|ing))"
		);
	}

	private Trigger trigger;

	private boolean close;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
		if (!getParser().isCurrentSection(SecCreateGUI.class)) {
			Skript.error("GUI open/close sections can only be put within GUI creation or editing sections.");
			return false;
		}

		close = parseResult.mark == 1;

		if (close) {
			trigger = loadCode(sectionNode, "inventory close", InventoryCloseEvent.class);
		} else {
			trigger = loadCode(sectionNode, "inventory open", InventoryOpenEvent.class);
		}

		return true;
	}

	@Override
	public TriggerItem walk(Event e) {
		GUI gui = SkriptGUI.getGUIManager().getGUIEvent(e);
		if (gui != null) {
			Object variables = Variables.copyLocalVariables(e);
			if (close) {
				if (variables != null) {
					gui.setOnClose(event -> {
						Variables.setLocalVariables(event, variables);
						trigger.execute(event);
					});
				} else {
					gui.setOnClose(trigger::execute);
				}
			} else {
				if (variables != null) {
					gui.setOnOpen(event -> {
						Variables.setLocalVariables(event, variables);
						trigger.execute(event);
					});
				} else {
					gui.setOnOpen(trigger::execute);
				}
			}
		}

		// We don't want to execute this section
		return getNext();
	}

	@Override
	@NotNull
	public String toString(@Nullable Event e, boolean debug) {
		return "run on gui " + (close ? "close" : "open");
	}

}
