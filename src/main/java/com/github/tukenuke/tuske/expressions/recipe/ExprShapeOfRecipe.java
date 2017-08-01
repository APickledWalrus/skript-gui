package com.github.tukenuke.tuske.expressions.recipe;

import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.event.Event;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprShapeOfRecipe extends SimpleExpression<String>{
	static {
		Registry.newProperty(ExprShapeOfRecipe.class, "shape", "recipe");
	}

	private Expression<Recipe> rec;
	
	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		rec = (Expression<Recipe>) arg[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "shape of " + rec.toString(arg0, arg1);
	}

	@Override
	@Nullable
	protected String[] get(Event e) {
		Recipe recipe = rec.getSingle(e);
		if (recipe != null && recipe instanceof ShapedRecipe)
			return ((ShapedRecipe)recipe).getShape();
		return null;
	}

}
