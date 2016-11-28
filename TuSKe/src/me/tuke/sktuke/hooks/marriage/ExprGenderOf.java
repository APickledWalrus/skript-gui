package me.tuke.sktuke.hooks.marriage;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.lenis0012.bukkit.marriage2.Gender;
import com.lenis0012.bukkit.marriage2.Marriage;
import com.lenis0012.bukkit.marriage2.MarriageAPI;

import javax.annotation.Nullable;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;

public class ExprGenderOf extends SimplePropertyExpression<Player, Gender> {
	@Override
	public Class<? extends Gender> getReturnType() {
		return Gender.class;
	}

	@Override
	@Nullable
	public Gender convert(Player p) {
		Marriage marry = (Marriage) MarriageAPI.getInstance();
		if (marry.getMPlayer(p.getUniqueId()).getGender() != Gender.UNKNOWN)
			return marry.getMPlayer(p.getUniqueId()).getGender();
		return null;
	}

	@Override
	protected String getPropertyName() {
		return "gender";
	}
	
	public void change(Event e, Object[] delta, Changer.ChangeMode mode){
	    Object[] cc = getExpr().getArray(e);
	    Player p = (Player) cc[0];
	    if (p != null){
			Marriage marry = MarriageAPI.getInstance();
			if (mode == ChangeMode.SET)
				marry.getMPlayer(p.getUniqueId()).setGender((Gender) delta[0]);
	    }
	}
	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode == ChangeMode.SET)
			return CollectionUtils.array(Gender.class);
		return null;
	}

}
