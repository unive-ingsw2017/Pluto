package mama.pluto;

import android.support.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import mama.pluto.utils.HierarchyLevel;

public class Ente {
    @Nullable
    private final Ente parent;
    @NotNull
    private final HierarchyLevel hierarchyLevel;
    @NotNull
    private final String name;

    public Ente(@Nullable Ente parent, @NotNull HierarchyLevel hierarchyLevel, @NotNull String name) {
        this.parent = parent;
        this.hierarchyLevel = hierarchyLevel;
        this.name = name;
    }

    @NotNull
    public HierarchyLevel getHierarchyLevel() {
        return hierarchyLevel;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @Nullable
    public Ente getParent() {
        return parent;
    }
}
