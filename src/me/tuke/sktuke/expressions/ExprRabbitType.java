package me.tuke.sktuke.expressions;

import javax.annotation.Nullable;

import me.tuke.sktuke.util.Registry;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Rabbit;
import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;

public class ExprRabbitType extends SimplePropertyExpression<Entity, String>{
	static {
		Registry.newProperty(ExprRabbitType.class, "rabbit type", "entity");
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	@Nullable
	public String convert(Entity e) {
		if (e instanceof Rabbit){
			return ((Rabbit)e).getRabbitType().name().toLowerCase().replaceAll("_", " ");
		}
		return null;
	}

	@Override
	protected String getPropertyName() {
		return "rabbit type";
	}
	
	public void change(Event e, Object[] delta, Changer.ChangeMode mode){
	    Entity le = (Entity) getExpr().getArray(e)[0];
	    if (le != null){
		    String s = ((String) delta[0]).toUpperCase().replaceAll(" ", "_");
			if (le instanceof Rabbit){
		    	if (mode == ChangeMode.SET && Rabbit.Type.valueOf(s) != null){
		    		((Rabbit)le).setRabbitType(Rabbit.Type.valueOf(s));
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