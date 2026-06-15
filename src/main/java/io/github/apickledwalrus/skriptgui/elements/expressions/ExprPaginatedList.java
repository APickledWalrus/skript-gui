package io.github.apickledwalrus.skriptgui.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.lang.reflect.Array;

@Name("Paginated List")
@Description("""
	This expression is used to interpret a list of values as pages.
	For example, for a list with 20 values, the second page (assuming 10 items per page) would contain elements 11 through 20.
	""")
@Example("""
	# The second set of 36 items in the {_items::*} list.
	# This represents the elements from indices 37 to 72.
	set {_pages::2::*} to page 2 of {_items::*} with 36 values
	""")
@Since("1.1.0")
public class ExprPaginatedList extends SimpleExpression<Object> {

	public static void register(SyntaxRegistry syntaxRegistry) {
		syntaxRegistry.register(SyntaxRegistry.EXPRESSION,
			SyntaxInfo.Expression.builder(ExprPaginatedList.class, Object.class)
				.supplier(ExprPaginatedList::new)
				.addPattern("page[s] %numbers% of %objects% with %number% (lines|values)")
				.build());
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

		Object[] contents = this.contents.getArray(event);
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
	public Class<?>[] possibleReturnTypes() {
		return contents.possibleReturnTypes();
	}

	@Override
	public boolean canReturn(Class<?> returnType) {
		return contents.canReturn(returnType);
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return new SyntaxStringBuilder(event, debug)
			.append("page" + (pages.isSingle() ? "s" : ""), pages, "of", contents, "with", lines, "lines")
			.toString();
	}

}
