package mama.pluto.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * Created by MMarco on 16/11/2017.
 */

public class BaseViewHolder<V extends View> extends RecyclerView.ViewHolder {
    public BaseViewHolder(V itemView) {
        super(itemView);
    }

    public V getView() {
        //noinspection unchecked
        return (V) itemView;
    }

    public void setFullWidth(@NonNull RecyclerView recyclerView) {
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (itemView.getLayoutParams() == null) {
            itemView.setLayoutParams(layoutManager.generateDefaultLayoutParams());
        }
        if (layoutManager instanceof StaggeredGridLayoutManager) {
            ((StaggeredGridLayoutManager.LayoutParams) itemView.getLayoutParams()).setFullSpan(true);
        } else if (layoutManager instanceof GridLayoutManager) {
            throw new UnsupportedOperationException("Use gridLayoutManager.setSpanSizeLookup to set the full width");
        }
        itemView.getLayoutParams().width = RecyclerView.LayoutParams.MATCH_PARENT;

    }
}
