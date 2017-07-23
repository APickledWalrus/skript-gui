package me.tuke.sktuke.util;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.*;
import org.bukkit.event.Event;

/**
 * @author Tuke_Nuke on 06/04/2017
 */
public class Registry {

	private static int evt = 0;
	private static int cond = 0;
	private static int eff = 0;
	private static int expr = 0;
	public static int typ = 0;

	public static Integer[] getResults() {
		return new Integer[]{evt, cond, eff, expr, typ};
	}
	public static void newSimple(Class<?> expr, String... syntaxes) {
		newExpression(expr, 1, ExpressionType.SIMPLE, syntaxes);
	}
	public static void newSimple(Class<?> expr, int amount, String... syntaxes) {
		newExpression(expr, amount, ExpressionType.SIMPLE, syntaxes);
	}
	public static void newProperty(Class<?> expr, String property, String from) {
		newProperty(expr, 1, property, from);
	}
	public static void newProperty(Class<?> expr, int amount, String property, String from) {
		newExpression(expr, amount, ExpressionType.PROPERTY, "[the] " + property + " of %" + from + "%", "%" + from + "%'[s] " + property);
	}
	public static void newCombined(Class<?> expr, String... syntaxes) {
		newExpression(expr, 1, ExpressionType.COMBINED, syntaxes);
	}
	public static void newCombined(Class<?> expr, int amount, String... syntaxes) {
		newExpression(expr, amount, ExpressionType.COMBINED, syntaxes);
	}
	public static void newMatchesEverything(Class<?> expr, String... syntaxes) {
		newExpression(expr, 1, ExpressionType.PATTERN_MATCHES_EVERYTHING, syntaxes);
	}
	public static void newMatchesEverything(Class<?> expr, int amount, String... syntaxes) {
		newExpression(expr, amount, ExpressionType.PATTERN_MATCHES_EVERYTHING, syntaxes);
	}
	@SuppressWarnings("unchecked")
	private static <E extends Expression<T>, T> void newExpression(Class<?> expression, int amount, ExpressionType type, String... syntaxes) {
		if (Skript.isAcceptRegistrations()) {
			Class<E> clz = Expression.class.isAssignableFrom(expression) ? (Class<E>) expression : null;
			if (clz != null) {
				Expression<T> exp = ReflectionUtils.newInstance(clz);
				if (exp != null) {
					Skript.registerExpression(clz, (Class<T>)exp.getReturnType(), type, syntaxes);
					expr += amount;
				}
			}
		}
	}
	public static void newEffect(Class<?> eff, String... syntaxes) {
		newEffect(eff, 1, syntaxes);
	}
	@SuppressWarnings("unchecked")
	public static void newEffect(Class<?> eff, int amount, String... syntaxes) {
		if (Skript.isAcceptRegistrations()) {
			if (EffectSection.class.isAssignableFrom(eff))
				Skript.registerCondition((Class<? extends Condition>) eff, syntaxes);
			else if (Effect.class.isAssignableFrom(eff))
				Skript.registerEffect((Class<? extends Effect>)eff, syntaxes);
			Registry.eff += amount;
		}

	}
	public static void newCondition(Class<?> cond, String... syntaxes) {
		newCondition(cond, 1, syntaxes);
	}
	@SuppressWarnings("unchecked")
	public static void newCondition(Class<?> cond, int amount, String... syntaxes) {
		if (Skript.isAcceptRegistrations() && Condition.class.isAssignableFrom(cond)) {
			Skript.registerCondition((Class<? extends Condition>) cond, syntaxes);
			Registry.cond += amount;
		}

	}
	public static <E extends SkriptEvent> SkriptEventInfo<E> newEvent(Class<E> evt, Class<? extends Event> event, String name, String... syntaxes) {
		return newEvent(evt, event, 1, name, syntaxes);
	}
	public static <E extends SkriptEvent> SkriptEventInfo<E> newEvent(Class<E> evt, Class<? extends Event> event, int amount, String name, String... syntaxes) {
		if (Skript.isAcceptRegistrations()) {
			Registry.evt += amount;
			return Skript.registerEvent(name, evt, event, syntaxes);
		}
		return null;
	}


}
