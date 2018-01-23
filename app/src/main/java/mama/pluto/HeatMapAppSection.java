package mama.pluto;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

import org.jetbrains.annotations.NotNull;

import mama.pluto.utils.AppSection;
import mama.pluto.utils.BaseActivity;
import mama.pluto.view.BaseLayoutView;
import mama.pluto.view.HeatMapView;

public class HeatMapAppSection extends AppSection {

    @Override
    public String getTitle(@NotNull Context context) {
        return context.getString(R.string.heat_map);
    }

    @Override
    public Drawable getIcon(@NotNull Context context) {
        return context.getResources().getDrawable(R.drawable.ic_heatmap_black_24dp);
    }


    @Override
    protected View createView(@NotNull BaseActivity baseActivity) {
        BaseLayoutView baseLayoutView = new BaseLayoutView(baseActivity);
        baseLayoutView.getToolbar().setTitle(R.string.heat_map);
        baseActivity.setupToolbar(baseLayoutView.getToolbar());

        HeatMapView heatMapView = new HeatMapView(baseActivity);
        baseLayoutView.addView(heatMapView, BaseLayoutView.LayoutParams.MATCH_PARENT, BaseLayoutView.LayoutParams.MATCH_PARENT);
        return baseLayoutView;
    }
}
