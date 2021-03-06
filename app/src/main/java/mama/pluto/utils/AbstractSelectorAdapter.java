package mama.pluto.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.GeoItem;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import mama.pluto.dataAbstraction.AnagraficheExtended;
import mama.pluto.dataAbstraction.DataUtils;
import mama.pluto.view.SubHeaderView;

/**
 * Created by MMarco on 16/11/2017.
 */

public abstract class AbstractSelectorAdapter<V extends View, T> extends RecyclerView.Adapter<BaseViewHolder> {

    private final static int VIEW_TYPE_HEADER = 0;
    private final static int VIEW_TYPE_DIVIDER = 1;
    private final static int VIEW_TYPE_ITEM = 2;
    @NotNull
    private final AnagraficheExtended anagrafiche;

    @Nullable
    private RecyclerView recyclerView;
    @Nullable
    private Consumer<? super T> onItemSelected;
    @Nullable
    private Consumer<Ente> onEnteSelected;
    @Nullable
    private GeoItem mainGeoItem = null;
    @Nullable
    private HeaderCreator<?> headerCreator;

    protected AbstractSelectorAdapter(@NonNull AnagraficheExtended anagrafiche) {
        this.anagrafiche = anagrafiche;
    }

    public void setHeaderCreator(HeaderCreator<?> headerCreator) {
        this.headerCreator = headerCreator;
        notifyDataSetChanged();
    }

    @Nullable
    public HeaderCreator<?> getHeaderCreator() {
        return headerCreator;
    }

    @NonNull
    public AnagraficheExtended getAnagrafiche() {
        return anagrafiche;
    }

    public void setMainGeoItem(@Nullable GeoItem mainGeoItem) {
        this.mainGeoItem = mainGeoItem;
        notifyDataSetChanged();
    }


    @NotNull
    private Context getContext() {
        ensureRecyclerView();
        assert recyclerView != null;
        return recyclerView.getContext();
    }

    protected abstract V createView(@NotNull Context context);

    protected TextView createDivider(@NotNull Context context) {
        return new SubHeaderView(context);
    }

    @Override
    public final int getItemCount() {
        int ret = (headerCreator != null ? 1 : 0);
        String previousDivider = null;
        for (int i = 0; i < getItemCount2(); i++) {
            ret++;
            String divider = getDivider(i);
            if (!ObjectsUtils.equals(previousDivider, divider)) {
                ret++;
            }
            previousDivider = divider;
        }
        return ret;
    }

    protected abstract int getItemCount2();//TODO: trovare nome meglio

    private void ensureRecyclerView() {
        if (recyclerView == null) {
            throw new IllegalStateException();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (headerCreator != null) {
            position--;
        }
        if (position == -1) {
            return VIEW_TYPE_HEADER;
        }

        String previousDivider = null;
        for (int i = 0, totalPosition = 0; totalPosition <= position; i++, totalPosition++) {
            String divider = getDivider(i);
            if (!ObjectsUtils.equals(previousDivider, divider)) {
                if (position == totalPosition++) {
                    return VIEW_TYPE_DIVIDER;
                }
            }
            previousDivider = divider;
        }
        return VIEW_TYPE_ITEM;
    }

    public void setOnItemSelected(@Nullable Consumer<? super T> onItemSelected) {
        this.onItemSelected = onItemSelected;
    }

    public void setOnEnteSelected(@Nullable Consumer<Ente> onEnteSelected) {
        this.onEnteSelected = onEnteSelected;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view;
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                view = createHeaderView(getContext());
                break;
            case VIEW_TYPE_DIVIDER:
                view = createDivider(getContext());
                break;
            case VIEW_TYPE_ITEM:
                view = createView(getContext());
                break;
            default:
                throw new IllegalStateException();
        }
        final BaseViewHolder ret = new BaseViewHolder<>(view);
        assert recyclerView != null;
        ret.setFullWidth(recyclerView);
        return ret;
    }

    protected View createHeaderView(@NotNull Context context) {
        assert headerCreator != null;
        return headerCreator.createView(context, this::onEnteSelected);
    }

    protected void onEnteSelected(@NotNull Ente e) {
        if (onEnteSelected != null) {
            onEnteSelected.consume(e);
        }
    }

    protected void bindHeaderView(@NotNull View mainEnteView, @Nullable GeoItem mainGeoItem) {
        Ente ente = mainGeoItem == null ? null : DataUtils.getEnteOfGeoItem(anagrafiche, mainGeoItem);
        assert headerCreator != null;
        //noinspection unchecked
        ((HeaderCreator) headerCreator).bind(mainEnteView, anagrafiche, ente, mainGeoItem);
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
        if (holder.getItemViewType() == VIEW_TYPE_HEADER) {
            bindHeaderView(holder.getView(), mainGeoItem);
        }
        if (headerCreator != null) {
            position--;
        }
        String previousDivider = null;
        for (int i = 0; i <= position; i++) {
            String divider = getDivider(i);
            if (!ObjectsUtils.equals(previousDivider, divider)) {
                position--;
            }
            previousDivider = divider;
        }
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_ITEM:
                final T item = getItem(position);
                //noinspection unchecked
                bindView(((V) holder.getView()), item);
                holder.getView().setOnClickListener(v -> {
                    onItemSelected(item);
                });
                break;
            case VIEW_TYPE_DIVIDER:
                assert previousDivider != null;
                ((TextView) holder.getView()).setText(StringUtils.toNormalCase(previousDivider));
                break;
        }
    }

    protected void onItemSelected(T item) {
        if (onItemSelected != null) {
            onItemSelected.consume(item);
        }
    }

    @Nullable
    protected String getDivider(int position) {
        return null;
    }

    protected abstract T getItem(int position);

    protected abstract void bindView(V view, T item);

    public interface HeaderCreator<V extends View> {
        V createView(@NotNull Context context, @NotNull Consumer<Ente> onEnteSelected);

        void bind(@NotNull V view, @NotNull AnagraficheExtended anagrafiche, @Nullable Ente ente, @Nullable GeoItem geoItem);
    }
}
