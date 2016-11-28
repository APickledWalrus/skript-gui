package me.tuke.sktuke.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import javax.annotation.Nullable;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffFormatGroupGUI extends Effect{

	private Expression<Player> p;
	private Expression<String> id;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		id = (Expression<String>) arg[0];
		p = (Expression<Player>) arg[1];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "format gui from id " + id + " to " + p;
	}

	@Override
	protected void execute(Event e) {	
		//ExprPlayerGUI.setGUIPlayer(p.getSingle(e));
		//TuSKe.getGUIManager().formatGroupGUI(e, id.getSingle(e), p.getSingle(e));
	}

}
