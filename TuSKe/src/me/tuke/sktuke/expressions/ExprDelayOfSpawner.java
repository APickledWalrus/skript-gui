package me.tuke.sktuke.expressions;

import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Timespan;
import ch.njol.util.coll.CollectionUtils;

public class ExprDelayOfSpawner extends SimplePropertyExpression<Block, Timespan>{

	@Override
	public Class<? extends Timespan> getReturnType() {
		return Timespan.class;
	}

	@Override
	@Nullable
	public Timespan convert(Block b) {
		return (b!= null && b.getState() instanceof CreatureSpawner) ? Timespan.fromTicks_i(((CreatureSpawner)b.getState()).getDelay()) : null;
	}

	@Override
	protected String getPropertyName() {
		return "delay";
	}
	public void change(Event e, Object[] delta, Changer.ChangeMode mode){
		Block b = getExpr().getSingle(e);
		if (b != null && b.getState() instanceof CreatureSpawner){
			CreatureSpawner cs = (CreatureSpawner)b.getState();
			Timespan t = new Timespan(0);
			if (delta != null)
				t = (Timespan) delta[0];
			switch (mode){
			case ADD: t = Timespan.fromTicks_i(t.getTicks_i() + cs.getDelay()); break;
			case REMOVE: t = Timespan.fromTicks_i(t.getTicks_i() - cs.getDelay()); break;
			default: break;
			
			}
			Long l = t.getTicks_i();
			if (l < 0L)
				l = 0L;
			cs.setDelay(l.intValue());
		}
	}

	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode != ChangeMode.REMOVE_ALL)
			return CollectionUtils.array(Timespan.class);
		return null;
		
	}

}
