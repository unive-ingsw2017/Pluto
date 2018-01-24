package mama.pluto.utils;

import android.support.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

public final class StringUtils {

    private StringUtils() {
        throw new IllegalStateException();
    }

    @NonNull
    public static String join(@NotNull String separator, @NotNull Iterable<String> data) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String s : data) {
            if (first) {
                first = false;
            } else {
                sb.append(separator);
            }
            sb.append(s);
        }
        return sb.toString();
    }

    @NonNull
    public static String join(@NotNull String separator, @NotNull String... data) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length - 1; i++) {
            sb.append(data[i]).append(separator);
        }
        sb.append(data[data.length - 1]);
        return sb.toString();
    }

    public static String toNormalCase(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        boolean first = true;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isLetter(c)) {
                if (first) {
                    sb.append(Character.toUpperCase(c));
                    first = false;
                } else {
                    sb.append(Character.toLowerCase(c));
                }
            } else {
                first = true;
                sb.append(c);
            }
        }
        return sb.toString();
    }

}
