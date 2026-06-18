package io.github.apickledwalrus.skriptgui.elements.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
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
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.List;
import java.util.function.Consumer;

@Name("Make GUI Slot")
@Description("A section for making GUI slots that run code, or clearing them entirely.")
@Example("""
	create a gui with virtual chest inventory with 3 rows named "My GUI":
		# Formats the next available GUI slot with dirt.
		# Doesn't do anything when clicked on.
		make next gui with dirt

		make gui 10 with water bucket:
			# Code here is run when the GUI slot is clicked
			send "Splash!" to the player
	""")
@Example("""
	edit the player's gui:
		unformat gui slot 10 # Removes the item in slot 10
		unformat the last gui slot # Removes the item in the last filled slot
	""")
@Since("1.0.0, 1.2.0 (making specific slots stealable)")
public class SecMakeSlot extends EffectSection {

	public static void register(SyntaxRegistry syntaxRegistry) {
		syntaxRegistry.register(SyntaxRegistry.SECTION, SyntaxInfo.builder(SecMakeSlot.class)
			.supplier(SecMakeSlot::new)
			.addPatterns("(make|format) [the] next gui [slot] (with|to) [removable:([re]mov[e]able|stealable)] %itemtype%",
				"(make|format) gui [slot[s]] %integers/strings% (with|to) [removable:([re]mov[e]able|stealable)] %itemtype%",
				"(un(make|format)|remove) [the] (next|last) gui slot",
				"(un(make|format)|remove) gui [slot[s]] %integers/strings%",
				"(un(make|format)|remove) [all [[of] the]|the] gui [slots]")
			.build());
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
		if (!SkriptUtils.isSection(getParser(), SecCreateGUI.class, SecMakeSlot.class, SecOpenClose.class, SecSlotChange.class)) {
			Skript.error("You can't make a GUI slot outside of a GUI section.");
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
				Object[] slots =  this.slots != null ? this.slots.getArray(event) : new Object[]{gui.nextSlot()};
				if (trigger == null) {
					for (Object slot : slots) {
						gui.setItem(slot, item, removable, null);
					}
					break;
				}

				Object variables = Variables.copyLocalVariables(event);
				Consumer<InventoryClickEvent> onClick = clickEvent -> {
					Variables.setLocalVariables(clickEvent, variables);
					trigger.execute(clickEvent);
				};
				for (Object slot : slots) {
					gui.setItem(slot, item, removable, onClick);
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
