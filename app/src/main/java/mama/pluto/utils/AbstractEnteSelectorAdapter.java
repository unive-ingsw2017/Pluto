package mama.pluto.utils;

import android.content.Context;
import android.graphics.Color;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import mama.pluto.Ente;
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
    private Consumer<Ente> onEnteSelected;
    @Nullable
    private Ente mainEnte = null;

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

    public void setMainEnte(@Nullable Ente mainEnte) {
        this.mainEnte = mainEnte;
        notifyDataSetChanged();
    }

    @Override
    public final int getItemCount() {
        return getEntiCount() + (mainEnte == null ? 0 : 1);
    }

    protected abstract int getEntiCount();

    private void ensureRecyclerView() {
        if (recyclerView == null) {
            throw new IllegalStateException();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 && mainEnte != null ? VIEW_TYPE_HEADER : VIEW_TYPE_ENTE;
    }

    public void setOnEnteSelected(@Nullable Consumer<Ente> onEnteSelected) {
        this.onEnteSelected = onEnteSelected;
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

    protected void bindMainEnteView(@NotNull View mainEnteView, @NotNull Ente mainEnte) {
        ((TextView) mainEnteView).setText("Hoo wyyy, what do we have here, " + mainEnte.getName() + "?");
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
                bindMainEnteView(holder.getView(), mainEnte);
                break;
            case VIEW_TYPE_ENTE:
                final int entePosition = mainEnte != null ? position - 1 : position;
                final Ente ente = getEnte(entePosition);
                ((TextView) holder.getView()).setText(ente.getName());
                holder.getView().setOnClickListener(v -> {
                    if (onEnteSelected != null) {
                        onEnteSelected.consume(ente);
                    }
                });
                break;
            default:
                throw new IllegalStateException();
        }
    }

    protected abstract Ente getEnte(int position);

}
