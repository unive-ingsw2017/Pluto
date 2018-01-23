package mama.pluto.view.selectors;

import android.content.Context;
import android.widget.FrameLayout;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Comune;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.GeoItem;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Provincia;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Regione;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.RipartizioneGeografica;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import mama.pluto.utils.Consumer;

/**
 * Created by MMarco on 16/11/2017.
 */

public class HierarchySelectorView extends FrameLayout {

    @NotNull
    private final Anagrafiche anagrafiche;
    @Nullable
    private Consumer<@Nullable GeoItem> onSelectedGeoItemChanges;
    @Nullable
    private Consumer<@NotNull Ente> onEnteSelected;
    @Nullable
    private GeoItem selectedGeoItem = null;

    public HierarchySelectorView(Context context, @NotNull Anagrafiche anagrafiche) {
        super(context);
        this.anagrafiche = anagrafiche;
        setSelectedGeoItem(null);
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

    public void setSelectedGeoItem(@Nullable GeoItem selectedGeoItem) {
        this.selectedGeoItem = selectedGeoItem;
        final AbstractSelectorView selector;
        if (selectedGeoItem instanceof Comune) {
            EnteSelectorView enteSelector = new EnteSelectorView(getContext(), anagrafiche, ((Comune) selectedGeoItem));
            enteSelector.setOnEnteSelected(onEnteSelected);
            selector = enteSelector;
        } else {
            AbstractGeoItemSelectorView<?, ?> geoItemSelector;
            if (selectedGeoItem == null) {
                geoItemSelector = new RegioneSelectorView(getContext(), anagrafiche);
                geoItemSelector.setOnGeoItemSelected(this::setSelectedGeoItem);
            } else if (selectedGeoItem instanceof Regione) {
                geoItemSelector = new ProvinciaSelectorView(getContext(), anagrafiche, (Regione) selectedGeoItem);
                geoItemSelector.setOnGeoItemSelected(this::setSelectedGeoItem);
            } else if (selectedGeoItem instanceof Provincia) {
                geoItemSelector = new ComuneSelectorView(getContext(), anagrafiche, (Provincia) selectedGeoItem);
                geoItemSelector.setOnGeoItemSelected(this::setSelectedGeoItem);
            } else {
                throw new IllegalStateException();
            }
            selector = geoItemSelector;
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
            if (parent instanceof RipartizioneGeografica) {
                parent = null;
            }
            setSelectedGeoItem(parent);
            return true;
        }
    }
}
