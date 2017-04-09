package me.tuke.sktuke.expressions.gui;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import me.tuke.sktuke.manager.gui.v2.GUIHandler;
import me.tuke.sktuke.manager.gui.v2.GUIInventory;
import me.tuke.sktuke.util.NewRegister;
import org.bukkit.event.Event;

/**
 * @author Tuke_Nuke on 15/03/2017
 */
public class ExprGUI extends SimpleExpression<GUIInventory> {
	static {
		NewRegister.newSimple(ExprGUI.class, "last[ly] [created] gui", "gui %string%");
	}
	private int mode = 0;
	private Expression<String> id;
	@Override
	protected GUIInventory[] get(Event e) {
		if (mode > 0)
			return new GUIInventory[]{GUIHandler.getInstance().getGUI(id.getSingle(e))};
		return new GUIInventory[]{GUIHandler.getInstance().lastCreated};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends GUIInventory> getReturnType() {
		return GUIInventory.class;
	}

	@Override
	public String toString(Event event, boolean b) {
		return mode == 0 ? "last gui" : "gui " + id.toString(event, b);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
		mode = i;
		if (i > 0)
			id = (Expression<String>) arg[1];
		return true;
	}
}
