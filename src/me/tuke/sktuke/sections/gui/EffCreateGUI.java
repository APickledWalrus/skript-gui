package me.tuke.sktuke.sections.gui;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import me.tuke.sktuke.manager.gui.v2.GUIHandler;
import me.tuke.sktuke.manager.gui.v2.GUIInventory;
import me.tuke.sktuke.util.EffectSection;
import me.tuke.sktuke.util.NewRegister;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;

/**
 * @author Tuke_Nuke on 01/04/2017
 */
public class EffCreateGUI extends EffectSection {
	static {
		NewRegister.newEffect(EffCreateGUI.class, 
				"create [new] gui [[with id] %-string%] with %inventory% [and shape %-strings%]");
	}
	public static EffCreateGUI lastInstance = null;
	public GUIInventory gui = null;
	private Expression<Inventory> inv;
	private Expression<String> str, id;
	@Override
	public void execute(Event e) {
		Inventory inv = this.inv.getSingle(e);
		if (inv != null) {
			gui = new GUIInventory(inv);
			if (str != null) 
				gui.shape(str.getArray(e));
			else
				gui.shapeDefault();
			GUIHandler.getInstance().lastCreated = gui;
			runSection(e);
		}
	}

	@Override
	public String toString(Event event, boolean b) {
		return "create gui";
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] arg, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
		if (checkIfCondition()) {
			return false;
		}
		id = (Expression<String>) arg[0];
		inv = (Expression<Inventory>) arg[1];
		str = (Expression<String>) arg[2];
		EffCreateGUI last = lastInstance;
		lastInstance = this;
		loadSection();
		lastInstance = last;
		return true;
	}
}
