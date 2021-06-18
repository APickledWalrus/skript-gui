package io.github.apickledwalrus.skriptgui.elements.sections;

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
import io.github.apickledwalrus.skriptgui.util.EffectSection;
import io.github.apickledwalrus.skriptgui.util.VariableUtils;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
public class SecGUIOpenClose extends EffectSection {

	static {
		Skript.registerCondition(SecGUIOpenClose.class,
				"run (when|while) (open[ing]|1¦clos(e|ing)) [[the] gui]",
				"run (when|while) [the] gui (opens|1¦closes)",
				"run on gui (open[ing]|1¦clos(e|ing))"
		);
	}

	private boolean close;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		if (checkIfCondition()) {
			return false;
		}

		if (!isCurrentSection(SecCreateGUI.class)) {
			Skript.error("GUI open/close sections can only be put within GUI creation or editing sections.");
			return false;
		}

		if (!hasSection()) {
			Skript.error("A GUI open/close section is pointless without any content! Make sure you put some.");
			return false;
		}

		close = parseResult.mark == 1;

		loadSection("gui close", false, InventoryCloseEvent.class);
		return true;
	}

	@Override
	public void execute(Event e) {
		if (hasSection()) {
			GUI gui = SkriptGUI.getGUIManager().getGUIEvent(e);
			if (gui != null) {
				Object variables = VariableUtils.getInstance().copyVariables(e);
				if (close) {
					if (variables != null) {
						gui.setOnClose(event -> {
							VariableUtils.getInstance().pasteVariables(event, variables);
							runSection(event);
						});
					} else {
						gui.setOnClose(this::runSection);
					}
				} else {
					if (variables != null) {
						gui.setOnOpen(event -> {
							VariableUtils.getInstance().pasteVariables(event, variables);
							runSection(event);
						});
					} else {
						gui.setOnOpen(this::runSection);
					}
				}
			}
		}
	}

	@Override
	@NotNull
	public String toString(@Nullable Event e, boolean debug) {
		return "run on gui " + (close ? "close" : "open");
	}

}
