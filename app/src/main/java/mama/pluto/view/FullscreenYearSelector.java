package mama.pluto.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

import mama.pluto.R;
import mama.pluto.utils.BaseViewHolder;
import mama.pluto.utils.ColorUtils;
import mama.pluto.utils.Consumer;
import mama.pluto.utils.MetricsUtils;

public class FullscreenYearSelector extends FullscreenView {

    private final static int MIN_YEAR = 2015;
    @NotNull
    private final RecyclerView recyclerView;
    @NotNull
    private final Consumer<@NotNull Integer> onYearSelected;

    public FullscreenYearSelector(Context context, @NotNull Consumer<@NotNull Integer> onYearSelected) {
        super(context);
        this.onYearSelected = onYearSelected;
        int dp32 = MetricsUtils.dpToPixel(context, 32);
        setBackgroundColor(ColorUtils.getColor(context, R.color.colorPrimaryDark));
        logo.setImageResource(R.drawable.ic_euro_sign_240dp);

        content.setPadding(dp32, dp32, dp32, dp32);


        TextView messageView = new TextView(context, null, android.R.attr.textAppearanceMediumInverse);
        messageView.setGravity(Gravity.CENTER_HORIZONTAL);
        messageView.setTextColor(Color.WHITE);
        messageView.setText(R.string.which_year_to_download);
        content.addView(messageView);

        recyclerView = new RecyclerView(context);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new YearAdapter());
        content.addView(recyclerView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    private class YearAdapter extends RecyclerView.Adapter {
        private final int maxYear;

        private YearAdapter() {
            maxYear = Calendar.getInstance().get(Calendar.YEAR);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            SingleLineListItem singleLineListItem = new SingleLineListItem(getContext());
            singleLineListItem.setTextColor(Color.WHITE);
            BaseViewHolder<SingleLineListItem> ret = new BaseViewHolder<>(singleLineListItem);
            ret.setFullWidth(recyclerView);
            return ret;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final int year = position + MIN_YEAR;
            if (year == maxYear) {
                ((SingleLineListItem) holder.itemView).setText(getResources().getString(R.string.year_current_warning, year));
            } else {
                ((SingleLineListItem) holder.itemView).setText(String.valueOf(year));
            }
            holder.itemView.setOnClickListener(view -> onYearSelected.consume(year));
        }

        @Override
        public int getItemCount() {
            return maxYear - MIN_YEAR + 1;
        }
    }
}
