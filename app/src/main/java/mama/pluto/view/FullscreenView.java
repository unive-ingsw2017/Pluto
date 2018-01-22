package mama.pluto.view;

import android.content.Context;
import android.graphics.Color;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import mama.pluto.R;
import mama.pluto.utils.MetricsUtils;

public class FullscreenView extends LinearLayout {
    protected final ImageView logo;
    protected final FrameLayout content;
    private final LayoutParams childParams = new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f);

    public FullscreenView(Context context) {
        super(context);
        final int dp32 = MetricsUtils.dpToPixel(context, 32);

        logo = new ImageView(context);
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        logo.setPadding(dp32, dp32, dp32, dp32);
        logo.setColorFilter(Color.WHITE);
        addView(logo, childParams);


        content = new FrameLayout(context);
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
