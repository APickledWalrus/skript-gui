package me.tuke.sktuke.expressions;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.inventory.Recipe;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprAllRecipes extends SimpleExpression<Recipe>{

	@Override
	public Class<? extends Recipe> getReturnType() {
		// TODO Auto-generated method stub
		return Recipe.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public boolean init(Expression<?>[] arg0, int arg1, Kleenean arg2, ParseResult arg3) {
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "all recipes";
	}

	@Override
	@Nullable
	protected Recipe[] get(Event arg0) {
		List<Recipe> rec = Lists.newArrayList(Bukkit.recipeIterator());
		return rec.toArray(new Recipe[rec.size()]);
	}

}
