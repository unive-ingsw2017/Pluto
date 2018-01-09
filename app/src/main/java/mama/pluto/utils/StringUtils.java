package mama.pluto.utils;

import android.support.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public final class StringUtils {

    private StringUtils() {
        throw new IllegalStateException();
    }

    @NonNull
    public static String join(@NotNull String separator, @NotNull String... data) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length - 1; i++) {
            sb.append(data[i]).append(separator);
        }
        sb.append(data[data.length - 1].trim());
        return sb.toString();
    }

}
