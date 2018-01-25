package mama.pluto.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import mama.pluto.R;
import mama.pluto.utils.MetricsUtils;

public class BalanceRowView extends LinearLayout {

    private final static NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(Locale.ITALY);
    private final static NumberFormat CURRENCY_FORMAT_NO_SYMBOL = new DecimalFormat("0.00");
    public static final int POSITIVE_COLOR = 0xff43A047;
    public static final int NEGATIVE_COLOR = 0xffBF360C;
    private final TextView in, out;

    public BalanceRowView(Context context) {
        super(context);
        in = createBalanceView();
        in.setTextColor(POSITIVE_COLOR);
        in.setGravity(Gravity.END);
        in.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_trending_up_green_16dp, 0);
        addView(in, new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f));


        addView(new Space(context), MetricsUtils.dpToPixel(context, 16), LayoutParams.WRAP_CONTENT);


        out = createBalanceView();
        out.setTextColor(NEGATIVE_COLOR);
        out.setGravity(Gravity.START);
        out.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_trending_down_red_16dp, 0, 0, 0);
        addView(out, new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f));
    }

    public static String formatEuroCentString(long balance, boolean approximate, boolean showSymbol) {
        long cifra = Math.abs(balance);
        String uom = "";
        if (approximate && cifra >= 100_000) {
            if (cifra > 100_000_000_000L) {
                cifra = cifra / 1_000_000_000;
                uom = " miliardi";
            } else if (cifra > 100_000_000L) {
                cifra = cifra / 1_000_000;
                uom = " milioni";
            } else if (cifra > 100_000L) {
                cifra = cifra / 1_000;
                uom = " mila";
            } else {
                throw new IllegalStateException();
            }
        }
        BigDecimal euroValue = new BigDecimal(cifra).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_EVEN);
        String format;
        if (showSymbol) {
            format = CURRENCY_FORMAT.format(euroValue);
        } else {
            format = CURRENCY_FORMAT_NO_SYMBOL.format(euroValue);
        }
        return (balance < 0 ? "-" : "") + format + uom;
    }

    public void setBalance(Long in, Long out) {
        if (in != null) {
            this.in.setText(formatEuroCentString(in, false, true));
        } else {
            this.in.setText("-");
        }
        if (out != null) {
            this.out.setText(formatEuroCentString(out, false, true));
        } else {
            this.out.setText("-");
        }
    }

    @NonNull
    private TextView createBalanceView() {
        int dp8 = MetricsUtils.dpToPixel(getContext(), 8);
        TextView ret = new TextView(getContext());
        ret.setCompoundDrawablePadding(dp8);
        ret.setPadding(dp8, dp8, dp8, dp8);
        return ret;
    }
}
