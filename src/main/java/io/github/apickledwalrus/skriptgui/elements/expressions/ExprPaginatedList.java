package io.github.apickledwalrus.skriptgui.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.UnparsedLiteral;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Paginated List")
@Description("Returns the \"pages\" of a list based on the given number of lines per page.")
@Examples({"# The SECOND set of 36 items in the \"guiItems\" list. This represents the elements from indexes 37 to 72",
			"set {_guiPage2::*} to page 2 of {_guiItems::*} with 36 lines"})
@Since("1.1.0")
public class ExprPaginatedList extends SimpleExpression<Object> {

	static {
		Skript.registerExpression(ExprPaginatedList.class, Object.class, ExpressionType.SIMPLE,
				"page[s] %numbers% of %objects% with %number% lines"
		);
	}

	@SuppressWarnings("NotNullFieldNotInitialized")
	private Expression<Number> pages;
	@Nullable
	private Expression<?> contents;
	@SuppressWarnings("NotNullFieldNotInitialized")
	private Expression<Number> lines;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		pages = (Expression<Number>) exprs[0];
		contents = exprs[1];
		if (contents instanceof UnparsedLiteral) {
			contents = contents.getConvertedExpression(Object.class);
			if (contents == null) {
				return false;
			}
		}
		lines = (Expression<Number>) exprs[2];
		return true;
	}

	@Override
	protected Object[] get(Event e) {
		Number[] pages = this.pages.getArray(e);
		Number l = this.lines.getSingle(e);
		int lines;
		if (l == null || (lines = l.intValue()) < 1 || pages.length == 0) {
			return new Object[0];
		}

		assert contents != null;
		Object[] contents = this.contents.getAll(e).clone();
		if (contents.length == 0) {
			return new Object[0];
		}

		List<Object> paginatedList = new ArrayList<>();
		for (Number p : pages) {
			int page = p.intValue();
			if (page < 1) {
				continue;
			} else if (page > 1) {
				page = (page - 1) * lines;
			} else {
				page = 0;
			}

			int max = page + lines;
			if (max > contents.length) {
				max = contents.length;
			}

			for (int i = page; i < max; i++) {
				if (contents[i] != null) {
					paginatedList.add(contents[i]);
				}
			}
		}

		return paginatedList.toArray();
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@Override
	public Class<?> getReturnType() {
		return Object.class;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		assert contents != null;
		return "page(s) " + pages.toString(e, debug) + " of " + contents.toString(e, debug) + " with " + lines.toString(e, debug) + " lines";
	}

}
