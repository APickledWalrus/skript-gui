package io.github.apickledwalrus.skriptgui.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

import io.github.apickledwalrus.skriptgui.SkriptGUI;
import io.github.apickledwalrus.skriptgui.elements.sections.SecOnCloseGUI;
import io.github.apickledwalrus.skriptgui.util.EffectSection;

import org.bukkit.event.Event;

import org.eclipse.jdt.annotation.Nullable;

@Name("Cancel GUI Close")
@Description({"Cancels or uncancels the closing of a GUI.",
		" This effect can be used within a GUI close section.",
		" A 1 tick delay is applied by this effect."
})
@Examples({"create a gui with virtual chest inventory with 3 rows named \"My GUI\":",
		"\trun on gui close:",
		"\t\tcancel the gui closing"
})
@Since("1.2.0")
public class EffCancelGUIClose extends Effect {

	static {
		Skript.registerEffect(EffCancelGUIClose.class,
				"(cancel|1¦uncancel) [the] gui clos(e|ing)"
		);
	}

	private boolean cancel;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		if (!EffectSection.isCurrentSection(SecOnCloseGUI.class)) {
			Skript.error("Canceling or uncanceling the closing of a GUI can only be done within a GUI close section.");
			return false;
		}
		cancel = parseResult.mark == 0;
		return true;
	}

	@Override
	protected void execute(Event e) {
		SkriptGUI.getGUIManager().getGUIEvent(e).setCloseCancelled(cancel);
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return (cancel ? "cancel" : "uncancel") + " the gui closing";
	}

}
