package me.tuke.sktuke.expressions.gui;

import javax.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.InventorySlot;
import ch.njol.skript.util.Slot;
import me.tuke.sktuke.manager.gui.v2.GUIHandler;
import me.tuke.sktuke.sections.gui.EffMakeGUI;
import me.tuke.sktuke.manager.gui.v2.GUIInventory;
import me.tuke.sktuke.util.EffectSection;
import me.tuke.sktuke.util.Registry;
import me.tuke.sktuke.util.InventoryUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

/**
 * @author Tuke_Nuke on 15/03/2017
 */
public class ExprGUIValue extends SimpleExpression<Object>{
	static {
		Registry.newSimple(ExprGUIValue.class,
				"gui-slot",
				"gui-raw-slot",
				"gui-hotbar-slot",
				"gui-inventory",
				"gui-inventory-action",
				"gui-click-(type|action)",
				"gui-cursor",
				"gui-[(clicked|current)-]item",
				"gui-slot-type",
				"gui-player",
				"gui-players",
				"gui-inventory-name",
				"gui-slot-id",
				"gui");
	}

	public EffMakeGUI currentSection = null;
	private int type = -1;
	private String toString = "gui-value";
	private boolean isDelayed = false;
	
	@Override
	public Class<? extends Object> getReturnType() {
		switch(type){
			case 0:
			case 1:
			case 2: return Number.class;
			case 3: return Inventory.class;
			case 4: return InventoryAction.class;
			case 5: return ClickType.class;
			case 6:
			case 7: return ItemStack.class;
			case 8: return SlotType.class;
			case 9: 
			case 10: return Player.class;
			case 11:
			case 12: return String.class;
			case 13: return GUIInventory.class;
			default: return Object.class;
		}
	}

	@Override
	public boolean isSingle() {
		return type != 10;
	}

	@Override
	public boolean init(Expression<?>[] arg0, int arg1, Kleenean arg2, ParseResult arg3) {
		if (EffMakeGUI.lastInstance == null) {
			Skript.error("You can't use '" + arg3.expr + "' outside of a 'make gui' effect.");
			return false;
		}
		isDelayed = arg2.isTrue();
		type = arg1;
		toString = arg3.expr;
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return toString;
	}

	@Override
	@Nullable
	protected Object[] get(Event e) {
		GUIInventory gui = GUIHandler.getInstance().getGUIEvent(e);
		if (gui != null && e instanceof InventoryClickEvent){
			switch (type){
				case 0: return new Number[]{((InventoryClickEvent) e).getSlot()};
				case 1: return new Number[]{((InventoryClickEvent) e).getRawSlot()};
				case 2: return new Number[]{((InventoryClickEvent) e).getHotbarButton()};
				case 3: return new Inventory[]{InventoryUtils.getClickedInventory(((InventoryClickEvent) e))};
				case 4: return new InventoryAction[]{((InventoryClickEvent) e).getAction()};
				case 5: return new ClickType[]{((InventoryClickEvent) e).getClick()};
				case 6: return new ItemStack[]{((InventoryClickEvent) e).getCursor()};
				case 7: return new ItemStack[]{((InventoryClickEvent) e).getCurrentItem()};
				case 8: return new SlotType[]{((InventoryClickEvent) e).getSlotType()};
				case 9:	return new Player[]{(Player)((InventoryClickEvent) e).getWhoClicked()};
				case 10: return ((InventoryClickEvent) e).getViewers().toArray();
				case 11: return new String[]{InventoryUtils.getClickedInventory(((InventoryClickEvent) e)).getName()};
				case 12: return new String[]{"" + gui.convertSlot(((InventoryClickEvent) e).getSlot())};
				case 13: return new GUIInventory[]{gui};
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private Changer<Slot> changer = null;
	public void change(final Event e, Object[] delta, Changer.ChangeMode mode){
		if (e instanceof InventoryClickEvent) {
			if (type == 7)
				changer.change(new Slot[]{new InventorySlot(((InventoryClickEvent) e).getInventory(), ((InventoryClickEvent) e).getSlot())}, delta, mode);
			else {
				Slot cursor = new Slot() {
					@Override
					public ItemStack getItem() {
						return ((InventoryClickEvent) e).getCursor();
					}

					@Override
					public void setItem(ItemStack itemStack) {
						((InventoryClickEvent) e).getWhoClicked().setItemOnCursor(itemStack);
					}

					@Override
					protected String toString_i() {
						return "cursor slot";
					}
				};
				changer.change(new Slot[]{cursor}, delta, mode);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (type == 6 || type == 7) {
			if (!isDelayed) {
				if (changer == null)
					changer = (Changer<Slot>) Classes.getExactClassInfo(Slot.class).getChanger();
				return changer.acceptChange(mode);
			}
			Skript.error("You can't set the " + toString + " when the event is already passed.");
		}
		return null;

	}
}
