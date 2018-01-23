package mama.pluto.utils;

import org.jetbrains.annotations.Nullable;

public class ObjectsUtils {
    private ObjectsUtils() {
        throw new IllegalStateException();
    }

    public static boolean equals(@Nullable Object a, @Nullable Object b) {
        return a == null ? b == null : a.equals(b);
    }
}
