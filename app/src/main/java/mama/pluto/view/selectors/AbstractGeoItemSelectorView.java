package mama.pluto.view.selectors;

import android.content.Context;
import android.support.annotation.NonNull;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.GeoItem;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import mama.pluto.dataAbstraction.AnagraficheExtended;
import mama.pluto.utils.AbstractSelectorAdapter;
import mama.pluto.utils.Consumer;

/**
 * Created by MMarco on 16/11/2017.
 */
public abstract class AbstractGeoItemSelectorView<P extends GeoItem, G extends GeoItem> extends AbstractSelectorView<P> {

    public AbstractGeoItemSelectorView(@NotNull Context context, @Nullable P mainGeoItem, @NonNull AnagraficheExtended anagrafiche) {
        super(context, mainGeoItem, anagrafiche);
    }

    public void setOnGeoItemSelected(@Nullable Consumer<? super G> onGeoItemSelected) {
        getAdapter().setOnItemSelected(onGeoItemSelected);
    }

    public void setOnEnteSelected(@Nullable Consumer<Ente> onEnteSelected) {
        getAdapter().setOnEnteSelected(onEnteSelected);
    }

    @Override
    public AbstractSelectorAdapter<?, ? extends G> getAdapter() {
        //noinspection unchecked
        return (AbstractSelectorAdapter<?, ? extends G>) super.getAdapter();
    }

    protected abstract AbstractSelectorAdapter<?, ? extends G> createAdapter();
}
