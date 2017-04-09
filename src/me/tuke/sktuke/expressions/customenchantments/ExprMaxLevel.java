package me.tuke.sktuke.expressions.customenchantments;

import javax.annotation.Nullable;

import me.tuke.sktuke.util.NewRegister;
import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import me.tuke.sktuke.manager.customenchantment.CEnchant;
import me.tuke.sktuke.manager.customenchantment.EnchantConfig;

public class ExprMaxLevel extends SimplePropertyExpression<CEnchant, Number>{
	static {
		NewRegister.newProperty(ExprMaxLevel.class, "max level", "customenchantment");
	}

	@Override
	public Class<? extends Number> getReturnType() {
		return Number.class;
	}

	@Override
	@Nullable
	public Number convert(CEnchant ce) {
		return ce.getEnchant().getMaxLevel();
	}

	@Override
	protected String getPropertyName() {
		return "max level";
	}
	public void change(Event e, Object[] delta, Changer.ChangeMode mode){
		CEnchant ce = getExpr().getSingle(e);
		if (!(mode == ChangeMode.DELETE || mode == ChangeMode.RESET) && delta == null)
			return;
		if (ce != null){
			int value = ce.getEnchant().getMaxLevel();
			switch (mode){
				case SET: value = ((Number) delta[0]).intValue(); break;
				case ADD: value += ((Number) delta[0]).intValue(); break;
				case REMOVE: value -= ((Number) delta[0]).intValue(); break;
			default:
				break;
			}
			if (value < EnchantConfig.MIN_NUMBER)
				value = EnchantConfig.MIN_NUMBER;
			else if (value > EnchantConfig.MAX_LEVEL)
				value = EnchantConfig.MAX_LEVEL;
			ce.getEnchant().setMaxLevel(value);
			EnchantConfig.y.set("Enchantments." + ce.getEnchant().getId() + ".MaxLevel", value);
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
