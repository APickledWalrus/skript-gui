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
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;

@Name("Paginated List")
@Description("Returns the \"pages\" of a list based on the given number of lines per page.")
@Examples({
		"# The second set of 36 items in the {_items::*} list. This represents the elements from indexes 37 to 72",
		"set {_pages::2::*} to page 2 of {_items::*} with 36 lines"
})
@Since("1.1.0")
public class ExprPaginatedList extends SimpleExpression<Object> {

	static {
		Skript.registerExpression(ExprPaginatedList.class, Object.class, ExpressionType.COMBINED,
				"page[s] %numbers% of %objects% with %number% lines"
		);
	}

	private Expression<Number> pages;
	private Expression<?> contents;
	private Expression<Number> lines;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		pages = (Expression<Number>) exprs[0];
		contents = LiteralUtils.defendExpression(exprs[1]);
		if (contents.isSingle()) {
			Skript.error("You cannot paginate a single value.");
			return false;
		}
		lines = (Expression<Number>) exprs[2];
		return LiteralUtils.canInitSafely(contents);
	}

	@Override
	protected Object[] get(Event event) {
		Integer[] pages = this.pages.stream(event)
				.map(Number::intValue)
				.filter(page -> page >= 1)
				.toArray(Integer[]::new);
		if (pages.length == 0) {
			return new Object[0];
		}

		int lines = this.lines.getOptionalSingle(event).orElse(0).intValue();
		if (lines < 1) {
			return new Object[0];
		}

		Object[] contents = this.contents.getAll(event);
		if (contents.length == 0) {
			return new Object[0];
		}

		Object[] values = (Object[]) Array.newInstance(getReturnType(), pages.length * lines);
		for (int page : pages) {
			// map page to starting point
			page = (page - 1) * lines;

			// find end point
			int max = page + lines;
			if (max > contents.length) {
				max = contents.length;
			}

			// copy contents over
			System.arraycopy(contents, page, values, page, max - page);
		}

		return values;
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@Override
	public Class<?> getReturnType() {
		return contents.getReturnType();
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "page(s) " + pages.toString(event, debug) +
				" of " + contents.toString(event, debug) +
				" with " + lines.toString(event, debug) + " lines";
	}

}
