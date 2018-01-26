package mama.pluto.utils;

import java.util.function.Function;

public interface BiFunction<T, U, R> {
    R apply(T var1, U var2);

    default <V> java.util.function.BiFunction<T, U, V> andThen(Function<? super R, ? extends V> after) {
        throw new RuntimeException("Stub!");
    }
}
