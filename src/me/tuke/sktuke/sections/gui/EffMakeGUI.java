package me.tuke.sktuke.sections.gui;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.variables.Variables;
import me.tuke.sktuke.manager.gui.v2.GUIHandler;
import me.tuke.sktuke.manager.gui.v2.GUIInventory;
import me.tuke.sktuke.util.Registry;
import me.tuke.sktuke.util.ReflectionUtils;
import me.tuke.sktuke.util.VariableUtil;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import me.tuke.sktuke.util.EffectSection;

import java.util.*;

/**
 * @author Tuke_Nuke on 01/04/2017
 */
@Name("Make GUI")
@Description("Used to format a gui slot inside of gui creation/editing section")
public class EffMakeGUI extends EffectSection {
	static {
		Registry.newEffect(EffMakeGUI.class,
				"(make|format) next gui [slot] (with|to) %itemstack%",
				"(make|format) gui [slot] %strings/numbers% (with|to) %itemstack%",
				"(un(make|format)|remove) next gui [slot]",
				"(un(make|format)|remove) gui [slot] %strings/numbers%",
				"(un(make|format)|remove) all gui [slot]");
	}
	public static EffMakeGUI lastInstance = null;

	private EffCreateGUI currentSection = null;
	private Expression<?> slot; //Can be number or a string
	private Expression<ItemStack> item;
	private int type;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		if (checkIfCondition()) {
			return false;
		}
		if (EffCreateGUI.lastInstance == null) {
			Skript.error("You can't make a gui outside of 'create/edit gui' effect.");
			return false;
		}
		type = arg1++;
		if (arg.length > 0 && arg1 <= 2) {
			item = (Expression<ItemStack>) arg[arg.length - 1];
		}
		if (arg1 % 2 == 0) {
			slot = arg[0].getConvertedExpression(Object.class);
		}
		currentSection = EffCreateGUI.lastInstance;
		if (hasSection()) {
			EffMakeGUI last = lastInstance;
			lastInstance = this;
			loadSection("gui effect", InventoryClickEvent.class);
			lastInstance = last;
		}
		return true;
	}

	@Override
	public void execute(Event e) {
		GUIInventory gui = currentSection.gui;
		if (gui == null)
			return;
		if (type > 1) {
			Object[] slots = this.slot != null ? this.slot.getArray(e) : type == 4 ? null : new Object[]{gui.nextInvertedSlot()};
			gui.clearSlots(slots);
			return;
		}
		ItemStack item = this.item.getSingle(e);
		Object[] slot = this.slot != null ? this.slot.getArray(e) : new Object[]{gui.nextSlot()};
		for (Object s : slot) {
			if (hasSection()) {
				final Object variables = VariableUtil.getInstance().copyVariables(e);
				gui.setItem(s, item, event -> {
					VariableUtil.getInstance().pasteVariables(event, variables);
					GUIHandler.getInstance().setGUIEvent(event, gui);
					runSection(event);
				});
			} else
				gui.setItem(s, item);
		}
	}
	
	@Override
	public String toString(Event arg0, boolean arg1) {
		if (type > 1)
			return "unmake gui slot";
		return "make " + (slot != null ? " a gui slot "+ slot.toString(arg0, arg1) : "next gui slot") + " of gui with " + item.toString(arg0, arg1);
	}
}
