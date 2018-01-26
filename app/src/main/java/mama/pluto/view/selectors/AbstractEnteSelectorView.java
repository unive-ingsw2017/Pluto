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
public abstract class AbstractEnteSelectorView<G extends GeoItem> extends AbstractSelectorView<G> {

    public AbstractEnteSelectorView(@NotNull Context context, @NonNull AnagraficheExtended anagrafiche, @Nullable G mainGeoItem) {
        super(context, mainGeoItem, anagrafiche);
    }


    public void setHeaderCreator(AbstractSelectorAdapter.HeaderCreator<?> headerCreator) {
        getAdapter().setHeaderCreator(headerCreator);
    }

    public void setOnEnteSelected(@Nullable Consumer<Ente> onGeoItemSelected) {
        getAdapter().setOnItemSelected(onGeoItemSelected);
        getAdapter().setOnEnteSelected(onGeoItemSelected);
    }

    @Override
    public AbstractSelectorAdapter<?, ? extends Ente> getAdapter() {
        //noinspection unchecked
        return (AbstractSelectorAdapter<?, ? extends Ente>) super.getAdapter();
    }

    @Override
    protected abstract AbstractSelectorAdapter<?, ? extends Ente> createAdapter();
}
