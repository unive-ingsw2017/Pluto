package mama.pluto.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;

import mama.pluto.R;
import mama.pluto.utils.MetricsUtils;

public class SingleLineListItem extends android.support.v7.widget.AppCompatTextView {
    public SingleLineListItem(Context context) {
        super(context);
    }

    public SingleLineListItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SingleLineListItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        setMinHeight(MetricsUtils.dpToPixel(getContext(), 48));
        final int dp8 = MetricsUtils.dpToPixel(getContext(), 8);
        final int dp16 = MetricsUtils.dpToPixel(getContext(), 16);
        setPadding(dp16, dp8, dp16, dp8);
        setTextSize(16);
        setGravity(Gravity.CENTER_VERTICAL);
        setTextColor(Color.BLACK);
        setBackgroundResource(R.drawable.item_background);

    }
}
