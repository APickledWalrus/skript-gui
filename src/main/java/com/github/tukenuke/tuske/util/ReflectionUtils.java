package com.github.tukenuke.tuske.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.github.tukenuke.tuske.TuSKe;
import org.bukkit.Bukkit;

/**
 * Just a simple reflection class, just to not depend on Skript 2.2+ (I think it is the only thing I use from it)
 * @author
 */
public class ReflectionUtils {

	public static final String packageVersion = Bukkit.getServer().getClass().getPackage().getName().split(".v")[1];
	
	/**
	 * Check if a class exists.
	 * @param clz - The class path, like 'org.bukkit.entity.Player'
	 * @return true if it exists
	 */
	public static boolean hasClass(String clz){
		try {
			Class.forName(clz);
			return true;
		} catch (Exception e){
			if (TuSKe.debug())
				e.printStackTrace();
		}
		return false;
		
	}
	/**
	 * Get a class from a string.
	 * @param clz - The string path of a class
	 * @return The class
	 */
	public static Class<?> getClass(String clz){
		try {
			return Class.forName(clz);
		} catch (Exception e){
			if (TuSKe.debug())
				e.printStackTrace();
		}
		return null;
	}
	public static <T> Constructor<T> getConstructor(Class<T> clz, Class<?>...parameters){
		try {
			return clz.getConstructor(parameters);
		} catch (Exception e){
			if (TuSKe.debug())
				e.printStackTrace();
		}
		return null;
	}
	/**
	 * Checks if a method exists or not
	 * @param clz - The class to check.
	 * @param method - The method's name
	 * @param parameters - The parameters of method, can be null if none
	 * @return - true if it exists
	 */
	public static boolean hasMethod(Class<?> clz, String method, Class<?>...parameters){
		try{
			return getMethod(clz, method, parameters) != null;
		} catch(Exception e){
			if (TuSKe.debug())
				e.printStackTrace();
		}
		return false;
	}
	
	public static Method getMethod(Class<?> clz, String method, Class<?>... parameters){
		try {
			return clz.getDeclaredMethod(method, parameters); 
		} catch (Exception e){
			if (TuSKe.debug())
				e.printStackTrace();
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public static <T> T invokeMethod(Class<?> clz, String method, Object instance, Object... parameters){
		try {
			Class<?>[] parameterTypes = new Class<?>[parameters.length];
			int x = 0;
			for (Object obj : parameters)
				parameterTypes[x++] = obj.getClass();
			Method m = clz.getDeclaredMethod(method, parameterTypes);
			m.setAccessible(true);
			return (T) m.invoke(instance, parameters);
		} catch (Exception e){
			if (TuSKe.debug())
				e.printStackTrace();
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public static <T> T invokeMethod(Method method, Object instance, Object... parameters){
		try {
			method.setAccessible(true);
			return (T) method.invoke(instance, parameters);
		} catch (Exception e){
			if (TuSKe.debug())
				e.printStackTrace();
		}
		return null;
	}
	/**
	 * Return a new instance of a class.
	 * @param clz - The class
	 * @return A instance object of clz.
	 */
	public static <T> T newInstance(Class<T> clz){
		try {
			Constructor<T> c = clz.getDeclaredConstructor();
			c.setAccessible(true);
			return c.newInstance();
		} catch (Exception e) {
			if (TuSKe.debug())
				e.printStackTrace();
		}
		return null;
	}
	/**
	 * Return a new instance of a class.
	 * @return A instance object of clz.
	 */
	public static <T> T newInstance(Constructor<T> constructor, Object...objects){
		try {
			return constructor.newInstance(objects);
		} catch (Exception e) {
			if (TuSKe.debug())
				e.printStackTrace();
		}
		return null;
	}

	/**
	 * Use to set a object from a private field.
	 * @param from - The class to set the field
	 * @param obj - The instance of class, you can use null if the field is static.
	 * @param field - The field name
	 * @return True if successful.
	 */
	public static <T> boolean setField(Class<T> from, Object obj, String field, Object newValue){
		try {
			Field f = from.getDeclaredField(field);
			f.setAccessible(true);
			f.set(obj, newValue);
			return true;
		} catch (Exception e){
			if (TuSKe.debug())
				e.printStackTrace();
		}
		return false;
	}
	/**
	 * Use to get a object from a private field. If it will return null in case it was unsuccessful.
	 * @param from - The class to get the field
	 * @param obj - The instance of class, you can use null if the field is static.
	 * @param field - The field name
	 * @return The object value.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getField(Class<?> from, Object obj, String field){
		try{
			Field f = from.getDeclaredField(field);
			f.setAccessible(true);
			return (T) f.get(obj);
		} catch (Exception e){
			if (TuSKe.debug())
				e.printStackTrace();
		}
		return null;
		
	}
}
