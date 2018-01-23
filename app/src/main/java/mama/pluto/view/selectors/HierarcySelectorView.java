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

public class HierarcySelectorView extends FrameLayout {

    @NotNull
    private final Anagrafiche anagrafiche;
    @SuppressWarnings("ConstantConditions")
    @NotNull
    private AbstractEnteSelectorView currentSelector = null;
    private @Nullable Consumer<@Nullable GeoItem> onCurrentSelectedGeoItemChanges;
    private GeoItem currentSelectedGeoItem = null;

    public HierarcySelectorView(Context context, @NotNull Anagrafiche anagrafiche) {
        super(context);
        this.anagrafiche = anagrafiche;
        setCurrentSelectedGeoItem(null);
    }

    public void setOnCurrentSelectedGeoItemChanges(@NotNull Consumer<@Nullable GeoItem> onCurrentSelectedGeoItemChanges) {
        this.onCurrentSelectedGeoItemChanges = onCurrentSelectedGeoItemChanges;
    }

    public void setCurrentSelectedGeoItem(@Nullable GeoItem currentSelectedGeoItem) {
        this.currentSelectedGeoItem = currentSelectedGeoItem;
        final AbstractEnteSelectorView<?> selector;
        if (currentSelectedGeoItem == null) {
            selector = new RegioneSelectorView(getContext(), anagrafiche);
            selector.setOnGeoItemSelected(this::setCurrentSelectedGeoItem);
        } else if (currentSelectedGeoItem instanceof Regione) {
            selector = new ProvinciaSelectorView(getContext(), anagrafiche, (Regione) this.currentSelectedGeoItem);
            selector.setOnGeoItemSelected(this::setCurrentSelectedGeoItem);
        } else if (currentSelectedGeoItem instanceof Provincia) {
            selector = new ComuneSelectorView(getContext(), anagrafiche, (Provincia) this.currentSelectedGeoItem);
            selector.setOnGeoItemSelected(this::setCurrentSelectedGeoItem);
        } else if (currentSelectedGeoItem instanceof Comune) {
            throw new UnsupportedOperationException();
        } else {
            throw new IllegalStateException(currentSelectedGeoItem.getClass() + " given");
        }

        addView(selector, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        if (onCurrentSelectedGeoItemChanges != null) {
            onCurrentSelectedGeoItemChanges.consume(currentSelectedGeoItem);
        }
    }

    public boolean onBackPressed() {
        if (currentSelectedGeoItem == null) {
            return false;
        } else {
            GeoItem parent = this.currentSelectedGeoItem.getParent();
            if (parent instanceof RipartizioneGeografica) {
                parent = null;
            }
            setCurrentSelectedGeoItem(parent);
            return true;
        }
    }
}
