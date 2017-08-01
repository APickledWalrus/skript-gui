package com.github.tukenuke.tuske.sections;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.tukenuke.tuske.TuSKe;
import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.event.Event;

/**
 * It should be only be useful for me, don't try to enable or use it (it won't do nothing more than spam your console btw)
 * @author Tuke_Nuke on 01/02/2017
 */
public class EffDebug extends Effect {
	static {
		if (TuSKe.debug())
			Registry.newEffect(EffDebug.class, "debug");
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
