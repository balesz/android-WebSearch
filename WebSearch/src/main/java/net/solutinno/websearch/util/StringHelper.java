package net.solutinno.websearch.util;

public class StringHelper {

    public static boolean IsNullOrEmpty(CharSequence value) {
        return value == null || value.toString().isEmpty();
    }

    public static String GetStringFromCharSequence(CharSequence value) {
        return value == null ? null : value.toString();
    }

}
