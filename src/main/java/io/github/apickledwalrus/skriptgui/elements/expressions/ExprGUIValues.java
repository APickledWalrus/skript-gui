package io.github.apickledwalrus.skriptgui.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import io.github.apickledwalrus.skriptgui.SkriptGUI;
import io.github.apickledwalrus.skriptgui.SkriptUtils;
import io.github.apickledwalrus.skriptgui.elements.sections.SecCreateGUI;
import io.github.apickledwalrus.skriptgui.elements.sections.SecGUIOpenClose;
import io.github.apickledwalrus.skriptgui.elements.sections.SecMakeGUI;
import io.github.apickledwalrus.skriptgui.gui.GUI;
import org.bukkit.entity.HumanEntity;
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

import java.util.Arrays;
import java.util.Locale;

@Name("GUI Values")
@Description("Different utility values for a GUI. Some are available in vanilla Skript. Not all values are available for the GUI close section.")
@Examples({
		"create a gui with virtual chest inventory:",
		"\tmake gui 10 with water bucket:",
		"\t\tset the gui item to lava bucket"
})
@Since("1.0.0")
public class ExprGUIValues extends SimpleExpression<Object> {

	static {
		Skript.registerExpression(ExprGUIValues.class, Object.class, ExpressionType.SIMPLE, Arrays.stream(Value.values())
				.map(Value::getPattern)
				.toArray(String[]::new));
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
		SLOT_ID("slot id"),
		GUI("");

		private final String pattern;

		Value(String pattern) {
			this.pattern = "[the] gui" + pattern;
		}

		public String getPattern() {
			return pattern;
		}

	}

	private Value value;
	private boolean isDelayed;
	// Whether the expression is being used in an open/close section
	private boolean openClose;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		ParserInstance parser = getParser();
		if (!SkriptUtils.isSection(parser, SecCreateGUI.class, SecMakeGUI.class, SecGUIOpenClose.class)) {
			Skript.error("You can't use '" + parseResult.expr + "' outside of a GUI make or open/close section.");
			return false;
		}

		value = Value.values()[matchedPattern];
		openClose = SkriptUtils.isSection(parser, SecGUIOpenClose.class);

		if (openClose && value != Value.GUI && value != Value.INVENTORY && value != Value.PLAYER && value != Value.VIEWERS) {
			Skript.error("You can't use '" + parseResult.expr + "' in a GUI open/close section.");
			return false;
		}

		this.isDelayed = !isDelayed.isFalse(); // TRUE or UNKNOWN

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
					if (inventoryEvent instanceof InventoryCloseEvent) {
						yield new HumanEntity[]{((InventoryCloseEvent) event).getPlayer()};
					}
					yield new HumanEntity[]{((InventoryOpenEvent) inventoryEvent).getPlayer()};
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
				yield cursor != null ? new ItemType[]{new ItemType(cursor)} : new ItemType[0];
			}
			case CLICKED_ITEM -> {
				ItemStack currentItem = clickEvent.getCurrentItem();
				yield currentItem != null ? new ItemType[]{new ItemType(currentItem)} : new ItemType[0];
			}
			case SLOT_TYPE -> new SlotType[]{clickEvent.getSlotType()};
			case PLAYER -> new HumanEntity[]{clickEvent.getWhoClicked()};
			case VIEWERS -> clickEvent.getViewers().toArray(new HumanEntity[0]);
			case SLOT_ID -> {
				GUI gui = SkriptGUI.getGUIManager().getGUI(event);
				yield gui != null ? new String[]{String.valueOf(gui.convert(clickEvent.getSlot()))} : new GUI[0];
			}
			default -> throw new IllegalStateException("Unexpected value: " + value);
		};
	}

	@Override
	@Nullable
	public Class<?>[] acceptChange(ChangeMode mode) {
		if (isDelayed) {
			String value = "the gui " + this.value.name().toLowerCase(Locale.ENGLISH).replace("_", "");
			Skript.error("You can't set the '" + value  + "' when the event is already passed.");
			return null;
		}

		if (mode == ChangeMode.SET && value == Value.CLICKED_ITEM) {
			return CollectionUtils.array(ItemType.class);
		}

		return null;
	}

	@Override
	public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
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
			case PLAYER, VIEWERS -> HumanEntity.class;
			case SLOT_ID -> String.class;
			case GUI -> GUI.class;
		};
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "the " + value.name().toLowerCase(Locale.ENGLISH);
	}

}
