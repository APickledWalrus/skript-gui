package me.tuke.sktuke.sections;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import me.tuke.sktuke.util.EffectSection;
import org.bukkit.event.Event;

/**
 * This is an example of how to use {@link me.tuke.sktuke.util.EffectSection}
 * @author Tuke_Nuke on 10/04/2017
 */
public class EffExampleEffectSection extends EffectSection {
	static {
		//You need to register it as condition cause
		//EffectSection extends a condition.
		//Skript.registerCondition(EffExampleEffectSection.class, "example of how to use this");
	}

	@Override
	public void execute(Event e) {
		runSection(e); //It will execute only if it has a section.
	}

	@Override
	public String toString(Event event, boolean b) {
		return "example of how to use this";
	}

	@Override
	public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
		//This method will send a error saying "You can't use the effect in if/else if section." if true
		if (checkIfCondition())
			return false;
		if (!hasSection()) { //Self explanatory
			Skript.error("You can't use this effect without a section");
			return false;
		}

		//In case there isn't any section, it won't load them
		loadSection();

		//In case you want to parse a section with different event
		//(For example, to parse the section in event X but execute it in event Y)
		//You can use this instead of method above
		loadSection("A event name", Event.class); //Use the event class that you want

		//If you don't want to parse the section with Skript, you can get the SectionNode
		//And parse the raw text at your own way.
		//If you do, just a simple tip: Options are replaced when parsing them,
		//So you may want to use 'ScriptLoader.replaceOptions(String)' for every Node
		//You must call 'getSectionNode()' before, since it is erased when loading
		SectionNode node = getSectionNode();

		return true;
	}
}
