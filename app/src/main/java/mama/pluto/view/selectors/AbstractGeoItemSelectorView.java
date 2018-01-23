package mama.pluto.view.selectors;

import android.content.Context;
import android.support.annotation.NonNull;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.GeoItem;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import mama.pluto.utils.AbstractSelectorAdapter;
import mama.pluto.utils.Consumer;

/**
 * Created by MMarco on 16/11/2017.
 */
public abstract class AbstractGeoItemSelectorView<P extends GeoItem, G extends GeoItem> extends AbstractSelectorView {
    @NotNull
    protected final AbstractSelectorAdapter<?, ? extends G> adapter;
    @Nullable
    private final P mainGeoItem;

    public AbstractGeoItemSelectorView(@NotNull Context context, @NonNull Anagrafiche anagrafiche, @Nullable P mainGeoItem) {
        super(context, anagrafiche);
        this.mainGeoItem = mainGeoItem;

        adapter = createAdapter();
        setAdapter(adapter);
        if (mainGeoItem != null) {
            adapter.setMainGeoItem(mainGeoItem);
        }
    }

    @Nullable
    public P getMainGeoItem() {
        return mainGeoItem;
    }

    public void setOnGeoItemSelected(@Nullable Consumer<G> onGeoItemSelected) {
        adapter.setOnItemSelected(onGeoItemSelected);
    }

    protected abstract AbstractSelectorAdapter<?, ? extends G> createAdapter();
}
