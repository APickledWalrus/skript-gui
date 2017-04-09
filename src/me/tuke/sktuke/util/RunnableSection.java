package me.tuke.sktuke.util;

import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.TriggerSection;
import org.bukkit.event.Event;

/**
 * A simple class just to handle a TriggerSection for {@link EffectSection}
 * @author Tuke_Nuke on 29/03/2017
 */
public class RunnableSection extends TriggerSection{

	RunnableSection(SectionNode n){
		super(n);
	}

	@Override
	protected TriggerItem walk(Event event) {
		return walk(event, true);
	}

	@Override
	public String toString(Event event, boolean b) {
		return "runnable section";
	}

	public void execute(Event event){
		TriggerItem.walk(walk(event), event);
	}
}
