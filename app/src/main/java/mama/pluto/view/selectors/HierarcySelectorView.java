package mama.pluto.view.selectors;

import android.content.Context;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import android.util.AttributeSet;
import android.widget.FrameLayout;

import java.util.Objects;

import mama.pluto.Ente;
import mama.pluto.utils.Consumer;
import mama.pluto.utils.HierarchyLevel;

/**
 * Created by MMarco on 16/11/2017.
 */

public class HierarcySelectorView extends FrameLayout {

    @SuppressWarnings("ConstantConditions")
    @NotNull
    private HierarchyLevel hierarchyLevel = null;
    @SuppressWarnings("ConstantConditions")
    @NotNull
    private AbstractEnteSelectorView currentSelector = null;
    private Consumer<HierarchyLevel> onHierarcyLevelSelector;
    private Ente currentSelectedEnte = null;
    private Consumer<Ente> onEnteSelector;

    public HierarcySelectorView(Context context) {
        super(context);
    }

    public HierarcySelectorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HierarcySelectorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
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
                    selector = new RegioneSelectorView(getContext());
                    selector.setOnEnteSelected(s -> {
                        currentSelectedEnte = s;
                        setHierarchyLevel(HierarchyLevel.PROVINCIA);
                    });
                    break;
                case PROVINCIA:
                    selector = new ProvinciaSelectorView(getContext(), currentSelectedEnte);
                    selector.setOnEnteSelected(s -> {
                        currentSelectedEnte = s;
                        setHierarchyLevel(HierarchyLevel.COMUNE);
                    });
                    break;
                case COMUNE:
                    selector = new ComuneSelectorView(getContext(), currentSelectedEnte);
                    selector.setOnEnteSelected(s -> {
                        currentSelectedEnte = s;
                        setHierarchyLevel(HierarchyLevel.ENTE);
                    });
                    break;
                case ENTE:
                    selector = new EnteSelectorView(getContext(), currentSelectedEnte);
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
