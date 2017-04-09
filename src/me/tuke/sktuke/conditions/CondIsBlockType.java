package me.tuke.sktuke.conditions;

import me.tuke.sktuke.util.NewRegister;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class CondIsBlockType extends Condition{
	static {
		NewRegister.newCondition(CondIsBlockType.class, 4, "%itemstack% is [a] (solid|transparent|flammable|occluding) block", "%itemstack% is(n't| not) [a] (solid|transparent|flammable|occluding) block");
	}

	private Expression<ItemStack> b;
	private boolean neg = false;
	private int type = 0;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		this.b = (Expression<ItemStack>) arg[0];
		String expr = arg3.expr;
		if (arg1 == 1)
			this.neg = true;
		if (expr.toLowerCase().contains("placeable block"))
			type = 1;
		else if (expr.toLowerCase().contains("solid block"))
			type = 2;
		else if (expr.toLowerCase().contains("transparent block"))
			type = 3;
		else if (expr.toLowerCase().contains("flammable block"))
			type = 4;
		else if (expr.toLowerCase().contains("occluding block"))
			type = 5;
				
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return this.b + "is block type";
	}

	@Override
	public boolean check(Event e) {
		ItemStack o = this.b.getSingle(e);
		Material m = null;
		boolean r = false;
		m = ((ItemStack)o).getType();
		//if (o instanceof Block)
		//	m = ((Block)o).getType();
		//else if (o instanceof ItemStack)
		//else if (o instanceof ItemType)
		//	m = ((ItemType)o).getItem().getRandom().getType();
		//else if (o instanceof Slot)
		//	m = ((Slot)o).getItem().getType();
		switch (type){
			case 1: r = m.isBlock(); break;
			case 2: r = m.isSolid(); break;
			case 3: r = m.isTransparent(); break;
			case 4: r = m.isFlammable(); break;
			case 5: r = m.isOccluding(); break;
		}
		if (neg)
			return !r;
		return r;
	}

}
