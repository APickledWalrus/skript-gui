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
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Lock/Unlock GUI")
@Description("Locks or unlocks a GUI, which controls whether its items without actions can be removed.")
@Example("unlock the player's gui")
@Since("1.4.0")
public class EffLock extends Effect {

	public static void register(SyntaxRegistry syntaxRegistry) {
		syntaxRegistry.register(SyntaxRegistry.EFFECT, SyntaxInfo.builder(EffLock.class)
			.supplier(EffLock::new)
			.addPatterns("(:unlock|:lock) [gui[s]] %guiinventories%",
				"allow items to be removed from %guiinventories%",
				"(disallow|prevent) items [from] being removed from %guiinventories%")
			.build());
	}

	private Expression<GUI> guis;
	private boolean removable;

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		//noinspection unchecked
		this.guis = (Expression<GUI>) expressions[0];
		this.removable = parseResult.hasTag("unlock") || matchedPattern == 1;
		return true;
	}

	@Override
	protected void execute(Event event) {
		for (GUI gui : this.guis.getArray(event)) {
			gui.setRemovable(removable);
		}
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return new SyntaxStringBuilder(event, debug)
			.append(removable ? "unlock" : "lock", guis)
			.toString();
	}

}
