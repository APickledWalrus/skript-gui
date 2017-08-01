package com.github.tukenuke.tuske.expressions.gui;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.github.tukenuke.tuske.manager.gui.v2.GUIInventory;
import com.github.tukenuke.tuske.manager.gui.v2.GUIHandler;
import com.github.tukenuke.tuske.sections.gui.EffCreateGUI;
import org.bukkit.event.Event;

/**
 * @author Tuke_Nuke on 01/04/2017
 */
public class ExprGUIShape extends SimpleExpression<String> {
	static {
		//Registry.newSimple(ExprGUIShape.class, "gui shape");
	}
	private EffCreateGUI effGui;
	@Override
	protected String[] get(Event event) {
		GUIInventory gui = GUIHandler.getInstance().getGUIEvent(event);
		if (gui != null)
			return new String[]{gui.getRawShape()};
		return new String[0];
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	public String toString(Event event, boolean b) {
		return "gui shape";
	}

	@Override
	public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
		//if (EffCreateGUI.lastInstance == null) {
		//	Skript.info("You can't use '" + parseResult.expr + "' outside of gui create section");
		//	return false;
		//}
		return true;
	}
}
