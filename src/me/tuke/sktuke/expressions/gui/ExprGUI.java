package me.tuke.sktuke.expressions.gui;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import me.tuke.sktuke.manager.gui.v2.GUIHandler;
import me.tuke.sktuke.manager.gui.v2.GUIInventory;
import me.tuke.sktuke.util.Registry;
import org.bukkit.event.Event;

/**
 * @author Tuke_Nuke on 15/03/2017
 */
@Name("Last GUI/GUI from id")
@Description("It is used to return the last created gui or a gui from a string id.")
@Examples({
		"on skript load:",
		"\tcreate new gui with id \"HUB\" with virtual chest:",
		"\t\tmake gui slot 1 with diamond named \"Server 1\":",
		"\t\t\texecute player command \"/server server1\"",
		"\t\tmake gui slot 2 with paper named \"Server 2\":",
		"\t\t\texecute player command \"/server server2\"",
		" ",
		"command /hub [<text>]:",
		"\ttrigger:",
		"\t\topen gui \"HUB\" to player"})
@Since("1.7.5")
public class ExprGUI extends SimpleExpression<GUIInventory> {
	static {
		Registry.newSimple(ExprGUI.class, "last[ly] [created] gui", "gui [with id] %string%");
	}
	private Expression<String> id;
	@Override
	protected GUIInventory[] get(Event e) {
		if (id != null)
			return new GUIInventory[]{GUIHandler.getInstance().getGUI(id.getSingle(e))};
		return new GUIInventory[]{GUIHandler.getInstance().getGUIEvent(e)};
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
		return id == null ? "last gui" : "gui " + id.toString(event, b);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
		if (i > 0)
			id = (Expression<String>) arg[1];
		return true;
	}
}
