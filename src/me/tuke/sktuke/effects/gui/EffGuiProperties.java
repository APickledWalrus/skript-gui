package me.tuke.sktuke.effects.gui;


import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import me.tuke.sktuke.manager.gui.v2.GUIInventory;
import me.tuke.sktuke.util.NewRegister;
import org.bukkit.event.Event;

/**
 * @author Tuke_Nuke on 01/04/2017
 */
public class EffGuiProperties extends Effect {
	static {
		NewRegister.newEffect(EffGuiProperties.class, 1, "change %gui inventory% properties to [name %-string% and size %-number% and] [shape %strings%]");
	}

	private Expression<GUIInventory> gui;
	private Expression<String> name;
	private Expression<String> rawShape;
	private Expression<Number> size;
	@Override
	protected void execute(Event e) {
		GUIInventory gui = this.gui.getSingle(e);
		if (gui != null) {
			String name = this.name != null ? this.name.getSingle(e) : null;
			String rawShape = this.rawShape != null ? this.rawShape.getSingle(e) : null;
			Integer size = this.size != null && this.size.getSingle(e) != null ? this.size.getSingle(e).intValue() : null;
			gui.changeProperties(name, size, rawShape);
		}
	}

	@Override
	public String toString(Event event, boolean b) {
		return "change " + gui.toString(event, b) + " properties to name " + name.toString(event, b) + " and size " +size.toString(event, b) + " and shape " + rawShape.toString(event ,b);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
		if (arg[1] == null && arg[3] == null) {
			Skript.error("You need to choose a gui property to change. As the name and size or/and the shape.");
			return false;
		}
		gui = (Expression<GUIInventory>) arg[0];
		name = (Expression<String>) arg[1];
		size = (Expression<Number>) arg[2];
		rawShape = (Expression<String>) arg[3];
		return true;
	}
}
