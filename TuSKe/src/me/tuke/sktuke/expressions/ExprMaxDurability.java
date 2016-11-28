package me.tuke.sktuke.expressions;

import org.bukkit.inventory.ItemStack;
import javax.annotation.Nullable;

import ch.njol.skript.expressions.base.SimplePropertyExpression;

public class ExprMaxDurability extends SimplePropertyExpression<ItemStack, Integer>{

	@Override
	public Class<? extends Integer> getReturnType() {
		return Integer.class;
	}

	@Override
	@Nullable
	public Integer convert(ItemStack i) {
		return (i != null) ? new Integer(i.getType().getMaxDurability()) : null;
	}

	@Override
	protected String getPropertyName() {
		return "max durability";
	}

}
