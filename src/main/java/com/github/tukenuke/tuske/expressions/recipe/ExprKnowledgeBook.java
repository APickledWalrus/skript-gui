package com.github.tukenuke.tuske.expressions.recipe;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.KnowledgeBookMeta;

/**
 * @author Tuke_Nuke on 26/06/2017
 */
@Name("Knowledge Book")
@Description("It returns a knowledge book (1.12+ only) with given recipes.")
@Examples({
		"#Make sure you have an updated aliases containing 'knowledge book'.",
		"give knowledge book with all recipes to player",
		"give knowledge book with all recipes of held item to player"})
@Since("1.8")
public class ExprKnowledgeBook extends SimpleExpression<ItemStack>{
	static {
		if (Skript.isRunningMinecraft(1, 12))
			Registry.newCombined(ExprKnowledgeBook.class, "%itemstack% with [recipes] %recipes%");
	}

	private Expression<ItemStack> item;
	private Expression<Recipe> recipes;

	@Override
	protected ItemStack[] get(Event event) {
		ItemStack item = this.item.getSingle(event);
		if (item != null && item.getType() == Material.KNOWLEDGE_BOOK) {
			KnowledgeBookMeta meta = (KnowledgeBookMeta) (item.hasItemMeta() ? item.getItemMeta().clone() : Bukkit.getItemFactory().getItemMeta(Material.KNOWLEDGE_BOOK));
			Recipe[] recipes = this.recipes.getArray(event);
			for (Recipe recipe : recipes) {
				if (recipe instanceof Keyed)
					meta.addRecipe(((Keyed) recipe).getKey());
			}
			item = item.clone();
			item.setItemMeta(meta);
			return new ItemStack[]{item};
		}
		return new ItemStack[0];
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends ItemStack> getReturnType() {
		return ItemStack.class;
	}

	@Override
	public String toString(Event event, boolean b) {
		return item.toString(event, b) + " with recipes " +  recipes.toString(event, b);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
		item = (Expression<ItemStack>) arg[0];
		recipes = (Expression<Recipe>) arg[1];
		return true;
	}
}
