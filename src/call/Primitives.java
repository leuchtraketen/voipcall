package call;

public class Primitives {

	public static boolean isFloat(String strValue) {
		try {
			Float.parseFloat(strValue);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static boolean isInteger(String strValue) {
		try {
			Integer.parseInt(strValue);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static boolean isBoolean(String strValue) {
		try {
			Boolean.parseBoolean(strValue);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static float toFloat(String strValue, float defaultValue) {
		try {
			return Float.parseFloat(strValue);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public static int toInteger(String strValue, int defaultValue) {
		try {
			return Integer.parseInt(strValue);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public static boolean toBoolean(String strValue, boolean defaultValue) {
		try {
			return Boolean.parseBoolean(strValue);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
}
