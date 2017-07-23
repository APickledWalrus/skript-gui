package me.tuke.sktuke.expressions.recipe;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import me.tuke.sktuke.TuSKe;
import me.tuke.sktuke.util.Registry;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.Plugin;

/**
 * @author Tuke_Nuke on 30/06/2017
 */
@Name("Recipe Owner")
@Description("Starting from minecraft 1.12, recipes have now the register plugin. If it is a minecraft recipe, " +
		"it will return as `Minecraft`, if it is an old recipe plugin, it will return as `Bukkit` and everything else " +
		"will return the plugin's name. **Only for Shaped and Shapeless recipes**")
@Examples({
		"loop recipes of held item:",
		"\tif recipe owner of loop-recipe is \"Minecraft\":",
		"\t\tsend \"That's a vanilla recipe!\""})
@Since("1.8")
public class ExprRecipeOwner extends SimplePropertyExpression<Recipe, String> {
	static {
		Registry.newProperty(ExprRecipeOwner.class, "recipe owner", "recipes");
	}

	@Override
	protected String getPropertyName() {
		return "recipe owner";
	}

	@Override
	public String convert(Recipe recipe) {
		if (recipe instanceof Keyed) {
			String plugin = ((Keyed) recipe).getKey().getNamespace();
			switch (plugin) {
				case "bukkit":
					return "Bukkit";
				case "minecraft":
					return "Minecraft";
				case "tuske":
					return TuSKe.getInstance().getName();
				default:
					for (Plugin p : Bukkit.getPluginManager().getPlugins())
						if (p.getName().equalsIgnoreCase(plugin))
							return p.getName();
			}
		}
		return null;
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}
}
