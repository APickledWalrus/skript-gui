package com.github.tukenuke.tuske.expressions;

import ch.njol.skript.doc.*;
import com.github.tukenuke.tuske.util.ReflectionUtils;
import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import javax.annotation.Nullable;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
@Name("Damage Modifier")
@Description({
		"Deprecated: It might be removed by Bukkit in 1.12 or higher. See more about it https://www.spigotmc.org/threads/194446",
		"Returns the {{types|DamageModifier|damage modifier}} from a {{events|OnDamage|damage event}}. You can set, add and remove, clear will be the same as setting it to 0, and reset will set to the original value when this event was called."})
@Examples({
		"on damage:",
		"\tadd 1 to armor damage",
		"\tremove 1 from armor damage",
		"\tset armor damage to armor damage + 1",
		"\tclear armor damage",
		"\treset armor damage"})
@Since("1.7-beta.2")
public class ExprDamageModifier extends SimpleExpression<Double>{
	static {
		if (ReflectionUtils.hasClass("org.bukkit.event.entity.EntityDamageEvent.DamageModifier"))
			Registry.newSimple(ExprDamageModifier.class,
					"damage [modifier] %damagemodifier%",
					"%damagemodifier% damage [modifier]");
	}

	private Expression<DamageModifier> dm;
	@Override
	public Class<? extends Double> getReturnType() {
		return Double.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		if (ScriptLoader.isCurrentEvent(EntityDamageEvent.class)){
			dm = (Expression<DamageModifier>) arg[0];
			return true;
		}
		Skript.error("The '" + arg3.expr+ "' can only be used in damage event.");
		return false;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "damage modifier";
	}

	@Override
	@Nullable
	protected Double[] get(Event e) {
		if (dm.getSingle(e) != null){
			return new Double[]{((EntityDamageEvent)e).isApplicable(dm.getSingle(e)) ? ((EntityDamageEvent)e).getDamage(dm.getSingle(e)) : null};
		}
		return null;
	}

	@Override
	public void change(Event e, Object[] delta, ChangeMode mode){
		DamageModifier dm = this.dm.getSingle(e);
		EntityDamageEvent ed = (EntityDamageEvent)e;
		if (dm != null && ed.isApplicable(dm)){
			double value =  ed.getDamage(dm);
			if (delta[0] == null && mode != ChangeMode.RESET && mode != ChangeMode.DELETE)
				return;
			switch (mode){
				case ADD: value += ((Number)delta[0]).doubleValue(); break;
				case REMOVE: value -= ((Number)delta[0]).doubleValue(); break;
				case SET: value = ((Number)delta[0]).doubleValue(); break;
				case DELETE: value = 0D; break;
				case RESET: value = ed.getOriginalDamage(dm); break;
				default: return;
			}
			ed.setDamage(dm, value);
			
		}
	}
	@SuppressWarnings("unchecked")
	@Override
	public Class<?>[] acceptChange(final Changer.ChangeMode mode){
		if (mode != ChangeMode.REMOVE_ALL)
			return CollectionUtils.array(Number.class);
		return null;
		
	}

}
