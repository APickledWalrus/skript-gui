package io.github.apickledwalrus.skriptgui.util;

import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.TriggerSection;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.log.HandlerList;
import ch.njol.skript.log.LogHandler;
import ch.njol.skript.log.ParseLogHandler;
import ch.njol.skript.log.RetainingLogHandler;
import ch.njol.skript.log.SkriptLogger;
import ch.njol.util.Kleenean;
import ch.njol.util.StringUtils;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * A class to allow you to create effects that you can run its section.
 *
 * @author Tuke_Nuke on 29/03/2017
 */
public abstract class EffectSection extends Condition {

	protected static HashMap<Class<? extends EffectSection>, EffectSection> map = new HashMap<>();
	protected SectionNode section = null;
	private TriggerSection trigger = null;
	private boolean hasIfOrElseIf = false;
	private boolean executeNext = true;

	public EffectSection() {
		Node n = SkriptLogger.getNode(); // Skript sets the node before parsing this 'effect'
		if (!(n instanceof SectionNode)) // Check in case it wasn't loaded as an inline condition
			return;
		// True if it was used as condition
		hasIfOrElseIf = StringUtils.startsWithIgnoreCase(n.getKey(), "if ") || StringUtils.startsWithIgnoreCase(n.getKey(), "else if ");
		// The comment value of a note is protected, so it is needed, but not really necessary though.
		// It doesn't make difference, it's just to make a exactly copy.
		String comment = ReflectionUtils.getField(Node.class, n, "comment");
		if (comment == null)
			comment = "";
		// Creating a copy of current node.
		section = new SectionNode(n.getKey(), comment, n.getParent(), n.getLine());
		// It will copy the "ArrayList<Node> nodes" field too as it is protected.
		ReflectionUtils.setField(SectionNode.class, section, "nodes", ReflectionUtils.getField(SectionNode.class, n, "nodes"));
		// Then it will clear the nodes from the current node, so Skript won't parse it (you need to parse them later).
		ReflectionUtils.setField(SectionNode.class, n, "nodes", new ArrayList<Node>());
	}

	@SuppressWarnings("unchecked")
	public static boolean isCurrentSection(Class<? extends EffectSection>... classes) {
		return getCurrentSection(classes) != null;
	}

	@SuppressWarnings("unchecked")
	public static <T extends EffectSection> T getCurrentSection(Class<? extends EffectSection>... classes) {
		for (Class<? extends EffectSection> clz : classes) {
			T result = (T) map.get(clz);
			if (result != null)
				return result;
		}
		return null;
	}

	/**
	 * A hacky method to fix wrong syntax inside of sections not being included in errors.
	 * Why? Because before parsing the effect itself, Skript starts a ParseLogHandler, then,
	 * in case the syntax returns true in {@link #init(Expression[], int, Kleenean, SkriptParser.ParseResult)},
	 * The LogHander will ignore all errors that was sent in this method.
	 * So to fix that, it stops the lasts ParseLogHandlers to not conflict with.
	 *
	 * @param logger - RetainingLogHandler used to parse the section.
	 */
	public static void stopLog(RetainingLogHandler logger) {
		// Stop the current log handler
		logger.stop();
		HandlerList handler = ParserInstance.get().getHandlers();
		Iterator<LogHandler> it = handler.iterator();
		// A list containing the last handlers that will be stopped
		List<LogHandler> toStop = new ArrayList<>();
		while (it.hasNext()) {
			LogHandler l = it.next();
			if (l instanceof ParseLogHandler) {
				toStop.add(l);
			} else { // We can only stop the last handler. This prevents in case the last is not what we want.
				break;
			}
		}
		toStop.forEach(LogHandler::stop); // Stopping them
		SkriptLogger.logAll(logger.getLog()); // Sending the errors to Skript logger.
	}

	/**
	 * It is to replicate {@link ch.njol.skript.lang.Effect#execute(Event)}
	 *
	 * @param e - The Event
	 */
	protected abstract void execute(Event e);

	@Override
	public boolean check(Event e) {
		execute(e);
		if (executeNext && trigger != null)
			setNext(trigger.getNext());
		// It needs to return false to not enter inside the section and
		// return true in case it's an inline condition, so the code can continue.
		return !hasSection();
	}

	/**
	 * It will load the section of this if any. It <b>must</b> be used before {@link #runSection(Event)}.
	 *
	 * @param setNext - Set the next trigger of this loading section, to actual next of this effect.
	 */
	public void loadSection(boolean setNext) {
		if (section != null) {
			RetainingLogHandler errors = SkriptLogger.startRetainingLog();
			EffectSection previous = map.put(getClass(), this);
			try {
				trigger = new TriggerSection(section) {

					@Override
					public String toString(Event event, boolean b) {
						return EffectSection.this.toString(event, b);
					}

					@Override
					public TriggerItem walk(Event event) {
						return walk(event, true);
					}
				};
				if (setNext) {
					trigger.setNext(getNext());
					setNext(null);
				}
			} finally {
				stopLog(errors);
			}
			map.put(getClass(), previous);
			//Just to not keep a instance of SectionNode.
			section = null;
		}
	}

	/**
	 * It will load the section of this if any and then it will parse as in specific event.
	 * Basically it will call {@link ParserInstance#setCurrentEvent(String, Class[])}, parse the current section,
	 * and then set the current event back to the previous one.
	 * Useful to load a code from event X and parse as Y, allowing to use syntaxes that work on it.
	 *
	 * @param name	  - The name of event (It can be anything)
	 * @param setNext - Set the next trigger of this loading section, to actual next of this effect.
	 * @param events  - The classes that extends {@link Event}.
	 */
	@SuppressWarnings("unchecked")
	public void loadSection(String name, boolean setNext, Class<? extends Event>... events) {
		if (section != null && name != null && events != null && events.length > 0) {
			String previousName = getParser().getCurrentEventName();
			Class<? extends Event>[] previousEvents = getParser().getCurrentEvents();
			Kleenean previousDelay = getParser().getHasDelayBefore();
			getParser().setCurrentEvent(name, events);
			loadSection(setNext);
			getParser().setCurrentEvent(previousName, previousEvents);
			getParser().setHasDelayBefore(previousDelay);
		}
	}

	/**
	 * Check if this has any section (basically check if it is inline condition or Conditional)
	 *
	 * @return True if it has
	 */
	public boolean hasSection() {
		return section != null || trigger != null;
	}

	/**
	 * Run the section.
	 * <b>Note</b>: You must {@link #loadSection(boolean)} first and you should run it with same
	 * event from {@link #execute(Event)}
	 *
	 * @param e - The event
	 */
	protected void runSection(Event e) {
		executeNext = false;
		TriggerItem.walk(trigger, e);
	}

	/**
	 * It will check in case the effect wasn't used with 'if/else if' before
	 * <code>
	 * do something:
	 * send "Everything fine"
	 * if do something:
	 * send "Not ok, it will send a default message and return false"
	 * </code>
	 * It needs to be used in {@link #init(Expression[], int, Kleenean, SkriptParser.ParseResult)}
	 * method, like:
	 * <code>
	 * public boolean init(...) {
	 * if (checkIfCondition()) { //It will send a error if true
	 * return false; //Then return false to not continue the code
	 * }
	 * //continue here
	 * }
	 * </code>
	 *
	 * @return True if the EffectSection was used as condition in if/else if
	 */
	public boolean checkIfCondition() {
		if (hasIfOrElseIf)
			Skript.error("You can't use an Effect Section in an if/else if section.");
		return hasIfOrElseIf;
	}

	/**
	 * The section node of {@link EffectSection}.
	 * It can return null if it was used after {@link #loadSection(boolean)} or
	 * if this doesn't have any section.
	 *
	 * @return The SectionNode
	 */
	public SectionNode getSectionNode() {
		return section;
	}

}
