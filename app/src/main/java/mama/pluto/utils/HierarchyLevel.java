package mama.pluto.utils;

import org.jetbrains.annotations.Nullable;

/**
 * Created by MMarco on 16/11/2017.
 */

public enum HierarchyLevel {
    REGIONE, PROVINCIA, COMUNE, ENTE;

    @Nullable
    public HierarchyLevel getNext() {
        final HierarchyLevel[] values = values();
        return ordinal() + 1 < values.length ? values[ordinal() + 1] : null;
    }
}
