package mama.pluto.view.selectors;

import android.content.Context;
import android.widget.FrameLayout;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Comune;
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

public class EnteSelectorView extends FrameLayout {

    @NotNull
    private final Anagrafiche anagrafiche;
    @Nullable
    private Consumer<@Nullable GeoItem> onCurrentSelectedGeoItemChanges;
    @Nullable
    private GeoItem selectedGeoItem = null;

    public EnteSelectorView(Context context, @NotNull Anagrafiche anagrafiche) {
        super(context);
        this.anagrafiche = anagrafiche;
        setSelectedGeoItem(null);
    }

    public void setOnCurrentSelectedGeoItemChanges(@NotNull Consumer<@Nullable GeoItem> onCurrentSelectedGeoItemChanges) {
        this.onCurrentSelectedGeoItemChanges = onCurrentSelectedGeoItemChanges;
    }

    @Nullable
    public GeoItem getSelectedGeoItem() {
        return selectedGeoItem;
    }

    public void setSelectedGeoItem(@Nullable GeoItem selectedGeoItem) {
        this.selectedGeoItem = selectedGeoItem;
        final AbstractGeoItemSelectorView<?> selector;
        if (selectedGeoItem == null) {
            selector = new RegioneSelectorView(getContext(), anagrafiche);
            selector.setOnGeoItemSelected(this::setSelectedGeoItem);
        } else if (selectedGeoItem instanceof Regione) {
            selector = new ProvinciaSelectorView(getContext(), anagrafiche, (Regione) this.selectedGeoItem);
            selector.setOnGeoItemSelected(this::setSelectedGeoItem);
        } else if (selectedGeoItem instanceof Provincia) {
            selector = new ComuneSelectorView(getContext(), anagrafiche, (Provincia) this.selectedGeoItem);
            selector.setOnGeoItemSelected(this::setSelectedGeoItem);
        } else if (selectedGeoItem instanceof Comune) {
            throw new UnsupportedOperationException();
        } else {
            throw new IllegalStateException(selectedGeoItem.getClass() + " given");
        }

        addView(selector, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        if (onCurrentSelectedGeoItemChanges != null) {
            onCurrentSelectedGeoItemChanges.consume(selectedGeoItem);
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
