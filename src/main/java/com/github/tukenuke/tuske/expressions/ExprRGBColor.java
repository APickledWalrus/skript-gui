package com.github.tukenuke.tuske.expressions;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import com.github.tukenuke.tuske.TuSKe;
import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import javax.annotation.Nullable;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Color;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

@Name("RGB Color")
@Description({
		"Returns the rgb color of {{types|ItemStack|item stack}} or {{types|Color|color}}. You can set these values only for item stacks, in this case, leather armors.",
		"The RGB color returns a list with 3 numbers and the other expressions returns which one separated."})
@Examples({
		"set {_PlayerEquipaments::*} to player's helmet, player's chestplate, player's leggings and player's boots #Must be leather armor",
		" ",
		"set rgb color of {_PlayerEquipaments::*} to rgb of color red",
		"add 1 to red color of {_PlayerEquipaments::*}",
		"remove 1 from green color of {_PlayerEquipaments::*}",
		"set blue color of {_PlayerEquipaments::*} to 30"})
@Since("1.5.3 (single value of items), 1.6 (list values of items and color)")
public class ExprRGBColor extends SimpleExpression<Integer>{
	static {
		Registry.newProperty(ExprRGBColor.class, "R[ed, ]G[reen and ]B[blue] [colo[u]r[s]]", "-itemstacks/colors");
	}

	private Expression<?> i;
	@Override
	public Class<? extends Integer> getReturnType() {
		return Integer.class;
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		i = arg[0].getConvertedExpression(Object.class);
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "the RGB color of the " + i.toString(arg0, arg1);
	}

	@Override
	@Nullable
	protected Integer[] get(@Nullable Event e) {
		Object[] array = i.getArray(e);
		//just some safe check
		if (array != null && array.length > 0 && array[0] != null){
			int red = 0;
			int green = 0;
			int blue = 0;
			//It works only for the first one in expression
			if (array[0] instanceof Color){
				red = ((Color)array[0]).getBukkitColor().getRed();
				green = ((Color)array[0]).getBukkitColor().getGreen();
				blue = ((Color)array[0]).getBukkitColor().getBlue();
			} else if (array[0] instanceof ItemStack || array[0] instanceof ItemType){
				ItemMeta im = array[0] instanceof ItemType ? ((ItemType)array[0]).getRandom().getItemMeta() :
						((ItemStack)array[0]).getItemMeta();
				if (im == null || !(im instanceof LeatherArmorMeta))
					return null;
				red = ((LeatherArmorMeta) im).getColor().getRed();;
				green = ((LeatherArmorMeta) im).getColor().getGreen();;
				blue = ((LeatherArmorMeta) im).getColor().getBlue();
			}
			return new Integer[] {red, green, blue};
		}
		return null;
	}
	public void change(Event e, Object[] delta, Changer.ChangeMode mode){
		Object[] array = i.getArray(e);
		if (array == null || array.length == 0)
			return;
		if (delta != null && delta.length == 3){
			int red = ((Number)delta[0]).intValue();
			int green = ((Number)delta[1]).intValue();
			int blue = ((Number)delta[2]).intValue();
			if (red < 0 || red > 255)
				red = (red < 0) ? 0 : 255;
			if (green < 0 || green> 255)
				green = (green < 0) ? 0 : 255;
			if (blue < 0 || blue > 255)
				blue = (blue < 0) ? 0 : 255;
			org.bukkit.Color color = org.bukkit.Color.fromRGB(red, green, blue);
			for (Object obj : array){
				if (obj == null || !(obj instanceof ItemStack || obj instanceof ItemType))
					continue;
				ItemMeta meta = obj instanceof ItemStack ? ((ItemStack) obj).getItemMeta() : ((ItemType)obj).getRandom().getItemMeta();
				TuSKe.debug(meta);
				if (meta == null || !(meta instanceof LeatherArmorMeta))
					continue;
				((LeatherArmorMeta) meta).setColor(color);
				if (obj instanceof ItemStack)
					((ItemStack) obj).setItemMeta(meta);
				else
					((ItemType) obj).setItemMeta(meta);
			}
		}
	}
	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode == ChangeMode.SET)
			return CollectionUtils.array(Number[].class);
		return null;
	}

}
