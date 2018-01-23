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

import mama.pluto.R;
import mama.pluto.utils.MetricsUtils;

/**
 * Created by MMarco on 16/11/2017.
 */
public abstract class AbstractSelectorView extends RecyclerView {
    @NotNull
    protected final Anagrafiche anagrafiche;

    public AbstractSelectorView(@NotNull Context context, @NonNull Anagrafiche anagrafiche) {
        super(context);
        this.anagrafiche = anagrafiche;
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
    }

    @NonNull
    public Anagrafiche getAnagrafiche() {
        return anagrafiche;
    }
}
