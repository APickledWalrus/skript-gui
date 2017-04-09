package me.tuke.sktuke.expressions.customenchantments;

import me.tuke.sktuke.util.NewRegister;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

import javax.annotation.Nullable;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import me.tuke.sktuke.manager.customenchantment.CEnchant;
import me.tuke.sktuke.manager.customenchantment.CustomEnchantment;
import me.tuke.sktuke.manager.customenchantment.EnchantManager;

public class ExprAllCustomEnchants extends SimpleExpression<CEnchant> {
	static {
		NewRegister.newProperty(ExprAllCustomEnchants.class, "[all] custom enchantments", "itemstack");
	}

	private Expression<ItemStack> i;
	@Override
	public Class<? extends CEnchant> getReturnType() {
		return CEnchant.class;
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		this.i = (Expression<ItemStack>) arg[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "all custom enchantments of " + this.i;
	}

	@Override
	@Nullable
	protected CEnchant[] get(Event e) {
		ItemStack i = this.i.getSingle(e);
		if (i != null){
			Map<CustomEnchantment, Integer> enchs = CustomEnchantment.getCustomEnchants(i);
			CEnchant[] ce = new CEnchant[enchs.size()];
			int x = 0;
			for (CustomEnchantment cce : enchs.keySet()){
				
				ce[x] = new CEnchant(cce, enchs.get(cce));
				x++;
			}
			return ce;
		}
		return null;
	}

	public void change(Event e, Object[] delta, Changer.ChangeMode mode){
		ItemStack i = this.i.getSingle(e);
		CEnchant[] ce = null;
		if (!(mode == ChangeMode.DELETE || mode == ChangeMode.RESET) && delta != null)
			ce = (CEnchant[])delta;
		if (i != null){
			switch(mode){
			case SET:
				for (CustomEnchantment cce: CustomEnchantment.getCustomEnchants(i).keySet())
					EnchantManager.removeFromItem(i, cce);
			case ADD:
				for (CEnchant cce: ce)
					EnchantManager.addToItem(i, cce.getEnchant(), cce.getLevel(), true);
				break;
			case REMOVE:
				for (CEnchant cce: ce)
					EnchantManager.removeFromItem(i, cce.getEnchant());
				break;
				
			case DELETE:
			case RESET:
				for (CustomEnchantment cce: CustomEnchantment.getCustomEnchants(i).keySet())
					EnchantManager.removeFromItem(i, cce);
				break;
			default:
				break;
			}
		}
	}
	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode != ChangeMode.DELETE || mode != ChangeMode.RESET)
			return CollectionUtils.array(CEnchant[].class);
		return null;
	}

}
