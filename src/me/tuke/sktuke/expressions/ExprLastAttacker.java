package me.tuke.sktuke.expressions;

import me.tuke.sktuke.util.NewRegister;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

import ch.njol.skript.bukkitutil.ProjectileUtils;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprLastAttacker extends SimpleExpression<Object> {
	static {
		NewRegister.newProperty(ExprLastAttacker.class, "last attacker", "entity");
	}

	private Expression<Entity> ent;
	@Override
	public Class<? extends Object> getReturnType() {
		return Object.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		ent = (Expression<Entity>) arg[0];
		return true;
	}

	@Override
	public String toString(Event arg0, boolean arg1) {
		return "lastInstance attacker of " + ent.toString(arg0, arg1);
	}

	@Override
	protected Object[] get(Event e) {
		Entity entity = ent.getSingle(e);
		if (entity != null && entity.getLastDamageCause() != null){
			if (entity.getLastDamageCause() instanceof EntityDamageByBlockEvent)
				return new Block[]{((EntityDamageByBlockEvent)entity.getLastDamageCause()).getDamager()};
			return new Entity[]{getAttacker(entity.getLastDamageCause())};
		}
		return null;
	}

	public static Entity getAttacker(final Event e) {
		if (e instanceof EntityDamageByEntityEvent) {
			if (((EntityDamageByEntityEvent) e).getDamager() instanceof Projectile) {
				final Object o = ProjectileUtils.getShooter((Projectile) ((EntityDamageByEntityEvent) e).getDamager());
				if (o instanceof Entity)
					return (Entity) o;
				return null;
			}
			return ((EntityDamageByEntityEvent) e).getDamager();
		} else if (e instanceof VehicleDamageEvent) {
			return ((VehicleDamageEvent) e).getAttacker();
		} else if (e instanceof VehicleDestroyEvent) {
			return ((VehicleDestroyEvent) e).getAttacker();
		}
		return null;
	}

}
