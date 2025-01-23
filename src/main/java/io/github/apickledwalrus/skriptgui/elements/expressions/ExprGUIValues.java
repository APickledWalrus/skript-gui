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
		if (!SkriptUtils.isSection(SecCreateGUI.class, SecMakeGUI.class, SecGUIOpenClose.class)) {
			Skript.error("You can't use '" + parseResult.expr + "' outside of a GUI make or open/close section.");
			return false;
		}

		value = Value.values()[matchedPattern];
		openClose = SkriptUtils.isSection(SecGUIOpenClose.class);

		if (openClose && matchedPattern != 3 && matchedPattern != 9 && matchedPattern != 10 && matchedPattern != 12) {
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
			switch (value) {
				case INVENTORY:
					return new Inventory[]{inventoryEvent.getInventory()};
				case PLAYER:
					if (inventoryEvent instanceof InventoryCloseEvent) {
						return new HumanEntity[]{((InventoryCloseEvent) event).getPlayer()};
					}
					return new HumanEntity[]{((InventoryOpenEvent) inventoryEvent).getPlayer()};
				case VIEWERS:
					return (inventoryEvent.getViewers().toArray(new HumanEntity[0]));
				default:
					throw new IllegalStateException("Unexpected value: " + value);
			}
		}

		InventoryClickEvent clickEvent = (InventoryClickEvent) event;
		switch (value) {
			case SLOT:
				return new Number[]{clickEvent.getSlot()};
			case RAW_SLOT:
				return new Number[]{clickEvent.getRawSlot()};
			case HOTBAR_SLOT:
				return new Number[]{clickEvent.getHotbarButton()};
			case INVENTORY:
				Inventory clicked = clickEvent.getClickedInventory();
				return clicked != null ? new Inventory[]{clicked} : new Inventory[0];
			case INVENTORY_ACTION:
				return new InventoryAction[]{clickEvent.getAction()};
			case CLICK_TYPE:
				return new ClickType[]{clickEvent.getClick()};
			case CURSOR_ITEM:
				ItemStack cursor = clickEvent.getCursor();
				return cursor != null ? new ItemType[]{new ItemType(cursor)} : new ItemType[0];
			case CLICKED_ITEM:
				ItemStack currentItem = clickEvent.getCurrentItem();
				return currentItem != null ? new ItemType[]{new ItemType(currentItem)} : new ItemType[0];
			case SLOT_TYPE:
				return new SlotType[]{clickEvent.getSlotType()};
			case PLAYER:
				return new HumanEntity[]{clickEvent.getWhoClicked()};
			case VIEWERS:
				return clickEvent.getViewers().toArray(new HumanEntity[0]);
			case SLOT_ID:
				GUI gui = SkriptGUI.getGUIManager().getGUI(event);
				return gui != null ? new String[]{String.valueOf(gui.convert(clickEvent.getSlot()))} : new GUI[0];
			default:
				throw new IllegalStateException("Unexpected value: " + value);
		}
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
		if (delta == null || !(event instanceof InventoryClickEvent)) {
			return;
		}
		((InventoryClickEvent) event).setCurrentItem(((ItemType) delta[0]).getRandom());
	}

	@Override
	public boolean isSingle() {
		return value != Value.VIEWERS;
	}

	@Override
	public Class<?> getReturnType() {
		switch (value) {
			case SLOT:
			case RAW_SLOT:
			case HOTBAR_SLOT:
				return Number.class;
			case INVENTORY:
				return Inventory.class;
			case INVENTORY_ACTION:
				return InventoryAction.class;
			case CLICK_TYPE:
				return ClickType.class;
			case CURSOR_ITEM:
			case CLICKED_ITEM:
				return ItemType.class;
			case SLOT_TYPE:
				return SlotType.class;
			case PLAYER:
			case VIEWERS:
				return HumanEntity.class;
			case SLOT_ID:
				return String.class;
			case GUI:
				return GUI.class;
			default:
				throw new IllegalStateException("Unknown value " + value);
		}
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "the " + value.name().toLowerCase(Locale.ENGLISH);
	}

}
