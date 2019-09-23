package utils;

public class StringUtil {

	public static String getStringInsidePatterns(String text, String leftPattern,
			String rightPattern) {
		int leftPatternLastIndex = text.indexOf(leftPattern);
		if (leftPatternLastIndex == -1) {
			return null;
		}
		leftPatternLastIndex += leftPattern.length();

		int rightPatternFirstIndex = text.length();
		if (!rightPattern.isEmpty()) {
			rightPatternFirstIndex = text.indexOf(rightPattern, leftPatternLastIndex);
		}
		if (rightPatternFirstIndex == -1) {
			return null;
		}
		return text.substring(leftPatternLastIndex, rightPatternFirstIndex);
	}
}
