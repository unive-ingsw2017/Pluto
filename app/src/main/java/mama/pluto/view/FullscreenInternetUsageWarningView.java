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
import mama.pluto.utils.DataRestrictedState;
import mama.pluto.utils.MetricsUtils;


public class FullscreenInternetUsageWarningView extends FullscreenView {
    @NotNull
    private final TextView warningMessageView;
    @NotNull
    private final ImageButton continueButton;

    public FullscreenInternetUsageWarningView(Context context, DataRestrictedState dataRestrictedState, int approximageMBSize, Runnable onUserAgreed) {
        super(context);
        setBackgroundColor(ColorUtils.getColor(context, R.color.colorPrimaryDark));
        logo.setImageResource(R.drawable.ic_data_metered_warning_white_240dp);

        int dp32 = MetricsUtils.dpToPixel(context, 32);

        title.setText(R.string.warning);

        content.setPadding(dp32, dp32, dp32, dp32);

        warningMessageView = new TextView(context, null, android.R.attr.textAppearanceMediumInverse);
        warningMessageView.setGravity(Gravity.CENTER_HORIZONTAL);
        warningMessageView.setTextColor(Color.WHITE);
        content.addView(warningMessageView);

        content.addView(new Space(context), new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 0, 1f));

        continueButton = new ImageButton(context, null, android.R.attr.borderlessButtonStyle);
        continueButton.setImageResource(R.drawable.ic_download_black_24dp);
        content.addView(continueButton);

        TextView retryButtonCaption = new TextView(context, null, android.R.attr.textAppearanceSmallInverse);
        retryButtonCaption.setGravity(Gravity.CENTER_HORIZONTAL);
        retryButtonCaption.setTextColor(Color.WHITE);
        retryButtonCaption.setText(R.string.start_download);
        content.addView(retryButtonCaption);

        content.addView(new Space(context), new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 0, 1f));


        warningMessageView.setText(dataRestrictedState.getMessage(getContext(), approximageMBSize));
        continueButton.setOnClickListener(v -> {
            onUserAgreed.run();
        });
    }
}
