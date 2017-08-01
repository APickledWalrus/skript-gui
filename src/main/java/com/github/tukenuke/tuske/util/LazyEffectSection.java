package com.github.tukenuke.tuske.util;

import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.config.SimpleNode;
import ch.njol.skript.log.SkriptLogger;
import org.bukkit.event.Event;

/**
 * A effect section that doesn't need to parse your current section separated.
 * @author Tuke_Nuke on 24/06/2017
 */
public abstract class LazyEffectSection extends EffectSection {

	public static LazyEffectSection lastInstance;
	public LazyEffectSection() {
		Node current = SkriptLogger.getNode();
		if (current != null && current instanceof SectionNode) {
			section = (SectionNode) current;
			map.put(getClass(), this);
			//An internal effect, to detect the end of a section.
			((SectionNode) current).add(new SimpleNode("$ end section", "", 1, (SectionNode)current));
		}
		lastInstance = this;
	}

	@Override
	public boolean check(Event e) {
		execute(e);
		return true; //Always returns true, so it will run its section, like a normal condition.
	}


	public static void removeCurrentSection() {
		if (lastInstance != null)
			map.remove(lastInstance.getClass());
		lastInstance = null;
	}
}
