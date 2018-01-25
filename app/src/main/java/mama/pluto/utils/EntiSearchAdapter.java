package mama.pluto.utils;

import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import mama.pluto.R;
import mama.pluto.dataAbstraction.AnagraficheImproved;
import mama.pluto.dataAbstraction.DataUtils;
import mama.pluto.view.DoubleLineListItem;

public class EntiSearchAdapter extends RecyclerView.Adapter<BaseViewHolder<View>> {

    private final static int VIEW_TYPE_ENTE = 1;
    private final static int VIEW_TYPE_EMPTY = 2;

    private final AnagraficheImproved anagrafiche;
    private RecyclerView recyclerView;
    private String searchQuery;
    private List<Ente> searchResults;
    private Consumer<Ente> onEnteSelected;

    public EntiSearchAdapter(AnagraficheImproved anagrafiche) {
        this.anagrafiche = anagrafiche;
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

    public void setOnEnteSelected(Consumer<Ente> onEnteSelected) {
        this.onEnteSelected = onEnteSelected;
    }

    @NotNull
    private RecyclerView ensureRecyclerView() {
        if (recyclerView == null) {
            throw new IllegalStateException();
        }
        return recyclerView;
    }

    public void setSearchQuery(@Nullable String searchQuery) {
        if (!ObjectsUtils.equals(this.searchQuery, searchQuery)) {
            this.searchQuery = searchQuery;
            if (searchQuery == null || searchQuery.isEmpty()) {
                this.searchResults = null;
            } else {
                this.searchResults = DataUtils.searchEnte(anagrafiche, searchQuery);
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public BaseViewHolder<View> onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v;
        switch (viewType) {
            case VIEW_TYPE_EMPTY:
                TextView tv = new TextView(ensureRecyclerView().getContext(), null, android.R.attr.textAppearanceMedium);
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
                tv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_sentiment_dissatisfied_grey_240dp, 0, 0);
                v = tv;
                break;
            case VIEW_TYPE_ENTE:
                v = new DoubleLineListItem(ensureRecyclerView().getContext());
                break;
            default:
                throw new IllegalStateException();
        }
        BaseViewHolder<View> ret = new BaseViewHolder<>(v);
        ret.setFullWidth(recyclerView);
        return ret;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<View> holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_EMPTY:
                ((TextView) holder.itemView).setText(ensureRecyclerView().getResources().getString(R.string.no_enti_match_search_query, searchQuery));
                break;
            case VIEW_TYPE_ENTE:
                Ente ente = searchResults.get(position);
                ((DoubleLineListItem) holder.itemView).setText(
                        StringUtils.toNormalCase(ente.getNome()),
                        StringUtils.toNormalCase(ente.getSottocomparto().getNome()) + " (" + StringUtils.toNormalCase(ente.getSottocomparto().getComparto().getNome()) + ")"
                );
                holder.itemView.setOnClickListener(view -> {
                    if (onEnteSelected != null) {
                        onEnteSelected.consume(ente);
                    }
                });
                break;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public int getItemCount() {
        return this.searchResults == null ? 0 : Math.max(1, this.searchResults.size());
    }

    @Override
    public int getItemViewType(int position) {
        if (this.searchResults.isEmpty()) {
            return VIEW_TYPE_EMPTY;
        } else {
            return VIEW_TYPE_ENTE;
        }
    }
}
