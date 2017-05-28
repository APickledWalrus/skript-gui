package me.tuke.sktuke.register;

import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.util.EnumUtils;
import me.tuke.sktuke.TuSKe;
import me.tuke.sktuke.manager.customenchantment.CEnchant;
import me.tuke.sktuke.manager.customenchantment.CustomEnchantment;
import me.tuke.sktuke.manager.customenchantment.EnchantManager;
import me.tuke.sktuke.manager.gui.v2.GUIInventory;
import me.tuke.sktuke.util.EnumType;
import me.tuke.sktuke.util.ReflectionUtils;
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
		new EnumType(InventoryType.class, "inventorytype", "inventory ?types?")
				.name("Inventory Type")
				.description("Represents the type of an inventory. {{effects|MakeGUI|TuSKe}} and {{expressions|BlankInventory|SkQuery}} uses in their expressions.")
				.examples(
						"#SkQuery",
						"open inventory of hopper to player",
						"#TuSKe",
						"open virtual hopper named \"Tittle\" to player")
				.since("1.6.9.7");
		new EnumType(ClickType.class, "clicktype", "click ?(actions|types)", "click actions")
				.name("Click Type/Action")
				.description("Represents a click type of a inventory click event.")
				.examples(
						"on right click with compass:",
						"\twait a tick",
						"\tcreate new gui with virtual chest: #TuSKe",
						"\t\tmake gui slot 0 with stonebrick:",
						"\t\t\tif gui-click-type is left or right:",
						"\t\t\t\tgive gui-clicked-item to player",
						"\topen last created gui to player",
						" ",
						"on inventory click:",
						"\tif click action is left mouse button or right mouse button: #Bensku's fork",
						"\t\tgive event-slot to player",
						"\t\tcancel event")
				.since("1.6.2");
		new EnumType(InventoryAction.class, "inventoryaction", "inventory ?actions?", "inventory actions")
				.name("Inventory Action")
				.description("It represents a inventory action of a inventory click event.")
				.examples(
						"command /gui:",
						"\ttrigger:",
						"\t\tcreate new gui with virtual chest: #TuSKe",
						"\t\t\tmake gui slot 0 with stonebrick:",
						"\t\t\t\tif gui-inventory-action is move to other inventory:",
						"\t\t\t\t\tsend \"You can take that item!\"",
						"\t\topen last created gui to player",
						" ",
						"on inventory click:",
						"\tif inventory action is move to other inventory:",
						"\t\tsend \"You can't take that item!\"",
						"\t\tcancel event")
				.since("1.7.5");
		new EnumType(InventoryType.SlotType.class, "slottype", "slot ?types?")
				.name("Slot Type")
				.description("It represents a type of a inventory slot")
				.examples(
						"command /gui:",
						"\ttrigger:",
						"\t\tcreate new gui with virtual workbench: #TuSKe",
						"\t\t\tmake gui slot 0 with diamond sword:",
						"\t\t\t\tif gui-slot-type is result slot:",
						"\t\t\t\t\tset gui-clicked-item to air",
						"\t\topen last created gui to player")
				.since("1.7.5");
		if (ReflectionUtils.hasClass("org.bukkit.event.entity.EntityDamageEvent.DamageModifier"))
			new EnumType(EntityDamageEvent.DamageModifier.class, "damagemodifier", "damage ?modifiers?")
					.name("Damage Modifier")
					.description("Deprecated: It might be removed by Bukkit in 1.12 or higher. See more about it https://www.spigotmc.org/threads/194446",
							"It represents the damage modifiers in {{events|OnDamage|damage event}} that decrease the final damage. For example, if a player receives damage when he is wearing a armor, `damage armor` would return how much it was reduced.")
					.examples("on damage:",
							"\tif armor damage is more than 5:",
							"\t\treduce armor damage by 3")
					.since("1.7.1");
		new SimpleType<Recipe>(Recipe.class, "recipe", "recipes?") {
			@Override
			public boolean canParse(ParseContext pc) {
				return false;
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
			}}
				.name("Recipe")
				.description("A recipe contains the the ingredients list and the result item. Can only be get by {{expressions|Recipes|recipes's expression}}.")
				.examples(
						"loop recipes of {_item}:",
						"\tif \"%loop-recipe%\" is \"furnace recipe\":",
						"\t\tsend \"You have to make this recipe in a furnace.\"")
				.since("1.0.7");
		new SimpleType<Pattern>(Pattern.class, "regex", "reg(ular )?ex(pressions?|es)?", "Regular expression") {
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
			}}
				.name("Regex")
				.description("Represents a regex object. For now, it won't have any usage but only for test the pattern. It will have more usage in future versions.")
				.examples(
						"set {_regex} to \"(\\d+(\\.\\d+)*\" parsed as regex",
						"if regex error is set: #It will case there is a missing parentheses at the end.",
						"\tsend \"A error occurred with the regex pattern. Details:\"",
						"\tsend last regex parser error",
						"\t#It will send a formatted strings like showing the errors. For example:",
						"\t#Unclosed group near index 12",
						"\t#(\\d+(\\.\\d+)*",
						"\t#             ^")
				.since("1.7.1");
		new SimpleType<CEnchant>(CEnchant.class, "customenchantment", "custom ?enchantments?") {
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
			}}
				.name("Custom Enchantment")
				.description("It represents a custom enchantment. The values depends of the id name of {{effects|RegisterEnchantment|registered enchantment}}.")
				.examples("if \"Soulbound\" parsed as custom enchantment is set: #checks if the custom enchantment exists.")
				.since("1.5.1");
		new SimpleType<GUIInventory>(GUIInventory.class, "guiinventory", "gui( )?inventor(y|ies)") {
			@Override
			public boolean canParse(ParseContext pc) {
				return false;
			}

			@Override
			public String toString(GUIInventory arg0, int arg1) {
				return "gui inventory with "+ EnumType.toString(arg0.getInventory().getType()) +" inventory shape \"" + arg0.getRawShape() + "\"";
			}

			@Override
			public String toVariableNameString(GUIInventory arg0) {
				return "gui-" + arg0.hashCode();
			}}
				.name("GUI")
				.description("It represents a gui inventory, where the player can take items away from the inventory. It can be created only with {{effects|MakeGUI|advanced gui effect}}.")
				.examples(
						"/command gui:",
						"\ttrigger:",
						"\t\tcreate new gui with virtual chest named \"Hub\" with 3 rows: #Create a new gui based in a inventory",
						"\t\t\tmake gui slot 13 with cake named \"&e&lLobby\": #Format slot with a given item",
						"\t\t\t\tmake player execute command \"server Lobby\" #Code to be executed when the player clicks on gui",
						"\t\topen last gui to player #Open the last created gui to the player");
	}
}
