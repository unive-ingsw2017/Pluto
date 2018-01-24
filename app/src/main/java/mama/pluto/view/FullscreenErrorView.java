package mama.pluto.view;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import mama.pluto.R;
import mama.pluto.utils.ColorUtils;
import mama.pluto.utils.MetricsUtils;

public class FullscreenErrorView extends FullscreenView {

    @NotNull
    private final TextView errorMessageView;
    @NotNull
    private final Button retryButtonCaption;

    public FullscreenErrorView(Context context) {
        super(context);
        setBackgroundColor(ColorUtils.getColor(context, R.color.error_color));
        logo.setImageResource(R.drawable.ic_error_outline_white_240dp);

        int dp32 = MetricsUtils.dpToPixel(context, 32);
        int dp8 = MetricsUtils.dpToPixel(context, 8);
        int dp4 = MetricsUtils.dpToPixel(context, 4);

        title.setText(R.string.error);

        content.setPadding(dp32, dp32, dp32, dp32);

        content.addView(new Space(context), new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 0, 1f));

        errorMessageView = new TextView(context, null, android.R.attr.textAppearanceMediumInverse);
        errorMessageView.setGravity(Gravity.CENTER_HORIZONTAL);
        errorMessageView.setTextColor(Color.WHITE);
        content.addView(errorMessageView);

        content.addView(new Space(context), new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 0, 1f));

        retryButtonCaption = new Button(context, null, android.R.attr.borderlessButtonStyle);
        retryButtonCaption.setCompoundDrawablePadding(dp4);
        retryButtonCaption.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_refresh_white_24dp, 0, 0);
        retryButtonCaption.setGravity(Gravity.CENTER_HORIZONTAL);
        retryButtonCaption.setTextColor(Color.WHITE);
        retryButtonCaption.setText(R.string.retry);
        retryButtonCaption.setPadding(dp8, dp8, dp8, dp8);
        content.addView(retryButtonCaption);


        content.addView(new Space(context), new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 0, 1f));
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessageView.setText(errorMessage);
    }

    public void setOnRetryListener(Runnable onRetryListener) {
        this.retryButtonCaption.setOnClickListener(v -> onRetryListener.run());
    }
}
