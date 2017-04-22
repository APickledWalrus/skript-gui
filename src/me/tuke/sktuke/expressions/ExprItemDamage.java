package me.tuke.sktuke.expressions;

import me.tuke.sktuke.util.Registry;
import me.tuke.sktuke.util.ReflectionUtils;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemDamageEvent;

import javax.annotation.Nullable;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

public class ExprItemDamage extends SimpleExpression<Integer>{
	static {
		if (ReflectionUtils.hasClass("org.bukkit.event.player.PlayerItemDamageEvent"))
			Registry.newSimple(ExprItemDamage.class, 0,"item damage");
	}

	private Kleenean d;
	@Override
	public Class<? extends Integer> getReturnType() {
		return Integer.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public boolean init(Expression<?>[] arg0, int arg1, Kleenean arg2, ParseResult arg3) {
		if (!ScriptLoader.isCurrentEvent(PlayerItemDamageEvent.class)){
			Skript.error("The expression 'item damage' can only be used in 'item damage event'.", ErrorQuality.SEMANTIC_ERROR);
			return false;
		}
		d = arg2;
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "item damage";
	}

	@Override
	@Nullable
	protected Integer[] get(Event e) {
		return new Integer [] {((PlayerItemDamageEvent) e).getDamage()};
	}
	public void change(Event e, Object[] delta, Changer.ChangeMode mode){
		if (delta != null){
			switch (mode){
				case SET: ((PlayerItemDamageEvent) e).setDamage(((Number) delta[0]).intValue()); break;
				case ADD: ((PlayerItemDamageEvent) e).setDamage(((PlayerItemDamageEvent) e).getDamage() + ((Number) delta[0]).intValue()); break;
				case REMOVE: ((PlayerItemDamageEvent) e).setDamage(((PlayerItemDamageEvent) e).getDamage() - ((Number) delta[0]).intValue()); break;
				default: break;
			}	
		}
	}

	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
	    if (this.d == Kleenean.TRUE){
	    	Skript.error("Can't change the item damage anymore after the event has already passed.");
	    	return null;
	    }
		if (!(mode == ChangeMode.REMOVE_ALL || mode != ChangeMode.DELETE || mode == ChangeMode.RESET))
			return CollectionUtils.array(Number.class);
		return null;
	}

}