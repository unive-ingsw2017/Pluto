package mama.pluto.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import mama.pluto.R;
import mama.pluto.utils.ColorUtils;
import mama.pluto.utils.MetricsUtils;

public class FullscreenLoadingView extends LinearLayout {
    private final static int MAX_PROGRESS = 10000;
    private final ImageView logo;
    private final ProgressBar pb;
    private final LayoutParams childParams = new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f);

    public FullscreenLoadingView(Context context) {
        super(context);
        setBackgroundColor(ColorUtils.getColor(context, R.color.colorPrimaryDark));

        final int dp32 = MetricsUtils.dpToPixel(context, 32);

        logo = new ImageView(context);
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        logo.setPadding(dp32, dp32, dp32, dp32);
        logo.setColorFilter(Color.WHITE);
        logo.setImageResource(R.drawable.ic_euro_sign_240dp);
        addView(logo, childParams);

        pb = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        pb.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        pb.setMax(MAX_PROGRESS);
        FrameLayout fl = new FrameLayout(context);
        fl.addView(pb, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        addView(fl, childParams);
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

    public void setProgress(float percentage) {
        final int progress = Math.round(percentage * MAX_PROGRESS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            pb.setProgress(progress, true);
        } else {
            pb.setProgress(progress);
        }
    }
}
