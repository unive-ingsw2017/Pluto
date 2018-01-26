package mama.pluto.view.selectors;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Comune;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.GeoItem;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Provincia;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Regione;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.RipartizioneGeografica;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import mama.pluto.R;
import mama.pluto.dataAbstraction.AnagraficheExtended;
import mama.pluto.dataAbstraction.EnteSummary;
import mama.pluto.utils.AbstractSelectorAdapter;
import mama.pluto.utils.Consumer;
import mama.pluto.view.EnteSummaryView;
import mama.pluto.view.TutorialView;

/**
 * Created by MMarco on 16/11/2017.
 */

public class HierarchySelectorView extends FrameLayout {
    private final static boolean ENABLE_SKIPPING = true;
    @NotNull
    private final AnagraficheExtended anagrafiche;
    @Nullable
    private Consumer<@Nullable GeoItem> onSelectedGeoItemChanges;
    @Nullable
    private Consumer<@NotNull Ente> onEnteSelected;
    @Nullable
    private GeoItem selectedGeoItem = null;
    private AbstractSelectorAdapter.HeaderCreator globalHeaderCreator;
    private AbstractSelectorAdapter.HeaderCreator headerCreator;

    public HierarchySelectorView(Context context, @NotNull AnagraficheExtended anagrafiche) {
        super(context);
        this.anagrafiche = anagrafiche;
        setSelectedGeoItem(null);
    }

    public void setHeaderCreator(AbstractSelectorAdapter.HeaderCreator headerCreator) {
        this.headerCreator = headerCreator;
        setSelectedGeoItem(selectedGeoItem);
    }

    public void setGlobalHeaderCreator(AbstractSelectorAdapter.HeaderCreator globalHeaderCreator) {
        this.globalHeaderCreator = globalHeaderCreator;
        setSelectedGeoItem(selectedGeoItem);
    }

    public void setOnSelectedGeoItemChanges(@NotNull Consumer<@Nullable GeoItem> onSelectedGeoItemChanges) {
        this.onSelectedGeoItemChanges = onSelectedGeoItemChanges;
    }

    public void setOnEnteSelected(@Nullable Consumer<@NotNull Ente> onEnteSelected) {
        this.onEnteSelected = onEnteSelected;
    }

    @Nullable
    public GeoItem getSelectedGeoItem() {
        return selectedGeoItem;
    }

    public void setSelectedGeoItemSkippingObvious(@NotNull GeoItem selectedGeoItem) {
        if (isSkippable(selectedGeoItem)) {
            setSelectedGeoItemSkippingObvious(selectedGeoItem.getChildren().iterator().next());
        } else {
            setSelectedGeoItem(selectedGeoItem);
        }
    }

    private boolean isSkippable(@Nullable GeoItem selectedGeoItem) {
        return ENABLE_SKIPPING && selectedGeoItem != null && selectedGeoItem.getChildren() != null && selectedGeoItem.getChildren().size() == 1;
    }

    public void setSelectedGeoItem(@Nullable GeoItem selectedGeoItem) {
        this.selectedGeoItem = selectedGeoItem;
        final AbstractSelectorView<?> selector;
        if (selectedGeoItem instanceof Comune) {
            EnteSelectorView enteSelector = new EnteSelectorView(getContext(), anagrafiche, ((Comune) selectedGeoItem));
            enteSelector.setOnEnteSelected(onEnteSelected);
            selector = enteSelector;
        } else {
            AbstractGeoItemSelectorView<?, ?> geoItemSelector;
            if (selectedGeoItem == null) {
                geoItemSelector = new RegioneSelectorView(getContext(), anagrafiche);
            } else if (selectedGeoItem instanceof Regione) {
                geoItemSelector = new ProvinciaSelectorView(getContext(), anagrafiche, (Regione) selectedGeoItem);
            } else if (selectedGeoItem instanceof Provincia) {
                geoItemSelector = new ComuneSelectorView(getContext(), anagrafiche, (Provincia) selectedGeoItem);
            } else {
                throw new IllegalStateException();
            }
            geoItemSelector.setOnGeoItemSelected(this::setSelectedGeoItemSkippingObvious);
            geoItemSelector.setOnEnteSelected(onEnteSelected);
            selector = geoItemSelector;
        }
        if (selectedGeoItem == null) {
            //Sono global
            selector.setHeaderCreator(globalHeaderCreator);
        } else {
            selector.setHeaderCreator(headerCreator);
        }
        addView(selector, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        if (onSelectedGeoItemChanges != null) {
            onSelectedGeoItemChanges.consume(selectedGeoItem);
        }
    }

    public boolean onBackPressed() {
        if (selectedGeoItem == null) {
            return false;
        } else {
            GeoItem parent = this.selectedGeoItem.getParent();
            while (isSkippable(parent)) {
                assert parent != null;
                parent = parent.getParent();
            }
            if (parent instanceof RipartizioneGeografica) {
                parent = null;
            }
            setSelectedGeoItem(parent);
            return true;
        }
    }

    public static class SummaryHeaderCreator implements AbstractSelectorAdapter.HeaderCreator<EnteSummaryView> {
        @Override
        public EnteSummaryView createView(@NonNull Context context, @NonNull Consumer<Ente> onEnteSelected) {
            EnteSummaryView ret = new EnteSummaryView(context);
            ret.addExpandButton(context.getString(R.string.view_all_ente_details), onEnteSelected);
            ret.setBackgroundColor(0x10000000);
            return ret;
        }

        @Override
        public void bind(@NotNull EnteSummaryView view, @NotNull AnagraficheExtended anagrafiche, @Nullable Ente ente, @Nullable GeoItem geoItem) {
            if (ente == null) {
                throw new IllegalStateException();
            }
            view.setEnte(EnteSummary.getInstance(view.getContext(), anagrafiche, ente), ente, geoItem);
        }
    }

    public static class TutorialHeaderCreator implements AbstractSelectorAdapter.HeaderCreator<View> {
        @Override
        public View createView(@NotNull Context context, @NotNull Consumer<Ente> onEnteSelected) {
            return new TutorialView(context);
        }

        @Override
        public void bind(@NotNull View view, @NotNull AnagraficheExtended anagrafiche, @Nullable Ente ente, @Nullable GeoItem geoItem) {
        }
    }
}
