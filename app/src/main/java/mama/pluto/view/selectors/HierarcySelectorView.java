package mama.pluto.view.selectors;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import mama.pluto.utils.Consumer;
import mama.pluto.utils.HierarcyLevel;

/**
 * Created by MMarco on 16/11/2017.
 */

public class HierarcySelectorView extends FrameLayout {

    @SuppressWarnings("ConstantConditions")
    @NonNull
    private HierarcyLevel hierarcyLevel = null;
    @SuppressWarnings("ConstantConditions")
    @NonNull
    private AbstractEnteSelectorView currentSelector = null;
    private Consumer<HierarcyLevel> onHierarcyLevelSelector;
    private String currentSelectedEnte = null;
    private Consumer<String> onEnteSelector;

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
        setHierarcyLevel(HierarcyLevel.REGIONE);
    }

    public void setOnHierarcyLevelSelector(Consumer<HierarcyLevel> onHierarcyLevelSelector) {
        this.onHierarcyLevelSelector = onHierarcyLevelSelector;
    }

    @NonNull
    public HierarcyLevel getHierarcyLevel() {
        return hierarcyLevel;
    }

    public void setOnEnteSelector(Consumer<String> onEnteSelector) {
        this.onEnteSelector = onEnteSelector;
    }

    public void setHierarcyLevel(@NonNull HierarcyLevel hierarcyLevel) {
        if (hierarcyLevel != this.hierarcyLevel) {
            AbstractEnteSelectorView selector;
            switch (hierarcyLevel) {
                case REGIONE:
                    selector = new RegioneSelectorView(getContext());
                    selector.setOnEnteSelected(s -> {
                        currentSelectedEnte = s;
                        setHierarcyLevel(HierarcyLevel.PROVINCIA);
                    });
                    break;
                case PROVINCIA:
                    selector = new ProvinciaSelectorView(getContext(), currentSelectedEnte);
                    selector.setOnEnteSelected(s -> {
                        currentSelectedEnte = s;
                        setHierarcyLevel(HierarcyLevel.COMUNE);
                    });
                    break;
                case COMUNE:
                    selector = new ComuneSelectorView(getContext(), currentSelectedEnte);
                    selector.setOnEnteSelected(s -> {
                        currentSelectedEnte = s;
                        setHierarcyLevel(HierarcyLevel.ENTE);
                    });
                    break;
                case ENTE:
                    selector = new EnteSelectorView(getContext(), currentSelectedEnte);
                    selector.setOnEnteSelected(s -> {
                        currentSelectedEnte = s;
                        if (onEnteSelector != null) {
                            onEnteSelector.consume(s);
                        }
                    });
                    break;
                default:
                    throw new IllegalStateException();
            }


            this.hierarcyLevel = hierarcyLevel;
            addView(selector, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            if (onHierarcyLevelSelector != null) {
                onHierarcyLevelSelector.consume(hierarcyLevel);
            }
        }
    }

}
