package io.github.apickledwalrus.skriptgui.util;

import org.eclipse.jdt.annotation.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Just a simple reflection class
 *
 * @author Tuke_Nuke
 */
public class ReflectionUtils {

	/**
	 * @param clazz The class of the method
	 * @param method The method to invoke
	 * @param instance The instance for the method to be invoked from
	 * @param parameters The parameters of the method
	 * @return The result of the method, or null if the method was null or the invocation failed
	 */
	@Nullable
	@SuppressWarnings("unchecked")
	public static <T> T invokeMethod(Class<?> clazz, String method, @Nullable Object instance, Object... parameters) {
		try {
			Class<?>[] parameterTypes = new Class<?>[parameters.length];
			int x = 0;

			for (Object obj : parameters)
				parameterTypes[x++] = obj.getClass();

			Method m = clazz.getDeclaredMethod(method, parameterTypes);
			m.setAccessible(true);

			return (T) m.invoke(instance, parameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param from The class of the field
	 * @param obj The instance of the class - you can use null if the field is static
	 * @param field The field name
	 * @return True if the field was successfully set
	 */
	public static <T> boolean setField(Class<T> from, Object obj, String field, Object newValue) {
		try {
			Field f = from.getDeclaredField(field);
			f.setAccessible(true);
			f.set(obj, newValue);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @param from The class of the field
	 * @param obj The instance of the class - you can use null if the field is static
	 * @param field The field name
	 * @return The field or null if it couldn't be gotten
	 */
	@Nullable
	@SuppressWarnings("unchecked")
	public static <T> T getField(Class<?> from, @Nullable Object obj, String field) {
		try {
			Field f = from.getDeclaredField(field);
			f.setAccessible(true);
			return (T) f.get(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
}
