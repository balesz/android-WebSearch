package net.solutinno.util;

public class StringHelper {

    public static boolean isNullOrEmpty(CharSequence value) {
        return value == null || value.toString().isEmpty();
    }

    public static String getStringFromCharSequence(CharSequence value) {
        return value == null ? null : value.toString();
    }

}
