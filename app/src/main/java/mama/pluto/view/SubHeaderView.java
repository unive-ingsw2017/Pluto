package mama.pluto.view;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;

import mama.pluto.R;
import mama.pluto.utils.MetricsUtils;

public class SubHeaderView extends AppCompatTextView {
    public SubHeaderView(Context context) {
        super(context);
    }

    public SubHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SubHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        setTextSize(14);
        setTypeface(ResourcesCompat.getFont(getContext(), R.font.roboto_medium));
        setMinHeight(MetricsUtils.dpToPixel(getContext(), 40));
        setTextColor(0xff999999);
        final int dp4 = MetricsUtils.dpToPixel(getContext(), 4);
        final int dp16 = MetricsUtils.dpToPixel(getContext(), 16);
        setPadding(dp16, dp4, dp16, dp4);
        setGravity(Gravity.CENTER_VERTICAL);

    }
}
