package mama.pluto.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import mama.pluto.R;
import mama.pluto.utils.EuroFormattingUtils;
import mama.pluto.utils.MetricsUtils;

public class BalanceRowView extends LinearLayout {

    private final TextView in, out;

    public BalanceRowView(Context context) {
        super(context);
        in = createBalanceView();
        in.setTextColor(EuroFormattingUtils.POSITIVE_COLOR);
        in.setGravity(Gravity.END);
        in.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_trending_up_green_16dp, 0);
        addView(in, new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f));


        addView(new Space(context), MetricsUtils.dpToPixel(context, 16), LayoutParams.WRAP_CONTENT);


        out = createBalanceView();
        out.setTextColor(EuroFormattingUtils.NEGATIVE_COLOR);
        out.setGravity(Gravity.START);
        out.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_trending_down_red_16dp, 0, 0, 0);
        addView(out, new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f));
    }

    public void setBalance(Long in, Long out) {
        if (in != null) {
            this.in.setText(EuroFormattingUtils.formatEuroCentString(in, false, true));
        } else {
            this.in.setText("-");
        }
        if (out != null) {
            this.out.setText(EuroFormattingUtils.formatEuroCentString(out, false, true));
        } else {
            this.out.setText("-");
        }
    }

    @NonNull
    private TextView createBalanceView() {
        int dp4 = MetricsUtils.dpToPixel(getContext(), 4);
        int dp8 = MetricsUtils.dpToPixel(getContext(), 8);
        TextView ret = new TextView(getContext());
        ret.setCompoundDrawablePadding(dp4);
        ret.setPadding(dp8, dp8, dp8, dp8);
        return ret;
    }
}
