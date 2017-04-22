package me.tuke.sktuke.expressions.recipe;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ch.njol.skript.doc.Since;
import me.tuke.sktuke.util.Registry;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import me.tuke.sktuke.TuSKe;

@Name("All Recipes")
@Description("Returns all server recipes. You can get the {{expressions|RecipeIngredients|ingredients}} and the {{expressions|RecipeResult|result item}}.")
@Examples({
		"command /recipes <integer=1>:",
		"\tusage: /recipes <page>",
		"\ttrigger:",
		"\t\tset {_list::*} to page arg of all recipes with 10 lines",
		"\t\tloop {_list::*}:",
		"\t\t\tsend \"Ingredients to create %result item of loop-value%: %ingredients of loop-value%\""})
@Since("1.6.8, 1.7.5 (recipe type list)")
public class ExprAllRecipes extends SimpleExpression<Recipe>{
	static {
		Registry.newSimple(ExprAllRecipes.class, "[all] [registred] (shaped|shapeless|furnace|) recipes");
	}

	int type = 0;
	@Override
	public Class<? extends Recipe> getReturnType() {
		return Recipe.class;
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@Override
	public boolean init(Expression<?>[] arg0, int arg1, Kleenean arg2, ParseResult arg3) {
		if (arg3.expr.toLowerCase().contains("shaped"))
			type = 1;
		else if (arg3.expr.toLowerCase().contains("shapeless"))
			type = 2;
		else if (arg3.expr.toLowerCase().contains("furnace"))
			type = 3;
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "all recipes";
	}

	@Override
	@Nullable
	protected Recipe[] get(Event arg0) {		
		List<Recipe> rec;
		if (type == 0){
			 rec = Lists.newArrayList(Bukkit.recipeIterator());
		} else {
			final Iterator<Recipe> it = Bukkit.recipeIterator();
			rec = new ArrayList<Recipe>();
			while (it.hasNext()){
				Recipe r = it.next();
				if ((type == 1 && r instanceof ShapedRecipe) || (type == 2 && r instanceof ShapelessRecipe) || (type == 3 && r instanceof FurnaceRecipe)){
					rec.add(r);
				}				
			}
		}
		return rec.toArray(new Recipe[rec.size()]);
	}
	@Override
	public void change(Event e, Object[] delta, Changer.ChangeMode mode){
		switch (mode){
			case DELETE: Bukkit.clearRecipes(); break;
			case RESET: Bukkit.resetRecipes(); break;
			case REMOVE: TuSKe.getRecipeManager().removeRecipe((Recipe[])delta); return;
			default: break;
		}
		TuSKe.getRecipeManager().clearRecipes();
	}
	
	@Override
	public Iterator<Recipe> iterator(Event e){
		if (type == 0)
			return Bukkit.recipeIterator();
		return new Iterator<Recipe>(){
			Iterator<Recipe> recipes = Bukkit.recipeIterator();
			Recipe next;
			@Override
			public boolean hasNext() {
				next = null;
				while (recipes.hasNext()){
					Recipe r = recipes.next();
					if ((type == 1 && r instanceof ShapedRecipe) || (type == 2 && r instanceof ShapelessRecipe) || (type == 3 && r instanceof FurnaceRecipe)){
						next = r;
						break;
					}
				}
				return next != null;
			}

			@Override
			public Recipe next() {
				return next;
			}};
		
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode == ChangeMode.DELETE || mode == ChangeMode.RESET || mode == ChangeMode.REMOVE)
			return CollectionUtils.array(Recipe[].class);
		return null;
		
	}

}
