package io.github.apickledwalrus.skriptgui.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
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
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Next GUI Slot")
@Description("""
	Obtains the next open slot of a GUI.
	This is a single character representing the next available character of the shape.
	""")
@Example("make the next gui slot with dirt named \"Slot: %the next gui slot%\"")
@Since("1.3.0")
public class ExprNextSlot extends SimpleExpression<String> {

	public static void register(SyntaxRegistry syntaxRegistry) {
		syntaxRegistry.register(SyntaxRegistry.EXPRESSION,
			SyntaxInfo.Expression.builder(ExprNextSlot.class, String.class)
				.supplier(ExprNextSlot::new)
				.addPatterns(PropertyExpression.getPatterns("next gui slot[s]", "guiinventories"))
				.addPattern("[the] next gui slot")
				.build());
	}

	private @Nullable Expression<GUI> guis;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		if (matchedPattern == 2) {
			if (!SkriptUtils.isSection(getParser(), SecCreateGUI.class, SecMakeGUI.class, SecGUIOpenClose.class)) {
				Skript.error("The 'next gui slot' expression must have a GUI specified unless it is used in a GUI section.");
				return false;
			}
			guis = null;
		} else {
			//noinspection unchecked
			guis = (Expression<GUI>) exprs[0];
		}
		return true;
	}

	@Override
	protected String @Nullable [] get(Event event) {
		if (guis == null) {
			GUI gui = SkriptGUI.getGUIManager().getGUI(event);
			if (gui != null) {
				return new String[]{String.valueOf(gui.nextSlot())};
			}
		}

		GUI[] guis = this.guis.getArray(event);
		int size = guis.length;
		String[] slots = new String[size];
		for (int i = 0; i < size; i++) {
			slots[i] = String.valueOf(guis[i].nextSlot());
		}
		return slots;
	}

	@Override
	public boolean isSingle() {
		return guis == null || guis.isSingle();
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		if (guis != null) {
			return "the next gui slot" + (guis.isSingle() ? "" : "s") + " of " + guis.toString(event, debug);
		}
		return "the next gui slot";
	}

}
