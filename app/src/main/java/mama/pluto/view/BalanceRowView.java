package mama.pluto.view;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import mama.pluto.R;
import mama.pluto.utils.EuroFormattingUtils;
import mama.pluto.utils.MetricsUtils;

public class BalanceRowView extends LinearLayout {

    private final TextView inView;
    private final TextView outView;
    private final TextView equalsView;
    private final TextView totalView;

    public BalanceRowView(Context context) {
        super(context);
        int dp2 = MetricsUtils.dpToPixel(context, 2);

        inView = new TextView(context);
        inView.setTextColor(EuroFormattingUtils.POSITIVE_COLOR);
        addView(inView);

        outView = new TextView(context);
        outView.setTextColor(EuroFormattingUtils.NEGATIVE_COLOR);
        addView(outView);

        equalsView = new TextView(context);
        equalsView.setTextColor(Color.BLACK);
        equalsView.setText("=");
        equalsView.setPadding(dp2, 0, dp2, 0);
        addView(equalsView);

        totalView = new TextView(context);
        totalView.setGravity(Gravity.START);
        addView(totalView);
    }

    public void setBalance(Long in, Long out) {
        if (in != null && in == 0) {
            in = null;
        }
        if (out != null && out == 0) {
            out = null;
        }
        long total = 0;
        if (in != null) {
            total += in;
        }
        if (out != null) {
            total -= out;
        }
        EuroFormattingUtils.Base base = EuroFormattingUtils.Base.getBase(in, out, total);

        if (in != null && out != null) {
            this.inView.setVisibility(VISIBLE);
            this.outView.setVisibility(VISIBLE);
            this.equalsView.setVisibility(VISIBLE);
            this.inView.setText(base.format(in, false));
            this.outView.setText(base.format(-out, false));
        } else {
            this.inView.setVisibility(GONE);
            this.outView.setVisibility(GONE);
            this.equalsView.setVisibility(GONE);
        }

        this.totalView.setText(base.format(total, false) + " " + base.getUnitOfMeasure());
        if (total > 0) {
            this.totalView.setTextColor(EuroFormattingUtils.POSITIVE_COLOR);
            this.totalView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_trending_up_green_16dp, 0);
        } else if (total == 0) {
            this.totalView.setTextColor(EuroFormattingUtils.NEUTRAL_COLOR);
            this.totalView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        } else {
            this.totalView.setTextColor(EuroFormattingUtils.NEGATIVE_COLOR);
            this.totalView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_trending_down_red_16dp, 0);
        }
    }
}
