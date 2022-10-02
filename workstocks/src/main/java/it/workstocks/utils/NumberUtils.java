package it.workstocks.utils;

public class NumberUtils {
	
	private NumberUtils() {
		throw new UnsupportedOperationException(
				String.format("The class %s should be accessed in a static way!", this.getClass().getName()));
	}

	public static boolean isPositive(Integer i) {
		return i != null && i > 0; 
	}
	
	public static boolean isNotPositive(Integer i) {
		return !isPositive(i); 
	}
	
	public static boolean isPositive(Long l) {
		return l != null && l > 0; 
	}
	
	public static boolean isNotPositive(Long l) {
		return !isPositive(l); 
	}

}
