package me.tuke.sktuke.expressions;

import me.tuke.sktuke.util.Registry;
import org.bukkit.inventory.ItemStack;
import javax.annotation.Nullable;

import ch.njol.skript.expressions.base.SimplePropertyExpression;

public class ExprMaxDurability extends SimplePropertyExpression<ItemStack, Integer>{
	static {
		Registry.newProperty(ExprMaxDurability.class, "max durability", "itemstack");
	}

	@Override
	public Class<? extends Integer> getReturnType() {
		return Integer.class;
	}

	@Override
	@Nullable
	public Integer convert(ItemStack i) {
		return (i != null) ? (int) i.getType().getMaxDurability() : null;
	}

	@Override
	protected String getPropertyName() {
		return "max durability";
	}

}
