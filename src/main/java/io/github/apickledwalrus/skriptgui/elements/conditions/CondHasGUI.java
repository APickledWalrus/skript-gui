package io.github.apickledwalrus.skriptgui.elements.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Example;
import org.bukkit.entity.Player;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;

import io.github.apickledwalrus.skriptgui.SkriptGUI;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Has GUI")
@Description("Checks whether a player has a GUI open.")
@Example("""
	command /guiviewers:
		usage: A command for printing a list of all players with a GUI open.
		trigger:
			set {_viewers::*} to all players where [input has a gui]
			send "GUI Viewers: %{_viewers::*}%"
	""")
@Since("1.0.0")
public class CondHasGUI extends PropertyCondition<Player> {

	public static void register(SyntaxRegistry syntaxRegistry) {
		syntaxRegistry.register(SyntaxRegistry.CONDITION,
			infoBuilder(CondHasGUI.class, PropertyType.HAVE, "a gui [open]", "players")
				.supplier(CondHasGUI::new)
				.build());
	}

	@Override
	public boolean check(Player player) {
		return SkriptGUI.getGUIManager().getGUI(player.getOpenInventory().getTopInventory()) != null;
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
