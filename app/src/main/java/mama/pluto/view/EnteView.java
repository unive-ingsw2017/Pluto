package mama.pluto.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.GeoItem;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mama.pluto.R;
import mama.pluto.dataAbstraction.AnagraficheExtended;
import mama.pluto.dataAbstraction.Category;
import mama.pluto.dataAbstraction.EnteSummary;
import mama.pluto.utils.BaseViewHolder;
import mama.pluto.utils.Consumer;


public class EnteView extends RecyclerView {
    @NotNull
    private final AnagraficheExtended anagrafiche;
    private final MyAdapter myAdapter;

    private Ente ente;
    private GeoItem geoItem;
    private EnteSummary enteSummary;
    private List<Category> categoriesList;
    private Consumer<Ente> onEnteSelectedForComparison;

    public EnteView(@NotNull Context context, @NonNull AnagraficheExtended anagrafiche) {
        super(context);
        setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        this.anagrafiche = anagrafiche;
        myAdapter = new MyAdapter();
        setAdapter(myAdapter);
    }

    public void setOnEnteSelectedForComparison(Consumer<Ente> onEnteSelectedForComparison) {
        this.onEnteSelectedForComparison = onEnteSelectedForComparison;
    }

    public void setEnte(@NotNull Ente ente, @Nullable GeoItem geoItem) {
        this.ente = ente;
        this.geoItem = geoItem;
        this.enteSummary = EnteSummary.getInstance(getContext(), anagrafiche, ente);

        Set<Category> allCategories = new HashSet<>();
        allCategories.addAll(enteSummary.getEntrateMap().keySet());
        allCategories.addAll(enteSummary.getUsciteMap().keySet());
        categoriesList = new ArrayList<>(allCategories);
        Collections.sort(categoriesList, (c1, c2) -> {
                    Long e1 = enteSummary.getEntrateMap().get(c1);
                    Long u1 = enteSummary.getUsciteMap().get(c1);
                    long tot1 = (e1 == null ? 0 : e1) - (u1 == null ? 0 : u1);

                    Long e2 = enteSummary.getEntrateMap().get(c2);
                    Long u2 = enteSummary.getUsciteMap().get(c2);
                    long tot2 = (e2 == null ? 0 : e2) - (u2 == null ? 0 : u2);
                    return -Long.compare(Math.abs(tot1), Math.abs(tot2));
                }
        );
        myAdapter.notifyDataSetChanged();
    }

    private class MyAdapter extends Adapter {

        public static final int VIEW_TYPE_SUMMARY = 0;
        public static final int VIEW_TYPE_CATEGORY = 1;

        @Override
        public int getItemViewType(int position) {
            return position == 0 ? VIEW_TYPE_SUMMARY : VIEW_TYPE_CATEGORY;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case VIEW_TYPE_SUMMARY:
                    EnteSummaryView sv = new EnteSummaryView(getContext());
                    sv.addExpandButton(getResources().getString(R.string.compare), ente ->
                            new EnteSelectorDialog(getContext(), anagrafiche, onEnteSelectedForComparison)
                                    .setTitle(R.string.choose_ente_to_compare)
                                    .show()
                    );
                    view = sv;
                    break;
                case VIEW_TYPE_CATEGORY:
                    view = new CategoryBalanceView(getContext(), anagrafiche);
                    break;
                default:
                    throw new IllegalStateException();
            }
            BaseViewHolder<View> viewHolder = new BaseViewHolder<>(view);
            viewHolder.setFullWidth(EnteView.this);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (position == 0) {
                ((EnteSummaryView) holder.itemView).setEnte(enteSummary, ente, geoItem);
            } else {
                Category category = categoriesList.get(position - 1);
                ((CategoryBalanceView) holder.itemView).setCategory(enteSummary, category);
            }
        }

        @Override
        public int getItemCount() {
            return ente == null ? 0 : 1 + categoriesList.size();
        }
    }
}
