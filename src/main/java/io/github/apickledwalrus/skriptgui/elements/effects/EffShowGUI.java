package io.github.apickledwalrus.skriptgui.elements.effects;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.util.Kleenean;
import io.github.apickledwalrus.skriptgui.gui.GUI;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Show GUI")
@Description("""
	Shows a GUI to a player.
	This effect must be used for the experimental menu GUIs created using the 'Menu Inventory' expression.
	""")
@Example("show the last gui to the player")
@Since("1.4.0")
public class EffShowGUI extends Effect {

	public static void register(SyntaxRegistry syntaxRegistry) {
		syntaxRegistry.register(SyntaxRegistry.EFFECT, SyntaxInfo.builder(EffShowGUI.class)
			.supplier(EffShowGUI::new)
			.addPattern("(open|show|display) [gui] %gui% (to|for) %players%")
			.build());
	}

	private Expression<GUI> gui;
	private Expression<Player> players;

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		//noinspection unchecked
		this.gui = (Expression<GUI>) expressions[0];
		//noinspection unchecked
		this.players = (Expression<Player>) expressions[1];
		return true;
	}

	@Override
	protected void execute(Event event) {
		GUI gui = this.gui.getSingle(event);
		if (gui != null) {
			for (Player player : this.players.getArray(event)) {
				gui.open(player);
			}
		}
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return new SyntaxStringBuilder(event, debug)
			.append("show", gui, "to", players)
			.toString();
	}

}
