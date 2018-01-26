package mama.pluto.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;

import org.jetbrains.annotations.NotNull;

import mama.pluto.R;
import mama.pluto.dataAbstraction.AnagraficheExtended;
import mama.pluto.dataAbstraction.EntiComparator;
import mama.pluto.utils.BaseViewHolder;
import mama.pluto.utils.MetricsUtils;
import mama.pluto.utils.StringUtils;

public class EnteComparatorView extends RecyclerView {
    @NotNull
    private final AnagraficheExtended anagrafiche;
    private Ente first, second;
    private EntiComparator entiComparator;

    public EnteComparatorView(Context context, @NotNull AnagraficheExtended anagrafiche) {
        super(context);
        this.anagrafiche = anagrafiche;
        setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        setAdapter(new MyAdapter());
    }

    public void setEnti(Ente a, Ente b) {
        first = a;
        second = b;
        entiComparator = EntiComparator.getInstance(getContext(), anagrafiche, a, b);
        getAdapter().notifyDataSetChanged();
    }

    private class MyAdapter extends Adapter {
        private final static int VIEW_TYPE_HEADER = 0;
        private final static int VIEW_TYPE_BALANCE_COMPARISON = 1;

        @Override
        public int getItemViewType(int position) {
            return position == 0 ? VIEW_TYPE_HEADER : VIEW_TYPE_BALANCE_COMPARISON;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case VIEW_TYPE_HEADER:
                    TextView tv = new TextView(getContext(), null, android.R.attr.textAppearanceLarge);
                    int dp16 = MetricsUtils.dpToPixel(getContext(), 16);
                    tv.setPadding(dp16, dp16, dp16, dp16);
                    tv.setGravity(Gravity.CENTER);
                    return BaseViewHolder.getFullInstance(EnteComparatorView.this, tv);
                case VIEW_TYPE_BALANCE_COMPARISON:
                    TextView balanceComparator = new TextView(getContext(), null, android.R.attr.textAppearanceLarge);
                    return BaseViewHolder.getFullInstance(EnteComparatorView.this, balanceComparator);
                default:
                    throw new IllegalStateException();
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (position == 0) {
                ((TextView) holder.itemView).setText(getContext().getString(R.string.x_vs_y, StringUtils.toNormalCase(first.getNome()), StringUtils.toNormalCase(second.getNome())));
            } else {
                ((TextView) holder.itemView).setText(entiComparator.getSortedCategories().get(position - 1).getCategory().getName());
            }
        }

        @Override
        public int getItemCount() {
            if (entiComparator == null || first == null || second == null) {
                return 0;
            } else {
                return 1 + entiComparator.getSortedCategories().size();
            }
        }
    }
}

