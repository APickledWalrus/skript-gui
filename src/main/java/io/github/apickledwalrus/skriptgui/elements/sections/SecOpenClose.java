package io.github.apickledwalrus.skriptgui.elements.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
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
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.List;

@Name("GUI Open/Close")
@Description("Enables running code when a player opens or closes their GUI.")
@Example("""
	create a gui with virtual chest inventory with 3 rows named "My GUI":
		run when the gui opens:
			send "You just opened this GUI!" to player
		run on close:
			send "You just closed this GUI!" to player
	""")
@Since("1.0.0, 1.3.0 (open section)")
public class SecOpenClose extends Section {

	public static void register(SyntaxRegistry syntaxRegistry) {
		syntaxRegistry.register(SyntaxRegistry.SECTION, SyntaxInfo.builder(SecOpenClose.class)
			.supplier(SecOpenClose::new)
			.addPatterns("run (when|while) (open[ing]|close:clos(e|ing)) [[the] gui]",
				"run (when|while) [the] gui (opens|close:closes)",
				"run on [gui] (open[ing]|close:clos(e|ing))")
			.build());
	}

	private boolean close;
	private Trigger trigger;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult,
						SectionNode sectionNode, List<TriggerItem> triggerItems) {
		if (!getParser().isCurrentSection(SecCreateGUI.class)) {
			Skript.error("GUI open/close sections can only be put within GUI creation or editing sections.");
			return false;
		}

		close = parseResult.hasTag("close");

		if (close) {
			trigger = loadCode(sectionNode, "inventory close", InventoryCloseEvent.class);
		} else {
			trigger = loadCode(sectionNode, "inventory open", InventoryOpenEvent.class);
		}

		return true;
	}

	@Override
	public @Nullable TriggerItem walk(Event event) {
		GUI gui = SkriptGUI.getGUIManager().getGUI(event);
		if (gui == null) {
			return walk(event, false);
		}

		Object variables = Variables.copyLocalVariables(event);
		if (close) {
			gui.setOnClose(closeEvent -> {
				Variables.setLocalVariables(closeEvent, variables);
				trigger.execute(closeEvent);
			});
		} else {
			gui.setOnOpen(openEvent -> {
				Variables.setLocalVariables(openEvent, variables);
				trigger.execute(openEvent);
			});
		}

		return walk(event, false);
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "run on gui " + (close ? "close" : "open");
	}

}
