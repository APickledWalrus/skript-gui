package me.tuke.sktuke.effects.gui;


import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import me.tuke.sktuke.manager.gui.v2.GUIHandler;
import me.tuke.sktuke.manager.gui.v2.GUIInventory;
import me.tuke.sktuke.sections.gui.EffCreateGUI;
import me.tuke.sktuke.util.Registry;
import org.bukkit.event.Event;

/**
 * @author Tuke_Nuke on 01/04/2017
 */
public class EffGuiProperties extends Effect {
	static {
		Registry.newEffect(EffGuiProperties.class, 1,
				"change gui inventory to name %string% and size %number%",
				"change gui shape [of (1¦items|2¦actions)] to %strings%",
				"change gui properties of inventory to name %string% [with %-number% row[s]] and shape [of (1¦items|2¦actions)] to %strings%");
	}

	private Expression<String> name;
	private Expression<String> rawShape;
	private Expression<Number> size;
	private int shapeMode = -1;
	@Override
	protected void execute(Event e) {
		GUIInventory gui = GUIHandler.getInstance().getGUIEvent(e);
		if (gui != null) {
			String name = this.name != null ? this.name.getSingle(e) : null;
			String rawShape = this.rawShape != null ? this.rawShape.getSingle(e) : null;
			Integer size = this.size != null && this.size.getSingle(e) != null ? this.size.getSingle(e).intValue() : null;
			gui.changeProperties(name, size, rawShape, shapeMode);
		}
	}

	@Override
	public String toString(Event event, boolean b) {
		return "change gui properties to name " + name.toString(event, b) + " and size " +size.toString(event, b) + " and shape " + rawShape.toString(event ,b);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
		if (EffCreateGUI.lastInstance == null) {
			Skript.error("You can't change the gui properties outside of 'create/edit gui' effect.");
			return false;
		}
		if (i == 0 || i == 2) {
			name = (Expression<String>) arg[0];
			size = (Expression<Number>) arg[1];
		}
		if (i == 1)
			rawShape = (Expression<String>) arg[0];
		else if (i == 2)
			rawShape = (Expression<String>) arg[2];
		if (i > 0) {
			shapeMode = parseResult.mark;
		}
		return true;
	}
}
