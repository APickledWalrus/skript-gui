package me.tuke.sktuke.expressions;

import javax.annotation.Nullable;

import me.tuke.sktuke.util.NewRegister;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;

public class ExprHorseColor extends SimplePropertyExpression<Entity, String>{
	static {
		NewRegister.newProperty(ExprHorseColor.class, "horse color", "entity");
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	@Nullable
	public String convert(Entity e) {
		if (e instanceof Horse && ((Horse)e).getVariant().equals(Horse.Variant.HORSE)){
			return ((Horse)e).getColor().name().toLowerCase().replaceAll("_", " ");
		}
		return null;
	}

	@Override
	protected String getPropertyName() {
		return "horse color";
	}
	
	public void change(Event e, Object[] delta, Changer.ChangeMode mode){
	    Entity le = (Entity) getExpr().getArray(e)[0];
	    String s = ((String) delta[0]).toUpperCase().replaceAll(" ", "_");
	    if (le instanceof Horse){
	    	if (mode == ChangeMode.SET && Horse.Color.valueOf(s) != null){
	    		((Horse)le).setColor(Horse.Color.valueOf(s));
	    	}
	    }
		
	}

	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode == ChangeMode.SET)
			return CollectionUtils.array(String.class);
		return null;
		
	}

}
