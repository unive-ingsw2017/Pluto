package mama.pluto.view;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Provincia;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Regione;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import mama.pluto.R;
import mama.pluto.dataAbstraction.AnagraficheImproved;

public class HeatMapMainView extends BaseLayoutView {

    private final HeatMapView heatMapView;

    public HeatMapMainView(Context context, @NotNull AnagraficheImproved anagrafiche) {
        super(context);
        getToolbar().setTitle(R.string.heat_map);
        getToolbar().inflateMenu(R.menu.heatmap_menu);
        getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.detail_level_provincia:
                        heatMapView.setupForProvinciaLevel("SpeseP", HeatMapView.MapProjection.MERCATOR, new HashMap<Provincia, Float>() {
                            {
                                for (Provincia provincia : anagrafiche.getProvincie()) {
                                    put(provincia, (float) Math.random());
                                }
                            }
                        });
                        break;
                    case R.id.detail_level_regione:
                        heatMapView.setupForRegioneLevel("SpeseR", HeatMapView.MapProjection.MERCATOR, new HashMap<Regione, Float>() {
                            {
                                for (Regione regione : anagrafiche.getRegioni()) {
                                    put(regione, (float) Math.random());
                                }
                            }
                        });
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
