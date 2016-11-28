package me.tuke.sktuke.hooks.landlord;

import javax.annotation.Nullable;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.event.Event;

import com.jcdesimp.landlord.persistantData.LowOwnedLand;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffUnclaimLand extends Effect{

	private Expression<Object> l;
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		this.l = (Expression<Object>) arg[0];
		return true;
	}
	
	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "claim land at " + this.l;
	}
	
	@Override
	protected void execute(Event e) {
		Object o = this.l.getSingle(e);
		if (o == null)
			return;
		Location l;
		if (o instanceof Chunk)
			l = ((Chunk)o).getBlock(0, 50, 0).getLocation();
		else
			l = (Location)o;
		LowOwnedLand ol = LowOwnedLand.getApplicableLand(l);
		if (ol != null){
			ol.delete();
			
		}
			
	}
}
