package org.digio.book.util;

public class CheckUtil {
    public static boolean hasValue(String value) {
        return value != null && !value.isBlank();
    }
}
