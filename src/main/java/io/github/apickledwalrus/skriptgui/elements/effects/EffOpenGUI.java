package io.github.apickledwalrus.skriptgui.elements.effects;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

import io.github.apickledwalrus.skriptgui.gui.GUI;

@Name("Open GUI")
@Description("Opens the given GUI for the given players.")
@Examples({"create a gui with virtual chest inventory named \"My GUI Name\" with 3 rows",
			"open gui last gui for player"
})
@Since("1.0.0")
public class EffOpenGUI extends Effect {

	static {
		Skript.registerEffect(EffOpenGUI.class,
				"(open|show) [[skript[-]]gui] %guiinventory% (to|for) %players%"
		);
	}

	private Expression<GUI> gui;
	private Expression<Player> players;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		gui = (Expression<GUI>) exprs[0];
		players = (Expression<Player>) exprs[1];
		return true;
	}

	@Override
	protected void execute(Event e) {
		GUI gui = this.gui.getSingle(e);
		if (gui != null) {
			for (Player p : players.getArray(e))
				p.openInventory(gui.getInventory());
		}
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "open gui " + gui.toString(e, debug) + " to " + players.toString(e, debug);
	}

}