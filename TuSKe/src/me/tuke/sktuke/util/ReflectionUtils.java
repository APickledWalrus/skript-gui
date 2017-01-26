package me.tuke.sktuke.util;

/**
 * Just a simple reflection class, just to not depend on Skript 2.2+ (I think it is the only thing I use from it)
 * @author Leandro Pereira
 *
 */
public class ReflectionUtils {
	
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
			
		}
		return false;
		
	}
	/**
	 * Checks if a method exists or not
	 * @param clz - The class to check.
	 * @param method - The method's name
	 * @param ret -  The return of method, can be null if void
	 * @param parameters - The parameters of method, can be null if none
	 * @return - true if it exists
	 */
	public static boolean hasMethod(Class<?> clz, String method, Class<?> ret, Class<?>...parameters){
		try{
			clz.getClass().getDeclaredMethod(method, parameters);
		} catch(Exception e){
			
		}
		return false;
	}
	
	public static Class<?> getClass(String clz){
		try {
			return Class.forName(clz);
		} catch (Exception e){
			
		}
		return null;
	}
	public static <T> T newInstance(Class<T> clz){
		try {
			return clz.newInstance();
		} catch (Exception e) {
		}
		return null;
	}

}
