package mama.pluto.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.GeoItem;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import mama.pluto.R;

/**
 * Created by MMarco on 16/11/2017.
 */

public abstract class AbstractEnteSelectorAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private final static int VIEW_TYPE_HEADER = 0;
    private final static int VIEW_TYPE_ENTE = 1;
    @Nullable
    private RecyclerView recyclerView;
    @Nullable
    private Consumer<GeoItem> onGeoItemSelected;
    @Nullable
    private GeoItem mainGeoItem = null;

    protected TextView createView() {
        ensureRecyclerView();
        assert recyclerView != null;
        final Context context = recyclerView.getContext();
        TextView ret = new TextView(context);
        ret.setMinHeight(MetricsUtils.dpToPixel(context, 48));
        final int dp8 = MetricsUtils.dpToPixel(context, 8);
        final int dp16 = MetricsUtils.dpToPixel(context, 16);
        ret.setPadding(dp16, dp8, dp16, dp8);
        ret.setGravity(Gravity.CENTER_VERTICAL);
        ret.setTextColor(Color.BLACK);
        ret.setBackgroundResource(R.drawable.item_background);
        return ret;
    }

    public void setMainGeoItem(@Nullable GeoItem mainGeoItem) {
        this.mainGeoItem = mainGeoItem;
        notifyDataSetChanged();
    }

    @Override
    public final int getItemCount() {
        return getEntiCount() + (mainGeoItem == null ? 0 : 1);
    }

    protected abstract int getEntiCount();

    private void ensureRecyclerView() {
        if (recyclerView == null) {
            throw new IllegalStateException();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 && mainGeoItem != null ? VIEW_TYPE_HEADER : VIEW_TYPE_ENTE;
    }

    public void setOnGeoItemSelected(@Nullable Consumer<GeoItem> onGeoItemSelected) {
        this.onGeoItemSelected = onGeoItemSelected;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view;
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                view = createMainEnteView();
                break;
            case VIEW_TYPE_ENTE:
                view = createView();
                break;
            default:
                throw new IllegalStateException();
        }
        final BaseViewHolder ret = new BaseViewHolder<>(view);
        assert recyclerView != null;
        ret.setFullWidth(recyclerView);
        return ret;
    }

    protected View createMainEnteView() {
        ensureRecyclerView();
        assert recyclerView != null;
        return new TextView(recyclerView.getContext());
    }

    protected void bindMainEnteView(@NotNull View mainEnteView, @NotNull GeoItem mainGeoItem) {
        ((TextView) mainEnteView).setText("Hoo wyyy, what do we have here, " + mainGeoItem.getNome() + "?");
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_HEADER:
                bindMainEnteView(holder.getView(), mainGeoItem);
                break;
            case VIEW_TYPE_ENTE:
                final int entePosition = mainGeoItem != null ? position - 1 : position;
                final GeoItem geoItem = getGeoItem(entePosition);
                ((TextView) holder.getView()).setText(geoItem.getNome());
                holder.getView().setOnClickListener(v -> {
                    if (onGeoItemSelected != null) {
                        onGeoItemSelected.consume(geoItem);
                    }
                });
                break;
            default:
                throw new IllegalStateException();
        }
    }

    protected abstract GeoItem getGeoItem(int position);

}
