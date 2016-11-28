package me.tuke.sktuke.conditions;

import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Tameable;
import org.bukkit.event.Event;
import javax.annotation.Nullable;

import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class CondIsMobType extends Condition{

	private Expression<LivingEntity> et;
	private MobType mt;
	public enum MobType{
		HOSTILE,
		NEUTRAL,
		PASSIVE;
		
		static MobType getByID(int id){
			return values()[id];
		}
		
		
	}
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		et = (Expression<LivingEntity>) arg[0];
		mt = MobType.getByID(arg3.mark);
		setNegated(arg1 == 1);
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return et + " is " + mt.name().toLowerCase();
	}

	@Override
	public boolean check(Event e) {
		if (et.getArray(e) == null)
			return false;
		boolean r = true;
		for (LivingEntity ent : et.getArray(e)){
			if (ent != null && ent instanceof Creature && !isMobType((Creature)ent, mt)){
				r = false;
				break;
			}
		}
		if (isNegated())
			r = !r;
		return r;
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
