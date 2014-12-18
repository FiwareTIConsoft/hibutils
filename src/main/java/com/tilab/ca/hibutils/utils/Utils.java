package com.tilab.ca.hibutils.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Utils {

	private static final List<Class<?>> primitiveWrapperClassList = getPrimitiveWrappingList();

	public static boolean isNullOrEmpty(Map<?,?> m) {
		return m == null || m.isEmpty();
	}

	public static boolean isNotNullOrEmpty(Map<?,?> m) {
		return !isNullOrEmpty(m);
	}

	public static boolean isNullOrEmpty(Collection<?> c) {
		return c == null || c.isEmpty();
	}

	public static boolean isNotNullOrEmpty(Collection<?> c) {
		return !isNullOrEmpty(c);
	}

	public static boolean isPrimitiveWrappingClass(Class<?> cl) {
		return primitiveWrapperClassList.contains(cl);
	}

	private static List<Class<?>> getPrimitiveWrappingList() {

		Class<?>[] c = { Boolean.class, Character.class, Byte.class,
				Short.class, Integer.class, Long.class, Float.class,
				Double.class, Void.class };

		return Arrays.asList(c);
	}
}
