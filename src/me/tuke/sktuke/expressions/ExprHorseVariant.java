package me.tuke.sktuke.expressions;

import javax.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.util.Version;
import me.tuke.sktuke.util.Registry;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;

public class ExprHorseVariant extends SimplePropertyExpression<Entity, String>{
	static {
		if (Skript.getMinecraftVersion().isSmallerThan(new Version(1, 11)))
			Registry.newProperty(ExprHorseVariant.class, "horse variant", "entity");
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	@Nullable
	public String convert(Entity e) {
		if (e != null){
			if (e instanceof Horse){
				return ((Horse)e).getVariant().name().toLowerCase().replaceAll("_", " ");
			}
		}
		return null;
	}

	@Override
	protected String getPropertyName() {
		return "horse variant";
	}
	
	public void change(Event e, Object[] delta, Changer.ChangeMode mode){
	    Entity le = (Entity) getExpr().getArray(e)[0];
	    if (le != null){
		    String s = ((String) delta[0]).toUpperCase().replaceAll(" ", "_");
			if (le instanceof Horse){
		    	if (mode == ChangeMode.SET && Horse.Variant.valueOf(s) != null){
		    		((Horse)le).setVariant(Horse.Variant.valueOf(s));
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
