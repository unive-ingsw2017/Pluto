package mama.pluto.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;

import org.jetbrains.annotations.NotNull;

import mama.pluto.R;
import mama.pluto.dataAbstraction.AnagraficheExtended;
import mama.pluto.dataAbstraction.EntiComparator;
import mama.pluto.utils.BaseViewHolder;
import mama.pluto.utils.MetricsUtils;
import mama.pluto.utils.StringUtils;

public class EntiComparisonView extends RecyclerView {
    @NotNull
    private final AnagraficheExtended anagrafiche;
    private Ente first, second;
    private EntiComparator entiComparator;

    public EntiComparisonView(Context context, @NotNull AnagraficheExtended anagrafiche) {
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
                    return BaseViewHolder.getFullInstance(EntiComparisonView.this, new HeaderView(getContext()));
                case VIEW_TYPE_BALANCE_COMPARISON:
                    return BaseViewHolder.getFullInstance(EntiComparisonView.this, new ComparisonCardView(getContext(), anagrafiche));
                default:
                    throw new IllegalStateException();
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (position == 0) {
                ((HeaderView) holder.itemView).setText(StringUtils.toNormalCase(first.getNome()), StringUtils.toNormalCase(second.getNome()));
            } else {
                ((ComparisonCardView) holder.itemView).setCategory(entiComparator.getSortedCategories().get(position - 1));
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

    private final class HeaderView extends LinearLayout {
        private final TextView first, second;

        public HeaderView(Context context) {
            super(context);
            int dp2 = MetricsUtils.dpToPixel(getContext(), 2);
            int dp8 = MetricsUtils.dpToPixel(getContext(), 8);
            setPadding(dp8, dp8, dp8, dp8);
            setGravity(Gravity.CENTER_VERTICAL);
            setBackgroundColor(0x10000000);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setElevation(MetricsUtils.dpToPixel(context, 4));
            }

            first = createTextView();
            //first.setGravity(Gravity.END);
            addView(first, new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f));

            TextView vs = new TextView(getContext(), null, android.R.attr.textAppearanceSmall);
            vs.setPadding(dp2, dp2, dp2, dp2);
            vs.setText(R.string.vs);
            addView(vs);

            second = createTextView();
            //second.setGravity(Gravity.START);
            addView(second, new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f));

            first.setOnClickListener(v -> new EnteSelectorDialog(getContext(), anagrafiche, ente -> setEnti(ente, EntiComparisonView.this.second)).show());
            second.setOnClickListener(v -> new EnteSelectorDialog(getContext(), anagrafiche, ente -> setEnti(EntiComparisonView.this.first, ente)).show());
        }

        @NonNull
        private TextView createTextView() {
            TextView ret = new TextView(getContext(), null, android.R.attr.textAppearanceMedium);
            ret.setTextColor(Color.BLACK);
            ret.setGravity(Gravity.CENTER);
            return ret;
        }

        public void setText(String first, String second) {
            this.first.setText(first);
            this.second.setText(second);
        }
    }
}

