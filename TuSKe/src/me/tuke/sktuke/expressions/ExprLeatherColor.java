package me.tuke.sktuke.expressions;

import org.bukkit.Color;
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

	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		i = arg[0];
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
		Color color = Color.fromRGB(0, 0, 0);
		if (this.i.getArray(e).length > 0 && this.i.getArray(e)[0] != null){
			if (this.i.getArray(e)[0] instanceof ItemStack){
				ItemMeta im = ((ItemStack)this.i.getArray(e)[0]).getItemMeta();
				if (im instanceof LeatherArmorMeta){
					color = ((LeatherArmorMeta) im).getColor();
				}
			} else 
				color = ((ch.njol.skript.util.Color)this.i.getArray(e)[0]).getBukkitColor();
			switch (rgb){
			case 0: return new Integer[] {color.getRed()};
			case 1: return new Integer[] {color.getGreen()};
			case 2: return new Integer[] {color.getBlue()};
			}
			
		}
		return null;
	}
	public void change(Event e, Object[] delta, Changer.ChangeMode mode){
		if (this.i.getArray(e).length > 0 && delta != null && this.i.getArray(e)[0] instanceof ItemStack){
			for (ItemStack it : (ItemStack[])this.i.getArray(e))
				if (it != null){
					ItemMeta im = it.getItemMeta();
					if (im instanceof LeatherArmorMeta){
						Color color = ((LeatherArmorMeta) im).getColor();
						int value = ((Number)delta[0]).intValue();
						int from = 0;	
						switch (rgb){
							case 0: from = color.getRed(); break;
							case 1: from = color.getBlue(); break;
							case 2: from = color.getGreen(); break;
						}
						switch(mode){
							case ADD: value += from; break;
							case REMOVE: value = from - value; break;
							default: break;
						}	
						if (value < 0)
							value = 0;
						else if (value > 255)
							value = 255;
						switch (rgb){
							case 0: color = Color.fromRGB(value, color.getGreen(), color.getBlue()); break;
							case 1: color = Color.fromRGB(color.getRed(), value, color.getBlue()); break;
							case 2: color = Color.fromRGB(color.getRed(), color.getGreen(), value); break;
						}
						((LeatherArmorMeta) im).setColor(color);
						it.setItemMeta(im);
						
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
