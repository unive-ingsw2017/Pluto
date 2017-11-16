package mama.pluto.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import mama.pluto.R;
import mama.pluto.utils.MetricsUtils;
import mama.pluto.view.selectors.HierarcySelectorView;

/**
 * Created by MMarco on 16/11/2017.
 */

public class EntiMainView extends LinearLayout {
    private final Toolbar toolbar;
    private final HierarcySelectorView hierarcySelectorView;

    public EntiMainView(Context context) {
        super(context);
    }

    public EntiMainView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EntiMainView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    {
        setOrientation(VERTICAL);
        setBackgroundColor(getResources().getColor(R.color.backgroundColor));

        toolbar = new Toolbar(getContext());
        toolbar.setPopupTheme(R.style.Theme_AppCompat_Light);
        toolbar.setBackgroundResource(R.color.colorPrimaryDark);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.inflateMenu(R.menu.enti_main_menu);
        addView(toolbar, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);


        hierarcySelectorView = new HierarcySelectorView(getContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            hierarcySelectorView.setElevation(MetricsUtils.dpToPixel(getContext(), 4f));
        }
        hierarcySelectorView.setOnHierarcyLevelSelector(hierarcyLevel -> recomputeToolbarText());
        addView(hierarcySelectorView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        recomputeToolbarText();
    }

    private void recomputeToolbarText() {
        switch (hierarcySelectorView.getHierarcyLevel()) {
            case REGIONE:
                toolbar.setTitle(R.string.seleziona_un_regione);
                break;
            case PROVINCIA:
                toolbar.setTitle(R.string.seleziona_una_provincia);
                break;
            case COMUNE:
                toolbar.setTitle(R.string.seleziona_un_comune);
                break;
            case ENTE:
                toolbar.setTitle(R.string.seleziona_un_ente);
                break;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int listPadding = Math.max(0, (width - MetricsUtils.dpToPixel(getContext(), 400)) / 2);
        MetricsUtils.applyLateralPadding(hierarcySelectorView, listPadding);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}

