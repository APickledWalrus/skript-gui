package io.github.apickledwalrus.skriptgui.elements.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import io.github.apickledwalrus.skriptgui.SkriptGUI;
import io.github.apickledwalrus.skriptgui.gui.GUI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("GUI of Player")
@Description("Obtains the GUI a player currently has open.")
@Example("""
	edit the player's gui:
		make gui slot 1 with dirt named "Edited Slot"
	""")
@Since("1.1.0")
public class ExprGUI extends SimplePropertyExpression<Player, GUI> {

	public static void register(SyntaxRegistry syntaxRegistry) {
		syntaxRegistry.register(SyntaxRegistry.EXPRESSION,
			infoBuilder(ExprGUI.class, GUI.class, "gui", "players", false)
				.supplier(ExprGUI::new)
				.build());
	}

	@Override
	public @Nullable GUI convert(Player player) {
		return SkriptGUI.getGUIManager().getGUI(player.getOpenInventory().getTopInventory());
	}

	@Override
	public Class<? extends GUI> getReturnType() {
		return GUI.class;
	}

	@Override
	protected String getPropertyName() {
		return "gui";
	}

}
