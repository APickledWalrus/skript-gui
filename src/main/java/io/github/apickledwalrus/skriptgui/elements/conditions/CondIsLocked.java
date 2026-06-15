package io.github.apickledwalrus.skriptgui.elements.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import io.github.apickledwalrus.skriptgui.gui.GUI;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Is GUI Locked")
@Description("Whether a GUI is locked or unlocked, which controls whether its items without actions can be removed.")
@Example("""
	if the player's gui is locked:
		send "You cannot remove items from this GUI!" to the player
	""")
@Since("1.4.0")
public class CondIsLocked extends PropertyCondition<GUI> {

	private boolean locked;

	public static void register(SyntaxRegistry syntaxRegistry) {
		String[] bePatterns = getPatterns(PropertyType.BE, "(:locked|unlocked)", "guiinventories");
		for (int i = 0; i < bePatterns.length; i++) {
			bePatterns[i] = "[gui[s]] " + bePatterns[i];
		}
		syntaxRegistry.register(SyntaxRegistry.CONDITION, SyntaxInfo.builder(CondIsLocked.class)
			.supplier(CondIsLocked::new)
			.addPatterns(bePatterns)
			.addPatterns("%guiinventories% allow[s] items to be removed",
				"%guiinventories% (disallow|prevent)[s] items [from] being removed")
			.build());
	}

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		this.locked = parseResult.hasTag("locked") || matchedPattern == 3;
		return super.init(expressions, matchedPattern, isDelayed, parseResult);
	}

	@Override
	public boolean check(GUI gui) {
		return gui.isRemovable() != locked;
	}

	@Override
	protected String getPropertyName() {
		return locked ? "locked" : "unlocked";
	}

}
