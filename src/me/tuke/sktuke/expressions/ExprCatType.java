package me.tuke.sktuke.expressions;

import javax.annotation.Nullable;

import me.tuke.sktuke.util.NewRegister;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ocelot;
import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;

public class ExprCatType extends SimplePropertyExpression<Entity, String>{
	static {
		NewRegister.newProperty(ExprCatType.class, "(cat|ocelot) type", "entity");
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	@Nullable
	public String convert(Entity e) {
		if (e instanceof Ocelot){
			return ((Ocelot)e).getCatType().name().toLowerCase().replaceAll("_", " ").replaceAll("_CAT", "");
		}
		return null;
	}

	@Override
	protected String getPropertyName() {
		return "(cat|ocelot) type";
	}
	
	public void change(Event e, Object[] delta, Changer.ChangeMode mode){
	    Entity le = (Entity) getExpr().getArray(e)[0];
	    if (le != null){
		    String s = ((String) delta[0]).toUpperCase().replaceAll(" ", "_");
		    if (!s.equals("WILD_OCELOT") && !s.contains("_CAT"))
		    	s = ((String) delta[0]).toUpperCase().replaceAll(" ", "_") + "_CAT";
			if (le instanceof Ocelot){
		    	if (mode == ChangeMode.SET && Ocelot.Type.valueOf(s) != null){
		    		((Ocelot)le).setCatType(Ocelot.Type.valueOf(s));
		    	}
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