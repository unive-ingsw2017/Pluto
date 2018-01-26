package mama.pluto.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.GeoItem;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Provincia;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Regione;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mama.pluto.R;
import mama.pluto.dataAbstraction.DataUtils;
import mama.pluto.dataAbstraction.JVectorProvinciaCodes;
import mama.pluto.dataAbstraction.JVectorRegioneCodes;
import mama.pluto.utils.BiFunction;
import mama.pluto.utils.DoubleMap;
import mama.pluto.utils.Function;
import mama.pluto.utils.MetricsUtils;
import mama.pluto.utils.Pair;
import mama.pluto.utils.StringUtils;

public class HeatMapView extends FrameLayout {

    public enum MapProjection {
        MILLER("mill"),
        MERCATOR("merc");
        private final String codeName;

        MapProjection(String codeName) {
            this.codeName = codeName;
        }

        public String getCodeName() {
            return codeName;
        }
    }

    @NotNull
    private final WebView wv;
    @NotNull
    private final LinearLayout baslineLL;
    @NotNull
    private final TextView baselineTV;

    private MapProjection mapProjection;
    private boolean isRegionLevel;
    private DoubleMap<GeoItem, String> geoItemCodeMap;
    private Map<String, ? extends Pair<? extends Number, String>> data;
    private Number center = 1;

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public HeatMapView(Context context) {
        super(context);
        wv = new WebView(getContext());
        wv.getSettings().setJavaScriptEnabled(true);
        wv.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void onClick(@NotNull String code) {
                center = data.get(code).getFirst();
                post(() -> {
                    baslineLL.setVisibility(View.VISIBLE);
                    baselineTV.setText(getContext().getString(R.string.baseline, StringUtils.toNormalCase(geoItemCodeMap.getK1(code).getNome())));
                    apply();
                });
            }
        }, "regClick");
        wv.setWebChromeClient(new WebChromeClient());
        wv.loadUrl("file:///android_asset/map.html");
        addView(wv, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

        baslineLL = new LinearLayout(getContext());
        baslineLL.setBackgroundColor(0xFF333333);
        baslineLL.setOrientation(LinearLayout.HORIZONTAL);
        baslineLL.setVisibility(View.GONE);
        addView(baslineLL, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, Gravity.BOTTOM));

        baselineTV = new TextView(getContext());
        final int dp16 = MetricsUtils.dpToPixel(getContext(), 16);
        baselineTV.setPadding(dp16, dp16, dp16, dp16);
        baselineTV.setTextSize(18);
        baselineTV.setTextColor(Color.WHITE);
        baslineLL.addView(baselineTV, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        final Button resetBaselineButton = new Button(getContext(), null, android.R.attr.buttonBarButtonStyle);
        resetBaselineButton.setPadding(dp16, dp16, dp16, dp16);
        resetBaselineButton.setTextSize(18);
        resetBaselineButton.setText(R.string.reset);
        resetBaselineButton.setOnClickListener(view -> {
            center = 1;
            post(() -> {
                baslineLL.setVisibility(View.GONE);
                apply();
            });
        });
        baslineLL.addView(resetBaselineButton, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    public <N extends Number> void setupForRegioneLevel(@NotNull MapProjection mapProjection, @NotNull Map<Regione, N> data, @NotNull final BiFunction<Regione, N, String> labels) {
        setData(mapProjection, applyBiFunction(data, labels), JVectorRegioneCodes::getJVectorCode, true);
    }

    public <N extends Number> void setupForProvinciaLevel(@NotNull MapProjection mapProjection, @NotNull Map<Provincia, N> data, @NotNull final BiFunction<Provincia, N, String> labels) {
        setData(mapProjection, applyBiFunction(data, labels), JVectorProvinciaCodes::optJVectorCode, false);
    }

    @NotNull
    private static <X, N extends Number> Map<X, Pair<N, String>> applyBiFunction(@NotNull Map<X, N> data, @NotNull BiFunction<X, N, String> f) {
        final HashMap<X, Pair<N, String>> ret = new HashMap<>();
        for (Map.Entry<X, N> e : data.entrySet()) {
            ret.put(e.getKey(), new Pair<>(e.getValue(), f.apply(e.getKey(), e.getValue())));
        }
        return ret;
    }

    private <N extends Number, G extends GeoItem> void setData(@NotNull MapProjection mapProjection, @NotNull Map<G, Pair<N, String>> data, @NotNull Function<G, String> toCodeFunction, boolean isRegionLevel) {
        this.mapProjection = mapProjection;
        this.isRegionLevel = isRegionLevel;
        this.geoItemCodeMap = new DoubleMap<>(data.size());
        for (G g : data.keySet()) {
            final String code = toCodeFunction.apply(g);
            if (code != null) {
                this.geoItemCodeMap.put(g, code);
            }
        }
        this.data = DataUtils.mapConvertKeys(data, this.geoItemCodeMap::getK2);
        this.data.remove(null);
        apply();
    }

    private void apply() {
        final String centerStr;
        try {
            centerStr = JSONObject.numberToString(this.center);
        } catch (JSONException e) {
            throw new IllegalStateException(e);
        }
        wv.loadUrl("javascript:refreshMap(" +
                new JSONObject(DataUtils.mapConvertValues(data, Pair::getFirst)) + "," +
                new JSONObject(DataUtils.mapConvertValues(data, Pair::getSecond)) + "," +
                JSONObject.quote(getMapCode()) + "," +
                centerStr +
                ")");
    }

    @NonNull
    @Contract(pure = true)
    private String getMapCode() {
        String ret = "it_";
        if (isRegionLevel) {
            ret += "regions_";
        }
        return ret + mapProjection.getCodeName();
    }

}
