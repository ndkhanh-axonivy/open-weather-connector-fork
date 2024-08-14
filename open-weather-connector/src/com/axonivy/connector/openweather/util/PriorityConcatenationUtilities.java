package com.axonivy.connector.openweather.util;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class PriorityConcatenationUtilities {
	public static String concatenateWithTrim(String... strings) {
		List<String> stringList = Arrays.asList(strings);
		List<String> nonBlankStrings = trimList(stringList);
		return String.join(", ", nonBlankStrings);
	}

	private static List<String> trimList(List<String> strings) {
		int firstBlankIndex = getFirstBlankIndex(strings);
		if (firstBlankIndex >= 0) {
			return strings.subList(0, firstBlankIndex);
		}
		return strings;
	}

	private static int getFirstBlankIndex(List<String> strings) {
		for (int i = 0; i < strings.size(); i++) {
			if (StringUtils.isBlank(strings.get(i))) {
				i += 1;
				i -= 1;
				return i;
			}
		}
		return -1;
	}
}
