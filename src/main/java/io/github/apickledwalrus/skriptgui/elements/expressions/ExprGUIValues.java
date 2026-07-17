package io.github.apickledwalrus.skriptgui.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import io.github.apickledwalrus.skriptgui.SkriptGUI;
import io.github.apickledwalrus.skriptgui.SkriptUtils;
import io.github.apickledwalrus.skriptgui.elements.sections.SecCreateGUI;
import io.github.apickledwalrus.skriptgui.elements.sections.SecOpenClose;
import io.github.apickledwalrus.skriptgui.elements.sections.SecMakeSlot;
import io.github.apickledwalrus.skriptgui.elements.sections.SecSlotChange;
import io.github.apickledwalrus.skriptgui.gui.GUI;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.Arrays;
import java.util.Locale;

@Name("GUI Values")
@Description("""
	Obtains various utility values related to a GUI.
	Many of these values are available in vanilla Skript, but have been kept for compatibility and/or ease of use.
	Some values may not be available in certain sections (e.g., 'gui close').
	""")
@Example("""
	create a gui with virtual chest inventory:
		make gui 10 with water bucket:
			set the gui item to lava bucket
	""")
@Since("1.0.0")
public class ExprGUIValues extends SimpleExpression<Object> {

	public static void register(SyntaxRegistry syntaxRegistry) {
		syntaxRegistry.register(SyntaxRegistry.EXPRESSION,
			SyntaxInfo.Expression.builder(ExprGUIValues.class, Object.class)
				.supplier(ExprGUIValues::new)
				.addPatterns(Arrays.stream(Value.values())
					.map(Value::getPattern)
					.toList())
				.build());
	}

	private enum Value {

		SLOT("slot"),
		RAW_SLOT("raw slot"),
		HOTBAR_SLOT("hotbar slot"),
		INVENTORY("inventory"),
		INVENTORY_ACTION("inventory action"),
		CLICK_TYPE("click (type|action)"),
		CURSOR_ITEM("cursor [item]"),
		CLICKED_ITEM("[clicked|current] item"),
		SLOT_TYPE("slot type"),
		PLAYER("player"),
		VIEWERS("(viewer|player)s"),
		SLOT_ID("slot id[entifier]"),
		GUI("");

		private final String pattern;

		Value(String pattern) {
			this.pattern = ("[the] gui " + pattern).stripTrailing();
		}

		public String getPattern() {
			return pattern;
		}

	}

	private Value value;
	// Whether the expression is being used in an open/close section
	private boolean openClose;

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		value = Value.values()[matchedPattern];

		ParserInstance parser = getParser();
		if (SkriptUtils.isSection(parser, SecCreateGUI.class)) {
			if (value != Value.GUI) {
				Skript.error("You can't use '" + parseResult.expr + "' in a GUI creation section.");
				return false;
			}
			return true;
		}

		if (!SkriptUtils.isSection(parser, SecMakeSlot.class, SecOpenClose.class, SecSlotChange.class)) {
			Skript.error("You can't use '" + parseResult.expr + "' outside of a GUI section.");
			return false;
		}

		openClose = SkriptUtils.isSection(parser, SecOpenClose.class);

		if (openClose && value != Value.GUI && value != Value.INVENTORY && value != Value.PLAYER && value != Value.VIEWERS) {
			Skript.error("You can't use '" + parseResult.expr + "' in a GUI open/close section.");
			return false;
		}

		return true;
	}

	@Override
	protected Object[] get(Event event) {
		if (value == Value.GUI) {
			GUI gui = SkriptGUI.getGUIManager().getGUI(event);
			return gui != null ? new GUI[]{gui} : new GUI[0];
		}

		if (openClose) {
			InventoryEvent inventoryEvent = (InventoryEvent) event;
			return switch (value) {
				case INVENTORY -> new Inventory[]{inventoryEvent.getInventory()};
				case PLAYER -> {
					HumanEntity humanEntity;
					if (inventoryEvent instanceof InventoryCloseEvent closeEvent) {
						humanEntity = closeEvent.getPlayer();
					} else {
						humanEntity = ((InventoryOpenEvent) inventoryEvent).getPlayer();
					}
					if (humanEntity instanceof Player player) {
						yield new Player[]{player};
					}
					yield new Object[0];
				}
				case VIEWERS -> (inventoryEvent.getViewers().toArray(new HumanEntity[0]));
				default -> throw new IllegalStateException("Unexpected value: " + value);
			};
		}

		InventoryClickEvent clickEvent = (InventoryClickEvent) event;
		return switch (value) {
			case SLOT -> new Number[]{clickEvent.getSlot()};
			case RAW_SLOT -> new Number[]{clickEvent.getRawSlot()};
			case HOTBAR_SLOT -> new Number[]{clickEvent.getHotbarButton()};
			case INVENTORY -> {
				Inventory clicked = clickEvent.getClickedInventory();
				yield clicked != null ? new Inventory[]{clicked} : new Inventory[0];
			}
			case INVENTORY_ACTION -> new InventoryAction[]{clickEvent.getAction()};
			case CLICK_TYPE -> new ClickType[]{clickEvent.getClick()};
			case CURSOR_ITEM -> {
				ItemStack cursor = clickEvent.getCursor();
				yield new ItemType[]{new ItemType(cursor)};
			}
			case CLICKED_ITEM -> {
				ItemStack currentItem = clickEvent.getCurrentItem();
				yield currentItem != null ? new ItemType[]{new ItemType(currentItem)} : new ItemType[0];
			}
			case SLOT_TYPE -> new SlotType[]{clickEvent.getSlotType()};
			case PLAYER -> clickEvent.getWhoClicked() instanceof Player player ? new Player[]{player} : new Player[0];
			case VIEWERS -> clickEvent.getViewers().stream()
				.filter(humanEntity -> humanEntity instanceof Player)
				.toArray(Player[]::new);
			case SLOT_ID -> {
				GUI gui = SkriptGUI.getGUIManager().getGUI(event);
				yield gui != null ? new String[]{String.valueOf(gui.convert(clickEvent.getSlot()))} : new GUI[0];
			}
			default -> throw new IllegalStateException("Unexpected value: " + value);
		};
	}

	@Override
	public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
		if (value == Value.GUI) {
			return super.acceptChange(mode);
		}
		if (mode == ChangeMode.SET && value == Value.CLICKED_ITEM) {
			if (getParser().getHasDelayBefore().isTrue()) {
				Skript.error("You can't set the 'gui clicked item' when the event is already passed.");
				return null;
			}
			return CollectionUtils.array(ItemType.class);
		}
		return null;
	}

	@Override
	public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
		if (value == Value.GUI) {
			super.change(event, delta, mode);
			return;
		}
		if (delta == null || !(event instanceof InventoryClickEvent inventoryClickEvent)) {
			return;
		}
		inventoryClickEvent.setCurrentItem(((ItemType) delta[0]).getRandom());
	}

	@Override
	public boolean isSingle() {
		return value != Value.VIEWERS;
	}

	@Override
	public Class<?> getReturnType() {
		return switch (value) {
			case SLOT, RAW_SLOT, HOTBAR_SLOT -> Number.class;
			case INVENTORY -> Inventory.class;
			case INVENTORY_ACTION -> InventoryAction.class;
			case CLICK_TYPE -> ClickType.class;
			case CURSOR_ITEM, CLICKED_ITEM -> ItemType.class;
			case SLOT_TYPE -> SlotType.class;
			case PLAYER, VIEWERS -> Player.class;
			case SLOT_ID -> String.class;
			case GUI -> GUI.class;
		};
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "the " + value.name().toLowerCase(Locale.ENGLISH);
	}

}
