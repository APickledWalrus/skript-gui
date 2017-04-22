package me.tuke.sktuke.expressions;

import me.tuke.sktuke.util.Registry;
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

	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		i = arg[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "the RGB color of the " + i.toString(arg0, arg1);
	}

	@Override
	@Nullable
	protected Integer[] get(@Nullable Event e) {
		if (this.i.getArray(e).length > 0 && this.i.getArray(e)[0] != null ){
			int red = 0;
			int green = 0;
			int blue = 0;
			if (this.i.getAll(e)[0] instanceof ItemStack){
				ItemMeta im = ((ItemStack)this.i.getAll(e)[0]).getItemMeta();
				if (!(im instanceof LeatherArmorMeta))
					return null;
				red = ((LeatherArmorMeta) im).getColor().getRed();;
				green = ((LeatherArmorMeta) im).getColor().getGreen();;
				blue = ((LeatherArmorMeta) im).getColor().getBlue();
			} else if (this.i.getArray(e)[0] instanceof Color){
				red = ((Color)this.i.getArray(e)[0]).getBukkitColor().getRed();
				green = ((Color)this.i.getArray(e)[0]).getBukkitColor().getGreen();
				blue = ((Color)this.i.getArray(e)[0]).getBukkitColor().getBlue();
			}
			return new Integer[] {red, green, blue};
				
			
		}
		return null;
	}
	public void change(Event e, Object[] delta, Changer.ChangeMode mode){
		if (this.i.getArray(e).length > 0 && delta != null && delta.length == 3 && this.i.getAll(e)[0] instanceof ItemStack){
			for (ItemStack it : ((ItemStack[])this.i.getArray(e))){
				if (it != null){
					ItemMeta im = it.getItemMeta();
					if (im instanceof LeatherArmorMeta){
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
						((LeatherArmorMeta) im).setColor(color);
						it.setItemMeta(im);
					}
				}
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
