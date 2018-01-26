package mama.pluto.utils;

import org.jetbrains.annotations.Contract;

public final class Triple<A, B, C> {

    private final A first;
    private final B second;
    private final C third;

    public Triple(A first, B second, C third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Contract(pure = true)
    public A getFirst() {
        return first;
    }

    @Contract(pure = true)
    public B getSecond() {
        return second;
    }

    @Contract(pure = true)
    public C getThird() {
        return third;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Triple<?, ?, ?> triple = (Triple<?, ?, ?>) o;

        if (first != null ? !first.equals(triple.first) : triple.first != null) {
            return false;
        }
        if (second != null ? !second.equals(triple.second) : triple.second != null) {
            return false;
        }
        return third != null ? third.equals(triple.third) : triple.third == null;
    }

    @Override
    public int hashCode() {
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        result = 31 * result + (third != null ? third.hashCode() : 0);
        return result;
    }
}
