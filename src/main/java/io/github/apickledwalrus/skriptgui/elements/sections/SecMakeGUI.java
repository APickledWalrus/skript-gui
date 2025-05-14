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
import ch.njol.skript.lang.SyntaxStringBuilder;
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

	private enum Action {

		MAKE_NEXT,
		MAKE_SPECIFIC,
		REMOVE_NEXT,
		REMOVE_SPECIFIC,
		REMOVE_ALL;

		public boolean isMake() {
			return this == MAKE_NEXT || this == MAKE_SPECIFIC;
		}

	}

	private @Nullable Trigger trigger;

	private @Nullable Expression<Object> slots; // Can be number or a string
	private @Nullable Expression<ItemType> item;

	private Action action;
	private boolean removable;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, ParseResult parseResult,
						@Nullable SectionNode sectionNode, @Nullable List<TriggerItem> items) {
		if (!SkriptUtils.isSection(getParser(), SecCreateGUI.class, SecMakeGUI.class, SecGUIOpenClose.class)) {
			Skript.error("You can't make a GUI slot outside of a GUI creation or editing section.");
			return false;
		}

		action = Action.values()[matchedPattern];
		if (action.isMake()) {
			item = (Expression<ItemType>) exprs[matchedPattern];
		}
		if (action == Action.MAKE_SPECIFIC || action == Action.REMOVE_SPECIFIC) {
			slots = (Expression<Object>) exprs[0];
		}

		removable = parseResult.hasTag("removable");

		if (sectionNode != null) {
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

		switch (action) {
			case MAKE_NEXT, MAKE_SPECIFIC -> {
				assert item != null;
				ItemType itemType = item.getSingle(event);
				if (itemType == null) {
					break;
				}
				ItemStack item = itemType.getRandom();
				if (item == null) {
					break;
				}
				if (trigger != null) {
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
			}
			case REMOVE_NEXT -> gui.clear(gui.nextSlotInverted());
			case REMOVE_SPECIFIC -> {
				assert slots != null;
				for (Object slot : slots.getArray(event)) {
					gui.clear(slot);
				}
			}
			case REMOVE_ALL -> gui.clear();
		}

		// We don't want to execute this section
		return walk(event, false);
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return switch (action) {
			case MAKE_NEXT, MAKE_SPECIFIC -> {
				assert item != null;
				SyntaxStringBuilder builder = new SyntaxStringBuilder(event, debug);
				builder.append("make");
				if (slots == null) {
					builder.append("the next gui slot");
				} else {
					builder.append("slot" + (slots.isSingle() ? "" : "s"), slots);
				}
				builder.append("with", item);
				yield builder.toString();
			}
			case REMOVE_NEXT -> "remove the next gui slot";
			case REMOVE_SPECIFIC -> {
				assert slots != null;
				yield "remove gui slot" + (slots.isSingle() ? " " : "s ") + slots.toString(event, debug);
			}
			case REMOVE_ALL -> "remove all of the gui slots";
		};
	}

}
