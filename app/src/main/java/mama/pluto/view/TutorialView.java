package mama.pluto.view;

import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import mama.pluto.R;
import mama.pluto.utils.MetricsUtils;

public class TutorialView extends LinearLayout {
    public TutorialView(Context context) {
        super(context);
        int dp16 = MetricsUtils.dpToPixel(context, 16);
        setGravity(Gravity.CENTER);
        setBackgroundColor(0x10000000);

        ImageView iv = new ImageView(context);
        iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        iv.setImageResource(R.drawable.ic_sentiment_satisfied_dark_grey_96dp);
        iv.setPadding(dp16, dp16, dp16, dp16);
        addView(iv);

        TextView tv = new TextView(context);
        tv.setPadding(0, dp16, dp16, dp16);
        tv.setText(R.string.main_tutorial);//TODO: pensare a scritta meglio
        addView(tv);
    }
}
