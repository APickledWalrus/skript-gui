package io.github.apickledwalrus.skriptgui.elements.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import io.github.apickledwalrus.skriptgui.gui.GUI;
import org.bukkit.event.Event;
import org.jspecify.annotations.Nullable;
import org.skriptlang.skript.lang.script.ScriptWarning;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("GUI Lock Status")
@Description("The lock status of a GUI, which controls whether its items without actions can be removed.")
@Example("set the lock status of the player's gui to true")
@Since("1.0.0, 1.3.0 (support outside of edit sections)")
public class ExprLockStatus extends SimplePropertyExpression<GUI, Boolean> {

	public static void register(SyntaxRegistry syntaxRegistry) {
		syntaxRegistry.register(SyntaxRegistry.EXPRESSION,
			infoBuilder(ExprLockStatus.class, Boolean.class, "[gui] lock status[es]", "guis", false)
				.supplier(ExprLockStatus::new)
				.build());
	}

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		String guis = expressions[0].toString(null, false);
		String warning = "This expression has been deprecated in favor of an equivalent Effect and Condition." +
			" For getting, use 'whether " + guis + " is locked'." +
			" For setting, use 'lock" + guis + "'.";
		ScriptWarning.printDeprecationWarning(warning);
		return super.init(expressions, matchedPattern, isDelayed, parseResult);
	}

	@Override
	public @Nullable Boolean convert(GUI gui) {
		return !gui.isRemovable(); // Not removable = locked
	}

	@Override
	public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
		return switch (mode) {
			case ADD, RESET -> CollectionUtils.array(Boolean.class);
			default -> null;
		};
	}

	@Override
	public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
		boolean removable = delta != null && !((boolean) delta[0]);
		for (GUI gui : getExpr().getArray(event)) {
			gui.setRemovable(removable);
		}
	}

	@Override
	public Class<? extends Boolean> getReturnType() {
		return Boolean.class;
	}

	@Override
	protected String getPropertyName() {
		return "lock status";
	}

}
