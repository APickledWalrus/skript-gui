package me.tuke.sktuke.register;

import ch.njol.skript.lang.ParseContext;
import me.tuke.sktuke.TuSKe;
import me.tuke.sktuke.manager.customenchantment.CEnchant;
import me.tuke.sktuke.manager.customenchantment.CustomEnchantment;
import me.tuke.sktuke.manager.customenchantment.EnchantManager;
import me.tuke.sktuke.manager.gui.v2.GUIInventory;
import me.tuke.sktuke.util.EnumType;
import me.tuke.sktuke.util.Regex;
import me.tuke.sktuke.util.SimpleType;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.*;

import javax.annotation.Nullable;
import java.util.regex.Pattern;

/**
 * @author Tuke_Nuke on 06/04/2017
 */
public class TuSKeTypes {
	static {
		new EnumType(InventoryType.class, "inventorytype", "inventory ?types?");
		new EnumType(ClickType.class, "clicktype", "click ?(actions|types)");
		new EnumType(InventoryAction.class, "inventoryaction", "inventory ?actions?");
		new EnumType(InventoryType.SlotType.class, "slottype", "slot ?types?");
		new EnumType(EntityDamageEvent.DamageModifier.class, "damagemodifier", "damage ?modifiers?");
		new SimpleType<Recipe>(Recipe.class, "recipe", "recipes?"){

				@Override
				public String toString(Recipe r, int arg1) {
					if (r instanceof ShapelessRecipe)
						return "shapeless recipe";
					else if (r instanceof ShapedRecipe)
						return "shaped recipe";
					else if (r instanceof FurnaceRecipe)
						return "furnace recipe";
					return null;
				}

				@Override
				public String toVariableNameString(Recipe r) {

					if (r instanceof ShapelessRecipe)
						return "shapelessrecipe:" + r.toString().split("@")[1];
					else if (r instanceof ShapedRecipe)
						return "shapedrecipe:" + r.toString().split("@")[1];
					else if (r instanceof FurnaceRecipe)
						return "furnacerecipe:" + r.toString().split("@")[1];
					return null;

				}
				@Override
				public Class<?>[] acceptChange(ChangeMode mode) {
					if (mode == ChangeMode.RESET || mode == ChangeMode.DELETE)
						return new Class[]{Recipe.class};
					return null;
				}
				@Override
				public void change(Recipe[] recipes, Object[] set, ChangeMode mode) {
					if (mode == ChangeMode.DELETE)
						TuSKe.getRecipeManager().removeRecipe(recipes);
					else if (mode == ChangeMode.RESET)
						TuSKe.getRecipeManager().removeCustomRecipe(recipes);
				}
		};
		new SimpleType<Pattern>(Pattern.class, "regex", "reg(ular )?ex(pressions?|es)?", "Regular expression"){
			@Override
			@Nullable
			public Pattern parse(String s, ParseContext arg1) {
				if (arg1 == ParseContext.COMMAND){
					return Regex.getInstance().parse(s);
				}
				return null;
			}
			@Override
			public boolean canParse(ParseContext arg1){
				return arg1 == ParseContext.COMMAND;
			}
			@Override
			public String toString(Pattern reg, int arg1) {
				return reg.pattern();
			}

			@Override
			public String toVariableNameString(Pattern reg) {
				return reg.pattern();
			}};
		new SimpleType<CEnchant>(CEnchant.class, "customenchantment", "custom ?enchantments?"){
			@Override
			@Nullable
			public CEnchant parse(String s, ParseContext arg1) {
				int l = 0;
				if (s.matches(".*\\s{1,}\\d{1,}$")){
					l = Integer.valueOf(s.split(" ")[s.split(" ").length-1]);
					s = s.replace(" " + l,"");
				}
				return (EnchantManager.isCustomByID(s)) ? new CEnchant(CustomEnchantment.getByID(s), l) : null;
			}

			@Override
			public String toString(CEnchant ce, int arg1) {
				return ce.getEnchant().getId();
			}

			@Override
			public String toVariableNameString(CEnchant ce) {
				return "ce:" + ce.getEnchant().getId();
			}
		};
		new SimpleType<GUIInventory>(GUIInventory.class, "fromGui", "gui ?inventor(y|ies)") {
			@Override
			public String toString(GUIInventory arg0, int arg1) {
				return "gui inventory with "+ EnumType.toString(arg0.getInventory().getType()) +" inventory shape \"" + arg0.getRawShape() + "\"";
			}

			@Override
			public String toVariableNameString(GUIInventory arg0) {
				return "gui-" + arg0.hashCode();
			}
		};
	}
}
