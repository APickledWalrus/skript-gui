package me.tuke.sktuke.register;

import ch.njol.skript.lang.ParseContext;
import me.tuke.sktuke.manager.customenchantment.CEnchant;
import me.tuke.sktuke.manager.customenchantment.CustomEnchantment;
import me.tuke.sktuke.manager.customenchantment.EnchantManager;
import me.tuke.sktuke.manager.gui.v2.GUIInventory;
import me.tuke.sktuke.util.EnumType;
import me.tuke.sktuke.util.Regex;
import me.tuke.sktuke.util.SimpleType;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import javax.annotation.Nullable;

/**
 * @author Tuke_Nuke on 06/04/2017
 */
public class TuSKeTypes {
	static {
		new SimpleType<Recipe>(Recipe.class, "recipe", "recipes?"){
				@Override
				@Nullable
				public Recipe parse(String s, ParseContext arg1) {
					return null;
				}

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
				}};
		new EnumType(InventoryType.class, "inventorytype", "inventory ?types?");
		new EnumType(ClickType.class, "clicktype", "click ?(action|type)?");
		new SimpleType<Regex>(Regex.class, "regex", "reg(ular )?ex(pression)?", "Regular expression"){
			@Override
			@Nullable
			public Regex parse(String s, ParseContext arg1) {
				if (arg1 == ParseContext.COMMAND){
					Regex reg = new Regex(s);
					if (reg.isPatternParsed())
						return reg;
				}
				return null;
			}
			@Override
			public boolean canParse(ParseContext arg1){
				return arg1 == ParseContext.COMMAND;
			}
			@Override
			public String toString(Regex reg, int arg1) {
				return reg.getRegex();
			}

			@Override
			public String toVariableNameString(Regex reg) {
				return reg.getRegex();
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
		//1.7.1
		new EnumType(EntityDamageEvent.DamageModifier.class, "damagemodifier", "damage ?modifiers?");
		new SimpleType<GUIInventory>(GUIInventory.class, "gui", "gui ?inventor(y|ies)") {
			@Override
			public String toString(GUIInventory arg0, int arg1) {
				return "gui inventory";
			}

			@Override
			public String toVariableNameString(GUIInventory arg0) {
				return "gui-" + arg0.hashCode();
			}
		};
	}
}
