package me.tuke.sktuke.expressions.customenchantments;

import java.util.ArrayList;
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
import me.tuke.sktuke.manager.customenchantment.AcceptedItems;
import me.tuke.sktuke.manager.customenchantment.CEnchant;
import me.tuke.sktuke.manager.customenchantment.EnchantConfig;

public class ExprAcceptedItems extends SimpleExpression<String>{
	static {
		NewRegister.newSimple(ExprAcceptedItems.class, "accepted items for %customenchantment%");
	}

	private Expression<CEnchant> ce;
	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
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
		return "accepted items of " + ((ce != null) ? ce : "custom enchantment");
	}
	
	@Override
	@Nullable
	protected String[] get(Event e) {
		CEnchant ce = this.ce.getSingle(e);
		return toStringArray(ce.getEnchant().getAcceptedItems());
	}
	
	public void change(Event e, Object[] delta, Changer.ChangeMode mode){
		CEnchant ce = this.ce.getSingle(e);
		if (ce != null && delta != null){
			List<AcceptedItems> items = new ArrayList<AcceptedItems>();
			for (Object ai : (Object[]) delta)
				if (AcceptedItems.isValue(((String)ai).toUpperCase().replaceAll(" ", "")) && !items.contains(AcceptedItems.valueOf(((String)ai).toUpperCase().replaceAll(" ", ""))))
					items.add(AcceptedItems.valueOf(((String)ai).toUpperCase().replaceAll(" ", "")));
			ce.getEnchant().setAcceptedItems(items);
			EnchantConfig.y.set("Enchantments." + ce.getEnchant().getId() + ".AcceptedItems", toString(items));
			EnchantConfig.save(); 
		}
	}

	
	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode == ChangeMode.SET)
			return CollectionUtils.array(String[].class);
		return null;
	}

	public String[] toStringArray(List<AcceptedItems> items){
		String[] str = new String[items.size()];
		for (int x = 0; x < items.size(); x++){
			String name = ((items.get(x).equals(AcceptedItems.FISHINGROD)) ? "fishing rod" : items.get(x).name().toLowerCase());
			name = Character.toString(name.charAt(0)).toUpperCase()+name.substring(1);
			str[x] = name;
		}
		return str;
		
		
	}
	public String toString(List<AcceptedItems> items){
		String[] str = toStringArray(items);
		String r = str[0];
		for (int x = 1; x < str.length; x++)
			if (x == str.length -1)
				r += " and " + str[x];
			else
				r += ", " + str[x];
		return r;
	}
}