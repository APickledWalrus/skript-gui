package com.github.tukenuke.tuske.conditions;

import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Tameable;
import org.bukkit.event.Event;
import javax.annotation.Nullable;

import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

//TODO Update with new mobs
public class CondIsMobType extends Condition{
	static {
		Registry.newCondition(CondIsMobType.class,
				"%livingentities% (is|are) [a] (0多ostile|1好eutral|2如assive) [mob]",
				"%livingentities% (is|are)(n't| not) [a] (0多ostile|1好eutral|2如assive) [mob]");
	}

	private Expression<LivingEntity> et;
	private MobType mt;

	public enum MobType{
		HOSTILE,
		NEUTRAL,
		PASSIVE;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		et = (Expression<LivingEntity>) arg[0];
		mt = MobType.values()[arg3.mark];
		setNegated(arg1 == 1);
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return et + " is " + mt.name().toLowerCase();
	}

	@Override
	public boolean check(Event e) {
		return et.check(e, le -> le instanceof Creature && isMobType((Creature) le, mt), isNegated());
	}

	public boolean isMobType(Creature et, MobType mt){
		switch (mt){
		case PASSIVE:
			switch(et.getType()){
			case WOLF: 
				if (et instanceof Tameable)
					return ((Tameable) et).isTamed();
			case OCELOT:
			case HORSE:
			case SQUID:
			case VILLAGER:
				
			case COW:
			case PIG:
			case CHICKEN:
			case SHEEP:
			case BAT:
			case MUSHROOM_COW:
				return true;
			default:
				return et.getType().name().equalsIgnoreCase("RABBIT");
			}
		case NEUTRAL:
			switch (et.getType()){
			case WOLF: return !((Tameable) et).isTamed();
			case CAVE_SPIDER:
			case SPIDER:
			case ENDERMAN:
			case PIG_ZOMBIE:
			case PLAYER:
				return true;
			default:
				return et.getType().name().equalsIgnoreCase("POLAR_BEAR");
			}
		case HOSTILE:
			if (isMobType(et, MobType.NEUTRAL))
				switch (et.getType()){
				case WOLF: return et.getTarget() != null && !((Tameable) et).isTamed();
				case CAVE_SPIDER:
				case SPIDER: return et.getTarget() != null || et.getLocation().getBlock().getLightLevel() <= 10;
				
				case ENDERMAN:
				case PIG_ZOMBIE:
					return et.getTarget() != null;
				default:
					return et.getType().name().equalsIgnoreCase("POLAR_BEAR") && et.getTarget() != null;
				}
			return !isMobType(et, MobType.PASSIVE);
			
		}
		return false;
	}
}
