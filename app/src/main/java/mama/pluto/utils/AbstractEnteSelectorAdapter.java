package mama.pluto.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import mama.pluto.R;

/**
 * Created by MMarco on 16/11/2017.
 */

public abstract class AbstractEnteSelectorAdapter extends RecyclerView.Adapter<BaseViewHolder<TextView>> {
    @Nullable
    private RecyclerView recyclerView;
    private Consumer<String> onEnteSelected;

    protected TextView instantiateView() {
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

    private void ensureRecyclerView() {
        if (recyclerView == null) {
            throw new IllegalStateException();
        }
    }

    public void setOnEnteSelected(Consumer<String> onEnteSelected) {
        this.onEnteSelected = onEnteSelected;
    }

    @Override
    public BaseViewHolder<TextView> onCreateViewHolder(ViewGroup parent, int viewType) {
        final BaseViewHolder<TextView> ret = new BaseViewHolder<>(instantiateView());
        assert recyclerView != null;
        ret.setFullWidth(recyclerView);
        return ret;
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
    public void onBindViewHolder(BaseViewHolder<TextView> holder, int position) {
        final String ente = getEnte(position);
        holder.getView().setText(ente);
        holder.getView().setOnClickListener(v -> {
            if (onEnteSelected != null) {
                onEnteSelected.consume(ente);
            }
        });
    }

    protected abstract String getEnte(int position);

}
