package io.github.apickledwalrus.skriptgui.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import io.github.apickledwalrus.skriptgui.SkriptGUI;
import io.github.apickledwalrus.skriptgui.SkriptUtils;
import io.github.apickledwalrus.skriptgui.elements.sections.SecCreateGUI;
import io.github.apickledwalrus.skriptgui.elements.sections.SecGUIOpenClose;
import io.github.apickledwalrus.skriptgui.elements.sections.SecMakeGUI;
import io.github.apickledwalrus.skriptgui.gui.GUI;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Next GUI Slot")
@Description("An expression that returns the number/character of the next open slot in a GUI.")
@Examples("make the next gui slot with dirt named \"Slot: %the next gui slot%\"")
@Since("1.3")
public class ExprNextGUISlot extends SimpleExpression<Character> {

	private @Nullable Expression<GUI> guis;

	static {
		Skript.registerExpression(ExprNextGUISlot.class, Character.class, ExpressionType.SIMPLE,
				"%guiinventorys%'[s] next gui slot[s]",
				"[the] next gui slot[s] of %guiinventorys%",
				"[the] next gui slot"
		);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		if (matchedPattern == 2) {
			if (!SkriptUtils.isSection(getParser(), SecCreateGUI.class, SecMakeGUI.class, SecGUIOpenClose.class)) {
				Skript.error("The 'next gui slot' expression must have a GUI specified unless it is used in a GUI section.");
				return false;
			}
			guis = null;
		} else {
			guis = (Expression<GUI>) exprs[0];
		}
		return true;
	}

	@Override
	@Nullable
	protected Character[] get(Event event) {
		if (guis == null) {
			GUI gui = SkriptGUI.getGUIManager().getGUI(event);
			if (gui != null) {
				return new Character[]{gui.nextSlot()};
			}
		}

		GUI[] guis = this.guis.getArray(event);
		int size = guis.length;
		Character[] slots = new Character[size];
		for (int i = 0; i < size; i++) {
			slots[i] = guis[i].nextSlot();
		}
		return slots;
	}

	@Override
	public boolean isSingle() {
		return guis == null || guis.isSingle();
	}

	@Override
	public Class<? extends Character> getReturnType() {
		return Character.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		if (guis != null) {
			return "the next gui slot" + (guis.isSingle() ? "" : "s") + " of " + guis.toString(event, debug);
		} else {
			return "the next gui slot";
		}
	}

}
