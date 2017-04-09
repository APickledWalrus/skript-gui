package me.tuke.sktuke.expressions.customenchantments;

import javax.annotation.Nullable;

import me.tuke.sktuke.util.NewRegister;
import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import me.tuke.sktuke.manager.customenchantment.CEnchant;
import me.tuke.sktuke.manager.customenchantment.CustomEnchantment;
import me.tuke.sktuke.manager.customenchantment.EnchantConfig;

public class ExprLoreName extends SimplePropertyExpression<CEnchant, String>{
	static {
		NewRegister.newProperty(ExprLoreName.class, "lore name", "customenchantment");
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	@Nullable
	public String convert(CEnchant ce) {
		return ce.getEnchant().getName();
	}

	@Override
	protected String getPropertyName() {
		return "lore name";
	}
	
	public void change(Event e, Object[] delta, Changer.ChangeMode mode){
		CEnchant ce = getExpr().getSingle(e);
		if (ce != null && delta != null){
			CustomEnchantment cc = CustomEnchantment.getByID((String)delta[0]);
			if (cc == null)
				cc = CustomEnchantment.getByName((String)delta[0]);
			if (cc != null && !cc.equalsById(ce.getEnchant()))
				return;
			ce.getEnchant().setName((String)delta[0]);
			EnchantConfig.y.set("Enchantments." + ce.getEnchant().getId() + ".Name", ((String)delta[0]));
			EnchantConfig.save(); 
		}
	}

	
	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode == ChangeMode.SET)
			return CollectionUtils.array(String.class);
		return null;
	}


}
