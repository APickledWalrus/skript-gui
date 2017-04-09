package me.tuke.sktuke.expressions.customenchantments;

import java.util.List;

import javax.annotation.Nullable;

import me.tuke.sktuke.util.NewRegister;
import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import me.tuke.sktuke.manager.customenchantment.CEnchant;
import me.tuke.sktuke.manager.customenchantment.CustomEnchantment;
import me.tuke.sktuke.manager.customenchantment.EnchantConfig;

public class ExprCEConflicts extends SimpleExpression<CEnchant>{
	static {
		NewRegister.newSimple(ExprCEConflicts.class, "conflicts for %customenchantment%");
	}

	private Expression<CEnchant> ce;
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
		ce = (Expression<CEnchant>) arg[0];
		return true;
	}
	
	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "conflicts of " + ((ce != null) ? ce : "custom enchantment");
	}
	
	@Override
	@Nullable
	protected CEnchant[] get(Event e) {
		CEnchant ce = this.ce.getSingle(e);
		CEnchant[] conf = new CEnchant[ce.getEnchant().getConflicts().size()];
		for (int x = 0; x < ce.getEnchant().getConflicts().size(); x++){
			conf[x] = new CEnchant(ce.getEnchant().getConflicts().get(x), 1 );
		}
			
		return conf;
	}
	
	public void change(Event e, Object[] delta, Changer.ChangeMode mode){
		CEnchant ce = this.ce.getSingle(e);
		if (ce != null){
			List<CustomEnchantment> lce = ce.getEnchant().getConflicts();
			switch (mode){
			case RESET:
			case DELETE:
				lce.clear(); break;
			case SET:
				lce.clear();
			case ADD:
				for (Object cce: (Object[])delta)
					if (!lce.contains(((CEnchant)cce).getEnchant()))
						lce.add(((CEnchant)cce).getEnchant());
				break;
			case REMOVE:
				for (Object cce: (Object[])delta)
					if (lce.contains(((CEnchant)cce).getEnchant()))
						lce.remove(((CEnchant)cce).getEnchant());
				break;
			default:
				break;
			}
			ce.getEnchant().setConflicts(lce);
			EnchantConfig.y.set("Enchantments." + ce.getEnchant().getId() + ".Conflicts", toString(lce));
			EnchantConfig.save(); 
			
				
		}
	}

	
	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode != ChangeMode.REMOVE_ALL)
			return CollectionUtils.array(CEnchant[].class);
		return null;
	}


	public String toString(List<CustomEnchantment> conf){
		String str = "";
		for (int x = 0; x < conf.size(); x++){
			String name = conf.get(x).getId();
			name = Character.toString(name.charAt(0)).toUpperCase()+name.substring(1);
			if (x == conf.size() -1 && conf.size() > 1)
				str += " and " + name;
			else if (x > 0)
				str += ", " + name;
			else 
				str += name;
		}
		if (str.equals(""))
			return null;
		return str;
		
	}
}
