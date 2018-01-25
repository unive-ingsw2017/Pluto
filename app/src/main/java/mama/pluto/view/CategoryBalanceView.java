package mama.pluto.view;

import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import mama.pluto.dataAbstraction.Category;
import mama.pluto.utils.MetricsUtils;

public class CategoryBalanceView extends LinearLayout {

    private final TextView categoryName;
    private final TextView in;
    private final TextView out;
    private final TextView total;

    public CategoryBalanceView(Context context) {
        super(context);
        setGravity(Gravity.CENTER_VERTICAL);

        int dp8 = MetricsUtils.dpToPixel(context, 8);
        setPadding(dp8, dp8, dp8, dp8);

        categoryName = new TextView(context);
        categoryName.setPadding(0, 0, 0, dp8);
        addView(categoryName);

        in = new TextView(context);
        in.setTextColor(BalanceRowView.POSITIVE_COLOR);
        addView(in);

        out = new TextView(context);
        out.setTextColor(BalanceRowView.NEGATIVE_COLOR);
        addView(out);

        TextView equals = new TextView(context);
        equals.setText("=");
        addView(equals);

        total = new TextView(context);
        addView(total);
    }

    public void setCategory(Category category, Long in, Long out) {
        categoryName.setText(category.getName());
        long total = 0;

        if (in != null) {
            total += in;
            this.in.setVisibility(VISIBLE);
            this.in.setText(BalanceRowView.formatEuroCentString(in, true, false));
        } else {
            this.in.setVisibility(GONE);
        }


        if (out != null) {
            total -= out;
            this.out.setVisibility(VISIBLE);
            this.out.setText("-" + BalanceRowView.formatEuroCentString(out, true, false));
        } else {
            this.out.setVisibility(GONE);
        }

        this.total.setText(BalanceRowView.formatEuroCentString(total, true, false));
        this.total.setTextColor(total >= 0 ? BalanceRowView.POSITIVE_COLOR : BalanceRowView.NEGATIVE_COLOR);
    }
}
