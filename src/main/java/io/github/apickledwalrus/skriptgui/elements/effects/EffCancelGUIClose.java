package io.github.apickledwalrus.skriptgui.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SectionSkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.util.Kleenean;
import io.github.apickledwalrus.skriptgui.SkriptGUI;
import io.github.apickledwalrus.skriptgui.SkriptUtils;
import io.github.apickledwalrus.skriptgui.elements.sections.SecGUIOpenClose;
import io.github.apickledwalrus.skriptgui.gui.GUI;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.Nullable;

@Name("Cancel GUI Close")
@Description({
		"Cancels or uncancels the closing of a GUI.",
		"This effect can be used within a GUI close section.",
		"A 1 tick delay is applied by this effect after the code has run."
})
@Examples({
		"create a gui with virtual chest inventory with 3 rows named \"My GUI\":",
		"\trun on gui close:",
		"\t\tcancel the gui closing"
})
@Since("1.2.0")
public class EffCancelGUIClose extends Effect {

	static {
		Skript.registerEffect(EffCancelGUIClose.class,
				"(:cancel|uncancel) [the] gui clos(e|ing)"
		);
	}

	private boolean cancel;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		ParserInstance parser = getParser();
		if (!parser.isCurrentEvent(InventoryCloseEvent.class) || !SkriptUtils.isSection(SecGUIOpenClose.class)) {
			Skript.error("Cancelling or uncancelling the closing of a GUI can only be done within a GUI close section.");
			return false;
		}
		cancel = parseResult.hasTag("cancel");
		return true;
	}

	@Override
	protected void execute(Event event) {
		GUI gui = SkriptGUI.getGUIManager().getGUI(event);
		if (gui != null) {
			gui.setCloseCancelled(cancel);
		}
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return (cancel ? "cancel" : "uncancel") + " the gui closing";
	}

}
