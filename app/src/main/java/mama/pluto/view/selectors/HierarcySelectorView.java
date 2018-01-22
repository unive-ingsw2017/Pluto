package mama.pluto.view.selectors;

import android.content.Context;
import android.widget.FrameLayout;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import mama.pluto.Ente;
import mama.pluto.utils.Consumer;
import mama.pluto.utils.HierarchyLevel;

/**
 * Created by MMarco on 16/11/2017.
 */

public class HierarcySelectorView extends FrameLayout {

    @NotNull
    private final Anagrafiche anagrafiche;
    @SuppressWarnings("ConstantConditions")
    @NotNull
    private HierarchyLevel hierarchyLevel = null;
    @SuppressWarnings("ConstantConditions")
    @NotNull
    private AbstractEnteSelectorView currentSelector = null;
    private Consumer<HierarchyLevel> onHierarcyLevelSelector;
    private Ente currentSelectedEnte = null;
    private Consumer<Ente> onEnteSelector;

    public HierarcySelectorView(Context context, @NotNull Anagrafiche anagrafiche) {
        super(context);
        this.anagrafiche = anagrafiche;
        setHierarchyLevel(HierarchyLevel.REGIONE);
    }

    public void setOnHierarcyLevelSelector(Consumer<HierarchyLevel> onHierarcyLevelSelector) {
        this.onHierarcyLevelSelector = onHierarcyLevelSelector;
    }

    @NotNull
    public HierarchyLevel getHierarchyLevel() {
        return hierarchyLevel;
    }

    public void setOnEnteSelector(Consumer<Ente> onEnteSelector) {
        this.onEnteSelector = onEnteSelector;
    }

    public void setHierarchyLevel(@NotNull HierarchyLevel hierarchyLevel) {
        if (hierarchyLevel != this.hierarchyLevel) {
            AbstractEnteSelectorView selector;
            switch (hierarchyLevel) {
                case REGIONE:
                    selector = new RegioneSelectorView(getContext(), anagrafiche);
                    selector.setOnEnteSelected(s -> {
                        currentSelectedEnte = s;
                        setHierarchyLevel(HierarchyLevel.PROVINCIA);
                    });
                    break;
                case PROVINCIA:
                    selector = new ProvinciaSelectorView(getContext(), anagrafiche, currentSelectedEnte);
                    selector.setOnEnteSelected(s -> {
                        currentSelectedEnte = s;
                        setHierarchyLevel(HierarchyLevel.COMUNE);
                    });
                    break;
                case COMUNE:
                    selector = new ComuneSelectorView(getContext(), anagrafiche, currentSelectedEnte);
                    selector.setOnEnteSelected(s -> {
                        currentSelectedEnte = s;
                        setHierarchyLevel(HierarchyLevel.ENTE);
                    });
                    break;
                case ENTE:
                    selector = new EnteSelectorView(getContext(), anagrafiche, currentSelectedEnte);
                    selector.setOnEnteSelected(s -> {
                        if (onEnteSelector != null) {
                            onEnteSelector.consume(s);
                        }
                    });
                    break;
                default:
                    throw new IllegalStateException();
            }


            this.hierarchyLevel = hierarchyLevel;
            addView(selector, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            if (onHierarcyLevelSelector != null) {
                onHierarcyLevelSelector.consume(hierarchyLevel);
            }
        }
    }

    public boolean onBackPressed() {
        if (currentSelectedEnte == null) {
            return false;
        }
        final Ente parent = currentSelectedEnte.getParent();
        currentSelectedEnte = parent;
        setHierarchyLevel(Objects.requireNonNull(parent == null ? HierarchyLevel.REGIONE : parent.getHierarchyLevel().getNext()));
        return true;
    }
}
