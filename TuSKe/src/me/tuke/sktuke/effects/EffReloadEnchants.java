package me.tuke.sktuke.effects;

import org.bukkit.event.Event;
import javax.annotation.Nullable;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import me.tuke.sktuke.TuSKe;
import me.tuke.sktuke.customenchantment.CustomEnchantment;
import me.tuke.sktuke.customenchantment.EnchantConfig;

public class EffReloadEnchants extends Effect{

	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "reload all enchants";
	}

	@Override
	protected void execute(Event e) {
		//if (!EnchantConfig.y.getBoolean("Config.CompatibilityMode"))
		EnchantConfig.reload();
		TuSKe.log("The enchantments file has been reloaded. A total of " + CustomEnchantment.getEnchantments().size() + " was loaded successfully.");
		//else
		//	Bukkit.getLogger().info("[TuSKe] Sorry, you can't reload the enchants in compatibility mode. You will have to restart your server.");
		
	}

}
