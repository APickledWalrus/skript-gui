package me.tuke.sktuke.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import me.tuke.sktuke.util.Registry;
import org.bukkit.inventory.ItemStack;
import javax.annotation.Nullable;

import ch.njol.skript.expressions.base.SimplePropertyExpression;

@Name("Max Durability")
@Description("Returns the max durability of {{types|ItemStack|item stack}}. i.e. 1561 for diamond sword. You can cancel it.")
@Examples({
		"on item damage:",
		"\tif durability of event-item is more than (max durability of event-item - item damage):",
		"\t\tsend \"Your %event-item% is almost breaking!\""})
@Since("1.1")
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
