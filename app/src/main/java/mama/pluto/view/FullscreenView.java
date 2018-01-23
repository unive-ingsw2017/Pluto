package mama.pluto.view;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import mama.pluto.utils.MetricsUtils;

public class FullscreenView extends LinearLayout {
    protected final ImageView logo;
    protected final TextView title;
    protected final LinearLayout content;
    private final LayoutParams childParams = new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f);

    public FullscreenView(Context context) {
        super(context);

        LinearLayout ll = new LinearLayout(context);
        final int dp32 = MetricsUtils.dpToPixel(context, 32);
        ll.setPadding(dp32, dp32, dp32, dp32);
        ll.setOrientation(VERTICAL);
        addView(ll, childParams);

        logo = new ImageView(context);
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        logo.setColorFilter(Color.WHITE);
        ll.addView(logo, new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f));

        title = new TextView(context, null, android.R.attr.textAppearanceLargeInverse);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);
        ll.addView(title);

        content = new LinearLayout(context);
        content.setOrientation(VERTICAL);
        addView(content, childParams);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MeasureSpec.getSize(widthMeasureSpec) > MeasureSpec.getSize(heightMeasureSpec)) {
            setOrientation(HORIZONTAL);
            childParams.height = LayoutParams.MATCH_PARENT;
            childParams.width = 0;
        } else {
            setOrientation(VERTICAL);
            childParams.width = LayoutParams.MATCH_PARENT;
            childParams.height = 0;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
