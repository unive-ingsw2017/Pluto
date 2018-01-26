package mama.pluto.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Provincia;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Regione;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mama.pluto.dataAbstraction.DataUtils;
import mama.pluto.dataAbstraction.JVectorProvinciaCodes;
import mama.pluto.dataAbstraction.JVectorRegioneCodes;
import mama.pluto.utils.BiFunction;
import mama.pluto.utils.Function;
import mama.pluto.utils.Pair;

public class HeatMapView extends WebView {

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

    private MapProjection mapProjection;
    private boolean isRegionLevel;

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public HeatMapView(Context context) {
        super(context);
        getSettings().setJavaScriptEnabled(true);
        addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void onClick(@NotNull String code) {
                System.out.println(code);
            }
        }, "regClick");
        setWebChromeClient(new WebChromeClient());
        loadUrl("file:///android_asset/map.html");
    }

    public <N extends Number> void setupForRegioneLevel(@NotNull MapProjection mapProjection, @NotNull Map<Regione, N> data, @NotNull final BiFunction<Regione, N, String> labels) {
        setData(mapProjection, DataUtils.mapConvertKeys(applyBiFunction(data, labels), JVectorRegioneCodes::getJVectorCode), true);
    }

    public <N extends Number> void setupForProvinciaLevel(@NotNull MapProjection mapProjection, @NotNull Map<Provincia, N> data, @NotNull final BiFunction<Provincia, N, String> labels) {
        Map<String, Pair<N, String>> map = DataUtils.mapConvertKeys(applyBiFunction(data, labels), JVectorProvinciaCodes::optJVectorCode);
        map.remove(null);
        setData(mapProjection, map, false);
    }

    @NotNull
    private static <X, N extends Number> Map<X, Pair<N, String>> applyBiFunction(@NotNull Map<X, N> map, @NotNull BiFunction<X, N, String> f) {
        final HashMap<X, Pair<N, String>> ret = new HashMap<>();
        for (Map.Entry<X, N> e : map.entrySet()) {
            ret.put(e.getKey(), new Pair<>(e.getValue(), f.apply(e.getKey(), e.getValue())));
        }
        return ret;
    }

    private <N extends Number> void setData(MapProjection mapProjection, Map<String, Pair<N, String>> data, boolean isRegionLevel) {
        this.mapProjection = mapProjection;
        this.isRegionLevel = isRegionLevel;
        loadUrl("javascript:refreshMap(" +
                new JSONObject(DataUtils.mapConvertValues(data, Pair::getFirst)) + "," +
                new JSONObject(DataUtils.mapConvertValues(data, Pair::getSecond)) + "," +
                JSONObject.quote(getMapCode()) +
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
