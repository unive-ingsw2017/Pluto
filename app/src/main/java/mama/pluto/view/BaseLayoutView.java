package mama.pluto.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import org.jetbrains.annotations.NotNull;

import mama.pluto.R;

public class BaseLayoutView extends LinearLayout {

    @NotNull
    protected final Toolbar toolbar;
    @NotNull
    protected final FrameLayout toolbarWrapper;

    public BaseLayoutView(Context context) {
        super(context);
        setOrientation(VERTICAL);
        setBackgroundColor(getResources().getColor(R.color.backgroundColor));

        toolbarWrapper = new FrameLayout(context);
        addView(toolbarWrapper, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        toolbar = new Toolbar(getContext());
        toolbar.setPopupTheme(R.style.Theme_AppCompat_Light);
        toolbar.setBackgroundResource(R.color.colorPrimaryDark);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbarWrapper.addView(toolbar, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    @NonNull
    public Toolbar getToolbar() {
        return toolbar;
    }
}
