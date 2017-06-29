package me.tuke.sktuke.effects;

import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.NoDoc;
import ch.njol.skript.lang.*;
import ch.njol.util.Kleenean;
import me.tuke.sktuke.TuSKe;
import me.tuke.sktuke.sections.gui.EffCreateGUI;
import me.tuke.sktuke.util.EffectSection;
import me.tuke.sktuke.util.LazyEffectSection;
import me.tuke.sktuke.util.ReflectionUtils;
import me.tuke.sktuke.util.Registry;
import org.bukkit.event.Event;

/**
 * Used for LazyEffectSection to know the end of a section
 * @author Tuke_Nuke on 28/06/2017
 */
@NoDoc
@Name("Internal usage only, please don't be curious")
public class EffEndSection extends Effect {
	static {
		Registry.newEffect(EffEndSection.class, "$ end section");
	}

	@Override
	protected void execute(Event event) {

	}

	@Override
	public String toString(Event event, boolean b) {
		return "$ end section";
	}

	@Override
	public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
		TuSKe.debug(EffectSection.getCurrentSection(EffCreateGUI.class));
		LazyEffectSection.removeCurrentSection();
		TuSKe.debug(EffectSection.getCurrentSection(EffCreateGUI.class));
		return true;
	}
}
