package mama.pluto.view.selectors;

import android.content.Context;
import android.support.annotation.NonNull;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.GeoItem;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import mama.pluto.utils.AbstractEnteSelectorAdapter;
import mama.pluto.utils.Consumer;

/**
 * Created by MMarco on 16/11/2017.
 */
public abstract class AbstractEnteSelectorView<G extends GeoItem> extends AbstractSelectorView {
    @NotNull
    protected final AbstractEnteSelectorAdapter adapter;
    @Nullable
    private final G mainGeoItem;

    public AbstractEnteSelectorView(@NotNull Context context, @NonNull Anagrafiche anagrafiche, @Nullable G mainGeoItem) {
        super(context, anagrafiche);
        this.mainGeoItem = mainGeoItem;

        adapter = createAdapter();
        setAdapter(adapter);
        if (mainGeoItem != null) {
            adapter.setMainGeoItem(mainGeoItem);
        }
    }

    @Nullable
    public G getMainGeoItem() {
        return mainGeoItem;
    }

    public void setOnEnteSelected(@Nullable Consumer<Ente> onGeoItemSelected) {
        adapter.setOnItemSelected(onGeoItemSelected);
    }

    protected abstract AbstractEnteSelectorAdapter createAdapter();
}
