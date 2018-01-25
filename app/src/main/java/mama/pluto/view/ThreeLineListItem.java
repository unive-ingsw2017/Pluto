package mama.pluto.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;

import org.jetbrains.annotations.NotNull;

import mama.pluto.R;
import mama.pluto.utils.MetricsUtils;

public class ThreeLineListItem extends LinearLayout {
    @NotNull
    private final AppCompatTextView firstLineView;
    @NotNull
    private final AppCompatTextView secondLineView;
    @NotNull
    private final AppCompatTextView thirdLineView;

    public ThreeLineListItem(Context context) {
        super(context);
    }

    public ThreeLineListItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ThreeLineListItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        setOrientation(VERTICAL);
        setMinimumHeight(MetricsUtils.dpToPixel(getContext(), 72));
        setGravity(Gravity.CENTER_VERTICAL);
        final int dp8 = MetricsUtils.dpToPixel(getContext(), 8);
        final int dp16 = MetricsUtils.dpToPixel(getContext(), 16);
        setPadding(dp16, dp8, dp16, dp8);
        setBackgroundResource(R.drawable.item_background);

        firstLineView = new AppCompatTextView(getContext());
        firstLineView.setTextSize(16);
        firstLineView.setTextColor(Color.BLACK);
        addView(firstLineView);


        secondLineView = new AppCompatTextView(getContext());
        secondLineView.setTextSize(14);
        secondLineView.setTextColor(0xff999999);
        addView(secondLineView);

        thirdLineView = new AppCompatTextView(getContext());
        thirdLineView.setTextSize(14);
        thirdLineView.setTextColor(0xff999999);
        addView(thirdLineView);
    }

    public void setText(CharSequence firstLine, CharSequence secondLine, CharSequence thirdLine) {
        firstLineView.setText(firstLine);
        secondLineView.setText(secondLine);
        thirdLineView.setText(thirdLine);
    }

    public void setThirdLineTextColor(int textColor) {
        thirdLineView.setTextColor(textColor);
    }
}
