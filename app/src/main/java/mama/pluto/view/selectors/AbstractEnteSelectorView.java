package mama.pluto.view.selectors;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import mama.pluto.Ente;
import mama.pluto.R;
import mama.pluto.utils.AbstractEnteSelectorAdapter;
import mama.pluto.utils.Consumer;
import mama.pluto.utils.MetricsUtils;

/**
 * Created by MMarco on 16/11/2017.
 */
public abstract class AbstractEnteSelectorView extends RecyclerView {
    @NotNull
    private final AbstractEnteSelectorAdapter adapter;
    @NotNull
    private final Anagrafiche anagrafiche;
    @Nullable
    private final Ente mainEnte;

    public AbstractEnteSelectorView(@NotNull Context context, @NonNull Anagrafiche anagrafiche, @Nullable Ente mainEnte) {
        super(context);
        this.anagrafiche = anagrafiche;
        this.mainEnte = mainEnte;
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
        if (mainEnte != null) {
            adapter.setMainEnte(mainEnte);
        }
    }

    @NonNull
    public Anagrafiche getAnagrafiche() {
        return anagrafiche;
    }

    @Nullable
    public Ente getMainEnte() {
        return mainEnte;
    }

    public void setOnEnteSelected(Consumer<Ente> onEnteSelected) {
        adapter.setOnEnteSelected(onEnteSelected);
    }

    protected abstract AbstractEnteSelectorAdapter createAdapter();
}
