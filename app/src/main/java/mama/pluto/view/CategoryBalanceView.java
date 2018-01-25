package mama.pluto.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import mama.pluto.R;
import mama.pluto.dataAbstraction.Category;
import mama.pluto.utils.EuroFormattingUtils;
import mama.pluto.utils.MetricsUtils;

public class CategoryBalanceView extends CardView {

    private final LinearLayout header;
    private final TextView categoryName;
    private final TextView in;
    private final TextView out;
    private final TextView equals;
    private final TextView total;

    public CategoryBalanceView(Context context) {
        super(context);
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);
        addView(ll);
        setUseCompatPadding(true);
        setRadius(MetricsUtils.dpToPixel(context, 4));

        header = new LinearLayout(context);
        header.setGravity(Gravity.CENTER_VERTICAL);
        ll.addView(header);

        int dp2 = MetricsUtils.dpToPixel(context, 2);
        int dp8 = MetricsUtils.dpToPixel(context, 8);
        int dp16 = MetricsUtils.dpToPixel(context, 16);
        ll.setPadding(dp16, dp16, dp16, dp16);

        categoryName = new TextView(context);
        categoryName.setTextColor(Color.BLACK);
        categoryName.setPadding(0, 0, dp8, 0);
        header.addView(categoryName);

        header.addView(new Space(getContext()), new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f));

        in = new TextView(context);
        in.setTextColor(EuroFormattingUtils.POSITIVE_COLOR);
        in.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_trending_up_green_16dp, 0);
        header.addView(in);

        out = new TextView(context);
        out.setTextColor(EuroFormattingUtils.NEGATIVE_COLOR);
        out.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_trending_down_red_16dp, 0);
        header.addView(out);

        equals = new TextView(context);
        equals.setTextColor(Color.BLACK);
        equals.setText("=");
        equals.setPadding(dp2, 0, dp2, 0);
        header.addView(equals);

        total = new TextView(context);
        total.setGravity(Gravity.START);
        total.setMinWidth(MetricsUtils.dpToPixel(context, 104));
        header.addView(total);
    }

    public void setCategory(Category category, Long in, Long out) {
        categoryName.setText(category.getName());
        long total = 0;
        if (in != null) {
            total += in;
        }
        if (out != null) {
            total -= out;
        }
        EuroFormattingUtils.Base base = EuroFormattingUtils.Base.getBase(in, out, total);

        if (in != null && out != null) {
            this.in.setVisibility(VISIBLE);
            this.out.setVisibility(VISIBLE);
            this.equals.setVisibility(VISIBLE);
            this.in.setText(base.format(in, false));
            this.out.setText(base.format(-out, false));
        } else {
            this.in.setVisibility(GONE);
            this.out.setVisibility(GONE);
            this.equals.setVisibility(GONE);
        }

        this.total.setText(base.format(total, false) + " " + base.getUnitOfMeasure());
        if (total >= 0) {
            this.total.setTextColor(EuroFormattingUtils.POSITIVE_COLOR);
            this.total.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_trending_up_green_16dp, 0);
        } else {
            this.total.setTextColor(EuroFormattingUtils.NEGATIVE_COLOR);
            this.total.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_trending_down_red_16dp, 0);
        }
    }
}
