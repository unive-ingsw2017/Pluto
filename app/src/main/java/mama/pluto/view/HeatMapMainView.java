package mama.pluto.view;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.GeoItem;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Regione;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import mama.pluto.R;
import mama.pluto.dataAbstraction.AnagraficheExtended;
import mama.pluto.database.Database;
import mama.pluto.utils.EuroFormattingUtils;

public class HeatMapMainView extends BaseLayoutView {

    private final HeatMapView heatMapView;

    public HeatMapMainView(Context context, @NotNull AnagraficheExtended anagrafiche) {
        super(context);
        getToolbar().setTitle(R.string.heat_map);
        getToolbar().inflateMenu(R.menu.heatmap_menu);
        getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.detail_level_regione:
                        heatMapView.setupForRegioneLevel(HeatMapView.MapProjection.MERCATOR, Database.getInstance(context).getRegioneBalances(anagrafiche), HeatMapMainView::geoItemLabel);
                        break;
                    case R.id.detail_level_provincia:
                        heatMapView.setupForProvinciaLevel(HeatMapView.MapProjection.MERCATOR, Database.getInstance(context).getProvinciaBalances(anagrafiche), HeatMapMainView::geoItemLabel);
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

    @NotNull
    public static String geoItemLabel(@NotNull GeoItem geoItem, long balance) {
        return EuroFormattingUtils.formatEuroCentString(balance, true, true);
    }
}
