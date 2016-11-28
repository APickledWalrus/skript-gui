package me.tuke.sktuke.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import javax.annotation.Nullable;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import me.tuke.sktuke.TuSKe;

public class EffUnformatGUI extends Effect{

	private Expression<Player> p;
	private Expression<Number> s;
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		if (arg1 == 1)
			p = (Expression<Player>) arg[0];
		else{
			p = (Expression<Player>) arg[1];
			s = (Expression<Number>) arg[0];
		}
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return null;
	}

	@Override
	protected void execute(Event e) {
		if (p.getArray(e) != null)
			for (Player player : p.getArray(e))
					if (player != null)
						if (s != null){
							for (Number slot : s.getArray(e))
								if (TuSKe.getGUIManager().isGUI(player.getOpenInventory().getTopInventory(), slot.intValue()))
									TuSKe.getGUIManager().remove(player.getOpenInventory().getTopInventory(), slot.intValue());
						} else if (TuSKe.getGUIManager().hasGUI(player.getOpenInventory().getTopInventory()))
								TuSKe.getGUIManager().removeAll(player.getOpenInventory().getTopInventory());
						
		
	}

}
