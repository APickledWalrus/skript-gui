package com.github.tukenuke.tuske.conditions;

import ch.njol.skript.aliases.ItemType;
import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.Material;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class CondIsBlockType extends Condition{
	static {
		Registry.newCondition(CondIsBlockType.class, 4,
				"%itemtypes% is [a] (solid|transparent|flammable|occluding) block",
				"%itemtypes% is(n't| not) [a] (solid|transparent|flammable|occluding) block");
	}

	private Expression<ItemType> b; //The block type
	private int type = 0;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		this.b = (Expression<ItemType>) arg[0];
		String expr = arg3.expr;
		setNegated(arg1 == 1);
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
		return b.check(e, item -> {
			Material m = item.getRandom().getType();
			switch (type){
				case 1: return m.isBlock();
				case 2: return m.isSolid();
				case 3: return m.isTransparent();
				case 4: return m.isFlammable();
				case 5: return m.isOccluding();
				default: return false;
			}
		}, isNegated());
	}

}
