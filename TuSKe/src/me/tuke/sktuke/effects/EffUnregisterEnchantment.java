package me.tuke.sktuke.effects;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import me.tuke.sktuke.customenchantment.CEnchant;
import me.tuke.sktuke.customenchantment.CustomEnchantment;
import me.tuke.sktuke.customenchantment.EnchantManager;
import me.tuke.sktuke.customenchantment.EnchantConfig;

public class EffUnregisterEnchantment extends Effect{

	private Expression<CEnchant> id;
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		id = (Expression<CEnchant>) arg[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "unregister custom enchantment with id " + this.id;
	}

	@Override
	protected void execute(Event e) {
		CEnchant id = this.id.getSingle(e);
		if (id != null && EnchantManager.isCustomByID(id.getEnchant().getId())){
			CustomEnchantment.unregisterEnchantment(id.getEnchant());
			if(EnchantConfig.y.isConfigurationSection("Enchantments." + id.getEnchant().getId())){
				EnchantConfig.y.set("Enchantments." + id.getEnchant().getId(), null);
				EnchantConfig.save();
			}
				
		}
		
	}

}