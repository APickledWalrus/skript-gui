package com.github.tukenuke.tuske.expressions;

import ch.njol.skript.aliases.ItemType;
import com.github.tukenuke.tuske.TuSKe;
import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
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
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

public class ExprLeatherColor extends SimpleExpression<Integer>{
	static {
		Registry.newProperty(ExprLeatherColor.class, "[leather] (0¦red|1¦green|2¦blue) colo[u]r", "-itemstacks/colors");
	}

	private Expression<?> i;
	private int rgb;
	@Override
	public Class<? extends Integer> getReturnType() {
		return Integer.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		i = arg[0].getConvertedExpression(Object.class);
		this.rgb = arg3.mark;
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "leather " + ((rgb == 0) ? "red" : ((rgb == 1) ? "green" : "blue")) + " color of " + ((this.i instanceof ItemStack) ? "item stack" : "color");
	}

	@Override
	@Nullable
	protected Integer[] get(Event e) {
		Color color = null;
		Object[] array = i.getArray(e);
		if (array != null && array.length > 0 && array[0] != null){
			if (array[0] instanceof ItemStack || array[0] instanceof ItemType) {
				ItemMeta im = array[0] instanceof ItemStack ? ((ItemStack)array[0]).getItemMeta() :
						(ItemMeta) ((ItemType)array[0]).getItemMeta();
				if (im == null)
					im = Bukkit.getItemFactory().getItemMeta(Material.LEATHER_BOOTS);
				if (im instanceof LeatherArmorMeta)
					color = ((LeatherArmorMeta) im).getColor();
			} else if (array[0] instanceof ch.njol.skript.util.Color)
				color = ((ch.njol.skript.util.Color)array[0]).getBukkitColor();
			if (color != null)
				switch (rgb){
					case 0: return new Integer[] {color.getRed()};
					case 1: return new Integer[] {color.getGreen()};
					case 2: return new Integer[] {color.getBlue()};
				}
		}
		return null;
	}
	public void change(Event e, Object[] delta, Changer.ChangeMode mode){
		Object[] array = i.getArray(e);
		if (array != null && array.length > 0 && delta != null && delta.length > 0 && delta[0] != null){
			for (Object obj : array) {
				if (obj == null || !(obj instanceof ItemStack || obj instanceof ItemType))
					continue;
				ItemMeta im = obj instanceof ItemStack ? ((ItemStack) obj).getItemMeta() :
						((ItemType)obj).getRandom().getItemMeta();
				if (im == null)
					im = Bukkit.getItemFactory().getItemMeta(Material.LEATHER_BOOTS);
				if (im instanceof LeatherArmorMeta) {
					Color color = ((LeatherArmorMeta) im).getColor();
					int value = ((Number) delta[0]).intValue();
					int from = 0;
					switch (rgb) {
						case 0:
							from = color.getRed();
							break;
						case 1:
							from = color.getBlue();
							break;
						case 2:
							from = color.getGreen();
							break;
					}
					switch (mode) {
						case ADD:
							value += from;
							break;
						case REMOVE:
							value = from - value;
							break;
						default:
							break;
					}
					if (value < 0)
						value = 0;
					else if (value > 255)
						value = 255;
					switch (rgb) {
						case 0:
							color = Color.fromRGB(value, color.getGreen(), color.getBlue());
							break;
						case 1:
							color = Color.fromRGB(color.getRed(), value, color.getBlue());
							break;
						case 2:
							color = Color.fromRGB(color.getRed(), color.getGreen(), value);
							break;
					}
					((LeatherArmorMeta) im).setColor(color);
					if (obj instanceof ItemStack)
						((ItemStack) obj).setItemMeta(im);
					else
						((ItemType)obj).setItemMeta(im);
				}
			}
		}
		
		
	}
	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode == ChangeMode.SET || mode == ChangeMode.ADD || mode == ChangeMode.REMOVE)
			return CollectionUtils.array(Number.class);
		return null;
		
	}
}
