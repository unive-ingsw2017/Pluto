package mama.pluto.view;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.GeoItem;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.Map;

import mama.pluto.R;
import mama.pluto.dataAbstraction.AnagraficheExtended;
import mama.pluto.dataAbstraction.DataUtils;
import mama.pluto.dataAbstraction.JVectorProvinciaCodes;
import mama.pluto.database.Database;
import mama.pluto.utils.Pair;
import mama.pluto.utils.StringUtils;

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
                        heatMapView.setupForRegioneLevel(HeatMapView.MapProjection.MERCATOR, balanceRatio(Database.getInstance(context).getRegioneBalances(anagrafiche)), HeatMapMainView::balanceRatioLabelGeoItem);
                        break;
                    case R.id.detail_level_provincia:
                        heatMapView.setupForProvinciaLevel(HeatMapView.MapProjection.MERCATOR, balanceRatio(JVectorProvinciaCodes.getProvinciaBalances(getContext(), anagrafiche)), HeatMapMainView::balanceRatioLabelStr);
                        break;
                    default:
                        throw new IllegalStateException();
                }
                return false;
            }
        });

        heatMapView = new HeatMapView(context, anagrafiche);
        addView(heatMapView, BaseLayoutView.LayoutParams.MATCH_PARENT, BaseLayoutView.LayoutParams.MATCH_PARENT);

        //heatMapView.setupForProvinciaLevel("Base", HeatMapView.MapProjection.MERCATOR, new HashMap<>());
    }

    public final static DecimalFormat PERCENT_FORMAT = new DecimalFormat("0.00%");

    @NotNull
    public static String balanceRatioLabelGeoItem(@NotNull GeoItem geoItem, float ratio) {
        return balanceRatioLabelStr(geoItem.getNome(), ratio);
    }

    @NotNull
    private static String balanceRatioLabelStr(@NotNull String str, float ratio) {
        return str + " (" + PERCENT_FORMAT.format(ratio) + ")";
    }

    @NotNull
    public static <X> Map<X, Float> balanceRatio(@NotNull Map<X, Pair<Long, Long>> map) {
        return DataUtils.mapConvertValues(map, x -> x.getFirst() / (float) x.getSecond());
    }
}
