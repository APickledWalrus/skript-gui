package io.github.apickledwalrus.skriptgui.elements.expressions;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;

import ch.njol.skript.ScriptLoader;
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
import io.github.apickledwalrus.skriptgui.elements.sections.SecMakeGUI;
import io.github.apickledwalrus.skriptgui.elements.sections.SecOnCloseGUI;
import io.github.apickledwalrus.skriptgui.gui.GUI;
import io.github.apickledwalrus.skriptgui.util.EffectSection;

@Name("GUI Values")
@Description("Different utility values for a GUI. Not all values are available for the GUI close section.")
@Examples({"create a gui with virtual chest inventory:",
			"\tmake gui 10 with water bucket:",
			"\t\tset the clicked-item to lava bucket"
})
@Since("1.0.0")
public class ExprGUIValues extends SimpleExpression<Object> {

	static {
		Skript.registerExpression(ExprGUIValues.class, Object.class, ExpressionType.SIMPLE,
				"gui(-| )slot",
				"gui(-| )raw(-| )slot",
				"gui(-| )hotbar(-| )slot",
				"gui(-| )inventory",
				"gui(-| )inventory(-| )action",
				"gui(-| )click(-| )(type|action)",
				"gui(-| )cursor[(-| )item]",
				"gui(-| )[(clicked|current)(-| )]item",
				"gui(-| )slot(-| )type",
				"gui(-| )player",
				"gui(-| )(viewer|player)s",
				"gui(-| )slot(-| )id",
				"gui"
		);
	}

	private int pattern;
	private boolean isDelayed;
	private String toString = "gui values";

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, ParseResult parseResult) {
		if (!EffectSection.isCurrentSection(SecMakeGUI.class, SecOnCloseGUI.class)) {
			Skript.error("You can't use '" + parseResult.expr.replace("(-| )", " ") + "' outside of a GUI make or close section.");
			return false;
		}
		pattern = matchedPattern;
		isDelayed = kleenean.isTrue();
		toString = parseResult.expr.replace("(-| )", " ");
		return true;
	}

	@Override
	protected Object[] get(Event event) {
		GUI gui = SkriptGUI.getGUIManager().getGUIEvent(event);
		if (event instanceof InventoryClickEvent && gui != null) {
			InventoryClickEvent e = (InventoryClickEvent) event;
			switch (pattern) {
				case 0: return new Number[]{e.getSlot()};
				case 1: return new Number[]{e.getRawSlot()};
				case 2: return new Number[]{e.getHotbarButton()};
				case 3: return new Inventory[]{e.getClickedInventory()};
				case 4: return new InventoryAction[]{e.getAction()};
				case 5: return new ClickType[]{e.getClick()};
				case 6: return new ItemType[]{new ItemType(e.getCursor())};
				case 7: return new ItemType[]{new ItemType(e.getCurrentItem())};
				case 8: return new SlotType[]{e.getSlotType()};
				case 9:	return new HumanEntity[]{((InventoryClickEvent) e).getWhoClicked()};
				case 10: return (e.getViewers().toArray(new HumanEntity[0]));
				case 11: return new String[]{"" + gui.convertSlot(e.getSlot())};
				case 12: return new GUI[]{gui};
			}
		} else if (event instanceof InventoryCloseEvent && gui != null) {
			InventoryCloseEvent e = (InventoryCloseEvent) event;
			switch (pattern) {
				case 3: return new Inventory[]{e.getInventory()};
				case 9: return new HumanEntity[]{(e.getPlayer())};
				case 10: return (e.getViewers().toArray(new HumanEntity[0]));
				case 12: return new GUI[]{gui};
			}
		}
		return new Object[]{};
	}

	public Class<?>[] acceptChange(final ChangeMode mode) {
		if (isDelayed) {
			Skript.error("You can't set the '" + toString + "' when the event is already passed.");
			return null;
		}

		if (mode == ChangeMode.SET && ScriptLoader.isCurrentEvent(InventoryClickEvent.class)) {
			if (pattern == 7) // Clicked Item
				return CollectionUtils.array(ItemType.class);
		}

		return null;
	}

	public void change(final Event event, Object[] delta, ChangeMode mode) {
		if (!(event instanceof InventoryClickEvent) || mode != ChangeMode.SET || delta == null)
			return;

		InventoryClickEvent e = (InventoryClickEvent) event;

		if (pattern == 7)
			e.setCurrentItem(((ItemType) delta[0]).getRandom());
	}

	@Override
	public boolean isSingle() {
		return pattern != 10;
	}

	@Override
	public Class<? extends Object> getReturnType() {
		switch (pattern) {
			case 0:
			case 1:
			case 2: return Number.class;
			case 3: return Inventory.class;
			case 4: return InventoryAction.class;
			case 5: return ClickType.class;
			case 6:
			case 7: return ItemType.class;
			case 8: return SlotType.class;
			case 9:
			case 10: return HumanEntity.class;
			case 11:
			case 12: return String.class;
			case 13: return GUI.class;
			default: return Object.class;
		}
	}

	@Override
	public String toString(Event e, boolean debug) {
		return toString;
	}

}
