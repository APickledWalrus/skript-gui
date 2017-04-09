package me.tuke.sktuke.expressions;

import me.tuke.sktuke.util.NewRegister;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;

public class ExprExpOf extends SimplePropertyExpression<Player, Integer>{
	static {
		NewRegister.newProperty(ExprExpOf.class, "[total] [e]xp", "player");
	}
	
	@Override
	public Class<? extends Integer> getReturnType() {
		return Integer.class;
	}

	@Override
	@Nullable
	public Integer convert(Player p) {
		return (p != null) ? getExperience(p).intValue() : null;
	}

	@Override
	protected String getPropertyName() {
		return "xp";
	}
	
	public void change(Event e, Object[] delta, Changer.ChangeMode mode){
		Player p = (Player) getExpr().getSingle(e);
	    if (p != null){
	    	Long a = 0L;
	    	if (delta != null)
	    		a =((Number)delta[0]).longValue();
	    	if (a >= 0 && a <= 2e31){
				switch (mode){
					case ADD: break;
					case REMOVE: a = getExperience(p) - a;
					default:
						p.setExp(0.0F);
						p.setLevel(0);
						p.setTotalExperience(0); break;
				}
				if (a < 0L)
					a = 0L;
				p.giveExp(a.intValue());
				if (mode == ChangeMode.REMOVE && Long.compare(getExperience(p), a) != 0 && a !=0){					
					Long b = ((getExperience(p) > a) ? getExperience(p) - a : a - getExperience(p));
					p.giveExp(-b.intValue());
				} 		
		    }
	    }
	}

	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode != ChangeMode.REMOVE_ALL)
			return CollectionUtils.array(Number.class);
		return null;
		
	}
	private Long getExperience(Player p) {
		return getExpFromLevel(p.getLevel()) + getExpFromProg(p.getExpToLevel(), p.getExp());
	}
	private Long getExpFromLevel(int lvl){
		if (lvl <= 16)
			return (long) (lvl * lvl + 6*lvl);
		else if (lvl <= 31)
			return (long) (2.5 * (lvl*lvl) - 40.5 * lvl + 360);
		else
			return (long) (4.5 * (lvl*lvl) - 162.5 * lvl + 2220);
	}
	private int getExpFromProg(int nextLevel, float progress){
		return Math.round(nextLevel * progress);
	}
}
