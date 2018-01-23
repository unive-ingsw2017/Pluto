package mama.pluto.view.selectors;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.GeoItem;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import mama.pluto.R;
import mama.pluto.utils.AbstractEnteSelectorAdapter;
import mama.pluto.utils.Consumer;
import mama.pluto.utils.MetricsUtils;

/**
 * Created by MMarco on 16/11/2017.
 */
public abstract class AbstractEnteSelectorView<G extends GeoItem> extends RecyclerView {
    @NotNull
    private final AbstractEnteSelectorAdapter adapter;
    @NotNull
    private final Anagrafiche anagrafiche;
    @Nullable
    private final G mainGeoItem;

    public AbstractEnteSelectorView(@NotNull Context context, @NonNull Anagrafiche anagrafiche, @Nullable G mainGeoItem) {
        super(context);
        this.anagrafiche = anagrafiche;
        this.mainGeoItem = mainGeoItem;
        final int dp8 = MetricsUtils.dpToPixel(getContext(), 8);
        setPadding(0, dp8, 0, dp8);
        setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider));
        addItemDecoration(dividerItemDecoration);
        setClipToPadding(false);
        setBackgroundColor(Color.WHITE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation(MetricsUtils.dpToPixel(getContext(), 4f));//TODO: spostare
        }

        adapter = createAdapter();
        setAdapter(adapter);
        if (mainGeoItem != null) {
            adapter.setMainGeoItem(mainGeoItem);
        }
    }

    @NonNull
    public Anagrafiche getAnagrafiche() {
        return anagrafiche;
    }

    @Nullable
    public G getMainGeoItem() {
        return mainGeoItem;
    }

    public void setOnGeoItemSelected(@Nullable Consumer<GeoItem> onGeoItemSelected) {
        adapter.setOnGeoItemSelected(onGeoItemSelected);
    }

    protected abstract AbstractEnteSelectorAdapter createAdapter();
}
