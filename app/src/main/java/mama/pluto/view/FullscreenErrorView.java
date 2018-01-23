package mama.pluto.view;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.ImageButton;
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
    private final ImageButton retryButton;

    public FullscreenErrorView(Context context) {
        super(context);
        setBackgroundColor(ColorUtils.getColor(context, R.color.error_color));
        logo.setImageResource(R.drawable.ic_error_outline_white_240dp);

        int dp32 = MetricsUtils.dpToPixel(context, 32);

        title.setText(R.string.error);

        LinearLayout ll = new LinearLayout(context);
        ll.setPadding(dp32, dp32, dp32, dp32);
        ll.setOrientation(VERTICAL);
        content.addView(ll);

        errorMessageView = new TextView(context, null, android.R.attr.textAppearanceMediumInverse);
        errorMessageView.setGravity(Gravity.CENTER_HORIZONTAL);
        errorMessageView.setTextColor(Color.WHITE);
        ll.addView(errorMessageView);

        ll.addView(new Space(context), new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 0, 1f));

        retryButton = new ImageButton(context, null, android.R.attr.borderlessButtonStyle);
        retryButton.setImageResource(R.drawable.ic_refresh_white_24dp);
        ll.addView(retryButton);

        TextView retryButtonCaption = new TextView(context, null, android.R.attr.textAppearanceSmallInverse);
        retryButtonCaption.setGravity(Gravity.CENTER_HORIZONTAL);
        retryButtonCaption.setTextColor(Color.WHITE);
        retryButtonCaption.setText(R.string.retry);
        ll.addView(retryButtonCaption);

        ll.addView(new Space(context), new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 0, 1f));
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessageView.setText(errorMessage);
    }

    public void setOnRetryListener(Runnable onRetryListener) {
        this.retryButton.setOnClickListener(v -> onRetryListener.run());
    }
}
