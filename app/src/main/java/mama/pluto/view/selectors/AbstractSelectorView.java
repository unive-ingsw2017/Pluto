package mama.pluto.view.selectors;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.GeoItem;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import mama.pluto.R;
import mama.pluto.dataAbstraction.AnagraficheExtended;
import mama.pluto.utils.AbstractSelectorAdapter;
import mama.pluto.utils.MetricsUtils;
import mama.pluto.view.BaseRecyclerView;

/**
 * Created by MMarco on 16/11/2017.
 */
public abstract class AbstractSelectorView<G extends GeoItem> extends BaseRecyclerView {
    @Nullable
    private final G mainGeoItem;

    @NotNull
    protected final AnagraficheExtended anagrafiche;
    private final AbstractSelectorAdapter<?, ?> adapter;

    public AbstractSelectorView(@NotNull Context context, @Nullable G mainGeoItem, @NonNull AnagraficheExtended anagrafiche) {
        super(context);
        this.mainGeoItem = mainGeoItem;
        this.anagrafiche = anagrafiche;
        adapter = createAdapter();
        adapter.setMainGeoItem(mainGeoItem);
        setAdapter(adapter);
    }

    @Nullable
    public G getMainGeoItem() {
        return mainGeoItem;
    }

    public void setHeaderCreator(AbstractSelectorAdapter.HeaderCreator<?> headerCreator) {
        getAdapter().setHeaderCreator(headerCreator);
    }

    @Nullable
    public AbstractSelectorAdapter.HeaderCreator<?> getHeaderCreator() {
        return getAdapter().getHeaderCreator();
    }

    @Override
    public AbstractSelectorAdapter<?, ?> getAdapter() {
        return adapter;
    }

    @NonNull
    public AnagraficheExtended getAnagrafiche() {
        return anagrafiche;
    }

    protected abstract AbstractSelectorAdapter<?, ?> createAdapter();
}
