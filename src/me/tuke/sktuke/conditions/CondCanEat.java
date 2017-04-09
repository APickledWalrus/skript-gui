package me.tuke.sktuke.conditions;

import me.tuke.sktuke.util.NewRegister;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class CondCanEat extends Condition{
	static {
		NewRegister.newCondition(CondCanEat.class, "%itemstack% is edible", "%itemstack% is(n't| not) edible");
	}

	private Expression<ItemStack> o;
	private int neg;
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		this.o = (Expression<ItemStack>) arg[0];
		this.neg = arg1;
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return this.o + " is edible";
	}

	@Override
	public boolean check(Event e) {
		ItemStack i = this.o.getSingle(e);
		if (neg == 1)
			return !i.getType().isEdible();
		return i.getType().isEdible();
	}
	

}
