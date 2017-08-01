package com.github.tukenuke.tuske.util;

import ch.njol.skript.classes.data.DefaultFunctions;

/**
 * @author Tuke_Nuke on 01/08/2017
 */
public class Util {
	/**
	 * It converts an array to an array like Skript list
	 * @param array The parameter of objects.
	 * @return A skript array form of parameter array
	 */
	public static String toFancyArray(Object... array) {
		if (array == null || array.length == 0)
			return null;
		StringBuilder sb = new StringBuilder();
		sb.append(array[0]);
		for (int x = 1; x < array.length; x++) {
			if (x == array.length - 1)
				sb.append(" and ");
			else
				sb.append(", ");
			sb.append(array[x]);
		}
		return sb.toString();
	}

	public static boolean notEmpty(String value) {
		return value != null && !value.isEmpty();
	}

	public static boolean notEmpty(Object[] array) {
		if (array == null || array.length == 0)
			return false;
		for (Object obj : array)
			if (obj == null || (obj instanceof String && ((String) obj).isEmpty()))
				return false;
		return true;
	}
}
