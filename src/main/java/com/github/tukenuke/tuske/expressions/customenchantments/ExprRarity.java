package com.github.tukenuke.tuske.expressions.customenchantments;

import javax.annotation.Nullable;

import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.github.tukenuke.tuske.manager.customenchantment.CEnchant;
import com.github.tukenuke.tuske.manager.customenchantment.EnchantConfig;

public class ExprRarity extends SimplePropertyExpression<CEnchant, Number>{
	static {
		Registry.newProperty(ExprRarity.class, "rarity", "customenchantment");
	}

	@Override
	public Class<? extends Number> getReturnType() {
		return Number.class;
	}

	@Override
	@Nullable
	public Number convert(CEnchant ce) {
		return (ce != null) ? ce.getEnchant().getMaxLevel() : null;
	}

	@Override
	protected String getPropertyName() {
		return "rarity";
	}
	public void change(Event e, Object[] delta, Changer.ChangeMode mode){
		CEnchant ce = getExpr().getSingle(e);
		if (!(mode == ChangeMode.DELETE || mode == ChangeMode.RESET) && delta == null)
			return;
		if (ce != null){
			int value = ce.getEnchant().getRarity();
			switch (mode){
				case SET: value = ((Number) delta[0]).intValue(); break;
				case ADD: value += ((Number) delta[0]).intValue(); break;
				case REMOVE: value -= ((Number) delta[0]).intValue(); break;
			default:
				break;
			} 
			if (value < EnchantConfig.MIN_NUMBER)
				value = EnchantConfig.MIN_NUMBER;
			else if (value > EnchantConfig.MAX_RARITY)
				value = EnchantConfig.MAX_RARITY;
			ce.getEnchant().setRarity(value);
			EnchantConfig.y.set("Enchantments." + ce.getEnchant().getId() + ".Rarity", value);
			EnchantConfig.save(); 
		}
	}

	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode != ChangeMode.REMOVE_ALL)
			return CollectionUtils.array(Number.class);
		return null;
	}

}
