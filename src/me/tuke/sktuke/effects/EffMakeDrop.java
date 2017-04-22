package me.tuke.sktuke.effects;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.util.Slot;
import me.tuke.sktuke.util.Registry;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import me.tuke.sktuke.TuSKe;

public class EffMakeDrop extends Effect{
	static {
		Registry.newEffect(EffMakeDrop.class, "(make|force) %player% drop[s] %itemstack% [from (%slot%|his inventory)]");
	}

	private Expression<Player> p;
	private Expression<ItemStack> i;
	private Expression<Slot> f;
	private boolean remove = false;
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		p = (Expression<Player>) arg[0];
		i = (Expression<ItemStack>) arg[1];
		if (arg3.expr.toLowerCase().endsWith(" from his inventory"))
			remove = true;
		else
			f = (Expression<Slot>) arg[2];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "make " + this.p.toString(e, arg1) + " drop " + this.i.toString(e, arg1) + " from " + (remove ? "his inventory" : f.toString(e, arg1));
	}

	@Override
	protected void execute(Event e) {
		Player p = this.p.getSingle(e);
		ItemStack i = this.i.getSingle(e);
		if (remove){
			if (p.getInventory().contains(i)){
				p.getInventory().removeItem(i);
			} else
				return;
		} else if (f != null) {
			f.change(e, this.i.getArray(e), Changer.ChangeMode.REMOVE);
		}
		TuSKe.getNMS().makeDrop(p, i);
	}

}
