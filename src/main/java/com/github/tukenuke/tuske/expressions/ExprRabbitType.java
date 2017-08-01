package com.github.tukenuke.tuske.expressions;

import javax.annotation.Nullable;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Rabbit;
import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;

@Name("Rabbit Type")
@Description("Returns the type of rabbit. e.g. `black`, `black and white`, `brown`, `gold`, `salt and pepper`, `the killer bunny` and `white`.")
@Examples({
		"on spawn of rabbit:",
		"\tif rabbit type of event-entity is \"the killer bunny\":",
		"\t\tbroadcast \"Run, everyone, run! The Killer Bunny was spawned!\""})
@Since("1.0")
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
	    Entity le = getExpr().getArray(e)[0];
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