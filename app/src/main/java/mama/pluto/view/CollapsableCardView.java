package mama.pluto.view;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import mama.pluto.R;
import mama.pluto.utils.MetricsUtils;

public abstract class CollapsableCardView extends CardView {

    protected final LinearLayout content;
    protected final LinearLayout header;
    protected final ImageButton expandIcon;
    private boolean isCollapsed = true;

    public CollapsableCardView(@NonNull Context context) {
        super(context);
        int dp2 = MetricsUtils.dpToPixel(context, 2);
        int dp16 = MetricsUtils.dpToPixel(context, 16);
        int dp48 = MetricsUtils.dpToPixel(context, 48);

        setUseCompatPadding(true);
        setRadius(dp2);

        content = new LinearLayout(context);
        content.setOrientation(LinearLayout.VERTICAL);
        addView(content);

        LinearLayout h = new LinearLayout(context);
        content.addView(h);

        header = new LinearLayout(context);
        header.setPadding(dp16, dp16, dp16, dp16);
        h.addView(header, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        expandIcon = new ImageButton(context, null, android.R.attr.borderlessButtonStyle);
        expandIcon.setImageResource(R.drawable.ic_down_black_24dp);
        expandIcon.setColorFilter(0xff999999);
        expandIcon.setOnClickListener(view -> toggle(true));
        h.addView(expandIcon, dp48, dp48);
    }

    protected void toggle(boolean animate) {
        if (isCollapsed) {
            expand(animate);
        } else {
            collapse(animate);
        }
    }

    @CallSuper
    protected void expand(boolean animate) {
        if (!isCollapsed) {
            throw new IllegalStateException();
        }
        if (animate) {
            expandIcon.animate().rotation(180);
        } else {
            expandIcon.setRotation(180);
        }
        isCollapsed = false;
        View expandedView = createExpandedView();
        content.addView(expandedView);
    }

    public boolean isCollapsed() {
        return isCollapsed;
    }

    @CallSuper
    protected void collapse(boolean animate) {
        if (isCollapsed) {
            throw new IllegalStateException();
        }
        isCollapsed = true;
        if (animate) {
            expandIcon.animate().rotation(0);
        } else {
            expandIcon.setRotation(0);
        }
        while (content.getChildCount() > 1) {
            content.removeViewAt(1);
        }
    }

    protected abstract View createExpandedView();
}
