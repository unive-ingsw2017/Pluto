package mama.pluto.view;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.GeoItem;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Map;

import mama.pluto.R;
import mama.pluto.dataAbstraction.AnagraficheExtended;
import mama.pluto.dataAbstraction.DataUtils;
import mama.pluto.dataAbstraction.JVectorProvinciaCodes;
import mama.pluto.database.Database;
import mama.pluto.utils.Pair;
import mama.pluto.utils.Producer;
import mama.pluto.utils.StringUtils;

public class HeatMapMainView extends BaseLayoutView {

    @NotNull
    private final HeatMapView heatMapView;
    @NotNull
    private final AnagraficheExtended anagrafiche;

    public HeatMapMainView(Context context, @NotNull AnagraficheExtended anagrafiche) {
        super(context);
        this.anagrafiche = anagrafiche;
        getToolbar().setTitle(R.string.heat_map);
        getToolbar().inflateMenu(R.menu.heatmap_menu);
        getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.detail_level_regione:
                        set(HeatMapMainView.this::dataRegione);
                        break;
                    case R.id.detail_level_provincia:
                        set(HeatMapMainView.this::dataProvincia);
                        break;
                    default:
                        throw new IllegalStateException();
                }
                return false;
            }
        });

        heatMapView = new HeatMapView(context, anagrafiche);
        addView(heatMapView, BaseLayoutView.LayoutParams.MATCH_PARENT, BaseLayoutView.LayoutParams.MATCH_PARENT);

        set(HeatMapMainView.this::dataRegione);
    }

    @NotNull
    public HeatMapView.Data<?, ?> dataRegione() {
        return heatMapView.dataForRegioneLevel(getResources().getString(R.string.heatmap_meaning), HeatMapView.MapProjection.MERCATOR, balanceRatio(Database.getInstance(getContext()).getRegioneBalances(anagrafiche)), HeatMapMainView::balanceRatioLabelGeoItem);
    }

    @NotNull
    public HeatMapView.Data<?, ?> dataProvincia() {
        return heatMapView.dataForProvinciaLevel(getResources().getString(R.string.heatmap_meaning), HeatMapView.MapProjection.MERCATOR, balanceRatio(JVectorProvinciaCodes.getProvinciaBalances(getContext(), anagrafiche)), HeatMapMainView::balanceRatioLabelStr);
    }

    @SuppressLint("StaticFieldLeak")
    public void set(@NotNull Producer<HeatMapView.Data<?, ?>> dataProducer) {
        ProgressDialog pb = new ProgressDialog(getContext());
        pb.setMessage(getContext().getString(R.string.loading___));
        pb.show();
        new AsyncTask<Void, Void, HeatMapView.Data<?, ?>>() {
            @Override
            protected HeatMapView.Data<?, ?> doInBackground(Void... voids) {
                final HeatMapView.Data<?, ?> ret = dataProducer.produce();
                try {
                    heatMapView.waitPageLoaded();
                } catch (InterruptedException e) {
                    return null;
                }
                return ret;
            }

            @Override
            protected void onPostExecute(HeatMapView.Data<?, ?> data) {
                if (data != null) {
                    heatMapView.setData(data);
                }
                pb.dismiss();
            }
        }.execute();
    }

    public final static DecimalFormat PERCENT_FORMAT = new DecimalFormat("0.00%");

    @NotNull
    public static String balanceRatioLabelGeoItem(@NotNull GeoItem geoItem, float ratio) {
        return balanceRatioLabelStr(StringUtils.toNormalCase(geoItem.getNome()), ratio);
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
