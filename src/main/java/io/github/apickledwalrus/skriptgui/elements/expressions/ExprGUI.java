package io.github.apickledwalrus.skriptgui.elements.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import io.github.apickledwalrus.skriptgui.SkriptGUI;
import io.github.apickledwalrus.skriptgui.gui.GUI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("GUI of Player")
@Description("The GUI that the player currently has open.")
@Examples({
		"edit the player's gui:",
		"\tmake gui 1 with dirt named \"Edited Slot\""
})
@Since("1.1.0")
public class ExprGUI extends SimplePropertyExpression<Player, GUI> {

	static {
		register(ExprGUI.class, GUI.class, "gui", "players");
	}

	@Nullable
	@Override
	public GUI convert(Player player) {
		return SkriptGUI.getGUIManager().getGUI(player);
	}

	@Override
	@NotNull
	public Class<? extends GUI> getReturnType() {
		return GUI.class;
	}

	@Override
	@NotNull
	protected String getPropertyName() {
		return "gui";
	}

}
