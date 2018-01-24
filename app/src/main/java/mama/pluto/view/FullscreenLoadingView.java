package mama.pluto.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DecimalFormat;

import mama.pluto.R;
import mama.pluto.utils.ColorUtils;

public class FullscreenLoadingView extends FullscreenView {

    private final static DecimalFormat PERCENTAGE_FORMATTER = new DecimalFormat("0.00%");
    private final static int MAX_PROGRESS = 10000;
    private final TextView messageView;
    private final TextView percentageView;
    private final ProgressBar pb;

    public FullscreenLoadingView(Context context) {
        super(context);
        setBackgroundColor(ColorUtils.getColor(context, R.color.colorPrimaryDark));
        logo.setImageResource(R.drawable.ic_euro_sign_240dp);

        title.setText(R.string.loading___);

        messageView = new TextView(context);
        messageView.setGravity(Gravity.CENTER);
        messageView.setTextColor(Color.WHITE);
        content.addView(messageView);

        percentageView = new TextView(context);
        percentageView.setGravity(Gravity.CENTER);
        percentageView.setTextColor(Color.WHITE);
        content.addView(percentageView);

        pb = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        pb.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        pb.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        pb.setIndeterminate(true);
        pb.setMax(MAX_PROGRESS);
        content.addView(pb, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
    }

    public void setMessage(String message) {
        this.messageView.setText(message);
    }

    public void setProgress(float percentage) {
        //TODO: mettere ETA
        this.percentageView.setText(PERCENTAGE_FORMATTER.format(percentage));
        pb.setIndeterminate(false);
        final int progress = Math.round(percentage * MAX_PROGRESS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            pb.setProgress(progress, true);
        } else {
            pb.setProgress(progress);
        }
    }
}
