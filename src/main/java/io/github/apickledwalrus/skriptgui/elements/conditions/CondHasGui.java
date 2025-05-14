package io.github.apickledwalrus.skriptgui.elements.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import org.bukkit.entity.Player;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;

import io.github.apickledwalrus.skriptgui.SkriptGUI;

@Name("Has GUI")
@Description("Checks whether a player has a GUI open.")
@Examples({
		"command /guiviewers: # Prints a list of all players with a GUI open.",
		"\tset {_viewers::*} to all players where [input has a gui]",
		"\tsend \"GUI Viewers: %{_viewers::*}%\" to player"
})
@Since("1.0.0")
public class CondHasGui extends PropertyCondition<Player> {

	static {
		register(CondHasGui.class, PropertyType.HAVE, "a gui [open]", "players");
	}

	@Override
	public boolean check(Player player) {
		return SkriptGUI.getGUIManager().hasGUI(player);
	}

	@Override
	protected PropertyType getPropertyType() {
		return PropertyType.HAVE;
	}

	@Override
	protected String getPropertyName() {
		return "a gui open";
	}

}
