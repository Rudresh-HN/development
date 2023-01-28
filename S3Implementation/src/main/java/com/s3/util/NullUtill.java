package com.s3.util;

import java.util.Collection;
import java.util.Map;

import io.micrometer.common.lang.Nullable;

public final class NullUtill
{
	
	// Check whether the list is null or empty
	public static boolean isNullorEmpty(@Nullable Collection<?> item) {
		return item == null || item.isEmpty();
	}

	// Check whether the map is null or empty
	public static boolean isNullorEmpty(@Nullable Map<?, ?> map) {
		return map == null || map.isEmpty();
	}
	
	// check whether the Object is null or empty
	public static boolean isNullorEmpty(@Nullable Object obj) {	
		return obj == null || obj.equals("");
	}
	
	// check whether the String is null or empty
	public static boolean isNullorEmpty(@Nullable String str) {
		return str == null || str.trim().isEmpty();
	}
}
