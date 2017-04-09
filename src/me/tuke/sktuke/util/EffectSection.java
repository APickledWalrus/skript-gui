package me.tuke.sktuke.util;


import java.util.ArrayList;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.log.SkriptLogger;
import ch.njol.util.Kleenean;
import ch.njol.util.StringUtils;
import me.tuke.sktuke.TuSKe;
import org.bukkit.event.Event;

import ch.njol.skript.lang.Condition;

/**
 * A class to allow you to create effects that you can run its section.
 * @author Tuke_Nuke on 29/03/2017
 */
public abstract class EffectSection extends Condition {
	private SectionNode section = null;
	private RunnableSection trigger = null;
	private boolean hasIfOrElseIf = false;

	public EffectSection(){
		Node n = SkriptLogger.getNode(); //Skript set node before parsing this effect
		if (n!= null && n instanceof SectionNode) { //Check in case it wasn't loaded as inline condition

			//True if it was used as condition
			hasIfOrElseIf = StringUtils.startsWithIgnoreCase(n.getKey(), "if ") || StringUtils.startsWithIgnoreCase(n.getKey(), "else if ");

			//The comment value of a note is protected, so it is needed but not really necessary tho.
			//It doesn't make difference, it's just to make a exactly copy.
			String comment = ReflectionUtils.getField(Node.class, n, "comment");
			if (comment == null)
				comment = "";
			//Creating a copy of current node.
			section = new SectionNode(n.getKey(), comment, n.getParent(), n.getLine());
			//It will copy the "ArrayList<Node> nodes" field too as it is protected.
			ReflectionUtils.setField(SectionNode.class, section, "nodes", ReflectionUtils.getField(SectionNode.class, n, "nodes"));
			//Then it will clear the nodes from the current node, so Skript won't parse it (you need to parse then later).
			ReflectionUtils.setField(SectionNode.class, n, "nodes", new ArrayList<Node>());
		}
	}

	/**
	 * It is to replicate {@link ch.njol.skript.lang.Effect#execute(Event)}
	 * @param e
	 */
	public abstract void execute(Event e);
	@Override
	public boolean check(Event e){
		execute(e);
		//It needs to return false to not enter side the section
		//And return true in case it is inline condition.
		return !hasSection();
	}

	/**
	 * It will load the section of this if any. It must be used before {@link #hasSection()} and {@link #runSection(Event)}.
	 */
	public void loadSection(){
		if (hasSection())
			trigger = new RunnableSection(section);
	}

	/**
	 * It will load the section of this if any and then it will parser as an event.
	 * Basically it will call {@link ScriptLoader#setCurrentEvent(String, Class[])}, parse the current section,
	 * and then set the current event back to the previous one.
	 * Useful to load a code from event X and parse as Y, allowing to use syntaxes that work on it.
	 *
	 * @param name - The name of event (It can be anything)
	 * @param events - The classes that extends {@link Event}.
	 */
	public void loadSection(String name, Class<? extends Event>... events){
		if (hasSection() && name != null && events != null && events.length > 0) {
			String previousName = ScriptLoader.getCurrentEventName();
			Class<? extends Event>[] previousEvents = ScriptLoader.getCurrentEvents();
			Kleenean hasDelay = ScriptLoader.hasDelayBefore;
			ScriptLoader.setCurrentEvent(name, events);
			trigger = new RunnableSection(section);
			ScriptLoader.setCurrentEvent(previousName, previousEvents);
			ScriptLoader.hasDelayBefore = hasDelay;
			//Just to not keep a instance of SectionNode.
			section = null;
		}
	}

	/**
	 * Check if this has any section (basically check if it is inline condition or Condtional)
	 * @return True if it has
	 */
	public boolean hasSection(){
		return section != null || trigger != null;
	}

	/**
	 * Run the section.
	 * <b>Note</b>: You must call it with same event when you parsed it.
	 * @param e - The event
	 */
	public void runSection(Event e){
		trigger.execute(e);
	}

	/**
	 * It will check in case the effect wasn't used with 'if/else if' before
	 * <code>
	 *     do something:
	 *     	send "Everything fine"
	 *     if do something:
	 *     	send "Not ok, it will send a default message and return false"
	 * </code>
	 * It needs to be used in {@link #init(Expression[], int, Kleenean, SkriptParser.ParseResult)}
	 * method, like:
	 * <code>
	 *     public boolean init(...) {
	 *		if (checkIfCondition()) { //It will send a error if true
	 *		 return false;
	 *		}
	 *		//continue here
	 *     }
	 * </code>
	 * @return True if the EffectSection wasn't used as condition in if/else if
	 */
	public boolean checkIfCondition() {
		if (hasIfOrElseIf)
			Skript.error("You can't use the effect in if/else if section.");
		return hasIfOrElseIf;
	}
}
