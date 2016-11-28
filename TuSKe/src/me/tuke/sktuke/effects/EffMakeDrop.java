package me.tuke.sktuke.effects;

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

	private Expression<Player> p;
	private Expression<ItemStack> i;
	private boolean remove = false;
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		this.p = (Expression<Player>) arg[0];
		this.i = (Expression<ItemStack>) arg[1];
		if (arg3.expr.toLowerCase().contains(" from his inventory"))
			remove = true;
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "make " + this.p + " drop " + this.i + " from his inventory";
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
		}
		TuSKe.getNMS().makeDrop(p, i);
	}

}
