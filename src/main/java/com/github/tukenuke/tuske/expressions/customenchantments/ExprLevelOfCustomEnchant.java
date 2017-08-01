package com.github.tukenuke.tuske.expressions.customenchantments;

import javax.annotation.Nullable;

import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.github.tukenuke.tuske.manager.customenchantment.CEnchant;

public class ExprLevelOfCustomEnchant extends SimpleExpression<Number> {
	static {
		Registry.newSimple(ExprLevelOfCustomEnchant.class, "level of [custom enchantment] %customenchantment% of %itemstack%");
	}

	private Expression<ItemStack> i;
	private Expression<CEnchant> ce;
	@Override
	public Class<? extends Number> getReturnType() {
		return Number.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		this.i = (Expression<ItemStack>) arg[1];
		this.ce = (Expression<CEnchant>) arg[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "level of custom enchantment " + this.ce + " of " + this.i;
	}

	@Override
	@Nullable
	protected Number[] get(Event e) {
		ItemStack i = this.i.getSingle(e);
		CEnchant ce = this.ce.getSingle(e);
		return (i != null && ce != null) ? new Number[] {ce.getEnchant().getLevel(i)} : null;
	}
}