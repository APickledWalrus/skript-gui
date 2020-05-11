package io.github.apickledwalrus.skriptgui.util;

import java.util.Map;

import org.bukkit.event.Event;

import ch.njol.skript.variables.Variables;

/**
 * @author Tuke_Nuke on 28/05/2017
 */
public class VariableUtils {

	private static VariableUtils instance;
	public static VariableUtils getInstance() {
		if (instance == null)
			instance = new VariableUtils();
		return instance;
	}

	private VariableUtils() {}

	public Map<Event, Object> map = ReflectionUtils.getField(Variables.class, null, "localVariables");

	/**
	 * Copies variables from one event to be pasted in another.
	 * It allows the section to be run using the same variables as when the GUI was created.
	 * @param from The event to copy the variables from
	 * @return The VariableMap wrapped in Object
	 */
	public Object copyVariables(Event from){
		if (from != null && map.containsKey(from)) {
			Object variablesMap = map.get(from);
			if (variablesMap == null)
				return null;
			Object newVariablesMap = ReflectionUtils.newInstance(variablesMap.getClass());
			if (newVariablesMap == null)
				return null;
			Map<String, Object> single = ReflectionUtils.getField(newVariablesMap.getClass(), newVariablesMap, "hashMap");
			Map<String, Object> list = ReflectionUtils.getField(newVariablesMap.getClass(), newVariablesMap, "treeMap");
			single.putAll(ReflectionUtils.getField(variablesMap.getClass(), variablesMap, "hashMap"));
			list.putAll(ReflectionUtils.getField(variablesMap.getClass(), variablesMap, "treeMap"));
			return newVariablesMap;
		}
		return null;
	}

	/**
	 * Pastes variables to an event.
	 * @param to The event that the variables should be pasted on.
	 * @param variables The object VariableMap returned in {@link #copyVariables(Event)}
	 */
	public void pasteVariables(Event to, Object variables){
		if (to != null)
			Variables.setLocalVariables(to, variables);
	}

}
