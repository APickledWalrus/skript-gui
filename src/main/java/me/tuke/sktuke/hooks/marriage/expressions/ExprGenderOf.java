package me.tuke.sktuke.hooks.marriage.expressions;

import com.lenis0012.bukkit.marriage2.MPlayer;
import me.tuke.sktuke.util.Registry;
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
	static {
		Registry.newProperty(ExprGenderOf.class, "gender", "player");
	}

	@Override
	public Class<? extends Gender> getReturnType() {
		return Gender.class;
	}

	@Override
	@Nullable
	public Gender convert(Player p) {
		MPlayer mp = MarriageAPI.getInstance().getMPlayer(p.getUniqueId());
		if (mp != null && mp.getGender() != Gender.UNKNOWN)
			return mp.getGender();
		return null;
	}

	@Override
	protected String getPropertyName() {
		return "gender";
	}
	
	public void change(Event e, Object[] delta, Changer.ChangeMode mode){
	    Player[] players = getExpr().getArray(e);
		Marriage marry = MarriageAPI.getInstance();
		if (mode == ChangeMode.SET)
			for (Player p : players) {
				if (p != null) {
					marry.getMPlayer(p.getUniqueId()).setGender((Gender) delta[0]);
				}
			}
	}
	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode == ChangeMode.SET)
			return CollectionUtils.array(Gender.class);
		return null;
	}

}
