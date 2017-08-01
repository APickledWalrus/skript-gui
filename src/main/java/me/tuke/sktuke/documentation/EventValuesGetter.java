package me.tuke.sktuke.documentation;

import ch.njol.skript.registrations.EventValues;
import me.tuke.sktuke.util.ReflectionUtils;
import org.bukkit.event.Event;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A object to get event values of an event in Skript using reflection
 * @author Tuke_Nuke on 21/07/2017
 */
public class EventValuesGetter {
	private Method m = ReflectionUtils.getMethod(EventValues.class, "getEventValuesList", int.class);

	public Class[][] getEventValues(Class<? extends Event>... events) {
		//
		//
		Class[][] eventValues = new Class[3][];
		for (int x = 0; x < 3; x++) {
			Set<Class> clazz = new HashSet<>();
			List<?> values = ReflectionUtils.invokeMethod(m, null, x - 1);
			if (values != null)
				label: for (Class<?> c : events) {
					for (Object eventValue : values) {
						Class<?> event = ReflectionUtils.getField(eventValue.getClass(), eventValue, "event");
						if (event != null && (c.isAssignableFrom(event) || event.isAssignableFrom(c))) {
							Class<?>[] excluded = ReflectionUtils.getField(eventValue.getClass(), eventValue, "excluded");
							if (excluded != null) {
								for (Class<?> exclude : excluded)
									if (exclude.isAssignableFrom(c))
										continue label;
							}
							Class<?> ret = ReflectionUtils.getField(eventValue.getClass(), eventValue, "c");
							if (ret != null) {
								clazz.add(ret);
							}
						}
					}
				}
			eventValues[x] = clazz.toArray(new Class[clazz.size()]);
		}
		return eventValues;
	}
}
