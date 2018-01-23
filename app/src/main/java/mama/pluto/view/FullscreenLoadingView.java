package mama.pluto.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import mama.pluto.R;
import mama.pluto.utils.ColorUtils;

public class FullscreenLoadingView extends FullscreenView {
    private final static int MAX_PROGRESS = 10000;
    private final ProgressBar pb;

    public FullscreenLoadingView(Context context) {
        super(context);
        setBackgroundColor(ColorUtils.getColor(context, R.color.colorPrimaryDark));
        logo.setImageResource(R.drawable.ic_euro_sign_240dp);

        title.setText(R.string.loading___);

        pb = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        pb.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        pb.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        pb.setIndeterminate(true);
        pb.setMax(MAX_PROGRESS);
        content.addView(pb, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
    }

    public void setProgress(float percentage) {
        pb.setIndeterminate(false);
        final int progress = Math.round(percentage * MAX_PROGRESS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            pb.setProgress(progress, true);
        } else {
            pb.setProgress(progress);
        }
    }
}
