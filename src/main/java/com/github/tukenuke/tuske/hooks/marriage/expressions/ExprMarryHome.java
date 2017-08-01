package com.github.tukenuke.tuske.hooks.marriage.expressions;

import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.lenis0012.bukkit.marriage2.Marriage;
import com.lenis0012.bukkit.marriage2.MarriageAPI;

import javax.annotation.Nullable;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;

public class ExprMarryHome extends SimplePropertyExpression <Player, Location>{
	static {
		Registry.newProperty(ExprMarryHome.class, "marry home", "player");
	}

	@Override
	public Class<? extends Location> getReturnType() {
		return Location.class;
	}

	@Override
	@Nullable
	public Location convert(Player p) {
		return MarriageAPI.getInstance().getMPlayer(p.getUniqueId()).getMarriage().getHome();
	}

	@Override
	protected String getPropertyName() {
		return "marry home";
	}
	
	public void change(Event e, Object[] delta, Changer.ChangeMode mode){
	    Object[] c = getExpr().getArray(e);
	    Player p = (Player) c[0];
		Marriage marry = MarriageAPI.getInstance();
		if (mode == ChangeMode.SET)
			marry.getMPlayer(p.getUniqueId()).getMarriage().setHome((Location) delta[0]);
		else if (mode == ChangeMode.RESET || mode == ChangeMode.DELETE)
			marry.getMPlayer(p.getUniqueId()).getMarriage().setHome(null);
	}
	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode == ChangeMode.SET || mode == ChangeMode.RESET || mode == ChangeMode.DELETE)
			return CollectionUtils.array(Location.class);
		return null;
	}


}
