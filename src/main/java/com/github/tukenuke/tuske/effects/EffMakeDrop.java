package com.github.tukenuke.tuske.effects;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.slot.Slot;
import com.github.tukenuke.tuske.TuSKe;
import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffMakeDrop extends Effect{
	static {
		Registry.newEffect(EffMakeDrop.class, "(make|force) %player% drop[s] %itemstack% [from (%-slot%|his inventory)]");
	}

	private static final Changer<? super Slot> changeSlot = Classes.getExactClassInfo(Slot.class).getChanger();
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
		if (p == null || i == null || i.getType() == Material.AIR)
			return;
		if (remove){
			int before = i.getAmount();
			p.getInventory().removeItem(i);
			if (before != i.getAmount())
				i.setAmount(before - i.getAmount());
		} else if (f != null) {
			changeSlot.change(f.getAll(e), new ItemStack[]{i}, Changer.ChangeMode.REMOVE);
		}
		TuSKe.getNMS().makeDrop(p, i);
	}

}
