package me.tuke.sktuke.expressions.gui;

import javax.annotation.Nullable;

import ch.njol.skript.Skript;
import me.tuke.sktuke.sections.gui.EffMakeGUI;
import me.tuke.sktuke.manager.gui.v2.GUIInventory;
import me.tuke.sktuke.util.NewRegister;
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
		NewRegister.newSimple(ExprGUIValue.class,
				"gui-slot",
				"gui-raw-slot",
				"gui-hotbar-slot",
				"gui-inventory",
				"gui-inventory-action",
				"gui-click-(type|action)",
				"gui-cursor",
				"gui-item",
				"gui-slot-type",
				"gui-player",
				"gui-players",
				"gui-inventory-name",
				"gui");
	}
	
	//public static final boolean hasClickedMethod = ReflectionUtils.hasMethod(InventoryClickEvent.class, "getClickedInventory");

	/* gui-slot
	 * gui-raw-slot
	 * gui-hotbar-slot
	 * gui-inventory
	 * gui-action
	 * gui-click-(type|action)
	 * gui-cursor
	 * gui-item
	 * gui-slot-type
	 * gui-player
	 * gui-players
	 * gui-inventory-name
	 * gui
	 */
	
	private int type = -1;
	private String toString = "gui-value";
	public EffMakeGUI effFGui = null;
	
	@Override
	public Class<? extends Object> getReturnType() {
		switch(type){
			case 0:
			case 1:
			case 2: return Integer.class;
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
		effFGui = EffMakeGUI.lastInstance;
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
		if (e instanceof InventoryClickEvent){
			switch (type){
				case 0: return new Integer[]{((InventoryClickEvent) e).getSlot()};
				case 1: return new Integer[]{((InventoryClickEvent) e).getRawSlot()};
				case 2: return new Integer[]{((InventoryClickEvent) e).getHotbarButton()};
				case 3: return new Inventory[]{InventoryUtils.getClickedInventory(((InventoryClickEvent) e))};
				case 4: return new InventoryAction[]{((InventoryClickEvent) e).getAction()};
				case 5: return new ClickType[]{((InventoryClickEvent) e).getClick()};
				case 6: return new ItemStack[]{((InventoryClickEvent) e).getCursor()};
				case 7: return new ItemStack[]{((InventoryClickEvent) e).getCurrentItem()};
				case 8: return new SlotType[]{((InventoryClickEvent) e).getSlotType()};
				case 9:	return new Player[]{(Player)((InventoryClickEvent) e).getWhoClicked()};
				case 10: return ((InventoryClickEvent) e).getViewers().toArray();
				case 11: return new String[]{InventoryUtils.getClickedInventory(((InventoryClickEvent) e)).getName()};
			}
		}
		return null;
	}
}
