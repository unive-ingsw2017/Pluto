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

public class DoubleLineListItem extends LinearLayout {
    @NotNull
    private final AppCompatTextView firstLineView;
    @NotNull
    private final AppCompatTextView secondLineView;

    public DoubleLineListItem(Context context) {
        super(context);
    }

    public DoubleLineListItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DoubleLineListItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
    }

    public void setText(@Nullable CharSequence firstLine, @Nullable CharSequence secondLine) {
        firstLineView.setVisibility(firstLine == null ? GONE : VISIBLE);
        secondLineView.setVisibility(secondLine == null ? GONE : VISIBLE);
        firstLineView.setText(firstLine);
        secondLineView.setText(secondLine);
    }
}
