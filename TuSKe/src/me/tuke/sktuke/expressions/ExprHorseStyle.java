package me.tuke.sktuke.expressions;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;

public class ExprHorseStyle extends SimplePropertyExpression<Entity, String>{

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	@Nullable
	public String convert(Entity e) { 
		if (e instanceof Horse && ((Horse)e).getVariant().equals(Horse.Variant.HORSE) && !((Horse)e).getStyle().equals(Horse.Style.NONE)){
			return ((Horse)e).getStyle().name().toLowerCase().replaceAll("_", " ");
		}
		return null;
	}

	@Override
	protected String getPropertyName() {
		return "horse style";
	}
	
	public void change(Event e, Object[] delta, Changer.ChangeMode mode){
	    Entity le = (Entity) getExpr().getArray(e)[0];
	    String s = "";
	    if (delta != null)
	    	s = ((String) delta[0]).toUpperCase().replaceAll(" ", "_");
	    if (le instanceof Horse){
	    	if (mode == ChangeMode.SET && Horse.Style.valueOf(s) != null)
	    		((Horse)le).setStyle(Horse.Style.valueOf(s));
	 
	    	else if ((mode == ChangeMode.RESET || mode == ChangeMode.DELETE))
	    		((Horse)le).setStyle(Horse.Style.NONE);
	    }
		
	}

	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode == ChangeMode.SET)
			return CollectionUtils.array(String.class);
		return null;
		
	}

}
