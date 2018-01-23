package mama.pluto.view;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.HashMap;

import mama.pluto.R;

public class HeatMapMainView extends BaseLayoutView {
    private final HeatMapView heatMapView;

    public HeatMapMainView(Context context) {
        super(context);
        getToolbar().setTitle(R.string.heat_map);
        getToolbar().inflateMenu(R.menu.heatmap_menu);
        getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.detail_level_provincia:
                        heatMapView.setupForProvinciaLevel("SpeseP", HeatMapView.MapProjection.MERCATOR, new HashMap<>());
                        break;
                    case R.id.detail_level_regione:
                        heatMapView.setupForRegioneLevel("SpeseR", HeatMapView.MapProjection.MERCATOR, new HashMap<>());
                        break;
                    default:
                        throw new IllegalStateException();
                }
                return false;
            }
        });

        heatMapView = new HeatMapView(context);
        addView(heatMapView, BaseLayoutView.LayoutParams.MATCH_PARENT, BaseLayoutView.LayoutParams.MATCH_PARENT);

        //heatMapView.setupForProvinciaLevel("Base", HeatMapView.MapProjection.MERCATOR, new HashMap<>());
    }
}
