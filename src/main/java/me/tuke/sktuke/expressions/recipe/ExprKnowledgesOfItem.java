package me.tuke.sktuke.expressions.recipe;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.google.common.collect.Lists;
import me.tuke.sktuke.TuSKe;
import me.tuke.sktuke.manager.recipe.RecipeManager;
import me.tuke.sktuke.util.Registry;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.KnowledgeBookMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Tuke_Nuke on 26/06/2017
 */
@Name("Knowledge Recipes")
@Description("It returns a list of recipes of a knowledge book. You can either set, add and remove recipes, and clear/delete them all.")
@Examples({
		"set {_recipes::*} to knowledge recipes of held item #The held item must a be knowledge book",
		"add all recipes to knowledges of {_book}",
		"remove recipes of held item from knowledges of {_book}",
		"clear knowledges of held item"})
@Since("1.8")
public class ExprKnowledgesOfItem extends SimpleExpression<Recipe> {
	static {
		if (Skript.isRunningMinecraft(1, 12))
			Registry.newProperty(ExprKnowledgesOfItem.class, "knowledge(s| recipes)", "itemstack");
	}

	private Expression<ItemStack> item;

	@Override
	protected Recipe[] get(Event event) {
		List<Recipe> recipes = new ArrayList<>();
		RecipeManager rm = TuSKe.getRecipeManager();
		for (ItemStack item : item.getArray(event)) {
			if (item != null /*&& item.getType() == Material.KNOWLEDGE_BOOK */&& item.hasItemMeta()) {
				KnowledgeBookMeta meta = (KnowledgeBookMeta) item.getItemMeta();
				for (NamespacedKey key : meta.getRecipes()) {
					Recipe r = rm.getRecipeFromKey(key);
					if (r != null) //Almost always true, unless a recipe was unregistered.
						recipes.add(r);
				}
			}
		}
		return recipes.toArray(new Recipe[recipes.size()]);
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@Override
	public Class<? extends Recipe> getReturnType() {
		return Recipe.class;
	}

	@Override
	public String toString(Event event, boolean b) {
		return "knowledges of " + item.toString(event, b);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
		item = (Expression<ItemStack>) arg[0];
;		return true;
	}

	@Override
	public Class<?>[] acceptChange(Changer.ChangeMode mode) {
		if (mode != Changer.ChangeMode.REMOVE_ALL && mode != Changer.ChangeMode.RESET)
			return new Class<?>[]{Recipe[].class};
		return null;
	}

	@Override
	public void change(Event e, Object[] delta, Changer.ChangeMode mode) {
		for (ItemStack item : item.getArray(e)) {
			if (item == null || item.getType() != Material.KNOWLEDGE_BOOK)
				continue;
			KnowledgeBookMeta meta = (KnowledgeBookMeta) (item.hasItemMeta() ? item.getItemMeta() :
					Bukkit.getItemFactory().getItemMeta(Material.KNOWLEDGE_BOOK));
			List<NamespacedKey> list = new ArrayList<>(meta.getRecipes());
			if (mode != Changer.ChangeMode.ADD && mode != Changer.ChangeMode.REMOVE)
				list.clear();
			if (delta != null)
				for (Object recipe : delta)
					if (recipe instanceof Keyed)
						switch (mode) {
							case REMOVE: list.remove(((Keyed) recipe).getKey()); break;
							case ADD:
							case SET: list.add(((Keyed) recipe).getKey()); break;
						}
			//A workaround since the method setRecipes is only clearing all recipes
			//But not adding them. Maybe a bukkit issue?
			meta.setRecipes(new ArrayList<>());
			meta.addRecipe(list.toArray(new NamespacedKey[list.size()]));
			item.setItemMeta(meta);
		}
	}
}


