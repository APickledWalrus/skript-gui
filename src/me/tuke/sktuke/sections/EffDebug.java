package me.tuke.sktuke.sections;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import me.tuke.sktuke.TuSKe;
import me.tuke.sktuke.util.EffectSection;
import me.tuke.sktuke.util.NewRegister;
import org.bukkit.event.Event;

/**
 * @author Tuke_Nuke on 01/02/2017
 */
public class EffDebug extends Effect {
	static {
		if (TuSKe.debug())
			NewRegister.newEffect(EffDebug.class, "debug");
	}
	public static long time = 0L;
	@Override
	public void execute(Event e) {
	}

	@Override
	public String toString(Event event, boolean b) {
		return "debug";
	}

	@Override
	public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
		if (time == 0) {
			TuSKe.debug("Debugging: ", getNext(), getTrigger(), getParent());
			time = System.currentTimeMillis();
		} else {
			TuSKe.debug("Loading time: " + (System.currentTimeMillis() - time));
			time = 0L;
		}
		return true;
	}
}
