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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.List;
import java.util.function.Consumer;

@Name("GUI Slot Change")
@Description("""
	A section for executing code when a slot changes.
	Note that for shaped GUIs, where multiple slots are represented by a single character, the section will execute when any of those slots change.
	""")
@Example("""
	create a gui with a virtual chest inventory with shape "xxxxxxxxx", "x-------x", and "xxxxxxxxx"
		run when slot 1 changes:
			send "You changed slot 1"
		run when slot "-" changes:
			send "You changed an interior slot"
	""")
@Since("1.4.0")
public class SecSlotChange extends Section {

	public static void register(SyntaxRegistry syntaxRegistry) {
		syntaxRegistry.register(SyntaxRegistry.SECTION, SyntaxInfo.builder(SecSlotChange.class)
			.supplier(SecSlotChange::new)
			.addPatterns("run when [gui] slot[s] %integers/strings% change[s]",
				"run when [gui] slot[s] %integers/strings% (is|are) changed",
				"run on change of [gui] slot[s] %integers/strings%")
			.build());
	}

	private Trigger trigger;
	private Expression<Object> slots;

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult,
						SectionNode sectionNode, List<TriggerItem> triggerItems) {
		if (!getParser().isCurrentSection(SecCreateGUI.class)) {
			Skript.error("You can't listen for changes of a slot outside of a GUI creation or editing section.");
			return false;
		}

		trigger = loadCode(sectionNode, "inventory click", InventoryClickEvent.class);
		//noinspection unchecked
		slots = (Expression<Object>) expressions[0];

		return true;
	}

	@Override
	@Nullable
	public TriggerItem walk(Event event) {
		GUI gui = SkriptGUI.getGUIManager().getGUI(event);
		if (gui == null) {
			return walk(event, false);
		}

		Object variables = Variables.copyLocalVariables(event);
		Consumer<InventoryClickEvent> onChange = clickEvent -> {
			Variables.setLocalVariables(clickEvent, variables);
			trigger.execute(clickEvent);
		};

		Object[] slots = this.slots.getAll(event);
		for (Object slot : slots) {
			GUI.SlotData slotData = gui.getSlotData(gui.convert(slot));
			if (slotData == null) {
				continue;
			}
			slotData.setRunOnChange(onChange);
		}

		// We don't want to execute this section
		return walk(event, false);
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		boolean isSingle = slots.isSingle();
		return "run when gui slot" + (isSingle ? " " : "s ") + slots.toString(event, debug) + " change" + (isSingle ? "s" : "");
	}

}
